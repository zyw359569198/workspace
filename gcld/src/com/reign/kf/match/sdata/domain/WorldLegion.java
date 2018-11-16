package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldLegion implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer ownerExpS;
    private Integer chiefExpS;
    private Integer max;
    private String name;
    private Integer pFoodS;
    private Integer ownerExp;
    private Integer goldDeploy;
    private Integer goldInit;
    private Integer chiefExp;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getOwnerExpS() {
        return this.ownerExpS;
    }
    
    public void setOwnerExpS(final Integer ownerExpS) {
        this.ownerExpS = ownerExpS;
    }
    
    public Integer getChiefExpS() {
        return this.chiefExpS;
    }
    
    public void setChiefExpS(final Integer chiefExpS) {
        this.chiefExpS = chiefExpS;
    }
    
    public Integer getMax() {
        return this.max;
    }
    
    public void setMax(final Integer max) {
        this.max = max;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getPFoodS() {
        return this.pFoodS;
    }
    
    public void setPFoodS(final Integer pFoodS) {
        this.pFoodS = pFoodS;
    }
    
    public Integer getOwnerExp() {
        return this.ownerExp;
    }
    
    public void setOwnerExp(final Integer ownerExp) {
        this.ownerExp = ownerExp;
    }
    
    public Integer getGoldDeploy() {
        return this.goldDeploy;
    }
    
    public void setGoldDeploy(final Integer goldDeploy) {
        this.goldDeploy = goldDeploy;
    }
    
    public Integer getGoldInit() {
        return this.goldInit;
    }
    
    public void setGoldInit(final Integer goldInit) {
        this.goldInit = goldInit;
    }
    
    public Integer getChiefExp() {
        return this.chiefExp;
    }
    
    public void setChiefExp(final Integer chiefExp) {
        this.chiefExp = chiefExp;
    }
}
