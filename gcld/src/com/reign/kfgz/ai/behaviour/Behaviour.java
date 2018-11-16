package com.reign.kfgz.ai.behaviour;

public abstract class Behaviour implements Runnable
{
    public abstract long getExecuteTime();
    
    public abstract void setExecuteTime(final long p0);
}
