package com.reign.gcld.grouparmy.dao;

import com.reign.gcld.grouparmy.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("groupArmyDao")
public class GroupArmyDao extends BaseDao<GroupArmy> implements IGroupArmyDao
{
    @Override
	public GroupArmy read(final int id) {
        return (GroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.GroupArmy.read", (Object)id);
    }
    
    @Override
	public GroupArmy readForUpdate(final int id) {
        return (GroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.GroupArmy.readForUpdate", (Object)id);
    }
    
    @Override
	public List<GroupArmy> getModels() {
        return (List<GroupArmy>)this.getSqlSession().selectList("com.reign.gcld.grouparmy.domain.GroupArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.GroupArmy.getModelSize");
    }
    
    @Override
	public int create(final GroupArmy groupArmy) {
        return this.getSqlSession().insert("com.reign.gcld.grouparmy.domain.GroupArmy.create", groupArmy);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.grouparmy.domain.GroupArmy.deleteById", id);
    }
    
    @Override
	public List<GroupArmy> getByNowCity(final int cityId) {
        return (List<GroupArmy>)this.getSqlSession().selectList("com.reign.gcld.grouparmy.domain.GroupArmy.getByNowCity", (Object)cityId);
    }
    
    @Override
	public void updateLeader(final int id, final int playerId) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("playerId", playerId);
        this.getSqlSession().update("com.reign.gcld.grouparmy.domain.GroupArmy.updateLeader", params);
    }
    
    @Override
	public void updateSpeed(final int id, final float speed) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("speed", speed);
        this.getSqlSession().update("com.reign.gcld.grouparmy.domain.GroupArmy.updateSpeed", params);
    }
    
    @Override
	public GroupArmy getBy2Id(final int leaderId, final int generalId) {
        final Params params = new Params();
        params.addParam("leaderId", leaderId);
        params.addParam("generalId", generalId);
        return (GroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.GroupArmy.getBy2Id", (Object)params);
    }
    
    @Override
	public int updateNowCity(final int armyId, final int cityId) {
        final Params params = new Params();
        params.addParam("armyId", armyId);
        params.addParam("cityId", cityId);
        return this.getSqlSession().update("com.reign.gcld.grouparmy.domain.GroupArmy.updateNowCity", params);
    }
    
    @Override
	public List<GroupArmy> getByLeaderId(final int leaderId) {
        return (List<GroupArmy>)this.getSqlSession().selectList("com.reign.gcld.grouparmy.domain.GroupArmy.getByLeaderId", (Object)leaderId);
    }
    
    @Override
	public int deleteByLeaderId(final int leaderId) {
        return this.getSqlSession().delete("com.reign.gcld.grouparmy.domain.GroupArmy.deleteByLeaderId", leaderId);
    }
}
