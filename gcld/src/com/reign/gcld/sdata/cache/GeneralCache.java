package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.sdata.common.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.activity.common.*;

@Component("generalCache")
public class GeneralCache extends AbstractCache<Integer, General>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<General>> qualityCivilGeneralMap;
    private Map<Integer, List<General>> qualityMilitaryGeneralMap;
    private static List<String> name;
    
    static {
        GeneralCache.name = new ArrayList<String>();
    }
    
    public GeneralCache() {
        this.qualityCivilGeneralMap = new HashMap<Integer, List<General>>();
        this.qualityMilitaryGeneralMap = new HashMap<Integer, List<General>>();
    }
    
    public static List<String> getGeneralName() {
        return GeneralCache.name;
    }
    
    public List<General> getGeneralByQuality(final int quality, final int type) {
        List<General> resultList = new ArrayList<General>();
        if (type == 1) {
            resultList = this.qualityCivilGeneralMap.get(quality);
        }
        else if (type == 2) {
            resultList = this.qualityMilitaryGeneralMap.get(quality);
        }
        return resultList;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<General> resultList = this.dataLoader.getModels((Class)General.class);
        final List<Troop> troopList = this.dataLoader.getModels((Class)Troop.class);
        final Set<Integer> troopTypeSet = new HashSet<Integer>();
        final Set<Integer> troopIdSet = new HashSet<Integer>();
        for (final Troop troop : troopList) {
            troopTypeSet.add(troop.getType());
            troopIdSet.add(troop.getId());
        }
        for (final General general : resultList) {
            if ((general.getType() == 1 || general.getType() == 2) && general.getName().length() < 5) {
                GeneralCache.name.add(general.getName());
            }
            if (general.getType() == 2) {
                if (!troopTypeSet.contains(general.getTroop())) {
                    throw new RuntimeException("General init fail in npcs, id:" + general.getId() + " TroopType:" + general.getTroop());
                }
            }
            else if (general.getType() == 3 && !troopIdSet.contains(general.getTroop())) {
                throw new RuntimeException("General init fail in npcs, id:" + general.getId() + " TroopId:" + general.getTroop());
            }
            GeneralSpecialInfo gsi = new GeneralSpecialInfo(1, 0.0);
            if (!StringUtils.isBlank(general.getSp())) {
                final String[] spStrs = general.getSp().split(",");
                if (spStrs[0].equalsIgnoreCase("fs")) {
                    gsi = new GeneralSpecialInfo(2, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("mz")) {
                    if (spStrs.length < 3) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(3, Double.valueOf(spStrs[1]));
                    gsi.param2 = Double.valueOf(spStrs[2]);
                }
                else if (spStrs[0].equalsIgnoreCase("hy")) {
                    if (spStrs.length < 2) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(4, Double.valueOf(spStrs[1]));
                }
                else if (spStrs[0].equalsIgnoreCase("ft")) {
                    if (spStrs.length < 2) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(5, Double.valueOf(spStrs[1]));
                }
                else if (spStrs[0].equalsIgnoreCase("bs")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(6, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("zf")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(7, 2.0);
                }
                else if (spStrs[0].equalsIgnoreCase("rb")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(8, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("yx")) {
                    gsi = new GeneralSpecialInfo(9, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("td")) {
                    if (spStrs.length < 3) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi.generalType = 10;
                    gsi.rowNum = Integer.valueOf(spStrs[1]);
                    gsi.param2 = Double.valueOf(spStrs[2]);
                }
                else if (spStrs[0].equalsIgnoreCase("dx")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new GeneralSpecialInfo(11, 0.0);
                }
            }
            general.setGeneralSpecialInfo(gsi);
            super.put((Object)general.getId(), (Object)general);
            if (general.getType() == 1) {
                List<General> gList = this.qualityCivilGeneralMap.get(general.getQuality());
                if (gList == null) {
                    gList = new ArrayList<General>();
                    this.qualityCivilGeneralMap.put(general.getQuality(), gList);
                }
                gList.add(general);
            }
            else {
                if (general.getType() != 2) {
                    continue;
                }
                List<General> gList = this.qualityMilitaryGeneralMap.get(general.getQuality());
                if (gList == null) {
                    gList = new ArrayList<General>();
                    this.qualityMilitaryGeneralMap.put(general.getQuality(), gList);
                }
                gList.add(general);
            }
        }
        this.checkThisCache();
    }
    
    private void checkThisCache() {
        for (final Integer armyId : MiddleAutumnCache.checkMap.keySet()) {
            if (this.get((Object)armyId) == null) {
                throw new RuntimeException("General init fail, General is absent, id:" + armyId);
            }
        }
    }
    
    public General getGeneralByName(final String generalName) {
        for (final General g : this.getModels()) {
            if (g.getName().equals(generalName)) {
                return g;
            }
        }
        return null;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.qualityCivilGeneralMap.clear();
        this.qualityMilitaryGeneralMap.clear();
    }
}
