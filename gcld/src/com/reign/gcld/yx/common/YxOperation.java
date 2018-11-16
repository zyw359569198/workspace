package com.reign.gcld.yx.common;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.chat.dao.*;
import com.reign.gcld.user.dao.*;
import com.reign.gcld.notice.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import com.reign.gcld.pay.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.chat.domain.*;
import com.reign.gcld.user.domain.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.notice.domain.*;
import com.reign.gcld.common.*;
import org.apache.commons.lang.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.*;
import java.io.*;
import java.util.*;
import java.net.*;
import com.reign.plugin.yx.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.plugin.yx.common.*;

@Component("yxOperation")
public class YxOperation implements IYxOperation
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPayService payService;
    @Autowired
    private IPlayerPayDao playerPayDao;
    @Autowired
    private ISilenceDao silenceDao;
    @Autowired
    private IUserBlockDao userBlockDao;
    @Autowired
    private ISystemNoticeDao systemNoticeDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerTaobaoDao playerTaobaoDao;
    @Autowired
    private IActivityService activityService;
    private static final Logger errorLog;
    private static final Log opReport;
    private int TEN_YEARS_HOURS;
    private static Map<String, YxTencentPayInfo> tencentPayMap;
    public static final String yxPengyou = "pengyou";
    public static final String yxQzone = "qzone";
    public static final String yx3366 = "3366";
    private static final int WXIN_REWARD_NUMBER = 20000;
    
    static {
        errorLog = new ErrorLogger();
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        YxOperation.tencentPayMap = new ConcurrentHashMap<String, YxTencentPayInfo>();
    }
    
    public YxOperation() {
        this.TEN_YEARS_HOURS = 87600;
    }
    
    @Override
	public Session login(final String yx, final String userId, final String userName, final String sfid, final String adult, final String yxSource, final Request request) {
        final UserDto dto = new UserDto();
        dto.userId = userId;
        dto.userName = userName;
        dto.loginTime = System.currentTimeMillis();
        dto.yx = yx;
        dto.setYxSource(yxSource);
        dto.firstLogin = (this.playerDao.getRoleCount(userId, yx) <= 0);
        if (WebUtil.needAntiAddiction(yx) && "0".equals(adult)) {
            dto.setNeedAntiAddiction(true);
        }
        else {
            dto.setNeedAntiAddiction(false);
        }
        final Session session = request.getNewSession();
        session.setAttribute("user", dto);
        return session;
    }
    
    @Override
	public Session loginForTencent(final String yx, final String adult, final String yxSource, final YxTencentUserInfo ytuInfo, final Request request) {
        final UserDto dto = new UserDto();
        dto.userId = ytuInfo.getUserId();
        dto.userName = ytuInfo.getUserName();
        dto.loginTime = System.currentTimeMillis();
        dto.yx = yx;
        dto.setYxSource(yxSource);
        dto.setOpenId(ytuInfo.getOpenId());
        dto.setOpenKey(ytuInfo.getOpenKey());
        dto.setIsYellowVip(ytuInfo.getIsYellowVip());
        dto.setYellowVipLevel(ytuInfo.getYellowVipLevel());
        dto.setIsYellowHighVip(ytuInfo.getIsYellowHighVip());
        dto.setIsYellowYearVip(ytuInfo.getIsYellowYearVip());
        dto.setPf(ytuInfo.getPf());
        dto.setPfKey(ytuInfo.getPfKey());
        dto.setUserIp(ytuInfo.getUserIp());
        dto.firstLogin = (this.playerDao.getRoleCountByUid(dto.userId) <= 0);
        if (WebUtil.needAntiAddiction(yx) && "0".equals(adult)) {
            dto.setNeedAntiAddiction(true);
        }
        else {
            dto.setNeedAntiAddiction(false);
        }
        final Session session = request.getNewSession();
        session.setAttribute("user", dto);
        return session;
    }
    
    @Override
	public Tuple<Integer, Integer> pay(final String orderId, int playerId, final String userId, String yx, final int gold, final Request request) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        if (playerId <= 0) {
            playerId = this.getDefaultPayPlayer(userId, yx);
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            tuple.left = 2;
            tuple.right = 2;
            return tuple;
        }
        if (this.checkTencentPf(yx) && !player.getYx().equals(yx)) {
            yx = player.getYx();
        }
        if (!player.getUserId().equalsIgnoreCase(userId) || !player.getYx().equals(yx)) {
            tuple.left = 3;
            tuple.right = 3;
            return tuple;
        }
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            if (this.playerPayDao.containsOrderId(orderId, yx)) {
                tuple.left = 5;
                tuple.right = 5;
                return tuple;
            }
            if (!this.payService.commonPay(orderId, gold, userId, yx, 0, "\u5145\u503c\u83b7\u5f97\u91d1\u5e01", player, new Date(), player.getYxSource(), request)) {
                tuple.left = 1000;
                tuple.right = 1000;
                return tuple;
            }
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        try {
            this.payService.addAdditionalGold(playerId, gold);
        }
        catch (Exception e) {
            YxOperation.errorLog.error("#pay_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#");
            YxOperation.errorLog.error("#pay_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#", e);
        }
        try {
            this.payService.addTicketGold(playerId, gold);
        }
        catch (Exception e) {
            YxOperation.errorLog.error("#pay_ticket_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#");
            YxOperation.errorLog.error("#pay_ticket_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#", e);
        }
        tuple.left = 1;
        tuple.right = 1;
        return tuple;
    }
    
    @Override
	public List<YxPlayerInfo> queryPlayer(final String userId, final String yx) {
        final List<Player> players = this.playerDao.getPlayerNoDeleteByUserIdAndYx(userId, yx);
        final List<YxPlayerInfo> playerList = new ArrayList<YxPlayerInfo>();
        for (final Player player : players) {
            final YxPlayerInfo ypi = new YxPlayerInfo(player.getPlayerId(), player.getPlayerName(), player.getState(), player.getPlayerLv(), player.getForceId(), userId, player.getCreateTime());
            playerList.add(ypi);
        }
        return playerList;
    }
    
    @Override
	public YxPlayerInfo queryPlayer(final String userId, final String yx, final String playerName) {
        final Player player = this.playerDao.queryPlayer(userId, yx, playerName);
        if (player == null) {
            return null;
        }
        final YxPlayerInfo ypi = new YxPlayerInfo(player.getPlayerId(), player.getPlayerName(), player.getState(), player.getPlayerLv(), player.getForceId(), userId, player.getCreateTime());
        return ypi;
    }
    
    @Override
	public YxPlayerInfo queryPlayerByPlayerNameAndYx(final String playerName, final String yx) {
        final Player player = this.playerDao.getPlayerByNameAndYx(playerName, yx);
        if (player == null) {
            return null;
        }
        final YxPlayerInfo ypi = new YxPlayerInfo(player.getPlayerId(), player.getPlayerName(), player.getState(), player.getPlayerLv(), player.getForceId(), player.getUserId(), player.getCreateTime());
        return ypi;
    }
    
    @Override
	public List<YxPlayerPayInfo> queryPlayerPayDetails(final String yx, final String userId, final int playerId) {
        final List<YxPlayerPayInfo> playerPayList = new ArrayList<YxPlayerPayInfo>();
        final List<PlayerPay> payList = this.playerPayDao.getPlayerPayByPlayerId(playerId);
        for (final PlayerPay p : payList) {
            if (p.getYx().equals(yx) && p.getUserId().equals(userId)) {
                final YxPlayerPayInfo ppi = new YxPlayerPayInfo(userId, p.getOrderId(), p.getGold(), p.getType(), p.getCreateTime(), p.getPlayerId());
                playerPayList.add(ppi);
            }
        }
        return playerPayList;
    }
    
    @Override
	public YxUserInfo getYxUserInfo(final Request request) {
        final Object obj = request.getSession().getAttribute("user");
        if (obj == null || !(obj instanceof UserDto)) {
            return null;
        }
        final UserDto dto = (UserDto)obj;
        return new YxUserInfo(dto.userId, dto.userName);
    }
    
    @Override
	public YxPlayerInfo getYxPlayerInfo(final Request request) {
        final PlayerDto dto = Players.getSession(request.getSession().getId());
        if (dto == null) {
            return null;
        }
        return new YxPlayerInfo(dto.playerId, dto.playerName, 0, dto.playerLv, dto.forceId, dto.userId, dto.createTime);
    }
    
    @Override
	public int getDefaultPayPlayer(final String userId, final String yx) {
        return this.playerDao.getDefaultPlayerId(userId, yx);
    }
    
    @Override
	public YxPlayerInfo getDefaultPayPlayer(final Request request) {
        final Object obj = request.getSession().getAttribute("user");
        if (obj == null || !(obj instanceof UserDto)) {
            return null;
        }
        final UserDto dto = (UserDto)obj;
        final int playerId = this.getDefaultPayPlayer(dto.userId, dto.yx);
        if (playerId <= 0) {
            return null;
        }
        final Player player = this.playerDao.read(playerId);
        return new YxPlayerInfo(playerId, player.getPlayerName(), player.getState(), player.getPlayerLv(), player.getForceId(), player.getUserId(), player.getCreateTime());
    }
    
    @Override
	public List<YxPlayerPayInfo> queryYxPayDetails(final String yx, final Date startTime, final Date endTime) {
        final List<YxPlayerPayInfo> playerPayList = new ArrayList<YxPlayerPayInfo>();
        final List<PlayerPay> payList = this.playerPayDao.getPlayerPayByDateAndYx(startTime, endTime, yx);
        for (final PlayerPay p : payList) {
            final YxPlayerPayInfo ppi = new YxPlayerPayInfo(p.getUserId(), p.getOrderId(), p.getGold(), p.getType(), p.getCreateTime(), p.getPlayerId());
            playerPayList.add(ppi);
        }
        return playerPayList;
    }
    
    @Override
	public List<YxSourceInfo> queryPlayerYxSource(final String yx, final Date date, final int page, final int size) {
        final List<Player> playerList = this.playerDao.getPlayerList(yx, date, page, size);
        if (playerList == null || playerList.size() <= 0) {
            return new ArrayList<YxSourceInfo>();
        }
        final List<YxSourceInfo> yxSourceInfoList = new ArrayList<YxSourceInfo>();
        for (final Player player : playerList) {
            yxSourceInfoList.add(new YxSourceInfo(player.getUserId(), player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getLoginTime()));
        }
        return yxSourceInfoList;
    }
    
    @Override
	public int queryPlayerYxSourceSize(final String yx, final Date date) {
        return this.playerDao.getSizeByYxAndDate(yx, date);
    }
    
    @Override
	public int getOnlinePlayersNumber(final String yx) {
        return Players.getOnlinePlayersNumber(yx);
    }
    
    @Override
	public YxPlayerInfo getPlayerById(final int playerId) {
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            return null;
        }
        return new YxPlayerInfo(playerId, player.getPlayerName(), player.getState(), player.getPlayerLv(), player.getForceId(), player.getUserId(), player.getCreateTime());
    }
    
    @Override
	public int getDailyOnlineTime(final int playerId) {
        int result = this.playerDao.getDailyOnlineTime(playerId);
        final PlayerDto dto = Players.getPlayer(playerId);
        if (dto != null) {
            result += (int)((System.currentTimeMillis() - dto.loginTime) / 1000L);
        }
        return result;
    }
    
    @Override
	public YxPlayerPayInfo queryOrder(final String orderId, final String yx) {
        final PlayerPay p = this.playerPayDao.queryOrder(orderId, yx);
        if (p == null) {
            return null;
        }
        return new YxPlayerPayInfo(p.getUserId(), p.getOrderId(), p.getGold(), p.getType(), p.getCreateTime(), p.getPlayerId());
    }
    
    @Override
	public List<YxPlayerPayInfo> queryOrderByDateAndPage(final Date startTime, final Date endTime, final int page, final int size, final String yx) {
        final List<PlayerPay> ppList = this.playerPayDao.getPlayerPayByDateAndPage(startTime, endTime, (page - 1) * size, size, yx);
        if (ppList == null) {
            return null;
        }
        final List<YxPlayerPayInfo> ppiList = new ArrayList<YxPlayerPayInfo>();
        for (final PlayerPay p : ppList) {
            ppiList.add(new YxPlayerPayInfo(p.getUserId(), p.getOrderId(), p.getGold(), p.getType(), p.getCreateTime(), p.getPlayerId()));
        }
        return ppiList;
    }
    
    @Override
	public boolean banChat(final String userIds, final int keepTime, final String yx) {
        boolean result = false;
        final String[] userIdArr = userIds.split(",");
        final Date nextSayTime = TimeUtil.nowAddMinutes(keepTime);
        final Date nowDate = new Date();
        String[] array;
        for (int length = (array = userIdArr).length, i = 0; i < length; ++i) {
            final String userId = array[i];
            final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
            for (final Player player : playerList) {
                final int playerId = player.getPlayerId();
                Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence != null) {
                    silence.setNextSayTime(nextSayTime);
                    silence.setSilenceTime(nowDate);
                    silence.setReason("");
                    silence.setType(1);
                    this.silenceDao.update(silence);
                }
                else {
                    silence = new Silence();
                    silence.setNextSayTime(nextSayTime);
                    silence.setPlayerId(playerId);
                    silence.setReason("");
                    silence.setSilenceTime(nowDate);
                    silence.setUserId(player.getUserId());
                    silence.setYx(yx);
                    silence.setType(1);
                    this.silenceDao.create(silence);
                }
                result = true;
            }
        }
        return result;
    }
    
    @Override
	public boolean unbanChat(final String userIds, final String yx) {
        boolean result = false;
        final String[] userIdArr = userIds.split(",");
        final Date nowDate = new Date();
        String[] array;
        for (int length = (array = userIdArr).length, i = 0; i < length; ++i) {
            final String userId = array[i];
            final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
            for (final Player player : playerList) {
                final int playerId = player.getPlayerId();
                final Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence.getType() == 2) {
                    continue;
                }
                silence.setNextSayTime(nowDate);
                this.silenceDao.update(silence);
                result = true;
            }
        }
        return result;
    }
    
    @Override
	public boolean banUser(final String userIds, final String yx) {
        boolean result = false;
        final Date endDate = TimeUtil.nowAddHours(this.TEN_YEARS_HOURS);
        final String[] userIdArr = userIds.split(",");
        String[] array;
        for (int length = (array = userIdArr).length, i = 0; i < length; ++i) {
            final String userId = array[i];
            final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
            for (final Player player : playerList) {
                final List<UserBlock> ubList = this.userBlockDao.getUserBlock(player.getUserId(), yx);
                if (ubList != null && ubList.size() > 0) {
                    for (final UserBlock ub : ubList) {
                        this.userBlockDao.update(ub.getVId(), "", endDate);
                    }
                }
                else {
                    final UserBlock ub = new UserBlock();
                    ub.setBlockEndTime(endDate);
                    ub.setPlayerId(player.getPlayerId());
                    ub.setUserId(player.getUserId());
                    ub.setYx(yx);
                    ub.setReason("");
                    this.userBlockDao.create(ub);
                }
                result = true;
                AuthInterceptor.blockPlayer(player.getUserId(), yx, endDate.getTime(), "");
            }
        }
        return result;
    }
    
    @Override
	public boolean unbanUser(final String userIds, final String yx) {
        boolean result = false;
        final String[] userIdArr = userIds.split(",");
        String[] array;
        for (int length = (array = userIdArr).length, i = 0; i < length; ++i) {
            final String userId = array[i];
            final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
            for (final Player player : playerList) {
                final List<UserBlock> ubList = this.userBlockDao.getUserBlock(player.getUserId(), yx);
                if (ubList != null) {
                    for (final UserBlock ub : ubList) {
                        if (ub.getPlayerId() == player.getPlayerId()) {
                            this.userBlockDao.deleteById(ub.getVId());
                            AuthInterceptor.unblockPlayer(player.getUserId(), yx);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    @Override
	public List<YxNoticeInfo> queryNotice(final String yx) {
        final List<SystemNotice> list = this.systemNoticeDao.getModels();
        final List<YxNoticeInfo> ynList = new ArrayList<YxNoticeInfo>();
        final Date now = new Date();
        for (final SystemNotice sn : list) {
            if (sn.getYx().equalsIgnoreCase(yx) && sn.getStartTime().before(now) && sn.getExpireTime().after(now)) {
                ynList.add(new YxNoticeInfo(sn.getContent()));
            }
        }
        return ynList;
    }
    
    @Override
	public Tuple<Boolean, Integer> modifyUserId(final String yx, final String oldUserId, final String newUserId) {
        final Tuple<Boolean, Integer> tuple = new Tuple();
        tuple.left = false;
        final List<Player> oldList = this.playerDao.getPlayerByUserId(oldUserId, yx);
        if (oldList.size() < 1) {
            tuple.right = 1011;
            return tuple;
        }
        this.playerDao.modifyUserId(yx, oldUserId, newUserId);
        for (final Player player : oldList) {
            final int playerId = player.getPlayerId();
            final PlayerDto dto = Players.playerMap.get(playerId);
            if (dto != null) {
                dto.userId = newUserId;
            }
            for (final PlayerDto playerDto : Players.sessionPlayerMap.values()) {
                if (playerDto.playerId == playerId) {
                    playerDto.userId = newUserId;
                }
            }
        }
        final String oldId = String.valueOf(oldUserId) + "-" + yx;
        final String newId = String.valueOf(newUserId) + "-" + yx;
        final UserDto userDto1 = Users.userMap.remove(oldId);
        if (userDto1 != null) {
            userDto1.userId = newUserId;
            userDto1.setId(newId);
            Users.userMap.put(newId, userDto1);
        }
        final Session session = Users.userSessionMap.remove(oldId);
        if (session != null) {
            Users.userSessionMap.put(newId, session);
        }
        final UserDto userDto2 = Users.sessionUserMap.remove(oldId);
        if (userDto2 != null) {
            userDto2.userId = newUserId;
            userDto2.setId(newId);
            Users.sessionUserMap.put(newId, userDto2);
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
	public boolean checkEmptyParams(final Map<String, String> map, final Request request, final String yx) {
        final long start = System.currentTimeMillis();
        for (final String key : map.keySet()) {
            final String value = map.get(key);
            if (StringUtils.isBlank(value)) {
                YxOperation.opReport.info((Object)OpLogUtil.formatOpInterfaceLog("yx" + yx, "yx" + yx + "_fail_" + key + "_IS_EMPTY", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1100)), 1100));
                return true;
            }
        }
        return false;
    }
    
    @Override
	public String exeTencentQuery(final String yx, final Map<String, Object> paramMap, final String baseUrl, final String uri, final String method, final boolean isHttps, final boolean isCallBack) {
        YxOperation.opReport.error("#start executing.....");
        final String appKey = PluginContext.configuration.getTencentAppKey(yx);
        final String sig = this.getOpenAPISign(uri, paramMap, appKey, method, isCallBack);
        paramMap.put("sig", sig);
        YxOperation.opReport.error("#sign:" + sig);
        String echo = "";
        try {
            String requestURL = WebUtils.getURL(String.valueOf(baseUrl) + uri, paramMap);
            requestURL = requestURL.replace("*", "%2A");
            YxOperation.opReport.error("query URL:" + requestURL);
            if (isHttps) {
                echo = WebUtils.sendSSLGetRequest(requestURL, paramMap);
            }
            else {
                echo = WebUtils.sendRequest(requestURL, paramMap);
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            YxOperation.opReport.error("#YxOperation#exeTencentQuery#UnsupportedEncodingException:" + echo);
        }
        YxOperation.opReport.error("echo:" + echo);
        return echo;
    }
    
    @Override
	public String getOpenAPISign(final String uri, final Map<String, Object> paramMap, final String appkey, String method, final boolean isCallBack) {
        final List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);
        final StringBuffer sb = new StringBuffer();
        for (final String key : keyList) {
            if (key.equals("sig")) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key);
            sb.append("=");
            if (isCallBack && key.equals("billno")) {
                final String value = paramMap.get(key).toString();
                char[] charArray;
                for (int length = (charArray = value.toCharArray()).length, i = 0; i < length; ++i) {
                    final int asciiIntForV;
                    final char v = (char)(asciiIntForV = charArray[i]);
                    if ((asciiIntForV >= 48 && asciiIntForV <= 57) || asciiIntForV == 33 || (asciiIntForV >= 40 && asciiIntForV <= 42) || (asciiIntForV >= 65 && asciiIntForV <= 90) || (asciiIntForV >= 97 && asciiIntForV <= 122)) {
                        sb.append(v);
                    }
                    else {
                        final String asciiForV = Integer.toHexString(asciiIntForV).toUpperCase();
                        sb.append("%");
                        sb.append(asciiForV);
                    }
                }
            }
            else {
                sb.append(paramMap.get(key));
            }
        }
        if (method == null || method.isEmpty()) {
            method = "POST";
        }
        String result = String.valueOf(method) + "&";
        try {
            result = String.valueOf(result) + URLEncoder.encode(uri, "UTF-8") + "&" + URLEncoder.encode(sb.toString(), "UTF-8");
            YxOperation.opReport.error("source:" + result);
            result = result.replace("*", "%2A");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return Coder.encryptBASE64(HMAC_SHA1.getHmacSHA1(result, String.valueOf(appkey) + "&")).trim();
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
    
    @Override
	public void setTencentPayMap(final Map<String, YxTencentPayInfo> tencentPayMap) {
        YxOperation.tencentPayMap = tencentPayMap;
    }
    
    @Override
	public Map<String, YxTencentPayInfo> getTencentPayMap() {
        return YxOperation.tencentPayMap;
    }
    
    @Override
	public void removeTencentPayMap(final String key) {
        YxOperation.tencentPayMap.remove(key);
    }
    
    @Override
	public boolean checkTencentPf(String pf) {
        pf = pf.replace("_m", "");
        return pf.equals("pengyou") || pf.equals("qzone") || pf.equals("3366");
    }
    
    @Override
	public Tuple<Integer, Integer> rewardWX(int playerId, final String userId, final String yx) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        if (playerId <= 0) {
            playerId = this.getDefaultPayPlayer(userId, yx);
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            tuple.left = 2;
            tuple.right = 2;
            return tuple;
        }
        if (!player.getUserId().equalsIgnoreCase(userId) || !player.getYx().equals(yx)) {
            tuple.left = 3;
            tuple.right = 3;
            return tuple;
        }
        Label_0267: {
            try {
                Constants.locks[playerId % Constants.LOCKS_LEN].lock();
                PlayerTaobao playerTaobao = this.playerTaobaoDao.read(player.getPlayerId());
                if (playerTaobao != null) {
                    tuple.left = 5;
                    tuple.right = 5;
                    return tuple;
                }
                playerTaobao = new PlayerTaobao();
                playerTaobao.setPlayerId(player.getPlayerId());
                this.playerTaobaoDao.create(playerTaobao);
            }
            catch (Exception e) {
                YxOperation.errorLog.error(this, e);
                break Label_0267;
            }
            finally {
                Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
            }
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        this.playerResourceDao.addCopperIgnoreMax(playerId, 20000.0, "\u65fa\u4fe1\u5927\u793c\u5305\u83b7\u53d6\u94f6\u5e01", false);
        this.playerResourceDao.addFoodIgnoreMax(playerId, 20000.0, "\u65fa\u4fe1\u5927\u793c\u5305\u83b7\u53d6\u7cae\u98df");
        this.playerResourceDao.addWoodIgnoreMax(playerId, 20000.0, "\u65fa\u4fe1\u5927\u793c\u5305\u83b7\u53d6\u6728\u6750", false);
        tuple.left = 1;
        tuple.right = 1;
        return tuple;
    }
    
    @Override
	public int getAllPlayerNumber() {
        return this.playerDao.getModelSize();
    }
    
    @Override
	public List<PingAnPlayerInfo> queryPlayerInfo(final String customer, final String yx, final String gatewayId) {
        final List<Player> playerList = this.playerDao.getPlayerNoDeleteByUserIdAndYx(customer, yx);
        final List<PingAnPlayerInfo> result = new ArrayList<PingAnPlayerInfo>();
        PingAnPlayerInfo info = null;
        for (final Player cell : playerList) {
            info = new PingAnPlayerInfo();
            info.roleName = cell.getPlayerName();
            info.roleLever = new StringBuilder().append(cell.getPlayerLv()).toString();
            info.roleServer = gatewayId;
            info.roleCoin = new StringBuilder().append(cell.getSysGold()).toString();
            result.add(info);
        }
        return result;
    }
    
    @Override
	public PingAnMoneyInfo queryMoneyInfo(final String customer, final String yx) {
        final List<Player> players = this.playerDao.getPlayerByUserId(customer, yx);
        int sum = 0;
        for (final Player player : players) {
            sum += player.getSysGold();
        }
        final PingAnMoneyInfo info = new PingAnMoneyInfo();
        info.avaliableGameAmount = String.valueOf(sum);
        return info;
    }
    
    @Override
	public int test360Privilege(final String uId, final int level) {
        return this.activityService.recv360PrivilegeForTest(uId, level);
    }
}
