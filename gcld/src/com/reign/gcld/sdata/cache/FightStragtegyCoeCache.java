package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("fightStragtegyCoeCache")
public class FightStragtegyCoeCache extends AbstractCache<Integer, FightStragtegyCoe>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, HashMap<Integer, FightStragtegyCoe>> fscDefMap;
    private Map<Integer, HashMap<Integer, FightStragtegyCoe>> fscAttMap;
    
    public FightStragtegyCoeCache() {
        this.fscDefMap = new HashMap<Integer, HashMap<Integer, FightStragtegyCoe>>();
        this.fscAttMap = new HashMap<Integer, HashMap<Integer, FightStragtegyCoe>>();
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
    }
    
    public FightStragtegyCoe getAttWin(final int defSt, final int[] attSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = this.fscDefMap.get(defSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < attSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(attSts[i]);
            if (temp.getWinerSide() == 1 && (fsc == null || temp.getDefLost() > fsc.getDefLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public FightStragtegyCoe getAttLose(final int defSt, final int[] attSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = this.fscDefMap.get(defSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < attSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(attSts[i]);
            if (temp.getWinerSide() == 2 && (fsc == null || temp.getAttLost() > fsc.getAttLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public FightStragtegyCoe getDefWin(final int attSt, final int[] defSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = this.fscAttMap.get(attSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < defSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(defSts[i]);
            if (temp.getWinerSide() == 2 && (fsc == null || temp.getAttLost() > fsc.getAttLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public FightStragtegyCoe getDefLose(final int attSt, final int[] defSts) {
        final HashMap<Integer, FightStragtegyCoe> fMap = this.fscAttMap.get(attSt);
        FightStragtegyCoe fsc = null;
        for (int i = 0; i < defSts.length; ++i) {
            final FightStragtegyCoe temp = fMap.get(defSts[i]);
            if (temp.getWinerSide() == 1 && (fsc == null || temp.getDefLost() > fsc.getDefLost())) {
                fsc = temp;
            }
        }
        return fsc;
    }
    
    public FightStragtegyCoe getFightStragtegyCoe(final int defSt, final int attSt) {
        final HashMap<Integer, FightStragtegyCoe> fMap = this.fscDefMap.get(defSt);
        if (fMap == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("fMap is null").append("defSt", defSt).append("attSt", attSt).appendClassName(this.getClass().getSimpleName()).appendMethodName("getFightStragtegyCoe").flush();
        }
        return fMap.get(attSt);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.fscDefMap.clear();
        this.fscAttMap.clear();
    }
}
