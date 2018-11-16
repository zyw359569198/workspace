package com.reign.gcld.task.request;

import com.reign.gcld.common.log.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.service.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.player.domain.*;

public class TaskRequestWorldTreasureByType extends TaskRequestCount
{
    private static final Logger log;
    private int boxType;
    
    static {
        log = CommonLog.getLog(TaskRequestWorldTreasure.class);
    }
    
    public TaskRequestWorldTreasureByType(final String[] strings) {
        super(1);
        this.boxType = Integer.valueOf(strings[1]);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageWorldTreasureByType) {
            final TaskMessageWorldTreasureByType taskMessageWorldTreasure = (TaskMessageWorldTreasureByType)message;
            final int requestId = taskMessageWorldTreasure.getBoxType();
            final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            if (this.boxType != 0 && requestId != this.boxType) {
                return;
            }
            dataGetter.getPlayerTaskDao().addProcess(vId, 1);
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(playerTask.getProcess() + 1 >= this.getTimes(), this.getTimes(), playerTask.getProcess() + 1);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageWorldTreasureByType;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerTask playerTask = taskDataGetter.getPlayerTaskDao().read(vId);
        final boolean isCompleted = playerTask != null && (playerTask.getState() == 2 || playerTask.getProcess() >= this.getTimes());
        return isCompleted | this.hasGottenBox(playerDto, taskDataGetter);
    }
    
    private boolean hasGottenBox(final PlayerDto playerDto, final IDataGetter taskDataGetter) {
        final PlayerWorld pw = taskDataGetter.getPlayerWorldDao().read(playerDto.playerId);
        final Player player = taskDataGetter.getPlayerDao().read(playerDto.playerId);
        try {
            int[] forceBoxArray = null;
            switch (player.getForceId()) {
                case 1: {
                    forceBoxArray = CityService.wei;
                    break;
                }
                case 2: {
                    forceBoxArray = CityService.shu;
                    break;
                }
                case 3: {
                    forceBoxArray = CityService.wu;
                    break;
                }
            }
            final String boxIsPicked = pw.getBoxispicked();
            final String[] box = boxIsPicked.split("\\|");
            for (int i = 0; i < box.length; ++i) {
                final int real = StringUtils.isBlank(box[i]) ? 0 : Integer.parseInt(box[i]);
                if (forceBoxArray[i] != 0 && real != 0) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e) {
            TaskRequestWorldTreasureByType.log.error("TaskRequestWorldTreasureByType hasGottenBox ", e);
            return false;
        }
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        TaskRequestProcessViewer tViewer = null;
        final boolean flag = this.check(playerDto, taskDataGetter, vId);
        tViewer = new TaskRequestProcessViewer(flag, 1, flag ? 1 : 0);
        return tViewer;
    }
}
