package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.sun.xml.internal.fastinfoset.stax.events.*;
import java.util.*;

@Component("soloDramaCache")
public class SoloDramaCache extends AbstractCache<Integer, SoloDrama>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private WdSjpDramaCache wdSjpDramaCache;
    private Map<Integer, SoloDrama> map;
    private Map<Integer, SoloDrama> worldDramaMap;
    
    public SoloDramaCache() {
        this.map = new HashMap<Integer, SoloDrama>();
        this.worldDramaMap = new HashMap<Integer, SoloDrama>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SoloDrama> list = this.dataLoader.getModels((Class)SoloDrama.class);
        int grade = 0;
        for (final SoloDrama sd : list) {
            super.put((Object)sd.getId(), (Object)sd);
            if (this.wdSjpDramaCache.getWorldDramaByDramaId(sd.getId()) == null) {
                grade = 0;
                if (!Util.isEmptyString(sd.getEvent1())) {
                    ++grade;
                }
                if (!Util.isEmptyString(sd.getEvent2())) {
                    ++grade;
                }
                if (!Util.isEmptyString(sd.getEvent3())) {
                    ++grade;
                }
                if (!Util.isEmptyString(sd.getEvent4())) {
                    ++grade;
                }
                if (!Util.isEmptyString(sd.getEvent5())) {
                    ++grade;
                }
                sd.setGrade(grade);
                this.map.put(sd.getLv(), sd);
            }
            else {
                this.worldDramaMap.put(sd.getLv(), sd);
            }
        }
    }
    
    public SoloDrama getSoloByLv(final int lv) {
        if (this.map.containsKey(lv)) {
            return this.map.get(lv);
        }
        return null;
    }
    
    public int getSoloOrder(final int soloId) {
        final SoloDrama sd = (SoloDrama)this.get((Object)soloId);
        int order = 1;
        for (final Integer lv : this.map.keySet()) {
            if (lv < sd.getLv()) {
                ++order;
            }
        }
        return order;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.worldDramaMap.clear();
        this.map.clear();
    }
}
