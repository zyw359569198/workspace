package com.reign.framework.jdbc;

public interface NameStrategy
{
    String columnNameToPropertyName(final String p0);
    
    String propertyNameToColumnName(final String p0);
}
