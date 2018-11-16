package com.reign.util.characterFilter;

public interface IKeyWordsVersionChecker
{
    void setVersion();
    
    boolean needUpdate();
    
    String getVersion();
}
