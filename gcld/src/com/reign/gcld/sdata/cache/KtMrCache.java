package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.battle.common.*;

@Component("ktMrCache")
public class KtMrCache extends AbstractCache<Integer, KtMrTroop>
{
    @Autowired
    SDataLoader dataLoader;
    private Map<Integer, KtMrSpeed> speedMap;
    private Map<Integer, KtMrTarget> targetMap;
    private Map<Integer, List<KtMrTroop>> nationToWorkerMap;
    private Map<Integer, Integer> targetNK2IdMap;
    private Map<Integer, Integer> speedTC2IdMap;
    private Map<Integer, Integer> troopNTT2IdMap;
    private List<KtMrTroop> troopList;
    private int interValTime;
    private List<Integer> defaultTimeList;
    
    public KtMrCache() {
        this.speedMap = new HashMap<Integer, KtMrSpeed>();
        this.targetMap = new HashMap<Integer, KtMrTarget>();
        this.nationToWorkerMap = new HashMap<Integer, List<KtMrTroop>>();
        this.targetNK2IdMap = new HashMap<Integer, Integer>();
        this.speedTC2IdMap = new HashMap<Integer, Integer>();
        this.troopNTT2IdMap = new HashMap<Integer, Integer>();
        this.troopList = new ArrayList<KtMrTroop>();
        this.defaultTimeList = new ArrayList<Integer>();
    }
    
    public List<KtMrTroop> getTroopList() {
        return this.troopList;
    }
    
    public void setTroopList(final List<KtMrTroop> troopList) {
        this.troopList = troopList;
    }
    
    public int getInterValTime() {
        return this.interValTime;
    }
    
    public void setInterValTime(final int interValTime) {
        this.interValTime = interValTime;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtMrSpeed> speedList = this.dataLoader.getModels((Class)KtMrSpeed.class);
        for (final KtMrSpeed speed : speedList) {
            this.speedMap.put(speed.getId(), speed);
            final int transId = speed.getTroopType() * 10 + speed.getCityType();
            this.speedTC2IdMap.put(transId, speed.getId());
            if (speed.getTroopType() == 1 && speed.getCityType() == 1 && !this.defaultTimeList.contains(speed.getTime())) {
                this.defaultTimeList.add(speed.getTime());
            }
            if (speed.getTroopType() == 2 && speed.getCityType() == 1 && !this.defaultTimeList.contains(speed.getTime())) {
                this.defaultTimeList.add(speed.getTime());
            }
        }
        final List<KtMrTarget> targets = this.dataLoader.getModels((Class)KtMrTarget.class);
        for (final KtMrTarget target : targets) {
            this.targetMap.put(target.getId(), target);
            final int transId2 = target.getNation() * 100 + target.getPeriod() * 10 + target.getKindomLv();
            this.targetNK2IdMap.put(transId2, target.getId());
        }
        final List<KtMrTroop> troops = this.dataLoader.getModels((Class)KtMrTroop.class);
        for (final KtMrTroop troop : troops) {
            super.put((Object)troop.getId(), (Object)troop);
            List<KtMrTroop> nationTroops = this.nationToWorkerMap.get(troop.getNation());
            if (nationTroops == null) {
                nationTroops = new ArrayList<KtMrTroop>();
                this.nationToWorkerMap.put(troop.getNation(), nationTroops);
            }
            if (!nationTroops.contains(troop)) {
                nationTroops.add(troop);
            }
            final int transId3 = troop.getNation() * 100 + troop.getTroopId() * 10 + troop.getType();
            this.troopNTT2IdMap.put(transId3, troop.getId());
            this.interValTime = Math.max(troop.getInterval(), this.interValTime);
            this.troopList.add(troop);
        }
    }
    
    public KtMrTarget getTargetByNationAndLv(final int forceId, final int forceLv, final int period) {
        final int transId = forceId * 100 + period * 10 + forceLv;
        final Integer id = this.targetNK2IdMap.get(transId);
        if (id == null) {
            return null;
        }
        return this.targetMap.get(id);
    }
    
    public KtMrSpeed getSpeedByTroopAndCityType(final int troopType, final int cityType) {
        final int transId = troopType * 10 + cityType;
        final Integer id = this.speedTC2IdMap.get(transId);
        return (id == null) ? null : this.speedMap.get(id);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.speedMap.clear();
        this.targetMap.clear();
        this.targetNK2IdMap.clear();
        this.speedTC2IdMap.clear();
        this.troopNTT2IdMap.clear();
        this.defaultTimeList.clear();
    }
    
    public Tuple<Integer, Integer> getExtraReward(final int forceId, final Integer forceLv, final int serialPar) {
        final KtMrTarget target = this.getTargetByNationAndLv(forceId, forceLv, serialPar);
        if (target == null) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        result.left = target.getRewardExp();
        result.right = target.getRewardIron();
        return result;
    }
    
    public List<Integer> getDefaultTimeList() {
        return this.defaultTimeList;
    }
    
    public void setDefaultTimeList(final List<Integer> defaultTimeList) {
        this.defaultTimeList = defaultTimeList;
    }
    
    public int getTroopMaterialType(final int troopId) {
        try {
            final KtMrTroop troop = this.troopList.get(troopId - 1);
            if (troop == null) {
                return 1;
            }
            if (troop.getSoil() != 0) {
                return 1;
            }
            if (troop.getStone() != 0) {
                return 2;
            }
            if (troop.getLumber() != 0) {
                return 3;
            }
            return 1;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 1;
        }
    }
}
