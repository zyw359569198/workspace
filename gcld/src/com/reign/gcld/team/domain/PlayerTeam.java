package com.reign.gcld.team.domain;

import com.reign.framework.mybatis.*;

public class PlayerTeam implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String playerGeneralId;
    private Integer worldLegionId;
    private String teamId;
    private String teamName;
    private Long createTime;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerGeneralId() {
        return this.playerGeneralId;
    }
    
    public void setPlayerGeneralId(final String playerGeneralId) {
        this.playerGeneralId = playerGeneralId;
    }
    
    public Integer getWorldLegionId() {
        return this.worldLegionId;
    }
    
    public void setWorldLegionId(final Integer worldLegionId) {
        this.worldLegionId = worldLegionId;
    }
    
    public String getTeamId() {
        return this.teamId;
    }
    
    public void setTeamId(final String teamId) {
        this.teamId = teamId;
    }
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }
    
    public Long getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }
}
