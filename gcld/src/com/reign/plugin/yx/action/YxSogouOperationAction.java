package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.util.*;
import com.reign.plugin.yx.common.*;
import java.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxSogouOperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String SOGOU = "sogou";
    private static final int AMOUNT_GOLD_RATE = 10;
    public static final String SOGOU_PAY_OK = "OK";
    public static final String SOGOU_PAY_INVALID_PARAM = "ERR_100";
    public static final String SOGOU_PAY_VALID_FAIL = "ERR_200";
    public static final String SOGOU_PAY_NO_USER = "ERR_300";
    public static final String SOGOU_PAY_INVALID_IP = "ERR_400";
    public static final String SOGOU_PAY_OTHER_ERROR = "ERR_500";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxSogouLogin")
    public ByteResult login(@RequestParam("gid") final int gid, @RequestParam("sid") final int sid, @RequestParam("uid") final String uid, @RequestParam("cm") final int cm, @RequestParam("time") final String time, @RequestParam("auth") final String auth, @RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        final String yx = "sogou";
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                return YxHelper.redirectUnlogPage(901, request, response);
            }
            if (StringUtils.isBlank(uid)) {
                YxSogouOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            long interval = System.currentTimeMillis() - DateUtil.parseDate(time).getTime();
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 10L) {
                YxSogouOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY_TIME_" + time, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("cm=");
            sb.append(cm);
            sb.append("&gid=");
            sb.append(gid);
            sb.append("&sid=");
            sb.append(sid);
            sb.append("&time=");
            sb.append(time);
            sb.append("&uid=");
            sb.append(uid);
            sb.append("&" + PluginContext.configuration.getSogouSecret(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), auth)) {
                YxSogouOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + auth, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            String isAdult = "0";
            if (cm == 2) {
                isAdult = "1";
            }
            final Session session = this.yxOperation.login(yx, uid, "", "", isAdult, yxSource, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL(yx));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxSogouOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxSogouOperationAction.errorLog.info("login_fail_EXCEPTION", e);
            YxSogouOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yxSogouPay")
    public ByteResult pay(@RequestParam("gid") final int gid, @RequestParam("sid") final int sid, @RequestParam("uid") final String uid, @RequestParam("role") final String role, @RequestParam("oid") final String oid, @RequestParam("date") final String date, @RequestParam("amount1") final int amount1, @RequestParam("amount2") final int amount2, @RequestParam("time") final String time, @RequestParam("auth") final String auth, final Request request, final Response response) {
        String rtn = "0";
        final String yx = "sogou";
        if (StringUtils.isBlank(yx)) {
            rtn = this.translateToSogouErrorCode(901);
            return new ByteResult(rtn.getBytes());
        }
        if (StringUtils.isBlank(uid)) {
            rtn = this.translateToSogouErrorCode(902);
            return new ByteResult(rtn.getBytes());
        }
        if (amount1 <= 0) {
            rtn = this.translateToSogouErrorCode(904);
            return new ByteResult(rtn.getBytes());
        }
        if (StringUtils.isBlank(oid)) {
            rtn = this.translateToSogouErrorCode(907);
            return new ByteResult(rtn.getBytes());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("amount1=");
        sb.append(amount1);
        sb.append("&amount2=");
        sb.append(amount2);
        sb.append("&date=");
        sb.append(date);
        sb.append("&gid=");
        sb.append(gid);
        sb.append("&oid=");
        sb.append(oid);
        sb.append("&role=");
        sb.append((role == null) ? "" : role);
        sb.append("&sid=");
        sb.append(sid);
        sb.append("&time=");
        sb.append(time);
        sb.append("&uid=");
        sb.append(uid);
        sb.append("&" + PluginContext.configuration.getSogouSecret(yx));
        if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), auth)) {
            YxSogouOperationAction.opReport.info(MessageFormatter.format("pay fail because of wrong ticket:yx={0},userId={1},playerId={2},orderId={3},gold={4},sign={5},osign={6}", new Object[] { yx, uid, 0, oid, amount1, auth, CodecUtil.md5(sb.toString()) }));
            rtn = this.translateToSogouErrorCode(909);
            return new ByteResult(rtn.getBytes());
        }
        final int playerId = this.yxOperation.getDefaultPayPlayer(uid, yx);
        final int gold = amount1 * 10;
        final Tuple<Integer, Integer> result = this.yxOperation.pay(oid, playerId, uid, yx, gold, request);
        if (result.left == 1) {
            YxSogouOperationAction.opReport.info(MessageFormatter.format("pay success:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { yx, uid, oid, result.right, gold }));
            rtn = this.translateToSogouErrorCode(result.left);
        }
        else {
            YxSogouOperationAction.opReport.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { yx, uid, oid, result.right, gold, result.left }));
            rtn = this.translateToSogouErrorCode(result.left);
        }
        return new ByteResult(rtn.getBytes());
    }
    
    @Command("yxSogouGetPlayersByUid")
    public ByteResult getPlayersByUid(@RequestParam("gid") final int gid, @RequestParam("sid") final int sid, @RequestParam("uid") final String uid, @RequestParam("time") final String time, @RequestParam("auth") final String auth, final Request request, final Response response) {
        final String yx = "sogou";
        String errorCode = "";
        if (StringUtils.isBlank(uid)) {
            errorCode = "ERR_100";
            return new ByteResult(errorCode.getBytes());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("gid=");
        sb.append(gid);
        sb.append("&sid=");
        sb.append(sid);
        sb.append("&time=");
        sb.append(time);
        sb.append("&uid=");
        sb.append(uid);
        sb.append("&" + PluginContext.configuration.getSogouSecret(yx));
        if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), auth)) {
            errorCode = "ERR_200";
            return new ByteResult(errorCode.getBytes());
        }
        final StringBuilder playersInfo = new StringBuilder();
        final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(uid, yx);
        if (playerList == null || playerList.size() <= 0) {
            errorCode = "ERR_300";
            return new ByteResult(errorCode.getBytes());
        }
        for (final YxPlayerInfo player : playerList) {
            playersInfo.append(String.valueOf(player.getPlayerName()) + "\t" + player.getLv());
            playersInfo.append("\r\n");
        }
        return new ByteResult(playersInfo.toString().getBytes());
    }
    
    @Command("yxSogouGetPlayersNum")
    public ByteResult getOnlinePlayersNum(@RequestParam("gid") final int gid, @RequestParam("sid") final int sid, @RequestParam("time") final String time, @RequestParam("auth") final String auth, final Request request, final Response response) {
        final String yx = "sogou";
        String errorCode = "";
        if (gid < 0 || sid < 0) {
            errorCode = "ERR_100";
            return new ByteResult(errorCode.getBytes());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("gid=");
        sb.append(gid);
        sb.append("&sid=");
        sb.append(sid);
        sb.append("&time=");
        sb.append(time);
        sb.append("&" + PluginContext.configuration.getSogouSecret(yx));
        if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), auth)) {
            errorCode = "ERR_200";
            return new ByteResult(errorCode.getBytes());
        }
        final int onlinePlayersNum = this.yxOperation.getOnlinePlayersNumber(yx);
        return new ByteResult(new StringBuilder().append(onlinePlayersNum).toString().getBytes());
    }
    
    public String translateToSogouErrorCode(final int errorCode) {
        if (1 == errorCode || 5 == errorCode) {
            return "OK";
        }
        if (908 == errorCode) {
            return "ERR_400";
        }
        if (909 == errorCode || 918 == errorCode) {
            return "ERR_500";
        }
        if (902 == errorCode || 903 == errorCode || 904 == errorCode || 907 == errorCode || 909 == errorCode || 910 == errorCode || 4 == errorCode) {
            return "ERR_100";
        }
        if (2 == errorCode || 3 == errorCode) {
            return "ERR_300";
        }
        return "ERR_500";
    }
}
