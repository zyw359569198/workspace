package com.reign.gcld.kfgz.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.kfgz.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.player.common.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.context.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.log.*;
import com.reign.gcld.kfgz.*;
import com.reign.kf.comm.transfer.*;
import com.reign.gcld.player.dto.*;
import com.reign.kfgz.constants.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.general.domain.*;
import org.springframework.beans.*;
import com.reign.framework.json.*;
import com.reign.gcld.kfgz.domain.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.util.*;
import com.reign.gcld.common.*;
import com.reign.kfgz.dto.response.*;
import java.util.*;
import com.reign.kfgz.dto.request.*;
import com.reign.util.*;

@Component
public class KfgzMatchService implements Runnable, IKfgzMatchService, InitializingBean, ApplicationContextAware
{
    private static Log logger;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IKfgzSignupDao kfgzSignupDao;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    @Autowired
    private IKfgzPlayerRewardDao kfgzPlayerRewardDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IResourceUpdateSynService resourceUpdateSynService;
    private static final int matchSynSocketCount = 11;
    private static KfConnection[][] connections;
    private IKfgzMatchService self;
    ApplicationContext context;
    private static Thread singleThread;
    private static final long sleepTime = 2000L;
    private static Map<Integer, KfgzSyncResDto> playerResourceCacheMap;
    private static int cacheMapRefreshCount;
    private static boolean mark;
    private KfgzNationResInfo[] kfgzNationResInfo;
    private boolean[] kfgzNationResInfoMark;
    private static Map<Integer, Long> sourceVersion;
    private static final int lockNum = 50;
    private static ReentrantLock[] versionLock;
    public static final String SPLIT = ":";
    public static final int maxRewardTimes = 4;
    @Autowired
    private EventHandler eventHandler;
    private static final Logger dayReportLogger;
    
    static {
        KfgzMatchService.logger = new KfgzLogger();
        KfgzMatchService.connections = new KfConnection[4][11];
        KfgzMatchService.singleThread = null;
        KfgzMatchService.playerResourceCacheMap = new ConcurrentHashMap<Integer, KfgzSyncResDto>();
        KfgzMatchService.cacheMapRefreshCount = 0;
        KfgzMatchService.mark = true;
        KfgzMatchService.sourceVersion = new ConcurrentHashMap<Integer, Long>();
        KfgzMatchService.versionLock = new ReentrantLock[50];
        for (int i = 0; i < 50; ++i) {
            KfgzMatchService.versionLock[i] = new ReentrantLock();
        }
        dayReportLogger = new DayReportLogger();
    }
    
    public KfgzMatchService() {
        this.kfgzNationResInfo = new KfgzNationResInfo[4];
        this.kfgzNationResInfoMark = new boolean[4];
    }
    
    @Override
    public void stopMatchService() {
        KfgzMatchService.mark = false;
        KfgzMatchService.singleThread = null;
    }
    
    public void setConnection(final String addressPort, final int nation) {
        final String address = addressPort.split(":")[0];
        final int port = Integer.valueOf(addressPort.split(":")[1]);
        for (int i = 0; i < 11; ++i) {
            KfgzMatchService.connections[nation][i] = new KfConnection((TransferConfig)new TransferConfigMatch(address, port), KfgzMatchService.logger, "kfgz_match_send_thread_new_" + nation);
        }
    }
    
    private long getVersion(final int playerId) {
        if (KfgzMatchService.sourceVersion.containsKey(playerId)) {
            return KfgzMatchService.sourceVersion.get(playerId);
        }
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        return pr.getKfgzVersion();
    }
    
    private boolean isNewVersion(final int playerId, final long versionFrom, final long versionTo) {
        if (this.getVersion(playerId) != versionFrom) {
            return false;
        }
        if (versionTo <= versionFrom) {
            return false;
        }
        KfgzMatchService.versionLock[playerId % 50].lock();
        try {
            if (!KfgzMatchService.sourceVersion.containsKey(playerId)) {
                if (this.getVersion(playerId) == versionFrom) {
                    return true;
                }
            }
            else if (versionFrom == KfgzMatchService.sourceVersion.get(playerId)) {
                return true;
            }
            return false;
        }
        finally {
            KfgzMatchService.versionLock[playerId % 50].unlock();
        }
    }
    
    private static void removeSourceVersionByPlayerId(final int playerId) {
        if (!KfgzMatchService.sourceVersion.containsKey(playerId)) {
            return;
        }
        KfgzMatchService.versionLock[playerId % 50].lock();
        try {
            if (!KfgzMatchService.sourceVersion.containsKey(playerId)) {
                return;
            }
            KfgzMatchService.sourceVersion.remove(playerId);
        }
        finally {
            KfgzMatchService.versionLock[playerId % 50].unlock();
        }
        KfgzMatchService.versionLock[playerId % 50].unlock();
    }
    
    public static void freshSourceVersion(final int playerId, final long version) {
        KfgzMatchService.versionLock[playerId % 50].lock();
        try {
            KfgzMatchService.sourceVersion.put(playerId, version);
        }
        finally {
            KfgzMatchService.versionLock[playerId % 50].unlock();
        }
        KfgzMatchService.versionLock[playerId % 50].unlock();
    }
    
    @Override
    public void init(final String address, final int nation) {
        KfgzMatchService.logger.info("matchService init start...");
        this.setConnection(address, nation);
        this.doInit();
        KfgzManager.getSignMap(nation).clear();
        KfgzMatchService.mark = true;
        if (KfgzMatchService.singleThread == null) {
            (KfgzMatchService.singleThread = new Thread(this)).start();
        }
        KfgzMatchService.logger.info("matchService init end");
    }
    
    @Override
    public byte[] signUp(final PlayerDto playerDto) {
        if (this.kfgzSeasonService.getMatchState() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_8);
        }
        final KfgzSignResult gzSignup1 = KfgzManager.getKfgzPlayerInfo(playerDto.playerId, playerDto.forceId);
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final KfgzSignResult gzSignup2 = this.self.doSignUp(player, this.getDefaultGIds(playerDto.playerId));
        if (gzSignup2 != null && gzSignup2.getMessages() != null && gzSignup2.getMessages().length > 0) {
            doc.startArray("messages");
            String[] messages;
            for (int length = (messages = gzSignup2.getMessages()).length, i = 0; i < length; ++i) {
                final String s = messages[i];
                doc.createElement(s);
            }
            doc.endArray();
        }
        if (gzSignup1 != null || gzSignup2 != null) {
            final KfgzSignResult gzSignup3 = (gzSignup1 == null) ? gzSignup2 : gzSignup1;
            if (this.kfgzSeasonService.getMatchState() == 2) {
                doc.createElement("playerId", gzSignup3.getPlayerId());
                doc.createElement("competitorId", gzSignup3.getCompetitor());
                doc.createElement("forceId", gzSignup3.getForceId());
                final String[] addr = this.kfgzSeasonService.getMatchAddressAndPortByNation(playerDto.forceId);
                if (addr != null) {
                    doc.createElement("matchaddress", addr[0]);
                    doc.createElement("matchport", addr[1]);
                    doc.createElement("kfgzKey", KfgzCommConstants.getKfgzKey(gzSignup3.getCompetitor(), this.kfgzSeasonService.getSeasonId()));
                }
                doc.createElement("battle", gzSignup3.getState() != 5);
            }
            doc.createElement("worldId", gzSignup3.getWorldId());
            doc.createElement("matchState", this.kfgzSeasonService.getMatchStateForQianduan());
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (this.kfgzSeasonService.isAfterLastBattle()) {
            doc.createElement("matchState", 4);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (this.kfgzSeasonService.getMatchStateForQianduan() == 3) {
            doc.createElement("matchState", 3);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        doc.createElement("matchState", 5);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getRewardBoard(final PlayerDto player) {
        final KfgzNationResInfo knri = this.kfgzNationResInfo[player.forceId];
        if (knri == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        final KfgzSignResult gzSignup = KfgzManager.getKfgzPlayerInfo(player.playerId, player.forceId);
        int cId = 0;
        if (gzSignup != null) {
            cId = gzSignup.getCompetitor();
        }
        else {
            cId = this.kfgzSignupDao.read(player.playerId).getCompetitorId();
        }
        final KfgzPlayerReward kpr = this.kfgzPlayerRewardDao.getKfgzPlayerReward(knri.getSeasonId(), knri.getGzId(), cId);
        if (kpr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        final JsonDocument doc = new JsonDocument();
        doc.createElement("isFinal", this.kfgzSeasonService.isFinalRound(player.forceId));
        doc.createElement("serverName1", knri.getServerName1());
        doc.createElement("nation1", knri.getNation1());
        doc.createElement("serverName2", knri.getServerName2());
        doc.createElement("nation2", knri.getNation2());
        doc.createElement("cityNum1", knri.getCityNum1());
        doc.createElement("cityNum2", knri.getCityNum2());
        doc.createElement("isWin", knri.isWin());
        doc.createElement("layer", KfgzCommConstants.getLayerByGzID(knri.getGzId()));
        doc.createElement("group", KfgzCommConstants.getGIdByGzID(knri.getGzId()));
        doc.createElement("round", KfgzCommConstants.getRoundByGzId(knri.getGzId()));
        KfgzPlayerResultInfo kpri = null;
        for (final KfgzPlayerResultInfo k : knri.getpList()) {
            if (k.getcId() == cId) {
                kpri = k;
                break;
            }
        }
        if (kpri != null) {
            doc.createElement("killArmy", kpri.getKillArmy());
            doc.createElement("occupyCity", kpri.getOccupyCity());
            doc.createElement("soloWinNum", kpri.getSoloWinNum());
        }
        doc.createElement("reward", kpr.getReward());
        doc.createElement("rewardTimes", kpr.getRewardTimes());
        doc.createElement("maxTimes", 4);
        doc.createElement("playerKillRank", kpri.getKillRank());
        final int xs = getXsByRewardTimes(kpr.getRewardTimes());
        doc.createElement("xs", xs);
        doc.createElement("gold", getTicketsGold(this.getRewardTickets(kpr.getReward()) * xs, kpr.getRewardTimes()));
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private int getRewardTickets(final String reward) {
        int result = 0;
        String[] split;
        for (int length = (split = reward.split(":")).length, i = 0; i < length; ++i) {
            final String r = split[i];
            result += Integer.valueOf(r);
        }
        return result;
    }
    
    public static int getXsByRewardTimes(final int times) {
        if (times <= 0) {
            return 1;
        }
        return (int)Math.pow(2.0, times - 1);
    }
    
    public static int getTicketsGold(final int tickets, final int times) {
        switch (times) {
            case 0: {
                return 0;
            }
            case 1: {
                return tickets / 100;
            }
            case 2: {
                return tickets / 50;
            }
            case 3: {
                return tickets / 25;
            }
            default: {
                return 0;
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] getReward(final PlayerDto playerDto, final int times) throws Exception {
        final KfgzSignResult gzSignup = KfgzManager.getKfgzPlayerInfo(playerDto.playerId, playerDto.forceId);
        int cId = 0;
        if (gzSignup != null) {
            cId = gzSignup.getCompetitor();
        }
        else {
            cId = this.kfgzSignupDao.read(playerDto.playerId).getCompetitorId();
        }
        final KfgzPlayerReward kpr = this.kfgzPlayerRewardDao.getKfgzPlayerReward(this.kfgzSeasonService.getSeasonId(), this.kfgzSeasonService.getGzIdByNation(playerDto.forceId), cId);
        if (kpr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_9);
        }
        if (kpr.getRewardTimes() != times) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_10);
        }
        if (times >= 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_11);
        }
        final int tickets = this.getRewardTickets(kpr.getReward()) * getXsByRewardTimes(times);
        final int gold = getTicketsGold(tickets, times);
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, gold, LocalMessages.ATTRIBUTEKEY_KFGZ_2)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        if (this.kfgzPlayerRewardDao.addRewardTimes(kpr.getId(), 1) != 1) {
            throw new Exception();
        }
        this.playerTicketsDao.addTickets(playerDto.playerId, tickets, LocalMessages.ATTRIBUTEKEY_TICKETS_1, true);
        final int xs = getXsByRewardTimes(times + 1);
        final int tickets2 = this.getRewardTickets(kpr.getReward()) * xs;
        return this.getResult(times + 1, getTicketsGold(tickets2, times + 1), xs);
    }
    
    private byte[] getResult(final int rewardTimes, final int gold, final int xs) {
        final JsonDocument doc = new JsonDocument();
        doc.createElement("rewardTimes", rewardTimes);
        doc.createElement("xs", xs);
        doc.createElement("gold", gold);
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private String getDefaultGIds(final int playerId) {
        final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        final StringBuilder sb = new StringBuilder();
        for (final PlayerGeneralMilitary pg : list) {
            sb.append(pg.getGeneralId());
            sb.append("#");
        }
        final String gIds = sb.toString();
        return gIds;
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfgzMatchService)this.context.getBean("kfgzMatchService");
    }
    
    @Override
    public KfgzSignResult doSignUp(final Player player, final String gIds) {
        final int playerId = player.getPlayerId();
        final Request request = new Request();
        request.setCommand(Command.KFGZ_SIGN_FROM_GAMESERVER);
        final KfgzSignInfoParam sInfo = new KfgzSignInfoParam();
        final KfgzPlayerInfo playerInfo = new KfgzPlayerInfo();
        playerInfo.setPic(String.valueOf(player.getPic()));
        playerInfo.setPlayerId(player.getPlayerId());
        playerInfo.setPlayerName(player.getPlayerName());
        playerInfo.setNation(player.getForceId());
        playerInfo.setPlayerLevel(player.getPlayerLv());
        final int officerId = this.dataGetter.getPlayerOfficeRelativeDao().getOfficerId(playerId);
        playerInfo.setOfficerId(officerId);
        playerInfo.setServerId(Configuration.getProperty(player.getYx(), "gcld.serverid"));
        playerInfo.setServerName(Configuration.getProperty(player.getYx(), "gcld.showservername"));
        final int liLianTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 16);
        final int gzJinYanTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 40);
        final int buyPhAddExp = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 49);
        final int OccupyCityAddExp = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 50);
        final int tuJinTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 39);
        final int bingzhongTech = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 28);
        playerInfo.setTech16(liLianTech);
        playerInfo.setTech39(tuJinTech);
        playerInfo.setTech40(gzJinYanTech);
        playerInfo.setTech49(buyPhAddExp);
        playerInfo.setTech50(OccupyCityAddExp);
        playerInfo.setTech28(bingzhongTech);
        KfgzSignup kfgzSignup = this.kfgzSignupDao.read(playerId);
        if (kfgzSignup == null) {
            final KfgzSignup cidFromGW = this.kfgzSeasonService.requestCidFromGW(playerId);
            if (cidFromGW == null) {
                return null;
            }
            kfgzSignup = cidFromGW;
        }
        playerInfo.setCompetitorId(kfgzSignup.getCompetitorId());
        sInfo.setPlayerInfo(playerInfo);
        try {
            sInfo.setCampInfo(Types.OBJECT_MAPPER.writeValueAsString((Object)this.dataGetter.getBattleService().getKfwdCampDatas(playerId, gIds)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (sInfo.getCampInfo() == null || sInfo.getCampInfo().length() < 10) {
            return null;
        }
        sInfo.setSeasonId(this.kfgzSeasonService.getSeasonId());
        sInfo.setGzId(this.kfgzSeasonService.getGzIdByNation(player.getForceId()));
        request.setMessage(sInfo);
        final List<Request> requestList = new ArrayList<Request>();
        requestList.add(request);
        Response response = null;
        try {
            response = this.getForcePlayerConnection(player.getForceId(), playerInfo.getCompetitorId()).sendSyncAndGetResponse((List)requestList);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        if (response == null) {
            return null;
        }
        final KfgzSignResult signResult = (KfgzSignResult)response.getMessage();
        if (signResult.getState() == 2 || signResult.getState() == 5) {
            return null;
        }
        if (signResult.getState() == 1 || KfgzManager.getKfgzPlayerInfo(playerId, player.getForceId()) == null) {
            KfgzMatchService.versionLock[playerId % 50].lock();
            try {
                this.playerResourceDao.clearKfgzVersion(playerId, signResult.getVersion());
                freshSourceVersion(playerId, signResult.getVersion());
                KfgzMatchService.logger.info("playerId: " + playerId + " gzId: " + this.kfgzSeasonService.getGzIdByNation(player.getForceId()) + " clear version to " + signResult.getVersion());
            }
            finally {
                KfgzMatchService.versionLock[playerId % 50].unlock();
            }
            KfgzMatchService.versionLock[playerId % 50].unlock();
        }
        KfgzManager.putKfgzPLayerInfo(signResult, player.getForceId());
        return signResult;
    }
    
    private KfConnection getForcePlayerConnection(final int forceId, final int cId) {
        return KfgzMatchService.connections[forceId][cId % 10];
    }
    
    public static void freshPlayerResourceCache(final PlayerResource pr) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(pr.getPlayerId())) {
            final KfgzSyncResDto ksrd = new KfgzSyncResDto();
            ksrd.setMubing(0);
            KfgzMatchService.playerResourceCacheMap.put(pr.getPlayerId(), ksrd);
        }
        final KfgzSyncResDto ksrd = KfgzMatchService.playerResourceCacheMap.get(pr.getPlayerId());
        ksrd.setCopper(pr.getCopper());
        ksrd.setFood(pr.getFood());
        ksrd.setIron(pr.getIron());
        ksrd.setWood(pr.getWood());
    }
    
    public static void freshPlayerResourceCacheGold(final int playerId, final int gold) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(playerId)) {
            final KfgzSyncResDto ksrd = new KfgzSyncResDto();
            KfgzMatchService.playerResourceCacheMap.put(playerId, ksrd);
        }
        final KfgzSyncResDto ksrd = KfgzMatchService.playerResourceCacheMap.get(playerId);
        ksrd.setGold(gold);
    }
    
    public static void freshPlayerResourceCacheMubing(final int playerId, final int mubing) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(playerId)) {
            final KfgzSyncResDto ksrd = new KfgzSyncResDto();
            KfgzMatchService.playerResourceCacheMap.put(playerId, ksrd);
        }
        final KfgzSyncResDto ksrd = KfgzMatchService.playerResourceCacheMap.get(playerId);
        ksrd.setMubing(mubing);
    }
    
    public static void freshPlayerResourceCacheRecruitToken(final int playerId, final int recruitToken) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(playerId)) {
            final KfgzSyncResDto ksrd = new KfgzSyncResDto();
            KfgzMatchService.playerResourceCacheMap.put(playerId, ksrd);
        }
        final KfgzSyncResDto ksrd = KfgzMatchService.playerResourceCacheMap.get(playerId);
        ksrd.setRecruitToken(recruitToken);
    }
    
    public static void freshPlayerResourceCachePhantomCount(final int playerId, final int phantomCount) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(playerId)) {
            final KfgzSyncResDto ksrd = new KfgzSyncResDto();
            KfgzMatchService.playerResourceCacheMap.put(playerId, ksrd);
        }
        final KfgzSyncResDto ksrd = KfgzMatchService.playerResourceCacheMap.get(playerId);
        ksrd.setPhantomCount(phantomCount);
    }
    
    private KfgzSyncResDto getPlayerResourceUseUncorrectCache(final int playerId) {
        if (!KfgzMatchService.playerResourceCacheMap.containsKey(playerId) || KfgzMatchService.cacheMapRefreshCount >= 10) {
            this.playerResourceDao.read(playerId);
            this.playerDao.read(playerId);
            this.buildingOutputCache.getBuildingsOutput(playerId, 5);
            this.playerAttributeDao.getRecruitTokenNum(playerId);
            this.playerBattleAttributeDao.read(playerId);
            KfgzMatchService.cacheMapRefreshCount = 0;
        }
        return KfgzMatchService.playerResourceCacheMap.get(playerId);
    }
    
    private Request fillSyncResourceRequest(final KfgzSignResult kfzbPlayerInfo) {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_SYNDATA_FROM_GAMESERVER);
        final KfgzSyncDataParam param = new KfgzSyncDataParam();
        param.setPlayerId(kfzbPlayerInfo.getPlayerId());
        param.setcId(kfzbPlayerInfo.getCompetitor());
        final KfgzSyncResDto ksrd = this.getPlayerResourceUseUncorrectCache(kfzbPlayerInfo.getPlayerId());
        param.setGold(ksrd.getGold());
        param.setCopper(ksrd.getCopper());
        param.setWood(ksrd.getWood());
        param.setFood(ksrd.getFood());
        param.setIron(ksrd.getIron());
        param.setRecruitToken(ksrd.getRecruitToken());
        param.setMubing(ksrd.getMubing());
        param.setPhantomCount(ksrd.getPhantomCount());
        param.setVersion(this.getVersion(kfzbPlayerInfo.getPlayerId()));
        request.setMessage(param);
        return request;
    }
    
    @Override
    public void doDatabaseForKfgz(final KfgzSyncDataResult syncResult) throws Exception {
        final int result = this.playerResourceDao.clearKfgzVersion(syncResult.getPlayerId(), syncResult.getVersionTo());
        if (result != 1) {
            throw new RuntimeException("set kfversion error");
        }
        freshSourceVersion(syncResult.getPlayerId(), syncResult.getVersionTo());
        try {
            this.playerResourceDao.updateResourceForKfgz(syncResult.getPlayerId(), syncResult.getCopper(), syncResult.getWood(), syncResult.getFood(), syncResult.getIron(), syncResult.getVersionFrom(), syncResult.getVersionTo(), LocalMessages.ATTRIBUTEKEY_KFGZ);
        }
        catch (Exception e) {
            KfgzMatchService.logger.info("doDatabaseForKfgz Resource ERROR ", e);
        }
        if (syncResult.getGold() != 0) {
            try {
                final Player player = this.playerDao.read(syncResult.getPlayerId());
                if (syncResult.getGold() > 0) {
                    this.playerDao.addSysGold(player, syncResult.getGold(), LocalMessages.ATTRIBUTEKEY_KFGZ_1);
                }
                else {
                    final boolean r2 = this.playerDao.consumeGoldForKFGZ(player, -syncResult.getGold());
                    if (!r2) {
                        throw new Exception();
                    }
                }
                this.playerDao.read(syncResult.getPlayerId());
            }
            catch (Exception e) {
                KfgzMatchService.logger.info("doDatabaseForKfgz Gold ERROR ", e);
            }
        }
        if (syncResult.getPhantomCount() != 0) {
            if (syncResult.getPhantomCount() < 0) {
                this.playerBattleAttributeDao.decreaseSomeVip3PhantomCount(syncResult.getPlayerId(), -syncResult.getPhantomCount(), "\u8de8\u670d\u56fd\u6218\u51cf\u5c11\u514d\u8d39\u501f\u5175\u6b21\u6570");
            }
            else {
                this.playerBattleAttributeDao.addVip3PhantomCount(syncResult.getPlayerId(), syncResult.getPhantomCount(), "\u8de8\u670d\u56fd\u6218\u589e\u52a0\u514d\u8d39\u501f\u5175\u6b21\u6570");
            }
            this.playerBattleAttributeDao.read(syncResult.getPlayerId());
        }
        if (syncResult.getRecruitToken() != 0) {
            int rr = 0;
            if (syncResult.getRecruitToken() > 0) {
                rr = this.playerAttributeDao.addRecruitToken(syncResult.getPlayerId(), syncResult.getRecruitToken(), "\u8de8\u670d\u56fd\u6218\u52df\u5175\u589e\u52a0\u52df\u5175\u4ee4");
            }
            else {
                rr = this.playerAttributeDao.updateRecruitToken(syncResult.getPlayerId(), -syncResult.getRecruitToken(), "\u8de8\u670d\u56fd\u6218\u52df\u5175\u6d88\u8017\u52df\u5175\u4ee4");
            }
            if (rr != 1) {
                throw new Exception();
            }
            this.playerAttributeDao.getRecruitTokenNum(syncResult.getPlayerId());
        }
        if (syncResult.getExp() > 0) {
            this.playerService.updateExpAndPlayerLevel(syncResult.getPlayerId(), syncResult.getExp(), LocalMessages.ATTRIBUTEKEY_KFGZ_3);
        }
        if (syncResult.getgExp() != null && syncResult.getgExp().size() > 0) {
            for (final Tuple<Integer, Integer> t : syncResult.getgExp()) {
                this.generalService.updateExpAndGeneralLevel(syncResult.getPlayerId(), (int)t.left, (int)t.right);
            }
        }
        KfgzMatchService.logger.info(syncResult.getPlayerId() + " write into database fromVersion=" + syncResult.getVersionFrom() + " toVersion=" + syncResult.getVersionTo() + ": gold=" + syncResult.getGold() + "copper=" + syncResult.getCopper() + "wood=" + syncResult.getWood() + "food=" + syncResult.getFood() + "iron=" + syncResult.getIron() + "exp=" + syncResult.getExp() + "recruitToken=" + syncResult.getRecruitToken());
    }
    
    private void syncResource(final int nation) {
        KfgzMatchService.logger.info("synResouce");
        final List<List<Request>> allList = new ArrayList<List<Request>>();
        List<Request> list = new ArrayList<Request>();
        if (KfgzManager.getSignMap(nation).isEmpty()) {
            return;
        }
        for (final Map.Entry<Integer, KfgzSignResult> en : KfgzManager.getSignMap(nation).entrySet()) {
            list.add(this.fillSyncResourceRequest(en.getValue()));
            if (list.size() >= 300) {
                allList.add(list);
                list = new ArrayList<Request>();
            }
        }
        allList.add(list);
        KfgzMatchService.logger.info("synResouce allList" + allList.size());
        for (final List<Request> requestList : allList) {
            KfgzMatchService.logger.info("synResouce resnum" + requestList.size());
            if (requestList.size() <= 0) {
                return;
            }
            List<Response> responseList = null;
            try {
                responseList = (List<Response>)KfgzMatchService.connections[nation][10].sendRequestAndGetResponseList((List)requestList);
            }
            catch (Exception e2) {
                KfgzMatchService.logger.info("nation=error1");
            }
            if (responseList == null) {
                return;
            }
            for (final Response response : responseList) {
                final KfgzSyncDataResult syncResult = (KfgzSyncDataResult)response.getMessage();
                if (!this.isNewVersion(syncResult.getPlayerId(), syncResult.getVersionFrom(), syncResult.getVersionTo())) {
                    continue;
                }
                try {
                    this.self.doDatabaseForKfgz(syncResult);
                    for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                        KfgzMatchService.dayReportLogger.info(log);
                    }
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                    this.eventHandler.handle(syncResult.getPlayerId(), new PlayerDto(), PushCommand.PUSH_UPDATE);
                }
                catch (Exception e) {
                    KfgzMatchService.logger.info("synResouce exception", e);
                    removeSourceVersionByPlayerId(syncResult.getPlayerId());
                }
            }
            KfgzMatchService.logger.info("synResouce end");
        }
    }
    
    private void syncMatchInfo(final int nation) {
        final List<Request> requestList = new ArrayList<Request>();
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GET_MATCH_GZINFO_FROM_GAMESERVER);
        final KfgzGzKey kgk = new KfgzGzKey();
        kgk.setSeasonId(this.kfgzSeasonService.getSeasonId());
        kgk.setGzId(this.kfgzSeasonService.getGzIdByNation(nation));
        request.setMessage(kgk);
        requestList.add(request);
        if (requestList.size() <= 0) {
            return;
        }
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfgzMatchService.connections[nation][10].sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            KfgzMatchService.logger.error("connections error=" + nation);
        }
        if (responseList == null) {
            return;
        }
        for (final Response response : responseList) {
            final KfgzBaseInfoRes syncResult = (KfgzBaseInfoRes)response.getMessage();
            if (syncResult != null) {
                if (syncResult.getState() == 2) {
                    this.kfgzNationResInfoMark[nation] = true;
                }
                if (syncResult.getMailQueue() == null) {
                    continue;
                }
                for (final MailDto m : syncResult.getMailQueue()) {
                    this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, m.getTitle(), m.getContent(), 1, m.getPlayerId(), new Date());
                }
            }
        }
    }
    
    private void syncKfgzNationResInfo(final int nation) {
        final List<Request> requestList = new ArrayList<Request>();
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GET_MATCH_GZRESULT_FROM_GAMESERVER);
        final KfgzNationResKey kgk = new KfgzNationResKey();
        kgk.setSeasonId(this.kfgzSeasonService.getSeasonId());
        kgk.setGzId(this.kfgzSeasonService.getGzIdByNation(nation));
        final List<Integer> cIdList = new ArrayList<Integer>();
        int forceId = -1;
        for (final Map.Entry<Integer, KfgzSignResult> en : KfgzManager.getSignMap(nation).entrySet()) {
            cIdList.add(en.getValue().getCompetitor());
            if (forceId < 0) {
                forceId = en.getValue().getForceId();
            }
        }
        kgk.setForceId(forceId);
        kgk.setcIdList(cIdList);
        request.setMessage(kgk);
        requestList.add(request);
        if (requestList.size() <= 0) {
            return;
        }
        List<Response> responseList = null;
        try {
            responseList = (List<Response>)KfgzMatchService.connections[nation][10].sendRequestAndGetResponseList((List)requestList);
        }
        catch (Exception e) {
            KfgzMatchService.logger.error("error3");
        }
        if (responseList == null) {
            return;
        }
        for (final Response response : responseList) {
            final KfgzNationResInfo syncResult = (KfgzNationResInfo)response.getMessage();
            if (syncResult != null && syncResult.getState() == 1) {
                this.self.writeKfgzNationResInfoToDatabase(syncResult, nation);
                this.kfgzNationResInfo[nation] = syncResult;
            }
        }
    }
    
    @Transactional
    @Override
    public synchronized void writeKfgzNationResInfoToDatabase(final KfgzNationResInfo syncResult, final int nation) {
        if (this.kfgzPlayerRewardDao.hasData(syncResult.getSeasonId(), syncResult.getGzId(), nation) > 0) {
            return;
        }
        this.self.issueRoundReward(syncResult.getSeasonId(), nation);
        final List<Tuple<Integer, String>> list = new ArrayList<Tuple<Integer, String>>();
        String[] split;
        for (int length = (split = syncResult.getKillRankingReward().split(",")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            final Tuple<Integer, String> t = (Tuple<Integer, String>)new Tuple();
            t.left = Integer.valueOf(s.split(":")[0]);
            t.right = s.split(":")[1];
            list.add(t);
        }
        for (final KfgzPlayerResultInfo kpri : syncResult.getpList()) {
            final KfgzPlayerReward kpr = new KfgzPlayerReward();
            kpr.setCompetitorId(kpri.getcId());
            kpr.setGzid(syncResult.getGzId());
            kpr.setNation(nation);
            kpr.setRewardTimes(0);
            kpr.setSeasonId(syncResult.getSeasonId());
            final StringBuffer sb = new StringBuffer();
            sb.append(syncResult.getSelfCityCount() * syncResult.getCityTicket());
            sb.append(":");
            sb.append(syncResult.getWinTicket());
            sb.append(":");
            if (kpri.getKillRank() > 0) {
                for (final Tuple<Integer, String> t : list) {
                    if (kpri.getKillRank() <= (int)t.left) {
                        sb.append((String)t.right);
                        break;
                    }
                }
            }
            else {
                sb.append(0);
            }
            sb.append(":");
            sb.append(syncResult.getSoloRewardCoef() * kpri.getSoloWinNum());
            sb.append(":");
            sb.append(syncResult.getOccupyCityRewardCoef() * kpri.getOccupyCity());
            kpr.setReward(sb.toString());
            this.kfgzPlayerRewardDao.create(kpr);
        }
    }
    
    @Transactional
    @Override
    public void issueRoundReward(final int seasonId, final int nation) {
        final List<KfgzPlayerReward> kprList = this.kfgzPlayerRewardDao.getModelsBySeasonIdForReward(seasonId, nation);
        for (final KfgzPlayerReward kpr : kprList) {
            int tickets = 0;
            String[] split;
            for (int length = (split = kpr.getReward().split(":")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                tickets += Integer.valueOf(s);
            }
            this.kfgzPlayerRewardDao.addRewardTimes(kpr.getId(), 1);
            final KfgzSignResult ksr = KfgzManager.getKfgzSignResultByCid(kpr.getCompetitorId());
            int playerId = 0;
            if (ksr == null) {
                final KfgzSignup ks = this.kfgzSignupDao.getByCid(kpr.getCompetitorId());
                playerId = ks.getPlayerId();
            }
            else {
                playerId = ksr.getPlayerId();
            }
            this.playerTicketsDao.addTickets(playerId, tickets, LocalMessages.ATTRIBUTEKEY_TICKETS_1, true);
            final String content = MessageFormatter.format(LocalMessages.KFGZ_14, new Object[] { tickets });
            this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFGZ_13, content, 1, playerId, 0);
        }
    }
    
    @Override
    public void run() {
        int other = 0;
        final int limite = 5;
        while (KfgzMatchService.mark) {
            try {
                ++other;
                ++KfgzMatchService.cacheMapRefreshCount;
                KfgzMatchService.logger.info("doloop");
                for (int nation = 1; nation < 4; ++nation) {
                    this.syncResource(nation);
                    if (other >= 5) {
                        this.syncMatchInfo(nation);
                        if (this.kfgzNationResInfoMark[nation]) {
                            if (this.kfgzNationResInfo[nation] == null || this.kfgzNationResInfo[nation].getState() != 1) {
                                this.syncKfgzNationResInfo(nation);
                            }
                            this.kfgzSeasonService.requestKfgzAllRankRes(nation);
                        }
                    }
                }
                if (other >= 5) {
                    other = 0;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                continue;
            }
            finally {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        for (int i = 0; i < 4; ++i) {
            KfgzMatchService.connections[i] = null;
        }
        this.doInit();
    }
    
    private void doInit() {
        KfgzMatchService.sourceVersion.clear();
        KfgzMatchService.cacheMapRefreshCount = 0;
        this.kfgzNationResInfo = new KfgzNationResInfo[4];
        this.kfgzNationResInfoMark = new boolean[4];
    }
}
