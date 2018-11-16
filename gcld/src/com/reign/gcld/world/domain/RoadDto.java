package com.reign.gcld.world.domain;

public class RoadDto
{
    private String key;
    private int id;
    private double x;
    private double y;
    private int wei;
    private int shu;
    private int wu;
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public int getWei() {
        return this.wei;
    }
    
    public void setWei(final int wei) {
        this.wei = wei;
    }
    
    public int getShu() {
        return this.shu;
    }
    
    public void setShu(final int shu) {
        this.shu = shu;
    }
    
    public int getWu() {
        return this.wu;
    }
    
    public void setWu(final int wu) {
        this.wu = wu;
    }
    
    public RoadDto(final String key, final int id, final double x, final double y, final int wei, final int shu, final int wu) {
        this.key = key;
        this.id = id;
        this.x = x;
        this.y = y;
        this.wei = wei;
        this.shu = shu;
        this.wu = wu;
    }
    
    public RoadDto() {
    }
}
