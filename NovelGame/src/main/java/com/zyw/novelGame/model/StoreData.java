package com.zyw.novelGame.model;

public class StoreData {
    private String id;

    private String storeId;

    private byte[] storeContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId == null ? null : storeId.trim();
    }

    public byte[] getStoreContent() {
        return storeContent;
    }

    public void setStoreContent(byte[] storeContent) {
        this.storeContent = storeContent;
    }
}