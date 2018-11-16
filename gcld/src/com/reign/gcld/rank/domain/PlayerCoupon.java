package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerCoupon implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vid;
    private Integer playerId;
    private Integer couponType;
    private Integer couponNum;
    
    public Integer getVid() {
        return this.vid;
    }
    
    public void setVid(final Integer vid) {
        this.vid = vid;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCouponType() {
        return this.couponType;
    }
    
    public void setCouponType(final Integer couponType) {
        this.couponType = couponType;
    }
    
    public Integer getCouponNum() {
        return this.couponNum;
    }
    
    public void setCouponNum(final Integer couponNum) {
        this.couponNum = couponNum;
    }
}
