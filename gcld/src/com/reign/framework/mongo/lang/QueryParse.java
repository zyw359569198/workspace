package com.reign.framework.mongo.lang;

import java.util.*;

public class QueryParse
{
    public static DBObject parse(final Query query) {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        for (final Query q : query.getQuery()) {
            _parse(dbObject, q);
        }
        return dbObject;
    }
    
    private static void _parse(final DBObject dbObject, final Query query) {
        if (query instanceof Where) {
            parseWhere((Where)query, dbObject);
        }
        else if (query instanceof Or) {
            final Or or = (Or)query;
            parseOr(or, dbObject);
        }
    }
    
    private static void parseOr(final Or or, final DBObject dbObject) {
        final BasicDBList dbObjectList = new BasicDBList();
        for (final Query query : or.getQuery()) {
            final DBObject temp = (DBObject)new BasicDBObject();
            _parse(temp, query);
            dbObjectList.add((Object)temp);
        }
        dbObject.put("$or", (Object)dbObjectList);
    }
    
    private static void parseWhere(final Where where, final DBObject dbObject) {
        switch (where.op) {
            case eq: {
                dbObject.put(where.column, where.value[0]);
                break;
            }
            case mod:
            case all:
            case in:
            case nin: {
                final DBObject temp = (DBObject)new BasicDBObject();
                temp.put(where.op.getValue(), (Object)where.value);
                dbObject.put(where.column, (Object)temp);
                break;
            }
            default: {
                final DBObject temp2 = (DBObject)new BasicDBObject();
                temp2.put(where.op.getValue(), where.value[0]);
                dbObject.put(where.column, (Object)temp2);
                break;
            }
        }
    }
}
