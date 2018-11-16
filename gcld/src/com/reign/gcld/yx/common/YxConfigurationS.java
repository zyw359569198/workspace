package com.reign.gcld.yx.common;

import com.reign.plugin.yx.common.*;
import org.springframework.stereotype.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;

@Component("configuration")
public class YxConfigurationS extends YxConfiguration
{
    @Override
	public String getLoginKey(final String yx) {
        return Configuration.getProperty(yx, "gcld.login.key");
    }
    
    @Override
	public String getPayKey(final String yx) {
        return Configuration.getProperty(yx, "gcld.pay.key");
    }
    
    @Override
	public String getQueryKey(final String yx) {
        return Configuration.getProperty(yx, "gcld.query.key");
    }
    
    @Override
	public String getGameURL(final String yx) {
        return Configuration.getProperty(yx, "gcld.game.url");
    }
    
    @Override
	public String getPassedIP(final String yx) {
        return Configuration.getProperty(yx, "gcld.yx.ip");
    }
    
    @Override
	public boolean isLimitYxIP(final String yx) {
        final String limit_yx_ip = Configuration.getProperty(yx, "gcld.limit.yx.ip");
        return !StringUtils.isBlank(limit_yx_ip) && -1 == limit_yx_ip.indexOf("0");
    }
    
    @Override
	public String getUnLoginRedirectURL(final String yx) {
        return Configuration.getProperty(yx, "gcld.unlogin.redirect.url");
    }
    
    @Override
	public int getMaxPlayerNum(final String yx) {
        final String obj = Configuration.getProperty(yx, "gcld.player.maxRoleNum");
        if (obj == null) {
            return 0;
        }
        return Integer.parseInt(obj);
    }
    
    @Override
	public String getServerId(final String yx) {
        return Configuration.getProperty(yx, "gcld.serverid");
    }
    
    @Override
	public String getServerIdS(final String yx) {
        return Configuration.getProperty(yx, "gcld.serverids");
    }
    
    @Override
	public String getKaixin001LoginUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.login.url");
    }
    
    @Override
	public String getKaixin001GameUrl(final String yx) {
        return this.getGameURL(yx);
    }
    
    @Override
	public String getKaixin001QueryUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.query.url");
    }
    
    @Override
	public String getKaixin001PayUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.pay.kaixin001.url");
    }
    
    @Override
	public String getKaixin001GameAid(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.game.aid");
    }
    
    @Override
	public String getKaixin001GameVendor(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.game.vendor");
    }
    
    @Override
	public String getKaixin001GameAppName(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.game.appname");
    }
    
    @Override
	public String getKaixin001GameGoods(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.game.goods");
    }
    
    @Override
	public String getKaixin001Secret(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.secret");
    }
    
    @Override
	public String getKaixin001PayHtml(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.pay.html");
    }
    
    @Override
	public String getAbsolutePayUrl(final String yx) {
        return PayUtil.getAbsolutePayUrl(yx, "", 0);
    }
    
    @Override
	public String getRenrenCode(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.code");
    }
    
    @Override
	public String getRenrenSecret(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.secret");
    }
    
    @Override
	public String getRenrenGameName(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.game.name");
    }
    
    @Override
	public String getRenrenLoginUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.login.url");
    }
    
    @Override
	public String getRenrenQueryUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.query.url");
    }
    
    @Override
	public String getRenrenPayUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renrendou.pay.url");
    }
    
    @Override
	public String getRenrenPayServiceUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.pay.service.url");
    }
    
    @Override
	public String getServerName(final String yx) {
        return Configuration.getProperty(yx, "gcld.showservername");
    }
    
    @Override
	public String getRenrenPayCheckUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.pay.check.url");
    }
    
    @Override
	public String getRenrenExpendCode(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.code");
    }
    
    @Override
	public String getRenrenExpendTrace(final String yx) {
        return Configuration.getProperty(yx, "gcld.serverids").toLowerCase();
    }
    
    @Override
	public String getRenrenNotifyUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.notify.url");
    }
    
    @Override
	public String getRenrenPayStartUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.yx.renren.start.pay");
    }
    
    @Override
	public String getRenrenLoginLimit(final String yx) {
        return Configuration.getProperty(yx, "gcld.renren.login.limt");
    }
    
    @Override
	public String getKaixin001PayServiceUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.kaixin001.pay.url");
    }
    
    @Override
	public String getSogouSecret(final String yx) {
        return Configuration.getProperty(yx, "gcld.sogou.secret");
    }
    
    @Override
	public String getXunleiAuthServerHost1(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.authServer.host1");
    }
    
    @Override
	public String getXunleiAuthServerPort1(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.authServer.port1");
    }
    
    @Override
	public String getXunleiAuthServerHost2(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.authServer.host2");
    }
    
    @Override
	public String getXunleiAuthServerPort2(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.authServer.port2");
    }
    
    @Override
	public String getXunleikeyBytes(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.keybytes");
    }
    
    @Override
	public String getXunleiGameId(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.gameid");
    }
    
    @Override
	public String getXunleiVersion(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.version");
    }
    
    @Override
	public String getXunleiCmd(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.cmd");
    }
    
    @Override
	public String getXunleiAesKey(final String yx) {
        return Configuration.getProperty(yx, "gcld.xunlei.aeskey");
    }
    
    @Override
	public String getXunleiLoginUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.game.login.url");
    }
    
    @Override
	public int getOnline(final String yx) {
        return Players.getOnlinePlayersNumber(yx);
    }
    
    @Override
	public String getCmwebgameAPIUrl(final String yx) {
        return Configuration.getProperty(yx, "cmwebgame_api_url");
    }
    
    @Override
	public String getCmwebgameServerUrl(final String yx) {
        return Configuration.getProperty(yx, "cmwebgame_server_url");
    }
    
    @Override
	public String getCmwebgameKey(final String yx) {
        return Configuration.getProperty(yx, "cmwebgame_key");
    }
    
    @Override
	public String get360PushPlayerInfoUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.360.pushPlayerInfo.url");
    }
    
    @Override
	public String get360PrivilegeCheckUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.check.url");
    }
    
    @Override
	public String get360PrivilegePageUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.page.url");
    }
    
    @Override
	public String get360PrivilegeAid(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.aid");
    }
    
    @Override
	public String get360PrivilegeGkey(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.gkey");
    }
    
    @Override
	public String get360PrivilegeType(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.type");
    }
    
    @Override
	public String get360PrivilegePriviKey(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.privikey");
    }
    
    @Override
	public String get360PrivilegeStartTime(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.start.time");
    }
    
    @Override
	public String get360PrivilegeEndTime(final String yx) {
        return Configuration.getProperty(yx, "gcld.privilege.end.time");
    }
    
    @Override
	public String getJDRedirectUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.jd.redirect.url");
    }
    
    @Override
	public String getJDHeFuList(final String yx) {
        return Configuration.getProperty(yx, "gcld.jd.hefu.list");
    }
    
    @Override
	public String getSinaRedirectUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.sina.redirect.url");
    }
    
    @Override
	public String getSinaHeFuList(final String yx) {
        return Configuration.getProperty(yx, "gcld.sina.hefu.list");
    }
    
    @Override
	public String getSinaReceiptVerificationUrl(final String yx) {
        return Configuration.getProperty(yx, "yx.sina.receipt.verification.url");
    }
    
    @Override
	public String getSinaIdentifier(final String yx) {
        return Configuration.getProperty(yx, "yx.sina.identifier");
    }
    
    @Override
	public String getTaobaoCoopId(final String yx) {
        return Configuration.getProperty(yx, "yx.taobao.coopId");
    }
    
    @Override
	public String getTaobaoCardId(final String yx) {
        return Configuration.getProperty(yx, "yx.taobao.cardId");
    }
    
    @Override
	public String getTencentAppId(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.app.id");
    }
    
    @Override
	public String getTencentAppName(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.app.id");
    }
    
    @Override
	public String getTencentAppKey(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.app.key");
    }
    
    @Override
	public String getTencentYunUrl(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.yun.url");
    }
    
    @Override
	public String getTencentUseInfoUri(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.userinfo.uri");
    }
    
    @Override
	public String getTencentBuyGoodsUri(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.pay.buy.goods");
    }
    
    @Override
	public String getTencentConfirmDeliveryUri(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.pay.confirm.delivery");
    }
    
    @Override
	public String getTencentGoodsMeta(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.pay.goodsmeta");
    }
    
    @Override
	public String getTxRedirectUrl(String yx) {
        yx = yx.replace("_m", "");
        return Configuration.getProperty(yx, "gcld.tencent.pay.redirect.url");
    }
    
    @Override
	public String getHostId(final String yx) {
        return Configuration.getProperty(yx, "gcld.host.id");
    }
    
    @Override
	public String getTaobaoRedirectUrl(final String yx) {
        return Configuration.getProperty(yx, "gcld.taobao.redirect.url");
    }
    
    @Override
	public String get5211gameAppId(final String yx) {
        return Configuration.getProperty(yx, "yx.5211game.app.id");
    }
    
    @Override
	public String get5211gameLoginUrl(final String yx) {
        return Configuration.getProperty(yx, "yx.5211game.login.url");
    }
    
    @Override
	public String get5211gameAccessUrl(final String yx) {
        return Configuration.getProperty(yx, "yx.5211game.access.url");
    }
    
    @Override
	public String getUCCpId(final String yx) {
        return null;
    }
    
    @Override
	public String getUCGameId(final String yx) {
        return null;
    }
    
    @Override
	public String getPingAnUrl(final String yx, final String key) {
        return Configuration.getProperty(yx, key);
    }
}
