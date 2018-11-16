package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.result.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.framework.netty.util.*;
import com.alibaba.fastjson.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.util.codec.*;
import java.util.*;
import com.reign.plugin.yx.common.*;
import com.reign.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class Yx5211gameOperationAction
{
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "5211game";
    private static ByteResult SUCCESS;
    private static ByteResult FAIL;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(Yx5211gameOperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        Yx5211gameOperationAction.SUCCESS = new ByteResult("1".getBytes());
        Yx5211gameOperationAction.FAIL = new ByteResult("0".getBytes());
    }
    
    @Command("yx5211gameLogin")
    public ByteResult login(@RequestParam("code") final String code, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(code)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_CODE_RESPONSE_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.redirectUnlogPage(1, request, response);
            }
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            final String appId = PluginContext.configuration.get5211gameAppId("5211game");
            paramMap.put("app_id", appId);
            paramMap.put("app_secret", PluginContext.configuration.getLoginKey("5211game"));
            paramMap.put("grant_type", "authorization_code");
            paramMap.put("code", code);
            final String redirectUrl = PluginContext.configuration.getSinaRedirectUrl("5211game");
            final String serverId = PluginContext.configuration.getServerId("5211game");
            paramMap.put("redirect_uri", MessageFormatter.format(redirectUrl, new Object[] { serverId, "yx5211gameLogin" }));
            try {
                final String accessUrl = PluginContext.configuration.get5211gameAccessUrl("5211game");
                String echo = WebUtils.sendGetRequest(accessUrl, (Map)paramMap);
                if (StringUtils.isBlank(echo)) {
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_ACCESS_RESPONSE_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(2)), 2));
                    return YxHelper.redirectUnlogPage(2, request, response);
                }
                echo = echo.trim();
                final JSONObject json = (JSONObject)JSON.parse(echo);
                final String userId = json.getString("uid");
                if (StringUtils.isBlank(userId)) {
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY_USERID_" + userId + "_ECHO_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                    return YxHelper.redirectUnlogPage(902, request, response);
                }
                final int isAdult = 1;
                final Session session = this.yxOperation.login("5211game", userId, "", "", new StringBuilder(String.valueOf(isAdult)).toString(), "", request);
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
                response.addHeader("Location", PluginContext.configuration.getGameURL("5211game"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            catch (Exception e) {
                Yx5211gameOperationAction.errorLog.error("login_fail_EXCEPTION", e);
                Yx5211gameOperationAction.opReport.error("login_fail_EXCEPTION", e);
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
                return YxHelper.getResult(6, request, response);
            }
        }
        catch (Exception e2) {
            Yx5211gameOperationAction.errorLog.error("login_fail_EXCEPTION", e2);
            Yx5211gameOperationAction.opReport.error("login_fail_EXCEPTION", e2);
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx5211gamePay")
    public ByteResult pay(@RequestParam("uid") final String userId, @RequestParam("amount") final int gold, @RequestParam("billno") final String orderId, @RequestParam("sid") final int serverId, @RequestParam("ts") final long time, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("5211game");
            final String passIp = PluginContext.configuration.getPassedIP("5211game");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 908));
                return Yx5211gameOperationAction.FAIL;
            }
            if (StringUtils.isBlank(userId)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 902));
                return Yx5211gameOperationAction.FAIL;
            }
            if (gold <= 0) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 904));
                return Yx5211gameOperationAction.FAIL;
            }
            if (StringUtils.isBlank(orderId)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 907));
                return Yx5211gameOperationAction.FAIL;
            }
            if (serverId <= 0) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 924));
                return Yx5211gameOperationAction.FAIL;
            }
            if (time <= 0L) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 910));
                return Yx5211gameOperationAction.FAIL;
            }
            long interval = System.currentTimeMillis() - time * 1000L;
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 5L) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_360_TIME_OUT", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 1005));
                return Yx5211gameOperationAction.FAIL;
            }
            if (StringUtils.isBlank(sign)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 922));
                return Yx5211gameOperationAction.FAIL;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(gold);
            sb.append(orderId);
            sb.append(serverId);
            sb.append(time);
            sb.append(PluginContext.configuration.getPayKey("5211game"));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), sign)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 909));
                return Yx5211gameOperationAction.FAIL;
            }
            final String currentServerId = PluginContext.configuration.getServerId("5211game");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("5211game"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx5211gamePay" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("uid", userId);
                paramMap.put("amount", gold);
                paramMap.put("billno", orderId);
                paramMap.put("sid", serverId);
                paramMap.put("ts", time);
                paramMap.put("sign", sign);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx5211gameOperationAction.errorLog.error("pay_fail_EXCEPTION", e);
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 6));
                    return Yx5211gameOperationAction.FAIL;
                }
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, 0, userId, "5211game", gold, request);
            if (result.left == 1) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.SUCCESS, 1));
                return Yx5211gameOperationAction.SUCCESS;
            }
            if (result.left == 5) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_EXISTS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 917));
                return Yx5211gameOperationAction.FAIL;
            }
            if (result.left == 2) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ERROR_NO_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 2));
                return Yx5211gameOperationAction.FAIL;
            }
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, result.left));
            return Yx5211gameOperationAction.FAIL;
        }
        catch (Exception e2) {
            Yx5211gameOperationAction.errorLog.error("pay_fail_EXCEPTION", e2);
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 6));
            return Yx5211gameOperationAction.FAIL;
        }
    }
    
    @Command("yx5211gameIsActivate")
    public ByteResult isActivate(@RequestParam("uid") final String userId, @RequestParam("sid") final String serverId, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP("5211game");
            final String passIp = PluginContext.configuration.getPassedIP("5211game");
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 908));
                return Yx5211gameOperationAction.FAIL;
            }
            if (StringUtils.isBlank(userId)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 902));
                return Yx5211gameOperationAction.FAIL;
            }
            if (StringUtils.isBlank(serverId)) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_SERVER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 924));
                return Yx5211gameOperationAction.FAIL;
            }
            final String currentServerId = PluginContext.configuration.getServerId("5211game");
            if (!currentServerId.equals(String.valueOf(serverId)) && !this.getMainServerId(String.valueOf(serverId)).equals(currentServerId)) {
                final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getSinaRedirectUrl("5211game"), new Object[] { this.getMainServerId(String.valueOf(serverId)), "yx5211gameIsActivate" });
                final Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("uid", userId);
                paramMap.put("sid", serverId);
                String echo = null;
                try {
                    echo = WebUtils.sendRequest(redirectUrl, (Map)paramMap);
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    Yx5211gameOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 6));
                    return Yx5211gameOperationAction.FAIL;
                }
            }
            final List<YxPlayerInfo> ypiList = this.yxOperation.queryPlayer(userId, "5211game");
            if (ypiList == null || ypiList.size() < 1) {
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_ERROR_NO_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 2));
                return Yx5211gameOperationAction.FAIL;
            }
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, Yx5211gameOperationAction.SUCCESS, 1));
            return Yx5211gameOperationAction.SUCCESS;
        }
        catch (Exception e2) {
            Yx5211gameOperationAction.errorLog.error("playerInfo_fail_EXCEPTION", e2);
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, Yx5211gameOperationAction.FAIL, 6));
            return Yx5211gameOperationAction.FAIL;
        }
    }
    
    public static void callYx5211Game(final String userId, final int playerId, final String playerName, final int playerLv, final int id, final String yx) {
        final long start = System.currentTimeMillis();
        try {
            final long tp = start / 1000L;
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(id);
            sb.append(tp);
            sb.append(PluginContext.configuration.getCmwebgameKey(yx));
            final String md5Value = MD5SecurityUtil.code(sb.toString()).toLowerCase();
            final String serverUrl = MessageFormatter.format(PluginContext.configuration.getCmwebgameAPIUrl(yx), new Object[] { userId, id, tp, md5Value });
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            try {
                String echo = WebUtils.sendGetRequest(serverUrl, (Map)paramMap);
                if (StringUtils.isNotBlank(echo)) {
                    echo = echo.trim();
                }
                final JSONObject json = (JSONObject)JSON.parse(echo);
                final int code = json.getIntValue("Code");
                if (code >= 0) {
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYx5211Game", "callYx5211Game_success_SUCCESS_ECHO_" + echo + "_userId_" + userId + "_playerId_" + playerId + "_playerName_" + playerName + "_playerLv_" + playerLv, "", null, "".getBytes(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 1));
                }
                else {
                    Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYx5211Game", "callYx5211Game_fail_ERROR_ECHO_" + echo + "_userId_" + userId + "_playerId_" + playerId + "_playerName_" + playerName + "_playerLv_" + playerLv + "_serverUrl_" + serverUrl, "", null, "".getBytes(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 2));
                }
            }
            catch (Exception e) {
                Yx5211gameOperationAction.opReport.error("callYx5211Game_fail_EXCEPTION", e);
                Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYx5211Game", "callYx5211Game_fail_EXCEPTION", "", null, "".getBytes(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
            }
        }
        catch (Exception e2) {
            Yx5211gameOperationAction.opReport.error("callYx5211Game_fail_EXCEPTION", e2);
            Yx5211gameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYx5211Game", "callYx5211Game_fail_EXCEPTION", "", null, "".getBytes(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("5211game");
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
