package com.reign.gcld.common;

import org.springframework.stereotype.*;

@Component("activityCache")
public class ActivityCache
{
    public static int PAY_ID;
    
    static {
        ActivityCache.PAY_ID = 1;
    }
}
