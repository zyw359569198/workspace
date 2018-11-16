package com.reign.gcld.juben.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;

public class TrickDto
{
    private static final Logger errorLogger;
    private int id;
    private int lv;
    private String type;
    private long lastTime;
    private long protectTime;
    private int forceId;
    private int extraPar;
    
    static {
        errorLogger = CommonLog.getLog(TrickDto.class);
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getLv() {
        return this.lv;
    }
    
    public void setLv(final int lv) {
        this.lv = lv;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public long getLastTime() {
        return this.lastTime;
    }
    
    public void setLastTime(final long lastTime) {
        this.lastTime = lastTime;
    }
    
    public long getProtectTime() {
        return this.protectTime;
    }
    
    public void setProtectTime(final long protectTime) {
        this.protectTime = protectTime;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getExtraPar() {
        return this.extraPar;
    }
    
    public void setExtraPar(final int extraPar) {
        this.extraPar = extraPar;
    }
    
    public TrickDto(final int id, final int lv, final String type, final long lastTime, final long protectTime, final int forceId, final int extraPar) {
        this.id = 1;
        this.lv = 1;
        this.type = "\u9f13\u821e";
        this.lastTime = 0L;
        this.protectTime = 0L;
        this.forceId = 0;
        this.extraPar = 0;
        this.id = id;
        this.lv = lv;
        this.type = type;
        this.lastTime = lastTime;
        this.protectTime = protectTime;
        this.forceId = forceId;
        this.extraPar = extraPar;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.id).append("-").append(this.lv).append("-").append(this.lastTime).append("-").append(this.protectTime).append("-").append(this.forceId);
        if (this.type.equalsIgnoreCase("xianjing")) {
            sb.append("-").append(this.extraPar);
        }
        return sb.toString();
    }
    
    public TrickDto(final String[] single, final IDataGetter dataGetter) {
        this.id = 1;
        this.lv = 1;
        this.type = "\u9f13\u821e";
        this.lastTime = 0L;
        this.protectTime = 0L;
        this.forceId = 0;
        this.extraPar = 0;
        try {
            final int id = Integer.parseInt(single[0]);
            final int lv = Integer.parseInt(single[1]);
            final long lastTime = Long.parseLong(single[2]);
            final long protectTime = Long.parseLong(single[3]);
            final int forceId = Integer.parseInt(single[4]);
            this.id = id;
            this.lv = lv;
            this.lastTime = lastTime;
            this.protectTime = protectTime;
            this.forceId = forceId;
            if (this.type.equalsIgnoreCase("xianjing")) {
                this.extraPar = Integer.parseInt(single[5]);
            }
            else {
                this.extraPar = 0;
            }
            this.type = ((Stratagem)dataGetter.getStratagemCache().get((Object)id)).getType();
        }
        catch (Exception e) {
            TrickDto.errorLogger.error("trickInfo:" + single.toString());
            TrickDto.errorLogger.error(e.getMessage());
            TrickDto.errorLogger.error(this, e);
        }
    }
}
