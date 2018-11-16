package com.reign.kfgz.control;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.world.*;
import com.reign.kfgz.rank.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.kfgz.service.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kfgz.dto.*;
import com.reign.kfgz.team.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.resource.*;
import com.reign.kfgz.constants.*;
import com.reign.kf.match.common.*;
import java.text.*;
import com.reign.kfgz.battle.*;
import com.reign.kfgz.comm.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;

@Component
public class KfgzManager
{
    public static volatile int curSeasonId;
    @Autowired
    KfgzNpcManager kfgzNpcManager;
    @Autowired
    KfgzWorldStgManager kfgzWorldStgManager;
    @Autowired
    KfgzMatchService matchService;
    private static Log scheduleInfoLog;
    public static Map<Integer, KfWorld> worldMap;
    public static Map<Integer, KfgzBaseInfo> gzMap;
    public static Map<Integer, KfgzBattleRank> gzBattleRankMap;
    public static Map<Integer, KfgzBattleRewardRes> gzRewardMap;
    static ScheduledThreadPoolExecutor exeutors;
    static LinkedBlockingQueue<KfgzBattleResultInfo> needSendBattleResQueue;
    
    static {
        KfgzManager.scheduleInfoLog = LogFactory.getLog("astd.kfgz.log.scheduleInfo");
        KfgzManager.worldMap = new ConcurrentHashMap<Integer, KfWorld>();
        KfgzManager.gzMap = new ConcurrentHashMap<Integer, KfgzBaseInfo>();
        KfgzManager.gzBattleRankMap = new ConcurrentHashMap<Integer, KfgzBattleRank>();
        KfgzManager.gzRewardMap = new ConcurrentHashMap<Integer, KfgzBattleRewardRes>();
        KfgzManager.exeutors = new ScheduledThreadPoolExecutor(2);
        KfgzManager.needSendBattleResQueue = new LinkedBlockingQueue<KfgzBattleResultInfo>();
    }
    
    public void ini() {
        final GzThread thread = new GzThread();
        thread.start();
        final SendBattleResThread thread2 = new SendBattleResThread();
        thread2.start();
    }
    
    public static KfgzBaseInfo getGzBaseInfoById(final int gzId) {
        return KfgzManager.gzMap.get(gzId);
    }
    
    public synchronized void iniNewGz(final int gzId, final KfgzScheduleInfoRes sInfo, final KfgzBattleRewardRes bRes, final KfgzRuleInfoRes rule, final KfgzLayerInfoRes layerInfo, final int worldId, final int worldstgId, final int worldNpcId) {
        final KfgzBaseInfo oldBaseInfo = KfgzManager.gzMap.get(gzId);
        if (oldBaseInfo != null && oldBaseInfo.getSeasonId() == sInfo.getSeasonId()) {
            return;
        }
        if (oldBaseInfo != null) {
            this.doClearGzData(oldBaseInfo);
        }
        final KfgzBaseInfo baseInfo = new KfgzBaseInfo(gzId, worldId, worldstgId, worldNpcId, sInfo.getRewardgId(), layerInfo.getExpCoef());
        baseInfo.setSeasonId(sInfo.getSeasonId());
        baseInfo.setGameServer1(sInfo.getGameServer1());
        baseInfo.setServerName1(sInfo.getServerName1());
        baseInfo.setNation1(sInfo.getNation1());
        baseInfo.setGameServer2(sInfo.getGameServer2());
        baseInfo.setServerName2(sInfo.getServerName2());
        baseInfo.setNation2(sInfo.getNation2());
        baseInfo.setGzStartTime(sInfo.getBattleDate());
        baseInfo.setGzEndTime(new Date(sInfo.getBattleDate().getTime() + rule.getBattleMSeconds()));
        baseInfo.setBattleReward(bRes);
        baseInfo.setLayerId(layerInfo.getLayerId());
        KfgzManager.scheduleInfoLog.info("ininew#" + baseInfo.getSeasonId() + "-" + baseInfo.getGzId());
        KfgzManager.gzMap.put(baseInfo.getGzId(), baseInfo);
        final KfWorld kfworld = new KfWorld(gzId);
        kfworld.init(worldId);
        KfgzManager.worldMap.put(kfworld.getGzId(), kfworld);
        this.kfgzNpcManager.ini(gzId, worldNpcId, baseInfo);
        this.kfgzWorldStgManager.ini(gzId, worldstgId);
        final KfgzBattleRank bRank = new KfgzBattleRank(gzId, bRes);
        KfgzManager.gzBattleRankMap.put(gzId, bRank);
        baseInfo.setState(1);
    }
    
    private synchronized void doClearGzData(final KfgzBaseInfo oldBaseInfo) {
        KfgzManager.scheduleInfoLog.info("clearData#" + oldBaseInfo.getSeasonId() + "-" + oldBaseInfo.getGzId());
        final int gzId = oldBaseInfo.getGzId();
        if (oldBaseInfo.isClear()) {
            return;
        }
        final KfWorld w1 = KfgzManager.worldMap.get(gzId);
        if (w1 != null) {
            w1.destroy();
        }
        KfgzManager.worldMap.remove(gzId);
        KfgzManager.gzBattleRankMap.remove(gzId);
        this.kfgzWorldStgManager.clearInfoByGzId(gzId);
        final ConcurrentHashMap<Integer, KfPlayerInfo> playerMap = KfgzPlayerManager.getPlayerMapByGz(gzId);
        if (playerMap != null) {
            playerMap.clear();
        }
        KfgzManager.gzMap.remove(gzId);
        KfgzTeamManager.moveAllGzTeam(gzId);
        KfgzGroupTeamManager.clearGzIdInfo(gzId);
        oldBaseInfo.setState(3);
    }
    
    public static KfgzBattleRank getBattleRankingByGzID(final int gzId) {
        return KfgzManager.gzBattleRankMap.get(gzId);
    }
    
    public static int getWorldIdByGzId(final int gzId) {
        return KfgzManager.gzMap.get(gzId).getWorldId();
    }
    
    public static KfWorld getKfWorldByGzId(final int gzId) {
        return KfgzManager.worldMap.get(gzId);
    }
    
    public void doSendBattleRes() {
        if (KfgzSeasonService.connection == null) {
            return;
        }
        for (int i = 0; i <= 5; ++i) {
            final KfgzBattleResultInfo battleRes = KfgzManager.needSendBattleResQueue.poll();
            if (battleRes == null) {
                break;
            }
            try {
                final KfgzBattleResultRes res = this.fetchScheduleInfoFromGw(battleRes);
                if (res == null) {
                    KfgzManager.needSendBattleResQueue.add(battleRes);
                }
            }
            catch (Exception e) {
                KfgzManager.needSendBattleResQueue.add(battleRes);
                e.printStackTrace();
            }
        }
    }
    
    private KfgzBattleResultRes fetchScheduleInfoFromGw(final KfgzBattleResultInfo battleRes) {
        final Request request = new Request();
        request.setCommand(Command.KFGZ_GW_PUSHBATTLERESULT_FROM_MATCH);
        request.setMessage(battleRes);
        final Response res = KfgzSeasonService.connection.sendSyncAndGetResponse(request);
        return (KfgzBattleResultRes)res.getMessage();
    }
    
    public void checkGzEnd() {
        for (final Map.Entry<Integer, KfgzBaseInfo> entry : KfgzManager.gzMap.entrySet()) {
            final KfgzBaseInfo gzInfo = entry.getValue();
            final Date endTime = gzInfo.getGzEndTime();
            if (endTime == null) {
                continue;
            }
            if (gzInfo.getRealState() != 2 && endTime != null && new Date().after(endTime)) {
                this.endGzByGzInfo(gzInfo);
            }
            final Date clearTime = new Date(endTime.getTime() + 86400000L);
            if (gzInfo.isClear() || endTime == null || !new Date().after(clearTime)) {
                continue;
            }
            this.doClearGzData(gzInfo);
        }
    }
    
    private synchronized void endGzByGzInfo(final KfgzBaseInfo gzInfo) {
        final Date endTime = gzInfo.getGzEndTime();
        if (gzInfo.getRealState() != 2 && endTime != null && new Date().after(endTime)) {
            gzInfo.setState(2);
            try {
                this.calculatePhantomInfoAfterBattle(gzInfo);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.getAndSendBattleRes(gzInfo);
            KfgzTeamManager.moveAllGzTeam(gzInfo.getGzId());
            KfgzGroupTeamManager.clearGzIdInfo(gzInfo.getGzId());
        }
    }
    
    private void calculatePhantomInfoAfterBattle(final KfgzBaseInfo gzInfo) {
        final KfWorld gzWorld = getKfWorldByGzId(gzInfo.getGzId());
        final Map<Integer, KfgzPhantomToExpObj> phInofMap = new HashMap<Integer, KfgzPhantomToExpObj>();
        for (final KfCity city : gzWorld.getCities().values()) {
            for (final KfGeneralInfo gInfo : city.attGList) {
                if (gInfo.isNotNpc() && gInfo.getCampArmy().isPhantom()) {
                    final KfPlayerInfo pInfo = gInfo.getpInfo();
                    final int cId = pInfo.getCompetitorId();
                    final KfCampArmy ca = gInfo.getCampArmy();
                    final int gId = gInfo.getgId();
                    final Troop troop = TroopCache.getTroopCacheById(gInfo.getCampArmy().getTroopId());
                    final int troopId = troop.getId();
                    final double troopFoodConsumeCoe = TroopConscribeCache.getTroopConscribeById(troopId).getFood();
                    final int hp = ca.getArmyHp();
                    final double troopFood = troopFoodConsumeCoe * hp;
                    final double delta = troopFood * 0.3;
                    KfgzPhantomToExpObj obj = phInofMap.get(cId);
                    if (obj == null) {
                        obj = new KfgzPhantomToExpObj(cId);
                        phInofMap.put(cId, obj);
                    }
                    final KfgzPhantomToExpObj kfgzPhantomToExpObj = obj;
                    kfgzPhantomToExpObj.copperSum += delta;
                    final double attTechAddGZJY = pInfo.getTech40() / 100.0;
                    final KfgzPhantomToExpObj kfgzPhantomToExpObj2 = obj;
                    kfgzPhantomToExpObj2.chiefExpSum += (1.0 + attTechAddGZJY) * delta;
                    final double gExpAdd = pInfo.getTech16() / 100.0;
                    final double gExp = (1.0 + gExpAdd) * delta;
                    final Double gExpSum = obj.gExpMap.get(gId);
                    if (gExpSum == null) {
                        obj.gExpMap.put(gId, gExp);
                    }
                    else {
                        obj.gExpMap.put(gId, gExpSum + gExp);
                    }
                }
            }
            for (final KfGeneralInfo gInfo : city.defGList) {
                if (gInfo.isNotNpc() && gInfo.getCampArmy().isPhantom()) {
                    final KfPlayerInfo pInfo = gInfo.getpInfo();
                    final int cId = pInfo.getCompetitorId();
                    final KfCampArmy ca = gInfo.getCampArmy();
                    final int gId = gInfo.getgId();
                    final Troop troop = TroopCache.getTroopCacheById(gInfo.getCampArmy().getTroopId());
                    final int troopId = troop.getId();
                    final double troopFoodConsumeCoe = TroopConscribeCache.getTroopConscribeById(troopId).getFood();
                    final int hp = ca.getArmyHp();
                    final double troopFood = troopFoodConsumeCoe * hp;
                    final double delta = troopFood * 0.3;
                    KfgzPhantomToExpObj obj = phInofMap.get(cId);
                    if (obj == null) {
                        obj = new KfgzPhantomToExpObj(cId);
                        phInofMap.put(cId, obj);
                    }
                    final KfgzPhantomToExpObj kfgzPhantomToExpObj3 = obj;
                    kfgzPhantomToExpObj3.copperSum += delta;
                    final double attTechAddGZJY = pInfo.getTech40() / 100.0;
                    final KfgzPhantomToExpObj kfgzPhantomToExpObj4 = obj;
                    kfgzPhantomToExpObj4.chiefExpSum += (1.0 + attTechAddGZJY) * delta;
                    final double gExpAdd = pInfo.getTech16() / 100.0;
                    final double gExp = (1.0 + gExpAdd) * delta;
                    final Double gExpSum = obj.gExpMap.get(gId);
                    if (gExpSum == null) {
                        obj.gExpMap.put(gId, gExp);
                    }
                    else {
                        obj.gExpMap.put(gId, gExpSum + gExp);
                    }
                }
            }
        }
        for (final KfgzPhantomToExpObj obj2 : phInofMap.values()) {
            final int cId2 = obj2.getcId();
            final double copper = obj2.getCopperSum();
            KfgzResChangeManager.addResource(cId2, (int)copper, "copper", "\u8de8\u670d\u5e7b\u5f71\u56de\u6536");
            final double pExp = obj2.getChiefExpSum();
            KfgzResChangeManager.addResource(cId2, (int)pExp, "exp", "\u8de8\u670d\u5e7b\u5f71\u56de\u6536");
            for (final Map.Entry<Integer, Double> entry : obj2.getgExpMap().entrySet()) {
                final int gId2 = entry.getKey();
                final double gExpSum2 = entry.getValue();
                KfgzResChangeManager.addGeneralExp(cId2, gId2, (int)gExpSum2, "\u8de8\u670d\u5e7b\u5f71\u56de\u6536");
            }
            final int round = KfgzCommConstants.getRoundByGzId(gzInfo.getGzId());
            final String content = MessageFormat.format(LocalMessages.KFGZ_PHANTOM_BACK, round, (int)copper, (int)pExp);
            KfgzMessageSender.sendChatToPlayer(cId2, content);
            final String title = MessageFormat.format(LocalMessages.KFGZ_PHANTOM_BACK_TITEL, round);
            final KfPlayerInfo pInfo2 = KfgzPlayerManager.getPlayerByCId(cId2);
            final MailDto dto = new MailDto(cId2, title, content, pInfo2.getPlayerId());
            if (pInfo2.getForceId() == 1) {
                gzInfo.addMail1(dto);
            }
            else {
                if (pInfo2.getForceId() != 2) {
                    continue;
                }
                gzInfo.addMail2(dto);
            }
        }
    }
    
    private void getAndSendBattleRes(final KfgzBaseInfo gzInfo) {
        final int gzId = gzInfo.getGzId();
        if (getBattleRankingByGzID(gzId) == null) {
            return;
        }
        final List<KfgzPlayerRankingInfoReq> reqList = getBattleRankingByGzID(gzId).getBattleResultInfo(gzInfo);
        final List<KfgzNationResultReq> nationRes = new ArrayList<KfgzNationResultReq>();
        final KfWorld kfWorld = getKfWorldByGzId(gzId);
        final KfgzNationResultReq nationReq1 = new KfgzNationResultReq();
        nationReq1.setGzId(gzId);
        nationReq1.setSeasonId(gzInfo.getSeasonId());
        nationReq1.setNation(gzInfo.getNation1());
        nationReq1.setGameServer(gzInfo.getGameServer1());
        nationReq1.setServerName(gzInfo.getServerName1());
        nationReq1.setSelfCity(kfWorld.getForceCityNum(1));
        nationReq1.setOppCity(kfWorld.getForceCityNum(2));
        nationRes.add(nationReq1);
        final KfgzNationResultReq nationReq2 = new KfgzNationResultReq();
        nationReq2.setGzId(gzId);
        nationReq2.setSeasonId(gzInfo.getSeasonId());
        nationReq2.setNation(gzInfo.getNation2());
        nationReq2.setGameServer(gzInfo.getGameServer2());
        nationReq2.setServerName(gzInfo.getServerName2());
        nationReq2.setSelfCity(kfWorld.getForceCityNum(2));
        nationReq2.setOppCity(kfWorld.getForceCityNum(1));
        nationRes.add(nationReq2);
        final KfgzBattleResultInfo bResultInfo = new KfgzBattleResultInfo();
        bResultInfo.setNationRes(nationRes);
        bResultInfo.setPlayerRes(reqList);
        KfgzManager.needSendBattleResQueue.add(bResultInfo);
    }
    
    public static boolean isGzStartByGzId(final int gzId) {
        final KfgzBaseInfo baseInfo = getGzBaseInfoById(gzId);
        return baseInfo != null && baseInfo.getState() == 1;
    }
    
    public static boolean isGzEndByGzId(final int gzId) {
        final KfgzBaseInfo baseInfo = getGzBaseInfoById(gzId);
        return baseInfo == null || baseInfo.getState() == 2;
    }
    
    public int getCurrentSeasonId() {
        return KfgzManager.curSeasonId;
    }
    
    public void iniNewSeason(final KfgzSeasonInfoRes seasonInfo) {
        KfgzManager.curSeasonId = seasonInfo.getSeasonId();
        KfgzResChangeManager.resetAtomciId();
        for (final KfWorld kfWorld : KfgzManager.worldMap.values()) {
            kfWorld.destroy();
        }
        KfgzManager.worldMap.clear();
        KfgzManager.gzMap.clear();
        KfgzManager.gzBattleRankMap.clear();
        KfgzManager.gzRewardMap.clear();
        KfgzNpcAIManager.clear();
        KfgzPlayerManager.clearAll();
        KfgzNpcManager.clear();
    }
    
    class GzThread extends Thread
    {
        public GzThread() {
            super("gzMainThread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        Thread.sleep(1000L);
                        KfgzManager.this.checkGzEnd();
                    }
                }
                catch (Exception e) {
                    KfgzManager.scheduleInfoLog.error("", e);
                    e.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
    
    class SendBattleResThread extends Thread
    {
        public SendBattleResThread() {
            super("gzSendBattleResThread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        Thread.sleep(100L);
                        KfgzManager.this.doSendBattleRes();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
    
    class PGMPhantomToExpObj
    {
        double copperSum;
        double chiefExpSum;
        Map<Integer, Double> gExpMap;
        
        PGMPhantomToExpObj() {
            this.copperSum = 0.0;
            this.chiefExpSum = 0.0;
            this.gExpMap = new HashMap<Integer, Double>();
        }
    }
}
