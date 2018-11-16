package com.reign.kf.comm.entity.match;

public class MatchStateEntity
{
    public static final int UN_SIGN = 0;
    public static final int SIGNING = 1;
    public static final int END_SIGN = 2;
    public static final int END_ASSIGN = 3;
    public static final int MATCHING = 4;
    public static final int MATCH_OVER = 6;
    public static final int MATCH_FINISHED = 7;
    private String matchTag;
    private int state;
    private long cd;
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public long getCd() {
        return this.cd;
    }
    
    public void setCd(final long cd) {
        this.cd = cd;
    }
}
