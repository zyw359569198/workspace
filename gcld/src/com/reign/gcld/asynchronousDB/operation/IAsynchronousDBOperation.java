package com.reign.gcld.asynchronousDB.operation;

import com.reign.gcld.common.*;

public interface IAsynchronousDBOperation
{
    void handleWhenAddQueue();
    
    void handleWhenExecuteSuccess();
    
    void handleWhenExecuteException();
    
    void handleOperation(final IDataGetter p0);
    
    String getOperationInfo();
}
