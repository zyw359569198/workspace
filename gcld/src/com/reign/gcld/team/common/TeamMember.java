package com.reign.gcld.team.common;

import java.util.*;
import com.reign.gcld.player.domain.*;

public class TeamMember implements Comparable<TeamMember>
{
    private String teamId;
    private int playerId;
    private int forceId;
    private String playerName;
    private int playerLv;
    private int pic;
    private boolean creator;
    private Date enterTime;
    private List<GeneralInfo> generalInfo;
    
    public TeamMember(final String teamId, final Player player, final boolean isCreator, final List<GeneralInfo> generalInfo) {
        this.teamId = teamId;
        this.creator = isCreator;
        this.enterTime = new Date();
        this.generalInfo = generalInfo;
        this.setPlayer(player);
    }
    
    private void setPlayer(final Player player) {
        this.playerId = player.getPlayerId();
        this.playerName = player.getPlayerName();
        this.playerLv = player.getPlayerLv();
        this.pic = player.getPic();
        this.forceId = player.getForceId();
    }
    
    public List<GeneralInfo> getGeneralInfo() {
        return this.generalInfo;
    }
    
    public String getTeamId() {
        return this.teamId;
    }
    
    public void setTeamId(final String teamId) {
        this.teamId = teamId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getPic() {
        return this.pic;
    }
    
    public boolean isCreator() {
        return this.creator;
    }
    
    public Date getEnterTime() {
        return this.enterTime;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    @Override
    public int compareTo(final TeamMember o) {
        if (o == null) {
            return -1;
        }
        return this.enterTime.compareTo(o.getEnterTime());
    }
}
