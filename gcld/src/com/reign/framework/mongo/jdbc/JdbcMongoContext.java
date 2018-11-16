package com.reign.framework.mongo.jdbc;

import org.springframework.beans.factory.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.mongo.annotation.*;
import com.reign.framework.common.*;
import com.reign.framework.jdbc.*;
import java.util.*;

public class JdbcMongoContext implements InitializingBean
{
    private String scanPackage;
    private Map<Class<?>, JdbcEntity> entityMap;
    
    public JdbcMongoContext() {
        this.entityMap = new HashMap<Class<?>, JdbcEntity>();
    }
    
    public void setScanPackage(final String scanPackage) {
        this.scanPackage = scanPackage;
    }
    
    public void init() {
        final Set<Class<?>> set = Scans.getClasses(this.getScanPackage());
        for (final Class<?> clazz : set) {
            final Domain domain = Lang.getAnnotation(clazz, Domain.class);
            if (domain != null) {
                final JdbcEntity entity = JdbcEntity.resolve(clazz, new DefaultNameStrategy());
                this.entityMap.put(clazz, entity);
            }
        }
    }
    
    public JdbcEntity getJdbcEntity(final Class<?> clazz) {
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
