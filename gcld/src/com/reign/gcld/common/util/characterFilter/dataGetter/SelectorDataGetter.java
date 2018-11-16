package com.reign.gcld.common.util.characterFilter.dataGetter;

import java.io.*;
import java.util.*;

public class SelectorDataGetter implements IDataGetter
{
    private List<IDataGetter> list;
    
    public SelectorDataGetter() {
        this.list = new ArrayList<IDataGetter>();
    }
    
    public SelectorDataGetter(final IDataGetter[] args) {
        this.list = new ArrayList<IDataGetter>();
        for (final IDataGetter dataGetter : args) {
            if (dataGetter != null) {
                this.list.add(dataGetter);
            }
        }
    }
    
    public SelectorDataGetter(final List<IDataGetter> list) {
        this.list = list;
    }
    
    public void clearDataGetter() {
        this.list.clear();
    }
    
    public void addDataGetter(final IDataGetter dataGetter) {
        this.list.add(dataGetter);
    }
    
    @Override
    public BufferedReader getData() {
        BufferedReader rtn = null;
        for (final IDataGetter dataGetter : this.list) {
            rtn = dataGetter.getData();
            if (rtn != null) {
                break;
            }
        }
        return rtn;
    }
}
