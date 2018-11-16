package com.reign.gcld.asynchronousDB.operation.wrapper;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.framework.exception.*;

public class AsynchronousDBOperationWrapperRetry extends AsynchronousDBOperationWrapper
{
    private int maxRetryTimes;
    
    public AsynchronousDBOperationWrapperRetry(final int maxRetryTimes, final IAsynchronousDBOperation operation) {
        super(operation);
        this.maxRetryTimes = maxRetryTimes;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        int retryTimes = 0;
        Exception operationException = null;
        while (retryTimes < this.maxRetryTimes) {
            try {
                dataGetter.handleAsynchronousDBOperation(this.operation);
                return;
            }
            catch (Exception e) {
                ++retryTimes;
                operationException = e;
            }
        }
        throw new BusinessException("asynchronous db operation error at last!", operationException);
    }
}
