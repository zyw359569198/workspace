package com.reign.gcld.team.common;

import java.util.*;
import java.util.concurrent.*;

public class Team
{
    private String teamId;
    private String teamName;
    private String teamType;
    private Date createTime;
    private Integer maxNum;
    private Integer minNum;
    private Integer curNum;
    private ConcurrentHashMap<Integer, TeamMember> memberMap;
    private ConcurrentHashMap<String, Long> kickMap;
    private TeamMember creator;
    private int worldLegionId;
    private long totalForces;
    private long totalMaxForces;
    private int ownerAddExp;
    private double inspireEffect;
    private boolean order;
    
    public Team() {
        this.kickMap = new ConcurrentHashMap<String, Long>();
    }
    
    public Team(final String teamId, final String teamName, final int maxNum, final String teamType, final int worldLegionId, final TeamMember creator, final int ownerAddExp, final Date createTime) {
        this.kickMap = new ConcurrentHashMap<String, Long>();
        this.teamId = teamId;
        this.teamName = teamName;
        this.maxNum = maxNum;
        this.teamType = teamType;
        this.createTime = createTime;
        this.minNum = 1;
        this.curNum = 0;
        this.memberMap = new ConcurrentHashMap<Integer, TeamMember>();
        this.creator = creator;
        this.worldLegionId = worldLegionId;
        this.ownerAddExp = ownerAddExp;
    }
    
    public ConcurrentHashMap<String, Long> getKickMap() {
        return this.kickMap;
    }
    
    public void setKickMap(final ConcurrentHashMap<String, Long> kickMap) {
        this.kickMap = kickMap;
    }
    
    public boolean isOrder() {
        return this.order;
    }
    
    public void setOrder(final boolean order) {
        this.order = order;
    }
    
    public double getInspireEffect() {
        return this.inspireEffect;
    }
    
    public void setInspireEffect(final double inspireEffect) {
        this.inspireEffect = inspireEffect;
    }
    
    public int getOwnerAddExp() {
        return this.ownerAddExp;
    }
    
    public void setOwnerAddExp(final int ownerAddExp) {
        this.ownerAddExp = ownerAddExp;
    }
    
    public long getTotalMaxForces() {
        return this.totalMaxForces;
    }
    
    public void setTotalMaxForces(final long totalMaxForces) {
        this.totalMaxForces = totalMaxForces;
    }
    
    public long getTotalForces() {
        return this.totalForces;
    }
    
    public void setTotalForces(final long totalForces) {
        this.totalForces = totalForces;
    }
    
    public int getWorldLegionId() {
        return this.worldLegionId;
    }
    
    public void setWorldLegionId(final int worldLegionId) {
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
    
    public String getTeamType() {
        return this.teamType;
    }
    
    public void setTeamType(final String teamType) {
        this.teamType = teamType;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getMaxNum() {
        return this.maxNum;
    }
    
    public void setMaxNum(final Integer maxNum) {
        this.maxNum = maxNum;
    }
    
    public Integer getMinNum() {
        return this.minNum;
    }
    
    public void setMinNum(final Integer minNum) {
        this.minNum = minNum;
    }
    
    public Integer getCurNum() {
        return this.curNum;
    }
    
    public void setCurNum(final Integer curNum) {
        this.curNum = curNum;
    }
    
    public ConcurrentHashMap<Integer, TeamMember> getMemberMap() {
        return this.memberMap;
    }
    
    public void setMemberMap(final ConcurrentHashMap<Integer, TeamMember> memberMap) {
        this.memberMap = memberMap;
    }
    
    public TeamMember getCreator() {
        return this.creator;
    }
    
    public void setCreator(final TeamMember creator) {
        this.creator = creator;
    }
}
