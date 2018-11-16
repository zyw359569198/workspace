package com.reign.framework.mongo;

public enum MongoActionOperation
{
    REMOVE("REMOVE", 0), 
    UPDATE("UPDATE", 1), 
    INSERT("INSERT", 2), 
    INSERT_LIST("INSERT_LIST", 3), 
    SAVE("SAVE", 4);
    
    private MongoActionOperation(final String s, final int n) {
    }
}
