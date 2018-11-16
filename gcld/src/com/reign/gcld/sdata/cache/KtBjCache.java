package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.util.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.battle.common.*;

@Component("ktBjCache")
public class KtBjCache extends AbstractCache<Integer, KtBjS>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Integer> ironMap;
    private Map<Integer, Integer> expMap;
    private List<Integer> indexLit;
    private Map<Integer, Set<Integer>> pitchCitiesMap;
    private int[][] idArray;
    static int length;
    private int legionStartMinutes;
    
    static {
        KtBjCache.length = 20;
    }
    
    public KtBjCache() {
        this.ironMap = new HashMap<Integer, Integer>();
        this.expMap = new HashMap<Integer, Integer>();
        this.indexLit = new ArrayList<Integer>();
        this.pitchCitiesMap = new HashMap<Integer, Set<Integer>>();
        this.idArray = null;
        this.legionStartMinutes = 0;
    }
    
    public int getLegionStartMinutes() {
        return this.legionStartMinutes;
    }
    
    public void setLegionStartMinutes(final int legionStartMinutes) {
        this.legionStartMinutes = legionStartMinutes;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtBjS> list = this.dataLoader.getModels((Class)KtBjS.class);
        int iron = 0;
        int exp = 0;
        Set<Integer> weiCities = null;
        Set<Integer> shuCities = null;
        Set<Integer> wuCities = null;
        final Set<Integer> weiInitCities = new HashSet<Integer>();
        final Set<Integer> shuInitCities = new HashSet<Integer>();
        final Set<Integer> wuInitCities = new HashSet<Integer>();
        final int kingdomlv = 1;
        this.idArray = new int[KtBjCache.length][KtBjCache.length];
        for (final KtBjS ktBjS : list) {
            if (ktBjS == null) {
                continue;
            }
            final int id = ktBjS.getId();
            final String weiString = ktBjS.getWei();
            final String shuString = ktBjS.getShu();
            final String wuString = ktBjS.getWu();
            weiCities = SymbolUtil.stringToSet(weiString, ";");
            shuCities = SymbolUtil.stringToSet(shuString, ";");
            wuCities = SymbolUtil.stringToSet(wuString, ";");
            ktBjS.setWeiCities(weiCities);
            ktBjS.setShuCities(shuCities);
            ktBjS.setWuCities(wuCities);
            super.put((Object)id, (Object)ktBjS);
            iron = ktBjS.getRewardIron();
            exp = ktBjS.getRewardExp();
            this.ironMap.put(ktBjS.getId(), iron);
            this.expMap.put(ktBjS.getId(), exp);
            this.idArray[ktBjS.getKindomLv()][ktBjS.getIndex()] = id;
            if (ktBjS.getKindomLv() == kingdomlv) {
                if (!this.indexLit.contains(ktBjS.getT())) {
                    this.indexLit.add(ktBjS.getT());
                }
                if (ktBjS.getTb() > 0 && this.legionStartMinutes == 0) {
                    this.legionStartMinutes = ktBjS.getT();
                }
            }
            weiInitCities.addAll(weiCities);
            shuInitCities.addAll(shuCities);
            wuInitCities.addAll(wuCities);
        }
        this.pitchCitiesMap.put(1, weiInitCities);
        this.pitchCitiesMap.put(2, shuInitCities);
        this.pitchCitiesMap.put(3, wuInitCities);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.ironMap.clear();
        this.expMap.clear();
        this.pitchCitiesMap.clear();
    }
    
    public Tuple<Integer, Integer> getRewardTuple(final int id) {
        final Integer iron = this.ironMap.get(id);
        final Integer exp = this.expMap.get(id);
        if (iron == null || exp == null) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        result.left = iron;
        result.right = exp;
        return result;
    }
    
    public Tuple<Integer, Integer> getRewardTuple(final int forceLv, final int index) {
        if (forceLv >= KtBjCache.length || forceLv <= 0 || index >= KtBjCache.length || index <= 0) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        int iron = 0;
        int exp = 0;
        KtBjS ktBjS = null;
        for (int i = 1; i <= index; ++i) {
            final int id = this.idArray[forceLv][i];
            ktBjS = (KtBjS)this.get((Object)id);
            if (ktBjS != null) {
                iron += ktBjS.getRewardIron();
                exp += ktBjS.getRewardExp();
            }
        }
        result.left = iron;
        result.right = exp;
        return result;
    }
    
    public int getIdByFAndI(final int forceLv, final int index) {
        if (forceLv >= KtBjCache.length || forceLv <= 0 || index >= KtBjCache.length || index <= 0) {
            return 0;
        }
        return this.idArray[forceLv][index];
    }
    
    public int getIndexByMinutes(final long minutes) {
        try {
            for (int i = 0; i < this.indexLit.size() - 1; ++i) {
                if (minutes >= this.indexLit.get(i) && minutes < this.indexLit.get(i + 1)) {
                    return i + 1;
                }
            }
            final int last = this.indexLit.get(this.indexLit.size() - 1);
            if (minutes >= last) {
                return this.indexLit.size();
            }
            return 0;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    public long getNextLegion(final KtBjS ktBjS, final long now) {
        if (ktBjS == null) {
            return now + 120000L;
        }
        final int addValue = Math.min(ktBjS.getTb(), 10);
        final long result = now + addValue * 60000L;
        return result;
    }
    
    public long getNextDT(final KtBjS ktBjS, final long now) {
        if (ktBjS == null) {
            return now + 10000L;
        }
        return now + ktBjS.getTd() * 1000L;
    }
    
    public Tuple<Integer, Long> getNextInvadeInfo(final long startTime) {
        final long now = System.currentTimeMillis();
        final long timeDiff = now - startTime;
        if (timeDiff <= 0L) {
            return null;
        }
        int index = -1;
        int sum = 0;
        for (int i = 0; i < this.indexLit.size(); ++i) {
            final int minute = this.indexLit.get(i);
            if (timeDiff < minute * 60000L) {
                index = i;
                sum = minute;
                break;
            }
        }
        if (index == -1) {
            return null;
        }
        final Tuple<Integer, Long> result = new Tuple();
        result.left = index + 1;
        result.right = sum * 60000L + startTime - now;
        return result;
    }
    
    public Set<Integer> getCitiesByForceId(final int forceId) {
        return this.pitchCitiesMap.get(forceId);
    }
}
