package com.reign.kfgz.control;

import java.util.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.kfgz.dto.*;

public class KfgzNpcAIManager
{
    public static Map<Integer, Map<Integer, KfgzNpcAIChoosenInfo>> worldNpcAIChoosenMap;
    public static final int CHOOSENINTERVAL = 120000;
    
    static {
        KfgzNpcAIManager.worldNpcAIChoosenMap = new ConcurrentHashMap<Integer, Map<Integer, KfgzNpcAIChoosenInfo>>();
    }
    
    public static int getNpcAIChooseResult(final int gzId, final int forceId) {
        final Map<Integer, KfgzNpcAIChoosenInfo> map = KfgzNpcAIManager.worldNpcAIChoosenMap.get(gzId);
        if (map == null || map.get(forceId) == null) {
            return 0;
        }
        return map.get(forceId).getChooseResult();
    }
    
    public static void clear() {
        KfgzNpcAIManager.worldNpcAIChoosenMap.clear();
    }
    
    public static KfgzNpcAIChoosenInfo getNpcAIChooseInfo(final int gzId, final int forceId) {
        final Map<Integer, KfgzNpcAIChoosenInfo> map = KfgzNpcAIManager.worldNpcAIChoosenMap.get(gzId);
        if (map == null) {
            return null;
        }
        final KfgzNpcAIChoosenInfo cInfo = map.get(forceId);
        return cInfo;
    }
    
    public static String getNpcAIXml(final int gzId, final int forceId) {
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final Map<Integer, KfgzNpcAIChoosenInfo> map = KfgzNpcAIManager.worldNpcAIChoosenMap.get(gzId);
        if (map == null || map.get(forceId) == null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject("choosenpcai");
            doc.createElement("choosed", 1);
            doc.createElement("choosenCD", 0);
            doc.endObject();
            return doc.toString();
        }
        final KfgzNpcAIChoosenInfo npcAiInfo = map.get(forceId);
        final JsonDocument doc2 = new JsonDocument();
        final long choosenCD = canChoose(npcAiInfo, baseInfo);
        doc2.startObject("choosenpcai");
        doc2.createElement("choosed", (npcAiInfo.getChooseResult() > 0) ? npcAiInfo.getChooseResult() : 1);
        doc2.createElement("choosenCD", choosenCD);
        doc2.endObject();
        return doc2.toString();
    }
    
    private static long canChoose(final KfgzNpcAIChoosenInfo npcAiInfo, final KfgzBaseInfo baseInfo) {
        if (npcAiInfo.getChooseResult() == 0) {
            return 0L;
        }
        final long nowTime = System.currentTimeMillis();
        final long beginTime = baseInfo.getGzStartTime().getTime();
        final long t = (nowTime - beginTime) / 120000L;
        final long lastChoosenTime = beginTime + t * 120000L;
        if (npcAiInfo.getLastSetTime() < lastChoosenTime) {
            return 0L;
        }
        long cd = lastChoosenTime + 120000L - nowTime;
        if (cd < 0L) {
            cd = 0L;
        }
        return cd;
    }
    
    public static boolean canChoosenNewAI(final int gzId, final int forceId) {
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final Map<Integer, KfgzNpcAIChoosenInfo> map = KfgzNpcAIManager.worldNpcAIChoosenMap.get(gzId);
        if (map == null || map.get(forceId) == null) {
            return true;
        }
        final KfgzNpcAIChoosenInfo npcAiInfo = map.get(forceId);
        final long choosenCD = canChoose(npcAiInfo, baseInfo);
        return choosenCD <= 0L;
    }
    
    public static void choosenNpcSkill(final int gzId, final int forceId, final int choosenId) {
        Map<Integer, KfgzNpcAIChoosenInfo> map = KfgzNpcAIManager.worldNpcAIChoosenMap.get(gzId);
        if (map == null) {
            map = new ConcurrentHashMap<Integer, KfgzNpcAIChoosenInfo>();
        }
        if (map.get(forceId) == null) {
            KfgzNpcAIManager.worldNpcAIChoosenMap.put(gzId, map);
            final KfgzNpcAIChoosenInfo aiInfo = new KfgzNpcAIChoosenInfo();
            aiInfo.setGzId(gzId);
            aiInfo.setForceId(forceId);
            aiInfo.setChooseResult(choosenId);
            aiInfo.setLastSetTime(System.currentTimeMillis());
            map.put(forceId, aiInfo);
            return;
        }
        final KfgzNpcAIChoosenInfo aiInfo = map.get(forceId);
        aiInfo.setChooseResult(choosenId);
        aiInfo.setLastSetTime(System.currentTimeMillis());
    }
}
