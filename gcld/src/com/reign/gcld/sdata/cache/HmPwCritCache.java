package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.common.*;

@Component("hmPwCritCache")
public class HmPwCritCache extends AbstractCache<Integer, HmPwCrit>
{
    @Autowired
    private SDataLoader dataLoader;
    List<Tuple<Double, HmPwCrit>> probList1;
    List<Tuple<Double, HmPwCrit>> probList2;
    
    public HmPwCritCache() {
        this.probList1 = new LinkedList<Tuple<Double, HmPwCrit>>();
        this.probList2 = new LinkedList<Tuple<Double, HmPwCrit>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HmPwCrit> resultList = this.dataLoader.getModels((Class)HmPwCrit.class);
        double accProb1 = 0.0;
        double accProb2 = 0.0;
        for (final HmPwCrit hmPwCrit : resultList) {
            accProb1 += hmPwCrit.getProb1();
            final Tuple<Double, HmPwCrit> tuple1 = new Tuple();
            tuple1.left = accProb1;
            tuple1.right = hmPwCrit;
            this.probList1.add(tuple1);
            accProb2 += hmPwCrit.getProb2();
            final Tuple<Double, HmPwCrit> tuple2 = new Tuple();
            tuple2.left = accProb2;
            tuple2.right = hmPwCrit;
            this.probList2.add(tuple2);
            super.put((Object)hmPwCrit.getId(), (Object)hmPwCrit);
        }
    }
    
    public HmPwCrit getRandHmPwCrit(final int type) {
        try {
            final double prob = WebUtil.nextDouble();
            List<Tuple<Double, HmPwCrit>> probList = null;
            if (type == 1) {
                probList = this.probList1;
            }
            else {
                if (type != 2) {
                    ErrorSceneLog.getInstance().appendErrorMsg("type error").append("type", type).appendMethodName("getRandHmPwCrit").appendClassName("HmPwCritCache").flush();
                    return null;
                }
                probList = this.probList2;
            }
            for (final Tuple<Double, HmPwCrit> tuple : probList) {
                if (prob < tuple.left) {
                    return tuple.right;
                }
            }
            ErrorSceneLog.getInstance().appendErrorMsg("result is null").append("type", type).append("prob", prob).appendMethodName("getRandHmPwCrit").appendClassName("HmPwCritCache").flush();
            return null;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("HmPwCritCache.getRandHmPwCrit catch Exception", e);
            return null;
        }
    }
}
