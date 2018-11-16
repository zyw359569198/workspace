package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import java.util.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddGeneralExp extends AsynchronousDBOperationBase
{
    private int playerId;
    private int generalId;
    private int exp;
    private Object attribute;
    
    public AsynchronousDBOperationAddGeneralExp(final int playerId, final int generalId, final int exp, final Object attribute) {
        this.playerId = playerId;
        this.generalId = generalId;
        this.exp = exp;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.exp < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("exp is negative").appendPlayerId(this.playerId).append("exp", this.exp).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddGeneralExp").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.exp == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("exp is 0").appendPlayerId(this.playerId).append("exp", this.exp).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddGeneralExp").appendMethodName("handleOperation").flush();
            return;
        }
        final List<UpdateExp> defUpExpList = dataGetter.getGeneralService().updateExpAndGeneralLevel(this.playerId, this.generalId, this.exp);
        int addGExp = 0;
        if (defUpExpList != null && defUpExpList.size() > 0) {
            for (final UpdateExp ue : defUpExpList) {
                addGExp += (int)ue.getCurExp();
            }
        }
        this.exp = addGExp;
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("gExp#{0}#{1}#{2};", new Object[] { this.playerId, this.generalId, this.exp });
    }
}
