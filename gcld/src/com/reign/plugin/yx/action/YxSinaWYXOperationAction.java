package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.framework.netty.util.*;
import com.alibaba.fastjson.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.json.*;
import com.reign.util.codec.*;
import com.reign.util.*;
import com.reign.plugin.yx.common.*;
import java.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxSinaWYXOperationAction
{
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "wyx";
    private static final int COUNT = 10;
    private static final int PAGE = 1;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxSinaWYXOperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxSinaWYXLogin")
    public ByteResult login(@RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(ticket)) {
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            if (!ticket.startsWith("ST-")) {
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_INVALID", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1018)), 1018));
                return YxHelper.redirectUnlogPage(1018, request, response);
            }
            final String requestURL = PluginContext.configuration.getSinaReceiptVerificationUrl("wyx");
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("s", PluginContext.configuration.getSinaIdentifier("wyx"));
            paramMap.put("ticket", ticket);
            String echo = WebUtils.sendGetRequest(requestURL, (Map)paramMap);
            echo = echo.trim();
            final JSONObject json = (JSONObject)JSON.parse(echo);
            final String retcode = json.getString("retcode");
            if (!"0".equals(retcode)) {
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_VERIFICATION_FAIL_ECHO_" + echo + "_RETCODE_" + retcode, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1019)), 1019));
                return YxHelper.redirectUnlogPage(1019, request, response);
            }
            final String userId = json.getString("uid");
            if (StringUtils.isBlank(userId)) {
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY_USERID_" + userId + "_ECHO_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            final String isAdult = "1";
            final Session session = this.yxOperation.login("wyx", userId, "", "", isAdult, "", request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL("wyx"));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxSinaWYXOperationAction.errorLog.error("login_fail_EXCEPTION", e);
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yxSinaWYXPay")
    public ByteResult pay(@RequestParam("SPID") final String spid, @RequestParam("ServerID") final int serverId, @RequestParam("UserID") final String userId, @RequestParam("UserIP") final String userIp, @RequestParam("RoleID") final int playerId, @RequestParam("OrderID") final String orderId, @RequestParam("GamePoint") final int gold, @RequestParam("PayPoint") final int payPoint, @RequestParam("Sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("ServerID", serverId);
        doc.createElement("UserID", userId);
        doc.createElement("RoleID", playerId);
        doc.createElement("GameOrderID", orderId);
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("wyx");
            final String passIp = PluginContext.configuration.getPassedIP("wyx");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                doc.createElement("Code", 211);
                doc.createElement("ErrorMsg", "IP\u4e0d\u5728\u5141\u8bb8\u7684\u5217\u8868\u91cc\u9762");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 908));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(spid)) {
                doc.createElement("Code", 200);
                doc.createElement("ErrorMsg", "SPID\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_SINA_SPID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 200));
                return new ByteResult(doc.toByte());
            }
            if (serverId <= 0) {
                doc.createElement("Code", 201);
                doc.createElement("ErrorMsg", "ServerId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userId)) {
                doc.createElement("Code", 202);
                doc.createElement("ErrorMsg", "UserId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userIp)) {
                doc.createElement("Code", 215);
                doc.createElement("ErrorMsg", "\u7ed9\u7528\u6237\u8d26\u6237\u5145\u503c\u5931\u8d25");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_IP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1020));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("Code", 204);
                doc.createElement("ErrorMsg", "OrderId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            if (gold <= 0) {
                doc.createElement("Code", 205);
                doc.createElement("ErrorMsg", "GamePoint\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_UGOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 904));
                return new ByteResult(doc.toByte());
            }
            if (payPoint <= 0) {
                doc.createElement("Code", 220);
                doc.createElement("ErrorMsg", "PayPoint\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_SINA_PAY_POINT_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 220));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("Code", 206);
                doc.createElement("ErrorMsg", "Sign\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("SPID=");
            sb.append(spid);
            sb.append("&ServerID=");
            sb.append(serverId);
            sb.append("&UserID=");
            sb.append(userId);
            sb.append("&UserIP=");
            sb.append(userIp);
            sb.append("&RoleID=");
            sb.append(playerId);
            sb.append("&OrderID=");
            sb.append(orderId);
            sb.append("&GamePoint=");
            sb.append(gold);
            sb.append("&PayPoint=");
            sb.append(payPoint);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getPayKey("wyx"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("Code", 210);
                doc.createElement("ErrorMsg", "\u7b7e\u540d\u9519\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("wyx");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("wyx"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yxSinaWYXPay" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("SPID", spid);
                paramMap.put("ServerID", serverId);
                paramMap.put("UserID", userId);
                paramMap.put("UserIP", userIp);
                paramMap.put("RoleID", playerId);
                paramMap.put("OrderID", orderId);
                paramMap.put("GamePoint", gold);
                paramMap.put("PayPoint", payPoint);
                paramMap.put("Sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxSinaWYXOperationAction.errorLog.error("pay_fail_EXCEPTION", e);
                    doc.createElement("Code", 215);
                    doc.createElement("ErrorMsg", "\u7ed9\u7528\u6237\u8d26\u6237\u5145\u503c\u5931\u8d25");
                    doc.endObject();
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            int newPlayerId = playerId;
            if (newPlayerId <= 0) {
                newPlayerId = this.yxOperation.getDefaultPayPlayer(userId, "wyx");
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, newPlayerId, userId, "wyx", gold, request);
            if (result.left == 1) {
                doc.createElement("Code", 0);
                doc.createElement("ErrorMsg", "");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
                return new ByteResult(doc.toByte());
            }
            if (result.left == 5) {
                doc.createElement("Code", 216);
                doc.createElement("ErrorMsg", "\u8ba2\u5355\u53f7\u91cd\u590d\u5145\u503c\u5931\u8d25");
                doc.endObject();
            }
            else {
                doc.createElement("Code", 215);
                doc.createElement("ErrorMsg", "\u7ed9\u7528\u6237\u8d26\u6237\u5145\u503c\u5931\u8d25");
                doc.endObject();
            }
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), result.left));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            YxSinaWYXOperationAction.errorLog.error("pay_fail_EXCEPTION", e2);
            doc.createElement("Code", 215);
            doc.createElement("ErrorMsg", "\u7ed9\u7528\u6237\u8d26\u6237\u5145\u503c\u5931\u8d25");
            doc.endObject();
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yxSinaWYXPlayerInfo")
    public ByteResult playerInfo(@RequestParam("SPID") final String spid, @RequestParam("ServerID") final int serverId, @RequestParam("UserID") final String userId, @RequestParam("UserIP") final String userIp, @RequestParam("Sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("ServerID", serverId);
        doc.createElement("UserID", userId);
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("wyx");
            final String passIp = PluginContext.configuration.getPassedIP("wyx");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                doc.createElement("Code", 111);
                doc.createElement("ErrorMsg", "IP\u4e0d\u5728\u5141\u8bb8\u7684\u5217\u8868\u91cc\u9762");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 908));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(spid)) {
                doc.createElement("Code", 100);
                doc.createElement("ErrorMsg", "SPID\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_YX_SINA_SPID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 200));
                return new ByteResult(doc.toByte());
            }
            if (serverId <= 0) {
                doc.createElement("Code", 101);
                doc.createElement("ErrorMsg", "ServerId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userId)) {
                doc.createElement("Code", 102);
                doc.createElement("ErrorMsg", "UserId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userIp)) {
                doc.createElement("Code", 121);
                doc.createElement("ErrorMsg", "\u8ba2\u5355\u53f7\u91cd\u590d\u5145\u503c\u5931\u8d25");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_IP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1020));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("Code", 103);
                doc.createElement("ErrorMsg", "Sign\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("SPID=");
            sb.append(spid);
            sb.append("&ServerID=");
            sb.append(serverId);
            sb.append("&UserID=");
            sb.append(userId);
            sb.append("&UserIP=");
            sb.append(userIp);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getQueryKey("wyx"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("Code", 110);
                doc.createElement("ErrorMsg", "\u7b7e\u540d\u9519\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("wyx");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("wyx"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yxSinaWYXPlayerInfo" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("SPID", spid);
                paramMap.put("ServerID", serverId);
                paramMap.put("UserID", userId);
                paramMap.put("UserIP", userIp);
                paramMap.put("Sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxSinaWYXOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
                    doc.createElement("Code", 6);
                    doc.createElement("ErrorMsg", "\u670d\u52a1\u5668\u5f02\u5e38");
                    doc.endObject();
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            final List<YxPlayerInfo> ypiList = this.yxOperation.queryPlayer(userId, "wyx");
            if (ypiList == null || ypiList.size() < 1) {
                doc.createElement("Code", 121);
                doc.createElement("ErrorMsg", "\u8ba2\u5355\u53f7\u91cd\u590d\u5145\u503c\u5931\u8d25");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_YX_SINA_PLAYER_ID_NOT_EXISTS_1", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 121));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("Code", 0);
            doc.createElement("ErrorMsg", "");
            doc.startArray("GameRole");
            for (final YxPlayerInfo ypi : ypiList) {
                ypi.buildSinaJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            YxSinaWYXOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e2);
            doc.createElement("Code", 6);
            doc.createElement("ErrorMsg", "\u670d\u52a1\u5668\u5f02\u5e38");
            doc.endObject();
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yxSinaWYXOrderQuery")
    public ByteResult orderQuery(@RequestParam("SPID") final String spid, @RequestParam("ServerID") final int serverId, @RequestParam("OrderID") final String orderId, @RequestParam("StartTime") final long startTime, @RequestParam("EndTime") final long endTime, @RequestParam("Count") final int count, @RequestParam("Page") final int page, @RequestParam("Sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("wyx");
            final String passIp = PluginContext.configuration.getPassedIP("wyx");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                doc.createElement("Code", 311);
                doc.createElement("ErrorMsg", "IP\u4e0d\u5728\u5141\u8bb8\u7684\u5217\u8868\u91cc\u9762");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 908));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(spid)) {
                doc.createElement("Code", 300);
                doc.createElement("ErrorMsg", "SPID\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_YX_SINA_SPID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 200));
                return new ByteResult(doc.toByte());
            }
            if (serverId <= 0) {
                doc.createElement("Code", 301);
                doc.createElement("ErrorMsg", "ServerId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("Code", 302);
                doc.createElement("ErrorMsg", "OrderId\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            if ("0".equals(orderId)) {
                if (startTime <= 0L) {
                    doc.createElement("Code", 324);
                    doc.createElement("ErrorMsg", "\u6279\u91cf\u67e5\u8be2\u8d77\u59cb\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
                    doc.endObject();
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_YX_SINA_START_TIME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 324));
                    return new ByteResult(doc.toByte());
                }
                if (endTime <= 0L) {
                    doc.createElement("Code", 325);
                    doc.createElement("ErrorMsg", "\u6279\u91cf\u67e5\u8be2\u7ed3\u675f\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
                    doc.endObject();
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_YX_SINA_END_TIME_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 325));
                    return new ByteResult(doc.toByte());
                }
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("Code", 306);
                doc.createElement("ErrorMsg", "Sign\u4fe1\u606f\u4e0d\u5b8c\u6574\u6216\u4fe1\u606f\u6709\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("SPID=");
            sb.append(spid);
            sb.append("&ServerID=");
            sb.append(serverId);
            sb.append("&OrderID=");
            sb.append(orderId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getQueryKey("wyx"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("Code", 310);
                doc.createElement("ErrorMsg", "\u7b7e\u540d\u9519\u8bef");
                doc.endObject();
                YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("wyx");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("wyx"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yxSinaWYXOrderQuery" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("SPID", spid);
                paramMap.put("ServerID", serverId);
                paramMap.put("OrderID", orderId);
                paramMap.put("StartTime", startTime);
                paramMap.put("EndTime", endTime);
                paramMap.put("Count", count);
                paramMap.put("Page", page);
                paramMap.put("Sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxSinaWYXOperationAction.errorLog.error("yxSinaOrderQuery_fail_EXCEPTION", e);
                    doc.createElement("Code", 6);
                    doc.createElement("ErrorMsg", "\u670d\u52a1\u5668\u5f02\u5e38");
                    doc.endObject();
                    YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            if ("0".equals(orderId)) {
                int realCount = count;
                if (realCount < 1) {
                    realCount = 10;
                }
                int realPage = page;
                if (realPage < 1) {
                    realPage = 1;
                }
                final List<YxPlayerPayInfo> ypiList = this.yxOperation.queryOrderByDateAndPage(new Date(startTime * 1000L), new Date(endTime * 1000L), realPage, realCount, "wyx");
                doc.createElement("Code", 0);
                doc.createElement("ErrorMsg", "");
                if (ypiList == null) {
                    doc.createElement("TotalNum", 0);
                }
                else {
                    doc.createElement("TotalNum", ypiList.size());
                    doc.startArray("OrderList");
                    for (final YxPlayerPayInfo ypi : ypiList) {
                        ypi.buildSinaJson(doc, currentServerId);
                    }
                    doc.endArray();
                }
            }
            else {
                final String[] orderIdArray = orderId.split(",");
                final List<String> arrayList = new ArrayList<String>();
                final List<YxPlayerPayInfo> ypiList = new ArrayList<YxPlayerPayInfo>();
                String[] array;
                for (int length = (array = orderIdArray).length, i = 0; i < length; ++i) {
                    final String query = array[i];
                    final YxPlayerPayInfo ypi2 = this.yxOperation.queryOrder(orderId, "wyx");
                    if (ypi2 == null) {
                        arrayList.add(query);
                    }
                    else {
                        ypiList.add(ypi2);
                    }
                }
                if (arrayList.size() <= 0) {
                    doc.createElement("Code", 0);
                    doc.createElement("ErrorMsg", "");
                }
                else {
                    doc.createElement("Code", 320);
                    final StringBuffer sb2 = new StringBuffer();
                    for (final String temp : arrayList) {
                        sb2.append(temp);
                        sb2.append(",");
                    }
                    sb2.setLength(sb2.length() - 1);
                    doc.createElement("ErrorMsg", sb2.toString());
                }
                doc.createElement("TotalNum", ypiList.size());
                doc.startArray("OrderList");
                for (final YxPlayerPayInfo ypi : ypiList) {
                    ypi.buildSinaJson(doc, currentServerId);
                }
                doc.endArray();
            }
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            YxSinaWYXOperationAction.errorLog.error("yxSinaOrderQuery_fail_EXCEPTION", e2);
            doc.createElement("Code", 6);
            doc.createElement("ErrorMsg", "\u670d\u52a1\u5668\u5f02\u5e38");
            doc.endObject();
            YxSinaWYXOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxSinaOrderQuery", "yxSinaOrderQuery_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("wyx");
        if (StringUtils.isNotBlank(hefuList)) {
            final String[] hefuArr = hefuList.split(";");
            String[] array;
            for (int length = (array = hefuArr).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                final String[] tempArr = temp.split(",");
                if (tempArr.length >= 2) {
                    String[] array2;
                    for (int length2 = (array2 = tempArr).length, j = 0; j < length2; ++j) {
                        final String serverId = array2[j];
                        if (serverId.equalsIgnoreCase(gateWayId)) {
                            return tempArr[0];
                        }
                    }
                }
            }
        }
        return gateWayId;
    }
}
