package com.reign.gcld.antiaddiction;

import com.reign.gcld.user.dto.*;
import java.util.*;

public class AntiAddictionStateFactory
{
    public static int MAX_RESETOFFLINETIME;
    private static final AntiAddictionStateFactory instance;
    private Map<Integer, IAntiAddictionState> map;
    
    static {
        AntiAddictionStateFactory.MAX_RESETOFFLINETIME = 18000000;
        instance = new AntiAddictionStateFactory();
    }
    
    private AntiAddictionStateFactory() {
        (this.map = new HashMap<Integer, IAntiAddictionState>()).put(0, new AntiAddictionStateNone());
        final int minute = 60;
        final long sec = 60L;
        final int interval = 15;
        this.map.put(1, new AntiAddictionStateInterval(0L, minute * sec * 1000L, 0));
        this.map.put(2, new AntiAddictionStateInterval(1 * minute * sec * 1000L, 2 * minute * sec * 1000L, 1));
        this.map.put(3, new AntiAddictionStateInterval(2 * minute * sec * 1000L, 3 * minute * sec * 1000L, 2));
        this.map.put(4, new AntiAddictionStateIntervalHalfEarnings(3 * minute * sec * 1000L, (long)(3.5 * minute * sec * 1000.0), 3));
        this.map.put(5, new AntiAddictionStateIntervalHalfEarnings((long)(3.5 * minute * sec * 1000.0), 4 * minute * sec * 1000L, 4));
        this.map.put(6, new AntiAddictionStateIntervalHalfEarnings(4 * minute * sec * 1000L, (long)(4.5 * minute * sec * 1000.0), 4));
        this.map.put(7, new AntiAddictionStateIntervalHalfEarnings((long)(4.5 * minute * sec * 1000.0), 5 * minute * sec * 1000L, 5));
        this.map.put(8, new AntiAddictionStateIntervalNoneEarnings(5 * minute * sec * 1000L, Long.MAX_VALUE, 7, interval * minute * sec * 1000L));
    }
    
    public static AntiAddictionStateFactory getInstance() {
        return AntiAddictionStateFactory.instance;
    }
    
    public IAntiAddictionState getDefaultState() {
        return this.map.get(0);
    }
    
    public IAntiAddictionState getState(final UserDto userDto) {
        for (final IAntiAddictionState state : this.map.values()) {
            if (state.isInterval(userDto)) {
                return state;
            }
        }
        return this.getDefaultState();
    }
}
