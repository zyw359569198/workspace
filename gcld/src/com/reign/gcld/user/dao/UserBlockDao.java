package com.reign.gcld.user.dao;

import com.reign.gcld.user.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("userBlockDao")
public class UserBlockDao extends BaseDao<UserBlock> implements IUserBlockDao
{
    @Override
	public UserBlock read(final int vId) {
        return (UserBlock)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserBlock.read", (Object)vId);
    }
    
    @Override
	public UserBlock readForUpdate(final int vId) {
        return (UserBlock)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserBlock.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<UserBlock> getModels() {
        return (List<UserBlock>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserBlock.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.user.domain.UserBlock.getModelSize");
    }
    
    @Override
	public int create(final UserBlock userBlock) {
        return this.getSqlSession().insert("com.reign.gcld.user.domain.UserBlock.create", userBlock);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.user.domain.UserBlock.deleteById", vId);
    }
    
    @Override
	public List<UserBlock> getUserBlock(final String userId, final String yx) {
        final Params param = new Params();
        param.addParam("userId", userId);
        param.addParam("yx", yx);
        return (List<UserBlock>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserBlock.getUserBlock", (Object)param);
    }
    
    @Override
	public void update(final int vId, final String reason, final Date endTime) {
        final Params param = new Params();
        param.addParam("vId", vId);
        param.addParam("reason", reason);
        param.addParam("endTime", endTime);
        this.getSqlSession().update("com.reign.gcld.user.domain.UserBlock.update", param);
    }
    
    @Override
	public List<UserBlock> getUserBanListByDateAndYx(final Date date, final String yx) {
        final Params param = new Params();
        param.addParam("date", date);
        param.addParam("yx", yx);
        return (List<UserBlock>)this.getSqlSession().selectList("com.reign.gcld.user.domain.UserBlock.getUserBanListByDateAndYx", (Object)param);
    }
}
