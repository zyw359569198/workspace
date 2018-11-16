package com.reign.gcld.battle.scene;

import java.io.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

public class PlayerInfo implements Serializable
{
    private static final long serialVersionUID = -8783437271194511140L;
    boolean isAttSide;
    int playerId;
    int curStrategy;
    int maxKillG;
    int killTotal;
    int lostTotal;
    int rbType;
    int rbTotal;
    int rbTop;
    int autoStrategy;
    public int battleMode;
    public static final int BATTLE_MODE_0_UNFIGHTED_WAIT = 0;
    public static final int BATTLE_MODE_1_UNFIGHTED_QUIT = 1;
    public static final int BATTLE_MODE_2_FIGHTED = 2;
    public static final int BATTLE_MODE_3_LAST_WIN = 3;
    Map<Integer, BattleDrop> dropMap;
    
    public PlayerInfo() {
        this.isAttSide = false;
        this.maxKillG = 0;
        this.killTotal = 0;
        this.lostTotal = 0;
        this.rbType = 0;
        this.rbTotal = 0;
        this.rbTop = 0;
        this.autoStrategy = 0;
        this.battleMode = 0;
        this.dropMap = new HashMap<Integer, BattleDrop>();
    }
    
    public PlayerInfo(final int playerId, final boolean isAttSide, final int autoStrategy) {
        this.isAttSide = false;
        this.maxKillG = 0;
        this.killTotal = 0;
        this.lostTotal = 0;
        this.rbType = 0;
        this.rbTotal = 0;
        this.rbTop = 0;
        this.autoStrategy = 0;
        this.battleMode = 0;
        this.dropMap = new HashMap<Integer, BattleDrop>();
        this.playerId = playerId;
        this.isAttSide = isAttSide;
        this.autoStrategy = autoStrategy;
    }
    
    public int getAutoStrategy() {
        return this.autoStrategy;
    }
    
    public void setAutoStrategy(final int autoStrategy) {
        this.autoStrategy = autoStrategy;
    }
    
    public void addDropMap(final Map<Integer, BattleDrop> addMap) {
        for (final Map.Entry<Integer, BattleDrop> entry : addMap.entrySet()) {
            this.addDrop(entry.getValue());
        }
    }
    
    public void addDropAnd(final BattleDropAnd battleDropAnd) {
        for (final Map.Entry<Integer, BattleDrop> entry : battleDropAnd.getDropAndMap().entrySet()) {
            this.addDrop(entry.getValue());
        }
    }
    
    public void addDrop(final BattleDrop battleDrop) {
        final Integer key = battleDrop.type;
        if (this.dropMap.containsKey(key)) {
            final BattleDrop battleDrop2 = this.dropMap.get(key);
            battleDrop2.num += battleDrop.num;
        }
        else {
            this.dropMap.put(key, new BattleDrop(battleDrop));
        }
    }
    
    public int getMaxKillG() {
        return this.maxKillG;
    }
    
    public void setMaxKillG(final int maxKillG) {
        this.maxKillG = maxKillG;
    }
    
    public int getRbType() {
        return this.rbType;
    }
    
    public void setRbType(final int rbType) {
        this.rbType = rbType;
    }
    
    public int getRbTop() {
        return this.rbTop;
    }
    
    public void setRbTop(final int rbTop) {
        this.rbTop = rbTop;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public boolean isAttSide() {
        return this.isAttSide;
    }
    
    public void setAttSide(final boolean isAttSide) {
        this.isAttSide = isAttSide;
    }
    
    public int getKillTotal() {
        return this.killTotal;
    }
    
    public void setKillTotal(final int killTotal) {
        this.killTotal = killTotal;
    }
    
    public int getLostTotal() {
        return this.lostTotal;
    }
    
    public void setLostTotal(final int lostTotal) {
        this.lostTotal = lostTotal;
    }
    
    public int getRbTotal() {
        return this.rbTotal;
    }
    
    public void setRbTotal(final int rbTotal) {
        this.rbTotal = rbTotal;
    }
}
