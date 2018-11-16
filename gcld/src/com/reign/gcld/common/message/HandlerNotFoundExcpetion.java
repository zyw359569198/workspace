package com.reign.gcld.common.message;

import com.reign.framework.exception.*;

public class HandlerNotFoundExcpetion extends BaseException
{
    private static final long serialVersionUID = 3119759925488602361L;
    
    public HandlerNotFoundExcpetion(final String message) {
        super(message);
    }
}
