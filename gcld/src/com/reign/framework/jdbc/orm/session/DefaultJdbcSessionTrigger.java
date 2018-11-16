package com.reign.framework.jdbc.orm.session;

import com.reign.framework.jdbc.orm.*;

public class DefaultJdbcSessionTrigger implements JdbcSessionTrigger, Comparable<DefaultJdbcSessionTrigger>
{
    private String tableName;
    private int triggerType;
    private JdbcSessionTrigger trigger;
    private JdbcEntity entity;
    
    public DefaultJdbcSessionTrigger(final JdbcEntity entity, final String tableName, final int triggerType, final JdbcSessionTrigger trigger) {
        this.tableName = tableName;
        this.triggerType = triggerType;
        this.trigger = trigger;
        (this.entity = entity).enableDelaySQL();
    }
    
    @Override
    public void trigger() {
        try {
            this.trigger.trigger();
        }
        finally {
            this.entity.resetDelaySQLFlag();
        }
        this.entity.resetDelaySQLFlag();
    }
    
    @Override
    public int compareTo(final DefaultJdbcSessionTrigger o) {
        if (this.triggerType == o.triggerType) {
            return this.tableName.compareTo(o.tableName);
        }
        if (this.triggerType > o.triggerType) {
            return 1;
        }
        return -1;
    }
}
