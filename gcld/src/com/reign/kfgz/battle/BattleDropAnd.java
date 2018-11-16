package com.reign.kfgz.battle;

import java.io.*;
import com.reign.kf.match.sdata.common.*;
import java.util.*;

public class BattleDropAnd implements Serializable
{
    private static final long serialVersionUID = 1439198373665764155L;
    private Map<Integer, BattleDrop> battleDropMap;
    
    public BattleDropAnd(final String[] drops) {
        this.battleDropMap = null;
        this.battleDropMap = new HashMap<Integer, BattleDrop>();
        for (int i = 0; i < drops.length; ++i) {
            final BattleDrop battleDrop = BattleDropFactory.getInstance().getBattleDrop(drops[i]);
            if (battleDrop != null) {
                this.battleDropMap.put(battleDrop.type, battleDrop);
            }
        }
    }
    
    public BattleDropAnd(final String[] drops, final boolean isWuZi) {
        this.battleDropMap = null;
        this.battleDropMap = new HashMap<Integer, BattleDrop>();
        for (int i = 0; i < drops.length; ++i) {
            final BattleDrop battleDrop = BattleDropFactory.getInstance().getBattleDrop(drops[i]);
            if (battleDrop != null) {
                final BattleDrop battleDrop2 = battleDrop;
                battleDrop2.type += 1000;
                this.battleDropMap.put(battleDrop.type, battleDrop);
            }
        }
    }
    
    public Map<Integer, BattleDrop> getDropAndMap() {
        return this.battleDropMap;
    }
}
