package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.common.*;

@Component("WdSjSeCache")
public class WdSjSeCache extends AbstractCache<Integer, WdSjSe>
{
    @Autowired
    private SDataLoader dataLoader;
    private ArrayList<Tuple<Double, WdSjSe>> probList;
    
    public WdSjSeCache() {
        this.probList = new ArrayList<Tuple<Double, WdSjSe>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjSe> resultList = this.dataLoader.getModels((Class)WdSjSe.class);
        double probSum = 0.0;
        for (final WdSjSe wdSjSe : resultList) {
            probSum += wdSjSe.getProb();
            final Tuple<Double, WdSjSe> tuple = new Tuple();
            tuple.left = probSum;
            tuple.right = wdSjSe;
            this.probList.add(tuple);
            super.put((Object)wdSjSe.getId(), (Object)wdSjSe);
        }
    }
    
    public int getRandomType() {
        final double random = WebUtil.nextDouble();
        for (final Tuple<Double, WdSjSe> tuple : this.probList) {
            if (random <= tuple.left) {
                return tuple.right.getId();
            }
        }
        ErrorSceneLog.getInstance().appendErrorMsg("type is 0").append("random", random).appendMethodName("getRandomType").appendClassName("WdSjSeCache").flush();
        return 0;
    }
    
    @Override
	public void clear() {
        super.clear();
    }
}
