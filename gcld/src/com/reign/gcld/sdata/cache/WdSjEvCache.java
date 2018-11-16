package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.util.*;

@Component("WdSjEvCache")
public class WdSjEvCache extends AbstractCache<Integer, WdSjEv>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, Map<Integer, ArrayList<WdSjEv>>>> lvViewTerrainMap;
    
    public WdSjEvCache() {
        this.lvViewTerrainMap = new HashMap<Integer, Map<Integer, Map<Integer, ArrayList<WdSjEv>>>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjEv> resultList = this.dataLoader.getModels((Class)WdSjEv.class);
        for (final WdSjEv wdSjEv : resultList) {
            Map<Integer, Map<Integer, ArrayList<WdSjEv>>> viewTerrainMap = this.lvViewTerrainMap.get(wdSjEv.getRewardLv());
            if (viewTerrainMap == null) {
                viewTerrainMap = new HashMap<Integer, Map<Integer, ArrayList<WdSjEv>>>();
                this.lvViewTerrainMap.put(wdSjEv.getRewardLv(), viewTerrainMap);
            }
            Map<Integer, ArrayList<WdSjEv>> terrainMap = viewTerrainMap.get(wdSjEv.getView());
            if (terrainMap == null) {
                terrainMap = new HashMap<Integer, ArrayList<WdSjEv>>();
                viewTerrainMap.put(wdSjEv.getView(), terrainMap);
            }
            ArrayList<WdSjEv> list = terrainMap.get(wdSjEv.getTerrain());
            if (list == null) {
                list = new ArrayList<WdSjEv>();
                terrainMap.put(wdSjEv.getTerrain(), list);
            }
            list.add(wdSjEv);
            final ITaskReward taskReward1 = TaskRewardFactory.getInstance().getTaskReward(wdSjEv.getReward1());
            if (taskReward1 == null) {
                throw new RuntimeException("eventDaily init eventDailyCache fail in taskReward1:" + wdSjEv.getId());
            }
            wdSjEv.setTaskReward1(taskReward1);
            final ITaskReward taskReward2 = TaskRewardFactory.getInstance().getTaskReward(wdSjEv.getReward2());
            if (taskReward2 == null) {
                throw new RuntimeException("eventDaily init eventDailyCache fail in taskReward2:" + wdSjEv.getId());
            }
            wdSjEv.setTaskReward2(taskReward2);
            super.put((Object)wdSjEv.getId(), (Object)wdSjEv);
        }
    }
    
    public WdSjEv getRandWdSjEvByRewardlvViewTerrain(final int rewardLv, final int viewType, final int terrainType) {
        final Map<Integer, Map<Integer, ArrayList<WdSjEv>>> viewTerrainMap = this.lvViewTerrainMap.get(rewardLv);
        if (viewTerrainMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("viewTerrainMap is null").append("rewardLv", rewardLv).appendMethodName("getRandWdSjEvByRewardlvViewTerrain").flush();
            return null;
        }
        final Map<Integer, ArrayList<WdSjEv>> terrainMap = viewTerrainMap.get(viewType);
        if (terrainMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("terrainMap is null").append("viewType", viewType).appendMethodName("getRandWdSjEvByRewardlvViewTerrain").flush();
            return null;
        }
        final ArrayList<WdSjEv> List = terrainMap.get(terrainType);
        if (List == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("List is null").append("terrainType", terrainType).appendMethodName("getRandWdSjEvByRewardlvViewTerrain").flush();
            return null;
        }
        final int index = WebUtil.nextInt(List.size());
        return List.get(index);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.lvViewTerrainMap.clear();
    }
}
