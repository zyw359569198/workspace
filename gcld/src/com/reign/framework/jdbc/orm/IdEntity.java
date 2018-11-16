package com.reign.framework.jdbc.orm;

public interface IdEntity
{
    boolean isAutoGenerator();
    
    void setKey(final Object p0, final Object... p1);
    
    Object[] getIdValue(final Object p0);
    
    String getIdStringValue(final Object p0);
    
    String[] getIdColumnName();
}
