package com.reign.kfzb.battle;

import org.apache.commons.logging.*;
import com.reign.util.*;
import java.util.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kf.match.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import org.apache.commons.lang.*;
import com.reign.kf.match.sdata.domain.*;

public class KfzbBuilder
{
    private static Log battleReportLog;
    private static Log interfaceLog;
    public static final int WORLD_CITY_ATTACKER_TACTIC_HALF = 1;
    public static final int GUANYU_WUSHENFUTI_TACTIC_HALF = 2;
    public static final int GEM_SKILL_RENGXING_TACTIC_HALF = 3;
    
    static {
        KfzbBuilder.battleReportLog = LogFactory.getLog("mj.kfzb.battleReport.log");
        KfzbBuilder.interfaceLog = LogFactory.getLog("astd.kfzb.log.interface");
    }
    
    public static void getCurCampInfo(final KfzbBattle bat, final KfzbRoundInfo roundInfo) {
        roundInfo.attBattleArmy = bat.attList.get(bat.attPos);
        roundInfo.defBattleArmy = bat.defList.get(bat.defPos);
        roundInfo.attCampArmy = roundInfo.attBattleArmy.getCampArmy();
        roundInfo.defCampArmy = roundInfo.defBattleArmy.getCampArmy();
        getReportType16(bat, roundInfo.battleMsg, roundInfo.attCampArmy, "att", false, false, roundInfo.defCampArmy);
        getReportType16(bat, roundInfo.battleMsg, roundInfo.defCampArmy, "def", false, false, roundInfo.attCampArmy);
    }
    
    public static void getReportType2(final StringBuilder battleMsg, final List<KfzbBattleArmy> addQlist, final String battleSide) {
        KfzbBattleArmy battleArmy = null;
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
    
    protected static void getReportType16(final KfzbBattle bat, final StringBuilder battleMsg, final KfzbCampArmy campArmy, final String battleSide, final boolean campArmyChanged, final boolean first, final KfzbCampArmy targetCampArmy) {
        battleMsg.append(16).append("|").append(battleSide).append("|").append(campArmy.generalLv).append("|").append(campArmy.armyHpOrg - campArmy.armyHpLoss).append("|").append(campArmy.armyHpOrg).append("|").append(campArmy.killGeneral).append("|").append(";");
        String playerName = campArmy.playerName;
        if (bat.getBattleType() == 2 || bat.getBattleType() == 4 || bat.getBattleType() == 3) {
            playerName = campArmy.playerName;
        }
        final int forceId = 0;
        battleMsg.append(forceId).append("|").append(playerName).append("|").append(campArmy.generalName).append("|").append(campArmy.generalPic).append("|").append(campArmy.quality).append("|").append(0).append(";");
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
    
    protected static void getReportType27(final StringBuilder battleMsg, final KfzbRoundInfo roundInfo, final KfzbBattle bat) {
        KfzbCampArmy attCa = null;
        final KfzbBattleArmy attBa = bat.getCurAttBattleArmy();
        if (attBa != null) {
            attCa = attBa.getCampArmy();
        }
        KfzbCampArmy defCa = null;
        final KfzbBattleArmy defBa = bat.getCurDefBattleArmy();
        if (defBa != null) {
            defCa = defBa.getCampArmy();
        }
        if (attCa == null || defCa == null) {
            return;
        }
        boolean attNextChoose = false;
        if (attBa != null && !attBa.choose && attBa.strategy == 0 && bat.attPos < bat.attList.size() && attBa != null && attBa.getCampArmy().getPlayerId() > 0) {
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
        if (!attNextChoose && attBa != null) {
            battleMsg.append(27).append("|").append("att").append("|").append(attCa.playerId).append("|").append(attBa.getPosition()).append("|").append(attCa.playerName).append("|").append(0).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
        boolean defNextChoose = false;
        if (defBa != null && !defBa.choose && defBa.strategy == 0 && bat.defPos < bat.defList.size() && defBa != null && defBa.getCampArmy().getPlayerId() > 0) {
            final int playerId2 = defCa.playerId;
            int canChooseTac2 = 0;
            if (defBa.getSpecial() > 0 || attCa == null) {
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
        if (!defNextChoose && defBa != null) {
            battleMsg.append(27).append("|").append("def").append("|").append(defCa.playerId).append("|").append(defBa.getPosition()).append("|").append(defCa.playerName).append("|").append(0).append("|").append(0).append(";");
            battleMsg.append("null").append(";");
            battleMsg.append("null").append("#");
        }
    }
    
    public static void sendMsgToAll(final KfzbBattle kfzbBattle, final StringBuilder battleMsg) {
        KfzbBuilder.battleReportLog.info(battleMsg.toString());
        final int player1Id = kfzbBattle.match.getPlayer1Id();
        final int player2Id = kfzbBattle.match.getPlayer2Id();
        sendMsgToOne(player1Id, battleMsg);
        sendMsgToOne(player2Id, battleMsg);
    }
    
    public static void sendMsgToOne(final int playerId, final StringBuilder battleMsg) {
        KfzbBuilder.battleReportLog.info("sendcId=" + playerId);
        final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(playerId, 3));
        if (session != null) {
            KfzbBuilder.battleReportLog.info("sendcId=" + playerId + "#suc");
            final byte[] bytes = JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BATTLE_DOKFZBBATTLE.getModule(), (Object)battleMsg);
            session.write(WrapperUtil.wrapper(PushCommand.PUSH_BATTLE_DOKFZBBATTLE.getCommand(), 0, bytes));
        }
    }
    
    public static void sendMsgToOne(final int playerId, final byte[] msg) {
        try {
            final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(playerId, 3));
            if (session != null) {
                KfzbBuilder.interfaceLog.info(String.valueOf(playerId) + "#infoSend " + new String(msg));
                final byte[] bytes = JsonBuilder.getJson(State.PUSH, msg);
                session.write(WrapperUtil.wrapper(PushCommand.PUSH_BATTLE_DOKFZBRTINFO.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void getReportType14(final KfzbBattle bat, final KfzbRoundInfo roundInfo) {
        final int attTaticPosNum = roundInfo.attBattleArmy.getPosition();
        final int defTaticPosNum = roundInfo.defBattleArmy.getPosition();
        roundInfo.battleMsg.append(14).append("|").append(roundInfo.tacticStrategyResult).append(";").append(attTaticPosNum).append("|").append(defTaticPosNum).append(";");
        if (roundInfo.attTacticInfo != null) {
            int attTacticReduceType = 0;
            final Tactic tactic = TacticCache.getTacticById(roundInfo.attTacticInfo.tacticId);
            if ((bat.battleType == 3 || bat.battleType == 13) && bat.terrainVal == 4 && !tactic.getSpecialEffect().equalsIgnoreCase("siege_gun")) {
                attTacticReduceType = 1;
            }
            if (roundInfo.attTacticInfo.zfJB) {
                attTacticReduceType = 3;
            }
            if (roundInfo.attTacticInfo.attacked_guanyu) {
                attTacticReduceType = 2;
            }
            int zfBj = 0;
            if (roundInfo.attTacticInfo.zfBJ) {
                zfBj = 1;
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
            int zfBj = 0;
            if (roundInfo.defTacticInfo.zfJB) {
                defTacticReduceType = 3;
            }
            if (roundInfo.defTacticInfo.attacked_guanyu) {
                defTacticReduceType = 2;
            }
            if (roundInfo.defTacticInfo.zfBJ) {
                zfBj = 1;
            }
            roundInfo.battleMsg.append(1).append(",").append(roundInfo.defTacticInfo.executed ? 1 : 0).append(",").append(roundInfo.defTacticInfo.tacticNameId).append(",").append(stopType2).append(",").append(roundInfo.defTacticInfo.beStop ? roundInfo.attCampArmy.getcDifyType() : roundInfo.defTacticInfo.tacticDisplayId).append(",").append(roundInfo.defTacticInfo.tacticBasicPic).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append("null").append(",").append(defTacticReduceType).append(",").append(roundInfo.attBattleArmy.getCampArmy().getGeneralPic()).append(",").append(roundInfo.defBattleArmy.getCampArmy().getGeneralPic()).append(",").append(zfBj);
        }
        else {
            final FightStrategies defFT = FightStrategiesCache.getStr(roundInfo.defBattleArmy.getUsedStrategy());
            roundInfo.battleMsg.append(2).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append(0).append(",").append(0).append(",").append(0).append(",").append(roundInfo.defBattleArmy.getUsedStrategy()).append(",").append((defFT == null) ? "" : defFT.getName()).append(",").append(0).append(",").append(0).append(",").append(0);
        }
        roundInfo.battleMsg.append(";");
        if (roundInfo.defTacticInfo != null) {
            if (StringUtils.isEmpty(roundInfo.defTacticInfo.tacticStr)) {
                roundInfo.battleMsg.append("null");
            }
            else {
                final String[] strs = roundInfo.defTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length; ++i) {
                    if (bat.getLivedBattleArmy(i, attTaticPosNum, true) == null) {
                        break;
                    }
                    roundInfo.battleMsg.append(bat.getLivedBattleArmy(i, attTaticPosNum, true).getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(bat.getLivedBattleArmy(i, attTaticPosNum, true).getTroopHp()[j]).append(",");
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
            if (StringUtils.isEmpty(roundInfo.attTacticInfo.tacticStr)) {
                roundInfo.battleMsg.append("null").append(";");
            }
            else {
                final String[] strs = roundInfo.attTacticInfo.tacticStr.split(";");
                for (int i = 0; i < strs.length && bat.getLivedBattleArmy(i, defTaticPosNum, false) != null; ++i) {
                    roundInfo.battleMsg.append(bat.getLivedBattleArmy(i, defTaticPosNum, false).getPosition()).append(",");
                    final String[] tempArr = strs[i].split("\\|");
                    final String tempStr = tempArr[tempArr.length - 1];
                    final String[] loss = tempStr.split(",");
                    for (int j = 0; j < loss.length; ++j) {
                        roundInfo.battleMsg.append(loss[j]).append(",").append(bat.getLivedBattleArmy(i, defTaticPosNum, false).getTroopHp()[j]).append(",");
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
    
    public static void getReportType30(final StringBuilder battleMsg, final KfzbRoundInfo roundInfo) {
        battleMsg.append(30).append("|");
        if (roundInfo.attKilledList.size() > 0) {
            String sb1 = new String();
            for (final KfzbBattleArmy ba : roundInfo.attKilledList) {
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
            for (final KfzbBattleArmy ba : roundInfo.defKilledList) {
                battleMsg.append(ba.getPosition()).append(",");
            }
            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
        }
        else {
            battleMsg.append(-1).append("#");
        }
        roundInfo.defKilledList.clear();
    }
    
    public static void getReportType31(final StringBuilder battleMsg, final KfzbRoundInfo roundInfo) {
        battleMsg.append(31).append("|").append(roundInfo.win).append("#");
    }
    
    public static void getReportType20(final KfzbBattle kfzbBattle, final StringBuilder battleMsg, final KfzbRoundInfo roundInfo) {
        if (roundInfo.killDefG) {
            battleMsg.append(20).append("|").append("att").append(";").append(roundInfo.attCampArmy.id).append(";").append(roundInfo.attCampArmy.killGeneral).append(";").append(roundInfo.attCampArmy.playerId).append(";");
            boolean addNeeded = true;
            final int currentCA = roundInfo.attCampArmy.id;
            for (int i = kfzbBattle.attPos; i < Math.min(kfzbBattle.attPos + 8, kfzbBattle.attList.size()); ++i) {
                final KfzbBattleArmy ba = kfzbBattle.getAttList().get(i);
                if (ba.getCampArmy().getId() == currentCA) {
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
            final int currentCA = roundInfo.defCampArmy.id;
            for (int i = kfzbBattle.defPos; i < Math.min(kfzbBattle.defPos + 8, kfzbBattle.defList.size()); ++i) {
                final KfzbBattleArmy ba = kfzbBattle.getDefList().get(i);
                if (ba.getCampArmy().getId() == currentCA) {
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
    
    public static void getReportType3(final StringBuilder battleMsg, final KfzbBattle bat, final KfzbRoundInfo roundInfo) {
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
}
