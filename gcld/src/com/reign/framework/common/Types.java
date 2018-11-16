package com.reign.framework.common;

import java.util.concurrent.atomic.*;
import java.util.*;

public class Types
{
    private static final AtomicInteger ID;
    private static final HashMap<Class<?>, Integer> type2Id;
    private static final HashMap<Integer, Class<?>> id2Type;
    
    static {
        ID = new AtomicInteger(1);
        type2Id = new HashMap<Class<?>, Integer>();
        id2Type = new HashMap<Integer, Class<?>>();
        register();
    }
    
    private static void register() {
    }
    
    public static synchronized void register(final Class<?> clazz) {
        final int id = Types.ID.getAndIncrement();
        if (Types.type2Id.put(clazz, id) != null) {
            throw new TypeRegisterException("register type " + clazz.getName() + " duplicate");
        }
        Types.id2Type.put(id, clazz);
    }
    
    public static final int id(final Class<?> clazz) {
        return Types.type2Id.get(clazz);
    }
    
    public static Class<?> clazz(final int type) {
        return Types.id2Type.get(type);
    }
}
