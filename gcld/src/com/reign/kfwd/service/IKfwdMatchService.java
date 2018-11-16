package com.reign.kfwd.service;

import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kfwd.domain.*;
import com.reign.kfwd.dto.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kfwd.battle.*;

public interface IKfwdMatchService
{
    KfwdSignResult signUp(final String p0, final KfwdSignInfoParam p1, final int p2, final boolean p3);
    
    KfwdRuntimeMatchResult runMatch(final KfwdRuntimeMatch p0);
    
    KfwdSeasonBattleRes getNationResultInfo(final String p0, final int p1);
    
    void doBattleRes(final FightResult p0, final KfwdRuntimeMatch p1);
}
