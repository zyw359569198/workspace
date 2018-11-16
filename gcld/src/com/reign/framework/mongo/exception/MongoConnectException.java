package com.reign.framework.mongo.exception;

public class MongoConnectException extends RuntimeException
{
    private static final long serialVersionUID = 6788253203647340010L;
    
    public MongoConnectException(final String msg) {
        super(msg);
    }
}
