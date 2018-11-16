package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.battle.common.*;

@Component("WorldPaidBCache")
public class WorldPaidBCache extends AbstractCache<Integer, WorldPaidB>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<WorldPaidB>> countryLvQinMiDuMap;
    private Map<Integer, Integer> lvMaxQmdMap;
    private List<WorldPaidB> resultList;
    
    public WorldPaidBCache() {
        this.countryLvQinMiDuMap = new HashMap<Integer, List<WorldPaidB>>();
        this.lvMaxQmdMap = new HashMap<Integer, Integer>();
        this.resultList = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<General> gList = this.dataLoader.getModels((Class)General.class);
        final Set<Integer> gSet = new HashSet<Integer>();
        for (final General general : gList) {
            gSet.add(general.getId());
        }
        final List<Army> armyList = this.dataLoader.getModels((Class)Army.class);
        final Set<Integer> armySet = new HashSet<Integer>();
        for (final Army army : armyList) {
            armySet.add(army.getGeneralId());
        }
        final Set<Integer> citySet = new HashSet<Integer>();
        final List<WorldCity> list = this.dataLoader.getModels((Class)WorldCity.class);
        for (final WorldCity wc : list) {
            citySet.add(wc.getId());
        }
        this.resultList = this.dataLoader.getModels((Class)WorldPaidB.class);
        for (final WorldPaidB worldPaidB : this.resultList) {
            String[] sArray = worldPaidB.getArmisWei().split(";");
            final Integer[] weiArmyId = new Integer[sArray.length];
            for (int i = 0; i < sArray.length; ++i) {
                try {
                    weiArmyId[i] = Integer.parseInt(sArray[i]);
                }
                catch (NumberFormatException e) {
                    throw new RuntimeException("WorldPaidBCache of WeiArmies. id:" + worldPaidB.getId());
                }
                if (!gSet.contains(weiArmyId[i])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table general, id:" + worldPaidB.getId() + ". invalid npc:" + weiArmyId[i]);
                }
                if (!armySet.contains(weiArmyId[i])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table army, id:" + worldPaidB.getId() + ". invalid npc:" + weiArmyId[i]);
                }
            }
            worldPaidB.setWeiArmyIds(weiArmyId);
            sArray = worldPaidB.getArmisShu().split(";");
            final Integer[] shuArmyId = new Integer[sArray.length];
            for (int j = 0; j < sArray.length; ++j) {
                try {
                    shuArmyId[j] = Integer.parseInt(sArray[j]);
                }
                catch (NumberFormatException e2) {
                    throw new RuntimeException("WorldPaidBCache of WeiArmies. id:" + worldPaidB.getId());
                }
                if (!gSet.contains(shuArmyId[j])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table general, id:" + worldPaidB.getId() + ". invalid npc:" + shuArmyId[j]);
                }
                if (!armySet.contains(shuArmyId[j])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table army, id:" + worldPaidB.getId() + ". invalid npc:" + shuArmyId[j]);
                }
            }
            worldPaidB.setShuArmyIds(shuArmyId);
            sArray = worldPaidB.getArmisWu().split(";");
            final Integer[] wuArmyId = new Integer[sArray.length];
            for (int k = 0; k < sArray.length; ++k) {
                try {
                    wuArmyId[k] = Integer.parseInt(sArray[k]);
                }
                catch (NumberFormatException e3) {
                    throw new RuntimeException("WorldPaidBCache of WeiArmies. id:" + worldPaidB.getId());
                }
                if (!gSet.contains(wuArmyId[k])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table general, id:" + worldPaidB.getId() + ". invalid npc:" + wuArmyId[k]);
                }
                if (!armySet.contains(wuArmyId[k])) {
                    throw new RuntimeException("WorldPaidBCache init fail in WeiArmies, not exist in table army, id:" + worldPaidB.getId() + ". invalid npc:" + wuArmyId[k]);
                }
            }
            worldPaidB.setWuArmyIds(wuArmyId);
            worldPaidB.setWeiR1CityIds(this.parseOneLine(worldPaidB.getWeiR1().trim(), worldPaidB.getId(), "WeiR1", citySet));
            worldPaidB.setWeiR2CityIds(this.parseOneLine(worldPaidB.getWeiR2().trim(), worldPaidB.getId(), "WeiR2", citySet));
            worldPaidB.setWeiR3CityIds(this.parseOneLine(worldPaidB.getWeiR3().trim(), worldPaidB.getId(), "WeiR3", citySet));
            worldPaidB.setShuR1CityIds(this.parseOneLine(worldPaidB.getShuR1().trim(), worldPaidB.getId(), "ShuR1", citySet));
            worldPaidB.setShuR2CityIds(this.parseOneLine(worldPaidB.getShuR2().trim(), worldPaidB.getId(), "ShuR2", citySet));
            worldPaidB.setShuR3CityIds(this.parseOneLine(worldPaidB.getShuR3().trim(), worldPaidB.getId(), "ShuR3", citySet));
            worldPaidB.setWuR1CityIds(this.parseOneLine(worldPaidB.getWuR1().trim(), worldPaidB.getId(), "WuR1", citySet));
            worldPaidB.setWuR2CityIds(this.parseOneLine(worldPaidB.getWuR2().trim(), worldPaidB.getId(), "WuR2", citySet));
            worldPaidB.setWuR3CityIds(this.parseOneLine(worldPaidB.getWuR3().trim(), worldPaidB.getId(), "WuR3", citySet));
            List<WorldPaidB> lvlist = this.countryLvQinMiDuMap.get(worldPaidB.getKl());
            if (lvlist == null) {
                lvlist = new LinkedList<WorldPaidB>();
                this.countryLvQinMiDuMap.put(worldPaidB.getKl(), lvlist);
            }
            lvlist.add(worldPaidB);
            final Integer key = worldPaidB.getKl();
            final Integer maxQmd = this.lvMaxQmdMap.get(key);
            if (maxQmd == null) {
                this.lvMaxQmdMap.put(key, worldPaidB.getQ());
            }
            else {
                final Integer value = worldPaidB.getQ();
                if (value > maxQmd) {
                    this.lvMaxQmdMap.put(key, value);
                }
            }
            super.put((Object)worldPaidB.getId(), (Object)worldPaidB);
        }
    }
    
    private Integer[] parseOneLine(final String lineString, final int worldPaidBId, final String infoString, final Set<Integer> citySet) {
        if (lineString != null && !lineString.trim().isEmpty()) {
            final String[] array = lineString.split(";");
            final Integer[] cityIds = new Integer[array.length];
            for (int i = 0; i < array.length; ++i) {
                final String s = array[i];
                int cityId = 0;
                try {
                    cityId = Integer.parseInt(s);
                }
                catch (Exception e) {
                    throw new RuntimeException("WorldPaidBCache parse " + infoString + " error. id:" + worldPaidBId + " s:" + s);
                }
                if (i == 0 && !WorldCityCommon.barbarainCitySet.contains(cityId)) {
                    throw new RuntimeException("WorldPaidBCache parse " + infoString + " error. line must begin from barbarain Main cityId. Id:" + worldPaidBId + ". cityId:" + cityId);
                }
                if (WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
                    throw new RuntimeException("EfLCache parse line error. " + infoString + " line contains Main cityId. Id:" + worldPaidBId + ". cityId:" + cityId);
                }
                if (!citySet.contains(cityId)) {
                    throw new RuntimeException("WorldPaidBCache parse " + infoString + " error. line contains invalid cityId. Id:" + worldPaidBId + ". cityId:" + cityId);
                }
                cityIds[i] = cityId;
            }
            return cityIds;
        }
        return null;
    }
    
    public WorldPaidB getWorldPaidBByCountryLvAndQmd(final int qmd) {
        final int countryLv = WorldCityCommon.MAX_COUNTRY_LV;
        WorldPaidB result = null;
        for (final WorldPaidB temp : this.resultList) {
            if (qmd >= temp.getQ() && countryLv >= temp.getKl()) {
                result = temp;
            }
        }
        if (result == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("WorldPaidB is null").append("countryLv", countryLv).append("qmd", qmd).appendMethodName("getWorldPaidBByCountryLvAndQmd").appendClassName("WorldPaidBCache").flush();
        }
        return result;
    }
    
    public int getMaxQmdByKl(int countryLv) {
        countryLv = WorldCityCommon.MAX_COUNTRY_LV;
        final Integer value = this.lvMaxQmdMap.get(countryLv);
        if (value == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("null from lvMaxQmdMap").append("countryLv", countryLv).appendClassName("WorldPaidBCache").appendMethodName("getMaxQmdByKl").flush();
        }
        return value;
    }
}
