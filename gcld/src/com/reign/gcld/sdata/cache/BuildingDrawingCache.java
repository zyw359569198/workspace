package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("buildingDrawingCache")
public class BuildingDrawingCache extends AbstractCache<Integer, BuildingDrawing>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<BuildingDrawing>> type2ListMap;
    private List<BuildingDrawing> openList;
    
    public BuildingDrawingCache() {
        this.type2ListMap = new HashMap<Integer, List<BuildingDrawing>>();
        this.openList = new ArrayList<BuildingDrawing>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<BuildingDrawing> resultList = this.dataLoader.getModels((Class)BuildingDrawing.class);
        for (final BuildingDrawing bd : resultList) {
            super.put((Object)bd.getId(), (Object)bd);
            final int type = bd.getGet();
            List<BuildingDrawing> bdList = this.type2ListMap.get(type);
            if (bdList == null) {
                bdList = new ArrayList<BuildingDrawing>();
                this.type2ListMap.put(type, bdList);
            }
            bdList.add(bd);
            if (bd.getGet() < 999) {
                this.openList.add(bd);
            }
        }
    }
    
    public List<BuildingDrawing> getListByType(final int type) {
        return this.type2ListMap.get(type);
    }
    
    public List<BuildingDrawing> getListByTypeAndLv(final int type, final int lv) {
        final List<BuildingDrawing> bdList = this.type2ListMap.get(type);
        if (bdList == null) {
            return null;
        }
        final List<BuildingDrawing> resultList = new ArrayList<BuildingDrawing>();
        for (final BuildingDrawing bd : bdList) {
            if (bd.getOpenLv() <= lv) {
                resultList.add(bd);
            }
        }
        return resultList;
    }
    
    public BuildingDrawing getByIdAndLvAndRate(final int id, final int lv, final double rate) {
        final BuildingDrawing bd = (BuildingDrawing)this.get((Object)id);
        if (bd == null || lv < bd.getOpenLv() || rate > bd.getProb()) {
            return null;
        }
        return bd;
    }
    
    public List<BuildingDrawing> getOpenList() {
        return this.openList;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.type2ListMap.clear();
        this.openList.clear();
    }
}
