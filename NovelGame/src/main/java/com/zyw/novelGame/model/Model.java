package com.zyw.novelGame.model;

public class Model {
    private String id;

    private String modelId;

    private String modelName;

    private String modelNameEn;

    private Integer orderDesc;

    private String modelUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId == null ? null : modelId.trim();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }

    public String getModelNameEn() {
        return modelNameEn;
    }

    public void setModelNameEn(String modelNameEn) {
        this.modelNameEn = modelNameEn == null ? null : modelNameEn.trim();
    }

    public Integer getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(Integer orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl == null ? null : modelUrl.trim();
    }
}