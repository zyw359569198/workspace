package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.event.util.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddIronTicket extends AsynchronousDBOperationBase
{
    private int playerId;
    private int num;
    private Object attribute;
    
    public AsynchronousDBOperationAddIronTicket(final int playerId, final int num, final Object attribute) {
        this.playerId = playerId;
        this.num = num;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.num < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("num is negative").appendPlayerId(this.playerId).append("num", this.num).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddIronTicket").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.num == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("num is 0").appendPlayerId(this.playerId).append("num", this.num).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddIronTicket").appendMethodName("handleOperation").flush();
            return;
        }
        EventUtil.handleOperation(this.playerId, 15, this.num);
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("ironTicket#{0}#{1};", new Object[] { this.playerId, this.num });
    }
}
