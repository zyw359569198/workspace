package com.reign.gcld.task.message;

public class TaskMessageResource extends TaskMessage
{
    private int outputType;
    private int num;
    
    public TaskMessageResource(final int playerId, final int outputType, final int num) {
        super(playerId, 7);
        this.outputType = outputType;
        this.num = num;
    }
    
    public int getOutputType() {
        return this.outputType;
    }
    
    public int getNum() {
        return this.num;
    }
}
