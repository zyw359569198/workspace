package com.reign.gcld.task.message;

public class TaskMessageBattleWin extends TaskMessage
{
    private int armyId;
    
    public TaskMessageBattleWin(final int playerId, final int armyId) {
        super(playerId, 10);
        this.armyId = armyId;
    }
    
    public int getArmyId() {
        return this.armyId;
    }
}
