package com.reign.gcld.rank.common;

public class ComparableFactor
{
    private int order;
    private int value;
    
    public ComparableFactor(final int order, final int value) {
        this.setOrder(order);
        this.setValue(value);
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int compareTo(final ComparableFactor oFactor) {
        int valueM = this.value - oFactor.value;
        if (this.order != oFactor.order) {
            return -2;
        }
        if (valueM == 0) {
            return 0;
        }
        valueM /= Math.abs(valueM);
        valueM = ((this.order == 0) ? valueM : (0 - valueM));
        return valueM;
    }
}
