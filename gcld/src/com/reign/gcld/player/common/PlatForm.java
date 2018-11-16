package com.reign.gcld.player.common;

public enum PlatForm
{
    PC("PC", 0, "p"), 
    MOBILE_IPHONE("MOBILE_IPHONE", 1, "i"), 
    MOBILE_ANDROID("MOBILE_ANDROID", 2, "a");
    
    private String value;
    
    private PlatForm(final String s, final int n, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
