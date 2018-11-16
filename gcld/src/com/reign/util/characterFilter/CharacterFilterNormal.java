package com.reign.util.characterFilter;

import com.reign.util.characterFilter.dataGetter.*;
import com.reign.util.log.*;
import java.io.*;

public class CharacterFilterNormal extends CharacterFilterBase
{
    Logger logger;
    private ICharacterFilter filter;
    
    public CharacterFilterNormal(final ICharacterFilter filter, final IDataGetter dataGetter) {
        this.logger = CommonLog.getLog(CharacterFilterNormal.class);
        this.filter = null;
        this.filter = filter;
        final BufferedReader br = dataGetter.getData();
        filter.buildFilterKeyWord(br);
    }
    
    @Override
    public String filter(final String str) {
        return this.filter.filter(str);
    }
    
    @Override
    public boolean isValid(final String str) {
        return this.filter.isValid(str);
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        this.filter.buildFilterKeyWord(buff);
    }
    
    @Override
    public void setReplaceCharacterGetter(final IReplaceCharacterGetter replaceCharacterGetter) {
        this.filter.setReplaceCharacterGetter(replaceCharacterGetter);
    }
}
