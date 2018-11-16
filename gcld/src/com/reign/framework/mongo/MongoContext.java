package com.reign.framework.mongo;

import org.springframework.beans.factory.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.mongo.annotation.*;
import com.reign.framework.common.*;
import java.util.*;

public class MongoContext implements InitializingBean
{
    private String scanPackage;
    private Map<Class<?>, MongoEntity> entityMap;
    
    public MongoContext() {
        this.entityMap = new HashMap<Class<?>, MongoEntity>();
    }
    
    public void setScanPackage(final String scanPackage) {
        this.scanPackage = scanPackage;
    }
    
    public void init() {
        final Set<Class<?>> set = Scans.getClasses(this.getScanPackage());
        for (final Class<?> clazz : set) {
            final Domain domain = Lang.getAnnotation(clazz, Domain.class);
            if (domain != null) {
                final MongoEntity entity = MongoEntity.resolve(clazz);
                this.entityMap.put(clazz, entity);
            }
        }
    }
    
    public MongoEntity getMongoEntity(final Class<?> clazz) {
        return this.entityMap.get(clazz);
    }
    
    public String getScanPackage() {
        return this.scanPackage;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.init();
    }
}
