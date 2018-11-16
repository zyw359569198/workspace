package com.reign.framework.mongo.lang;

import java.util.*;

public class UpdateParse
{
    public static DBObject parse(final Update update) {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        for (final Update u : update.getUpdate()) {
            if (u instanceof UpdateField) {
                final UpdateField field = (UpdateField)u;
                _parse(dbObject, field);
            }
        }
        return dbObject;
    }
    
    private static void _parse(final DBObject dbObject, final UpdateField field) {
        final DBObject temp = (DBObject)new BasicDBObject();
        switch (field.op) {
            case each: {
                temp.put(field.op.getValue(), (Object)field.column);
                dbObject.put(field.column, (Object)temp);
                break;
            }
            case addToSet: {
                if (field.value[0] instanceof UpdateField) {
                    final DBObject temp2 = (DBObject)new BasicDBObject();
                    _parse(temp2, (UpdateField)field.value[0]);
                    dbObject.put(field.op.getValue(), (Object)temp2);
                    break;
                }
            }
            case pushAll:
            case pullAll: {
                temp.put(field.column, (Object)field.value);
                dbObject.put(field.op.getValue(), (Object)temp);
                break;
            }
            default: {
                temp.put(field.column, field.value[0]);
                dbObject.put(field.op.getValue(), (Object)temp);
                break;
            }
        }
    }
}
