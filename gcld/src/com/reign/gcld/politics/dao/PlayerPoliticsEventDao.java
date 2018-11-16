package com.reign.gcld.politics.dao;

import com.reign.gcld.politics.domain.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.common.*;
import com.reign.framework.jdbc.*;
import java.util.*;

@Component("playerPoliticsEventDao")
public class PlayerPoliticsEventDao extends BaseDao<PlayerPoliticsEvent> implements IPlayerPoliticsEventDao
{
    @Autowired
    private IBatchExecute batchExecute;
    
    @Override
	public PlayerPoliticsEvent read(final int playerId) {
        return (PlayerPoliticsEvent)this.getSqlSession().selectOne("com.reign.gcld.politics.domain.PlayerPoliticsEvent.read", (Object)playerId);
    }
    
    @Override
	public PlayerPoliticsEvent readForUpdate(final int playerId) {
        return (PlayerPoliticsEvent)this.getSqlSession().selectOne("com.reign.gcld.politics.domain.PlayerPoliticsEvent.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerPoliticsEvent> getModels() {
        return (List<PlayerPoliticsEvent>)this.getSqlSession().selectList("com.reign.gcld.politics.domain.PlayerPoliticsEvent.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.politics.domain.PlayerPoliticsEvent.getModelSize");
    }
    
    @Override
	public int create(final PlayerPoliticsEvent playerPoliticsEvent) {
        return this.getSqlSession().insert("com.reign.gcld.politics.domain.PlayerPoliticsEvent.create", playerPoliticsEvent);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.politics.domain.PlayerPoliticsEvent.deleteById", playerId);
    }
    
    @Override
	public int getEventNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.politics.domain.PlayerPoliticsEvent.getEventNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int addPoliticEventNum(final int playerId, final int num, final Date now) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("num", (Object)num).addParam("now", (Object)now);
        return this.getSqlSession().update("com.reign.gcld.politics.domain.PlayerPoliticsEvent.addPoliticEventNum", (Object)params);
    }
    
    @Override
	public int minusePoliticEventNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.politics.domain.PlayerPoliticsEvent.minusePoliticEventNum", playerId);
    }
    
    @Override
	public int addPeopleLoyal(final int playerId, final int peopleLoyal, final int max) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("peopleLoyal", (Object)peopleLoyal).addParam("max", (Object)max);
        return this.getSqlSession().update("com.reign.gcld.politics.domain.PlayerPoliticsEvent.addPeopleLoyal", (Object)params);
    }
    
    @Override
	public int resetPeopleLoyal(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.politics.domain.PlayerPoliticsEvent.resetPeopleLoyal", playerId);
    }
    
    @Override
	public int getPeopleLoyal(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.politics.domain.PlayerPoliticsEvent.getPeopleLoyal", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<Integer> getByPlayerIdsAndNum(final List<Integer> playerIds, final int num) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("playerList", playerIds);
        map.put("num", num);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.politics.domain.PlayerPoliticsEvent.getByPlayerIdsAndNum", (Object)map);
    }
    
    @Override
	public int addPoliticsNum(final List<Integer> playerIds, final int num, final Date date) {
        final String sql = "UPDATE PLAYER_POLITICS_EVENT SET POLITICS_EVENT_NUM = POLITICS_EVENT_NUM + " + num + ", LAST_EVENT_TIME = '" + TimeUtil.getMysqlDateString(date) + "' WHERE PLAYER_ID = ?";
        final List<List<Param>> paramsList = new ArrayList<List<Param>>();
        for (final Integer playerId : playerIds) {
            final List<Param> params = new ArrayList<Param>();
            params.add(new Param(playerId, Type.Int));
            paramsList.add(params);
        }
        return this.batchExecute.batch(this.getSqlSession(), sql, paramsList);
    }
}
