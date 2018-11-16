package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("generalTreasureCache")
public class GeneralTreasureCache extends AbstractCache<Integer, GeneralTreasure>
{
    @Autowired
    private SDataLoader dataLoader;
    public List<Integer> openLvList;
    
    public GeneralTreasureCache() {
        this.openLvList = new ArrayList<Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<GeneralTreasure> list = this.dataLoader.getModels((Class)GeneralTreasure.class);
        for (final GeneralTreasure temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (!this.openLvList.contains(temp.getMinGeneralLevel())) {
                this.openLvList.add(temp.getMinGeneralLevel());
            }
        }
    }
    
    private List<GeneralTreasure> getGTListByLv(final int lv) {
        final List<GeneralTreasure> gtList = new ArrayList<GeneralTreasure>();
        for (final GeneralTreasure gt : super.getModels()) {
            if (lv >= gt.getMinGetLv()) {
                gtList.add(gt);
            }
        }
        return gtList;
    }
    
    public GeneralTreasure getGeneralTreasure(final int playerLv) {
        final List<GeneralTreasure> gtList = this.getGTListByLv(playerLv);
        if (gtList == null || gtList.size() == 0) {
            return null;
        }
        if (1 == gtList.size()) {
            return gtList.get(0);
        }
        int total = 0;
        for (final GeneralTreasure gt : gtList) {
            total += gt.getProb();
        }
        int rate = WebUtil.nextInt(total) + 1;
        while (gtList.size() > 0) {
            if (rate <= gtList.get(0).getProb()) {
                return gtList.get(0);
            }
            rate -= gtList.get(0).getProb();
            gtList.remove(0);
        }
        return null;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.openLvList.clear();
    }
}
