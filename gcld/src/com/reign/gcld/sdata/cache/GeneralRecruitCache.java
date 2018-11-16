package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.sdata.common.*;
import java.util.*;

@Component("generalRecruitCache")
public class GeneralRecruitCache extends AbstractCache<Integer, GeneralRecruit>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private PowerCache powerCache;
    private Map<Integer, GeneralRecruit> generalRecruitMap;
    List<RecruitInfo> allMilitaryDropList;
    List<RecruitInfo> allCivilDropList;
    
    public GeneralRecruitCache() {
        this.generalRecruitMap = new HashMap<Integer, GeneralRecruit>();
        this.allMilitaryDropList = new ArrayList<RecruitInfo>();
        this.allCivilDropList = new ArrayList<RecruitInfo>();
    }
    
    public GeneralRecruit getByGeneralId(final int generalId) {
        return this.generalRecruitMap.get(generalId);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<GeneralRecruit> resultList = this.dataLoader.getModels((Class)GeneralRecruit.class);
        for (final GeneralRecruit temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            this.generalRecruitMap.put(temp.getGeneralId(), temp);
            if (temp.getDropIndex() > 0) {
                final General general = (General)this.generalCache.get((Object)temp.getGeneralId());
                if (general == null) {
                    continue;
                }
                final Power power = (Power)this.powerCache.get((Object)temp.getPowerId());
                if (power == null) {
                    continue;
                }
                final RecruitInfo ri = new RecruitInfo();
                ri.setGeneralId(temp.getGeneralId());
                ri.setGeneralName(general.getName());
                ri.setQuality(general.getQuality());
                ri.setPowerId(temp.getPowerId());
                ri.setPowerName(temp.getIntro());
                ri.setDropIndex(temp.getDropIndex());
                if (temp.getType() == 1) {
                    this.allCivilDropList.add(ri);
                }
                else {
                    this.allMilitaryDropList.add(ri);
                }
            }
        }
        Collections.sort(this.allCivilDropList, new RecruitComparator());
        Collections.sort(this.allMilitaryDropList, new RecruitComparator());
    }
    
    public List<RecruitInfo> getDropGeneralList(final int type) {
        if (type == 1) {
            return this.allCivilDropList;
        }
        return this.allMilitaryDropList;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.generalRecruitMap.clear();
        this.allCivilDropList.clear();
        this.allMilitaryDropList.clear();
    }
}
