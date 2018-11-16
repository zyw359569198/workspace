package com.reign.gcld.notice.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.notice.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.notice.domain.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import org.springframework.transaction.annotation.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;

@Component("noticeService")
public class NoticeService implements INoticeService, InitializingBean
{
    @Autowired
    private ISystemNoticeDao systemNoticeDao;
    @Autowired
    private IChatService chatService;
    private Map<Integer, SystemNotice> noticeMap;
    private static final Logger errorLogger;
    private static final Logger timerLog;
    
    static {
        errorLogger = CommonLog.getLog(NoticeService.class);
        timerLog = new TimerLogger();
    }
    
    public NoticeService() {
        this.noticeMap = new ConcurrentHashMap<Integer, SystemNotice>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final Date nowDate = new Date();
        final List<SystemNotice> list = this.systemNoticeDao.getModels();
        int i = 0;
        for (final SystemNotice notice : list) {
            if (notice.getExpireTime().before(nowDate)) {
                this.systemNoticeDao.deleteById(notice.getId());
            }
            else {
                if (notice.getStartTime().before(nowDate)) {
                    notice.setNextBroadcastTime(TimeUtil.specialAddMinutes(nowDate, i * 7));
                    ++i;
                }
                else {
                    notice.setNextBroadcastTime(notice.getStartTime());
                }
                this.noticeMap.put(notice.getId(), notice);
            }
        }
    }
    
    @Transactional
    @Override
    public int addNotice(final int noticeType, final String noticeContent, final String yx, final long expireTime, final long startTime, final int frequency) {
        try {
            final SystemNotice notice = new SystemNotice();
            notice.setNoticeType(noticeType);
            notice.setContent(noticeContent);
            notice.setYx(yx);
            notice.setCreateTime(new Date());
            notice.setExpireTime(new Date(expireTime));
            final Date startDate = new Date(startTime);
            notice.setStartTime(startDate);
            notice.setNextBroadcastTime(startDate);
            notice.setFrequency(frequency);
            this.systemNoticeDao.create(notice);
            this.noticeMap.put(notice.getId(), notice);
            if (startTime <= System.currentTimeMillis()) {
                if (1 == noticeType) {
                    this.chatService.sendYxChat(notice.getYx(), notice.getContent());
                }
                else if (2 == noticeType) {
                    this.pushRightNotice(notice.getYx(), notice.getContent());
                }
                else {
                    this.chatService.sendYxChat(notice.getYx(), notice.getContent());
                    this.pushRightNotice(notice.getYx(), notice.getContent());
                }
            }
        }
        catch (Exception e) {
            NoticeService.errorLogger.error("ClassName:NoticeService#Method:addNotice#noticeContent:" + noticeContent);
            NoticeService.errorLogger.error("errMsg:" + e.getMessage());
            NoticeService.errorLogger.error(this, e);
            return 4;
        }
        return 0;
    }
    
    @Transactional
    @Override
    public int modifyNotice(final int noticeType, final int id, final String noticeContent, final String yx, final long expireTime, final long startTime, final int frequency) {
        final String orgYx = this.systemNoticeDao.getYxById(id);
        if (StringUtils.isBlank(orgYx)) {
            return 2;
        }
        if (!orgYx.equals(yx)) {
            return 7;
        }
        final SystemNotice notice = new SystemNotice();
        notice.setNoticeType(noticeType);
        notice.setId(id);
        notice.setContent(noticeContent);
        notice.setYx(yx);
        notice.setExpireTime(new Date(expireTime));
        final Date startDate = new Date(startTime);
        notice.setStartTime(startDate);
        notice.setFrequency(frequency);
        this.systemNoticeDao.update(notice);
        notice.setNextBroadcastTime(startDate);
        this.noticeMap.put(id, notice);
        if (startTime <= System.currentTimeMillis()) {
            if (1 == noticeType) {
                this.chatService.sendYxChat(notice.getYx(), notice.getContent());
            }
            else if (2 == noticeType) {
                this.pushRightNotice(notice.getYx(), notice.getContent());
            }
            else {
                this.chatService.sendYxChat(notice.getYx(), notice.getContent());
                this.pushRightNotice(notice.getYx(), notice.getContent());
            }
        }
        return 0;
    }
    
    @Transactional
    @Override
    public int deleteNotice(final int noticeId, final String yx) {
        final String orgYx = this.systemNoticeDao.getYxById(noticeId);
        if (StringUtils.isBlank(orgYx)) {
            return 2;
        }
        if (!orgYx.equals(yx)) {
            return 7;
        }
        this.systemNoticeDao.deleteById(noticeId);
        this.noticeMap.remove(noticeId);
        return 0;
    }
    
    @Transactional
    @Override
    public byte[] getNoticeList(final String yx) {
        final Date nowDate = new Date();
        final List<SystemNotice> list = new ArrayList<SystemNotice>();
        final List<Integer> delList = new ArrayList<Integer>();
        for (final Map.Entry<Integer, SystemNotice> entry : this.noticeMap.entrySet()) {
            final SystemNotice notice = entry.getValue();
            if (notice.getExpireTime().before(nowDate)) {
                this.systemNoticeDao.deleteById(entry.getKey());
                delList.add(notice.getId());
            }
            else {
                if (!notice.getYx().equals(yx)) {
                    continue;
                }
                list.add(notice);
            }
        }
        for (final int id : delList) {
            this.noticeMap.remove(id);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        for (final SystemNotice notice2 : list) {
            doc.startObject();
            doc.createElement("noticeType", notice2.getNoticeType());
            doc.createElement("id", notice2.getId());
            doc.createElement("content", notice2.getContent());
            doc.createElement("startTime", notice2.getStartTime().getTime());
            doc.createElement("expireTime", notice2.getExpireTime().getTime());
            doc.createElement("frequency", notice2.getFrequency());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void pushNotice() {
        final Date nowDate = new Date();
        final List<Integer> delList = new ArrayList<Integer>();
        for (final Map.Entry<Integer, SystemNotice> entry : this.noticeMap.entrySet()) {
            final SystemNotice notice = entry.getValue();
            if (notice.getExpireTime().after(nowDate)) {
                if (!notice.getNextBroadcastTime().before(nowDate)) {
                    continue;
                }
                final int noticeType = notice.getNoticeType();
                if (1 == noticeType) {
                    this.chatService.sendYxChat(notice.getYx(), notice.getContent());
                }
                else if (2 == noticeType) {
                    this.pushRightNotice(notice.getYx(), notice.getContent());
                }
                else {
                    this.chatService.sendYxChat(notice.getYx(), notice.getContent());
                    this.pushRightNotice(notice.getYx(), notice.getContent());
                }
                notice.setNextBroadcastTime(new Date(notice.getNextBroadcastTime().getTime() + notice.getFrequency() * 60000L));
            }
            else {
                this.systemNoticeDao.deleteById(notice.getId());
                delList.add(notice.getId());
            }
        }
        for (final int id : delList) {
            this.noticeMap.remove(id);
        }
        NoticeService.timerLog.info(LogUtil.formatThreadLog("NoticeService", "pushNotice", 2, System.currentTimeMillis() - nowDate.getTime(), ""));
    }
    
    private void pushRightNotice(final String yx, final String msg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", "GLOBAL");
        doc.createElement("content", msg);
        doc.endObject();
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_RIGHT_NOTICE.getModule(), doc.toByte()));
        GroupManager.getInstance().notifyAll(String.valueOf(ChatType.YX.toString()) + yx, WrapperUtil.wrapper(PushCommand.PUSH_RIGHT_NOTICE.getCommand(), 0, bytes));
    }
}
