package com.reign.framework.common.cache;

import org.springframework.orm.hibernate3.support.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.hibernate.metadata.*;
import java.io.*;

public class SDataLoader extends HibernateDaoSupport
{
    protected static final String SDATA_PATH_TOKEN = "${sdata.path}";
    
    public <E> List<E> getModels(final Class<E> clazz) {
        final String path = System.getProperty("${sdata.path}");
        if (StringUtils.isBlank(path)) {
            return this.getHibernateTemplate().find(this.getHQL(clazz));
        }
        final ClassMetadata data = this.getSessionFactory().getClassMetadata(clazz);
        final SDataXMLLoader loader = SDataXMLLoader.getInstance(path);
        loader.getModels(clazz, data);
        return loader.getModels(clazz, data);
    }
    
    public <E, PK extends Serializable> E read(final Class<E> clazz, final PK id) {
        return (E)this.getHibernateTemplate().get((Class)clazz, (Serializable)id);
    }
    
    public <E, PK extends Serializable> E load(final Class<E> clazz, final PK id) {
        return (E)this.getHibernateTemplate().load((Class)clazz, (Serializable)id);
    }
    
    public List<?> getResultByHQLAndParam(final String hql) {
        return this.getHibernateTemplate().find(hql);
    }
    
    public List<?> getResultByHQLAndParam(final String hql, final Object object) {
        return this.getHibernateTemplate().find(hql, object);
    }
    
    public List<?> getResultByHQLAndParam(final String hql, final Object[] object) {
        return this.getHibernateTemplate().find(hql, object);
    }
    
    private <E> String getHQL(final Class<E> clazz) {
        return "FROM " + clazz.getSimpleName();
    }
}
