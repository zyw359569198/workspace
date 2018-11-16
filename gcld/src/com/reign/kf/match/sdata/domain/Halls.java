package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;
import org.apache.commons.lang.*;

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
    public static final String SPLIT1 = ";";
    public static final String SPLIT2 = ",";
    public static final String REWARD_TYPE_FOOD = "food";
    
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
    
    public int getFoodAdd() {
        if (StringUtils.isBlank(this.orderReward)) {
            return 0;
        }
        final String[] ss = this.orderReward.split(";");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            final String[] ss2 = this.orderReward.split(",");
            if (ss2[0].equals("food")) {
                return Integer.parseInt(ss2[1]);
            }
        }
        return 0;
    }
}
