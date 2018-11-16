package com.reign.gcld.task.message;

public class TaskMessageBonusBattle extends TaskMessage
{
    int bonusId;
    
    public TaskMessageBonusBattle(final int playerId, final int bonusId) {
        super(playerId, 65);
        this.bonusId = bonusId;
    }
    
    public int getBonusId() {
        return this.bonusId;
    }
    
    public void setBonusId(final int bonusId) {
        this.bonusId = bonusId;
    }
}
