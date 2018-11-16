package com.reign.gcld.common;

import com.reign.gcld.common.log.*;
import org.apache.commons.lang.*;
import com.reign.util.*;
import java.util.*;
import java.text.*;

public class PayUtil
{
    private static final Logger errorLog;
    
    static {
        errorLog = CommonLog.getLog(PayUtil.class);
    }
    
    public static String getAbsolutePayUrl(final String yx, final String userId, final int playerId) {
        if (StringUtils.isBlank(Configuration.getProperty(yx, "gcld.pay.url"))) {
            return "";
        }
        return MessageFormatter.format(Configuration.getProperty(yx, "gcld.pay.url").trim(), new Object[] { Configuration.getProperty(yx, "gcld.serverid").trim(), userId, playerId });
    }
    
    public boolean hasPayActivity() {
        final String startTimeSTR = Configuration.getProperty("gcld.pay.activity.startTime");
        final String endTimeSTR = Configuration.getProperty("gcld.pay.activity.endTime");
        if (StringUtils.isBlank(startTimeSTR) || StringUtils.isBlank(endTimeSTR)) {
            return false;
        }
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date now = new Date();
        Date startTime = new Date();
        Date endTime = new Date();
        try {
            startTime = df.parse(startTimeSTR);
            endTime = df.parse(endTimeSTR);
        }
        catch (ParseException e) {
            PayUtil.errorLog.error("pay activity conf error: startTimeSTR[" + startTimeSTR + "] endTime[" + endTimeSTR + "]", e);
            return false;
        }
        if (startTime.after(endTime)) {
            PayUtil.errorLog.error("pay activity conf error: startTime[" + startTime + "] endTime[" + endTime + "]");
        }
        return !now.before(startTime) && !now.after(endTime);
    }
}
