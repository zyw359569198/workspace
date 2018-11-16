package com.reign.gcld.task.request;

import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.common.*;

public class TaskRequestOr extends TaskRequestBase
{
    Logger logger;
    private List<ITaskRequest> requestList;
    private boolean isShowProcess;
    
    public TaskRequestOr(final String[] targets, final boolean isShowProcess) {
        this.logger = CommonLog.getLog(TaskRequestAnd.class);
        this.requestList = new ArrayList<ITaskRequest>();
        this.isShowProcess = isShowProcess;
        int i = 0;
        if (targets[0].split(",")[0].equalsIgnoreCase("or")) {
            ++i;
        }
        while (i < targets.length) {
            final ITaskRequest taskRequest = TaskRequestFactory.getInstance().getTaskReQuest(targets[i]);
            if (taskRequest == null) {
                this.logger.error("Task init [OR] taskRequest fail in targets:[" + targets.toString() + "],target[" + targets[i] + "]");
                throw new RuntimeException("Task init [OR] taskRequest fail in targets:[" + targets.toString() + "],target[" + targets[i] + "]");
            }
            this.requestList.add(taskRequest);
            ++i;
        }
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        boolean rtn = false;
        for (final ITaskRequest taskRequest : this.requestList) {
            if (taskRequest.check(playerDto, dataGetter, vId)) {
                rtn = true;
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
        return new TaskRequestProcessViewerComposite(list, 2, this.isShowProcess);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (!this.isConcernedMessage(message)) {
            return;
        }
        final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
        final List<TaskRequestProcessViewer> list = new ArrayList<TaskRequestProcessViewer>();
        for (final ITaskRequest taskRequest : this.requestList) {
            taskRequest.handleMessage(message, dataGetter, vId);
            list.add(taskRequest.getProcess(playerDto, dataGetter, vId));
        }
        final TaskRequestProcessViewerComposite viewer = new TaskRequestProcessViewerComposite(list, 2, this.isShowProcess);
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
    
    @Override
    public void setTask(final GameTask gameTask) {
        super.setTask(gameTask);
        for (final ITaskRequest taskRequest : this.requestList) {
            taskRequest.setTask(gameTask);
        }
    }
    
    public List<ITaskRequest> getRequestList() {
        return this.requestList;
    }
}
