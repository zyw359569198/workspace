package com.reign.framework.jdbc.orm;

import java.util.*;

public class ComplexIdEntity implements IdEntity
{
    private JdbcField[] fields;
    private boolean autoGenerator;
    private JdbcEntity entity;
    
    public ComplexIdEntity(final JdbcField[] fields, final JdbcEntity entity) {
        this.fields = fields;
        this.autoGenerator = false;
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
        final int index = 0;
        try {
            JdbcField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final JdbcField field = fields[i];
                field.field.setAccessible(true);
                field.field.set(obj, args[index]);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("set key error", t);
        }
    }
    
    @Override
    public Object[] getIdValue(final Object obj) {
        try {
            final Object[] array = new Object[this.fields.length];
            int index = 0;
            JdbcField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final JdbcField field = fields[i];
                field.field.setAccessible(true);
                array[index++] = field.field.get(obj);
            }
            return array;
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public String getIdStringValue(final Object obj) {
        try {
            final Object[] array = new Object[this.fields.length];
            int index = 0;
            JdbcField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final JdbcField field = fields[i];
                field.field.setAccessible(true);
                array[index++] = field.field.get(obj);
            }
            return Arrays.toString(array);
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public String[] getIdColumnName() {
        final String[] names = new String[this.fields.length];
        for (int i = 0; i < this.fields.length; ++i) {
            names[i] = this.fields[i].columnName;
        }
        return names;
    }
}
