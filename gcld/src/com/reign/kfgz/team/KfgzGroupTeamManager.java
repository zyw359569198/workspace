package com.reign.kfgz.team;

import java.util.concurrent.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;
import com.reign.kfgz.comm.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;

public class KfgzGroupTeamManager
{
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfGroupArmyTeam>> teamMap;
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>>> focusTeamMap;
    
    static {
        KfgzGroupTeamManager.teamMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfGroupArmyTeam>>();
        KfgzGroupTeamManager.focusTeamMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>>>();
    }
    
    public static void addNewTeam(final KfGroupArmyTeam kfGroupArmyTeam) {
        final int gzId = kfGroupArmyTeam.getGzId();
        final int teamId = kfGroupArmyTeam.getTeamId();
        KfgzGroupTeamManager.teamMap.putIfAbsent(gzId, new ConcurrentHashMap<Integer, KfGroupArmyTeam>());
        KfgzGroupTeamManager.teamMap.get(gzId).put(teamId, kfGroupArmyTeam);
    }
    
    public static void clearGzIdInfo(final int gzId) {
        KfgzGroupTeamManager.teamMap.remove(gzId);
        KfgzGroupTeamManager.focusTeamMap.remove(gzId);
    }
    
    public static boolean hasPlayerCreateTeam(final int gzId, final int cId) {
        final ConcurrentHashMap<Integer, KfGroupArmyTeam> gzIdTeamMap = KfgzGroupTeamManager.teamMap.get(gzId);
        if (gzIdTeamMap == null) {
            return false;
        }
        for (final KfGroupArmyTeam team : gzIdTeamMap.values()) {
            if (team.isActive() && team.getCreateCId() == cId) {
                return true;
            }
        }
        return false;
    }
    
    public static KfGroupArmyTeam getPlayerGroupArmyInfo(final int gzId, final int cId) {
        final ConcurrentHashMap<Integer, KfGroupArmyTeam> gzIdTeamMap = KfgzGroupTeamManager.teamMap.get(gzId);
        if (gzIdTeamMap == null) {
            return null;
        }
        for (final KfGroupArmyTeam team : gzIdTeamMap.values()) {
            if (team.isActive() && team.getCreateCId() == cId) {
                return team;
            }
        }
        return null;
    }
    
    public static KfGroupArmyTeam createNewTeam(final KfPlayerInfo player, final WorldLegion wl) {
        final int cId = player.getCompetitorId();
        final int gzId = player.getGzId();
        final int forceId = player.getForceId();
        final KfGroupArmyTeam team = new KfGroupArmyTeam(gzId, forceId, player, wl);
        return team;
    }
    
    public static Set<KfGroupArmyTeam> getJoinTeam(final int gzId, final KfPlayerInfo player) {
        final Set<KfGroupArmyTeam> set = new HashSet<KfGroupArmyTeam>();
        final ConcurrentHashMap<Integer, KfGroupArmyTeam> gzIdTeamMap = KfgzGroupTeamManager.teamMap.get(gzId);
        if (gzIdTeamMap == null) {
            return set;
        }
        for (final KfGroupArmyTeam team : gzIdTeamMap.values()) {
            if (team.getCreateCId() == player.getCompetitorId() && team.isActive()) {
                set.add(team);
            }
        }
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            final KfTeam gTeam = gInfo.getTeam();
            if (gTeam instanceof KfGroupArmyTeam && ((KfGroupArmyTeam)gTeam).isActive()) {
                set.add((KfGroupArmyTeam)gTeam);
            }
        }
        return set;
    }
    
    public static Set<KfGroupArmyTeam> getAllTeam(final int gzId) {
        final Set<KfGroupArmyTeam> set = new HashSet<KfGroupArmyTeam>();
        final ConcurrentHashMap<Integer, KfGroupArmyTeam> gzIdTeamMap = KfgzGroupTeamManager.teamMap.get(gzId);
        if (gzIdTeamMap == null) {
            return set;
        }
        for (final KfGroupArmyTeam team : gzIdTeamMap.values()) {
            if (team.isActive()) {
                set.add(team);
            }
        }
        return set;
    }
    
    public static Set<KfGroupArmyTeam> getAllTeamByForceId(final int gzId, final int forceId) {
        final Set<KfGroupArmyTeam> set = new HashSet<KfGroupArmyTeam>();
        final ConcurrentHashMap<Integer, KfGroupArmyTeam> gzIdTeamMap = KfgzGroupTeamManager.teamMap.get(gzId);
        if (gzIdTeamMap == null) {
            return set;
        }
        for (final KfGroupArmyTeam team : gzIdTeamMap.values()) {
            if (team.isActive() && team.getForceId() == forceId) {
                set.add(team);
            }
        }
        return set;
    }
    
    public static void focusOnGroupTeam(final int gzId, final int cId, final int forceId) {
        final Set<KfGroupArmyTeam> set = new HashSet<KfGroupArmyTeam>();
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>> gzIdFocusTeamMap = KfgzGroupTeamManager.focusTeamMap.get(gzId);
        if (gzIdFocusTeamMap == null) {
            gzIdFocusTeamMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>>();
            KfgzGroupTeamManager.focusTeamMap.putIfAbsent(gzId, gzIdFocusTeamMap);
        }
        ConcurrentSkipListSet<Integer> focusSet = gzIdFocusTeamMap.get(forceId);
        if (focusSet == null) {
            focusSet = new ConcurrentSkipListSet<Integer>();
            gzIdFocusTeamMap.put(forceId, focusSet);
        }
        focusSet.add(cId);
    }
    
    public static void unfocusOnGroupTeam(final int gzId, final int cId, final int forceId) {
        final ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>> gzIdFocusTeamMap = KfgzGroupTeamManager.focusTeamMap.get(gzId);
        if (gzIdFocusTeamMap == null) {
            return;
        }
        final ConcurrentSkipListSet<Integer> focusSet = gzIdFocusTeamMap.get(forceId);
        if (focusSet == null) {
            return;
        }
        focusSet.remove(cId);
    }
    
    public static ConcurrentSkipListSet<Integer> getFocusSet(final int gzId, final int forceId) {
        final ConcurrentHashMap<Integer, ConcurrentSkipListSet<Integer>> gzIdFocusTeamMap = KfgzGroupTeamManager.focusTeamMap.get(gzId);
        if (gzIdFocusTeamMap == null) {
            return null;
        }
        final ConcurrentSkipListSet<Integer> focusSet = gzIdFocusTeamMap.get(forceId);
        if (focusSet == null) {
            return null;
        }
        return focusSet;
    }
    
    public static void sendTeamChangeInfo(final int gzId, final int forceId) {
        final ConcurrentSkipListSet<Integer> focusSet = getFocusSet(gzId, forceId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gtc", 1);
        doc.endObject();
        if (focusSet != null) {
            for (final Integer cId : focusSet) {
                KfgzMessageSender.sendMsgToOne(cId, doc.toByte(), PushCommand.PUSH_KF_GROUPTEAMCHANGE);
            }
        }
    }
}
