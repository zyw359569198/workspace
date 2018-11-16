package com.reign.gcld.task.request;

import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.domain.*;

public class TaskRequestWorldMistWin extends TaskRequestBase
{
    int area;
    int times;
    
    public TaskRequestWorldMistWin(final String[] s) {
        this.area = Integer.parseInt(s[1]);
        this.times = Integer.parseInt(s[2]);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (message instanceof TaskMessageWorldMistWin) {
            final TaskMessageWorldMistWin tmwmw = (TaskMessageWorldMistWin)message;
            if (tmwmw.getArea() >= this.area) {
                final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
                if (playerTask == null) {
                    return;
                }
                dataGetter.getPlayerTaskDao().addProcess(vId, 1);
                final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(playerTask.getProcess() + 1 >= this.times, this.times, playerTask.getProcess() + 1);
                final TaskChangeContent taskChangeContent = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
                final PlayerDto playerDto = Players.getPlayer(message.getPlayerId());
                if (playerDto != null) {
                    Players.push(message.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, dataGetter));
                }
            }
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageWorldMistWin;
    }
    
    @Override
    public boolean check(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.ifFinishTask(dataGetter, vId, playerDto);
    }
    
    private boolean ifFinishTask(final IDataGetter dataGetter, final int vId, final PlayerDto playerDto) {
        final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(playerDto.playerId);
        final String attedCitie = (pw == null) ? "" : pw.getAttedId();
        String[] cities = null;
        boolean allWin = false;
        final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
        if (this.area == 1 && this.times == 1) {
            allWin = (playerTask != null && playerTask.getProcess() > 0);
        }
        else {
            try {
                int count = 0;
                cities = attedCitie.split(",");
                String[] array;
                for (int length = (array = cities).length, i = 0; i < length; ++i) {
                    final String city = array[i];
                    if (!StringUtils.isBlank(city)) {
                        final int cityId = Integer.parseInt(city);
                        if (dataGetter.getWorldCityCache().getMaskSet().contains(cityId)) {
                            ++count;
                            final int areaId = dataGetter.getWorldCityCache().getArea(playerDto.forceId, cityId);
                            if (areaId >= this.area) {
                                allWin = true;
                            }
                        }
                    }
                }
                if (count == dataGetter.getWorldCityCache().getMaskSet().size()) {
                    allWin = true;
                }
            }
            catch (Exception ex) {}
        }
        return allWin;
    }
    
    @Override
    public boolean doRequest(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        return this.check(playerDto, dataGetter, vId);
    }
    
    @Override
    public TaskRequestProcessViewer getProcess(final PlayerDto playerDto, final IDataGetter dataGetter, final int vId) {
        TaskRequestProcessViewer rtn = null;
        final boolean allWin = this.ifFinishTask(dataGetter, vId, playerDto);
        rtn = new TaskRequestProcessViewer(allWin, 1, allWin ? 1 : 0);
        return rtn;
    }
}
