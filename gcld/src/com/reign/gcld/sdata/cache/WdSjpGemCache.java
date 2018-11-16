package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.util.*;

@Component("wdSjpGemCache")
public class WdSjpGemCache extends AbstractCache<Integer, WdSjpGem>
{
    @Autowired
    private SDataLoader dataLoader;
    private List<Tuple<Integer, Integer>> stepList;
    public static final int BAO_JI_COUNT_1 = 1;
    
    public WdSjpGemCache() {
        this.stepList = new LinkedList<Tuple<Integer, Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<WdSjpGem> resultList = this.dataLoader.getModels((Class)WdSjpGem.class);
        for (final WdSjpGem wdSjpGem : resultList) {
            if (wdSjpGem.getEnd() > 0) {
                final Tuple<Integer, Integer> tuple = new Tuple();
                tuple.left = wdSjpGem.getEnd();
                tuple.right = wdSjpGem.getId();
                this.stepList.add(tuple);
            }
            else if (wdSjpGem.getEnd() == 0) {
                final Tuple<Integer, Integer> tuple = new Tuple();
                tuple.left = Integer.MAX_VALUE;
                tuple.right = wdSjpGem.getId();
                this.stepList.add(tuple);
            }
            this.parserWdSjpGem(wdSjpGem);
            super.put((Object)wdSjpGem.getId(), (Object)wdSjpGem);
        }
    }
    
    private void parserWdSjpGem(final WdSjpGem wdSjpGem) {
        if (wdSjpGem.getMulti() == null || wdSjpGem.getMulti().trim().isEmpty()) {
            throw new RuntimeException("WdSjpGem parse fail1, id:" + wdSjpGem.getId());
        }
        final String[] probStrings = wdSjpGem.getMulti().split(";");
        final List<Tuple<Double, Integer>> probList = new LinkedList<Tuple<Double, Integer>>();
        double accuProb = 0.0;
        String[] array;
        for (int length = (array = probStrings).length, i = 0; i < length; ++i) {
            final String probString = array[i];
            final String[] prob = probString.split(",");
            if (prob.length != 2) {
                throw new RuntimeException("WdSjpGem parse fail2, id:" + wdSjpGem.getId());
            }
            accuProb += Double.parseDouble(prob[0]);
            final Tuple<Double, Integer> tuple = new Tuple();
            tuple.left = accuProb;
            tuple.right = Integer.parseInt(prob[1]);
            probList.add(tuple);
        }
        wdSjpGem.setProbList(probList);
    }
    
    public Integer getIronCost(final int buyCount) {
        try {
            Integer targetId = null;
            for (final Tuple<Integer, Integer> tuple : this.stepList) {
                if (buyCount <= tuple.left) {
                    targetId = tuple.right;
                    break;
                }
            }
            if (targetId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, targetId is null").append("buyCount", buyCount).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            final WdSjpGem wdSjpGem = (WdSjpGem)this.get((Object)targetId);
            if (wdSjpGem == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, wdSjpGem is null").append("buyCount", buyCount).append("targetId", targetId).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            int times = buyCount - wdSjpGem.getStart();
            if (times < 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, times is negative").append("buyCount", buyCount).append("targetId", targetId).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").append("wdSjpGem.getStart()", wdSjpGem.getStart()).flush();
                times = 0;
            }
            final int iron = wdSjpGem.getIronInit() - wdSjpGem.getIronDe() * times;
            if (iron < 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, times is negative").append("buyCount", buyCount).append("targetId", targetId).append("times", times).append("wdSjpGem.getIronInit()", wdSjpGem.getIronInit()).append("wdSjpGem.getIronDe()", wdSjpGem.getIronDe()).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            return iron;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WdSjpGemCache.getIronCost exception", e);
            return null;
        }
    }
    
    public Integer getBaoJiCount(final int buyCount) {
        try {
            Integer targetId = null;
            for (final Tuple<Integer, Integer> tuple : this.stepList) {
                if (buyCount <= tuple.left) {
                    targetId = tuple.right;
                    break;
                }
            }
            if (targetId == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, targetId is null").append("buyCount", buyCount).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            final WdSjpGem wdSjpGem = (WdSjpGem)this.get((Object)targetId);
            if (wdSjpGem == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("buyCount error, wdSjpGem is null").append("buyCount", buyCount).append("targetId", targetId).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            final double randDouble = WebUtil.nextDouble();
            Tuple<Double, Integer> targetTuple = null;
            for (final Tuple<Double, Integer> tuple2 : wdSjpGem.getProbList()) {
                if (randDouble < tuple2.left) {
                    targetTuple = tuple2;
                    break;
                }
            }
            if (targetTuple == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("targetTuple is null").append("buyCount", buyCount).append("targetId", targetId).append("randDouble", randDouble).appendMethodName("getIronCost").appendClassName("WdSjpGemCache").flush();
                return null;
            }
            final Integer baojiCount = targetTuple.right;
            return baojiCount;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("WdSjpGemCache.getBaoJiCount exception", e);
            return null;
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.stepList.clear();
    }
}
