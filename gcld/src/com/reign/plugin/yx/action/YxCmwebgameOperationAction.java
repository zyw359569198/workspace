package com.reign.plugin.yx.action;

import com.reign.framework.netty.mvc.view.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.*;
import com.reign.util.*;
import com.reign.framework.netty.servlet.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.util.*;
import com.reign.plugin.yx.common.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxCmwebgameOperationAction
{
    private static final Log opReport;
    private static final String DEFAULT_YX = "cmwebgame";
    public static Map<String, String> yxUserIdServerIdMap;
    @Autowired
    IYxOperation yxOperation;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        YxCmwebgameOperationAction.yxUserIdServerIdMap = new ConcurrentHashMap<String, String>();
    }
    
    @Command("yxCmwebgameLogin")
    public ByteResult login(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final String tp, @RequestParam("ticket") final String ticket, @RequestParam("isAdult") int isAdult, @RequestParam("yxSource") final String yxSource, @RequestParam("originalServerid") final String originalServerid, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        try {
            isAdult = ((isAdult == 1) ? isAdult : 0);
            String YX = yx;
            if (StringUtils.isBlank(yx)) {
                YX = "cmwebgame";
            }
            if (StringUtils.isBlank(userId)) {
                YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(902)), 902));
                return YxHelper.redirectUnlogPage(902, request, response);
            }
            if (StringUtils.isBlank(tp)) {
                YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(910)), 910));
                return YxHelper.redirectUnlogPage(910, request, response);
            }
            if (StringUtils.isBlank(ticket)) {
                YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(922)), 922));
                return YxHelper.redirectUnlogPage(922, request, response);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(userId);
            sb.append(tp);
            sb.append(PluginContext.configuration.getLoginKey(YX));
            if (!YxHelper.isTicketPass(MD5SecurityUtil.code(sb.toString()), ticket)) {
                YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET_SRC_" + sb.toString() + "_MD5_" + MD5SecurityUtil.code(sb.toString()) + "_TICKET_" + ticket, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(909)), 909));
                return YxHelper.redirectUnlogPage(909, request, response);
            }
            final Session session = this.yxOperation.login(YX, userId, "", "", new StringBuilder().append(isAdult).toString(), yxSource, request);
            if (StringUtils.isNotBlank(originalServerid)) {
                YxCmwebgameOperationAction.yxUserIdServerIdMap.put(String.valueOf(yx) + "_" + userId, originalServerid);
            }
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Set-Cookie", MessageFormatter.format("ticket={0};path=/", new Object[] { session.getId() }));
            final Cookie cookie = new DefaultCookie("userId", userId);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.addHeader("Location", PluginContext.configuration.getGameURL(YX));
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            return YxHelper.getResult(1, request, response);
        }
        catch (Exception e) {
            YxCmwebgameOperationAction.opReport.error("login_fail_EXCEPTION", e);
            YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(6)), 6));
            return YxHelper.getResult(6, request, response);
        }
    }
    
    public static void callYxCm(final Request request, final String userId, String yxSource, final String yx, final String currentServerId) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yxSource)) {
                yxSource = "";
            }
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user_account", userId);
            String originalServerid = YxCmwebgameOperationAction.yxUserIdServerIdMap.get(String.valueOf(yx) + "_" + userId);
            if (StringUtils.isBlank(originalServerid)) {
                originalServerid = currentServerId;
            }
            final String serverUrl = MessageFormatter.format(PluginContext.configuration.getCmwebgameServerUrl(yx), new Object[] { originalServerid });
            paramMap.put("serverurl", serverUrl);
            paramMap.put("promotecode", yxSource);
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getCmwebgameKey(yx));
            sb.append(userId);
            sb.append(serverUrl);
            final String md5Value = MD5SecurityUtil.code(sb.toString()).toLowerCase();
            paramMap.put("hash", md5Value);
            try {
                String echo = WebUtils.sendGetRequest(PluginContext.configuration.getCmwebgameAPIUrl(yx), paramMap);
                if (StringUtils.isNotBlank(echo)) {
                    echo = echo.trim();
                }
                if ("result=1".equals(echo)) {
                    YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYxCm", "callYxCm_success_SUCCESS_ECHO_" + echo + "_userId_" + userId + "_yxSource_" + yxSource, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 1));
                }
                else if ("result=0".equals(echo)) {
                    YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYxCm", "callYxCm_fail_KEY_ERROR_ECHO_" + echo + "_SRC_" + sb.toString() + "_MD5_" + md5Value + "_userId_" + userId + "_yxSource_" + yxSource, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 7));
                }
                else {
                    YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYxCm", "callYxCm_fail_USERID_ERROR_OR_SERVERURL_ERROR_ECHO_" + echo + "_userId_" + userId + "_yxSource_" + yxSource, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 2));
                }
            }
            catch (Exception e) {
                YxCmwebgameOperationAction.opReport.error("callYxCm_fail_EXCEPTION", e);
                YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYxCm", "callYxCm_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
            }
        }
        catch (Exception e2) {
            YxCmwebgameOperationAction.opReport.error("callYxCm_fail_EXCEPTION", e2);
            YxCmwebgameOperationAction.opReport.info(OpLogUtil.formatOpInterfaceLog("callYxCm", "callYxCm_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult("".getBytes()), 6));
        }
    }
}
