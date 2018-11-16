package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.battle.common.*;

@Component("ktNfCache")
public class KtNfCache extends AbstractCache<Integer, KtNfTroop>
{
    @Autowired
    SDataLoader dataLoader;
    private Map<Integer, KtMrSpeed> speedMap;
    private Map<Integer, KtMrTarget> targetMap;
    private Map<Integer, List<KtMrTroop>> nationToWorkerMap;
    private Map<Integer, Integer> targetNK2IdMap;
    private Map<Integer, Integer> speedTC2IdMap;
    private Map<Integer, Integer> troopNTT2IdMap;
    private List<Integer> defaultTimeList;
    private List<KtMrTroop> troopList;
    private int interValTime;
    
    public KtNfCache() {
        this.speedMap = new HashMap<Integer, KtMrSpeed>();
        this.targetMap = new HashMap<Integer, KtMrTarget>();
        this.nationToWorkerMap = new HashMap<Integer, List<KtMrTroop>>();
        this.targetNK2IdMap = new HashMap<Integer, Integer>();
        this.speedTC2IdMap = new HashMap<Integer, Integer>();
        this.troopNTT2IdMap = new HashMap<Integer, Integer>();
        this.defaultTimeList = new ArrayList<Integer>();
        this.troopList = new ArrayList<KtMrTroop>();
    }
    
    public List<Integer> getDefaultTimeList() {
        return this.defaultTimeList;
    }
    
    public void setDefaultTimeList(final List<Integer> defaultTimeList) {
        this.defaultTimeList = defaultTimeList;
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
        final List<KtNfSpeed> speedList = this.dataLoader.getModels((Class)KtNfSpeed.class);
        KtMrSpeed tmp = null;
        for (final KtNfSpeed speed : speedList) {
            tmp = new KtMrSpeed();
            this.doCopy(tmp, speed);
            this.speedMap.put(speed.getId(), tmp);
            final int transId = speed.getTroopType() * 10 + speed.getCityType();
            this.speedTC2IdMap.put(transId, speed.getId());
            if (speed.getTroopType() == 1 && speed.getCityType() == 1 && !this.defaultTimeList.contains(speed.getTime())) {
                this.defaultTimeList.add(speed.getTime());
            }
            if (speed.getTroopType() == 2 && speed.getCityType() == 1 && !this.defaultTimeList.contains(speed.getTime())) {
                this.defaultTimeList.add(speed.getTime());
            }
        }
        final List<KtNfTarget> targets = this.dataLoader.getModels((Class)KtNfTarget.class);
        KtMrTarget tmpT = null;
        for (final KtNfTarget target : targets) {
            tmpT = new KtMrTarget();
            this.doCopy(tmpT, target);
            this.targetMap.put(target.getId(), tmpT);
            final int transId2 = target.getNation() * 100 + target.getPeriod() * 10 + target.getKindomLv();
            this.targetNK2IdMap.put(transId2, target.getId());
        }
        final List<KtNfTroop> troops = this.dataLoader.getModels((Class)KtNfTroop.class);
        KtMrTroop tempT = null;
        for (final KtNfTroop troop : troops) {
            tempT = new KtMrTroop();
            this.doCopy(tempT, troop);
            super.put((Object)troop.getId(), (Object)troop);
            List<KtMrTroop> nationTroops = this.nationToWorkerMap.get(troop.getNation());
            if (nationTroops == null) {
                nationTroops = new ArrayList<KtMrTroop>();
                this.nationToWorkerMap.put(troop.getNation(), nationTroops);
            }
            if (!nationTroops.contains(troop)) {
                nationTroops.add(tempT);
            }
            final int transId3 = troop.getNation() * 100 + troop.getTroopId() * 10 + troop.getType();
            this.troopNTT2IdMap.put(transId3, troop.getId());
            final int time = (troop.getInterval() == null) ? 0 : troop.getInterval();
            this.interValTime = Math.max(time, this.interValTime);
            this.troopList.add(tempT);
        }
    }
    
    private void doCopy(final KtMrTroop tempT, final KtNfTroop troop) {
        tempT.setId(troop.getId());
        tempT.setInterval(troop.getInterval());
        tempT.setLumber(troop.getLumber());
        tempT.setName(troop.getName());
        tempT.setNation(troop.getNation());
        tempT.setPath(troop.getPath());
        tempT.setSoil(troop.getSoil());
        tempT.setStone(troop.getStone());
        tempT.setTroopId(troop.getTroopId());
        tempT.setType(troop.getType());
    }
    
    private void doCopy(final KtMrTarget tmpT, final KtNfTarget target) {
        tmpT.setId(target.getId());
        tmpT.setKindomLv(target.getKindomLv());
        tmpT.setLumber(target.getLumber());
        tmpT.setNation(target.getNation());
        tmpT.setPeriod(target.getNation());
        tmpT.setRewardExp(target.getRewardExp());
        tmpT.setRewardIron(target.getRewardIron());
        tmpT.setSoil(target.getSoil());
        tmpT.setStone(target.getStone());
    }
    
    private void doCopy(final KtMrSpeed tmp, final KtNfSpeed speed) {
        tmp.setCityType(speed.getCityType());
        tmp.setId(speed.getId());
        tmp.setTime(speed.getTime());
        tmp.setTroopType(speed.getTroopType());
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
    
    public KtMrTroop getTroop(final int id) {
        final KtMrTroop tmp = new KtMrTroop();
        final KtNfTroop troop = (KtNfTroop)super.get((Object)id);
        this.doCopy(tmp, troop);
        return tmp;
    }
}
