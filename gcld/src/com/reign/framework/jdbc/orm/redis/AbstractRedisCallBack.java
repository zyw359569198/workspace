package com.reign.framework.jdbc.orm.redis;

public abstract class AbstractRedisCallBack implements RedisCallBack
{
    public boolean doInTransactionSucc;
    
    public AbstractRedisCallBack(final boolean doInTransactionSucc) {
        this.doInTransactionSucc = doInTransactionSucc;
    }
}
