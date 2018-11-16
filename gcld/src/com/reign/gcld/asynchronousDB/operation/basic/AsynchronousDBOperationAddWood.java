package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddWood extends AsynchronousDBOperationBase
{
    private int playerId;
    private int wood;
    private Object attribute;
    
    public AsynchronousDBOperationAddWood(final int playerId, final int wood, final Object attribute) {
        this.playerId = playerId;
        this.wood = wood;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.wood < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("wood is negative").appendPlayerId(this.playerId).append("wood", this.wood).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddWood").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.wood == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("wood is 0").appendPlayerId(this.playerId).append("wood", this.wood).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddWood").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerResourceDao().addWoodIgnoreMax(this.playerId, this.wood, String.valueOf(this.attribute.toString()) + "\u6728\u6750", true);
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("wood#{0}#{1};", new Object[] { this.playerId, this.wood });
    }
}
