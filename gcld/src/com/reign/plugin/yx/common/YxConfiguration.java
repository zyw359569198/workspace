package com.reign.plugin.yx.common;

public abstract class YxConfiguration
{
    public abstract String getLoginKey(final String p0);
    
    public abstract String getPayKey(final String p0);
    
    public abstract String getQueryKey(final String p0);
    
    public abstract String getGameURL(final String p0);
    
    public abstract String getPassedIP(final String p0);
    
    public abstract boolean isLimitYxIP(final String p0);
    
    public abstract String getUnLoginRedirectURL(final String p0);
    
    public abstract int getMaxPlayerNum(final String p0);
    
    public abstract String getServerId(final String p0);
    
    public abstract String getServerIdS(final String p0);
    
    public abstract String getServerName(final String p0);
    
    public boolean isSingleRole(final String yx) {
        return this.getMaxPlayerNum(yx) <= 1;
    }
    
    public abstract int getOnline(final String p0);
    
    public abstract String getKaixin001LoginUrl(final String p0);
    
    public abstract String getKaixin001GameUrl(final String p0);
    
    public abstract String getKaixin001QueryUrl(final String p0);
    
    public abstract String getKaixin001PayServiceUrl(final String p0);
    
    public abstract String getKaixin001PayUrl(final String p0);
    
    public abstract String getKaixin001GameAid(final String p0);
    
    public abstract String getKaixin001GameVendor(final String p0);
    
    public abstract String getKaixin001GameAppName(final String p0);
    
    public abstract String getKaixin001GameGoods(final String p0);
    
    public abstract String getKaixin001Secret(final String p0);
    
    public abstract String getKaixin001PayHtml(final String p0);
    
    public abstract String getAbsolutePayUrl(final String p0);
    
    public abstract String getRenrenCode(final String p0);
    
    public abstract String getRenrenSecret(final String p0);
    
    public abstract String getRenrenGameName(final String p0);
    
    public abstract String getRenrenLoginUrl(final String p0);
    
    public abstract String getRenrenQueryUrl(final String p0);
    
    public abstract String getRenrenPayCheckUrl(final String p0);
    
    public abstract String getRenrenPayServiceUrl(final String p0);
    
    public abstract String getRenrenPayUrl(final String p0);
    
    public abstract String getRenrenExpendCode(final String p0);
    
    public abstract String getRenrenExpendTrace(final String p0);
    
    public abstract String getRenrenNotifyUrl(final String p0);
    
    public abstract String getRenrenPayStartUrl(final String p0);
    
    public abstract String getRenrenLoginLimit(final String p0);
    
    public abstract String getSogouSecret(final String p0);
    
    public abstract String getXunleiAuthServerHost1(final String p0);
    
    public abstract String getXunleiAuthServerPort1(final String p0);
    
    public abstract String getXunleiAuthServerHost2(final String p0);
    
    public abstract String getXunleiAuthServerPort2(final String p0);
    
    public abstract String getXunleikeyBytes(final String p0);
    
    public abstract String getXunleiGameId(final String p0);
    
    public abstract String getXunleiVersion(final String p0);
    
    public abstract String getXunleiCmd(final String p0);
    
    public abstract String getXunleiAesKey(final String p0);
    
    public abstract String getXunleiLoginUrl(final String p0);
    
    public abstract String getCmwebgameAPIUrl(final String p0);
    
    public abstract String getCmwebgameServerUrl(final String p0);
    
    public abstract String getCmwebgameKey(final String p0);
    
    public abstract String get360PushPlayerInfoUrl(final String p0);
    
    public String get360PrivilegeCheckUrl(final String yx) {
        return null;
    }
    
    public String get360PrivilegePageUrl(final String yx) {
        return null;
    }
    
    public String get360PrivilegeAid(final String yx) {
        return null;
    }
    
    public String get360PrivilegeGkey(final String yx) {
        return null;
    }
    
    public String get360PrivilegeType(final String yx) {
        return null;
    }
    
    public String get360PrivilegePriviKey(final String yx) {
        return null;
    }
    
    public abstract String getJDRedirectUrl(final String p0);
    
    public abstract String getJDHeFuList(final String p0);
    
    public abstract String getSinaRedirectUrl(final String p0);
    
    public abstract String getSinaHeFuList(final String p0);
    
    public abstract String getSinaReceiptVerificationUrl(final String p0);
    
    public abstract String getSinaIdentifier(final String p0);
    
    public abstract String getTaobaoCoopId(final String p0);
    
    public abstract String getTaobaoCardId(final String p0);
    
    public String getTencentAppId(final String yx) {
        return null;
    }
    
    public String getTencentAppName(final String yx) {
        return null;
    }
    
    public String getTencentAppKey(final String yx) {
        return null;
    }
    
    public String getTencentUseInfoUri(final String yx) {
        return null;
    }
    
    public String getTencentYunUrl(final String yx) {
        return null;
    }
    
    public String getTencentBuyGoodsUri(final String yx) {
        return null;
    }
    
    public String getTencentConfirmDeliveryUri(final String yx) {
        return null;
    }
    
    public String getTencentGoodsMeta(final String yx) {
        return null;
    }
    
    public String getTxRedirectUrl(final String yx) {
        return null;
    }
    
    public String getTaobaoRedirectUrl(final String yx) {
        return null;
    }
    
    public String getHostId(final String yx) {
        return null;
    }
    
    public abstract String get5211gameAppId(final String p0);
    
    public abstract String get5211gameLoginUrl(final String p0);
    
    public abstract String get5211gameAccessUrl(final String p0);
    
    public String getPingAnUrl(final String yx, final String key) {
        return null;
    }
    
    public abstract String getUCCpId(final String p0);
    
    public abstract String getUCGameId(final String p0);
    
    public String get360PrivilegeStartTime(final String yx) {
        return null;
    }
    
    public String get360PrivilegeEndTime(final String yx) {
        return null;
    }
}
