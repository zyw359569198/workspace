package com.reign.gcld.general.dto;

public class GeneralMoveDto
{
    public int cityState;
    public long startMoveTime;
    public long nextMoveTime;
    public int startCityId;
    public String moveLine;
    public int taskId;
    public int type;
    public long runawayTime;
    public long farmtime;
    
    public GeneralMoveDto() {
        this.moveLine = "";
    }
}
