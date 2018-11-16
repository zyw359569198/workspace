package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("searchComboEventDao")
public class SearchComboEventDao extends BaseDao<SearchComboEvent> implements ISearchComboEventDao
{
    @Override
	public SearchComboEvent read(final int comboPos) {
        return (SearchComboEvent)this.getSqlSession().selectOne("com.reign.gcld.world.domain.SearchComboEvent.read", (Object)comboPos);
    }
    
    @Override
	public SearchComboEvent readForUpdate(final int comboPos) {
        return (SearchComboEvent)this.getSqlSession().selectOne("com.reign.gcld.world.domain.SearchComboEvent.readForUpdate", (Object)comboPos);
    }
    
    @Override
	public List<SearchComboEvent> getModels() {
        return (List<SearchComboEvent>)this.getSqlSession().selectList("com.reign.gcld.world.domain.SearchComboEvent.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.SearchComboEvent.getModelSize");
    }
    
    @Override
	public int create(final SearchComboEvent searchComboEvent) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.SearchComboEvent.create", searchComboEvent);
    }
    
    @Override
	public int deleteById(final int comboPos) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.SearchComboEvent.deleteById", comboPos);
    }
    
    @Override
	public void updateInfo(final int pos, final String info, final int revealed) {
        final Params params = new Params();
        params.addParam("pos", pos);
        params.addParam("info", info);
        params.addParam("revealed", revealed);
        this.getSqlSession().update("com.reign.gcld.world.domain.SearchComboEvent.updateInfo", params);
    }
    
    @Override
	public void revealEvent(final int pos) {
        this.getSqlSession().update("com.reign.gcld.world.domain.SearchComboEvent.revealEvent", pos);
    }
}
