package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("ktTzEvCache")
public class KtTzEvCache extends AbstractCache<Integer, KtTzEv>
{
    @Autowired
    private SDataLoader dataLoader;
    private int eventNum;
    private List<KtTzEv> kTzEvs;
    private Map<Integer, List<KtTzEv>> eventToListMap;
    
    public KtTzEvCache() {
        this.eventNum = 0;
        this.kTzEvs = new ArrayList<KtTzEv>();
        this.eventToListMap = new HashMap<Integer, List<KtTzEv>>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtTzEv> list = this.dataLoader.getModels((Class)KtTzEv.class);
        for (final KtTzEv eleEv : list) {
            super.put((Object)eleEv.getId(), (Object)eleEv);
            if (eleEv.getS() >= this.eventNum) {
                this.eventNum = eleEv.getS();
            }
            this.kTzEvs.add(eleEv);
            List<KtTzEv> evs = this.eventToListMap.get(eleEv.getS());
            if (evs == null) {
                evs = new ArrayList<KtTzEv>();
                this.eventToListMap.put(eleEv.getS(), evs);
            }
            evs.add(eleEv);
        }
        Collections.sort(this.kTzEvs);
    }
    
    public KtTzEv getRandomEvent() {
        int random = WebUtil.nextInt(this.eventNum);
        ++random;
        for (final Integer integer : super.getCacheMap().keySet()) {
            final KtTzEv ktTzEv = (KtTzEv)super.get((Object)integer);
            if (ktTzEv.getS() != random) {
                continue;
            }
            if (ktTzEv.getT() == 0) {
                return ktTzEv;
            }
        }
        return null;
    }
    
    public KtTzEv getNextSerial(final int id) {
        final KtTzEv ktTzEv = (KtTzEv)super.get((Object)id);
        if (ktTzEv == null) {
            return null;
        }
        final KtTzEv nextEv = (KtTzEv)super.get((Object)(id + 1));
        if (nextEv == null) {
            return null;
        }
        if (nextEv.getI() == ktTzEv.getI() + 1) {
            return nextEv;
        }
        return null;
    }
    
    public KtTzEv getCurSerial(final long lastTime, final int event) {
        final long minutes = lastTime / 60000L;
        if (minutes >= 120L) {
            return null;
        }
        int index = 0;
        for (final KtTzEv ktTzEv : this.kTzEvs) {
            final int integer = ktTzEv.getId();
            if (ktTzEv.getS() != event) {
                continue;
            }
            if (minutes < ktTzEv.getT()) {
                return (KtTzEv)super.get((Object)index);
            }
            index = integer;
        }
        return (KtTzEv)super.get((Object)index);
    }
    
    public boolean isLastSerial(final KtTzEv ktTzEv, final int event) {
        int maxT = 0;
        for (final Integer integer : super.getCacheMap().keySet()) {
            final KtTzEv temp = (KtTzEv)super.get((Object)integer);
            if (temp.getS() != event) {
                continue;
            }
            if (temp.getT() <= maxT) {
                continue;
            }
            maxT = temp.getT();
        }
        return ktTzEv.getT() == maxT;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.kTzEvs.clear();
        this.eventToListMap.clear();
    }
    
    public KtTzEv getLastSerial(final int event) {
        KtTzEv result = null;
        for (final KtTzEv tempEv : this.kTzEvs) {
            if (tempEv.getS() != event) {
                continue;
            }
            result = tempEv;
        }
        return result;
    }
    
    public List<KtTzEv> getEvListByEvent(final int event) {
        return this.eventToListMap.get(event);
    }
}
