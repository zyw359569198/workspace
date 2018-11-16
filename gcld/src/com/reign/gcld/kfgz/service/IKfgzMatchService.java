package com.reign.gcld.kfgz.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.kfgz.dto.response.*;

public interface IKfgzMatchService
{
    byte[] signUp(final PlayerDto p0);
    
    void doDatabaseForKfgz(final KfgzSyncDataResult p0) throws Exception;
    
    KfgzSignResult doSignUp(final Player p0, final String p1);
    
    void init(final String p0, final int p1);
    
    void stopMatchService();
    
    void writeKfgzNationResInfoToDatabase(final KfgzNationResInfo p0, final int p1);
    
    byte[] getRewardBoard(final PlayerDto p0);
    
    byte[] getReward(final PlayerDto p0, final int p1) throws Exception;
    
    void issueRoundReward(final int p0, final int p1);
}
