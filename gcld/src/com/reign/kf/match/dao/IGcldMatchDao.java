package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import java.util.*;

public interface IGcldMatchDao extends IBaseDao<GcldMatch, Integer>
{
    List<GcldMatch> getMatch(final int p0, final int p1, final String p2);
    
    GcldMatch getFirstMatch(final String p0, final int p1);
    
    int getCurrentTurn(final String p0);
    
    List<GcldMatch> getMatchByTurn(final String p0, final int p1);
    
    List<GcldMatch> getMatchNumMatch(final int p0, final String p1);
    
    GcldMatch getMatch(final int p0, final int p1, final int p2, final String p3);
}
