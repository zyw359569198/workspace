package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.util.*;
import com.reign.util.*;
import java.util.*;
import com.reign.plugin.yx.common.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class Yx10086OperationAction
{
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "10086";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(Yx10086OperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yx10086Login")
    public ByteResult login(@RequestParam("userid") final String userId, @RequestParam("serverid") final String serverId, @RequestParam("sign") final String sign, @RequestParam("isadult") final int isAdult, @RequestParam("issm") final int isSm, @RequestParam("source") final int source, @RequestParam("app") final String app, @RequestParam("time") final int time, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (StringUtils.isBlank(userId)) {
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(serverId)) {
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(924)), 924));
                return YxHelper.redirectUnlogPage(924, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("userid=");
            sb.append(userId);
            sb.append("&serverid=");
            sb.append(serverId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getLoginKey("10086"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final int isAdultTemp = (isAdult == 1 && isSm == 1) ? 1 : 0;
            final Session session = this.yxOperation.login("10086", userId, "", "", new StringBuilder(String.valueOf(isAdultTemp)).toString(), new StringBuilder(String.valueOf(source)).toString(), request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL("10086"));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            Yx10086OperationAction.errorLog.error("login_fail_EXCEPTION", e);
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx10086Pay")
    public ByteResult pay(@RequestParam("userid") final String userId, @RequestParam("serverid") final String serverId, @RequestParam("sign") final String sign, @RequestParam("money") final int money, @RequestParam("orderid") final String orderId, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("10086");
            final String passIp = PluginContext.configuration.getPassedIP("10086");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                doc.createElement("resultCode", 207);
                doc.createElement("resultMsg", "IP\u9274\u6743\u5931\u8d25");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 908));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(userId)) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (money <= 0) {
                doc.createElement("resultCode", 207);
                doc.createElement("resultMsg", "\u5145\u503c\u91d1\u989d\u6709\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 904));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("resultCode", 206);
                doc.createElement("resultMsg", "\u8ba2\u5355\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("userid=");
            sb.append(userId);
            sb.append("&serverid=");
            sb.append(serverId);
            sb.append("&money=");
            sb.append(money);
            sb.append("&orderid=");
            sb.append(orderId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getPayKey("10086"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("10086");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("10086"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx10086Pay" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userid", userId);
                paramMap.put("serverid", serverId);
                paramMap.put("sign", sign);
                paramMap.put("money", money);
                paramMap.put("orderid", orderId);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx10086OperationAction.errorLog.error("pay_fail_EXCEPTION", e);
                    doc.createElement("resultCode", 208);
                    doc.createElement("resultMsg", "\u8ba2\u5355\u5904\u7406\u5931\u8d25");
                    doc.endObject();
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, 0, userId, "10086", money * 10, request);
            if (result.left == 1) {
                doc.createElement("resultCode", 200);
                doc.createElement("resultMsg", "\u6210\u529f");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
                return new ByteResult(doc.toByte());
            }
            if (result.left == 5) {
                doc.createElement("resultCode", 203);
                doc.createElement("resultMsg", "\u8ba2\u5355\u5df2\u5904\u7406");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_EXISTS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 917));
                return new ByteResult(doc.toByte());
            }
            if (result.left == 2) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ERROR_NO_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 2));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("resultCode", 208);
            doc.createElement("resultMsg", "\u8ba2\u5355\u5904\u7406\u5931\u8d25");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), result.left));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            Yx10086OperationAction.errorLog.error("pay_fail_EXCEPTION", e2);
            doc.createElement("resultCode", 208);
            doc.createElement("resultMsg", "\u8ba2\u5355\u5904\u7406\u5931\u8d25");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yx10086PlayerInfo")
    public ByteResult playerInfo(@RequestParam("userid") final String userId, @RequestParam("serverid") final String serverId, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (StringUtils.isBlank(userId)) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(serverId)) {
                doc.createElement("resultCode", 204);
                doc.createElement("resultMsg", "\u6e38\u620f\u533a\u7f16\u53f7\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("userid=");
            sb.append(userId);
            sb.append("&serverid=");
            sb.append(serverId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getQueryKey("10086"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("10086");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("10086"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx10086PlayerInfo" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userid", userId);
                paramMap.put("serverid", serverId);
                paramMap.put("sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx10086OperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
                    doc.createElement("resultCode", (-1));
                    doc.createElement("resultMsg", "\u5931\u8d25");
                    doc.endObject();
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            final List<YxPlayerInfo> ypiList = this.yxOperation.queryPlayer(userId, "10086");
            if (ypiList == null || ypiList.size() < 1) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_ERROR_NO_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 2));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("resultCode", 200);
            doc.createElement("resultMsg", "\u6210\u529f");
            doc.startObject("resultUserinfo");
            doc.startArray("item");
            for (final YxPlayerInfo ypi : ypiList) {
                ypi.build10086Json(doc);
            }
            doc.endArray();
            doc.endObject();
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            Yx10086OperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e2);
            doc.createElement("resultCode", (-1));
            doc.createElement("resultMsg", "\u5931\u8d25");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yx10086OrderQuery")
    public ByteResult orderQuery(@RequestParam("userid") final String userId, @RequestParam("serverid") final String serverId, @RequestParam("sign") final String sign, @RequestParam("money") final int money, @RequestParam("orderid") final String orderId, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (StringUtils.isBlank(userId)) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(serverId)) {
                doc.createElement("resultCode", 204);
                doc.createElement("resultMsg", "\u6e38\u620f\u533a\u7f16\u53f7\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (money <= 0) {
                doc.createElement("resultCode", 207);
                doc.createElement("resultMsg", "\u5145\u503c\u91d1\u989d\u6709\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 904));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(orderId)) {
                doc.createElement("resultCode", 206);
                doc.createElement("resultMsg", "\u8ba2\u5355\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 907));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("userid=");
            sb.append(userId);
            sb.append("&serverid=");
            sb.append(serverId);
            sb.append("&money=");
            sb.append(money);
            sb.append("&orderid=");
            sb.append(orderId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getQueryKey("10086"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("10086");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("10086"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx10086OrderQuery" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userid", userId);
                paramMap.put("serverid", serverId);
                paramMap.put("sign", sign);
                paramMap.put("money", money);
                paramMap.put("orderid", orderId);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx10086OperationAction.errorLog.error("queryOrder_fail_EXCEPTION", e);
                    doc.createElement("resultCode", 208);
                    doc.createElement("resultMsg", "\u8ba2\u5355\u5904\u7406\u5931\u8d25");
                    doc.endObject();
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            final YxPlayerPayInfo ypi = this.yxOperation.queryOrder(orderId, "10086");
            if (ypi == null) {
                doc.createElement("resultCode", 206);
                doc.createElement("resultMsg", "\u8ba2\u5355\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_ORDER_ID_NOT_EXISTS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1017));
                return new ByteResult(doc.toByte());
            }
            if (!ypi.getUserId().equals(userId)) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_USER_ID_NOT_EXISTS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 202));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("resultCode", 200);
            doc.createElement("resultMsg", "\u6210\u529f");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            Yx10086OperationAction.errorLog.error("queryOrder_fail_EXCEPTION", e2);
            doc.createElement("resultCode", 208);
            doc.createElement("resultMsg", "\u8ba2\u5355\u5904\u7406\u5931\u8d25");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    @Command("yx10086QueryUser")
    public ByteResult queryUser(@RequestParam("userid") final String userId, @RequestParam("serverid") final String serverId, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        try {
            if (StringUtils.isBlank(userId)) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 902));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(serverId)) {
                doc.createElement("resultCode", 204);
                doc.createElement("resultMsg", "\u6e38\u620f\u533a\u7f16\u53f7\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 924));
                return new ByteResult(doc.toByte());
            }
            if (StringUtils.isBlank(sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 922));
                return new ByteResult(doc.toByte());
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("userid=");
            sb.append(userId);
            sb.append("&serverid=");
            sb.append(serverId);
            sb.append("&key=");
            sb.append(PluginContext.configuration.getQueryKey("10086"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                doc.createElement("resultCode", 201);
                doc.createElement("resultMsg", "Md5\u52a0\u5bc6\u53c2\u6570\u9519\u8bef");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 909));
                return new ByteResult(doc.toByte());
            }
            final String currentServerId = PluginContext.configuration.getServerId("10086");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("10086"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx10086QueryUser" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userid", userId);
                paramMap.put("serverid", serverId);
                paramMap.put("sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx10086OperationAction.errorLog.error("queryUser_fail_EXCEPTION", e);
                    doc.createElement("resultCode", (-1));
                    doc.createElement("resultMsg", "\u5931\u8d25");
                    doc.endObject();
                    Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
                    return new ByteResult(doc.toByte());
                }
            }
            final List<YxPlayerInfo> ypiList = this.yxOperation.queryPlayer(userId, "10086");
            if (ypiList == null || ypiList.size() <= 0) {
                doc.createElement("resultCode", 202);
                doc.createElement("resultMsg", "\u8d26\u53f7\u4e0d\u5b58\u5728");
                doc.endObject();
                Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_NO_USER", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1008));
                return new ByteResult(doc.toByte());
            }
            doc.createElement("resultCode", 200);
            doc.createElement("resultMsg", "\u6210\u529f");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 1));
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            Yx10086OperationAction.errorLog.error("queryUser_fail_EXCEPTION", e2);
            doc.createElement("resultCode", (-1));
            doc.createElement("resultMsg", "\u5931\u8d25");
            doc.endObject();
            Yx10086OperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryUser", "queryUser_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(doc.toByte()), 6));
            return new ByteResult(doc.toByte());
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("10086");
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
    
    public static void main(final String[] args) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("test1", 1);
        doc.createElement("test2", 2);
        doc.startObject("resultUserinfo");
        doc.startArray("item");
        doc.startObject();
        doc.createElement("item3", 3);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        doc.endObject();
        System.out.println(doc.toString());
    }
}
