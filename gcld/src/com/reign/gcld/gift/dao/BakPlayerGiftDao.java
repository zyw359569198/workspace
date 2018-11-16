package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("bakPlayerGiftDao")
public class BakPlayerGiftDao extends BaseDao<BakPlayerGift> implements IBakPlayerGiftDao
{
    @Override
	public BakPlayerGift read(final int bakId) {
        return (BakPlayerGift)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakPlayerGift.read", (Object)bakId);
    }
    
    @Override
	public BakPlayerGift readForUpdate(final int bakId) {
        return (BakPlayerGift)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakPlayerGift.readForUpdate", (Object)bakId);
    }
    
    @Override
	public List<BakPlayerGift> getModels() {
        return (List<BakPlayerGift>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.BakPlayerGift.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakPlayerGift.getModelSize");
    }
    
    @Override
	public int create(final BakPlayerGift bakPlayerGift) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.BakPlayerGift.create", bakPlayerGift);
    }
    
    @Override
	public int deleteById(final int bakId) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.BakPlayerGift.deleteById", bakId);
    }
}
