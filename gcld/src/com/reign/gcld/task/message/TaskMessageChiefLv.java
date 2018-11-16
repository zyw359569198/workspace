package com.reign.gcld.task.message;

public class TaskMessageChiefLv extends TaskMessage
{
    private int lv;
    
    public TaskMessageChiefLv(final int playerId, final int lv) {
        super(playerId, 21);
        this.lv = lv;
    }
    
    public int getLv() {
        return this.lv;
    }
}
