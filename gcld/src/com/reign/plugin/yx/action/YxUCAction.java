package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.framework.json.*;
import com.reign.util.codec.*;
import com.reign.plugin.yx.common.*;
import com.alibaba.fastjson.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class) })
public class YxUCAction
{
    private static final long serialVersionUID = 973256186245556095L;
    private static final Log errorLog;
    private static final Log opReport;
    private static final String YX = "uc";
    @Autowired
    IYxOperation yxOperation;
    
    static {
        errorLog = LogFactory.getLog(YxUCAction.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxANDUCLogin")
    public ByteResult login(@RequestParam("sid") final String sid, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(sid)) {
                YxUCAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_SESSION_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef"))), 1022));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u53c2\u6570\u9519\u8bef")));
            }
            final String requestURL = PluginContext.configuration.getSinaReceiptVerificationUrl("uc");
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("id", start);
            doc.createElement("service", "ucid.user.sidInfo");
            doc.startObject("game");
            final String cpId = PluginContext.configuration.getUCCpId("uc");
            doc.createElement("cpId", cpId);
            final String gameId = PluginContext.configuration.getUCGameId("uc");
            doc.createElement("gameId", gameId);
            final String channelId = "2";
            doc.createElement("channelId", channelId);
            final String serverId = "2663";
            doc.createElement("serverId", serverId);
            doc.endObject();
            doc.startObject("data");
            doc.createElement("sid", sid);
            doc.endObject();
            doc.createElement("encrypt", "md5");
            final String apiKey = PluginContext.configuration.getLoginKey("uc").trim();
            final String signSource = String.valueOf(cpId) + "sid=" + sid + apiKey;
            final String sign = CodecUtil.md5(signSource);
            doc.createElement("sign", sign);
            doc.endObject();
            String echo = WebUtils.sendRequestByPostWithJson(requestURL, doc.toString());
            echo = echo.trim();
            JSONObject json = (JSONObject)JSON.parse(echo);
            final JSONObject json2 = (JSONObject)JSON.parse(json.getString("data"));
            final String ucid = json2.getString("ucid");
            if (StringUtils.isBlank(ucid)) {
                YxUCAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_VERIFICATION_FAIL_ECHO_" + echo + "_SRC_" + signSource + "_SIGN_" + sign, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25"))), 1019));
                return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, "\u6821\u9a8c\u5931\u8d25")));
            }
            json = (JSONObject)JSON.parse(json.getString("data"));
            final Session session = this.yxOperation.login("uc", ucid, "", "", "1", "", request);
            YxUCAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId())))), 1));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("sessionId", session.getId()))));
        }
        catch (Exception e) {
            YxUCAction.errorLog.error("login_fail_EXCEPTION", e);
            YxUCAction.opReport.error("login_fail_EXCEPTION", e);
            YxUCAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25"))), 6));
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.EXCEPTION, "\u6821\u9a8c\u5931\u8d25")));
        }
    }
}
