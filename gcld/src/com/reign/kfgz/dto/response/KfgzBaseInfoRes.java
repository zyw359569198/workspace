package com.reign.kfgz.dto.response;

import java.util.concurrent.*;
import org.codehaus.jackson.annotate.*;
import java.util.*;

@JsonAutoDetect
public class KfgzBaseInfoRes
{
    public static final int STATE_UNBEGIN = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_END = 2;
    public static final int STATE_CLEARALL = 3;
    public static final int STATE_NONE = 4;
    String gameServer1;
    String serverName1;
    int nation1;
    String gameServer2;
    String serverName2;
    int nation2;
    Date gzStartTime;
    int seasonId;
    int gzId;
    int worldId;
    int worldstgId;
    int worldNpcId;
    Date gzEndTime;
    int state;
    long lastMubingTime;
    LinkedBlockingQueue<MailDto> mailQueue;
    
    public KfgzBaseInfoRes() {
        this.state = 1;
        this.lastMubingTime = 0L;
        this.mailQueue = new LinkedBlockingQueue<MailDto>();
    }
    
    public LinkedBlockingQueue<MailDto> getMailQueue() {
        return this.mailQueue;
    }
    
    public void setMailQueue(final LinkedBlockingQueue<MailDto> mailQueue) {
        this.mailQueue = mailQueue;
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
    
    @JsonIgnore
    public int getState() {
        if (this.gzStartTime == null || this.gzStartTime.after(new Date())) {
            return 0;
        }
        if ((this.gzEndTime != null && new Date().after(this.gzEndTime)) || this.state >= 2) {
            return 2;
        }
        return this.state;
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
    
    public void addMail(final MailDto mail) {
        this.mailQueue.add(mail);
    }
    
    public void addMails(final LinkedBlockingQueue<MailDto> queue) {
        this.mailQueue.addAll((Collection<?>)queue);
    }
}
