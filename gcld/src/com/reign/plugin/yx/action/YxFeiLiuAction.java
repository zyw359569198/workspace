package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.common.*;
import com.reign.plugin.yx.*;
import com.reign.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class YxFeiLiuAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "feiliu";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxFeiLiuAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxIOSFeiLiuLogin")
    public ByteResult login(@RequestParam("uuid") final String uuid, @RequestParam("timestamp") final long timestamp, @RequestParam("sign") final String sign, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(uuid)) {
                YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 902));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            if (timestamp <= 0L) {
                YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 910));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            if (StringUtils.isBlank(sign)) {
                YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 922));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            final String gameId = PluginContext.configuration.getLoginKey("feiliu").trim();
            final String src = RSAUtil.decrypt(Integer.parseInt(gameId), sign);
            final String[] tempArr = src.split("&");
            final String userId = tempArr[1];
            final String time = tempArr[0];
            if (!uuid.equals(userId) || !time.equals(new StringBuilder(String.valueOf(timestamp)).toString())) {
                YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_VERIFICATION_FAIL_SRC_" + src, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25"))), 1019));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25")));
            }
            final Session session = this.yxOperation.login("feiliu", userId, "", "", "1", "", request);
            YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId())))), 1));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId()))));
        }
        catch (Exception e) {
            YxFeiLiuAction.errorLog.error("login_fail_EXCEPTION", e);
            YxFeiLiuAction.opReport.error("login_fail_EXCEPTION", e);
            YxFeiLiuAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25"))), 6));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25")));
        }
    }
}
