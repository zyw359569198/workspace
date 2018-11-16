package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.common.*;

@Component("wdSjpHyCache")
public class WdSjpHyCache extends AbstractCache<Integer, WdSjpHy>
{
    @Autowired
    private SDataLoader dataLoader;
    List<Tuple<Double, WdSjpHy>> probList;
    
    public WdSjpHyCache() {
        this.probList = new LinkedList<Tuple<Double, WdSjpHy>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpHy> resultList = this.dataLoader.getModels((Class)WdSjpHy.class);
        double accProb = 0.0;
        for (final WdSjpHy wdSjpHy : resultList) {
            accProb += wdSjpHy.getProb();
            final Tuple<Double, WdSjpHy> tuple = new Tuple();
            tuple.left = accProb;
            tuple.right = wdSjpHy;
            this.probList.add(tuple);
            super.put((Object)wdSjpHy.getId(), (Object)wdSjpHy);
        }
    }
    
    public WdSjpHy getRandWdSjpHy() {
        final double prob = WebUtil.nextDouble();
        for (final Tuple<Double, WdSjpHy> tuple : this.probList) {
            if (prob <= tuple.left) {
                return tuple.right;
            }
        }
        ErrorSceneLog.getInstance().appendErrorMsg("result is null").append("prob", prob).appendMethodName("getRandWdSjpHy").appendClassName("WdSjpHyCache").flush();
        return null;
    }
}
