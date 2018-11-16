package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("worldCityCache")
public class WorldCityCache extends AbstractCache<Integer, WorldCity>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<WorldCity>> cityWeiMap;
    private Map<Integer, List<WorldCity>> cityShuMap;
    private Map<Integer, List<WorldCity>> cityWuMap;
    private List<String> cityNames;
    private Map<Integer, Integer> cityToDropTypeMap;
    private Map<Integer, Map<Integer, Integer>> distanceMap;
    private Map<Integer, List<WorldCity>> distance3List;
    private Map<Integer, List<WorldCity>> barbarainInvadeCities;
    private Map<Integer, Integer> borderMap;
    private Set<Integer> showMaskSet;
    private List<WorldCity> list;
    private Map<Integer, List<List<WorldCity>>> countryDistanceSetMap;
    private Map<Integer, List<List<WorldCity>>> countryLessThanDistanceSetMap;
    private List<List<WorldCity>> lessThanDistanceSetMap;
    private Integer maxDistance;
    
    public WorldCityCache() {
        this.cityWeiMap = new HashMap<Integer, List<WorldCity>>();
        this.cityShuMap = new HashMap<Integer, List<WorldCity>>();
        this.cityWuMap = new HashMap<Integer, List<WorldCity>>();
        this.cityNames = new ArrayList<String>();
        this.cityToDropTypeMap = new HashMap<Integer, Integer>();
        this.distanceMap = new HashMap<Integer, Map<Integer, Integer>>();
        this.distance3List = new HashMap<Integer, List<WorldCity>>();
        this.barbarainInvadeCities = new HashMap<Integer, List<WorldCity>>();
        this.borderMap = new HashMap<Integer, Integer>();
        this.showMaskSet = new HashSet<Integer>();
        this.list = null;
        this.countryDistanceSetMap = new HashMap<Integer, List<List<WorldCity>>>();
        this.countryLessThanDistanceSetMap = new HashMap<Integer, List<List<WorldCity>>>();
        this.lessThanDistanceSetMap = new ArrayList<List<WorldCity>>();
        this.maxDistance = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.list = this.dataLoader.getModels((Class)WorldCity.class);
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
        final Map<Integer, Integer> weiSet = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> shuSet = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> wuSet = new HashMap<Integer, Integer>();
        this.distanceMap.put(1, weiSet);
        this.distanceMap.put(2, shuSet);
        this.distanceMap.put(3, wuSet);
        final List<WorldCity> weiList3 = new ArrayList<WorldCity>();
        final List<WorldCity> shuList3 = new ArrayList<WorldCity>();
        final List<WorldCity> wuList3 = new ArrayList<WorldCity>();
        this.distance3List.put(1, weiList3);
        this.distance3List.put(2, shuList3);
        this.distance3List.put(3, wuList3);
        final List<WorldCity> beidiInvadeList = new ArrayList<WorldCity>();
        final List<WorldCity> xirongInvadeList = new ArrayList<WorldCity>();
        final List<WorldCity> dongyiInvadeList = new ArrayList<WorldCity>();
        this.barbarainInvadeCities.put(101, beidiInvadeList);
        this.barbarainInvadeCities.put(102, xirongInvadeList);
        this.barbarainInvadeCities.put(103, dongyiInvadeList);
        this.countryDistanceSetMap.put(1, new ArrayList<List<WorldCity>>());
        this.countryDistanceSetMap.put(2, new ArrayList<List<WorldCity>>());
        this.countryDistanceSetMap.put(3, new ArrayList<List<WorldCity>>());
        for (final WorldCity worldCity : this.list) {
            if (this.cityNames.contains(worldCity.getName())) {
                throw new RuntimeException("worldCityCache init fail in cities,two cities have the same name: " + worldCity.getName());
            }
            this.cityNames.add(worldCity.getName());
            if (!gSet.contains(worldCity.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + worldCity.getId() + "npc:" + worldCity.getChief());
            }
            if (!armySet.contains(worldCity.getChief())) {
                throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + worldCity.getId() + "npc:" + worldCity.getChief());
            }
            if (worldCity.getWeiDistance() <= 5) {
                weiSet.put(worldCity.getId(), worldCity.getWeiDistance());
                this.borderMap.put(worldCity.getId(), worldCity.getWeiDistance());
                if (worldCity.getWeiDistance() <= 5) {
                    weiList3.add(worldCity);
                }
            }
            if (worldCity.getShuDistance() <= 5) {
                shuSet.put(worldCity.getId(), worldCity.getShuDistance());
                this.borderMap.put(worldCity.getId(), worldCity.getShuDistance());
                if (worldCity.getShuDistance() <= 5) {
                    shuList3.add(worldCity);
                }
            }
            if (worldCity.getWuDistance() <= 5) {
                wuSet.put(worldCity.getId(), worldCity.getWuDistance());
                this.borderMap.put(worldCity.getId(), worldCity.getWuDistance());
                if (worldCity.getWuDistance() <= 5) {
                    wuList3.add(worldCity);
                }
            }
            List<WorldCity> weiList4 = this.cityWeiMap.get(worldCity.getWeiArea());
            List<WorldCity> shuList4 = this.cityShuMap.get(worldCity.getShuArea());
            List<WorldCity> wuList4 = this.cityWuMap.get(worldCity.getWuArea());
            if (weiList4 == null) {
                weiList4 = new ArrayList<WorldCity>();
                this.cityWeiMap.put(worldCity.getWeiArea(), weiList4);
            }
            if (shuList4 == null) {
                shuList4 = new ArrayList<WorldCity>();
                this.cityShuMap.put(worldCity.getShuArea(), shuList4);
            }
            if (wuList4 == null) {
                wuList4 = new ArrayList<WorldCity>();
                this.cityWuMap.put(worldCity.getWuArea(), wuList4);
            }
            weiList4.add(worldCity);
            shuList4.add(worldCity);
            wuList4.add(worldCity);
            if (worldCity.getNpcs() != null && !worldCity.getNpcs().trim().isEmpty()) {
                final String[] npcs = worldCity.getNpcs().split(";");
                final Integer[] armiesId = new Integer[npcs.length + 1];
                for (int i = 0; i < npcs.length; ++i) {
                    final int armyId = Integer.valueOf(npcs[i]);
                    armiesId[i] = armyId;
                    if (!gSet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table general wrong, id:" + worldCity.getId() + "npc:" + armyId);
                    }
                    if (!armySet.contains(armyId)) {
                        throw new RuntimeException("worldCityCache init fail in npcs, table army wrong, id:" + worldCity.getId() + "npc:" + armyId);
                    }
                }
                armiesId[armiesId.length - 1] = worldCity.getChief();
                worldCity.setArmiesId(armiesId);
            }
            if (worldCity.getShowMask() == 1) {
                this.showMaskSet.add(worldCity.getId());
            }
            final int troopId = gMap.get(worldCity.getChief()).getTroop();
            final Troop troop2 = troopMap.get(troopId);
            final int dropType = BattleDrop.getDropType(troop2.getDrop());
            if (dropType == 0) {
                throw new RuntimeException("worldCityCache init fail in chief, troop has no drop, id:" + worldCity.getId() + " troopId:" + troopId + " troopDrop:" + troop2.getDrop());
            }
            this.cityToDropTypeMap.put(worldCity.getId(), dropType);
            super.put((Object)worldCity.getId(), (Object)worldCity);
            final List<List<WorldCity>> weiDistanceSetMap = this.countryDistanceSetMap.get(1);
            final int weiDistance = worldCity.getWeiDistance();
            if (weiDistance > weiDistanceSetMap.size() - 1) {
                for (int j = weiDistanceSetMap.size(); j <= weiDistance; ++j) {
                    weiDistanceSetMap.add(new ArrayList<WorldCity>());
                }
            }
            final List<WorldCity> weiCitySet = weiDistanceSetMap.get(weiDistance);
            weiCitySet.add(worldCity);
            final List<List<WorldCity>> shuDistanceSetMap = this.countryDistanceSetMap.get(2);
            final int shuDistance = worldCity.getShuDistance();
            if (shuDistance > shuDistanceSetMap.size() - 1) {
                for (int k = shuDistanceSetMap.size(); k <= shuDistance; ++k) {
                    shuDistanceSetMap.add(new ArrayList<WorldCity>());
                }
            }
            final List<WorldCity> shuCitySet = shuDistanceSetMap.get(shuDistance);
            shuCitySet.add(worldCity);
            final List<List<WorldCity>> wuDistanceSetMap = this.countryDistanceSetMap.get(3);
            final int wuDistance = worldCity.getWuDistance();
            if (wuDistance > wuDistanceSetMap.size() - 1) {
                for (int l = wuDistanceSetMap.size(); l <= wuDistance; ++l) {
                    wuDistanceSetMap.add(new ArrayList<WorldCity>());
                }
            }
            final List<WorldCity> wuCitySet = wuDistanceSetMap.get(wuDistance);
            wuCitySet.add(worldCity);
        }
        for (final Map.Entry<Integer, List<List<WorldCity>>> entry1 : this.countryDistanceSetMap.entrySet()) {
            final int country = entry1.getKey();
            final List<List<WorldCity>> equalDistanceSetMap = entry1.getValue();
            final List<List<WorldCity>> lessThanDistanceSetMap = new ArrayList<List<WorldCity>>();
            this.countryLessThanDistanceSetMap.put(country, lessThanDistanceSetMap);
            if (equalDistanceSetMap.size() > this.maxDistance) {
                this.maxDistance = equalDistanceSetMap.size();
            }
            for (int distance = 0; distance < equalDistanceSetMap.size(); ++distance) {
                final List<WorldCity> equalList = equalDistanceSetMap.get(distance);
                final List<WorldCity> lessThanList = new ArrayList<WorldCity>();
                if (distance > 0) {
                    final List<WorldCity> lowerLessThanList = lessThanDistanceSetMap.get(distance - 1);
                    lessThanList.addAll(lowerLessThanList);
                }
                lessThanList.addAll(equalList);
                lessThanDistanceSetMap.add(distance, lessThanList);
            }
        }
        final Set<Integer> keySet = this.countryLessThanDistanceSetMap.keySet();
        for (Integer distance2 = 0; distance2 < this.maxDistance; ++distance2) {
            final ArrayList<WorldCity> lessThanList2 = new ArrayList<WorldCity>();
            for (final Integer country2 : keySet) {
                if (this.countryLessThanDistanceSetMap.get(country2).size() > distance2) {
                    final List<WorldCity> oneCountryLessThanList = this.countryLessThanDistanceSetMap.get(country2).get(distance2);
                    lessThanList2.addAll(oneCountryLessThanList);
                }
            }
            this.lessThanDistanceSetMap.add(distance2, lessThanList2);
        }
    }
    
    public void printMap() {
        final StringBuilder sb = new StringBuilder();
        sb.append("WorldCityCache").append("maxDistance-").append(this.maxDistance).append("\n");
        sb.append("countryDistanceSetMap").append("\n");
        for (final Map.Entry<Integer, List<List<WorldCity>>> entry : this.countryDistanceSetMap.entrySet()) {
            final int country = entry.getKey();
            final List<List<WorldCity>> distanceSetMap = entry.getValue();
            sb.append(country).append(":").append("\n");
            for (int distance = 0; distance < distanceSetMap.size(); ++distance) {
                final List<WorldCity> list = distanceSetMap.get(distance);
                sb.append(distance).append(":");
                for (final WorldCity wc : list) {
                    sb.append(wc.getName()).append("|");
                }
                sb.append("\n");
            }
        }
        sb.append("countryLessThanDistanceSetMap").append("\n");
        for (final Map.Entry<Integer, List<List<WorldCity>>> entry : this.countryLessThanDistanceSetMap.entrySet()) {
            final int country = entry.getKey();
            final List<List<WorldCity>> distanceSetMap = entry.getValue();
            sb.append(country).append(":").append("\n");
            for (int distance = 0; distance < distanceSetMap.size(); ++distance) {
                final List<WorldCity> list = distanceSetMap.get(distance);
                sb.append(distance).append(":");
                for (final WorldCity wc : list) {
                    sb.append(wc.getName()).append("|");
                }
                sb.append("\n");
            }
        }
        for (int distance2 = 0; distance2 < this.lessThanDistanceSetMap.size(); ++distance2) {
            final List<WorldCity> list2 = this.lessThanDistanceSetMap.get(distance2);
            sb.append(distance2).append(":");
            for (final WorldCity wc2 : list2) {
                sb.append(wc2.getName()).append("|");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
    
    public List<WorldCity> getWorldCityList() {
        return this.list;
    }
    
    public List<WorldCity> getCitySetByLessThanDistance(final int distance) {
        final List<WorldCity> citySet = this.lessThanDistanceSetMap.get(distance);
        if (citySet == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("citySet is null").append("distance", distance).appendMethodName("getCitySetByCountryAndLessThanDistance").appendClassName("getCitySetByLessThanDistance").flush();
        }
        return citySet;
    }
    
    public List<WorldCity> getCitySetByCountryAndLessThanDistance(final int country, final int distance) {
        final List<List<WorldCity>> distanceSetMap = this.countryLessThanDistanceSetMap.get(country);
        if (distanceSetMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("distanceSetMap is null").append("country", country).appendMethodName("getCitySetByCountryAndLessThanDistance").appendClassName("WorldCityCache").flush();
            return null;
        }
        final List<WorldCity> citySet = distanceSetMap.get(distance);
        if (citySet == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("citySet is null").append("distance", distance).appendMethodName("getCitySetByCountryAndLessThanDistance").appendClassName("WorldCityCache").flush();
            return null;
        }
        return citySet;
    }
    
    public List<WorldCity> getCitySetByCountryAndEqualDistance(final int country, final int distance) {
        final List<List<WorldCity>> distanceSetMap = this.countryDistanceSetMap.get(country);
        if (distanceSetMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("distanceSetMap is null").append("country", country).appendMethodName("getCitySetByCountryAndDistance").appendClassName("WorldCityCache").flush();
            return null;
        }
        final List<WorldCity> citySet = distanceSetMap.get(distance);
        if (citySet == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("citySet is null").append("distance", distance).appendMethodName("getCitySetByCountryAndDistance").appendClassName("WorldCityCache").flush();
            return null;
        }
        return citySet;
    }
    
    public List<WorldCity> getAreaCity(final int forceId, final int areaId) {
        if (forceId == 1) {
            return this.cityWeiMap.get(areaId);
        }
        if (forceId == 2) {
            return this.cityShuMap.get(areaId);
        }
        return this.cityWuMap.get(areaId);
    }
    
    public int getArea(final int forceId, final WorldCity worldCity) {
        if (forceId == 1) {
            return worldCity.getWeiArea();
        }
        if (forceId == 2) {
            return worldCity.getShuArea();
        }
        return worldCity.getWuArea();
    }
    
    public int getArea(final int forceId, final int cityId) {
        final WorldCity worldCity = (WorldCity)this.get((Object)cityId);
        return this.getArea(forceId, worldCity);
    }
    
    public WorldCity getCityIdByName(final String cityName) {
        for (final WorldCity wc : this.getModels()) {
            if (wc.getName().endsWith(cityName)) {
                return wc;
            }
        }
        return null;
    }
    
    public int getCityDropType(final int cityId) {
        return this.cityToDropTypeMap.get(cityId);
    }
    
    public Set<Integer> getMaskSet() {
        return this.showMaskSet;
    }
    
    public Map<Integer, Integer> getDistanceCities() {
        return this.borderMap;
    }
    
    public Map<Integer, Integer> getDistanceCities(final int forceId) {
        return this.distanceMap.get(forceId);
    }
    
    public Map<Integer, List<WorldCity>> getDistanceLess3Cities() {
        return this.distance3List;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.cityWeiMap.clear();
        this.cityShuMap.clear();
        this.cityWuMap.clear();
        this.cityToDropTypeMap.clear();
        this.distanceMap.clear();
        this.distance3List.clear();
        this.borderMap.clear();
        this.showMaskSet.clear();
        this.cityNames.clear();
        this.countryDistanceSetMap.clear();
        this.countryLessThanDistanceSetMap.clear();
        this.lessThanDistanceSetMap.clear();
    }
}
