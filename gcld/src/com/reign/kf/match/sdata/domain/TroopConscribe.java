package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class TroopConscribe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer troopId;
    private String request;
    private String inputType;
    private Double outputE;
    private Double copper;
    private Double lumber;
    private Double food;
    private Double iron;
    
    public Integer getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final Integer troopId) {
        this.troopId = troopId;
    }
    
    public String getRequest() {
        return this.request;
    }
    
    public void setRequest(final String request) {
        this.request = request;
    }
    
    public String getInputType() {
        return this.inputType;
    }
    
    public void setInputType(final String inputType) {
        this.inputType = inputType;
    }
    
    public Double getOutputE() {
        return this.outputE;
    }
    
    public void setOutputE(final Double outputE) {
        this.outputE = outputE;
    }
    
    public Double getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Double copper) {
        this.copper = copper;
    }
    
    public Double getLumber() {
        return this.lumber;
    }
    
    public void setLumber(final Double lumber) {
        this.lumber = lumber;
    }
    
    public Double getFood() {
        return this.food;
    }
    
    public void setFood(final Double food) {
        this.food = food;
    }
    
    public Double getIron() {
        return this.iron;
    }
    
    public void setIron(final Double iron) {
        this.iron = iron;
    }
}
