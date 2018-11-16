package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldPaidB implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer q;
    private Integer kl;
    private String nameWei;
    private String nameShu;
    private String nameWu;
    private String armisWei;
    private String armisShu;
    private String armisWu;
    private Integer lv;
    private String weiR1;
    private String weiR2;
    private String weiR3;
    private String shuR1;
    private String shuR2;
    private String shuR3;
    private String wuR1;
    private String wuR2;
    private String wuR3;
    private Integer n1;
    private Integer n2;
    private Integer n3;
    private Integer c1;
    private Integer cm;
    private Integer cd;
    private Integer cdm;
    private Integer ce;
    private Integer[] weiArmyIds;
    private Integer[] shuArmyIds;
    private Integer[] wuArmyIds;
    private Integer[] weiR1CityIds;
    private Integer[] weiR2CityIds;
    private Integer[] weiR3CityIds;
    private Integer[] shuR1CityIds;
    private Integer[] shuR2CityIds;
    private Integer[] shuR3CityIds;
    private Integer[] wuR1CityIds;
    private Integer[] wuR2CityIds;
    private Integer[] wuR3CityIds;
    
    public WorldPaidB() {
        this.weiArmyIds = null;
        this.shuArmyIds = null;
        this.wuArmyIds = null;
        this.weiR1CityIds = null;
        this.weiR2CityIds = null;
        this.weiR3CityIds = null;
        this.shuR1CityIds = null;
        this.shuR2CityIds = null;
        this.shuR3CityIds = null;
        this.wuR1CityIds = null;
        this.wuR2CityIds = null;
        this.wuR3CityIds = null;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getQ() {
        return this.q;
    }
    
    public void setQ(final Integer q) {
        this.q = q;
    }
    
    public Integer getKl() {
        return this.kl;
    }
    
    public void setKl(final Integer kl) {
        this.kl = kl;
    }
    
    public String getNameWei() {
        return this.nameWei;
    }
    
    public void setNameWei(final String nameWei) {
        this.nameWei = nameWei;
    }
    
    public String getNameShu() {
        return this.nameShu;
    }
    
    public void setNameShu(final String nameShu) {
        this.nameShu = nameShu;
    }
    
    public String getNameWu() {
        return this.nameWu;
    }
    
    public void setNameWu(final String nameWu) {
        this.nameWu = nameWu;
    }
    
    public String getArmisWei() {
        return this.armisWei;
    }
    
    public void setArmisWei(final String armisWei) {
        this.armisWei = armisWei;
    }
    
    public String getArmisShu() {
        return this.armisShu;
    }
    
    public void setArmisShu(final String armisShu) {
        this.armisShu = armisShu;
    }
    
    public String getArmisWu() {
        return this.armisWu;
    }
    
    public void setArmisWu(final String armisWu) {
        this.armisWu = armisWu;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getWeiR1() {
        return this.weiR1;
    }
    
    public void setWeiR1(final String weiR1) {
        this.weiR1 = weiR1;
    }
    
    public String getWeiR2() {
        return this.weiR2;
    }
    
    public void setWeiR2(final String weiR2) {
        this.weiR2 = weiR2;
    }
    
    public String getWeiR3() {
        return this.weiR3;
    }
    
    public void setWeiR3(final String weiR3) {
        this.weiR3 = weiR3;
    }
    
    public String getShuR1() {
        return this.shuR1;
    }
    
    public void setShuR1(final String shuR1) {
        this.shuR1 = shuR1;
    }
    
    public String getShuR2() {
        return this.shuR2;
    }
    
    public void setShuR2(final String shuR2) {
        this.shuR2 = shuR2;
    }
    
    public String getShuR3() {
        return this.shuR3;
    }
    
    public void setShuR3(final String shuR3) {
        this.shuR3 = shuR3;
    }
    
    public String getWuR1() {
        return this.wuR1;
    }
    
    public void setWuR1(final String wuR1) {
        this.wuR1 = wuR1;
    }
    
    public String getWuR2() {
        return this.wuR2;
    }
    
    public void setWuR2(final String wuR2) {
        this.wuR2 = wuR2;
    }
    
    public String getWuR3() {
        return this.wuR3;
    }
    
    public void setWuR3(final String wuR3) {
        this.wuR3 = wuR3;
    }
    
    public Integer getN1() {
        return this.n1;
    }
    
    public void setN1(final Integer n1) {
        this.n1 = n1;
    }
    
    public Integer getN2() {
        return this.n2;
    }
    
    public void setN2(final Integer n2) {
        this.n2 = n2;
    }
    
    public Integer getN3() {
        return this.n3;
    }
    
    public void setN3(final Integer n3) {
        this.n3 = n3;
    }
    
    public Integer getC1() {
        return this.c1;
    }
    
    public void setC1(final Integer c1) {
        this.c1 = c1;
    }
    
    public Integer getCm() {
        return this.cm;
    }
    
    public void setCm(final Integer cm) {
        this.cm = cm;
    }
    
    public Integer getCd() {
        return this.cd;
    }
    
    public void setCd(final Integer cd) {
        this.cd = cd;
    }
    
    public Integer getCdm() {
        return this.cdm;
    }
    
    public void setCdm(final Integer cdm) {
        this.cdm = cdm;
    }
    
    public Integer getCe() {
        return this.ce;
    }
    
    public void setCe(final Integer ce) {
        this.ce = ce;
    }
    
    public Integer[] getWeiArmyIds() {
        return this.weiArmyIds;
    }
    
    public void setWeiArmyIds(final Integer[] weiArmyIds) {
        this.weiArmyIds = weiArmyIds;
    }
    
    public Integer[] getShuArmyIds() {
        return this.shuArmyIds;
    }
    
    public void setShuArmyIds(final Integer[] shuArmyIds) {
        this.shuArmyIds = shuArmyIds;
    }
    
    public Integer[] getWuArmyIds() {
        return this.wuArmyIds;
    }
    
    public void setWuArmyIds(final Integer[] wuArmyIds) {
        this.wuArmyIds = wuArmyIds;
    }
    
    public Integer[] getWeiR1CityIds() {
        return this.weiR1CityIds;
    }
    
    public void setWeiR1CityIds(final Integer[] weiR1CityIds) {
        this.weiR1CityIds = weiR1CityIds;
    }
    
    public Integer[] getWeiR2CityIds() {
        return this.weiR2CityIds;
    }
    
    public void setWeiR2CityIds(final Integer[] weiR2CityIds) {
        this.weiR2CityIds = weiR2CityIds;
    }
    
    public Integer[] getWeiR3CityIds() {
        return this.weiR3CityIds;
    }
    
    public void setWeiR3CityIds(final Integer[] weiR3CityIds) {
        this.weiR3CityIds = weiR3CityIds;
    }
    
    public Integer[] getShuR1CityIds() {
        return this.shuR1CityIds;
    }
    
    public void setShuR1CityIds(final Integer[] shuR1CityIds) {
        this.shuR1CityIds = shuR1CityIds;
    }
    
    public Integer[] getShuR2CityIds() {
        return this.shuR2CityIds;
    }
    
    public void setShuR2CityIds(final Integer[] shuR2CityIds) {
        this.shuR2CityIds = shuR2CityIds;
    }
    
    public Integer[] getShuR3CityIds() {
        return this.shuR3CityIds;
    }
    
    public void setShuR3CityIds(final Integer[] shuR3CityIds) {
        this.shuR3CityIds = shuR3CityIds;
    }
    
    public Integer[] getWuR1CityIds() {
        return this.wuR1CityIds;
    }
    
    public void setWuR1CityIds(final Integer[] wuR1CityIds) {
        this.wuR1CityIds = wuR1CityIds;
    }
    
    public Integer[] getWuR2CityIds() {
        return this.wuR2CityIds;
    }
    
    public void setWuR2CityIds(final Integer[] wuR2CityIds) {
        this.wuR2CityIds = wuR2CityIds;
    }
    
    public Integer[] getWuR3CityIds() {
        return this.wuR3CityIds;
    }
    
    public void setWuR3CityIds(final Integer[] wuR3CityIds) {
        this.wuR3CityIds = wuR3CityIds;
    }
}
