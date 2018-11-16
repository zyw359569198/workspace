package com.reign.gcld.general.dao;

import com.reign.gcld.general.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.chat.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;

@Component("playerGeneralCivilDao")
public class PlayerGeneralCivilDao extends BaseDao<PlayerGeneralCivil> implements IPlayerGeneralCivilDao
{
    @Autowired
    private BroadCastUtil broadCastUtil;
    @Autowired
    private GeneralCache generalCache;
    
    @Override
	public PlayerGeneralCivil read(final int vId) {
        return (PlayerGeneralCivil)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.read", (Object)vId);
    }
    
    @Override
	public PlayerGeneralCivil readForUpdate(final int vId) {
        return (PlayerGeneralCivil)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGeneralCivil> getModels() {
        return (List<PlayerGeneralCivil>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralCivil.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.getModelSize");
    }
    
    @Override
	public int create(final PlayerGeneralCivil playerGeneralCivil) {
        final int result = this.getSqlSession().insert("com.reign.gcld.general.domain.PlayerGeneralCivil.create", playerGeneralCivil);
        if (result > 0) {
            this.broadCastUtil.sendGainGeneralBroadCast(playerGeneralCivil.getPlayerId(), playerGeneralCivil.getGeneralId());
        }
        return result;
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.general.domain.PlayerGeneralCivil.deleteById", vId);
    }
    
    @Override
	public PlayerGeneralCivil getCivil(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerGeneralCivil)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivil", (Object)params);
    }
    
    @Override
	public List<PlayerGeneralCivil> getCivilList(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerGeneralCivil>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivilList", (Object)params);
    }
    
    @Override
	public List<PlayerGeneralCivil> getCivilListOrderByLv(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        final List<PlayerGeneralCivil> list = (List<PlayerGeneralCivil>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivilListOrderByLv", (Object)params);
        return this.sortByQuality(list);
    }
    
    @Override
	public List<PlayerGeneralCivil> getCivilAdviser(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerGeneralCivil>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivilAdviser", (Object)params);
    }
    
    @Override
	public int getCivilNum(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivilNum", (Object)params);
    }
    
    @Override
	public int getCivilOwnerNum(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralCivil.getCivilOwnerNum", (Object)playerId);
    }
    
    @Override
	public int updateUpTime(final int playerId, final Date updateTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("updateTime", updateTime);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.updateUpTime", params);
    }
    
    @Override
	public void search(final int playerId, final int generalId, final int state, final Date nextMoveDate, final int taskId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("state", state);
        params.addParam("nextMoveDate", nextMoveDate);
        params.addParam("taskId", taskId);
        this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.search", params);
    }
    
    @Override
	public int updateState(final int playerId, final int generalId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.updateStateByPidAndGid", params);
    }
    
    @Override
	public void updateMoveTime(final int vId, final Date date) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.updateMoveTime", params);
    }
    
    @Override
	public int updateExpAndGlv(final int playerId, final int generalId, final int curExp, final int addLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("curExp", curExp).addParam("addLv", addLv);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.updateExpAndGlv", params);
    }
    
    @Override
	public int addExp(final int playerId, final int generalId, final int addExp) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addExp", addExp);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addExp", params);
    }
    
    @Override
	public int addIntel(final int playerId, final int generalId, final int addIntel) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addIntel", addIntel);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addIntel", params);
    }
    
    @Override
	public int addPolitics(final int playerId, final int generalId, final int addPolitics) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addPolitics", addPolitics);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addPolitics", params);
    }
    
    @Override
	public int addIntelCareMax(final int playerId, final int generalId, final int addIntel, final int max) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addIntel", addIntel).addParam("max", max);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addIntelCareMax", params);
    }
    
    @Override
	public int addPoliticsCareMax(final int playerId, final int generalId, final int addPolitics, final int max) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addPolitics", addPolitics).addParam("max", max);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addPoliticsCareMax", params);
    }
    
    @Override
	public int addIntelAndPolitics(final int playerId, final int generalId, final int addIntel, final int addPolitics) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addIntel", addIntel).addParam("addPolitics", addPolitics);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.addIntelAndPolitics", params);
    }
    
    @Override
	public int consumeIntelAndPolitics(final int playerId, final int generalId, final int consumeIntel, final int consumePolitics) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("consumeIntel", consumeIntel).addParam("consumePolitics", consumePolitics);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.consumeIntelAndPolitics", params);
    }
    
    @Override
	public int setCivilLv(final int playerId, final int lv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("lv", lv);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.setCivilLv", params);
    }
    
    private List<PlayerGeneralCivil> sortByQuality(final List<PlayerGeneralCivil> list) {
        if (list.size() < 2) {
            return list;
        }
        int begin = 0;
        int end = 0;
        boolean find = false;
        PlayerGeneralCivil temp = null;
        for (int i = 0; i < list.size() - 1; ++i) {
            if (list.get(i).getLv().equals(list.get(i + 1).getLv())) {
                if (!find) {
                    begin = i;
                    find = true;
                }
                end = i + 1;
            }
            else if (find) {
                final int round = end - begin;
                for (int j = begin; j < end; ++j) {
                    for (int k = 0; k < round; ++k) {
                        if (((General)this.generalCache.get((Object)list.get(begin + k).getGeneralId())).getQuality() < ((General)this.generalCache.get((Object)list.get(begin + k + 1).getGeneralId())).getQuality()) {
                            temp = list.get(begin + k);
                            list.set(begin + k, list.get(begin + k + 1));
                            list.set(begin + k + 1, temp);
                        }
                    }
                }
                find = false;
            }
        }
        if (find) {
            final int round2 = end - begin;
            for (int l = begin; l < end; ++l) {
                for (int m = 0; m < round2; ++m) {
                    if (((General)this.generalCache.get((Object)list.get(begin + m).getGeneralId())).getQuality() < ((General)this.generalCache.get((Object)list.get(begin + m + 1).getGeneralId())).getQuality()) {
                        temp = list.get(begin + m);
                        list.set(begin + m, list.get(begin + m + 1));
                        list.set(begin + m + 1, temp);
                    }
                }
            }
        }
        return list;
    }
    
    @Override
	public void updateCd(final int playerId, final int vId, final Date date) {
        final Params params = new Params();
        params.addParam("id", playerId);
        params.addParam("generalId", vId);
        params.addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralCivil.updateCd", params);
    }
}
