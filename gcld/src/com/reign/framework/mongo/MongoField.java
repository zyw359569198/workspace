package com.reign.framework.mongo;

import com.reign.framework.common.*;
import java.lang.reflect.*;
import com.reign.framework.jdbc.*;
import org.apache.commons.lang.*;

public class MongoField
{
    public Field field;
    public String fieldName;
    public Lang.ClassType type;
    public Method getter;
    public Method writter;
    public boolean isPrimary;
    public boolean insertIgnore;
    public Type jdbcType;
    
    public MongoField(final Lang.MyField field) {
        this.field = field.field;
        this.fieldName = StringUtils.capitalize(field.fieldName);
        this.type = field.type;
        this.getter = field.getter;
        this.writter = field.writter;
    }
}
