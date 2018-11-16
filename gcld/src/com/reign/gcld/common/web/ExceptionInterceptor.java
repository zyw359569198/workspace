package com.reign.gcld.common.web;

import com.reign.framework.netty.mvc.interceptor.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.user.dto.*;
import org.apache.commons.lang.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.*;
import com.reign.gcld.player.common.*;
import com.reign.framework.netty.servlet.*;

public class ExceptionInterceptor implements Interceptor
{
    private static final Logger logger;
    private static final Logger log;
    private static final Logger dayReportLogger;
    private static final ByteResult E0010_BYTERESULT;
    private static final ByteResult E0010_BYTERESULT_SPEC;
    @Autowired
    private IPlayerDao playerDao;
    
    static {
        logger = new InterfaceLogger();
        log = CommonLog.getLog(ExceptionInterceptor.class);
        dayReportLogger = new DayReportLogger();
        E0010_BYTERESULT = new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10035)));
        E0010_BYTERESULT_SPEC = new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10035));
    }
    
    @Override
	public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        final long starttime = System.currentTimeMillis();
        boolean exception = false;
        final String actionName = invocation.getActionName();
        final String methodName = invocation.getMethodName();
        Session session = request.getSession(false);
        PlayerDto dto = (session != null) ? ((PlayerDto)session.getAttribute("PLAYER")) : null;
        try {
            final Result<?> obj = invocation.invoke(interceptors, request, response);
            for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                ExceptionInterceptor.dayReportLogger.info(log);
            }
            return obj;
        }
        catch (Throwable t) {
            exception = true;
            ByteResult result = ExceptionInterceptor.E0010_BYTERESULT;
            if ("OfficialWebsiteAction".equalsIgnoreCase(actionName) || "BackStageAction".equalsIgnoreCase(actionName) || "VersionAction".equalsIgnoreCase(actionName) || "NoticeAction".equalsIgnoreCase(actionName)) {
                result = ExceptionInterceptor.E0010_BYTERESULT_SPEC;
            }
            final int playerId = (dto == null) ? -1 : dto.playerId;
            final String playerName = (dto == null) ? "null" : dto.playerName;
            WebUtil.print(ExceptionInterceptor.log, "call " + actionName + " " + methodName + " playerId" + playerId + " playerName" + playerName, t);
            ThreadLocalFactory.setTreadLocalLog("ERROR");
            return result;
        }
        finally {
            final String threadLogInfo = ThreadLocalFactory.getTreadLocalLog();
            ThreadLocalFactory.clearTreadLocalLog();
            session = request.getSession(false);
            UserDto userDto = (session != null) ? ((UserDto)session.getAttribute("user")) : null;
            dto = ((session != null) ? ((PlayerDto)session.getAttribute("PLAYER")) : null);
            String userId = null;
            String yx = null;
            boolean firstLogin = true;
            if (userDto != null) {
                firstLogin = userDto.firstLogin;
                userId = userDto.userId;
                yx = userDto.yx;
                if ("playerAction".equalsIgnoreCase(actionName) && "setPlayerForce".equalsIgnoreCase(methodName) && userDto.success && 1 >= this.playerDao.getRoleCount(userId, yx)) {
                    firstLogin = true;
                }
            }
            if (("OfficialWebsiteAction".equalsIgnoreCase(actionName) && methodName.equalsIgnoreCase("preLogin")) || ("OfficialWebsiteAction".equalsIgnoreCase(actionName) && methodName.equalsIgnoreCase("login")) || ("UserAction".equalsIgnoreCase(actionName) && methodName.equalsIgnoreCase("login_user"))) {
                if (StringUtils.isBlank(userId) && session != null) {
                    final Tuple<String, String> tuple = (Tuple<String, String>)session.getAttribute("yx");
                    if (tuple != null) {
                        userId = tuple.left;
                        yx = tuple.right;
                    }
                    session.removeAttribute("yx");
                }
                if (this.playerDao.getRoleCount(userId, yx) >= 1) {
                    firstLogin = false;
                }
            }
            final String ip = WebUtil.getIpAddr(request);
            final long endtime = System.currentTimeMillis();
            if (dto != null) {
                if (!methodName.endsWith("chat@speak")) {
                    ExceptionInterceptor.logger.info(LogUtil.formatInterfaceLog(dto.playerId, dto.playerName, dto.playerLv, userId, dto.yx, ip, actionName, methodName, request.getParamterMap(), "".getBytes(), endtime - starttime, false, exception, firstLogin, dto.yxSource, dto.forceId, dto.consumeLv, request.getProtocol(), threadLogInfo, dto.platForm));
                }
            }
            else if (("OfficialWebsiteAction".equalsIgnoreCase(actionName) && methodName.equalsIgnoreCase("prelogin")) || ("OfficialWebsiteAction".equalsIgnoreCase(actionName) && methodName.equalsIgnoreCase("login"))) {
                String[] temp = request.getParamterMap().get("userId");
                if (temp != null && temp.length > 0) {
                    userId = temp[0];
                }
                temp = request.getParamterMap().get("yx");
                if (temp != null && temp.length > 0) {
                    yx = temp[0];
                }
                ExceptionInterceptor.logger.info(LogUtil.formatInterfaceLog(null, null, null, userId, yx, ip, actionName, methodName, request.getParamterMap(), request.getContent(), endtime - starttime, false, exception, firstLogin, null, 0, 0, request.getProtocol(), threadLogInfo, null));
            }
            else {
                if (StringUtils.isBlank(yx)) {
                    final String[] temp = request.getParamterMap().get("yx");
                    if (temp != null && temp.length > 0) {
                        yx = temp[0];
                    }
                }
                if (StringUtils.isBlank(userId)) {
                    session = request.getSession(false);
                    userDto = ((session != null) ? ((UserDto)session.getAttribute("user")) : null);
                    if (userDto != null) {
                        userId = userDto.userId;
                    }
                }
                ExceptionInterceptor.logger.info(LogUtil.formatInterfaceLog(null, null, null, userId, yx, ip, actionName, methodName, request.getParamterMap(), request.getContent(), endtime - starttime, false, exception, firstLogin, null, 0, 0, request.getProtocol(), threadLogInfo, null));
            }
        }
    }
}
