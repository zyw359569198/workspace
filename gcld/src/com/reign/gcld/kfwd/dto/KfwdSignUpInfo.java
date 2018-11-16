package com.reign.gcld.kfwd.dto;

import com.reign.kf.comm.entity.kfwd.response.*;

public class KfwdSignUpInfo
{
    int playerId;
    boolean canSigned;
    boolean isSigned;
    int scheduleId;
    int completedId;
    String certifacate;
    KfwdGInfo pgInfo;
    String matchAdress;
    String matchPort;
    
    public String getMatchAdress() {
        return this.matchAdress;
    }
    
    public void setMatchAdress(final String matchAdress) {
        this.matchAdress = matchAdress;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public boolean isCanSigned() {
        return this.canSigned;
    }
    
    public void setCanSigned(final boolean canSigned) {
        this.canSigned = canSigned;
    }
    
    public boolean isSigned() {
        return this.isSigned;
    }
    
    public void setSigned(final boolean isSigned) {
        this.isSigned = isSigned;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public KfwdGInfo getPgInfo() {
        return this.pgInfo;
    }
    
    public void setPgInfo(final KfwdGInfo pgInfo) {
        this.pgInfo = pgInfo;
    }
    
    public int getCompletedId() {
        return this.completedId;
    }
    
    public void setCompletedId(final int completedId) {
        this.completedId = completedId;
    }
    
    public String getCertifacate() {
        return this.certifacate;
    }
    
    public void setCertifacate(final String certifacate) {
        this.certifacate = certifacate;
    }
    
    public String getMatchPort() {
        return this.matchPort;
    }
    
    public void setMatchPort(final String matchPort) {
        this.matchPort = matchPort;
    }
}
