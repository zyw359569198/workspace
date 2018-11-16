package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("nationIndivTaskCache")
public class NationIndivTaskCache extends AbstractCache<Integer, NationIndivTask>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Map<Integer, Double>> randMap;
    private Map<Integer, IndivTasks> indivTaskMap;
    private Map<Integer, Integer> typeMap;
    private Map<Integer, InMemmoryIndivTask> infosMap;
    
    public NationIndivTaskCache() {
        this.randMap = new HashMap<Integer, Map<Integer, Double>>();
        this.indivTaskMap = new HashMap<Integer, IndivTasks>();
        this.typeMap = new HashMap<Integer, Integer>();
        this.infosMap = new HashMap<Integer, InMemmoryIndivTask>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<NationIndivTask> list = this.dataLoader.getModels((Class)NationIndivTask.class);
        final List<IndivTasks> tasks = this.dataLoader.getModels((Class)IndivTasks.class);
        for (final NationIndivTask task : list) {
            super.put((Object)task.getId(), (Object)task);
            Map<Integer, Double> map1 = this.randMap.get(task.getId());
            if (map1 == null) {
                map1 = new HashMap<Integer, Double>();
                this.randMap.put(task.getId(), map1);
            }
            final String randRule = task.getRandRule();
            final String goldRule = task.getGoldRule();
            final String[] rands = randRule.split(";");
            double sum = 0.0;
            String[] array;
            for (int length = (array = rands).length, i = 0; i < length; ++i) {
                final String cell = array[i];
                final String[] cells = cell.split(",");
                final int grade = Integer.parseInt(cells[0]);
                final double value = Double.parseDouble(cells[1]);
                sum += value;
                if (!map1.containsKey(grade)) {
                    map1.put(grade, value);
                }
            }
            if (Math.abs(sum - 1.0) >= 1.0E-7) {
                throw new RuntimeException("sum of rand_rules not equal 1...");
            }
            if (StringUtils.isBlank(goldRule)) {}
        }
        final Set<String> keySet = new HashSet<String>();
        InMemmoryIndivTask indivTask = null;
        for (final IndivTasks task2 : tasks) {
            this.indivTaskMap.put(task2.getId(), task2);
            final String key = task2.getTaskType() + "-" + task2.getIndivTaskType();
            if (!keySet.contains(key)) {
                keySet.add(key);
                final Integer number = this.typeMap.get(task2.getTaskType());
                if (number == null) {
                    this.typeMap.put(task2.getTaskType(), 1);
                }
                else {
                    this.typeMap.put(task2.getTaskType(), number + 1);
                }
            }
            indivTask = new InMemmoryIndivTask();
            indivTask.id = task2.getId();
            indivTask.taskType = task2.getTaskType();
            indivTask.indivTaskType = task2.getIndivTaskType();
            indivTask.grade = task2.getGrade();
            indivTask.name = task2.getName();
            indivTask.intro = task2.getIntro();
            indivTask.req = InMemmoryIndivTaskRequestCreate.creatRequest(task2.getReq());
            indivTask.reward = InMemmoryIndivTaskRewardCreate.creatReward(task2.getReward());
            indivTask.pic = task2.getPic();
            this.infosMap.put(task2.getId(), indivTask);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        this.typeMap.clear();
        this.randMap.clear();
        this.infosMap.clear();
        this.indivTaskMap.clear();
    }
    
    public IndivTasks getIndivTasks(final int id) {
        return this.indivTaskMap.get(id);
    }
    
    public List<MultiResult> getInitThreeTasks(final int forceLv, final int taskType) {
        final NationIndivTask task = (NationIndivTask)super.get((Object)forceLv);
        final Map<Integer, Double> map = this.randMap.get(forceLv);
        if (task == null || map == null) {
            return null;
        }
        final List<MultiResult> result = new ArrayList<MultiResult>();
        final int totalNumber = this.typeMap.get(taskType);
        final Set<Integer> set = new HashSet<Integer>();
        MultiResult temp = null;
        for (int i = 0; i < 3; ++i) {
            final int crit = getCritIndex(map);
            temp = new MultiResult();
            int randNum;
            for (randNum = WebUtil.nextInt(totalNumber); set.contains(randNum); randNum = (randNum + 1) % totalNumber) {}
            temp.result1 = randNum + 1;
            temp.result2 = crit;
            temp.result3 = 0;
            result.add(temp);
            set.add(randNum);
        }
        return result;
    }
    
    public static int getCritIndex(final Map<Integer, Double> map) {
        final double prob = WebUtil.nextDouble();
        double sum = 0.0;
        int index = 0;
        for (final Integer key : map.keySet()) {
            sum += map.get(key);
            if (prob <= sum) {
                index = key;
                break;
            }
        }
        if (map.keySet().contains(index)) {
            return index;
        }
        return 1;
    }
    
    public InMemmoryIndivTask getInMemmoryIndivTaskById(final int id) {
        return this.infosMap.get(id);
    }
    
    public InMemmoryIndivTask getInMemmoryIndivTaskBy2TypeAndGrade(final int taskType, final int indivTaskType, final int grade) {
        for (final InMemmoryIndivTask task : this.infosMap.values()) {
            if (task.taskType == taskType && indivTaskType == task.indivTaskType && task.grade == grade) {
                return task;
            }
        }
        return null;
    }
}
