package com.reign.gcld.asynchronousDB.operation.wrapper;

import com.reign.gcld.common.log.*;
import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

public class AsynchronousDBOperationWrapper extends AsynchronousDBOperationBase
{
    private static final Logger logger;
    private String guid;
    protected IAsynchronousDBOperation operation;
    
    static {
        logger = new AsynchronousDBOperationLogger();
    }
    
    public AsynchronousDBOperationWrapper(final IAsynchronousDBOperation operation) {
        this.guid = UUIDHexGenerator.getInstance().generate();
        this.operation = operation;
    }
    
    @Override
    public void handleWhenAddQueue() {
        if (this.operation.getOperationInfo() == null) {
            return;
        }
    }
    
    @Override
    public void handleWhenExecuteSuccess() {
        if (this.operation.getOperationInfo() == null) {
            return;
        }
    }
    
    @Override
    public void handleWhenExecuteException() {
        if (this.operation.getOperationInfo() == null) {
            return;
        }
        AsynchronousDBOperationWrapper.logger.info(MessageFormatter.format("#{0}#fail#{1}", new Object[] { this.guid, this.operation.getOperationInfo() }));
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        dataGetter.handleAsynchronousDBOperationNoTrans(this.operation);
    }
}
