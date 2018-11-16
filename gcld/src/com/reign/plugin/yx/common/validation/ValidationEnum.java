package com.reign.plugin.yx.common.validation;

import java.util.*;

public enum ValidationEnum
{
    API_KEY("API_KEY", 0, "api_key", "1003", "api_key incorrect"), 
    USER_ID("USER_ID", 1, "user_id", "1003", "user_id incorrect"), 
    USER_NAME("USER_NAME", 2, "user_name", "1003", "user_name incorrect"), 
    SERVER_ID("SERVER_ID", 3, "server_id", "1003", "server_id incorrect"), 
    CM_FLAG("CM_FLAG", 4, "cm_flag", "1003", "cm_flag incorrect"), 
    TIMESTAMP("TIMESTAMP", 5, "timestamp", "1003", "timestamp incorrect"), 
    SIGN_TYPE("SIGN_TYPE", 6, "sign_type", "1003", "sign_type incorrect"), 
    SIGN("SIGN", 7, "sign", "1003", "sign incorrect"), 
    TOKEN("TOKEN", 8, "token", "1003", "token incorrect"), 
    FORMAT("FORMAT", 9, "format", "1003", "format incorrect"), 
    ORDER_ID("ORDER_ID", 10, "order_id", "1003", "order_id incorrect"), 
    AMOUNT("AMOUNT", 11, "amount", "1003", "amount incorrect"), 
    RATE("RATE", 12, "rate", "1003", "rate incorrect");
    
    private String name;
    private String errorCode;
    private String errorString;
    public static Map<String, ValidationEnum> map;
    
    static {
        ValidationEnum.map = new HashMap<String, ValidationEnum>();
        final ValidationEnum[] values = values();
        ValidationEnum[] array;
        for (int length = (array = values).length, i = 0; i < length; ++i) {
            final ValidationEnum en = array[i];
            ValidationEnum.map.put(en.name, en);
        }
    }
    
    private ValidationEnum(final String s, final int n, final String name, final String errorCode, final String errorString) {
        this.name = name;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorString() {
        return this.errorString;
    }
    
    public void setErrorString(final String errorString) {
        this.errorString = errorString;
    }
    
    public static ValidationEnum getEnumByName(final String name) {
        return ValidationEnum.map.get(name);
    }
}
