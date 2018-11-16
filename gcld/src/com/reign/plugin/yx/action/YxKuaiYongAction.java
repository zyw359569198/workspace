package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.*;
import java.util.*;
import com.reign.util.codec.*;
import com.reign.plugin.yx.common.*;
import com.alibaba.fastjson.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class YxKuaiYongAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "kuaiyong";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxKuaiYongAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxIOSKuaiYongLogin")
    public ByteResult login(@RequestParam("tokenKey") final String tokenKey, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(tokenKey)) {
                YxKuaiYongAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 922));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            final String requestURL = PluginContext.configuration.getSinaReceiptVerificationUrl("kuaiyong");
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("tokenKey", tokenKey);
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getLoginKey("kuaiyong").trim());
            sb.append(tokenKey.trim());
            paramMap.put("sign", CodecUtil.md5(sb.toString()));
            String echo = WebUtils.sendGetRequest(requestURL, paramMap);
            echo = echo.trim();
            JSONObject json = (JSONObject)JSON.parse(echo);
            final String code = json.getString("code");
            if (!"0".equals(code)) {
                YxKuaiYongAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_VERIFICATION_FAIL_ECHO_" + echo + "_RETCODE_" + code + "_SRC_" + sb.toString() + "_SIGN_" + CodecUtil.md5(sb.toString()), YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25"))), 1019));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25")));
            }
            json = (JSONObject)JSON.parse(json.getString("data"));
            final String userId = json.getString("guid");
            final Session session = this.yxOperation.login("kuaiyong", userId, "", "", "1", "", request);
            YxKuaiYongAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId())))), 1));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId()))));
        }
        catch (Exception e) {
            YxKuaiYongAction.errorLog.error("login_fail_EXCEPTION", e);
            YxKuaiYongAction.opReport.error("login_fail_EXCEPTION", e);
            YxKuaiYongAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25"))), 6));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25")));
        }
    }
}
