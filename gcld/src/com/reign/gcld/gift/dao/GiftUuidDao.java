package com.reign.gcld.gift.dao;

import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("giftUuidDao")
public class GiftUuidDao extends BaseDao<GiftUuid> implements IGiftUuidDao
{
    @Override
	public GiftUuid read(final int playerId) {
        return (GiftUuid)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftUuid.read", (Object)playerId);
    }
    
    @Override
	public GiftUuid readForUpdate(final int playerId) {
        return (GiftUuid)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftUuid.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<GiftUuid> getModels() {
        return (List<GiftUuid>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.GiftUuid.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftUuid.getModelSize");
    }
    
    @Override
	public int create(final GiftUuid giftUuid) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.GiftUuid.create", giftUuid);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.GiftUuid.deleteById", playerId);
    }
    
    @Override
	public int modifyUuid(final int playerId, final int uuid) {
        final Params params = new Params();
        params.addParam("playerId", "playerId");
        params.addParam("uuid", "uuid");
        return this.getSqlSession().update("com.reign.gcld.gift.domain.GiftUuid.modifyUuid", params);
    }
}
