package com.reign.framework.jdbc;

import java.util.*;

public class Params extends ArrayList<Param>
{
    private static final long serialVersionUID = 8500390903099575640L;
    public static final List<Param> EMPTY;
    
    static {
        EMPTY = new ArrayList<Param>();
    }
    
    public void addParam(final Param param) {
        super.add(param);
    }
    
    public void addParam(final Object o) {
        super.add(new Param(o));
    }
    
    public void addParam(final Object o, final Type type) {
        super.add(new Param(o, type));
    }
}
