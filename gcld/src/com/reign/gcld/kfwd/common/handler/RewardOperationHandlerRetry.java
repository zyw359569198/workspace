package com.reign.gcld.kfwd.common.handler;

import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.*;
import java.text.*;

public class RewardOperationHandlerRetry implements IRewardOperationHandler
{
    private static final Logger logger;
    private List<IRewardOperationHandler> handlerList;
    private int maxRetryTimes;
    private int playerId;
    
    static {
        logger = CommonLog.getLog(RewardOperationHandlerRetry.class);
    }
    
    public RewardOperationHandlerRetry(final int maxRetryTimes, final int playerId) {
        this.maxRetryTimes = maxRetryTimes;
        this.playerId = playerId;
        this.handlerList = new ArrayList<IRewardOperationHandler>();
    }
    
    public void addHandler(final IRewardOperationHandler handler) {
        this.handlerList.add(handler);
    }
    
    public void clearHandler() {
        this.handlerList.clear();
    }
    
    @Override
    public void handle(final IDataGetter dataGetter) {
        int retryTimes = 1;
        while (retryTimes <= this.maxRetryTimes) {
            try {
                dataGetter.doHandleWithTrans(this.handlerList);
                return;
            }
            catch (Exception e) {
                RewardOperationHandlerRetry.logger.error(MessageFormat.format("{0} RewardOperationHandlerRetry fail!retry times:{1}", this.playerId, retryTimes), e);
                e.printStackTrace();
                ++retryTimes;
            }
        }
        RewardOperationHandlerRetry.logger.error(MessageFormat.format("{0} RewardOperationHandlerRetry fail at last!", this.playerId));
    }
}
