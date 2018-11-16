package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.common.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddChiefExp extends AsynchronousDBOperationBase
{
    private int playerId;
    private int chiefExp;
    private Object attribute;
    
    public AsynchronousDBOperationAddChiefExp(final int playerId, final int chiefExp, final Object attribute) {
        this.playerId = playerId;
        this.chiefExp = chiefExp;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.chiefExp < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("chiefExp is negative").appendPlayerId(this.playerId).append("chiefExp", this.chiefExp).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddChiefExp").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.chiefExp == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("chiefExp is 0").appendPlayerId(this.playerId).append("chiefExp", this.chiefExp).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddChiefExp").appendMethodName("handleOperation").flush();
            return;
        }
        final AddExpInfo expResult = dataGetter.getPlayerService().updateExpAndPlayerLevel(this.playerId, this.chiefExp, String.valueOf(this.attribute.toString()) + "\u73a9\u5bb6\u7ecf\u9a8c\u503c");
        this.chiefExp = expResult.addExp;
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("exp#{0}#{1};", new Object[] { this.playerId, this.chiefExp });
    }
}
