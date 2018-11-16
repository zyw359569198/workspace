package com.reign.gcld.kfwd.common;

import java.util.concurrent.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public class MatchManager
{
    private static final MatchManager instance;
    private ConcurrentMap<String, Match> matchMap;
    
    static {
        instance = new MatchManager();
    }
    
    private MatchManager() {
        this.matchMap = new ConcurrentHashMap<String, Match>();
    }
    
    public static MatchManager getInstance() {
        return MatchManager.instance;
    }
    
    public Match getMatch(final String matchTag) {
        return this.matchMap.get(matchTag);
    }
    
    public void addMatch(final Match match) {
        this.matchMap.put(match.getMatchTag(), match);
    }
    
    public void removeMatch(final String matchTag) {
        this.matchMap.remove(matchTag);
    }
    
    public boolean containMatch(final String matchTag) {
        return this.matchMap.containsKey(matchTag);
    }
    
    public byte[] signup(final Player player, final String gIds) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        Match match = null;
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                match = m;
                break;
            }
        }
        if (match == null) {
            for (final Match m : this.matchMap.values()) {
                if (m.isPassPlayerCondition(player.getPlayerId())) {
                    match = m;
                    break;
                }
            }
        }
        if (match == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10003);
        }
        return match.signup(player, gIds);
    }
    
    public byte[] enter(final Player player) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        Match match = null;
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                match = m;
                break;
            }
        }
        if (match == null) {
            for (final Match m : this.matchMap.values()) {
                if (m.isPassPlayerCondition(player.getPlayerId())) {
                    match = m;
                    break;
                }
            }
        }
        if (match == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10003);
        }
        return match.enter(player);
    }
    
    public byte[] exit(final Player player) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        Match match = null;
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                match = m;
                break;
            }
        }
        if (match == null) {
            for (final Match m : this.matchMap.values()) {
                if (m.isPassPlayerCondition(player.getPlayerId())) {
                    match = m;
                    break;
                }
            }
        }
        if (match == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10003);
        }
        return match.exit(player);
    }
    
    public byte[] query(final Player player) {
        final int playerId = player.getPlayerId();
        if (this.matchMap.size() == 0) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            MatchJsonBuilder.buildNoMatch(doc);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        Match defaultMatch = null;
        for (final Match m : this.matchMap.values()) {
            if (defaultMatch == null) {
                defaultMatch = m;
            }
            if (m.getSignMatchAttendee(playerId) != null) {
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                m.query(playerId, doc2);
                doc2.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(playerId)) {
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                m.query(playerId, doc2);
                doc2.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
            }
        }
        final JsonDocument doc3 = new JsonDocument();
        doc3.startObject();
        defaultMatch.query(playerId, doc3);
        doc3.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc3.toByte());
    }
    
    public byte[] setFormation(final Player player, final String gIds) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                return m.setFormation(player, gIds);
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(player.getPlayerId())) {
                return m.setFormation(player, gIds);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
    }
    
    public byte[] changeRewardMode(final Player player, final int changeRewardMode) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                return m.changeRewardMode(player, changeRewardMode);
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(player.getPlayerId())) {
                return m.changeRewardMode(player, changeRewardMode);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
    }
    
    public byte[] inspire(final Player player) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                return m.inspire(player);
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(player.getPlayerId())) {
                return m.inspire(player);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
    }
    
    public byte[] getBoxInfo(final Player player) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                return m.getBoxInfo(player);
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(player.getPlayerId())) {
                return m.getBoxInfo(player);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
    }
    
    public byte[] receiveBox(final Player player, final int point, final IDataGetter dataGetter) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        for (final Match m : this.matchMap.values()) {
            if (m.getSignMatchAttendee(player.getPlayerId()) != null) {
                return m.receiveBox(player, point, dataGetter);
            }
        }
        for (final Match m : this.matchMap.values()) {
            if (m.isPassPlayerCondition(player.getPlayerId())) {
                return m.receiveBox(player, point, dataGetter);
            }
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10006);
    }
    
    public byte[] queryRankList(final int playerId, final IPlayerDao playerDao) {
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        Match defaultMatch = null;
        for (final Match match : this.matchMap.values()) {
            if (defaultMatch == null) {
                defaultMatch = match;
            }
            if (match.getSignMatchAttendee(playerId) != null) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("canSign", true);
                doc.createElement("isIn", true);
                MatchJsonBuilder.buildRankList(playerId, match, doc, playerDao);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
        for (final Match match : this.matchMap.values()) {
            if (match.isPassPlayerCondition(playerId)) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("canSign", true);
                doc.createElement("isIn", match.getSignMatchAttendee(playerId) != null);
                MatchJsonBuilder.buildRankList(playerId, match, doc, playerDao);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("canSign", false);
        MatchJsonBuilder.buildRankList(playerId, defaultMatch, doc2, playerDao);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    public void queryNotify(final int playerId, final JsonDocument doc) {
        if (this.matchMap.size() == 0) {
            doc.startObject("worldMatchNotify");
            doc.createElement("state", 0);
            doc.endObject();
            return;
        }
        Match defaultMatch = null;
        for (final Match match : this.matchMap.values()) {
            if (defaultMatch == null) {
                defaultMatch = match;
            }
            if (match.getSignMatchAttendee(playerId) != null) {
                doc.startObject("worldMatchNotify");
                doc.createElement("canSign", true);
                match.buildNotify(playerId, doc);
                doc.endObject();
                return;
            }
        }
        for (final Match match : this.matchMap.values()) {
            if (match.isPassPlayerCondition(playerId)) {
                doc.startObject("worldMatchNotify");
                doc.createElement("canSign", true);
                match.buildNotify(playerId, doc);
                doc.endObject();
                return;
            }
        }
        doc.startObject("worldMatchNotify");
        doc.createElement("canSign", false);
        defaultMatch.buildNotify(playerId, doc);
        doc.endObject();
    }
    
    public byte[] getRankList(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (this.matchMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_KFWD_MATCH_10002);
        }
        Match defaultMatch = null;
        for (final Match match : this.matchMap.values()) {
            if (defaultMatch == null) {
                defaultMatch = match;
            }
            if (match.getSignMatchAttendee(playerId) != null) {
                defaultMatch = match;
                break;
            }
            if (match.isPassPlayerCondition(playerId)) {
                defaultMatch = match;
                break;
            }
        }
        return defaultMatch.getFinalRank(playerDto);
    }
    
    public void clearFinishedMatch() {
        final List<String> needRemoveMatchTagList = new ArrayList<String>();
        boolean needNotify = false;
        final long now = System.currentTimeMillis();
        for (final Match match : this.matchMap.values()) {
            if ((match.getState() == 6 || match.getState() == 7 || match.getState() == 0) && now - match.getMatchTime().getTime() > 10800000L) {
                needRemoveMatchTagList.add(match.getMatchTag());
            }
        }
        for (final String matchTag : needRemoveMatchTagList) {
            this.removeMatch(matchTag);
            needNotify = true;
        }
    }
}
