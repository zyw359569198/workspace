package com.reign.gcld.chat.dao;

import com.reign.gcld.chat.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("silenceDao")
public class SilenceDao extends BaseDao<Silence> implements ISilenceDao
{
    @Override
	public Silence read(final int silenceId) {
        return (Silence)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.Silence.read", (Object)silenceId);
    }
    
    @Override
	public Silence readForUpdate(final int silenceId) {
        return (Silence)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.Silence.readForUpdate", (Object)silenceId);
    }
    
    @Override
	public List<Silence> getModels() {
        return (List<Silence>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.Silence.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.Silence.getModelSize");
    }
    
    @Override
	public int create(final Silence silence) {
        return this.getSqlSession().insert("com.reign.gcld.chat.domain.Silence.create", silence);
    }
    
    @Override
	public int deleteById(final int silenceId) {
        return this.getSqlSession().delete("com.reign.gcld.chat.domain.Silence.deleteById", silenceId);
    }
    
    @Override
	public Silence getByPlayerIdAndYx(final int playerId, final String yx) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("yx", yx);
        return (Silence)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.Silence.getByPlayerIdAndYx", (Object)params);
    }
    
    @Override
	public int update(final Silence silence) {
        return this.getSqlSession().update("com.reign.gcld.chat.domain.Silence.update", silence);
    }
    
    @Override
	public Silence getByPlayerId(final int playerId) {
        return (Silence)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.Silence.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public List<Silence> getByDateAndYx(final Date date, final String yx, final int type) {
        final Params params = new Params();
        params.addParam("date", date).addParam("yx", yx).addParam("type", type);
        return (List<Silence>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.Silence.getByDateAndYx", (Object)params);
    }
}
