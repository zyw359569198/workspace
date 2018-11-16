package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddFood extends AsynchronousDBOperationBase
{
    private int playerId;
    private int food;
    private Object attribute;
    
    public AsynchronousDBOperationAddFood(final int playerId, final int food, final Object attribute) {
        this.playerId = playerId;
        this.food = food;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.food < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("food is negative").appendPlayerId(this.playerId).append("food", this.food).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddFood").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.food == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("food is 0").appendPlayerId(this.playerId).append("food", this.food).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddFood").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerResourceDao().addFoodIgnoreMax(this.playerId, this.food, String.valueOf(this.attribute.toString()) + "\u7cae\u98df");
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("food#{0}#{1};", new Object[] { this.playerId, this.food });
    }
}
