package com.reign.plugin.yx.util.kingnet.udplog;

public class MaintainStatHead
{
    public byte httpHead;
    public int nPackageLength;
    public String nUID;
    public char shFlag;
    public char shOptionalLen;
    public char lpbyOptional;
    public short shHeaderLen;
    public char shMessageID;
    public char shMessageType;
    public char shVersionType;
    public char shVersion;
    public long nResourceId;
    public long nTimestamp;
    
    public MaintainStatHead(final long resourceId) {
        this.httpHead = 0;
        this.shFlag = '\0';
        this.shOptionalLen = '\0';
        this.lpbyOptional = '\0';
        this.shHeaderLen = this.getSize();
        this.shMessageID = '\u0010';
        this.shMessageType = '\u0003';
        this.shVersionType = '\u0003';
        this.shVersion = '\u0004';
        this.nResourceId = resourceId;
        this.nTimestamp = System.currentTimeMillis() / 100L;
    }
    
    public short getSize() {
        return 30;
    }
    
    public byte getHttpHead() {
        return this.httpHead;
    }
    
    public void setHttpHead(final byte httpHead) {
        this.httpHead = httpHead;
    }
    
    public int getnPackageLength() {
        return this.nPackageLength;
    }
    
    public void setnPackageLength(final int nPackageLength) {
        this.nPackageLength = nPackageLength;
    }
    
    public char getShFlag() {
        return this.shFlag;
    }
    
    public void setShFlag(final char shFlag) {
        this.shFlag = shFlag;
    }
    
    public char getShOptionalLen() {
        return this.shOptionalLen;
    }
    
    public void setShOptionalLen(final char shOptionalLen) {
        this.shOptionalLen = shOptionalLen;
    }
    
    public char getLpbyOptional() {
        return this.lpbyOptional;
    }
    
    public void setLpbyOptional(final char lpbyOptional) {
        this.lpbyOptional = lpbyOptional;
    }
    
    public char getShMessageID() {
        return this.shMessageID;
    }
    
    public void setShMessageID(final char shMessageID) {
        this.shMessageID = shMessageID;
    }
    
    public char getShMessageType() {
        return this.shMessageType;
    }
    
    public void setShMessageType(final char shMessageType) {
        this.shMessageType = shMessageType;
    }
    
    public char getShVersionType() {
        return this.shVersionType;
    }
    
    public void setShVersionType(final char shVersionType) {
        this.shVersionType = shVersionType;
    }
    
    public char getShVersion() {
        return this.shVersion;
    }
    
    public void setShVersion(final char shVersion) {
        this.shVersion = shVersion;
    }
    
    public String getnUID() {
        return this.nUID;
    }
    
    public void setnUID(final String nUID) {
        this.nUID = nUID;
    }
    
    public long getnResourceId() {
        return this.nResourceId;
    }
    
    public void setnResourceId(final long nResourceId) {
        this.nResourceId = nResourceId;
    }
    
    public long getnTimestamp() {
        return this.nTimestamp;
    }
    
    public void setnTimestamp(final long nTimestamp) {
        this.nTimestamp = nTimestamp;
    }
    
    public short getShHeaderLen() {
        return this.shHeaderLen;
    }
    
    public void setShHeaderLen(final short shHeaderLen) {
        this.shHeaderLen = shHeaderLen;
    }
}
