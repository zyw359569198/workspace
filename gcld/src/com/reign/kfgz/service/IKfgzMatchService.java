package com.reign.kfgz.service;

import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;

public interface IKfgzMatchService
{
    KfgzSignResult doSignUp(final KfgzSignInfoParam p0, final String p1);
    
    KfgzBaseInfoRes getGzBaseInfo(final KfgzGzKey p0, final String p1);
    
    KfgzNationResInfo getGzResultInfo(final KfgzNationResKey p0, final String p1);
}
