package com.reign.gcld.player.common;

public class AddExpInfo
{
    public static int STATE_SUCC;
    public static int STATE_ADD_PART;
    public static int STATE_NO_ADD;
    public static int STATE_TOP_LEVEL;
    public boolean upLv;
    public int addExp;
    public int state;
    
    static {
        AddExpInfo.STATE_SUCC = 1;
        AddExpInfo.STATE_ADD_PART = 2;
        AddExpInfo.STATE_NO_ADD = 3;
        AddExpInfo.STATE_TOP_LEVEL = 4;
    }
    
    public AddExpInfo() {
        this.upLv = false;
        this.state = 1;
    }
}
