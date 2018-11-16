package com.reign.framework.mongo.convert;

public class BeanWrapper
{
    private ObjectId _id;
    private Object obj;
    
    public ObjectId get_id() {
        return this._id;
    }
    
    public void set_id(final ObjectId _id) {
        this._id = _id;
    }
    
    public Object getObj() {
        return this.obj;
    }
    
    public void setObj(final Object obj) {
        this.obj = obj;
    }
}
