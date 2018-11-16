package com.reign.gcld.task.request;

import com.reign.gcld.world.common.*;
import java.util.*;
import java.text.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.world.domain.*;

public class TaskRequestKillRank extends TaskRequestCount
{
    int killNum;
    int days;
    
    public TaskRequestKillRank(final String[] s) {
        super(s);
        this.days = Integer.parseInt(s[1]);
        this.killNum = Integer.parseInt(s[2]);
    }
    
    @Override
    public void handleMessage(final TaskMessage message, final IDataGetter dataGetter, final int vId) {
        if (this.isConcernedMessage(message)) {
            final PlayerTask playerTask = dataGetter.getPlayerTaskDao().read(vId);
            if (playerTask == null) {
                return;
            }
            final TaskMessageKillNum tkn = (TaskMessageKillNum)message;
            if (tkn.getKillNum() < this.killNum) {
                return;
            }
            boolean canAdd = false;
            if (tkn.getKillNum() - tkn.getKillTotal() >= this.killNum) {
                if (playerTask.getProcess() == 0) {
                    final long taskStartTime = playerTask.getStartTime();
                    Date startDate = new Date(taskStartTime);
                    startDate = WorldCityCommon.getDateAfter23(startDate);
                    final Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);
                    final int startDay = startCalendar.get(6);
                    final long now = System.currentTimeMillis();
                    Date nowDate = new Date(now);
                    nowDate = WorldCityCommon.getDateAfter23(nowDate);
                    final Calendar nowCalendar = Calendar.getInstance();
                    nowCalendar.setTime(nowDate);
                    final int nowDay = nowCalendar.get(6);
                    if (nowDay == startDay) {
                        canAdd = true;
                    }
                }
            }
            else {
                canAdd = true;
            }
            if (!canAdd) {
                return;
            }
            if (playerTask.getProcess() == 0) {
                dataGetter.getPlayerTaskDao().addProcess(vId, 1);
            }
            else {
                Date today = new Date();
                today = WorldCityCommon.getDateAfter23(today);
                today = WorldCityCommon.getYesterday(today);
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final String dateStrToday = sdf.format(today);
                final PlayerKillInfo pKillInfo = dataGetter.getPlayerKillInfoDao().getByTodayInfo(tkn.getPlayerId(), dateStrToday);
                final int preKill = (pKillInfo == null) ? 0 : pKillInfo.getKillNum();
                if (preKill < this.killNum) {
                    playerTask.setProcess(1);
                    dataGetter.getPlayerTaskDao().update(playerTask);
                    return;
                }
                dataGetter.getPlayerTaskDao().addProcess(vId, 1);
            }
            final int curNum = playerTask.getProcess() + 1;
            final TaskRequestProcessViewer viewer = new TaskRequestProcessViewer(curNum >= this.days, this.days, curNum);
            final TaskChangeContent content = new TaskChangeContent(this.getTask(), viewer.getProcessStr(), viewer.isCompleted());
            Players.push(tkn.getPlayerId(), PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(content, Players.getPlayer(message.getPlayerId()), dataGetter));
        }
    }
    
    @Override
    public boolean isConcernedMessage(final TaskMessage message) {
        return message instanceof TaskMessageKillNum;
    }
}
