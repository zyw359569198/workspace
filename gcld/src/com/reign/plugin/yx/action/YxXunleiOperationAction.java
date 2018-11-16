package com.reign.plugin.yx.action;

import javax.servlet.http.*;
import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.domain.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.plugin.yx.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import com.reign.plugin.yx.common.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.util.*;
import java.net.*;
import java.io.*;
import com.reign.util.codec.*;
import java.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxXunleiOperationAction extends HttpServlet
{
    @Autowired
    IYxOperation yxOperation;
    private String XUNLEI;
    private static final long serialVersionUID = -5851796979763052578L;
    private static final Log opReport;
    private static final byte[] cooperateserverkey;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        cooperateserverkey = new byte[] { -25, -82, -2, 109, 2, -44, -85, 27, 20, -12, 4, 46, -104, -46, -81, 23 };
    }
    
    public YxXunleiOperationAction() {
        this.XUNLEI = "xunlei";
    }
    
    @Command("yxXunleiAuth")
    public ByteResult XunleiAuth(@RequestParam("userinfo") final String userInfo, @RequestParam("serverid") final String serverId, @RequestParam("svrflag") final String serverFlag, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        if (StringUtils.isBlank(userInfo)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_USERINFO_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1004));
            return new ByteResult("result=1".getBytes());
        }
        if (StringUtils.isBlank(serverId)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_SERVERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1005));
            return new ByteResult("result=1".getBytes());
        }
        if (StringUtils.isBlank(serverFlag)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_SERVERFLAG_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1006));
            return new ByteResult("result=1".getBytes());
        }
        byte[] decodebytes = new byte[112];
        byte[] authbytes = new byte[100];
        String userId = "";
        final String yxSource = "";
        final byte[] userinfobytes = Base64.decode(userInfo);
        if (userinfobytes == null) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_WRONG_BASE64", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1007));
            return new ByteResult("result=1".getBytes());
        }
        try {
            decodebytes = AESUtil.decrypt(userinfobytes, AESUtil.makeKey(YxXunleiOperationAction.cooperateserverkey));
            authbytes = Arrays.copyOfRange(decodebytes, 0, 100);
            final XunleiLoginResult result = this.loginXunleiAuthServer(serverFlag, authbytes);
            if (result != null && "0000".equalsIgnoreCase(result.getCode())) {
                userId = result.getCustomerId();
                final byte isAdult = (byte)((result.getAdultFlag() != 1) ? 1 : 0);
                final long timestamp = System.currentTimeMillis();
                final String ticket = this.getMD5TicketForXunleiLogin(userId, serverId, timestamp, this.XUNLEI);
                final String url = this.getXunLeiLoginURL(this.XUNLEI, result.getCustomerId(), serverId, timestamp, isAdult, yxSource, ticket);
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(("result=0;url=" + url).getBytes()), 1));
                return new ByteResult(("result=0;url=" + url).getBytes());
            }
            if (result == null) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_AUTH_RETURN_RESULT_IS_NULL", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1007));
            }
            else {
                int authErrorCode = 0;
                try {
                    authErrorCode = Integer.parseInt(result.getCode());
                }
                catch (NumberFormatException e) {
                    YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_AUTH_NUMBER_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1008));
                    return new ByteResult("result=1".getBytes());
                }
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_AUTH_FAILED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), authErrorCode));
            }
            return new ByteResult("result=1".getBytes());
        }
        catch (Exception e2) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiAuth", "xunlei_auth_fail_AUTH_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult("result=1".getBytes()), 1009));
            return new ByteResult("result=1".getBytes());
        }
    }
    
    @Command("yxXunleiLogin")
    public ByteResult Login(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("serverId") final String serverId, @RequestParam("tstamp") final long tstamp, @RequestParam("isAdult") final String isAdult, @RequestParam("yxSource") final String yxSource, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return YxHelper.redirectUnlogPage(901, request, response);
            }
            if (StringUtils.isBlank(userId)) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(serverId)) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
                return YxHelper.redirectUnlogPage(1005, request, response);
            }
            if (tstamp <= 0L) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            if (StringUtils.isBlank(ticket)) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            long interval = System.currentTimeMillis() - tstamp;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_TIMESTAMP_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1010)), 1010));
                return YxHelper.redirectUnlogPage(1010, request, response);
            }
            if (!YxHelper.isTicketPass(this.getMD5TicketForXunleiLogin(userId, serverId, tstamp, this.XUNLEI), ticket)) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            final Session session = this.yxOperation.login(yx, userId, "", "", isAdult, yxSource, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL(yx));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiLogin", "yxXunleiLogin_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.redirectUnlogPage(6, request, response);
        }
    }
    
    @Command("yxXunleiPay")
    public ByteResult pay(@RequestParam("orderid") final String orderid, @RequestParam("roleid") final String roleid, @RequestParam("user") final String userid, @RequestParam("gold") final int gold, @RequestParam("money") final int money, @RequestParam("time") final long time, @RequestParam("sign") final String sign, @RequestParam("server") final int serverid, @RequestParam("ip") final String ip, @RequestParam("ext") final String ext, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String ipXunlei = YxHelper.getIp(request);
        final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(this.XUNLEI);
        final String passIp = PluginContext.configuration.getPassedIP(this.XUNLEI);
        if (isLimitYxIP && (ipXunlei == null || passIp == null || passIp.indexOf(ipXunlei) == -1)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
            return new ByteResult(String.valueOf(908).getBytes());
        }
        if (StringUtils.isBlank(orderid)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_ORDERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(907)), 907));
            return new ByteResult(String.valueOf(907).getBytes());
        }
        if (StringUtils.isBlank(roleid)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_PLAYERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(903)), 903));
            return new ByteResult(String.valueOf(903).getBytes());
        }
        if (StringUtils.isBlank(userid)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_USERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
            return new ByteResult(String.valueOf(902).getBytes());
        }
        if (gold <= 0 || money <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(904)), 904));
            return new ByteResult(String.valueOf(904).getBytes());
        }
        if (time <= 0L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
            return new ByteResult(String.valueOf(910).getBytes());
        }
        if (serverid <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
            return new ByteResult(String.valueOf(-5).getBytes());
        }
        if (StringUtils.isBlank(sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
            return new ByteResult(String.valueOf(922).getBytes());
        }
        String userId = "";
        try {
            userId = URLDecoder.decode(userid, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_URLDECODE_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1011)), 1011));
            return new ByteResult(String.valueOf(1011).getBytes());
        }
        final StringBuilder params = new StringBuilder();
        params.append(orderid);
        params.append(userId);
        params.append(gold);
        params.append(money);
        params.append(serverid);
        params.append(ip);
        final String ticket = this.getMD5TicketForXunleiPay(orderid, userId, gold, money, time, this.XUNLEI);
        if (!ticket.equals(sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
            return new ByteResult(String.valueOf(909).getBytes());
        }
        long interval = System.currentTimeMillis() / 1000L - time;
        if (interval < 0L) {
            interval *= -1L;
        }
        if (interval / 60L > 5L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_TIMESTAMP_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1010)), 1010));
            return new ByteResult(String.valueOf(1010).getBytes());
        }
        int playerId = 0;
        try {
            playerId = Integer.parseInt(roleid);
        }
        catch (NumberFormatException e2) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_AUTH_NUMBER_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1008)), 1008));
            return new ByteResult(String.valueOf(1008).getBytes());
        }
        try {
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderid, playerId, userId, this.XUNLEI, gold, request);
            if (1 == result.left) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            if (5 == result.left) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_ORDERID_EXISTED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(2)), 2));
                return new ByteResult(String.valueOf(2).getBytes());
            }
            if (2 == result.left) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_USER_NOT_EXIST", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-3)), -3));
                return new ByteResult(String.valueOf(-3).getBytes());
            }
            if (3 == result.left) {
                YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_WRONG_USER", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-8)), -8));
                return new ByteResult(String.valueOf(-8).getBytes());
            }
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(result.left)), result.left));
            return new ByteResult(String.valueOf(result.left).getBytes());
        }
        catch (Exception e3) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiPay", "yxXunleiPay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return new ByteResult(String.valueOf(6).getBytes());
        }
    }
    
    @Command("yxXunleiQueryDefaultPlayer")
    public ByteResult queryDefaultPlayer(@RequestParam("user") final String URLEncodeUser, @RequestParam("time") final long time, @RequestParam("sign") final String sign, @RequestParam("server") final int serverid, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String ip = YxHelper.getIp(request);
        final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(this.XUNLEI);
        final String passIp = PluginContext.configuration.getPassedIP(this.XUNLEI);
        if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(908));
        }
        if (StringUtils.isBlank(URLEncodeUser)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_USERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(902));
        }
        if (time <= 0L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(910));
        }
        if (StringUtils.isBlank(sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(922));
        }
        if (serverid <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1005));
        }
        String userId = "";
        try {
            userId = URLDecoder.decode(URLEncodeUser, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_URLDECODE_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1011)), 1011));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1011));
        }
        final String ticket = this.getMD5TicketForXunleiQueryPlayer(userId, time, this.XUNLEI);
        if (!YxHelper.isTicketPass(ticket, sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-2));
        }
        long interval = System.currentTimeMillis() / 1000L - time;
        if (interval < 0L) {
            interval *= -1L;
        }
        if (interval / 60L > 5L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_TIMESTAMP_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1010)), 1010));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1010));
        }
        final int playerId = this.yxOperation.getDefaultPayPlayer(userId, this.XUNLEI);
        final YxPlayerInfo YPI = this.yxOperation.getPlayerById(playerId);
        if (YPI == null) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_fail_PLAYER_NOT_EXIST", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-3)), -3));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-3));
        }
        YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryDefaultPlayer", "yxXunleiQueryDefaultPlayer_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
        return new ByteResult(this.buildJsonForXunleiDefaultQuery(YPI, userId));
    }
    
    @Command("yxXunleiQueryAllPlayers")
    public ByteResult queryAllPlayers(@RequestParam("user") final String URLEncodeUser, @RequestParam("time") final long time, @RequestParam("sign") final String sign, @RequestParam("server") final int serverid, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String ip = YxHelper.getIp(request);
        final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(this.XUNLEI);
        final String passIp = PluginContext.configuration.getPassedIP(this.XUNLEI);
        if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(908));
        }
        if (StringUtils.isBlank(URLEncodeUser)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_USERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(902));
        }
        if (time <= 0L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(910));
        }
        if (StringUtils.isBlank(sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(922));
        }
        if (serverid <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1005));
        }
        String userId = "";
        try {
            userId = URLDecoder.decode(URLEncodeUser, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_URLDECODE_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1011)), 1011));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1011));
        }
        final String ticket = this.getMD5TicketForXunleiQueryPlayer(userId, time, this.XUNLEI);
        if (!YxHelper.isTicketPass(ticket, sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-2));
        }
        long interval = System.currentTimeMillis() / 1000L - time;
        if (interval < 0L) {
            interval *= -1L;
        }
        if (interval / 60L > 5L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_TIMESTAMP_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1010)), 1010));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1010));
        }
        final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(userId, this.XUNLEI);
        if (playerList.size() <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_fail_PLAYER_NOT_EXIST", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-3)), -3));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-3));
        }
        YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryAllPlayers", "yxXunleiQueryAllPlayers_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
        return new ByteResult(this.buildJsonSuccessMsgForXunleiQuery(playerList, userId));
    }
    
    @Command("yxXunleiQueryOnlineTime")
    public ByteResult queryOnlineTime(@RequestParam("user") final String URLEncodeUser, @RequestParam("time") final long time, @RequestParam("sign") final String sign, @RequestParam("server") final int serverid, final Request request, final Response response) {
        final JsonDocument doc = new JsonDocument();
        final long start = System.currentTimeMillis();
        final String ip = YxHelper.getIp(request);
        final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(this.XUNLEI);
        final String passIp = PluginContext.configuration.getPassedIP(this.XUNLEI);
        if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-2)), 908));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-2));
        }
        if (StringUtils.isBlank(URLEncodeUser)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_USERID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(902));
        }
        if (time <= 0L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(910));
        }
        if (StringUtils.isBlank(sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(922));
        }
        if (serverid <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1005));
        }
        String userId = "";
        try {
            userId = URLDecoder.decode(URLEncodeUser, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_URLDECODE_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1011)), 1011));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(1011));
        }
        final String ticket = this.getMD5TicketForXunleiQueryOnlieTime(userId, time, this.XUNLEI);
        if (!YxHelper.isTicketPass(ticket, sign)) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-2)), 909));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-2));
        }
        long interval = System.currentTimeMillis() / 1000L - time;
        if (interval < 0L) {
            interval *= -1L;
        }
        if (interval / 60L > 5L) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_TIMESTAMP_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-2)), 1010));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-2));
        }
        final int playerId = this.yxOperation.getDefaultPayPlayer(userId, this.XUNLEI);
        if (playerId <= 0) {
            YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_fail_PLAYER_NOT_EXIST", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-3)), -3));
            return new ByteResult(this.buildJsonErrorMsgForXunleiQuery(-3));
        }
        final int dailyOnlineTime = this.yxOperation.getDailyOnlineTime(playerId);
        doc.startObject();
        doc.createElement("status", 0);
        doc.startObject("data");
        doc.createElement("user", CommonUtil.chineseToUnicode(userId));
        doc.createElement("onlineTime", dailyOnlineTime);
        doc.endObject();
        doc.endObject();
        YxXunleiOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxXunleiQueryOnlineTime", "yxXunleiQueryOnlineTime_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(CommonUtil.trimLine(doc.toByte())), 1));
        return new ByteResult(CommonUtil.trimLine(doc.toByte()));
    }
    
    public XunleiLoginResult loginXunleiAuthServer(final String serverFlag, final byte[] userinfo) throws UnknownHostException, IOException, AESException {
        String host = "";
        int port;
        if ("11".equals(serverFlag)) {
            host = PluginContext.configuration.getXunleiAuthServerHost1(this.XUNLEI);
            port = Integer.parseInt(PluginContext.configuration.getXunleiAuthServerPort1(this.XUNLEI));
        }
        else {
            if (!"12".equals(serverFlag)) {
                return null;
            }
            host = PluginContext.configuration.getXunleiAuthServerHost2(this.XUNLEI);
            port = Integer.parseInt(PluginContext.configuration.getXunleiAuthServerPort2(this.XUNLEI));
        }
        String gameId = PluginContext.configuration.getXunleiGameId(this.XUNLEI);
        String version = PluginContext.configuration.getXunleiVersion(this.XUNLEI);
        String cmd = PluginContext.configuration.getXunleiCmd(this.XUNLEI);
        Socket socket = null;
        try {
            Object publicKkey = null;
            try {
                publicKkey = AESUtil.makeKey(YxXunleiOperationAction.cooperateserverkey);
            }
            catch (AESException e) {
                e.printStackTrace();
            }
            cmd = CommonUtil.formatString(cmd, 10, ' ', true);
            gameId = CommonUtil.formatString(gameId, 5, ' ', true);
            version = CommonUtil.formatString(version, 5, ' ', true);
            final String sn = "12";
            final byte[] requestBytesbefore = new byte[112];
            System.arraycopy(cmd.getBytes(), 0, requestBytesbefore, 0, 10);
            System.arraycopy(sn.getBytes(), 0, requestBytesbefore, 10, 2);
            System.arraycopy(userinfo, 0, requestBytesbefore, 12, 100);
            byte[] requestBytes = null;
            try {
                requestBytes = AESUtil.encrypt(requestBytesbefore, publicKkey);
            }
            catch (AESException e2) {
                e2.printStackTrace();
            }
            final byte[] requestLengthBytes = CommonUtil.intToByteArray(requestBytes.length + gameId.length() + version.length());
            socket = new Socket(host, port);
            socket.setSoTimeout(40000);
            final OutputStream out = socket.getOutputStream();
            final byte[] sendData = new byte[14 + requestBytes.length];
            System.arraycopy(requestLengthBytes, 0, sendData, 0, 4);
            System.arraycopy(gameId.getBytes(), 0, sendData, 4, 5);
            System.arraycopy(version.getBytes(), 0, sendData, 9, 5);
            System.arraycopy(requestBytes, 0, sendData, 14, requestBytes.length);
            out.write(sendData);
            final InputStream in = socket.getInputStream();
            final byte[] responseLengthBytes = new byte[4];
            in.read(responseLengthBytes);
            byte[] responseBytes = new byte[CommonUtil.byteArrayToInt(responseLengthBytes)];
            in.read(responseBytes);
            socket.close();
            final byte[] receivedGameId = new byte[10];
            System.arraycopy(responseBytes, 0, receivedGameId, 0, 10);
            final byte[] receivedDate = new byte[responseBytes.length - 10];
            System.arraycopy(responseBytes, 10, receivedDate, 0, receivedDate.length);
            responseBytes = AESUtil.decrypt(receivedDate, publicKkey);
            final String response = new String(responseBytes);
            if (response != null && response.length() > 4) {
                return new XunleiLoginResult(response);
            }
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
        if (socket != null) {
            socket.close();
        }
        return null;
    }
    
    private String getXunLeiLoginURL(final String yx, final String customerId, final String serverId, final long tstamp, final byte isAdult, final String yxSource, final String ticket) {
        final String basicUrl = PluginContext.configuration.getXunleiLoginUrl(this.XUNLEI);
        final StringBuilder sb = new StringBuilder();
        sb.append("&yx=");
        sb.append(yx);
        sb.append("&userId=");
        sb.append(customerId);
        sb.append("&serverId=");
        sb.append(serverId);
        sb.append("&tstamp=");
        sb.append(tstamp);
        sb.append("&isAdult=");
        sb.append(isAdult);
        sb.append("&yxSource=");
        sb.append(yxSource);
        sb.append("&ticket=");
        sb.append(ticket);
        return String.valueOf(basicUrl) + sb.toString();
    }
    
    private String getMD5TicketForXunleiLogin(final String userId, final String serverId, final long timestamp, final String yx) {
        final StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(serverId);
        sb.append(timestamp);
        sb.append(PluginContext.configuration.getLoginKey(yx));
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForXunleiPay(final String orderId, final String user, final int gold, final int money, final long timestamp, final String yx) {
        final StringBuilder sb = new StringBuilder();
        sb.append(orderId);
        sb.append(user);
        sb.append(new StringBuilder(String.valueOf(gold)).toString());
        sb.append(new StringBuilder(String.valueOf(money)).toString());
        sb.append(timestamp);
        sb.append(PluginContext.configuration.getPayKey(yx));
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForXunleiQueryPlayer(final String user, final long timestamp, final String yx) {
        final StringBuilder sb = new StringBuilder();
        sb.append(user);
        sb.append(timestamp);
        sb.append(PluginContext.configuration.getQueryKey(yx));
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForXunleiQueryOnlieTime(final String user, final long timestamp, final String yx) {
        final StringBuilder sb = new StringBuilder();
        sb.append(user);
        sb.append(timestamp);
        sb.append(PluginContext.configuration.getQueryKey(yx));
        return CodecUtil.md5(sb.toString());
    }
    
    private byte[] buildJsonSuccessMsgForXunleiQuery(final List<YxPlayerInfo> list, final String userId) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray();
        for (final YxPlayerInfo YPI : list) {
            doc.startObject();
            doc.createElement("status", 0);
            doc.startObject("data");
            doc.createElement("user", CommonUtil.chineseToUnicode(userId));
            doc.createElement("nickname", CommonUtil.chineseToUnicode(YPI.getPlayerName()));
            doc.createElement("roleid", YPI.getPlayerId());
            doc.createElement("sex", "");
            doc.createElement("career", "");
            doc.createElement("level", YPI.getLv());
            doc.createElement("rank", "");
            doc.createElement("gold", "");
            doc.createElement("silver", "");
            doc.createElement("copper", "");
            doc.createElement("fighting_effect", "");
            doc.createElement("club_name", "");
            doc.endObject();
            doc.endObject();
        }
        doc.endArray();
        return CommonUtil.trimLine(doc.toByte());
    }
    
    private byte[] buildJsonErrorMsgForXunleiQuery(final int statusCode) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("status", statusCode);
        doc.endObject();
        return doc.toByte();
    }
    
    private byte[] buildJsonForXunleiDefaultQuery(final YxPlayerInfo YPI, final String userId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("status", 0);
        doc.startObject("data");
        doc.createElement("user", CommonUtil.chineseToUnicode(userId));
        doc.createElement("nickname", CommonUtil.chineseToUnicode(YPI.getPlayerName()));
        doc.createElement("roleid", YPI.getPlayerId());
        doc.createElement("sex", "");
        doc.createElement("career", "");
        doc.createElement("level", YPI.getLv());
        doc.createElement("rank", "");
        doc.createElement("gold", "");
        doc.createElement("silver", "");
        doc.createElement("copper", "");
        doc.createElement("fighting_effect", "");
        doc.createElement("club_name", "");
        doc.endObject();
        doc.endObject();
        return CommonUtil.trimLine(doc.toByte());
    }
}
