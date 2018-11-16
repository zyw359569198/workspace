package com.reign.kf.match.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.kf.comm.param.match.*;
import org.apache.commons.lang.*;
import java.util.*;

@Component("generalCache")
public class GeneralCache extends AbstractCache<Integer, General>
{
    @Autowired
    private SDataLoader dataLoader;
    public static final int GENERAL_TYPE_CIVIL = 1;
    public static final int GENERAL_TYPE_MILITARY = 2;
    private Map<Integer, List<General>> qualityCivilGeneralMap;
    private Map<Integer, List<General>> qualityMilitaryGeneralMap;
    public static GeneralCache generalCache;
    
    static {
        GeneralCache.generalCache = null;
    }
    
    public GeneralCache() {
        this.qualityCivilGeneralMap = new HashMap<Integer, List<General>>();
        this.qualityMilitaryGeneralMap = new HashMap<Integer, List<General>>();
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
            if (general.getType() == 2) {
                if (!troopTypeSet.contains(general.getTroop())) {
                    throw new RuntimeException("General init fail in npcs, id:" + general.getId() + " TroopType:" + general.getTroop());
                }
            }
            else if (general.getType() == 3 && !troopIdSet.contains(general.getTroop())) {
                throw new RuntimeException("General init fail in npcs, id:" + general.getId() + " TroopId:" + general.getTroop());
            }
            KfSpecialGeneral gsi = new KfSpecialGeneral(1, 0.0);
            if (!StringUtils.isBlank(general.getSp())) {
                final String[] spStrs = general.getSp().split(",");
                if (spStrs[0].equalsIgnoreCase("fs")) {
                    gsi = new KfSpecialGeneral(2, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("mz")) {
                    if (spStrs.length < 2) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(3, Double.valueOf(spStrs[1]));
                }
                else if (spStrs[0].equalsIgnoreCase("hy")) {
                    if (spStrs.length < 2) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(4, Double.valueOf(spStrs[1]));
                }
                else if (spStrs[0].equalsIgnoreCase("ft")) {
                    if (spStrs.length < 2) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(5, Double.valueOf(spStrs[1]));
                }
                else if (spStrs[0].equalsIgnoreCase("bs")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(6, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("zf")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(7, 2.0);
                }
                else if (spStrs[0].equalsIgnoreCase("rb")) {
                    if (spStrs.length < 1) {
                        throw new RuntimeException("General init fail in GeneralSpecialInfo, id:" + general.getId() + " Sp:" + general.getSp());
                    }
                    gsi = new KfSpecialGeneral(8, 0.0);
                }
                else if (spStrs[0].equalsIgnoreCase("yx")) {
                    gsi = new KfSpecialGeneral(9, 0.0);
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
                    gsi = new KfSpecialGeneral(11, 0.0);
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
        GeneralCache.generalCache = this;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.qualityCivilGeneralMap.clear();
        this.qualityMilitaryGeneralMap.clear();
    }
    
    public static General getGeneralById(final Integer generalId) {
        return (General)GeneralCache.generalCache.get((Object)generalId);
    }
}
