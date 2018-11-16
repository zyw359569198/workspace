package com.reign.kf.gw.common.web;

import com.reign.util.timer.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;

public class LocalInitProjectListener implements InitProjectListener
{
    private static List<BaseSystemTimeTimer> timerList;
    
    static {
        LocalInitProjectListener.timerList = new ArrayList<BaseSystemTimeTimer>();
    }
    
    public static void registerTimer(final BaseSystemTimeTimer timer) {
        LocalInitProjectListener.timerList.add(timer);
    }
    
    @Override
	public void init(final ServletContext context, final NettyConfig config) {
        for (final BaseSystemTimeTimer timer : LocalInitProjectListener.timerList) {
            timer.start();
        }
    }
}
