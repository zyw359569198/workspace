package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("wdSjpDramaCache")
public class WdSjpDramaCache extends AbstractCache<Integer, WdSjpDrama>
{
    @Autowired
    SDataLoader dataLoader;
    private Map<Integer, WdSjpDrama> map;
    private List<Integer> dramaTechList;
    private Map<Integer, List<WdSjpDrama>> tech2DramaListMap;
    private int minLv;
    
    public WdSjpDramaCache() {
        this.map = new HashMap<Integer, WdSjpDrama>();
        this.dramaTechList = new ArrayList<Integer>();
        this.tech2DramaListMap = new HashMap<Integer, List<WdSjpDrama>>();
        this.minLv = Integer.MAX_VALUE;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpDrama> list = this.dataLoader.getModels((Class)WdSjpDrama.class);
        for (final WdSjpDrama drama : list) {
            super.put((Object)drama.getId(), (Object)drama);
            this.map.put(drama.getDramaId(), drama);
            if (drama.getOpenLv() < this.minLv) {
                this.minLv = drama.getOpenLv();
            }
            if (!this.dramaTechList.contains(drama.getOpenTech())) {
                this.dramaTechList.add(drama.getOpenTech());
            }
            final int tech = drama.getOpenTech();
            List<WdSjpDrama> dramas = this.tech2DramaListMap.get(tech);
            if (dramas == null) {
                dramas = new ArrayList<WdSjpDrama>();
                this.tech2DramaListMap.put(tech, dramas);
            }
            dramas.add(drama);
        }
    }
    
    public WdSjpDrama getWorldDramaByDramaId(final int dramaId) {
        return this.map.get(dramaId);
    }
    
    public WdSjpDrama getWorldDramaByDramaIdAndGrade(final int dramaId, final int grade) {
        for (final Integer key : super.getCacheMap().keySet()) {
            final WdSjpDrama drama = (WdSjpDrama)this.get((Object)key);
            if (drama.getDramaId() == dramaId && drama.getDifficulty() == grade) {
                return drama;
            }
        }
        return null;
    }
    
    @Override
	public void clear() {
        this.map.clear();
        this.dramaTechList.clear();
        this.tech2DramaListMap.clear();
        super.clear();
    }
    
    public int getMinWorldDramaLv() {
        return this.minLv;
    }
    
    public List<WdSjpDrama> getDramaListByLv(final Integer playerLv) {
        List<WdSjpDrama> result = null;
        for (final Integer key : super.getCacheMap().keySet()) {
            final WdSjpDrama drama = (WdSjpDrama)this.get((Object)key);
            if (drama.getOpenLv() <= playerLv) {
                if (result == null) {
                    result = new ArrayList<WdSjpDrama>();
                }
                result.add(drama);
            }
        }
        return result;
    }
    
    public List<WdSjpDrama> getDramaListByOpenLv(final Integer playerLv) {
        List<WdSjpDrama> result = null;
        for (final Integer key : super.getCacheMap().keySet()) {
            final WdSjpDrama drama = (WdSjpDrama)this.get((Object)key);
            if (drama.getOpenLv() == playerLv) {
                if (result == null) {
                    result = new ArrayList<WdSjpDrama>();
                }
                result.add(drama);
            }
        }
        return result;
    }
    
    public List<Integer> getDramaOpenList() {
        return this.dramaTechList;
    }
    
    public List<WdSjpDrama> getDramaListByOpenTech(final int openTech) {
        return this.tech2DramaListMap.get(openTech);
    }
    
    public int getTechIndexByTechId(final int openTech) {
        return this.dramaTechList.indexOf(openTech);
    }
}
