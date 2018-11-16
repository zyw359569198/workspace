package com.reign.kf.comm.param.gw;

public class UpdateSeasonParam
{
    private int id;
    private int season;
    private int state;
    private String tag;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public void setSeason(final int season) {
        this.season = season;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
}
