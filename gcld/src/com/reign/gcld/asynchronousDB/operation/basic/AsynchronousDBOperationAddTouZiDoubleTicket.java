package com.reign.gcld.asynchronousDB.operation.basic;

import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;

public class AsynchronousDBOperationAddTouZiDoubleTicket extends AsynchronousDBOperationBase
{
    private int playerId;
    private int tickets;
    private Object attribute;
    
    public AsynchronousDBOperationAddTouZiDoubleTicket(final int playerId, final int tickets, final Object attribute) {
        this.playerId = playerId;
        this.tickets = tickets;
        this.attribute = attribute;
    }
    
    @Override
    public void handleOperation(final IDataGetter dataGetter) {
        if (this.tickets < 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("tickets is negative").appendPlayerId(this.playerId).append("tickets", this.tickets).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddTouZiDoubleTicket").appendMethodName("handleOperation").flush();
            return;
        }
        if (this.tickets == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("tickets is 0").appendPlayerId(this.playerId).append("tickets", this.tickets).append("attribute", this.attribute.toString()).appendClassName("AsynchronousDBOperationAddTouZiDoubleTicket").appendMethodName("handleOperation").flush();
            return;
        }
        dataGetter.getStoreHouseService().gainItems(this.playerId, this.tickets, 401, String.valueOf(this.attribute.toString()) + "\u6295\u8d44\u7ffb\u500d\u5238");
        final Player player = dataGetter.getPlayerDao().read(this.playerId);
        if (player != null) {
            dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(this.playerId, player.getForceId()), this.tickets, "fanbeiquan");
        }
    }
    
    @Override
    public String getOperationInfo() {
        return MessageFormatter.format("touzi_double_ticket#{0}#{1};", new Object[] { this.playerId, this.tickets });
    }
}
