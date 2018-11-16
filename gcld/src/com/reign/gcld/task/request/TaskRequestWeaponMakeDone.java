package com.reign.gcld.task.request;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.weapon.domain.*;
import java.util.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;

public class TaskRequestWeaponMakeDone extends TaskRequestBase
{
    public TaskRequestWeaponMakeDone(final String[] s) {
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        final List<PlayerWeapon> list = taskDataGetter.getPlayerWeaponDao().getPlayerWeapons(playerDto.playerId);
        if (list.size() < 6) {
            return false;
        }
        for (final PlayerWeapon pw : list) {
            if (pw.getLv() < 1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        return this.check(playerDto, taskDataGetter, vId);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter taskDataGetter, final int vId) {
        if (message instanceof TaskMessageWeaponMakeDone) {
            final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
            final List<PlayerWeapon> list = taskDataGetter.getPlayerWeaponDao().getPlayerWeapons(playerDto.playerId);
            int curNum = 0;
            for (final PlayerWeapon pw : list) {
                if (pw.getLv() > 0) {
                    ++curNum;
                }
            }
            if (curNum >= 6) {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(true, 6, curNum);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
            else {
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(false, 6, curNum);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, taskDataGetter));
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageWeaponMakeDone;
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter taskDataGetter, final int vId) {
        boolean completed = false;
        final List<PlayerWeapon> list = taskDataGetter.getPlayerWeaponDao().getPlayerWeapons(playerDto.playerId);
        int curNum = 0;
        for (final PlayerWeapon pw : list) {
            if (pw.getLv() > 0) {
                ++curNum;
            }
        }
        if (curNum >= 6) {
            completed = true;
        }
        final TaskRequestProcessViewer rtn = new TaskRequestProcessViewer(completed, 6, completed ? 6 : curNum);
        return rtn;
    }
}
