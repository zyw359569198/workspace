package com.reign.util;

import java.io.*;

public class Tuple4<T1, T2, T3, T4> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private T1 _1;
    private T2 _2;
    private T3 _3;
    private T4 _4;
    
    public Tuple4(final T1 _1_, final T2 _2_, final T3 _3_, final T4 _4_) {
        this._1 = _1_;
        this._2 = _2_;
        this._3 = _3_;
        this._4 = _4_;
    }
    
    public Tuple4() {
    }
    
    public T1 get_1() {
        return this._1;
    }
    
    public T2 get_2() {
        return this._2;
    }
    
    public T3 get_3() {
        return this._3;
    }
    
    public T4 get_4() {
        return this._4;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Tuple4) {
            final Tuple4 to = (Tuple4)other;
            return this._1.equals(to._1) && this._2.equals(to._2) && this._3.equals(to._3) && this._4.equals(to._4);
        }
        return false;
    }
}
