package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.store.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestCollect extends TaskRequestBase
{
    private int itemId;
    private int num;
    
    public TaskRequestCollect(final String[] s) {
        this.itemId = Integer.parseInt(s[1]);
        if (s.length > 2) {
            this.num = Integer.parseInt(s[2]);
        }
        else {
            this.num = 1;
        }
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        int currentNum = 0;
        final List<StoreHouse> shList = dataGetter.getStoreHouseDao().getByItemId(playerDto.playerId, this.itemId, 5);
        if (shList != null && shList.size() > 0) {
            currentNum = shList.get(0).getNum();
        }
        if (currentNum >= this.num) {
            dataGetter.getStoreHouseDao().deleteByType(playerDto.playerId, this.itemId, 5);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("collectId", ((Items)dataGetter.getItemsCache().get((Object)this.itemId)).getIndex());
            doc.endObject();
            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        int currentNum = 0;
        final List<StoreHouse> shList = dataGetter.getStoreHouseDao().getByItemId(playerDto.playerId, this.itemId, 5);
        if (shList != null && shList.size() > 0) {
            currentNum = shList.get(0).getNum();
        }
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(currentNum >= this.num, this.num, currentNum, true);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageCollect) {
            final TaskMessageCollect tmc = (TaskMessageCollect)message;
            if (tmc.getItemId() == this.itemId) {
                int currentNum = 0;
                final List<StoreHouse> shList = dataGetter.getStoreHouseDao().getByItemId(message.getPlayerId(), this.itemId, 5);
                if (shList != null && shList.size() > 0) {
                    currentNum = shList.get(0).getNum();
                }
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(currentNum >= this.num, this.num, currentNum, true);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageCollect;
    }
}
