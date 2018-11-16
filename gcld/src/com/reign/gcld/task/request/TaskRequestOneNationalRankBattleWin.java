package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;

public class TaskRequestOneNationalRankBattleWin extends TaskRequestCount
{
    public TaskRequestOneNationalRankBattleWin(final String[] s) {
        super(1);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageOneNationalRankBattleWin) {
            final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
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
        return message instanceof TaskMessageOneNationalRankBattleWin;
    }
}
