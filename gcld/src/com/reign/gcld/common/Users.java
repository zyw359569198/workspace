package com.reign.gcld.common;

import com.reign.gcld.user.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.log.*;
import java.util.*;

public class Users
{
    public static ConcurrentMap<String, UserDto> userMap;
    public static ConcurrentMap<String, Session> userSessionMap;
    public static ConcurrentMap<String, UserDto> sessionUserMap;
    private static final Logger TimeLog;
    
    static {
        Users.userMap = new ConcurrentHashMap<String, UserDto>();
        Users.userSessionMap = new ConcurrentHashMap<String, Session>();
        Users.sessionUserMap = new ConcurrentHashMap<String, UserDto>();
        TimeLog = new TimerLogger();
    }
    
    public static void addUser(final UserDto dto, final Session session) {
        Users.userMap.put(dto.getId(), dto);
        Users.userSessionMap.put(dto.getId(), session);
        Users.sessionUserMap.put(session.getId(), dto);
    }
    
    public static void removeUser(final String id) {
        Users.userMap.remove(id);
        final Session session = Users.userSessionMap.remove(id);
        if (session != null) {
            Users.sessionUserMap.remove(session.getId());
        }
    }
    
    public static void sessionInvalidate(final String sessionId) {
        final UserDto dto = Users.sessionUserMap.remove(sessionId);
        if (dto != null) {
            Users.userMap.remove(dto.getId());
            Users.userSessionMap.remove(dto.getId());
        }
    }
    
    public static Session getSession(final String id) {
        return Users.userSessionMap.get(id);
    }
    
    public static Collection<UserDto> getAllUser() {
        return Users.userMap.values();
    }
    
    public static boolean isValidate(final UserDto dto) {
        if (Users.userMap.putIfAbsent(dto.getId(), dto) == null) {
            Users.TimeLog.info("userId:" + dto.userId + " userName:" + dto.userName + " first login login_time:" + dto.loginTime);
            return true;
        }
        final UserDto temp = Users.userMap.get(dto.getId());
        Users.TimeLog.info("userId:" + dto.userId + " userName:" + dto.userName + " userDto.loginTime:" + temp.loginTime + " dto.loginTime:" + dto.loginTime);
        if (dto.loginTime >= temp.loginTime) {
            Users.TimeLog.info("userId:" + dto.userId + " userName:" + dto.userName + " dto.loginTime: greater than temp.loginTime");
            return true;
        }
        Users.TimeLog.info("userId:" + dto.userId + " userName:" + "kick away");
        return false;
    }
    
    public static UserDto getUserDto(final String userId, final String yx) {
        final String id = new StringBuilder(30).append(userId).append("-").append(yx).toString();
        return Users.userMap.get(id);
    }
    
    public static void modifyUserMap(final String userId, final String originYx, final String newYx) {
        final String id = new StringBuilder(30).append(userId).append("-").append(originYx).toString();
        final UserDto userDto = Users.userMap.get(id);
        final String newId = new StringBuilder(30).append(userId).append("-").append(newYx).toString();
        if (userDto != null) {
            userDto.yx = newYx;
            userDto.setId(newId);
            Users.userMap.remove(id);
            Users.userMap.put(newId, userDto);
        }
    }
}
