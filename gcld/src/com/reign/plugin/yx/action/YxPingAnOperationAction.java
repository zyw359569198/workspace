package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import com.reign.util.log.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.http.*;
import com.reign.plugin.yx.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.plugin.yx.common.validation.*;
import org.apache.commons.lang.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.plugin.yx.util.json.*;
import com.reign.util.*;
import com.reign.plugin.yx.common.xml.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.common.*;
import java.text.*;
import java.util.*;
import com.reign.util.codec.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxPingAnOperationAction
{
    private ErrorLogger log;
    public static final Log opReport;
    private static final String SECURITY_GUARD_PINGAN = "pingan";
    private static final String LOGIN = "login";
    private static final String PAY = "pay";
    private static final String QUERY_PLAYER = "query_player";
    private static final String QUERY_MONEY = "query_money";
    private static final String INTEFACE_FAIL = "fail";
    private static final String INTEFACE_SUCCESS = "success";
    private static final String API_KEY = "gcld001";
    private static final String YX_TOKEN_URL = "yx.pingan.token.url";
    private static final String YX_REQUEST_URL = "yx.pingan.request.url";
    public static final String PINGAN_ERRORCODE_1001 = "1001";
    public static final String PINGAN_ERRORCODE_1002 = "1002";
    public static final String PINGAN_ERRORCODE_1003 = "1003";
    public static final MultiResult tokenFail;
    public static final MultiResult ipLimit;
    public static final MultiResult signFail;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        tokenFail = new MultiResult(false, "1001", "token fail..");
        ipLimit = new MultiResult(false, "1003", "ip fail..");
        signFail = new MultiResult(false, "1001", "sign fail");
    }
    
    public YxPingAnOperationAction() {
        this.log = new ErrorLogger();
    }
    
    @Command("yxPingAnLogin")
    public ByteResult login(final Request request, final Response response, @RequestParam("format") String format) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxPingAnLogin";
        try {
            System.out.println("PingAN........");
            final String yx = "pingan";
            final String content = new String(request.getContent());
            final String[] urls = ((HttpRequest)request).getUrl().split("\\?");
            final String url = urls[0];
            format = ((format != null && format.equalsIgnoreCase("xml")) ? "xml" : "json");
            final YxValidation validation = new YxPingAnValidation(content, url);
            MultiResult toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.API_KEY.getName();
            toCheck.result2 = "gcld001";
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.USER_ID.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.USER_NAME.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.TIMESTAMP.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SIGN.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.TOKEN.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SERVER_ID.getName();
            validation.addCheck(toCheck);
            final MultiResult result = validation.validateParameter();
            if (result == null) {
                return YxHelper.redirectUnlogPage("", request, response);
            }
            if (!(boolean)result.result1) {
                this.errorOpReport(request, result, start, "login", false);
                return YxHelper.redirectUnlogPage((String)result.result2, request, response);
            }
            final String key = PluginContext.configuration.getLoginKey(yx);
            final boolean sha1 = validation.validateSign(1, key);
            if (!sha1) {
                this.errorOpReport(request, YxPingAnOperationAction.signFail, start, "login", false);
                return YxHelper.redirectUnlogPage((String)YxPingAnOperationAction.signFail.result2, request, response);
            }
            final String requestURL = PluginContext.configuration.getPingAnUrl(yx, "yx.pingan.token.url");
            final String token = validation.getValueByName(ValidationEnum.TOKEN.getName());
            final String code = this.sendAndGetToken(token, requestURL);
            if (code.equalsIgnoreCase("0")) {
                this.errorOpReport(request, YxPingAnOperationAction.tokenFail, start, "login", false);
                return YxHelper.redirectUnlogPage((String)YxPingAnOperationAction.tokenFail.result2, request, response);
            }
            final String gatewayId = validation.getValueByName(ValidationEnum.SERVER_ID.getName());
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(gatewayId) && !this.getMainServerId(gatewayId).equals(currentServerId)) {
                String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(gatewayId), "yxPingAnLogin" });
                redirectUrl = String.valueOf(redirectUrl) + "?" + content;
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Location", redirectUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                this.errorOpReport(request, YxValidation.sucResult, start, "login_REDIRECT_GATE_WAY_ID_" + gatewayId, true);
                return YxHelper.getResult(1, request, response);
            }
            final String userId = validation.getValueByName(ValidationEnum.USER_ID.getName());
            String adult = validation.getValueByName(ValidationEnum.CM_FLAG.getName());
            if (StringUtils.isBlank(adult)) {
                adult = "1";
            }
            else {
                adult = (adult.equalsIgnoreCase("y") ? "0" : "1");
            }
            final Session session = this.yxOperation.login(yx, userId, "", "", adult, yx, request);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            response.addHeader("Location", PluginContext.configuration.getGameURL(yx));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            this.errorOpReport(request, YxValidation.sucResult, start, "login_REDIRECT_GATE_WAY_ID_" + gatewayId, true);
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            this.log.error(this, e);
            this.errorOpReport(request, YxValidation.excResult, start, "login", false);
            return YxHelper.getResult(6, request, response);
        }
    }
    
    private String sendAndGetToken(final String token, final String url) {
        try {
            final String tokenContent = String.valueOf(ValidationEnum.API_KEY.getName()) + "=" + "gcld001" + "&" + ValidationEnum.TOKEN.getName() + "=" + token;
            final String echo = WebUtils.sendRequest(url, tokenContent);
            final JSONObject json = new JSONObject(echo);
            final String code = json.getString("code");
            return code;
        }
        catch (Exception e) {
            this.log.error(this, e);
            return "0";
        }
    }
    
    private void errorOpReport(final Request request, final MultiResult mr, final long start, final String interfaceName, final boolean isSuc) {
        try {
            final String errorCode = (String)mr.result2;
            final String errorString = (String)mr.result3;
            final String detail = String.valueOf(interfaceName) + "_" + (isSuc ? "success" : "fail");
            YxPingAnOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog(interfaceName, String.valueOf(detail) + errorString, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(errorCode)), Integer.parseInt(errorCode)));
        }
        catch (Exception e) {
            this.log.error(this, e);
        }
    }
    
    private String getMainServerId(final String gateWayId) {
        final String hefuList = PluginContext.configuration.getJDHeFuList("pingan");
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
    
    @Command("yxPingAnPay")
    public ByteResult pay(final Request request, final Response response, @RequestParam("format") String format) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxPingAnPay";
        YxPingAnOperationAction.opReport.error("AAAAAAAAAAAAAAAAAAAAA");
        try {
            final String yx = "pingan";
            format = ((format != null && format.equalsIgnoreCase("xml")) ? "xml" : "json");
            final String content = new String(request.getContent());
            final String[] urls = ((HttpRequest)request).getUrl().split("\\?");
            final String url = urls[0];
            final YxValidation validation = new YxPingAnValidation(content, url);
            MultiResult toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.API_KEY.getName();
            toCheck.result2 = "gcld001";
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.USER_ID.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.ORDER_ID.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.RATE.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.AMOUNT.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.TIMESTAMP.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SIGN.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SERVER_ID.getName();
            validation.addCheck(toCheck);
            final MultiResult result = validation.validateParameter();
            if (result == null) {
                this.errorOpReport(request, YxValidation.excResult, start, "pay", false);
                return this.getResult(format, false, null);
            }
            if (!(boolean)result.result1) {
                this.errorOpReport(request, result, start, "pay", false);
                return this.getResult(format, false, null);
            }
            final String key = PluginContext.configuration.getLoginKey(yx);
            final boolean sha1 = validation.validateSign(1, key);
            if (!sha1) {
                this.errorOpReport(request, YxPingAnOperationAction.signFail, start, "pay", false);
                return this.getResult(format, false, null);
            }
            YxPingAnOperationAction.opReport.error("AAAAAAAAAAAAAAAAAAAAA");
            final String gatewayId = validation.getValueByName(ValidationEnum.SERVER_ID.getName());
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(String.valueOf(gatewayId)) && !this.getMainServerId(gatewayId).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(gatewayId), "yxPingAnPay" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxPingAnOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gatewayId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    this.log.error(this, e);
                    this.errorOpReport(request, YxValidation.excResult, start, "pay", false);
                    return this.getResult(format, false, null);
                }
            }
            final String rate = validation.getValueByName(ValidationEnum.RATE.getName());
            final String amount = validation.getValueByName(ValidationEnum.AMOUNT.getName());
            final String orderId = validation.getValueByName(ValidationEnum.ORDER_ID.getName());
            final String customer = validation.getValueByName(ValidationEnum.USER_ID.getName());
            final int gold = (int)Math.floor(Float.parseFloat(rate) * Float.parseFloat(amount));
            final int playerId = this.yxOperation.getDefaultPayPlayer(customer, yx);
            final Tuple<Integer, Integer> pay = this.yxOperation.pay(orderId, playerId, customer, yx, gold, request);
            YxPingAnOperationAction.opReport.error("AAAAAAAAAAAAAAAAAAAAA-------pay.left:" + pay.left + "pay.right:" + pay.right);
            if (pay.left == 1) {
                this.errorOpReport(request, YxValidation.sucResult, start, "pay", true);
                return this.getResult(format, true, null);
            }
            final MultiResult re = new MultiResult(false, pay.left, pay.right);
            this.errorOpReport(request, re, start, "pay", false);
            return this.getResult(format, false, null);
        }
        catch (Exception e2) {
            this.log.error(this, e2);
            this.errorOpReport(request, YxValidation.excResult, start, "pay", false);
            return this.getResult(format, false, null);
        }
    }
    
    private ByteResult getResult(final String format, final boolean flag, final DataInfo info) {
        if (format != null && format.equalsIgnoreCase("xml")) {
            final XMLDocument doc = new XMLDocument();
            doc.startRoot("root");
            doc.createElement("code", flag ? 1 : 0);
            if (info != null) {
                doc.createElement("data", info.getXmlInfo());
            }
            doc.endRoot("root");
            return new ByteResult(doc.toString().getBytes());
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("code", flag ? 1 : 0);
        if (info != null) {
            doc2.appendJson("data", info.getJsonInfo());
        }
        doc2.endObject();
        return new ByteResult(doc2.toByte());
    }
    
    private static ByteResult getListResult(final String format, final boolean flag, final List<PingAnPlayerInfo> info) {
        if (format.equalsIgnoreCase("xml")) {
            final XMLDocument doc = new XMLDocument();
            doc.startRoot("root");
            doc.createElement("code", flag ? 1 : 0);
            if (info != null) {
                doc.startRoot("data");
                for (final DataInfo cell : info) {
                    doc.createElement("role", cell.getXmlInfo());
                }
                doc.endRoot("data");
            }
            doc.endRoot("root");
            System.out.println(doc.toString());
            return new ByteResult(doc.toString().getBytes());
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("code", flag ? 1 : 0);
        if (info != null) {
            doc2.startArray("data");
            for (final DataInfo cell : info) {
                doc2.appendJson(cell.getJsonInfo());
            }
            doc2.endArray();
        }
        doc2.endObject();
        System.out.println(doc2.toString());
        return new ByteResult(doc2.toByte());
    }
    
    @Command("yxPingAnQueryPlayer")
    public ByteResult queryPlayer(final Request request, final Response response, @RequestParam("format") String format) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxPingAnQueryPlayer";
        try {
            final String yx = "pingan";
            final String content = new String(request.getContent());
            format = ((format != null && format.equalsIgnoreCase("xml")) ? "xml" : "json");
            final String[] urls = ((HttpRequest)request).getUrl().split("\\?");
            final String url = urls[0];
            final YxValidation validation = new YxPingAnValidation(content, url);
            MultiResult toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.API_KEY.getName();
            toCheck.result2 = "gcld001";
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.USER_ID.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.TIMESTAMP.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SIGN.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SERVER_ID.getName();
            validation.addCheck(toCheck);
            final MultiResult result = validation.validateParameter();
            if (result == null) {
                this.errorOpReport(request, YxValidation.excResult, start, "query_player", false);
                return this.getResult(format, false, null);
            }
            if (!(boolean)result.result1) {
                this.errorOpReport(request, result, start, "query_player", false);
                return this.getResult(format, false, null);
            }
            final String key = PluginContext.configuration.getLoginKey(yx);
            final boolean sha1 = validation.validateSign(1, key);
            if (!sha1) {
                this.errorOpReport(request, YxPingAnOperationAction.signFail, start, "query_player", false);
                return this.getResult(format, false, null);
            }
            final String gatewayId = validation.getValueByName(ValidationEnum.SERVER_ID.getName());
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(String.valueOf(gatewayId)) && !this.getMainServerId(gatewayId).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(gatewayId), "yxPingAnQueryPlayer" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxPingAnOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("query_player", "query_player_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gatewayId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    this.log.error(this, e);
                    this.errorOpReport(request, YxValidation.excResult, start, "query_player", false);
                    return this.getResult(format, false, null);
                }
            }
            final String customer = validation.getValueByName(ValidationEnum.USER_ID.getName());
            final List<PingAnPlayerInfo> playerInfo = this.yxOperation.queryPlayerInfo(customer, yx, gatewayId);
            this.errorOpReport(request, YxValidation.excResult, start, "query_player", true);
            return getListResult(format, false, playerInfo);
        }
        catch (Exception e2) {
            this.log.error(this, e2);
            this.errorOpReport(request, YxValidation.excResult, start, "pay", false);
            return this.getResult(format, false, null);
        }
    }
    
    @Command("yxPingAnQueryMoney")
    public ByteResult queryMoney(final Request request, final Response response, @RequestParam("format") String format) {
        final long start = System.currentTimeMillis();
        final String METHOD_NAME = "yxPingAnQueryMoney";
        try {
            final String yx = "pingan";
            final String content = new String(request.getContent());
            format = (StringUtils.isBlank(format) ? "json" : format);
            final String[] urls = ((HttpRequest)request).getUrl().split("\\?");
            final String url = urls[0];
            final YxValidation validation = new YxPingAnValidation(content, url);
            MultiResult toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.API_KEY.getName();
            toCheck.result2 = "gcld001";
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.USER_ID.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.TIMESTAMP.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SIGN.getName();
            validation.addCheck(toCheck);
            toCheck = new MultiResult();
            toCheck.result1 = ValidationEnum.SERVER_ID.getName();
            validation.addCheck(toCheck);
            final MultiResult result = validation.validateParameter();
            if (result == null) {
                this.errorOpReport(request, YxValidation.excResult, start, "query_money", false);
                return this.getResult(format, false, null);
            }
            if (!(boolean)result.result1) {
                this.errorOpReport(request, result, start, "query_money", false);
                return this.getResult(format, false, null);
            }
            final String key = PluginContext.configuration.getLoginKey(yx);
            final boolean sha1 = validation.validateSign(1, key);
            if (!sha1) {
                this.errorOpReport(request, YxPingAnOperationAction.signFail, start, "query_money", false);
                return this.getResult(format, false, null);
            }
            final String gatewayId = validation.getValueByName(ValidationEnum.SERVER_ID.getName());
            final String currentServerId = PluginContext.configuration.getServerId(yx);
            if (!currentServerId.equals(String.valueOf(gatewayId)) && !this.getMainServerId(gatewayId).equals(currentServerId)) {
                try {
                    final String redirectUrl = MessageFormatter.format(PluginContext.configuration.getTaobaoRedirectUrl(yx), new Object[] { this.getMainServerId(gatewayId), "yxPingAnQueryMoney" });
                    final String echo = WebUtils.sendRequest(redirectUrl, content);
                    YxPingAnOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("query_money", "query_money_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + gatewayId + "_REDIRECT_URL_" + redirectUrl + "_echo_" + echo, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(echo.getBytes()), 1));
                    return new ByteResult(echo.getBytes());
                }
                catch (Exception e) {
                    this.log.error(this, e);
                    this.errorOpReport(request, YxValidation.excResult, start, "query_money", false);
                    return this.getResult(format, false, null);
                }
            }
            final String customer = validation.getValueByName(ValidationEnum.USER_ID.getName());
            final PingAnMoneyInfo moneyInfo = this.yxOperation.queryMoneyInfo(customer, yx);
            return this.getResult(format, false, moneyInfo);
        }
        catch (Exception e2) {
            this.log.error(this, e2);
            this.errorOpReport(request, YxValidation.excResult, start, "query_money", false);
            return this.getResult(format, false, null);
        }
    }
    
    public static void main(final String[] args) {
        final List<PingAnPlayerInfo> playerInfo = new ArrayList<PingAnPlayerInfo>();
        final PingAnPlayerInfo info = new PingAnPlayerInfo();
        info.roleCoin = "1";
        info.roleLever = "2";
        info.roleName = "3";
        info.roleServer = "4";
        playerInfo.add(info);
        getListResult("xml", false, playerInfo);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        final String time = sdf.format(new Date());
        System.out.println(time);
        System.out.println("absfasfjlsdkgj".toUpperCase());
    }
    
    @Command("yxPingAnRequest")
    public ByteResult request(final Request request, final Response response, @RequestParam("format") String format, @RequestParam("c_id") final String cid, @RequestParam("uid") final String uid) {
        try {
            final long start = System.currentTimeMillis();
            final String METHOD_NAME = "yxPingAnLogin";
            final String yx = "pingan";
            format = ((format != null && format.equalsIgnoreCase("xml")) ? "xml" : "json");
            String redirectUrl = PluginContext.configuration.getPingAnUrl(yx, "yx.pingan.request.url");
            final String pattern = "http://(\\w+.)+.(com)|(cn)/";
            String value = redirectUrl.replaceAll(pattern, "");
            value = value.replace("/", "");
            value = value.replace("?", "");
            value = value.replace(".", "");
            final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date now = new Date();
            final String time1 = sdf1.format(now);
            final String time2 = sdf2.format(now);
            final StringBuffer sb = new StringBuffer();
            sb.append(PluginContext.configuration.getLoginKey(yx));
            sb.append(value);
            sb.append(ValidationEnum.API_KEY.getName()).append("gcld001");
            sb.append(ValidationEnum.RATE.getName()).append(10);
            sb.append(ValidationEnum.SERVER_ID.getName()).append(cid);
            sb.append(ValidationEnum.TIMESTAMP.getName()).append(time1);
            sb.append(ValidationEnum.USER_ID.getName()).append(uid);
            String sign = CodecUtil.sha1(sb.toString());
            sign = sign.toUpperCase();
            final StringBuffer contentBuffer = new StringBuffer();
            contentBuffer.append(ValidationEnum.API_KEY.getName()).append("=").append("gcld001").append("&");
            contentBuffer.append(ValidationEnum.RATE.getName()).append("=").append(10).append("&");
            contentBuffer.append(ValidationEnum.SERVER_ID.getName()).append("=").append(cid).append("&");
            contentBuffer.append(ValidationEnum.TIMESTAMP.getName()).append("=").append(time2).append("&");
            contentBuffer.append(ValidationEnum.USER_ID.getName()).append("=").append(uid).append("&");
            contentBuffer.append(ValidationEnum.SIGN.getName()).append("=").append(sign.toString());
            redirectUrl = String.valueOf(redirectUrl) + "?" + contentBuffer.toString();
            YxPingAnOperationAction.opReport.error("sb:" + sb.toString() + " sign:" + sign + " redirectUrl:" + redirectUrl);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Location", redirectUrl);
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            this.errorOpReport(request, YxValidation.sucResult, start, "yxPingAnLogin", true);
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            this.log.error(this, e);
            return this.getResult("json", false, null);
        }
    }
}
