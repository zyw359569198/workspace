package com.reign.kfgz.battle;

import org.apache.commons.logging.*;
import com.reign.kfgz.team.*;
import com.reign.kf.match.common.util.*;
import com.reign.kfgz.comm.*;
import java.util.concurrent.*;
import com.reign.kfgz.control.*;
import com.reign.kf.match.sdata.common.*;
import com.reign.kfgz.resource.*;
import com.reign.kf.match.common.*;
import com.reign.util.*;
import com.reign.kfgz.dto.*;
import org.springframework.beans.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.framework.netty.servlet.*;
import com.reign.kf.comm.param.match.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import ast.gcldcore.fight.*;
import com.reign.framework.json.*;
import java.io.*;

public class KfBattle extends GzLifeCycle
{
    private static Log battleLog;
    private static Log battleReportLog;
    int battleId;
    KfTeam kfTeam;
    private int battleState;
    int fightRound;
    public long nextRoundAttTime;
    List<KfBattleArmy> attList;
    List<KfBattleArmy> defList;
    Map<Integer, KfBattlePInfo> kfBattlePInfoMap;
    List<Tuple<Integer, String>> attBuffListInit;
    List<Tuple<Integer, String>> defBuffListInit;
    List<Tuple<Integer, String>> attBuffListRound;
    List<Tuple<Integer, String>> defBuffListRound;
    ConcurrentSkipListSet<Integer> inSceneSet;
    public static final int BATTLE_STATE_INI = 1;
    public static final int BATTLE_STATE_BATTLE = 2;
    public static final int BATTLE_STATE_FIN = 3;
    public static final int STATE_MINTIMERUN = 1;
    public static final int STATE_MAXTIMERUN = 2;
    public static final int STATE_IMMEDIATERUN = 3;
    static ScheduledThreadPoolExecutor exeutors;
    
    static {
        KfBattle.battleLog = LogFactory.getLog("mj.kfgz.battle.log");
        KfBattle.battleReportLog = LogFactory.getLog("mj.kfgz.battleReport.log");
        KfBattle.exeutors = new ScheduledThreadPoolExecutor(3);
    }
    
    public int getBattleState() {
        return this.battleState;
    }
    
    public void setBattleState(final int battleState) {
        if (this.battleState != battleState) {
            this.battleState = battleState;
            this.kfTeam.pushTeamInfo();
        }
    }
    
    public void addInSceneSet(final Integer cId) {
        KfBattleManager.setPlayerWatchBattleId(cId, this.kfTeam.getTeamId());
        this.inSceneSet.add(cId);
    }
    
    public boolean isInSceneSet(final Integer playerId) {
        return this.inSceneSet.contains(playerId);
    }
    
    public void leave(final Integer cId) {
        KfBattleManager.removePlayerWatchBattleId(cId);
        this.inSceneSet.remove(cId);
    }
    
    public KfBattle(final KfTeam kfTeam, final int battleId) {
        this.fightRound = 0;
        this.attList = new LinkedList<KfBattleArmy>();
        this.defList = new LinkedList<KfBattleArmy>();
        this.kfBattlePInfoMap = new ConcurrentHashMap<Integer, KfBattlePInfo>();
        this.attBuffListInit = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.defBuffListInit = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.attBuffListRound = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.defBuffListRound = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.inSceneSet = new ConcurrentSkipListSet<Integer>();
        this.kfTeam = kfTeam;
        this.battleId = battleId;
        if (this.kfTeam.terrainVal == 4) {
            final Tuple<Integer, String> tacticHalfBuff = new Tuple();
            tacticHalfBuff.left = 10;
            tacticHalfBuff.right = LocalMessages.BUFF_TIPS_10;
            this.attBuffListInit.add(tacticHalfBuff);
        }
        this.battleState = 1;
        this.gzId = kfTeam.gzId;
        KfBattle.battleReportLog.info("new Battle=" + battleId);
    }
    
    public int doRush(final KfGeneralInfo gInfo, final int toCityId) {
        final KfTeam toTeam = KfgzTeamManager.getKfTeam(toCityId, this.kfTeam.getGzId());
        if (toTeam == null || toTeam.getTeamType() != 1) {
            return 2;
        }
        if (this.kfTeam.getTeamType() != 1) {
            return 2;
        }
        final KfPlayerInfo pInfo = gInfo.getpInfo();
        final int tech39 = pInfo.getTech39();
        try {
            KfgzConstants.doLockCities((KfCity)this.kfTeam, (KfCity)toTeam);
            boolean isAtt = true;
            if (this.kfTeam.getForceId() == gInfo.getpInfo().getForceId()) {
                isAtt = false;
            }
            Label_0177: {
                if (isAtt) {
                    if (this.kfTeam.attGList.size() >= (3 - tech39) * this.kfTeam.defGList.size()) {
                        break Label_0177;
                    }
                }
                else if (this.kfTeam.defGList.size() >= (3 - tech39) * this.kfTeam.attGList.size()) {
                    break Label_0177;
                }
                return 2;
            }
            if (gInfo.getTeam() != this.kfTeam) {
                return 2;
            }
            if (gInfo.getState() != 2) {
                return 2;
            }
            if (toTeam.getForceId() == gInfo.pInfo.getForceId()) {
                if (toTeam.battle == null) {
                    return 2;
                }
                if (toTeam.battle.battleState != 2) {
                    return 2;
                }
            }
            if (((KfCity)toTeam).isCaptial()) {
                return 2;
            }
            if (!KfgzManager.getKfWorldByGzId(this.kfTeam.getGzId()).isCityNearBy(gInfo.getTeam().getTeamId(), toCityId)) {
                return 2;
            }
            this.kfTeam.removeGeneral(gInfo);
            gInfo.getCampArmy().setTacticRemain(true);
            toTeam.addGeneral(gInfo);
            return 1;
        }
        finally {
            KfgzConstants.doUnlockCities((KfCity)this.kfTeam, (KfCity)toTeam);
        }
    }
    
    public KfGeneralInfo getSoloGeneral(final KfPlayerInfo player) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            final List<KfGeneralInfo> list = new ArrayList<KfGeneralInfo>();
            for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
                final KfGeneralInfo gInfo = entry.getValue();
                if (gInfo.team == this.kfTeam && gInfo.getState() == 2) {
                    list.add(gInfo);
                }
            }
            if (list.size() == 0) {
                return null;
            }
            return list.get(0);
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
    }
    
    public int doSoloByPlayer(final KfPlayerInfo player) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            final List<KfGeneralInfo> list = new ArrayList<KfGeneralInfo>();
            for (final Map.Entry<Integer, KfGeneralInfo> entry : player.getgMap().entrySet()) {
                final KfGeneralInfo gInfo = entry.getValue();
                if (gInfo.team == this.kfTeam && gInfo.getState() == 2) {
                    list.add(gInfo);
                }
            }
            if (list.size() == 0) {
                return 4;
            }
            return this.doSolo(list.get(0));
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
    }
    
    private void doRandomSolo(final int soloNum) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            for (int i = 0; i < soloNum; ++i) {
                final KfGeneralInfo attGeneralInfo = this.checkAndGetSoloGeneral(true);
                if (attGeneralInfo != null) {
                    this.doSolo(attGeneralInfo);
                }
            }
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
        this.kfTeam.teamLock.writeLock().unlock();
    }
    
    public int doSolo(final KfGeneralInfo gInfo) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            if (gInfo.getState() == 3) {
                return 2;
            }
            boolean isAtt = false;
            if (gInfo.getpInfo().getForceId() != this.kfTeam.getForceId()) {
                isAtt = true;
            }
            final KfGeneralInfo targetKfGeneralInfo = this.checkAndGetSoloGeneral(isAtt);
            if (targetKfGeneralInfo == null) {
                return 3;
            }
            return this.createNewSoloGTeam(gInfo, targetKfGeneralInfo, isAtt);
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
    }
    
    private int createNewSoloGTeam(final KfGeneralInfo gInfo, final KfGeneralInfo targetKfGeneralInfo, final boolean isAtt) {
        final KfSoloTeam soloTeam = new KfSoloTeam(this.kfTeam.getGzId(), this.kfTeam.getCityId(), this.kfTeam.terrain, this.kfTeam.terrainVal);
        soloTeam.setForceId(this.kfTeam.getForceId());
        gInfo.getCampArmy().setTacticRemain(true);
        targetKfGeneralInfo.getCampArmy().setTacticRemain(true);
        if (isAtt) {
            this.kfTeam.removeGeneral(targetKfGeneralInfo);
            soloTeam.addGeneral(targetKfGeneralInfo, false);
            this.kfTeam.removeGeneral(gInfo);
            soloTeam.addGeneral(gInfo, true);
        }
        else {
            this.kfTeam.removeGeneral(gInfo);
            soloTeam.addGeneral(gInfo, false);
            this.kfTeam.removeGeneral(targetKfGeneralInfo);
            soloTeam.addGeneral(targetKfGeneralInfo, true);
        }
        return soloTeam.getTeamId();
    }
    
    private KfGeneralInfo checkAndGetSoloGeneral(final boolean isAtt) {
        List<KfGeneralInfo> gList = null;
        if (isAtt) {
            gList = this.kfTeam.defGList;
        }
        else {
            gList = this.kfTeam.attGList;
        }
        for (final KfGeneralInfo gInfo : gList) {
            if (gInfo.getState() == 2) {
                return gInfo;
            }
        }
        return null;
    }
    
    public void doCheckAddMembertoBattle(final boolean isAtt, final KfGeneralInfo generalInfo) {
        List<KfBattleArmy> list = null;
        if (isAtt) {
            list = this.attList;
        }
        else {
            list = this.defList;
        }
        if (list.size() < 8) {
            this.AddNewArmy(isAtt, 8 - list.size());
        }
    }
    
    public List<KfBattleArmy> AddNewArmy(final boolean isAtt, final int armyNum) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            final List<KfGeneralInfo> addgeneralList = this.kfTeam.getNewBattlePrePareGeneral(isAtt, armyNum);
            final List<KfBattleArmy> armylist = new ArrayList<KfBattleArmy>();
            final List<KfBattleArmy> upList = new ArrayList<KfBattleArmy>();
            for (final KfGeneralInfo gInfo : addgeneralList) {
                final List<KfBattleArmy> newAddarmylist = this.addGeneralToFight(isAtt, gInfo);
                armylist.addAll(newAddarmylist);
                for (final KfBattleArmy ka : newAddarmylist) {
                    upList.add(ka);
                }
            }
            final String rTitle = this.getRoundTitleIni();
            final StringBuilder battleMsg = new StringBuilder(rTitle);
            KfBuilder.getReportType2(battleMsg, upList, isAtt ? "att" : "def");
            if (upList.size() > 0) {
                KfgzMessageSender.sendMsgToAll(this, battleMsg);
            }
            return armylist;
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
    }
    
    private List<KfBattleArmy> addGeneralToFight(final boolean isAtt, final KfGeneralInfo gInfo) {
        if (gInfo.isNotNpc()) {
            final KfPlayerInfo pInfo = gInfo.getpInfo();
            KfBattlePInfo bpInfo = this.kfBattlePInfoMap.get(pInfo.getCompetitorId());
            if (bpInfo == null) {
                bpInfo = new KfBattlePInfo(pInfo.getCompetitorId(), isAtt);
                this.kfBattlePInfoMap.put(bpInfo.getCompetitorId(), bpInfo);
            }
        }
        List<KfBattleArmy> armylist = new ArrayList<KfBattleArmy>();
        final List<KfBattleArmy> newAddArmylist = new ArrayList<KfBattleArmy>();
        if (isAtt) {
            armylist = this.attList;
        }
        else {
            armylist = this.defList;
        }
        final KfCampArmy ca = gInfo.campArmy;
        int[] strategyArray = new int[3];
        final Troop troop = TroopCache.getTroopCacheById(ca.getTroopId());
        if (isAtt) {
            strategyArray = troop.getStrategyMap().get(this.kfTeam.terrainVal);
        }
        else {
            strategyArray = troop.getStrategyDefMap().get(this.kfTeam.terrainVal);
        }
        final int strategyLengh = strategyArray.length;
        ca.setStrategies(strategyArray);
        int lastpos = 0;
        if (armylist.size() > 0) {
            lastpos = armylist.get(armylist.size() - 1).position;
        }
        getTerrainValue(this.kfTeam.terrainVal, troop, isAtt, ca);
        if (!ca.isTacticRemain()) {
            if (ca.getTacicId() > 0) {
                ca.setTacticVal(1);
                if (ca.getKfspecialGeneral() != null && ca.getKfspecialGeneral().generalType == 7) {
                    ca.setTacticVal((int)ca.getKfspecialGeneral().param);
                }
            }
            else {
                ca.setTacticVal(0);
            }
        }
        if (ca.getArmyHp() < ca.getArmyHpOrg()) {
            ca.setTacticVal(0);
        }
        if (ca.getKfspecialGeneral() != null && ca.getKfspecialGeneral().generalType == 2) {
            final int num = KfgzManager.getKfWorldByGzId(this.kfTeam.getGzId()).getCityNumFromCityToCapital(ca.getGeneralInfo().getCityPos(), ca.getGeneralInfo().getpInfo().getForceId());
            if (num > 0) {
                final KfgzBaseInfo bInfo = KfgzManager.getGzBaseInfoById(this.gzId);
                if (bInfo != null && bInfo.getLayerId() == 2) {
                    ca.getKfspecialGeneral().setParam(num * 2.5);
                }
                else {
                    ca.getKfspecialGeneral().setParam(num * 3);
                }
            }
        }
        ca.armyHpKill = 0;
        ca.armyHpLoss = 0;
        ca.killGeneral = 0;
        final int troopHp = ca.getTroopHp();
        int armyHp = ca.getArmyHp();
        if (armyHp < 3) {
            armyHp = 3;
        }
        final int phantomStg = WebUtil.nextInt(strategyLengh);
        while (armyHp >= 3) {
            ++lastpos;
            final int colHp = (armyHp < troopHp) ? armyHp : troopHp;
            final int perArmyHp = colHp / 3;
            final KfBattleArmy battleArmy = new KfBattleArmy();
            battleArmy.setChoose(false);
            battleArmy.setCampArmy(ca);
            battleArmy.setPosition(lastpos);
            battleArmy.setDefaultStrategy(strategyArray[WebUtil.nextInt(strategyLengh)]);
            if (ca.isPhantom) {
                battleArmy.setDefaultStrategy(strategyArray[phantomStg]);
            }
            final int[] colHpList = new int[3];
            for (int i = 0; i < colHpList.length; ++i) {
                colHpList[i] = perArmyHp;
            }
            battleArmy.setTroopHp(colHpList);
            armyHp -= colHp;
            if (armyHp < 3) {
                battleArmy.setGeneralLastArmy(true);
            }
            armylist.add(battleArmy);
            newAddArmylist.add(battleArmy);
        }
        int gState = 1003;
        if (this.kfTeam.getTeamType() == 2) {
            gState = 1013;
        }
        gInfo.setGState(3, gState);
        return newAddArmylist;
    }
    
    public static double getTerrainValue(final int terrainType, final Troop troop, final boolean isAtt, final KfCampArmy campArmy) {
        if (troop == null || troop.getTerrains() == null) {
            return 0.0;
        }
        final TroopTerrain terrain = troop.getTerrains().get(terrainType);
        int effect = 0;
        if (terrain != null) {
            if (isAtt) {
                effect = terrain.getAttEffect();
            }
            else {
                effect = terrain.getDefEffect();
            }
            campArmy.setTerrainQ(terrain.getQuality());
        }
        campArmy.setTerrain(effect / 100.0);
        campArmy.setTerrainAdd(effect);
        return campArmy.getTerrain();
    }
    
    public int chooseStrategyOrTactic(final int cId, final int pos, final int tacticId) {
        try {
            this.kfTeam.teamLock.writeLock().lock();
            KfBattleArmy battleArmy = null;
            final KfBattleArmy attArmy = this.getCurAttBattleArmy();
            final KfBattleArmy defArmy = this.getCurDefBattleArmy();
            if (attArmy == null || defArmy == null) {
                return 2;
            }
            boolean isAtt = false;
            if (attArmy.getCampArmy().getGeneralInfo().pInfo.getCompetitorId() == cId) {
                isAtt = true;
            }
            else {
                if (defArmy.getCampArmy().getGeneralInfo().pInfo.getCompetitorId() != cId) {
                    return 2;
                }
                isAtt = false;
            }
            if (isAtt) {
                battleArmy = this.getCurAttBattleArmy();
            }
            else {
                battleArmy = this.getCurDefBattleArmy();
            }
            if (battleArmy == null) {
                return 2;
            }
            if (battleArmy.getSpecial() == 1) {
                return 2;
            }
            KfBattle.battleLog.info("choosenST:" + isAtt + "_" + pos + "_" + tacticId + "_" + battleArmy.getPosition() + "_" + battleArmy.getStrategy());
            if (tacticId == 100) {
                if (battleArmy.getCampArmy().tacticVal <= 0) {
                    return 2;
                }
                if (battleArmy.choose) {
                    return 2;
                }
                battleArmy.choose = true;
                KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseTatic");
                final boolean candoFightNow = this.canDoFightImmediately();
                if (candoFightNow) {
                    final int runNextRound = this.fightRound + 1;
                    KfBattle.exeutors.schedule(new Runnable() {
                        @Override
                        public void run() {
                            KfBattle.this.runRound(runNextRound, 3);
                        }
                    }, 0L, TimeUnit.MILLISECONDS);
                }
                return 1;
            }
            else {
                if (tacticId <= 0) {
                    return 2;
                }
                if (battleArmy.getStrategy() > 0) {
                    KfBattle.battleLog.error("hasUSedStr" + tacticId);
                    return 2;
                }
                final int[] strs = battleArmy.getCampArmy().getStrategies();
                boolean inStrs = false;
                int[] array;
                for (int length = (array = strs).length, i = 0; i < length; ++i) {
                    final int st = array[i];
                    if (tacticId == st) {
                        inStrs = true;
                        break;
                    }
                }
                if (!inStrs) {
                    KfBattle.battleLog.error("errorStr=isAtt" + isAtt + " :" + battleArmy.getCampArmy().getGeneralName() + tacticId + " not in" + strs[0] + "-" + strs[1] + "-" + strs[2]);
                    return 2;
                }
                battleArmy.setStrategy(tacticId);
                KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseStrategy");
                final boolean candoFightNow2 = this.canDoFightImmediately();
                if (candoFightNow2) {
                    final int runNextRound2 = this.fightRound + 1;
                    KfBattle.exeutors.schedule(new Runnable() {
                        @Override
                        public void run() {
                            KfBattle.this.runRound(runNextRound2, 3);
                        }
                    }, 0L, TimeUnit.MILLISECONDS);
                }
                return 1;
            }
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
    }
    
    public void runBattle() {
        this.setBattleState(2);
        final long delay = 6000L;
        this.nextRoundAttTime = System.currentTimeMillis() + delay;
        KfBattle.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                KfBattle.this.runRound(1, 2);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private void runRound(final int roundNum, final int runState) {
        KfBattleRoundInfo kfwdRoundInfo = null;
        if (this.isEnd()) {
            KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "end " + this.gzId + " baseInfo=" + KfgzManager.isGzEndByGzId(this.gzId));
            return;
        }
        try {
            this.kfTeam.teamLock.writeLock().lock();
            final int attGNum = this.kfTeam.attGList.size();
            final int defGNum = this.kfTeam.defGList.size();
            final int allGeneralNum = attGNum + defGNum;
            final int soloNum = allGeneralNum / 150;
            if (attGNum > 12 && defGNum > 12) {
                this.doRandomSolo(soloNum);
            }
        }
        finally {
            this.kfTeam.teamLock.writeLock().unlock();
        }
        this.kfTeam.teamLock.writeLock().unlock();
        Label_0700: {
            try {
                this.kfTeam.teamLock.writeLock().lock();
                try {
                    KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "dofirst");
                    if (roundNum < this.fightRound + 1 || this.battleState == 3) {
                        return;
                    }
                    if (runState == 1) {
                        final boolean candoFightNow = this.canDoFightImmediately();
                        if (!candoFightNow) {
                            KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#doMaxTime");
                            KfBattle.exeutors.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    KfBattle.this.runRound(roundNum, 2);
                                }
                            }, 6000L, TimeUnit.MILLISECONDS);
                            return;
                        }
                        KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "doImmediate");
                    }
                    KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "runRound");
                    kfwdRoundInfo = this.doRoundFight(this.fightRound);
                    ++this.fightRound;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    KfBattle.battleReportLog.error("error", e);
                }
                final long t1 = System.currentTimeMillis();
                this.doProcessKillRanking(kfwdRoundInfo);
                final long t2 = System.currentTimeMillis();
                final long delay;
                final long d2 = delay = kfwdRoundInfo.nextMaxExeTime;
                this.nextRoundAttTime = System.currentTimeMillis() + delay;
                if (kfwdRoundInfo.needPushReport13) {
                    KfBuilder.getReportType13(this, kfwdRoundInfo.battleMsg);
                }
                KfgzMessageSender.sendMsgToAll(this, kfwdRoundInfo.battleMsg);
                KfBattle.battleLog.info("fightRes=" + kfwdRoundInfo.state + "  --delay=" + delay);
                KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#" + kfwdRoundInfo.state + "#delay=" + delay);
                this.doProcessRoundResult(kfwdRoundInfo);
                if (kfwdRoundInfo.state != 2) {
                    final int runNextRound = this.fightRound + 1;
                    KfBattle.exeutors.schedule(new Runnable() {
                        @Override
                        public void run() {
                            KfBattle.this.runRound(runNextRound, 1);
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
                else {
                    this.setBattleState(3);
                    this.doBattleFinishRes(kfwdRoundInfo);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                break Label_0700;
            }
            finally {
                this.kfTeam.teamLock.writeLock().unlock();
            }
            this.kfTeam.teamLock.writeLock().unlock();
        }
        if (kfwdRoundInfo != null) {
            this.kfTeam.processKfKickBack();
        }
    }
    
    private void doProcessKillRanking(final KfBattleRoundInfo kfwdRoundInfo) {
        final KfCampArmy attCa = kfwdRoundInfo.attCampArmy;
        if (attCa != null) {
            KfgzManager.getBattleRankingByGzID(this.kfTeam.getGzId()).addNewRanking(attCa.getGeneralInfo(), 1, kfwdRoundInfo.getAllKill(true));
        }
        final KfCampArmy defCa = kfwdRoundInfo.defCampArmy;
        if (defCa != null) {
            KfgzManager.getBattleRankingByGzID(this.kfTeam.getGzId()).addNewRanking(defCa.getGeneralInfo(), 1, kfwdRoundInfo.getAllKill(false));
        }
    }
    
    public void sendResReport7(final boolean isAttWin, final KfBattleRoundInfo kfwdRoundInfo) {
        final String rTitle = String.valueOf(this.getRoundTitle()) + 7;
        for (final int cId : this.inSceneSet) {
            KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#cIdEnd+" + cId);
            final Integer teamId = KfBattleManager.getPlayerWatchBattleId(cId);
            KfBattlePInfo bpInfo = this.kfBattlePInfoMap.get(cId);
            if (teamId != null && teamId.equals(this.kfTeam.getTeamId())) {
                final StringBuilder battleMsg = new StringBuilder();
                final KfPlayerInfo pInfo = KfgzPlayerManager.getPlayerByCId(cId);
                if (pInfo == null) {
                    continue;
                }
                battleMsg.append(rTitle).append("|");
                final boolean isAtt = pInfo.getForceId() != this.kfTeam.getForceId();
                if ((isAtt && isAttWin) || (!isAtt && !isAttWin)) {
                    battleMsg.append(1).append("|");
                }
                else {
                    battleMsg.append(2).append("|");
                }
                battleMsg.append(0).append(";");
                if (bpInfo == null) {
                    bpInfo = new KfBattlePInfo();
                }
                battleMsg.append(bpInfo.killTotal).append("|").append(bpInfo.lostTotal).append("|").append(bpInfo.maxKillG).append("|").append("").append(";");
                if (bpInfo.dropMap.size() == 0) {
                    battleMsg.append(0).append("*").append(0).append(";");
                }
                else {
                    for (final Map.Entry<Integer, BattleDrop> entry : bpInfo.dropMap.entrySet()) {
                        battleMsg.append(entry.getValue().type).append("*").append(entry.getValue().num).append("|");
                    }
                    battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), ";");
                }
                final Integer wTeamId = KfBattleManager.getPlayerWatchBattleId(cId);
                if (wTeamId != null && wTeamId.equals(this.kfTeam.getTeamId())) {
                    KfBattle.battleReportLog.info(battleMsg.toString());
                    KfgzMessageSender.sendBattleMsgToOne(cId, battleMsg);
                }
                this.leave(cId);
            }
        }
    }
    
    private void doBattleFinishRes(final KfBattleRoundInfo kfwdRoundInfo) {
        boolean isAttWin = false;
        if (this.defList.size() <= 0) {
            isAttWin = true;
        }
        KfBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "battleEnd" + "isAttWin" + isAttWin);
        if (isAttWin && this.kfTeam.getTeamType() == 1) {
            final KfPlayerInfo pInfo = kfwdRoundInfo.attCampArmy.generalInfo.pInfo;
            final int addExp = pInfo.getTech50();
            if (addExp > 0) {
                final int occupyCId = pInfo.getCompetitorId();
                final KfBattlePInfo bpInfo = this.kfBattlePInfoMap.get(occupyCId);
                if (bpInfo != null) {
                    final BattleDrop battleDrop = new BattleDrop();
                    battleDrop.type = 1005;
                    battleDrop.num = addExp;
                    bpInfo.addDrop(battleDrop);
                    try {
                        KfgzResChangeManager.addResource(pInfo.getCompetitorId(), addExp, "exp", "\u56fd\u6218\u5360\u57ce");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.sendResReport7(isAttWin, kfwdRoundInfo);
        final int attForceId = kfwdRoundInfo.attBattleArmy.getCampArmy().generalInfo.getpInfo().getForceId();
        for (final KfGeneralInfo gInfo : this.kfTeam.attGList) {
            gInfo.setGState(1, 1);
            if (this.kfTeam.getTeamType() != 2) {
                gInfo.getCampArmy().setTeamEffect(0.0);
                gInfo.getCampArmy().setTeamGenreal(null);
                gInfo.getCampArmy().setTacticRemain(false);
            }
            else {
                gInfo.getCampArmy().setTacticRemain(true);
            }
        }
        for (final KfGeneralInfo gInfo : this.kfTeam.defGList) {
            if (this.kfTeam.getTeamType() != 2) {
                gInfo.getCampArmy().setTeamEffect(0.0);
                gInfo.getCampArmy().setTeamGenreal(null);
                gInfo.getCampArmy().setTacticRemain(false);
            }
            else {
                gInfo.getCampArmy().setTacticRemain(true);
            }
            gInfo.setGState(1, 1);
        }
        try {
            KfgzOfficeToken officeToken = KfgzManager.getKfWorldByGzId(this.gzId).getOfficeTokenByCityIdAndForceId(this.kfTeam.getTeamId(), 1);
            if (officeToken != null && officeToken.isEffect()) {
                officeToken.setState(2);
                KfgzMessageSender.sendMsgToForce(this.gzId, officeToken.getForceId(), officeToken.createJsonObject(), PushCommand.PUSH_KF_OFFICETOKEN);
            }
            officeToken = KfgzManager.getKfWorldByGzId(this.gzId).getOfficeTokenByCityIdAndForceId(this.kfTeam.getTeamId(), 2);
            if (officeToken != null && officeToken.isEffect()) {
                officeToken.setState(2);
                KfgzMessageSender.sendMsgToForce(this.gzId, officeToken.getForceId(), officeToken.createJsonObject(), PushCommand.PUSH_KF_OFFICETOKEN);
            }
        }
        catch (Exception ex) {}
        if (isAttWin) {
            if (this.kfTeam.getTeamType() == 1) {
                ((KfCity)this.kfTeam).changeNation(attForceId);
                final KfgzWorldCity kwc = WorldCityCache.getById(this.kfTeam.getTeamId());
                boolean mark = false;
                if (kwc.getType() == 3 && ((KfCity)this.kfTeam).getRewardFinish() == 0) {
                    for (final Map.Entry<Integer, KfPlayerInfo> en : KfgzPlayerManager.getPlayerMapByGz(this.gzId).entrySet()) {
                        final KfPlayerInfo kpi = en.getValue();
                        if (kpi.getForceId() == kfwdRoundInfo.attCampArmy.forceId) {
                            if (kwc.getExp() > 0) {
                                KfgzResChangeManager.addResource(kpi.getCompetitorId(), kwc.getExp(), "exp", "\u8d44\u6e90\u57ce\u5956\u52b1");
                            }
                            if (kwc.getFood() > 0) {
                                KfgzResChangeManager.addResource(kpi.getCompetitorId(), kwc.getFood(), "food", "\u8d44\u6e90\u57ce\u5956\u52b1");
                            }
                            if (kwc.getIron() <= 0) {
                                continue;
                            }
                            KfgzResChangeManager.addResource(kpi.getCompetitorId(), kwc.getIron(), "iron", "\u8d44\u6e90\u57ce\u5956\u52b1");
                        }
                    }
                    ((KfCity)this.kfTeam).setRewardFinish(1);
                    mark = true;
                }
                try {
                    final String content = MessageFormatter.format(LocalMessages.CHAT_1, new Object[] { ColorUtil.getBlueMsg(kfwdRoundInfo.attCampArmy.playerName), kwc.getName() });
                    KfgzMessageSender.sendChatToForce(this.gzId, kfwdRoundInfo.attCampArmy.generalInfo.pInfo.getForceId(), content);
                    if (mark) {
                        String resCityContent1 = "";
                        if (kwc.getExp() > 0) {
                            if (!resCityContent1.isEmpty()) {
                                resCityContent1 = String.valueOf(resCityContent1) + LocalMessages.COMM_4;
                            }
                            resCityContent1 = String.valueOf(resCityContent1) + MessageFormatter.format(LocalMessages.CHAT_3_1, new Object[] { kwc.getExp() });
                        }
                        if (kwc.getFood() > 0) {
                            if (!resCityContent1.isEmpty()) {
                                resCityContent1 = String.valueOf(resCityContent1) + LocalMessages.COMM_4;
                            }
                            resCityContent1 = String.valueOf(resCityContent1) + MessageFormatter.format(LocalMessages.CHAT_3_2, new Object[] { kwc.getFood() });
                        }
                        if (kwc.getIron() > 0) {
                            if (!resCityContent1.isEmpty()) {
                                resCityContent1 = String.valueOf(resCityContent1) + LocalMessages.COMM_4;
                            }
                            resCityContent1 = String.valueOf(resCityContent1) + MessageFormatter.format(LocalMessages.CHAT_3_3, new Object[] { kwc.getIron() });
                        }
                        final String resCityContent2 = MessageFormatter.format(LocalMessages.CHAT_3, new Object[] { kwc.getName(), resCityContent1 });
                        KfgzMessageSender.sendChatToForce(this.gzId, kfwdRoundInfo.attCampArmy.generalInfo.pInfo.getForceId(), resCityContent2);
                    }
                }
                catch (Exception ex2) {}
                KfgzManager.getBattleRankingByGzID(this.kfTeam.getGzId()).addNewRanking(kfwdRoundInfo.attCampArmy.getGeneralInfo(), 3, 1);
            }
            else if (this.kfTeam.getTeamType() == 2) {
                for (final KfGeneralInfo gInfo : this.kfTeam.attGList) {
                    KfgzManager.getKfWorldByGzId(this.kfTeam.getGzId()).getCities().get(gInfo.getCityPos()).addGeneral(gInfo);
                }
                final KfGeneralInfo attGInfo = kfwdRoundInfo.attCampArmy.getGeneralInfo();
                if (attGInfo.isPlayerRealGeneral()) {
                    final int attCId = attGInfo.getpInfo().getCompetitorId();
                    kfwdRoundInfo.defCampArmy.getGeneralInfo();
                }
                KfgzManager.getBattleRankingByGzID(this.kfTeam.getGzId()).addNewRanking(kfwdRoundInfo.attCampArmy.getGeneralInfo(), 2, 1);
            }
        }
        else if (this.kfTeam.getTeamType() == 2) {
            for (final KfGeneralInfo gInfo : this.kfTeam.defGList) {
                KfgzManager.getKfWorldByGzId(this.kfTeam.getGzId()).getCities().get(gInfo.getCityPos()).addGeneral(gInfo);
            }
            try {
                KfgzManager.getBattleRankingByGzID(this.kfTeam.getGzId()).addNewRanking(kfwdRoundInfo.defCampArmy.getGeneralInfo(), 2, 1);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.kfBattlePInfoMap.clear();
        this.kfTeam.checkAndSetForce();
        if (isAttWin && this.kfTeam.getTeamType() == 1) {
            final KfCampArmy lastAttCa = kfwdRoundInfo.attCampArmy;
            if (lastAttCa != null) {
                final KfGeneralInfo newDefGeneral = this.copyWinBattleGeneralInfo(lastAttCa);
                this.kfTeam.addGeneral(newDefGeneral);
            }
        }
    }
    
    private KfGeneralInfo copyWinBattleGeneralInfo(final KfCampArmy lastAttCa) {
        final KfGeneralInfo newGInfo = new KfGeneralInfo();
        final KfCampArmy newCampArmy = new KfCampArmy();
        BeanUtils.copyProperties(lastAttCa, newCampArmy);
        newCampArmy.armyHp = lastAttCa.armyHpOrg;
        final KfPlayerInfo newPInfo = new KfPlayerInfo(0, this.kfTeam.getGzId());
        newPInfo.setPlayerName("\u5b88\u536b");
        newPInfo.setForceId(lastAttCa.getGeneralInfo().getpInfo().getForceId());
        newGInfo.setpInfo(newPInfo);
        newGInfo.setgId(lastAttCa.getGeneralId());
        newGInfo.setCampArmy(newCampArmy);
        newCampArmy.setPlayerId(0);
        newCampArmy.setPlayerName(newPInfo.getPlayerName());
        newCampArmy.setKfspecialGeneral(new KfSpecialGeneral(lastAttCa.getKfspecialGeneral().getGeneralType(), lastAttCa.getKfspecialGeneral().getParam()));
        newCampArmy.setGeneralInfo(newGInfo);
        return newGInfo;
    }
    
    private void doProcessRoundResult(final KfBattleRoundInfo kfwdRoundInfo) {
    }
    
    private void processRoundResult(final KfBattleRoundInfo kfRoundInfo) {
    }
    
    private KfBattleRoundInfo doRoundFight(final int roundNum) {
        final KfBattleRoundInfo roundInfo = new KfBattleRoundInfo(roundNum);
        final String rTitle = this.getRoundTitle();
        KfBuilder.getCurCampInfo(this, roundInfo);
        boolean usedTactic = false;
        final boolean AttReBound = this.canReBound(true);
        final boolean DefReBound = this.canReBound(false);
        final boolean canAttUseTatic = this.canUseTactic(true);
        final boolean canDefUseTatic = this.canUseTactic(false);
        if (this.getCurAttBattleArmy() != null) {
            this.getCurAttBattleArmy().isFirstAction = false;
        }
        if (this.getCurDefBattleArmy() != null) {
            this.getCurDefBattleArmy().isFirstAction = false;
        }
        if ((canAttUseTatic && !DefReBound) || (canDefUseTatic && AttReBound)) {
            usedTactic = true;
            this.printTroopDatas();
            this.useTactic(true, roundInfo, canDefUseTatic && AttReBound);
            roundInfo.attRebound = (canDefUseTatic && AttReBound);
            this.doCheckProcessFightResult(roundInfo);
            this.doProcessFightRes(roundInfo);
        }
        if (roundInfo.state == 0) {
            boolean defArmyConfunsion = false;
            final KfBattleArmy army1Def = this.getCurDefBattleArmy();
            if (army1Def != null && army1Def.getSpecial() == 1) {
                defArmyConfunsion = true;
            }
            if ((!defArmyConfunsion && canDefUseTatic && !AttReBound) || (canAttUseTatic && DefReBound)) {
                usedTactic = true;
                this.printTroopDatas();
                this.useTactic(false, roundInfo, canAttUseTatic && DefReBound);
                roundInfo.defRebound = (canAttUseTatic && DefReBound);
                this.doCheckProcessFightResult(roundInfo);
                this.doProcessFightRes(roundInfo);
            }
            if (roundInfo.state == 0 && !usedTactic) {
                this.printTroopDatas();
                this.useStrategy(roundInfo);
                this.doCheckProcessFightResult(roundInfo);
                this.doProcessFightRes(roundInfo);
            }
        }
        KfBuilder.getReportType14(this, roundInfo);
        if (roundInfo.state != 0) {
            this.doRoundReward(roundInfo);
        }
        KfBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            KfBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        }
        this.doMakeReport16(roundInfo);
        KfBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfBuilder.getReportType26(cd, delay);
            KfBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
            roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
            return roundInfo;
        }
        this.doNormalFight(roundInfo);
        this.doCheckProcessFightResult(roundInfo);
        this.doRoundReward(roundInfo);
        KfBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        KfBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        KfBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        this.doMakeReport16(roundInfo);
        if (roundInfo.state != 0) {
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfBuilder.getReportType26(cd, delay);
            KfBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
        }
        this.doProcessFightRes(roundInfo);
        roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
        return roundInfo;
    }
    
    private void doRoundReward(final KfBattleRoundInfo roundInfo) {
        KfBuilder.dealTroopDrop(this, roundInfo);
        KfBuilder.roundCaculateAttReward(this, roundInfo);
        KfBuilder.roundCaculateDefReward(this, roundInfo);
        this.roundAddReward(this, roundInfo, roundInfo.attRoundReward, roundInfo.defRoundReward);
        KfBuilder.getReportType19(roundInfo.battleMsg, roundInfo);
    }
    
    public void roundAddReward(final KfBattle bat, final KfBattleRoundInfo roundInfo, final RoundReward attRoundReward, final RoundReward defRoundReward) {
        KfBuilder.roundAddRewardSingle(roundInfo.attCampArmy, attRoundReward);
        KfBuilder.roundAddRewardSingle(roundInfo.defCampArmy, defRoundReward);
    }
    
    public void iniBattle() {
        this.kfBattlePInfoMap.clear();
        this.inSceneSet.clear();
        this.AddNewArmy(true, 8);
        this.AddNewArmy(false, 8);
    }
    
    private boolean canDoFightImmediately() {
        final boolean res = true;
        final KfBattleArmy attArmy = this.getCurAttBattleArmy();
        final KfBattleArmy defArmy = this.getCurDefBattleArmy();
        if (attArmy == null || defArmy == null) {
            return res;
        }
        final int attId = attArmy.getCampArmy().getPlayerId();
        final int defId = defArmy.getCampArmy().getPlayerId();
        final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(attId, 2));
        if (session != null && !attArmy.getCampArmy().isPhantom && attArmy.getCampArmy().getGeneralInfo().isNotNpc() && !this.canUseTactic(true, true) && this.getCurAttBattleArmy() != null && this.getCurAttBattleArmy().strategy == 0) {
            return false;
        }
        final Session session2 = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(defId, 2));
        return (session2 == null || defArmy.getCampArmy().isPhantom || !defArmy.getCampArmy().getGeneralInfo().isNotNpc() || this.canUseTactic(false, true) || this.getCurDefBattleArmy() == null || this.getCurDefBattleArmy().strategy != 0) && res;
    }
    
    public void getIniBattleMsg(final StringBuilder battleMsgInput, final int cId) {
        this.addInSceneSet(cId);
        StringBuilder battleMsg = new StringBuilder();
        final List<KfBattleArmy> attUpList = new ArrayList<KfBattleArmy>();
        for (int i = 0; i < this.attList.size(); ++i) {
            attUpList.add(this.attList.get(i));
        }
        final List<KfBattleArmy> defUpList = new ArrayList<KfBattleArmy>();
        for (int j = 0; j < this.defList.size(); ++j) {
            defUpList.add(this.defList.get(j));
        }
        KfBuilder.getReportType2(battleMsg, attUpList, "att");
        KfBuilder.getReportType2(battleMsg, defUpList, "def");
        if (this.attList.size() > 0 && this.getCurAttBattleArmy() != null) {
            final KfBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
            KfBuilder.getReportType16(this, battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", true, true, (targetBattleArmy == null) ? null : targetBattleArmy.getCampArmy());
        }
        if (this.defList.size() > 0 && this.getCurDefBattleArmy() != null) {
            final KfBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
            KfBuilder.getReportType16(this, battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", true, true, (targetBattleArmy == null) ? null : targetBattleArmy.getCampArmy());
        }
        KfBuilder.getReportType27(battleMsg, new KfBattleRoundInfo(0), this);
        long delay = this.nextRoundAttTime - System.currentTimeMillis();
        if (delay < 0L) {
            delay = 0L;
        }
        final String rTitle = this.getRoundTitleIni();
        final StringBuilder cd = new StringBuilder();
        KfBuilder.getReportType26(cd, delay);
        battleMsg = new StringBuilder(cd).append(battleMsg);
        battleMsg = new StringBuilder(rTitle).append(battleMsg);
        KfBuilder.getReportType13(this, battleMsg);
        battleMsgInput.append(battleMsg);
        String[] split;
        for (int length = (split = battleMsg.toString().split("#")).length, k = 0; k < length; ++k) {
            final String s = split[k];
        }
    }
    
    public KfBattleArmy getCurAttBattleArmy() {
        if (this.attList.size() == 0) {
            return null;
        }
        return this.attList.get(0);
    }
    
    public KfBattleArmy getBattleArmyByPos(final boolean isAtt, final int pos) {
        List<KfBattleArmy> list = null;
        if (isAtt) {
            list = this.attList;
        }
        else {
            list = this.defList;
        }
        if (pos >= list.size()) {
            return null;
        }
        return list.get(pos);
    }
    
    public KfBattleArmy getCurDefBattleArmy() {
        if (this.defList.size() == 0) {
            return null;
        }
        return this.defList.get(0);
    }
    
    private boolean canUseTactic(final boolean isAtt) {
        return this.canUseTactic(isAtt, false);
    }
    
    private boolean canUseTactic(final boolean isAtt, final boolean checkDoImmediately) {
        final KfBattleArmy army1Att = this.getCurAttBattleArmy();
        final KfBattleArmy army1Def = this.getCurDefBattleArmy();
        final KfCampArmy attCamp = army1Att.getCampArmy();
        final KfCampArmy defCamp = army1Def.getCampArmy();
        KfCampArmy camp1 = null;
        KfBattleArmy army1 = null;
        if (isAtt) {
            camp1 = attCamp;
            army1 = army1Att;
        }
        else {
            camp1 = defCamp;
            army1 = army1Def;
        }
        if (army1 == null || camp1 == null) {
            return false;
        }
        final boolean isFirstDoAction = army1.isFirstAction;
        KfBattle.battleReportLog.info("doFirst=" + (isAtt ? "att" : "def") + "#" + army1.getPosition() + army1.isFirstAction + "#" + (army1.strategy == 0) + "#" + (army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0)));
        return camp1.tacticVal > 0 && ((army1.choose && isFirstDoAction) || (!checkDoImmediately && army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0))) && army1.getSpecial() != 1;
    }
    
    public String getRoundTitle() {
        final String matchId = KfgzBattleConstants.getBattleTitle(this.kfTeam.getTeamId(), this.kfTeam.getGzId());
        return String.valueOf(this.fightRound) + "|" + matchId + "#";
    }
    
    public String getRoundTitleIni() {
        final String matchId = KfgzBattleConstants.getBattleTitle(this.kfTeam.getTeamId(), this.kfTeam.getGzId());
        return String.valueOf(this.fightRound) + "|" + matchId + "|" + "ini" + "#";
    }
    
    public KfBattleArmy getLivedBattleArmy(final int pos, final boolean isAtt) {
        List<KfBattleArmy> list = null;
        if (isAtt) {
            list = this.attList;
        }
        else {
            list = this.defList;
        }
        if (list.size() <= pos) {
            return null;
        }
        return list.get(pos);
    }
    
    private boolean canReBound(final boolean isAtt) {
        final KfBattleArmy army1Att = this.getCurAttBattleArmy();
        final KfBattleArmy army1Def = this.getCurDefBattleArmy();
        final KfCampArmy attCamp = army1Att.getCampArmy();
        final KfCampArmy defCamp = army1Def.getCampArmy();
        KfCampArmy camp1 = null;
        KfBattleArmy army1 = null;
        if (isAtt) {
            camp1 = attCamp;
            army1 = army1Att;
        }
        else {
            camp1 = defCamp;
            army1 = army1Def;
        }
        return camp1.kfspecialGeneral.generalType == 5 && WebUtil.nextDouble() < camp1.kfspecialGeneral.param;
    }
    
    private void printTroopDatas() {
        final String rTitle = this.getRoundTitle();
        for (int i = 0; i < this.attList.size(); ++i) {
            String sb = "";
            for (int j = 0; j < 3; ++j) {
                sb = String.valueOf(sb) + this.attList.get(i).troopHp[j] + ",";
            }
        }
        for (int i = 0; i < this.defList.size(); ++i) {
            String sb = "";
            for (int j = 0; j < 3; ++j) {
                sb = String.valueOf(sb) + this.defList.get(i).troopHp[j] + ",";
            }
        }
    }
    
    private void useTactic(final boolean isAttUseTactic, final KfBattleRoundInfo roundInfo, final boolean reBound) {
        KfBattleArmy army1Att = this.getCurAttBattleArmy();
        KfBattleArmy army1Def = this.getCurDefBattleArmy();
        KfCampArmy attCamp = army1Att.getCampArmy();
        KfCampArmy defCamp = army1Def.getCampArmy();
        int defSize = this.defList.size();
        int defTaticEffectSize = (defSize > 6) ? 6 : defSize;
        List<KfBattleArmy> realAttList = this.attList;
        List<KfBattleArmy> realDefList = this.defList;
        int realDefPos = 0;
        if (!isAttUseTactic) {
            realAttList = this.defList;
            realDefList = this.attList;
            realDefPos = 0;
            army1Att = this.getCurDefBattleArmy();
            army1Def = this.getCurAttBattleArmy();
            attCamp = army1Att.getCampArmy();
            defCamp = army1Def.getCampArmy();
            defSize = this.attList.size();
            defTaticEffectSize = ((defSize > 6) ? 6 : defSize);
        }
        roundInfo.tacticStrategyResult = 3;
        final KfTacticInfo tacticInfo = new KfTacticInfo();
        if (isAttUseTactic) {
            roundInfo.attTacticInfo = tacticInfo;
        }
        else {
            roundInfo.defTacticInfo = tacticInfo;
        }
        com.reign.kf.match.sdata.domain.Tactic tactic = TacticCache.getTacticById(attCamp.getTacicId());
        if (reBound) {
            tactic = TacticCache.getTacticById(defCamp.getTacicId());
        }
        if (tactic == null) {
            return;
        }
        if (reBound) {
            final KfTacticInfo targetTacticInfo = new KfTacticInfo();
            if (isAttUseTactic) {
                roundInfo.defTacticInfo = targetTacticInfo;
            }
            else {
                roundInfo.attTacticInfo = targetTacticInfo;
            }
            targetTacticInfo.tacticId = tactic.getId();
            targetTacticInfo.tacticDisplayId = tactic.getDisplayId();
            targetTacticInfo.tacticNameId = tactic.getPic();
            targetTacticInfo.tacticBasicPic = tactic.getBasicPic();
            targetTacticInfo.specialType = tactic.getSpecialType();
            targetTacticInfo.executed = true;
            if (defCamp != null) {
                final KfCampArmy kfCampArmy = defCamp;
                --kfCampArmy.tacticVal;
            }
        }
        tactic.calcuSpecial();
        roundInfo.nextMaxExeTime += tactic.getPlayertime() + 1600;
        roundInfo.timePredicationBuffer.append("tactic:").append(tactic.getPlayertime() + 1600).append("|");
        tacticInfo.tacticId = tactic.getId();
        tacticInfo.tacticDisplayId = tactic.getDisplayId();
        tacticInfo.tacticNameId = tactic.getPic();
        tacticInfo.tacticBasicPic = tactic.getBasicPic();
        tacticInfo.specialType = tactic.getSpecialType();
        for (final KfBattleArmy ba : realDefList) {
            if (ba.getTD_defense_e() > 0.0) {
                tacticInfo.attacked_guanyu = true;
                break;
            }
        }
        roundInfo.nextMaxExeTime += 1000;
        roundInfo.timePredicationBuffer.append("tactic reduce:").append(1000).append("|");
        final TroopData[] attacker = new TroopData[3];
        this.copyTacticBattleInfo(army1Att, attacker, army1Def, reBound);
        final TroopData[][] defenders = new TroopData[defTaticEffectSize][3];
        this.copyTacticBattleInfo(realDefList, realDefPos, defenders, army1Att);
        final String tacticString = Tactic.tacticAttack(attacker, defenders);
        tacticInfo.tacticStr = tacticString;
        army1Att.setStrategy(army1Att.getUsedStrategy());
        final KfCampArmy kfCampArmy2 = attCamp;
        --kfCampArmy2.tacticVal;
        tacticInfo.executed = true;
        if (tacticInfo.tacticStr.equalsIgnoreCase("SP")) {
            tacticInfo.beStop = true;
            roundInfo.timePredicationBuffer.append("tactic is stoped:").append(0).append("|");
            return;
        }
        final General generalA = GeneralCache.getGeneralById(attCamp.getGeneralId());
        if (generalA != null && generalA.getGeneralSpecialInfo() != null && generalA.getGeneralSpecialInfo().generalType == 10) {
            int wuShenFuTiLimit = generalA.getGeneralSpecialInfo().rowNum;
            if (realAttList.size() < wuShenFuTiLimit) {
                wuShenFuTiLimit = realAttList.size();
            }
            for (int i = 0; i < wuShenFuTiLimit; ++i) {
                realAttList.get(i).setTD(true);
                realAttList.get(i).setTD_defense_e(generalA.getGeneralSpecialInfo().param2);
            }
        }
        if (tacticInfo.specialType > 0) {
            if (tacticInfo.specialType == 2) {
                return;
            }
            if (tacticInfo.specialType == 1) {
                final int range = tactic.getRange();
                final StringBuilder sb = new StringBuilder();
                for (int j = 0; j < range; ++j) {
                    final int newPos = realDefPos + j;
                    if (newPos >= realDefList.size()) {
                        break;
                    }
                    final KfBattleArmy dfArmy = realDefList.get(newPos);
                    dfArmy.setSpecial(1);
                    sb.append(dfArmy.getPosition()).append(",");
                }
                tacticInfo.columnStr = sb.toString();
            }
        }
        this.doProcessTacticResult(tacticString, isAttUseTactic, roundInfo);
    }
    
    private void copyTacticBattleInfo(final List<KfBattleArmy> armyList, final int armyPos, final TroopData[][] defenders, final KfBattleArmy targetArmy) {
        for (int i = 0; i < defenders.length; ++i) {
            final KfBattleArmy bArmy = armyList.get(i);
            final TroopData[] troopData = new TroopData[3];
            this.copyTacticBattleInfo(bArmy, troopData, targetArmy, false);
            defenders[i] = troopData;
        }
    }
    
    private void copyTacticBattleInfo(KfBattleArmy battleArmy, final TroopData[] troopData, final KfBattleArmy targetArmy, final boolean ReBound) {
        if (ReBound && targetArmy != null && targetArmy.getCampArmy() != null) {
            battleArmy = targetArmy;
        }
        final KfCampArmy campArmy = battleArmy.getCampArmy();
        final int att = campArmy.getAttEffect();
        final int def = campArmy.getDefEffect();
        campArmy.getPlayerId();
        final com.reign.kf.match.sdata.domain.Tactic tactic = TacticCache.getTacticById(campArmy.getTacicId());
        boolean isYX = false;
        int YX_cur_Blood = 0;
        int YX_max_Blood = 0;
        if (campArmy.getKfspecialGeneral().generalType == 9) {
            isYX = true;
            YX_cur_Blood = campArmy.getArmyHpOrg() - campArmy.getArmyHpLoss();
            YX_max_Blood = campArmy.getArmyHpOrg();
        }
        for (int i = 0; i < battleArmy.getTroopHp().length; ++i) {
            final TroopData tempData = new TroopData();
            tempData.troop_id = campArmy.getId();
            tempData.hp = battleArmy.getTroopHp()[i];
            tempData.max_hp = campArmy.getTroopHp() / 3;
            tempData.att = att;
            tempData.def = def;
            tempData.base_damage = campArmy.getBdEffect();
            tempData.Str = campArmy.getStrength();
            tempData.Lea = campArmy.getLeader();
            tempData.general_quality = campArmy.getQuality();
            tempData.TACTIC_ATT = campArmy.getTACTIC_ATT();
            tempData.TACTIC_DEF = campArmy.getTACTIC_DEF();
            tempData.ATT_B = campArmy.getATT_B();
            tempData.DEF_B = campArmy.getDEF_B();
            tempData.tech_yingyong_damage_e = campArmy.getTechYinYong();
            tempData.tech_jianren_damage_e = campArmy.getTechJianRen();
            if (targetArmy != null && targetArmy.getCampArmy().getKfspecialGeneral().generalType == 11) {
                tempData.terrain_effect = 0.0;
            }
            else {
                tempData.terrain_effect = campArmy.getTerrain();
            }
            tempData.world_legion_e = campArmy.getTeamEffect();
            if (campArmy.getKfspecialGeneral().generalType == 2) {
                tempData.isFS = true;
                tempData.world_fs_d = (int)campArmy.getKfspecialGeneral().param;
                if (tempData.world_fs_d <= 0) {
                    tempData.isFS = false;
                }
            }
            else {
                tempData.isFS = false;
                tempData.world_fs_d = 0;
            }
            boolean isBS = false;
            tempData.isYX = isYX;
            tempData.YX_cur_Blood = YX_cur_Blood;
            tempData.YX_max_Blood = YX_max_Blood;
            tempData.isTD = battleArmy.isTD();
            tempData.TD_defense_e = battleArmy.getTD_defense_e();
            final int attackerBSNum = this.kfTeam.attGList.size();
            final int defenderBSNum = this.kfTeam.defGList.size();
            if (tactic != null) {
                if (tactic.getSpecialEffect().equalsIgnoreCase("bs")) {
                    isBS = true;
                }
                tempData.tactic_id = tactic.getId();
                tempData.tactic_damage_e = tactic.getDamageE();
                tempData.tactic_range = tactic.getRange();
                tempData.isBS = isBS;
                tempData.BS_My = attackerBSNum;
                tempData.BS_Your = defenderBSNum;
            }
            final GemAttribute gemAttribute = campArmy.getGemAttribute();
            if (gemAttribute != null) {
                if (this.kfTeam.getTeamType() == 10000) {
                    tempData.JS_SKILL_dt = gemAttribute.skillDt;
                }
                tempData.JS_SKILL_ms = gemAttribute.skillMs;
                tempData.JS_SKILL_bj = gemAttribute.skillBj;
                tempData.JS_SKILL_att = gemAttribute.skillAtt;
                tempData.JS_SKILL_zfbj = gemAttribute.skillZfbj;
                tempData.JS_SKILL_zfjb = gemAttribute.skillZfjb;
            }
            troopData[i] = tempData;
        }
    }
    
    private void doProcessTacticResult(final String tacticString, final boolean isDefLostHp, final KfBattleRoundInfo roundInfo) {
        if (!StringUtils.isEmpty(tacticString)) {
            final String[] ts = tacticString.split(";");
            int dpos = 0;
            List<KfBattleArmy> armyList = this.defList;
            KfBattleArmy attArmy = this.getCurAttBattleArmy();
            if (!isDefLostHp) {
                dpos = 0;
                armyList = this.attList;
                attArmy = this.getCurDefBattleArmy();
            }
            final KfCampArmy attCa = attArmy.getCampArmy();
            KfTacticInfo tacticInfo = new KfTacticInfo();
            if (isDefLostHp) {
                tacticInfo = roundInfo.attTacticInfo;
            }
            else {
                tacticInfo = roundInfo.defTacticInfo;
            }
            final KfCampArmy firstDefCamp = armyList.get(dpos).getCampArmy();
            final Map<KfCampArmy, Integer> reduceMap = new HashMap<KfCampArmy, Integer>();
            for (int i = 0; i < ts.length; ++i) {
                if (ts[i].contains("jsBJ")) {
                    tacticInfo.zfBJ = true;
                }
                if (ts[i].contains("jsJB")) {
                    tacticInfo.zfJB = true;
                }
                final String[] tempArr = ts[i].split("\\|");
                ts[i] = tempArr[tempArr.length - 1];
                final String[] ts2 = ts[i].split(",");
                final KfBattleArmy curArmy = armyList.get(dpos);
                final KfCampArmy defendCa = curArmy.getCampArmy();
                tacticInfo.defArmyList.add(curArmy);
                for (int j = 0; j < ts2.length; ++j) {
                    final String ts3 = ts2[j];
                    int reduce = Integer.parseInt(ts3);
                    if (reduce > curArmy.troopHp[j]) {
                        reduce = curArmy.troopHp[j];
                    }
                    curArmy.troopHp[j] -= reduce;
                    if (defendCa == firstDefCamp) {
                        final KfTacticInfo kfTacticInfo = tacticInfo;
                        kfTacticInfo.firstCReduce += reduce;
                    }
                    final KfTacticInfo kfTacticInfo2 = tacticInfo;
                    kfTacticInfo2.allCReduce += reduce;
                    final KfCampArmy kfCampArmy = defendCa;
                    kfCampArmy.armyHpLoss += reduce;
                    defendCa.armyHp = ((defendCa.armyHp - reduce >= 0) ? (defendCa.armyHp - reduce) : 0);
                    if (reduceMap.containsKey(defendCa)) {
                        reduceMap.put(defendCa, reduceMap.get(defendCa) + reduce);
                    }
                    else {
                        reduceMap.put(defendCa, reduce);
                    }
                    tacticInfo.reduceMap = reduceMap;
                    final KfCampArmy campArmy = attArmy.getCampArmy();
                    campArmy.armyHpKill += reduce;
                    final KfBattlePInfo attbPInf = this.getKfBattlePInfoFromCampArmy(attCa);
                    if (attbPInf != null) {
                        final KfBattlePInfo kfBattlePInfo = attbPInf;
                        kfBattlePInfo.killTotal += reduce;
                    }
                    final KfBattlePInfo defbPInfo = this.getKfBattlePInfoFromCampArmy(defendCa);
                    if (defbPInfo != null) {
                        final KfBattlePInfo kfBattlePInfo2 = defbPInfo;
                        kfBattlePInfo2.lostTotal += reduce;
                    }
                }
                ++dpos;
            }
            if (isDefLostHp) {
                roundInfo.tacticStrategyResult = 1;
            }
            else {
                roundInfo.tacticStrategyResult = 2;
            }
        }
    }
    
    public KfBattlePInfo getKfBattlePInfoFromCampArmy(final KfCampArmy ca) {
        if (ca == null || ca.getGeneralInfo() == null) {
            return null;
        }
        final KfGeneralInfo gInfo = ca.getGeneralInfo();
        if (!gInfo.isNotNpc() || gInfo.getpInfo() == null) {
            return null;
        }
        return this.kfBattlePInfoMap.get(gInfo.getpInfo().getCompetitorId());
    }
    
    private void doCheckProcessFightResult(final KfBattleRoundInfo roundInfo) {
        boolean roundFinish = false;
        boolean battleFinish = false;
        final int oldDefPos = 0;
        final KfBattleArmy firstAttArmy = this.getCurAttBattleArmy();
        final KfBattleArmy firstDefArmy = this.getCurDefBattleArmy();
        final Set<KfGeneralInfo> changeHpgList = new HashSet<KfGeneralInfo>();
        int defNextArmyPos = 0;
        KfBattleArmy curDefArmy = firstDefArmy;
        if (curDefArmy != null && curDefArmy.troopHp[0] <= 0) {
            roundFinish = true;
        }
        while (curDefArmy != null) {
            if (curDefArmy.troopHp[0] <= 0) {
                roundInfo.defKilledList.add(curDefArmy);
                roundInfo.defFirstRowKilled = true;
                roundInfo.win = 1;
                if (curDefArmy.isGeneralLastArmy && defNextArmyPos > 0) {
                    final KfBattleArmy lastDefArmy = this.getBattleArmyByPos(false, defNextArmyPos - 1);
                    if (lastDefArmy.getCampArmy() == curDefArmy.getCampArmy()) {
                        lastDefArmy.setGeneralLastArmy(true);
                        curDefArmy.setGeneralLastArmy(false);
                    }
                }
                if (curDefArmy.isGeneralLastArmy) {
                    final KfCampArmy campArmy = firstAttArmy.getCampArmy();
                    ++campArmy.killGeneral;
                    roundInfo.needPushReport13 = true;
                    roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                    roundInfo.killDefG = true;
                    final KfBattlePInfo attbPInf = this.getKfBattlePInfoFromCampArmy(firstAttArmy.getCampArmy());
                    if (attbPInf != null) {
                        final KfBattlePInfo kfBattlePInfo = attbPInf;
                        ++kfBattlePInfo.maxKillG;
                    }
                }
                final KfGeneralInfo kickeGInfo = this.removeFirstArmy(false, defNextArmyPos);
                if (kickeGInfo != null) {
                    this.kfTeam.kickBackQueue.add(kickeGInfo);
                }
                if (this.kfTeam.defGList.size() == 0) {
                    battleFinish = true;
                    break;
                }
                curDefArmy = this.getBattleArmyByPos(false, defNextArmyPos);
            }
            else {
                ++defNextArmyPos;
                curDefArmy = this.getBattleArmyByPos(false, defNextArmyPos);
            }
        }
        int attNextArmyPos = 0;
        KfBattleArmy curAttArmy = firstAttArmy;
        if (curAttArmy != null && curAttArmy.troopHp[0] <= 0) {
            roundFinish = true;
        }
        while (curAttArmy != null) {
            if (curAttArmy.troopHp[0] <= 0) {
                roundInfo.attKilledList.add(curAttArmy);
                roundInfo.attFirstRowKilled = true;
                if (roundInfo.win == 1) {
                    roundInfo.win = 3;
                }
                else {
                    roundInfo.win = 2;
                }
                if (curAttArmy.isGeneralLastArmy && attNextArmyPos > 0) {
                    final KfBattleArmy lastAttArmy = this.getBattleArmyByPos(true, attNextArmyPos - 1);
                    if (lastAttArmy.getCampArmy() == curAttArmy.getCampArmy()) {
                        lastAttArmy.setGeneralLastArmy(true);
                        curAttArmy.setGeneralLastArmy(false);
                    }
                }
                if (curAttArmy.isGeneralLastArmy) {
                    final KfCampArmy campArmy2 = firstDefArmy.getCampArmy();
                    ++campArmy2.killGeneral;
                    roundInfo.needPushReport13 = true;
                    roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                    roundInfo.killAttG = true;
                    final KfBattlePInfo defbPInf = this.getKfBattlePInfoFromCampArmy(firstDefArmy.getCampArmy());
                    if (defbPInf != null) {
                        final KfBattlePInfo kfBattlePInfo2 = defbPInf;
                        ++kfBattlePInfo2.maxKillG;
                    }
                }
                final KfGeneralInfo kickeGInfo2 = this.removeFirstArmy(true, attNextArmyPos);
                if (kickeGInfo2 != null) {
                    this.kfTeam.kickBackQueue.add(kickeGInfo2);
                }
                if (this.kfTeam.attGList.size() == 0) {
                    battleFinish = true;
                    break;
                }
                curAttArmy = this.getBattleArmyByPos(true, attNextArmyPos);
            }
            else {
                ++attNextArmyPos;
                curAttArmy = this.getBattleArmyByPos(true, attNextArmyPos);
            }
        }
        if (this.defList.size() < 8) {
            final List<KfBattleArmy> armylist = this.AddNewArmy(false, 8 - this.defList.size());
            roundInfo.defUpList.addAll(armylist);
        }
        if (this.attList.size() < 8) {
            final List<KfBattleArmy> armylist = this.AddNewArmy(true, 8 - this.attList.size());
            roundInfo.attUpList.addAll(armylist);
        }
        final Map<KfCampArmy, KfHpChangeInfo> roundHpChangeInfo = roundInfo.getRoundHpChangeInfo();
        for (final Map.Entry<KfCampArmy, KfHpChangeInfo> entry : roundHpChangeInfo.entrySet()) {
            final KfCampArmy ca = entry.getKey();
            final KfHpChangeInfo hpInfo = entry.getValue();
            final KfGeneralInfo changegInfo = ca.getGeneralInfo();
            if (changegInfo.isPlayerRealGeneral()) {
                changegInfo.pushHpDataWithHpChangeInfo(hpInfo);
            }
        }
        if (battleFinish) {
            roundInfo.state = 2;
        }
        else if (roundFinish) {
            roundInfo.state = 1;
        }
    }
    
    private KfGeneralInfo removeFirstArmy(final boolean isAtt, final int pos) {
        if (isAtt) {
            final KfBattleArmy removeArmy = this.attList.remove(pos);
            if (removeArmy.isGeneralLastArmy()) {
                return this.kfTeam.removeGeneral(removeArmy.getCampArmy().generalInfo);
            }
        }
        else {
            final KfBattleArmy removeArmy = this.defList.remove(pos);
            if (removeArmy.isGeneralLastArmy()) {
                return this.kfTeam.removeGeneral(removeArmy.getCampArmy().generalInfo);
            }
        }
        return null;
    }
    
    private void doProcessFightRes(final KfBattleRoundInfo fightRes) {
        this.printTroopDatas();
    }
    
    private void useStrategy(final KfBattleRoundInfo roundInfo) {
        final KfBattleArmy attArmy = this.getCurAttBattleArmy();
        final KfBattleArmy defArmy = this.getCurDefBattleArmy();
        int attSt = attArmy.getStrategy();
        if (attSt == 0) {
            attSt = attArmy.getDefaultStrategy();
            attArmy.setStrategy(attArmy.getUsedStrategy());
        }
        int defSt = defArmy.getStrategy();
        if (defSt == 0) {
            defSt = defArmy.getDefaultStrategy();
            defArmy.setStrategy(defArmy.getUsedStrategy());
        }
        if (defArmy.getSpecial() > 0) {
            final FightStragtegyCoe defLostsc = FightStragtegyCoeCache.getDefLose(attSt, defArmy.getCampArmy().getStrategies());
            if (defLostsc != null) {
                defSt = defLostsc.getDefStrategy();
                defArmy.setStrategy(defSt);
            }
        }
        if (attArmy.getSpecial() > 0) {
            final FightStragtegyCoe attLostsc = FightStragtegyCoeCache.getAttLose(defSt, attArmy.getCampArmy().getStrategies());
            if (attLostsc != null) {
                attSt = attLostsc.getAttStrategy();
                attArmy.setStrategy(attSt);
            }
        }
        final FightStragtegyCoe fsc = FightStragtegyCoeCache.getFightStragtegyCoe(defSt, attSt);
        final int winSide = fsc.getWinerSide();
        roundInfo.tacticStrategyResult = winSide;
        final FightStrategies fsAtt = FightStrategiesCache.getStr(attSt);
        final FightStrategies fsDef = FightStrategiesCache.getStr(defSt);
        final double attLostPercent = fsc.getAttLost();
        final double defLostPercent = fsc.getDefLost();
        for (int i = 0; i < 3; ++i) {
            int attLost = (int)(defArmy.troopHp[i] * attLostPercent);
            int defLost = (int)(attArmy.troopHp[i] * defLostPercent);
            if (winSide == 1) {
                defLost += fsAtt.getBaseDamage();
            }
            else if (winSide == 2) {
                attLost += fsDef.getBaseDamage();
            }
            attLost = ((attArmy.troopHp[i] >= attLost) ? attLost : attArmy.troopHp[i]);
            defLost = ((defArmy.troopHp[i] >= defLost) ? defLost : defArmy.troopHp[i]);
            attArmy.troopHp[i] -= attLost;
            defArmy.troopHp[i] -= defLost;
            roundInfo.defStrategyLost += defLost;
            final KfCampArmy defCampArmy = roundInfo.defCampArmy;
            defCampArmy.armyHpLoss += defLost;
            roundInfo.defCampArmy.armyHp = ((roundInfo.defCampArmy.armyHp - defLost >= 0) ? (roundInfo.defCampArmy.armyHp - defLost) : 0);
            final KfCampArmy attCampArmy = roundInfo.attCampArmy;
            attCampArmy.armyHpKill += defLost;
            roundInfo.attStrategyLost += attLost;
            final KfCampArmy attCampArmy2 = roundInfo.attCampArmy;
            attCampArmy2.armyHpLoss += attLost;
            roundInfo.attCampArmy.armyHp = ((roundInfo.attCampArmy.armyHp - attLost >= 0) ? (roundInfo.attCampArmy.armyHp - attLost) : 0);
            final KfCampArmy defCampArmy2 = roundInfo.defCampArmy;
            defCampArmy2.armyHpKill += attLost;
            final KfBattlePInfo attbPInf = this.getKfBattlePInfoFromCampArmy(attArmy.getCampArmy());
            if (attbPInf != null) {
                final KfBattlePInfo kfBattlePInfo = attbPInf;
                kfBattlePInfo.killTotal += defLost;
                final KfBattlePInfo kfBattlePInfo2 = attbPInf;
                kfBattlePInfo2.lostTotal += attLost;
            }
            final KfBattlePInfo defbPInf = this.getKfBattlePInfoFromCampArmy(defArmy.getCampArmy());
            if (defbPInf != null) {
                final KfBattlePInfo kfBattlePInfo3 = defbPInf;
                kfBattlePInfo3.killTotal += attLost;
                final KfBattlePInfo kfBattlePInfo4 = defbPInf;
                kfBattlePInfo4.lostTotal += defLost;
            }
            final boolean lostBlood = true;
            if (lostBlood) {
                roundInfo.nextMaxExeTime += 1000;
                roundInfo.timePredicationBuffer.append("strategy reduce:").append(1000).append("|");
            }
        }
    }
    
    private void doMakeReport16(final KfBattleRoundInfo roundInfo) {
        if (roundInfo.killAttG) {
            final KfBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
            if (this.getCurAttBattleArmy() != null) {
                KfBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", true, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        else {
            final KfBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
            if (this.getCurAttBattleArmy() != null) {
                KfBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", false, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        if (roundInfo.killDefG) {
            final KfBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
            if (this.getCurDefBattleArmy() != null) {
                KfBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", true, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        else {
            final KfBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
            if (this.getCurDefBattleArmy() != null) {
                KfBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", false, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
    }
    
    private void doNormalFight(final KfBattleRoundInfo roundInfo) {
        final KfBattleArmy attArmy = this.getCurAttBattleArmy();
        final TroopData[] attacker = new TroopData[3];
        final KfBattleArmy defArmy = this.getCurDefBattleArmy();
        this.copyTacticBattleInfo(attArmy, attacker, defArmy, false);
        final TroopData[] defender = new TroopData[3];
        this.copyTacticBattleInfo(defArmy, defender, attArmy, false);
        final String[] st = Fight.fight(attacker, defender);
        roundInfo.reports = st;
        int winRes = 0;
        final KfBattlePInfo attbPInf = this.getKfBattlePInfoFromCampArmy(attArmy.getCampArmy());
        final KfBattlePInfo defbPInf = this.getKfBattlePInfoFromCampArmy(defArmy.getCampArmy());
        for (int i = 0; i < 3; ++i) {
            final int attLostHp = attArmy.getTroopHp()[i] - attacker[i].hp;
            roundInfo.attLost += attLostHp;
            roundInfo.attRemain += attacker[i].hp;
            final KfCampArmy campArmy = attArmy.getCampArmy();
            campArmy.armyHpLoss += attLostHp;
            attArmy.getCampArmy().armyHp = ((attArmy.getCampArmy().armyHp - attLostHp >= 0) ? (attArmy.getCampArmy().armyHp - attLostHp) : 0);
            final int defLostHp = defArmy.getTroopHp()[i] - defender[i].hp;
            roundInfo.defLost += defLostHp;
            roundInfo.defRemain += defender[i].hp;
            attArmy.getTroopHp()[i] = attacker[i].hp;
            defArmy.getTroopHp()[i] = defender[i].hp;
            final KfCampArmy campArmy2 = defArmy.getCampArmy();
            campArmy2.armyHpLoss += defLostHp;
            defArmy.getCampArmy().armyHp = ((defArmy.getCampArmy().armyHp - defLostHp >= 0) ? (defArmy.getCampArmy().armyHp - defLostHp) : 0);
            if (attacker[i].hp <= 0) {
                if (winRes != 1) {
                    winRes = 2;
                }
                else {
                    winRes = 3;
                }
            }
            if (defender[i].hp <= 0) {
                if (winRes != 2) {
                    winRes = 1;
                }
                else {
                    winRes = 3;
                }
            }
            if (attbPInf != null) {
                final KfBattlePInfo kfBattlePInfo = attbPInf;
                kfBattlePInfo.killTotal += defLostHp;
                final KfBattlePInfo kfBattlePInfo2 = attbPInf;
                kfBattlePInfo2.lostTotal += attLostHp;
            }
            if (defbPInf != null) {
                final KfBattlePInfo kfBattlePInfo3 = defbPInf;
                kfBattlePInfo3.killTotal += attLostHp;
                final KfBattlePInfo kfBattlePInfo4 = defbPInf;
                kfBattlePInfo4.lostTotal += defLostHp;
            }
        }
        int numMax = 0;
        int num = 0;
        String[] reports;
        for (int length = (reports = roundInfo.reports).length, j = 0; j < length; ++j) {
            final String s = reports[j];
            num = s.split(";").length - 1;
            if (num > numMax) {
                numMax = num;
            }
        }
        roundInfo.win = winRes;
        roundInfo.nextMaxExeTime += 500 + numMax * 400;
        roundInfo.timePredicationBuffer.append("fight:").append(500 + numMax * 400).append("|");
        KfBuilder.getReportType3(roundInfo.battleMsg, this, roundInfo);
    }
    
    public int doRetreat(final KfGeneralInfo gInfo, final int toCityId) {
        final KfTeam toTeam = KfgzTeamManager.getKfTeam(toCityId, this.kfTeam.getGzId());
        if (toTeam == null || toTeam.getTeamType() != 1) {
            return 2;
        }
        if (this.kfTeam.getTeamType() != 1) {
            return 2;
        }
        try {
            KfgzConstants.doLockCities((KfCity)this.kfTeam, (KfCity)toTeam);
            if (gInfo.getTeam() == this.kfTeam && gInfo.getState() == 2) {
                if (toTeam.getForceId() == gInfo.pInfo.getForceId()) {
                    if (KfgzManager.getKfWorldByGzId(this.kfTeam.getGzId()).isCityNearBy(gInfo.getTeam().getTeamId(), toCityId)) {
                        this.kfTeam.removeGeneral(gInfo);
                        gInfo.getCampArmy().setArmyHp(gInfo.getCampArmy().getArmyHp() * 9 / 10);
                        toTeam.addGeneral(gInfo);
                        return 1;
                    }
                }
            }
            return 2;
        }
        finally {
            KfgzConstants.doUnlockCities((KfCity)this.kfTeam, (KfCity)toTeam);
        }
    }
    
    public void buildAndSendNewReport13(final Integer cId) {
        if (this.battleState != 2) {
            return;
        }
        final String rTitle = this.getRoundTitleIni();
        final StringBuilder battleMsg = new StringBuilder(rTitle);
        KfBuilder.getReportType13(this, battleMsg);
        KfgzMessageSender.sendBattleMsgToOne(cId, battleMsg);
    }
    
    public KfTeam getKfTeam() {
        return this.kfTeam;
    }
    
    public void setKfTeam(final KfTeam kfTeam) {
        this.kfTeam = kfTeam;
    }
    
    @Override
    public void doEnd() {
    }
    
    public void setBattleRoundBuff(final KfCampArmy attCa, final KfCampArmy defCa, final KfCampArmy targetCampArmy) {
        this.attBuffListRound.clear();
        this.defBuffListRound.clear();
        if (this.attList.size() == 0 || this.defList.size() == 0) {
            return;
        }
        if (attCa != null && attCa.getTerrainAdd() > 0 && (targetCampArmy == null || targetCampArmy.getKfspecialGeneral() == null || targetCampArmy.getKfspecialGeneral().getGeneralType() != 11)) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 23 + attCa.getTerrainQ();
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.kfTeam.terrainName, attCa.getTerrainAdd() });
            this.attBuffListRound.add(temp);
        }
        if (defCa != null && defCa.getTerrainAdd() > 0 && (targetCampArmy == null || targetCampArmy.getKfspecialGeneral() == null || targetCampArmy.getKfspecialGeneral().getGeneralType() != 11)) {
            final Tuple<Integer, String> temp = new Tuple();
            if (defCa.getTerrainQ() == 7) {
                temp.left = 29;
                temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_29, new Object[] { defCa.getTerrainAdd() });
            }
            else {
                temp.left = 23;
                temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.kfTeam.terrainName, defCa.getTerrainAdd() });
            }
            this.defBuffListRound.add(temp);
        }
        final GemAttribute attGemAttr = (attCa == null) ? null : attCa.getGemAttribute();
        if (attGemAttr != null) {
            if (attGemAttr.skillMs > 0.0) {
                final Tuple<Integer, String> temp2 = new Tuple();
                temp2.left = 45;
                temp2.right = LocalMessages.BUFF_TIPS_45;
                this.attBuffListRound.add(temp2);
            }
            if (this.kfTeam.getTeamType() == 2 && attGemAttr.skillDt > 0.0) {
                final Tuple<Integer, String> temp2 = new Tuple();
                temp2.left = 46;
                temp2.right = LocalMessages.BUFF_TIPS_46;
                this.attBuffListRound.add(temp2);
            }
            if (attGemAttr.skillAtt > 0.0) {
                final Tuple<Integer, String> temp2 = new Tuple();
                temp2.left = 48;
                temp2.right = LocalMessages.BUFF_TIPS_48;
                this.attBuffListRound.add(temp2);
            }
        }
        final GemAttribute defGemAttr = (defCa == null) ? null : defCa.getGemAttribute();
        if (defGemAttr != null) {
            if (defGemAttr.skillMs > 0.0) {
                final Tuple<Integer, String> temp3 = new Tuple();
                temp3.left = 45;
                temp3.right = LocalMessages.BUFF_TIPS_45;
                this.defBuffListRound.add(temp3);
            }
            if (this.kfTeam.getTeamType() == 2 && defGemAttr.skillDt > 0.0) {
                final Tuple<Integer, String> temp3 = new Tuple();
                temp3.left = 46;
                temp3.right = LocalMessages.BUFF_TIPS_46;
                this.defBuffListRound.add(temp3);
            }
            if (defGemAttr.skillDef > 0.0) {
                final Tuple<Integer, String> temp3 = new Tuple();
                temp3.left = 47;
                temp3.right = LocalMessages.BUFF_TIPS_47;
                this.defBuffListRound.add(temp3);
            }
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 4) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 30;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp3);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 2 && (int)attCa.kfspecialGeneral.param > 0) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 31;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp3);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 3) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 32;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp3);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 8) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 33;
            temp3.right = LocalMessages.BUFF_TIPS_33;
            this.attBuffListRound.add(temp3);
        }
        if (attCa != null && attCa.getKfspecialGeneral().generalType == 11) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 44;
            temp3.right = LocalMessages.BUFF_TIPS_44;
            this.attBuffListRound.add(temp3);
        }
        if (defCa != null && defCa.getKfspecialGeneral().generalType == 11) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 44;
            temp3.right = LocalMessages.BUFF_TIPS_44;
            this.defBuffListRound.add(temp3);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 4) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 30;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp3);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 2 && (int)defCa.kfspecialGeneral.param > 0) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 31;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp3);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 3) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 32;
            temp3.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp3);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 8) {
            final Tuple<Integer, String> temp3 = new Tuple();
            temp3.left = 33;
            temp3.right = LocalMessages.BUFF_TIPS_33;
            this.defBuffListRound.add(temp3);
        }
    }
    
    public static void main(final String[] args) {
        final KfgzOfficeToken newOfficeToken = new KfgzOfficeToken();
        final byte[] bytes = JsonBuilder.getJson(State.PUSH, "fdasf", newOfficeToken.createJsonObject());
        System.out.println(new String(bytes));
    }
    
    public byte[] getBattleCampList(final int page, final int side) {
        final int startIndex = (page - 1) * 8 + 1;
        final int endIndex = page * 8;
        boolean watchAtt = true;
        if (side == 1) {
            watchAtt = false;
        }
        try {
            this.kfTeam.teamLock.readLock().lock();
            List<KfGeneralInfo> caList = new ArrayList<KfGeneralInfo>();
            if (watchAtt) {
                caList = this.kfTeam.attGList;
            }
            else {
                caList = this.kfTeam.defGList;
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("pageList");
            final List<KfGeneralInfo> getList = new ArrayList<KfGeneralInfo>();
            for (int i = startIndex; i <= endIndex; ++i) {
                final int pos = i - 1;
                if (pos < 0) {
                    break;
                }
                if (pos >= caList.size()) {
                    break;
                }
                final KfGeneralInfo generalInfo = caList.get(pos);
                getList.add(generalInfo);
            }
            for (final KfGeneralInfo general : getList) {
                final KfCampArmy ca = general.getCampArmy();
                int playerId = ca.getPlayerId();
                if (ca.isPhantom) {
                    playerId = -playerId;
                }
                int state = 0;
                if (general.getState() == 3) {
                    state = 1;
                }
                else {
                    state = 2;
                }
                doc.startObject();
                doc.createElement("playerId", playerId);
                doc.createElement("playerName", ca.getPlayerName());
                doc.createElement("generalName", ca.getGeneralName());
                doc.createElement("quality", ca.getQuality());
                doc.createElement("isOnQueue", state);
                doc.createElement("forceId", ca.getForceId());
                doc.endObject();
            }
            doc.endArray();
            final int totalPage = (caList.size() + 8 - 1) / 8;
            doc.createElement("totalPage", totalPage);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            this.kfTeam.teamLock.readLock().unlock();
        }
    }
}
