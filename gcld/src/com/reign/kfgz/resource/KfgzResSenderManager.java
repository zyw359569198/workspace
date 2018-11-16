package com.reign.kfgz.resource;

import java.util.concurrent.*;
import com.reign.kfgz.battle.*;
import com.reign.kf.match.sdata.common.*;
import java.util.*;

public class KfgzResSenderManager
{
    static LinkedBlockingQueue<PlayerBattleDropItem> addQueue;
    static Map<Integer, String> dropTypeMap;
    static boolean resThreadRunState;
    
    static {
        KfgzResSenderManager.addQueue = new LinkedBlockingQueue<PlayerBattleDropItem>();
        (KfgzResSenderManager.dropTypeMap = new HashMap<Integer, String>()).put(1, "copper");
        KfgzResSenderManager.dropTypeMap.put(2, "wood");
        KfgzResSenderManager.dropTypeMap.put(3, "food");
        KfgzResSenderManager.dropTypeMap.put(4, "iron");
        KfgzResSenderManager.dropTypeMap.put(5, "exp");
        KfgzResSenderManager.dropTypeMap.put(11, "gold");
        KfgzResSenderManager.dropTypeMap.put(13, "gExp");
        KfgzResSenderManager.dropTypeMap.put(1001, "copper");
        KfgzResSenderManager.dropTypeMap.put(1002, "wood");
        KfgzResSenderManager.dropTypeMap.put(1003, "food");
        KfgzResSenderManager.dropTypeMap.put(1004, "iron");
        KfgzResSenderManager.dropTypeMap.put(1005, "exp");
        KfgzResSenderManager.dropTypeMap.put(11, "gold");
        KfgzResSenderManager.resThreadRunState = true;
    }
    
    public static void addNewDropItem(final PlayerBattleDropItem dropItem) {
        KfgzResSenderManager.addQueue.add(dropItem);
    }
    
    public static void addNewDropMap(final Integer competitorId, final Map<Integer, BattleDrop> dropMap) {
        if (competitorId <= 0 || dropMap == null) {
            return;
        }
        for (final Map.Entry<Integer, BattleDrop> entry : dropMap.entrySet()) {
            final int type = entry.getKey();
            final BattleDrop dropItem = entry.getValue();
            final String key = KfgzResSenderManager.dropTypeMap.get(dropItem.type);
            if (key != null) {
                final PlayerBattleDropItem pbd = new PlayerBattleDropItem(competitorId, key, dropItem.num);
                KfgzResSenderManager.addQueue.add(pbd);
            }
        }
    }
    
    public static void ini() {
        final ResSendThread sThread = new ResSendThread("gzResSendThread");
        sThread.start();
    }
    
    static class ResSendThread extends Thread
    {
        public ResSendThread(final String name) {
            super(name);
        }
        
        @Override
        public void run() {
            while (KfgzResSenderManager.resThreadRunState) {
                try {
                    final PlayerBattleDropItem pbd = KfgzResSenderManager.addQueue.poll();
                    if (pbd != null) {
                        if (pbd.getType() == "gExp") {
                            KfgzResChangeManager.addGeneralExp(pbd.getCompetitorId(), pbd.getgId(), pbd.getValue(), "\u6218\u6597\u5956\u52b1");
                        }
                        else {
                            KfgzResChangeManager.addResource(pbd.getCompetitorId(), pbd.getValue(), pbd.getType(), "\u6218\u6597\u5956\u52b1");
                        }
                    }
                    else {
                        Thread.sleep(100L);
                    }
                }
                catch (Exception e2) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e2.printStackTrace();
                }
            }
        }
    }
}
