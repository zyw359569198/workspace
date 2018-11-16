package com.reign.gcld.user.dao;

import com.reign.gcld.user.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("userRewardDao")
public class UserRewardDao extends BaseDao<UserReward> implements IUserRewardDao
{
    @Override
	public UserReward read(final int vId) {
        return (UserReward)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserReward.read", (Object)vId);
    }
    
    @Override
	public UserReward readForUpdate(final int vId) {
        return (UserReward)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserReward.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<UserReward> getModels() {
        return (List<UserReward>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserReward.getModelSize");
    }
    
    @Override
	public int create(final UserReward userReward) {
        return this.getSqlSession().insert("com.reign.gcld.user.domain.UserReward.create", userReward);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.user.domain.UserReward.deleteById", vId);
    }
    
    @Override
	public List<UserReward> getUserReward(final int userId, final String yx) {
        final Params param = new Params();
        param.addParam("userId", userId);
        param.addParam("yx", yx);
        return (List<UserReward>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserReward.getUserReward", (Object)param);
    }
}
