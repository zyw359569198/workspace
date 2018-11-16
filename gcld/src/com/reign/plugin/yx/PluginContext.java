package com.reign.plugin.yx;

import com.reign.plugin.yx.common.*;

public final class PluginContext
{
    public static YxConfiguration configuration;
    
    public static void check() {
        if (PluginContext.configuration == null) {
            throw new RuntimeException("yxoperation plugin need YxConfiguration, can't be null");
        }
    }
}
