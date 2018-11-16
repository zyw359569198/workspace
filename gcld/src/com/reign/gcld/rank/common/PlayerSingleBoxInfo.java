package com.reign.gcld.rank.common;

public class PlayerSingleBoxInfo
{
    private int boxId;
    private int boxNum;
    
    public int getBoxId() {
        return this.boxId;
    }
    
    public void setBoxId(final int boxId) {
        this.boxId = boxId;
    }
    
    public int getBoxNum() {
        return this.boxNum;
    }
    
    public void setBoxNum(final int boxNum) {
        this.boxNum = boxNum;
    }
    
    public PlayerSingleBoxInfo(final String string) {
        final String[] info = string.split(",");
        this.setBoxId(Integer.parseInt(info[0]));
        this.setBoxNum(Integer.parseInt(info[1]));
    }
    
    public PlayerSingleBoxInfo() {
    }
    
    @Override
    public String toString() {
        final StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(this.boxId).append(",").append(this.boxNum);
        return sbBuffer.toString();
    }
}
