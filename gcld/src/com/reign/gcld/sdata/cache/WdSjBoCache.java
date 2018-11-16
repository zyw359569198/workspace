package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.util.*;

@Component("WdSjBoCache")
public class WdSjBoCache extends AbstractCache<Integer, WdSjBo>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, Map<Integer, ArrayList<WdSjBo>>>> lvViewTerrainMap;
    
    public WdSjBoCache() {
        this.lvViewTerrainMap = new HashMap<Integer, Map<Integer, Map<Integer, ArrayList<WdSjBo>>>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final Map<Integer, General> gMap = new HashMap<Integer, General>();
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
            gMap.put(general.getId(), general);
        }
        final List<Army> armyList = this.dataLoader.getModels((Class)Army.class);
        final Set<Integer> armySet = new HashSet<Integer>();
        for (final Army army : armyList) {
            armySet.add(army.getGeneralId());
        }
        final List<WdSjBo> resultList = this.dataLoader.getModels((Class)WdSjBo.class);
        for (final WdSjBo wdSjBo : resultList) {
            if (!gSet.contains(wdSjBo.getChief())) {
                throw new RuntimeException("WdSjBoCache init fail in npcs, table general wrong, id:" + wdSjBo.getId() + "npc:" + wdSjBo.getChief());
            }
            if (!armySet.contains(wdSjBo.getChief())) {
                throw new RuntimeException("WdSjBoCache init fail in npcs, table army wrong, id:" + wdSjBo.getId() + "npc:" + wdSjBo.getChief());
            }
            if (!wdSjBo.getNpcs().trim().isEmpty()) {
                final String[] ss = wdSjBo.getNpcs().split(";");
                final Integer[] Ids = new Integer[ss.length + 1];
                for (int i = 0; i < ss.length; ++i) {
                    if (!ss[i].equals("")) {
                        final int armyId = Integer.valueOf(ss[i]);
                        Ids[i] = armyId;
                        if (!gSet.contains(armyId)) {
                            throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + wdSjBo.getId() + "npc:" + armyId);
                        }
                        if (!armySet.contains(armyId)) {
                            throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + wdSjBo.getId() + "npc:" + armyId);
                        }
                    }
                }
                Ids[Ids.length - 1] = wdSjBo.getChief();
                wdSjBo.setArmiesId(Ids);
            }
            else {
                final Integer[] Ids2 = { wdSjBo.getChief() };
                wdSjBo.setArmiesId(Ids2);
            }
            Map<Integer, Map<Integer, ArrayList<WdSjBo>>> viewTerrainMap = this.lvViewTerrainMap.get(wdSjBo.getRewardLv());
            if (viewTerrainMap == null) {
                viewTerrainMap = new HashMap<Integer, Map<Integer, ArrayList<WdSjBo>>>();
                this.lvViewTerrainMap.put(wdSjBo.getRewardLv(), viewTerrainMap);
            }
            Map<Integer, ArrayList<WdSjBo>> terrainMap = viewTerrainMap.get(wdSjBo.getView());
            if (terrainMap == null) {
                terrainMap = new HashMap<Integer, ArrayList<WdSjBo>>();
                viewTerrainMap.put(wdSjBo.getView(), terrainMap);
            }
            ArrayList<WdSjBo> List = terrainMap.get(wdSjBo.getTerrainEffectType());
            if (List == null) {
                List = new ArrayList<WdSjBo>();
                terrainMap.put(wdSjBo.getTerrainEffectType(), List);
            }
            List.add(wdSjBo);
            super.put((Object)wdSjBo.getId(), (Object)wdSjBo);
        }
    }
    
    public WdSjBo getRandWdSjBoByRewardlvViewTerrain(final int rewardLv, final int viewType, final int terrainType) {
        final Map<Integer, Map<Integer, ArrayList<WdSjBo>>> viewTerrainMap = this.lvViewTerrainMap.get(rewardLv);
        if (viewTerrainMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("viewTerrainMap is null").append("rewardLv", rewardLv).appendMethodName("getRandWdSjBoByRewardlvViewTerrain").flush();
            return null;
        }
        final Map<Integer, ArrayList<WdSjBo>> terrainMap = viewTerrainMap.get(viewType);
        if (terrainMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("terrainMap is null").append("viewType", viewType).appendMethodName("getRandWdSjBoByRewardlvViewTerrain").flush();
            return null;
        }
        final ArrayList<WdSjBo> List = terrainMap.get(terrainType);
        if (List == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("List is null").append("terrainType", terrainType).appendMethodName("getRandWdSjBoByRewardlvViewTerrain").flush();
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
