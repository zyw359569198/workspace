package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;

@Component("EfLvCache")
public class EfLvCache extends AbstractCache<Integer, EfLv>
{
    @Autowired
    private SDataLoader dataLoader;
    
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
        final List<EfLv> resultList = this.dataLoader.getModels((Class)EfLv.class);
        for (final EfLv eflv : resultList) {
            String[] sArray = eflv.getWeiArmies().split(";");
            final Integer[] weiArmyId = new Integer[sArray.length];
            for (int i = 0; i < sArray.length; ++i) {
                try {
                    weiArmyId[i] = Integer.parseInt(sArray[i]);
                    if (!gSet.contains(weiArmyId[i])) {
                        throw new RuntimeException("EfLvCache init fail in WeiArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiArmyId[i]);
                    }
                    if (!armySet.contains(weiArmyId[i])) {
                        throw new RuntimeException("EfLvCache init fail in WeiArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiArmyId[i]);
                    }
                }
                catch (NumberFormatException e) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWeiArmyIds(weiArmyId);
            sArray = eflv.getShuArmies().split(";");
            final Integer[] shuArmyId = new Integer[sArray.length];
            for (int j = 0; j < sArray.length; ++j) {
                try {
                    shuArmyId[j] = Integer.parseInt(sArray[j]);
                    if (!gSet.contains(shuArmyId[j])) {
                        throw new RuntimeException("EfLvCache init fail in ShuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuArmyId[j]);
                    }
                    if (!armySet.contains(shuArmyId[j])) {
                        throw new RuntimeException("EfLvCache init fail in ShuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuArmyId[j]);
                    }
                }
                catch (NumberFormatException e2) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setShuArmyIds(shuArmyId);
            sArray = eflv.getWuArmies().split(";");
            final Integer[] wuArmyId = new Integer[sArray.length];
            for (int k = 0; k < sArray.length; ++k) {
                try {
                    wuArmyId[k] = Integer.parseInt(sArray[k]);
                    if (!gSet.contains(wuArmyId[k])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuArmyId[k]);
                    }
                    if (!armySet.contains(wuArmyId[k])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuArmyId[k]);
                    }
                }
                catch (NumberFormatException e3) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWuArmyIds(wuArmyId);
            sArray = eflv.getWeiDefArmies().split(";");
            final Integer[] weiDefArmyIds = new Integer[sArray.length];
            for (int l = 0; l < sArray.length; ++l) {
                try {
                    weiDefArmyIds[l] = Integer.parseInt(sArray[l]);
                    if (!gSet.contains(weiDefArmyIds[l])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiDefArmyIds[l]);
                    }
                    if (!armySet.contains(weiDefArmyIds[l])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiDefArmyIds[l]);
                    }
                }
                catch (NumberFormatException e4) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWeiDefArmyIds(weiDefArmyIds);
            sArray = eflv.getShuDefArmies().split(";");
            final Integer[] shuDefArmyIds = new Integer[sArray.length];
            for (int m = 0; m < sArray.length; ++m) {
                try {
                    shuDefArmyIds[m] = Integer.parseInt(sArray[m]);
                    if (!gSet.contains(shuDefArmyIds[m])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuDefArmyIds[m]);
                    }
                    if (!armySet.contains(shuDefArmyIds[m])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuDefArmyIds[m]);
                    }
                }
                catch (NumberFormatException e5) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setShuDefArmyIds(shuDefArmyIds);
            sArray = eflv.getWuDefArmies().split(";");
            final Integer[] wuDefArmyIds = new Integer[sArray.length];
            for (int i2 = 0; i2 < sArray.length; ++i2) {
                try {
                    wuDefArmyIds[i2] = Integer.parseInt(sArray[i2]);
                    if (!gSet.contains(wuDefArmyIds[i2])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuDefArmyIds[i2]);
                    }
                    if (!armySet.contains(wuDefArmyIds[i2])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuDefArmyIds[i2]);
                    }
                }
                catch (NumberFormatException e6) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWuDefArmyIds(wuDefArmyIds);
            sArray = eflv.getWeiAttArmies().split(";");
            final Integer[] weiAttArmyIds = new Integer[sArray.length];
            for (int i3 = 0; i3 < sArray.length; ++i3) {
                try {
                    weiAttArmyIds[i3] = Integer.parseInt(sArray[i3]);
                    if (!gSet.contains(weiAttArmyIds[i3])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiAttArmyIds[i3]);
                    }
                    if (!armySet.contains(weiAttArmyIds[i3])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + weiAttArmyIds[i3]);
                    }
                }
                catch (NumberFormatException e7) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWeiAttArmyIds(weiAttArmyIds);
            sArray = eflv.getShuAttArmies().split(";");
            final Integer[] shuAttArmyIds = new Integer[sArray.length];
            for (int i4 = 0; i4 < sArray.length; ++i4) {
                try {
                    shuAttArmyIds[i4] = Integer.parseInt(sArray[i4]);
                    if (!gSet.contains(shuAttArmyIds[i4])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuAttArmyIds[i4]);
                    }
                    if (!armySet.contains(shuAttArmyIds[i4])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + shuAttArmyIds[i4]);
                    }
                }
                catch (NumberFormatException e8) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setShuAttArmyIds(shuAttArmyIds);
            sArray = eflv.getWuAttArmies().split(";");
            final Integer[] wuAttArmyIds = new Integer[sArray.length];
            for (int i5 = 0; i5 < sArray.length; ++i5) {
                try {
                    wuAttArmyIds[i5] = Integer.parseInt(sArray[i5]);
                    if (!gSet.contains(wuAttArmyIds[i5])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuAttArmyIds[i5]);
                    }
                    if (!armySet.contains(wuAttArmyIds[i5])) {
                        throw new RuntimeException("EfLvCache init fail in WuArmies, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + wuAttArmyIds[i5]);
                    }
                }
                catch (NumberFormatException e9) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setWuAttArmyIds(wuAttArmyIds);
            final String[] probArray = eflv.getTicketProb().split(";");
            if (probArray.length != 2) {
                throw new RuntimeException("TicketProb size is not 2. ef_lv:" + eflv.getEfLv());
            }
            final Double[] ticketProbArray = new Double[probArray.length];
            for (int i6 = 0; i6 < probArray.length; ++i6) {
                try {
                    ticketProbArray[i6] = Double.parseDouble(probArray[i6]);
                }
                catch (NumberFormatException e10) {
                    throw new RuntimeException("NumberFormatException of WeiArmies. ef_lv:" + eflv.getEfLv());
                }
                if (ticketProbArray[i6] < 0.0 || ticketProbArray[i6] > 1.0) {
                    throw new RuntimeException("EfLvCache init fail in TicketProb, ef_lv:" + eflv.getEfLv());
                }
            }
            eflv.setTicketProbArray(ticketProbArray);
            final String[] ticketArmyArray = eflv.getTicketArmy().split(";");
            final Integer[] ticketArmyIds = new Integer[ticketArmyArray.length];
            for (int i7 = 0; i7 < ticketArmyArray.length; ++i7) {
                try {
                    ticketArmyIds[i7] = Integer.parseInt(ticketArmyArray[i7]);
                }
                catch (NumberFormatException e11) {
                    throw new RuntimeException("NumberFormatException of TicketArmy. ef_lv:" + eflv.getEfLv());
                }
                if (!gSet.contains(ticketArmyIds[i7])) {
                    throw new RuntimeException("EfLvCache init fail in TicketArmy, not exist in table general, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + ticketArmyIds[i7]);
                }
                if (!armySet.contains(ticketArmyIds[i7])) {
                    throw new RuntimeException("EfLvCache init fail in TicketArmy, not exist in table army, ef_lv:" + eflv.getEfLv() + ". invalid npc:" + ticketArmyIds[i7]);
                }
            }
            eflv.setTicketArmyIds(ticketArmyIds);
            super.put((Object)eflv.getEfLv(), (Object)eflv);
        }
    }
}
