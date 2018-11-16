package com.reign.gcld.user.service;

import com.reign.gcld.common.*;
import com.reign.gcld.user.domain.*;

public interface IUserService
{
    byte[] createUser(final String p0, final String p1);
    
    ThreeTuple<Boolean, User, byte[]> login(final String p0, final String p1);
}
