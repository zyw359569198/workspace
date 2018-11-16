package com.reign.gcld.building.service;

public interface IBuildingOutputCache
{
    int getBuildingsOutput(final int p0, final int p1);
    
    int getBuildingsOutputBase(final int p0, final int p1);
    
    int getAdditionsOutput(final int p0, final int p1);
    
    int getTechsOutput(final int p0, final int p1);
    
    int getOfficersOutput(final int p0, final int p1);
    
    double getAdditionCache(final int p0, final int p1);
    
    int getBuildingOutputBase(final int p0, final int p1);
    
    int getBuildingOutput(final int p0, final int p1);
    
    void clearOutputAddition(final int p0, final int p1);
    
    void clearLimit(final int p0);
    
    void logoutClear(final int p0);
    
    void clearBase(final int p0, final int p1);
    
    void clearTech(final int p0, final int p1);
    
    void clearTechGaoGuan(final int p0);
    
    void clearOfficer(final int p0);
    
    void clearTechBinZhong(final int p0);
}
