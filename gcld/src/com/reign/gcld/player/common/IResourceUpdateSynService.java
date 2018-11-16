package com.reign.gcld.player.common;

import java.util.*;

public interface IResourceUpdateSynService
{
    void clearPlayerResourceMap();
    
    Resource getResouce(final int p0);
    
    int getCopper(final int p0);
    
    int getWood(final int p0);
    
    int getFood(final int p0);
    
    int getIron(final int p0);
    
    Date getUpdateTime(final int p0);
    
    long getKfgzVersion(final int p0);
    
    void setKfgzVersion(final int p0, final long p1);
    
    int updateResource(final ResourceParams p0);
}
