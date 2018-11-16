package com.reign.util;

import java.io.*;

public class Tuple3<T1, T2, T3> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private T1 _1;
    private T2 _2;
    private T3 _3;
    
    public Tuple3(final T1 _1_, final T2 _2_, final T3 _3_) {
        this._1 = _1_;
        this._2 = _2_;
        this._3 = _3_;
    }
    
    public Tuple3() {
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
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Tuple3) {
            final Tuple3 to = (Tuple3)other;
            return this._1.equals(to._1) && this._2.equals(to._2) && this._3.equals(this._3);
        }
        return false;
    }
}
