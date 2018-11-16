package com.reign.framework.mongo.lang;

public enum UpdateOp
{
    set("set", 0, "$set"), 
    inc("inc", 1, "$inc"), 
    unset("unset", 2, "$unset"), 
    push("push", 3, "$push"), 
    pushAll("pushAll", 4, "$pushAll"), 
    addToSet("addToSet", 5, "$addToSet"), 
    pop("pop", 6, "$pop"), 
    pull("pull", 7, "$pull"), 
    pullAll("pullAll", 8, "$pullAll"), 
    rename("rename", 9, "$rename"), 
    bit("bit", 10, "$bit"), 
    each("each", 11, "$each");
    
    private String value;
    
    private UpdateOp(final String s, final int n, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
