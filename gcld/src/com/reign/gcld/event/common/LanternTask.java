package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.log.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.event.domain.*;
import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.reward.*;
import java.util.*;

public class LanternTask extends Thread
{
    private static final Logger timerLog;
    private IDataGetter dataGetter;
    private static long GAP_MS;
    public static AtomicInteger rounds;
    public static State state;
    public static Date nextCd;
    
    static {
        timerLog = new TimerLogger();
        LanternTask.GAP_MS = 30L;
        LanternTask.rounds = new AtomicInteger(0);
        LanternTask.state = State.END;
        LanternTask.nextCd = null;
    }
    
    public LanternTask(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void run() {
        while (EventUtil.isEventTime(21)) {
            try {
                final long start = System.currentTimeMillis();
                final LanternRank lanterRank = this.dataGetter.getRankService().getLanternRank();
                if (LanternTask.nextCd == null || LanternTask.nextCd.getTime() < start) {
                    if (LanternTask.state == State.END) {
                        System.out.println("state:" + LanternTask.state + "#State.END:" + State.END + "#state == State.END" + (LanternTask.state == State.END) + "#ThreadName:" + Thread.currentThread().getName());
                        LanternTask.state = State.ING;
                        final List<PlayerEvent> rewardList = this.dataGetter.getPlayerEventDao().getLanternRankRewardList(21);
                        for (final PlayerEvent pe : rewardList) {
                            try {
                                final int playerId = pe.getPlayerId();
                                final PlayerDto playerDto = Players.getPlayer(playerId);
                                if (playerDto == null) {
                                    PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                                }
                                final int rank = pe.getParam4();
                                final String rewardStr = LanternEvent.getBaseRewardNumString(playerDto.playerLv, playerDto.forceId, rank, this.dataGetter);
                                final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                                final Map<Integer, Reward> rewardMap = tr.rewardPlayer(playerDto, this.dataGetter, "\u5143\u5bb5\u6d3b\u52a8\u6392\u540d\u5956\u52b1", null);
                                final StringBuffer msgSb = new StringBuffer();
                                msgSb.append(MessageFormatter.format(LocalMessages.LANTERN_MAIL_CONTENT, new Object[] { rank }));
                                for (final Reward reward : rewardMap.values()) {
                                    msgSb.append(reward.getName());
                                    msgSb.append(reward.getNum());
                                    msgSb.append("\uff0c");
                                }
                                final String msg = msgSb.substring(0, msgSb.length() - 1);
                                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, MessageFormatter.format(LocalMessages.LANTERN_MAIL_HEAD, new Object[] { LanternTask.rounds.get() }), msg, 1, playerId, 0);
                            }
                            catch (Exception e2) {
                                LanternTask.timerLog.error("class:LanternTask#autoReward#exception#round" + LanternTask.rounds.get() + "#playerId:" + pe.getPlayerId() + "#rank:" + pe.getParam4());
                            }
                        }
                        this.dataGetter.getPlayerEventDao().updateParam4All(21, 0);
                        lanterRank.clear();
                        this.dataGetter.getPlayerEventDao().updateParam3All(21, 0);
                        if (LanternTask.rounds.get() >= 10) {
                            LanternTask.state = State.OVER;
                            LanternTask.timerLog.error("class:LanternTask#over");
                            break;
                        }
                        LanternTask.nextCd = TimeUtil.nowAddMs(60000L);
                        LanternTask.state = State.START;
                        LanternTask.rounds.getAndIncrement();
                        if (1 == LanternTask.rounds.get()) {
                            LanternTask.timerLog.error("class:LanternTask#start");
                        }
                        LanternTask.timerLog.error("class:LanternTask#roundStar#round" + LanternTask.rounds.get() + "#time:" + new Date(start));
                        System.out.println("class:LanternTask#roundStar#round" + LanternTask.rounds.get() + "#time:" + new Date(start));
                    }
                    else {
                        LanternTask.state = State.ING;
                        LanternEvent.randomMap.clear();
                        LanternTask.nextCd = TimeUtil.nowAddMs(30000L);
                        for (int i = 1; i <= 3; ++i) {
                            for (final RankData rd : lanterRank.getRankList(i)) {
                                final int rank2 = lanterRank.getRank(1, rd.playerId, i);
                                if (rank2 > 0) {
                                    this.dataGetter.getPlayerEventDao().updateParam4(rd.playerId, 21, rank2);
                                }
                                LanternTask.timerLog.error("logLanterRank#round#" + LanternTask.rounds.get() + "#playerId#" + rd.playerId + "#forceId#" + i + "#" + "num#" + rd.value + "#rank#" + rank2);
                            }
                        }
                        this.dataGetter.getPlayerEventDao().addParam2WithParam3(21);
                        this.dataGetter.getPlayerEventDao().updateParam3All(21, 0);
                        LanternTask.state = State.END;
                        LanternTask.timerLog.error("class:LanternTask#roundOver#round" + LanternTask.rounds.get() + "#time:" + new Date(start));
                        System.out.println("class:LanternTask#roundOver#round" + LanternTask.rounds.get() + "#time:" + new Date(start));
                    }
                }
                Thread.sleep(LanternTask.GAP_MS);
            }
            catch (Exception e) {
                LanternTask.timerLog.error("class:LanternTask#exception:", e);
                try {
                    Thread.sleep(LanternTask.GAP_MS);
                }
                catch (Exception ex) {
                    LanternTask.timerLog.error("class:LanternTask#exception#sleepException:", e);
                }
            }
        }
    }
    
    public static void main(final String[] args) {
        State state = State.END;
        state = State.START;
        System.out.println(state == State.END);
    }
    
    public enum State
    {
        START("START", 0), 
        END("END", 1), 
        ING("ING", 2), 
        OVER("OVER", 3);
        
        private State(final String s, final int n) {
        }
    }
}
