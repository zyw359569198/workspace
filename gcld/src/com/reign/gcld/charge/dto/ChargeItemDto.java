package com.reign.gcld.charge.dto;

public class ChargeItemDto
{
    private int chargeItemId;
    private String name;
    private boolean show;
    private int cost;
    private boolean dynamic;
    private int alert;
    private int lv;
    
    public void setCost(final int cost) {
        this.cost = cost;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public void setDynamic(final boolean dynamic) {
        this.dynamic = dynamic;
    }
    
    public boolean getDynamic() {
        return this.dynamic;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setShow(final boolean show) {
        this.show = show;
    }
    
    public boolean getShow() {
        return this.show;
    }
    
    public void setChargeItemId(final int chargeItemId) {
        this.chargeItemId = chargeItemId;
    }
    
    public int getChargeItemId() {
        return this.chargeItemId;
    }
    
    public void setAlert(final int alert) {
        this.alert = alert;
    }
    
    public int getAlert() {
        return this.alert;
    }
    
    public int getLv() {
        return this.lv;
    }
    
    public void setLv(final int lv) {
        this.lv = lv;
    }
}
