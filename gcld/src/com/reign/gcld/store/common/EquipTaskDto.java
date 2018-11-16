package com.reign.gcld.store.common;

import com.reign.gcld.task.request.*;

public class EquipTaskDto
{
    private int type;
    private int quality;
    private int degree;
    private int num;
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public int getDegree() {
        return this.degree;
    }
    
    public void setDegree(final int degree) {
        this.degree = degree;
    }
    
    public int getNum() {
        return this.num;
    }
    
    public void setNum(final int num) {
        this.num = num;
    }
    
    public EquipTaskDto(final TaskRequestEquip equip) {
        this.type = equip.getType();
        this.quality = equip.getQuality();
        this.degree = equip.getDegree();
        this.num = equip.getNum();
    }
    
    public EquipTaskDto() {
    }
    
    public EquipTaskDto(final TaskRequestStoreBuyS request) {
        this.type = request.getRequestType();
        this.quality = request.getRequestQuality();
        this.degree = 0;
        this.num = 1;
    }
    
    public EquipTaskDto(final TaskRequestEquipOn taskRequestEquipOn) {
        this.type = taskRequestEquipOn.getType();
        this.quality = taskRequestEquipOn.getQuality();
        this.degree = taskRequestEquipOn.getDegree();
        this.num = taskRequestEquipOn.getNum();
    }
}
