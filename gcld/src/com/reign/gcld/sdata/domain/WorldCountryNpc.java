package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldCountryNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer country;
    private String name;
    private Integer degree;
    private Integer tacticId;
    private Integer leader;
    private Integer strength;
    private Integer troopId;
    private String pic;
    private String intro;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getCountry() {
        return this.country;
    }
    
    public void setCountry(final Integer country) {
        this.country = country;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getTacticId() {
        return this.tacticId;
    }
    
    public void setTacticId(final Integer tacticId) {
        this.tacticId = tacticId;
    }
    
    public Integer getLeader() {
        return this.leader;
    }
    
    public void setLeader(final Integer leader) {
        this.leader = leader;
    }
    
    public Integer getStrength() {
        return this.strength;
    }
    
    public void setStrength(final Integer strength) {
        this.strength = strength;
    }
    
    public Integer getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final Integer troopId) {
        this.troopId = troopId;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
}
