package com.reign.plugin.yx.util.kingnet.udplog;

import java.nio.*;

public class MaintainStat
{
    private MaintainStatHead head;
    private MaintainStatBody body;
    private Boolean isDebug;
    
    public MaintainStat(final long resourceId, final LogUserInfo userInfo, final Boolean Debug) {
        final MaintainStatHead head = new MaintainStatHead(resourceId);
        this.isDebug = Debug;
        final MaintainStatBody body = new MaintainStatBody(userInfo);
        this.setHead(head);
        this.setBody(body);
        if (userInfo.getOuid() != null && userInfo.getOuid() != "") {
            head.setnUID(userInfo.getOuid());
            body.setUid(userInfo.getOuid());
        }
    }
    
    public MaintainStat(final long resouceId, final String ouid, final String iuid, final Boolean isDebug) {
    }
    
    public MaintainStatHead getHead() {
        return this.head;
    }
    
    public void setHead(final MaintainStatHead head) {
        this.head = head;
    }
    
    public MaintainStatBody getBody() {
        return this.body;
    }
    
    public void setBody(final MaintainStatBody body) {
        this.body = body;
    }
    
    public void setLoginLog(final String mainRef, final String subRef, final int onlineTime, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.LOGIN_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.LOGIN.getValue());
        }
        this.body.setCustPram1(mainRef);
        this.body.setCustPram2(subRef);
        this.body.setCustPram3("");
        this.body.setCustPram4("");
        this.body.setCustPram5(String.valueOf(onlineTime));
        this.body.setCustPram6("login");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setPropsLog(final StatConstants.RMB_ITEM_OPERATIONS operateType, final StatConstants.RMB_ITEM_CATEGORY category, final String systemItemId, final float unitPrice, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.PROPS_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.PROPS.getValue());
        }
        this.body.setCustPram1(operateType.toString());
        this.body.setCustPram2(String.valueOf(category.getValue()));
        this.body.setCustPram3(systemItemId);
        this.body.setCustPram4(String.valueOf(unitPrice));
        this.body.setCustPram5("");
        this.body.setCustPram6("");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setPayLog(final String unit, final double amount, final int coinNum, final String orderId, final String packageId, final StatConstants.PAY_TYPE payment, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.PAY_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.PAY.getValue());
        }
        this.body.setCustPram1(String.valueOf(unit) + "#" + orderId);
        this.body.setCustPram2(String.valueOf(amount));
        this.body.setCustPram3(String.valueOf(coinNum));
        this.body.setCustPram4(packageId);
        this.body.setCustPram5(payment.toString());
        this.body.setCustPram6("pay");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setActLog(final String catgory, final String sub_category, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.ACT_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.ACT.getValue());
        }
        this.body.setCustPram1(catgory);
        this.body.setCustPram2(sub_category);
        this.body.setCustPram3("");
        this.body.setCustPram4("");
        this.body.setCustPram5("");
        this.body.setCustPram6("");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setGuideLog(final String catgory, final String step, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.GUIDE_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.GUIDE.getValue());
        }
        this.body.setCustPram1(catgory);
        this.body.setCustPram2(step);
        this.body.setCustPram3("");
        this.body.setCustPram4("");
        this.body.setCustPram5("");
        this.body.setCustPram6("");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setReferLog(final String mainRef, final String subRef, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.REFER_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.REFER.getValue());
        }
        this.body.setCustPram1(mainRef);
        this.body.setCustPram2(subRef);
        this.body.setCustPram3("");
        this.body.setCustPram4("");
        this.body.setCustPram5("");
        this.body.setCustPram6("");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setCurrencyLog(final String type, final int num, final String orderId, final StatConstants.CURRENCY_OPERATIONS operationType, final int count) {
        if (this.isDebug) {
            this.body.setTableType(StatConstants.LOG_TYPE.CURRENCY_TEST.getValue());
        }
        else {
            this.body.setTableType(StatConstants.LOG_TYPE.CURRENCY.getValue());
        }
        this.body.setCustPram1(type);
        this.body.setCustPram2(String.valueOf(num));
        this.body.setCustPram3(orderId);
        this.body.setCustPram4(operationType.toString());
        this.body.setCustPram5("");
        this.body.setCustPram6("");
        this.body.setCount(String.valueOf(count));
    }
    
    public void setCustomLog(final StatConstants.LOG_TYPE typeId, final String str1, final String str2, final String str3, final String str4, final String str5, final String str6, final int count) {
        this.body.setTableType(typeId.getValue());
        this.body.setCustPram1(str1);
        this.body.setCustPram2(str2);
        this.body.setCustPram3(str3);
        this.body.setCustPram4(str4);
        this.body.setCustPram5(str5);
        this.body.setCustPram6(str6);
        this.body.setCount(String.valueOf(count));
    }
    
    public void setCustomLog(final StatConstants.LOG_TYPE typeId, final String str1, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7) {
        this.body.setTableType(typeId.getValue());
        this.body.setCustPram1(str1);
        this.body.setCustPram2(str2);
        this.body.setCustPram3(str3);
        this.body.setCustPram4(str4);
        this.body.setCustPram5(str5);
        this.body.setCustPram6(str6);
        this.body.setCount(str7);
    }
    
    public void setCustomLog(final short typeId, final String str1, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7) {
        this.body.setTableType(typeId);
        this.body.setCustPram1(str1);
        this.body.setCustPram2(str2);
        this.body.setCustPram3(str3);
        this.body.setCustPram4(str4);
        this.body.setCustPram5(str5);
        this.body.setCustPram6(str6);
        this.body.setCount(str7);
    }
    
    public ByteBuffer toByteBuffer() {
        UdpByteBuffer data = null;
        try {
            data = UdpMaintainEncoder.encode(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data.toByteBuffer();
    }
    
    public void setUserInfo(final LogUserInfo userInfo) {
        if (userInfo.getOuid() != null && userInfo.getOuid() != "") {
            this.head.setnUID(userInfo.getOuid());
        }
        this.body.setUserInfo(userInfo);
    }
    
    public static String toLogString(final long val) {
        if (val == StatConstants.LOG_NULL) {
            return "";
        }
        return String.valueOf(val);
    }
}
