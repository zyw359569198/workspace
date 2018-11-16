package com.reign.gcld.scenario.common;

import org.springframework.stereotype.*;
import com.reign.gcld.scenario.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.scenario.message.*;

@Component("scenarioMessageHandler")
public class ScenarioMessageHandler implements Handler
{
    @Autowired
    private ScenarioEventManager scenarioEventManager;
    
    @Override
    public void handler(final Message message) {
        if (message == null) {
            return;
        }
        if (message instanceof ScenarioEventMessage) {
            final ScenarioEventMessage scenarioEventMessage = (ScenarioEventMessage)message;
            this.scenarioEventManager.handleMessage(scenarioEventMessage);
        }
    }
}
