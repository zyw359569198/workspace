package com.zyw.novelGame.model;

public class Catagory {
    private String id;

    private String cataId;

    private String cataName;

    private String cataNameEn;

    private Integer orderDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCataId() {
        return cataId;
    }

    public void setCataId(String cataId) {
        this.cataId = cataId == null ? null : cataId.trim();
    }

    public String getCataName() {
        return cataName;
    }

    public void setCataName(String cataName) {
        this.cataName = cataName == null ? null : cataName.trim();
    }

    public String getCataNameEn() {
        return cataNameEn;
    }

    public void setCataNameEn(String cataNameEn) {
        this.cataNameEn = cataNameEn == null ? null : cataNameEn.trim();
    }
    public Integer getOrderDesc(){
        return orderDesc;
    }

    public void setOrderDesc(Integer orderDesc) {
        this.orderDesc = orderDesc;
    }
}