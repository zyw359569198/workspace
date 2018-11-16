package com.reign.gcld.battle.common;

import java.io.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import java.util.*;
import com.reign.gcld.common.*;

public class BattleDrop implements Serializable
{
    private static final long serialVersionUID = -1242469678903096560L;
    private static final Logger battleLog;
    public static final String COPPER = "copper";
    public static final String LUMBER = "lumber";
    public static final String FOOD = "food";
    public static final String IRON = "iron";
    public static final String CHIEFEX = "ChiefExp";
    public static final String GEM = "gem";
    public static final String DOUBLE_TICKET = "DoubleTicket";
    public static final String RECRUIT_TOKEN = "recruit_token";
    public static final String MOON_CAKE = "moonCake";
    public static final String BAO_MA = "bmw";
    public static final String MEI_JIU = "xo";
    public static final String SHU_HUA = "picasso";
    public static final String IRON_TICKET = "ironTicket";
    public static final String GIFT_BOX = "giftBox";
    public static final String BALL = "ball";
    public static final String SNOW = "snow";
    public static final String BAO_ZHU = "baoZhu";
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
    public static final int DROP_TOUZI_DOUBLE_TICKET = 23;
    public static final int DROP_FREE_JIEBING = 24;
    public static final int DROP_FREE_ZHANCHENG = 25;
    public static final int DROP_FREE_ZHUGONG = 26;
    public static final int DROP_FREE_ZHUZHEN = 27;
    public static final int DROP_STONE = 28;
    public static final int DROP_STONE_SRC = 29;
    public static final int DROP_MOON_CAKE = 30;
    public static final int DROP_BAO_MA = 31;
    public static final int DROP_MEI_JIU = 32;
    public static final int DROP_SHU_HUA = 33;
    public static final int DROP_IRON_TICKET = 34;
    public static final int DROP_GIFT_BOX = 35;
    public static final int DROP_BALL = 36;
    public static final int DROP_SNOW = 37;
    public static final int DROP_BAO_ZHU = 38;
    public static final int DROP_MUBINGLING = 42;
    public static final int DROP_LIANBINGLING = 43;
    public static final int DROP_CITY_SLAVE = 44;
    public static final int DROP_TROOP_WUZI_TYPE_START = 1000;
    public static final int DROP_COPPER_TYPE_WUZI = 1001;
    public static final int DROP_LUMBER_TYPE_WUZI = 1002;
    public static final int DROP_FOOD_TYPE_WUZI = 1003;
    public static final int DROP_IRON_TYPE_WUZI = 1004;
    public static final int DROP_CHIEFEXP_TYPE_WUZI = 1005;
    public static final int DROP_DUEl_TYPE = 1006;
    public static final int DROP_GEM_TYPE_WUZI = 1007;
    public static final int DROP_TOUZI_DOUBLE_TICKET_WUZI = 1023;
    public static final int DROP_MOON_CAKE_WUZI = 1030;
    public static final int DROP_BAO_MA_WUZI = 1031;
    public static final int DROP_MEI_JIU_WUZI = 1032;
    public static final int DROP_SHU_HUA_WUZI = 1033;
    public static final int DROP_IRON_TICKET_WUZI = 1034;
    public static final int DROP_GIFT_BOX_WUZI = 1035;
    public static final int DROP_BALL_WUZI = 1036;
    public static final int DROP_SNOW_WUZI = 1037;
    public static final int DROP_BAO_ZHU_WUZI = 1038;
    public static final String GENERAL = "general";
    public static final String TECH = "tech";
    public static final String ARMIES_REWARD = "armies_reward";
    public static final String POWER_EXTRA = "power_extra";
    public static final String DROP_ITEM = "drop_item";
    public static final String DROP_TUZHI = "drawing";
    public static final String DROP_PERSONAL_JUBEN = "solo_drama";
    public static final int DROP_GENERAL_TYPE = 101;
    public static final int DROP_TECH_TYPE = 102;
    public static final int DROP_ARMIES_REWARD_TYPE = 103;
    public static final int DROP_POWER_EXTRA_TYPE = 104;
    public static final int DROP_PERSONAL_JUBEN_TYPE = 105;
    public static final int DROP_ITEM_TYPE_START = 200;
    public static final int DROP_DRAWING_TYPE_START = 800;
    public static final Set<Integer> REPORT_SPECIAL_TYPE_1;
    public int type;
    public int id;
    public int num;
    public double pro;
    public int limit;
    public String reserve;
    public String reason;
    
    static {
        battleLog = new BattleLogger();
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
        this.reason = null;
    }
    
    public BattleDrop(final BattleDrop battleDrop) {
        this.type = 0;
        this.id = 0;
        this.num = 0;
        this.pro = 0.0;
        this.limit = 0;
        this.reserve = null;
        this.reason = null;
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
        this.reason = null;
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
            BattleDrop.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
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
            if (ss[0].equalsIgnoreCase("DoubleTicket")) {
                return 23;
            }
            if (ss[0].equalsIgnoreCase("moonCake")) {
                return 30;
            }
            if (ss[0].equalsIgnoreCase("bmw")) {
                return 31;
            }
            if (ss[0].equalsIgnoreCase("xo")) {
                return 32;
            }
            if (ss[0].equalsIgnoreCase("picasso")) {
                return 33;
            }
            if (ss[0].equalsIgnoreCase("ironTicket")) {
                return 34;
            }
            if (ss[0].equalsIgnoreCase("giftBox")) {
                return 35;
            }
            if (ss[0].equalsIgnoreCase("ball")) {
                return 36;
            }
            if (ss[0].equalsIgnoreCase("snow")) {
                return 37;
            }
            if (ss[0].equalsIgnoreCase("baoZhu")) {
                return 38;
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
            if (ss[0].equalsIgnoreCase("solo_drama")) {
                return 105;
            }
            return 0;
        }
        catch (Exception e) {
            BattleDrop.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
            return 0;
        }
    }
    
    public static String getDropToString(final String dropString) {
        if (dropString == null || dropString.trim().isEmpty()) {
            return null;
        }
        final String[] ss = dropString.split(",");
        if (ss.length < 2) {
            BattleDrop.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
            return null;
        }
        try {
            if (ss[0].equalsIgnoreCase("copper")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10004;
            }
            if (ss[0].equalsIgnoreCase("lumber")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10005;
            }
            if (ss[0].equalsIgnoreCase("food")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10017;
            }
            if (ss[0].equalsIgnoreCase("iron")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10018;
            }
            if (ss[0].equalsIgnoreCase("ChiefExp")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10006;
            }
            if (ss[0].equalsIgnoreCase("recruit_token")) {
                return String.valueOf(ss[1]) + LocalMessages.T_COMM_10022;
            }
            return null;
        }
        catch (Exception e) {
            BattleDrop.battleLog.error("BattleDropFactory\u89e3\u6790\u7684\u5b57\u7b26\u4e32\u975e\u6cd5\uff1a" + dropString);
            return null;
        }
    }
}
