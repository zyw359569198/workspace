package com.reign.gcld.battle.scene;

import com.reign.gcld.common.log.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;

public class BarbarainInvader extends Thread
{
    private static final Logger timerLog;
    public IDataGetter dataGetter;
    public int countryLv;
    public Barbarain barbarain;
    public int stepNum;
    public BitSet bitSet;
    public long startTime;
    public long endTime;
    public int step;
    public long[] timeSteps;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public BarbarainInvader(final IDataGetter dataGetter) {
        this.dataGetter = null;
        this.countryLv = 0;
        this.barbarain = null;
        this.stepNum = 0;
        this.bitSet = null;
        this.startTime = 0L;
        this.endTime = 0L;
        this.step = 0;
        this.timeSteps = null;
        this.dataGetter = dataGetter;
        final List<ForceInfo> forceInfoList = this.dataGetter.getForceInfoDao().getModels();
        int minCountryLv = Integer.MAX_VALUE;
        for (final ForceInfo temp : forceInfoList) {
            if (temp.getForceLv() < minCountryLv) {
                minCountryLv = temp.getForceLv();
            }
        }
        this.countryLv = minCountryLv;
        this.barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)minCountryLv);
        (this.bitSet = new BitSet(this.stepNum)).clear();
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + 7200000L;
        this.step = 0;
    }
    
    public BarbarainInvader(final IDataGetter dataGetter, final long endTime) {
        this.dataGetter = null;
        this.countryLv = 0;
        this.barbarain = null;
        this.stepNum = 0;
        this.bitSet = null;
        this.startTime = 0L;
        this.endTime = 0L;
        this.step = 0;
        this.timeSteps = null;
        this.dataGetter = dataGetter;
        final List<ForceInfo> forceInfoList = this.dataGetter.getForceInfoDao().getModels();
        int minCountryLv = Integer.MAX_VALUE;
        for (final ForceInfo temp : forceInfoList) {
            if (temp.getForceLv() < minCountryLv) {
                minCountryLv = temp.getForceLv();
            }
        }
        this.countryLv = minCountryLv;
        this.barbarain = (Barbarain)dataGetter.getBarbarainCache().get((Object)minCountryLv);
        (this.bitSet = new BitSet(this.stepNum)).clear();
        this.endTime = endTime;
        this.startTime = this.endTime - 7200000L;
        final long timeDifference = System.currentTimeMillis() - this.startTime;
        this.step = dataGetter.getKtMzSCache().getKtMzSByTime(timeDifference).getId();
    }
    
    @Override
    public void run() {
        final long start = System.currentTimeMillis();
        BarbarainInvader.timerLog.info(LogUtil.formatThreadLog("BarbarainInvader", "run", 0, 0L, "threadId:" + this.getId()));
        while (System.currentTimeMillis() < this.endTime) {
            try {
                final long timeDifference = System.currentTimeMillis() - this.startTime;
                final KtMzS temp = this.dataGetter.getKtMzSCache().getKtMzSByTime(timeDifference);
                if (temp != null) {
                    this.step = temp.getId();
                    if (!this.bitSet.get(this.step - 1)) {
                        final int npcNum = temp.getN();
                        for (final Integer cityId : temp.getWeiSet()) {
                            this.dataGetter.getBattleService().addBarbarainNpc(this.barbarain, 101, cityId, npcNum);
                        }
                        for (final Integer cityId : temp.getShuSet()) {
                            this.dataGetter.getBattleService().addBarbarainNpc(this.barbarain, 102, cityId, npcNum);
                        }
                        for (final Integer cityId : temp.getWuSet()) {
                            this.dataGetter.getBattleService().addBarbarainNpc(this.barbarain, 103, cityId, npcNum);
                        }
                        BarbarainInvader.timerLog.info(LogUtil.formatThreadLog("BarbarainInvader", "run", 1, System.currentTimeMillis() - start, "threadId:" + this.getId() + ",step:" + (this.step - 1) + ",npcNum:" + npcNum));
                        this.bitSet.set(this.step - 1);
                    }
                }
                else {
                    this.step = 0;
                }
                final KtMzS nextKtMzS = (KtMzS)this.dataGetter.getKtMzSCache().get((Object)(this.step + 1));
                if (nextKtMzS != null) {
                    final long nextCountDown = this.startTime + nextKtMzS.getT() * 60000L - System.currentTimeMillis();
                    if (nextCountDown <= 0L) {
                        ErrorSceneLog.getInstance().appendErrorMsg("nextCountDown is nor positive").append("nextCountDown", nextCountDown).append("step", this.step).appendMethodName("BarbarainInvader.run").flush();
                    }
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("countDown", nextCountDown);
                    doc.createElement("round", this.step + 1);
                    doc.endObject();
                    NewBattleManager.getInstance().BarbarainInvadeCountDown = nextCountDown;
                    NewBattleManager.getInstance().BarbarainInvadeRound = this.step + 1;
                    final byte[] send = doc.toByte();
                    for (final PlayerDto playerDto : Players.getAllPlayer()) {
                        Players.push(playerDto.playerId, PushCommand.PUSH_BARBARAIN_INVADE, send);
                    }
                    if (nextCountDown <= 60000L) {
                        try {
                            this.dataGetter.getChatUtil().sendBarbarainChat(1, LocalMessages.WEI_DEFENCE_MANZU_NAME);
                            this.dataGetter.getChatUtil().sendBarbarainChat(2, LocalMessages.SHU_DEFENCE_MANZU_NAME);
                            this.dataGetter.getChatUtil().sendBarbarainChat(3, LocalMessages.WU_DEFENCE_MANZU_NAME);
                        }
                        catch (Exception e2) {
                            ErrorSceneLog.getInstance().appendErrorMsg("ChatUtil.sendBarbarainChat exception").appendClassName("BarbarainInvader").appendMethodName("run").flush();
                        }
                    }
                }
                Thread.sleep(30000L);
                BarbarainInvader.timerLog.info(LogUtil.formatThreadLog("BarbarainInvader", "run", 2, System.currentTimeMillis() - start, "threadId:" + this.getId()));
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("BarbarainInvader run catch exception. step:" + (this.step - 1), e);
                BarbarainInvader.timerLog.error("BarbarainInvader run catch exception. threadId:" + this.getId() + " step:" + (this.step - 1), e);
                try {
                    Thread.sleep(30000L);
                }
                catch (InterruptedException e3) {
                    ErrorSceneLog.getInstance().error("BarbarainInvader InterruptedException. step:" + (this.step - 1), e);
                    BarbarainInvader.timerLog.error("BarbarainInvader InterruptedException. threadId:" + this.getId() + " step:" + (this.step - 1), e);
                }
            }
        }
        NewBattleManager.getInstance().BarbarainInvadeCountDown = 0L;
        NewBattleManager.getInstance().BarbarainInvadeRound = 0;
    }
}
