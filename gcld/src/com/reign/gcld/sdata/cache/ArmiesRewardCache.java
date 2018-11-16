package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.util.*;

@Component("armiesRewardCache")
public class ArmiesRewardCache extends AbstractCache<Integer, ArmiesReward>
{
    @Autowired
    private SDataLoader dataLoader;
    Map<Integer, LinkedList<ArmiesReward>> powerToArmiesMap;
    Map<Integer, ArmiesReward> VipArmiesMap;
    private Map<Integer, Integer> bonusToDropTypeMap;
    private Map<Integer, String> techConditionMap;
    
    public ArmiesRewardCache() {
        this.powerToArmiesMap = new HashMap<Integer, LinkedList<ArmiesReward>>();
        this.VipArmiesMap = new HashMap<Integer, ArmiesReward>();
        this.bonusToDropTypeMap = new HashMap<Integer, Integer>();
        this.techConditionMap = new HashMap<Integer, String>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<ArmiesReward> resultList = this.dataLoader.getModels((Class)ArmiesReward.class);
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
        final List<Troop> troopList = this.dataLoader.getModels((Class)Troop.class);
        final Map<Integer, Troop> troopMap = new HashMap<Integer, Troop>();
        for (final Troop troop : troopList) {
            troopMap.put(troop.getId(), troop);
        }
        final List<Tech> tList = this.dataLoader.getModels((Class)Tech.class);
        final Set<Integer> techSet = new HashSet<Integer>();
        for (final Tech tech : tList) {
            techSet.add(tech.getId());
        }
        for (final ArmiesReward armiesReward : resultList) {
            if (armiesReward.getId() < 0) {
                this.VipArmiesMap.put(armiesReward.getId(), armiesReward);
                if (armiesReward.getPowerId() != 0) {
                    throw new RuntimeException("worldCityCache init fail, vip5 record powerId is valid, id:" + armiesReward.getId());
                }
            }
            if (!gSet.contains(armiesReward.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + armiesReward.getId() + "npc:" + armiesReward.getChief());
            }
            if (!armySet.contains(armiesReward.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + armiesReward.getId() + "npc:" + armiesReward.getChief());
            }
            final String[] ss = armiesReward.getNpcs().split(";");
            final Integer[] Ids = new Integer[ss.length + 1];
            for (int i = 0; i < ss.length; ++i) {
                if (!ss[i].equals("")) {
                    final int armyId = Integer.valueOf(ss[i]);
                    Ids[i] = armyId;
                    if (!gSet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + armiesReward.getId() + "npc:" + armyId);
                    }
                    if (!armySet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + armiesReward.getId() + "npc:" + armyId);
                    }
                }
            }
            Ids[Ids.length - 1] = armiesReward.getChief();
            armiesReward.setArmiesId(Ids);
            if (this.powerToArmiesMap.get(armiesReward.getPowerId()) == null) {
                final LinkedList<ArmiesReward> list = new LinkedList<ArmiesReward>();
                this.powerToArmiesMap.put(armiesReward.getPowerId(), list);
            }
            this.powerToArmiesMap.get(armiesReward.getPowerId()).add(armiesReward);
            final int troopId = gMap.get(armiesReward.getChief()).getTroop();
            final Troop troop2 = troopMap.get(troopId);
            final int dropType = BattleDrop.getDropType(troop2.getDrop());
            this.bonusToDropTypeMap.put(armiesReward.getId(), dropType);
            if (armiesReward.getReward() != null && !StringUtils.isBlank(armiesReward.getReward())) {
                armiesReward.setDropMap(new HashMap<Integer, BattleDrop>());
                final String[] rewards = armiesReward.getReward().split(";");
                String[] array;
                for (int length = (array = rewards).length, j = 0; j < length; ++j) {
                    final String temp = array[j];
                    final String[] reward = temp.split(",");
                    if ("general".equalsIgnoreCase(reward[0])) {
                        if (!gSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armiesRewardCache init fail in rewards, id:" + armiesReward.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 101;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        battleDrop.pro = 1.0;
                        armiesReward.getDropMap().put(101, battleDrop);
                    }
                    else if ("tech".equalsIgnoreCase(reward[0])) {
                        if (!techSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armiesReward\u8868\u5305\u542b\u975e\u6cd5tech, id:" + armiesReward.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 102;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        armiesReward.getDropMap().put(102, battleDrop);
                        this.techConditionMap.put(Integer.parseInt(reward[1]), MessageFormatter.format(LocalMessages.TECH_OPEN_CONDITION, new Object[] { armiesReward.getName() }));
                    }
                }
            }
            super.put((Object)armiesReward.getId(), (Object)armiesReward);
        }
    }
    
    public LinkedList<ArmiesReward> getArmiesRewardByPowerId(final int powerId) {
        return this.powerToArmiesMap.get(powerId);
    }
    
    public int getBonusDropType(final int defId) {
        return this.bonusToDropTypeMap.get(defId);
    }
    
    public String getTechCondition(final int techId) {
        return this.techConditionMap.get(techId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.powerToArmiesMap.clear();
        this.VipArmiesMap.clear();
        this.bonusToDropTypeMap.clear();
        this.techConditionMap.clear();
    }
}
