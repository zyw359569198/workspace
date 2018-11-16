package com.reign.framework.common;

public enum ServerProtocol
{
    TCP("TCP", 0), 
    HTTP("HTTP", 1);
    
    private ServerProtocol(final String s, final int n) {
    }
}
