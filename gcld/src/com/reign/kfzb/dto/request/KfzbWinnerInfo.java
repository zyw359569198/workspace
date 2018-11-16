package com.reign.kfzb.dto.request;

import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfzbWinnerInfo
{
    List<KfzbTopPlayerInfo> list;
    int seasonId;
    
    public KfzbWinnerInfo() {
        this.list = new ArrayList<KfzbTopPlayerInfo>();
    }
    
    public List<KfzbTopPlayerInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbTopPlayerInfo> list) {
        this.list = list;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    @JsonIgnore
    public Map<Integer, KfzbTopPlayerInfo> getKfzbWinnerMap() {
        final Map<Integer, KfzbTopPlayerInfo> map = new HashMap<Integer, KfzbTopPlayerInfo>();
        for (final KfzbTopPlayerInfo playerInfo : this.list) {
            map.put(playerInfo.getPos(), playerInfo);
        }
        return map;
    }
}
