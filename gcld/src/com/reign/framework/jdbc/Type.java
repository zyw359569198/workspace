package com.reign.framework.jdbc;

public enum Type
{
    Object("Object", 0), 
    Like("Like", 1), 
    Out("Out", 2), 
    Int("Int", 3), 
    Long("Long", 4), 
    Float("Float", 5), 
    Double("Double", 6), 
    BigDecimal("BigDecimal", 7), 
    String("String", 8), 
    Date("Date", 9), 
    SqlDate("SqlDate", 10), 
    Time("Time", 11), 
    Timestamp("Timestamp", 12), 
    Byte("Byte", 13), 
    Bytes("Bytes", 14), 
    Blob("Blob", 15), 
    Clob("Clob", 16), 
    NClob("NClob", 17), 
    Bool("Bool", 18);
    
    private Type(final String s, final int n) {
    }
}
