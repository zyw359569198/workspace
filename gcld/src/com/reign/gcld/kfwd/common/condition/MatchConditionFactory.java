package com.reign.gcld.kfwd.common.condition;

import java.util.*;

public class MatchConditionFactory
{
    private static final String PLAYER_LV_SCOPE = "level";
    private static final String SERVER_YEAR = "time";
    private static final String SERVER_YEAR_LESS = "timeLess";
    private static final String SERVER_YX = "yx";
    private static final String SERVER_TIME_LESS = "serverTimeLess";
    private static final String SERVER_TIME_GREATER = "serverTimeGreater";
    
    public static IMatchCondition getMatchCondition(final String arg) {
        final String[] s = arg.split(",");
        if (s.length <= 0) {
            return null;
        }
        final String name = s[0];
        if (name.equals("level")) {
            return new MatchConditionPlayerLvScope(s);
        }
        if (name.equals("time")) {
            return new MatchConditionServerYear(s);
        }
        if (name.equals("yx")) {
            return new MatchConditionYx(s);
        }
        if (name.equals("timeLess")) {
            return new MatchConditionServerYearLess(s);
        }
        if (name.equals("serverTimeLess")) {
            return new MatchConditionServerTimeLess(s);
        }
        if (name.equals("serverTimeGreater")) {
            return new MatchConditionServerTimeGreater(s);
        }
        return null;
    }
    
    public static List<IMatchCondition> getMatchConditionList(final String arg) {
        final List<IMatchCondition> rtn = new ArrayList<IMatchCondition>();
        String[] split;
        for (int length = (split = arg.split(";")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            final IMatchCondition condition = getMatchCondition(s);
            if (condition != null) {
                rtn.add(condition);
            }
        }
        return rtn;
    }
}
