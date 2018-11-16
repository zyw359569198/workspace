package com.reign.gcld.battle.reward;

import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;
import java.util.*;

public class RewardType
{
    public static final String SPECIAL_TATIC_ROB_FOOD = "rob_food";
    public static final String SPECIAL_TATIC_CONFUSION = "confusion";
    public static final String SPECIAL_TATIC_SIEGE_GUN = "siege_gun";
    public static final String SPECIAL_TATIC_BEI_SHUI = "bs";
    public static final int COPPER_TYPE = 1;
    public static final String COPPER_TYPE_STRING = "copper";
    public static final int LUMBER_TYPE = 2;
    public static final String LUMBER_TYPE_STRING = "lumber";
    public static final int FOOD_TYPE = 3;
    public static final String FOOD_TYPE_STRING = "food";
    public static final int IRON_TYPE = 4;
    public static final String IRON_TYPE_STRING = "iron";
    public static final int ITEMS_TYPE = 5;
    public static final String ITEMS_TYPE_STRING = "item";
    public static final int GENERAL_TREASURE = 6;
    public static final String GENERAL_TREASURE_STRING = "general_treasure";
    public static final int BUILDING_DRAWING = 7;
    public static final String BUILDING_DRAWING_STRING = "building_drawing";
    public static final int NATION_EXP = 8;
    public static final String NATION_EXP_STRING = "nation_exp";
    public static final int TOKEN = 9;
    public static final String TOKEN_STRING = "recruit_token";
    public static final int CHIEF_EXP = 10;
    public static final String CHIEF_EXP_STRING = "chief_exp";
    public static final int GOLD = 11;
    public static final String GOLD_STRING = "gold";
    public static final int QUENCHING = 12;
    public static final String QUENCHING_STRING = "xilian";
    public static final int FEAT = 13;
    public static final String FEAT_STRING = "gongxun";
    private static final int DELIMETER = 1900;
    public static final int KILL_RANK_BOX_REWARD = 1;
    public static final int OFFICER_TOKEN_REWARD = 2;
    public static final int NATION_TASK_REWARD = 3;
    public static final int BAR_TASK_REWARD = 4;
    public static final int FARM_REWARD = 8;
    public static final int TRAINNING_REWARD = 9;
    public static final int HARD_TRAINNING_REWARD = 10;
    public static final int GOLD_TRAINNING_REWARD = 11;
    public static final int NATION_INDIV_TASK_REWARD = 12;
    
    public static int getTypeInt(final String string) {
        if (string.equalsIgnoreCase("copper")) {
            return 1;
        }
        if (string.equalsIgnoreCase("lumber")) {
            return 2;
        }
        if (string.equalsIgnoreCase("food")) {
            return 3;
        }
        if (string.equalsIgnoreCase("iron")) {
            return 4;
        }
        if (string.equalsIgnoreCase("item")) {
            return 5;
        }
        if (string.equalsIgnoreCase("general_treasure")) {
            return 6;
        }
        if (string.equalsIgnoreCase("building_drawing")) {
            return 7;
        }
        if (string.equalsIgnoreCase("nation_exp")) {
            return 8;
        }
        if (string.equalsIgnoreCase("recruit_token")) {
            return 9;
        }
        if (string.equalsIgnoreCase("chief_exp")) {
            return 10;
        }
        if (string.equalsIgnoreCase("gold")) {
            return 11;
        }
        if (string.equalsIgnoreCase("xilian")) {
            return 12;
        }
        if (string.equalsIgnoreCase("gongxun")) {
            return 13;
        }
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public static String getType(final int type) {
        if (type == 1) {
            return "copper";
        }
        if (type == 2) {
            return "lumber";
        }
        if (type == 3) {
            return "food";
        }
        if (type == 4) {
            return "iron";
        }
        if (type == 5) {
            return "item";
        }
        if (type == 6) {
            return "general_treasure";
        }
        if (type == 7) {
            return "building_drawing";
        }
        if (type == 8) {
            return "nation_exp";
        }
        if (type == 9) {
            return "recruit_token";
        }
        if (type == 10) {
            return "chief_exp";
        }
        if (type == 11) {
            return "gold";
        }
        if (type == 12) {
            return "xilian";
        }
        if (type == 13) {
            return "gongxun";
        }
        return "wrong";
    }
    
    public static String getTypeWord(final int rewardType) {
        if (rewardType == 1) {
            return LocalMessages.T_COMM_10004;
        }
        if (rewardType == 2) {
            return LocalMessages.T_COMM_10005;
        }
        if (rewardType == 3) {
            return LocalMessages.T_COMM_10017;
        }
        if (rewardType == 4) {
            return LocalMessages.T_COMM_10018;
        }
        if (rewardType == 10) {
            return LocalMessages.T_COMM_10006;
        }
        if (rewardType == 9) {
            return LocalMessages.T_COMM_10022;
        }
        if (rewardType == 11) {
            return LocalMessages.T_COMM_10009;
        }
        if (rewardType == 12) {
            return LocalMessages.T_COMM_10052;
        }
        if (rewardType == 13) {
            return LocalMessages.T_COMM_10053;
        }
        return "";
    }
    
    public static void reward(final IDataGetter dataGetter, final int type, final int value, final int playerId, final int reason) {
        String reasonString = "";
        switch (reason) {
            case 1: {
                reasonString = "\u6740\u654c\u699c\u5b9d\u7bb1\u5956\u52b1";
                break;
            }
            case 2: {
                reasonString = "\u5b98\u5458\u4ee4\u5956\u52b1";
                break;
            }
            case 3: {
                reasonString = "\u56fd\u5bb6\u4efb\u52a1\u5956\u52b1";
                break;
            }
            case 4: {
                reasonString = "\u86ee\u65cf\u4efb\u52a1\u5956\u52b1";
                break;
            }
            case 5: {
                reasonString = "\u5355\u6311\u699c\u5956\u52b1";
                break;
            }
            case 6: {
                reasonString = "\u653b\u57ce\u699c\u5956\u52b1";
                break;
            }
            case 7: {
                reasonString = "\u65b0\u624b\u4efb\u52a1\u5956\u52b1";
                break;
            }
            case 8: {
                reasonString = "\u5c6f\u7530\u5956\u52b1";
                break;
            }
            case 9: {
                reasonString = "\u7ec3\u5175\u5956\u52b1";
                break;
            }
            case 10: {
                reasonString = "\u82e6\u7ec3\u5956\u52b1";
                break;
            }
            case 11: {
                reasonString = "\u91d1\u5e01\u82e6\u7ec3\u5956\u52b1";
                break;
            }
            case 12: {
                reasonString = "\u56fd\u5bb6\u4e2a\u4eba\u4efb\u52a1\u5956\u52b1";
                break;
            }
        }
        reasonString = String.valueOf(reasonString) + getTypeWord(type);
        if (type == 1) {
            dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerId, value, reasonString, true);
        }
        else if (type == 2) {
            dataGetter.getPlayerResourceDao().addWoodIgnoreMax(playerId, value, reasonString, true);
        }
        else if (type == 3) {
            dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, value, reasonString);
        }
        else if (type == 4) {
            dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, value, reasonString, true);
        }
        else if (type == 10) {
            dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, value, reasonString);
        }
        else if (type == 9) {
            dataGetter.getPlayerAttributeDao().addRecruitToken(playerId, value, reasonString);
        }
        else if (type == 11) {
            dataGetter.getPlayerDao().addSysGold(dataGetter.getPlayerDao().read(playerId), value, reasonString);
        }
        else if (type > 1900) {
            dataGetter.getDiamondShopService().dropProps(playerId, type, value);
        }
        else if (type == 12) {
            dataGetter.getPlayerQuenchingRelativeDao().addFreeQuenchingTimes(playerId, value);
        }
        else if (type == 13) {
            dataGetter.getRankService().addFeat(playerId, value);
        }
    }
    
    public static void rewardDoc(final String reward, final JsonDocument doc) {
        String[] rewards = reward.split(";");
        doc.startArray("rewards");
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                doc.startObject();
                doc.createElement("type", getTypeInt(single[0]));
                doc.createElement("value", single[1]);
                doc.endObject();
            }
        }
        rewards = null;
        doc.endArray();
    }
    
    public static void reward(final String reward, final IDataGetter dataGetter, final int playerId, final int reason) {
        String[] rewards = reward.split(";");
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                final int type = getTypeInt(single[0]);
                final int value = Integer.parseInt(single[1]);
                if (value > 0) {
                    reward(dataGetter, type, value, playerId, reason);
                }
            }
        }
        rewards = null;
    }
    
    public static String getRewardsString(final String reward) {
        final String[] rewards = reward.split(";");
        final StringBuffer buffer = new StringBuffer();
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                final int type = getTypeInt(single[0]);
                final int value = Integer.parseInt(single[1]);
                if (type > 0 && value > 0) {
                    if (type <= 1900) {
                        buffer.append(getTypeWord(type)).append(" ").append(value).append(" ");
                    }
                }
            }
        }
        return buffer.toString();
    }
    
    public static byte[] rewards(final String reward, final String arrayName) {
        final JsonDocument doc = new JsonDocument();
        String[] rewards = reward.split(";");
        doc.startArray(arrayName);
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                doc.startObject();
                doc.createElement("type", getTypeInt(single[0]));
                doc.createElement("value", single[1]);
                doc.endObject();
            }
        }
        rewards = null;
        doc.endArray();
        return doc.toByte();
    }
    
    public static String mergeRewards(final String... rewardsStrings) {
        final Map<String, Integer> rewardMap = new HashMap<String, Integer>();
        for (int i = 0; i < rewardsStrings.length; ++i) {
            final String s = rewardsStrings[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(";");
                String[] array;
                for (int length = (array = single).length, j = 0; j < length; ++j) {
                    final String string = array[j];
                    if (!StringUtils.isBlank(string)) {
                        final String[] ele = string.split(",");
                        final String type = ele[0];
                        int value = Integer.parseInt(ele[1]);
                        if (rewardMap.containsKey(type)) {
                            value += rewardMap.get(type);
                            rewardMap.put(type, value);
                        }
                        else {
                            rewardMap.put(type, value);
                        }
                    }
                }
            }
        }
        final StringBuffer buffer = new StringBuffer();
        for (final String key : rewardMap.keySet()) {
            buffer.append(key).append(",").append(rewardMap.get(key)).append(";");
        }
        SymbolUtil.removeTheLast(buffer);
        return buffer.toString();
    }
    
    public static String mergeRewards2(final String... rewardsStrings) {
        final Map<String, Integer> rewardMap = new HashMap<String, Integer>();
        for (int i = 0; i < rewardsStrings.length; ++i) {
            final String s = rewardsStrings[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(";");
                String[] array;
                for (int length = (array = single).length, j = 0; j < length; ++j) {
                    final String string = array[j];
                    if (!StringUtils.isBlank(string)) {
                        final String[] ele = string.split(",");
                        final String type = ele[0];
                        int value = Integer.parseInt(ele[1]);
                        if (rewardMap.containsKey(type)) {
                            value += rewardMap.get(type);
                            rewardMap.put(type, value);
                        }
                        else {
                            rewardMap.put(type, value);
                        }
                    }
                }
            }
        }
        final StringBuffer buffer = new StringBuffer();
        for (final String key : rewardMap.keySet()) {
            buffer.append(key).append(",").append(rewardMap.get(key)).append(";");
        }
        SymbolUtil.removeTheLast(buffer);
        return buffer.toString();
    }
    
    public static String rewardByTimees(final String reward, final float extraRewards) {
        if (StringUtils.isBlank(reward)) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer();
        final String[] rewards = reward.split(";");
        String[] array;
        for (int length = (array = rewards).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                final String type = single[0];
                int value = Integer.parseInt(single[1]);
                final int rewarType = getTypeInt(type);
                if (rewarType <= 1900) {
                    value *= (int)extraRewards;
                    buffer.append(type).append(",").append(value).append(";");
                }
            }
        }
        SymbolUtil.removeTheLast(buffer);
        return buffer.toString();
    }
}
