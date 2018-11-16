package com.reign.framework.json;

public enum JsonType
{
    PRIMITIVE_TYPE("PRIMITIVE_TYPE", 0), 
    STATIC_TYPE("STATIC_TYPE", 1), 
    FINAL_TYPE("FINAL_TYPE", 2), 
    DATE_TYPE("DATE_TYPE", 3), 
    MAP_TYPE("MAP_TYPE", 4), 
    LIST_TYPE("LIST_TYPE", 5), 
    ARRAY_TYPE("ARRAY_TYPE", 6);
    
    private JsonType(final String s, final int n) {
    }
}
