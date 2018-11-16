package com.reign.plugin.yx.common;

import java.util.*;
import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import com.reign.plugin.yx.common.xml.*;

public class AsynJob extends TimerTask
{
    private String url;
    private Map<String, Object> map;
    private Log log;
    
    public AsynJob(final String notifyUrl, final Map<String, Object> paramMap, final Log log) {
        this.url = notifyUrl;
        this.map = paramMap;
        this.log = log;
    }
    
    @Override
    public void run() {
        int retryTimes = 3;
        while (retryTimes-- > 0) {
            try {
                final String value = WebUtils.sendGetRequest(this.url, this.map);
                this.log.error("value:" + value);
                if (!StringUtils.isBlank(value)) {
                    final XMLParser xmlParser = new XMLParser(value);
                    final String returnValue = xmlParser.getValueByTagName("tbOrderSuccess");
                    this.log.error("returned value:" + returnValue);
                    if ("T".equalsIgnoreCase(returnValue)) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                this.log.error(this, e);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                this.log.error(this, e);
            }
        }
    }
}
