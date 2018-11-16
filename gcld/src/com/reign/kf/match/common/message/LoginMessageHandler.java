package com.reign.kf.match.common.message;

import org.springframework.stereotype.*;
import com.reign.kf.match.log.*;
import com.reign.util.*;

@Component("loginMessageHandler")
public class LoginMessageHandler implements Handler
{
    private static final Logger logger;
    
    static {
        logger = CommonLog.getLog(LoginMessageHandler.class);
    }
    
    @Override
    public void handler(final Message message) {
        if (message == null) {
            return;
        }
        if (message instanceof LoginMessage) {
            final LoginMessage loginMessage = (LoginMessage)message;
            if (loginMessage.getPlayerDto() == null || loginMessage.getPlayerDto().getUuid() == 0L) {
                return;
            }
            if (Action.LOGIN.equals(loginMessage.getAction())) {
                this.handlerLogin(loginMessage);
            }
            else if (Action.LOGINOUT.equals(loginMessage.getAction())) {
                this.handlerLoginOut(loginMessage);
            }
            else if (Action.REGISTER.equals(loginMessage.getAction())) {
                this.handlerRegister(loginMessage);
            }
        }
    }
    
    private void handlerRegister(final LoginMessage loginMessage) {
        LoginMessageHandler.logger.info(MessageFormatter.format("{0}#{1}#register.", new Object[] { loginMessage.getPlayerDto().getUuid(), loginMessage.getPlayerDto().getPlayerName() }));
    }
    
    private void handlerLoginOut(final LoginMessage loginMessage) {
        LoginMessageHandler.logger.info(MessageFormatter.format("{0}#{1}#logout.", new Object[] { loginMessage.getPlayerDto().getUuid(), loginMessage.getPlayerDto().getPlayerName() }));
    }
    
    private void handlerLogin(final LoginMessage loginMessage) {
        LoginMessageHandler.logger.info(MessageFormatter.format("{0}#{1}#login.", new Object[] { loginMessage.getPlayerDto().getUuid(), loginMessage.getPlayerDto().getPlayerName() }));
    }
}
