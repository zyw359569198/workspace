package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("WnCitynpcLvCache")
public class WnCitynpcLvCache extends AbstractCache<Integer, WnCitynpcLv>
{
    private static final Logger errorLog;
    private List<WnCitynpcLv> resultList;
    @Autowired
    private SDataLoader dataLoader;
    
    static {
        errorLog = CommonLog.getLog(WnCitynpcLvCache.class);
    }
    
    public WnCitynpcLvCache() {
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
        this.resultList = this.dataLoader.getModels((Class)WnCitynpcLv.class);
        for (final WnCitynpcLv temp : this.resultList) {
            String[] sArray = temp.getWeiArmies().split(";");
            final Integer[] weiArmyId = new Integer[sArray.length];
            for (int i = 0; i < sArray.length; ++i) {
                try {
                    weiArmyId[i] = Integer.parseInt(sArray[i]);
                    if (!gSet.contains(weiArmyId[i])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WeiArmies, not exist in table general, ef_lv:" + temp.getLv() + ". invalid npc:" + weiArmyId[i]);
                    }
                    if (!armySet.contains(weiArmyId[i])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WeiArmies, not exist in table army, ef_lv:" + temp.getLv() + ". invalid npc:" + weiArmyId[i]);
                    }
                }
                catch (NumberFormatException e) {
                    throw new RuntimeException("WnCitynpcLvCache NumberFormatException of WeiArmies. ef_lv:" + temp.getLv());
                }
            }
            temp.setWeiArmyIds(weiArmyId);
            sArray = temp.getShuArmies().split(";");
            final Integer[] shuArmyId = new Integer[sArray.length];
            for (int j = 0; j < sArray.length; ++j) {
                try {
                    shuArmyId[j] = Integer.parseInt(sArray[j]);
                    if (!gSet.contains(shuArmyId[j])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in ShuArmies, not exist in table general, ef_lv:" + temp.getLv() + ". invalid npc:" + shuArmyId[j]);
                    }
                    if (!armySet.contains(shuArmyId[j])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in ShuArmies, not exist in table army, ef_lv:" + temp.getLv() + ". invalid npc:" + shuArmyId[j]);
                    }
                }
                catch (NumberFormatException e2) {
                    throw new RuntimeException("WnCitynpcLvCache NumberFormatException of WeiArmies. ef_lv:" + temp.getLv());
                }
            }
            temp.setShuArmyIds(shuArmyId);
            sArray = temp.getWuArmies().split(";");
            final Integer[] wuArmyId = new Integer[sArray.length];
            for (int k = 0; k < sArray.length; ++k) {
                try {
                    wuArmyId[k] = Integer.parseInt(sArray[k]);
                    if (!gSet.contains(wuArmyId[k])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WuArmies, not exist in table general, ef_lv:" + temp.getLv() + ". invalid npc:" + wuArmyId[k]);
                    }
                    if (!armySet.contains(wuArmyId[k])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WuArmies, not exist in table army, ef_lv:" + temp.getLv() + ". invalid npc:" + wuArmyId[k]);
                    }
                }
                catch (NumberFormatException e3) {
                    throw new RuntimeException("WnCitynpcLvCache NumberFormatException of WeiArmies. ef_lv:" + temp.getLv());
                }
            }
            temp.setWuArmyIds(wuArmyId);
            sArray = temp.getNdArmies().split(";");
            final Integer[] ndArmyId = new Integer[sArray.length];
            for (int l = 0; l < sArray.length; ++l) {
                try {
                    ndArmyId[l] = Integer.parseInt(sArray[l]);
                    if (!gSet.contains(ndArmyId[l])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WuArmies, not exist in table general, ef_lv:" + temp.getLv() + ". invalid npc:" + ndArmyId[l]);
                    }
                    if (!armySet.contains(ndArmyId[l])) {
                        throw new RuntimeException("WnCitynpcLvCache init fail in WuArmies, not exist in table army, ef_lv:" + temp.getLv() + ". invalid npc:" + ndArmyId[l]);
                    }
                }
                catch (NumberFormatException e4) {
                    throw new RuntimeException("WnCitynpcLvCache NumberFormatException of WeiArmies. ef_lv:" + temp.getLv());
                }
            }
            temp.setNdArmyIds(ndArmyId);
            super.put((Object)temp.getLv(), (Object)temp);
        }
        Collections.sort(this.resultList, new Comparator<WnCitynpcLv>() {
            @Override
            public int compare(final WnCitynpcLv wnCitynpcLv1, final WnCitynpcLv wnCitynpcLv2) {
                return wnCitynpcLv1.getDay() - wnCitynpcLv2.getDay();
            }
        });
    }
    
    public WnCitynpcLv getWnCitynpcLvByDay(final int day) {
        WnCitynpcLv result = null;
        for (final WnCitynpcLv temp : this.resultList) {
            if (day < temp.getDay()) {
                break;
            }
            result = temp;
        }
        if (result == null) {
            WnCitynpcLvCache.errorLog.error("class:WnCitynpcLvCache#method:WnCitynpcLv#day:" + day);
            return (WnCitynpcLv)this.get((Object)1);
        }
        return result;
    }
}
