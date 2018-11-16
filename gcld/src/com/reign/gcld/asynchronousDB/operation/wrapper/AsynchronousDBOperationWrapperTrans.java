package com.reign.gcld.asynchronousDB.operation.wrapper;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;

public class AsynchronousDBOperationWrapperTrans extends AsynchronousDBOperationWrapper
{
    public AsynchronousDBOperationWrapperTrans(final IAsynchronousDBOperation operation) {
        super(operation);
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        dataGetter.handleAsynchronousDBOperation(this.operation);
    }
}
