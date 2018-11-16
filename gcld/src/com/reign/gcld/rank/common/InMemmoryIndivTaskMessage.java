package com.reign.gcld.rank.common;

import com.reign.gcld.common.message.*;

public class InMemmoryIndivTaskMessage implements Message
{
    public int playerId;
    public int forceId;
    public String identifier;
    public int count;
    
    public InMemmoryIndivTaskMessage(final String params) {
        final String[] single = params.split(",");
        this.playerId = Integer.parseInt(single[0]);
        this.forceId = Integer.parseInt(single[1]);
        this.identifier = single[2];
        this.count = Integer.parseInt(single[3]);
    }
    
    public InMemmoryIndivTaskMessage() {
        this.count = 1;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.playerId).append(",").append(this.forceId).append(",").append(this.identifier).append(",").append(this.count).append(",");
        return sb.toString();
    }
}
