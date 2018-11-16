package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.common.*;
import java.util.*;

@Component("KtSdmzSCache")
public class KtSdmzSCache extends AbstractCache<Integer, KtSdmzS>
{
    @Autowired
    private SDataLoader dataLoader;
    private Set<Integer> touFangCitySet;
    private int maxRound;
    private Map<Integer, KtSdmzSCacheItem> lvMap;
    
    public KtSdmzSCache() {
        this.touFangCitySet = new HashSet<Integer>();
        this.maxRound = 0;
        this.lvMap = new HashMap<Integer, KtSdmzSCacheItem>();
    }
    
    public int getMaxRound() {
        return this.maxRound;
    }
    
    public void setMaxRound(final int maxRound) {
        this.maxRound = maxRound;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final Set<Integer> citySet = new HashSet<Integer>();
        final List<WorldCity> cityList = this.dataLoader.getModels((Class)WorldCity.class);
        for (final WorldCity wc : cityList) {
            citySet.add(wc.getId());
        }
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
        final List<KtSdmzS> allList = this.dataLoader.getModels((Class)KtSdmzS.class);
        for (final KtSdmzS ktSdmzS : allList) {
            final String[] weiCityArray = ktSdmzS.getWei().split(";");
            final String[] shuCityArray = ktSdmzS.getShu().split(";");
            final String[] wuCityArray = ktSdmzS.getWu().split(";");
            int cityId = 0;
            try {
                String[] array;
                for (int length = (array = weiCityArray).length, l = 0; l < length; ++l) {
                    final String s = array[l];
                    cityId = Integer.parseInt(s);
                    ktSdmzS.getWeiSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtSdmzSCache init fail in parse weiCityArray. id=" + ktSdmzS.getId());
            }
            try {
                String[] array2;
                for (int length2 = (array2 = shuCityArray).length, n = 0; n < length2; ++n) {
                    final String s = array2[n];
                    cityId = Integer.parseInt(s);
                    ktSdmzS.getShuSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtSdmzSCache init fail in parse shuCityArray. id=" + ktSdmzS.getId());
            }
            try {
                String[] array3;
                for (int length3 = (array3 = wuCityArray).length, n2 = 0; n2 < length3; ++n2) {
                    final String s = array3[n2];
                    cityId = Integer.parseInt(s);
                    ktSdmzS.getWuSet().add(cityId);
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("KtSdmzSCache init fail in parse wuCityArray. id=" + ktSdmzS.getId());
            }
            String[] sArray = ktSdmzS.getWeiArmies().split(";");
            final Integer[] weiArmyId = new Integer[sArray.length];
            for (int i = 0; i < sArray.length; ++i) {
                try {
                    weiArmyId[i] = Integer.parseInt(sArray[i]);
                    if (!gSet.contains(weiArmyId[i])) {
                        throw new RuntimeException("KtSdmzSCache init fail in WeiArmies, not exist in table general, id:" + ktSdmzS.getId() + ". invalid npc:" + weiArmyId[i]);
                    }
                    if (!armySet.contains(weiArmyId[i])) {
                        throw new RuntimeException("KtSdmzSCache init fail in WeiArmies, not exist in table army, id:" + ktSdmzS.getId() + ". invalid npc:" + weiArmyId[i]);
                    }
                }
                catch (NumberFormatException e2) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + ktSdmzS.getId());
                }
            }
            ktSdmzS.setWeiArmyIds(weiArmyId);
            sArray = ktSdmzS.getShuArmies().split(";");
            final Integer[] shuArmyId = new Integer[sArray.length];
            for (int j = 0; j < sArray.length; ++j) {
                try {
                    shuArmyId[j] = Integer.parseInt(sArray[j]);
                    if (!gSet.contains(shuArmyId[j])) {
                        throw new RuntimeException("KtSdmzSCache init fail in ShuArmies, not exist in table general, id:" + ktSdmzS.getId() + ". invalid npc:" + shuArmyId[j]);
                    }
                    if (!armySet.contains(shuArmyId[j])) {
                        throw new RuntimeException("KtSdmzSCache init fail in ShuArmies, not exist in table army, id:" + ktSdmzS.getId() + ". invalid npc:" + shuArmyId[j]);
                    }
                }
                catch (NumberFormatException e3) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + ktSdmzS.getId());
                }
            }
            ktSdmzS.setShuArmyIds(shuArmyId);
            sArray = ktSdmzS.getWuArmies().split(";");
            final Integer[] wuArmyId = new Integer[sArray.length];
            for (int k = 0; k < sArray.length; ++k) {
                try {
                    wuArmyId[k] = Integer.parseInt(sArray[k]);
                    if (!gSet.contains(wuArmyId[k])) {
                        throw new RuntimeException("KtSdmzSCache init fail in WuArmies, not exist in table general, id:" + ktSdmzS.getId() + ". invalid npc:" + wuArmyId[k]);
                    }
                    if (!armySet.contains(wuArmyId[k])) {
                        throw new RuntimeException("KtSdmzSCache init fail in WuArmies, not exist in table army, id:" + ktSdmzS.getId() + ". invalid npc:" + wuArmyId[k]);
                    }
                }
                catch (NumberFormatException e4) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + ktSdmzS.getId());
                }
            }
            ktSdmzS.setWuArmyIds(wuArmyId);
            this.touFangCitySet.addAll(ktSdmzS.getWeiSet());
            this.touFangCitySet.addAll(ktSdmzS.getShuSet());
            this.touFangCitySet.addAll(ktSdmzS.getWuSet());
            KtSdmzSCacheItem ktSdmzSCacheItem = this.lvMap.get(ktSdmzS.getKindomLv());
            if (ktSdmzSCacheItem == null) {
                ktSdmzSCacheItem = new KtSdmzSCacheItem();
                this.lvMap.put(ktSdmzS.getKindomLv(), ktSdmzSCacheItem);
            }
            ktSdmzSCacheItem.list.add(ktSdmzS);
            ktSdmzSCacheItem.map.put(ktSdmzS.getIndex(), ktSdmzS);
            if (ktSdmzS.getIndex() > this.maxRound) {
                this.maxRound = ktSdmzS.getIndex();
            }
            super.put((Object)ktSdmzS.getId(), (Object)ktSdmzS);
        }
    }
    
    public KtSdmzS getKtSdmzSByRound(final int round) {
        if (round <= 0 || round > this.maxRound) {
            ErrorSceneLog.getInstance().appendErrorMsg("round is invalid").append("round", round).appendClassName("KtSdmzSCache").flush();
            return null;
        }
        final int maxCountryLv = WorldCityCommon.MAX_COUNTRY_LV;
        final KtSdmzSCacheItem ktSdmzSCacheItem = this.lvMap.get(maxCountryLv);
        if (ktSdmzSCacheItem == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("ktSdmzSCacheItem is null").append("maxCountryLv", maxCountryLv).appendClassName("KtSdmzSCache").flush();
            return null;
        }
        final KtSdmzS result = ktSdmzSCacheItem.map.get(round);
        if (result == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("result is null").append("maxCountryLv", maxCountryLv).append("round", round).appendClassName("KtSdmzSCache").flush();
            return null;
        }
        return result;
    }
    
    public Set<Integer> getCitySet(final int round, final int forceId) {
        final KtSdmzS item = this.getKtSdmzSByRound(round);
        if (item == null) {
            return null;
        }
        return item.getCitySet(forceId);
    }
    
    public Set<Integer> getTouFangCitySet() {
        return this.touFangCitySet;
    }
    
    public class KtSdmzSCacheItem
    {
        private List<KtSdmzS> list;
        Map<Integer, KtSdmzS> map;
        
        public KtSdmzSCacheItem() {
            this.list = new LinkedList<KtSdmzS>();
            this.map = new HashMap<Integer, KtSdmzS>();
        }
    }
}
