package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.util.*;

@Component("armiesCache")
public class ArmiesCache extends AbstractCache<Integer, Armies>
{
    @Autowired
    private SDataLoader dataLoader;
    Map<Integer, LinkedList<Armies>> map;
    Map<Integer, String> techConditionMap;
    ArrayList<Integer> techList;
    private Map<Integer, Integer> npcAndTechNumMap;
    private List<Integer> hasGold;
    private List<Armies> listJuben;
    private static List<String> name;
    Set<Integer> lastArmies;
    
    static {
        ArmiesCache.name = new ArrayList<String>();
    }
    
    public ArmiesCache() {
        this.map = new HashMap<Integer, LinkedList<Armies>>();
        this.techConditionMap = new HashMap<Integer, String>();
        this.techList = new ArrayList<Integer>();
        this.npcAndTechNumMap = new HashMap<Integer, Integer>();
        this.hasGold = new ArrayList<Integer>();
        this.listJuben = new ArrayList<Armies>();
        this.lastArmies = new HashSet<Integer>();
    }
    
    public static List<String> getGeneralName() {
        return ArmiesCache.name;
    }
    
    public List<Armies> getJubenOpenList() {
        return this.listJuben;
    }
    
    public List<Integer> getHasGold() {
        return this.hasGold;
    }
    
    public void setHasGold(final List<Integer> hasGold) {
        this.hasGold = hasGold;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Armies> resultList = this.dataLoader.getModels((Class)Armies.class);
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
        }
        final List<ArmiesReward> armiesRewardList = this.dataLoader.getModels((Class)ArmiesReward.class);
        final Set<Integer> armiesRewardSet = new HashSet<Integer>();
        for (final ArmiesReward ar : armiesRewardList) {
            armiesRewardSet.add(ar.getId());
        }
        final List<Power> powerList = this.dataLoader.getModels((Class)Power.class);
        final Set<Integer> powerSet = new HashSet<Integer>();
        for (final Power power : powerList) {
            powerSet.add(power.getId());
        }
        final List<Tech> tList = this.dataLoader.getModels((Class)Tech.class);
        final Set<Integer> techSet = new HashSet<Integer>();
        for (final Tech tech : tList) {
            techSet.add(tech.getId());
        }
        final List<Items> itemList = this.dataLoader.getModels((Class)Items.class);
        final Map<Integer, Items> itemMap = new HashMap<Integer, Items>();
        for (final Items item : itemList) {
            itemMap.put(item.getId(), item);
        }
        final List<BuildingDrawing> buildingDrawingList = this.dataLoader.getModels((Class)BuildingDrawing.class);
        final Map<Integer, BuildingDrawing> buildingDrawingMap = new HashMap<Integer, BuildingDrawing>();
        for (final BuildingDrawing drawing : buildingDrawingList) {
            buildingDrawingMap.put(drawing.getId(), drawing);
        }
        int techNum = 0;
        final Map<Integer, Armies> mapJuben = new HashMap<Integer, Armies>();
        for (final Armies armies : resultList) {
            if (!gSet.contains(armies.getChief())) {
                throw new RuntimeException("armiesCache init fail in chief, id:" + armies.getId());
            }
            if (armies.getName().length() < 4) {
                ArmiesCache.name.add(armies.getName());
            }
            if (!StringUtils.isBlank(armies.getGoldReward())) {
                this.hasGold.add(armies.getId());
            }
            if (armies.getNpcs() != null && !armies.getNpcs().trim().isEmpty()) {
                final String[] npcs = armies.getNpcs().split(";");
                final Integer[] armiesId = new Integer[npcs.length];
                for (int i = 0; i < npcs.length; ++i) {
                    final int armyId = Integer.valueOf(npcs[i]);
                    armiesId[i] = armyId;
                    if (!gSet.contains(armyId)) {
                        throw new RuntimeException("armiesCache init fail in npcs, id:" + armies.getId() + ",armyId:" + armyId);
                    }
                }
                armies.setArmiesId(armiesId);
            }
            final String str = armies.getReward();
            if (str != null && !str.trim().isEmpty()) {
                armies.setDropMap(new HashMap<Integer, BattleDrop>());
                final String[] rewards = str.split(";");
                String[] array;
                for (int length = (array = rewards).length, j = 0; j < length; ++j) {
                    final String ss = array[j];
                    final String[] reward = ss.split(",");
                    if ("general".equalsIgnoreCase(reward[0])) {
                        if (reward.length != 3) {
                            throw new RuntimeException("Init armies fail in armies Reward:" + str);
                        }
                        if (!gSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armiesCache init fail in rewards, id:" + armies.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 101;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        battleDrop.pro = Double.valueOf(reward[2]);
                        armies.getDropMap().put(101, battleDrop);
                    }
                    else if ("armies_reward".equalsIgnoreCase(reward[0])) {
                        if (!armiesRewardSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armies\u8868\u5305\u542b\u975e\u6cd5armies_reward, id:" + armies.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 103;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        armies.getDropMap().put(103, battleDrop);
                    }
                    else if ("power_extra".equalsIgnoreCase(reward[0])) {
                        if (!powerSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armies\u8868\u5305\u542b\u975e\u6cd5power_extra, id:" + armies.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 104;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        armies.getDropMap().put(104, battleDrop);
                    }
                    else if ("tech".equalsIgnoreCase(reward[0])) {
                        if (!techSet.contains(Integer.valueOf(reward[1]))) {
                            throw new RuntimeException("armies\u8868\u5305\u542b\u975e\u6cd5tech, id:" + armies.getId());
                        }
                        this.techConditionMap.put(Integer.parseInt(reward[1]), MessageFormatter.format(LocalMessages.TECH_OPEN_CONDITION, new Object[] { armies.getName() }));
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.type = 102;
                        battleDrop.id = Integer.valueOf(reward[1]);
                        armies.getDropMap().put(102, battleDrop);
                        this.techList.add(Integer.parseInt(reward[1]));
                        ++techNum;
                    }
                    else if ("drop_item".equalsIgnoreCase(reward[0])) {
                        if (itemMap.get(Integer.valueOf(reward[1])) == null) {
                            throw new RuntimeException("armies\u8868\u5305\u542b\u975e\u6cd5item, id:" + armies.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.id = Integer.valueOf(reward[1]);
                        battleDrop.pro = Double.valueOf(reward[2]);
                        battleDrop.type = 200 + battleDrop.id % 1000;
                        battleDrop.num = 1;
                        if (reward.length >= 4) {
                            battleDrop.limit = Integer.valueOf(reward[3]);
                        }
                        else {
                            battleDrop.limit = 1;
                        }
                        final Items item2 = itemMap.get(battleDrop.id);
                        battleDrop.reserve = String.valueOf(item2.getName()) + "*" + item2.getPic();
                        armies.getDropMap().put(battleDrop.type, battleDrop);
                    }
                    else if ("drawing".equalsIgnoreCase(reward[0])) {
                        if (buildingDrawingMap.get(Integer.valueOf(reward[1])) == null) {
                            throw new RuntimeException("armies\u8868\u5305\u542b\u975e\u6cd5drawing, id:" + armies.getId());
                        }
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.id = Integer.valueOf(reward[1]);
                        battleDrop.pro = 1.0;
                        battleDrop.type = 800 + battleDrop.id;
                        battleDrop.num = 1;
                        final BuildingDrawing drawing2 = buildingDrawingMap.get(battleDrop.id);
                        battleDrop.reserve = String.valueOf(drawing2.getName()) + "*" + drawing2.getPic();
                        armies.getDropMap().put(battleDrop.type, battleDrop);
                    }
                    else if ("solo_drama".equalsIgnoreCase(reward[0])) {
                        final BattleDrop battleDrop = new BattleDrop();
                        battleDrop.id = Integer.valueOf(reward[1]);
                        battleDrop.pro = 1.0;
                        battleDrop.type = 105;
                        armies.getDropMap().put(battleDrop.type, battleDrop);
                        mapJuben.put(battleDrop.id, armies);
                    }
                }
            }
            this.npcAndTechNumMap.put(armies.getId(), techNum);
            super.put((Object)armies.getId(), (Object)armies);
            if (this.map.get(armies.getPowerId()) == null) {
                final LinkedList<Armies> list = new LinkedList<Armies>();
                list.add(armies);
                this.map.put(armies.getPowerId(), list);
            }
            else {
                boolean add = false;
                int index = 0;
                for (final Armies as : this.map.get(armies.getPowerId())) {
                    if (as.getPos() > armies.getPos()) {
                        this.map.get(armies.getPowerId()).add(index, armies);
                        add = true;
                        break;
                    }
                    ++index;
                }
                if (add) {
                    continue;
                }
                this.map.get(armies.getPowerId()).add(armies);
            }
        }
        Collections.sort(this.hasGold);
        for (final Integer key : this.map.keySet()) {
            int lastId = 0;
            for (final Armies link : this.map.get(key)) {
                if (link.getType() == 1) {
                    lastId = link.getId();
                }
            }
            this.lastArmies.add(lastId);
        }
        final Armies[] ars = new Armies[mapJuben.size()];
        int k = 0;
        for (final Integer jubenKey : mapJuben.keySet()) {
            final Armies armies2 = mapJuben.get(jubenKey);
            int n = k;
            for (int m = 0; m < k; ++m) {
                if (ars[m].getLevel() > armies2.getLevel()) {
                    while (n > m) {
                        ars[n] = ars[n - 1];
                        --n;
                    }
                    break;
                }
            }
            ars[n] = armies2;
            ++k;
        }
        Armies[] array2;
        for (int length2 = (array2 = ars).length, l = 0; l < length2; ++l) {
            final Armies as2 = array2[l];
            this.listJuben.add(as2);
        }
    }
    
    public boolean isLastArmies(final int armiesId) {
        return this.lastArmies.contains(armiesId);
    }
    
    public LinkedList<Armies> getArmiesByPowerId(final int powerId) {
        return this.map.get(powerId);
    }
    
    public Armies getNextArmies(final int id) {
        final int powerId = ((Armies)this.get((Object)id)).getPowerId();
        final LinkedList<Armies> link = this.map.get(powerId);
        final Iterator<Armies> it = link.listIterator();
        while (it.hasNext()) {
            final Armies armies = it.next();
            if (armies.getId() == id && it.hasNext()) {
                return it.next();
            }
        }
        return null;
    }
    
    public String getTechCondition(final int techId) {
        return this.techConditionMap.get(techId);
    }
    
    public int getTechNum(final int npcId) {
        return this.npcAndTechNumMap.get(npcId);
    }
    
    public int getTechId(final int index) {
        if (index >= this.techList.size()) {
            return 0;
        }
        return this.techList.get(index);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.map.clear();
        this.techConditionMap.clear();
        this.techList.clear();
        this.npcAndTechNumMap.clear();
        this.lastArmies.clear();
        this.hasGold.clear();
        this.listJuben.clear();
    }
}
