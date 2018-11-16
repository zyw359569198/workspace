package com.reign.framework.mongo.lang;

public enum Op
{
    eq("eq", 0, "$eq"), 
    lt("lt", 1, "$lt"), 
    gt("gt", 2, "$gt"), 
    ne("ne", 3, "$ne"), 
    lte("lte", 4, "$lte"), 
    gte("gte", 5, "$gte"), 
    mod("mod", 6, "$mod"), 
    all("all", 7, "$all"), 
    exists("exists", 8, "$exists"), 
    in("in", 9, "$in"), 
    nin("nin", 10, "$nin"), 
    size("size", 11, "$size"), 
    type("type", 12, "$type"), 
    regex("regex", 13, "$regex");
    
    private String value;
    
    private Op(final String s, final int n, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
