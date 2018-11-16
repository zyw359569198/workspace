package com.reign.framework.jdbc;

import com.reign.framework.jdbc.orm.util.*;
import net.sf.cglib.beans.*;
import com.reign.framework.jdbc.orm.*;
import com.reign.util.*;
import java.sql.*;
import java.lang.reflect.*;
import java.beans.*;
import java.util.*;

public class BeanProcessor
{
    protected static final int PROPERTY_NOT_FOUND = -1;
    private static final Map<Class<?>, Object> primitiveDefaults;
    private NameStrategy strategy;
    
    static {
        (primitiveDefaults = new HashMap<Class<?>, Object>()).put(Integer.TYPE, 0);
        BeanProcessor.primitiveDefaults.put(Short.TYPE, (short)0);
        BeanProcessor.primitiveDefaults.put(Byte.TYPE, (byte)0);
        BeanProcessor.primitiveDefaults.put(Float.TYPE, 0.0f);
        BeanProcessor.primitiveDefaults.put(Double.TYPE, 0.0);
        BeanProcessor.primitiveDefaults.put(Long.TYPE, 0L);
        BeanProcessor.primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        BeanProcessor.primitiveDefaults.put(Character.TYPE, '\0');
    }
    
    public BeanProcessor() {
        this.strategy = new DefaultNameStrategy();
    }
    
    public <T> T toBean(final ResultSet rs, final Class<T> type) throws SQLException {
        final BeanMap beanMap = JdbcUtil.getBeanMap(type);
        final JdbcField[] fields = JdbcUtil.getJdbcFields(type, this.strategy);
        if (fields == null) {
            final PropertyDescriptor[] props = this.propertyDescriptors(type);
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);
            return this.createBean(rs, type, props, columnToProperty);
        }
        if (beanMap == null) {
            final ResultSetMetaData rsmd2 = rs.getMetaData();
            final int[] columnToProperty2 = this.mapColumnsToProperties(rsmd2, fields);
            return this.createBean(rs, type, fields, columnToProperty2);
        }
        final ResultSetMetaData rsmd2 = rs.getMetaData();
        final int[] columnToProperty2 = this.mapColumnsToProperties(rsmd2, fields);
        return this.createBean(rs, type, fields, beanMap, columnToProperty2);
    }
    
    public <T> List<T> toBeanList(final ResultSet rs, final Class<T> type) throws SQLException {
        final List<T> results = new ArrayList<T>();
        if (!rs.next()) {
            return results;
        }
        final JdbcField[] fields = JdbcUtil.getJdbcFields(type, this.strategy);
        final BeanMap beanMap = JdbcUtil.getBeanMap(type);
        if (fields == null) {
            final PropertyDescriptor[] props = this.propertyDescriptors(type);
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);
            do {
                results.add(this.createBean(rs, type, props, columnToProperty));
            } while (rs.next());
            return results;
        }
        if (beanMap == null) {
            final ResultSetMetaData rsmd2 = rs.getMetaData();
            final int[] columnToProperty2 = this.mapColumnsToProperties(rsmd2, fields);
            do {
                results.add(this.createBean(rs, type, fields, columnToProperty2));
            } while (rs.next());
            return results;
        }
        final ResultSetMetaData rsmd2 = rs.getMetaData();
        final int[] columnToProperty2 = this.mapColumnsToProperties(rsmd2, fields);
        do {
            results.add(this.createBean(rs, type, fields, beanMap, columnToProperty2));
        } while (rs.next());
        return results;
    }
    
    private <T> T createBean(final ResultSet rs, final Class<T> type, final JdbcField[] fields, final BeanMap beanMap, final int[] columnToProperty) throws SQLException {
        final T bean = this.newInstance(type);
        final BeanMap newBeanMap = beanMap.newInstance(bean);
        for (int i = 1; i < columnToProperty.length; ++i) {
            if (columnToProperty[i] != -1) {
                final JdbcField field = fields[columnToProperty[i]];
                Object value = this.processColumn(rs, i, field.jdbcType);
                if (value == null && field.field.getType().isPrimitive()) {
                    value = BeanProcessor.primitiveDefaults.get(field.field.getType());
                }
                newBeanMap.put(field.propertyName, value);
            }
        }
        return bean;
    }
    
    private <T> T createBean(final ResultSet rs, final Class<T> type, final JdbcField[] fields, final int[] columnToProperty) throws SQLException {
        final T bean = this.newInstance(type);
        for (int i = 1; i < columnToProperty.length; ++i) {
            if (columnToProperty[i] != -1) {
                final JdbcField field = fields[columnToProperty[i]];
                Object value = this.processColumn(rs, i, field.jdbcType);
                if (value == null && field.field.getType().isPrimitive()) {
                    value = BeanProcessor.primitiveDefaults.get(field.field.getType());
                }
                ReflectUtil.set(field.field, bean, value);
            }
        }
        return bean;
    }
    
    private <T> T createBean(final ResultSet rs, final Class<T> type, final PropertyDescriptor[] props, final int[] columnToProperty) throws SQLException {
        final T bean = this.newInstance(type);
        for (int i = 1; i < columnToProperty.length; ++i) {
            if (columnToProperty[i] != -1) {
                final PropertyDescriptor prop = props[columnToProperty[i]];
                final Class<?> propType = prop.getPropertyType();
                Object value = this.processColumn(rs, i, propType);
                if (propType != null && value == null && propType.isPrimitive()) {
                    value = BeanProcessor.primitiveDefaults.get(propType);
                }
                this.callSetter(bean, prop, value);
            }
        }
        return bean;
    }
    
    private void callSetter(final Object target, final PropertyDescriptor prop, Object value) throws SQLException {
        final Method setter = prop.getWriteMethod();
        if (setter == null) {
            return;
        }
        final Class[] params = setter.getParameterTypes();
        try {
            if (value != null && value instanceof Date) {
                if (params[0].getName().equals("java.sql.Date")) {
                    value = new java.sql.Date(((Date)value).getTime());
                }
                else if (params[0].getName().equals("java.sql.Time")) {
                    value = new Time(((Date)value).getTime());
                }
                else if (params[0].getName().equals("java.sql.Timestamp")) {
                    value = new Timestamp(((Date)value).getTime());
                }
            }
            if (!this.isCompatibleType(value, params[0])) {
                throw new SQLException("Cannot set " + prop.getName() + ": incompatible types.");
            }
            setter.invoke(target, value);
        }
        catch (IllegalArgumentException e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        }
        catch (IllegalAccessException e2) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e2.getMessage());
        }
        catch (InvocationTargetException e3) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e3.getMessage());
        }
    }
    
    private boolean isCompatibleType(final Object value, final Class<?> type) {
        return value == null || type.isInstance(value) || (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) || (type.equals(Long.TYPE) && Long.class.isInstance(value)) || (type.equals(Double.TYPE) && Double.class.isInstance(value)) || (type.equals(Float.TYPE) && Float.class.isInstance(value)) || (type.equals(Short.TYPE) && Short.class.isInstance(value)) || (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) || (type.equals(Character.TYPE) && Character.class.isInstance(value)) || (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value));
    }
    
    protected <T> T newInstance(final Class<T> c) throws SQLException {
        try {
            return c.newInstance();
        }
        catch (InstantiationException e) {
            throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
        }
        catch (IllegalAccessException e2) {
            throw new SQLException("Cannot create " + c.getName() + ": " + e2.getMessage());
        }
    }
    
    private PropertyDescriptor[] propertyDescriptors(final Class<?> c) throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);
        }
        catch (IntrospectionException e) {
            throw new SQLException("Bean introspection failed: " + e.getMessage());
        }
        return beanInfo.getPropertyDescriptors();
    }
    
    protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd, final PropertyDescriptor[] props) throws SQLException {
        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, -1);
        for (int col = 1; col <= cols; ++col) {
            String columnName = rsmd.getColumnLabel(col);
            if (columnName == null || columnName.length() == 0) {
                columnName = rsmd.getColumnName(col);
            }
            for (int i = 0; i < props.length; ++i) {
                if (this.strategy.columnNameToPropertyName(columnName).equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }
        return columnToProperty;
    }
    
    protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd, final JdbcField[] fields) throws SQLException {
        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, -1);
        for (int col = 1; col <= cols; ++col) {
            String columnName = rsmd.getColumnLabel(col);
            if (columnName == null || columnName.length() == 0) {
                columnName = rsmd.getColumnName(col);
            }
            for (int i = 0; i < fields.length; ++i) {
                if (fields[i].columnName.equalsIgnoreCase(columnName)) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }
        return columnToProperty;
    }
    
    protected Object processColumn(final ResultSet rs, final int index, final Class<?> propType) throws SQLException {
        if (propType.equals(String.class)) {
            return rs.getString(index);
        }
        if (propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
            return rs.getInt(index);
        }
        if (propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
            return rs.getBoolean(index);
        }
        if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
            return rs.getLong(index);
        }
        if (propType.equals(Double.TYPE) || propType.equals(Double.class)) {
            return rs.getDouble(index);
        }
        if (propType.equals(Float.TYPE) || propType.equals(Float.class)) {
            return rs.getFloat(index);
        }
        if (propType.equals(Short.TYPE) || propType.equals(Short.class)) {
            return rs.getShort(index);
        }
        if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
            return rs.getByte(index);
        }
        if (propType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);
        }
        return rs.getObject(index);
    }
    
    private Object processColumn(final ResultSet rs, final int index, final Type jdbcType) throws SQLException {
        switch (jdbcType) {
            case Object: {
                return rs.getObject(index);
            }
            case Int: {
                return rs.getInt(index);
            }
            case Long: {
                return rs.getLong(index);
            }
            case Double: {
                return rs.getDouble(index);
            }
            case Float: {
                return rs.getFloat(index);
            }
            case String: {
                return rs.getString(index);
            }
            case Byte: {
                return rs.getByte(index);
            }
            case SqlDate: {
                return rs.getDate(index);
            }
            case Timestamp: {
                return rs.getTimestamp(index);
            }
            case Time: {
                return rs.getTime(index);
            }
            case BigDecimal: {
                return rs.getBigDecimal(index);
            }
            case Blob: {
                return rs.getBlob(index);
            }
            case Clob: {
                return rs.getClob(index);
            }
            case NClob: {
                return rs.getNClob(index);
            }
            case Bytes: {
                return rs.getBytes(index);
            }
            case Bool: {
                return rs.getBoolean(index);
            }
            default: {
                return rs.getObject(index);
            }
        }
    }
}
