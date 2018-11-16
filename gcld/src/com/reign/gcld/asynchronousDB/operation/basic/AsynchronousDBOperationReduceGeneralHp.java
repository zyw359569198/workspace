package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

public class AsynchronousDBOperationReduceGeneralHp extends AsynchronousDBOperationBase
{
    private int playerId;
    private int generalId;
    private int reduce;
    
    public AsynchronousDBOperationReduceGeneralHp(final int playerId, final int generalId, final int reduce) {
        this.playerId = playerId;
        this.generalId = generalId;
        this.reduce = reduce;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.reduce < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("reduce is negative").appendPlayerId(this.playerId).append("generalId", this.generalId).append("reduce", this.reduce).appendClassName("AsynchronousDBOperationReduceGeneralHp").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.reduce == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("reduce is 0").appendPlayerId(this.playerId).append("generalId", this.generalId).append("reduce", this.reduce).appendClassName("AsynchronousDBOperationReduceGeneralHp").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getPlayerGeneralMilitaryDao().consumeForces(this.playerId, this.generalId, this.reduce, new Date());
        dataGetter.getGeneralService().sendGmUpdate(this.playerId, this.generalId, true);
    }
    
    @Override
    public String getOperationInfo() {
        return null;
    }
}
