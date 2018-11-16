package com.reign.gcld.user.action;

import com.reign.gcld.user.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.json.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.user.domain.*;
import com.reign.util.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.building.service.*;
import com.reign.framework.netty.servlet.*;

public class UserAction extends BaseAction
{
    private static final long serialVersionUID = 7009474579223100645L;
    @Autowired
    private IUserService userService;
    @Autowired
    private IPlayerDao playerDao;
    
    @Command("createUser")
    public ByteResult createUser(@RequestParam("userName") final String userName, @RequestParam("password") final String password, final Request request) {
        final byte[] result = this.userService.createUser(userName, password);
        return this.getResult(result, request);
    }
    
    @Command("reconnect")
    public ByteResult reconnect(@RequestParam("sessionId") final String sessionId, final Request request, final Response response) {
        final Session session = SessionManager.getInstance().getSession(sessionId, false);
        final Session oldSession = request.getSession(false);
        if (session == null) {
            if (oldSession != null) {
                oldSession.markDiscard();
            }
            response.markClose();
            return this.getNoneExtraResult(JsonBuilder.getJson(State.REDIRECT, LocalMessages.T_AUTH_10001));
        }
        if (oldSession != null && !oldSession.getId().equals(sessionId)) {
            oldSession.markDiscard();
        }
        session.setChannel(request.getChannel());
        request.setSessionId(session.getId());
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
    
    @Command("login_user")
    public ByteResult login_user(@RequestParam("userkey") final String userkey, final Request request, final Response response) {
        final Session session = SessionManager.getInstance().getSession(userkey);
        final Session oldSession = request.getSession(false);
        if (session == null) {
            if (oldSession != null) {
                oldSession.markDiscard();
            }
            response.markClose();
            return this.getNoneExtraResult(JsonBuilder.getJson(State.REDIRECT, LocalMessages.T_AUTH_10001));
        }
        final UserDto dto = (UserDto)session.getAttribute("user");
        if (dto == null) {
            if (oldSession != null) {
                oldSession.markDiscard();
            }
            response.markClose();
            return this.getResult(JsonBuilder.getJson(State.REDIRECT, LocalMessages.T_AUTH_10001), request);
        }
        final int count = this.playerDao.getRoleCount(dto.userId, dto.yx);
        if (count <= 0) {
            dto.firstLogin = true;
        }
        else {
            dto.firstLogin = false;
        }
        if (oldSession != null && !oldSession.getId().equals(session.getId())) {
            oldSession.markDiscard();
        }
        session.setChannel(request.getChannel());
        request.setSessionId(session.getId());
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "sessionId", session.getId()), request);
    }
    
    @Command("login2")
    public ByteResult login2(@RequestParam("userName") final String userName, @RequestParam("password") final String password, final Request request, final Response response) {
        final ThreeTuple<Boolean, User, byte[]> tuple = this.userService.login(userName, password);
        if (tuple.left) {
            final Tuple<String, String> key = new Tuple(tuple.middle.getId().toString(), "gcld");
            final Tuple<Long, String> temp = AuthInterceptor.blockPlayerMap.get(key);
            return AuthInterceptor.createBlockError(temp.left, temp.right, response);
        }
        if (tuple.middle == null) {
            return this.getResult(tuple.right, request);
        }
        final UserDto dto = new UserDto();
        final User user = tuple.middle;
        this.copyProperties(dto, user);
        final int count = this.playerDao.getRoleCount(dto.userId, dto.yx);
        if (count <= 0) {
            dto.firstLogin = true;
        }
        else {
            dto.firstLogin = false;
        }
        if (WebUtil.needAntiAddiction(dto.yx) && user.getAdult() == 0) {
            dto.setNeedAntiAddiction(true);
        }
        else {
            dto.setNeedAntiAddiction(false);
        }
        final Session session = request.getSession();
        request.setSessionId(session.getId());
        this.putToSession("user", dto, request);
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, "sessionId", session.getId()), request);
    }
    
    @Command("quit")
    public ByteResult quit(final Request request, final Response response) {
        this.clearSession(request);
        response.markClose();
        return this.getNoneExtraResult(JsonBuilder.getJson(State.SUCCESS, ""));
    }
    
    @Command("logout")
    public ByteResult logout(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        BuildingService.clearPlayerBuilding(playerDto.playerId);
        this.leaveChatGroup(playerDto, request.getSession());
        this.clearSession(request, "PLAYER");
        return this.getNoneExtraResult(JsonBuilder.getJson(State.SUCCESS, ""));
    }
    
    private void copyProperties(final UserDto dto, final User user) {
        dto.userId = String.valueOf(user.getId());
        dto.userName = user.getUserName();
        dto.loginTime = System.currentTimeMillis();
        dto.activate = user.getActivate();
        dto.yx = "gcld";
    }
    
    private void leaveChatGroup(final PlayerDto playerDto, final Session session) {
        final String sessionId = session.getId();
        GroupManager.getInstance().leave(sessionId);
    }
}
