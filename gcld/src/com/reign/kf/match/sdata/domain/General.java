package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.kf.comm.param.match.*;

public class General implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer quality;
    private Integer leader;
    private Integer strength;
    private Integer intel;
    private Integer politics;
    private Integer upExpS;
    private Double upExpE;
    private String pic;
    private String intro;
    private Integer tacticId;
    private Integer troop;
    private Integer stratagemId;
    private Integer broadCast;
    private String sp;
    private String spI;
    private KfSpecialGeneral generalSpecialInfo;
    
    public KfSpecialGeneral getGeneralSpecialInfo() {
        return this.generalSpecialInfo;
    }
    
    public void setGeneralSpecialInfo(final KfSpecialGeneral generalSpecialInfo) {
        this.generalSpecialInfo = generalSpecialInfo;
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
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
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
    
    public Integer getIntel() {
        return this.intel;
    }
    
    public void setIntel(final Integer intel) {
        this.intel = intel;
    }
    
    public Integer getPolitics() {
        return this.politics;
    }
    
    public void setPolitics(final Integer politics) {
        this.politics = politics;
    }
    
    public Integer getUpExpS() {
        return this.upExpS;
    }
    
    public void setUpExpS(final Integer upExpS) {
        this.upExpS = upExpS;
    }
    
    public Double getUpExpE() {
        return this.upExpE;
    }
    
    public void setUpExpE(final Double upExpE) {
        this.upExpE = upExpE;
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
    
    public Integer getTacticId() {
        return this.tacticId;
    }
    
    public void setTacticId(final Integer tacticId) {
        this.tacticId = tacticId;
    }
    
    public Integer getTroop() {
        return this.troop;
    }
    
    public void setTroop(final Integer troop) {
        this.troop = troop;
    }
    
    public Integer getStratagemId() {
        return this.stratagemId;
    }
    
    public void setStratagemId(final Integer stratagemId) {
        this.stratagemId = stratagemId;
    }
    
    public Integer getBroadCast() {
        return this.broadCast;
    }
    
    public void setBroadCast(final Integer broadCast) {
        this.broadCast = broadCast;
    }
    
    public String getSp() {
        return this.sp;
    }
    
    public void setSp(final String sp) {
        this.sp = sp;
    }
    
    public String getSpI() {
        return this.spI;
    }
    
    public void setSpI(final String spI) {
        this.spI = spI;
    }
}
