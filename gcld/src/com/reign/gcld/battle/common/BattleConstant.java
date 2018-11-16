package com.reign.gcld.battle.common;

import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;

public class BattleConstant
{
    public static final int COLUMN = 6;
    public static final int ROW = 3;
    public static final int FORCE_BATTLE_ROW = 10;
    public static final int BATTLE_GENERAL_LIST_NUM = 8;
    public static final int TACTIC_MAX_TIMES = 0;
    public static final int TIME_FIRST = 12000;
    public static final int TIME_FIRST_AUTO_STRATEGY = 6000;
    public static final int TIME_AUTO_STRATEGY_DEVIATION = 2000;
    public static final int TIME_MOVE = 1400;
    public static final int TIME_ST_BASE = 700;
    public static final int TIME_ST_LOST = 1500;
    public static final int TIME_RB_TACTIC = 25000;
    public static final int TIME_TACTIC_ADD = 3000;
    public static final int TIME_BASE_LOST = 600;
    public static final int TIME_PER_LOST = 800;
    public static final int TIME_CHEERS = 0;
    public static final int TIME_ST_CHOOSE = 6000;
    public static final int TIME_LOWER_LIMIT = 3600;
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
    public static final int BATTLE_BARBARAIN = 14;
    public static final int BATTLE_BARBARAIN_ONE2ONE = 15;
    public static final int BATTLE_DUEL = 16;
    public static final int BATTLE_CITY_EVENT = 17;
    public static final int BATTLE_SCENARIO = 18;
    public static final int BATTLE_SCENARIO_ONE2ONE = 19;
    public static final int BATTLE_SCENARIO_EVENT = 20;
    public static final int BATTLE_ARMIES_TIME = 333;
    public static final int BATTLE_SCENARIO_ONE2ONE_EXPEND_SCALE = 1000;
    public static Set<Integer> city_battle;
    public static final int BATTLE_TITLE_PIC = 100000;
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
    public static final int REPORT_TYPE_100 = 100;
    public static final int REPORT_TYPE_100_TYPE_1 = 1;
    public static final int REPORT_TYPE_100_TYPE_2 = 2;
    public static final int REPORT_TYPE_100_TYPE_3 = 3;
    public static final int REPORT_TYPE_100_TYPE_4 = 4;
    public static final int REPORT_TYPE_100_TYPE_5 = 5;
    public static final int REPORT_TYPE_100_TYPE_6 = 6;
    public static final String REPORT_ATT = "att";
    public static final String REPORT_DEF = "def";
    public static final int ATT_TYPE_PLAYER = 1;
    public static final int ATT_TYPE_NATION_NPC = 2;
    public static final int ATT_TYPE_PHANTOM = 3;
    public static final int ATT_TYPE_MANZU = 4;
    public static final int ATT_TYPE_EA = 5;
    public static final int ATT_TYPE_MANZU_EA = 6;
    public static final int ATT_TYPE_JUBEN_NPC = 7;
    public static final int ATT_TYPE_SYS_FIRE = Integer.MAX_VALUE;
    public static final int PLAYER_ID_ATT = -1000;
    public static final int PLAYER_ID_DEF = -1;
    public static final int PLAYER_ID_ATT_CITY_NPC = -2;
    public static final int PLAYER_ID_ATT_PHANTOM = -3;
    public static final int PLAYER_ID_ATT_MANZU_NPC = -4;
    public static final int PLAYER_ID_ATT_YUANZHENJUN_NPC = -5;
    public static final int PLAYER_ID_ATT_MANZU_YUANZHENJUN_NPC = -6;
    public static final int PLAYER_ID_ATT_NATION_TASK_YUANZHENJUN_NPC = -7;
    public static final int PLAYER_ID_ATT_SAO_DANG_MANZU_NPC = -8;
    public static final int PLAYER_ID_DEF_YELLOW_TURBANS_NPC = -9;
    public static final int PLAYER_ID_HUIZHAN_PK_REWARD_NPC = -10;
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
    public static final int YELLOW_TACTIC_TIME = 5000;
    public static final int RED_TACTIC_TIME = 5000;
    public static final int BLUE_TACTIC_TIME = 5000;
    public static final String CORE_SHIPO_SIGN = "SP";
    public static final int STRATEGY_ID_1 = 1;
    public static final int STRATEGY_ID_2 = 2;
    public static final int STRATEGY_ID_3 = 3;
    public static final int STRATEGY_DAMAGE_LOWER_LIMIT = 10;
    public static final int TACTIC_STRATEGY_ID_100 = 100;
    public static final int YOUDI_CHUJI_COLUMN = 1;
    public static final int YOUDI_CHUJI_COLUMN10 = 10;
    public static final int BATTLE_TYPE_YOUDIN = 1;
    public static final int BATTLE_TYPE_CHUJI = 2;
    public static final int BATTLE_TYPE_SYS_PK = 3;
    public static final int STRATEGY_RESULT_1 = 1;
    public static final int STRATEGY_RESULT_2 = 2;
    public static final int STRATEGY_RESULT_3 = 3;
    public static final int SPECIAL_TATIC_CONFUNSION = 1;
    public static final int SPECIAL_TATIC_ROB_FOOD = 2;
    public static final int CAN_CHOOSE_TATIC_CONFUNSION = 0;
    public static final int NO_CHOOSE_TATIC_CONFUNSION = 1;
    public static final int NO_CHOOSE_TATIC_RB_GENERAL = 3;
    public static final int NO_CHOOSE_TATIC_SURROUNDED = 2;
    public static Map<Integer, String> batNameMap;
    public static final int BATTLE_RESULT_1 = 1;
    public static final int BATTLE_RESULT_2 = 2;
    public static final int BATTLE_RESULT_3 = 3;
    public static Set<Integer> batMap;
    public static Map<Integer, String> resultMap;
    public static Map<Integer, String> officerNames;
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
    public static final String CITY_DEFENCE_NPC_PIC = "zumao";
    public static final String PLAYER_NAME_NPC = "NPC";
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
    public static final int BUFF_TYPE_14 = 14;
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
    public static final int BUFF_TYPE_49 = 49;
    public static final double ATTACK_ALLY_EXP_REDUCE = -0.2;
    public static final double HELP_ALLY_EXP_ADD = 0.05;
    public static final String PHANTOM_GENERAL_PIC = "0";
    public static final int PHANTOM_COUNT_EACH_DAY_VIP3_LV = 3;
    public static final int VIP3_PHANTOM_COUNT_EACH_DAY = 1;
    public static final int TUJIN_TIMES = 3;
    public static final int BARBARAIN_NUM = 100;
    public static final int BARBARAIN_CHUJI_THRESHOLD = 50;
    public static final int BARBARAIN_CHUJI_BEGIN_ID = 10;
    public static final int BARBARAIN_CHUJI_EACH_COUNT = 2;
    public static final int BARBARAIN_INVADE_TIME_HOURS = 2;
    public static final long BARBARAIN_INVADE_SLEEP_TIME_GAP = 30000L;
    public static final int BARBARAIN_INVADE_REMAIN_COUNT_LIMIT = 10;
    public static final int BARBARAIN_INVADE_FOOD_ARMY_NUM_AFTER_BESIEGED = 10;
    public static final long COUNTRY_EA_ADD_TIME_GAP = 1800000L;
    public static final int NATION_TASK_ADD_DEF_NUM = 10;
    public static final int WORLD_LEGION_MIN_NUM = 10;
    public static final int AUTO_STRATEGY_CANNOT_CHOOSE = -1;
    public static final int AUTO_STRATEGY_NOT_CHOOSE = 0;
    public static final int AUTO_STRATEGY_CHOOSED = 1;
    public static final int TEAMBATTLE_BATTLE_TYPE0 = 0;
    public static final int TEAMBATTLE_BATTLE_TYPE1 = 1;
    public static final long BATTLE_CHECK = 30000L;
    public static final long JUBEN_CHECK = 60000L;
    public static final long BATTLE_STOP_TIME = 180000L;
    public static final long BATTLE_STOP_TIME_MIN = 60000L;
    public static final int GENERAL_TYPE_INT_1 = 1;
    public static final int GENERAL_TYPE_INT_2 = 2;
    public static final String GENERAL_TYPE_STR_2 = "fs";
    public static final int GENERAL_TYPE_INT_3 = 3;
    public static final String GENERAL_TYPE_STR_3 = "mz";
    public static final int GENERAL_TYPE_INT_4 = 4;
    public static final String GENERAL_TYPE_STR_4 = "hy";
    public static final int GENERAL_TYPE_INT_5 = 5;
    public static final String GENERAL_TYPE_STR_5 = "ft";
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
    public static final int GENERAL_TYPE_INT_11 = 11;
    public static final String GENERAL_TYPE_STR_11 = "dx";
    public static final long ATT_DEF_LIST_NUM = 50L;
    public static final int SCENARIO_ARMY_TYPE_PGM_PHANTOM = 1;
    public static final int SCENARIO_ARMY_TYPE_SYS_NPC = 2;
    public static final int START_FRESH_MAN_TASK_BONUS_ID = 201;
    public static final float ACTIVITY_BAT_ADD_EXP_1 = 0.1f;
    public static final float ACTIVITY_BAT_ADD_EXP_2 = 0.2f;
    public static final float ACTIVITY_BAT_ADD_EXP_3 = 0.3f;
    public static final double JUBEN_PHANTOM_HP_EXP_COPPER_COE = 0.28;
    public static final double BARBARAIN_PHANTOM_HP_EXP_COPPER_COE = 0.15;
    public static final int WATCH_BATTLE_TYPE_1_GUANZHI_CAN_ATTACK = 1;
    public static final int WATCH_BATTLE_TYPE_2_GUANZHI_ATT_SIDE = 2;
    public static final int WATCH_BATTLE_TYPE_3_GUANZHI_DEF_SIDE = 3;
    public static final int NATION_TASK_TICKET_ARMY_ADD_ROUND_NUM = 4;
    public static final int NATION_TASK_TICKET_ARMY_ADD_TIME_GAP_MINUTE = 30;
    public static final int NATION_TASK_TICKET_ARMY_ADD_NUM = 50;
    public static final int OFFICE_KILL_TOKEN_NUM_LIMIT = 1;
    public static final long OFFICE_KILL_TOKEN_DURATION_TIME = 1800000L;
    public static final int NOT_JOIN_TEAM = 0;
    public static final int IN_TEAM_NOT_INSPIRE = 1;
    public static final int IN_TEAM_AND_INSPIRE = 2;
    public static final int GUANYU_WUSHENFUTI = 3;
    public static final double NT_YELLOW_TURBANS_PLAYER_PHANTOM_HP_EXP_COPPER_COE = 0.45;
    public static final int[] NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_1;
    public static final int[] NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_2;
    public static final int CITY_HU_LAO_GUAN = 133;
    public static final int CITY_CHI_BI = 144;
    public static final int CITY_WU_XIA_GUAN = 102;
    public static final int NT_XIANG_YANG_TERRIAN_PIC = 11;
    public static final int NT_XIANG_YANG_PHANTOM_TIME_1 = 30;
    public static final int NT_XIANG_YANG_PHANTOM_TIME_2 = 60;
    public static final int NT_XIANG_YANG_PHANTOM_TIME_3 = 90;
    public static final int NORMAL_JUBEN_FIGHT_COEID = 21;
    public static final int JS_SKILL_MS = 1;
    public static final int JS_SKILL_BJ = 2;
    public static final int JS_SKILL_ZF_BJ = 3;
    public static final int JS_SKILL_ZF_JB = 4;
    public static final int JS_SKILL_DT = 5;
    public static final int JS_SKILL_DEF = 6;
    public static final int JS_SKILL_ATT = 7;
    
    static {
        (BattleConstant.city_battle = new HashSet<Integer>()).add(3);
        BattleConstant.city_battle.add(13);
        BattleConstant.city_battle.add(14);
        BattleConstant.city_battle.add(15);
        BattleConstant.city_battle.add(16);
        BattleConstant.city_battle.add(17);
        (BattleConstant.batNameMap = new HashMap<Integer, String>()).put(1, LocalMessages.BATTLE_ARMY_NAME);
        BattleConstant.batNameMap.put(2, LocalMessages.BATTLE_ARMY_NAME);
        BattleConstant.batNameMap.put(3, LocalMessages.BATTLE_CITY_NAME);
        BattleConstant.batNameMap.put(4, LocalMessages.BATTLE_OCCUPY_NAME);
        BattleConstant.batNameMap.put(5, LocalMessages.BATTLE_ARMY_NAME);
        BattleConstant.batNameMap.put(6, LocalMessages.BATTLE_MINE_NAME);
        BattleConstant.batNameMap.put(7, LocalMessages.BATTLE_MINE_NAME);
        (BattleConstant.batMap = new HashSet<Integer>()).add(1);
        BattleConstant.batMap.add(2);
        BattleConstant.batMap.add(5);
        (BattleConstant.resultMap = new HashMap<Integer, String>()).put(1, LocalMessages.BATTLE_RESULT_1);
        BattleConstant.resultMap.put(2, LocalMessages.BATTLE_RESULT_2);
        BattleConstant.resultMap.put(3, LocalMessages.BATTLE_RESULT_3);
        (BattleConstant.officerNames = new HashMap<Integer, String>()).put(1, LocalMessages.HALL_BUILDING_1);
        BattleConstant.officerNames.put(2, LocalMessages.HALL_BUILDING_2);
        BattleConstant.officerNames.put(3, LocalMessages.HALL_BUILDING_3);
        BattleConstant.officerNames.put(4, LocalMessages.HALL_BUILDING_4);
        BattleConstant.officerNames.put(5, LocalMessages.HALL_BUILDING_5);
        BattleConstant.officerNames.put(6, LocalMessages.HALL_BUILDING_6);
        BattleConstant.officerNames.put(7, LocalMessages.HALL_BUILDING_7);
        BattleConstant.officerNames.put(8, LocalMessages.HALL_BUILDING_8);
        BattleConstant.officerNames.put(9, LocalMessages.HALL_BUILDING_9);
        BattleConstant.officerNames.put(10, LocalMessages.HALL_BUILDING_10);
        BattleConstant.officerNames.put(11, LocalMessages.HALL_BUILDING_11);
        BattleConstant.officerNames.put(12, LocalMessages.HALL_BUILDING_12);
        BattleConstant.officerNames.put(13, LocalMessages.HALL_BUILDING_13);
        BattleConstant.officerNames.put(14, LocalMessages.HALL_BUILDING_14);
        BattleConstant.officerNames.put(15, LocalMessages.HALL_BUILDING_15);
        BattleConstant.officerNames.put(16, LocalMessages.HALL_BUILDING_16);
        BattleConstant.officerNames.put(17, LocalMessages.HALL_BUILDING_17);
        BattleConstant.officerNames.put(18, LocalMessages.HALL_BUILDING_18);
        BattleConstant.officerNames.put(19, LocalMessages.HALL_BUILDING_19);
        BattleConstant.CAN_BATTLESTART_HOUR = 10;
        BattleConstant.CAN_BATTLE_END_HOUR = 24;
        (BattleConstant.battle_stat = new HashMap<Integer, Float>()).put(1, 0.1f);
        BattleConstant.battle_stat.put(2, 0.2f);
        BattleConstant.battle_stat.put(3, 0.5f);
        BattleConstant.battle_stat.put(4, 1.0f);
        (BattleConstant.officerBatMap = new HashSet<Integer>()).add(19);
        BattleConstant.officerBatMap.add(18);
        NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_1 = new int[] { 56, 101, 104, 134, 137, 142, 97, 98, 106, 138, 140, 143, 103, 135, 139, 105, 102, 133, 144 };
        NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_2 = new int[] { 60, 132, 193 };
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
    
    public static boolean isInTime(final int startHour, final int endHour, final Date date) {
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
        return date.getTime() >= startTime.getTimeInMillis() && date.getTime() <= endTime.getTimeInMillis();
    }
    
    public static String getBuildingName(final Halls halls, final int forceId) {
        if (halls == null) {
            return "";
        }
        switch (forceId) {
            case 1: {
                return halls.getBuildingNameWei();
            }
            case 2: {
                return halls.getBuildingNameShu();
            }
            default: {
                return halls.getBuildingNameWu();
            }
        }
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
