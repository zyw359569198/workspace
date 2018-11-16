package com.reign.gcld.kfwd.common;

import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.common.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.gcld.player.dao.*;

public class MatchJsonBuilder
{
    public static void buildNoMatch(final JsonDocument doc) {
        doc.startObject("match");
        doc.createElement("state", 0);
        doc.endObject();
    }
    
    public static void buildMatchCancel(final JsonDocument doc) {
        doc.startObject("match");
        doc.createElement("state", 7);
        doc.endObject();
    }
    
    public static void buildPrepare(final Match match, final JsonDocument doc) {
        doc.startObject("match");
        doc.createElement("state", 1);
        doc.createElement("startSighLeftMilsec", match.getSignupStartTime().getTime() - System.currentTimeMillis());
        doc.endObject();
    }
    
    public static void buildSign(final Match match, final int playerId, final JsonDocument doc, final IDataGetter dataGetter) {
        final MatchAttendee matchAttendee = match.getSignMatchAttendee(playerId);
        if (matchAttendee == null) {
            doc.startObject("match");
            doc.createElement("canSign", true);
            doc.createElement("maxLv", match.getMaxLv());
            doc.createElement("minLv", match.getMinLv());
            doc.createElement("state", 2);
            doc.createElement("isIn", false);
            doc.createElement("endSighLeftMilsec", match.getSignupEndTime().getTime() - System.currentTimeMillis());
            doc.endObject();
        }
        else {
            doc.startObject("match");
            doc.createElement("canSign", false);
            doc.createElement("maxLv", match.getMaxLv());
            doc.createElement("minLv", match.getMinLv());
            doc.createElement("state", 2);
            doc.createElement("isIn", true);
            doc.createElement("endSighLeftMilsec", match.getSignupEndTime().getTime() - System.currentTimeMillis());
            doc.endObject();
            doc.startObject("currMatch");
            doc.createElement("turnNum", 1);
            doc.createElement("setNum", 0);
            doc.createElement("matchFightState", 1);
            doc.createElement("playerName", matchAttendee.getPlayerName());
            doc.createElement("playerLv", matchAttendee.getPlayerLv());
            doc.createElement("playerPic", matchAttendee.getPlayerPic());
            doc.createElement("playerForce", WorldCityCommon.nationIdNameMap.get(matchAttendee.getForceId()));
            doc.createElement("serverName", matchAttendee.getServerName());
            doc.createElement("serverId", matchAttendee.getServerId());
            doc.createElement("turnWinTimes", 0);
            doc.createElement("setWinTimes", 0);
            doc.endObject();
        }
    }
    
    public static void buildArrage(final Match match, final int playerId, final JsonDocument doc, final IDataGetter dataGetter) {
        final MatchAttendee matchAttendee = match.getSignMatchAttendee(playerId);
        if (matchAttendee == null) {
            doc.startObject("match");
            doc.createElement("state", 3);
            doc.createElement("isIn", false);
            doc.endObject();
        }
        else {
            doc.startObject("match");
            doc.createElement("state", 3);
            doc.createElement("isIn", true);
            doc.endObject();
            doc.startObject("currMatch");
            doc.createElement("turnNum", 1);
            doc.createElement("setNum", 0);
            doc.createElement("matchFightState", 1);
            doc.createElement("playerName", matchAttendee.getPlayerName());
            doc.createElement("playerLv", matchAttendee.getPlayerLv());
            doc.createElement("playerPic", matchAttendee.getPlayerPic());
            doc.createElement("playerForce", WorldCityCommon.nationIdNameMap.get(matchAttendee.getForceId()));
            doc.createElement("serverName", matchAttendee.getServerName());
            doc.createElement("serverId", matchAttendee.getServerId());
            doc.createElement("turnWinTimes", 0);
            doc.createElement("setWinTimes", 0);
            doc.endObject();
        }
    }
    
    public static void buildMatching(final Match match, final int playerId, final JsonDocument doc, final IDataGetter dataGetter) {
        final MatchAttendee matchAttendee = match.getSignMatchAttendee(playerId);
        if (matchAttendee == null) {
            doc.startObject("match");
            doc.createElement("state", 5);
            doc.createElement("isIn", false);
            doc.createElement("turn", match.getTurn());
            doc.endObject();
        }
        else {
            doc.startObject("match");
            doc.createElement("state", 5);
            doc.createElement("isIn", true);
            doc.endObject();
            final MatchFight matchFight = matchAttendee.getMatchFight();
            if (matchFight == null) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", 1);
                doc.createElement("setNum", 0);
                doc.createElement("matchFightState", 1);
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("playerName", matchAttendee.getPlayerName());
                doc.createElement("playerLv", matchAttendee.getPlayerLv());
                doc.createElement("playerPic", matchAttendee.getPlayerPic());
                doc.createElement("playerForce", WorldCityCommon.nationIdNameMap.get(matchAttendee.getForceId()));
                doc.createElement("serverName", matchAttendee.getServerName());
                doc.createElement("serverId", matchAttendee.getServerId());
                doc.createElement("turnWinTimes", 0);
                doc.createElement("setWinTimes", 0);
                doc.endObject();
            }
            else if (matchFight.getState() == 1) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 1);
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("playerName", matchAttendee.getPlayerName());
                doc.createElement("playerLv", matchAttendee.getPlayerLv());
                doc.createElement("playerPic", matchAttendee.getPlayerPic());
                doc.createElement("playerForce", WorldCityCommon.nationIdNameMap.get(matchAttendee.getForceId()));
                doc.createElement("serverName", matchAttendee.getServerName());
                doc.createElement("serverId", matchAttendee.getServerId());
                doc.createElement("turnWinTimes", 0);
                doc.createElement("setWinTimes", 0);
                doc.endObject();
            }
            else if (matchFight.getState() == 2) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 2);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("leftFormationMilSec", matchFight.getFormationEndTime().getTime() - System.currentTimeMillis());
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", matchFight.getMatchNum() == 1 && matchAttendee.getRewardMode() != 2);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", member.getCompetitorId() == matchAttendee.getCompetitorId() && matchFight.getFormationEndTime().getTime() - System.currentTimeMillis() > 0L);
                doc.startObject("attacker");
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 3) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 3);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("leftFormationMilSec", matchFight.getFormationEndTime().getTime() - System.currentTimeMillis());
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", matchFight.getMatchNum() == 1 && matchAttendee.getRewardMode() != 2);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                MatchFightMember member = matchFight.getMember2();
                if (member != null && member.getCompetitorId() == matchAttendee.getCompetitorId() && matchFight.getFormationEndTime().getTime() - System.currentTimeMillis() > 0L) {
                    doc.createElement("canFormation", true);
                }
                else {
                    doc.createElement("canFormation", false);
                }
                doc.startObject("attacker");
                member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 4) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 4);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("leftFormationMilSec", matchFight.getFormationEndTime().getTime() - System.currentTimeMillis());
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", matchFight.getMatchNum() == 1 && matchAttendee.getRewardMode() != 2);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", matchFight.getFormationEndTime().getTime() - System.currentTimeMillis() > 0L);
                doc.startObject("attacker");
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 5) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 5);
                doc.createElement("leftMilSec", 0);
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", false);
                doc.startObject("attacker");
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 6) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 6);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", false);
                doc.startObject("attacker");
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 7) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 7);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", false);
                doc.startObject("attacker");
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
            else if (matchFight.getState() == 8) {
                doc.startObject("currMatch");
                doc.createElement("turnNum", matchFight.getTurn());
                doc.createElement("setNum", matchFight.getMatchNum());
                doc.createElement("matchFightState", 8);
                doc.createElement("leftMilSec", matchFight.getFightTime().getTime() - System.currentTimeMillis());
                doc.createElement("rewardMode", matchAttendee.getRewardMode());
                doc.createElement("canChangeRewardMode", false);
                doc.createElement("changeRewardModeGold", match.getChangeRewardModeGold(matchAttendee.getRewardMode()));
                doc.createElement("canFormation", false);
                doc.startObject("attacker");
                MatchFightMember member = matchFight.getMember1();
                doc.createElement("playerName", member.getPlayerName());
                doc.createElement("playerLv", member.getPlayerLv());
                doc.createElement("playerPic", member.getPlayerPic());
                doc.createElement("playerForce", member.getForceName());
                doc.createElement("serverName", member.getServerName());
                doc.createElement("serverId", member.getServerId());
                doc.createElement("turnWinTimes", member.getWinTurns());
                doc.createElement("setWinTimes", member.getWinMatch());
                doc.endObject();
                member = matchFight.getMember2();
                if (member != null) {
                    doc.startObject("defender");
                    doc.createElement("playerName", member.getPlayerName());
                    doc.createElement("playerLv", member.getPlayerLv());
                    doc.createElement("playerPic", member.getPlayerPic());
                    doc.createElement("playerForce", member.getForceName());
                    doc.createElement("serverName", member.getServerName());
                    doc.createElement("serverId", member.getServerId());
                    doc.createElement("turnWinTimes", member.getWinTurns());
                    doc.createElement("setWinTimes", member.getWinMatch());
                    doc.endObject();
                }
                doc.endObject();
            }
        }
    }
    
    public static void buildFinish(final Match match, final int playerId, final JsonDocument doc, final IDataGetter dataGetter) {
        final MatchAttendee matchAttendee = match.getSignMatchAttendee(playerId);
        if (matchAttendee == null) {
            doc.startObject("match");
            doc.createElement("state", 6);
            doc.createElement("isIn", false);
            doc.endObject();
        }
        else {
            doc.startObject("match");
            doc.createElement("state", 6);
            doc.createElement("isIn", true);
            doc.endObject();
            doc.startObject("reward");
            doc.createElement("seq", matchAttendee.getSeq());
            doc.createElement("turnWinTimes", matchAttendee.getWinTimes());
            doc.createElement("turnLossTimes", matchAttendee.getLossTimes());
            doc.createElement("hasBoxLeft", matchAttendee.getPoints() > 3);
            doc.endObject();
            if (match.getRankList() != null && match.getRankList().size() > 0) {
                final MatchRankEntity matchRankEntity = match.getRankList().get(0);
                doc.startObject("champion");
                doc.createElement("playerName", matchRankEntity.getPlayerName());
                doc.createElement("forceName", matchRankEntity.getForceName());
                doc.createElement("serverId", matchRankEntity.getServerId());
                doc.createElement("serverName", matchRankEntity.getServerName());
                doc.endObject();
            }
        }
    }
    
    public static void buildRankList(final int playerId, final Match match, final JsonDocument doc, final IPlayerDao playerDao) {
        doc.startArray("rankList");
        for (final MatchRankEntity matchRankEntity : match.getRankList()) {
            doc.startObject();
            doc.createElement("rank", matchRankEntity.getRank());
            doc.createElement("playerName", matchRankEntity.getPlayerName());
            doc.createElement("forceName", matchRankEntity.getForceName());
            doc.createElement("serverId", matchRankEntity.getServerId());
            doc.createElement("serverName", matchRankEntity.getServerName());
            doc.createElement("winTimes", matchRankEntity.getWinNum());
            doc.createElement("turn", matchRankEntity.getTurn());
            doc.endObject();
        }
        doc.endArray();
    }
}
