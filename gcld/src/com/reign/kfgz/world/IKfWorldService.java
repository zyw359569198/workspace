package com.reign.kfgz.world;

import com.reign.kfgz.comm.*;
import com.reign.kf.comm.util.*;

public interface IKfWorldService
{
    byte[] getWorldMap(final KfPlayerInfo p0);
    
    Tuple<byte[], Boolean> move(final KfPlayerInfo p0, final int p1, final int p2);
    
    byte[] getCityInfo(final KfPlayerInfo p0, final int p1);
    
    byte[] getJieBingInfo(final KfPlayerInfo p0);
    
    byte[] getAllyInfo(final KfPlayerInfo p0);
}
