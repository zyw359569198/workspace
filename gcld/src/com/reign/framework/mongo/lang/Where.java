package com.reign.framework.mongo.lang;

public class Where extends Query
{
    public String column;
    public Object[] value;
    public Op op;
    
    public Where(final String column, final Op op, final Object... value) {
        this.column = column;
        this.value = value;
        this.op = op;
    }
}
