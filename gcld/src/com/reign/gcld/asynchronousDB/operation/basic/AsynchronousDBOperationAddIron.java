package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddIron extends AsynchronousDBOperationBase
{
    private int playerId;
    private int iron;
    private Object attribute;
    
    public AsynchronousDBOperationAddIron(final int playerId, final int iron, final Object attribute) {
        this.playerId = playerId;
        this.iron = iron;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.iron < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("iron is negative").appendPlayerId(this.playerId).append("iron", this.iron).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddIron").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.iron == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("iron is 0").appendPlayerId(this.playerId).append("iron", this.iron).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddIron").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerResourceDao().addIronIgnoreMax(this.playerId, this.iron, String.valueOf(this.attribute.toString()) + "\u9554\u94c1", true);
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("iron#{0}#{1};", new Object[] { this.playerId, this.iron });
    }
}
