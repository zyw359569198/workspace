package com.reign.plugin.yx.common;

import java.util.*;
import com.reign.util.*;
import java.util.concurrent.*;

public class UserLoginRequestRecorder
{
    private static final UserLoginRequestRecorder instance;
    private Map<Tuple<String, String>, Tuple<String, String>> recordMap;
    
    static {
        instance = new UserLoginRequestRecorder();
    }
    
    private UserLoginRequestRecorder() {
        this.recordMap = new ConcurrentHashMap<Tuple<String, String>, Tuple<String, String>>();
    }
    
    public static UserLoginRequestRecorder getInstance() {
        return UserLoginRequestRecorder.instance;
    }
    
    public void put(final String yx, final String userId, final String value, final String additionalKey) {
        this.recordMap.put(new Tuple(yx, userId), new Tuple(value, additionalKey));
    }
    
    public boolean contains(final String yx, final String userId) {
        return this.recordMap.containsKey(new Tuple(yx, userId));
    }
    
    public Tuple<String, String> get(final String yx, final String userId) {
        return this.recordMap.get(new Tuple(yx, userId));
    }
    
    public void remove(final String yx, final String userId) {
        this.recordMap.remove(new Tuple(yx, userId));
    }
}
