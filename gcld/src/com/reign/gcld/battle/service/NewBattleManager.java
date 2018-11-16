package com.reign.gcld.battle.service;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;

public class NewBattleManager
{
    private static final Logger battleLog;
    private static final NewBattleManager instance;
    private Object lock;
    public int BarbarainInvadeRound;
    public long BarbarainInvadeCountDown;
    Map<Integer, ConcurrentHashMap<Integer, Battle>> pBatInfoMap;
    Map<Integer, Battle> pWatchBatMap;
    public Lock[] barbarainLocks;
    public Lock[] tryLocks;
    Map<Integer, Long> countryBarbarainBatMap;
    Map<Integer, Long> tryBatMap;
    Map<String, Battle> batMap;
    public static final String LINE = "_";
    
    static {
        battleLog = new BattleLogger();
        instance = new NewBattleManager();
    }
    
    private NewBattleManager() {
        this.lock = new Object();
        this.BarbarainInvadeRound = 0;
        this.BarbarainInvadeCountDown = 0L;
        this.pBatInfoMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Battle>>();
        this.pWatchBatMap = new ConcurrentHashMap<Integer, Battle>();
        this.barbarainLocks = new ReentrantLock[3];
        this.tryLocks = new ReentrantLock[3];
        this.countryBarbarainBatMap = new ConcurrentHashMap<Integer, Long>();
        this.tryBatMap = new ConcurrentHashMap<Integer, Long>();
        this.batMap = new ConcurrentHashMap<String, Battle>();
        for (int i = 0; i < this.barbarainLocks.length; ++i) {
            this.barbarainLocks[i] = new ReentrantLock();
        }
    }
    
    public static NewBattleManager getInstance() {
        return NewBattleManager.instance;
    }
    
    public ConcurrentHashMap<Integer, Battle> getGMap(final int playerId) {
        return this.pBatInfoMap.get(playerId);
    }
    
    public void joinBattle(final Battle bat, final int playerId, final int... generalIds) {
        ConcurrentHashMap<Integer, Battle> gMap = null;
        if (this.pBatInfoMap.containsKey(playerId)) {
            gMap = this.pBatInfoMap.get(playerId);
        }
        else {
            gMap = new ConcurrentHashMap<Integer, Battle>();
            this.pBatInfoMap.put(playerId, gMap);
        }
        for (final int gId : generalIds) {
            gMap.put(gId, bat);
        }
    }
    
    public void quitBattle(final Battle bat, final int playerId, final int... generalIds) {
        Map<Integer, Battle> gMap = null;
        if (!this.pBatInfoMap.containsKey(playerId)) {
            return;
        }
        gMap = this.pBatInfoMap.get(playerId);
        for (final int gId : generalIds) {
            gMap.remove(gId);
        }
    }
    
    public void quitBattle(final int playerId, final String battleId) {
        Map<Integer, Battle> gMap = null;
        if (!this.pBatInfoMap.containsKey(playerId)) {
            return;
        }
        gMap = this.pBatInfoMap.get(playerId);
        for (final Integer key : gMap.keySet()) {
            final Battle bat = gMap.get(key);
            if (bat.getBattleId().equals(battleId)) {
                gMap.remove(key);
            }
        }
    }
    
    public Battle createBattle(final String battleId) {
        synchronized (this.lock) {
            if (this.batMap.containsKey(battleId)) {
                // monitorexit(this.lock)
                return null;
            }
            final Battle battle = new Battle(battleId);
            this.batMap.put(battleId, battle);
            // monitorexit(this.lock)
            return battle;
        }
    }
    
    public void deleteBattle(final String battleId) {
        synchronized (this.lock) {
            if (this.batMap.containsKey(battleId)) {
                this.batMap.remove(battleId);
                GroupManager.getInstance().deleteGroup(battleId);
            }
        }
        // monitorexit(this.lock)
    }
    
    public Battle getBattleByBatType(final int playerId, final int battleType) {
        ConcurrentHashMap<Integer, Battle> gMap = this.pBatInfoMap.get(playerId);
        if (gMap == null) {
            gMap = new ConcurrentHashMap<Integer, Battle>();
            this.pBatInfoMap.put(playerId, gMap);
        }
        for (final Integer key : gMap.keySet()) {
            final Battle bat = gMap.get(key);
            if (bat.getBattleType() == battleType) {
                return bat;
            }
        }
        return null;
    }
    
    public Battle getBattleByBatId(final String battleId) {
        if (battleId == null) {
            return null;
        }
        if (this.batMap.containsKey(battleId)) {
            return this.batMap.get(battleId);
        }
        return null;
    }
    
    public Battle getBattleByParm(final int battleType, final int attForceId, final int defId) {
        return this.getBattleByBatId(getBattleId(battleType, attForceId, defId));
    }
    
    public Battle getBattleByDefId(final int battleType, final int defId) {
        final List<Battle> battles = new LinkedList<Battle>();
        for (final String key : this.batMap.keySet()) {
            final String[] str = key.split("_");
            if (Integer.parseInt(str[2]) == defId && Integer.valueOf(str[0]) == battleType) {
                battles.add(this.batMap.get(key));
                return this.batMap.get(key);
            }
        }
        if (battles.size() == 0) {
            return null;
        }
        if (battles.size() > 1) {
            final StringBuilder sb = new StringBuilder();
            for (final Battle battle : battles) {
                sb.append(battle.getBattleId()).append(";");
            }
            ErrorSceneLog.getInstance().appendErrorMsg("NewBattleManager.getBattleByDefId get more than 1").append("battleType", battleType).append("defId", defId).append("battles", sb.toString()).flush();
            throw new RuntimeException("NewBattleManager.getBattleByDefId get more than 1");
        }
        return battles.get(0);
    }
    
    public List<Battle> getAllScenariosOneToOneBattle(final int playerId, final int nodeId) {
        final List<Battle> battles = new LinkedList<Battle>();
        for (final String key : this.batMap.keySet()) {
            final String[] str = key.split("_");
            if (Integer.valueOf(str[0]) == 19 && Integer.parseInt(str[2]) == nodeId) {
                final int temp = Integer.valueOf(str[1]) / 1000;
                if (temp != playerId) {
                    continue;
                }
                battles.add(this.batMap.get(key));
            }
        }
        if (battles.size() == 0) {
            return null;
        }
        return battles;
    }
    
    public Set<Integer> getInBattleCitySet() {
        final Set<Integer> citySet = new HashSet<Integer>();
        for (final Battle battle : this.batMap.values()) {
            if (battle.getBattleType() == 3 || battle.getBattleType() == 13 || battle.getBattleType() == 14 || battle.getBattleType() == 15) {
                citySet.add(battle.getDefBaseInfo().getId());
            }
        }
        return citySet;
    }
    
    public Battle getBattleByGId(final int playerId, final int generalId) {
        ConcurrentHashMap<Integer, Battle> gMap = this.pBatInfoMap.get(playerId);
        if (gMap == null) {
            gMap = new ConcurrentHashMap<Integer, Battle>();
            this.pBatInfoMap.put(playerId, gMap);
        }
        return gMap.get(generalId);
    }
    
    public Map<Integer, Battle> getBattleByPid(final int playerId) {
        ConcurrentHashMap<Integer, Battle> gMap = this.pBatInfoMap.get(playerId);
        if (gMap == null) {
            gMap = new ConcurrentHashMap<Integer, Battle>();
            this.pBatInfoMap.put(playerId, gMap);
        }
        final ConcurrentHashMap<Integer, Battle> pMap = new ConcurrentHashMap<Integer, Battle>();
        for (final Integer key : gMap.keySet()) {
            final Battle bat = gMap.get(key);
            if (!pMap.containsKey(bat.getBattleType())) {
                pMap.put(bat.getBattleType(), bat);
            }
        }
        return pMap;
    }
    
    public Battle resetBattle(final Battle battle) {
        synchronized (this.lock) {
            if (this.batMap.containsKey(battle.getBattleId())) {
                NewBattleManager.battleLog.error("NewBattleManager.resetBattle: battle already exists, reset skipped. battleId:" + battle.getBattleId());
                // monitorexit(this.lock)
                return null;
            }
            this.batMap.put(battle.getBattleId(), battle);
            for (final CampArmy campArmy : battle.getAttCamp()) {
                if (campArmy.isInBattle()) {
                    ConcurrentHashMap<Integer, Battle> gMap = this.pBatInfoMap.get(campArmy.getPlayerId());
                    if (gMap == null) {
                        gMap = new ConcurrentHashMap<Integer, Battle>();
                        this.pBatInfoMap.put(campArmy.getPlayerId(), gMap);
                    }
                    gMap.put(campArmy.getGeneralId(), battle);
                }
            }
            for (final CampArmy campArmy : battle.getDefCamp()) {
                if (campArmy.isInBattle()) {
                    ConcurrentHashMap<Integer, Battle> gMap = this.pBatInfoMap.get(campArmy.getPlayerId());
                    if (gMap == null) {
                        gMap = new ConcurrentHashMap<Integer, Battle>();
                        this.pBatInfoMap.put(campArmy.getPlayerId(), gMap);
                    }
                    gMap.put(campArmy.getGeneralId(), battle);
                }
            }
            // monitorexit(this.lock)
            return battle;
        }
    }
    
    public void clearBarbarainKill(final int forceId) {
        try {
            this.barbarainLocks[forceId - 1].lock();
            this.countryBarbarainBatMap.remove(forceId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("clearBarbarainKill catch exception. forceId=" + forceId, e);
            return;
        }
        finally {
            this.barbarainLocks[forceId - 1].unlock();
        }
        this.barbarainLocks[forceId - 1].unlock();
    }
    
    public void clearTryKill(final int forceId) {
        try {
            this.tryLocks[forceId - 1].lock();
            this.tryBatMap.remove(forceId);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("clearTryKill catch exception. forceId=" + forceId, e);
            return;
        }
        finally {
            this.tryLocks[forceId - 1].unlock();
        }
        this.tryLocks[forceId - 1].unlock();
    }
    
    public void addBarbarainKill(final int forceId, final int killAdd) {
        if (killAdd == 0) {
            return;
        }
        if (killAdd < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("addBarbarainKill, killAdd is negative").append("forceId", forceId).append("killAdd", killAdd).flush();
            return;
        }
        if (forceId != 1 && forceId != 2 && forceId != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("addBarbarainKill forceId invalid").append("forceId", forceId).flush();
            return;
        }
        try {
            this.barbarainLocks[forceId - 1].lock();
            Long killTotal = this.countryBarbarainBatMap.get(forceId);
            if (killTotal == null) {
                this.countryBarbarainBatMap.put(forceId, (long)killAdd);
            }
            else {
                killTotal += (Long)killAdd;
                this.countryBarbarainBatMap.put(forceId, killTotal);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addBarbarainKill catch exception", e);
            return;
        }
        finally {
            this.barbarainLocks[forceId - 1].unlock();
        }
        this.barbarainLocks[forceId - 1].unlock();
    }
    
    public void addTryKill(final int forceId, final int killAdd) {
        if (killAdd == 0) {
            return;
        }
        if (killAdd < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("addTryKill, killAdd is negative").append("forceId", forceId).append("killAdd", killAdd).flush();
            return;
        }
        if (forceId != 1 && forceId != 2 && forceId != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("addTryKill forceId invalid").append("forceId", forceId).flush();
            return;
        }
        try {
            this.tryLocks[forceId - 1].lock();
            Long killTotal = this.tryBatMap.get(forceId);
            if (killTotal == null) {
                this.tryBatMap.put(forceId, (long)killAdd);
            }
            else {
                killTotal += (Long)killAdd;
                this.tryBatMap.put(forceId, killTotal);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("addTryKill catch exception", e);
            return;
        }
        finally {
            this.tryLocks[forceId - 1].unlock();
        }
        this.tryLocks[forceId - 1].unlock();
    }
    
    public long getBarbarainKillByForceId(final int forceId) {
        if (forceId != 1 && forceId != 2 && forceId != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("getKillTotalByForceId forceId invalid").append("forceId", forceId).flush();
            return Long.MIN_VALUE;
        }
        try {
            this.barbarainLocks[forceId - 1].lock();
            final Long killTotal = this.countryBarbarainBatMap.get(forceId);
            if (killTotal == null) {
                return Long.MIN_VALUE;
            }
            return killTotal;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("getBarbarainKillByForceId catch exception", e);
        }
        finally {
            this.barbarainLocks[forceId - 1].unlock();
        }
        return Long.MIN_VALUE;
    }
    
    public long getTryKillByForceId(final int forceId) {
        if (forceId != 1 && forceId != 2 && forceId != 3) {
            ErrorSceneLog.getInstance().appendErrorMsg("geTryKillByForceId forceId invalid").append("forceId", forceId).flush();
            return Long.MIN_VALUE;
        }
        final Long killTotal = this.tryBatMap.get(forceId);
        if (killTotal == null) {
            return Long.MIN_VALUE;
        }
        return killTotal;
    }
    
    public Battle getPlayerWatchBattle(final int playerId) {
        if (!this.pWatchBatMap.containsKey(playerId)) {
            return null;
        }
        return this.pWatchBatMap.get(playerId);
    }
    
    public void setPlayerWatchBattle(final int playerId, final Battle bat) {
        this.pWatchBatMap.put(playerId, bat);
    }
    
    public void clearPlayerWatchBattle(final int playerId) {
        this.pWatchBatMap.remove(playerId);
    }
    
    public void clearPlayerWatchBattle(final int playerId, final String battleId) {
        if (!this.pWatchBatMap.containsKey(playerId)) {
            return;
        }
        if (this.pWatchBatMap.get(playerId).getBattleId().equals(battleId)) {
            this.pWatchBatMap.remove(playerId);
        }
    }
    
    public boolean isWatchBattle(final int playerId, final String battleId) {
        return this.pWatchBatMap.containsKey(playerId) && this.pWatchBatMap.get(playerId).getBattleId().equals(battleId);
    }
    
    public static String getBattleId(final int battleType, final int attForceId, final int defId) {
        if (battleType == 18) {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(attForceId);
            return juBenDto.juBenCityDtoMap.get(defId).battleId;
        }
        if (battleType == 3 || battleType == 14) {
            return CityService.cityBatIdSet.get(defId);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(battleType).append("_").append(attForceId).append("_").append(defId);
        return sb.toString();
    }
    
    public static void inPveBattle(final int playerId, final boolean inBattle) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("inPveBattle", inBattle);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    public static void inOccupyBattle(final int playerId, final boolean inBattle) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("inOccupyBattle", inBattle);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
}
