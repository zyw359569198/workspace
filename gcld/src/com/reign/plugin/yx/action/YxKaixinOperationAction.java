package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import java.util.concurrent.atomic.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.plugin.yx.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import org.apache.commons.lang.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.common.*;
import java.util.*;
import com.reign.util.codec.*;
import com.reign.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false"), @View(name = "freemarker", type = FreeMarkerView.class, compress = "false") })
public class YxKaixinOperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log logger;
    private static final String KAIXIN001 = "kaixin001";
    AtomicInteger atomicInteger;
    private static final int AMOUNT_GOLD_RATE = 10;
    private static final int QUERY_ACCOUNT_FAIL = 0;
    public static final String T_KAIXIN001_PAY_TIPS_1 = "\u60a8\u672a\u767b\u5f55\u6e38\u620f\uff0c\u4e0d\u80fd\u5145\u503c\uff0c\u8bf7\u8fd4\u56de\u767b\u5f55\u9875\u9762\u91cd\u65b0\u767b\u5f55\u6e38\u620f";
    public static final String T_KAIXIN001_PAY_TIPS_2 = "\u8bf7\u8f93\u5165\u6b63\u786e\u7684\u91d1\u989d\uff0c\u53ea\u80fd\u6574\u989d\u5145\u503c";
    public static final String T_KAIXIN001_PAY_TIPS_3 = "\u5145\u503c\u6210\u529f";
    public static final String T_KAIXIN001_PAY_TIPS_4 = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
    public static final String T_KAIXIN001_PAY_TIPS_5 = "\u5145\u503c\u670d\u52a1\u5668\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
    public static final String T_KAIXIN001_PAY_TIPS_6 = "\u751f\u6210\u8ba2\u5355\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5\uff01\u6216\u6b64\u8ba2\u5355\u5df2\u7ecf\u5b8c\u6210\uff0c\u8bf7\u52ff\u91cd\u590d\u63d0\u4ea4\uff01";
    public static final String T_KAIXIN001_PAY_TIPS_7 = "\u4f59\u989d\u4e0d\u8db3\uff0c\u6216\u6263\u9664\u5931\u8d25";
    public static final String T_KAIXIN001_PAY_TIPS_8 = "\u8d26\u53f7\u5df2\u8fc7\u671f\uff0c\u8bf7\u4ece\u5f00\u5fc3\u7f51\u91cd\u65b0\u8fdb\u5165\u6e38\u620f";
    public static final String T_KAIXIN001_PAY_TIPS_9 = "\u5145\u503c\u91d1\u989d\u586b\u5199\u9519\u8bef";
    public static final String T_KAIXIN001_PAY_TIPS_10 = "\u65f6\u95f4\u6233\u5df2\u7ecf\u8fc7\u671f";
    public static final String T_KAIXIN001_PAY_TIPS_11 = "\u9519\u8bef\u7684Verify";
    public static final String T_KAIXIN001_PAY_TIPS_12 = "\u9519\u8bef\u7684uid";
    public static final String T_KAIXIN001_PAY_TIPS_13 = "\u6765\u6e90ip\u672a\u6388\u6743";
    public static final String T_KAIXIN001_PAY_TIPS_14 = "\u7ec4\u4ef6\u672a\u6388\u6743\u8c03\u7528\u6b64\u63a5\u53e3";
    public static final String T_KAIXIN001_PAY_TIPS_15 = "\u6570\u5b57\u7b7e\u540d\u5931\u8d25";
    public static final String T_KAIXIN001_PAY_TIPS_16 = "\u53c2\u6570\u9519\u8bef";
    public static final String T_KAIXIN001_PAY_TIPS_17 = "\u91cd\u590d\u8bf7\u6c42\u65f6\u6570\u636e\u4e0d\u7b26";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        logger = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    public YxKaixinOperationAction() {
        this.atomicInteger = new AtomicInteger(1);
    }
    
    @Command("yxKaixin001Login")
    public ByteResult login(@RequestParam("verify") final String verify, @RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        try {
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("verify", verify);
            YxKaixinOperationAction.logger.info("verify:" + verify);
            try {
                final String echo = WebUtils.sendRequest(PluginContext.configuration.getKaixin001LoginUrl("kaixin001"), paramMap);
                final String uid = this.getJsonValue(echo, "v_uid");
                final String userName = this.getJsonValue(echo, "v_real_name");
                final String idchecked = this.getJsonValue(echo, "v_idchecked");
                final String agechecked = this.getJsonValue(echo, "v_agechecked");
                if (uid == null) {
                    YxKaixinOperationAction.logger.info("pimp:" + echo);
                    return YxHelper.redirectUnlogPage(1001, request, response);
                }
                final String userId = uid;
                String adult = "1";
                if ("0".equals(idchecked) || "0".equals(agechecked)) {
                    adult = "0";
                }
                final Session session = this.yxOperation.login("kaixin001", userId, userName, "", adult, yxSource, request);
                session.setAttribute("kaixin001_verify", verify);
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
                response.addHeader("Location", PluginContext.configuration.getKaixin001GameUrl("kaixin001"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxKaixinOperationAction.logger.info(MessageFormatter.format("login success:yx={0},userId={1},sfId={2},adult={3}", new Object[] { "kaixin001", userId, "", adult }));
                return YxHelper.getResult(1, request, response);
            }
            catch (Exception e) {
                YxKaixinOperationAction.logger.error("send kaixin login query fail", e);
                return YxHelper.redirectUnlogPage(1001, request, response);
            }
        }
        catch (Exception e2) {
            YxKaixinOperationAction.logger.error("unknow excepted exception:", e2);
            return YxHelper.redirectUnlogPage(1001, request, response);
        }
    }
    
    @Command("yxKaixin001QueryAccount")
    public Object queryAccount(@RequestParam("tips") String tips, final Request request, final Response response) {
        if (StringUtils.isBlank(tips)) {
            tips = "";
        }
        final YxPlayerInfo player = this.yxOperation.getYxPlayerInfo(request);
        if (player == null) {
            return new ByteResult(String.valueOf(0).getBytes());
        }
        final YxUserInfo uDto = this.yxOperation.getYxUserInfo(request);
        String userName = "";
        if (uDto != null) {
            userName = this.getUnicodeString(uDto.getUserName());
        }
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("verify", request.getSession().getAttribute("kaixin001_verify"));
        try {
            YxKaixinOperationAction.logger.info("verify:" + request.getSession().getAttribute("kaixin001_verify"));
            YxKaixinOperationAction.logger.info("url:" + PluginContext.configuration.getKaixin001QueryUrl("kaixin001"));
            final String echo = WebUtils.sendRequest(PluginContext.configuration.getKaixin001QueryUrl("kaixin001"), paramMap);
            YxKaixinOperationAction.logger.info("echo:" + echo);
            final String balance = this.getJsonValue(echo, "balance");
            final FreeMarkerResult rtn = new FreeMarkerResult("payKaixin.ftl");
            rtn.put("money", balance);
            rtn.put("userId", userName);
            rtn.put("tips", tips);
            rtn.put("payUrl", String.valueOf(PluginContext.configuration.getKaixin001PayUrl("kaixin001")) + "?from=" + PluginContext.configuration.getKaixin001GameAid("kaixin001") + "&vuid=" + uDto.getUserId());
            rtn.put("serverName", String.valueOf(PluginContext.configuration.getServerName("kaixin001")) + PluginContext.configuration.getServerId("kaixin001") + "\u670d");
            rtn.put("jumpUrl", PluginContext.configuration.getKaixin001PayHtml("kaixin001"));
            return rtn;
        }
        catch (Exception e) {
            YxKaixinOperationAction.logger.info("kaixin verify is over date, try again with out verify", e);
            paramMap.remove("verify");
            paramMap.clear();
            paramMap.put("ver", "1");
            paramMap.put("uid", uDto.getUserId());
            paramMap.put("aid", PluginContext.configuration.getKaixin001GameAid("kaixin001"));
            final Date nowDate = new Date();
            final String ts = String.valueOf(nowDate.getTime() / 1000L);
            paramMap.put("ts", ts);
            paramMap.put("sign", this.getMD5TicketForKaixinNotVerify(paramMap));
            YxKaixinOperationAction.logger.info("start send again.");
            try {
                final String echo2 = WebUtils.sendRequest(PluginContext.configuration.getKaixin001QueryUrl("kaixin001"), paramMap);
                YxKaixinOperationAction.logger.info("echo again:" + echo2);
                final String balance2 = this.getJsonValue(echo2, "balance");
                final FreeMarkerResult rtn2 = new FreeMarkerResult("payKaixin.ftl");
                rtn2.put("money", balance2);
                rtn2.put("userId", userName);
                rtn2.put("tips", tips);
                rtn2.put("payUrl", String.valueOf(PluginContext.configuration.getKaixin001PayUrl("kaixin001")) + "?from=" + PluginContext.configuration.getKaixin001GameAid("kaixin001") + "&vuid=" + uDto.getUserId());
                rtn2.put("serverName", String.valueOf(PluginContext.configuration.getServerName("kaixin001")) + PluginContext.configuration.getServerId("kaixin001") + "\u670d");
                rtn2.put("jumpUrl", PluginContext.configuration.getKaixin001PayHtml("kaixin001"));
                return rtn2;
            }
            catch (Exception ee) {
                YxKaixinOperationAction.logger.error("send KAIXIN001 query account fail", ee);
                return YxHelper.getResult(0, request, response);
            }
        }
    }
    
    @Command("yxKaixin001Pay")
    public Object pay(@RequestParam("money") final int money, final Request request, final Response response) {
        final String ip = YxHelper.getIp(request);
        String tips = "";
        final YxUserInfo userDto = this.yxOperation.getYxUserInfo(request);
        if (userDto == null || request.getSession().getAttribute("kaixin001_verify") == null) {
            tips = "\u60a8\u672a\u767b\u5f55\u6e38\u620f\uff0c\u4e0d\u80fd\u5145\u503c\uff0c\u8bf7\u8fd4\u56de\u767b\u5f55\u9875\u9762\u91cd\u65b0\u767b\u5f55\u6e38\u620f";
            return new ByteResult(String.valueOf(0).getBytes());
        }
        if (money <= 0) {
            tips = "\u5145\u503c\u91d1\u989d\u586b\u5199\u9519\u8bef";
            YxKaixinOperationAction.logger.error("wrong gold: " + money);
            return this.queryAccount(tips, request, response);
        }
        final int playerId = this.yxOperation.getDefaultPayPlayer(userDto.getUserId(), "kaixin001");
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        final Date nowDate = new Date();
        final String orderId = this.createOrderId(userDto.getUserId());
        paramMap.put("orderid", orderId);
        paramMap.put("amount", String.valueOf(money));
        paramMap.put("vendor", PluginContext.configuration.getKaixin001GameVendor("kaixin001"));
        paramMap.put("appname", PluginContext.configuration.getKaixin001GameAppName("kaixin001"));
        paramMap.put("goods", PluginContext.configuration.getKaixin001GameGoods("kaixin001"));
        paramMap.put("num", String.valueOf(money * 10));
        paramMap.put("ip", ip);
        paramMap.put("verify", request.getSession().getAttribute("kaixin001_verify"));
        paramMap.put("area", PluginContext.configuration.getServerId("kaixin001"));
        paramMap.put("sign", this.getMD5TicketForKaixin(paramMap));
        paramMap.put("_debug", "1");
        if (YxKaixinOperationAction.logger.isDebugEnabled()) {
            for (final String s : paramMap.keySet()) {
                YxKaixinOperationAction.logger.debug(String.valueOf(s) + ":" + paramMap.get(s));
            }
        }
        try {
            final String echo = WebUtils.sendRequest(PluginContext.configuration.getKaixin001PayServiceUrl("kaixin001"), paramMap);
            if (YxKaixinOperationAction.logger.isDebugEnabled()) {
                YxKaixinOperationAction.logger.debug("echo:" + echo);
            }
            final String resultStr = this.getJsonValue(echo, "result");
            final String sign = this.getJsonValue(echo, "sign");
            final String _orderId = this.getJsonValue(echo, "orderid");
            if (resultStr != null) {
                final int result = Integer.parseInt(resultStr.trim());
                if (1 != result) {
                    tips = "\u4f59\u989d\u4e0d\u8db3\uff0c\u6216\u6263\u9664\u5931\u8d25";
                    YxKaixinOperationAction.logger.error("receive kaixin result: " + resultStr);
                }
                else if (!sign.equals(this.getMD5TicketForKaixinResult(echo, false)) && !sign.equals(this.getMD5TicketForKaixinResult(echo, true))) {
                    tips = "\u5145\u503c\u670d\u52a1\u5668\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
                    YxKaixinOperationAction.logger.error("wrong kaixin sign");
                }
                else if (!paramMap.get("orderid").equals(_orderId)) {
                    tips = "\u5145\u503c\u670d\u52a1\u5668\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
                    YxKaixinOperationAction.logger.error("wrong kaixin orderid");
                }
                else {
                    final Tuple<Integer, Integer> ret = this.yxOperation.pay(_orderId, playerId, userDto.getUserId(), "kaixin001", money * 10, request);
                    if (1 == ret.left) {
                        tips = "\u5145\u503c\u6210\u529f";
                        YxKaixinOperationAction.logger.info(MessageFormatter.format("pay success:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "kaixin001", userDto.getUserId(), _orderId, playerId, money }));
                    }
                    else {
                        YxKaixinOperationAction.logger.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "kaixin001", userDto.getUserId(), _orderId, playerId, money, ret.left }));
                        tips = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
                    }
                }
            }
            else {
                final String errorNo = this.getJsonValue(echo, "errno");
                if (errorNo.equals("1")) {
                    tips = "\u65f6\u95f4\u6233\u5df2\u7ecf\u8fc7\u671f";
                }
                else if (errorNo.equals("2")) {
                    tips = "\u9519\u8bef\u7684Verify";
                }
                else if (errorNo.equals("3")) {
                    tips = "\u9519\u8bef\u7684uid";
                }
                else if (errorNo.equals("4")) {
                    tips = "\u6765\u6e90ip\u672a\u6388\u6743";
                }
                else if (errorNo.equals("5")) {
                    tips = "\u7ec4\u4ef6\u672a\u6388\u6743\u8c03\u7528\u6b64\u63a5\u53e3";
                }
                else if (errorNo.equals("-1")) {
                    tips = "\u6570\u5b57\u7b7e\u540d\u5931\u8d25";
                }
                else if (errorNo.equals("-2")) {
                    tips = "\u53c2\u6570\u9519\u8bef";
                }
                else if (errorNo.equals("-3")) {
                    tips = "\u91cd\u590d\u8bf7\u6c42\u65f6\u6570\u636e\u4e0d\u7b26";
                }
                else {
                    tips = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
                }
            }
        }
        catch (Exception e) {
            YxKaixinOperationAction.logger.info("kaixin verify is over date, try again with out verify", e);
            paramMap.remove("verify");
            paramMap.remove("sign");
            paramMap.put("ver", "1");
            paramMap.put("uid", userDto.getUserId());
            paramMap.put("aid", PluginContext.configuration.getKaixin001GameAid("kaixin001"));
            final String ts = String.valueOf(nowDate.getTime() / 1000L);
            paramMap.put("ts", ts);
            paramMap.put("sign", this.getMD5TicketForKaixinNotVerifyPay(paramMap));
            paramMap.put("_debug", "1");
            try {
                final String echo2 = WebUtils.sendRequest(PluginContext.configuration.getKaixin001PayServiceUrl("kaixin001"), paramMap);
                if (YxKaixinOperationAction.logger.isDebugEnabled()) {
                    YxKaixinOperationAction.logger.debug("echo:" + echo2);
                }
                final String resultStr2 = this.getJsonValue(echo2, "result");
                final String sign2 = this.getJsonValue(echo2, "sign");
                final String _orderId2 = this.getJsonValue(echo2, "orderId");
                if (resultStr2 != null) {
                    final int result2 = Integer.parseInt(resultStr2.trim());
                    if (1 != result2) {
                        tips = "\u4f59\u989d\u4e0d\u8db3\uff0c\u6216\u6263\u9664\u5931\u8d25";
                        YxKaixinOperationAction.logger.error("receive kaixin result: " + resultStr2);
                        return this.queryAccount(tips, request, response);
                    }
                    if (!sign2.equals(this.getMD5TicketForKaixinResult(echo2, false)) && !sign2.equals(this.getMD5TicketForKaixinResult(echo2, true))) {
                        tips = "\u5145\u503c\u670d\u52a1\u5668\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
                        YxKaixinOperationAction.logger.error("wrong kaixin sign");
                        return this.queryAccount(tips, request, response);
                    }
                    if (!paramMap.get("orderid").equals(_orderId2)) {
                        tips = "\u5145\u503c\u670d\u52a1\u5668\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
                        YxKaixinOperationAction.logger.error("wrong kaixin orderid");
                        return this.queryAccount(tips, request, response);
                    }
                    final Tuple<Integer, Integer> ret2 = this.yxOperation.pay(_orderId2, playerId, userDto.getUserId(), "kaixin001", money * 10, request);
                    if (1 == ret2.left) {
                        tips = "\u5145\u503c\u6210\u529f";
                        YxKaixinOperationAction.logger.info(MessageFormatter.format("pay success:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "kaixin001", userDto.getUserId(), _orderId2, playerId, money }));
                        return this.queryAccount(tips, request, response);
                    }
                    YxKaixinOperationAction.logger.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "kaixin001", userDto.getUserId(), _orderId2, playerId, money, ret2.left }));
                    tips = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
                    return this.queryAccount(tips, request, response);
                }
                else {
                    final String errorNo2 = this.getJsonValue(echo2, "errno");
                    if (errorNo2.equals("1")) {
                        tips = "\u65f6\u95f4\u6233\u5df2\u7ecf\u8fc7\u671f";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("2")) {
                        tips = "\u9519\u8bef\u7684Verify";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("3")) {
                        tips = "\u9519\u8bef\u7684uid";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("4")) {
                        tips = "\u6765\u6e90ip\u672a\u6388\u6743";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("5")) {
                        tips = "\u7ec4\u4ef6\u672a\u6388\u6743\u8c03\u7528\u6b64\u63a5\u53e3";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("-1")) {
                        tips = "\u6570\u5b57\u7b7e\u540d\u5931\u8d25";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("-2")) {
                        tips = "\u53c2\u6570\u9519\u8bef";
                        return this.queryAccount(tips, request, response);
                    }
                    if (errorNo2.equals("-3")) {
                        tips = "\u91cd\u590d\u8bf7\u6c42\u65f6\u6570\u636e\u4e0d\u7b26";
                        return this.queryAccount(tips, request, response);
                    }
                    tips = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
                    return this.queryAccount(tips, request, response);
                }
            }
            catch (Exception ee) {
                tips = "\u672a\u77e5\u5f02\u5e38\uff0c\u5145\u503c\u5931\u8d25";
                YxKaixinOperationAction.logger.error("send kaixin pay fail", ee);
                return this.queryAccount(tips, request, response);
            }
        }
        return this.queryAccount(tips, request, response);
    }
    
    @Command("yxKaixin001CheckUser")
    public ByteResult queryUser(@RequestParam("verify") final String verify, final Request request, final Response response) {
        try {
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("verify", verify);
            try {
                final String echo = WebUtils.sendRequest(PluginContext.configuration.getKaixin001LoginUrl("kaixin001"), paramMap);
                final String uid = this.getJsonValue(echo, "v_uid");
                if (uid == null) {
                    return YxHelper.redirectUnlogPage(1001, request, response);
                }
                final List<YxPlayerInfo> list = this.yxOperation.queryPlayer(uid, "kaixin001");
                if (list != null && list.size() > 0) {
                    return new ByteResult(String.valueOf(1).getBytes());
                }
                return new ByteResult(String.valueOf(0).getBytes());
            }
            catch (Exception e) {
                return YxHelper.redirectUnlogPage(1001, request, response);
            }
        }
        catch (Exception e2) {
            return YxHelper.redirectUnlogPage(1001, request, response);
        }
    }
    
    private String getJsonValue(final String jsonStr, final String str) {
        if (jsonStr.indexOf(str) == -1) {
            return null;
        }
        int idx = jsonStr.indexOf("\"" + str + "\"");
        final int idx2 = jsonStr.indexOf(34, idx + str.length() + 2);
        final int _idx = jsonStr.indexOf(58, idx + str.length() + 2);
        int _idx2 = jsonStr.indexOf(44, idx + str.length() + 2);
        if (_idx2 == -1) {
            _idx2 = jsonStr.indexOf(125, idx + str.length() + 2);
        }
        idx = jsonStr.indexOf(34, idx2 + 1);
        if (idx2 == -1 || _idx2 < idx2) {
            return jsonStr.substring(_idx + 1, _idx2);
        }
        return jsonStr.substring(idx2 + 1, idx);
    }
    
    private String getUnicodeString(String value) {
        value = value.replaceAll("\\\\u", ";");
        final StringBuilder builder = new StringBuilder();
        boolean start = false;
        final StringBuilder temp = new StringBuilder(4);
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            switch (c) {
                case ';': {
                    start = true;
                    break;
                }
                default: {
                    if (!start) {
                        builder.append(c);
                        break;
                    }
                    temp.append(c);
                    if (temp.length() == 4) {
                        final int j = Integer.valueOf(temp.toString().substring(0, 4), 16);
                        builder.append((char)j);
                        temp.setLength(0);
                        start = false;
                        break;
                    }
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    private String getMD5TicketForKaixinNotVerify(final Map<String, Object> m) {
        final StringBuilder sb = new StringBuilder();
        sb.append("aid=");
        sb.append(m.get("aid"));
        sb.append("&ts=");
        sb.append(m.get("ts"));
        sb.append("&uid=");
        sb.append(m.get("uid"));
        sb.append("&ver=");
        sb.append(m.get("ver"));
        sb.append("&");
        sb.append(PluginContext.configuration.getKaixin001Secret("kaixin001"));
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForKaixin(final Map<String, Object> m) {
        final StringBuilder sb = new StringBuilder();
        sb.append("amount=");
        sb.append(m.get("amount"));
        sb.append("&appname=");
        sb.append(m.get("appname"));
        sb.append("&area=");
        sb.append(m.get("area"));
        sb.append("&goods=");
        sb.append(m.get("goods"));
        sb.append("&ip=");
        sb.append(m.get("ip"));
        sb.append("&num=");
        sb.append(m.get("num"));
        sb.append("&orderid=");
        sb.append(m.get("orderid"));
        sb.append("&vendor=");
        sb.append(m.get("vendor"));
        sb.append("&verify=");
        sb.append(m.get("verify"));
        sb.append("&");
        sb.append(PluginContext.configuration.getKaixin001Secret("kaixin001"));
        YxKaixinOperationAction.logger.info("send string for sign:" + sb.toString());
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForKaixinResult(final String echo, final boolean isTest) {
        final StringBuilder sb = new StringBuilder();
        sb.append("aid=");
        sb.append(this.getJsonValue(echo, "aid"));
        sb.append("&amount=");
        sb.append(this.getJsonValue(echo, "amount"));
        sb.append("&kts=");
        sb.append(this.getJsonValue(echo, "kts"));
        sb.append("&orderid=");
        sb.append(this.getJsonValue(echo, "orderid"));
        sb.append("&pid=");
        sb.append(this.getJsonValue(echo, "pid"));
        sb.append("&result=");
        sb.append(this.getJsonValue(echo, "result"));
        if (isTest) {
            sb.append("&test=");
            sb.append(this.getJsonValue(echo, "test"));
        }
        sb.append("&ts=");
        sb.append(this.getJsonValue(echo, "ts"));
        sb.append("&uid=");
        sb.append(this.getJsonValue(echo, "uid"));
        sb.append("&");
        sb.append(PluginContext.configuration.getKaixin001Secret("kaixin001"));
        YxKaixinOperationAction.logger.info("test receive string for sign:" + sb.toString());
        YxKaixinOperationAction.logger.info("test sign:" + CodecUtil.md5(sb.toString()));
        return CodecUtil.md5(sb.toString());
    }
    
    private String getMD5TicketForKaixinNotVerifyPay(final Map<String, Object> m) {
        final StringBuilder sb = new StringBuilder();
        sb.append("aid=");
        sb.append(m.get("aid"));
        sb.append("&amount=");
        sb.append(m.get("amount"));
        sb.append("&appname=");
        sb.append(m.get("appname"));
        sb.append("&goods=");
        sb.append(m.get("goods"));
        sb.append("&ip=");
        sb.append(m.get("ip"));
        sb.append("&num=");
        sb.append(m.get("num"));
        sb.append("&orderid=");
        sb.append(m.get("orderid"));
        sb.append("&ts=");
        sb.append(m.get("ts"));
        sb.append("&uid=");
        sb.append(m.get("uid"));
        sb.append("&vendor=");
        sb.append(m.get("vendor"));
        sb.append("&ver=");
        sb.append(m.get("ver"));
        sb.append("&");
        sb.append(PluginContext.configuration.getKaixin001Secret("kaixin001"));
        YxKaixinOperationAction.logger.info("send string for sign:" + sb.toString());
        return CodecUtil.md5(sb.toString());
    }
    
    private synchronized String createOrderId(final String userId) {
        final int seq = this.atomicInteger.getAndIncrement();
        if (seq == 99) {
            this.atomicInteger.set(1);
        }
        return MessageFormatter.format("{0}{1}{2}{3}{4}", new Object[] { "k", PluginContext.configuration.getServerId("kaixin001"), userId, DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"), StringUtils.leftPad(String.valueOf(seq), 2, '0') });
    }
}
