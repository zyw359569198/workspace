package com.reign.kf.match.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.dao.*;

@Component("dataGetter")
public class DataGetter implements IDataGetter
{
    @Autowired
    private IMatchService matchService;
    @Autowired
    private IMatchPlayerDao matchPlayerDao;
    @Autowired
    private IGcldMatchDao gcldMatchDao;
    @Autowired
    private IMatchScoreDao matchScoreDao;
    @Autowired
    private IMatchPlayerGeneralDao matchPlayerGeneralDao;
    
    @Override
    public IMatchService getMatchService() {
        return this.matchService;
    }
    
    @Override
    public IMatchPlayerDao getMatchPlayerDao() {
        return this.matchPlayerDao;
    }
    
    @Override
    public IGcldMatchDao getGcldMatchDao() {
        return this.gcldMatchDao;
    }
    
    @Override
    public IMatchScoreDao getMatchScoreDao() {
        return this.matchScoreDao;
    }
    
    @Override
    public IMatchPlayerGeneralDao getMatchPlayerGeneralDao() {
        return this.matchPlayerGeneralDao;
    }
}
