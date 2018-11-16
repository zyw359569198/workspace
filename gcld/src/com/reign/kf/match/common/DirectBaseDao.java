package com.reign.kf.match.common;

import com.reign.framework.hibernate.model.*;
import java.io.*;
import org.springframework.orm.hibernate3.support.*;
import com.reign.framework.hibernate.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.lang.reflect.*;
import org.hibernate.criterion.*;
import com.reign.framework.hibernate.page.*;
import org.hibernate.*;
import java.util.*;

public class DirectBaseDao<T extends IModel, PK extends Serializable> extends HibernateDaoSupport implements IBaseDao<T, PK>
{
    @Autowired
    @Qualifier("sessionFactory")
    public SessionFactory sessionFactory;
    private Class<T> clazz;
    
    public DirectBaseDao() {
        try {
            this.clazz = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        catch (Exception e) {
            this.clazz = (Class<T>)this.getClass().getGenericSuperclass();
        }
    }
    
    @Override
	public PK create(final T o) {
        return (PK)this.getHibernateTemplate().save(o);
    }
    
    @Override
	public T read(final PK id) {
        return (T)this.getHibernateTemplate().get((Class)this.clazz, (Serializable)id);
    }
    
    @Override
	public T loadForUpdate(final PK id) {
        return (T)this.getHibernateTemplate().load((Class)this.clazz, (Serializable)id, LockMode.UPGRADE_NOWAIT);
    }
    
    @Override
	public T readForUpdate(final PK id) {
        return (T)this.getHibernateTemplate().get((Class)this.clazz, (Serializable)id, LockMode.UPGRADE_NOWAIT);
    }
    
    @Override
	public T load(final PK id) {
        return (T)this.getHibernateTemplate().load((Class)this.clazz, (Serializable)id);
    }
    
    @Override
	public void update(final T o) {
        this.getHibernateTemplate().update(o);
    }
    
    @Override
	public void delete(final T o) {
        this.getHibernateTemplate().delete(o);
    }
    
    @Override
	public void saveOrUpdate(final T newInstance) {
        this.getHibernateTemplate().saveOrUpdate(newInstance);
    }
    
    @Override
	public void deleteById(final PK id) {
        final T o = this.read(id);
        if (o != null) {
            this.getHibernateTemplate().delete(o);
        }
    }
    
    @Override
	public List<T> getModels() {
        this.getHibernateTemplate().setCacheQueries(true);
        return this.getHibernateTemplate().loadAll((Class)this.clazz);
    }
    
    @Override
	public Long getModelSize() {
        final String hql = "select count(*) from " + this.clazz.getSimpleName();
        final List list = this.getResultByHQLAndParam(hql);
        return list.get(0);
    }
    
    @Override
	public List<T> getModelByPage(T exampleEntity, final int begin, final int count) {
        int first = begin;
        int size = count;
        if (first < 0) {
            first = 1;
        }
        if (size < 0) {
            size = 1;
        }
        List<T> ret = null;
        if (exampleEntity == null) {
            try {
                exampleEntity = this.clazz.newInstance();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }
        this.getHibernateTemplate().setCacheQueries(true);
        ret = this.getHibernateTemplate().findByExample(exampleEntity, first, size);
        return ret;
    }
    
    @Override
	public List<T> getModelByHibernateCriteria(final DetachedCriteria criteria) {
        List<T> list = null;
        if (criteria != null) {
            list = this.getHibernateTemplate().findByCriteria(criteria);
        }
        return list;
    }
    
    @Override
	public List<T> getModelByHibernateCriteria(final DetachedCriteria criteria, final int begin, final int count) {
        int first = begin;
        int size = count;
        if (first < 0) {
            first = 1;
        }
        if (size < 0) {
            size = 1;
        }
        List<T> list = null;
        if (criteria != null) {
            list = this.getHibernateTemplate().findByCriteria(criteria, first, size);
        }
        return list;
    }
    
    @Override
	public List<?> getResultByHQLAndParam(final String hql) {
        this.getHibernateTemplate().setCacheQueries(true);
        return this.getHibernateTemplate().find(hql);
    }
    
    @Override
	public T getFirstResultByHQLAndParam(final String hql) {
        this.getHibernateTemplate().setCacheQueries(true);
        final List<T> resultList = this.getHibernateTemplate().find(hql);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
	public List<?> getResultByHQLAndParam(final String hql, final Object... object) {
        this.getHibernateTemplate().setCacheQueries(true);
        return this.getHibernateTemplate().find(hql, object);
    }
    
    @Override
	public T getFirstResultByHQLAndParam(final String hql, final Object... object) {
        this.getHibernateTemplate().setCacheQueries(true);
        final List<T> resultList = this.getHibernateTemplate().find(hql, object);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
	public List<?> getResultByHQLAndParam(final String hql, final PagingData page, final Object... object) {
        final Session session = this.getSession();
        final StringBuffer counterHql = new StringBuffer();
        final int fromIndex = hql.toUpperCase().indexOf("FROM");
        counterHql.append("SELECT count(*) ").append(hql.substring(fromIndex)).append("");
        final Query counterQuery = session.createQuery(counterHql.toString());
        counterQuery.setCacheable(true);
        this.prepareQuery(counterQuery, object);
        final Long counter = counterQuery.iterate().next();
        page.setRowsCount((int)(Object)counter);
        page.setPagesCount();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        this.prepareQuery(query, object);
        query.setFirstResult(page.getCurrentPage() * page.getRowsPerPage());
        query.setMaxResults(page.getRowsPerPage());
        final List<?> resultList = query.list();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public List<?> getResultByHQLAndParamNoUpdate(final String hql, final PagingData page, final Object... object) {
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        this.prepareQuery(query, object);
        query.setFirstResult(page.getCurrentPage() * page.getRowsPerPage());
        query.setMaxResults(page.getRowsPerPage());
        final List<?> resultList = query.list();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public List<?> getResultByHQLAndParamForUpdate(final String hql, final String alias) {
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setLockMode(alias, LockMode.UPGRADE_NOWAIT);
        final List<?> resultList = query.list();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public List<?> getResultByHQLAndParamForUpdate(final String hql, final String alias, final Object... object) {
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        this.prepareQuery(query, object);
        query.setLockMode(alias, LockMode.UPGRADE_NOWAIT);
        final List<?> resultList = query.list();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public Iterator<?> getIteratorByHQLAndParam(final String hql) {
        return this.getHibernateTemplate().iterate(hql);
    }
    
    @Override
	public T getFirstIteratorByHQLAndParam(final String hql) {
        final Iterator<T> it = this.getHibernateTemplate().iterate(hql);
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
    
    @Override
	public Iterator<?> getIteratorByHQLAndParam(final String hql, final Object... object) {
        return this.getHibernateTemplate().iterate(hql, object);
    }
    
    @Override
	public T getFirstIteratorByHQLAndParam(final String hql, final Object... object) {
        final Iterator<T> it = this.getHibernateTemplate().iterate(hql, object);
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
    
    @Override
	public Iterator<?> getIteratorByHQLAndParam(final String hql, final PagingData page, final Object... object) {
        final Session session = this.getSession();
        final StringBuffer counterHql = new StringBuffer();
        final int fromIndex = hql.toUpperCase().indexOf("FROM");
        counterHql.append("SELECT count(*) ").append(hql.substring(fromIndex)).append("");
        final Query counterQuery = session.createQuery(counterHql.toString());
        counterQuery.setCacheable(true);
        this.prepareQuery(counterQuery, object);
        final Long counter = counterQuery.iterate().next();
        if (page.getCurrentPage() > 0 && page.getShift() > 0) {
            page.setRowsCount((int)(Object)counter + page.getShift());
        }
        else {
            page.setRowsCount((int)(Object)counter);
        }
        page.setPagesCount();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        this.prepareQuery(query, object);
        query.setFirstResult(page.getCurrentPage() * page.getRowsPerPage() - page.getShift());
        query.setMaxResults(page.getRowsPerPage());
        final Iterator<?> resultList = query.iterate();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public Iterator<?> getIteratorByHQLAndParamNoUpdate(final String hql, final PagingData page, final Object... object) {
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        this.prepareQuery(query, object);
        query.setFirstResult(page.getCurrentPage() * page.getRowsPerPage() - page.getShift());
        query.setMaxResults(page.getRowsPerPage());
        final Iterator<?> resultList = query.iterate();
        this.releaseSession(session);
        return resultList;
    }
    
    @Override
	public Session getCurrentSession() {
        return this.getSession();
    }
    
    @Override
	public void evict(final T o) {
        this.getCurrentSession().evict(o);
    }
    
    @Override
	public void evict(final PK pk) {
        this.getSessionFactory().getCache().evictEntity(this.clazz, pk);
    }
    
    @Override
	public void evictAll() {
        this.getSessionFactory().getCache().evictEntityRegion(this.clazz);
    }
    
    private void prepareQuery(final Query query, final Object[] args) {
        int i = 0;
        if (args != null) {
            for (final Object o : args) {
                query.setParameter(i, o);
                ++i;
            }
        }
    }
}
