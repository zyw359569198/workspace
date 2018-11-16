package com.reign.kfzb.dto.response;

import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfzbPhase1RewardInfo
{
    int cId;
    boolean isFinish;
    List<Integer> rewardTicketList;
    int totalLayer;
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public boolean isFinish() {
        return this.isFinish;
    }
    
    public void setFinish(final boolean isFinish) {
        this.isFinish = isFinish;
    }
    
    public List<Integer> getRewardTicketList() {
        return this.rewardTicketList;
    }
    
    public void setRewardTicketList(final List<Integer> rewardTicketList) {
        this.rewardTicketList = rewardTicketList;
    }
    
    public int getTotalLayer() {
        return this.totalLayer;
    }
    
    public void setTotalLayer(final int totalLayer) {
        this.totalLayer = totalLayer;
    }
    
    @JsonIgnore
    public int getLostLayer() {
        if (this.isCampain()) {
            return 0;
        }
        if (!this.isFinish) {
            return -1;
        }
        final int t1 = this.totalLayer - this.rewardTicketList.size();
        if (t1 > 4) {
            return t1;
        }
        return t1 + 1;
    }
    
    @JsonIgnore
    public boolean isCampain() {
        return !this.isFinish && this.totalLayer == this.rewardTicketList.size();
    }
}
