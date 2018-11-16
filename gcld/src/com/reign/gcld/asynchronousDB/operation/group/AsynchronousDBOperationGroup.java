package com.reign.gcld.asynchronousDB.operation.group;

import com.reign.gcld.common.log.*;
import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;
import com.reign.gcld.common.*;
import java.util.*;

public class AsynchronousDBOperationGroup extends AsynchronousDBOperationBase
{
    private static final Logger logger;
    private String guid;
    protected List<IAsynchronousDBOperation> operationList;
    
    static {
        logger = new AsynchronousDBOperationLogger();
    }
    
    public AsynchronousDBOperationGroup(final IAsynchronousDBOperation operation) {
        this.guid = UUIDHexGenerator.getInstance().generate();
        this.operationList = new LinkedList<IAsynchronousDBOperation>();
    }
    
    public void addDBOperationToGroup(final IAsynchronousDBOperation operation) {
        try {
            this.operationList.add(operation);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("AsynchronousDBOperationGroup.addDBOperationToGroup catch Exception", e);
        }
    }
    
    @Override
    public void handleWhenAddQueue() {
    }
    
    @Override
    public void handleWhenExecuteSuccess() {
    }
    
    @Override
    public void handleWhenExecuteException() {
        AsynchronousDBOperationGroup.logger.info(MessageFormatter.format("#{0}#fail#{1}", new Object[] { this.guid, this.getOperationInfo() }));
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        dataGetter.handleAsynchronousDBOperationListInNewTrans(this.operationList);
    }
    
    @Override
    public String getOperationInfo() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final IAsynchronousDBOperation operation : this.operationList) {
            stringBuilder.append(operation.getOperationInfo()).append("#");
        }
        return stringBuilder.toString();
    }
}
