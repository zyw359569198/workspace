package com.reign.kf.match.ini;

import com.reign.framework.netty.mvc.*;
import com.reign.kf.match.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.ai.*;
import com.reign.kfgz.resource.*;
import org.springframework.context.*;
import com.reign.kfgz.control.*;

public class IniData implements InitProjectListener
{
    private static final long serialVersionUID = 1L;
    private static Logger log;
    protected ServletContext context;
    protected static ObjectFactory objectFactory;
    
    static {
        IniData.log = CommonLog.getLog(IniData.class);
    }
    
    public ServletContext getServletContext() {
        return this.context;
    }
    
    @Override
	public void init(final ServletContext context, final NettyConfig config) {
        this.context = context;
        IniData.log.info("begin IniData");
        if (KfgzConstants.GZALLCLOSE) {
            return;
        }
        AIBehaviourExecutor.getInstance().init();
        KfgzResSenderManager.ini();
        final ApplicationContext applicationContext = (ApplicationContext)this.getServletContext().getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        final KfgzManager gzManager = (KfgzManager)applicationContext.getBean("kfgzManager");
        gzManager.ini();
        IniData.log.info("end IniData");
    }
}
