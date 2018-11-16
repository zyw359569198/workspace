package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddCopper extends AsynchronousDBOperationBase
{
    private int playerId;
    private int copper;
    private Object attribute;
    
    public AsynchronousDBOperationAddCopper(final int playerId, final int copper, final Object attribute) {
        this.playerId = playerId;
        this.copper = copper;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.copper < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("copper is negative").appendPlayerId(this.playerId).append("copper", this.copper).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddCopper").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.copper == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("copper is 0").appendPlayerId(this.playerId).append("copper", this.copper).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddCopper").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerResourceDao().addCopperIgnoreMax(this.playerId, this.copper, String.valueOf(this.attribute.toString()) + "\u94f6\u5e01", true);
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("copper#{0}#{1};", new Object[] { this.playerId, this.copper });
    }
}
