package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddMuBingLing extends AsynchronousDBOperationBase
{
    private int playerId;
    private int muBingLing;
    private Object attribute;
    
    public AsynchronousDBOperationAddMuBingLing(final int playerId, final int muBingLing, final Object attribute) {
        this.playerId = playerId;
        this.muBingLing = muBingLing;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.muBingLing < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("muBingLing is negative").appendPlayerId(this.playerId).append("muBingLing", this.muBingLing).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddMuBingLing").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.muBingLing == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("muBingLing is 0").appendPlayerId(this.playerId).append("muBingLing", this.muBingLing).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddMuBingLing").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerAttributeDao().addRecruitToken(this.playerId, this.muBingLing, String.valueOf(this.attribute.toString()) + "\u52df\u5175\u4ee4");
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("token#{0}#{1};", new Object[] { this.playerId, this.muBingLing });
    }
}
