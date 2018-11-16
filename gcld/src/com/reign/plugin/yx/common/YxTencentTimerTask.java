package com.reign.plugin.yx.common;

import org.apache.commons.logging.*;
import com.reign.plugin.yx.*;
import com.reign.plugin.yx.util.json.*;
import com.reign.framework.netty.mvc.result.*;
import java.util.*;

public class YxTencentTimerTask extends TimerTask
{
    private static final Log opReport;
    private IYxOperation yxOperation;
    private String openId;
    private String appId;
    private String payItem;
    private String token;
    private String billNo;
    private String version;
    private String zoneId;
    private String provideType;
    private String amt;
    private String payAmtCoins;
    private String pubacctPayAmtCoins;
    private String openKey;
    private String pf;
    private String retCode;
    private String retMsg;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    public YxTencentTimerTask(final String openId, final String appId, final String payItem, final String token, final String billNo, final String version, final String zoneId, final String provideType, final String amt, final String payAmtCoins, final String pubacctPayAmtCoins, final String openKey, final String pf, final String ret, final String msg, final IYxOperation yxOperation) {
        this.openId = openId;
        this.appId = appId;
        this.payItem = payItem;
        this.token = token;
        this.billNo = billNo;
        this.version = version;
        this.zoneId = zoneId;
        this.provideType = provideType;
        this.amt = amt;
        this.payAmtCoins = payAmtCoins;
        this.pubacctPayAmtCoins = pubacctPayAmtCoins;
        this.openKey = openKey;
        this.pf = pf;
        this.retCode = ret;
        this.retMsg = msg;
        this.yxOperation = yxOperation;
    }
    
    @Override
    public void run() {
        final long start = System.currentTimeMillis();
        try {
            YxTencentTimerTask.opReport.info("#sendConfirmDeliveryMsg starts...#Time:" + System.currentTimeMillis());
            final String yunUrl = PluginContext.configuration.getTencentYunUrl(this.pf);
            final String baseUrl = yunUrl.replace("http", "https");
            final String uri = PluginContext.configuration.getTencentConfirmDeliveryUri(this.pf);
            final String ts = new StringBuilder(String.valueOf(System.currentTimeMillis() / 1000L)).toString();
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("openid", this.openId);
            paramMap.put("openKey", this.openKey);
            paramMap.put("appid", this.appId);
            paramMap.put("pf", this.pf);
            paramMap.put("ts", ts);
            paramMap.put("payitem", this.payItem);
            paramMap.put("token_id", this.token);
            paramMap.put("billno", this.billNo);
            paramMap.put("zoneid", this.zoneId);
            paramMap.put("provide_errno", this.retCode);
            paramMap.put("amt", this.amt);
            if (this.payAmtCoins != null) {
                paramMap.put("payamt_coins", this.payAmtCoins);
            }
            if (this.retMsg != null) {
                paramMap.put("provide_errmsg", this.retMsg);
            }
            if (this.version != null) {
                paramMap.put("version", this.version);
            }
            if (this.provideType != null) {
                paramMap.put("providetype", this.provideType);
            }
            if (this.pubacctPayAmtCoins != null) {
                paramMap.put("pubacct_pay_amt_coins", this.pubacctPayAmtCoins);
            }
            final String echo = this.yxOperation.exeTencentQuery(this.pf, paramMap, baseUrl, uri, "GET", true, false);
            YxTencentTimerTask.opReport.error("delivery echo:" + echo);
            final JSONObject jo = new JSONObject(echo);
            final int ret = (int)jo.get("ret");
            if (ret == 0) {
                YxTencentTimerTask.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay_confirmDelivery", "yxTencentPay_confirmDelivery_SUCCESS!", null, null, null, true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
            }
            else {
                YxTencentTimerTask.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay_confirmDelivery", "yxTencentPay_confirmDelivery_FAILED!", null, null, null, true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(ret)), ret));
            }
        }
        catch (Exception e) {
            YxTencentTimerTask.opReport.info(OpLogUtil.formatOpInterfaceLog("yxTencentPay_confirmDelivery", "yxTencentPay_confirmDelivery_FAILED_EXCEPTION:" + e, null, null, null, true, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(11)), 11));
        }
        this.yxOperation.removeTencentPayMap(this.token);
    }
}
