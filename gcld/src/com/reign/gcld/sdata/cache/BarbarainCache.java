package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("barbarainCache")
public class BarbarainCache extends AbstractCache<Integer, Barbarain>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Barbarain> degreeMap;
    
    public BarbarainCache() {
        this.degreeMap = new HashMap<Integer, Barbarain>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Barbarain> resultList = this.dataLoader.getModels((Class)Barbarain.class);
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
        for (final Barbarain barbarain : resultList) {
            this.degreeMap.put(barbarain.getDegree(), barbarain);
            final BattleDrop battleDrop = BattleDropFactory.getInstance().getBattleDrop(barbarain.getReward());
            barbarain.setBattleDrop(battleDrop);
            String[] sArray = barbarain.getWeiArmies().split(";");
            final Integer[] weiArmyId = new Integer[sArray.length];
            for (int i = 0; i < sArray.length; ++i) {
                try {
                    weiArmyId[i] = Integer.parseInt(sArray[i]);
                    if (!gSet.contains(weiArmyId[i])) {
                        throw new RuntimeException("BarbarainCache init fail in WeiArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + weiArmyId[i]);
                    }
                    if (!armySet.contains(weiArmyId[i])) {
                        throw new RuntimeException("BarbarainCache init fail in WeiArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + weiArmyId[i]);
                    }
                }
                catch (NumberFormatException e) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setWeiArmyIds(weiArmyId);
            sArray = barbarain.getShuArmies().split(";");
            final Integer[] shuArmyId = new Integer[sArray.length];
            for (int j = 0; j < sArray.length; ++j) {
                try {
                    shuArmyId[j] = Integer.parseInt(sArray[j]);
                    if (!gSet.contains(shuArmyId[j])) {
                        throw new RuntimeException("BarbarainCache init fail in ShuArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + shuArmyId[j]);
                    }
                    if (!armySet.contains(shuArmyId[j])) {
                        throw new RuntimeException("BarbarainCache init fail in ShuArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + shuArmyId[j]);
                    }
                }
                catch (NumberFormatException e2) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setShuArmyIds(shuArmyId);
            sArray = barbarain.getWuArmies().split(";");
            final Integer[] wuArmyId = new Integer[sArray.length];
            for (int k = 0; k < sArray.length; ++k) {
                try {
                    wuArmyId[k] = Integer.parseInt(sArray[k]);
                    if (!gSet.contains(wuArmyId[k])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + wuArmyId[k]);
                    }
                    if (!armySet.contains(wuArmyId[k])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + wuArmyId[k]);
                    }
                }
                catch (NumberFormatException e3) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setWuArmyIds(wuArmyId);
            sArray = barbarain.getWeiIArmies().split(";");
            final Integer[] weiIArmyId = new Integer[sArray.length];
            for (int l = 0; l < sArray.length; ++l) {
                try {
                    weiIArmyId[l] = Integer.parseInt(sArray[l]);
                    if (!gSet.contains(weiIArmyId[l])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + weiIArmyId[l]);
                    }
                    if (!armySet.contains(weiIArmyId[l])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + weiIArmyId[l]);
                    }
                }
                catch (NumberFormatException e4) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setWeiIArmyIds(weiIArmyId);
            sArray = barbarain.getShuIArmies().split(";");
            final Integer[] shuIArmyId = new Integer[sArray.length];
            for (int m = 0; m < sArray.length; ++m) {
                try {
                    shuIArmyId[m] = Integer.parseInt(sArray[m]);
                    if (!gSet.contains(shuIArmyId[m])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + shuIArmyId[m]);
                    }
                    if (!armySet.contains(shuIArmyId[m])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + shuIArmyId[m]);
                    }
                }
                catch (NumberFormatException e5) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setShuIArmyIds(shuIArmyId);
            sArray = barbarain.getWuIArmies().split(";");
            final Integer[] wuIArmyId = new Integer[sArray.length];
            for (int i2 = 0; i2 < sArray.length; ++i2) {
                try {
                    wuIArmyId[i2] = Integer.parseInt(sArray[i2]);
                    if (!gSet.contains(wuIArmyId[i2])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + wuIArmyId[i2]);
                    }
                    if (!armySet.contains(wuIArmyId[i2])) {
                        throw new RuntimeException("BarbarainCache init fail in WuArmies, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + wuIArmyId[i2]);
                    }
                }
                catch (NumberFormatException e6) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. id:" + barbarain.getId());
                }
            }
            barbarain.setWuIArmyIds(wuIArmyId);
            sArray = barbarain.getRewardArmy().split(";");
            final Integer[] rewardArmyIds = new Integer[sArray.length];
            for (int i3 = 0; i3 < sArray.length; ++i3) {
                try {
                    rewardArmyIds[i3] = Integer.parseInt(sArray[i3]);
                    if (!gSet.contains(rewardArmyIds[i3])) {
                        throw new RuntimeException("BarbarainCache init fail in RewardArmy, not exist in table general, id:" + barbarain.getId() + ". invalid npc:" + rewardArmyIds[i3]);
                    }
                    if (!armySet.contains(rewardArmyIds[i3])) {
                        throw new RuntimeException("BarbarainCache init fail in RewardArmy, not exist in table army, id:" + barbarain.getId() + ". invalid npc:" + rewardArmyIds[i3]);
                    }
                }
                catch (NumberFormatException e7) {
                    throw new RuntimeException("NumberFormatException of RewardArmy. id:" + barbarain.getId());
                }
            }
            barbarain.setRewardArmyIds(rewardArmyIds);
            super.put((Object)barbarain.getId(), (Object)barbarain);
        }
    }
    
    public Barbarain getByDegree(final int degree) {
        return this.degreeMap.get(degree);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.degreeMap.clear();
    }
}
