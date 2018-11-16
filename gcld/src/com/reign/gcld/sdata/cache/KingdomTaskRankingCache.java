package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.*;

@Component("kingdomTaskRankingCache")
public class KingdomTaskRankingCache extends AbstractCache<Integer, KindomTaskRanking>
{
    private Map<Integer, BarbarainRanking> taskRankingType0;
    private Map<Integer, KindomTaskRanking> taskRankingType1;
    private Map<Integer, KtSdRanking> taskRankingType2;
    private Map<Integer, KtMzRanking> taskRankingType3;
    private Map<Integer, KtTzRanking> taskRankingType4;
    private Map<Integer, KtImRanking> taskRankingType5;
    private Map<Integer, KtKjRanking> taskRankingType6;
    private Map<Integer, KtSdmzRanking> taskRankingType7;
    private Map<Integer, KillRankingExtra> taskRankingType999;
    private Map<Integer, KtBjRanking> taskRankingType8;
    private Map<Integer, KtHjRanking> taskRankingType9;
    private Map<Integer, KtMrRanking> taskRankingType10;
    private Map<Integer, KtNfRanking> taskRankingType12;
    private Map<Integer, KtRankingLv> titleMap;
    @Autowired
    private SDataLoader dataLoader;
    
    public KingdomTaskRankingCache() {
        this.taskRankingType0 = new HashMap<Integer, BarbarainRanking>();
        this.taskRankingType1 = new HashMap<Integer, KindomTaskRanking>();
        this.taskRankingType2 = new HashMap<Integer, KtSdRanking>();
        this.taskRankingType3 = new HashMap<Integer, KtMzRanking>();
        this.taskRankingType4 = new HashMap<Integer, KtTzRanking>();
        this.taskRankingType5 = new HashMap<Integer, KtImRanking>();
        this.taskRankingType6 = new HashMap<Integer, KtKjRanking>();
        this.taskRankingType7 = new HashMap<Integer, KtSdmzRanking>();
        this.taskRankingType999 = new HashMap<Integer, KillRankingExtra>();
        this.taskRankingType8 = new HashMap<Integer, KtBjRanking>();
        this.taskRankingType9 = new HashMap<Integer, KtHjRanking>();
        this.taskRankingType10 = new HashMap<Integer, KtMrRanking>();
        this.taskRankingType12 = new HashMap<Integer, KtNfRanking>();
        this.titleMap = new HashMap<Integer, KtRankingLv>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.taskRankingType1.clear();
        this.taskRankingType2.clear();
        this.taskRankingType3.clear();
        this.taskRankingType4.clear();
        this.taskRankingType5.clear();
        this.taskRankingType6.clear();
        this.taskRankingType7.clear();
        this.taskRankingType999.clear();
        this.taskRankingType8.clear();
        this.titleMap.clear();
        this.taskRankingType9.clear();
        this.taskRankingType10.clear();
        this.taskRankingType12.clear();
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<BarbarainRanking> list0 = this.dataLoader.getModels((Class)BarbarainRanking.class);
        for (final BarbarainRanking taskRanking : list0) {
            this.taskRankingType0.put(taskRanking.getId(), taskRanking);
        }
        final List<KindomTaskRanking> list2 = this.dataLoader.getModels((Class)KindomTaskRanking.class);
        for (final KindomTaskRanking taskRanking2 : list2) {
            this.taskRankingType1.put(taskRanking2.getId(), taskRanking2);
        }
        final List<KtSdRanking> list3 = this.dataLoader.getModels((Class)KtSdRanking.class);
        for (final KtSdRanking taskRanking3 : list3) {
            this.taskRankingType2.put(taskRanking3.getId(), taskRanking3);
        }
        final List<KtMzRanking> list4 = this.dataLoader.getModels((Class)KtMzRanking.class);
        for (final KtMzRanking taskRanking4 : list4) {
            this.taskRankingType3.put(taskRanking4.getId(), taskRanking4);
        }
        final List<KtTzRanking> list5 = this.dataLoader.getModels((Class)KtTzRanking.class);
        for (final KtTzRanking taskRanking5 : list5) {
            this.taskRankingType4.put(taskRanking5.getId(), taskRanking5);
        }
        final List<KtImRanking> list6 = this.dataLoader.getModels((Class)KtImRanking.class);
        for (final KtImRanking ktImRanking : list6) {
            this.taskRankingType5.put(ktImRanking.getId(), ktImRanking);
        }
        final List<KtKjRanking> list7 = this.dataLoader.getModels((Class)KtKjRanking.class);
        for (final KtKjRanking ktKjRanking : list7) {
            this.taskRankingType6.put(ktKjRanking.getId(), ktKjRanking);
        }
        final List<KtSdmzRanking> list8 = this.dataLoader.getModels((Class)KtSdmzRanking.class);
        for (final KtSdmzRanking ktKjRanking2 : list8) {
            this.taskRankingType7.put(ktKjRanking2.getId(), ktKjRanking2);
        }
        final List<KillRankingExtra> list9 = this.dataLoader.getModels((Class)KillRankingExtra.class);
        for (final KillRankingExtra kre : list9) {
            this.taskRankingType999.put(kre.getId(), kre);
        }
        final List<KtRankingLv> lvs = this.dataLoader.getModels((Class)KtRankingLv.class);
        for (final KtRankingLv lv : lvs) {
            this.titleMap.put(lv.getLv(), lv);
        }
        final List<KtBjRanking> list10 = this.dataLoader.getModels((Class)KtBjRanking.class);
        for (final KtBjRanking lv2 : list10) {
            this.taskRankingType8.put(lv2.getId(), lv2);
        }
        final List<KtHjRanking> list11 = this.dataLoader.getModels((Class)KtHjRanking.class);
        for (final KtHjRanking khr : list11) {
            this.taskRankingType9.put(khr.getId(), khr);
        }
        final List<KtMrRanking> list12 = this.dataLoader.getModels((Class)KtMrRanking.class);
        for (final KtMrRanking kmr : list12) {
            this.taskRankingType10.put(kmr.getId(), kmr);
        }
        final List<KtNfRanking> list13 = this.dataLoader.getModels((Class)KtNfRanking.class);
        for (final KtNfRanking kmr2 : list13) {
            this.taskRankingType12.put(kmr2.getId(), kmr2);
        }
    }
    
    public MultiResult getTaskRanking(final int rank, final int forceLv, final int type, final int order) {
        final MultiResult result = new MultiResult();
        if (type == 1) {
            for (final Integer id : this.taskRankingType1.keySet()) {
                final KindomTaskRanking ranking = this.taskRankingType1.get(id);
                if (ranking.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                    result.result1 = ranking.getRewardIron();
                    result.result2 = ranking.getRewardExp();
                    result.result3 = ranking.getGemItem();
                    result.result4 = ranking.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 2) {
            for (final Integer id : this.taskRankingType2.keySet()) {
                final KtSdRanking ranking2 = this.taskRankingType2.get(id);
                if (ranking2.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking2.getTaskR() != order) {
                    continue;
                }
                if (rank <= ranking2.getLowLv() && rank >= ranking2.getHighLv()) {
                    result.result1 = ranking2.getRewardIron();
                    result.result2 = ranking2.getRewardExp();
                    result.result3 = ranking2.getGemItem();
                    result.result4 = ranking2.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 3) {
            for (final Integer id : this.taskRankingType3.keySet()) {
                final KtMzRanking ranking3 = this.taskRankingType3.get(id);
                if (ranking3.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking3.getTaskR() != order) {
                    continue;
                }
                if (rank <= ranking3.getLowLv() && rank >= ranking3.getHighLv()) {
                    result.result1 = ranking3.getRewardIron();
                    result.result2 = ranking3.getRewardExp();
                    result.result3 = ranking3.getGemItem();
                    result.result4 = ranking3.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 4) {
            for (final Integer id : this.taskRankingType4.keySet()) {
                final KtTzRanking ranking4 = this.taskRankingType4.get(id);
                if (ranking4.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking4.getTaskR() != order) {
                    continue;
                }
                if (rank <= ranking4.getLowLv() && rank >= ranking4.getHighLv()) {
                    result.result1 = ranking4.getRewardIron();
                    result.result2 = ranking4.getRewardExp();
                    result.result3 = ranking4.getGemItem();
                    result.result4 = ranking4.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 0) {
            for (final Integer id : this.taskRankingType0.keySet()) {
                final BarbarainRanking ranking5 = this.taskRankingType0.get(id);
                if (ranking5.getBarbarainLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking5.getLowLv() && rank >= ranking5.getHighLv()) {
                    result.result1 = ranking5.getRewardIron();
                    result.result2 = ranking5.getRewardExp();
                    return result;
                }
            }
        }
        else if (type == 5) {
            for (final Integer id : this.taskRankingType5.keySet()) {
                final KtImRanking ranking6 = this.taskRankingType5.get(id);
                if (ranking6.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking6.getTaskR() != 1) {
                    continue;
                }
                if (rank <= ranking6.getLowLv() && rank >= ranking6.getHighLv()) {
                    result.result1 = ranking6.getRewardIron();
                    result.result2 = ranking6.getRewardExp();
                    result.result3 = ranking6.getGemItem();
                    result.result4 = ranking6.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 6) {
            for (final Integer id : this.taskRankingType6.keySet()) {
                final KtKjRanking ranking7 = this.taskRankingType6.get(id);
                if (ranking7.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking7.getLowLv() && rank >= ranking7.getHighLv()) {
                    result.result1 = ranking7.getRewardIron();
                    result.result2 = ranking7.getRewardExp();
                    result.result3 = ranking7.getGemItem();
                    result.result4 = ranking7.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 999) {
            for (final Integer id : this.taskRankingType999.keySet()) {
                final KillRankingExtra kre = this.taskRankingType999.get(id);
                if (kre.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= kre.getLowLv() && rank >= kre.getHighLv()) {
                    result.result1 = kre.getRewardFood();
                    return result;
                }
            }
        }
        else if (type == 7) {
            for (final Integer id : this.taskRankingType7.keySet()) {
                final KtSdmzRanking ranking8 = this.taskRankingType7.get(id);
                if (ranking8.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking8.getLowLv() && rank >= ranking8.getHighLv()) {
                    result.result1 = ranking8.getRewardIron();
                    result.result2 = ranking8.getRewardExp();
                    result.result3 = ranking8.getGemItem();
                    result.result4 = ranking8.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 8) {
            for (final Integer id : this.taskRankingType8.keySet()) {
                final KtBjRanking ranking9 = this.taskRankingType8.get(id);
                if (ranking9.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking9.getLowLv() && rank >= ranking9.getHighLv()) {
                    result.result1 = ranking9.getRewardIron();
                    result.result2 = ranking9.getRewardExp();
                    result.result3 = ranking9.getGemItem();
                    result.result4 = ranking9.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 9) {
            for (final Integer id : this.taskRankingType9.keySet()) {
                final KtHjRanking ranking10 = this.taskRankingType9.get(id);
                if (ranking10.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking10.getPeriod() != order) {
                    continue;
                }
                if (rank <= ranking10.getLowLv() && rank >= ranking10.getHighLv()) {
                    result.result1 = ranking10.getRewardIron();
                    result.result2 = ranking10.getRewardExp();
                    result.result3 = ranking10.getGemItem();
                    result.result4 = ranking10.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 10) {
            for (final Integer id : this.taskRankingType10.keySet()) {
                final KtMrRanking ranking11 = this.taskRankingType10.get(id);
                if (ranking11.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking11.getPeriod() != order) {
                    continue;
                }
                if (rank <= ranking11.getLowLv() && rank >= ranking11.getHighLv()) {
                    result.result1 = ranking11.getRewardIron();
                    result.result2 = ranking11.getRewardExp();
                    result.result3 = ranking11.getGemItem();
                    result.result4 = ranking11.getGemNum();
                    return result;
                }
            }
        }
        else if (type == 12) {
            for (final Integer id : this.taskRankingType12.keySet()) {
                final KtNfRanking ranking12 = this.taskRankingType12.get(id);
                if (ranking12.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking12.getPeriod() != order) {
                    continue;
                }
                if (rank <= ranking12.getLowLv() && rank >= ranking12.getHighLv()) {
                    result.result1 = ranking12.getRewardIron();
                    result.result2 = ranking12.getRewardExp();
                    result.result3 = ranking12.getGemItem();
                    result.result4 = ranking12.getGemNum();
                    return result;
                }
            }
        }
        return null;
    }
    
    public int getNextRank(final int rank, final int nationRank, final int taskType, final int forceLv) {
        int curSerial = 0;
        int nextSerial = 0;
        if (taskType == 1) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType1.keySet()) {
                final KindomTaskRanking ranking = this.taskRankingType1.get(id);
                if (ranking.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                    curSerial = ranking.getId();
                }
                if (minLowLv >= ranking.getLowLv()) {
                    continue;
                }
                minLowLv = ranking.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KindomTaskRanking curRanking = this.taskRankingType1.get(curSerial);
            final KindomTaskRanking nextRanking = this.taskRankingType1.get(nextSerial);
            if (nextRanking == null || !nextRanking.getKindomLv().equals(curRanking.getKindomLv())) {
                return 0;
            }
            return nextRanking.getLowLv();
        }
        else if (taskType == 2) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType2.keySet()) {
                final KtSdRanking ranking2 = this.taskRankingType2.get(id);
                if (ranking2.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking2.getTaskR() != nationRank) {
                    continue;
                }
                if (rank <= ranking2.getLowLv() && rank >= ranking2.getHighLv()) {
                    curSerial = ranking2.getId();
                }
                if (minLowLv >= ranking2.getLowLv()) {
                    continue;
                }
                minLowLv = ranking2.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtSdRanking nextRanking2 = this.taskRankingType2.get(nextSerial);
            final KtSdRanking curRanking2 = this.taskRankingType2.get(curSerial);
            if (nextRanking2 == null || !nextRanking2.getKindomLv().equals(curRanking2.getKindomLv()) || !nextRanking2.getTaskR().equals(curRanking2.getTaskR())) {
                return 0;
            }
            return nextRanking2.getLowLv();
        }
        else if (taskType == 3) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType3.keySet()) {
                final KtMzRanking ranking3 = this.taskRankingType3.get(id);
                if (ranking3.getKindomLv() != nationRank) {
                    continue;
                }
                if (ranking3.getTaskR() != nationRank) {
                    continue;
                }
                if (rank <= ranking3.getLowLv() && rank >= ranking3.getHighLv()) {
                    curSerial = ranking3.getId();
                }
                if (minLowLv >= ranking3.getLowLv()) {
                    continue;
                }
                minLowLv = ranking3.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtMzRanking nextRanking3 = this.taskRankingType3.get(nextSerial);
            final KtMzRanking curRanking3 = this.taskRankingType3.get(curSerial);
            if (nextRanking3 == null || !nextRanking3.getKindomLv().equals(curRanking3.getKindomLv()) || !nextRanking3.getTaskR().equals(curRanking3.getTaskR())) {
                return 0;
            }
            return nextRanking3.getLowLv();
        }
        else if (taskType == 4) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType4.keySet()) {
                final KtTzRanking ranking4 = this.taskRankingType4.get(id);
                if (ranking4.getKindomLv() != forceLv) {
                    continue;
                }
                if (ranking4.getTaskR() != nationRank) {
                    continue;
                }
                if (rank <= ranking4.getLowLv() && rank >= ranking4.getHighLv()) {
                    curSerial = ranking4.getId();
                }
                if (minLowLv >= ranking4.getLowLv()) {
                    continue;
                }
                minLowLv = ranking4.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtTzRanking nextRanking4 = this.taskRankingType4.get(nextSerial);
            final KtTzRanking curRanking4 = this.taskRankingType4.get(curSerial);
            if (nextRanking4 == null || !nextRanking4.getKindomLv().equals(curRanking4.getKindomLv()) || !nextRanking4.getTaskR().equals(curRanking4.getTaskR())) {
                return 0;
            }
            return nextRanking4.getLowLv();
        }
        else if (taskType == 0) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType0.keySet()) {
                final BarbarainRanking ranking5 = this.taskRankingType0.get(id);
                if (ranking5.getBarbarainLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking5.getLowLv() && rank >= ranking5.getHighLv()) {
                    curSerial = ranking5.getId();
                }
                if (minLowLv >= ranking5.getLowLv()) {
                    continue;
                }
                minLowLv = ranking5.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final BarbarainRanking nextRanking5 = this.taskRankingType0.get(nextSerial);
            final BarbarainRanking curRanking5 = this.taskRankingType0.get(curSerial);
            if (nextRanking5 == null || !nextRanking5.getBarbarainLv().equals(curRanking5.getBarbarainLv())) {
                return 0;
            }
            return nextRanking5.getLowLv();
        }
        else if (taskType == 5) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType5.keySet()) {
                final KtImRanking ranking6 = this.taskRankingType5.get(id);
                if (ranking6.getKindomLv() != nationRank) {
                    continue;
                }
                if (ranking6.getTaskR() != 1) {
                    continue;
                }
                if (rank <= ranking6.getLowLv() && rank >= ranking6.getHighLv()) {
                    curSerial = ranking6.getId();
                }
                if (minLowLv >= ranking6.getLowLv()) {
                    continue;
                }
                minLowLv = ranking6.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtImRanking nextRanking6 = this.taskRankingType5.get(nextSerial);
            final KtImRanking curRanking6 = this.taskRankingType5.get(curSerial);
            if (nextRanking6 == null || !nextRanking6.getKindomLv().equals(curRanking6.getKindomLv()) || !nextRanking6.getTaskR().equals(curRanking6.getTaskR())) {
                return 0;
            }
            return nextRanking6.getLowLv();
        }
        else if (taskType == 6) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType6.keySet()) {
                final KtKjRanking ranking7 = this.taskRankingType6.get(id);
                if (ranking7.getKindomLv() != nationRank) {
                    continue;
                }
                if (rank <= ranking7.getLowLv() && rank >= ranking7.getHighLv()) {
                    curSerial = ranking7.getId();
                }
                if (minLowLv >= ranking7.getLowLv()) {
                    continue;
                }
                minLowLv = ranking7.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtKjRanking nextRanking7 = this.taskRankingType6.get(nextSerial);
            final KtKjRanking curRanking7 = this.taskRankingType6.get(curSerial);
            if (nextRanking7 == null || !nextRanking7.getKindomLv().equals(curRanking7.getKindomLv())) {
                return 0;
            }
            return nextRanking7.getLowLv();
        }
        else if (taskType == 999) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType1.keySet()) {
                final KindomTaskRanking ranking = this.taskRankingType1.get(id);
                if (ranking.getKindomLv() != forceLv) {
                    continue;
                }
                if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                    curSerial = ranking.getId();
                }
                if (minLowLv >= ranking.getLowLv()) {
                    continue;
                }
                minLowLv = ranking.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KindomTaskRanking curRanking = this.taskRankingType1.get(curSerial);
            final KindomTaskRanking nextRanking = this.taskRankingType1.get(nextSerial);
            if (nextRanking == null || !nextRanking.getKindomLv().equals(curRanking.getKindomLv())) {
                return 0;
            }
            return nextRanking.getLowLv();
        }
        else if (taskType == 7) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType7.keySet()) {
                final KtSdmzRanking ranking8 = this.taskRankingType7.get(id);
                if (ranking8.getKindomLv() != nationRank) {
                    continue;
                }
                if (rank <= ranking8.getLowLv() && rank >= ranking8.getHighLv()) {
                    curSerial = ranking8.getId();
                }
                if (minLowLv >= ranking8.getLowLv()) {
                    continue;
                }
                minLowLv = ranking8.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtSdmzRanking nextRanking8 = this.taskRankingType7.get(nextSerial);
            final KtSdmzRanking curRanking8 = this.taskRankingType7.get(curSerial);
            if (nextRanking8 == null || !nextRanking8.getKindomLv().equals(curRanking8.getKindomLv())) {
                return 0;
            }
            return nextRanking8.getLowLv();
        }
        else if (taskType == 8) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType8.keySet()) {
                final KtBjRanking ranking9 = this.taskRankingType8.get(id);
                if (ranking9.getKindomLv() != nationRank) {
                    continue;
                }
                if (rank <= ranking9.getLowLv() && rank >= ranking9.getHighLv()) {
                    curSerial = ranking9.getId();
                }
                if (minLowLv >= ranking9.getLowLv()) {
                    continue;
                }
                minLowLv = ranking9.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtBjRanking nextRanking9 = this.taskRankingType8.get(nextSerial);
            final KtBjRanking curRanking9 = this.taskRankingType8.get(curSerial);
            if (nextRanking9 == null || !nextRanking9.getKindomLv().equals(curRanking9.getKindomLv())) {
                return 0;
            }
            return nextRanking9.getLowLv();
        }
        else if (taskType == 9) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType9.keySet()) {
                final KtHjRanking ranking10 = this.taskRankingType9.get(id);
                if (ranking10.getKindomLv() != nationRank) {
                    continue;
                }
                if (ranking10.getPeriod() != 1) {
                    continue;
                }
                if (rank <= ranking10.getLowLv() && rank >= ranking10.getHighLv()) {
                    curSerial = ranking10.getId();
                }
                if (minLowLv >= ranking10.getLowLv()) {
                    continue;
                }
                minLowLv = ranking10.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtHjRanking nextRanking10 = this.taskRankingType9.get(nextSerial);
            final KtHjRanking curRanking10 = this.taskRankingType9.get(curSerial);
            if (nextRanking10 == null || !nextRanking10.getKindomLv().equals(curRanking10.getKindomLv())) {
                return 0;
            }
            return nextRanking10.getLowLv();
        }
        else if (taskType == 10) {
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType10.keySet()) {
                final KtMrRanking ranking11 = this.taskRankingType10.get(id);
                if (ranking11.getKindomLv() != nationRank) {
                    continue;
                }
                if (ranking11.getPeriod() != 1) {
                    continue;
                }
                if (rank <= ranking11.getLowLv() && rank >= ranking11.getHighLv()) {
                    curSerial = ranking11.getId();
                }
                if (minLowLv >= ranking11.getLowLv()) {
                    continue;
                }
                minLowLv = ranking11.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtMrRanking nextRanking11 = this.taskRankingType10.get(nextSerial);
            final KtMrRanking curRanking11 = this.taskRankingType10.get(curSerial);
            if (nextRanking11 == null || !nextRanking11.getKindomLv().equals(curRanking11.getKindomLv())) {
                return 0;
            }
            return nextRanking11.getLowLv();
        }
        else {
            if (taskType != 12) {
                return 999;
            }
            int minLowLv = 0;
            for (final Integer id : this.taskRankingType12.keySet()) {
                final KtNfRanking ranking12 = this.taskRankingType12.get(id);
                if (ranking12.getKindomLv() != nationRank) {
                    continue;
                }
                if (ranking12.getPeriod() != 1) {
                    continue;
                }
                if (rank <= ranking12.getLowLv() && rank >= ranking12.getHighLv()) {
                    curSerial = ranking12.getId();
                }
                if (minLowLv >= ranking12.getLowLv()) {
                    continue;
                }
                minLowLv = ranking12.getLowLv();
            }
            if (curSerial == 0) {
                return minLowLv;
            }
            nextSerial = curSerial - 1;
            final KtNfRanking nextRanking12 = this.taskRankingType12.get(nextSerial);
            final KtNfRanking curRanking12 = this.taskRankingType12.get(curSerial);
            if (nextRanking12 == null || !nextRanking12.getKindomLv().equals(curRanking12.getKindomLv())) {
                return 0;
            }
            return nextRanking12.getLowLv();
        }
    }
    
    public int getTaskRankingLv(final int rank, final int type) {
        if (type == 1) {
            for (final Integer id : this.taskRankingType1.keySet()) {
                final KindomTaskRanking ranking = this.taskRankingType1.get(id);
                if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                    return ranking.getLv();
                }
            }
        }
        else if (type == 2) {
            for (final Integer id : this.taskRankingType2.keySet()) {
                final KtSdRanking ranking2 = this.taskRankingType2.get(id);
                if (rank <= ranking2.getLowLv() && rank >= ranking2.getHighLv()) {
                    return ranking2.getLv();
                }
            }
        }
        else if (type == 3) {
            for (final Integer id : this.taskRankingType3.keySet()) {
                final KtMzRanking ranking3 = this.taskRankingType3.get(id);
                if (rank <= ranking3.getLowLv() && rank >= ranking3.getHighLv()) {
                    return ranking3.getLv();
                }
            }
        }
        else if (type == 4) {
            for (final Integer id : this.taskRankingType4.keySet()) {
                final KtTzRanking ranking4 = this.taskRankingType4.get(id);
                if (rank <= ranking4.getLowLv() && rank >= ranking4.getHighLv()) {
                    return ranking4.getLv();
                }
            }
        }
        else if (type == 0) {
            for (final Integer id : this.taskRankingType0.keySet()) {
                final BarbarainRanking ranking5 = this.taskRankingType0.get(id);
                if (rank <= ranking5.getLowLv() && rank >= ranking5.getHighLv()) {
                    return ranking5.getLv();
                }
            }
        }
        else if (type == 5) {
            for (final Integer id : this.taskRankingType5.keySet()) {
                final KtImRanking ranking6 = this.taskRankingType5.get(id);
                if (rank <= ranking6.getLowLv() && rank >= ranking6.getHighLv()) {
                    return ranking6.getLv();
                }
            }
        }
        else if (type == 6) {
            for (final Integer id : this.taskRankingType6.keySet()) {
                final KtKjRanking ranking7 = this.taskRankingType6.get(id);
                if (rank <= ranking7.getLowLv() && rank >= ranking7.getHighLv()) {
                    return ranking7.getLv();
                }
            }
        }
        else if (type == 999) {
            for (final Integer id : this.taskRankingType1.keySet()) {
                final KindomTaskRanking ranking = this.taskRankingType1.get(id);
                if (rank <= ranking.getLowLv() && rank >= ranking.getHighLv()) {
                    return ranking.getLv();
                }
            }
        }
        else if (type == 7) {
            for (final Integer id : this.taskRankingType7.keySet()) {
                final KtSdmzRanking ranking8 = this.taskRankingType7.get(id);
                if (rank <= ranking8.getLowLv() && rank >= ranking8.getHighLv()) {
                    return ranking8.getLv();
                }
            }
        }
        else if (type == 8) {
            for (final Integer id : this.taskRankingType8.keySet()) {
                final KtBjRanking ranking9 = this.taskRankingType8.get(id);
                if (rank <= ranking9.getLowLv() && rank >= ranking9.getHighLv()) {
                    return ranking9.getLv();
                }
            }
        }
        else if (type == 9) {
            for (final Integer id : this.taskRankingType9.keySet()) {
                final KtHjRanking ranking10 = this.taskRankingType9.get(id);
                if (rank <= ranking10.getLowLv() && rank >= ranking10.getHighLv()) {
                    return ranking10.getLv();
                }
            }
        }
        else if (type == 10) {
            for (final Integer id : this.taskRankingType10.keySet()) {
                final KtMrRanking ranking11 = this.taskRankingType10.get(id);
                if (rank <= ranking11.getLowLv() && rank >= ranking11.getHighLv()) {
                    return ranking11.getLv();
                }
            }
        }
        else if (type == 12) {
            for (final Integer id : this.taskRankingType12.keySet()) {
                final KtNfRanking ranking12 = this.taskRankingType12.get(id);
                if (rank <= ranking12.getLowLv() && rank >= ranking12.getHighLv()) {
                    return ranking12.getLv();
                }
            }
        }
        return 0;
    }
    
    public String getTitleString(final int lv, final int taskType) {
        final KtRankingLv rankingLv = this.titleMap.get(lv);
        if (rankingLv == null) {
            return "";
        }
        if (taskType == 4) {
            return rankingLv.getName2();
        }
        if (taskType == 5) {
            return rankingLv.getName3();
        }
        if (taskType == 999) {
            return rankingLv.getName4();
        }
        if (taskType == 11) {
            return rankingLv.getName5();
        }
        return rankingLv.getName1();
    }
    
    public String getTitlePic(final int lv, final int taskType) {
        final KtRankingLv rankingLv = this.titleMap.get(lv);
        if (rankingLv == null) {
            return "";
        }
        if (taskType == 4) {
            return rankingLv.getPic2();
        }
        if (taskType == 5) {
            return rankingLv.getPic3();
        }
        if (taskType == 999) {
            return rankingLv.getPic4();
        }
        if (taskType == 11) {
            return rankingLv.getPic5();
        }
        return rankingLv.getPic1();
    }
    
    public String getTitlePicName(final int lv, final int taskType) {
        final KtRankingLv rankingLv = this.titleMap.get(lv);
        if (rankingLv == null) {
            return "";
        }
        if (taskType == 4) {
            return rankingLv.getName2();
        }
        if (taskType == 5) {
            return rankingLv.getName3();
        }
        if (taskType == 999) {
            return rankingLv.getName4();
        }
        if (taskType == 11) {
            return rankingLv.getName5();
        }
        return rankingLv.getName1();
    }
    
    public int getTitleQuality(final int lv, final int taskType) {
        final KtRankingLv rankingLv = this.titleMap.get(lv);
        if (rankingLv == null) {
            return 1;
        }
        if (taskType == 4) {
            return rankingLv.getQuality2();
        }
        if (taskType == 5) {
            return rankingLv.getQuality3();
        }
        if (taskType == 999) {
            return rankingLv.getQuality4();
        }
        if (taskType == 11) {
            return rankingLv.getQuality5();
        }
        return rankingLv.getQuality1();
    }
}
