package com.reign.gcld.mail.service;

import org.springframework.stereotype.*;
import com.reign.gcld.mail.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.mail.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import java.util.*;

@Component("mailService")
public class MailService implements IMailService
{
    @Autowired
    private IMailDao mailDao;
    @Autowired
    private IPlayerDao playerDao;
    private static final Logger timerLog;
    
    static {
        timerLog = new TimerLogger();
    }
    
    @Transactional
    @Override
    public byte[] writeMail(final Mail mail, final String toPlayerName) {
        final Player toPlayer = this.playerDao.getPlayerByName(toPlayerName);
        final Player player = this.playerDao.read(mail.getFId());
        if (toPlayer == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10004);
        }
        if (!player.getForceId().equals(toPlayer.getForceId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CAN_NOT_COMM_WITH_OTHER_FORCE);
        }
        mail.setTId(toPlayer.getPlayerId());
        mail.setFName(player.getPlayerName());
        mail.setLinkId(0);
        final int value = this.mailDao.saveMail(mail);
        if (-1 == value) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10005);
        }
        if (this.mailDao.getMailCount(mail.getTId(), 3) > 30) {
            this.mailDao.deleteById(this.mailDao.getFirstReceivedMailId(mail.getTId()));
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("hasNewMail", true);
        doc.endObject();
        Players.push(toPlayer.getPlayerId(), PushCommand.PUSH_UPDATE, doc.toByte());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] getMailByPlayerId(final int playerId, int page) {
        final double totalMailCount = this.mailDao.getMailCount(playerId, 3);
        final int totalPage = (int)Math.ceil(totalMailCount / 6.0);
        if (page <= 0 || totalPage <= 0) {
            page = 0;
        }
        else if (page + 1 > totalPage) {
            page = totalPage - 1;
        }
        final List<Mail> mailList = this.mailDao.getMail(playerId, page, 3);
        this.readFirstMail(playerId, mailList);
        final int currentPage = page;
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(mailList, totalPage, currentPage));
    }
    
    @Transactional
    @Override
    public byte[] getMailByType(final int playerId, final int type, final int page) {
        final List<Mail> mailList = this.mailDao.getMailByType(playerId, type, page);
        this.readFirstMail(playerId, mailList);
        final double mailCountByType = this.mailDao.getMailCountByType(playerId, type);
        final int totalPage = (int)Math.ceil(mailCountByType / 6.0);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(mailList, totalPage, page));
    }
    
    @Transactional
    @Override
    public byte[] deleteMail(final int playerId, final int mailId) {
        final Mail mail = this.mailDao.read(mailId);
        if (mail.getTId() == playerId) {
            this.mailDao.updateMailDeleteState(mailId, 1);
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("hasNewMail", this.haveNewMail(playerId)));
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10003);
    }
    
    @Transactional
    @Override
    public byte[] saveMail(final int playerId, final int mailId) {
        final Mail mail = this.mailDao.read(mailId);
        if (mail.getTId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10006);
        }
        if (mail.getIsDelete() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10012);
        }
        final int saveNum = this.mailDao.getMailCountByType(playerId, 3);
        if (saveNum >= 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10015);
        }
        this.mailDao.updateType(mailId, 3);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] retrieveMail(final int playerId, final int mailId) {
        final Mail mail = this.mailDao.read(mailId);
        if (mail.getTId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10006);
        }
        if (mail.getIsDelete() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10013);
        }
        this.mailDao.updateMailDeleteState(mailId, 0);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] deleteMailAll(final int playerId, final int[] mailIdArray) {
        this.mailDao.deleteUserMail(playerId, mailIdArray);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("hasNewMail", this.haveNewMail(playerId)));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] thoroughDeleteMail(final int playerId) {
        if (this.mailDao.thoroughDeleteMail(playerId) > 0) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10016);
    }
    
    @Transactional
    @Override
    public byte[] getDeleteMailByPlayerId(final int playerId, final int page) {
        final List<Mail> mailList = this.mailDao.getDeleteMail(playerId, page);
        this.readFirstMail(playerId, mailList);
        final double deleteMailCount = this.mailDao.getDeleteMailCount(playerId);
        final int totalPage = (int)Math.ceil(deleteMailCount / 6.0);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(mailList, totalPage, page));
    }
    
    private void readFirstMail(final int playerId, final List<Mail> mailList) {
        if (mailList != null && mailList.size() > 0) {
            final Mail mail = mailList.get(0);
            if (mail.getIsRead() == 1) {
                this.readMail(playerId, mail.getId());
                mail.setIsRead(1);
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] readMail(final int playerId, final int mailId) {
        final Mail mail = this.mailDao.read(mailId);
        if (mail.getTId() == playerId) {
            this.mailDao.readmail(mailId);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("num", this.mailDao.haveNewMail(playerId));
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10002);
    }
    
    @Transactional
    @Override
    public byte[] getMail(final int playerId, final int mailId) {
        final Mail mail = this.mailDao.read(mailId);
        if (mail != null && mail.getTId() == playerId) {
            return JsonBuilder.getJson(State.SUCCESS, "mail", (Object)mail);
        }
        if (mail == null) {
            return JsonBuilder.getJson(State.SUCCESS, "mail", (Object)mail);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10001);
    }
    
    @Transactional
    @Override
    public void writeSystemMail(final String fName, final String title, final String content, final int mailType, final int toPlayerId, final int linkId) {
        final Mail mail = new Mail();
        mail.setContent(content);
        mail.setFId(0);
        mail.setFName(fName);
        mail.setIsDelete(0);
        mail.setIsRead(0);
        mail.setMailType(mailType);
        mail.setSendtime(new Date());
        mail.setTitle(title);
        mail.setTId(toPlayerId);
        mail.setLinkId(linkId);
        final int count = this.mailDao.saveSystemMail(mail);
        if (count < 1) {
            return;
        }
        if (this.mailDao.getMailCount(toPlayerId, 3) > 30) {
            this.mailDao.deleteById(this.mailDao.getFirstReceivedMailId(toPlayerId));
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("hasNewMail", true);
        doc.endObject();
        Players.push(toPlayerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Transactional
    @Override
    public void writeSystemMail(final String fName, final String title, final String content, final int mailType, final int toPlayerId, final Date date) {
        final Mail mail = new Mail();
        mail.setContent(content);
        mail.setFId(0);
        mail.setFName(fName);
        mail.setIsDelete(0);
        mail.setIsRead(0);
        mail.setMailType(mailType);
        mail.setSendtime(date);
        mail.setTitle(title);
        mail.setTId(toPlayerId);
        mail.setLinkId(0);
        final int count = this.mailDao.saveSystemMail(mail);
        if (count < 1) {
            return;
        }
        if (this.mailDao.getMailCount(toPlayerId, 3) > 30) {
            this.mailDao.deleteById(this.mailDao.getFirstReceivedMailId(toPlayerId));
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("hasNewMail", true);
        doc.endObject();
        Players.push(toPlayerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    private byte[] getResult(final List<Mail> mailList, final int totalPagte, final int currentPage) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("mailList", this.replaceContents(mailList));
        doc.createElement("currentPage", currentPage);
        doc.createElement("totalPage", totalPagte);
        doc.endObject();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public void clearDeleteMail() {
        final long start = System.currentTimeMillis();
        this.mailDao.clearExpiredMail(3, 7);
        MailService.timerLog.info(LogUtil.formatThreadLog("MailService", "clearDeleteMail", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    @Override
    public boolean haveNewMail(final int playerId) {
        return this.mailDao.haveNewMail(playerId) > 0;
    }
    
    private List<Mail> replaceContents(final List<Mail> mailList) {
        for (final Mail mail : mailList) {
            String temp = mail.getTitle();
            temp = StringUtils.replace(temp, "%quot;", "\"");
            temp = StringUtils.replace(temp, "*quot;", "\"");
            temp = StringUtils.replace(temp, "$quot;", "\"");
            temp = StringUtils.replace(temp, "#quot;", "\"");
            temp = StringUtils.replace(temp, "@quot;", "\"");
            mail.setTitle(temp);
            temp = mail.getContent();
            temp = StringUtils.replace(temp, "%quot;", "\"");
            temp = StringUtils.replace(temp, "*quot;", "\"");
            temp = StringUtils.replace(temp, "$quot;", "\"");
            temp = StringUtils.replace(temp, "#quot;", "\"");
            temp = StringUtils.replace(temp, "@quot;", "\"");
            mail.setContent(temp);
        }
        return mailList;
    }
}
