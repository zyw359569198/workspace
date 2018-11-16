package com.reign.plugin.yx.util.kingnet.udplog;

public class LogUserInfo
{
    private String clientIp;
    private long gameTime;
    private String ouid;
    private String iuid;
    private String birthday;
    private String sex;
    private int gameFriend;
    private int platFriend;
    private int userLevel;
    private int userExp;
    private int moneycoin;
    private int gamecoin;
    private String addi1;
    private String addi2;
    private String addi3;
    private String addi4;
    private String vipLevel;
    private int installTime;
    private String version;
    private String country;
    private String entry;
    private String isTest;
    private String email;
    
    public LogUserInfo() {
        this.clientIp = "";
        this.gameTime = StatConstants.LOG_NULL;
        this.ouid = "";
        this.iuid = "";
        this.birthday = "";
        this.sex = "";
        this.gameFriend = StatConstants.LOG_NULL;
        this.platFriend = StatConstants.LOG_NULL;
        this.userLevel = StatConstants.LOG_NULL;
        this.userExp = StatConstants.LOG_NULL;
        this.moneycoin = StatConstants.LOG_NULL;
        this.gamecoin = StatConstants.LOG_NULL;
        this.addi1 = "";
        this.addi2 = "";
        this.addi3 = "";
        this.addi4 = "";
        this.vipLevel = "";
        this.installTime = StatConstants.LOG_NULL;
        this.version = "";
        this.country = "";
        this.entry = "";
        this.isTest = "";
        this.email = "";
    }
    
    public String getOuid() {
        return this.ouid;
    }
    
    public void setOuid(final String ouid) {
        this.ouid = ouid;
    }
    
    public String getIuid() {
        return this.iuid;
    }
    
    public void setIuid(final String iuid) {
        this.iuid = iuid;
    }
    
    public long getTimestamp() {
        return this.gameTime;
    }
    
    public void setTimestamp(final long timestamp) {
        this.gameTime = timestamp;
    }
    
    @Override
    public String toString() {
        final String separatorOr = "|";
        final StringBuilder sb = new StringBuilder();
        sb.append(this.clientIp);
        sb.append(separatorOr);
        sb.append(this.toLogString(this.gameTime));
        sb.append(separatorOr);
        sb.append(this.ouid);
        sb.append(separatorOr);
        sb.append(this.iuid);
        sb.append(separatorOr);
        sb.append(this.birthday);
        sb.append(separatorOr);
        sb.append(this.sex);
        sb.append(separatorOr);
        sb.append(this.toLogString(this.gameFriend));
        sb.append(separatorOr);
        sb.append(this.toLogString(this.platFriend));
        sb.append(separatorOr);
        sb.append(this.toLogString(this.userLevel));
        sb.append(separatorOr);
        sb.append(this.toLogString(this.userExp));
        sb.append(separatorOr);
        sb.append(this.toLogString(this.moneycoin));
        sb.append(separatorOr);
        sb.append(this.toLogString(this.gamecoin));
        sb.append(separatorOr);
        sb.append(this.addi1);
        sb.append(separatorOr);
        sb.append(this.addi2);
        sb.append(separatorOr);
        sb.append(this.addi3);
        sb.append(separatorOr);
        sb.append(this.addi4);
        sb.append(separatorOr);
        sb.append(this.vipLevel);
        sb.append(separatorOr);
        sb.append(this.toLogString(this.installTime));
        sb.append(separatorOr);
        sb.append(this.version);
        sb.append(separatorOr);
        sb.append(this.country);
        sb.append(separatorOr);
        sb.append(this.entry);
        sb.append(separatorOr);
        sb.append(this.isTest);
        sb.append(separatorOr);
        sb.append(this.email);
        return sb.toString();
    }
    
    public String getClientIp() {
        return this.clientIp;
    }
    
    public void setClientIp(final String clientIp) {
        this.clientIp = clientIp;
    }
    
    public long getGameTime() {
        return this.gameTime;
    }
    
    public void setGameTime(final long gameTime) {
        this.gameTime = gameTime;
    }
    
    public String getBirthday() {
        return this.birthday;
    }
    
    public void setBirthday(final String birthday) {
        this.birthday = birthday;
    }
    
    public String getSex() {
        return this.sex;
    }
    
    public void setSex(final String sex) {
        this.sex = sex;
    }
    
    public int getGameFriend() {
        return this.gameFriend;
    }
    
    public void setGameFriend(final int gameFriend) {
        this.gameFriend = gameFriend;
    }
    
    public int getPlatFriend() {
        return this.platFriend;
    }
    
    public void setPlatFriend(final int platFriend) {
        this.platFriend = platFriend;
    }
    
    public int getUserLevel() {
        return this.userLevel;
    }
    
    public void setUserLevel(final int userLevel) {
        this.userLevel = userLevel;
    }
    
    public int getUserExp() {
        return this.userExp;
    }
    
    public void setUserExp(final int userExp) {
        this.userExp = userExp;
    }
    
    public int getMoneycoin() {
        return this.moneycoin;
    }
    
    public void setMoneycoin(final int moneycoin) {
        this.moneycoin = moneycoin;
    }
    
    public int getGamecoin() {
        return this.gamecoin;
    }
    
    public void setGamecoin(final int gamecoin) {
        this.gamecoin = gamecoin;
    }
    
    public String getAddi1() {
        return this.addi1;
    }
    
    public void setAddi1(final String addi1) {
        this.addi1 = addi1;
    }
    
    public String getAddi2() {
        return this.addi2;
    }
    
    public void setAddi2(final String addi2) {
        this.addi2 = addi2;
    }
    
    public String getAddi3() {
        return this.addi3;
    }
    
    public void setAddi3(final String addi3) {
        this.addi3 = addi3;
    }
    
    public String getAddi4() {
        return this.addi4;
    }
    
    public void setAddi4(final String addi4) {
        this.addi4 = addi4;
    }
    
    public String getVipLevel() {
        return this.vipLevel;
    }
    
    public void setVipLevel(final String vipLevel) {
        this.vipLevel = vipLevel;
    }
    
    public int getInstallTime() {
        return this.installTime;
    }
    
    public void setInstallTime(final int installTime) {
        this.installTime = installTime;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(final String country) {
        this.country = country;
    }
    
    public String getEntry() {
        return this.entry;
    }
    
    public void setEntry(final String entry) {
        this.entry = entry;
    }
    
    public String getIsTest() {
        return this.isTest;
    }
    
    public void setIsTest(final String isTest) {
        this.isTest = isTest;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String toLogString(final int val) {
        if (val == StatConstants.LOG_NULL) {
            return "";
        }
        return String.valueOf(val);
    }
    
    public String toLogString(final long val) {
        if (val == StatConstants.LOG_NULL) {
            return "";
        }
        return String.valueOf(val);
    }
}
