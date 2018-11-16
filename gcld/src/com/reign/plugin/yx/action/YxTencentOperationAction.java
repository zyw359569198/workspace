package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.*;
import org.jboss.netty.handler.codec.http.*;
import java.util.regex.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.net.*;
import java.io.*;
import com.reign.util.codec.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;
import com.reign.plugin.yx.util.kingnet.demo.*;
import com.reign.plugin.yx.common.*;
import java.util.*;
import com.reign.util.*;
import com.reign.plugin.yx.util.json.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxTencentOperationAction
{
    @Autowired
    IYxOperation yxOperation;
    private static final Log opReport;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxTencentLogin")
    public ByteResult Login(@RequestParam("openid") final String openId, @RequestParam("openkey") final String openKey, @RequestParam("pf") final String pf, @RequestParam("pfkey") final String pfKey, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            final Map<String, String> map = new HashMap<String, String>();
            map.put("openid", openId);
            map.put("openkey", openKey);
            map.put("pf", pf);
            map.put("pfkey", pfKey);
            final boolean isEmpty = this.yxOperation.checkEmptyParams(map, request, "TencentLogin");
            if (isEmpty) {
                return YxHelper.txRedirectUnlogPage(1100, pf, request, response);
            }
            YxTencentOperationAction.opReport.error("#openid:" + openId + "#openkey:" + openKey + "#pf:" + pf + "#pfKey:" + pfKey);
            if (!this.yxOperation.checkTencentPf(pf)) {
                return YxHelper.txRedirectUnlogPage(1102, pf, request, response);
            }
            final Pattern openIdPattern = Pattern.compile("^([0-9A-F]{32})$");
            final Matcher openIdMatcher = openIdPattern.matcher(openId);
            final Pattern openKeyPattern = Pattern.compile("[0-9a-fA-F]*");
            final Matcher openKeyMatcher = openKeyPattern.matcher(openKey);
            if (!openIdMatcher.matches() || !openKeyMatcher.matches()) {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentLogin", "yxTencentLogin_fail_REGEX_CHECK_FAILED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1101)), 1101));
                return YxHelper.txRedirectUnlogPage(1101, pf, request, response);
            }
            final String baseUrl = PluginContext.configuration.getTencentYunUrl(pf);
            final String uri = PluginContext.configuration.getTencentUseInfoUri(pf);
            final String appId = PluginContext.configuration.getTencentAppId(pf);
            YxTencentOperationAction.opReport.error("#baseUrl:" + baseUrl + "#uri:" + uri + "#appid" + appId);
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("openid", openId);
            paramMap.put("openkey", openKey);
            paramMap.put("appid", appId);
            paramMap.put("pf", pf);
            final String echo = this.yxOperation.exeTencentQuery(pf, paramMap, baseUrl, uri, "POST", false, false);
            int yellowVipLevel = 0;
            final JSONObject jo = new JSONObject(echo);
            final int ret = (int)jo.get("ret");
            if (ret == 0) {
                final String userName = (String)jo.get("nickname");
                final int isYellowVip = (int)jo.get("is_yellow_vip");
                final int isYellowYearVip = (int)jo.get("is_yellow_year_vip");
                final int isYellowHighVip = (int)jo.get("is_yellow_high_vip");
                final YxTencentUserInfo ytuInfo = new YxTencentUserInfo();
                ytuInfo.setUserId(openId);
                ytuInfo.setOpenId(openId);
                ytuInfo.setOpenKey(openKey);
                ytuInfo.setUserName(userName);
                ytuInfo.setPf(pf);
                ytuInfo.setPfKey(pfKey);
                ytuInfo.setUserIp(YxHelper.getIp(request));
                ytuInfo.setIsYellowHighVip(isYellowHighVip);
                ytuInfo.setIsYellowVip(isYellowVip);
                ytuInfo.setIsYellowYearVip(isYellowYearVip);
                if (isYellowVip == 1) {
                    yellowVipLevel = (int)jo.get("yellow_vip_level");
                    ytuInfo.setYellowVipLevel(yellowVipLevel);
                }
                String via = "";
                final String[] tempArr1 = request.getParamterMap().get("app_cunstom");
                final String[] tempArr2 = request.getParamterMap().get("source");
                final String[] tempArr3 = request.getParamterMap().get("via");
                if (tempArr1 != null && tempArr1.length > 0) {
                    via = tempArr1[0];
                }
                else if (tempArr2 != null && tempArr2.length > 0) {
                    via = tempArr2[0];
                }
                else if (tempArr3 != null && tempArr3.length > 0) {
                    via = tempArr3[0];
                }
                YxTencentOperationAction.opReport.error("#via:" + via);
                final String yxSource = via;
                String mainUrl = PluginContext.configuration.getGameURL(pf);
                mainUrl = String.valueOf(mainUrl) + "?platform=" + pf + "&openid=" + openId;
                final Session session = this.yxOperation.loginForTencent(pf, "", yxSource, ytuInfo, request);
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
                response.addHeader("Location", mainUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentLogin", "yxTencentLogin_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                return YxHelper.getResult(1, request, response);
            }
            return YxHelper.txRedirectUnlogPage(6, pf, request, response);
        }
        catch (Exception e) {
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentLogin", "yxTencentLogin_fail_EXCEPTION" + e, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(14)), 14));
            return YxHelper.txRedirectUnlogPage(14, pf, request, response);
        }
    }
    
    @Command("yxTencentPay")
    public ByteResult pay(@RequestParam("openid") final String openId, @RequestParam("appid") final String appId, @RequestParam("ts") final String ts, @RequestParam("payitem") final String payItem, @RequestParam("token") final String token, @RequestParam("billno") final String billNo, @RequestParam("version") final String version, @RequestParam("zoneid") final String zoneId, @RequestParam("providetype") final String provideType, @RequestParam("amt") final String amt, @RequestParam("payamt_coins") final String payAmtCoins, @RequestParam("pubacct_payamt_coins") final String pubacctPayAmtCoins, @RequestParam("sig") final String sign, @RequestParam("kingnet_sign") final String kingnetSign, @RequestParam("addition") String addition, final Request request, final Response response) throws JSONException, UnsupportedEncodingException {
        final JSONObject resJson = new JSONObject();
        final long start = System.currentTimeMillis();
        final String[] tempArray = payItem.split("_");
        final int serverId = Integer.parseInt(tempArray[0]);
        final int curServerId = Integer.parseInt(PluginContext.configuration.getHostId("qzone"));
        YxTencentOperationAction.opReport.error("#serverId:" + serverId + "#currentServerId:" + curServerId);
        if (curServerId != serverId) {
            String requestURL = MessageFormatter.format(PluginContext.configuration.getTxRedirectUrl("qzone"), new Object[] { serverId });
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("openid", openId);
            paramMap.put("appid", appId);
            paramMap.put("ts", ts);
            paramMap.put("payitem", payItem);
            paramMap.put("token", token);
            paramMap.put("billno", billNo);
            paramMap.put("version", version);
            paramMap.put("zoneid", zoneId);
            paramMap.put("providetype", provideType);
            paramMap.put("amt", amt);
            paramMap.put("sig", sign);
            paramMap.put("kingnet_sign", kingnetSign);
            paramMap.put("addition", addition);
            if (payAmtCoins != null) {
                paramMap.put("payamt_coins", payAmtCoins);
            }
            if (pubacctPayAmtCoins != null) {
                paramMap.put("pubacct_payamt_coins", pubacctPayAmtCoins);
            }
            YxTencentOperationAction.opReport.error(requestURL);
            final String echo = WebUtils.sendGetRequest(requestURL, paramMap);
            requestURL = WebUtils.getURL(requestURL, paramMap);
            YxTencentOperationAction.opReport.error("#echo:" + echo);
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_success_SUCCESS_REDIRECT_GATE_WAY_ID_" + serverId + "_REDIRECT_URL_" + requestURL, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return new ByteResult(echo.getBytes());
        }
        try {
            addition = URLDecoder.decode(addition, "UTF-8");
        }
        catch (UnsupportedEncodingException e3) {
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_URLDECODE_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(4)), 4));
            return new ByteResult(String.valueOf(4).getBytes());
        }
        YxTencentOperationAction.opReport.error("#addition:" + addition);
        final String[] payArray = addition.split("\\|")[0].split("#");
        String payUnit = "";
        String packageId = "";
        if (payArray != null && payArray.length >= 2) {
            payUnit = payArray[0];
            packageId = payArray[1];
        }
        YxTencentOperationAction.opReport.error("#payUnit:" + payUnit);
        YxTencentOperationAction.opReport.error("#packageId:" + packageId);
        final Map<String, String> map = new HashMap<String, String>();
        map.put("openid", openId);
        map.put("appid", appId);
        map.put("ts", ts);
        map.put("payitem", payItem);
        map.put("token", token);
        map.put("billno", billNo);
        map.put("zoneid", zoneId);
        map.put("providetype", provideType);
        map.put("sig", sign);
        map.put("kingnet_sign", kingnetSign);
        final boolean isEmpty = this.yxOperation.checkEmptyParams(map, request, "TencentPay");
        if (isEmpty) {
            resJson.put("ret", 1100);
            resJson.put("msg", "\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_PARAM_IS_NULL", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1100)), 1100));
            return new ByteResult(resJson.toString().getBytes());
        }
        final Map<String, YxTencentPayInfo> tPayMap = this.yxOperation.getTencentPayMap();
        if (tPayMap == null) {
            resJson.put("ret", 3);
            resJson.put("msg", "TOKEN\u4e0d\u5b58\u5728");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_tPayMap_IS_NULL", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(3)), 3));
            return new ByteResult(resJson.toString().getBytes());
        }
        final YxTencentPayInfo yTencentPayInfo = tPayMap.get(token);
        if (yTencentPayInfo == null) {
            resJson.put("ret", 3);
            resJson.put("msg", "TOKEN\u4e0d\u5b58\u5728");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_TOKEN_NOT_FOUND", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(3)), 3));
            return new ByteResult(resJson.toString().getBytes());
        }
        final long now = System.currentTimeMillis() / 1000L;
        final long timestamp = Integer.parseInt(yTencentPayInfo.getTs());
        if ((now - timestamp) / 60L >= 15L) {
            resJson.put("ret", 2);
            resJson.put("msg", "TOKEN\u5df2\u8fc7\u671f");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_TOKEN_EXPIRED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(2)), 2));
            return new ByteResult(resJson.toString().getBytes());
        }
        final String openKey = yTencentPayInfo.getOpneKey();
        final String pf = yTencentPayInfo.getPf();
        final String appKey = PluginContext.configuration.getTencentAppKey(pf);
        if (!this.yxOperation.checkTencentPf(pf)) {
            resJson.put("ret", 1102);
            resJson.put("msg", "\u975e\u6cd5\u7684\u5e73\u53f0\u6765\u6e90");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_Invalid_platform", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1102)), 1102));
            return new ByteResult(resJson.toString().getBytes());
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(appId).append(openId).append(amt).append(billNo).append(sign).append(ts).append(appKey);
        final String myKingnetSign = CodecUtil.md5(sb.toString());
        if (!myKingnetSign.equalsIgnoreCase(kingnetSign)) {
            resJson.put("ret", 13);
            resJson.put("msg", "\u607a\u82f1\u7b7e\u540d\u9a8c\u8bc1\u5931\u8d25");
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_KINGNET_TOKEN_CHECK_FAILED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(13)), 13));
            return new ByteResult(resJson.toString().getBytes());
        }
        int price = 0;
        int num = 0;
        final String[] payItems = payItem.split("\\*");
        try {
            price = Integer.parseInt(payItems[1]);
            num = Integer.parseInt(payItems[2]);
        }
        catch (Exception e) {
            resJson.put("ret", 8);
            resJson.put("msg", "\u5145\u503c\u5f02\u5e38#payItem\u89e3\u6790\u5f02\u5e38\uff01#" + payItem);
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_PAYITEM_EXCEPTION#e:" + e, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(8)), 8));
            return new ByteResult(resJson.toString().getBytes());
        }
        final int gold = price * 10 * num;
        YxTencentOperationAction.opReport.info("#gold:" + gold + "#price:" + price + "#num:" + num);
        int ret = 0;
        String msg = "";
        try {
            final Tuple<Integer, Integer> result = this.yxOperation.pay(billNo, yTencentPayInfo.getPlayerId(), yTencentPayInfo.getUserId(), pf, gold, request);
            if (1 == result.left) {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                ret = 0;
                msg = "OK";
                resJson.put("ret", ret);
                resJson.put("msg", msg);
                try {
                    final LogUserInfo logInfo = new LogUserInfo();
                    logInfo.setGameTime(System.currentTimeMillis() / 1000L);
                    logInfo.setOuid(openId);
                    logInfo.setIuid(new StringBuilder(String.valueOf(yTencentPayInfo.getPlayerId())).toString());
                    logInfo.setUserLevel(yTencentPayInfo.getPlayerLv());
                    logInfo.setVipLevel(new StringBuilder(String.valueOf(yTencentPayInfo.getYellowVipLevel())).toString());
                    logInfo.setTimestamp(System.currentTimeMillis() / 1000L);
                    final UdpSender udpSender = new UdpSender();
                    udpSender.sendPayLog(logInfo, payUnit, gold, billNo, packageId);
                }
                catch (Exception e4) {
                    YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_UDP_SENT_FAILED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(8)), 8));
                }
            }
            else if (5 == result.left) {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_ORDERID_EXISTED", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(5)), 5));
                ret = 5;
                msg = "\u8ba2\u5355\u53f7\u91cd\u590d";
                resJson.put("ret", ret);
                resJson.put("msg", msg);
            }
            else if (2 == result.left) {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_NO_SUCH_ROLE", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
                ret = 6;
                msg = "\u89d2\u8272\u4e0d\u5b58\u5728";
                resJson.put("ret", ret);
                resJson.put("msg", msg);
            }
            else if (3 == result.left) {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_INVALID_USER", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(7)), 7));
                ret = 7;
                msg = "\u975e\u6cd5\u7528\u6237";
                resJson.put("ret", ret);
                resJson.put("msg", msg);
            }
            else {
                YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(result.left)), result.left));
                ret = 9;
                msg = "\u672a\u77e5\u9519\u8bef";
                resJson.put("ret", ret);
                resJson.put("msg", msg);
            }
        }
        catch (Exception e2) {
            YxTencentOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay", "yxTencentPay_fail_EXCEPTION::" + e2, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(8)), 8));
            ret = 8;
            msg = "\u5145\u503c\u5f02\u5e38";
            resJson.put("ret", ret);
            resJson.put("msg", msg);
        }
        YxTencentOperationAction.opReport.info("#ret:" + ret + "#msg:" + msg);
        final YxTencentTimerTask yxTencentTimerTask = new YxTencentTimerTask(openId, appId, payItem, token, billNo, version, zoneId, provideType, amt, payAmtCoins, pubacctPayAmtCoins, openKey, pf, new StringBuilder(String.valueOf(ret)).toString(), msg, this.yxOperation);
        final Timer timer = new Timer();
        timer.schedule(yxTencentTimerTask, 10000L);
        YxTencentOperationAction.opReport.info("#sendConfirmDeliveryMsg has been set up...#Time:" + System.currentTimeMillis());
        return new ByteResult(resJson.toString().getBytes());
    }
}
