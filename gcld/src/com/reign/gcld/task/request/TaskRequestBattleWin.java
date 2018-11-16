package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestBattleWin extends TaskRequestBase
{
    private int armyId;
    private int winNum;
    
    public TaskRequestBattleWin(final String[] s) {
        this.armyId = Integer.parseInt(s[1]);
        if (s.length > 2) {
            this.winNum = Integer.parseInt(s[2]);
        }
        else {
            this.winNum = 1;
        }
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final PlayerArmy playerArmy = taskDataGetter.getPlayerArmyDao().getPlayerArmy(playerDto.playerId, this.armyId);
        return playerArmy != null && playerArmy.getWinNum() >= this.winNum;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageBattleWin) {
            final TaskMessageBattleWin taskMessageBattleWin = (TaskMessageBattleWin)message;
            if (taskMessageBattleWin.getArmyId() == this.armyId) {
                final PlayerArmy playerArmy = taskDataGetter.getPlayerArmyDao().getPlayerArmy(taskMessageBattleWin.getPlayerId(), this.armyId);
                if (playerArmy.getWinNum() >= this.winNum) {
                    final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1, "");
                    final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                    final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
                    Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
                }
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageBattleWin;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final PlayerArmy playerArmy = dataGetter.getPlayerArmyDao().getPlayerArmy(playerDto.playerId, this.armyId);
        int curNum = 0;
        if (playerArmy != null) {
            curNum = playerArmy.getWinNum();
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(curNum >= this.winNum, this.winNum, curNum, "");
        return rtn;
    }
}
