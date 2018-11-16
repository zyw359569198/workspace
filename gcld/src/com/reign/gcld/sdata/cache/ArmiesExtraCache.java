package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("armiesExtraCache")
public class ArmiesExtraCache extends AbstractCache<Integer, ArmiesExtra>
{
    @Autowired
    private SDataLoader dataLoader;
    Map<Integer, ArrayList<ArmiesExtra>> PowerToArmiesExtraMap;
    private Map<Integer, Integer> extraToDropTypeMap;
    
    public ArmiesExtraCache() {
        this.PowerToArmiesExtraMap = new HashMap<Integer, ArrayList<ArmiesExtra>>();
        this.extraToDropTypeMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
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
        final Set<Integer> powerSet = new HashSet<Integer>();
        final List<Power> powerList = this.dataLoader.getModels((Class)Power.class);
        for (final Power power : powerList) {
            powerSet.add(power.getId());
        }
        final List<ArmiesExtra> resultList = this.dataLoader.getModels((Class)ArmiesExtra.class);
        for (final ArmiesExtra armiesExtra : resultList) {
            if (!powerSet.contains(armiesExtra.getPowerId())) {
                throw new RuntimeException("ArmiesExtraCache init fail in powerId, id:" + armiesExtra.getId());
            }
            if (!gSet.contains(armiesExtra.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + armiesExtra.getId() + "npc:" + armiesExtra.getChief());
            }
            if (!armySet.contains(armiesExtra.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + armiesExtra.getId() + "npc:" + armiesExtra.getChief());
            }
            final String[] ss = armiesExtra.getNpcs().split(";");
            final Integer[] Ids = new Integer[ss.length + 1];
            for (int i = 0; i < ss.length; ++i) {
                if (!ss[i].equals("")) {
                    final int armyId = Integer.valueOf(ss[i]);
                    Ids[i] = armyId;
                    if (!gSet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + armiesExtra.getId() + "npc:" + armyId);
                    }
                    if (!armySet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + armiesExtra.getId() + "npc:" + armyId);
                    }
                }
            }
            Ids[Ids.length - 1] = armiesExtra.getChief();
            armiesExtra.setArmiesId(Ids);
            if (this.PowerToArmiesExtraMap.get(armiesExtra.getPowerId()) == null) {
                final ArrayList<ArmiesExtra> list = new ArrayList<ArmiesExtra>();
                this.PowerToArmiesExtraMap.put(armiesExtra.getPowerId(), list);
            }
            this.PowerToArmiesExtraMap.get(armiesExtra.getPowerId()).add(armiesExtra);
            final int troopId = gMap.get(armiesExtra.getChief()).getTroop();
            final Troop troop2 = troopMap.get(troopId);
            final int dropType = BattleDrop.getDropType(troop2.getDrop());
            this.extraToDropTypeMap.put(armiesExtra.getId(), dropType);
            super.put((Object)armiesExtra.getId(), (Object)armiesExtra);
        }
    }
    
    public ArrayList<ArmiesExtra> getArmiesExtraByPowerId(final int powerId) {
        return this.PowerToArmiesExtraMap.get(powerId);
    }
    
    public ArmiesExtra getNextArmiesExtra(final int id) {
        final int powerId = ((ArmiesExtra)this.get((Object)id)).getPowerId();
        final List<ArmiesExtra> link = this.PowerToArmiesExtraMap.get(powerId);
        final Iterator<ArmiesExtra> it = link.listIterator();
        while (it.hasNext()) {
            final ArmiesExtra armies = it.next();
            if (armies.getId() == id && it.hasNext()) {
                return it.next();
            }
        }
        return null;
    }
    
    public int getExtraDropType(final int defId) {
        return this.extraToDropTypeMap.get(defId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.PowerToArmiesExtraMap.clear();
    }
}
