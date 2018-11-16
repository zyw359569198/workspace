package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestArmsWeaponOn extends TaskRequestBase
{
    private int id;
    private int degree;
    
    public TaskRequestArmsWeaponOn(final String[] s) {
        this.id = Integer.valueOf(s[1]);
        this.degree = Integer.valueOf(s[2]);
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        final PlayerWeapon playerWeapon = dataGetter.getPlayerWeaponDao().getPlayerWeapon(playerDto.playerId, this.id);
        return playerWeapon != null && playerWeapon.getLv() >= this.degree;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        int nowLv = -1;
        final PlayerWeapon playerWeapon = dataGetter.getPlayerWeaponDao().getPlayerWeapon(playerDto.playerId, this.id);
        if (playerWeapon != null) {
            nowLv = playerWeapon.getLv();
        }
        final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowLv >= this.degree, this.degree, nowLv);
        return viewer;
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageArmsWeaponOn) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            int nowLv = 0;
            final PlayerWeapon playerWeapon = dataGetter.getPlayerWeaponDao().getPlayerWeapon(message.getPlayerId(), this.id);
            if (playerWeapon != null) {
                nowLv = playerWeapon.getLv();
            }
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(nowLv >= this.degree, this.degree, nowLv);
            final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageArmsWeaponOn;
    }
}
