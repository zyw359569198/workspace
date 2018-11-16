package com.reign.gcld.task.request;

import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;

public class TaskRequestAnd extends TaskRequestBase
{
    Logger logger;
    private List<ITaskRequest> requestList;
    private boolean isShowProcess;
    
    public TaskRequestAnd(final String[] targets, final boolean isShowProces) {
        this.logger = CommonLog.getLog(TaskRequestAnd.class);
        this.requestList = new ArrayList<ITaskRequest>();
        this.isShowProcess = isShowProces;
        int i = 0;
        if (targets[0].split(",")[0].equalsIgnoreCase("and")) {
            ++i;
        }
        while (i < targets.length) {
            final ITaskRequest taskRequest = TaskRequestFactory.getInstance().getTaskReQuest(targets[i]);
            if (taskRequest == null) {
                this.logger.error("Task init AND taskRequest fail in targets:[" + targets.toString() + "],target[" + targets[i] + "]");
                throw new RuntimeException("Task init AND taskRequest fail in targets:[" + targets.toString() + "],target[" + targets[i] + "]");
            }
            this.requestList.add(taskRequest);
            ++i;
        }
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        boolean rtn = true;
        for (final ITaskRequest taskRequest : this.requestList) {
            if (!taskRequest.check(playerDto, dataGetter, vId)) {
                rtn = false;
                break;
            }
        }
        return rtn;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final List<TaskRequestProcessViewer> list = new ArrayList<TaskRequestProcessViewer>();
        for (final ITaskRequest taskRequest : this.requestList) {
            list.add(taskRequest.getProcess(playerDto, dataGetter, vId));
        }
        return new TaskRequestProcessViewerComposite(list, 1, this.isShowProcess);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (!this.isConcernedMessage(message)) {
            return;
        }
        PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
        if (playerDto == null) {
            final Player player = dataGetter.getPlayerDao().read(message.getPlayerId());
            playerDto = new PlayerDto(message.getPlayerId(), player.getForceId());
        }
        final List<TaskRequestProcessViewer> list = new ArrayList<TaskRequestProcessViewer>();
        for (final ITaskRequest taskRequest : this.requestList) {
            list.add(taskRequest.getProcess(playerDto, dataGetter, vId));
        }
        final TaskRequestProcessViewerComposite viewer = new TaskRequestProcessViewerComposite(list, 1, this.isShowProcess);
        final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
        Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        boolean rtn = false;
        for (final ITaskRequest taskRequest : this.requestList) {
            if (taskRequest.isConcernedMessage(message)) {
                rtn = true;
                break;
            }
        }
        return rtn;
    }
    
    public List<ITaskRequest> getRequestList() {
        return this.requestList;
    }
}
