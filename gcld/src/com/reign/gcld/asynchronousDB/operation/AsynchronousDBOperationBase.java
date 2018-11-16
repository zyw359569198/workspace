package com.reign.gcld.asynchronousDB.operation;

import com.reign.gcld.common.*;

public abstract class AsynchronousDBOperationBase implements IAsynchronousDBOperation
{
    @Override
    public void handleWhenAddQueue() {
    }
    
    @Override
    public void handleWhenExecuteSuccess() {
    }
    
    @Override
    public void handleWhenExecuteException() {
    }
    
    @Override
    public abstract void handleOperation(final IDataGetter p0);
    
    @Override
    public String getOperationInfo() {
        return null;
    }
}
