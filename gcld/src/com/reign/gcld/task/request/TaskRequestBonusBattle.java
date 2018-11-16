package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.battle.scene.*;

public class TaskRequestBonusBattle extends TaskRequestBase
{
    private int bonusId;
    
    public TaskRequestBonusBattle(final String[] s) {
        this.bonusId = Integer.parseInt(s[1]);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageBonusBattle) {
            final TaskMessageBonusBattle taskMessageBonusBattle = (TaskMessageBonusBattle)message;
            if (taskMessageBonusBattle.getBonusId() == this.bonusId) {
                dataGetter.getPlayerTaskDao().addProcess(vId, 1);
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 1, 1, "");
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageBonusBattle;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final PlayerArmyReward par = dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerDto.playerId, this.bonusId);
        final Battle bonusBattle = NewBattleManager.getInstance().getBattleByBatType(playerDto.playerId, 11);
        final boolean unAttacked = par.getState() == 0 && par.getNpcLost() == null && par.getBuyCount() == 0 && bonusBattle == null;
        return !unAttacked;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        TaskRequestProcessViewer rtn = null;
        final PlayerArmyReward par = dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerDto.playerId, this.bonusId);
        final Battle bonusBattle = NewBattleManager.getInstance().getBattleByBatType(playerDto.playerId, 11);
        final boolean unAttacked = par.getState() == 0 && par.getNpcLost() == null && par.getBuyCount() == 0 && bonusBattle == null;
        final boolean completed = !unAttacked;
        rtn = new TaskRequestProcessViewer(completed, 1, completed ? 1 : 0);
        return rtn;
    }
}
