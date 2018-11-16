package com.reign.util;

import java.io.*;

public class Tuple7<T1, T2, T3, T4, T5, T6, T7> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private T1 _1;
    private T2 _2;
    private T3 _3;
    private T4 _4;
    private T5 _5;
    private T6 _6;
    private T7 _7;
    
    public Tuple7(final T1 _1_, final T2 _2_, final T3 _3_, final T4 _4_, final T5 _5_, final T6 _6_, final T7 _7_) {
        this._1 = _1_;
        this._2 = _2_;
        this._3 = _3_;
        this._4 = _4_;
        this._5 = _5_;
        this._6 = _6_;
        this._7 = _7_;
    }
    
    public Tuple7() {
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
    
    public T5 get_5() {
        return this._5;
    }
    
    public T6 get_6() {
        return this._6;
    }
    
    public T7 get_7() {
        return this._7;
    }
    
    public Object getValue(final int index) {
        if (1 == index) {
            return this.get_1();
        }
        if (2 == index) {
            return this.get_2();
        }
        if (3 == index) {
            return this.get_3();
        }
        if (4 == index) {
            return this.get_4();
        }
        if (5 == index) {
            return this.get_5();
        }
        if (6 == index) {
            return this.get_6();
        }
        if (7 == index) {
            return this.get_7();
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Tuple7) {
            final Tuple7 to = (Tuple7)other;
            return this._1.equals(to._1) && this._2.equals(to._2) && this._3.equals(to._3) && this._4.equals(to._4) && this._5.equals(to._5) && this._6.equals(to._6) && this._7.equals(to._7);
        }
        return false;
    }
}
