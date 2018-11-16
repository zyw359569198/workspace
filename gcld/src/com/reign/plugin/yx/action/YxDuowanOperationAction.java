package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.plugin.yx.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import java.net.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.plugin.yx.result.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.common.*;
import com.reign.util.*;
import java.util.*;
import java.text.*;
import java.io.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false"), @View(name = "plain", type = PlainView.class, compress = "false") })
public class YxDuowanOperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String DUOWAN = "duowan";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxDuowanOperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxDuowanLogin")
    public ByteResult login(@RequestParam("account") final String account, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("time") final long time, @RequestParam("fm") final int fm, @RequestParam("backurl") final String backurl, @RequestParam("dwservId") final String dwservId, @RequestParam("tocken") final String tocken, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final int isAdult = (fm == 1) ? fm : 0;
            if (StringUtils.isBlank(account)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1001)), 1001));
                return YxHelper.redirectUnlogPage(1001, request, response);
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(924)), 924));
                return YxHelper.redirectUnlogPage(924, request, response);
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1005)), 1005));
                return YxHelper.redirectUnlogPage(1005, request, response);
            }
            if (StringUtils.isBlank(backurl)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_BACKURL_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1002)), 1002));
                return YxHelper.redirectUnlogPage(1002, request, response);
            }
            if (StringUtils.isBlank(dwservId)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_DWSERVID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1003)), 1003));
                return YxHelper.redirectUnlogPage(1003, request, response);
            }
            if (StringUtils.isBlank(tocken)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TOCKEN_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1004)), 1004));
                return YxHelper.redirectUnlogPage(1004, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(account);
            sb.append(fm);
            sb.append(time);
            sb.append(game);
            sb.append(server);
            sb.append(URLDecoder.decode(backurl, "UTF-8"));
            sb.append(dwservId);
            sb.append(tocken);
            sb.append(PluginContext.configuration.getLoginKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            final Session session = this.yxOperation.login("duowan", account, "", "", new StringBuilder().append(isAdult).toString(), "", request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL("duowan"));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("login_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yxDuowanPlayerInfo")
    public PlainResult queryPlayer(@RequestParam("account") final String account, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("time") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(account)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 902));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 1001));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 924));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 910));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-1))), 1005));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-1)));
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 922));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-1))), 908));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-1)));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(account);
            sb.append(game);
            sb.append(server);
            sb.append(time);
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-1))), 909));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-1)));
            }
            final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(account, "duowan");
            if (playerList.size() < 1) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-2))), 1));
                return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-2)));
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("retcode", 0);
            doc.startArray("roleinfo");
            for (final YxPlayerInfo player : playerList) {
                player.buildDuowanJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new PlainResult(doc.toByte());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getSimpleJson("retcode", (-3))), 6));
            return new PlainResult(JsonBuilder.getSimpleJson("retcode", (-3)));
        }
    }
    
    @Command("yxDuowanPay")
    public ByteResult pay(@RequestParam("account") final String userId, @RequestParam("orderid") final String orderId, @RequestParam("rmb") final int rmb, @RequestParam("coin") final int gold, @RequestParam("sign") final String sign, @RequestParam("type") final String type, @RequestParam("time") final long time, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("nickname") String playerName, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-21).getBytes()), 908));
                return new ByteResult(String.valueOf(-21).getBytes());
            }
            if (StringUtils.isBlank(userId)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 902));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (StringUtils.isBlank(orderId)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 907));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (rmb <= 0) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_RMB_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 921));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (gold <= 0) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 904));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 922));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 910));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-15).getBytes()), 1005));
                return new ByteResult(String.valueOf(-15).getBytes());
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 1001));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 924));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            if (StringUtils.isBlank(playerName)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_PLAYER_NAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-10).getBytes()), 1006));
                return new ByteResult(String.valueOf(-10).getBytes());
            }
            playerName = URLDecoder.decode(playerName, "UTF-8");
            final YxPlayerInfo ypi = this.yxOperation.queryPlayer(userId, "duowan", playerName);
            if (ypi == null) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ERROR_WRONG_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-19).getBytes()), 3));
                return new ByteResult(String.valueOf(-19).getBytes());
            }
            final int playerId = ypi.getPlayerId();
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(orderId);
            sb.append(rmb);
            sb.append(gold);
            sb.append(game);
            sb.append(server);
            sb.append(playerName);
            sb.append(time);
            sb.append(PluginContext.configuration.getPayKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-11).getBytes()), 909));
                return new ByteResult(String.valueOf(-11).getBytes());
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, playerId, userId, "duowan", gold, request);
            if (result.left == 1) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(1).getBytes()), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            if (result.left == 5) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ERROR_EXISTS_ORDERID", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-18).getBytes()), result.left));
                return new ByteResult(String.valueOf(-18).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-20).getBytes()), result.left));
            return new ByteResult(String.valueOf(-20).getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("pay_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-100).getBytes()), 6));
            return new ByteResult(String.valueOf(-100).getBytes());
        }
    }
    
    @Command("yxDuowanBanChat")
    public ByteResult banChat(@RequestParam("accounts") final String userIds, @RequestParam("keeptime") final int keepTime, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 908));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(userIds)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 902));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (keepTime <= 0) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_KEEP_TIME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1007));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1001));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 924));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 910));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1005));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 922));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userIds);
            sb.append(keepTime);
            sb.append(game);
            sb.append(server);
            sb.append(time);
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final boolean result = this.yxOperation.banChat(userIds, keepTime, "duowan");
            if (result) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(1).getBytes()), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("yxDuowanBanChat_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanChat", "yxDuowanBanChat_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 6));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
    }
    
    @Command("yxDuowanUnbanChat")
    public ByteResult unbanChat(@RequestParam("accounts") final String userIds, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 908));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(userIds)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 902));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1001));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 924));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 910));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1005));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 922));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userIds);
            sb.append(game);
            sb.append(server);
            sb.append(time);
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final boolean result = this.yxOperation.unbanChat(userIds, "duowan");
            if (result) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(1).getBytes()), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("yxDuowanUnbanChat_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanChat", "yxDuowanUnbanChat_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 6));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
    }
    
    @Command("yxDuowanBanUser")
    public ByteResult banUser(@RequestParam("accounts") final String userIds, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 908));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(userIds)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 902));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1001));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 924));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 910));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1005));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 922));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userIds);
            sb.append(game);
            sb.append(server);
            sb.append(time);
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final boolean result = this.yxOperation.banUser(userIds, "duowan");
            if (result) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(1).getBytes()), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_NO_USER", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1008));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("yxDuowanBanUser_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanBanUser", "yxDuowanBanUser_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 6));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
    }
    
    @Command("yxDuowanUnbanUser")
    public ByteResult unbanUser(@RequestParam("accounts") final String userIds, @RequestParam("game") final String game, @RequestParam("server") final String server, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 908));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(userIds)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 902));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(game)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_GAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1001));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 924));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 910));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 1005));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 922));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userIds);
            sb.append(game);
            sb.append(server);
            sb.append(time);
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            final boolean result = this.yxOperation.unbanUser(userIds, "duowan");
            if (result) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(1).getBytes()), 1));
                return new ByteResult(String.valueOf(1).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 909));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("yxDuowanUnbanUser_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanUnbanUser", "yxDuowanUnbanUser_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 6));
            return new ByteResult(String.valueOf(-1).getBytes());
        }
    }
    
    @Command("yxDuowanQueryUserId")
    public PlainResult queryUserId(@RequestParam("server") final String server, @RequestParam("nickname") String playerName, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("duowan");
            final String passIp = PluginContext.configuration.getPassedIP("duowan");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 908));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            if (StringUtils.isBlank(server)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 924));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            if (StringUtils.isBlank(playerName)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_PLAYER_NAME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 1006));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            playerName = URLDecoder.decode(playerName, "UTF-8");
            if (time <= 0L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 910));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 1005));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 922));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getQueryKey("duowan"));
            sb.append(server);
            sb.append(playerName);
            sb.append(time);
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), 909));
                return new PlainResult(String.valueOf(-2).getBytes());
            }
            final YxPlayerInfo yxPlayerInfo = this.yxOperation.queryPlayerByPlayerNameAndYx(playerName, "duowan");
            if (yxPlayerInfo == null) {
                YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_PLAYER_NOT_EXSIT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 916));
                return new PlainResult(String.valueOf(-1).getBytes());
            }
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(yxPlayerInfo.getUserId().getBytes()), 1));
            return new PlainResult(yxPlayerInfo.getUserId().getBytes());
        }
        catch (Exception e) {
            YxDuowanOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
            YxDuowanOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxDuowanQueryUserId", "yxDuowanQueryUserId_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-3).getBytes()), 6));
            return new PlainResult(String.valueOf(-3).getBytes());
        }
    }
    
    public static void main(final String[] args) throws UnsupportedEncodingException {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("retcode", 0);
        doc.startArray("roleinfo");
        final List<YxPlayerInfo> playerList = new ArrayList<YxPlayerInfo>();
        final YxPlayerInfo pi = new YxPlayerInfo(1, "playerName", 1, 1, 1, "2", new Date());
        playerList.add(pi);
        for (final YxPlayerInfo player : playerList) {
            player.buildDuowanJson(doc);
        }
        doc.endArray();
        doc.endObject();
        System.out.print(new String(doc.toByte()));
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.print(sdf.format(new Date()));
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("retcode", 1);
        doc2.startObject("date");
        doc2.createElement("roleTotal", 12);
        doc2.createElement("loginTotal", 34);
        doc2.createElement("maxOnline", 56);
        doc2.endObject();
        doc2.endObject();
        System.out.print(new String(doc2.toByte()));
    }
}
