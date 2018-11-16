package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.result.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import java.util.*;
import java.net.*;
import com.reign.plugin.yx.common.*;
import com.alibaba.fastjson.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class Yx360OperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String SECURITY_GUARD_360 = "360";
    private static long monitorCD;
    ByteResult PAY_SUCCESS;
    ByteResult PAY_DUPLICATE;
    ByteResult PAY_FAIL;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(Yx360OperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        Yx360OperationAction.monitorCD = 0L;
    }
    
    public Yx360OperationAction() {
        this.PAY_SUCCESS = new ByteResult("1".getBytes());
        this.PAY_DUPLICATE = new ByteResult("2".getBytes());
        this.PAY_FAIL = new ByteResult("0".getBytes());
    }
    
    @Command("yx360Login")
    public ByteResult login(@RequestParam("qid") final long qid, @RequestParam("server_id") final String serverId, @RequestParam("time") final long time, @RequestParam("sign") final String sign, @RequestParam("isAdult") int isAdult, @RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String yx = "360";
            final String userId = new StringBuilder().append(qid).toString();
            isAdult = ((isAdult == 1) ? isAdult : 0);
            if (StringUtils.isBlank(userId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(serverId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(924)), 924));
                return YxHelper.redirectUnlogPage(924, request, response);
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_YX_360_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(-2)), -2));
                return YxHelper.redirectUnlogPage(-2, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("qid=");
            sb.append(userId);
            sb.append("&time=");
            sb.append(time);
            sb.append("&server_id=");
            sb.append(serverId);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            final Session session = this.yxOperation.login(yx, userId, "", "", new StringBuilder().append(isAdult).toString(), yxSource, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL(yx));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            Yx360OperationAction.errorLog.error("login_fail_EXCEPTION", e);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx360IsActivate")
    public ByteResult queryPlayer(@RequestParam("qid") final String userId, @RequestParam("server_id") final String serverId, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            String rtn = "0";
            final String yx = "360";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return YxHelper.getResult(908, request, response);
            }
            if (StringUtils.isBlank(userId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.getResult(902, request, response);
            }
            if (StringUtils.isBlank(serverId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(924)), 924));
                return YxHelper.getResult(924, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.getResult(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(serverId);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.getResult(909, request, response);
            }
            final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(userId, yx);
            if (playerList.size() > 0) {
                rtn = "1";
            }
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(rtn.getBytes()), 1));
            return new ByteResult(rtn.getBytes());
        }
        catch (Exception e) {
            Yx360OperationAction.errorLog.error("yx360IsActivate_fail_EXCEPTION", e);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360IsActivate", "yx360IsActivate_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx360Pay")
    public ByteResult pay(@RequestParam("qid") final String userId, @RequestParam("server_id") final String serverId, @RequestParam("order_amount") int gold, @RequestParam("order_id") final String orderId, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String yx = "360";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 908));
                return this.PAY_FAIL;
            }
            gold *= 10;
            if (StringUtils.isBlank(userId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 902));
                return this.PAY_FAIL;
            }
            if (StringUtils.isBlank(serverId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 924));
                return this.PAY_FAIL;
            }
            if (gold <= 0) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 904));
                return this.PAY_FAIL;
            }
            if (StringUtils.isBlank(orderId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 907));
                return this.PAY_FAIL;
            }
            if (StringUtils.isBlank(sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 922));
                return this.PAY_FAIL;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(gold / 10);
            sb.append(orderId);
            sb.append(serverId.toUpperCase());
            sb.append(PluginContext.configuration.getPayKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_FAIL, 909));
                return this.PAY_FAIL;
            }
            final int playerId = this.yxOperation.getDefaultPayPlayer(userId, yx);
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, playerId, userId, yx, gold, request);
            if (result.left == 1) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, this.PAY_SUCCESS, 1));
                return this.PAY_SUCCESS;
            }
            ByteResult temp = this.PAY_FAIL;
            if (result.left == 5) {
                temp = this.PAY_DUPLICATE;
            }
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, temp, result.left));
            return temp;
        }
        catch (Exception e) {
            Yx360OperationAction.errorLog.error("pay_fail_EXCEPTION", e);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, this.PAY_FAIL, 6));
            return this.PAY_FAIL;
        }
    }
    
    @Command("yx360PlayerInfo")
    public ByteResult queryPlayerDetails(@RequestParam("users") final String users, @RequestParam("server_id") final String serverId, @RequestParam("time") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String yx = "360";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(908).getBytes()), 908));
                return new ByteResult(String.valueOf(908).getBytes());
            }
            if (StringUtils.isBlank(users)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 902));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (StringUtils.isBlank(serverId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-1).getBytes()), 924));
                return new ByteResult(String.valueOf(-1).getBytes());
            }
            if (time <= 0L) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_YX_360_PARAM_MISSING_PARAM", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), -4));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 60L) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_YX_360_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), -2));
                return new ByteResult(String.valueOf(-2).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), 922));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            final String[] userIdList = users.split(",");
            if (userIdList == null || userIdList.length <= 0) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), 902));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            if (userIdList.length > 20) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_YX_360_REQUEST_NUM_TOO_MAX", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-6).getBytes()), -6));
                return new ByteResult(String.valueOf(-6).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("get_player_info_");
            sb.append(time);
            sb.append("_");
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-3).getBytes()), -3));
                return new ByteResult(String.valueOf(-3).getBytes());
            }
            final JsonDocument doc = new JsonDocument();
            boolean hasData = false;
            doc.startObject();
            String[] array;
            for (int length = (array = userIdList).length, i = 0; i < length; ++i) {
                final String userId = array[i];
                final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(userId, yx);
                if (playerList.size() > 0) {
                    hasData = true;
                    doc.startArray(userId);
                    for (final YxPlayerInfo player : playerList) {
                        doc.startObject();
                        doc.createElement("level", player.getLv());
                        doc.createElement("name", player.getPlayerName());
                        doc.endObject();
                    }
                    doc.endArray();
                }
            }
            doc.endObject();
            if (!hasData) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_YX_360_CAN_NOT_QUERY_DATA", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-7).getBytes()), -7));
                return new ByteResult(String.valueOf(-7).getBytes());
            }
            return new ByteResult(doc.toByte());
        }
        catch (Exception e) {
            Yx360OperationAction.errorLog.error("yx360PlayerInfo_fail_EXCEPTION", e);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx360PlayerInfo", "yx360PlayerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return new ByteResult(String.valueOf(6).getBytes());
        }
    }
    
    @Command("yx360QueryOnline")
    public ByteResult queryOnline(@RequestParam("gkey") final String gkey, @RequestParam("server_id") final String serverId, @RequestParam("time") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final String yx = "360";
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_SERVER_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-5).getBytes()), -5));
                return new ByteResult(String.valueOf(-5).getBytes());
            }
            if (StringUtils.isBlank(gkey)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_PARAM_MISSING_PARAM", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), -4));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            if (StringUtils.isBlank(serverId)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_PARAM_MISSING_PARAM", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), -4));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            if (time <= 0L) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_PARAM_MISSING_PARAM", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), -4));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 30L) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-2).getBytes()), -2));
                return new ByteResult(String.valueOf(-2).getBytes());
            }
            if (StringUtils.isBlank(sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_PARAM_MISSING_PARAM", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-4).getBytes()), -4));
                return new ByteResult(String.valueOf(-4).getBytes());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(time);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_YX_360_PARAM_CHECK_FAIL_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-3).getBytes()), -3));
                return new ByteResult(String.valueOf(-3).getBytes());
            }
            final int num = PluginContext.configuration.getOnline(yx);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(String.valueOf(num).getBytes()), 1));
            return new ByteResult(String.valueOf(num).getBytes());
        }
        catch (Exception e) {
            Yx360OperationAction.errorLog.error("queryOnline_fail_EXCEPTION", e);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOnline", "queryOnline_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(String.valueOf(-103).getBytes()), -103));
            return new ByteResult(String.valueOf(-103).getBytes());
        }
    }
    
    public static void pushPlayerInfo(final Request request, final String userId, final int playerId, final String serverId, final String gKey, final int playerLv, final String playerName) {
        final long start = System.currentTimeMillis();
        try {
            final String yx = "360";
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("gender", "m");
            paramMap.put("qid", userId);
            paramMap.put("server_id", serverId);
            paramMap.put("gkey", gKey);
            paramMap.put("level", String.valueOf(playerLv));
            paramMap.put("name", URLEncoder.encode(playerName, "UTF-8"));
            final StringBuilder sb = new StringBuilder();
            sb.append("m");
            sb.append(gKey);
            sb.append(playerLv);
            sb.append(playerName);
            sb.append(userId);
            sb.append(serverId);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            final String md5Value = CodecUtil.md5(sb.toString()).toLowerCase();
            paramMap.put("sign", md5Value);
            try {
                String echo = WebUtils.sendRequest(PluginContext.configuration.get360PushPlayerInfoUrl(yx), paramMap);
                if (StringUtils.isNotBlank(echo)) {
                    echo = echo.trim();
                }
                final JSONObject json = (JSONObject)JSON.parse(echo);
                Yx360OperationAction.opReport.error("debug#pushPlayerInfo#echo_" + echo);
                final String errno = json.get("errno").toString();
                if ("0".equals(errno)) {
                    Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pushPlayerInfo", "pushPlayerInfo_success_SUCCESS_ECHO_" + echo, (request != null) ? YxHelper.getIp(request) : "", (request != null) ? request.getParamterMap() : null, (request != null) ? request.getContent() : "".getBytes(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 1));
                }
                else {
                    Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pushPlayerInfo", "pushPlayerInfo_fail_USERID_ERROR_OR_SERVERURL_ERROR_ECHO_" + echo, (request != null) ? YxHelper.getIp(request) : "", (request != null) ? request.getParamterMap() : null, (request != null) ? request.getContent() : "".getBytes(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), Integer.parseInt(errno)));
                }
            }
            catch (Exception e) {
                Yx360OperationAction.errorLog.error("pushPlayerInfo_fail_EXCEPTION", e);
                Yx360OperationAction.opReport.error("pushPlayerInfo_fail_EXCEPTION_qid_" + userId + "_playerid_" + playerId + "_server_id_" + serverId + "_gKey_" + gKey + "_level_" + playerLv + "_playerName_" + playerName + "_sign_" + md5Value, e);
                Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pushPlayerInfo", "pushPlayerInfo_fail_EXCEPTION", (request != null) ? YxHelper.getIp(request) : "", (request != null) ? request.getParamterMap() : null, (request != null) ? request.getContent() : "".getBytes(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
            }
        }
        catch (Exception e2) {
            Yx360OperationAction.opReport.error("pushPlayerInfo_fail_EXCEPTION", e2);
            Yx360OperationAction.errorLog.error("pushPlayerInfo_fail_EXCEPTION", e2);
            Yx360OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pushPlayerInfo", "pushPlayerInfo_fail_EXCEPTION", (request != null) ? YxHelper.getIp(request) : "", (request != null) ? request.getParamterMap() : null, (request != null) ? request.getContent() : "".getBytes(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
        }
    }
    
    @Command("test360Privilege")
    public ByteResult test360Privilege(@RequestParam("qid") final String uId, @RequestParam("skey") final String skey, @RequestParam("level") final int level, final Request request, final Response response) {
        int newLv = 0;
        if (level == 5) {
            newLv = 1;
        }
        else if (level == 6) {
            newLv = 2;
        }
        else if (level == 7) {
            newLv = 3;
        }
        else if (level == 8) {
            newLv = 4;
        }
        else {
            newLv = 5;
        }
        if (System.currentTimeMillis() - Yx360OperationAction.monitorCD > 600000L) {
            final int ret = this.yxOperation.test360Privilege(uId, newLv);
            Yx360OperationAction.monitorCD = System.currentTimeMillis();
            return new ByteResult(new StringBuilder(String.valueOf(ret)).toString().getBytes());
        }
        return new ByteResult("-2".getBytes());
    }
}
