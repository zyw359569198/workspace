package com.reign.kfgz.resource;

import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kfgz.comm.*;

public interface IKfgzResourceService
{
    KfgzSyncDataResult syncResource(final KfgzSyncDataParam p0);
    
    byte[] startMubing(final KfPlayerInfo p0, final int p1);
    
    byte[] getInfo(final KfPlayerInfo p0);
}
