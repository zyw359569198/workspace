package com.reign.gcld.battle.common;

import org.springframework.stereotype.*;
import com.reign.gcld.common.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.gcld.sdata.domain.*;

@Component("BattleDropFactory")
public class BattleDropFactory
{
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger battleLog;
    private static final BattleDropFactory instance;
    
    static {
        battleLog = new BattleLogger();
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
            BattleDropFactory.battleLog.error("BattleDropAnd\u9519\u8bef\uff1a" + dropString);
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
            BattleDropFactory.battleLog.error("BattleDropAnd\u9519\u8bef\uff1a" + dropString);
            return null;
        }
        return new BattleDropAnd(drops, true);
    }
    
    public BattleDrop getBattleDrop(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            BattleDropFactory.battleLog.error("BattleDropFactory\u5c1d\u8bd5\u89e3\u6790\u7a7a\u5b57\u7b26\u4e32");
            return null;
        }
        final String[] ss = dropString.split(",");
        if (ss.length < 2) {
            BattleDropFactory.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
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
                battleDrop.id = 1;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("DoubleTicket")) {
                battleDrop.type = 23;
                battleDrop.num = Integer.parseInt(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("recruit_token")) {
                battleDrop.type = 42;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("moonCake")) {
                battleDrop.type = 30;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("bmw")) {
                battleDrop.type = 31;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("xo")) {
                battleDrop.type = 32;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("picasso")) {
                battleDrop.type = 33;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("ironTicket")) {
                battleDrop.type = 34;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("giftBox")) {
                battleDrop.type = 35;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("ball")) {
                battleDrop.type = 36;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("snow")) {
                battleDrop.type = 37;
                battleDrop.num = Integer.valueOf(ss[1]);
            }
            else if (ss[0].equalsIgnoreCase("baoZhu")) {
                battleDrop.type = 38;
                battleDrop.num = Integer.valueOf(ss[1]);
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
                final Items item = (Items)this.dataGetter.getItemsCache().get((Object)battleDrop.id);
                battleDrop.reserve = String.valueOf(item.getName()) + "*" + item.getPic();
            }
            else if (ss[0].equalsIgnoreCase("drawing")) {
                battleDrop.id = Integer.valueOf(ss[1]);
                battleDrop.pro = 1.0;
                battleDrop.type = 800 + battleDrop.id;
                battleDrop.num = 1;
                final Items item = (Items)this.dataGetter.getItemsCache().get((Object)battleDrop.id);
                battleDrop.reserve = String.valueOf(item.getName()) + "*" + item.getPic();
            }
            else if (ss[0].equalsIgnoreCase("solo_drama")) {
                battleDrop.id = Integer.valueOf(ss[1]);
                battleDrop.pro = 1.0;
                battleDrop.type = 105;
            }
            return battleDrop;
        }
        catch (Exception e) {
            BattleDropFactory.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
            return null;
        }
    }
}
