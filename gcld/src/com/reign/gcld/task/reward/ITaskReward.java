package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import java.util.*;

public interface ITaskReward
{
    Map<Integer, Reward> rewardPlayer(final PlayerDto p0, final IDataGetter p1, final String p2, final Object p3);
    
    Map<Integer, Reward> getReward(final PlayerDto p0, final IDataGetter p1, final Object p2);
}
