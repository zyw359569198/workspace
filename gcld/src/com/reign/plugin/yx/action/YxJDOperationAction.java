package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import com.alibaba.fastjson.parser.*;
import com.alibaba.fastjson.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import java.util.*;
import com.reign.plugin.yx.common.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxJDOperationAction
{
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "jd";
    private static final int PAY_SUCCESS = 0;
    private static final int PAY_FAIL = 1;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxJDOperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxJDLogin")
    public ByteResult login(@RequestParam("customerId") final long customerId, @RequestParam("data") final String data, @RequestParam("timestamp") final long timeStamp, @RequestParam("identify") final String identify, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (customerId <= 0L) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_CUSTOMER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1012)), 1012));
                return YxHelper.redirectUnlogPage(1012, request, response);
            }
            if (StringUtils.isBlank(data)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_DATA_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1013)), 1013));
                return YxHelper.redirectUnlogPage(1013, request, response);
            }
            if (timeStamp <= 0L) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            if (StringUtils.isBlank(identify)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_IDENTIFY_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1014)), 1014));
                return YxHelper.redirectUnlogPage(1014, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("customerId=");
            sb.append(customerId);
            sb.append("&data=");
            sb.append(data);
            sb.append("&identify=");
            sb.append(identify);
            sb.append("&timestamp=");
            sb.append(timeStamp);
            sb.append("&");
            sb.append(PluginContext.configuration.getLoginKey("jd"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            final byte[] userInfobytes = Base64.decode(data);
            final JSONObject json = (JSONObject)JSON.parse(userInfobytes, new Feature[0]);
            final String gateWayId = json.getString("gateWayId");
            if (StringUtils.isBlank(gateWayId)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_GATE_WAY_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1015)), 1015));
                return YxHelper.redirectUnlogPage(1015, request, response);
            }
            final String serverIds = PluginContext.configuration.getServerIdS("jd");
            if (!gateWayId.equalsIgnoreCase(serverIds) && !this.getMainServerId(gateWayId).equalsIgnoreCase(serverIds)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getJDRedirectUrl("jd"), new Object[] { this.getMainServerId(gateWayId), "yxJDLogin" });
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gateWayId + "_REDIRECT_URL_" + redirectUrl, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            final String userId = json.getString("userId");
            if (StringUtils.isBlank(userId)) {
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            String isAdult = json.getString("isAdult");
            if (!"0".equals(isAdult)) {
                isAdult = "1";
            }
            final Session session = this.yxOperation.login("jd", userId, "", "", isAdult, "", request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL("jd"));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxJDOperationAction.errorLog.error("login_fail_EXCEPTION", e);
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yxJDPay")
    public ByteResult pay(@RequestParam("customerId") final long customerId, @RequestParam("data") final String data, @RequestParam("timestamp") final long timeStamp, @RequestParam("identify") final String identify, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("jd");
            final String passIp = PluginContext.configuration.getPassedIP("jd");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                doc.createElement("retCode", 104);
                doc.createElement("retMessage", "IP\u5730\u5740\u4e0d\u7b26\u5408\u8981\u6c42");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 908));
                return new ByteResult(doc.toByte());
            }
            if (customerId <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_CUSTOMER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1012));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(data)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_DATA_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1013));
                return new ByteResult(doc.toByte());
            }
            if (timeStamp <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 910));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(identify)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IDENTIFY_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1014));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("customerId=");
            sb.append(customerId);
            sb.append("&data=");
            sb.append(data);
            sb.append("&identify=");
            sb.append(identify);
            sb.append("&timestamp=");
            sb.append(timeStamp);
            sb.append("&");
            sb.append(PluginContext.configuration.getPayKey("jd"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("retCode", 105);
                doc.createElement("retMessage", "\u9a8c\u8bc1\u6458\u8981\u4e32\u9a8c\u8bc1\u5931\u8d25");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final byte[] userInfobytes = Base64.decode(data);
            final JSONObject json = (JSONObject)JSON.parse(userInfobytes, new Feature[0]);
            final String gateWayId = json.getString("gateWayId");
            if (StringUtils.isBlank(gateWayId)) {
                doc.createElement("retCode", 107);
                doc.createElement("retMessage", "\u533a\u670did\u4e0d\u5b58\u5728\u6216\u8005\u9519\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GATE_WAY_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1015));
                return new ByteResult(doc.toByte());
            }
            final String serverIds = PluginContext.configuration.getServerIdS("jd");
            if (!gateWayId.equalsIgnoreCase(serverIds) && !this.getMainServerId(gateWayId).equalsIgnoreCase(serverIds)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getJDRedirectUrl("jd"), new Object[] { this.getMainServerId(gateWayId), "yxJDPay" });
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gateWayId + "_REDIRECT_URL_" + redirectUrl, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            final String orderId = json.getString("orderId");
            final String userId = json.getString("userId");
            final double chargeMoney = json.getDoubleValue("chargeMoney");
            final int gold = (int)(chargeMoney * 10.0);
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userId)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (gold <= 0) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 904));
                return new ByteResult(doc.toByte());
            }
            final int playerId = this.yxOperation.getDefaultPayPlayer(userId, "jd");
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, playerId, userId, "jd", gold, request);
            if (result.left == 1) {
                doc.createElement("retCode", 100);
                doc.createElement("retMessage", "\u6210\u529f");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 0)));
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
                return new ByteResult(doc.toByte());
            }
            if (result.left == 5) {
                doc.createElement("retCode", 102);
                doc.createElement("retMessage", "\u8ba2\u5355\u53f7\u4e0d\u5141\u8bb8\u91cd\u590d");
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 0)));
                doc.endObject();
            }
            else {
                doc.createElement("retCode", result.left);
                doc.createElement("retMessage", result.left);
                doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
                doc.endObject();
            }
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), result.left));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e) {
            YxJDOperationAction.errorLog.error("pay_fail_EXCEPTION", e);
            doc.createElement("retCode", 999);
            doc.createElement("retMessage", "\u7cfb\u7edf\u9519\u8bef");
            doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 1)));
            doc.endObject();
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yxJDPlayerInfo")
    public ByteResult playerInfo(@RequestParam("customerId") final long customerId, @RequestParam("data") final String data, @RequestParam("timestamp") final long timeStamp, @RequestParam("identify") final String identify, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (customerId <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_CUSTOMER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1012));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(data)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_DATA_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1013));
                return new ByteResult(doc.toByte());
            }
            if (timeStamp <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 910));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(identify)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_IDENTIFY_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1014));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("customerId=");
            sb.append(customerId);
            sb.append("&data=");
            sb.append(data);
            sb.append("&identify=");
            sb.append(identify);
            sb.append("&timestamp=");
            sb.append(timeStamp);
            sb.append("&");
            sb.append(PluginContext.configuration.getQueryKey("jd"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("retCode", 105);
                doc.createElement("retMessage", "\u9a8c\u8bc1\u6458\u8981\u4e32\u9a8c\u8bc1\u5931\u8d25");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final byte[] userInfobytes = Base64.decode(data);
            final JSONObject json = (JSONObject)JSON.parse(userInfobytes, new Feature[0]);
            final String gateWayId = json.getString("gateWayId");
            if (StringUtils.isBlank(gateWayId)) {
                doc.createElement("retCode", 107);
                doc.createElement("retMessage", "\u533a\u670did\u4e0d\u5b58\u5728\u6216\u8005\u9519\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_GATE_WAY_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1015));
                return new ByteResult(doc.toByte());
            }
            final String serverIds = PluginContext.configuration.getServerIdS("jd");
            if (!gateWayId.equalsIgnoreCase(serverIds) && !this.getMainServerId(gateWayId).equalsIgnoreCase(serverIds)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getJDRedirectUrl("jd"), new Object[] { this.getMainServerId(gateWayId), "yxJDPlayerInfo" });
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gateWayId + "_REDIRECT_URL_" + redirectUrl, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            final String userId = json.getString("userId");
            if (StringUtils.isBlank(userId)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            final List<YxPlayerInfo> ypiInfoList = this.yxOperation.queryPlayer(userId, "jd");
            if (ypiInfoList == null || ypiInfoList.size() <= 0) {
                doc.createElement("retCode", 108);
                doc.createElement("retMessage", "\u89d2\u8272\u4e0d\u5b58\u5728(\u8be5\u7528\u6237\u6ca1\u6709\u521b\u5efa\u89d2\u8272)");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_NO_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1016));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("retCode", 100);
            doc.createElement("retMessage", "\u6210\u529f");
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.startArray("roleInfos");
            for (final YxPlayerInfo ypiInfo : ypiInfoList) {
                ypiInfo.buildJDJson(doc2);
            }
            doc2.endObject();
            doc2.endObject();
            doc.createElement("data", (Object)Base64.encodeToString(doc2.toByte()));
            doc.endObject();
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e) {
            YxJDOperationAction.errorLog.error("yxJDPlayerInfo_fail_EXCEPTION", e);
            doc.createElement("retCode", 999);
            doc.createElement("retMessage", "\u7cfb\u7edf\u9519\u8bef");
            doc.createElement("data", "");
            doc.endObject();
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDPlayerInfo", "yxJDPlayerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yxJDOrderQuery")
    public ByteResult orderQuery(@RequestParam("customerId") final long customerId, @RequestParam("data") final String data, @RequestParam("timestamp") final long timeStamp, @RequestParam("identify") final String identify, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (customerId <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_CUSTOMER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1012));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(data)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_DATA_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1013));
                return new ByteResult(doc.toByte());
            }
            if (timeStamp <= 0L) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 910));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(identify)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_IDENTIFY_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1014));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("customerId=");
            sb.append(customerId);
            sb.append("&data=");
            sb.append(data);
            sb.append("&identify=");
            sb.append(identify);
            sb.append("&timestamp=");
            sb.append(timeStamp);
            sb.append("&");
            sb.append(PluginContext.configuration.getQueryKey("jd"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("retCode", 105);
                doc.createElement("retMessage", "\u9a8c\u8bc1\u6458\u8981\u4e32\u9a8c\u8bc1\u5931\u8d25");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final byte[] userInfobytes = Base64.decode(data);
            final JSONObject json = (JSONObject)JSON.parse(userInfobytes, new Feature[0]);
            final String gateWayId = json.getString("gateWayId");
            if (StringUtils.isBlank(gateWayId)) {
                doc.createElement("retCode", 107);
                doc.createElement("retMessage", "\u533a\u670did\u4e0d\u5b58\u5728\u6216\u8005\u9519\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_GATE_WAY_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1015));
                return new ByteResult(doc.toByte());
            }
            final String serverIds = PluginContext.configuration.getServerIdS("jd");
            if (!gateWayId.equalsIgnoreCase(serverIds) && !this.getMainServerId(gateWayId).equalsIgnoreCase(serverIds)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getJDRedirectUrl("jd"), new Object[] { this.getMainServerId(gateWayId), "yxJDOrderQuery" });
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gateWayId + "_REDIRECT_URL_" + redirectUrl, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            final String orderId = json.getString("orderId");
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("retCode", 103);
                doc.createElement("retMessage", "\u4f20\u5165\u7684\u53c2\u6570\u6709\u8bef");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            final YxPlayerPayInfo ypi = this.yxOperation.queryOrder(orderId, "jd");
            if (ypi == null) {
                doc.createElement("retCode", 101);
                doc.createElement("retMessage", "\u8ba2\u5355\u4e0d\u5b58\u5728");
                doc.createElement("data", "");
                doc.endObject();
                YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_ORDER_ID_NOT_EXISTS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1017));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("retCode", 100);
            doc.createElement("retMessage", "\u6210\u529f");
            doc.createElement("data", (Object)Base64.encodeToString(JsonBuilder.getSimpleJson("orderStatus", 0)));
            doc.endObject();
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e) {
            YxJDOperationAction.errorLog.error("yxJDOrderQuery_fail_EXCEPTION", e);
            doc.createElement("retCode", 999);
            doc.createElement("retMessage", "\u7cfb\u7edf\u9519\u8bef");
            doc.createElement("data", "");
            doc.endObject();
            YxJDOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxJDOrderQuery", "yxJDOrderQuery_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("jd");
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
