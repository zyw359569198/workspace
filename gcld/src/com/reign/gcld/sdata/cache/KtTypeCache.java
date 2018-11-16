package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("ktTypeCache")
public class KtTypeCache extends AbstractCache<Integer, KtType>
{
    @Autowired
    private SDataLoader dataLoader;
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtType> list = this.dataLoader.getModels((Class)KtType.class);
        for (final KtType ktType : list) {
            super.put((Object)ktType.getId(), (Object)ktType);
        }
    }
    
    public int getKtType() {
        final List<KtType> list = this.dataLoader.getModels((Class)KtType.class);
        final double random = WebUtil.nextDouble();
        double sum = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            final KtType ktType = list.get(i);
            sum += ktType.getProb();
            if (random <= sum) {
                return ktType.getId();
            }
        }
        return 1;
    }
    
    public int getRankGrade(final int type, final int rank) {
        final KtType ktType = (KtType)super.get((Object)type);
        if (rank == 1) {
            return ktType.getJ1();
        }
        if (rank == 2) {
            return ktType.getJ2();
        }
        if (rank == 3) {
            return ktType.getJ3();
        }
        return 0;
    }
}
