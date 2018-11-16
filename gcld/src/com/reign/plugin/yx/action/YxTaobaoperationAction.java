package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.result.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import java.text.*;
import com.reign.plugin.yx.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.util.codec.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.plugin.yx.common.xml.*;
import com.reign.util.*;
import com.reign.framework.netty.util.*;
import java.net.*;
import com.reign.plugin.yx.common.*;
import java.util.*;
import com.reign.framework.json.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxTaobaoperationAction
{
    private static final Log errorLog;
    private static final Log opReport;
    private static final String SECURITY_GUARD_TAOBAO = "taobao";
    private static final String LOGIN = "login";
    private static final String PAY = "pay";
    private static final String QUERY = "query";
    private static final String GOLD_PAY = "gold_pay";
    private static final String QUERY_INFO = "query_info";
    private static final String QUERY_STATUS = "query_status";
    private static final String INTEFACE_FAIL = "fail";
    private static final String INTEFACE_SUCCESS = "success";
    public static final String TAOBAO_SNAP_FORMAT = "{0} | {1} | {2}";
    private static final String ORDER_FAILED = "ORDER_FAILED";
    private static final String SUCCESS = "SUCCESS";
    private static final String REQUEST_FAILED = "REQUEST_FAILED";
    private static final String FAILED = "FAILED";
    private static final String USER_ACOUNT_ERROR = "USER_ACOUNT_ERROR";
    ByteResult PAY_SUCCESS;
    ByteResult PAY_DUPLICATE;
    ByteResult PAY_FAIL;
    private static final String TYPE_TJB = "tjb";
    private static final String TYPE_WXIN = "wxin";
    private static Timer timer;
    @Autowired
    IYxOperation yxOperation;
    private static Map<String, Integer> tscList;
    public static Map<String, String> serverIdMap;
    
    static {
        errorLog = LogFactory.getLog("com.reign.gcld.error");
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        YxTaobaoperationAction.timer = new Timer();
        (YxTaobaoperationAction.tscList = new HashMap<String, Integer>()).put("GG3AAE9TK", 5);
        YxTaobaoperationAction.tscList.put("Q6RUJ55Q7", 10);
        YxTaobaoperationAction.tscList.put("64CJNPJNP", 20);
        YxTaobaoperationAction.tscList.put("9XUD5FTYC", 30);
        YxTaobaoperationAction.tscList.put("AXPMRCKP3", 50);
        YxTaobaoperationAction.tscList.put("C6ELWQDJ7", 100);
        YxTaobaoperationAction.tscList.put("M7GECLUCP", 200);
        YxTaobaoperationAction.tscList.put("P9ATXT4VG", 300);
        YxTaobaoperationAction.tscList.put("KN9GL9737", 500);
        YxTaobaoperationAction.tscList.put("EC3FRS4HM", 1000);
        YxTaobaoperationAction.tscList.put("TAGDWUSQT", 2000);
        YxTaobaoperationAction.tscList.put("XHF9GVGNR", 3000);
        YxTaobaoperationAction.tscList.put("5PJ7PA9QF", 5000);
        YxTaobaoperationAction.tscList.put("4VMQVMVWT", 10000);
        (YxTaobaoperationAction.serverIdMap = new ConcurrentHashMap<String, String>()).put("34210", "1");
        YxTaobaoperationAction.serverIdMap.put("35147", "2");
        YxTaobaoperationAction.serverIdMap.put("35148", "3");
        YxTaobaoperationAction.serverIdMap.put("35149", "4");
        YxTaobaoperationAction.serverIdMap.put("35150", "5");
        YxTaobaoperationAction.serverIdMap.put("35151", "6");
        YxTaobaoperationAction.serverIdMap.put("35152", "7");
        YxTaobaoperationAction.serverIdMap.put("35153", "8");
        YxTaobaoperationAction.serverIdMap.put("35154", "9");
        YxTaobaoperationAction.serverIdMap.put("35155", "10");
        YxTaobaoperationAction.serverIdMap.put("35156", "11");
        YxTaobaoperationAction.serverIdMap.put("35157", "12");
        YxTaobaoperationAction.serverIdMap.put("35158", "13");
        YxTaobaoperationAction.serverIdMap.put("35159", "14");
        YxTaobaoperationAction.serverIdMap.put("35160", "15");
        YxTaobaoperationAction.serverIdMap.put("35161", "16");
        YxTaobaoperationAction.serverIdMap.put("35162", "17");
        YxTaobaoperationAction.serverIdMap.put("35163", "18");
        YxTaobaoperationAction.serverIdMap.put("35164", "19");
        YxTaobaoperationAction.serverIdMap.put("35165", "20");
    }
    
    public YxTaobaoperationAction() {
        this.PAY_SUCCESS = new ByteResult("1".getBytes());
        this.PAY_DUPLICATE = new ByteResult("2".getBytes());
        this.PAY_FAIL = new ByteResult("0".getBytes());
    }
    
    @Command("yxTaobaoLogin")
    public ByteResult login(@RequestParam("tbCoopId") String tbCoopId, @RequestParam("tbUid") String tbUid, @RequestParam("gameId") String gameId, @RequestParam("gatewayId") String gatewayId, @RequestParam("sign") final String sign, @RequestParam("isAdult") String isAdult, @RequestParam("loginTime") String loginTime, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxTaobaoLogin";
        try {
            final String content = new String(request.getContent());
            final Map<String, Object> map = this.parseParam(content);
            final String yx = "taobao";
            if (StringUtils.isBlank(tbCoopId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(gameId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(tbUid)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TBUSERID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_TBUSERID_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(gatewayId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY.errorCode, request, response);
            }
            gatewayId = URLDecoder.decode(gatewayId, "GBK");
            if (!YxTaobaoperationAction.serverIdMap.keySet().contains(gatewayId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY.errorCode, request, response);
            }
            gatewayId = YxTaobaoperationAction.serverIdMap.get(gatewayId);
            if (StringUtils.isBlank(gatewayId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_GATEWAYID_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(isAdult)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_ISADULT_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_ISADULT_IS_EMPTY.errorCode, request, response);
            }
            if (StringUtils.isBlank(loginTime)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_LOGINTIME_IS_EMPTY, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_LOGINTIME_IS_EMPTY.errorCode, request, response);
            }
            tbCoopId = URLDecoder.decode(tbCoopId, "GBK");
            gameId = URLDecoder.decode(gameId, "GBK");
            isAdult = URLDecoder.decode(isAdult, "GBK");
            loginTime = URLDecoder.decode(loginTime, "GBK");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            final Date date = dateFormat.parse(loginTime);
            long interval = System.currentTimeMillis() - date.getTime();
            if (interval < 0L) {
                interval *= -1L;
            }
            if (interval / 1000L / 60L > 2L) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TIME_OUT, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_TIME_OUT.errorCode, request, response);
            }
            tbUid = map.get("tbUid");
            final String tbUidAfterDecode = URLDecoder.decode(tbUid, "GBK");
            final StringBuilder sb = this.getSignInfo2(map);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            final String md5 = CodecUtil.md5(sb.toString());
            YxTaobaoperationAction.errorLog.error("sb :" + sb.toString() + " md5:" + md5 + " sin:" + sign);
            if (!YxHelper.isTicketPass(md5, sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "login", false);
                return YxHelper.redirectUnlogPage(TaobaoES.TAOBAO_ERROR_WRONG_TICKET.errorCode, request, response);
            }
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(gatewayId) && !this.getMainServerId(gatewayId).equals(currentServerId)) {
                String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(gatewayId), "yxTaobaoLogin" });
                redirectUrl = String.valueOf(redirectUrl) + "?" + content;
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "login_REDIRECT_GATE_WAY_ID_" + gatewayId, true);
                return YxHelper.getResult(1, request, response);
            }
            final byte[] userByte = Base64.decode(tbUidAfterDecode);
            final String userId = new String(userByte);
            YxTaobaoperationAction.errorLog.error("userId:" + userId);
            final String adult = "1".equalsIgnoreCase(isAdult) ? "1" : "0";
            final Session session = this.yxOperation.login(yx, userId, "", "", adult, yx, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL(yx));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "login", true);
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error("login_fail_EXCEPTION", e);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "login", false);
            return YxHelper.getResult(6, request, response);
        }
    }
    
    private StringBuilder getSignInfo(final Map<String, String[]> paramterMap) {
        try {
            final StringBuilder sb = new StringBuilder();
            if (paramterMap == null) {
                return sb;
            }
            final List<String> keys = new ArrayList<String>(paramterMap.keySet());
            Collections.sort(keys);
            for (final String key : keys) {
                if (key.equalsIgnoreCase("sign")) {
                    continue;
                }
                final String[] value = paramterMap.get(key);
                sb.append(key);
                if (value == null) {
                    continue;
                }
                String[] array;
                for (int length = (array = value).length, i = 0; i < length; ++i) {
                    final String s = array[i];
                    sb.append(s);
                }
            }
            return sb;
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
            return new StringBuilder();
        }
    }
    
    private StringBuilder getSignInfo2(final Map<String, Object> paramterMap) {
        try {
            final StringBuilder sb = new StringBuilder();
            if (paramterMap == null) {
                return sb;
            }
            final List<String> keys = new ArrayList<String>(paramterMap.keySet());
            Collections.sort(keys);
            for (final String key : keys) {
                if (key.equalsIgnoreCase("sign")) {
                    continue;
                }
                final String value = paramterMap.get(key);
                sb.append(key);
                if (value == null) {
                    continue;
                }
                sb.append(value);
            }
            return sb;
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
            return new StringBuilder();
        }
    }
    
    private void errorOpReport(final Request request, final TaobaoES es, final long start, final String interfaceName, final boolean isSuc) {
        try {
            final String detail = String.valueOf(interfaceName) + "_" + (isSuc ? "success" : "fail");
            YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog(interfaceName, String.valueOf(detail) + es.getErrorString(), YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(es.getErrorCodeLog())), es.getErrorCodeLog()));
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
        }
    }
    
    @Command("yxTaobaoOrderQuery")
    public ByteResult orderQuery(@RequestParam("coopId") String coopId, @RequestParam("tbOrderNo") String tbOrderNo, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String yx = "taobao";
        try {
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, false, "REQUEST_FAILED", "");
            }
            if (StringUtils.isBlank(coopId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY, false, "REQUEST_FAILED", "");
            }
            if (!coopId.equalsIgnoreCase(PluginContext.configuration.getTaobaoCoopId(yx))) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_COOPID_WRONG_REQUEST, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_COOPID_WRONG_REQUEST, false, "REQUEST_FAILED", "");
            }
            if (StringUtils.isBlank(tbOrderNo)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY, false, "REQUEST_FAILED", "");
            }
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, false, "REQUEST_FAILED", "");
            }
            final StringBuilder sb = this.getSignInfo(request.getParamterMap());
            coopId = URLDecoder.decode(coopId, "GBK");
            tbOrderNo = URLDecoder.decode(tbOrderNo, "GBK");
            sb.append(PluginContext.configuration.getQueryKey(yx));
            final String md5 = CodecUtil.md5(sb.toString());
            YxTaobaoperationAction.errorLog.error("sb :" + sb.toString() + " md5:" + md5 + " sin:" + sign);
            if (!YxHelper.isTicketPass(md5, sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, false, "REQUEST_FAILED", "");
            }
            final YxPlayerPayInfo ypi = this.yxOperation.queryOrder(tbOrderNo, yx);
            if (ypi == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_ORDER_DOESNT_EXIST, start, "query", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_ORDER_DOESNT_EXIST, false, "REQUEST_FAILED", "");
            }
            this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "query", true);
            final String orderSnap = MessageFormatter.format("{0} | {1} | {2}", new Object[] { ypi.getGold() / 10, PluginContext.configuration.getServerName(yx), PluginContext.configuration.getServerIdS(yx) });
            return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_SUCCESS, true, "SUCCESS", orderSnap);
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error("query_fail_EXCEPTION", e);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "query", false);
            return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_EXCEPTION, false, "REQUEST_FAILED", "");
        }
    }
    
    private ByteResult getXMLResult(final String tbOrderNo, final TaobaoES es, final boolean isSuc, final String orderStatus, final String snap) {
        final XMLDocumentUpper doc = new XMLDocumentUpper("1.0", "utf-8");
        doc.startRoot("response");
        doc.createElement("tbOrderNo", tbOrderNo);
        doc.createElement("coopOrderNo", tbOrderNo);
        doc.createElement("coopOrderStatus", orderStatus);
        if (isSuc) {
            doc.createElement("coopOrderSnap", snap);
            final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            final String par2 = format.format(new Date());
            doc.createElement("coopOrderSuccessTime", par2);
            doc.createElement("failedCode", "");
            doc.createElement("failedReason", "");
        }
        else {
            doc.createElement("coopOrderSnap", "");
            doc.createElement("coopOrderSuccessTime", "");
            doc.createElement("failedCode", es.errorCode);
            doc.createElement("failedReason", es.errorString);
        }
        doc.endRoot("response");
        return new ByteResult(doc.toString().getBytes());
    }
    
    @Command("yxTaobaoPay")
    public ByteResult pay(@RequestParam("coopId") String coopId, @RequestParam("tbOrderNo") String tbOrderNo, @RequestParam("cardId") String cardId, @RequestParam("cardNum") String cardNum, @RequestParam("customer") String customer, @RequestParam("sum") String sum, @RequestParam("gameId") final String gameId, @RequestParam("section1") String section1, @RequestParam("section2") final String section2, @RequestParam("tbOrderSnap") String tbOrderSnap, @RequestParam("notifyUrl") String notifyUrl, @RequestParam("sign") final String sign, @RequestParam("version") final String version, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxTaobaoPay";
        try {
            final String yx = "taobao";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(coopId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_COOPID_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (!coopId.equalsIgnoreCase(PluginContext.configuration.getTaobaoCoopId(yx))) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_COOPID_WRONG_ORDER, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_COOPID_WRONG_ORDER, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(tbOrderNo)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(cardId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CARDID_IS_NOT_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_CARDID_IS_NOT_EMPTY, false, "ORDER_FAILED", "");
            }
            if (!YxTaobaoperationAction.tscList.keySet().contains(cardId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CARDID_WRONG, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_CARDID_WRONG, false, "ORDER_FAILED", "");
            }
            final int cardValue = YxTaobaoperationAction.tscList.get(cardId);
            if (StringUtils.isBlank(cardNum)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CARDNUMBER_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_CARDNUMBER_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(customer)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(sum)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, false, "REQUEST_FAILED", "");
            }
            section1 = URLDecoder.decode(section1, "GBK");
            if (!YxTaobaoperationAction.serverIdMap.keySet().contains(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, false, "REQUEST_FAILED", "");
            }
            section1 = YxTaobaoperationAction.serverIdMap.get(section1);
            if (StringUtils.isBlank(tbOrderSnap)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TAOBAOORDERSNAP_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_TAOBAOORDERSNAP_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(notifyUrl)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_NOTIFYURL_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_NOTIFYURL_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, false, "ORDER_FAILED", "");
            }
            cardId = URLDecoder.decode(cardId, "GBK");
            cardNum = URLDecoder.decode(cardNum, "GBK");
            coopId = URLDecoder.decode(coopId, "GBK");
            customer = URLDecoder.decode(customer, "GBK");
            sum = URLDecoder.decode(sum, "GBK");
            tbOrderNo = URLDecoder.decode(tbOrderNo, "GBK");
            tbOrderSnap = this.getTbOrderSnap(request.getContent());
            final String[] snap = { tbOrderSnap };
            request.getParamterMap().put("tbOrderSnap", snap);
            notifyUrl = URLDecoder.decode(notifyUrl, "GBK");
            int gold = cardValue * Integer.parseInt(cardNum);
            gold *= 10;
            final StringBuilder sb = this.getSignInfo(request.getParamterMap());
            sb.append(PluginContext.configuration.getPayKey(yx));
            final String md5 = CodecUtil.md5(sb.toString(), "gbk");
            final String utf8 = CodecUtil.md5(sb.toString(), "utf-8");
            YxTaobaoperationAction.errorLog.error("sb :" + sb.toString() + " md5:" + md5 + " sin:" + sign + "utf8:" + utf8);
            if (!YxHelper.isTicketPass(md5, sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "pay", false);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, false, "ORDER_FAILED", "");
            }
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(String.valueOf(section1)) && !this.getMainServerId(section1).equals(currentServerId)) {
                try {
                    final String content = new String(request.getContent(), "gbk");
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(section1), "yxTaobaoPay" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content, "gbk");
                    YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + section1 + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxTaobaoperationAction.errorLog.error("pay_fail_EXCEPTION", e);
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "pay", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
                }
            }
            final int playerId = this.yxOperation.getDefaultPayPlayer(customer, yx);
            final Tuple<Integer, Integer> result = this.yxOperation.pay(tbOrderNo, playerId, customer, yx, gold, request);
            if (result.left == 1) {
                this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "pay", true);
                final Map<String, Object> paramMap = this.getAsynParaMap(coopId, tbOrderNo, tbOrderSnap, PluginContext.configuration.getPayKey(yx), true, "");
                this.doAsynWork(notifyUrl, paramMap);
                return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_SUCCESS, true, "SUCCESS", tbOrderSnap);
            }
            TaobaoES es = TaobaoES.TAOBAO_ERROR_PAY_FAIL;
            ByteResult temp = this.PAY_FAIL;
            if (result.left == 5) {
                temp = this.PAY_DUPLICATE;
                es = TaobaoES.TAOBAO_ERROR_ORDER_EXIST;
            }
            else if (result.left == 2) {
                YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, temp, result.left));
                final Map<String, Object> paramMap2 = this.getAsynParaMap(coopId, tbOrderNo, es.errorCode, PluginContext.configuration.getPayKey(yx), false, "USER_ACOUNT_ERROR");
                this.doAsynWork(notifyUrl, paramMap2);
                return this.getXMLResult(tbOrderNo, es, false, "FAILED", "");
            }
            YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, temp, result.left));
            return this.getXMLResult(tbOrderNo, es, false, "ORDER_FAILED", "");
        }
        catch (Exception e2) {
            YxTaobaoperationAction.errorLog.error("pay_fail_EXCEPTION", e2);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "pay", false);
            return this.getXMLResult(tbOrderNo, TaobaoES.TAOBAO_ERROR_EXCEPTION, false, "ORDER_FAILED", "");
        }
    }
    
    private String getTbOrderSnap(final byte[] c) {
        try {
            final String content = new String(c);
            if (StringUtils.isBlank(content)) {
                return "";
            }
            final String str = content.trim();
            final String[] strs = str.split("&");
            String[] array;
            for (int length = (array = strs).length, i = 0; i < length; ++i) {
                final String value = array[i];
                final String[] values = value.split("=");
                final String k = Utils.decode(values[0], "gbk");
                if (k.equalsIgnoreCase("tbOrderSnap")) {
                    return Utils.decode(values[1], "gbk");
                }
            }
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
        }
        return "";
    }
    
    private Map<String, Object> getAsynParaMap(String coopId, String tbOrderNo, String snapOrCode, final String key, final boolean isSuc, String errorReason) {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            coopId = URLEncoder.encode(coopId, "GBK");
            tbOrderNo = URLEncoder.encode(tbOrderNo, "GBK");
            String status = isSuc ? "SUCCESS" : "FAILED";
            status = URLEncoder.encode(status, "GBK");
            paramMap.put("coopId", coopId);
            paramMap.put("tbOrderNo", tbOrderNo);
            paramMap.put("coopOrderNo", tbOrderNo);
            paramMap.put("coopOrderStatus", status);
            if (isSuc) {
                paramMap.put("coopOrderSnap", snapOrCode);
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = dateFormat.format(new Date());
                date = URLEncoder.encode(date, "GBK");
                paramMap.put("coopOrderSuccessTime", date);
            }
            else {
                errorReason = URLEncoder.encode(errorReason, "GBK");
                paramMap.put("failedCode", snapOrCode);
                paramMap.put("failedReason", errorReason);
            }
            final StringBuilder sb = this.getSignInfo2(paramMap);
            sb.append(PluginContext.configuration.getPayKey("taobao"));
            final String sign = CodecUtil.md5(sb.toString(), "gbk");
            paramMap.put("sign", sign);
            if (isSuc) {
                snapOrCode = URLEncoder.encode(snapOrCode, "GBK");
                paramMap.put("coopOrderSnap", snapOrCode);
            }
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
        }
        return paramMap;
    }
    
    private void doAsynWork(final String notifyUrl, final Map<String, Object> paramMap) {
        try {
            if (paramMap == null || paramMap.isEmpty()) {
                YxTaobaoperationAction.errorLog.error("paraMap is empty ----------------");
                return;
            }
            final TimerTask task = new AsynJob(notifyUrl, paramMap, YxTaobaoperationAction.errorLog);
            YxTaobaoperationAction.timer.schedule(task, 0L);
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
        }
    }
    
    @Command("yxTaobaoGoldPay")
    public ByteResult goldPay(@RequestParam("account") String account, @RequestParam("tjbOrderId") String tjbOrderId, @RequestParam("tbUid") final String tbUid, @RequestParam("gameId") final String gameId, @RequestParam("section1") String section1, @RequestParam("section2") final String section2, @RequestParam("gameMoney") String gameMoney, @RequestParam("tjb") final String tjb, @RequestParam("sign") final String sign, @RequestParam("type") String type, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxTaobaoGoldPay";
        try {
            final String yx = "taobao";
            final String content = new String(request.getContent());
            final Map<String, Object> map = this.parseParam(content);
            account = ((map.get("account") == null) ? "" : map.get("account"));
            tjbOrderId = ((map.get("tjbOrderId") == null) ? "" : map.get("tjbOrderId"));
            gameMoney = ((map.get("gameMoney") == null) ? "" : map.get("gameMoney"));
            section1 = ((map.get("section1") == null) ? "" : map.get("section1"));
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN.errorString);
            }
            if (StringUtils.isBlank(type)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TYPE_IS_WRONG, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_TYPE_IS_WRONG.errorString);
            }
            type = map.get("type");
            type = new String(Base64.decode(type));
            if (!"tjb".equalsIgnoreCase(type) && !"wxin".equalsIgnoreCase(type)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TYPE_IS_WRONG, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_TYPE_IS_WRONG.errorString);
            }
            if (StringUtils.isBlank(account)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY.errorString);
            }
            if (StringUtils.isBlank(tbUid)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_CUSTOMER_IS_EMPTY.errorString);
            }
            if (StringUtils.isBlank(gameId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            if (type.equalsIgnoreCase("tjb")) {
                if (StringUtils.isBlank(tjbOrderId)) {
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY, start, "gold_pay", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_TBORDERN0_IS_EMPTY.errorString);
                }
                if (StringUtils.isBlank(gameMoney)) {
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY, start, "gold_pay", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY.errorString);
                }
                if (StringUtils.isBlank(tjb)) {
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY, start, "gold_pay", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SUM_IS_EMPTY.errorString);
                }
            }
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorString);
            }
            account = new String(Base64.decode(account));
            section1 = new String(Base64.decode(section1));
            if (!YxTaobaoperationAction.serverIdMap.keySet().contains(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "gold_pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            section1 = YxTaobaoperationAction.serverIdMap.get(section1);
            final StringBuilder sb = this.getSignInfo2(map);
            sb.append(PluginContext.configuration.getLoginKey(yx));
            final String md5 = CodecUtil.md5(sb.toString(), "gbk");
            final String utf8 = CodecUtil.md5(sb.toString(), "utf-8");
            YxTaobaoperationAction.errorLog.error("sb :" + sb.toString() + " md5:" + md5 + " sin:" + sign + "utf8:" + utf8);
            if (!YxHelper.isTicketPass(md5, sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "pay", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_WRONG_TICKET.errorString);
            }
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(String.valueOf(section1)) && !this.getMainServerId(section1).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(section1), "yxTaobaoGoldPay" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("gold_pay", "gold_pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + section1 + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxTaobaoperationAction.errorLog.error("gold_pay_fail_EXCEPTION", e);
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "gold_pay", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
                }
            }
            final int playerId = this.yxOperation.getDefaultPayPlayer(account, yx);
            Tuple<Integer, Integer> result = null;
            if (type.equalsIgnoreCase("wxin")) {
                result = this.yxOperation.rewardWX(playerId, account, yx);
            }
            else {
                tjbOrderId = new String(Base64.decode(tjbOrderId));
                gameMoney = new String(Base64.decode(gameMoney));
                final int gold = (int)Double.parseDouble(gameMoney);
                result = this.yxOperation.pay(tjbOrderId, playerId, account, yx, gold, request);
            }
            if (result.left == 1) {
                this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "gold_pay", true);
                return this.getJsonResult(true, "");
            }
            TaobaoES es = TaobaoES.TAOBAO_ERROR_PAY_FAIL;
            ByteResult temp = this.PAY_FAIL;
            if (result.left == 5) {
                temp = this.PAY_DUPLICATE;
                es = TaobaoES.TAOBAO_ERROR_ORDER_EXIST;
            }
            YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("gold_pay", "gold_pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, temp, result.left));
            return this.getJsonResult(false, es.errorString);
        }
        catch (Exception e2) {
            YxTaobaoperationAction.errorLog.error("gold_pay_fail_EXCEPTION", e2);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "gold_pay", false);
            return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
        }
    }
    
    private Map<String, Object> parseParam(final String content) {
        try {
            if (StringUtils.isBlank(content)) {
                return null;
            }
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            final String str = content.trim();
            final String[] strs = str.split("&");
            String[] array;
            for (int length = (array = strs).length, i = 0; i < length; ++i) {
                final String value = array[i];
                final String[] values = value.split("=", 2);
                final String k = Utils.decode(values[0], "utf-8");
                if (values.length == 1) {
                    paramMap.put(k, null);
                }
                else {
                    final String v = Utils.decode(values[1], "utf-8");
                    paramMap.put(k, v);
                }
            }
            return paramMap;
        }
        catch (Exception e) {
            YxTaobaoperationAction.errorLog.error(this, e);
            return null;
        }
    }
    
    private ByteResult getJsonResult(final boolean state, final String errorString) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("status", state);
        doc.createElement("message", errorString);
        doc.endObject();
        return new ByteResult(doc.toByte());
    }
    
    @Command("yxTaobaoQueryInfo")
    public ByteResult queryInfo(final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxTaobaoQueryInfo";
        try {
            final String yx = "taobao";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN.errorString);
            }
            final String content = new String(request.getContent());
            if (StringUtils.isBlank(content)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Map<String, Object> paraMap = this.parseParam(content);
            if (paraMap == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Object gameIdOb = paraMap.get("gameId");
            if (gameIdOb == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final String gameId = (String)gameIdOb;
            if (StringUtils.isBlank(gameId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Object section1Ob = paraMap.get("section1");
            if (section1Ob == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            String section1 = (String)section1Ob;
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            section1 = new String(Base64.decode(section1));
            section1 = YxTaobaoperationAction.serverIdMap.get(section1);
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            final Object signOb = paraMap.get("sign");
            if (signOb == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorString);
            }
            final String sign = (String)signOb;
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorString);
            }
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equalsIgnoreCase(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            final StringBuilder sb = this.getSignInfo2(paraMap);
            final String key = PluginContext.configuration.getLoginKey(yx);
            sb.append(key);
            final String md5 = CodecUtil.md5(sb.toString());
            if (!md5.equalsIgnoreCase(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "query_info", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_WRONG_TICKET.errorString);
            }
            if (!currentServerId.equals(String.valueOf(section1)) && !this.getMainServerId(section1).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(section1), "yxTaobaoQueryInfo" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("query_info", "query_info_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + section1 + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxTaobaoperationAction.errorLog.error("query_info_fail_EXCEPTION", e);
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "query_info", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
                }
            }
            final int max = 3000;
            final int onlineNum = this.yxOperation.getOnlinePlayersNumber(yx);
            final int totalRoleNum = this.yxOperation.getAllPlayerNumber();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("status", true);
            doc.createElement("max", max);
            doc.createElement("onlineNum", onlineNum);
            doc.createElement("totalRoleNum", totalRoleNum);
            doc.endObject();
            this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "query_info", true);
            return new ByteResult(doc.toByte());
        }
        catch (Exception e2) {
            YxTaobaoperationAction.errorLog.error(this, e2);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "query_info", false);
            return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
        }
    }
    
    @Command("yxTaobaoQueryStatus")
    public ByteResult queryStatus(final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxTaobaoQueryStatus";
        try {
            final String yx = "taobao";
            final String ip = YxHelper.getIp(request);
            final boolean isLimitYxIP = PluginContext.configuration.isLimitYxIP(yx);
            final String passIp = PluginContext.configuration.getPassedIP(yx);
            if (isLimitYxIP && (ip == null || passIp == null || passIp.indexOf(ip) == -1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_IP_IS_FORBIDDEN.errorString);
            }
            final String content = new String(request.getContent());
            if (StringUtils.isBlank(content)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Map<String, Object> paraMap = this.parseParam(content);
            if (paraMap == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Object gameIdOb = paraMap.get("gameId");
            if (gameIdOb == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final String gameId = (String)gameIdOb;
            if (StringUtils.isBlank(gameId)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_GAMEID_IS_EMPTY.errorString);
            }
            final Object section1Ob = paraMap.get("section1");
            if (section1Ob == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            String section1 = (String)section1Ob;
            section1 = new String(Base64.decode(section1));
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            section1 = YxTaobaoperationAction.serverIdMap.get(section1);
            if (StringUtils.isBlank(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            final Object signOb = paraMap.get("sign");
            if (signOb == null) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorString);
            }
            final String sign = (String)signOb;
            if (StringUtils.isBlank(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SIGN_IS_EMPTY.errorString);
            }
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equalsIgnoreCase(section1)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_SECTION1_IS_EMPTY.errorString);
            }
            final StringBuilder sb = this.getSignInfo2(paraMap);
            final String key = PluginContext.configuration.getLoginKey(yx);
            sb.append(key);
            final String md5 = CodecUtil.md5(sb.toString());
            if (!md5.equalsIgnoreCase(sign)) {
                this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_WRONG_TICKET, start, "query_status", false);
                return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_WRONG_TICKET.errorString);
            }
            if (!currentServerId.equals(String.valueOf(section1)) && !this.getMainServerId(section1).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(section1), "yxTaobaoQueryStatus" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxTaobaoperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("query_status", "query_status_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + section1 + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    YxTaobaoperationAction.errorLog.error("query_status_fail_EXCEPTION", e);
                    this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "query_status", false);
                    return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
                }
            }
            this.errorOpReport(request, TaobaoES.TAOBAO_SUCCESS, start, "query_status", true);
            return this.getJsonResult(true, "");
        }
        catch (Exception e2) {
            YxTaobaoperationAction.errorLog.error(this, e2);
            this.errorOpReport(request, TaobaoES.TAOBAO_ERROR_EXCEPTION, start, "query_status", false);
            return this.getJsonResult(false, TaobaoES.TAOBAO_ERROR_EXCEPTION.errorString);
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("taobao");
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
