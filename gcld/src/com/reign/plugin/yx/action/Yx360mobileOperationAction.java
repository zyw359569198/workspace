package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.*;
import java.util.*;
import com.reign.plugin.yx.common.*;
import com.alibaba.fastjson.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class Yx360mobileOperationAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "360mobile";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(Yx360mobileOperationAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxAND360mobileLogin")
    public ByteResult login(@RequestParam("code") final String code, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(code)) {
                Yx360mobileOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_CODE_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 1023));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            final String requestURL = PluginContext.configuration.getSinaReceiptVerificationUrl("360mobile");
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("grant_type", "authorization_code");
            paramMap.put("code", code);
            paramMap.put("client_id", PluginContext.configuration.getLoginKey("360mobile"));
            paramMap.put("client_secret", PluginContext.configuration.getPayKey("360mobile"));
            paramMap.put("redirect_uri", "oob");
            String echo = WebUtils.sendSSLGetRequest(requestURL, paramMap);
            echo = echo.trim();
            final JSONObject json = (JSONObject)JSON.parse(echo);
            final String access_token = json.getString("access_token");
            final String requestURL2 = PluginContext.configuration.getCmwebgameServerUrl("360mobile");
            final Map<String, Object> paramMap2 = new HashMap<String, Object>();
            paramMap2.put("access_token", access_token);
            String echo2 = WebUtils.sendSSLGetRequest(requestURL2, paramMap2);
            echo2 = echo2.trim();
            final JSONObject json2 = (JSONObject)JSON.parse(echo2);
            final String userId = json2.getString("id");
            if (StringUtils.isBlank(userId)) {
                Yx360mobileOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY_ECHO2_" + echo2, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25"))), 902));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25")));
            }
            final Session session = this.yxOperation.login("360mobile", userId, "", "", "1", "", request);
            Yx360mobileOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId())))), 1));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId()))));
        }
        catch (Exception e) {
            Yx360mobileOperationAction.errorLog.error("login_fail_EXCEPTION", e);
            Yx360mobileOperationAction.opReport.error("login_fail_EXCEPTION", e);
            Yx360mobileOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25"))), 6));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25")));
        }
    }
}
