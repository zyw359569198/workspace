package com.reign.gcld.user.dto;

import com.reign.gcld.antiaddiction.*;
import com.reign.gcld.antiaddiction.util.*;
import java.util.*;

public class UserDto
{
    private String id;
    public String userId;
    public String yx;
    public String userName;
    public long loginTime;
    public int activate;
    private boolean needAntiAddiction;
    private long onlineTime;
    private long enterAntiAddictionStateTime;
    private AntiAddictionStateMachine antiAddictionStateMachine;
    private String yxSource;
    public boolean firstLogin;
    public boolean success;
    private String openId;
    private String openKey;
    private String pf;
    private String pfKey;
    private String userIp;
    private int isYellowVip;
    private int isYellowYearVip;
    private int isYellowHighVip;
    private int yellowVipLevel;
    
    public UserDto() {
        this.needAntiAddiction = false;
        this.onlineTime = 0L;
        this.enterAntiAddictionStateTime = 0L;
        this.firstLogin = true;
        this.success = false;
        this.antiAddictionStateMachine = new AntiAddictionStateMachine(this);
    }
    
    public AntiAddictionStateMachine getAntiAddictionStateMachine() {
        return this.antiAddictionStateMachine;
    }
    
    public long getEnterAntiAddictionStateTime() {
        return this.enterAntiAddictionStateTime;
    }
    
    public void setEnterAntiAddictionStateTime(final long enterAntiAddictionStateTime) {
        this.enterAntiAddictionStateTime = enterAntiAddictionStateTime;
    }
    
    public String getId() {
        if (this.id == null) {
            synchronized (this) {
                if (this.id == null) {
                    this.id = new StringBuilder(30).append(this.userId).append("-").append(this.yx).toString();
                }
            }
        }
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public long getLoginTime() {
        return this.loginTime;
    }
    
    public void setLoginTime(final long loginTime) {
        this.loginTime = loginTime;
    }
    
    public boolean isNeedAntiAddiction() {
        return this.needAntiAddiction;
    }
    
    public void setNeedAntiAddiction(final boolean needAntiAddiction) {
        this.needAntiAddiction = needAntiAddiction;
    }
    
    public long getOnlineTime() {
        final Date refreshPoint = AntiAddictionUtil.getOnlineTimeRefreshPoint();
        if (this.loginTime < refreshPoint.getTime()) {
            this.onlineTime = 0L;
            this.loginTime = refreshPoint.getTime();
        }
        return this.onlineTime + System.currentTimeMillis() - this.loginTime;
    }
    
    public void setOnlineTime(long onlineTime) {
        if (onlineTime < 0L) {
            onlineTime = 0L;
        }
        this.onlineTime = onlineTime;
    }
    
    public void setYxSource(final String yxSource) {
        this.yxSource = yxSource;
    }
    
    public String getYxSource() {
        return this.yxSource;
    }
    
    public void setOpenId(final String openId) {
        this.openId = openId;
    }
    
    public String getOpenId() {
        return this.openId;
    }
    
    public void setOpenKey(final String openKey) {
        this.openKey = openKey;
    }
    
    public String getOpenKey() {
        return this.openKey;
    }
    
    public void setPf(final String pf) {
        this.pf = pf;
    }
    
    public String getPf() {
        return this.pf;
    }
    
    public void setPfKey(final String pfKey) {
        this.pfKey = pfKey;
    }
    
    public String getPfKey() {
        return this.pfKey;
    }
    
    public void setUserIp(final String userIp) {
        this.userIp = userIp;
    }
    
    public String getUserIp() {
        return this.userIp;
    }
    
    public void setIsYellowVip(final int isYellowVip) {
        this.isYellowVip = isYellowVip;
    }
    
    public int getIsYellowVip() {
        return this.isYellowVip;
    }
    
    public void setIsYellowYearVip(final int isYellowYearVip) {
        this.isYellowYearVip = isYellowYearVip;
    }
    
    public int getIsYellowYearVip() {
        return this.isYellowYearVip;
    }
    
    public void setIsYellowHighVip(final int isYellowHighVip) {
        this.isYellowHighVip = isYellowHighVip;
    }
    
    public int getIsYellowHighVip() {
        return this.isYellowHighVip;
    }
    
    public void setYellowVipLevel(final int yellowVipLevel) {
        this.yellowVipLevel = yellowVipLevel;
    }
    
    public int getYellowVipLevel() {
        return this.yellowVipLevel;
    }
}
