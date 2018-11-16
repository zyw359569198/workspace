package com.reign.plugin.yx.util.kingnet.udplog;

import java.io.*;

public class MaintainStatBody
{
    private String uid;
    private short tableType;
    private String custPram1;
    private String custPram2;
    private String custPram3;
    private String custPram4;
    private String custPram5;
    private String custPram6;
    private String count;
    private LogUserInfo userInfo;
    
    public MaintainStatBody(final LogUserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getMsgContent() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.custPram1);
        final String separatorOr = "|";
        sb.append(separatorOr);
        sb.append(this.custPram2);
        sb.append(separatorOr);
        sb.append(this.custPram3);
        sb.append(separatorOr);
        sb.append(this.custPram4);
        sb.append(separatorOr);
        sb.append(this.custPram5);
        sb.append(separatorOr);
        sb.append(this.custPram6);
        sb.append(separatorOr);
        sb.append(this.count);
        sb.append(separatorOr);
        sb.append(this.userInfo.toString());
        final String msgContent = sb.toString();
        return msgContent;
    }
    
    public String getUid() {
        return this.uid;
    }
    
    public void setUid(final String uid) {
        this.uid = uid;
    }
    
    public int getSize() throws UnsupportedEncodingException {
        final int bodySize = this.getMsgContent().getBytes("UTF-8").length + 9;
        return bodySize;
    }
    
    public short getTableType() {
        return this.tableType;
    }
    
    public void setTableType(final short tableType) {
        this.tableType = tableType;
    }
    
    public LogUserInfo getUserInfo() {
        return this.userInfo;
    }
    
    public void setUserInfo(final LogUserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo.getOuid() != null && userInfo.getOuid() != "") {
            this.setUid(userInfo.getOuid());
        }
    }
    
    public String getCustPram1() {
        return this.custPram1;
    }
    
    public void setCustPram1(final String custPram1) {
        this.custPram1 = custPram1;
    }
    
    public String getCustPram2() {
        return this.custPram2;
    }
    
    public void setCustPram2(final String custPram2) {
        this.custPram2 = custPram2;
    }
    
    public String getCustPram3() {
        return this.custPram3;
    }
    
    public void setCustPram3(final String custPram3) {
        this.custPram3 = custPram3;
    }
    
    public String getCustPram4() {
        return this.custPram4;
    }
    
    public void setCustPram4(final String custPram4) {
        this.custPram4 = custPram4;
    }
    
    public String getCustPram5() {
        return this.custPram5;
    }
    
    public void setCustPram5(final String custPram5) {
        this.custPram5 = custPram5;
    }
    
    public String getCustPram6() {
        return this.custPram6;
    }
    
    public void setCustPram6(final String custPram6) {
        this.custPram6 = custPram6;
    }
    
    public String getCount() {
        return this.count;
    }
    
    public void setCount(final String count) {
        this.count = count;
    }
}
