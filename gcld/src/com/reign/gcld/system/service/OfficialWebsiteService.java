package com.reign.gcld.system.service;

import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.user.dto.*;
import java.util.concurrent.*;
import com.reign.gcld.common.log.*;
import org.apache.commons.lang.*;
import com.reign.gcld.system.util.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.framework.netty.servlet.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import java.util.*;
import com.reign.framework.json.*;

@Component("officialWebsiteService")
public class OfficialWebsiteService implements IOfficialWebsiteService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerPayDao playerPayDao;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IPayService payService;
    public Map<String, Tuple<Long, String>> infoMap;
    public static Map<String, UserDto> userDto;
    private static final Logger opPeport;
    private static final Logger errorLog;
    
    static {
        OfficialWebsiteService.userDto = new ConcurrentHashMap<String, UserDto>();
        opPeport = new OpReportLogger();
        errorLog = CommonLog.getLog(OfficialWebsiteService.class);
    }
    
    public OfficialWebsiteService() {
        this.infoMap = new ConcurrentHashMap<String, Tuple<Long, String>>();
    }
    
    @Override
    public byte[] preLogin(final String yx, final String userId, final long tp, final String additionalKey, final String ticket, final Request request) {
        final Tuple<String, String> tuple = new Tuple();
        tuple.left = userId;
        tuple.right = yx;
        final Session session = request.getSession(false);
        if (session != null) {
            session.setAttribute("yx", tuple);
        }
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_YX_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(101)), 101));
                return BackstageUtil.returnError(101);
            }
            if (StringUtils.isBlank(userId)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_USER_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(102)), 102));
                return BackstageUtil.returnError(102);
            }
            if (tp <= 0L) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_TIMESTAMP_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(106)), 106));
                return BackstageUtil.returnError(106);
            }
            if (StringUtils.isBlank(additionalKey)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_KEY_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(105)), 105));
                return BackstageUtil.returnError(105);
            }
            if (StringUtils.isBlank(ticket)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_TICKET_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(108)), 108));
                return BackstageUtil.returnError(108);
            }
            if (!YxUtil.isMatched(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_YX_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(114)), 114));
                return BackstageUtil.returnError(114);
            }
            final String loginKey = Configuration.getProperty(yx, "gcld.login.key");
            final String authenticTicket = MD5SecurityUtil.code(String.valueOf(yx) + userId + tp + loginKey);
            if (authenticTicket.equals(ticket)) {
                this.infoMap.put(String.valueOf(yx) + ":" + userId, new Tuple(tp, additionalKey));
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_success_SUCCESS", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnSuccess()), 1));
                return BackstageUtil.returnSuccess();
            }
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_WRONG_TICKET", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(204)), 204));
            return BackstageUtil.returnError(204);
        }
        catch (Exception e) {
            OfficialWebsiteService.errorLog.info("preLogin_fail_EXCEPTION", e);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_EXCEPTION", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(212)), 212));
            return BackstageUtil.returnError(212);
        }
    }
    
    @Override
    public byte[] login(final String yx, final String userId, final String userName, final long tp, String sfid, int adult, final String yxSource, final String ticket, final Request request, final Response response) {
        final Tuple<String, String> tuple1 = new Tuple();
        tuple1.left = userId;
        tuple1.right = yx;
        final Session session1 = request.getSession(false);
        if (session1 != null) {
            session1.setAttribute("yx", tuple1);
        }
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_YX_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(101)), 101));
                return BackstageUtil.returnError(101);
            }
            if (!YxUtil.isMatched(yx)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_YX_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(114)), 114));
                return BackstageUtil.returnError(114);
            }
            if (StringUtils.isBlank(userId)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(102)), 102));
                return BackstageUtil.returnError(102);
            }
            if (StringUtils.isBlank(userName)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_USER_NAME_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(110)), 110));
                return BackstageUtil.returnError(110);
            }
            if (tp <= 0L) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(106)), 106));
                return BackstageUtil.returnError(106);
            }
            if (StringUtils.isBlank(ticket)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TICKET_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(108)), 108));
                return BackstageUtil.returnError(108);
            }
            if (adult < 0 || adult > 1) {
                adult = 1;
            }
            final String loginKey = Configuration.getProperty(yx, "gcld.login.key");
            final Tuple<Long, String> tuple2 = this.infoMap.get(String.valueOf(yx) + ":" + userId);
            if (tuple2 == null) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_NOT_PRELOGIN", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(201)), 201));
                return BackstageUtil.returnError(201);
            }
            if (!tuple2.left.equals(tp)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_TIMESTAMP_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(115)), 115));
                return BackstageUtil.returnError(115);
            }
            if (StringUtils.isBlank(sfid)) {
                sfid = "";
            }
            final String authenticTicket = MD5SecurityUtil.code(String.valueOf(yx) + userId + sfid + tp + tuple2.right + loginKey);
            if (!authenticTicket.equals(ticket)) {
                response.addHeader("Location", Configuration.getProperty(yx, "gcld.unprelogin.redirect.url"));
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_WRONG_TICKET", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(204)), 204));
                return BackstageUtil.returnError(204);
            }
            final UserDto dto = new UserDto();
            dto.userId = userId;
            dto.loginTime = System.currentTimeMillis();
            dto.yx = yx;
            dto.setYxSource(yxSource);
            dto.firstLogin = (this.playerDao.getRoleCount(userId, yx) <= 0);
            if (WebUtil.needAntiAddiction(yx) && adult == 0) {
                dto.setNeedAntiAddiction(true);
            }
            else {
                dto.setNeedAntiAddiction(false);
            }
            final Session session2 = request.getNewSession();
            session2.setAttribute("user", dto);
            this.infoMap.remove(String.valueOf(yx) + ":" + userId);
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Location", Configuration.getProperty(yx, "gcld.game.url"));
            response.addHeader("Set-Cookie", "ticket=" + session2.getId() + ";path=/");
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_success_SUCCESS", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnSuccess()), 1));
            return BackstageUtil.returnSuccess();
        }
        catch (Exception e) {
            OfficialWebsiteService.errorLog.info("login_fail_EXCEPTION", e);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("login", "login_fail_EXCEPTION", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(212)), 212));
            return BackstageUtil.returnError(212);
        }
    }
    
    @Transactional
    @Override
    public byte[] pay(final String yx, final String userId, int playerId, final String orderId, final int gold, final long tp, final String ticket, final Request request) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(101)), 101));
                return BackstageUtil.returnError(101);
            }
            if (StringUtils.isBlank(userId)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_USER_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(102)), 102));
                return BackstageUtil.returnError(102);
            }
            if (StringUtils.isBlank(orderId)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(104)), 104));
                return BackstageUtil.returnError(104);
            }
            if (gold <= 0) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_GOLD_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(107)), 107));
                return BackstageUtil.returnError(107);
            }
            if (tp <= 0L) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TIMESTAMP_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(106)), 106));
                return BackstageUtil.returnError(106);
            }
            if (StringUtils.isBlank(ticket)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_TICKET_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(108)), 108));
                return BackstageUtil.returnError(108);
            }
            if (!YxUtil.isMatched(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_YX_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(114)), 114));
                return BackstageUtil.returnError(114);
            }
            Player player = null;
            if (playerId <= 0) {
                final List<Player> players = this.playerDao.getPlayerByUserId(userId, yx);
                playerId = this.getDefaultPlayerId(players);
                player = this.playerDao.read(playerId);
            }
            else {
                player = this.playerDao.read(playerId);
            }
            if (playerId <= 0 || player == null) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_PLAYER_NOT_EXIST", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(209)), 209));
                return BackstageUtil.returnError(209);
            }
            if (!player.getUserId().equals(userId)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_PLAYER_NOT_BELONG", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(206)), 206));
                return BackstageUtil.returnError(206);
            }
            if (this.playerPayDao.containsOrderId(orderId, yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_EXIST", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(208)), 208));
                return BackstageUtil.returnError(208);
            }
            final String payKey = Configuration.getProperty(yx, "gcld.pay.key");
            final String authenticTicket = MD5SecurityUtil.code(String.valueOf(yx) + userId + orderId + gold + tp + payKey);
            if (!ticket.equals(authenticTicket)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_WRONG_TICKET", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(204)), 204));
                return BackstageUtil.returnError(204);
            }
            return this.payService.pay(orderId, playerId, userId, yx, gold, player.getYxSource(), request);
        }
        catch (Exception e) {
            OfficialWebsiteService.errorLog.error("pay_fail_EXCEPTION", e);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(212)), 212));
            OfficialWebsiteService.errorLog.error("#pay_exception#playerid#" + playerId + "#orderId#" + orderId + "#" + "gold#" + gold + "#");
            OfficialWebsiteService.errorLog.error("#pay_exception#playerid#" + playerId + "#orderId#" + orderId + "#" + "gold" + gold + "#", e);
            return BackstageUtil.returnError(212);
        }
    }
    
    private int getDefaultPlayerId(final List<Player> players) {
        if (players == null || players.size() == 0) {
            return 0;
        }
        int playerId = 0;
        int playerLv = 0;
        for (final Player player : players) {
            if (player.getPlayerLv() > playerLv) {
                playerId = player.getPlayerId();
                playerLv = player.getPlayerLv();
            }
        }
        return playerId;
    }
    
    @Override
    public byte[] palayerInfo(final String yx, final String userId, final Request request) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_YX_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(101)), 101));
                return BackstageUtil.returnError(101);
            }
            if (StringUtils.isBlank(userId)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_USER_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(102)), 102));
                return BackstageUtil.returnError(102);
            }
            if (!YxUtil.isMatched(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_YX_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(114)), 114));
                return BackstageUtil.returnError(114);
            }
            final List<Player> players = this.playerDao.getPlayerNoDeleteByUserIdAndYx(userId, yx);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("players");
            for (final Player player : players) {
                doc.startObject();
                doc.createElement("playerId", player.getPlayerId());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("forceId", player.getForceId());
                doc.createElement("playerLv", player.getPlayerLv());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_success_SUCCESS", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte())), 1));
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            OfficialWebsiteService.errorLog.info("playerInfo_fail_EXCEPTION", e);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_EXCEPTION", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(212)), 212));
            return BackstageUtil.returnError(212);
        }
    }
    
    @Override
    public byte[] rankList(final String yx, final int forceId, final Request request) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_fail_YX_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(101)), 101));
                return BackstageUtil.returnError(101);
            }
            if (forceId < 1 || forceId > 3) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_fail_FORCE_ID_IS_EMPTY", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(111)), 111));
                return BackstageUtil.returnError(111);
            }
            if (!YxUtil.isMatched(yx)) {
                OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_fail_YX_NOT_MATCH", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(114)), 114));
                return BackstageUtil.returnError(114);
            }
            final List<Integer> playerIdList = this.rankService.getForceLevelRankList(forceId, 1, 10);
            Player player = null;
            int rank = 1;
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("ranks");
            for (final int playerId : playerIdList) {
                doc.startObject();
                player = this.playerDao.read(playerId);
                doc.createElement("rank", (rank++));
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("playerLv", player.getPlayerLv());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_success_SUCCESS", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte())), 1));
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            OfficialWebsiteService.errorLog.info("rankList_fail_EXCEPTION", e);
            OfficialWebsiteService.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_fail_EXCEPTION", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(212)), 212));
            return BackstageUtil.returnError(212);
        }
    }
}
