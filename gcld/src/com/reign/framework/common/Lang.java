package com.reign.framework.common;

import com.reign.framework.netty.mvc.spring.*;
import com.reign.framework.netty.mvc.*;
import org.apache.commons.logging.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import com.reign.framework.netty.servlet.*;
import org.springframework.context.*;
import java.beans.*;
import java.util.*;
import com.reign.framework.jdbc.*;
import java.math.*;
import java.sql.*;

public final class Lang
{
    private static final Log log;
    private static Map<Class<?>, Object> DEFAULT_VALUE_MAP;
    private static Set<Class<?>> SIMPLE_CLASS;
    private static Map<Class<?>, Class<?>> DEFAULT_WRAPPERCLASS_MAP;
    private static SpringObjectFactory objectFactory;
    private static ObjectFactory comObjectFactory;
    public static Map<Class<?>, MyField[]> FIELD_MAP;
    
    static {
        log = LogFactory.getLog(Lang.class);
        Lang.DEFAULT_VALUE_MAP = new HashMap<Class<?>, Object>();
        Lang.SIMPLE_CLASS = new HashSet<Class<?>>();
        Lang.DEFAULT_WRAPPERCLASS_MAP = new HashMap<Class<?>, Class<?>>();
        Lang.objectFactory = null;
        Lang.comObjectFactory = new ObjectFactory();
        Lang.FIELD_MAP = new HashMap<Class<?>, MyField[]>(1024);
        Lang.DEFAULT_VALUE_MAP.put(Boolean.TYPE, false);
        Lang.DEFAULT_VALUE_MAP.put(Byte.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Character.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Short.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Integer.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Long.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Float.TYPE, 0);
        Lang.DEFAULT_VALUE_MAP.put(Double.TYPE, 0);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Boolean.TYPE, Boolean.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Byte.TYPE, Byte.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Character.TYPE, Character.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Short.TYPE, Short.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Integer.TYPE, Integer.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Long.TYPE, Long.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Float.TYPE, Float.class);
        Lang.DEFAULT_WRAPPERCLASS_MAP.put(Double.TYPE, Double.class);
        for (final Class<?> clazz : Lang.DEFAULT_VALUE_MAP.keySet()) {
            Lang.SIMPLE_CLASS.add(clazz);
        }
        for (final Class<?> clazz : Lang.DEFAULT_WRAPPERCLASS_MAP.values()) {
            Lang.SIMPLE_CLASS.add(clazz);
        }
    }
    
    public static <T extends Annotation> T getAnnotation(final Class<?> clazz, final Class<T> anClass) {
        Class<?> cc = clazz;
        T annotation = null;
        while (cc != null && cc != Object.class) {
            annotation = cc.getAnnotation(anClass);
            if (annotation != null) {
                return annotation;
            }
            cc = cc.getSuperclass();
        }
        return null;
    }
    
    public static <T extends Annotation> T getAnnotation(final Field field, final Class<T> anClass) {
        final T annotation = field.getAnnotation(anClass);
        return annotation;
    }
    
    public static boolean hasAnnotation(final Field field, final Class<? extends Annotation> anClass) {
        final Annotation annotation = field.getAnnotation(anClass);
        return annotation != null;
    }
    
    public static boolean isStaticMethod(final Method method) {
        return Modifier.isStatic(method.getModifiers()) && !Modifier.isFinal(method.getModifiers());
    }
    
    public static <T> T castTo(final Object src, final Class<T> clazz) {
        if (src == null) {
            return (T)getDefaultValue(clazz);
        }
        return castTo(src, src.getClass(), clazz);
    }
    
    public static <T, F> T castTo(final Object src, final Class<F> fromType, final Class<T> toType) {
        if (fromType.getName().equals(toType.getName())) {
            return (T)src;
        }
        if (toType.isAssignableFrom(fromType)) {
            return (T)src;
        }
        if (fromType == String.class) {
            return String2Object((String)src, toType);
        }
        if (fromType.isArray() && !toType.isArray()) {
            return castTo(Array.get(src, 0), toType);
        }
        if (fromType.isArray() && toType.isArray()) {
            final int len = Array.getLength(src);
            final Object result = Array.newInstance(toType.getComponentType(), len);
            for (int i = 0; i < len; ++i) {
                Array.set(result, i, castTo(Array.get(src, i), toType.getComponentType()));
            }
            return (T)result;
        }
        return (T)getDefaultValue(toType);
    }
    
    public static <T> T String2Object(final String str, final Class<T> type) {
        try {
            if (isBoolean(type)) {
                return (T)Boolean.valueOf(str);
            }
            if (isByte(type)) {
                return (T)Byte.valueOf(str);
            }
            if (isChar(type)) {
                return (T)str.charAt(0);
            }
            if (isInteger(type)) {
                return (T)Integer.valueOf(str);
            }
            if (isFloat(type)) {
                return (T)Float.valueOf(str);
            }
            if (isLong(type)) {
                return (T)Long.valueOf(str);
            }
            if (isDouble(type)) {
                return (T)Double.valueOf(str);
            }
            if (isShort(type)) {
                return (T)Short.valueOf(str);
            }
            if (isString(type)) {
                return (T)str;
            }
            if (isStringLike(type)) {
                return (T)str;
            }
            final Constructor<T> constructor = (Constructor<T>)getWrapper(type).getConstructor(String.class);
            if (constructor != null) {
                return constructor.newInstance(str);
            }
        }
        catch (Throwable t) {}
        return (T)getDefaultValue(type);
    }
    
    private static Class<?> getWrapper(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return Lang.DEFAULT_WRAPPERCLASS_MAP.get(clazz);
        }
        return clazz;
    }
    
    public static boolean isBoolean(final Class<?> clazz) {
        return is(clazz, Boolean.TYPE) || is(clazz, Boolean.class);
    }
    
    public static boolean isByte(final Class<?> clazz) {
        return is(clazz, Byte.TYPE) || is(clazz, Byte.class);
    }
    
    public static boolean isChar(final Class<?> clazz) {
        return is(clazz, Character.TYPE) || is(clazz, Character.class);
    }
    
    public static boolean isShort(final Class<?> clazz) {
        return is(clazz, Short.TYPE) || is(clazz, Short.class);
    }
    
    public static boolean isInteger(final Class<?> clazz) {
        return is(clazz, Integer.TYPE) || is(clazz, Integer.class);
    }
    
    public static boolean isLong(final Class<?> clazz) {
        return is(clazz, Long.TYPE) || is(clazz, Long.class);
    }
    
    public static boolean isFloat(final Class<?> clazz) {
        return is(clazz, Float.TYPE) || is(clazz, Float.class);
    }
    
    public static boolean isDouble(final Class<?> clazz) {
        return is(clazz, Double.TYPE) || is(clazz, Double.class);
    }
    
    public static boolean isString(final Class<?> clazz) {
        return is(clazz, String.class);
    }
    
    public static boolean isStringLike(final Class<?> clazz) {
        return CharSequence.class.isAssignableFrom(clazz);
    }
    
    private static boolean is(final Class<?> clazz1, final Class<?> clazz2) {
        return clazz1 == clazz2;
    }
    
    public static Object getDefaultValue(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return Lang.DEFAULT_VALUE_MAP.get(clazz);
        }
        return null;
    }
    
    public static boolean isSimpleClass(final Class<?> clazz) {
        return Lang.SIMPLE_CLASS.contains(clazz);
    }
    
    public static Object createObject(final ServletContext servletContext, final Class<?> clazz) throws Exception {
        ObjectFactory localObjectFactory = Lang.objectFactory;
        if (localObjectFactory == null) {
            final SpringObjectFactory springObjectFactory = new SpringObjectFactory();
            final ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (applicationContext == null) {
                Lang.log.info("ApplicationContext could not be found. Action classes will not be autowired");
                localObjectFactory = Lang.comObjectFactory;
            }
            else {
                springObjectFactory.setApplicationContext(applicationContext);
                localObjectFactory = springObjectFactory;
                Lang.objectFactory = springObjectFactory;
            }
        }
        return localObjectFactory.buildBean(clazz);
    }
    
    public static Object createObject(final Class<?> clazz) {
        try {
            return Lang.comObjectFactory.buildBean(clazz);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static MyField[] getFields(final Class<?> clazz) {
        MyField[] myFields = Lang.FIELD_MAP.get(clazz);
        if (myFields == null) {
            synchronized (Lang.FIELD_MAP) {
                myFields = Lang.FIELD_MAP.get(clazz);
                if (myFields == null) {
                    try {
                        final Field[] fields = clazz.getDeclaredFields();
                        final Map<String, Field> map = new HashMap<String, Field>();
                        Field[] array;
                        for (int length = (array = fields).length, i = 0; i < length; ++i) {
                            final Field field = array[i];
                            map.put(field.getName(), field);
                        }
                        final BeanInfo bi = Introspector.getBeanInfo(clazz);
                        final PropertyDescriptor[] pds = bi.getPropertyDescriptors();
                        final List<MyField> list = new ArrayList<MyField>();
                        PropertyDescriptor[] array2;
                        for (int length2 = (array2 = pds).length, j = 0; j < length2; ++j) {
                            final PropertyDescriptor pd = array2[j];
                            if (pd.getPropertyType() != Class.class) {
                                final MyField field2 = new MyField();
                                field2.fieldName = pd.getDisplayName();
                                field2.field = map.get(field2.fieldName);
                                field2.type = getType(pd.getPropertyType());
                                field2.getter = pd.getReadMethod();
                                field2.writter = pd.getWriteMethod();
                                list.add(field2);
                            }
                        }
                        myFields = list.toArray(new MyField[0]);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Lang.FIELD_MAP.put(clazz, myFields);
                }
            }
            // monitorexit(Lang.FIELD_MAP)
        }
        return myFields;
    }
    
    private static ClassType getType(final Class<?> clazzType) {
        ClassType type = ClassType.PRIMITIVE_TYPE;
        if (clazzType.isAssignableFrom(Date.class)) {
            type = ClassType.DATE_TYPE;
        }
        else if (clazzType.isAssignableFrom(Map.class)) {
            type = ClassType.MAP_TYPE;
        }
        else if (clazzType.isAssignableFrom(List.class)) {
            type = ClassType.LIST_TYPE;
        }
        else if (clazzType.isArray()) {
            type = ClassType.ARRAY_TYPE;
        }
        return type;
    }
    
    public static Type getJdbcType(final Class<?> clazzType) {
        Type type = Type.Object;
        if (isInteger(clazzType)) {
            type = Type.Int;
        }
        else if (isLong(clazzType)) {
            type = Type.Long;
        }
        else if (isDouble(clazzType)) {
            type = Type.Double;
        }
        else if (isFloat(clazzType)) {
            type = Type.Float;
        }
        else if (isStringLike(clazzType)) {
            type = Type.String;
        }
        else if (isByte(clazzType)) {
            type = Type.Byte;
        }
        else if (isShort(clazzType)) {
            type = Type.Int;
        }
        else if (isChar(clazzType)) {
            type = Type.Int;
        }
        else if (is(clazzType, java.sql.Date.class)) {
            type = Type.SqlDate;
        }
        else if (is(clazzType, Date.class)) {
            type = Type.Date;
        }
        else if (is(clazzType, Time.class)) {
            type = Type.Time;
        }
        else if (is(clazzType, Timestamp.class)) {
            type = Type.Timestamp;
        }
        else if (clazzType.isAssignableFrom(BigDecimal.class)) {
            type = Type.BigDecimal;
        }
        else if (clazzType.isAssignableFrom(Blob.class)) {
            type = Type.Blob;
        }
        else if (clazzType.isAssignableFrom(Clob.class)) {
            type = Type.Clob;
        }
        else if (clazzType.isAssignableFrom(NClob.class)) {
            type = Type.NClob;
        }
        else if (is(byte[].class, clazzType) || is(Byte[].class, clazzType)) {
            type = Type.Bytes;
        }
        else if (isBoolean(clazzType)) {
            type = Type.Bool;
        }
        return type;
    }
    
    public enum ClassType
    {
        PRIMITIVE_TYPE("PRIMITIVE_TYPE", 0), 
        STATIC_TYPE("STATIC_TYPE", 1), 
        FINAL_TYPE("FINAL_TYPE", 2), 
        DATE_TYPE("DATE_TYPE", 3), 
        MAP_TYPE("MAP_TYPE", 4), 
        LIST_TYPE("LIST_TYPE", 5), 
        ARRAY_TYPE("ARRAY_TYPE", 6);
        
        private ClassType(final String s, final int n) {
        }
    }
    
    public static class MyField
    {
        public Field field;
        public String fieldName;
        public ClassType type;
        public Method getter;
        public Method writter;
    }
}
