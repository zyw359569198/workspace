package com.reign.gcld.battle.common;

import java.util.*;

public class RoundReward
{
    public int gExp;
    public int mUpLv;
    public int gUpLv;
    public Map<Integer, BattleDrop> roundDropMap;
    
    public RoundReward() {
        this.gExp = 0;
        this.mUpLv = 0;
        this.gUpLv = 0;
        this.roundDropMap = new HashMap<Integer, BattleDrop>();
    }
    
    public void addDrop(final BattleDrop battleDrop) {
        final Integer key = battleDrop.type;
        if (this.roundDropMap.containsKey(key)) {
            final BattleDrop battleDrop2 = this.roundDropMap.get(key);
            battleDrop2.num += battleDrop.num;
        }
        else {
            this.roundDropMap.put(key, new BattleDrop(battleDrop));
        }
    }
    
    public void addDropMap(final Map<Integer, BattleDrop> addMap) {
        for (final BattleDrop battleDrop : addMap.values()) {
            final Integer key = battleDrop.type;
            if (this.roundDropMap.containsKey(key)) {
                final BattleDrop battleDrop2 = this.roundDropMap.get(key);
                battleDrop2.num += battleDrop.num;
            }
            else {
                this.roundDropMap.put(key, new BattleDrop(battleDrop));
            }
        }
    }
}
