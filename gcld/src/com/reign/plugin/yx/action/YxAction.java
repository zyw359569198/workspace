package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.plugin.yx.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.result.*;
import java.util.*;
import java.text.*;
import com.reign.plugin.yx.common.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false"), @View(name = "plain", type = PlainView.class, compress = "false") })
public class YxAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "yx";
    private static int SIZE;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        YxAction.SIZE = 1000;
    }
    
    @Command("login")
    public ByteResult login(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final String tp, @RequestParam("sfid") String sfid, @RequestParam("adult") String adult, @RequestParam("yxSource") final String yxSource, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return YxHelper.redirectUnlogPage(901, request, response);
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            if (StringUtils.isBlank(sfid)) {
                sfid = "";
            }
            if (StringUtils.isBlank(adult)) {
                adult = "1";
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final Tuple<String, String> preKey = UserLoginRequestRecorder.getInstance().get(yx, userId);
            if (preKey == null) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_NOT_PRELOGIN", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(911)), 911));
                return YxHelper.redirectUnlogPage(911, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(userId);
            sb.append(sfid);
            sb.append(tp);
            sb.append(preKey.right);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            if (!preKey.left.equals(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_LOGIN_NOT_FIT_PRELOGIN_SRC_" + preKey.left + "_TP_" + tp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(912)), 912));
                return YxHelper.redirectUnlogPage(912, request, response);
            }
            UserLoginRequestRecorder.getInstance().remove(yx, userId);
            final Session session = this.yxOperation.login(yx, userId, "", sfid, adult, yxSource, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            final List<YxPlayerInfo> roleList = this.yxOperation.queryPlayer(userId, yx);
            final int roleNum = (roleList == null) ? 0 : roleList.size();
            response.addHeader("Location", MessageFormatter.format(PluginContext.configuration.getGameURL(yx), new Object[] { roleNum }));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxAction.errorLog.error("login_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("preLogin")
    public ByteResult preLogin(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final String tp, @RequestParam("additionalKey") final String additionalKey, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final Tuple<String, String> tuple = new Tuple();
        tuple.left = userId;
        tuple.right = yx;
        final Session session = request.getSession(false);
        if (session != null) {
            session.setAttribute("yx", tuple);
        }
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return YxHelper.getResult(908, request, response);
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return YxHelper.getResult(901, request, response);
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.getResult(902, request, response);
            }
            if (StringUtils.isBlank(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.getResult(910, request, response);
            }
            if (StringUtils.isBlank(additionalKey)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_ADDITIONAL_KEY_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(915)), 915));
                return YxHelper.getResult(915, request, response);
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.getResult(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(userId);
            sb.append(tp);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.getResult(909, request, response);
            }
            UserLoginRequestRecorder.getInstance().put(yx, userId, tp, additionalKey);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxAction.errorLog.error("preLogin_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("pay")
    public ByteResult pay(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("playerId") final int playerId, @RequestParam("orderId") final String orderId, @RequestParam("gold") final int gold, @RequestParam("tp") final String tp, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final Tuple<String, String> tuple = new Tuple();
        tuple.left = userId;
        tuple.right = yx;
        final Session session = request.getSession(false);
        if (session != null) {
            session.setAttribute("yx", tuple);
        }
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return YxHelper.getResult(908, request, response);
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return YxHelper.getResult(901, request, response);
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.getResult(902, request, response);
            }
            if (StringUtils.isBlank(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.getResult(910, request, response);
            }
            if (playerId <= 0 && !PluginContext.configuration.isSingleRole(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_PLAYER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(903)), 903));
                return YxHelper.getResult(903, request, response);
            }
            if (gold <= 0) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_ERROR", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(904)), 904));
                return YxHelper.getResult(904, request, response);
            }
            if (StringUtils.isBlank(orderId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(907)), 907));
                return YxHelper.getResult(907, request, response);
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.getResult(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(userId);
            sb.append(orderId);
            sb.append(gold);
            sb.append(tp);
            sb.append(PluginContext.configuration.getPayKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.getResult(909, request, response);
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(orderId, playerId, userId, yx, gold, request);
            if (result.left == 1) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(result.left)), result.left));
            return YxHelper.getResult(result.left, request, response);
        }
        catch (Exception e) {
            YxAction.errorLog.error("pay_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("playerInfo")
    public ByteResult queryPlayer(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final String tp, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "902"));
            }
            final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(userId, yx);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("players");
            for (final YxPlayerInfo player : playerList) {
                player.buildJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("playerInfo_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx@queryPlayerPayDetails")
    public ByteResult queryPlayerPayDetails(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("playerId") final int playerId, @RequestParam("tp") final String tp, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "902"));
            }
            if (playerId <= 0) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_PLAYER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(903)), 903));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "903"));
            }
            if (StringUtils.isBlank(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "910"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(userId);
            if (!PluginContext.configuration.isSingleRole(yx)) {
                sb.append(playerId);
            }
            sb.append(tp);
            sb.append(PluginContext.configuration.getQueryKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            final List<YxPlayerPayInfo> payList = this.yxOperation.queryPlayerPayDetails(yx, userId, playerId);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("payHistory");
            for (final YxPlayerPayInfo player : payList) {
                player.buildJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("yx@queryPlayerPayDetails_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryPlayerPayDetails", "yx@queryPlayerPayDetails_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("yx@queryYxSource")
    public PlainResult queryYxSource(@RequestParam("date") final String date, @RequestParam("yx") final String yx, @RequestParam("ticket") final String ticket, @RequestParam("page") int page, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new PlainResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(date)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_fail_DATE_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(923)), 923));
                return new PlainResult(JsonBuilder.getJson(State.FAIL, "923"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new PlainResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new PlainResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Date queryDate = new Date();
            try {
                queryDate = df.parse(date);
            }
            catch (ParseException e) {
                YxAction.errorLog.error("yx@queryYxSource_fail_EXCEPTION", e);
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yyx@queryYxSource_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
                return YxHelper.getPlainResult(6, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getQueryKey(yx));
            sb.append(date);
            sb.append(yx);
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_fail_WRONG_TICKET", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new PlainResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            if (page < 1) {
                page = 1;
            }
            final List<YxSourceInfo> yxSourceInfoList = this.yxOperation.queryPlayerYxSource(yx, queryDate, page, YxAction.SIZE);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            final int totalSize = this.yxOperation.queryPlayerYxSourceSize(yx, queryDate);
            int totalPage = 1;
            if (totalSize > 0) {
                totalPage = (totalSize - 1) / YxAction.SIZE + 1;
            }
            doc.createElement("totalSize", totalSize);
            doc.createElement("totalPage", totalPage);
            doc.createElement("page", page);
            doc.startArray("list");
            for (final YxSourceInfo yxSourceInfo : yxSourceInfoList) {
                yxSourceInfo.buildJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yx@queryYxSource_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new PlainResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e2) {
            YxAction.errorLog.error("yx@queryYxSource_fail_EXCEPTION", e2);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yx@queryYxSource", "yyx@queryYxSource_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getPlainResult(6, request, response);
        }
    }
    
    @Command("batchPlayerInfo")
    public ByteResult batchPlayerInfo(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final String tp, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(userId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "902"));
            }
            if (StringUtils.isBlank(tp)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "910"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(tp);
            sb.append(PluginContext.configuration.getQueryKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            final String[] userIds = userId.trim().split(",");
            doc.startArray("users");
            String[] array;
            for (int length = (array = userIds).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(temp.trim(), yx);
                doc.startObject();
                doc.startArray("players");
                for (final YxPlayerInfo player : playerList) {
                    player.buildJson(doc);
                }
                doc.endArray();
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("batchPlayerInfo_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("batchPlayerInfo", "batchPlayerInfo_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("queryOrder")
    public ByteResult queryOrder(@RequestParam("yx") final String yx, @RequestParam("orderId") final String orderId, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(orderId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(907)), 907));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "907"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(orderId);
            sb.append(PluginContext.configuration.getQueryKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            final YxPlayerPayInfo pi = this.yxOperation.queryOrder(orderId, yx);
            doc.startArray("order");
            if (pi != null) {
                pi.buildJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("queryOrder_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryOrder", "queryOrder_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("queryNotice")
    public ByteResult queryNotice(@RequestParam("yx") final String yx, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(PluginContext.configuration.getQueryKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            final List<YxNoticeInfo> ynList = this.yxOperation.queryNotice(yx);
            doc.startArray("order");
            for (final YxNoticeInfo yn : ynList) {
                yn.buildJson(doc);
            }
            doc.endArray();
            doc.endObject();
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("queryNotice_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("queryNotice", "queryNotice_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    @Command("modifyUserId")
    public ByteResult modifyUserId(@RequestParam("yx") final String yx, @RequestParam("oldUserId") final String oldUserId, @RequestParam("newUserId") final String newUserId, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_IP_IS_FORBIDDEN_PASSIP_" + passIp, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(908)), 908));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "908"));
            }
            if (StringUtils.isBlank(yx)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_YX_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(901)), 901));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "901"));
            }
            if (StringUtils.isBlank(oldUserId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_OLD_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1009)), 1009));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "1009"));
            }
            if (StringUtils.isBlank(newUserId)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_NEW_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1010)), 1010));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "1010"));
            }
            if (StringUtils.isBlank(ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "922"));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(yx);
            sb.append(oldUserId);
            sb.append(newUserId);
            sb.append(PluginContext.configuration.getQueryKey(yx));
            if (!YxHelper.isTicketPass(CodecUtil.md5(sb.toString()), ticket)) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + CodecUtil.md5(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return new ByteResult(JsonBuilder.getJson(State.FAIL, "909"));
            }
            final Tuple<Boolean, Integer> tuple = this.yxOperation.modifyUserId(yx, oldUserId, newUserId);
            if (tuple.left) {
                YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return new ByteResult(JsonBuilder.getJson(State.SUCCESS, ""));
            }
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_" + tuple.right, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(tuple.right)), tuple.right));
            return new ByteResult(JsonBuilder.getJson(State.FAIL, new StringBuilder().append(tuple.right).toString()));
        }
        catch (Exception e) {
            YxAction.errorLog.error("modifyUserId_fail_EXCEPTION", e);
            YxAction.opReport.info(OpLogUtil.formatOpInterfaceLog("modifyUserId", "modifyUserId_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
}
