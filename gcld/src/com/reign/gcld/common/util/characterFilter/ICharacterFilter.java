package com.reign.gcld.common.util.characterFilter;

import java.io.*;

public interface ICharacterFilter
{
    String filter(final String p0);
    
    boolean isValid(final String p0);
    
    void buildFilterKeyWord(final BufferedReader p0);
    
    void setReplaceCharacterGetter(final IReplaceCharacterGetter p0);
}
