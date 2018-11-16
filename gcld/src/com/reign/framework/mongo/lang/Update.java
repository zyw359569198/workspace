package com.reign.framework.mongo.lang;

import java.util.*;

public class Update
{
    protected List<Update> updateList;
    
    public Update() {
        this.updateList = new ArrayList<Update>();
    }
    
    public Update add(final Update update) {
        this.updateList.add(update);
        return this;
    }
    
    public List<Update> getUpdate() {
        return this.updateList;
    }
}
