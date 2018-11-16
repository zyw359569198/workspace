package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;

@Component("hmBsMainCache")
public class HmBsMainCache extends AbstractCache<Integer, HmBsMain>
{
    @Autowired
    private SDataLoader dataLoader;
    private Logger errorLog;
    private Map<Integer, List<HmBsMain>> hbmListMap;
    private Map<String, Integer> outputMap;
    private Map<Integer, Integer> bsIdDMaxMap;
    private Map<String, HmBsMain> bsIdQualityHBMMap;
    
    public HmBsMainCache() {
        this.errorLog = CommonLog.getLog(HmBsMainCache.class);
        this.hbmListMap = new HashMap<Integer, List<HmBsMain>>();
        this.outputMap = new ConcurrentHashMap<String, Integer>();
        this.bsIdDMaxMap = new HashMap<Integer, Integer>();
        this.bsIdQualityHBMMap = new ConcurrentHashMap<String, HmBsMain>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmBsMain> resultList = this.dataLoader.getModels((Class)HmBsMain.class);
        for (final HmBsMain hbm : resultList) {
            super.put((Object)hbm.getId(), (Object)hbm);
            List<HmBsMain> hbmList = this.hbmListMap.get(hbm.getBsId());
            if (hbmList == null) {
                hbmList = new ArrayList<HmBsMain>();
                this.hbmListMap.put(hbm.getBsId(), hbmList);
            }
            hbmList.add(hbm);
            this.bsIdDMaxMap.put(hbm.getBsId(), hbm.getDMax());
        }
    }
    
    public int getOutput(final int bsId, final int quality) {
        final String key = this.getKey(bsId, quality);
        if (this.outputMap.containsKey(key)) {
            return this.outputMap.get(key);
        }
        final HmBsMain hbm = this.getHmBsMain(bsId, quality);
        if (hbm != null) {
            this.outputMap.put(key, hbm.getOutput());
            return hbm.getOutput();
        }
        this.errorLog.error("class:HmBsMainCache#method:getOutput#bsId:" + bsId + "#quality:" + quality);
        this.outputMap.put(key, 0);
        return 0;
    }
    
    public HmBsMain getHmBsMain(final int bsId, final int quality) {
        final String key = this.getKey(bsId, quality);
        if (this.bsIdQualityHBMMap.containsKey(key)) {
            return this.bsIdQualityHBMMap.get(key);
        }
        final List<HmBsMain> hbmList = this.hbmListMap.get(bsId);
        for (final HmBsMain hbm : hbmList) {
            if (hbm.getQualityLow() <= quality && quality <= hbm.getQualityHigh()) {
                this.bsIdQualityHBMMap.put(key, hbm);
                return hbm;
            }
        }
        this.errorLog.error("class:HmBsMainCache#method:getHmBsMain:" + bsId + "#quality:" + quality);
        return null;
    }
    
    private String getKey(final int bsId, final int quality) {
        return String.valueOf(bsId) + "_" + quality;
    }
    
    public int getDMax(final int bsId) {
        return this.bsIdDMaxMap.get(bsId);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.hbmListMap.clear();
        this.outputMap.clear();
        this.bsIdDMaxMap.clear();
    }
}
