package com.reign.framework.jdbc.orm.cache;

public enum AccessType
{
    READ_WRITE("READ_WRITE", 0, "read-write");
    
    private String value;
    
    private AccessType(final String s, final int n, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public AccessType getAccessType(final String value) {
        if (value.equalsIgnoreCase("read-write")) {
            return AccessType.READ_WRITE;
        }
        return AccessType.READ_WRITE;
    }
}
