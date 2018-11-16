package com.reign.gcld.user.dao;

import com.reign.gcld.user.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("userDao")
public class UserDao extends BaseDao<User> implements IUserDao
{
    @Override
	public User read(final int id) {
        return (User)this.getSqlSession().selectOne("com.reign.gcld.user.domain.User.read", (Object)id);
    }
    
    @Override
	public User readForUpdate(final int id) {
        return (User)this.getSqlSession().selectOne("com.reign.gcld.user.domain.User.readForUpdate", (Object)id);
    }
    
    @Override
	public List<User> getModels() {
        return (List<User>)this.getSqlSession().selectList("com.reign.gcld.user.domain.User.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.user.domain.User.getModelSize");
    }
    
    @Override
	public int create(final User user) {
        return this.getSqlSession().insert("com.reign.gcld.user.domain.User.create", user);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.user.domain.User.deleteById", id);
    }
    
    @Override
	public User getUserByUserName(final String userName) {
        return (User)this.getSqlSession().selectOne("com.reign.gcld.user.domain.User.getUserByUserName", (Object)userName);
    }
    
    @Override
	public int updateRewardFroce(final int id, final int forceId) {
        final Params params = new Params();
        params.addParam("id", id).addParam("forceId", forceId);
        return this.getSqlSession().update("com.reign.gcld.user.domain.User.updateRewardFroce", params);
    }
}
