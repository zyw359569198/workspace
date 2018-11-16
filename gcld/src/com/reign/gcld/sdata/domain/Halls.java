package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Halls implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer pri;
    private Integer id;
    private Integer degree;
    private Integer officialId;
    private String nameList;
    private Integer output;
    private Integer chief;
    private String npcs;
    private String baseReward;
    private String ironReward;
    private Integer order;
    private String orderReward;
    private Integer hyT;
    private Integer hyN;
    private String pic;
    private String buildingNameWu;
    private String buildingNameShu;
    private String buildingNameWei;
    private Integer quality;
    private String pic1;
    
    public Integer getPri() {
        return this.pri;
    }
    
    public void setPri(final Integer pri) {
        this.pri = pri;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getOfficialId() {
        return this.officialId;
    }
    
    public void setOfficialId(final Integer officialId) {
        this.officialId = officialId;
    }
    
    public String getNameList() {
        return this.nameList;
    }
    
    public void setNameList(final String nameList) {
        this.nameList = nameList;
    }
    
    public Integer getOutput() {
        return this.output;
    }
    
    public void setOutput(final Integer output) {
        this.output = output;
    }
    
    public Integer getChief() {
        return this.chief;
    }
    
    public void setChief(final Integer chief) {
        this.chief = chief;
    }
    
    public String getNpcs() {
        return this.npcs;
    }
    
    public void setNpcs(final String npcs) {
        this.npcs = npcs;
    }
    
    public String getBaseReward() {
        return this.baseReward;
    }
    
    public void setBaseReward(final String baseReward) {
        this.baseReward = baseReward;
    }
    
    public String getIronReward() {
        return this.ironReward;
    }
    
    public void setIronReward(final String ironReward) {
        this.ironReward = ironReward;
    }
    
    public Integer getOrder() {
        return this.order;
    }
    
    public void setOrder(final Integer order) {
        this.order = order;
    }
    
    public String getOrderReward() {
        return this.orderReward;
    }
    
    public void setOrderReward(final String orderReward) {
        this.orderReward = orderReward;
    }
    
    public Integer getHyT() {
        return this.hyT;
    }
    
    public void setHyT(final Integer hyT) {
        this.hyT = hyT;
    }
    
    public Integer getHyN() {
        return this.hyN;
    }
    
    public void setHyN(final Integer hyN) {
        this.hyN = hyN;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getBuildingNameWu() {
        return this.buildingNameWu;
    }
    
    public void setBuildingNameWu(final String buildingNameWu) {
        this.buildingNameWu = buildingNameWu;
    }
    
    public String getBuildingNameShu() {
        return this.buildingNameShu;
    }
    
    public void setBuildingNameShu(final String buildingNameShu) {
        this.buildingNameShu = buildingNameShu;
    }
    
    public String getBuildingNameWei() {
        return this.buildingNameWei;
    }
    
    public void setBuildingNameWei(final String buildingNameWei) {
        this.buildingNameWei = buildingNameWei;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public String getPic1() {
        return this.pic1;
    }
    
    public void setPic1(final String pic1) {
        this.pic1 = pic1;
    }
}
