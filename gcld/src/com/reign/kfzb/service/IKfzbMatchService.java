package com.reign.kfzb.service;

import com.reign.kfzb.domain.*;
import com.reign.kfzb.dto.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kfzb.battle.*;

public interface IKfzbMatchService
{
    KfzbRuntimeMatchResult runMatch(final KfzbRuntimeMatch p0);
    
    KfzbSignResult signUp(final String p0, final KfzbSignInfo p1, final boolean p2);
    
    void doBattleRes(final FightResult p0, final KfzbRuntimeMatch p1);
}
