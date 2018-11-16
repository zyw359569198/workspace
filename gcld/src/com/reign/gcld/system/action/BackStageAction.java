package com.reign.gcld.system.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.gcld.system.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.alibaba.fastjson.parser.*;
import com.alibaba.fastjson.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class BackStageAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ISystemService systemService;
    private static final Logger errorLogger;
    
    static {
        errorLogger = CommonLog.getLog(BackStageAction.class);
    }
    
    @Command("backstage@banChat2")
    public ByteResult banChat2(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.banChat2(json.get("uid").toString(), json.get("cause").toString(), 1000L * Long.parseLong(json.get("duration").toString()), yx));
    }
    
    @Command("backstage@unbanChat2")
    public ByteResult unbanChat2(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.unbanChat2(json.get("uid").toString(), yx));
    }
    
    @Command("backstage@getBanRecord2")
    public ByteResult getBanRecord2(@RequestParam("yx") final String yx, final Request request) {
        if (StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        return new ByteResult(this.systemService.getBanRecord2(yx));
    }
    
    @Command("backstage@banChat")
    public ByteResult banChat(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.banChat(json.get("playerNames").toString(), json.get("cause").toString(), 1000L * Long.parseLong(json.get("duration").toString()), yx));
    }
    
    @Command("backstage@unbanChat")
    public ByteResult unbanChat(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.unbanChat(json.get("playerNames").toString(), yx));
    }
    
    @Command("backstage@getBanRecord")
    public ByteResult getBanRecord(@RequestParam("yx") final String yx, final Request request) {
        if (StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(this.systemService.getBanRecord(null, yx));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.getBanRecord(json.get("playerNames").toString(), yx));
    }
    
    @Command("backstage@banUser")
    public ByteResult banUser(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.banUser(json.get("playerNames").toString(), json.get("blockReason").toString(), Long.parseLong(json.get("interval").toString()), yx));
    }
    
    @Command("backstage@unbanUser")
    public ByteResult unbanUser(@RequestParam("yx") final String yx, final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0 || StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.unbanUser(json.get("playerNames").toString(), yx));
    }
    
    @Command("backstage@getUserRecord")
    public ByteResult getUserBanList(@RequestParam("yx") final String yx, final Request request) {
        if (StringUtils.isBlank(yx)) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        return new ByteResult(this.systemService.getUserBanListByYx(yx));
    }
    
    @Command("backstage@getPlayerInfo")
    public ByteResult getPlayerInfo(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.getPlayerInfo(json.get("playerNames").toString(), json.get("userId").toString(), json.get("yx").toString()));
    }
    
    @Command("backstage@repay")
    public ByteResult repay(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.repay(json.get("orderId").toString(), Integer.parseInt(json.get("gold").toString()), json.get("userId").toString(), Integer.parseInt(json.get("playerId").toString()), json.get("yx").toString(), new Date(Long.parseLong(json.get("payDate").toString())), 0, request));
    }
    
    @Command("backstage@backpay")
    public ByteResult backpay(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.backpay(json.get("playerNames").toString(), Integer.parseInt(json.get("gold").toString()), json.get("partner").toString(), request));
    }
    
    @Command("backstage@yxPayData")
    public ByteResult yxPayData(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.yxPayData(new Date(Long.parseLong(json.get("startTime").toString())), new Date(Long.parseLong(json.get("endTime").toString())), json.get("yx").toString()));
    }
    
    @Command("backstage@yxPayHistory")
    public ByteResult yxPayHistory(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.yxPayHistory(new Date(Long.parseLong(json.get("startTime").toString())), new Date(Long.parseLong(json.get("endTime").toString())), json.get("yx").toString()));
    }
    
    @Command("backstage@getGiftContent")
    public ByteResult getGiftContent(final Request request) {
        return new ByteResult(this.systemService.getGiftContent());
    }
    
    @Command("backstage@addGift")
    public ByteResult addGift(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            return new ByteResult(this.systemService.addGift(json));
        }
        catch (Exception e) {
            BackStageAction.errorLogger.error("className:BackStageAction#methodName:addGift");
            BackStageAction.errorLogger.error(e.getMessage());
            BackStageAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, "add gift error : error message:" + e.getMessage()));
        }
    }
    
    @Command("backstage@sendMail")
    public ByteResult sendMail(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            return new ByteResult(this.systemService.sendMail(Integer.parseInt(json.get("noticeType").toString()), Integer.parseInt(json.get("playerLv").toString()), json.get("countryIds").toString(), json.get("playerNames").toString(), json.get("title").toString(), json.get("content").toString(), json.get("yx").toString()));
        }
        catch (Exception e) {
            BackStageAction.errorLogger.error("className:BackStageAction#methodName:sendMail");
            BackStageAction.errorLogger.error(e.getMessage());
            BackStageAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, "send mail error : error message:" + e.getMessage()));
        }
    }
    
    @Command("backstage@activity")
    public ByteResult activity(final Request request) {
        try {
            final byte[] bt = request.getContent();
            if (bt == null || bt.length == 0) {
                return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
            }
            final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
            return new ByteResult(this.systemService.activity(Integer.valueOf(json.get("type").toString()), json.get("startTime").toString(), json.get("endTime").toString(), json.get("content").toString()));
        }
        catch (Exception e) {
            BackStageAction.errorLogger.error("className:BackStageAction#methodName:activity");
            BackStageAction.errorLogger.error(e.getMessage());
            BackStageAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, "send activity error : error message:" + e.getMessage()));
        }
    }
    
    @Command("backstage@activityList")
    public ByteResult activityList(final Request request) {
        try {
            return new ByteResult(this.systemService.activityList());
        }
        catch (Exception e) {
            BackStageAction.errorLogger.error("className:BackStageAction#methodName:activity");
            BackStageAction.errorLogger.error(e.getMessage());
            BackStageAction.errorLogger.error(this, e);
            return new ByteResult(JsonBuilder.getJson(State.FAIL, "send activity error : error message:" + e.getMessage()));
        }
    }
    
    @Command("backstage@gmAuthority")
    public ByteResult gmAuthority(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.gmAuthority(json.get("playerNames").toString(), Integer.valueOf(json.get("gm").toString())));
    }
    
    @Command("backstage@getGmInfo")
    public ByteResult getGmInfo(final Request request) {
        return new ByteResult(this.systemService.getGmInfo());
    }
    
    @Command("backstage@consumeGold")
    public ByteResult consumeGold(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        return new ByteResult(this.systemService.consumeGold(json.get("playerNames").toString()));
    }
    
    @Command("backstage@rtblock")
    public ByteResult rtblock(final Request request) {
        final byte[] bt = request.getContent();
        if (bt == null || bt.length == 0) {
            return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
        }
        final JSONObject json = (JSONObject)JSON.parse(bt, new Feature[0]);
        final Object pIdsObj = json.get("playerIds");
        if (pIdsObj != null && !StringUtils.isBlank(pIdsObj.toString())) {
            return new ByteResult(this.systemService.rtblockByIds(pIdsObj.toString()));
        }
        final Object prNamesObj = json.get("playerNames");
        if (prNamesObj != null && !StringUtils.isBlank(prNamesObj.toString())) {
            return new ByteResult(this.systemService.rtblockByNames(prNamesObj.toString()));
        }
        return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011));
    }
}
