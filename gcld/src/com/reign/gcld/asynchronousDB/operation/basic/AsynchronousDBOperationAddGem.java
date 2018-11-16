package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddGem extends AsynchronousDBOperationBase
{
    private int playerId;
    private int gemId;
    private int gemNum;
    private Object attribute;
    
    public AsynchronousDBOperationAddGem(final int playerId, final int gemId, final int gemNum, final Object attribute) {
        this.playerId = playerId;
        this.gemId = gemId;
        this.gemNum = gemNum;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.gemNum < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("gemNum is negative").appendPlayerId(this.playerId).append("gemNum", this.gemNum).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddGem").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.gemNum == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("gemNum is 0").appendPlayerId(this.playerId).append("gemNum", this.gemNum).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddGem").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getStoreHouseService().gainGem(dataGetter.getPlayerDao().read(this.playerId), this.gemNum, this.gemId, String.valueOf(this.attribute.toString()) + "\u5b9d\u77f3", null);
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("gem#{0}#{1}#{2};", new Object[] { this.playerId, this.gemId, this.gemNum });
    }
}
