package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("worldCityAreaCache")
public class WorldCityAreaCache extends AbstractCache<Integer, WorldCityArea>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Integer> areaToDropTypeMap;
    
    public WorldCityAreaCache() {
        this.areaToDropTypeMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WorldCityArea> resultList = this.dataLoader.getModels((Class)WorldCityArea.class);
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final List<Army> armyList = this.dataLoader.getModels((Class)Army.class);
        final Map<Integer, General> gMap = new HashMap<Integer, General>();
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
            gMap.put(general.getId(), general);
        }
        final Set<Integer> armySet = new HashSet<Integer>();
        for (final Army army : armyList) {
            armySet.add(army.getGeneralId());
        }
        final List<Troop> troopList = this.dataLoader.getModels((Class)Troop.class);
        final Map<Integer, Troop> troopMap = new HashMap<Integer, Troop>();
        for (final Troop troop : troopList) {
            troopMap.put(troop.getId(), troop);
        }
        for (final WorldCityArea temp : resultList) {
            if (temp.getMaskNpcs() != null && !temp.getMaskNpcs().trim().isEmpty()) {
                final String[] npcs = temp.getMaskNpcs().split(";");
                final Integer[] armiesId = new Integer[npcs.length + 1];
                for (int i = 0; i < npcs.length; ++i) {
                    final int armyId = Integer.valueOf(npcs[i]);
                    armiesId[i] = armyId;
                    if (!gSet.contains(armyId)) {
                        throw new RuntimeException("worldCityAreaCache init fail in npcs, table general wrong, id:" + temp.getArea() + "npc:" + armyId);
                    }
                    if (!armySet.contains(armyId)) {
                        throw new RuntimeException("worldCityAreaCache init fail in npcs, table army wrong, id:" + temp.getArea() + "npc:" + armyId);
                    }
                }
                armiesId[armiesId.length - 1] = temp.getMaskChief();
                temp.setMaskArmiesId(armiesId);
            }
            final int troopId = gMap.get(temp.getMaskChief()).getTroop();
            final Troop troop2 = troopMap.get(troopId);
            final int dropType = BattleDrop.getDropType(troop2.getDrop());
            if (dropType == 0) {
                throw new RuntimeException("worldCityAreaCache init fail in chief, troop has no drop, id:" + temp.getArea() + " troopId:" + troopId + " troopDrop:" + troop2.getDrop());
            }
            this.areaToDropTypeMap.put(temp.getArea(), dropType);
            super.put((Object)temp.getArea(), (Object)temp);
        }
    }
    
    public int getMaskDropType(final int areaId) {
        return this.areaToDropTypeMap.get(areaId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.areaToDropTypeMap.clear();
    }
}
