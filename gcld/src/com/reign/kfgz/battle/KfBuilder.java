package com.reign.kfgz.battle;

import com.reign.util.*;
import org.apache.commons.lang.*;
import com.reign.kf.match.sdata.common.*;
import java.util.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.resource.*;
import com.reign.kf.match.sdata.domain.*;

public class KfBuilder
{
    public static final int WORLD_CITY_ATTACKER_TACTIC_HALF = 1;
    public static final int GUANYU_WUSHENFUTI_TACTIC_HALF = 2;
    public static final int GEM_SKILL_RENGXING_TACTIC_HALF = 3;
    
    public static void getCurCampInfo(final KfBattle bat, final KfBattleRoundInfo roundInfo) {
        roundInfo.attBattleArmy = bat.getCurAttBattleArmy();
        roundInfo.defBattleArmy = bat.getCurDefBattleArmy();
        if (roundInfo.attBattleArmy == null || roundInfo.defBattleArmy == null) {
            System.out.println("!!!errror");
        }
        roundInfo.attCampArmy = roundInfo.attBattleArmy.getCampArmy();
        roundInfo.defCampArmy = roundInfo.defBattleArmy.getCampArmy();
        getReportType16(bat, roundInfo.battleMsg, roundInfo.attCampArmy, "att", false, false, roundInfo.defCampArmy);
        getReportType16(bat, roundInfo.battleMsg, roundInfo.defCampArmy, "def", false, false, roundInfo.attCampArmy);
    }
    
    public static void getReportType2(final StringBuilder battleMsg, final List<KfBattleArmy> addQlist, final String battleSide) {
        KfBattleArmy battleArmy = null;
        if (addQlist.size() > 0) {
            battleMsg.append(2).append("|").append(battleSide).append(";");
            for (int i = 0; i < addQlist.size(); ++i) {
                battleArmy = addQlist.get(i);
                int specialType = (battleArmy.getCampArmy().getTeamGenreal() == null) ? 0 : ((battleArmy.getCampArmy().getTeamEffect() > 0.0) ? 2 : 1);
                if (battleArmy.getTD_defense_e() > 0.0) {
                    specialType = 3;
                }
                final int troopHpMax = battleArmy.getCampArmy().getTroopHp() / 3;
                battleMsg.append(battleArmy.getPosition()).append("|").append(battleArmy.getCampArmy().getPlayerId()).append("|").append(battleArmy.getCampArmy().getTroopSerial()).append("|").append(battleArmy.getCampArmy().getTroopType()).append("|").append(battleArmy.getCampArmy().getTroopName()).append("|").append(battleArmy.getCampArmy().getTroopDropType()).append("|").append(battleArmy.getStrategy()).append("|").append(specialType).append("|");
                for (int j = 0; j < 3; ++j) {
                    battleMsg.append(battleArmy.getTroopHp()[j]).append("*").append(troopHpMax).append(",");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), ";");
            }
            battleMsg.append("#");
        }
    }
    
    protected static void getReportType16(final KfBattle bat, final StringBuilder battleMsg, final KfCampArmy campArmy, final String battleSide, final boolean campArmyChanged, final boolean first, final KfCampArmy targetCampArmy) {
        battleMsg.append(16).append("|").append(battleSide).append("|").append(campArmy.generalLv).append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("|").append(campArmy.killGeneral).append("|").append(";");
        String playerName = campArmy.playerName;
        playerName = campArmy.playerName;
        battleMsg.append(campArmy.getGeneralInfo().getpInfo().getForceId()).append("|").append(playerName).append("|").append(campArmy.generalName).append("|").append(campArmy.generalPic).append("|").append(campArmy.quality).append("|").append(0).append("|");
        String teamTips = campArmy.getTeamGenreal();
        if (campArmy.getTeamGenreal() != null && campArmy.getTeamEffect() > 0.0) {
            teamTips = String.valueOf(teamTips) + MessageFormatter.format("\u58eb\u6c14\u9ad8\u6602\uff1a\u653b\u9632\u4e0a\u5347{0}%!", new Object[] { campArmy.getTeamEffect() * 100.0 });
        }
        battleMsg.append(teamTips).append("|").append((campArmy.getTeamEffect() > 0.0) ? 1 : 0).append(";");
        boolean thirdPartAdded = false;
        try {
            final List<Tuple<Integer, String>> reportBuffList = new LinkedList<Tuple<Integer, String>>();
            if (battleSide.equals("att")) {
                bat.setBattleRoundBuff(campArmy, null, targetCampArmy);
                reportBuffList.addAll(bat.attBuffListInit);
                reportBuffList.addAll(bat.attBuffListRound);
            }
            else {
                bat.setBattleRoundBuff(null, campArmy, targetCampArmy);
                reportBuffList.addAll(bat.defBuffListInit);
                reportBuffList.addAll(bat.defBuffListRound);
            }
            for (int i = reportBuffList.size() - 1; i >= 0; --i) {
                final Tuple<Integer, String> buff = reportBuffList.get(i);
                final Tactic tactic = TacticCache.getTacticById(campArmy.tacicId);
                if (tactic != null && tactic.getSpecialEffect() != null && tactic.getSpecialEffect().equalsIgnoreCase("siege_gun") && buff.left.equals(10)) {
                    reportBuffList.remove(i);
                }
            }
            if (reportBuffList.size() > 0) {
                for (final Tuple<Integer, String> buff2 : reportBuffList) {
                    if (buff2 == null) {
                        break;
                    }
                    battleMsg.append(buff2.left).append("*").append(buff2.right).append("|");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
                thirdPartAdded = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (!thirdPartAdded) {
            battleMsg.append("null").append("#");
        }
    }
    
    protected static void getReportType27(final StringBuilder battleMsg, final KfBattleRoundInfo roundInfo, final KfBattle bat) {
        KfCampArmy attCa = null;
        final KfBattleArmy attBa = bat.getCurAttBattleArmy();
        if (attBa != null) {
            attCa = attBa.getCampArmy();
        }
        KfCampArmy defCa = null;
        final KfBattleArmy defBa = bat.getCurDefBattleArmy();
        if (defBa != null) {
            defCa = defBa.getCampArmy();
        }
        if (attCa == null || defCa == null) {
            return;
        }
        boolean attNextChoose = false;
        if (attBa != null && !attBa.choose && attBa.strategy == 0 && !attBa.getCampArmy().isPhantom) {
            if (attCa.getGeneralInfo().isNotNpc() && attCa.getGeneralInfo().getpInfo().isAutoStg()) {
                attNextChoose = true;
                attBa.choose = true;
                final int defSt = defBa.getStrategy();
                final int[] attSts = attCa.getStrategies();
                if (defSt > 0) {
                    final FightStragtegyCoe fsc = FightStragtegyCoeCache.getAttWin(defSt, attSts);
                    final int defWinSt = (fsc != null) ? fsc.getAttStrategy() : attSts[0];
                    attBa.setStrategy(defWinSt);
                }
            }
            else if (attBa.getCampArmy().getPlayerId() > 0) {
                final int playerId = attCa.playerId;
                int canChooseTac = 0;
                if (attBa.getSpecial() > 0) {
                    canChooseTac = 1;
                }
                else if (defCa.getKfspecialGeneral().generalType == 8) {
                    canChooseTac = 2;
                }
                final FightStragtegyCoe fsc = FightStragtegyCoeCache.getAttWin(defBa.getUsedStrategy(), attCa.strategies);
                battleMsg.append(27).append("|").append("att").append("|").append(playerId).append("|").append(attBa.getPosition()).append("|").append(attCa.playerName).append("|").append(0).append("|").append(1).append(";");
                battleMsg.append(fsc.getAttStrategy()).append("|").append(attCa.strategies[0]).append("|").append(FightStrategiesCache.getStrategyPic(attCa.strategies[0])).append("|").append(attCa.strategies[1]).append("|").append(FightStrategiesCache.getStrategyPic(attCa.strategies[1])).append("|").append(attCa.strategies[2]).append("|").append(FightStrategiesCache.getStrategyPic(attCa.strategies[2])).append(";");
                battleMsg.append(attBa.getPosition()).append("|").append(attBa.getCampArmy().getTacticVal()).append("|").append(attBa.getCampArmy().getGeneralPic()).append("|").append(canChooseTac).append("|").append(attBa.getSpecial()).append("#");
                attNextChoose = true;
            }
        }
        if (!attNextChoose && attBa != null) {
            battleMsg.append(27).append("|").append("att").append("|").append(attCa.playerId).append("|").append(attBa.getPosition()).append("|").append(attCa.playerName).append("|").append(0).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
        boolean defNextChoose = false;
        if (defBa != null && !defBa.choose && defBa.strategy == 0 && !defBa.getCampArmy().isPhantom) {
            if (defCa.getGeneralInfo().isNotNpc() && defCa.getGeneralInfo().getpInfo().isAutoStg()) {
                defNextChoose = true;
                defBa.choose = true;
                final int attSt = attBa.getStrategy();
                final int[] defSts = defCa.getStrategies();
                if (attSt > 0) {
                    final FightStragtegyCoe fsc2 = FightStragtegyCoeCache.getDefWin(attSt, defSts);
                    final int defWinSt2 = (fsc2 != null) ? fsc2.getDefStrategy() : defSts[0];
                    defBa.setStrategy(defWinSt2);
                }
            }
            else if (defBa.getCampArmy().getPlayerId() > 0) {
                final int playerId2 = defCa.playerId;
                int canChooseTac2 = 0;
                if (defBa.getSpecial() > 0) {
                    canChooseTac2 = 1;
                }
                else if (attCa.getKfspecialGeneral().generalType == 8) {
                    canChooseTac2 = 2;
                }
                final FightStragtegyCoe fsc2 = FightStragtegyCoeCache.getAttWin(attBa.getUsedStrategy(), defCa.strategies);
                battleMsg.append(27).append("|").append("def").append("|").append(playerId2).append("|").append(defBa.getPosition()).append("|").append(defCa.playerName).append("|").append(0).append("|").append(1).append(";");
                battleMsg.append(fsc2.getDefStrategy()).append("|").append(defCa.strategies[0]).append("|").append(FightStrategiesCache.getStrategyPic(defCa.strategies[0])).append("|").append(defCa.strategies[1]).append("|").append(FightStrategiesCache.getStrategyPic(defCa.strategies[1])).append("|").append(defCa.strategies[2]).append("|").append(FightStrategiesCache.getStrategyPic(defCa.strategies[2])).append(";");
                battleMsg.append(defBa.getPosition()).append("|").append(defBa.getCampArmy().getTacticVal()).append("|").append(defBa.getCampArmy().getGeneralPic()).append("|").append(canChooseTac2).append("|").append(defBa.getSpecial()).append("#");
                defNextChoose = true;
            }
        }
        if (!defNextChoose && defBa != null) {
            battleMsg.append(27).append("|").append("def").append("|").append(defCa.playerId).append("|").append(defBa.getPosition()).append("|").append(defCa.playerName).append("|").append(0).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
    }
    
    public static void getReportType14(final KfBattle bat, final KfBattleRoundInfo roundInfo) {
        final int attTaticPosNum = roundInfo.attBattleArmy.getPosition();
        final int defTaticPosNum = roundInfo.defBattleArmy.getPosition();
        roundInfo.battleMsg.append(14).append("|").append(roundInfo.tacticStrategyResult).append(";").append(attTaticPosNum).append("|").append(defTaticPosNum).append(";");
        if (roundInfo.attTacticInfo != null) {
            int attTacticReduceType = 0;
            final Tactic tactic = TacticCache.getTacticById(roundInfo.attTacticInfo.tacticId);
            if (bat.getKfTeam().terrainVal == 4 && tactic != null && tactic.getSpecialEffect() != null && !tactic.getSpecialEffect().equalsIgnoreCase("siege_gun")) {
                attTacticReduceType = 1;
            }
            if (roundInfo.attTacticInfo.zfJB) {
                attTacticReduceType = 3;
            }
            int zfBj = 0;
            if (roundInfo.attTacticInfo.zfBJ) {
                zfBj = 1;
            }
            if (roundInfo.attTacticInfo.attacked_guanyu) {
                attTacticReduceType = 2;
            }
            int stopType = 0;
            if (roundInfo.attRebound) {
                stopType = 2;
                if (roundInfo.attTacticInfo.beStop) {
                    stopType = 3;
                }
            }
            else if (roundInfo.attTacticInfo.beStop) {
                stopType = 1;
            }
            roundInfo.battleMsg.append(1).append(",").append(1).append(",").append(roundInfo.attTacticInfo.tacticNameId).append(",").append(stopType).append(",").append(roundInfo.attTacticInfo.beStop ? roundInfo.defCampArmy.getcDifyType() : roundInfo.attTacticInfo.tacticDisplayId).append(",").append(roundInfo.attTacticInfo.tacticBasicPic).append(",").append(roundInfo.attBattleArmy.getUsedStrategy()).append(",").append("null").append(",").append(attTacticReduceType).append(",").append(roundInfo.attBattleArmy.getCampArmy().getGeneralPic()).append(",").append(roundInfo.defBattleArmy.getCampArmy().getGeneralPic()).append(",").append(zfBj);
        }
        else {
            final FightStrategies attFT = FightStrategiesCache.getStr(roundInfo.attBattleArmy.getUsedStrategy());
            roundInfo.battleMsg.append(2).append(",").append(0).append(",").append(roundInfo.attBattleArmy.getUsedStrategy()).append(",").append(0).append(",").append(0).append(",").append(0).append(",").append(roundInfo.attBattleArmy.getUsedStrategy()).append(",").append((attFT == null) ? "" : attFT.getName()).append(",").append(0).append(",").append(0).append(",").append(0);
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.defTacticInfo != null) {
            int stopType2 = 0;
            if (roundInfo.defRebound) {
                stopType2 = 2;
                if (roundInfo.defTacticInfo.beStop) {
                    stopType2 = 3;
                }
            }
            else if (roundInfo.defTacticInfo.beStop) {
                stopType2 = 1;
            }
            int defTacticReduceType = 0;
            if (roundInfo.defTacticInfo.zfJB) {
                defTacticReduceType = 3;
            }
            int zfBj = 0;
            if (roundInfo.defTacticInfo.zfBJ) {
                zfBj = 1;
            }
            if (roundInfo.defTacticInfo.attacked_guanyu) {
                defTacticReduceType = 2;
            }
            roundInfo.battleMsg.append(1).append(",").append(roundInfo.defTacticInfo.executed ? 1 : 0).append(",").append(roundInfo.defTacticInfo.tacticNameId).append(",").append(stopType2).append(",").append(roundInfo.defTacticInfo.beStop ? roundInfo.attCampArmy.getcDifyType() : roundInfo.defTacticInfo.tacticDisplayId).append(",").append(roundInfo.defTacticInfo.tacticBasicPic).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append("null").append(",").append(defTacticReduceType).append(",").append(roundInfo.attBattleArmy.getCampArmy().getGeneralPic()).append(",").append(roundInfo.defBattleArmy.getCampArmy().getGeneralPic()).append(",").append(zfBj);
        }
        else {
            final FightStrategies defFT = FightStrategiesCache.getStr(roundInfo.defBattleArmy.getUsedStrategy());
            roundInfo.battleMsg.append(2).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append(0).append(",").append(0).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append((defFT == null) ? "" : defFT.getName()).append(",").append(0).append(",").append(0).append(",").append(0);
        }
        roundInfo.battleMsg.append(";");
        if (roundInfo.defTacticInfo != null) {
            if (StringUtils.isEmpty(roundInfo.defTacticInfo.tacticStr) || roundInfo.defTacticInfo.tacticStr.equalsIgnoreCase("SP")) {
                roundInfo.battleMsg.append("null");
            }
            else {
                final String[] strs = roundInfo.defTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length; ++i) {
                    final KfBattleArmy tdefArmy = roundInfo.defTacticInfo.defArmyList.get(i);
                    roundInfo.battleMsg.append(tdefArmy.getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(tdefArmy.getTroopHp()[j]).append(",");
                    }
                    roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), "*");
                }
            }
        }
        else if (roundInfo.attStrategyLost > 0) {
            final int lost = roundInfo.attStrategyLost / 3;
            roundInfo.battleMsg.append(roundInfo.attBattleArmy.getPosition()).append(",");
            for (int k = 0; k < roundInfo.attBattleArmy.getTroopHp().length; ++k) {
                roundInfo.battleMsg.append(lost).append(",").append(roundInfo.attBattleArmy.getTroopHp()[k]).append(",");
            }
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.attTacticInfo != null) {
            if (StringUtils.isEmpty(roundInfo.attTacticInfo.tacticStr) || roundInfo.attTacticInfo.tacticStr.equalsIgnoreCase("SP")) {
                roundInfo.battleMsg.append("null").append(";");
            }
            else {
                final String[] strs = roundInfo.attTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length; ++i) {
                    final KfBattleArmy tdefArmy = roundInfo.attTacticInfo.defArmyList.get(i);
                    roundInfo.battleMsg.append(tdefArmy.getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(tdefArmy.getTroopHp()[j]).append(",");
                    }
                    roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), "*");
                }
                roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), ";");
            }
        }
        else if (roundInfo.defStrategyLost > 0) {
            final int lost = roundInfo.defStrategyLost / 3;
            roundInfo.battleMsg.append(roundInfo.defBattleArmy.getPosition()).append(",");
            for (int i = 0; i < roundInfo.defBattleArmy.getTroopHp().length; ++i) {
                roundInfo.battleMsg.append(lost).append(",").append(roundInfo.defBattleArmy.getTroopHp()[0]).append(",");
            }
            roundInfo.battleMsg.replace(roundInfo.battleMsg.length() - 1, roundInfo.battleMsg.length(), ";");
        }
        else {
            roundInfo.battleMsg.append("null").append(";");
        }
        if (roundInfo.attTacticInfo != null) {
            if (roundInfo.attTacticInfo.beStop) {
                roundInfo.battleMsg.append("null");
            }
            else {
                roundInfo.battleMsg.append("null");
            }
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.defTacticInfo != null) {
            if (roundInfo.defTacticInfo.beStop) {
                roundInfo.battleMsg.append("null").append(";");
            }
            else {
                roundInfo.battleMsg.append("null").append(";");
            }
        }
        else {
            roundInfo.battleMsg.append("null").append(";");
        }
        if (roundInfo.defTacticInfo != null && !roundInfo.defTacticInfo.beStop && roundInfo.defTacticInfo.columnStr != null && roundInfo.defTacticInfo.columnStr != "") {
            roundInfo.battleMsg.append(roundInfo.defTacticInfo.columnStr);
        }
        else {
            roundInfo.battleMsg.append("null");
        }
        roundInfo.battleMsg.append("|");
        if (roundInfo.attTacticInfo != null && !roundInfo.attTacticInfo.beStop && roundInfo.attTacticInfo.columnStr != null && roundInfo.attTacticInfo.columnStr != "") {
            roundInfo.battleMsg.append(roundInfo.attTacticInfo.columnStr).append("#");
        }
        else {
            roundInfo.battleMsg.append("null").append("#");
        }
    }
    
    public static void getReportType30(final StringBuilder battleMsg, final KfBattleRoundInfo roundInfo) {
        battleMsg.append(30).append("|");
        if (roundInfo.attKilledList.size() > 0) {
            String sb1 = new String();
            for (final KfBattleArmy ba : roundInfo.attKilledList) {
                sb1 = String.valueOf(sb1) + ba.getPosition() + ",";
                battleMsg.append(ba.getPosition()).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
        }
        else {
            battleMsg.append(-1).append("|");
        }
        roundInfo.attKilledList.clear();
        if (roundInfo.defKilledList.size() > 0) {
            final String sb1 = new String();
            for (final KfBattleArmy ba : roundInfo.defKilledList) {
                battleMsg.append(ba.getPosition()).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
        else {
            battleMsg.append(-1).append("#");
        }
        roundInfo.defKilledList.clear();
    }
    
    public static void getReportType31(final StringBuilder battleMsg, final KfBattleRoundInfo roundInfo) {
        battleMsg.append(31).append("|").append(roundInfo.win).append("#");
    }
    
    public static void getReportType20(final KfBattle kfwdBattle, final StringBuilder battleMsg, final KfBattleRoundInfo roundInfo) {
        if (roundInfo.killDefG) {
            battleMsg.append(20).append("|").append("att").append(";").append(roundInfo.attCampArmy.id).append(";").append(roundInfo.attCampArmy.killGeneral).append(";").append(roundInfo.attCampArmy.playerId).append(";");
            boolean addNeeded = true;
            final KfCampArmy currentCA = roundInfo.attCampArmy;
            for (int i = 0; i < Math.min(8, kfwdBattle.attList.size()); ++i) {
                final KfBattleArmy ba = kfwdBattle.attList.get(i);
                if (ba.getCampArmy() == currentCA) {
                    addNeeded = false;
                    battleMsg.append(ba.getPosition()).append(",");
                }
            }
            if (addNeeded) {
                battleMsg.append("null").append("#");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
        if (roundInfo.killAttG) {
            battleMsg.append(20).append("|").append("def").append(";").append(roundInfo.defCampArmy.id).append(";").append(roundInfo.defCampArmy.killGeneral).append(";").append(roundInfo.defCampArmy.playerId).append(";");
            boolean addNeeded = true;
            final KfCampArmy currentCA = roundInfo.defCampArmy;
            for (int i = 0; i < Math.min(8, kfwdBattle.defList.size()); ++i) {
                final KfBattleArmy ba = kfwdBattle.defList.get(i);
                if (ba.getCampArmy() == currentCA) {
                    addNeeded = false;
                    battleMsg.append(ba.getPosition()).append(",");
                }
            }
            if (addNeeded) {
                battleMsg.append("null").append("#");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
    }
    
    public static void getReportType3(final StringBuilder battleMsg, final KfBattle bat, final KfBattleRoundInfo roundInfo) {
        final int len = roundInfo.reports.length;
        if (len > 0) {
            battleMsg.append(3).append("|").append(0).append("|").append(roundInfo.win).append(";");
            battleMsg.append(roundInfo.attBattleArmy.getPosition()).append("|").append(roundInfo.defBattleArmy.getPosition()).append(";");
            for (int i = 0; i < len; ++i) {
                battleMsg.append(roundInfo.reports[i].replace(";", ","));
                if (i < len - 1) {
                    battleMsg.append("*");
                }
            }
            battleMsg.append(";");
            int[] troopHp;
            for (int length = (troopHp = roundInfo.attBattleArmy.getTroopHp()).length, j = 0; j < length; ++j) {
                final int left = troopHp[j];
                battleMsg.append(left).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            int[] troopHp2;
            for (int length2 = (troopHp2 = roundInfo.defBattleArmy.getTroopHp()).length, k = 0; k < length2; ++k) {
                final int left = troopHp2[k];
                battleMsg.append(left).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
    }
    
    protected static void getReportType26(final StringBuilder battleMsg, final long delay) {
        battleMsg.append(26).append("|").append(delay).append("#");
    }
    
    public static void getReportType13(final KfBattle battle, final StringBuilder battleMsg) {
        final List<KfGeneralInfo> attCaList = battle.kfTeam.attGList;
        final List<KfGeneralInfo> defCaList = battle.kfTeam.defGList;
        if (attCaList.size() > 0 || defCaList.size() > 0) {
            battleMsg.append(13).append("|");
            if (attCaList.size() > 0) {
                for (int i = 0; i < attCaList.size() && i < 3; ++i) {
                    final KfGeneralInfo attGeneral = attCaList.get(i);
                    final KfCampArmy ca = attGeneral.getCampArmy();
                    int state = 0;
                    if (attGeneral.getState() == 3) {
                        state = 1;
                    }
                    else {
                        state = 2;
                    }
                    final int join = 0;
                    int playerId = ca.getPlayerId();
                    if (ca.isPhantom) {
                        playerId = -playerId;
                    }
                    final String nameAppend = "";
                    battleMsg.append(i + 1).append(",").append(playerId).append(",").append(ca.getPlayerName()).append(",").append(ca.getGeneralName()).append(",").append(ca.getQuality()).append(",").append(state).append(",").append(join).append(",").append(ca.getForceId()).append(";");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            }
            else {
                battleMsg.append("null").append("|");
            }
            if (defCaList.size() > 0) {
                for (int i = 0; i < defCaList.size() && i < 3; ++i) {
                    final KfGeneralInfo defGeneral = defCaList.get(i);
                    final KfCampArmy ca = defGeneral.getCampArmy();
                    int state = 0;
                    if (defGeneral.getState() == 3) {
                        state = 1;
                    }
                    else {
                        state = 2;
                    }
                    final int join = 0;
                    int playerId = ca.getPlayerId();
                    if (ca.isPhantom) {
                        playerId = -playerId;
                    }
                    final String nameAppend = "";
                    battleMsg.append(i + 1).append(",").append(playerId).append(",").append(ca.getPlayerName()).append(",").append(ca.getGeneralName()).append(",").append(ca.getQuality()).append(",").append(state).append(",").append(join).append(",").append(ca.getForceId()).append(";");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "|");
            }
            else {
                battleMsg.append("null").append("|");
            }
            battleMsg.append("null").append("|");
            battleMsg.append(attCaList.size()).append("|").append(defCaList.size()).append("#");
        }
    }
    
    protected static void getReportType19(final StringBuilder battleMsg, final KfBattleRoundInfo roundInfo) {
        if (roundInfo.attCampArmy.playerId > 0) {
            battleMsg.append(19).append("|").append("att").append(";");
            battleMsg.append(roundInfo.attCampArmy.playerId).append("|").append(roundInfo.attRoundReward.gExp).append("|").append(roundInfo.attRoundReward.mUpLv).append("|").append(roundInfo.attRoundReward.gUpLv).append(";");
            if (roundInfo.attRoundReward.roundDropMap == null || roundInfo.attRoundReward.roundDropMap.size() == 0) {
                battleMsg.append(0).append("*").append(0).append("#");
            }
            else {
                final Map<Integer, Integer> attRoundDrop = new HashMap<Integer, Integer>();
                for (final BattleDrop battleDrop : roundInfo.attRoundReward.roundDropMap.values()) {
                    Integer key = battleDrop.type;
                    if (key > 1000) {
                        key -= 1000;
                    }
                    if (attRoundDrop.containsKey(key)) {
                        final int newNum = attRoundDrop.get(key) + battleDrop.num;
                        attRoundDrop.put(key, newNum);
                    }
                    else {
                        attRoundDrop.put(key, battleDrop.num);
                    }
                }
                for (final Integer key2 : attRoundDrop.keySet()) {
                    battleMsg.append(key2).append("*").append(attRoundDrop.get(key2)).append("|");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
            }
        }
        if (roundInfo.defCampArmy.playerId > 0) {
            battleMsg.append(19).append("|").append("def").append(";");
            battleMsg.append(roundInfo.defCampArmy.playerId).append("|").append(roundInfo.defRoundReward.gExp).append("|").append(roundInfo.defRoundReward.mUpLv).append("|").append(roundInfo.defRoundReward.gUpLv).append(";");
            if (roundInfo.defRoundReward.roundDropMap == null || roundInfo.defRoundReward.roundDropMap.size() == 0) {
                battleMsg.append(0).append("*").append(0).append("#");
            }
            else {
                final Map<Integer, Integer> defRoundDrop = new HashMap<Integer, Integer>();
                for (final BattleDrop battleDrop : roundInfo.defRoundReward.roundDropMap.values()) {
                    Integer key = battleDrop.type;
                    if (key > 1000) {
                        key -= 1000;
                    }
                    if (defRoundDrop.containsKey(key)) {
                        final int newNum = defRoundDrop.get(key) + battleDrop.num;
                        defRoundDrop.put(key, newNum);
                    }
                    else {
                        defRoundDrop.put(key, battleDrop.num);
                    }
                }
                for (final Integer key2 : defRoundDrop.keySet()) {
                    battleMsg.append(key2).append("*").append(defRoundDrop.get(key2)).append("|");
                }
                battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
            }
        }
    }
    
    public static void roundCaculateAttReward(final KfBattle bat, final KfBattleRoundInfo roundInfo) {
        final FightRewardCoe frc = FightRewardCoeCache.getFightRewardCoeById(3);
        final float expCoef = KfgzManager.getGzBaseInfoById(bat.gzId).getExpCoef();
        if (roundInfo.defTacticInfo != null && roundInfo.defTacticInfo.reduceMap != null && roundInfo.defTacticInfo.reduceMap.size() > 1) {
            final int counterLost = 0;
            final int counterLv = roundInfo.defCampArmy.playerLv;
            final int gCounterLv = roundInfo.defCampArmy.generalLv;
            final int counterTroopId = roundInfo.defCampArmy.troopId;
            for (final KfCampArmy campArmy : roundInfo.defTacticInfo.reduceMap.keySet()) {
                final KfBattlePInfo pInfo = bat.getKfBattlePInfoFromCampArmy(campArmy);
                final KfPlayerInfo kfPlayerInfo = campArmy.getGeneralInfo().getpInfo();
                if (pInfo != null && campArmy != roundInfo.attCampArmy && campArmy.getGeneralInfo().isNotNpc()) {
                    final RoundReward tempRD = new RoundReward();
                    final int myLost = roundInfo.defTacticInfo.reduceMap.get(campArmy);
                    final int myLv = campArmy.playerLv;
                    final int gMyLv = campArmy.generalLv;
                    final int myTroopId = campArmy.troopId;
                    final double mAttOmega = getRoundRewardBase(frc, myTroopId, counterTroopId, myLost, counterLost, myLv, counterLv);
                    final double gAttOmega = getRoundRewardBase(frc, myTroopId, counterTroopId, myLost, counterLost, gMyLv, gCounterLv);
                    BattleDrop battleDrop = new BattleDrop();
                    final int copper = (int)(mAttOmega * frc.getM());
                    battleDrop.type = 1;
                    battleDrop.num = copper;
                    tempRD.roundDropMap.put(1, battleDrop);
                    pInfo.addDrop(battleDrop);
                    battleDrop = new BattleDrop();
                    int mExp = (int)(mAttOmega * frc.getC() * expCoef);
                    final double attLowLvSCoe = getLowLvSCoe(bat, myLv, counterLv);
                    double attTechAddGZJY = 0.0;
                    if (kfPlayerInfo != null) {
                        attTechAddGZJY = kfPlayerInfo.getTech40() / 100.0;
                    }
                    double specialGeneralAddExp = 0.0;
                    if (roundInfo.attCampArmy.getKfspecialGeneral().generalType == 4 && roundInfo.defCampArmy.isPhantom) {
                        specialGeneralAddExp = roundInfo.attCampArmy.getKfspecialGeneral().param;
                    }
                    mExp *= (int)(attLowLvSCoe + attTechAddGZJY + specialGeneralAddExp);
                    battleDrop.type = 5;
                    battleDrop.num = mExp;
                    tempRD.roundDropMap.put(5, battleDrop);
                    pInfo.addDrop(battleDrop);
                    double gExpAdd = 0.0;
                    if (kfPlayerInfo != null) {
                        gExpAdd = kfPlayerInfo.getTech16() / 100.0;
                    }
                    tempRD.gExp = (int)(gAttOmega * frc.getE() * expCoef * (1.0 + gExpAdd + specialGeneralAddExp));
                    roundAddRewardSingle(campArmy, tempRD);
                }
            }
        }
        int attTacticLost = 0;
        if (roundInfo.defTacticInfo != null) {
            attTacticLost = roundInfo.defTacticInfo.firstCReduce;
        }
        final int attLost = roundInfo.attLost + roundInfo.attStrategyLost + attTacticLost;
        final int attLv = roundInfo.attCampArmy.getPlayerLv();
        final int gAttLv = roundInfo.attCampArmy.getGeneralLv();
        final int troopIdAtt = roundInfo.attCampArmy.getTroopId();
        int defTacticLost = 0;
        if (roundInfo.attTacticInfo != null) {
            defTacticLost = roundInfo.attTacticInfo.allCReduce;
        }
        final int defLost = roundInfo.defLost + roundInfo.defStrategyLost + defTacticLost;
        final int defLv = roundInfo.defCampArmy.getGeneralInfo().getpInfo().getPlayerLevel();
        final int gDefLv = roundInfo.defCampArmy.getGeneralLv();
        final int TroopIdDef = roundInfo.defCampArmy.getTroopId();
        final double mAttOmega2 = getRoundRewardBase(frc, troopIdAtt, TroopIdDef, attLost, defLost, attLv, defLv);
        final double gAttOmega2 = getRoundRewardBase(frc, troopIdAtt, TroopIdDef, attLost, defLost, gAttLv, gDefLv);
        final KfBattlePInfo pInfo2 = bat.getKfBattlePInfoFromCampArmy(roundInfo.attCampArmy);
        BattleDrop battleDrop2 = new BattleDrop();
        final int copper2 = (int)(mAttOmega2 * frc.getM());
        battleDrop2.type = 1;
        battleDrop2.num = copper2;
        roundInfo.attRoundReward.roundDropMap.put(1, battleDrop2);
        if (pInfo2 != null) {
            pInfo2.addDrop(battleDrop2);
        }
        battleDrop2 = new BattleDrop();
        int mExp2 = (int)(mAttOmega2 * frc.getC() * expCoef);
        final double att_world_frontLine_buff_exp_e = 1.0;
        final double attLowLvSCoe = getLowLvSCoe(bat, attLv, defLv);
        double attTechAddGZJY = 0.0;
        final KfPlayerInfo attKfPlayerInfo = roundInfo.attCampArmy.getGeneralInfo().getpInfo();
        if (attKfPlayerInfo != null) {
            attTechAddGZJY = attKfPlayerInfo.getTech40() / 100.0;
        }
        double specialGeneralAddExp2 = 0.0;
        if (roundInfo.attCampArmy.getKfspecialGeneral().generalType == 4 && roundInfo.defCampArmy.isPhantom) {
            specialGeneralAddExp2 = roundInfo.attCampArmy.getKfspecialGeneral().param;
        }
        mExp2 *= (int)(att_world_frontLine_buff_exp_e + attLowLvSCoe + attTechAddGZJY + specialGeneralAddExp2);
        battleDrop2.type = 5;
        battleDrop2.num = mExp2;
        roundInfo.attRoundReward.roundDropMap.put(5, battleDrop2);
        if (pInfo2 != null) {
            pInfo2.addDrop(battleDrop2);
        }
        double gExpAdd2 = 0.0;
        if (attKfPlayerInfo != null) {
            gExpAdd2 = attKfPlayerInfo.getTech16() / 100.0;
        }
        roundInfo.attRoundReward.gExp = (int)(gAttOmega2 * frc.getE() * expCoef * (1.0 + gExpAdd2 + specialGeneralAddExp2));
    }
    
    public static void roundCaculateDefReward(final KfBattle bat, final KfBattleRoundInfo roundInfo) {
        final FightRewardCoe frc = FightRewardCoeCache.getFightRewardCoeById(3);
        final float expCoef = KfgzManager.getGzBaseInfoById(bat.gzId).getExpCoef();
        if (roundInfo.attTacticInfo != null && roundInfo.attTacticInfo.reduceMap != null && roundInfo.attTacticInfo.reduceMap.size() > 1) {
            final int counterLost = 0;
            final int counterLv = roundInfo.attCampArmy.playerLv;
            final int gCounterLv = roundInfo.attCampArmy.generalLv;
            final int counterTroopId = roundInfo.attCampArmy.troopId;
            for (final KfCampArmy campArmy : roundInfo.attTacticInfo.reduceMap.keySet()) {
                final KfBattlePInfo pInfo = bat.getKfBattlePInfoFromCampArmy(campArmy);
                final KfPlayerInfo kfPlayerInfo = campArmy.getGeneralInfo().getpInfo();
                if (campArmy != roundInfo.defCampArmy && campArmy.playerId > 0) {
                    final RoundReward tempRD = new RoundReward();
                    final int myLost = roundInfo.attTacticInfo.reduceMap.get(campArmy);
                    final int myLv = campArmy.playerLv;
                    final int gMyLv = campArmy.generalLv;
                    final int myTroopId = campArmy.troopId;
                    final double mAttOmega = getRoundRewardBase(frc, myTroopId, counterTroopId, myLost, counterLost, myLv, counterLv);
                    final double gAttOmega = getRoundRewardBase(frc, myTroopId, counterTroopId, myLost, counterLost, gMyLv, gCounterLv);
                    BattleDrop battleDrop = new BattleDrop();
                    final int copper = (int)(mAttOmega * frc.getM());
                    battleDrop.type = 1;
                    battleDrop.num = copper;
                    tempRD.roundDropMap.put(1, battleDrop);
                    if (pInfo != null) {
                        pInfo.addDrop(battleDrop);
                    }
                    battleDrop = new BattleDrop();
                    int mExp = (int)(mAttOmega * frc.getC() * expCoef);
                    final double att_world_frontLine_buff_exp_e = 1.0;
                    final double attLowLvSCoe = getLowLvSCoe(bat, myLv, counterLv);
                    double attTechAddGZJY = 0.0;
                    if (kfPlayerInfo != null) {
                        attTechAddGZJY = kfPlayerInfo.getTech40() / 100.0;
                    }
                    double specialGeneralAddExp = 0.0;
                    if (roundInfo.defCampArmy.getKfspecialGeneral().generalType == 4 && roundInfo.attCampArmy.isPhantom) {
                        specialGeneralAddExp = roundInfo.defCampArmy.getKfspecialGeneral().param;
                    }
                    mExp *= (int)(att_world_frontLine_buff_exp_e + attLowLvSCoe + attTechAddGZJY + specialGeneralAddExp);
                    battleDrop.type = 5;
                    battleDrop.num = mExp;
                    tempRD.roundDropMap.put(5, battleDrop);
                    if (pInfo != null) {
                        pInfo.addDrop(battleDrop);
                    }
                    final double gExpAdd = 0.0;
                    if (kfPlayerInfo != null) {
                        attTechAddGZJY = kfPlayerInfo.getTech16() / 100.0;
                    }
                    tempRD.gExp = (int)(gAttOmega * frc.getE() * expCoef * (1.0 + gExpAdd + specialGeneralAddExp));
                    roundAddRewardSingle(campArmy, tempRD);
                }
            }
        }
        int attTacticLost = 0;
        if (roundInfo.defTacticInfo != null) {
            attTacticLost = roundInfo.defTacticInfo.allCReduce;
        }
        final int attLost = roundInfo.attLost + roundInfo.attStrategyLost + attTacticLost;
        final int attLv = roundInfo.attCampArmy.getPlayerLv();
        final int gAttLv = roundInfo.attCampArmy.getGeneralLv();
        final int troopIdAtt = roundInfo.attCampArmy.getTroopId();
        int defTacticLost = 0;
        if (roundInfo.attTacticInfo != null) {
            defTacticLost = roundInfo.attTacticInfo.firstCReduce;
        }
        final int defLost = roundInfo.defLost + roundInfo.defStrategyLost + defTacticLost;
        final int defLv = roundInfo.defCampArmy.getGeneralInfo().getpInfo().getPlayerLevel();
        final int gDefLv = roundInfo.defCampArmy.getGeneralLv();
        final int TroopIdDef = roundInfo.defCampArmy.getTroopId();
        final double mDefOmega = getRoundRewardBase(frc, TroopIdDef, troopIdAtt, defLost, attLost, defLv, attLv);
        final double gDefOmega = getRoundRewardBase(frc, TroopIdDef, troopIdAtt, defLost, attLost, gDefLv, gAttLv);
        final KfBattlePInfo pInfo2 = bat.getKfBattlePInfoFromCampArmy(roundInfo.defCampArmy);
        BattleDrop battleDrop2 = new BattleDrop();
        final int copper2 = (int)(mDefOmega * frc.getM());
        battleDrop2.type = 1;
        battleDrop2.num = copper2;
        roundInfo.defRoundReward.roundDropMap.put(1, battleDrop2);
        if (pInfo2 != null) {
            pInfo2.addDrop(battleDrop2);
        }
        battleDrop2 = new BattleDrop();
        int mExp2 = (int)(mDefOmega * frc.getC() * expCoef);
        final double def_world_frontLine_buff_exp_e = 1.0;
        final double defLowLvSCoe = getLowLvSCoe(bat, defLv, attLv);
        double defTechAddGZJY = 0.0;
        final KfPlayerInfo defKfPlayerInfo = roundInfo.defCampArmy.getGeneralInfo().getpInfo();
        if (defKfPlayerInfo != null) {
            defTechAddGZJY = defKfPlayerInfo.getTech40() / 100.0;
        }
        double specialGeneralAddExp2 = 0.0;
        if (roundInfo.defCampArmy.getKfspecialGeneral().generalType == 4 && roundInfo.attCampArmy.isPhantom) {
            specialGeneralAddExp2 = roundInfo.defCampArmy.getKfspecialGeneral().param;
        }
        mExp2 *= (int)(def_world_frontLine_buff_exp_e + defLowLvSCoe + defTechAddGZJY + specialGeneralAddExp2);
        battleDrop2.type = 5;
        battleDrop2.num = mExp2;
        roundInfo.defRoundReward.roundDropMap.put(5, battleDrop2);
        if (pInfo2 != null) {
            pInfo2.addDrop(battleDrop2);
        }
        double gExpAdd2 = 0.0;
        if (defKfPlayerInfo != null) {
            gExpAdd2 = defKfPlayerInfo.getTech16() / 100.0;
        }
        roundInfo.defRoundReward.gExp = (int)(gDefOmega * frc.getE() * expCoef * (1.0 + gExpAdd2 + specialGeneralAddExp2));
    }
    
    protected static double getRoundRewardBase(final FightRewardCoe frc, final int troopIdA, final int TroopIdB, final int aLost, final int bLost, final int aLv, final int bLv) {
        final double troopFoodConsumeCoeA = TroopConscribeCache.getTroopConscribeById(troopIdA).getFood();
        final double troopFoodConsumeCoeB = TroopConscribeCache.getTroopConscribeById(TroopIdB).getFood();
        final double troopFoodA = troopFoodConsumeCoeA * aLost;
        final double troopFoodB = troopFoodConsumeCoeB * bLost;
        final double troopDamageCoe = getTroopDamageCoe(frc, troopFoodA, troopFoodB);
        final double levelDifferCoe = getLevelDifferCoe(frc, aLv, bLv);
        return troopDamageCoe * levelDifferCoe * troopFoodA;
    }
    
    static double getLowLvSCoe(final KfBattle bat, final int myLv, final int counterLv) {
        if (counterLv - myLv >= 25) {
            return 1.0;
        }
        if (counterLv - myLv >= 15) {
            return 0.1 * (counterLv - myLv - 15);
        }
        return 0.0;
    }
    
    protected static double getTroopDamageCoe(final FightRewardCoe frc, final double troopFoodA, final double troopFoodB) {
        double yita = troopFoodB / (1.0 + troopFoodA);
        if (yita < 0.5) {
            yita = 0.5;
        }
        else if (yita > 2.0) {
            yita = 2.0;
        }
        final double selta = 0.5 * (yita - 1.0);
        final double delta = 1.0 + frc.getDelta() * selta;
        return delta;
    }
    
    protected static double getLevelDifferCoe(final FightRewardCoe frc, final int aLv, final int bLv) {
        double l = 0.0;
        if (aLv > bLv + 5) {
            l = -1.0;
        }
        else if (aLv > bLv) {
            l = -0.2 * (aLv - bLv);
        }
        else {
            l = 0.0;
        }
        return 1.0 + frc.getLvCoe() * l;
    }
    
    public static void dealTroopDrop(final KfBattle kfBattle, final KfBattleRoundInfo roundInfo) {
        final KfGeneralInfo attGInfo = roundInfo.attCampArmy.getGeneralInfo();
        if (attGInfo.isNotNpc()) {
            final KfBattlePInfo pInfo = kfBattle.getKfBattlePInfoFromCampArmy(roundInfo.attCampArmy);
            for (final KfBattleArmy ba : roundInfo.defKilledList) {
                final int troopId = ba.getCampArmy().getTroopId();
                final Troop bonusTroop = TroopCache.getTroopCacheById(troopId);
                if (bonusTroop != null && bonusTroop.getTroopDrop() != null) {
                    final Map<Integer, BattleDrop> dropMap = bonusTroop.getTroopDrop().getDropAndMap();
                    dropMap.size();
                    roundInfo.attRoundReward.addDropMap(dropMap);
                    KfgzResSenderManager.addNewDropMap(attGInfo.getpInfo().getCompetitorId(), dropMap);
                    if (pInfo == null) {
                        continue;
                    }
                    pInfo.addDropMap(dropMap);
                }
            }
        }
        final KfGeneralInfo defGInfo = roundInfo.defCampArmy.getGeneralInfo();
        if (defGInfo.isNotNpc()) {
            final KfBattlePInfo pInfo2 = kfBattle.getKfBattlePInfoFromCampArmy(roundInfo.defCampArmy);
            for (final KfBattleArmy ba2 : roundInfo.attKilledList) {
                final int troopId2 = ba2.getCampArmy().getTroopId();
                final Troop bonusTroop2 = TroopCache.getTroopCacheById(troopId2);
                if (bonusTroop2 != null && bonusTroop2.getTroopDrop() != null) {
                    final Map<Integer, BattleDrop> dropMap2 = bonusTroop2.getTroopDrop().getDropAndMap();
                    dropMap2.size();
                    roundInfo.defRoundReward.addDropMap(dropMap2);
                    KfgzResSenderManager.addNewDropMap(defGInfo.getpInfo().getCompetitorId(), dropMap2);
                    if (pInfo2 == null) {
                        continue;
                    }
                    pInfo2.addDropMap(dropMap2);
                }
            }
        }
    }
    
    public static void roundAddRewardSingle(final KfCampArmy campArmy, final RoundReward roundReward) {
        final KfGeneralInfo gInfo = campArmy.getGeneralInfo();
        if (roundReward == null || !gInfo.isNotNpc()) {
            return;
        }
        final int mExp = roundReward.roundDropMap.get(5).num;
        try {
            roundReward.roundDropMap.get(5).num = mExp;
            roundReward.roundDropMap.get(5).reserve = "1";
            final PlayerBattleDropItem bdi = new PlayerBattleDropItem(gInfo.getpInfo().getCompetitorId(), "exp", mExp);
            KfgzResSenderManager.addNewDropItem(bdi);
        }
        catch (Exception ex) {}
        final int copper = roundReward.roundDropMap.get(1).num;
        PlayerBattleDropItem bdi2 = new PlayerBattleDropItem(gInfo.getpInfo().getCompetitorId(), "copper", copper);
        KfgzResSenderManager.addNewDropItem(bdi2);
        try {
            if (roundReward.gExp > 0) {
                bdi2 = new PlayerBattleDropItem(gInfo.getpInfo().getCompetitorId(), gInfo.getgId(), "gExp", copper);
                KfgzResSenderManager.addNewDropItem(bdi2);
            }
        }
        catch (Exception ex2) {}
        final BattleDrop drop = roundReward.roundDropMap.get(1501);
        if (drop != null && drop.num > 0) {
            bdi2 = new PlayerBattleDropItem(gInfo.getpInfo().getCompetitorId(), "phantomCount", drop.num);
            KfgzResSenderManager.addNewDropItem(bdi2);
        }
    }
}
