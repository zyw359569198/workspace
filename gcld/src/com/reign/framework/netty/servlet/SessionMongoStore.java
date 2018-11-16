package com.reign.framework.netty.servlet;

import com.reign.framework.mongo.*;
import com.reign.framework.mongo.convert.*;
import com.reign.framework.mongo.lang.*;

@Deprecated
public class SessionMongoStore
{
    private MongoTemplate template;
    private DBCollection dbCollection;
    private AbstractConvert convert;
    
    public SessionMongoStore() {
        this.template = new MongoTemplate("Session");
        this.dbCollection = this.template.getDBCollection("session", true);
        this.convert = new DefaultConvert();
    }
    
    public void store(final Session session) {
    }
    
    public Session get(final String sessionId) {
        final DBCursor cursor = this.template.query(new Query().add(new Where("id", Op.eq, new Object[] { sessionId })), this.dbCollection);
        if (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            return this.convert.convert(dbObject, StandardSession.class);
        }
        return null;
    }
    
    public void update(final Update update, final Query query) {
        this.template.update(query, update, this.dbCollection);
    }
}
