package com.reign.kfgz.ai.constants;

public class AIConstants
{
    public static final String SPLIT = ":";
    public static final String MOVETO = "moveto";
    public static final String MOVEROAD = "moveroad";
    public static final String DEFEND = "defend";
    public static final String ATTACK = "attack";
    public static final int START = 0;
    public static final int MOVE_DONE = 1;
    public static final int BATTLE_DONE = 2;
    public static final int MOVE_WRONG_TIME = 3;
    public static final int TYPE_MOVE = 1;
    
    public static String getMoveToCityString(final int cityId) {
        return "moveto:" + cityId;
    }
    
    public static String getMoveRoadCityString(final String roadList) {
        return "moveroad:" + roadList;
    }
}
