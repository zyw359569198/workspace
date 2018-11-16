package com.reign.framework.jdbc;

public class Param
{
    public Object obj;
    public Type type;
    
    public Param(final Object obj) {
        this.obj = obj;
        this.type = Type.Object;
    }
    
    public Param(final Object obj, final Type type) {
        this.obj = obj;
        this.type = type;
    }
}
