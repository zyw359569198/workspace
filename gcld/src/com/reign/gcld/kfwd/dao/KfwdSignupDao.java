package com.reign.gcld.kfwd.dao;

import com.reign.gcld.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfwdSignupDao")
public class KfwdSignupDao extends BaseDao<KfwdSignup> implements IKfwdSignupDao
{
    @Override
	public KfwdSignup read(final int pk) {
        return (KfwdSignup)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdSignup.read", (Object)pk);
    }
    
    @Override
	public KfwdSignup readForUpdate(final int pk) {
        return (KfwdSignup)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdSignup.readForUpdate", (Object)pk);
    }
    
    @Override
	public List<KfwdSignup> getModels() {
        return (List<KfwdSignup>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdSignup.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdSignup.getModelSize");
    }
    
    @Override
	public int create(final KfwdSignup kfwdSignup) {
        return this.getSqlSession().insert("com.reign.gcld.kfwd.domain.KfwdSignup.create", kfwdSignup);
    }
    
    @Override
	public int deleteById(final int pk) {
        return this.getSqlSession().delete("com.reign.gcld.kfwd.domain.KfwdSignup.deleteById", pk);
    }
    
    @Override
	public List<KfwdSignup> getSignUpInfoBySeasonIdAndSchduleId(final int seasonId, final int scheduleId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("scheduleId", scheduleId);
        return (List<KfwdSignup>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdSignup.getSignUpInfoBySeasonIdAndSchduleId", (Object)params);
    }
}
