package com.reign.gcld.world.common;

import java.util.*;

public class FarmingInfo
{
    public int vId;
    public int playerId;
    public int generalId;
    public int type;
    public int lv;
    public int rewardNum;
    public Date endTimeDate;
    
    public FarmingInfo() {
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.vId).append(",");
        sb.append(this.playerId).append(",").append(this.generalId).append(",").append(this.type).append(",").append(this.rewardNum).append(",").append(this.endTimeDate.getTime()).append(",").append(this.lv).append(",");
        return sb.toString();
    }
    
    public FarmingInfo(final String info) {
        final String[] infos = info.split(",");
        if (infos.length >= 7) {
            this.vId = Integer.parseInt(infos[0]);
            this.playerId = Integer.parseInt(infos[1]);
            this.generalId = Integer.parseInt(infos[2]);
            this.type = Integer.parseInt(infos[3]);
            this.rewardNum = Integer.parseInt(infos[4]);
            this.endTimeDate = new Date(Long.parseLong(infos[5]));
            this.lv = Integer.parseInt(infos[6]);
        }
    }
}
