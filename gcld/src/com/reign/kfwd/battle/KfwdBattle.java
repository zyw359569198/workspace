package com.reign.kfwd.battle;

import com.reign.kfwd.domain.*;
import org.apache.commons.logging.*;
import com.reign.kfwd.constants.*;
import com.reign.kf.match.sdata.common.*;
import org.springframework.beans.*;
import com.reign.kf.match.common.util.*;
import java.util.concurrent.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.framework.netty.servlet.*;
import ast.gcldcore.fight.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kfwd.service.*;

public class KfwdBattle
{
    public static final int STATE_FINSHED = 1;
    private static Log battleLog;
    private static Log battleReportLog;
    int battleType;
    int fightRound;
    KfwdBattleRes res;
    public KfwdRuntimeMatch match;
    long battleId;
    List<KfwdBattleArmy> attList;
    List<KfwdBattleArmy> defList;
    int attPos;
    int defPos;
    ArrayList<KfwdCampArmy> attCamp;
    ArrayList<KfwdCampArmy> defCamp;
    int terrain;
    public int terrainVal;
    public int state;
    public long nextRoundAttTime;
    static ScheduledThreadPoolExecutor exeutors;
    public static final int STATE_MINTIMERUN = 1;
    public static final int STATE_MAXTIMERUN = 2;
    public static final int STATE_IMMEDIATERUN = 3;
    
    static {
        KfwdBattle.battleLog = LogFactory.getLog("mj.kfwd.battle.log");
        KfwdBattle.battleReportLog = LogFactory.getLog("mj.kfwd.battleReport.log");
        KfwdBattle.exeutors = new ScheduledThreadPoolExecutor(3);
    }
    
    public KfwdBattle() {
        this.fightRound = 0;
        this.attList = new ArrayList<KfwdBattleArmy>(8);
        this.defList = new ArrayList<KfwdBattleArmy>(8);
        this.attPos = 0;
        this.defPos = 0;
        this.attCamp = new ArrayList<KfwdCampArmy>();
        this.defCamp = new ArrayList<KfwdCampArmy>();
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
    
    public KfwdBattleRes getRes() {
        return this.res;
    }
    
    public KfwdBattleRes getNotNullRes() {
        if (this.res == null) {
            (this.res = new KfwdBattleRes()).setPlayer1Id(this.match.getPlayer1Id());
            this.res.setPlayer2Id(this.match.getPlayer2Id());
        }
        return this.res;
    }
    
    public void setRes(final KfwdBattleRes res) {
        this.res = res;
    }
    
    public KfwdRuntimeMatch getMatch() {
        return this.match;
    }
    
    public void setMatch(final KfwdRuntimeMatch match) {
        this.match = match;
    }
    
    public long getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final long battleId) {
        this.battleId = battleId;
    }
    
    public List<KfwdBattleArmy> getAttList() {
        return this.attList;
    }
    
    public void setAttList(final List<KfwdBattleArmy> attList) {
        this.attList = attList;
    }
    
    public List<KfwdBattleArmy> getDefList() {
        return this.defList;
    }
    
    public void setDefList(final List<KfwdBattleArmy> defList) {
        this.defList = defList;
    }
    
    public ArrayList<KfwdCampArmy> getAttCamp() {
        return this.attCamp;
    }
    
    public void setAttCamp(final ArrayList<KfwdCampArmy> attCamp) {
        this.attCamp = attCamp;
    }
    
    public ArrayList<KfwdCampArmy> getDefCamp() {
        return this.defCamp;
    }
    
    public void setDefCamp(final ArrayList<KfwdCampArmy> defCamp) {
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
            KfwdBattleArmy battleArmy = null;
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
            KfwdBattle.battleLog.info("choosenST:" + isAtt + "_" + pos + "_" + strategyId + "_" + battleArmy.getPosition() + "_" + battleArmy.getStrategy());
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
                KfwdBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseTatic");
                final boolean candoFightNow = this.canDoFightImmediately();
                if (candoFightNow) {
                    this.runRound(this.fightRound + 1, 3);
                }
                // monitorexit(this)
                return 1;
            }
            else if (strategyId > 0) {
                if (battleArmy.getStrategy() > 0) {
                    KfwdBattle.battleLog.error("hasUSedStr" + strategyId);
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
                    KfwdBattle.battleLog.error("errorStr=isAtt" + isAtt + " :" + battleArmy.getCampArmy().getGeneralName() + strategyId + " not in" + strs[0] + "-" + strs[1] + "-" + strs[2]);
                    // monitorexit(this)
                    return 2;
                }
                battleArmy.setStrategy(strategyId);
                KfwdBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#chooseStrategy");
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
        this.terrain = KfwdConstantsAndMethod.getRanTerrain(this.match.getMatchId(), this.match.getRound(), this.match.getSeasonId(), this.match.getScheduleId());
        this.terrainVal = KfwdConstantsAndMethod.getTerrainValByTerrain(this.terrain);
        this.buildArmyData(this.attCamp, this.attList, p1FightData, true);
        this.buildArmyData(this.defCamp, this.defList, p2FightData, false);
        final StringBuilder battleMsg = new StringBuilder();
        final long delay = 6000L;
        this.nextRoundAttTime = System.currentTimeMillis() + delay;
        this.getIniBattleMsg(battleMsg);
        KfwdBuilder.sendMsgToAll(this, battleMsg);
    }
    
    public void getIniBattleMsg(final StringBuilder battleMsgInput) {
        synchronized (this) {
            StringBuilder battleMsg = new StringBuilder();
            final List<KfwdBattleArmy> attUpList = new ArrayList<KfwdBattleArmy>();
            for (int i = this.attPos; i < Math.min(8 + this.attPos, this.attList.size()); ++i) {
                attUpList.add(this.attList.get(i));
            }
            final List<KfwdBattleArmy> defUpList = new ArrayList<KfwdBattleArmy>();
            for (int j = this.defPos; j < Math.min(8 + this.defPos, this.defList.size()); ++j) {
                defUpList.add(this.defList.get(j));
            }
            KfwdBuilder.getReportType2(battleMsg, attUpList, "att");
            KfwdBuilder.getReportType2(battleMsg, defUpList, "def");
            if (this.attList.size() > 0 && this.getCurAttBattleArmy() != null) {
                KfwdBuilder.getReportType16(this, battleMsg, this.attList.get(this.attPos).getCampArmy(), "att", true, true);
            }
            if (this.defList.size() > 0 && this.getCurDefBattleArmy() != null) {
                KfwdBuilder.getReportType16(this, battleMsg, this.defList.get(this.defPos).getCampArmy(), "def", true, true);
            }
            KfwdBuilder.getReportType27(battleMsg, new KfwdRoundInfo(0), this);
            long delay = this.nextRoundAttTime - System.currentTimeMillis();
            if (delay < 0L) {
                delay = 0L;
            }
            final String rTitle = this.getRoundTitleIni();
            final StringBuilder cd = new StringBuilder();
            KfwdBuilder.getReportType26(cd, delay);
            battleMsg = new StringBuilder(cd).append(battleMsg);
            battleMsg = new StringBuilder(rTitle).append(battleMsg);
            battleMsgInput.append(battleMsg);
            String[] split;
            for (int length = (split = battleMsg.toString().split("#")).length, k = 0; k < length; ++k) {
                final String s = split[k];
            }
        }
    }
    
    public static double getTerrainValue(final int terrainType, final Troop troop, final boolean isAtt, final KfwdCampArmy campArmy) {
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
    
    private void buildArmyData(final ArrayList<KfwdCampArmy> campList, final List<KfwdBattleArmy> armyList, final CampArmyParam[] p1FightData, final boolean isAtt) {
        campList.clear();
        armyList.clear();
        int pos = 0;
        for (final CampArmyParam cap : p1FightData) {
            final KfwdCampArmy wdca = new KfwdCampArmy();
            BeanUtils.copyProperties(cap, wdca, new String[] { "terrainAttDefAdd" });
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
                final KfwdBattleArmy battleArmy = new KfwdBattleArmy();
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
            campList.add(wdca);
        }
    }
    
    public void runBattle() {
        final long delay = 12000L;
        KfwdBattle.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                KfwdBattle.this.runRound(1, 2);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private void runRound(final int roundNum, final int runState) {
        synchronized (this) {
            KfwdRoundInfo kfwdRoundInfo = null;
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
                        KfwdBattle.exeutors.schedule(new Runnable() {
                            @Override
                            public void run() {
                                KfwdBattle.this.runRound(roundNum, 2);
                            }
                        }, 6000L, TimeUnit.MILLISECONDS);
                        // monitorexit(this)
                        return;
                    }
                    KfwdBattle.battleReportLog.info("doImmediate");
                }
                kfwdRoundInfo = this.doRoundFight(this.fightRound);
                ++this.fightRound;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final long delay;
            final long d2 = delay = kfwdRoundInfo.nextMaxExeTime;
            this.nextRoundAttTime = System.currentTimeMillis() + delay;
            KfwdBuilder.sendMsgToAll(this, kfwdRoundInfo.battleMsg);
            KfwdBattle.battleReportLog.info(String.valueOf(this.getRoundTitle()) + "#delay=" + delay);
            if (kfwdRoundInfo.state != 2) {
                final int runNextRound = this.fightRound + 1;
                KfwdBattle.exeutors.schedule(new Runnable() {
                    @Override
                    public void run() {
                        KfwdBattle.this.runRound(runNextRound, 1);
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
            else {
                this.state = 1;
                final FightResult fightRes = this.setBattleRes(kfwdRoundInfo);
                fightRes.setBattle(this);
                KfwdMatchService.doFinishBattle(fightRes, this.match);
            }
        }
    }
    
    private boolean canDoFightImmediately() {
        final boolean res = true;
        final int attId = this.match.getPlayer1Id();
        final int defId = this.match.getPlayer2Id();
        final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(attId, 1));
        if (session != null && !this.canUseTactic(true, true) && this.getCurAttBattleArmy() != null && this.getCurAttBattleArmy().strategy == 0) {
            return false;
        }
        final Session session2 = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(defId, 1));
        return (session2 == null || this.canUseTactic(false, true) || this.getCurDefBattleArmy() == null || this.getCurDefBattleArmy().strategy != 0) && res;
    }
    
    private FightResult setBattleRes(final KfwdRoundInfo roundInfo) {
        final FightResult fightRes = new FightResult();
        fightRes.setAttRemainNum(this.attList.size() - this.attPos);
        fightRes.setDefRemainNum(this.defList.size() - this.defPos);
        if (this.attPos < this.attList.size()) {
            fightRes.setAttWin(true);
        }
        else {
            fightRes.setAttWin(false);
        }
        int attLost = 0;
        for (final KfwdCampArmy campArmy : this.attCamp) {
            attLost += campArmy.armyHpLoss;
        }
        fightRes.setAttKilledForce(attLost);
        int defLost = 0;
        for (final KfwdCampArmy campArmy2 : this.defCamp) {
            defLost += campArmy2.armyHpLoss;
        }
        fightRes.setDefKilledForce(defLost);
        return fightRes;
    }
    
    private KfwdRoundInfo doRoundFight(final int roundNum) {
        final KfwdRoundInfo roundInfo = new KfwdRoundInfo(roundNum);
        final String rTitle = this.getRoundTitle();
        KfwdBuilder.getCurCampInfo(this, roundInfo);
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
            final KfwdBattleArmy army1Def = this.defList.get(this.defPos);
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
        KfwdBuilder.getReportType14(this, roundInfo);
        KfwdBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            KfwdBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        }
        this.doMakeReport16(roundInfo);
        KfwdBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        if (roundInfo.state != 0) {
            KfwdBuilder.getReportType2(roundInfo.battleMsg, roundInfo.attUpList, "att");
            KfwdBuilder.getReportType2(roundInfo.battleMsg, roundInfo.defUpList, "def");
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfwdBuilder.getReportType26(cd, delay);
            KfwdBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
            roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
            return roundInfo;
        }
        this.doNormalFight(roundInfo);
        this.doCheckProcessFightResult(roundInfo);
        KfwdBuilder.getReportType31(roundInfo.battleMsg, roundInfo);
        KfwdBuilder.getReportType30(roundInfo.battleMsg, roundInfo);
        KfwdBuilder.getReportType20(this, roundInfo.battleMsg, roundInfo);
        this.doMakeReport16(roundInfo);
        KfwdBuilder.getReportType2(roundInfo.battleMsg, roundInfo.attUpList, "att");
        KfwdBuilder.getReportType2(roundInfo.battleMsg, roundInfo.defUpList, "def");
        if (roundInfo.state != 0) {
            final long delay = roundInfo.nextMaxExeTime + 6000L;
            final StringBuilder cd = new StringBuilder();
            KfwdBuilder.getReportType26(cd, delay);
            KfwdBuilder.getReportType27(roundInfo.battleMsg = new StringBuilder(cd).append(roundInfo.battleMsg), roundInfo, this);
        }
        this.doProcessFightRes(roundInfo);
        roundInfo.battleMsg = new StringBuilder(rTitle).append(roundInfo.battleMsg);
        return roundInfo;
    }
    
    private boolean canReBound(final boolean isAtt) {
        final KfwdBattleArmy army1Att = this.attList.get(this.attPos);
        final KfwdBattleArmy army1Def = this.defList.get(this.defPos);
        final KfwdCampArmy attCamp = army1Att.getCampArmy();
        final KfwdCampArmy defCamp = army1Def.getCampArmy();
        KfwdCampArmy camp1 = null;
        KfwdBattleArmy army1 = null;
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
    
    private void doMakeReport16(final KfwdRoundInfo roundInfo) {
        if (roundInfo.killAttG && this.attPos < this.attList.size()) {
            if (this.getCurAttBattleArmy() != null) {
                KfwdBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", true, false);
            }
        }
        else if (this.getCurAttBattleArmy() != null) {
            KfwdBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurAttBattleArmy().getCampArmy(), "att", false, false);
        }
        if (roundInfo.killDefG && this.defPos < this.defList.size()) {
            if (this.getCurDefBattleArmy() != null) {
                KfwdBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", true, false);
            }
        }
        else if (this.getCurDefBattleArmy() != null) {
            KfwdBuilder.getReportType16(this, roundInfo.battleMsg, this.getCurDefBattleArmy().getCampArmy(), "def", false, false);
        }
    }
    
    private String getRoundTitle() {
        final String matchId = "kfwd_" + this.match.getMatchId() + "_" + this.match.getRound() + "_" + this.match.getsRound();
        return String.valueOf(this.fightRound) + "|" + matchId + "#";
    }
    
    public String getRoundTitleIni() {
        final String matchId = "kfwd_" + this.match.getMatchId() + "_" + this.match.getRound() + "_" + this.match.getsRound();
        return String.valueOf(this.fightRound) + "|" + matchId + "|" + "ini" + "#";
    }
    
    public static String getRoundTitle(final KfwdRuntimeMatch match) {
        final String matchId = "kfwd_" + match.getMatchId() + "_" + match.getRound() + "_" + match.getsRound();
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
    
    private void doNormalFight(final KfwdRoundInfo roundInfo) {
        final KfwdBattleArmy attArmy = this.attList.get(this.attPos);
        final TroopData[] attacker = new TroopData[3];
        final KfwdBattleArmy defArmy = this.defList.get(this.defPos);
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
            final KfwdCampArmy campArmy = attArmy.getCampArmy();
            campArmy.armyHpLoss += attLostHp;
            final int defLostHp = defArmy.getTroopHp()[i] - defender[i].hp;
            roundInfo.defLost += defLostHp;
            roundInfo.defRemain += defender[i].hp;
            attArmy.getTroopHp()[i] = attacker[i].hp;
            defArmy.getTroopHp()[i] = defender[i].hp;
            final KfwdCampArmy campArmy2 = defArmy.getCampArmy();
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
        KfwdBuilder.getReportType3(roundInfo.battleMsg, this, roundInfo);
    }
    
    private void useStrategy(final KfwdRoundInfo roundInfo) {
        final KfwdBattleArmy attArmy = this.attList.get(this.attPos);
        final KfwdBattleArmy defArmy = this.defList.get(this.defPos);
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
            final KfwdCampArmy defCampArmy = roundInfo.defCampArmy;
            defCampArmy.armyHpLoss += roundInfo.defStrategyLost;
            final KfwdCampArmy attCampArmy = roundInfo.attCampArmy;
            attCampArmy.armyHpKill += roundInfo.defStrategyLost;
            roundInfo.attStrategyLost += attLost;
            final KfwdCampArmy attCampArmy2 = roundInfo.attCampArmy;
            attCampArmy2.armyHpLoss += roundInfo.attStrategyLost;
            final KfwdCampArmy defCampArmy2 = roundInfo.defCampArmy;
            defCampArmy2.armyHpKill += roundInfo.attStrategyLost;
            final boolean lostBlood = true;
            if (lostBlood) {
                roundInfo.nextMaxExeTime += 1000;
                roundInfo.timePredicationBuffer.append("strategy reduce:").append(1000).append("|");
            }
        }
    }
    
    private void doProcessFightRes(final KfwdRoundInfo fightRes) {
        this.printTroopDatas();
    }
    
    private void useTactic(final boolean isAttUseTactic, final KfwdRoundInfo roundInfo, final boolean reBound) {
        KfwdBattleArmy army1Att = this.getCurAttBattleArmy();
        KfwdBattleArmy army1Def = this.getCurDefBattleArmy();
        KfwdCampArmy attCamp = army1Att.getCampArmy();
        KfwdCampArmy defCamp = army1Def.getCampArmy();
        int defSize = this.defList.size() - this.defPos;
        int defTaticEffectSize = (defSize > 6) ? 6 : defSize;
        List<KfwdBattleArmy> realAttList = this.attList;
        List<KfwdBattleArmy> realDefList = this.defList;
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
        final KfwdTacticInfo tacticInfo = new KfwdTacticInfo();
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
            final KfwdTacticInfo targetTacticInfo = new KfwdTacticInfo();
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
                final KfwdCampArmy kfwdCampArmy = defCamp;
                --kfwdCampArmy.tacticVal;
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
            final KfwdBattleArmy ba = realDefList.get(i);
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
        final KfwdCampArmy kfwdCampArmy2 = attCamp;
        --kfwdCampArmy2.tacticVal;
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
                    final KfwdBattleArmy dfArmy = realDefList.get(newPos);
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
        final KfwdBattleArmy army1Att = this.attList.get(this.attPos);
        final KfwdBattleArmy army1Def = this.defList.get(this.defPos);
        final KfwdCampArmy attCamp = army1Att.getCampArmy();
        final KfwdCampArmy defCamp = army1Def.getCampArmy();
        KfwdCampArmy camp1 = null;
        KfwdBattleArmy army1 = null;
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
        KfwdBattle.battleReportLog.info("doFirst=" + (isAtt ? "att" : "def") + "#" + army1.getPosition() + army1.isFirstAction + "#" + (army1.strategy == 0) + "#" + (army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0)));
        return camp1.tacticVal > 0 && ((army1.choose && isFirstDoAction) || (!checkDoImmediately && army1.isGeneralLastArmy && (!isFirstDoAction || army1.strategy == 0))) && army1.getSpecial() != 1;
    }
    
    private void doCheckProcessFightResult(final KfwdRoundInfo roundInfo) {
        boolean roundFinish = false;
        boolean battleFinish = false;
        final int oldDefPos = this.defPos;
        final KfwdBattleArmy firstAttArmy = this.attList.get(this.attPos);
        final KfwdBattleArmy firstDefArmy = this.defList.get(this.defPos);
        while (this.defList.get(this.defPos).troopHp[0] == 0) {
            roundFinish = true;
            roundInfo.defKilledList.add(this.defList.get(this.defPos));
            if (this.defPos + 8 < this.defList.size()) {
                roundInfo.defUpList.add(this.defList.get(this.defPos + 8));
            }
            roundInfo.defFirstRowKilled = true;
            roundInfo.win = 1;
            if (this.defPos + 1 >= this.defList.size() || !this.defList.get(this.defPos).getCampArmy().equals(this.defList.get(this.defPos + 1).getCampArmy())) {
                final KfwdCampArmy campArmy = firstAttArmy.getCampArmy();
                ++campArmy.killGeneral;
                roundInfo.needPushReport13 = true;
                roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                final KfwdCampArmy defendCa = this.defList.get(this.defPos).getCampArmy();
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
                final KfwdCampArmy campArmy2 = firstDefArmy.getCampArmy();
                ++campArmy2.killGeneral;
                roundInfo.needPushReport13 = true;
                roundInfo.nextMaxExeTime += 0;
                roundInfo.timePredicationBuffer.append("tactic cheers:").append(0).append("|");
                final KfwdCampArmy defendCa2 = this.attList.get(this.attPos).getCampArmy();
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
    
    private void doProcessTacticResult(final String tacticString, final boolean isDefLostHp, final KfwdRoundInfo roundInfo) {
        if (!StringUtils.isEmpty(tacticString)) {
            final String[] ts = tacticString.split(";");
            int dpos = this.defPos;
            List<KfwdBattleArmy> armyList = this.defList;
            KfwdBattleArmy attArmy = this.attList.get(this.attPos);
            if (!isDefLostHp) {
                dpos = this.attPos;
                armyList = this.attList;
                attArmy = this.defList.get(this.defPos);
            }
            KfwdTacticInfo tacticInfo = new KfwdTacticInfo();
            if (isDefLostHp) {
                tacticInfo = roundInfo.attTacticInfo;
            }
            else {
                tacticInfo = roundInfo.defTacticInfo;
            }
            final int firstDefCampId = armyList.get(dpos).getCampArmy().getId();
            final Map<KfwdCampArmy, Integer> reduceMap = new HashMap<KfwdCampArmy, Integer>();
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
                final KfwdBattleArmy curArmy = armyList.get(dpos);
                final KfwdCampArmy defendCa = curArmy.getCampArmy();
                for (int j = 0; j < ts2.length; ++j) {
                    final String ts3 = ts2[j];
                    int reduce = Integer.parseInt(ts3);
                    if (reduce > armyList.get(dpos).troopHp[j]) {
                        reduce = armyList.get(dpos).troopHp[j];
                    }
                    curArmy.troopHp[j] = armyList.get(dpos).troopHp[j] - reduce;
                    if (defendCa.getId() == firstDefCampId) {
                        final KfwdTacticInfo kfwdTacticInfo = tacticInfo;
                        kfwdTacticInfo.firstCReduce += reduce;
                    }
                    final KfwdTacticInfo kfwdTacticInfo2 = tacticInfo;
                    kfwdTacticInfo2.allCReduce += reduce;
                    final KfwdCampArmy kfwdCampArmy = defendCa;
                    kfwdCampArmy.armyHpLoss += reduce;
                    if (reduceMap.containsKey(defendCa)) {
                        reduceMap.put(defendCa, reduceMap.get(defendCa) + reduce);
                    }
                    else {
                        reduceMap.put(defendCa, reduce);
                    }
                    tacticInfo.reduceMap = reduceMap;
                    final KfwdCampArmy campArmy = attArmy.getCampArmy();
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
    
    private void copyTacticBattleInfo(final List<KfwdBattleArmy> armyList, final int armyPos, final TroopData[][] defenders, final KfwdBattleArmy targetArmy) {
        for (int i = 0; i < defenders.length; ++i) {
            final KfwdBattleArmy bArmy = armyList.get(armyPos + i);
            if (bArmy == null) {
                return;
            }
            final TroopData[] troopData = new TroopData[3];
            this.copyTacticBattleInfo(bArmy, troopData, targetArmy, false);
            defenders[i] = troopData;
        }
    }
    
    private void copyTacticBattleInfo(KfwdBattleArmy battleArmy, final TroopData[] troopData, final KfwdBattleArmy targetArmy, final boolean reBound) {
        if (reBound && targetArmy != null && targetArmy.getCampArmy() != null) {
            battleArmy = targetArmy;
        }
        final KfwdCampArmy campArmy = battleArmy.getCampArmy();
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
        KfwdBattleArmy ba = null;
        KfwdCampArmy ca = null;
        ArrayList<KfwdCampArmy> caList = null;
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
        final KfwdCampArmy wdca = new KfwdCampArmy();
        BeanUtils.copyProperties(cap, wdca, new String[] { "terrainAttDefAdd" });
    }
    
    public KfwdBattleArmy getCurAttBattleArmy() {
        if (this.attPos >= this.attList.size()) {
            return null;
        }
        return this.attList.get(this.attPos);
    }
    
    public KfwdBattleArmy getCurDefBattleArmy() {
        if (this.defPos >= this.defList.size()) {
            return null;
        }
        return this.defList.get(this.defPos);
    }
    
    public KfwdBattleArmy getLivedBattleArmy(final int i, final int orgPos, final boolean isAtt) {
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
        final long nextRoundCd = KfwdTimeControlService.getRunDelayMillSecondsByRound(this.match.getRound() + 1, this.match.getScheduleId());
        sb.append(this.res.getResReport7(competitorId, nextRoundCd));
        return sb;
    }
}
