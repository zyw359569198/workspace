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
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class YxWanDouJiaAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "wandoujia";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxWanDouJiaAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxANDWanDouJiaLogin")
    public ByteResult login(@RequestParam("uid") final String uid, @RequestParam("token") final String token, @RequestParam("appkey_id") final String appkey_id, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(uid)) {
                YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 902));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            if (StringUtils.isBlank(token)) {
                YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 922));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            if (StringUtils.isBlank(appkey_id)) {
                YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_APPKEY_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 1021));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            final String requestURL = PluginContext.configuration.getSinaReceiptVerificationUrl("wandoujia");
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("uid", uid);
            paramMap.put("token", token);
            paramMap.put("appkey_id", appkey_id);
            String echo = WebUtils.sendGetRequest(requestURL, paramMap);
            echo = echo.trim();
            if (!"true".equals(echo)) {
                YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_VERIFICATION_FAIL_ECHO_" + echo + "_uid_" + uid + "_token_" + token + "_appkey_id_" + appkey_id, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25"))), 1019));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25")));
            }
            final Session session = this.yxOperation.login("wandoujia", uid, "", "", "1", "", request);
            YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId())))), 1));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId()))));
        }
        catch (Exception e) {
            YxWanDouJiaAction.errorLog.error("login_fail_EXCEPTION", e);
            YxWanDouJiaAction.opReport.error("login_fail_EXCEPTION", e);
            YxWanDouJiaAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25"))), 6));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25")));
        }
    }
}
