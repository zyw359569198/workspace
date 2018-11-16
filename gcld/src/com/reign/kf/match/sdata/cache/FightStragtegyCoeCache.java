package com.reign.kf.match.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;

@Component("fightStragtegyCoeCache")
public class FightStragtegyCoeCache extends AbstractCache<Integer, FightStragtegyCoe>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, HashMap<Integer, FightStragtegyCoe>> fscDefMap;
    private Map<Integer, HashMap<Integer, FightStragtegyCoe>> fscAttMap;
    public static FightStragtegyCoeCache staticFightStragtegyCoeCache;
    
    static {
        FightStragtegyCoeCache.staticFightStragtegyCoeCache = new FightStragtegyCoeCache();
    }
    
    public FightStragtegyCoeCache() {
        this.fscDefMap = new HashMap<Integer, HashMap<Integer, FightStragtegyCoe>>();
        this.fscAttMap = new HashMap<Integer, HashMap<Integer, FightStragtegyCoe>>();
    }
    
    public static FightStragtegyCoe getFightStragtegyCoe(final int defSt, final int attSt) {
        final HashMap<Integer, FightStragtegyCoe> fMap = FightStragtegyCoeCache.staticFightStragtegyCoeCache.fscDefMap.get(defSt);
        return fMap.get(attSt);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FightStragtegyCoe> fsList = this.dataLoader.getModels((Class)FightStragtegyCoe.class);
        for (final FightStragtegyCoe temp : fsList) {
            super.put((Object)temp.getId(), (Object)temp);
            HashMap<Integer, FightStragtegyCoe> defFMap = this.fscDefMap.get(temp.getDefStrategy());
            if (defFMap == null) {
                defFMap = new HashMap<Integer, FightStragtegyCoe>();
                this.fscDefMap.put(temp.getDefStrategy(), defFMap);
            }
            defFMap.put(temp.getAttStrategy(), temp);
            HashMap<Integer, FightStragtegyCoe> attFMap = this.fscAttMap.get(temp.getAttStrategy());
            if (attFMap == null) {
                attFMap = new HashMap<Integer, FightStragtegyCoe>();
                this.fscAttMap.put(temp.getAttStrategy(), attFMap);
            }
            attFMap.put(temp.getDefStrategy(), temp);
        }
        final List<FightStrategies> list = this.dataLoader.getModels((Class)FightStrategies.class);
        final int[] fss = new int[list.size()];
        int i = 0;
        for (final FightStrategies fs : list) {
            fss[i++] = fs.getId();
        }
        for (final FightStrategies fs : list) {
            final HashMap<Integer, FightStragtegyCoe> fMap = this.fscDefMap.get(fs.getId());
            for (i = 0; i < fss.length; ++i) {
                final FightStragtegyCoe fstc = fMap.get(fss[i]);
                if (fstc == null) {
                    throw new RuntimeException("fightStragtegyCoeCache init fail, attStrategy id:" + fs.getId() + " " + fss[i]);
                }
            }
        }
        FightStragtegyCoeCache.staticFightStragtegyCoeCache = this;
    }
    
    public static FightStragtegyCoe getAttWin(final int defSt, final int[] attSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = FightStragtegyCoeCache.staticFightStragtegyCoeCache.fscDefMap.get(defSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < attSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(attSts[i]);
            if (temp.getWinerSide() == 1 && (fsc == null || temp.getDefLost() > fsc.getDefLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public static FightStragtegyCoe getDefWin(final int attSt, final int[] defSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = FightStragtegyCoeCache.staticFightStragtegyCoeCache.fscAttMap.get(attSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < defSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(defSts[i]);
            if (temp.getWinerSide() == 2 && (fsc == null || temp.getAttLost() > fsc.getAttLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public static FightStragtegyCoe getAttLose(final int defSt, final int[] attSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = FightStragtegyCoeCache.staticFightStragtegyCoeCache.fscDefMap.get(defSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < attSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(attSts[i]);
            if (temp.getWinerSide() == 2 && (fsc == null || temp.getAttLost() > fsc.getAttLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public static FightStragtegyCoe getDefLose(final int attSt, final int[] defSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = FightStragtegyCoeCache.staticFightStragtegyCoeCache.fscAttMap.get(attSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < defSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(defSts[i]);
            if (temp.getWinerSide() == 1 && (fsc == null || temp.getDefLost() > fsc.getDefLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.fscDefMap.clear();
        this.fscAttMap.clear();
    }
}
