package com.reign.plugin.yx.domain;

public class XunleiLoginResult
{
    private String code;
    private String customerId;
    private byte adultFlag;
    private byte bindPasswordCardFlag;
    private String vipLevel;
    private String vipEndDate;
    private String fcmTime;
    private String xlVIP;
    private String xlLevel;
    private byte sexFlag;
    
    public XunleiLoginResult(final String response) {
        this.vipLevel = "0";
        this.vipEndDate = "";
        this.fcmTime = "";
        this.xlVIP = "";
        this.xlLevel = "";
        this.sexFlag = 0;
        this.code = response.substring(0, 4);
        if (this.code != null && this.code.equals("0000")) {
            this.customerId = response.substring(6, 26).trim();
            this.adultFlag = response.getBytes()[26];
            this.bindPasswordCardFlag = response.getBytes()[27];
            this.vipLevel = response.substring(28, 30).trim();
            this.vipEndDate = response.substring(30, 40).trim();
            if (response.length() >= 45) {
                this.fcmTime = response.substring(40, 45).trim();
            }
            if (response.length() >= 47) {
                this.xlVIP = response.substring(45, 47).trim();
            }
            if (response.length() >= 49) {
                this.xlLevel = response.substring(47, 49).trim();
            }
            if (response.length() >= 50) {
                this.sexFlag = response.getBytes()[49];
            }
        }
    }
    
    @Override
    public String toString() {
        return "code:" + this.code + ",customerId:" + this.customerId + ",adultFlag:" + this.adultFlag + ",bindPasswordCardFlag:" + this.bindPasswordCardFlag + ",vipLevel:" + this.vipLevel + ",vipEndDate:" + this.vipEndDate + ",fcmTime:" + this.fcmTime + ",xlVIP:" + this.xlVIP + ",xlLevel:" + this.xlLevel + ",sexFlag:" + this.sexFlag;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
    
    public String getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }
    
    public byte getAdultFlag() {
        return this.adultFlag;
    }
    
    public void setAdultFlag(final byte adultFlag) {
        this.adultFlag = adultFlag;
    }
    
    public byte getBindPasswordCardFlag() {
        return this.bindPasswordCardFlag;
    }
    
    public void setBindPasswordCardFlag(final byte bindPasswordCardFlag) {
        this.bindPasswordCardFlag = bindPasswordCardFlag;
    }
    
    public String getVipLevel() {
        return this.vipLevel;
    }
    
    public void setVipLevel(final String vipLevel) {
        this.vipLevel = vipLevel;
    }
    
    public String getVipEndDate() {
        return this.vipEndDate;
    }
    
    public void setVipEndDate(final String vipEndDate) {
        this.vipEndDate = vipEndDate;
    }
    
    public String getFcmTime() {
        return this.fcmTime;
    }
    
    public void setFcmTime(final String fcmTime) {
        this.fcmTime = fcmTime;
    }
    
    public String getXlVIP() {
        return this.xlVIP;
    }
    
    public void setXlVIP(final String xlVIP) {
        this.xlVIP = xlVIP;
    }
    
    public String getXlLevel() {
        return this.xlLevel;
    }
    
    public void setXlLevel(final String xlLevel) {
        this.xlLevel = xlLevel;
    }
    
    public byte getSexFlag() {
        return this.sexFlag;
    }
    
    public void setSexFlag(final byte sexFlag) {
        this.sexFlag = sexFlag;
    }
}
