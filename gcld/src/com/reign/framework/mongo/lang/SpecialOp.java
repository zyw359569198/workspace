package com.reign.framework.mongo.lang;

public enum SpecialOp
{
    returnKey("returnKey", 0, "$returnKey"), 
    maxScan("maxScan", 1, "$maxScan"), 
    orderby("orderby", 2, "$orderby"), 
    explain("explain", 3, "$explain"), 
    snapshot("snapshot", 4, "$snapshot"), 
    hint("hint", 5, "$hint"), 
    comment("comment", 6, "$comment"), 
    showDiskLoc("showDiskLoc", 7, "$showDiskLoc");
    
    private String value;
    
    private SpecialOp(final String s, final int n, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
