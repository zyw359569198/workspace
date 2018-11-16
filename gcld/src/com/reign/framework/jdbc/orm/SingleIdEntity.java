package com.reign.framework.jdbc.orm;

import com.reign.framework.jdbc.orm.annotation.*;

public class SingleIdEntity implements IdEntity
{
    private JdbcField field;
    private boolean autoGenerator;
    private JdbcEntity entity;
    
    public SingleIdEntity(final JdbcField field, final JdbcEntity entity) {
        this.field = field;
        this.autoGenerator = (field.field.getAnnotation(AutoGenerator.class) != null);
        this.entity = entity;
    }
    
    @Override
    public boolean isAutoGenerator() {
        return this.autoGenerator;
    }
    
    @Override
    public void setKey(final Object obj, final Object... args) {
        if (!this.autoGenerator) {
            return;
        }
        try {
            this.field.field.setAccessible(true);
            this.field.field.set(obj, args[0]);
        }
        catch (Throwable t) {
            throw new RuntimeException("set key error", t);
        }
    }
    
    @Override
    public Object[] getIdValue(final Object obj) {
        try {
            this.field.field.setAccessible(true);
            final Object result = this.field.field.get(obj);
            return new Object[] { result };
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public String getIdStringValue(final Object obj) {
        try {
            this.field.field.setAccessible(true);
            final Object result = this.field.field.get(obj);
            return String.valueOf(result);
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public String[] getIdColumnName() {
        return new String[] { this.field.columnName };
    }
}
