package com.reign.kf.match.sdata.common;

import java.io.*;
import java.util.*;

public class BattleDrop implements Serializable
{
    private static final long serialVersionUID = -1242469678903096560L;
    public static final String COPPER = "copper";
    public static final String LUMBER = "lumber";
    public static final String FOOD = "food";
    public static final String IRON = "iron";
    public static final String CHIEFEX = "ChiefExp";
    public static final String GEM = "gem";
    public static final String GOLD = "gold";
    public static final String GZ_JIEBING = "gzjiebing";
    public static final int DROP_COPPER_TYPE = 1;
    public static final int DROP_LUMBER_TYPE = 2;
    public static final int DROP_FOOD_TYPE = 3;
    public static final int DROP_IRON_TYPE = 4;
    public static final int DROP_CHIEFEXP_TYPE = 5;
    public static final int DROP_NATIONA_RANK_BAT_JIFEN_TYPE = 6;
    public static final int DROP_GEM_TYPE = 7;
    public static final int DROP_RELEASE_SLAVE_TYPE = 8;
    public static final int DROP_BE_SLAVE_TYPE = 9;
    public static final int DROP_BE_SLAVEHOLDERS_TYPE = 10;
    public static final int DROP_GOLD_TYPE = 11;
    public static final int DROP_ADD_WORSHIP = 22;
    public static final int DROP_GENEXP_TYPE = 13;
    public static final int DROP_GZ_FREE_JIEBING = 501;
    public static final int DROP_TROOP_WUZI_TYPE_START = 1000;
    public static final int DROP_COPPER_TYPE_WUZI = 1001;
    public static final int DROP_LUMBER_TYPE_WUZI = 1002;
    public static final int DROP_FOOD_TYPE_WUZI = 1003;
    public static final int DROP_IRON_TYPE_WUZI = 1004;
    public static final int DROP_CHIEFEXP_TYPE_WUZI = 1005;
    public static final int DROP_DUEl_TYPE = 1006;
    public static final String GENERAL = "general";
    public static final String TECH = "tech";
    public static final String ARMIES_REWARD = "armies_reward";
    public static final String POWER_EXTRA = "power_extra";
    public static final String DROP_ITEM = "drop_item";
    public static final String DROP_TUZHI = "drawing";
    public static final int DROP_GENERAL_TYPE = 101;
    public static final int DROP_TECH_TYPE = 102;
    public static final int DROP_ARMIES_REWARD_TYPE = 103;
    public static final int DROP_POWER_EXTRA_TYPE = 104;
    public static final int DROP_ITEM_TYPE_START = 200;
    public static final int DROP_DRAWING_TYPE_START = 800;
    public static final Set<Integer> REPORT_SPECIAL_TYPE_1;
    public int type;
    public int id;
    public int num;
    public double pro;
    public int limit;
    public String reserve;
    
    static {
        (REPORT_SPECIAL_TYPE_1 = new HashSet<Integer>()).add(8);
        BattleDrop.REPORT_SPECIAL_TYPE_1.add(9);
        BattleDrop.REPORT_SPECIAL_TYPE_1.add(10);
    }
    
    public BattleDrop() {
        this.type = 0;
        this.id = 0;
        this.num = 0;
        this.pro = 0.0;
        this.limit = 0;
        this.reserve = null;
    }
    
    public BattleDrop(final BattleDrop battleDrop) {
        this.type = 0;
        this.id = 0;
        this.num = 0;
        this.pro = 0.0;
        this.limit = 0;
        this.reserve = null;
        this.type = battleDrop.type;
        this.id = battleDrop.id;
        this.num = battleDrop.num;
        this.pro = battleDrop.pro;
        this.reserve = battleDrop.reserve;
    }
    
    public BattleDrop(final BattleDrop battleDrop, final boolean isWuZi) {
        this.type = 0;
        this.id = 0;
        this.num = 0;
        this.pro = 0.0;
        this.limit = 0;
        this.reserve = null;
        final int type = battleDrop.type;
        this.type = battleDrop.type - 1000;
        this.id = battleDrop.id;
        this.num = battleDrop.num;
        this.pro = battleDrop.pro;
        this.reserve = battleDrop.reserve;
    }
    
    public static int getDropType(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            return 0;
        }
        final String[] ss = dropString.split(",");
        if (ss.length < 2) {
            return 0;
        }
        try {
            if (ss[0].equalsIgnoreCase("copper")) {
                return 1;
            }
            if (ss[0].equalsIgnoreCase("lumber")) {
                return 2;
            }
            if (ss[0].equalsIgnoreCase("food")) {
                return 3;
            }
            if (ss[0].equalsIgnoreCase("iron")) {
                return 4;
            }
            if (ss[0].equalsIgnoreCase("ChiefExp")) {
                return 5;
            }
            if (ss[0].equalsIgnoreCase("gem")) {
                return 7;
            }
            if (ss[0].equals("gzjiebing")) {
                return 501;
            }
            if (ss[0].equalsIgnoreCase("general")) {
                return 101;
            }
            if (ss[0].equalsIgnoreCase("tech")) {
                return 102;
            }
            if (ss[0].equalsIgnoreCase("armies_reward")) {
                return 103;
            }
            if (ss[0].equalsIgnoreCase("power_extra")) {
                return 104;
            }
            if (ss[0].equalsIgnoreCase("drop_item")) {
                final int id = Integer.parseInt(ss[1]);
                return 200 + id % 1000;
            }
            if (ss[0].equalsIgnoreCase("drawing")) {
                final int id = Integer.parseInt(ss[1]);
                return 800 + id % 1000;
            }
            return 0;
        }
        catch (Exception e) {
            return 0;
        }
    }
}
