package com.reign.gcld.rank.common;

import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class InMemmoryIndivTaskManager
{
    private static ErrorLogger log;
    private static InMemmoryIndivTaskManager instance;
    private ConcurrentHashMap<Integer, Map<Integer, InMemmoryIndivTask>> tasks;
    private Map<Integer, List<InMemmoryIndivTask>> defaultTasks;
    private Map<Integer, List<String>> forceIdTypeMap;
    private CopyOnWriteArrayList<Integer> participateCountrySet;
    private volatile boolean[] initialOver;
    private IDataGetter getter;
    private final int DIAMOND_DROP_NUM = 5;
    
    static {
        InMemmoryIndivTaskManager.log = new ErrorLogger();
        InMemmoryIndivTaskManager.instance = null;
    }
    
    public InMemmoryIndivTaskManager() {
        this.tasks = new ConcurrentHashMap<Integer, Map<Integer, InMemmoryIndivTask>>();
        this.defaultTasks = new ConcurrentHashMap<Integer, List<InMemmoryIndivTask>>();
        this.forceIdTypeMap = new ConcurrentHashMap<Integer, List<String>>();
        this.participateCountrySet = new CopyOnWriteArrayList<Integer>();
        this.initialOver = new boolean[3];
    }
    
    public static InMemmoryIndivTaskManager getInstance() {
        if (InMemmoryIndivTaskManager.instance == null) {
            InMemmoryIndivTaskManager.instance = new InMemmoryIndivTaskManager();
        }
        return InMemmoryIndivTaskManager.instance;
    }
    
    public void init(final IDataGetter dataGetter) {
        this.getter = dataGetter;
        final List<ForceInfo> forceInfos = dataGetter.getForceInfoDao().getModels();
        InMemmoryIndivTask indivTask = null;
        List<InMemmoryIndivTask> defaultList = null;
        final Map<Integer, Set<Integer>> temMap = new HashMap<Integer, Set<Integer>>();
        List<String> typeList = null;
        final boolean[] flag = new boolean[3];
        for (final ForceInfo forceInfo : forceInfos) {
            final String info = forceInfo.getNationIndivInfo();
            if (StringUtils.isBlank(info)) {
                continue;
            }
            final String[] infos = info.split(",");
            defaultList = Collections.synchronizedList(new ArrayList<InMemmoryIndivTask>());
            typeList = Collections.synchronizedList(new ArrayList<String>());
            final Set<Integer> temp = new HashSet<Integer>();
            String[] array;
            for (int length = (array = infos).length, i = 0; i < length; ++i) {
                final String cell = array[i];
                final Integer integer = Integer.parseInt(cell);
                int id = integer;
                if (integer > 1000) {
                    id = integer - 1000;
                }
                indivTask = this.getter.getNationIndivTaskCache().getInMemmoryIndivTaskById(id);
                try {
                    indivTask = indivTask.clone();
                }
                catch (CloneNotSupportedException e) {
                    InMemmoryIndivTaskManager.log.error(this, e);
                    continue;
                }
                if (id != integer) {
                    indivTask.canUpdate = true;
                }
                defaultList.add(indivTask);
                if (indivTask.req != null) {
                    typeList.add(indivTask.req.identifier);
                }
                temp.add(id);
            }
            this.defaultTasks.put(forceInfo.getForceId(), defaultList);
            this.forceIdTypeMap.put(forceInfo.getForceId(), typeList);
            temMap.put(forceInfo.getForceId(), temp);
            this.participateCountrySet.add(forceInfo.getForceId());
            flag[forceInfo.getForceId() - 1] = true;
        }
        final List<PlayerIndivTask> list = dataGetter.getPlayerIndivTaskDao().getModels();
        Map<Integer, InMemmoryIndivTask> tempForceMap = null;
        if (list != null && !list.isEmpty()) {
            InMemmoryIndivTask inMemmoryIndivTask = null;
            for (final PlayerIndivTask task : list) {
                final String info2 = task.getIndivTaskInfo();
                final int playerId = task.getPlayerId();
                final int forceId = task.getForceId();
                tempForceMap = this.tasks.get(playerId);
                if (tempForceMap == null) {
                    tempForceMap = new ConcurrentHashMap<Integer, InMemmoryIndivTask>();
                    this.tasks.put(playerId, tempForceMap);
                }
                if (StringUtils.isBlank(info2)) {
                    continue;
                }
                if (temMap == null) {
                    continue;
                }
                if (temMap.get(forceId) == null) {
                    continue;
                }
                try {
                    final String[] infos2 = info2.split(";");
                    String[] array2;
                    for (int length2 = (array2 = infos2).length, j = 0; j < length2; ++j) {
                        final String cell2 = array2[j];
                        final String[] single = cell2.split(":");
                        final int id2 = Integer.parseInt(single[0]);
                        final String[] vsInfo = single[1].split(",");
                        final int count = Integer.parseInt(vsInfo[1]);
                        int hasRewarded = 0;
                        int itemId = 0;
                        int itemNum = 0;
                        if (vsInfo.length >= 3) {
                            hasRewarded = Integer.parseInt(vsInfo[2]);
                        }
                        else {
                            hasRewarded = 1;
                        }
                        if (vsInfo.length >= 4) {
                            itemId = Integer.parseInt(vsInfo[3]);
                        }
                        if (vsInfo.length >= 5) {
                            itemNum = Integer.parseInt(vsInfo[4]);
                        }
                        final InMemmoryIndivTask toInserTask = dataGetter.getNationIndivTaskCache().getInMemmoryIndivTaskById(id2);
                        if (toInserTask == null) {
                            InMemmoryIndivTaskManager.log.error("InMemmoryIndivTaskManager init cannot find the specific id task...id:" + id2);
                        }
                        else {
                            inMemmoryIndivTask = toInserTask.clone();
                            if (temMap.get(forceId) != null && !temMap.get(forceId).contains(id2)) {
                                final int addId = id2 + 1000;
                                if (temMap.get(forceId).contains(addId)) {
                                    inMemmoryIndivTask.canUpdate = true;
                                }
                                else {
                                    inMemmoryIndivTask.hasUpdate = true;
                                }
                            }
                            if (inMemmoryIndivTask.req != null) {
                                inMemmoryIndivTask.req.restore(count, hasRewarded);
                                inMemmoryIndivTask.reward.itemsId = itemId;
                                inMemmoryIndivTask.reward.itemNum = itemNum;
                            }
                            tempForceMap.put(id2, inMemmoryIndivTask);
                        }
                    }
                }
                catch (Exception e2) {
                    InMemmoryIndivTaskManager.log.error(this, e2);
                }
            }
        }
        this.initialOver[0] = flag[0];
        this.initialOver[1] = flag[1];
        this.initialOver[2] = flag[2];
    }
    
    public void initialOver() {
        this.initialOver[0] = true;
        this.initialOver[1] = true;
        this.initialOver[2] = true;
    }
    
    public void handleMessage(final InMemmoryIndivTaskMessage message) {
        try {
            final int playerId = message.playerId;
            final int forceId = message.forceId;
            if (playerId <= 0 || forceId < 1 || forceId > 3) {
                return;
            }
            if (!this.initialOver[forceId - 1] || !this.participateCountrySet.contains(forceId)) {
                return;
            }
            Map<Integer, InMemmoryIndivTask> pTask = this.tasks.get(playerId);
            if (pTask == null) {
                pTask = copyProperties(this.defaultTasks.get(forceId));
                final Map<Integer, InMemmoryIndivTask> pre = this.tasks.putIfAbsent(playerId, pTask);
                if (pre != null) {
                    InMemmoryIndivTaskManager.log.error("handleMessage pre is not null playerId:" + playerId);
                    pTask = pre;
                }
            }
            synchronized (pTask) {
                for (final InMemmoryIndivTask task : pTask.values()) {
                    task.getDelegate().handle(message, this.getter);
                }
            }
        }
        catch (Exception e) {
            InMemmoryIndivTaskManager.log.error(this, e);
        }
    }
    
    public static Map<Integer, InMemmoryIndivTask> copyProperties(final List<InMemmoryIndivTask> list) {
        final Map<Integer, InMemmoryIndivTask> result = new ConcurrentHashMap<Integer, InMemmoryIndivTask>();
        for (final InMemmoryIndivTask key : list) {
            try {
                result.put(key.id, key.clone());
            }
            catch (CloneNotSupportedException e) {
                InMemmoryIndivTaskManager.log.error("InMemmoryIndivTaskManager copyProperties", e);
            }
        }
        return result;
    }
    
    public void initDefaultTasks(final int forceId, final int forceLv, final int taskType) {
        try {
            if (forceId < 1 || forceId > 3 || this.initialOver[forceId - 1]) {
                return;
            }
            final NationIndivTask task = (NationIndivTask)this.getter.getNationIndivTaskCache().get((Object)forceLv);
            if (task == null) {
                return;
            }
            this.participateCountrySet.add(forceId);
            final List<MultiResult> list = this.getter.getNationIndivTaskCache().getInitThreeTasks(forceLv, taskType);
            this.initTasksByList(list, forceId, taskType);
        }
        catch (Exception e) {
            InMemmoryIndivTaskManager.log.error("initDefaultTasks error forceId:" + forceId, e);
        }
    }
    
    public void initTasksByList(final List<MultiResult> list, final int forceId, final int taskType) {
        if (list == null || list.size() < 3) {
            InMemmoryIndivTaskManager.log.error("initDefaultTasks number is not 3...");
        }
        InMemmoryIndivTask indivTask = null;
        InMemmoryIndivTask temp = null;
        List<InMemmoryIndivTask> tasks = this.defaultTasks.get(forceId);
        if (tasks == null) {
            tasks = Collections.synchronizedList(new ArrayList<InMemmoryIndivTask>());
            this.defaultTasks.put(forceId, tasks);
        }
        final StringBuffer sb = new StringBuffer();
        final List<String> typeList = Collections.synchronizedList(new ArrayList<String>());
        for (final MultiResult result : list) {
            final int type = (int)result.result1;
            final int grade = (int)result.result2;
            final int canUpdate = (int)result.result3;
            indivTask = this.getter.getNationIndivTaskCache().getInMemmoryIndivTaskBy2TypeAndGrade(taskType, type, grade);
            if (indivTask == null) {
                InMemmoryIndivTaskManager.log.error("InMemmoryIndivTaskManager initTasksByList cannot get indivTask taskType:" + taskType + " type:" + type + " grade:" + grade);
                indivTask = this.getter.getNationIndivTaskCache().getInMemmoryIndivTaskBy2TypeAndGrade(taskType, 1, 1);
            }
            try {
                temp = indivTask.clone();
            }
            catch (CloneNotSupportedException e) {
                InMemmoryIndivTaskManager.log.error(this, e);
            }
            if (canUpdate > 0) {
                temp.canUpdate = true;
            }
            if (temp.taskType == 1) {
                this.randomGemShopDrop(temp);
            }
            tasks.add(temp);
            final int tempToRestore = temp.id + canUpdate * 1000;
            sb.append(tempToRestore).append(",");
            if (temp.req != null) {
                typeList.add(temp.req.identifier);
                InMemmoryIndivTaskManager.log.error("initTasksByList addType:" + temp.req.identifier + " forceId:" + forceId);
            }
        }
        this.forceIdTypeMap.put(forceId, typeList);
        SymbolUtil.removeTheLast(sb);
        this.getter.getForceInfoDao().updateNationIndivId(forceId, sb.toString());
    }
    
    private void randomGemShopDrop(final InMemmoryIndivTask temp) {
        final int itemsId = this.getter.getHmGtDropCache().getRandomItems();
        final int num = 5;
        temp.reward.itemsId = itemsId;
        temp.reward.itemNum = num;
    }
    
    public void clearAfterTaskIsOver() {
        this.initialOver[0] = false;
        this.initialOver[1] = false;
        this.initialOver[2] = false;
    }
    
    public void clearNationIndivTask(final int forceId) {
        if (forceId < 1 || forceId > 3) {
            return;
        }
        this.initialOver[forceId - 1] = false;
        InMemmoryIndivTaskManager.log.error("NATIONINDIVTASK-----clearNationIndivTask forceId:" + forceId);
    }
    
    public ConcurrentHashMap<Integer, Map<Integer, InMemmoryIndivTask>> getTasks() {
        return this.tasks;
    }
    
    public void setTasks(final ConcurrentHashMap<Integer, Map<Integer, InMemmoryIndivTask>> tasks) {
        this.tasks = tasks;
    }
    
    public List<InMemmoryIndivTask> getDefaultTasksByForceId(final int forceId) {
        return (this.defaultTasks == null) ? null : this.defaultTasks.get(forceId);
    }
    
    public boolean isIndivTaskTime(final int forceId) {
        return forceId >= 1 && forceId <= 3 && this.initialOver[forceId - 1];
    }
    
    public Map<Integer, List<InMemmoryIndivTask>> getDefaultTasks() {
        return this.defaultTasks;
    }
    
    public void setDefaultTasks(final Map<Integer, List<InMemmoryIndivTask>> defaultTasks) {
        this.defaultTasks = defaultTasks;
    }
    
    public boolean concernedMessage(final int forceId, final String type) {
        final List<String> list = this.forceIdTypeMap.get(forceId);
        return list != null && list.contains(type);
    }
    
    public Map<Integer, InMemmoryIndivTask> getTaskByPlayerId(final int playerId) {
        return (this.tasks.get(playerId) == null) ? new HashMap<Integer, InMemmoryIndivTask>() : this.tasks.get(playerId);
    }
    
    public void clearNextTaskStart() {
        this.rewardAll();
        this.initialOver[0] = false;
        this.initialOver[1] = false;
        this.initialOver[2] = false;
        this.tasks.clear();
        this.participateCountrySet.clear();
        this.defaultTasks.clear();
        if (this.getter != null) {
            this.getter.getPlayerIndivTaskDao().deleteAll();
        }
        this.forceIdTypeMap.clear();
        InMemmoryIndivTaskManager.log.error("NATIONINDIVTASK-----clearAfterTaskIsOver");
    }
    
    private void rewardAll() {
        Player player = null;
        String msg = null;
        for (final Integer playerId : this.tasks.keySet()) {
            try {
                player = this.getter.getPlayerDao().read(playerId);
                final Tuple<byte[], String> result = this.getter.getIndividualTaskService().getReward(new PlayerDto(playerId, player.getForceId()));
                if (result == null || result.right == null) {
                    continue;
                }
                msg = RewardType.getRewardsString(result.right);
                if (StringUtils.isBlank(msg)) {
                    continue;
                }
                final String mailContents = MessageFormatter.format(LocalMessages.NATION_TASK_INDIV_MAIL_CONTENT, new Object[] { msg });
                this.getter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.NATION_TASK_MAIL_HEADER_1, mailContents, 1, playerId, new Date());
            }
            catch (Exception e) {
                InMemmoryIndivTaskManager.log.error(this, e);
            }
        }
    }
}
