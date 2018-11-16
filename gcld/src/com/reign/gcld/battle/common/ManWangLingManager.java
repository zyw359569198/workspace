package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import java.util.*;

public class ManWangLingManager
{
    private static final Logger timeLog;
    private static final ManWangLingManager instance;
    private IDataGetter dataGetter;
    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ManWangLingObj>> manWangLingObjMap;
    private Map<Integer, ManWangLingObj> receivedMap;
    
    static {
        timeLog = new TimerLogger();
        instance = new ManWangLingManager();
    }
    
    private ManWangLingManager() {
        this.dataGetter = null;
        this.manWangLingObjMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ManWangLingObj>>();
        this.receivedMap = new ConcurrentHashMap<Integer, ManWangLingObj>();
    }
    
    public static ManWangLingManager getInstance() {
        return ManWangLingManager.instance;
    }
    
    public void initManWangLingManager(final IDataGetter dataGetter) {
        try {
            this.dataGetter = dataGetter;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.initManWangLingManager catch Exception", e);
        }
    }
    
    public void addManWangLingObj(final int playerForceId, final int type, final ManWangLingObj manWangLingObj) {
        try {
            ConcurrentHashMap<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(playerForceId);
            if (map == null) {
                map = new ConcurrentHashMap<Integer, ManWangLingObj>();
                this.manWangLingObjMap.put(playerForceId, map);
            }
            map.put(type, manWangLingObj);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.addManWangLingObj catch Exception", e);
        }
    }
    
    public void pushManWangLingMsg(final int toForceId) {
        try {
            final ConcurrentHashMap<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(toForceId);
            if (map == null) {
                return;
            }
            for (final PlayerDto playerDto : Players.getAllPlayer()) {
                if (playerDto.forceId != toForceId) {
                    continue;
                }
                final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerDto.playerId);
                final char[] cs = pa.getFunctionId().toCharArray();
                if (cs[10] != '1') {
                    continue;
                }
                int num = map.size();
                ManWangLingObj manWangLingObj = map.get(1);
                if (manWangLingObj == null || manWangLingObj.playerSet.contains(playerDto.playerId)) {
                    manWangLingObj = map.get(2);
                    --num;
                }
                final JsonDocument doc = new JsonDocument();
                if (manWangLingObj == null || manWangLingObj.playerSet.contains(playerDto.playerId)) {
                    doc.startObject();
                    doc.createElement("countDown", (-1));
                    doc.endObject();
                    final byte[] send = doc.toByte();
                    Players.push(playerDto.playerId, PushCommand.PUSH_MAN_WANG_LING, send);
                }
                else {
                    doc.startObject();
                    doc.createElement("type", manWangLingObj.type);
                    doc.createElement("manZuForceId", manWangLingObj.manZuForceId);
                    doc.createElement("countDown", manWangLingObj.expireTime - System.currentTimeMillis());
                    doc.createElement("targetCityId", manWangLingObj.targetCityId);
                    doc.createElement("targetCityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)manWangLingObj.targetCityId)).getName());
                    doc.createElement("num", num);
                    doc.endObject();
                    final byte[] send = doc.toByte();
                    Players.push(playerDto.playerId, PushCommand.PUSH_MAN_WANG_LING, send);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.pushMsgAfterFireManWangLing catch Exception", e);
        }
    }
    
    public void caculateOnePlayer(final int playerId, final int forceId, final JsonDocument doc) {
        try {
            final ConcurrentHashMap<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(forceId);
            if (map == null) {
                return;
            }
            int num = map.size();
            ManWangLingObj manWangLingObj = map.get(1);
            if (manWangLingObj == null || manWangLingObj.playerSet.contains(playerId)) {
                manWangLingObj = map.get(2);
                --num;
            }
            if (manWangLingObj == null || manWangLingObj.playerSet.contains(playerId)) {
                return;
            }
            doc.startObject("MWLNext");
            doc.createElement("type", manWangLingObj.type);
            doc.createElement("manZuForceId", manWangLingObj.manZuForceId);
            doc.createElement("countDown", manWangLingObj.expireTime - System.currentTimeMillis());
            doc.createElement("targetCityId", manWangLingObj.targetCityId);
            doc.createElement("targetCityName", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)manWangLingObj.targetCityId)).getName());
            doc.createElement("num", num);
            doc.endObject();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.caculateOnePlayer catch Exception", e);
        }
    }
    
    public ManWangLingObj removeManWangLingObj(final int toForceId, final int type) {
        try {
            ManWangLingManager.timeLog.info(LogUtil.formatThreadLog("ManWangLingManager", "removeManWangLing", 0, 0L, "toForceId:" + toForceId + "type:" + type));
            final ConcurrentHashMap<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(toForceId);
            if (map == null) {
                return null;
            }
            final ManWangLingObj manWangLingObj = map.remove(type);
            if (manWangLingObj != null) {
                this.pushManWangLingMsg(toForceId);
            }
            return manWangLingObj;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.removeManWangLingObj catch Exception", e);
            return null;
        }
    }
    
    public ManWangLingObj removeManWangLingTryAfterCityConquered(final int cityId, final int oldForceId) {
        try {
            final List<ManWangLingObj> list = new LinkedList<ManWangLingObj>();
            for (final ConcurrentHashMap<Integer, ManWangLingObj> map : this.manWangLingObjMap.values()) {
                for (final ManWangLingObj manWangLingObj : map.values()) {
                    if (manWangLingObj.targetCityId == cityId && manWangLingObj.fromForceId == oldForceId) {
                        list.add(manWangLingObj);
                    }
                }
            }
            for (final ManWangLingObj manWangLingObj2 : list) {
                ManWangLingManager.timeLog.info(LogUtil.formatThreadLog("ManWangLingManager", "removeManWangLingTryAfterCityConquered", 0, 0L, "targetCityId:" + manWangLingObj2.targetCityId + "fromForceId:" + manWangLingObj2.fromForceId + "toForceId:" + manWangLingObj2.toForceId + "type:" + manWangLingObj2.type));
                this.removeManWangLingObj(manWangLingObj2.toForceId, manWangLingObj2.type);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.removeManWangLingTryAfterCityConquered catch Exception", e);
        }
        return null;
    }
    
    public ManWangLingObj getCanReplyMWLByForceId(final int playerId, final int forceId) {
        final ConcurrentHashMap<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(forceId);
        if (map == null) {
            return null;
        }
        ManWangLingObj manWangLingObj = map.get(1);
        if (manWangLingObj == null || manWangLingObj.playerSet.contains(playerId)) {
            manWangLingObj = map.get(2);
        }
        if (manWangLingObj != null && !manWangLingObj.playerSet.contains(playerId)) {
            return manWangLingObj;
        }
        return null;
    }
    
    public ManWangLingObj getProtectManWangLingByToForceId(final int toForceId) {
        final Map<Integer, ManWangLingObj> map = this.manWangLingObjMap.get(toForceId);
        if (map == null) {
            return null;
        }
        return map.get(2);
    }
    
    public void clearProtectManWangLingByFromForceId(final int fromForceId) {
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, ManWangLingObj> set : this.receivedMap.entrySet()) {
            if (set.getValue().fromForceId == fromForceId) {
                list.add(set.getKey());
            }
        }
        for (final Integer key : list) {
            this.receivedMap.remove(key);
        }
    }
    
    public void clearProtectManWangLingByTargetCity(final int targetCityId) {
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, ManWangLingObj> set : this.receivedMap.entrySet()) {
            if (set.getValue().targetCityId == targetCityId) {
                list.add(set.getKey());
            }
        }
        for (final Integer key : list) {
            this.receivedMap.remove(key);
        }
    }
    
    public void clearProtectManWangLingByToForceId(final int toForceId) {
        final List<Integer> list = new ArrayList<Integer>();
        for (final Map.Entry<Integer, ManWangLingObj> set : this.receivedMap.entrySet()) {
            if (set.getValue().toForceId == toForceId) {
                list.add(set.getKey());
            }
        }
        for (final Integer key : list) {
            this.receivedMap.remove(key);
        }
    }
    
    public List<Integer> getToForceId(final int fromForceId) {
        final List<Integer> toForceIdList = new ArrayList<Integer>();
        if (1 == fromForceId) {
            toForceIdList.add(2);
            toForceIdList.add(3);
        }
        else if (2 == fromForceId) {
            toForceIdList.add(1);
            toForceIdList.add(3);
        }
        else if (3 == fromForceId) {
            toForceIdList.add(1);
            toForceIdList.add(2);
        }
        return toForceIdList;
    }
    
    public void resProtectManWang(final int playerId, final ManWangLingObj manWangLingObj) {
        try {
            this.receivedMap.put(playerId, manWangLingObj);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("ManWangLingManager.resProtectManWang catch Exception", e);
        }
    }
    
    public boolean hasProtectManWang(final int playerId, final int fromForceId) {
        final Tuple<Integer, Date> res = this.dataGetter.getNationService().getTryMap().get(fromForceId);
        return res.left == 3;
    }
}
