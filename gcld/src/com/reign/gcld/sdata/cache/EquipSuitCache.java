package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import java.io.*;

@Component("equipSuitCache")
public class EquipSuitCache extends AbstractCache<Integer, EquipSuit>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, String> itemToSuitName;
    private Map<Integer, List<EquipSuit>> qualityEquipMap;
    private List<Integer> levelList;
    private Map<Integer, Integer> intimacyMap;
    
    public EquipSuitCache() {
        this.itemToSuitName = new HashMap<Integer, String>();
        this.qualityEquipMap = new HashMap<Integer, List<EquipSuit>>();
        this.levelList = new ArrayList<Integer>();
        this.intimacyMap = new HashMap<Integer, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EquipSuit> list = this.dataLoader.getModels((Class)EquipSuit.class);
        for (final EquipSuit es : list) {
            super.put((Object)es.getId(), (Object)es);
            final String[] itemList = es.getEquipList().split(";");
            String[] array;
            for (int length = (array = itemList).length, i = 0; i < length; ++i) {
                final String item = array[i];
                this.itemToSuitName.put(Integer.parseInt(item), es.getName());
            }
            if (this.qualityEquipMap.containsKey(es.getMinChiefLv())) {
                if (!this.qualityEquipMap.get(es.getMinChiefLv()).contains(es)) {
                    this.qualityEquipMap.get(es.getMinChiefLv()).add(es);
                }
            }
            else {
                final List<EquipSuit> toAddList = new ArrayList<EquipSuit>();
                toAddList.add(es);
                this.qualityEquipMap.put(es.getMinChiefLv(), toAddList);
            }
            if (!this.levelList.contains((int)es.getMinChiefLv())) {
                this.levelList.add((int)es.getMinChiefLv());
            }
            if (es.getType() == 1) {
                this.intimacyMap.put(es.getMinChiefLv(), es.getMaxIntimacyLv());
            }
        }
        Collections.sort(this.levelList);
    }
    
    public int getMaxQuality(final int type, final int playerLv) {
        int result = 1;
        for (final EquipSuit es : this.getModels()) {
            if (es.getType() == type && es.getMinChiefLv() <= playerLv && es.getQuality() > result) {
                result = es.getQuality();
            }
        }
        return result;
    }
    
    public String getStateEquips(final int playerLv, final int quality, final int type) {
        final StringBuilder sb = new StringBuilder();
        for (final EquipSuit es : this.getModels()) {
            if (es.getType() == type && es.getMinChiefLv() <= playerLv && es.getQuality() == quality) {
                sb.append(es.getEquipList());
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    public String getUnstateEquips(final int playerLv, final int state, final int type) {
        final StringBuilder sb = new StringBuilder();
        for (final EquipSuit es : this.getModels()) {
            if (es.getType() == type && es.getMinChiefLv() <= playerLv) {
                if (es.getQuality() < state) {
                    sb.append(es.getEquipList());
                    sb.append(";");
                }
                if (state != 1 || es.getQuality() != state) {
                    continue;
                }
                sb.append(es.getEquipList());
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    public String getSuitName(final int itemId) {
        if (this.itemToSuitName.get(itemId) != null) {
            return this.itemToSuitName.get(itemId);
        }
        return "";
    }
    
    public Map<Integer, List<EquipSuit>> getQualityEquipMap() {
        return this.qualityEquipMap;
    }
    
    public void setQualityEquipMap(final Map<Integer, List<EquipSuit>> qualityEquipMap) {
        this.qualityEquipMap = qualityEquipMap;
    }
    
    public List<Integer> getLevelList() {
        return this.levelList;
    }
    
    public void setLevelList(final List<Integer> levelList) {
        this.levelList = levelList;
    }
    
    public String getEquipsByTypeNotAboveLv(final Integer playerLv, final int type) {
        final StringBuilder sb = new StringBuilder();
        for (final EquipSuit es : this.getModels()) {
            if (es.getType() == type && es.getMinChiefLv() <= playerLv) {
                sb.append(es.getEquipList());
                sb.append(";");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public int getNowMaxIntimacyLv(final Integer playerLv) {
        final List<Integer> lvList = new ArrayList<Integer>();
        lvList.addAll(this.intimacyMap.keySet());
        Collections.sort(lvList);
        for (int i = 0; i < lvList.size(); ++i) {
            Integer lv = lvList.get(i);
            if (lv != null && playerLv < lv && i >= 0) {
                lv = lvList.get(i - 1);
                return (this.intimacyMap.get(lv) == null) ? 0 : this.intimacyMap.get(lv);
            }
        }
        if (lvList != null && !lvList.isEmpty()) {
            final Integer maxLvInteger = lvList.get(lvList.size() - 1);
            final int maxLv = (maxLvInteger == null) ? 70 : maxLvInteger;
            if (playerLv >= maxLv) {
                return (this.intimacyMap.get(maxLv) == null) ? 15 : this.intimacyMap.get(maxLv);
            }
        }
        return 1;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.itemToSuitName.clear();
        this.qualityEquipMap.clear();
        this.levelList.clear();
        this.intimacyMap.clear();
    }
}
