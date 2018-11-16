package com.reign.gcld.common;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.message.login.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.event.common.*;

@Component("register")
public class Register implements InitializingBean
{
    @Autowired
    @Qualifier("taskMessageHandler")
    private Handler taskMessageHandler;
    @Autowired
    @Qualifier("loginMessageHandler")
    private Handler loginMessageHandler;
    @Autowired
    @Qualifier("scenarioMessageHandler")
    private Handler scenarioMessageHandler;
    @Autowired
    @Qualifier("eventService")
    private Handler eventService;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.registerMessageHandler();
    }
    
    private void registerMessageHandler() {
        HandlerManager.registerHandler(LoginMessage.class, this.loginMessageHandler);
        HandlerManager.registerHandler(TaskMessage.class, this.taskMessageHandler);
        HandlerManager.registerHandler(ScenarioEventMessage.class, this.scenarioMessageHandler);
        HandlerManager.registerHandler(Event.class, this.eventService);
    }
}
