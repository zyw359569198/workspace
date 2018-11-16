package com.reign.framework.mongo.lang;

import java.util.*;

public class Query
{
    protected List<Query> queryList;
    
    public Query add(final Query query) {
        if (this.queryList == null) {
            this.queryList = new ArrayList<Query>();
        }
        this.queryList.add(query);
        return this;
    }
    
    public List<Query> getQuery() {
        return this.queryList;
    }
}
