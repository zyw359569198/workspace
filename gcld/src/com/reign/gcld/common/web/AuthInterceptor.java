package com.reign.gcld.common.web;

import com.reign.framework.netty.mvc.interceptor.*;
import java.util.concurrent.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.user.dto.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.plug.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

public class AuthInterceptor implements Interceptor
{
    private static Map<String, Integer> map;
    public static ConcurrentMap<Tuple<String, String>, Tuple<Long, String>> blockPlayerMap;
    private static Set<String> methodSet;
    private static final Logger errorLogger;
    private static final int ACTION_NONEED_CHECK = 1;
    private static final int ACTION_IP_CHECK = 2;
    private static final int ACTION_NO_PLAYER_CHECK = 3;
    private static final int ACTION_METHOD_CHECK = 4;
    
    static {
        AuthInterceptor.map = new HashMap<String, Integer>();
        AuthInterceptor.blockPlayerMap = new ConcurrentHashMap<Tuple<String, String>, Tuple<Long, String>>();
        AuthInterceptor.methodSet = new HashSet<String>();
        errorLogger = CommonLog.getLog(AuthInterceptor.class);
        AuthInterceptor.map.put("UserAction", 1);
        AuthInterceptor.map.put("VersionAction", 1);
        AuthInterceptor.map.put("OfficialWebsiteAction", 1);
        AuthInterceptor.map.put("YxAction", 1);
        AuthInterceptor.map.put("YxRenrenOperationAction", 1);
        AuthInterceptor.map.put("YxRenrenOperationAction2", 1);
        AuthInterceptor.map.put("YxKaixinOperationAction", 1);
        AuthInterceptor.map.put("YxSogouOperationAction", 1);
        AuthInterceptor.map.put("YxXunleiOperationAction", 1);
        AuthInterceptor.map.put("Yx360OperationAction", 1);
        AuthInterceptor.map.put("YxCmwebgameOperationAction", 1);
        AuthInterceptor.map.put("YxDuowanOperationAction", 1);
        AuthInterceptor.map.put("ValidateCodeAction", 1);
        AuthInterceptor.map.put("YxJDOperationAction", 1);
        AuthInterceptor.map.put("YxSinaWWOperationAction", 1);
        AuthInterceptor.map.put("YxSinaWYXOperationAction", 1);
        AuthInterceptor.map.put("YxTaobaoperationAction", 1);
        AuthInterceptor.map.put("YxTencentOperationAction", 1);
        AuthInterceptor.map.put("Yx10086OperationAction", 1);
        AuthInterceptor.map.put("Yx5211gameOperationAction", 1);
        AuthInterceptor.map.put("YxPingAnOperationAction", 1);
        AuthInterceptor.map.put("NoticeAction", 2);
        AuthInterceptor.map.put("BackStageAction", 2);
        AuthInterceptor.map.put("PlayerAction", 3);
        AuthInterceptor.methodSet.add("getPlayerInfo");
        AuthInterceptor.methodSet.add("getPlayerList");
        AuthInterceptor.methodSet.add("deletePlayer");
        AuthInterceptor.methodSet.add("retrievePlayer");
        AuthInterceptor.methodSet.add("setPlayerName");
        AuthInterceptor.methodSet.add("setPlayerNames");
        AuthInterceptor.methodSet.add("getForceInfo");
        AuthInterceptor.methodSet.add("setPlayerForce");
        AuthInterceptor.methodSet.add("game");
        AuthInterceptor.methodSet.add("setDefaultPay");
        AuthInterceptor.methodSet.add("wantToLeave");
        AuthInterceptor.methodSet.add("leave");
    }
    
    @Override
	public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        final String actionName = invocation.getActionName();
        final String methodName = invocation.getMethodName();
        final Tuple<Boolean, Integer> tuple = this.check(actionName, methodName);
        final PlayerDto playerDto = (PlayerDto)request.getSession().getAttribute("PLAYER");
        if (tuple.left) {
            if (tuple.right == null || 3 == tuple.right) {
                final UserDto user = (UserDto)request.getSession().getAttribute("user");
                if (user == null) {
                    request.getSession().markDiscard();
                    return (Result<?>)this.createUnloginError(LocalMessages.T_AUTH_10001, response);
                }
                return this.validateByPlayer(invocation, interceptors, request, response, user, tuple.right == null);
            }
            else if (2 == tuple.right) {
                final String ips = Configuration.getProperty("gcld.backstage.ip");
                if (StringUtils.isBlank(ips) || !YxUtil.includeIp(ips, WebUtil.getIpAddr(request))) {
                    AuthInterceptor.errorLogger.error("#back_ip_forbidden#ips:" + ips + "#ip:" + WebUtil.getIpAddr(request) + "#");
                    return new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_AUTH_10006));
                }
            }
        }
        return this.invoke(invocation, interceptors, request, response, playerDto);
    }
    
    private Result<?> validateByPlayer(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response, final UserDto user, final boolean checkPlayerDto) throws Exception {
        final PlayerDto playerDto = (PlayerDto)request.getSession().getAttribute("PLAYER");
        if (playerDto == null && !checkPlayerDto) {
            return this.invoke(invocation, interceptors, request, response, null);
        }
        if (playerDto == null) {
            request.getSession().markDiscard();
            return (Result<?>)this.createUnloginError(LocalMessages.T_AUTH_10001, response);
        }
        if (playerDto.playerId != 0) {
            if (!Players.isValidate(playerDto)) {
                request.getSession().markDiscard();
                return (Result<?>)this.createUnloginError(LocalMessages.T_AUTH_10002, response);
            }
            final Tuple<String, String> key = new Tuple(playerDto.userId, playerDto.yx);
            if (checkBlock(key)) {
                request.getSession().markDiscard();
                final Tuple<Long, String> tuple = AuthInterceptor.blockPlayerMap.get(key);
                return createBlockError(tuple.left, tuple.right, response);
            }
        }
        if (TimeSlice.getInstance().needBlock(playerDto.playerId) && !AuthInterceptor.methodSet.contains(invocation.getMethodName())) {
            ThreadLocalFactory.setTreadLocalLog("BLOCK");
            return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.CODE, "")));
        }
        return this.invoke(invocation, interceptors, request, response, playerDto);
    }
    
    private final Result<?> invoke(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response, final PlayerDto playerDto) throws Exception {
        if (playerDto != null && playerDto.playerId != 0) {
            try {
                Constants.locks[playerDto.playerId % Constants.LOCKS_LEN].lock();
                return invocation.invoke(interceptors, request, response);
            }
            finally {
                Constants.locks[playerDto.playerId % Constants.LOCKS_LEN].unlock();
            }
        }
        return invocation.invoke(interceptors, request, response);
    }
    
    private Tuple<Boolean, Integer> check(final String actionName, final String methodName) {
        final Integer result = AuthInterceptor.map.get(actionName);
        final Tuple<Boolean, Integer> tuple = new Tuple();
        tuple.right = result;
        if (result == null) {
            tuple.left = true;
        }
        else if (result == 1) {
            tuple.left = false;
        }
        else if (result == 4) {
            tuple.left = !AuthInterceptor.methodSet.contains(methodName);
            tuple.right = null;
        }
        else if (result == 2) {
            tuple.left = true;
            tuple.right = 2;
        }
        else if (result == 3) {
            tuple.left = true;
            tuple.right = (AuthInterceptor.methodSet.contains(methodName) ? result : null);
        }
        else {
            tuple.left = true;
        }
        return tuple;
    }
    
    public static void blockPlayer(final String userId, final String yx, final long endTimestamp, final String reason) {
        AuthInterceptor.blockPlayerMap.put(new Tuple(userId, yx), new Tuple(endTimestamp, reason));
    }
    
    public static void unblockPlayer(final String userId, final String yx) {
        AuthInterceptor.blockPlayerMap.remove(new Tuple(userId, yx));
    }
    
    public static boolean checkBlock(final String userId, final String yx) {
        final Tuple<String, String> key = new Tuple(userId, yx);
        return checkBlock(key);
    }
    
    private static boolean checkBlock(final Tuple<String, String> key) {
        if (AuthInterceptor.blockPlayerMap.containsKey(key)) {
            if (AuthInterceptor.blockPlayerMap.get(key).left > System.currentTimeMillis()) {
                return true;
            }
            AuthInterceptor.blockPlayerMap.remove(key);
        }
        return false;
    }
    
    private ByteResult createUnloginError(final String msg, final Response response) {
        response.markClose();
        return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.REDIRECT, msg)));
    }
    
    public static ByteResult createBlockError(final long endTime, final String msg, final Response response) {
        response.markClose();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("reason", (Object)MessageFormatter.format(LocalMessages.T_USER_10006, new Object[] { msg }));
        doc.createElement("cd", (Object)TimeUtil.now2specMs(endTime));
        doc.endObject();
        return new ByteResult(JsonBuilder.getJson(JsonBuilder.getJson(State.BLOCK, doc.toByte())));
    }
}
