package com.reign.gcld.kfzb.util;

import com.reign.util.*;
import com.reign.gcld.common.*;
import java.util.*;

public class KfZbConstants
{
    public static final int FLOWER_1_INIT_NUM = 8;
    public static final int FLOWER_1_BUY_GOLD = 1;
    public static final int FLOWER_1_BUY_NUM = 4;
    public static final int FLOWER_2_INIT_NUM = 6;
    public static final int FLOWER_2_BUY_GOLD = 2;
    public static final int FLOWER_2_BUY_NUM = 5;
    public static final Map<Integer, Tuple<Integer, Integer>> kickedOutFlowermap;
    public static final int FLOWER_SUPPORT_TYPE_1 = 1;
    public static final int FLOWER_SUPPORT_TYPE_2 = 2;
    public static final int FLOWER_1_SUPPORT_TYPE_1_TICKETS = 200;
    public static final int FLOWER_1_SUPPORT_TYPE_2_TICKETS = 100;
    public static final int FLOWER_2_SUPPORT_TYPE_1_TICKETS = 400;
    public static final int FLOWER_2_SUPPORT_TYPE_2_TICKETS = 200;
    public static final Map<Integer, String> layerToTitleMap;
    
    static {
        kickedOutFlowermap = new HashMap<Integer, Tuple<Integer, Integer>>();
        Tuple<Integer, Integer> tuple = new Tuple();
        tuple.left = 4;
        tuple.right = 6;
        KfZbConstants.kickedOutFlowermap.put(4, tuple);
        tuple = new Tuple();
        tuple.left = 0;
        tuple.right = 6;
        KfZbConstants.kickedOutFlowermap.put(3, tuple);
        tuple = new Tuple();
        tuple.left = 0;
        tuple.right = 5;
        KfZbConstants.kickedOutFlowermap.put(2, tuple);
        (layerToTitleMap = new HashMap<Integer, String>()).put(0, ColorUtil.getVioletMsg(LocalMessages.TITLE_KFZB_0));
        KfZbConstants.layerToTitleMap.put(1, ColorUtil.getVioletMsg(LocalMessages.TITLE_KFZB_1));
        KfZbConstants.layerToTitleMap.put(2, ColorUtil.getRedMsg(LocalMessages.TITLE_KFZB_2));
        KfZbConstants.layerToTitleMap.put(3, ColorUtil.getRedMsg(LocalMessages.TITLE_KFZB_3));
        KfZbConstants.layerToTitleMap.put(4, ColorUtil.getYellowMsg(LocalMessages.TITLE_KFZB_4));
    }
    
    public static void printTestData() {
        for (final Map.Entry<Integer, String> entry : KfZbConstants.layerToTitleMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
