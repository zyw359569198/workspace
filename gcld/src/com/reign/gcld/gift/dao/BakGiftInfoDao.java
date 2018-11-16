package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("bakGiftInfoDao")
public class BakGiftInfoDao extends BaseDao<BakGiftInfo> implements IBakGiftInfoDao
{
    @Override
	public BakGiftInfo read(final int bakId) {
        return (BakGiftInfo)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakGiftInfo.read", (Object)bakId);
    }
    
    @Override
	public BakGiftInfo readForUpdate(final int bakId) {
        return (BakGiftInfo)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakGiftInfo.readForUpdate", (Object)bakId);
    }
    
    @Override
	public List<BakGiftInfo> getModels() {
        return (List<BakGiftInfo>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.BakGiftInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.BakGiftInfo.getModelSize");
    }
    
    @Override
	public int create(final BakGiftInfo bakGiftInfo) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.BakGiftInfo.create", bakGiftInfo);
    }
    
    @Override
	public int deleteById(final int bakId) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.BakGiftInfo.deleteById", bakId);
    }
}
