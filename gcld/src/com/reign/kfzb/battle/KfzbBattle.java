package com.reign.kfzb.battle;

import com.reign.kfzb.domain.*;
import org.apache.commons.logging.*;
import com.reign.kfzb.constants.*;
import com.reign.kf.match.sdata.common.*;
import org.springframework.beans.*;
import com.reign.kf.match.common.util.*;
import java.util.concurrent.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.framework.netty.servlet.*;
import ast.gcldcore.fight.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kfzb.service.*;
import com.reign.kf.match.common.*;
import com.reign.util.*;

public class KfzbBattle
{
    public static final int STATE_FINSHED = 1;
    private static Log battleLog;
    private static Log battleReportLog;
    int battleType;
    public int fightRound;
    KfzbBattleRes res;
    public KfzbRuntimeMatch match;
    long battleId;
    List<KfzbBattleArmy> attList;
    List<KfzbBattleArmy> defList;
    int attPos;
    int defPos;
    ArrayList<KfzbCampArmy> attCamp;
    ArrayList<KfzbCampArmy> defCamp;
    List<Tuple<Integer, String>> attBuffListInit;
    List<Tuple<Integer, String>> defBuffListInit;
    List<Tuple<Integer, String>> attBuffListRound;
    List<Tuple<Integer, String>> defBuffListRound;
    int terrain;
    public int terrainVal;
    public int state;
    public long nextRoundAttTime;
    static ScheduledThreadPoolExecutor exeutors;
    public static final int STATE_MINTIMERUN = 1;
    public static final int STATE_MAXTIMERUN = 2;
    public static final int STATE_IMMEDIATERUN = 3;
    
    static {
        KfzbBattle.battleLog = LogFactory.getLog("mj.kfzb.battle.log");
        KfzbBattle.battleReportLog = LogFactory.getLog("mj.kfzb.battleReport.log");
        KfzbBattle.exeutors = new ScheduledThreadPoolExecutor(10);
    }
    
    public KfzbBattle() {
        this.fightRound = 0;
        this.attList = new ArrayList<KfzbBattleArmy>(8);
        this.defList = new ArrayList<KfzbBattleArmy>(8);
        this.attPos = 0;
        this.defPos = 0;
        this.attCamp = new ArrayList<KfzbCampArmy>();
        this.defCamp = new ArrayList<KfzbCampArmy>();
        this.attBuffListInit = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.defBuffListInit = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.attBuffListRound = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.defBuffListRound = new CopyOnWriteArrayList<Tuple<Integer, String>>();
        this.terrain = 1;
        this.terrainVal = 1;
        this.state = 0;
    }
    
    public int getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final int terrain) {
        this.terrain = terrain;
    }
    
    public int getTerrainVal() {
        return this.terrainVal;
    }
    
    public void setTerrainVal(final int terrainVal) {
        this.terrainVal = terrainVal;
    }
    
    public int getBattleType() {
        return this.battleType;
    }
    
    public void setBattleType(final int battleType) {
        this.battleType = battleType;
    }
    
    public KfzbBattleRes getRes() {
        return this.res;
    }
    
    public KfzbBattleRes getNotNullRes() {
        if (this.res == null) {
            this.res = new KfzbBattleRes();
            final boolean needChange = KfzbMatchService.getNeedChangeFromMatch(this.match.getMatchId(), this.match.getRound());
            if (needChange) {
                this.res.setPlayer1Id(this.match.getPlayer2Id());
                this.res.setPlayer2Id(this.match.getPlayer1Id());
            }
            else {
                this.res.setPlayer1Id(this.match.getPlayer1Id());
                this.res.setPlayer2Id(this.match.getPlayer2Id());
            }
        }
        return this.res;
    }
    
    public void setRes(final KfzbBattleRes res) {
        this.res = res;
    }
    
    public KfzbRuntimeMatch getMatch() {
        return this.match;
    }
    
    public void setMatch(final KfzbRuntimeMatch match) {
        this.match = match;
    }
    
    public long getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final long battleId) {
        this.battleId = battleId;
    }
    
    public List<KfzbBattleArmy> getAttList() {
        return this.attList;
    }
    
    public void setAttList(final List<KfzbBattleArmy> attList) {
        this.attList = attList;
    }
    
    public List<KfzbBattleArmy> getDefList() {
        return this.defList;
    }
    
    public void setDefList(final List<KfzbBattleArmy> defList) {
        this.defList = defList;
    }
    
    public ArrayList<KfzbCampArmy> getAttCamp() {
        return this.attCamp;
    }
    
    public void setAttCamp(final ArrayList<KfzbCampArmy> attCamp) {
        this.attCamp = attCamp;
    }
    
    public ArrayList<KfzbCampArmy> getDefCamp() {
        return this.defCamp;
    }
    
    public void setDefCamp(final ArrayList<KfzbCampArmy> defCamp) {
        this.defCamp = defCamp;
    }
    
    public int getAttPos() {
        return this.attPos;
    }
    
    public void setAttPos(final int attPos) {
        this.attPos = attPos;
    }
    
    public int getDefPos() {
        return this.defPos;
    }
    
    public void setDefPos(final int defPos) {
        this.defPos = defPos;
    }
    
    public int chooseStrategyOrTactic(final boolean isAtt, final int pos, final int tacticId, int strategyId) {
        strategyId = tacticId;
        synchronized (this) {
            KfzbBattleArmy battleArmy = null;
            if (isAtt) {
                battleArmy = this.getCurAttBattleArmy();
            }
            else {
                battleArmy = this.getCurDefBattleArmy();
            }
            if (battleArmy == null) {
                // monitorexit(this)
                return 2;
            }
            if (battleArmy.getSpecial() == 1) {
                // monitorexit(this)
                return 2;
            }
            KfzbBattle.battleLog.info("choosenST:" + isAtt + "_" + pos + "_" + strategyId + "_" + battleArmy.getPosition() + "_" + battleArmy.getStrategy());
            if (strategyId == 100) {
                if (battleArmy.getCampArmy().tacticVal <= 0) {
                    // monitorexit(this)
                    return 2;
                }
                if (battleArmy.choose) {
                    // monitorexit(this)
                    return 2;
                }
                battleArmy.choose = true;
                KfzbBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseTatic");
                final boolean candoFightNow = this.canDoFightImmediately();
                if (candoFightNow) {
                    this.runRound(this.fightRound + 1, 3);
                }
                // monitorexit(this)
                return 1;
            }
            else if (strategyId > 0) {
                if (battleArmy.getStrategy() > 0) {
                    KfzbBattle.battleLog.error("hasUSedStr" + strategyId);
                    // monitorexit(this)
                    return 2;
                }
                final int[] strs = battleArmy.getCampArmy().getStrategies();
                boolean inStrs = false;
                int[] array;
                for (int length = (array = strs).length, i = 0; i < length; ++i) {
                    final int st = array[i];
                    if (strategyId == st) {
                        inStrs = true;
                        break;
                    }
                }
                if (!inStrs) {
                    KfzbBattle.battleLog.error("errorStr=isAtt" + isAtt + " :" + battleArmy.getCampArmy().getGeneralName() + strategyId + " not in" + strs[0] + "-" + strs[1] + "-" + strs[2]);
                    // monitorexit(this)
                    return 2;
                }
                battleArmy.setStrategy(strategyId);
                KfzbBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseStrategy");
                final boolean candoFightNow2 = this.canDoFightImmediately();
                if (candoFightNow2) {
                    this.runRound(this.fightRound + 1, 3);
                }
                // monitorexit(this)
                return 1;
            }
        }
        return 2;
    }
    
    public void iniBattleInfo(final CampArmyParam[] p1FightData, final CampArmyParam[] p2FightData) {
        this.terrain = KfzbCommonConstants.getRanTerrain(this.match.getMatchId(), this.match.getRound(), this.match.getSeasonId());
        this.terrainVal = KfzbCommonConstants.getTerrainValByTerrain(this.terrain);
        this.buildArmyData(this.attCamp, this.attList, p1FightData, true);
        this.buildArmyData(this.defCamp, this.defList, p2FightData, false);
        final StringBuilder battleMsg = new StringBuilder();
        final long delay = 6000L;
        this.nextRoundAttTime = System.currentTimeMillis() + delay;
        this.getIniBattleMsg(battleMsg);
        KfzbBuilder.sendMsgToAll(this, battleMsg);
    }
    
    public void getIniBattleMsg(final StringBuilder battleMsgInput) {
        synchronized (this) {
            StringBuilder battleMsg = new StringBuilder();
            final List<KfzbBattleArmy> attUpList = new ArrayList<KfzbBattleArmy>();
            for (int i = this.attPos; i < Math.min(8 + this.attPos, this.attList.size()); ++i) {
                attUpList.add(this.attList.get(i));
            }
            final List<KfzbBattleArmy> defUpList = new ArrayList<KfzbBattleArmy>();
            for (int j = this.defPos; j < Math.min(8 + this.defPos, this.defList.size()); ++j) {
                defUpList.add(this.defList.get(j));
            }
            KfzbBuilder.getReportType2(battleMsg, attUpList, "att");
            KfzbBuilder.getReportType2(battleMsg, defUpList, "def");
            if (this.attList.size() > 0 && this.getCurAttBattleArmy() != null) {
                final KfzbBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
                KfzbBuilder.getReportType16(this, battleMsg, this.attList.get(this.attPos).getCampArmy(), "att", true, true, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
            if (this.defList.size() > 0 && this.getCurDefBattleArmy() != null) {
                final KfzbBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
                KfzbBuilder.getReportType16(this, battleMsg, this.defList.get(this.defPos).getCampArmy(), "def", true, true, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
            KfzbBuilder.getReportType27(battleMsg, new KfzbRoundInfo(0), this);
            long delay = this.nextRoundAttTime - System.currentTimeMillis();
            if (delay < 0L) {
                delay = 0L;
            }
            final String rTitle = this.getRoundTitleIni();
            final StringBuilder cd = new StringBuilder();
            KfzbBuilder.getReportType26(cd, delay);
            battleMsg = new StringBuilder(cd).append(battleMsg);
            battleMsg = new StringBuilder(rTitle).append(battleMsg);
            battleMsgInput.append(battleMsg);
            String[] split;
            for (int length = (split = battleMsg.toString().split("#")).length, k = 0; k < length; ++k) {
                final String s = split[k];
            }
        }
    }
    
    public static double getTerrainValue(final int terrainType, final Troop troop, final boolean isAtt, final KfzbCampArmy campArmy) {
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
        campArmy.terrainAdd = effect;
        return campArmy.getTerrain();
    }
    
    private void buildArmyData(final ArrayList<KfzbCampArmy> campList, final List<KfzbBattleArmy> armyList, final CampArmyParam[] p1FightData, final boolean isAtt) {
        campList.clear();
        armyList.clear();
        int pos = 0;
        final int player1Win = this.match.getPlayer1Win();
        final int player2Win = this.match.getPlayer2Win();
        final int round = this.match.getRound();
        final boolean needChange = KfzbMatchService.getNeedChangeFromMatch(this.match.getMatchId(), round);
        int revengeBuff = 0;
        if (isAtt) {
            if (!needChange) {
                revengeBuff = player2Win;
            }
            else {
                revengeBuff = player1Win;
            }
        }
        else if (!needChange) {
            revengeBuff = player1Win;
        }
        else {
            revengeBuff = player2Win;
        }
        for (final CampArmyParam cap : p1FightData) {
            final KfzbCampArmy wdca = new KfzbCampArmy();
            BeanUtils.copyProperties(cap, wdca, new String[] { "terrainAttDefAdd" });
            wdca.revengeBuff = revengeBuff;
            int[] strategyArray = new int[3];
            final Troop troop = TroopCache.getTroopCacheById(wdca.getTroopId());
            if (isAtt) {
                strategyArray = troop.getStrategyMap().get(this.terrainVal);
            }
            else {
                strategyArray = troop.getStrategyDefMap().get(this.terrainVal);
            }
            final int strategyLengh = strategyArray.length;
            wdca.setStrategies(strategyArray);
            wdca.setKfspecialGeneral(cap.getKfspecialGeneral());
            wdca.setGemAttribute(cap.getGemAttribute());
            if (wdca.getTacicId() > 0) {
                wdca.setTacticVal(1);
                if (wdca.getKfspecialGeneral().generalType == 7) {
                    wdca.setTacticVal((int)wdca.getKfspecialGeneral().param);
                }
            }
            else {
                wdca.setTacticVal(0);
            }
            getTerrainValue(this.terrainVal, troop, isAtt, wdca);
            campList.add(wdca);
            final int troopHp = wdca.getTroopHp();
            int armyHp = wdca.getArmyHp();
            while (armyHp >= 3) {
                final int colHp = (armyHp < troopHp) ? armyHp : troopHp;
                final int perArmyHp = colHp / 3;
                final KfzbBattleArmy battleArmy = new KfzbBattleArmy();
                battleArmy.setChoose(false);
                battleArmy.setCampArmy(wdca);
                battleArmy.setPosition(pos);
                battleArmy.setDefaultStrategy(strategyArray[WebUtil.nextInt(strategyLengh)]);
                final int[] colHpList = new int[3];
                for (int i = 0; i < colHpList.length; ++i) {
                    colHpList[i] = perArmyHp;
                }
                battleArmy.setTroopHp(colHpList);
                armyHp -= colHp;
                ++pos;
                if (armyHp < 3) {
                    battleArmy.setGeneralLastArmy(true);
                }
                armyList.add(battleArmy);
            }
        }
    }
    
    public void runBattle() {
        final long delay = 12000L;
        KfzbBattle.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                KfzbBattle.this.runRound(1, 2);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private void runRound(final int roundNum, final int runState) {
        FrameBattleReport report = null;
        synchronized (this) {
            KfzbRoundInfo kfzbRoundInfo = null;
            try {
                if (roundNum < this.fightRound + 1) {
                    // monitorexit(this)
                    return;
                }
                if (this.state == 1) {
                    // monitorexit(this)
                    return;
                }
                if (runState == 1) {
                    final boolean candoFightNow = this.canDoFightImmediately();
                    if (!candoFightNow) {
                        KfzbBattle.exeutors.schedule(new Runnable() {
                            @Override
                            public void run() {
                                KfzbBattle.this.runRound(roundNum, 2);
                            }
                        }, 6000L, TimeUnit.MILLISECONDS);
                        // monitorexit(this)
                        return;
                    }
                    KfzbBattle.battleReportLog.info("doImmediate");
                }
                if (this.match.getLayer() <= 4) {
                    report = new FrameBattleReport();
                    report.setFrame(this.fightRound + 1);
                    boolean rewardNone = false;
                    final KfzbBattleArmy attFirstArmy = this.getCurAttBattleArmy();
                    final KfzbBattleArmy defFirstArmy = this.getCurDefBattleArmy();
                    if (attFirstArmy == null || defFirstArmy == null) {
                        rewardNone = true;
                    }
                    else if (attFirstArmy.getCampArmy() == this.attList.get(this.attList.size() - 1).getCampArmy() || defFirstArmy.getCampArmy() == this.defList.get(this.defList.size() - 1).getCampArmy()) {
                        rewardNone = true;
                    }
                    if (this.match.getLayer() >= 3 && this.match.getRound() >= 2) {
                        rewardNone = true;
                    }
                    final StringBuilder sb = new StringBuilder();
                    this.getIniBattleMsg(sb);
                    report.setState(2);
                    report.setIniReport(sb.toString());
                }
                kfzbRoundInfo = this.doRoundFight(this.fightRound);
                ++this.fightRound;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final long delay;
            final long d2 = delay = kfzbRoundInfo.nextMaxExeTime;
            this.nextRoundAttTime = System.currentTimeMillis() + delay;
            if (this.match.getLayer() <= 4 && report != null) {
                report.setBattleReport(kfzbRoundInfo.battleMsg.toString());
                KfzbScheduleService.addNewFrameReport(report, this.match);
            }
            KfzbBuilder.sendMsgToAll(this, kfzbRoundInfo.battleMsg);
            KfzbBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#delay=" + delay);
            if (kfzbRoundInfo.state != 2) {
                final int runNextRound = this.fightRound + 1;
                KfzbBattle.exeutors.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfzbBattle.this.runRound(runNextRound, 1);
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
            else {
                this.state = 1;
                final FightResult fightRes = this.setBattleRes(kfzbRoundInfo);
                fightRes.setBattle(this);
                try {
                    KfzbMatchService.doFinishBattle(fightRes, this.match);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    private boolean canDoFightImmediately() {
        final boolean res = true;
        final int attId = this.match.getPlayer1Id();
        final int defId = this.match.getPlayer2Id();
        final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(attId, 3));
        if (session != null && !this.canUseTactic(true, true) && this.getCurAttBattleArmy() != null && this.getCurAttBattleArmy().strategy == 0) {
            return false;
        }
        final Session session2 = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(defId, 3));
        return (session2 == null || this.canUseTactic(false, true) || this.getCurDefBattleArmy() == null || this.getCurDefBattleArmy().strategy != 0) && res;
    }
    
    private FightResult setBattleRes(final KfzbRoundInfo roundInfo) {
        final FightResult fightRes = new FightResult();
        fightRes.setAttRemainNum(this.attList.size() - this.attPos);
        fightRes.setDefRemainNum(this.defList.size() - this.defPos);
        if (this.attPos < this.attList.size()) {
            fightRes.setAttWin(true);
        }
        else {
            fightRes.setAttWin(false);
        }
        final int attLost = 0;
        final int defLost = 0;
        int attTotalArmy = 0;
        for (final KfzbCampArmy campArmy : this.attCamp) {
            attTotalArmy += campArmy.maxForces;
        }
        int defTotalArmy = 0;
        for (final KfzbCampArmy campArmy2 : this.defCamp) {
            defTotalArmy += campArmy2.maxForces;
        }
        int attCurArmy = 0;
        for (final KfzbBattleArmy battleArmy : this.attList) {
            attCurArmy += battleArmy.getTroopHp()[0] * 3;
        }
        int defCurArmy = 0;
        for (final KfzbBattleArmy battleArmy2 : this.defList) {
            defCurArmy += battleArmy2.getTroopHp()[0] * 3;
        }
        fightRes.setAttKilledForce(attTotalArmy - attCurArmy);
        fightRes.setDefKilledForce(defTotalArmy - defCurArmy);
        return fightRes;
    }
    
    private KfzbRoundInfo doRoundFight(final int roundNum) {
        final KfzbRoundInfo roundInfo = new KfzbRoundInfo(roundNum);
        final String rTitle = this.getRoundTitle();
        KfzbBuilder.getCurCampInfo(this, roundInfo);
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
            final KfzbBattleArmy army1Def = this.defList.get(this.defPos);
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
        KfzbBuilder.getReportType14(this, roundInfo);
        KfzbBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            KfzbBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        }
        this.doMakeReport16(roundInfo);
        KfzbBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            KfzbBuilder.getReportType2(roundInfo.battleMsg, roundInfo.attUpList, "att");
            KfzbBuilder.getReportType2(roundInfo.battleMsg, roundInfo.defUpList, "def");
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfzbBuilder.getReportType26(cd, delay);
            KfzbBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
            roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
            return roundInfo;
        }
        this.doNormalFight(roundInfo);
        this.doCheckProcessFightResult(roundInfo);
        KfzbBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        KfzbBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        KfzbBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        this.doMakeReport16(roundInfo);
        KfzbBuilder.getReportType2(roundInfo.battleMsg, roundInfo.attUpList, "att");
        KfzbBuilder.getReportType2(roundInfo.battleMsg, roundInfo.defUpList, "def");
        if (roundInfo.state != 0) {
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfzbBuilder.getReportType26(cd, delay);
            KfzbBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
        }
        this.doProcessFightRes(roundInfo);
        roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
        return roundInfo;
    }
    
    private boolean canReBound(final boolean isAtt) {
        final KfzbBattleArmy army1Att = this.attList.get(this.attPos);
        final KfzbBattleArmy army1Def = this.defList.get(this.defPos);
        final KfzbCampArmy attCamp = army1Att.getCampArmy();
        final KfzbCampArmy defCamp = army1Def.getCampArmy();
        KfzbCampArmy camp1 = null;
        KfzbBattleArmy army1 = null;
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
    
    private void doMakeReport16(final KfzbRoundInfo roundInfo) {
        if (roundInfo.killAttG && this.attPos < this.attList.size()) {
            final KfzbBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
            if (this.getCurAttBattleArmy() != null) {
                KfzbBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", true, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        else {
            final KfzbBattleArmy targetBattleArmy = this.getCurDefBattleArmy();
            if (this.getCurAttBattleArmy() != null) {
                KfzbBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", false, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        if (roundInfo.killDefG && this.defPos < this.defList.size()) {
            final KfzbBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
            if (this.getCurDefBattleArmy() != null) {
                KfzbBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", true, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
        else {
            final KfzbBattleArmy targetBattleArmy = this.getCurAttBattleArmy();
            if (this.getCurDefBattleArmy() != null) {
                KfzbBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", false, false, (targetBattleArmy != null) ? targetBattleArmy.getCampArmy() : null);
            }
        }
    }
    
    private String getRoundTitle() {
        final String matchId = "kfzb_" + this.match.getMatchId() + "_" + this.match.getRound();
        return String.valueOf(this.fightRound) + "|" + matchId + "#";
    }
    
    public String getRoundTitleIni() {
        final String matchId = "kfzb_" + this.match.getMatchId() + "_" + this.match.getRound();
        return String.valueOf(this.fightRound) + "|" + matchId + "|" + "ini" + "#";
    }
    
    public static String getRoundTitle(final KfzbRuntimeMatch match) {
        final String matchId = "kfzb_" + match.getMatchId() + "_" + match.getRound();
        return "0|" + matchId + "#";
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
    
    private void doNormalFight(final KfzbRoundInfo roundInfo) {
        final KfzbBattleArmy attArmy = this.attList.get(this.attPos);
        final KfzbBattleArmy defArmy = this.defList.get(this.defPos);
        final TroopData[] attacker = new TroopData[3];
        this.copyTacticBattleInfo(attArmy, attacker, defArmy, false);
        final TroopData[] defender = new TroopData[3];
        this.copyTacticBattleInfo(defArmy, defender, attArmy, false);
        final String[] st = Fight.fight(attacker, defender);
        roundInfo.reports = st;
        int winRes = 0;
        for (int i = 0; i < 3; ++i) {
            final int attLostHp = attArmy.getTroopHp()[i] - attacker[i].hp;
            roundInfo.attLost += attLostHp;
            roundInfo.attRemain += attacker[i].hp;
            final KfzbCampArmy campArmy = attArmy.getCampArmy();
            campArmy.armyHpLoss += attLostHp;
            final int defLostHp = defArmy.getTroopHp()[i] - defender[i].hp;
            roundInfo.defLost += defLostHp;
            roundInfo.defRemain += defender[i].hp;
            attArmy.getTroopHp()[i] = attacker[i].hp;
            defArmy.getTroopHp()[i] = defender[i].hp;
            final KfzbCampArmy campArmy2 = defArmy.getCampArmy();
            campArmy2.armyHpLoss += defLostHp;
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
        KfzbBuilder.getReportType3(roundInfo.battleMsg, this, roundInfo);
    }
    
    private void useStrategy(final KfzbRoundInfo roundInfo) {
        final KfzbBattleArmy attArmy = this.attList.get(this.attPos);
        final KfzbBattleArmy defArmy = this.defList.get(this.defPos);
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
            final KfzbCampArmy defCampArmy = roundInfo.defCampArmy;
            defCampArmy.armyHpLoss += defLost;
            final KfzbCampArmy attCampArmy = roundInfo.attCampArmy;
            attCampArmy.armyHpKill += defLost;
            roundInfo.attStrategyLost += attLost;
            final KfzbCampArmy attCampArmy2 = roundInfo.attCampArmy;
            attCampArmy2.armyHpLoss += attLost;
            final KfzbCampArmy defCampArmy2 = roundInfo.defCampArmy;
            defCampArmy2.armyHpKill += attLost;
            final boolean lostBlood = true;
            if (lostBlood) {
                roundInfo.nextMaxExeTime += 1000;
                roundInfo.timePredicationBuffer.append("strategy reduce:").append(1000).append("|");
            }
        }
    }
    
    private void doProcessFightRes(final KfzbRoundInfo fightRes) {
        this.printTroopDatas();
    }
    
    private void useTactic(final boolean isAttUseTactic, final KfzbRoundInfo roundInfo, final boolean reBound) {
        KfzbBattleArmy army1Att = this.getCurAttBattleArmy();
        KfzbBattleArmy army1Def = this.getCurDefBattleArmy();
        KfzbCampArmy attCamp = army1Att.getCampArmy();
        KfzbCampArmy defCamp = army1Def.getCampArmy();
        int defSize = this.defList.size() - this.defPos;
        int defTaticEffectSize = (defSize > 6) ? 6 : defSize;
        List<KfzbBattleArmy> realAttList = this.attList;
        List<KfzbBattleArmy> realDefList = this.defList;
        int realDefPos = this.defPos;
        int readAttPos = this.attPos;
        if (!isAttUseTactic) {
            realAttList = this.defList;
            realDefList = this.attList;
            realDefPos = this.attPos;
            readAttPos = this.defPos;
            army1Att = this.defList.get(this.defPos);
            army1Def = this.attList.get(this.attPos);
            attCamp = army1Att.getCampArmy();
            defCamp = army1Def.getCampArmy();
            defSize = this.attList.size() - this.attPos;
            defTaticEffectSize = ((defSize > 6) ? 6 : defSize);
        }
        roundInfo.tacticStrategyResult = 3;
        final KfzbTacticInfo tacticInfo = new KfzbTacticInfo();
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
            final KfzbTacticInfo targetTacticInfo = new KfzbTacticInfo();
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
                final KfzbCampArmy kfzbCampArmy = defCamp;
                --kfzbCampArmy.tacticVal;
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
        roundInfo.nextMaxExeTime += 1000;
        roundInfo.timePredicationBuffer.append("tactic reduce:").append(1000).append("|");
        for (int i = realDefPos; i < realDefList.size(); ++i) {
            final KfzbBattleArmy ba = realDefList.get(i);
            if (ba.getTD_defense_e() > 0.0) {
                tacticInfo.attacked_guanyu = true;
                break;
            }
        }
        final TroopData[] attacker = new TroopData[3];
        this.copyTacticBattleInfo(army1Att, attacker, army1Def, reBound);
        final TroopData[][] defenders = new TroopData[defTaticEffectSize][3];
        this.copyTacticBattleInfo(realDefList, realDefPos, defenders, army1Att);
        final String tacticString = Tactic.tacticAttack(attacker, defenders);
        tacticInfo.tacticStr = tacticString;
        army1Att.setStrategy(army1Att.getUsedStrategy());
        final KfzbCampArmy kfzbCampArmy2 = attCamp;
        --kfzbCampArmy2.tacticVal;
        tacticInfo.executed = true;
        if (tacticInfo.tacticStr.equalsIgnoreCase("SP")) {
            tacticInfo.beStop = true;
            roundInfo.timePredicationBuffer.append("tactic is stoped:").append(0).append("|");
            return;
        }
        final General generalA = GeneralCache.getGeneralById(attCamp.getGeneralId());
        if (generalA != null && generalA.getGeneralSpecialInfo() != null && generalA.getGeneralSpecialInfo().generalType == 10) {
            for (int wuShenFuTiLimit = generalA.getGeneralSpecialInfo().rowNum, j = readAttPos; j < wuShenFuTiLimit + readAttPos && j < realAttList.size(); ++j) {
                realAttList.get(j).setTD(true);
                realAttList.get(j).setTD_defense_e(generalA.getGeneralSpecialInfo().param2);
            }
        }
        if (tacticInfo.specialType > 0) {
            if (tacticInfo.specialType == 2) {
                return;
            }
            if (tacticInfo.specialType == 1) {
                final int range = tactic.getRange();
                final StringBuilder sb = new StringBuilder();
                for (int k = 0; k < range; ++k) {
                    final int newPos = realDefPos + k;
                    if (newPos >= realDefList.size()) {
                        break;
                    }
                    final KfzbBattleArmy dfArmy = realDefList.get(newPos);
                    dfArmy.setSpecial(1);
                    sb.append(dfArmy.getPosition()).append(",");
                }
                tacticInfo.columnStr = sb.toString();
            }
        }
        this.doProcessTacticResult(tacticString, isAttUseTactic, roundInfo);
    }
    
    private boolean canUseTactic(final boolean isAtt) {
        return this.canUseTactic(isAtt, false);
    }
    
    private boolean canUseTactic(final boolean isAtt, final boolean checkDoImmediately) {
        final KfzbBattleArmy army1Att = this.attList.get(this.attPos);
        final KfzbBattleArmy army1Def = this.defList.get(this.defPos);
        final KfzbCampArmy attCamp = army1Att.getCampArmy();
        final KfzbCampArmy defCamp = army1Def.getCampArmy();
        KfzbCampArmy camp1 = null;
        KfzbBattleArmy army1 = null;
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
        KfzbBattle.battleReportLog.info("doFirst=" + (isAtt ? "att" : "def") + "#" + army1.getPosition() + army1.isFirstAction + "#" + (army1.strategy == 0) + "#" + (army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0)));
        return camp1.tacticVal > 0 && ((army1.choose && isFirstDoAction) || (!checkDoImmediately && army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0))) && army1.getSpecial() != 1;
    }
    
    private void doCheckProcessFightResult(final KfzbRoundInfo roundInfo) {
        boolean roundFinish = false;
        boolean battleFinish = false;
        final int oldDefPos = this.defPos;
        final KfzbBattleArmy firstAttArmy = this.attList.get(this.attPos);
        final KfzbBattleArmy firstDefArmy = this.defList.get(this.defPos);
        while (this.defList.get(this.defPos).troopHp[0] == 0) {
            roundFinish = true;
            roundInfo.defKilledList.add(this.defList.get(this.defPos));
            if (this.defPos + 8 < this.defList.size()) {
                roundInfo.defUpList.add(this.defList.get(this.defPos + 8));
            }
            roundInfo.defFirstRowKilled = true;
            roundInfo.win = 1;
            if (this.defPos + 1 >= this.defList.size() || !this.defList.get(this.defPos).getCampArmy().equals(this.defList.get(this.defPos + 1).getCampArmy())) {
                final KfzbCampArmy campArmy = firstAttArmy.getCampArmy();
                ++campArmy.killGeneral;
                roundInfo.needPushReport13 = true;
                roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                final KfzbCampArmy defendCa = this.defList.get(this.defPos).getCampArmy();
                defendCa.armyHp = -1;
                if (this.defPos == oldDefPos) {
                    roundInfo.killDefG = true;
                }
            }
            ++this.defPos;
            if (this.defPos >= this.defList.size()) {
                battleFinish = true;
                break;
            }
        }
        final int oldAttPos = this.attPos;
        while (this.attList.get(this.attPos).troopHp[0] == 0) {
            roundFinish = true;
            roundInfo.attKilledList.add(this.attList.get(this.attPos));
            if (this.attPos + 8 < this.attList.size()) {
                roundInfo.attUpList.add(this.attList.get(this.attPos + 8));
            }
            roundInfo.attFirstRowKilled = true;
            if (roundInfo.win == 1) {
                roundInfo.win = 3;
            }
            else {
                roundInfo.win = 2;
            }
            if (this.attPos + 1 >= this.attList.size() || !this.attList.get(this.attPos).getCampArmy().equals(this.attList.get(this.attPos + 1).getCampArmy())) {
                final KfzbCampArmy campArmy2 = firstDefArmy.getCampArmy();
                ++campArmy2.killGeneral;
                roundInfo.needPushReport13 = true;
                roundInfo.nextMaxExeTime += 0;
                roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                final KfzbCampArmy defendCa2 = this.attList.get(this.attPos).getCampArmy();
                defendCa2.armyHp = -1;
                if (this.attPos == oldAttPos) {
                    roundInfo.killAttG = true;
                }
            }
            ++this.attPos;
            if (this.attPos >= this.attList.size()) {
                battleFinish = true;
                break;
            }
        }
        if (battleFinish) {
            roundInfo.state = 2;
        }
        else if (roundFinish) {
            roundInfo.state = 1;
        }
    }
    
    private void doProcessTacticResult(final String tacticString, final boolean isDefLostHp, final KfzbRoundInfo roundInfo) {
        if (!StringUtils.isEmpty(tacticString)) {
            final String[] ts = tacticString.split(";");
            int dpos = this.defPos;
            List<KfzbBattleArmy> armyList = this.defList;
            KfzbBattleArmy attArmy = this.attList.get(this.attPos);
            if (!isDefLostHp) {
                dpos = this.attPos;
                armyList = this.attList;
                attArmy = this.defList.get(this.defPos);
            }
            KfzbTacticInfo tacticInfo = new KfzbTacticInfo();
            if (isDefLostHp) {
                tacticInfo = roundInfo.attTacticInfo;
            }
            else {
                tacticInfo = roundInfo.defTacticInfo;
            }
            final int firstDefCampId = armyList.get(dpos).getCampArmy().getId();
            final Map<KfzbCampArmy, Integer> reduceMap = new HashMap<KfzbCampArmy, Integer>();
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
                final KfzbBattleArmy curArmy = armyList.get(dpos);
                final KfzbCampArmy defendCa = curArmy.getCampArmy();
                for (int j = 0; j < ts2.length; ++j) {
                    final String ts3 = ts2[j];
                    int reduce = Integer.parseInt(ts3);
                    if (reduce > armyList.get(dpos).troopHp[j]) {
                        reduce = armyList.get(dpos).troopHp[j];
                    }
                    curArmy.troopHp[j] = armyList.get(dpos).troopHp[j] - reduce;
                    if (defendCa.getId() == firstDefCampId) {
                        final KfzbTacticInfo kfzbTacticInfo = tacticInfo;
                        kfzbTacticInfo.firstCReduce += reduce;
                    }
                    final KfzbTacticInfo kfzbTacticInfo2 = tacticInfo;
                    kfzbTacticInfo2.allCReduce += reduce;
                    final KfzbCampArmy kfzbCampArmy = defendCa;
                    kfzbCampArmy.armyHpLoss += reduce;
                    if (reduceMap.containsKey(defendCa)) {
                        reduceMap.put(defendCa, reduceMap.get(defendCa) + reduce);
                    }
                    else {
                        reduceMap.put(defendCa, reduce);
                    }
                    tacticInfo.reduceMap = reduceMap;
                    final KfzbCampArmy campArmy = attArmy.getCampArmy();
                    campArmy.armyHpKill += tacticInfo.allCReduce;
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
    
    private void copyTacticBattleInfo(final List<KfzbBattleArmy> armyList, final int armyPos, final TroopData[][] defenders, final KfzbBattleArmy targetArmy) {
        for (int i = 0; i < defenders.length; ++i) {
            final KfzbBattleArmy bArmy = armyList.get(armyPos + i);
            if (bArmy == null) {
                return;
            }
            final TroopData[] troopData = new TroopData[3];
            this.copyTacticBattleInfo(bArmy, troopData, targetArmy, false);
            defenders[i] = troopData;
        }
    }
    
    private void copyTacticBattleInfo(KfzbBattleArmy battleArmy, final TroopData[] troopData, final KfzbBattleArmy targetArmy, final boolean reBound) {
        if (reBound && targetArmy != null && targetArmy.getCampArmy() != null) {
            battleArmy = targetArmy;
        }
        final KfzbCampArmy campArmy = battleArmy.getCampArmy();
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
            final TroopData troopData2;
            final TroopData tempData = troopData2 = new TroopData();
            troopData2.base_damage += KfzbCommonConstants.getRevengeEffect(campArmy.revengeBuff);
            tempData.troop_id = campArmy.getId();
            tempData.hp = battleArmy.getTroopHp()[i];
            tempData.max_hp = campArmy.getTroopHp() / 3;
            tempData.att = att;
            tempData.def = def;
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
            boolean isBS = false;
            final int attackerBSNum = this.getLivedCamp(true);
            final int defenderBSNum = this.getLivedCamp(false);
            tempData.isYX = isYX;
            tempData.YX_cur_Blood = YX_cur_Blood;
            tempData.YX_max_Blood = YX_max_Blood;
            tempData.isTD = battleArmy.isTD();
            tempData.TD_defense_e = battleArmy.getTD_defense_e();
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
                tempData.JS_SKILL_ms = gemAttribute.skillMs;
                tempData.JS_SKILL_bj = gemAttribute.skillBj;
                tempData.JS_SKILL_att = gemAttribute.skillAtt;
                tempData.JS_SKILL_zfbj = gemAttribute.skillZfbj;
                tempData.JS_SKILL_zfjb = gemAttribute.skillZfjb;
            }
            troopData[i] = tempData;
        }
    }
    
    private int getLivedCamp(final boolean isAtt) {
        KfzbBattleArmy ba = null;
        KfzbCampArmy ca = null;
        ArrayList<KfzbCampArmy> caList = null;
        if (isAtt) {
            ba = this.getCurAttBattleArmy();
            caList = this.attCamp;
        }
        else {
            ba = this.getCurDefBattleArmy();
            caList = this.defCamp;
        }
        if (ba != null) {
            ca = ba.getCampArmy();
        }
        if (ca != null && ba != null && caList != null) {
            final int allCampNum = caList.size();
            final int nowGId = ca.getGeneralId();
            int nowPos = 0;
            for (int i = 0; i < allCampNum; ++i) {
                if (nowGId == caList.get(i).generalId) {
                    nowPos = i;
                    break;
                }
            }
            return allCampNum - nowPos + 1;
        }
        return 0;
    }
    
    public static void main(final String[] args) {
        final CampArmyParam cap = new CampArmyParam();
        final KfzbCampArmy wdca = new KfzbCampArmy();
        BeanUtils.copyProperties(cap, wdca, new String[] { "terrainAttDefAdd" });
    }
    
    public KfzbBattleArmy getCurAttBattleArmy() {
        if (this.attPos >= this.attList.size()) {
            return null;
        }
        return this.attList.get(this.attPos);
    }
    
    public KfzbBattleArmy getCurDefBattleArmy() {
        if (this.defPos >= this.defList.size()) {
            return null;
        }
        return this.defList.get(this.defPos);
    }
    
    public KfzbBattleArmy getLivedBattleArmy(final int i, final int orgPos, final boolean isAtt) {
        if (isAtt) {
            if (i + orgPos >= this.attList.size()) {
                return null;
            }
            return this.attList.get(i + orgPos);
        }
        else {
            if (i + orgPos >= this.defList.size()) {
                return null;
            }
            return this.defList.get(i + orgPos);
        }
    }
    
    public StringBuilder getResReport7(final int competitorId) {
        final StringBuilder sb = new StringBuilder();
        final String rTitle = this.getRoundTitle();
        sb.append(rTitle);
        long nextRoundCdShowCD = 0L;
        nextRoundCdShowCD = this.match.getStartTime().getTime() + KfzbTimeControlService.getBattleInterval() * 1000L - System.currentTimeMillis();
        final StringBuilder battleRes = this.res.getResReport7(competitorId, nextRoundCdShowCD, this.match);
        sb.append(battleRes);
        return sb;
    }
    
    public void setBattleRoundBuff(final KfzbCampArmy attCa, final KfzbCampArmy defCa, final KfzbCampArmy targetCampArmy) {
        this.attBuffListRound.clear();
        this.defBuffListRound.clear();
        if (this.attList.size() == 0 || this.defList.size() == 0) {
            return;
        }
        if (attCa != null && attCa.getTerrainAdd() > 0 && (targetCampArmy == null || targetCampArmy.getKfspecialGeneral() == null || targetCampArmy.getKfspecialGeneral().getGeneralType() != 11)) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 23 + attCa.getTerrainQ();
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.getTerrianName(), attCa.getTerrainAdd() });
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
                temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.getTerrianName(), defCa.getTerrainAdd() });
            }
            this.defBuffListRound.add(temp);
        }
        if (attCa != null) {
            GemAttribute attGemAttr = null;
            attGemAttr = attCa.getGemAttribute();
            if (attGemAttr != null) {
                if (attGemAttr.skillMs > 0.0) {
                    final Tuple<Integer, String> temp2 = new Tuple();
                    temp2.left = 45;
                    temp2.right = LocalMessages.BUFF_TIPS_45;
                    this.attBuffListRound.add(temp2);
                }
                if (attGemAttr.skillAtt > 0.0) {
                    final Tuple<Integer, String> temp2 = new Tuple();
                    temp2.left = 48;
                    temp2.right = LocalMessages.BUFF_TIPS_48;
                    this.attBuffListRound.add(temp2);
                }
            }
        }
        if (defCa != null) {
            final GemAttribute defGemAttr = defCa.getGemAttribute();
            if (defGemAttr != null) {
                if (defGemAttr.skillMs > 0.0) {
                    final Tuple<Integer, String> temp2 = new Tuple();
                    temp2.left = 45;
                    temp2.right = LocalMessages.BUFF_TIPS_45;
                    this.defBuffListRound.add(temp2);
                }
                if (defGemAttr.skillDef > 0.0) {
                    final Tuple<Integer, String> temp2 = new Tuple();
                    temp2.left = 47;
                    temp2.right = LocalMessages.BUFF_TIPS_47;
                    this.defBuffListRound.add(temp2);
                }
            }
        }
        if (attCa != null && attCa.getKfspecialGeneral().generalType == 11) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 44;
            temp.right = LocalMessages.BUFF_TIPS_44;
            this.attBuffListRound.add(temp);
        }
        if (defCa != null && defCa.getKfspecialGeneral().generalType == 11) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 44;
            temp.right = LocalMessages.BUFF_TIPS_44;
            this.defBuffListRound.add(temp);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 4) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 30;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 2 && (int)attCa.kfspecialGeneral.param > 0) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 31;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 3) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 32;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(attCa.kfspecialGeneral.param * 100.0) });
            this.attBuffListRound.add(temp);
        }
        if (attCa != null && attCa.kfspecialGeneral.generalType == 8) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 33;
            temp.right = LocalMessages.BUFF_TIPS_33;
            this.attBuffListRound.add(temp);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 4) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 30;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 2 && (int)defCa.kfspecialGeneral.param > 0) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 31;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 3) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 32;
            temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(defCa.kfspecialGeneral.param * 100.0) });
            this.defBuffListRound.add(temp);
        }
        if (defCa != null && defCa.kfspecialGeneral.generalType == 8) {
            final Tuple<Integer, String> temp = new Tuple();
            temp.left = 33;
            temp.right = LocalMessages.BUFF_TIPS_33;
            this.defBuffListRound.add(temp);
        }
    }
    
    public String getTerrianName() {
        String terrainName = "";
        switch (this.terrainVal) {
            case 1: {
                terrainName = LocalMessages.TERRAIN_NAME_1;
                break;
            }
            case 2: {
                terrainName = LocalMessages.TERRAIN_NAME_2;
                break;
            }
            case 3: {
                terrainName = LocalMessages.TERRAIN_NAME_3;
                break;
            }
            case 4: {
                terrainName = LocalMessages.TERRAIN_NAME_4;
                break;
            }
        }
        return terrainName;
    }
}
