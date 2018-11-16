package com.reign.gcld.task.reward;

public class Reward
{
    private int type;
    private String name;
    private int id;
    private int num;
    private int lv;
    
    public Reward() {
    }
    
    public Reward(final int type, final String name, final int num) {
        this.name = name;
        this.type = type;
        this.num = num;
    }
    
    public Reward(final int type, final String name, final int num, final int lv) {
        this.name = name;
        this.type = type;
        this.num = num;
        this.lv = lv;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getNum() {
        return this.num;
    }
    
    public void setNum(final int num) {
        this.num = num;
    }
    
    public int getLv() {
        return this.lv;
    }
    
    public void setLv(final int lv) {
        this.lv = lv;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
}
