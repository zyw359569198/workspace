package com.reign.framework.jdbc.orm;

import com.reign.framework.common.*;
import java.lang.reflect.*;
import com.reign.framework.jdbc.*;
import org.apache.commons.lang.*;

public class JdbcField
{
    public Field field;
    public String fieldName;
    public String propertyName;
    public String columnName;
    public Lang.ClassType type;
    public Method getter;
    public Method writter;
    public boolean isPrimary;
    public boolean insertIgnore;
    public boolean ignore;
    public Type jdbcType;
    
    public JdbcField(final Lang.MyField field, final NameStrategy strategy) {
        this.field = field.field;
        this.fieldName = StringUtils.capitalize(field.fieldName);
        this.propertyName = field.fieldName;
        this.columnName = strategy.propertyNameToColumnName(this.fieldName);
        this.type = field.type;
        this.getter = field.getter;
        this.writter = field.writter;
    }
}
