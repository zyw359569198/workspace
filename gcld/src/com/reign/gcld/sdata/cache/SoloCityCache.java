package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.battle.common.*;

@Component("soloCityCache")
public class SoloCityCache extends AbstractCache<Integer, SoloCity>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, SoloCity>> soloIdMap;
    private Map<Integer, Integer> jubenIdToCapitalIdMap;
    private Map<Integer, Integer> cityIdIdToCapitalIdMap;
    private Map<Integer, List<Integer>> jubenIdToForceListMap;
    
    public SoloCityCache() {
        this.soloIdMap = new HashMap<Integer, Map<Integer, SoloCity>>();
        this.jubenIdToCapitalIdMap = new HashMap<Integer, Integer>();
        this.cityIdIdToCapitalIdMap = new HashMap<Integer, Integer>();
        this.jubenIdToForceListMap = new HashMap<Integer, List<Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SoloCity> list = this.dataLoader.getModels((Class)SoloCity.class);
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final List<Army> armyList = this.dataLoader.getModels((Class)Army.class);
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
        }
        final Set<Integer> armySet = new HashSet<Integer>();
        for (final Army army : armyList) {
            armySet.add(army.getGeneralId());
        }
        for (final SoloCity sc : list) {
            Map<Integer, SoloCity> map = this.soloIdMap.get(sc.getSoloId());
            if (map == null) {
                map = new HashMap<Integer, SoloCity>();
                this.soloIdMap.put(sc.getSoloId(), map);
            }
            map.put(sc.getId(), sc);
            final Map<Integer, List<Integer>> npcListMap = new HashMap<Integer, List<Integer>>();
            this.parseNpcList(npcListMap, sc, 1, sc.getNpcs1(), armySet, gSet);
            this.parseNpcList(npcListMap, sc, 2, sc.getNpcs2(), armySet, gSet);
            this.parseNpcList(npcListMap, sc, 3, sc.getNpcs3(), armySet, gSet);
            this.parseNpcList(npcListMap, sc, 4, sc.getNpcs4(), armySet, gSet);
            this.parseNpcList(npcListMap, sc, 5, sc.getNpcs5(), armySet, gSet);
            sc.setNpcListMap(npcListMap);
            if (sc.getCapital() == 1 && sc.getBelong() == 0) {
                this.jubenIdToCapitalIdMap.put(sc.getSoloId(), sc.getId());
            }
            super.put((Object)sc.getId(), (Object)sc);
        }
        for (final SoloCity sc : list) {
            final Integer capitalId = this.jubenIdToCapitalIdMap.get(sc.getSoloId());
            if (capitalId == null) {
                throw new RuntimeException("SoloCityCache init fail. capitalId of :" + sc.getId() + " is null");
            }
            this.cityIdIdToCapitalIdMap.put(sc.getId(), capitalId);
            List<Integer> forceList = this.jubenIdToForceListMap.get(sc.getSoloId());
            if (forceList == null) {
                forceList = new ArrayList<Integer>();
                this.jubenIdToForceListMap.put(sc.getSoloId(), forceList);
            }
            if (forceList.contains(sc.getBelong())) {
                continue;
            }
            forceList.add(sc.getBelong());
        }
    }
    
    private void parseNpcList(final Map<Integer, List<Integer>> npcListMap, final SoloCity sc, final int grade, final String npcs, final Set<Integer> armySet, final Set<Integer> gSet) {
        if (StringUtils.isBlank(npcs)) {
            return;
        }
        final List<Integer> npcList = new ArrayList<Integer>();
        final String[] npcArray = npcs.split(";");
        String[] array;
        for (int length = (array = npcArray).length, i = 0; i < length; ++i) {
            final String sId = array[i];
            if (!sId.trim().isEmpty()) {
                int id = 0;
                try {
                    id = Integer.parseInt(sId);
                }
                catch (NumberFormatException e) {
                    throw new RuntimeException("SoloCityCache init fail in Npcs" + grade + ", id:" + sc.getId() + " sId:" + sId);
                }
                if (!gSet.contains(id)) {
                    throw new RuntimeException("SoloCityCache init fail in Npcs" + grade + ", table general wrong, id:" + sc.getId() + " npc:" + sId);
                }
                if (!armySet.contains(id)) {
                    throw new RuntimeException("SoloCityCache init fail in Npcs" + grade + ", table army wrong, id:" + sc.getId() + " npc:" + sId);
                }
                npcList.add(id);
            }
        }
        npcListMap.put(grade, npcList);
    }
    
    public Map<Integer, SoloCity> getBySoloId(final int soloId) {
        return this.soloIdMap.get(soloId);
    }
    
    public Integer getCapitalIdByJubenId(final int jubenId) {
        final Integer capitalId = this.jubenIdToCapitalIdMap.get(jubenId);
        if (capitalId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("capitalId is null").append("jubenId", jubenId).appendClassName("SoloCityCache").flush();
        }
        return capitalId;
    }
    
    public Integer getCapitalIdByCityId(final int cityId) {
        final Integer capitalId = this.cityIdIdToCapitalIdMap.get(cityId);
        if (capitalId == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("capitalId is null").append("cityId", cityId).appendClassName("SoloCityCache").flush();
        }
        return capitalId;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.soloIdMap.clear();
        this.jubenIdToCapitalIdMap.clear();
        this.cityIdIdToCapitalIdMap.clear();
        this.jubenIdToForceListMap.clear();
    }
    
    public int getCapitalCityIdByForceId(final int jubenId, final int jadeBelong) {
        if (jubenId != 10) {
            return 0;
        }
        if (jadeBelong == 101) {
            return 124;
        }
        if (jadeBelong == 102) {
            return 127;
        }
        if (jadeBelong == 103) {
            return 133;
        }
        if (jadeBelong == 104) {
            return 138;
        }
        if (jadeBelong == 0) {
            return 139;
        }
        if (jadeBelong == 1) {
            return 140;
        }
        return 0;
    }
    
    public List<Integer> getForceIdList(final int jubenId) {
        return this.jubenIdToForceListMap.get(jubenId);
    }
}
