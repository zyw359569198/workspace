package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("cityDefenceNpcDao")
public class CityDefenceNpcDao extends BaseDao<CityDefenceNpc> implements ICityDefenceNpcDao
{
    @Override
	public CityDefenceNpc read(final int cityId) {
        return (CityDefenceNpc)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.CityDefenceNpc.read", (Object)cityId);
    }
    
    @Override
	public CityDefenceNpc readForUpdate(final int cityId) {
        return (CityDefenceNpc)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.CityDefenceNpc.readForUpdate", (Object)cityId);
    }
    
    @Override
	public List<CityDefenceNpc> getModels() {
        return (List<CityDefenceNpc>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.CityDefenceNpc.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.CityDefenceNpc.getModelSize");
    }
    
    @Override
	public int create(final CityDefenceNpc cityDefenceNpc) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.CityDefenceNpc.create", cityDefenceNpc);
    }
    
    @Override
	public int deleteById(final int cityId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.CityDefenceNpc.deleteById", cityId);
    }
    
    @Override
	public int updateDefenceNpc(final int cityId, final CityDefenceNpc cityDefenceNpc) {
        final Params params = new Params();
        params.addParam("cityId", cityId).addParam("pLv", cityDefenceNpc.getPlayerLv()).addParam("gId", cityDefenceNpc.getGeneralId()).addParam("gLv", cityDefenceNpc.getGeneralLv()).addParam("troopId", cityDefenceNpc.getTroopId()).addParam("str", cityDefenceNpc.getStrength()).addParam("lead", cityDefenceNpc.getLeader()).addParam("att", cityDefenceNpc.getAtt()).addParam("def", cityDefenceNpc.getDef()).addParam("hp", cityDefenceNpc.getHp()).addParam("colNum", cityDefenceNpc.getColumnNum()).addParam("attB", cityDefenceNpc.getAttB()).addParam("defB", cityDefenceNpc.getDefB()).addParam("tacticAtt", cityDefenceNpc.getTacticAtt()).addParam("tacticDef", cityDefenceNpc.getTacticDef());
        return this.getSqlSession().update("com.reign.gcld.battle.domain.CityDefenceNpc.updateDefenceNpc", params);
    }
}
