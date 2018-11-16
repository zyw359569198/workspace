package com.reign.kfgz.battle;

import org.springframework.stereotype.*;
import com.reign.kf.match.sdata.common.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;

@Component("BattleDropFactory")
public class BattleDropFactory
{
    private static final BattleDropFactory instance;
    
    static {
        instance = new BattleDropFactory();
    }
    
    public static BattleDropFactory getInstance() {
        return BattleDropFactory.instance;
    }
    
    public BattleDropAnd getBattleDropAnd(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            return null;
        }
        final String[] drops = dropString.split(";");
        if (drops.length < 1) {
            return null;
        }
        return new BattleDropAnd(drops);
    }
    
    public BattleDropAnd getTroopDropAnd(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            return null;
        }
        final String[] drops = dropString.split(";");
        if (drops.length < 1) {
            return null;
        }
        return new BattleDropAnd(drops, true);
    }
    
    public BattleDrop getBattleDrop(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            return null;
        }
        final String[] ss = dropString.split(",");
        if (ss.length < 2) {
            return null;
        }
        try {
            final BattleDrop battleDrop = new BattleDrop();
            if (ss[0].equalsIgnoreCase("copper")) {
                battleDrop.type = 1;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("lumber")) {
                battleDrop.type = 2;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("food")) {
                battleDrop.type = 3;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("iron")) {
                battleDrop.type = 4;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("ChiefExp")) {
                battleDrop.type = 5;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("gem")) {
                battleDrop.type = 7;
                battleDrop.id = Integer.parseInt(ss[1]);
                battleDrop.num = Integer.parseInt(ss[2]);
            }
            else if (ss[0].equalsIgnoreCase("gzjiebing")) {
                battleDrop.type = 501;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("general")) {
                battleDrop.type = 101;
                battleDrop.id = Integer.parseInt(ss[1]);
                battleDrop.pro = Integer.parseInt(ss[2]);
            }
            else if (ss[0].equalsIgnoreCase("tech")) {
                battleDrop.type = 102;
                battleDrop.id = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("armies_reward")) {
                battleDrop.type = 103;
                battleDrop.id = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("power_extra")) {
                battleDrop.type = 104;
                battleDrop.id = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("drop_item")) {
                battleDrop.id = Integer.valueOf(ss[1]);
                battleDrop.pro = Double.valueOf(ss[2]);
                battleDrop.type = 200 + battleDrop.id % 1000;
                battleDrop.num = 1;
                final Items item = ItemsCache.getItemsById(battleDrop.id);
                battleDrop.reserve = String.valueOf(item.getName()) + "*" + item.getPic();
            }
            else if (ss[0].equalsIgnoreCase("drawing")) {
                battleDrop.id = Integer.valueOf(ss[1]);
                battleDrop.pro = 1.0;
                battleDrop.type = 800 + battleDrop.id;
                battleDrop.num = 1;
                final Items item = ItemsCache.getItemsById(battleDrop.id);
                battleDrop.reserve = String.valueOf(item.getName()) + "*" + item.getPic();
            }
            return battleDrop;
        }
        catch (Exception e) {
            return null;
        }
    }
}
