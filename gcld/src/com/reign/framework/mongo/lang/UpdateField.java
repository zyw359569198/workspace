package com.reign.framework.mongo.lang;

public class UpdateField extends Update
{
    public String column;
    public Object[] value;
    public UpdateOp op;
    
    public UpdateField(final String column, final UpdateOp op, final Object... value) {
        this.column = column;
        this.value = value;
        this.op = op;
    }
}
