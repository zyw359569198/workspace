package com.reign.framework.jdbc.orm.util;

import com.reign.framework.jdbc.orm.*;
import net.sf.cglib.beans.*;
import org.apache.commons.logging.*;
import java.util.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.common.*;
import java.lang.annotation.*;
import com.reign.framework.jdbc.orm.annotation.*;

public final class JdbcUtil
{
    private static final Log log;
    private static Map<Class<?>, JdbcField[]> cacheMap;
    private static Map<Class<?>, BeanMap> beanMapCache;
    
    static {
        log = LogFactory.getLog(JdbcUtil.class);
        JdbcUtil.cacheMap = new HashMap<Class<?>, JdbcField[]>();
        JdbcUtil.beanMapCache = new HashMap<Class<?>, BeanMap>();
    }
    
    public static BeanMap getBeanMap(final Class<?> clazz) {
        return JdbcUtil.beanMapCache.get(clazz);
    }
    
    public static BeanMap createBeanMap(final Class<?> clazz) {
        try {
            final BeanMap beanMap = BeanMap.create(clazz.newInstance());
            JdbcUtil.beanMapCache.put(clazz, beanMap);
            return beanMap;
        }
        catch (InstantiationException e) {
            JdbcUtil.log.error("create bean map error, clazz: " + clazz.getName(), e);
        }
        catch (IllegalAccessException e2) {
            JdbcUtil.log.error("create bean map error, clazz: " + clazz.getName(), e2);
        }
        return null;
    }
    
    public static JdbcField[] getJdbcFields(final Class<?> clazz, final NameStrategy nameStrategy) {
        return JdbcUtil.cacheMap.get(clazz);
    }
    
    public static JdbcField[] createJdbcFields(final Class<?> clazz, final NameStrategy nameStrategy) {
        final Lang.MyField[] fields = Lang.getFields(clazz);
        if (fields == null) {
            return null;
        }
        JdbcField[] jdbcFields = null;
        if (fields.length == 0) {
            jdbcFields = new JdbcField[0];
            JdbcUtil.cacheMap.put(clazz, jdbcFields);
            return jdbcFields;
        }
        jdbcFields = new JdbcField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            jdbcFields[i] = new JdbcField(fields[i], nameStrategy);
            jdbcFields[i].isPrimary = Lang.hasAnnotation(jdbcFields[i].field, Id.class);
            jdbcFields[i].insertIgnore = Lang.hasAnnotation(jdbcFields[i].field, InsertIgnoreField.class);
            jdbcFields[i].ignore = Lang.hasAnnotation(jdbcFields[i].field, IgnoreField.class);
            jdbcFields[i].jdbcType = Lang.getJdbcType(jdbcFields[i].field.getType());
        }
        JdbcUtil.cacheMap.put(clazz, jdbcFields);
        return jdbcFields;
    }
}
