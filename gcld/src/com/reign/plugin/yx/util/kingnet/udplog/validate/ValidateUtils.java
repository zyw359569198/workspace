package com.reign.plugin.yx.util.kingnet.udplog.validate;

import com.reign.plugin.yx.util.kingnet.udplog.exception.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;
import java.util.regex.*;

public class ValidateUtils
{
    public static String LINE_SEP;
    
    static {
        ValidateUtils.LINE_SEP = System.getProperty("line.separator");
    }
    
    public static boolean checkLog(final MaintainStat stat, final StatConstants.LOG_TYPE type) throws ValidateException {
        System.out.println("---------------" + type + "\u65e5\u5fd7\u68c0\u67e5------------------------------");
        if (stat == null || stat.getBody() == null) {
            throw new ValidateException("\u6d88\u606f\u4f53\u4e3a\u7a7a\uff0c\u68c0\u67e5\u4f60\u7684\u65e5\u5fd7\u683c\u5f0f\u548c\u65e5\u5fd7\u7c7b\u578b\u662f\u5426\u5339\u914d!");
        }
        System.out.println(stat.getBody().getMsgContent());
        String errorMsg = "";
        errorMsg = String.valueOf(errorMsg) + checkBaseColmns(stat);
        switch (type) {
            case LOGIN:
            case LOGIN_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkLoginLog(stat);
                break;
            }
            case PAY:
            case PAY_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkPayLog(stat);
                break;
            }
            case PROPS:
            case PROPS_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkPropsLog(stat);
                break;
            }
            case ACT:
            case ACT_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkActLog(stat);
                break;
            }
            case GUIDE:
            case GUIDE_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkGuideLog(stat);
                break;
            }
            case REFER:
            case REFER_TEST: {
                errorMsg = String.valueOf(errorMsg) + checkReferLog(stat);
                break;
            }
            default: {
                errorMsg = "\u6682\u4e0d\u652f\u6301\u8be5\u65e5\u5fd7\u7c7b\u578b\u68c0\u67e5!";
                break;
            }
        }
        if (StringUtils.isNotBlank(errorMsg)) {
            System.out.println(errorMsg);
            return false;
        }
        return true;
    }
    
    private static String checkLoginLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        if (StringUtils.isBlank(msgBody.getCustPram1())) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u6709\u6765\u6e90\u65f6\u4e00\u5b9a\u8981\u52a0\u4e0a!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u6709\u6765\u6e90\u65f6\u4e00\u5b9a\u8981\u52a0\u4e0a!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkPayLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        final String custom1 = msgBody.getCustPram1();
        if (StringUtils.isBlank(custom1) || custom1.indexOf("#") == -1) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u683c\u5f0f\u4e3a \u8d27\u5e01\u5355\u4f4d#\u8ba2\u5355ID !").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u5145\u503c\u91d1\u989d\u4e3a\u7a7a !").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram3())) {
            errorMsg.append("\u8b66\u544a:CustPram3 \u652f\u4ed8\u5957\u9910\u53f7\u4e3a\u7a7a !").append(ValidateUtils.LINE_SEP);
        }
        final String custom2 = msgBody.getCustPram4();
        if (StringUtils.isBlank(custom2) || !"pay".equals(custom2) || "props".equals(custom2)) {
            errorMsg.append("\u8b66\u544a:CustPram4 \u5145\u503c\u65b9\u5f0f\u5b57\u6bb5\u5e94\u4e3apay\u6216props!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkPropsLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        final String custom1 = msgBody.getCustPram1();
        if (StringUtils.isBlank(custom1) || !"add".equals(custom1) || "sub".equals(custom1)) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u589e\u52a0\u9053\u5177\uff1aadd;\u4f7f\u7528\u9053\uff1asub!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u9053\u5177\u7c7b\u522b\u4e3a\u7a7a !").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram3())) {
            errorMsg.append("\u8b66\u544a:CustPram3 \u9053\u5177\u60df\u4e00id\u4e3a\u7a7a !").append(ValidateUtils.LINE_SEP);
        }
        final String custom2 = msgBody.getCustPram4();
        if (StringUtils.isBlank(custom2)) {
            errorMsg.append("\u8b66\u544a:CustPram4 \u9053\u5177\u5355\u4ef7\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        final String custom3 = msgBody.getCustPram5();
        if (StringUtils.isBlank(custom3) || !matchGeZeroInt(custom3)) {
            errorMsg.append("\u8b66\u544a:CustPram5 \u9053\u5177\u65f6\u6548\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkActLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        if (StringUtils.isBlank(msgBody.getCustPram1())) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u884c\u4e3a\u5927\u7c7b\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u884c\u4e3a\u5c0f\u7c7b\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkGuideLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        final String custom1 = msgBody.getCustPram1();
        if (StringUtils.isBlank(custom1) || !"load".equals(custom1) || "guide".equals(custom1)) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u8be5\u5b57\u6bb5\u4e3aload\u6216guide!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u65b0\u624b\u5f15\u5bfc\u5c0f\u7c7b\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkReferLog(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        if (StringUtils.isBlank(msgBody.getCustPram1())) {
            errorMsg.append("\u8b66\u544a:CustPram1 \u6709\u6765\u6e90\u65f6\u4e00\u5b9a\u8981\u52a0\u4e0a!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(msgBody.getCustPram2())) {
            errorMsg.append("\u8b66\u544a:CustPram2 \u6709\u6765\u6e90\u65f6\u4e00\u5b9a\u8981\u52a0\u4e0a!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static String checkBaseColmns(final MaintainStat stat) {
        final StringBuffer errorMsg = new StringBuffer();
        final MaintainStatBody msgBody = stat.getBody();
        if (!matchGtZeroInt(msgBody.getCount())) {
            errorMsg.append("\u8b66\u544a: Count \u5fc5\u987b\u4e3a\u6b63\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        final LogUserInfo userInfo = msgBody.getUserInfo();
        if (userInfo == null) {
            errorMsg.append("\u8b66\u544a:UserInfo \u5bf9\u8c61\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        final long gameTime = userInfo.getGameTime();
        if (gameTime == 0L && String.valueOf(gameTime).length() != 10) {
            errorMsg.append("\u8b66\u544a: GameTime\u6e38\u620f\u65f6\u95f4\u5fc5\u987b\u4e3a10\u4f4d\u6570\u5b57\u683c\u5f0f\u65f6\u95f4\u6233!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(userInfo.getOuid())) {
            errorMsg.append("\u8b66\u544a:  uid[\u5e73\u53f0ID]\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(userInfo.getIuid())) {
            errorMsg.append("\u8b66\u544a: uid[\u89d2\u8272ID]\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getGameFriend()))) {
            errorMsg.append("\u8b66\u544a: GameFriend \u6e38\u620f\u597d\u53cb\u6570\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getPlatFriend()))) {
            errorMsg.append("\u8b66\u544a: PlatFriend \u5e73\u53f0\u597d\u53cb\u6570\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getUserExp()))) {
            errorMsg.append("\u8b66\u544a: UserExp \u7528\u6237\u7ecf\u9a8c\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getMoneycoin()))) {
            errorMsg.append("\u8b66\u544a: Moneycoin \u4ed8\u8d39\u8d27\u5e01\u4f59\u989d\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getGamecoin()))) {
            errorMsg.append("\u8b66\u544a: Gamecoin \u5176\u4ed6\u6e38\u620f\u8d27\u5e01\u4f59\u989d\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (!matchGeZeroInt(String.valueOf(userInfo.getVipLevel()))) {
            errorMsg.append("\u8b66\u544a: VipLevel VIP\u7b49\u7ea7\u5e94\u4e3a\u975e\u8d1f\u6574\u6570!").append(ValidateUtils.LINE_SEP);
        }
        if (userInfo.getInstallTime() == 0 && String.valueOf(userInfo.getInstallTime()).length() != 10) {
            errorMsg.append("\u8b66\u544a: InstallTime \u9996\u6b21\u8fdb\u5165\u6e38\u620f\u65f6\u95f4\u5fc5\u987b\u4e3a10\u4f4d\u6570\u5b57\u683c\u5f0f\u65f6\u95f4\u6233!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(userInfo.getVersion())) {
            errorMsg.append("\u8b66\u544a: Version \u5206\u670did\u4e3a\u7a7a!").append(ValidateUtils.LINE_SEP);
        }
        if (StringUtils.isBlank(userInfo.getEntry()) || userInfo.getEntry().length() > 10) {
            errorMsg.append("\u8b66\u544a: Entry \u6e38\u620f\u5e73\u53f0\u5165\u53e3\u4e0d\u5e94\u4e3a\u7a7a\u6216\u8d85\u8fc710\u4f4d!").append(ValidateUtils.LINE_SEP);
        }
        return errorMsg.toString();
    }
    
    private static boolean matchGtZeroInt(final String count) {
        final String regEx = "^[1-9]\\d*$";
        final Pattern p = Pattern.compile(regEx);
        final Matcher m = p.matcher(count);
        return m.find();
    }
    
    private static boolean matchGeZeroInt(final String count) {
        final String regEx = "^[1-9]\\d*|0$";
        final Pattern p = Pattern.compile(regEx);
        final Matcher m = p.matcher(count);
        return m.find();
    }
}
