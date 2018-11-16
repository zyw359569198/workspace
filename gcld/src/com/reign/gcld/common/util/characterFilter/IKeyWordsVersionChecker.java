package com.reign.gcld.common.util.characterFilter;

public interface IKeyWordsVersionChecker
{
    void setVersion();
    
    boolean needUpdate();
    
    String getVersion();
}
