package com.reign.gcld.chat.service;

public enum ChatType
{
    COUNTRY("COUNTRY", 0), 
    AREA("AREA", 1), 
    ONE2ONE("ONE2ONE", 2), 
    SYS2ONE("SYS2ONE", 3), 
    LEGION("LEGION", 4), 
    GLOBAL("GLOBAL", 5), 
    WORLD("WORLD", 6), 
    BATTLE("BATTLE", 7), 
    WORLD_1("WORLD_1", 8), 
    WORLD_2("WORLD_2", 9), 
    WORLD_3("WORLD_3", 10), 
    WORLD_OPENED_1("WORLD_OPENED_1", 11), 
    WORLD_OPENED_2("WORLD_OPENED_2", 12), 
    WORLD_OPENED_3("WORLD_OPENED_3", 13), 
    YX("YX", 14);
    
    private ChatType(final String s, final int n) {
    }
}
