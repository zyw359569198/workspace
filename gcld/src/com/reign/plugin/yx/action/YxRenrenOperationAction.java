package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.plugin.yx.*;
import com.reign.util.codec.*;
import org.apache.commons.lang.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.common.*;
import com.reign.util.*;
import java.net.*;
import java.io.*;
import java.util.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false"), @View(name = "freemarker", type = FreeMarkerView.class, compress = "false") })
public class YxRenrenOperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log logger;
    private static Long lastOrderId;
    private static final String RENREN = "renren";
    private static final int QUERY_ACCOUNT_FAIL = 0;
    private static final int AMOUNT_GOLD_RATE = 10;
    private static final int PLAYERNAME_NOT_EXSIT = 0;
    private static final int PLAYERNAME_EXSIT = 1;
    public static final String T_RENREN_PAY_TIPS_0 = "\u5145\u503c\u6210\u529f";
    public static final String T_RENREN_PAY_TIPS_1 = "\u6d88\u8d39\u53c2\u6570\u4e3a\u8d1f";
    public static final String T_RENREN_PAY_TIPS_2 = "\u4f59\u989d\u4e0d\u8db3";
    public static final String T_RENREN_PAY_TIPS_3 = "\u7cfb\u7edf\u5f02\u5e38";
    public static final String T_RENREN_PAY_TIPS_4 = "\u65e0\u64cd\u4f5c\u6743\u9650";
    public static final String T_RENREN_PAY_TIPS_5 = "\u5bf9\u5e94\u7684\u8fdc\u7a0b\u8ba2\u5355\u53f7\u5df2\u88ab\u6d88\u8d39";
    public static final String T_RENREN_PAY_TIPS_6 = "\u6e38\u620f\u5145\u503c\u5931\u8d25";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        logger = LogFactory.getLog("com.reign.gcld.opreport");
        YxRenrenOperationAction.lastOrderId = 0L;
    }
    
    @Command("yxRenrenLogin")
    public ByteResult login(@RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        try {
            String cookieValue = request.getCookieValue("t");
            cookieValue = ((cookieValue == null) ? "" : cookieValue);
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("s", cookieValue);
            paramMap.put("trace", PluginContext.configuration.getServerId("renren"));
            paramMap.put("code", PluginContext.configuration.getRenrenCode("renren"));
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getRenrenCode("renren"));
            sb.append(PluginContext.configuration.getServerId("renren"));
            sb.append(cookieValue);
            sb.append(PluginContext.configuration.getRenrenSecret("renren"));
            final String md5Value = CodecUtil.md5(sb.toString()).toLowerCase();
            paramMap.put("key", md5Value);
            paramMap.put("flag", "1");
            try {
                final String loginLimit = PluginContext.configuration.getRenrenLoginLimit("renren");
                if (StringUtils.isNotBlank(loginLimit) && "1".equals(loginLimit.trim())) {
                    YxRenrenOperationAction.logger.info("login.action, GM LIMIT LOGIN, loginLimit:" + loginLimit);
                    return YxHelper.redirectUnlogPage(1001, request, response);
                }
                final String echo = WebUtils.sendGetRequest(PluginContext.configuration.getRenrenLoginUrl("renren"), paramMap);
                YxRenrenOperationAction.logger.info("login.action, echo:" + echo);
                if (echo.equals("0")) {
                    return YxHelper.redirectUnlogPage(1001, request, response);
                }
                final String userId = echo;
                final Session session = this.yxOperation.login("renren", userId, userId, "", "1", yxSource, request);
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
                response.addHeader("Location", PluginContext.configuration.getGameURL("renren"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                YxRenrenOperationAction.logger.info(MessageFormatter.format("login success:yx={0},userId={1},sfId={2},adult={3}", new Object[] { "renren", userId, "", "1" }));
                return YxHelper.getResult(1, request, response);
            }
            catch (Exception e) {
                YxRenrenOperationAction.logger.error("send renren login query fail", e);
                return YxHelper.redirectUnlogPage(1001, request, response);
            }
        }
        catch (Exception e2) {
            YxRenrenOperationAction.logger.error("unknow excepted exception:", e2);
            return YxHelper.redirectUnlogPage(1001, request, response);
        }
    }
    
    @Command("yxRenrenQueryAccount")
    public FreeMarkerResult queryAccount(@RequestParam("tips") final String tips, final Request request, final Response response) {
        final YxPlayerInfo playerDto = this.yxOperation.getDefaultPayPlayer(request);
        if (playerDto == null) {
            final FreeMarkerResult rtn = new FreeMarkerResult("errorInfo.ftl");
            rtn.put("errorInfo", 0);
            return rtn;
        }
        final FreeMarkerResult rtn = new FreeMarkerResult("pay.ftl");
        rtn.put("userId", playerDto.getUserId());
        rtn.put("playerName", playerDto.getPlayerName());
        rtn.put("serverName", String.valueOf(PluginContext.configuration.getServerName("renren")) + PluginContext.configuration.getServerId("renren") + "\u670d");
        rtn.put("payUrl", PluginContext.configuration.getRenrenPayUrl("renren"));
        rtn.put("jumpUrl", PluginContext.configuration.getRenrenPayStartUrl("renren"));
        return rtn;
    }
    
    @Command("yxRenrenStartPay")
    public ByteResult yxRenrenStartPay(@RequestParam("bean") final int bean, final Request request, final Response response) {
        if (bean < 1 || bean > 99999) {
            return YxHelper.redirectUnlogPage(4, request, response);
        }
        final YxPlayerInfo playerDto = this.yxOperation.getDefaultPayPlayer(request);
        if (playerDto == null) {
            return YxHelper.redirectUnlogPage(903, request, response);
        }
        final String expend_code = PluginContext.configuration.getRenrenExpendCode("renren");
        final String expend_trace = PluginContext.configuration.getRenrenExpendTrace("renren");
        final String notify_url = PluginContext.configuration.getRenrenNotifyUrl("renren");
        final long out_trade_no = getOrderId(System.currentTimeMillis() / 1000L);
        final int quantity = bean * 10;
        final String subject = "\u91d1\u5e01";
        final String uid = playerDto.getUserId();
        final StringBuilder sb = new StringBuilder();
        sb.append("expend_code=").append(expend_code).append("&");
        sb.append("expend_trace=").append(expend_trace).append("&");
        sb.append("notify_url=").append(notify_url).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        sb.append("quantity=").append(quantity).append("&");
        sb.append("rrd=").append(bean).append("&");
        sb.append("subject=").append(subject).append("&");
        sb.append("uid=").append(uid);
        sb.append(PluginContext.configuration.getRenrenSecret("renren"));
        final String sign = CodecUtil.md5(sb.toString()).toLowerCase();
        YxRenrenOperationAction.logger.info("yxRenrenStartPay sign=" + sign + ",param=" + sb.toString());
        final Map<String, String> param = new HashMap<String, String>();
        param.put("expend_code", expend_code);
        param.put("expend_trace", expend_trace);
        param.put("notify_url", notify_url);
        param.put("out_trade_no", new StringBuilder().append(out_trade_no).toString());
        param.put("quantity", new StringBuilder().append(quantity).toString());
        param.put("rrd", new StringBuilder().append(bean).toString());
        param.put("subject", subject);
        param.put("uid", uid);
        param.put("sign", sign);
        this.redirectPayPage(request, response, param);
        return new ByteResult("1".getBytes());
    }
    
    @Command("yxRenrenPay")
    public ByteResult pay(@RequestParam("sign") final String sign, @RequestParam("trade_finish_time") final String trade_finish_time, @RequestParam("notify_id") final String notify_id, @RequestParam("trade_no") final String trade_no, @RequestParam("out_trade_no") final String out_trade_no, @RequestParam("rrd") final int rrd, @RequestParam("sign_type") final String sign_type, @RequestParam("trade_status") final String trade_status, @RequestParam("subject") final String subject, @RequestParam("quantity") final int quantity, @RequestParam("expend_trace") final String expend_trace, @RequestParam("uid") final String uid, final Request request, final Response response) {
        YxRenrenOperationAction.logger.info(MessageFormatter.format("yxRenrenPay start [sign={0},trade_finish_time={1},notify_id={2},trade_no={3},out_trade_no={4},rrd={5},trade_status={6},subject={7},quantity={8},expend_trace={9},uid={10}]", new Object[] { sign, trade_finish_time, notify_id, trade_no, out_trade_no, rrd, trade_status, subject, quantity, expend_trace, uid }));
        final StringBuilder sb = new StringBuilder();
        sb.append("expend_trace=").append(expend_trace).append("&");
        sb.append("notify_id=").append(notify_id).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        sb.append("quantity=").append(quantity).append("&");
        sb.append("rrd=").append(rrd).append("&");
        sb.append("sign_type=").append(sign_type).append("&");
        sb.append("subject=").append(subject).append("&");
        sb.append("trade_finish_time=").append(trade_finish_time).append("&");
        sb.append("trade_no=").append(trade_no).append("&");
        sb.append("trade_status=").append(trade_status).append("&");
        sb.append("uid=").append(uid);
        sb.append(PluginContext.configuration.getRenrenSecret("renren"));
        final String ticket = CodecUtil.md5(sb.toString()).toLowerCase();
        if (!ticket.equals(sign)) {
            YxRenrenOperationAction.logger.info("yxRenrenPay sign error: sign=" + sign + ",ticket=" + ticket + ",param=" + sb.toString());
            return new ByteResult(String.valueOf(7).getBytes());
        }
        if (!trade_status.equalsIgnoreCase("SUCCESS")) {
            YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {4}:yx={0},userId={1},orderId={2},gold={3}", new Object[] { "renren", uid, trade_no, rrd * 10, 8 }));
            return new ByteResult(String.valueOf(8).getBytes());
        }
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("expend_code", PluginContext.configuration.getRenrenExpendCode("renren"));
        paramMap.put("notify_id", notify_id);
        try {
            final String echo = WebUtils.sendRequest(PluginContext.configuration.getRenrenPayCheckUrl("renren"), paramMap);
            if (!echo.trim().equalsIgnoreCase("TRUE")) {
                YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {4}:yx={0},userId={1},orderId={2},gold={3}", new Object[] { "renren", uid, trade_no, rrd * 10, 9 }));
                return new ByteResult(String.valueOf(9).getBytes());
            }
        }
        catch (Exception e2) {
            YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {4}:yx={0},userId={1},orderId={2},gold={3}", new Object[] { "renren", uid, trade_no, rrd * 10, 9 }));
            return new ByteResult(String.valueOf(9).getBytes());
        }
        final int playerId = this.yxOperation.getDefaultPayPlayer(uid, "renren");
        try {
            final Tuple<Integer, Integer> result = this.yxOperation.pay(trade_no, playerId, uid, "renren", rrd * 10, request);
            if (1 == result.left) {
                YxRenrenOperationAction.logger.info(MessageFormatter.format("pay success:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "renren", uid, trade_no, result.right, rrd * 10 }));
                return new ByteResult("success".getBytes());
            }
            if (5 == result.left) {
                YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "renren", uid, trade_no, result.right, rrd * 10, result.left }));
                return new ByteResult("success".getBytes());
            }
            YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "renren", uid, trade_no, result.right, rrd * 10, result.left }));
            return new ByteResult(String.valueOf(result.left).getBytes());
        }
        catch (Exception e) {
            YxRenrenOperationAction.logger.info(MessageFormatter.format("pay fail because of {5}:yx={0},userId={1},orderId={2},playerId={3},gold={4}", new Object[] { "renren", uid, trade_no, playerId, rrd * 10, 6 }));
            YxRenrenOperationAction.logger.error("pay fail.", e);
            return new ByteResult(String.valueOf(6).getBytes());
        }
    }
    
    @Command("yxRenrenQueryUser")
    public ByteResult queryUser(@RequestParam("userId") final String userId, final Request request, final Response response) {
        final List<YxPlayerInfo> playerList = this.yxOperation.queryPlayer(userId, "renren");
        if (playerList == null || playerList.size() == 0) {
            return new ByteResult(String.valueOf(0).getBytes());
        }
        return new ByteResult(String.valueOf(1).getBytes());
    }
    
    private static long getOrderId(final long timestamp) {
        synchronized (YxRenrenOperationAction.lastOrderId) {
            if (timestamp > YxRenrenOperationAction.lastOrderId) {
                YxRenrenOperationAction.lastOrderId = timestamp;
            }
            else {
                ++YxRenrenOperationAction.lastOrderId;
            }
            // monitorexit(YxRenrenOperationAction.lastOrderId)
            return YxRenrenOperationAction.lastOrderId;
        }
    }
    
    private void redirectPayPage(final Request request, final Response response, final Map<String, String> params) {
        response.addHeader("Location", String.valueOf(PluginContext.configuration.getRenrenPayServiceUrl("renren")) + "?" + this.getParam(params));
        response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
    }
    
    private String getParam(final Map<String, String> paramMap) {
        final StringBuilder builder = new StringBuilder();
        final Set<Map.Entry<String, String>> entrySet = paramMap.entrySet();
        int index = 0;
        for (final Map.Entry<String, String> entry : entrySet) {
            if (index != 0) {
                builder.append("&");
            }
            try {
                builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
            }
            catch (UnsupportedEncodingException ex) {}
            ++index;
        }
        return builder.toString();
    }
}
