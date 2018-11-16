package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import java.util.*;
import com.reign.util.*;

public interface IMatchPlayerDao extends IBaseDao<MatchPlayer, Integer>
{
    List<MatchPlayer> getSignPlayers(final String p0);
    
    MatchPlayer getMatchPlayer(final String p0);
    
    Set<Tuple<String, String>> getServerSet(final String p0, final String p1);
}
