package com.reign.gcld.yx.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.sdata.cache.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.logging.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.plugin.yx.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.reign.plugin.yx.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.plugin.yx.util.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class YxTencentOperationAction2 extends BaseAction
{
    private static final long serialVersionUID = 4749768944541356724L;
    @Autowired
    private GiftTxCache giftTxCache;
    @Autowired
    IYxOperation yxOperation;
    private static final Log opReport;
    private static final Logger errorLog;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        errorLog = CommonLog.getLog(YxTencentOperationAction2.class);
    }
    
    @Command("yxTencent@buyGoods")
    public ByteResult prePay(@RequestParam("goodId") final int goodId, final Request request) throws JSONException {
        try {
            final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
            if (playerDto == null) {
                return null;
            }
            final UserDto userDto = Users.getUserDto(playerDto.userId, playerDto.yx);
            if (!this.yxOperation.checkTencentPf(playerDto.yx)) {
                YxTencentOperationAction2.opReport.error("#className:YxTencentOperationAction2#methodName:prePay#reason:\u975e\u6cd5\u7684\u5e73\u53f0\u6765\u6e90");
                YxTencentOperationAction2.errorLog.error("#className:YxTencentOperationAction2#methodName:prePay#reason:\u975e\u6cd5\u7684\u5e73\u53f0\u6765\u6e90");
                return this.getResult(JsonBuilder.getJson(State.FAIL, "\u975e\u6cd5\u7684\u5e73\u53f0\u6765\u6e90"), request);
            }
            final GiftTx giftTx = this.giftTxCache.getGiftByTypeAndLv(5, goodId);
            if (giftTx == null) {
                return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_INVALID_GOODID), request);
            }
            final String yunUrl = PluginContext.configuration.getTencentYunUrl(playerDto.yx);
            final String buyGoodsUri = PluginContext.configuration.getTencentBuyGoodsUri(playerDto.yx);
            final String baseUrl = yunUrl.replace("http", "https");
            String goodsMeta = giftTx.getGold() + "\u5143\u5b9d*\u6d88\u8d39\u5143\u5b9d";
            YxTencentOperationAction2.opReport.error("#goodMeta:" + goodsMeta);
            try {
                goodsMeta = URLEncoder.encode(goodsMeta, "UTF-8").replaceAll("\n", "").replaceAll("\r", "");
            }
            catch (UnsupportedEncodingException e) {
                YxTencentOperationAction2.opReport.error("#className:YxTencentOperationAction2#methodName:prePay#reason:\u5145\u503c\u5f02\u5e38#e:" + e);
                return this.getResult(JsonBuilder.getJson(State.FAIL, "\u5145\u503c\u5f02\u5e38" + e), request);
            }
            YxTencentOperationAction2.opReport.error("#goodMeta:" + goodsMeta);
            final String ts = String.valueOf(System.currentTimeMillis() / 1000L);
            final int price = giftTx.getGold() / 9;
            final int num = 1;
            final StringBuilder sb1 = new StringBuilder();
            final String serverId = PluginContext.configuration.getHostId(playerDto.yx);
            final String kingNetPayItem = sb1.append(serverId).append("_").append(userDto.getYellowVipLevel()).append("_").append(playerDto.yx).append("_").append(playerDto.playerLv).append("_").append(playerDto.playerId).append("_").toString();
            final StringBuilder sb2 = new StringBuilder();
            final String txPayItem = sb2.append(goodId).append("*").append(price).append("*").append(num).toString();
            final String payItem = String.valueOf(kingNetPayItem) + txPayItem;
            YxTencentOperationAction2.opReport.error("#payItem:" + payItem);
            final String amt = String.valueOf(price);
            final String goodsPicUrl = giftTx.getPicUrl();
            final String zoneId = "0";
            final String appMode = "2";
            final String openId = userDto.getOpenId();
            final String openKey = userDto.getOpenKey();
            final String appId = PluginContext.configuration.getTencentAppId(playerDto.yx);
            final String pf = userDto.getPf();
            final String pfKey = userDto.getPfKey();
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("openid", openId);
            paramMap.put("openkey", openKey);
            paramMap.put("appid", appId);
            paramMap.put("pf", pf);
            paramMap.put("amt", amt);
            paramMap.put("appmode", appMode);
            paramMap.put("goodsmeta", goodsMeta);
            paramMap.put("goodsurl", goodsPicUrl);
            paramMap.put("payitem", payItem);
            paramMap.put("pfkey", pfKey);
            paramMap.put("ts", ts);
            paramMap.put("zoneid", zoneId);
            final String echo = this.yxOperation.exeTencentQuery(pf, paramMap, baseUrl, buyGoodsUri, "GET", true, false);
            final JSONObject jo = new JSONObject(echo);
            final int ret = (int)jo.get("ret");
            if (ret == 0) {
                final String token = (String)jo.get("token");
                final String url = (String)jo.get("url_params");
                Map<String, YxTencentPayInfo> tPayMap = this.yxOperation.getTencentPayMap();
                if (tPayMap == null) {
                    tPayMap = new ConcurrentHashMap<String, YxTencentPayInfo>();
                }
                final YxTencentPayInfo ytpi = new YxTencentPayInfo();
                ytpi.setGoodId(String.valueOf(goodId));
                ytpi.setPlayerId(playerDto.playerId);
                ytpi.setTs(ts);
                ytpi.setToken(token);
                ytpi.setUserId(playerDto.userId);
                ytpi.setPf(pf);
                ytpi.setOpneKey(openKey);
                tPayMap.put(token, ytpi);
                this.yxOperation.setTencentPayMap(tPayMap);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("url", url);
                doc.endObject();
                return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
            }
            final String errorMsg = jo.getString("msg");
            YxTencentOperationAction2.opReport.error("#className:YxTencentOperationAction2#methodName:prePay#reason:" + errorMsg);
            return this.getResult(JsonBuilder.getJson(State.FAIL, errorMsg), request);
        }
        catch (Exception e2) {
            YxTencentOperationAction2.opReport.error("#className:YxTencentOperationAction2#methodName:prePay#Exception:" + e2);
            return this.getResult(JsonBuilder.getJson(State.FAIL, "\u5145\u503c\u5f02\u5e38"), request);
        }
    }
}
