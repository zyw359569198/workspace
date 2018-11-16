package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class ArmsWeapon implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer baseAttribute;
    private Integer strengthen;
    private Double ironE;
    private Integer ironS;
    private Integer ironT;
    private String pic;
    private String intro;
    private Integer itemId;
    private Integer itemNum;
    private String cost;
    private String markTrace;
    private Map<Integer, Integer> costMap;
    private String introUngot;
    private String introGot;
    
    public Map<Integer, Integer> getCostMap() {
        return this.costMap;
    }
    
    public void setCostMap(final Map<Integer, Integer> costMap) {
        this.costMap = costMap;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getBaseAttribute() {
        return this.baseAttribute;
    }
    
    public void setBaseAttribute(final Integer baseAttribute) {
        this.baseAttribute = baseAttribute;
    }
    
    public Integer getStrengthen() {
        return this.strengthen;
    }
    
    public void setStrengthen(final Integer strengthen) {
        this.strengthen = strengthen;
    }
    
    public Double getIronE() {
        return this.ironE;
    }
    
    public void setIronE(final Double ironE) {
        this.ironE = ironE;
    }
    
    public Integer getIronS() {
        return this.ironS;
    }
    
    public void setIronS(final Integer ironS) {
        this.ironS = ironS;
    }
    
    public Integer getIronT() {
        return this.ironT;
    }
    
    public void setIronT(final Integer ironT) {
        this.ironT = ironT;
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
    
    public Integer getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }
    
    public Integer getItemNum() {
        return this.itemNum;
    }
    
    public void setItemNum(final Integer itemNum) {
        this.itemNum = itemNum;
    }
    
    public String getCost() {
        return this.cost;
    }
    
    public void setCost(final String cost) {
        this.cost = cost;
    }
    
    public String getMarkTrace() {
        return this.markTrace;
    }
    
    public void setMarkTrace(final String markTrace) {
        this.markTrace = markTrace;
    }
    
    public String getIntroUngot() {
        return this.introUngot;
    }
    
    public void setIntroUngot(final String introUngot) {
        this.introUngot = introUngot;
    }
    
    public String getIntroGot() {
        return this.introGot;
    }
    
    public void setIntroGot(final String introGot) {
        this.introGot = introGot;
    }
}
