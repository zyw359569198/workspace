package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.service.*;
import com.reign.kf.match.log.*;
import com.reign.kf.match.sdata.common.*;
import java.util.*;
import com.reign.kfgz.battle.*;

@Component("troopCache")
public class TroopCache extends AbstractCache<Integer, Troop>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private IDataGetter dataGetter;
    private Map<Integer, List<Troop>> typeTroopMap;
    private Map<Integer, List<Troop>> lvTroopMap;
    private Logger logger;
    private static TroopCache tcSatic;
    private Logger errorLogger;
    
    static {
        TroopCache.tcSatic = new TroopCache();
    }
    
    public TroopCache() {
        this.typeTroopMap = new HashMap<Integer, List<Troop>>();
        this.lvTroopMap = new HashMap<Integer, List<Troop>>();
        this.logger = CommonLog.getLog(TroopCache.class);
        this.errorLogger = new ErrorLogger();
    }
    
    public static Troop getTroopCacheById(final int troopId) {
        return (Troop)TroopCache.tcSatic.get((Object)troopId);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Troop> resultList = this.dataLoader.getModels((Class)Troop.class);
        for (final Troop troop : resultList) {
            if (troop.getTerrainSpec() != null && !troop.getTerrainSpec().isEmpty()) {
                final String[] strs = troop.getTerrainSpec().split(";");
                String[] array;
                for (int length = (array = strs).length, n = 0; n < length; ++n) {
                    final String str = array[n];
                    final String[] terrains = str.split(",");
                    if (terrains.length < 5) {
                        this.logger.error("troopCache init fail in troopTerrain:" + troop.getTerrainSpec());
                    }
                    final TroopTerrain troopTerrain = new TroopTerrain();
                    troopTerrain.setTerrainId(Integer.valueOf(terrains[0]));
                    troopTerrain.setShow(Integer.valueOf(terrains[1]));
                    troopTerrain.setQuality(Integer.valueOf(terrains[2]));
                    troopTerrain.setAttEffect(Integer.valueOf(terrains[3]));
                    troopTerrain.setDefEffect(Integer.valueOf(terrains[4]));
                    troop.getTerrains().put(troopTerrain.getTerrainId(), troopTerrain);
                }
            }
            List<Troop> list = this.typeTroopMap.get(troop.getType());
            if (list == null) {
                list = new ArrayList<Troop>();
                this.typeTroopMap.put(troop.getType(), list);
            }
            list.add(troop);
            List<Troop> lvList = this.lvTroopMap.get(troop.getOpenLv());
            if (lvList == null) {
                lvList = new ArrayList<Troop>();
                this.lvTroopMap.put(troop.getOpenLv(), lvList);
            }
            lvList.add(troop);
            if (troop.getDrop() != null && !troop.getDrop().trim().isEmpty()) {
                final BattleDropAnd battleDropAnd = BattleDropFactory.getInstance().getTroopDropAnd(troop.getDrop());
                if (battleDropAnd != null) {
                    troop.setTroopDrop(battleDropAnd);
                }
            }
            final Map<Integer, int[]> strategyMap = new HashMap<Integer, int[]>();
            final String[] strs2 = troop.getTerrainStrategy().split(";");
            for (int i = 0; i < strs2.length; ++i) {
                final String[] ss = strs2[i].split("\\|");
                final String[] ssValues = ss[1].split(",");
                final int[] values = new int[ssValues.length];
                for (int j = 0; j < ssValues.length; ++j) {
                    values[j] = Integer.valueOf(ssValues[j]);
                }
                strategyMap.put(Integer.valueOf(ss[0]), values);
            }
            troop.setStrategyMap(strategyMap);
            final Map<Integer, int[]> strategyDefMap = new HashMap<Integer, int[]>();
            final String[] strsDef = troop.getTerrainStrategeDefense().split(";");
            for (int k = 0; k < strsDef.length; ++k) {
                final String[] ss2 = strsDef[k].split("\\|");
                final String[] ssValues2 = ss2[1].split(",");
                final int[] values2 = new int[ssValues2.length];
                for (int l = 0; l < ssValues2.length; ++l) {
                    values2[l] = Integer.valueOf(ssValues2[l]);
                }
                strategyDefMap.put(Integer.valueOf(ss2[0]), values2);
            }
            troop.setStrategyDefMap(strategyDefMap);
            if (troop.getTerrainStrategySpec() != null && !troop.getTerrainStrategySpec().isEmpty()) {
                final String[] tsss = troop.getTerrainStrategySpec().split(";");
                final List<TerrainStrategySpecDto> tsList = new ArrayList<TerrainStrategySpecDto>();
                String[] array2;
                for (int length2 = (array2 = tsss).length, n2 = 0; n2 < length2; ++n2) {
                    final String str2 = array2[n2];
                    final String[] tss = str2.split("\\|");
                    final TerrainStrategySpecDto ts = new TerrainStrategySpecDto();
                    ts.terrainId = Integer.valueOf(tss[0]);
                    ts.show = Integer.valueOf(tss[1]);
                    ts.strategyId = Integer.valueOf(tss[2]);
                    tsList.add(ts);
                }
                troop.setTsstList(tsList);
            }
            super.put((Object)troop.getId(), (Object)troop);
        }
        TroopCache.tcSatic = this;
    }
    
    public static Troop getTroop(final int troopType, final int techLv) {
        final List<Troop> resultList = TroopCache.tcSatic.typeTroopMap.get(troopType);
        if (resultList == null) {
            return (Troop)TroopCache.tcSatic.get((Object)1);
        }
        final int troopLvUp = techLv + 1;
        for (final Troop troop : resultList) {
            if (troop.getLevel() == troopLvUp) {
                return troop;
            }
        }
        return resultList.get(0);
    }
    
    public List<Troop> getOpenLvTroop(final int playerLv) {
        if (this.lvTroopMap.containsKey(playerLv)) {
            return this.lvTroopMap.get(playerLv);
        }
        return null;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeTroopMap.clear();
        this.lvTroopMap.clear();
    }
}
