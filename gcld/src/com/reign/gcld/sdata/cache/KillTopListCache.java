package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import org.apache.commons.lang.*;

@Component("killTopListCache")
public class KillTopListCache extends AbstractCache<Integer, KillToplist>
{
    public static int[] boxArrays;
    @Autowired
    private SDataLoader dataLoader;
    
    static {
        KillTopListCache.boxArrays = null;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KillToplist> toplists = this.dataLoader.getModels((Class)KillToplist.class);
        KillTopListCache.boxArrays = new int[toplists.size()];
        int i = 0;
        for (final KillToplist k : toplists) {
            super.put((Object)k.getId(), (Object)k);
            KillTopListCache.boxArrays[i++] = k.getKill();
        }
    }
    
    public int getTreasureId(final int boxId) {
        final KillToplist killToplist = (KillToplist)this.get((Object)boxId);
        final double probType = WebUtil.nextDouble();
        final String[] typeList = killToplist.getTreasureTypeList().split(";");
        int type = 0;
        double sum = 0.0;
        for (int i = 0; i < typeList.length; ++i) {
            if (!StringUtils.isBlank(typeList[i])) {
                final String[] typeSingle = typeList[i].split(",");
                sum += Double.valueOf(typeSingle[1]);
                if (probType <= sum) {
                    type = Integer.parseInt(typeSingle[0]);
                    break;
                }
            }
        }
        int quality = 0;
        final double probQuantity = WebUtil.nextDouble();
        final String[] qualityList = killToplist.getTreasureQualityList().split(";");
        sum = 0.0;
        for (int j = 0; j < qualityList.length; ++j) {
            if (!StringUtils.isBlank(qualityList[j])) {
                final String[] qualitySingle = qualityList[j].split(",");
                sum += Double.valueOf(qualitySingle[1]);
                if (probQuantity <= sum) {
                    quality = Integer.parseInt(qualitySingle[0]);
                    break;
                }
            }
        }
        final StringBuffer result = new StringBuffer();
        result.append(boxId);
        if (type >= 10) {
            result.append(type);
        }
        else {
            result.append("0").append(type);
        }
        if (quality >= 10) {
            result.append(quality);
        }
        else {
            result.append("0").append(quality);
        }
        return Integer.parseInt(result.toString());
    }
    
    public static int[] getBoxArray(final int startId) {
        final int[] result = new int[5];
        int index = startId;
        for (int i = 0; i < result.length; ++i) {
            result[i] = KillTopListCache.boxArrays[index++];
        }
        return result;
    }
    
    @Override
	public void clear() {
        super.clear();
        KillTopListCache.boxArrays = null;
    }
}
