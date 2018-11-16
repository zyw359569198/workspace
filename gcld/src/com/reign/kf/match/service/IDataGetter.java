package com.reign.kf.match.service;

import com.reign.kf.match.dao.*;

public interface IDataGetter
{
    IMatchService getMatchService();
    
    IMatchPlayerDao getMatchPlayerDao();
    
    IGcldMatchDao getGcldMatchDao();
    
    IMatchScoreDao getMatchScoreDao();
    
    IMatchPlayerGeneralDao getMatchPlayerGeneralDao();
}
