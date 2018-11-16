package com.reign.kfgz.dto;

import java.util.*;
import java.util.concurrent.*;
import com.reign.kfgz.dto.response.*;

public class KfgzBaseInfo
{
    public static final int STATE_UNBEGIN = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_END = 2;
    public static final int STATE_CLEARALL = 3;
    String gameServer1;
    int nation1;
    String gameServer2;
    String serverName1;
    String serverName2;
    int layerId;
    int nation2;
    Date gzStartTime;
    int seasonId;
    int gzId;
    int worldId;
    int worldstgId;
    int worldNpcId;
    int rewardgId;
    float expCoef;
    boolean canChoosenNpcAI;
    KfgzBattleRewardRes battleReward;
    Date gzEndTime;
    int state;
    long lastMubingTime;
    LinkedBlockingQueue<MailDto> mailQueue1;
    LinkedBlockingQueue<MailDto> mailQueue2;
    
    public KfgzBaseInfo(final int gzId, final int worldId, final int worldstgId, final int worldNpcId, final int rewardgId, final float expCoef) {
        this.expCoef = 1.0f;
        this.canChoosenNpcAI = false;
        this.state = 1;
        this.lastMubingTime = 0L;
        this.mailQueue1 = new LinkedBlockingQueue<MailDto>();
        this.mailQueue2 = new LinkedBlockingQueue<MailDto>();
        this.gzId = gzId;
        this.worldId = worldId;
        this.worldstgId = worldstgId;
        this.worldNpcId = worldNpcId;
        this.rewardgId = rewardgId;
        this.expCoef = expCoef;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public int getWorldId() {
        return this.worldId;
    }
    
    public void setWorldId(final int worldId) {
        this.worldId = worldId;
    }
    
    public int getWorldstgId() {
        return this.worldstgId;
    }
    
    public void setWorldstgId(final int worldstgId) {
        this.worldstgId = worldstgId;
    }
    
    public int getWorldNpcId() {
        return this.worldNpcId;
    }
    
    public void setWorldNpcId(final int worldNpcId) {
        this.worldNpcId = worldNpcId;
    }
    
    public int getState() {
        if (this.gzStartTime == null || this.gzStartTime.after(new Date())) {
            return 0;
        }
        if ((this.gzEndTime != null && new Date().after(this.gzEndTime)) || this.state >= 2) {
            return 2;
        }
        return this.state;
    }
    
    public int getRealState() {
        return this.state;
    }
    
    public boolean isClear() {
        return this.state == 3;
    }
    
    public void setState(final int state) {
        if (state == 1 && this.lastMubingTime == 0L) {
            this.lastMubingTime = System.currentTimeMillis();
        }
        this.state = state;
    }
    
    public long getLastMubingTime() {
        return this.lastMubingTime;
    }
    
    public void setLastMubingTime(final long lastMubingTime) {
        this.lastMubingTime = lastMubingTime;
    }
    
    public Date getGzEndTime() {
        return this.gzEndTime;
    }
    
    public void setGzEndTime(final Date gzEndTime) {
        this.gzEndTime = gzEndTime;
    }
    
    public String getGameServer1() {
        return this.gameServer1;
    }
    
    public void setGameServer1(final String gameServer1) {
        this.gameServer1 = gameServer1;
    }
    
    public int getNation1() {
        return this.nation1;
    }
    
    public void setNation1(final int nation1) {
        this.nation1 = nation1;
    }
    
    public String getGameServer2() {
        return this.gameServer2;
    }
    
    public void setGameServer2(final String gameServer2) {
        this.gameServer2 = gameServer2;
    }
    
    public int getNation2() {
        return this.nation2;
    }
    
    public void setNation2(final int nation2) {
        this.nation2 = nation2;
    }
    
    public Date getGzStartTime() {
        return this.gzStartTime;
    }
    
    public void setGzStartTime(final Date gzStartTime) {
        this.gzStartTime = gzStartTime;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getRewardgId() {
        return this.rewardgId;
    }
    
    public void setRewardgId(final int rewardgId) {
        this.rewardgId = rewardgId;
    }
    
    public KfgzBattleRewardRes getBattleReward() {
        return this.battleReward;
    }
    
    public void setBattleReward(final KfgzBattleRewardRes battleReward) {
        this.battleReward = battleReward;
    }
    
    public String getServerName1() {
        return this.serverName1;
    }
    
    public void setServerName1(final String serverName1) {
        this.serverName1 = serverName1;
    }
    
    public String getServerName2() {
        return this.serverName2;
    }
    
    public void setServerName2(final String serverName2) {
        this.serverName2 = serverName2;
    }
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public boolean canChoosenNpcAI() {
        return this.canChoosenNpcAI;
    }
    
    public void setCanChoosenNpcAI(final boolean canChoosenNpcAI) {
        this.canChoosenNpcAI = canChoosenNpcAI;
    }
    
    public float getExpCoef() {
        if (this.expCoef < 1.0f) {
            return 1.0f;
        }
        return this.expCoef;
    }
    
    public void setExpCoef(final float expCoef) {
        this.expCoef = expCoef;
    }
    
    public long getEndCD() {
        if (this.gzEndTime != null) {
            long cd = this.gzEndTime.getTime() - new Date().getTime();
            if (cd < 0L) {
                cd = 0L;
            }
            return cd;
        }
        return 0L;
    }
    
    public void addMail1(final MailDto mail) {
        this.mailQueue1.add(mail);
    }
    
    public void addMail2(final MailDto mail) {
        this.mailQueue2.add(mail);
    }
    
    public LinkedBlockingQueue<MailDto> getAndClearMail1() {
        final LinkedBlockingQueue<MailDto> mailQueue = this.mailQueue1;
        this.mailQueue1 = new LinkedBlockingQueue<MailDto>();
        return mailQueue;
    }
    
    public LinkedBlockingQueue<MailDto> getAndClearMail2() {
        final LinkedBlockingQueue<MailDto> mailQueue = this.mailQueue2;
        this.mailQueue2 = new LinkedBlockingQueue<MailDto>();
        return mailQueue;
    }
}
