package com.reign.gcld.task.message;

public class TaskMessageKillNum extends TaskMessage
{
    private int killNum;
    private int killTotal;
    
    public int getKillTotal() {
        return this.killTotal;
    }
    
    public void setKillTotal(final int killTotal) {
        this.killTotal = killTotal;
    }
    
    public int getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final int killNum) {
        this.killNum = killNum;
    }
    
    public TaskMessageKillNum(final int playerId, final int num, final int killTotal) {
        super(playerId, 130);
        this.killNum = num;
        this.killTotal = killTotal;
    }
}
