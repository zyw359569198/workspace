package com.reign.util.struct.charts;

import java.util.*;

public class ChartsItemOperatorAddToList<T extends ISetSeqable<T>> implements IChartsItemOperator<T>
{
    private List<T> list;
    
    public ChartsItemOperatorAddToList() {
        this.list = new ArrayList<T>();
    }
    
    @Override
    public void operate(final T t) {
        this.list.add(t);
    }
    
    public List<T> getList() {
        return this.list;
    }
}
