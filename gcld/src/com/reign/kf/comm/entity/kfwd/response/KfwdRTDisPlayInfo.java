package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;
import com.reign.kf.comm.entity.kfwd.request.*;

public class KfwdRTDisPlayInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int matchId;
    private int round;
    private int sRound;
    private KfwdPlayerInfo pInfo1;
    private KfwdPlayerInfo pInfo2;
    private KfwdGInfo gInfo1;
    private KfwdGInfo gInfo2;
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getsRound() {
        return this.sRound;
    }
    
    public void setsRound(final int sRound) {
        this.sRound = sRound;
    }
    
    public KfwdPlayerInfo getpInfo1() {
        return this.pInfo1;
    }
    
    public void setpInfo1(final KfwdPlayerInfo pInfo1) {
        this.pInfo1 = pInfo1;
    }
    
    public KfwdPlayerInfo getpInfo2() {
        return this.pInfo2;
    }
    
    public void setpInfo2(final KfwdPlayerInfo pInfo2) {
        this.pInfo2 = pInfo2;
    }
    
    public KfwdGInfo getgInfo1() {
        return this.gInfo1;
    }
    
    public void setgInfo1(final KfwdGInfo gInfo1) {
        this.gInfo1 = gInfo1;
    }
    
    public KfwdGInfo getgInfo2() {
        return this.gInfo2;
    }
    
    public void setgInfo2(final KfwdGInfo gInfo2) {
        this.gInfo2 = gInfo2;
    }
}
