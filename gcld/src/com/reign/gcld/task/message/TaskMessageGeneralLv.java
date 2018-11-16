package com.reign.gcld.task.message;

public class TaskMessageGeneralLv extends TaskMessage
{
    private int lv;
    
    public TaskMessageGeneralLv(final int playerId, final int lv) {
        super(playerId, 19);
        this.lv = lv;
    }
    
    public int getLv() {
        return this.lv;
    }
}
