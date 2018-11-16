package com.reign.gcld.juben.common;

import java.util.*;
import com.reign.gcld.common.*;

public class JubenConstans
{
    public static final int forceId_101 = 101;
    public static final int forceId_102 = 102;
    public static final int forceId_103 = 103;
    public static final int forceId_104 = 104;
    public static final int forceId_107 = 107;
    public static final int forceId_108 = 108;
    public static final int forceId_0 = 0;
    public static final int forceId_1 = 1;
    public static final int forceId_capital_id101 = 124;
    public static final int forceId_capital_id102 = 127;
    public static final int forceId_capital_id103 = 133;
    public static final int forceId_capital_id104 = 138;
    public static final int forceId_capital_id0 = 139;
    public static final int forceId_capital_id1 = 140;
    public static final int DEFAULT_PHANTOM_NUMBER = 10;
    public static final int WORLD_DRAMA_DELIMETER_NUMBER = 10000;
    public static Map<Integer, Integer> map;
    public static Map<Integer, String> forceId2NameMap;
    
    static {
        (JubenConstans.map = new HashMap<Integer, Integer>()).put(0, 139);
        JubenConstans.map.put(1, 140);
        JubenConstans.map.put(101, 124);
        JubenConstans.map.put(102, 127);
        JubenConstans.map.put(103, 133);
        JubenConstans.map.put(104, 138);
        (JubenConstans.forceId2NameMap = new HashMap<Integer, String>()).put(101, LocalMessages.JUBEN_FORCE_YUANSHU);
        JubenConstans.forceId2NameMap.put(102, LocalMessages.JUBEN_FORCE_LIUBIAO);
        JubenConstans.forceId2NameMap.put(103, LocalMessages.JUBEN_FORCE_DONGZHUO);
        JubenConstans.forceId2NameMap.put(104, LocalMessages.JUBEN_FORCE_CAOCAO);
        JubenConstans.forceId2NameMap.put(0, LocalMessages.JUBEN_FORCE_SUNJIAN);
    }
}
