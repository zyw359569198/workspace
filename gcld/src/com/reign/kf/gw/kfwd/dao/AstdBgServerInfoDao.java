package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.hibernate.*;

@Component
public class AstdBgServerInfoDao extends DirectBaseDao<AstdBgServerInfo, Integer> implements IAstdBgServerInfoDao
{
    @Override
    public List<AstdBgServerInfo> getAll() {
        final String hql = "from AstdBgServerInfo";
        return (List<AstdBgServerInfo>)this.getResultByHQLAndParam(hql);
    }
    
    @Override
    public void deleteAll() {
        final String hql = "delete from AstdBgServerInfo";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.executeUpdate();
        this.releaseSession(session);
    }
}
