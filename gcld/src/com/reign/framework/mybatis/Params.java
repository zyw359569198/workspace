package com.reign.framework.mybatis;

import java.util.*;

public class Params extends HashMap<String, Object>
{
    private static final long serialVersionUID = 9109040637831222502L;
    public static final Map<String, Object> EMPTY_PARAM;
    
    static {
        EMPTY_PARAM = new HashMap<String, Object>(0);
    }
    
    public Params() {
    }
    
    public Params(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public Params addParam(final String key, final Object value) {
        super.put(key, value);
        return this;
    }
}
