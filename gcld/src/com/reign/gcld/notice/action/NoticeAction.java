package com.reign.gcld.notice.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.alibaba.fastjson.parser.*;
import com.alibaba.fastjson.*;
import com.reign.gcld.notice.service.*;
import com.reign.framework.netty.mvc.annotation.*;
import org.apache.commons.lang.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class NoticeAction extends BaseAction
{
    private static final long serialVersionUID = 7770453503519680059L;
    @Autowired
    private INoticeService noticeService;
    private static final Logger errorLogger;
    
    static {
        errorLogger = CommonLog.getLog(NoticeAction.class);
    }
    
    @Command("notice@addNotice")
    public ByteResult addNotice(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            int noticeType = 3;
            if (json.get("noticeType") != null) {
                noticeType = Integer.parseInt(json.get("noticeType").toString());
            }
            final String noticeContent = json.get("content").toString();
            final long expireTime = Long.parseLong(json.get("expireTime").toString());
            final long startTime = Long.parseLong(json.get("startTime").toString());
            final int frequency = Integer.parseInt(json.get("frequency").toString());
            final String yx = json.get("yx").toString();
            int result = this.checkParam(noticeType, noticeContent, yx, expireTime, startTime, frequency);
            if (result == 0) {
                result = this.noticeService.addNotice(noticeType, noticeContent, yx, expireTime, startTime, frequency);
            }
            if (result == 0) {
                return new ByteResult(JsonBuilder.getJson(State.SUCCESS, ""));
            }
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(result)));
        }
        catch (Exception e) {
            NoticeAction.errorLogger.error("ClassName:NoticeAction#method:addNotice#requestContent:" + new String(request.getContent()));
            NoticeAction.errorLogger.error("errMsg:" + e.getMessage());
            NoticeAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(4)));
        }
    }
    
    @Command("notice@modifyNotice")
    public ByteResult modifyNotice(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            int noticeType = 3;
            if (json.get("noticeType") != null) {
                noticeType = Integer.parseInt(json.get("noticeType").toString());
            }
            final int id = Integer.parseInt(json.get("id").toString());
            final String noticeContent = json.get("content").toString();
            final long expireTime = Long.parseLong(json.get("expireTime").toString());
            final long startTime = Long.parseLong(json.get("startTime").toString());
            final int frequency = Integer.parseInt(json.get("frequency").toString());
            final String yx = json.get("yx").toString();
            int result = this.checkParam(noticeType, noticeContent, yx, expireTime, startTime, frequency);
            if (result == 0) {
                result = this.noticeService.modifyNotice(noticeType, id, noticeContent, yx, expireTime, startTime, frequency);
            }
            if (result == 0) {
                return new ByteResult(JsonBuilder.getJson(State.SUCCESS, ""));
            }
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(result)));
        }
        catch (Exception e) {
            NoticeAction.errorLogger.error("ClassName:NoticeAction#method:modifyNotice#requestContent:" + new String(request.getContent()));
            NoticeAction.errorLogger.error("errMsg:" + e.getMessage());
            NoticeAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(4)));
        }
    }
    
    @Command("notice@deleteNotice")
    public ByteResult deleteNotice(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            final int noticeId = Integer.parseInt(json.get("id").toString());
            final String yx = json.get("yx").toString();
            int result = 0;
            if (noticeId <= 0 || StringUtils.isBlank(yx)) {
                result = 4;
            }
            if (result == 0) {
                result = this.noticeService.deleteNotice(noticeId, yx);
            }
            if (result == 0) {
                return new ByteResult(JsonBuilder.getJson(State.SUCCESS, ""));
            }
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(result)));
        }
        catch (Exception e) {
            NoticeAction.errorLogger.error("ClassName:NoticeAction#method:deleteNotice#requestContent:" + new String(request.getContent()));
            NoticeAction.errorLogger.error("errMsg:" + e.getMessage());
            NoticeAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(4)));
        }
    }
    
    @Command("notice@getNoticeList")
    public ByteResult getNoticeList(final Request request) {
        try {
            String yx = null;
            final String[] yxs = request.getParamterValues("yx");
            if (yxs != null && yxs.length > 0) {
                yx = yxs[0];
            }
            if (StringUtils.isBlank(yx)) {
                final byte[] bt = request.getContent();
                if (bt == null || bt.length == 0) {
                    return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
                }
                final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
                yx = json.get("yx").toString();
            }
            if (StringUtils.isBlank(yx)) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            return new ByteResult(this.noticeService.getNoticeList(yx));
        }
        catch (Exception e) {
            NoticeAction.errorLogger.error("ClassName:NoticeAction#method:getNoticeList#requestContent:" + new String(request.getContent()));
            NoticeAction.errorLogger.error("errMsg:" + e.getMessage());
            NoticeAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, NoticeErrors.getErrorMsg(4)));
        }
    }
    
    private int checkParam(final int noticeType, final String noticeContent, final String yx, final long expireTime, final long startTime, final int frequency) {
        if (noticeType < 1 || noticeType > 3 || StringUtils.isBlank(noticeContent) || StringUtils.isBlank(yx) || startTime <= 0L || expireTime <= 0L || frequency <= 0 || startTime > expireTime) {
            return 4;
        }
        if (expireTime < System.currentTimeMillis()) {
            return 5;
        }
        return 0;
    }
    
    public static void main(final String[] args) {
        System.out.println(String.valueOf("abc".getBytes()));
        System.out.println(new String("abc".getBytes()));
    }
}
