package com.reign.gcld.user.dao;

import com.reign.gcld.user.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("userLoginInfoDao")
public class UserLoginInfoDao extends BaseDao<UserLoginInfo> implements IUserLoginInfoDao
{
    @Override
	public UserLoginInfo read(final int vId) {
        return (UserLoginInfo)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserLoginInfo.read", (Object)vId);
    }
    
    @Override
	public UserLoginInfo readForUpdate(final int vId) {
        return (UserLoginInfo)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserLoginInfo.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<UserLoginInfo> getModels() {
        return (List<UserLoginInfo>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserLoginInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserLoginInfo.getModelSize");
    }
    
    @Override
	public int create(final UserLoginInfo userLoginInfo) {
        return this.getSqlSession().insert("com.reign.gcld.user.domain.UserLoginInfo.create", userLoginInfo);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.user.domain.UserLoginInfo.deleteById", vId);
    }
    
    @Override
	public UserLoginInfo getUserLoginInfo(final String yx, final String userId) {
        final Params params = new Params();
        params.addParam("yx", yx);
        params.addParam("userId", userId);
        return (UserLoginInfo)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserLoginInfo.getUserLoginInfo", (Object)params);
    }
    
    @Override
	public void update(final UserLoginInfo uli) {
        this.getSqlSession().update("com.reign.gcld.user.domain.UserLoginInfo.update", uli);
    }
}
