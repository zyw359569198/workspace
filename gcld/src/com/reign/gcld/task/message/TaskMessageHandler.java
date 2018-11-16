package com.reign.gcld.task.message;

import org.springframework.stereotype.*;
import com.reign.gcld.task.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.message.*;

@Component("taskMessageHandler")
public class TaskMessageHandler implements Handler
{
    @Autowired
    private IPlayerTaskService playerTaskService;
    
    @Override
    public void handler(final Message message) {
        if (message == null) {
            return;
        }
        if (message instanceof TaskMessage) {
            final TaskMessage taskMessage = (TaskMessage)message;
            this.playerTaskService.handleMessage(taskMessage);
        }
    }
}
