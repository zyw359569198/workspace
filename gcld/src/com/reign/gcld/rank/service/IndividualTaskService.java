package com.reign.gcld.rank.service;

import org.springframework.stereotype.*;
import com.reign.gcld.log.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.common.*;
import org.springframework.transaction.annotation.*;
import com.reign.util.*;
import com.reign.gcld.battle.reward.*;
import java.util.*;

@Component("individualTaskService")
public class IndividualTaskService implements IIndividualTaskService
{
    private static ErrorLogger log;
    @Autowired
    private IDataGetter getter;
    
    static {
        IndividualTaskService.log = new ErrorLogger();
    }
    
    @Override
    public byte[] getIndiviInfo(final PlayerDto playerDto) {
        final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        if (!manager.isIndivTaskTime(forceId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.INDIVTASK_NO_TASKS);
        }
        final Map<Integer, InMemmoryIndivTask> map = manager.getTasks().get(playerId);
        Collection<InMemmoryIndivTask> list = null;
        if (map == null) {
            list = manager.getDefaultTasksByForceId(forceId);
            if (list == null || list.size() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.INDIVTASK_NO_TASKS);
            }
        }
        else {
            list = map.values();
        }
        final JsonDocument doc = IndividualJsonBuilder.getTasksJson(list);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void sendTaskMessage(final String params) {
        final InMemmoryIndivTaskMessage message = new InMemmoryIndivTaskMessage(params);
        InMemmoryIndivTaskManager.getInstance().handleMessage(message);
    }
    
    @Override
    public byte[] getIndiviInfo(final int playerId, final int forceId) {
        try {
            final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
            final Map<Integer, InMemmoryIndivTask> map = manager.getTasks().get(playerId);
            Collection<InMemmoryIndivTask> list = null;
            if (map == null) {
                list = manager.getDefaultTasksByForceId(forceId);
            }
            else {
                list = map.values();
            }
            final JsonDocument doc = IndividualJsonBuilder.getAppendTasksJson(list, playerId, this.getter);
            return doc.toByte();
        }
        catch (Exception e) {
            IndividualTaskService.log.error(this, e);
            return null;
        }
    }
    
    @Override
    public byte[] getRightBarIndivTaskInfo(final PlayerDto playerDto) {
        try {
            final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
            final int playerId = playerDto.playerId;
            final int forceId = playerDto.forceId;
            final int hasNationTasks = this.getter.getRankService().hasNationTasks(forceId);
            final Map<Integer, InMemmoryIndivTask> map = manager.getTasks().get(playerId);
            Collection<InMemmoryIndivTask> list = null;
            if (map == null) {
                list = manager.getDefaultTasksByForceId(forceId);
            }
            else {
                list = map.values();
            }
            final JsonDocument doc = IndividualJsonBuilder.getAppendTasksSimpleJson(list, playerId, this.getter, hasNationTasks);
            return doc.toByte();
        }
        catch (Exception e) {
            IndividualTaskService.log.error(this, e);
            return null;
        }
    }
    
    @Override
    public void sendTaskMessage(final PlayerDto playerDto, final int count, final String type) {
        try {
            if (playerDto.playerId <= 0) {
                return;
            }
            final int forceId = playerDto.forceId;
            if (forceId < 1 || forceId > 3) {
                return;
            }
            final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
            if (playerDto == null || !manager.isIndivTaskTime(forceId) || !manager.concernedMessage(forceId, type)) {
                return;
            }
            final InMemmoryIndivTaskMessage message = new InMemmoryIndivTaskMessage();
            message.playerId = playerDto.playerId;
            message.forceId = forceId;
            message.identifier = type;
            message.count = count;
            AddJob.getInstance().addJob(JobClassMethondEnum.INDIVIDUALTASK_SENDMESSAGE, message.toString(), 0L);
        }
        catch (Exception e) {
            IndividualTaskService.log.error(this, e);
        }
    }
    
    @Transactional
    @Override
    public byte[] getIndivReward(final PlayerDto playerDto, final int id) {
        return this.getReward(playerDto, id);
    }
    
    @Override
    public byte[] getReward(final PlayerDto playerDto, final int id) {
        final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
        final Map<Integer, InMemmoryIndivTask> playerTasks = manager.getTaskByPlayerId(playerDto.playerId);
        if (!playerTasks.containsKey(id)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        synchronized (playerTasks) {
            final InMemmoryIndivTask task = playerTasks.get(id);
            if (task == null || task.req == null) {
                // monitorexit(playerTasks)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (!task.req.isFinished() || task.req.getHasRewarded() == 1) {
                // monitorexit(playerTasks)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            task.getDelegate().handleReward(this.getter, playerDto);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public Tuple<byte[], String> getReward(final PlayerDto playerDto) {
        try {
            final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
            final Map<Integer, InMemmoryIndivTask> playerTasks = manager.getTaskByPlayerId(playerDto.playerId);
            if (playerTasks.isEmpty()) {
                return null;
            }
            int count = 0;
            final StringBuffer sb = new StringBuffer();
            synchronized (playerTasks) {
                for (final InMemmoryIndivTask task : playerTasks.values()) {
                    if (task.req != null && task.req.isFinished()) {
                        if (task.req.getHasRewarded() == 1) {
                            continue;
                        }
                        ++count;
                        sb.append(task.getDelegate().handleReward(this.getter, playerDto)).append(";");
                    }
                }
            }
            // monitorexit(playerTasks)
            if (count <= 0) {
                return null;
            }
            final String lastString = RewardType.mergeRewards(sb.toString(), "");
            final Tuple<byte[], String> result = new Tuple();
            final byte[] bytes = RewardType.rewards(lastString, "indivRewards");
            result.left = bytes;
            result.right = lastString;
            return result;
        }
        catch (Exception e) {
            IndividualTaskService.log.error(this, e);
            return null;
        }
    }
}
