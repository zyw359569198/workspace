package com.reign.kf.match.common;

import java.util.*;

public class BattleConstant
{
    public static final int COLUMN = 8;
    public static final int ROW = 3;
    public static final int FORCE_BATTLE_ROW = 10;
    public static final int TACTIC_MAX_TIMES = 0;
    public static final int TIME_FIRST = 12000;
    public static final int TIME_MOVE = 1400;
    public static final int TIME_ST_BASE = 700;
    public static final int TIME_ST_LOST = 1000;
    public static final int TIME_RB_TACTIC = 25000;
    public static final int TIME_TACTIC_ADD = 1600;
    public static final int TIME_BASE_LOST = 500;
    public static final int TIME_PER_LOST = 400;
    public static final int TIME_CHEERS = 0;
    public static final int TIME_ST_CHOOSE = 6000;
    public static final int TIME_LOWER_LIMIT = 3100;
    public static final int CHOOSE_TIME = 4000;
    public static final int BATTLE_ARMY = 1;
    public static final int BATTLE_ARMIES = 2;
    public static final int BATTLE_CITY = 3;
    public static final int BATTLE_OCCUPY = 4;
    public static final int BATTLE_ARMY_AUTO = 5;
    public static final int BATTLE_PERSONAL_MINE = 6;
    public static final int BATTLE_GROUP_MINE = 7;
    public static final int BATTLE_NATION_RANK = 8;
    public static final int BATTLE_KF_WD = 9;
    public static final int BATTLE_CITY_NPC = 10;
    public static final int BATTLE_ARMY_REWARD = 11;
    public static final int BATTLE_ARMY_EXTRA = 12;
    public static final int BATTLE_CITY_ONE2ONE = 13;
    public static final int BATTLE_ARMIES_TIME = 333;
    public static final int BATTLE_TITLE_PIC = 10000;
    public static final int HP_1 = 1;
    public static final int HP_2 = 2;
    public static final int HP_3 = 3;
    public static final int HP_4 = 4;
    public static final int FORCE_LEAVE_HP = -1;
    public static final int BACK_VIEW_TICKET = 0;
    public static final String SYMBOL_A = "|";
    public static final String SYMBOL_AA = "\\|";
    public static final String SYMBOL_B = "#";
    public static final String SYMBOL_C = ";";
    public static final String SYMBOL_D = "*";
    public static final String SYMBOL_DD = "\\*";
    public static final String SYMBOL_E = ":";
    public static final String SYMBOL_F = ",";
    public static final String SYMBOL_EQUAL = "=";
    public static final double FORCE_CAN_BATTLE_HP_PRO = 0.05;
    public static final int REPORT_TYPE_1 = 1;
    public static final int REPORT_TYPE_2 = 2;
    public static final int REPORT_TYPE_3 = 3;
    public static final int REPORT_TYPE_5 = 5;
    public static final int REPORT_TYPE_6 = 6;
    public static final int REPORT_TYPE_7 = 7;
    public static final int REPORT_TYPE_8 = 8;
    public static final int REPORT_TYPE_9 = 9;
    public static final int REPORT_TYPE_10 = 10;
    public static final int REPORT_TYPE_11 = 11;
    public static final int REPORT_TYPE_12 = 12;
    public static final int REPORT_TYPE_13 = 13;
    public static final int REPORT_TYPE_14 = 14;
    public static final int REPORT_TYPE_15 = 15;
    public static final int REPORT_TYPE_16 = 16;
    public static final int REPORT_TYPE_17 = 17;
    public static final int REPORT_TYPE_18 = 18;
    public static final int REPORT_TYPE_19 = 19;
    public static final int REPORT_TYPE_20 = 20;
    public static final int REPORT_TYPE_21 = 21;
    public static final int REPORT_TYPE_22 = 22;
    public static final int REPORT_TYPE_23 = 23;
    public static final int REPORT_TYPE_24 = 24;
    public static final int REPORT_TYPE_25 = 25;
    public static final int REPORT_TYPE_26 = 26;
    public static final int REPORT_TYPE_27 = 27;
    public static final int REPORT_TYPE_28 = 28;
    public static final int REPORT_TYPE_29 = 29;
    public static final int REPORT_TYPE_30 = 30;
    public static final int REPORT_TYPE_31 = 31;
    public static final String REPORT_ATT = "att";
    public static final String REPORT_DEF = "def";
    public static final int PLAYER_ID_ATT = -1000;
    public static final int PLAYER_ID_DEF = -2;
    public static final int BATTLE_ATT = 1;
    public static final int BATTLE_DEF = 0;
    public static final int BATTLE_MINE_RANK_NUM = 10;
    public static final long SEND_CHAT_HELP_INFO = 60000L;
    public static final int NATIONAL_RAN_BAT_NUM_LIMIT = 12;
    public static final int NATIONAL_RAN_BAT_NUM_GIFT = 10;
    public static final int NATIONAL_RAN_BAT_NUM_HOUR = 4;
    public static final long NATIONAL_RAN_BAT_ONE_NUM_TIME = 14400000L;
    public static final int NATIONAL_RAN_BAT_JIFEN_LIMIT = 1000;
    public static final String NATIONAL_RAN_JIFEN_REWARD_CCACHE_PARAM = "Qualifying.Credit.Reward";
    public static final String YELLOW_TACTIC = "\u9ec4\u6218\u6cd5";
    public static final String RED_TACTIC = "\u7ea2\u6218\u6cd5";
    public static final String BLUE_TACTIC = "\u7d2b\u6218\u6cd5";
    public static final int YELLOW_TACTIC_TIME = 5000;
    public static final int RED_TACTIC_TIME = 5000;
    public static final int BLUE_TACTIC_TIME = 5000;
    public static final int STRATEGY_ID_1 = 1;
    public static final int STRATEGY_ID_2 = 2;
    public static final int STRATEGY_ID_3 = 3;
    public static final int STRATEGY_DAMAGE_LOWER_LIMIT = 10;
    public static final int TACTIC_STRATEGY_ID_100 = 100;
    public static final int YOUDI_CHUJI_COLUMN = 10;
    public static final int BATTLE_TYPE_YOUDIN = 1;
    public static final int BATTLE_TYPE_CHUJI = 2;
    public static final int STRATEGY_RESULT_1 = 1;
    public static final int STRATEGY_RESULT_2 = 2;
    public static final int STRATEGY_RESULT_3 = 3;
    public static final int BATTLE_RESULT_1 = 1;
    public static final int BATTLE_RESULT_2 = 2;
    public static final int BATTLE_RESULT_3 = 3;
    public static Set<Integer> batMap;
    public static int CAN_BATTLESTART_HOUR;
    public static int CAN_BATTLE_END_HOUR;
    public static final float BATTLE_STAT_1 = 0.1f;
    public static final float BATTLE_STAT_2 = 0.2f;
    public static final float BATTLE_STAT_3 = 0.5f;
    public static final float BATTLE_STAT_4 = 1.0f;
    public static Map<Integer, Float> battle_stat;
    public static Set<Integer> officerBatMap;
    public static final int OFFICER_ID_RIGHT = 19;
    public static final int OFFICER_ID_LEFT = 18;
    public static final String WEI_DEFENCE_PLAYER_NAME = "\u9b4f\u56fd\u5b88\u536b";
    public static final String SHU_DEFENCE_PLAYER_NAME = "\u8700\u56fd\u5b88\u536b";
    public static final String WU_DEFENCE_PLAYER_NAME = "\u5434\u56fd\u5b88\u536b";
    public static final String CITY_DEFENCE_NPC_PIC = "zumao";
    public static final int BUFF_TYPE_GUWU = 0;
    public static final int BUFF_TYPE_1 = 1;
    public static final int BUFF_TYPE_2 = 2;
    public static final int BUFF_TYPE_3 = 3;
    public static final int BUFF_TYPE_DONGYAO = 3;
    public static final int BUFF_TYPE_4 = 4;
    public static final int BUFF_TYPE_5 = 5;
    public static final int BUFF_TYPE_6 = 6;
    public static final int BUFF_TYPE_QIANXIAN = 6;
    public static final int BUFF_TYPE_7 = 7;
    public static final int BUFF_TYPE_8 = 8;
    public static final int BUFF_TYPE_9 = 9;
    public static final int BUFF_TYPE_10 = 10;
    public static final int BUFF_TYPE_11 = 11;
    public static final int BUFF_TYPE_20 = 20;
    public static final int BUFF_TYPE_21 = 21;
    public static final int BUFF_TYPE_22 = 22;
    public static final int BUFF_TYPE_TERRAIN_START = 23;
    public static final int BUFF_TYPE_23 = 23;
    public static final int BUFF_TYPE_24 = 24;
    public static final int BUFF_TYPE_25 = 25;
    public static final int BUFF_TYPE_26 = 26;
    public static final int BUFF_TYPE_27 = 27;
    public static final int BUFF_TYPE_28 = 28;
    public static final int BUFF_TYPE_29 = 29;
    public static final int BUFF_TYPE_30 = 30;
    public static final int BUFF_TYPE_31 = 31;
    public static final int BUFF_TYPE_32 = 32;
    public static final int BUFF_TYPE_33 = 33;
    public static final int BUFF_TYPE_34 = 34;
    public static final int BUFF_TYPE_35 = 35;
    public static final int BUFF_TYPE_36 = 36;
    public static final int BUFF_TYPE_37 = 37;
    public static final int BUFF_TYPE_38 = 38;
    public static final int BUFF_TYPE_39 = 39;
    public static final int BUFF_TYPE_40 = 40;
    public static final int BUFF_TYPE_41 = 40;
    public static final int BUFF_TYPE_42 = 42;
    public static final int BUFF_TYPE_43 = 42;
    public static final int BUFF_TYPE_44 = 44;
    public static final int BUFF_TYPE_45 = 45;
    public static final int BUFF_TYPE_46 = 46;
    public static final int BUFF_TYPE_47 = 47;
    public static final int BUFF_TYPE_48 = 48;
    public static final String PHANTOM_GENERAL_PIC = "0";
    public static final int MAX_TACTIC_EFFECT_RANGE = 6;
    public static final String SPECIAL_TATIC_BEI_SHUI = "bs";
    public static final String SPECIAL_TATIC_ROB_FOOD_EFFECTS = "rob_food";
    public static final String SPECIAL_TATIC_CONFUSION_EFFECTS = "confusion";
    public static final int SPECIAL_TATIC_CONFUNSION = 1;
    public static final int SPECIAL_TATIC_ROB_FOOD = 2;
    public static final String CORE_SHIPO_SIGN = "SP";
    public static final int GENERAL_TYPE_INT_1 = 1;
    public static final int GENERAL_TYPE_INT_2 = 2;
    public static final String GENERAL_TYPE_STR_2 = "fs";
    public static final int GENERAL_TYPE_INT_3 = 3;
    public static final String GENERAL_TYPE_STR_3 = "mz";
    public static final int GENERAL_TYPE_INT_4 = 4;
    public static final String GENERAL_TYPE_STR_4 = "hy";
    public static final int GENERAL_TYPE_INT_5 = 5;
    public static final String GENERAL_TYPE_STR_5 = "ft";
    public static final int ATT_DEF_LIST_NUM = 3;
    public static final int GENERAL_TYPE_INT_6 = 6;
    public static final String GENERAL_TYPE_STR_6 = "bs";
    public static final int GENERAL_TYPE_INT_7 = 7;
    public static final String GENERAL_TYPE_STR_7 = "zf";
    public static final int GENERAL_TYPE_INT_8 = 8;
    public static final String GENERAL_TYPE_STR_8 = "rb";
    public static final int GENERAL_TYPE_INT_9 = 9;
    public static final String GENERAL_TYPE_STR_9 = "yx";
    public static final int GENERAL_TYPE_INT_10 = 10;
    public static final String GENERAL_TYPE_STR_10 = "td";
    public static final int CAN_CHOOSE_TATIC_CONFUNSION = 0;
    public static final int NO_CHOOSE_TATIC_CONFUNSION = 1;
    public static final int NO_CHOOSE_TATIC_RB_GENERAL = 2;
    public static final int NO_CHOOSE_TATIC_SURROUNDED = 3;
    public static final int NOT_JOIN_TEAM = 0;
    public static final int IN_TEAM_NOT_INSPIRE = 1;
    public static final int IN_TEAM_AND_INSPIRE = 2;
    public static final int GUANYU_WUSHENFUTI = 3;
    public static final long MIN_MILLISECONDS = 60000L;
    public static final String SPECIAL_TATIC_SIEGE_GUN = "siege_gun";
    public static final int TEAM_INSPIRE_ADD_EXP = 500;
    public static final int TECH_KEY_1_JIDIAN = 1;
    public static final int TECH_KEY_2_GANJI = 2;
    public static final int TECH_KEY_3_HEXIE = 3;
    public static final int TECH_KEY_4_DIANBING = 4;
    public static final int TECH_KEY_5_QINZHENG = 5;
    public static final int TECH_KEY_6_TUNTIAN = 6;
    public static final int TECH_KEY_7_ZHUJIAN = 7;
    public static final int TECH_KEY_8_ZHAOMU = 8;
    public static final int TECH_KEY_9_DUANJIA = 9;
    public static final int TECH_KEY_10_YINGYONG = 10;
    public static final int TECH_KEY_11_KUSI = 11;
    public static final int TECH_KEY_12_LINGDAN = 12;
    public static final int TECH_KEY_13_JIANREN = 13;
    public static final int TECH_KEY_14_TANYOU = 14;
    public static final int TECH_KEY_15_JIAOHUO = 15;
    public static final int TECH_KEY_16_LILIAN = 16;
    public static final int TECH_KEY_17_JIAOFU = 17;
    public static final int TECH_KEY_18_KAOSHANG = 18;
    public static final int TECH_KEY_19_SHENGYAN = 19;
    public static final int TECH_KEY_20_CANGKU = 20;
    public static final int TECH_KEY_21_RILIWANJI = 21;
    public static final int TECH_KEY_22_GAOGUAN = 22;
    public static final int TECH_KEY_23_PIJING = 23;
    public static final int TECH_KEY_24_QIANGGONG = 24;
    public static final int TECH_KEY_25_QIANGFANG = 25;
    public static final int TECH_KEY_26_QIANGBING = 26;
    public static final int TECH_KEY_27_WUJIANG = 27;
    public static final int TECH_KEY_28_BINGZHONG = 28;
    public static final int TECH_KEY_29_JIANYAN = 29;
    public static final int TECH_KEY_30_QIANGZHUANG = 30;
    public static final int TECH_KEY_31_XIANGZHU = 31;
    public static final int TECH_KEY_32_WENGUAN = 32;
    public static final int TECH_KEY_33_SHADI = 33;
    public static final int TECH_KEY_34_TIAOZHAN = 34;
    public static final int TECH_KEY_35_GUANYUN = 35;
    public static final int TECH_KEY_36_BTJISHI = 36;
    public static final int TECH_KEY_37_HENGTONG = 37;
    public static final int TECH_KEY_38_XJWX = 38;
    public static final int TECH_KEY_39_TJQH = 39;
    public static final int TECH_KEY_40_GZJY = 40;
    public static final int TECH_KEY_41_JITUANJUN = 41;
    public static final int TECH_KEY_42_ZHENGDIAN = 42;
    public static final int TECH_KEY_43_ZDZS = 43;
    public static final double KFGZ_PHANTOM_HP_EXP_COPPER_COE = 0.3;
    public static final int JS_SKILL_MS = 1;
    public static final int JS_SKILL_BJ = 2;
    public static final int JS_SKILL_ZF_BJ = 3;
    public static final int JS_SKILL_ZF_JB = 4;
    public static final int JS_SKILL_DT = 5;
    public static final int JS_SKILL_DEF = 6;
    public static final int JS_SKILL_ATT = 7;
    public static final int GENERAL_TYPE_INT_11 = 11;
    public static final String GENERAL_TYPE_STR_11 = "dx";
    public static final int BATTLE_GENERAL_LIST_NUM = 8;
    
    static {
        (BattleConstant.batMap = new HashSet<Integer>()).add(1);
        BattleConstant.batMap.add(2);
        BattleConstant.batMap.add(5);
        BattleConstant.CAN_BATTLESTART_HOUR = 10;
        BattleConstant.CAN_BATTLE_END_HOUR = 24;
        (BattleConstant.battle_stat = new HashMap<Integer, Float>()).put(1, 0.1f);
        BattleConstant.battle_stat.put(2, 0.2f);
        BattleConstant.battle_stat.put(3, 0.5f);
        BattleConstant.battle_stat.put(4, 1.0f);
        (BattleConstant.officerBatMap = new HashSet<Integer>()).add(19);
        BattleConstant.officerBatMap.add(18);
    }
    
    public static int strategyWinSide(final int attSt, final int defSt) {
        if (attSt == 1) {
            if (defSt == 2) {
                return 1;
            }
            if (defSt == 3) {
                return 2;
            }
            return 3;
        }
        else if (attSt == 2) {
            if (defSt == 3) {
                return 1;
            }
            if (defSt == 1) {
                return 2;
            }
            return 3;
        }
        else {
            if (attSt != 3) {
                return 2;
            }
            if (defSt == 1) {
                return 1;
            }
            if (defSt == 2) {
                return 2;
            }
            return 3;
        }
    }
    
    public static boolean isInTime(final int startHour, final int endHour) {
        final Calendar startTime = Calendar.getInstance();
        startTime.set(11, 0);
        startTime.set(12, 0);
        startTime.set(13, 0);
        startTime.set(14, 0);
        startTime.add(11, startHour);
        final Calendar endTime = Calendar.getInstance();
        endTime.set(11, 0);
        endTime.set(12, 0);
        endTime.set(13, 0);
        endTime.set(14, 0);
        endTime.add(11, endHour);
        final Date now = new Date();
        return now.after(startTime.getTime()) && now.before(endTime.getTime());
    }
    
    public static long getCountdownTime(final int startHour, final int endHour) {
        final Calendar startTime = Calendar.getInstance();
        startTime.set(11, 0);
        startTime.set(12, 0);
        startTime.set(13, 0);
        startTime.set(14, 0);
        startTime.add(11, startHour);
        final Calendar endTime = Calendar.getInstance();
        endTime.set(11, 0);
        endTime.set(12, 0);
        endTime.set(13, 0);
        endTime.set(14, 0);
        endTime.add(11, endHour);
        final Date now = new Date();
        if (now.after(startTime.getTime()) && now.before(endTime.getTime())) {
            return 0L;
        }
        if (now.before(startTime.getTime())) {
            return startTime.getTimeInMillis() - now.getTime();
        }
        return startTime.getTimeInMillis() + 86400000L - now.getTime();
    }
}
