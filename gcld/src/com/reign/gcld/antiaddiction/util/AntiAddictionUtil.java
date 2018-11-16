package com.reign.gcld.antiaddiction.util;

import java.util.*;
import com.reign.gcld.user.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.user.domain.*;

public class AntiAddictionUtil
{
    public static Date getOnlineTimeRefreshPoint() {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }
    
    public static UserLoginInfo getUserLoginInfo(final IUserLoginInfoDao userLoginInfoDao, final PlayerDto playerDto) {
        UserLoginInfo uli = userLoginInfoDao.getUserLoginInfo(playerDto.yx, playerDto.userId);
        if (uli == null) {
            uli = getNewUserLoginInfo(playerDto.yx, playerDto.userId);
            userLoginInfoDao.create(uli);
        }
        return uli;
    }
    
    public static UserLoginInfo getNewUserLoginInfo(final String yx, final String userId) {
        final UserLoginInfo uli = new UserLoginInfo();
        uli.setYx(yx);
        uli.setUserId(userId);
        uli.setOnlineTime(0L);
        uli.setOfflineTime(0L);
        uli.setLastLoginTime(new Date());
        uli.setLastLogoutTime(null);
        uli.setTag(0);
        return uli;
    }
}
