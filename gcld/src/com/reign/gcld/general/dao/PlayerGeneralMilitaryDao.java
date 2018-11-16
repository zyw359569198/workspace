package com.reign.gcld.general.dao;

import com.reign.gcld.general.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.common.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;

@Component("playerGeneralMilitaryDao")
public class PlayerGeneralMilitaryDao extends BaseDao<PlayerGeneralMilitary> implements IPlayerGeneralMilitaryDao
{
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private GeneralCache generalCache;
    
    @Override
	public PlayerGeneralMilitary read(final int vId) {
        return (PlayerGeneralMilitary)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.read", (Object)vId);
    }
    
    @Override
	public PlayerGeneralMilitary readForUpdate(final int vId) {
        return (PlayerGeneralMilitary)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getModels() {
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.getModelSize");
    }
    
    @Override
	public int create(final PlayerGeneralMilitary playerGeneralMilitary) {
        this.dataGetter.getCourtesyService().addPlayerEvent(playerGeneralMilitary.getPlayerId(), 3, 0);
        return this.getSqlSession().insert("com.reign.gcld.general.domain.PlayerGeneralMilitary.create", playerGeneralMilitary);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.general.domain.PlayerGeneralMilitary.deleteById", vId);
    }
    
    @Override
	public PlayerGeneralMilitary getMilitary(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerGeneralMilitary)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitary", (Object)params);
    }
    
    @Override
	public PlayerGeneralMilitary getMaxLvMilitary(final int forceId) {
        return (PlayerGeneralMilitary)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMaxLvMilitary", (Object)forceId);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getMilitaryList(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryList", (Object)params);
    }
    
    @Override
	public Map<Integer, PlayerGeneralMilitary> getMilitaryMap(final int playerId) {
        return (Map<Integer, PlayerGeneralMilitary>)this.getSqlSession().selectMap("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryMap", (Object)playerId, "generalId");
    }
    
    @Override
	public Map<Integer, Object> getCityGenrealNum() {
        return (Map<Integer, Object>)this.getSqlSession().selectMap("com.reign.gcld.general.domain.PlayerGeneralMilitary.getCityGenrealNum", "locationId");
    }
    
    @Override
	public List<PlayerGeneralMilitary> getMilitaryListOrder(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        final List<PlayerGeneralMilitary> list = (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryListOrder", (Object)params);
        return this.sortByQuality(list);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getMilitaryByState(final int playerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("state", state);
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getListByState", (Object)params);
    }
    
    @Override
	public int updateState(final int vId, final Date date, final int state, final int orgState) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("date", date).addParam("state", state).addParam("orgState", orgState);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateWithDate", params);
    }
    
    @Override
	public int updateStateAuto(final int vId, final Date date, final int state, final int auto, final int orgState) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("date", date).addParam("state", state).addParam("auto", auto).addParam("orgState", orgState);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateAuto", params);
    }
    
    @Override
	public int updateState(final int vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateState", params);
    }
    
    @Override
	public int updateStateByPidAndGid(final int playerId, final int generalId, final int state, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateByPidAndGid", params);
    }
    
    @Override
	public int updateByLocationAndState3(final int locationId, final int state, final Date date) {
        final Params params = new Params();
        params.addParam("locationId", locationId).addParam("state", state).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateByLocationAndState3", params);
    }
    
    @Override
	public int updateStateCheck(final int playerId, final int generalId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateCheck", params);
    }
    
    @Override
	public int updateAutoRecruit(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateAutoRecruit", params);
    }
    
    @Override
	public int updateStateCityCheck(final int playerId, final int generalId, final int state, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateCityCheck", params);
    }
    
    @Override
	public int updateStateLocationId(final int playerId, final int generalId, final int state, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateStateLocationId", params);
    }
    
    @Override
	public int getMilitaryNum(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryNum", (Object)params);
    }
    
    @Override
	public int updateExpAndGlv(final int playerId, final int generalId, final int curExp, final int addLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("curExp", curExp).addParam("addLv", addLv);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateExpAndGlv", params);
    }
    
    @Override
	public int addExp(final int playerId, final int generalId, final int addExp) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addExp", addExp);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addExp", params);
    }
    
    @Override
	public int updateAllState() {
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateAllState");
    }
    
    @Override
	public int move(final int playerId, final int generalId, final int state, final int cityId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("state", state);
        params.addParam("cityId", cityId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.move", params);
    }
    
    @Override
	public int attack(final int playerId, final int generalId, final int state, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("state", state);
        params.addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.attack", params);
    }
    
    @Override
	public int restartRecruit(final int playerId, final int generalId, final int state, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("state", state).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.restartRecruit", params);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getMilitaryByLocationId(final int locationId) {
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryByLocationId", (Object)locationId);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getByLocationAndState3(final int locationId) {
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getByLocationAndState3", (Object)locationId);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getMilitaryByLocationIdOrderByPlayerIdLvDesc(final int locationId) {
        return (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getMilitaryByLocationIdOrderByPlayerIdLvDesc", (Object)locationId);
    }
    
    @Override
	public int countMilitaryByState(final int playerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("state", state);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.countMilitaryByState", (Object)params);
    }
    
    @Override
	public int updateLocationId(final int playerId, final int generalId, final int locationId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateLocationId", params);
    }
    
    @Override
	public int updateAuto(final int playerId, final int generalId, final int auto) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("auto", auto);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateAuto", params);
    }
    
    @Override
	public int addLeader(final int playerId, final int generalId, final int addLeader) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addLeader", addLeader);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addLeader", params);
    }
    
    @Override
	public int addStrength(final int playerId, final int generalId, final int addStrength) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addStrength", addStrength);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addStrength", params);
    }
    
    @Override
	public int updateGlv(final int playerId, final int addLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addLv", addLv);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateGlv", params);
    }
    
    @Override
	public int SetGlv(final int playerId, final int lv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("lv", lv);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.SetGlv", params);
    }
    
    @Override
	public int addLeaderAndStrength(final int playerId, final int generalId, final int addLeader, final int addStrength) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addLeader", addLeader).addParam("addStrength", addStrength);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addLeaderAndStrength", params);
    }
    
    @Override
	public int consumeLeaderAndStrength(final int playerId, final int generalId, final int consumeLeader, final int consumeStrength) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("consumeLeader", consumeLeader).addParam("consumeStrength", consumeStrength);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.consumeLeaderAndStrength", params);
    }
    
    @Override
	public List<PlayerGeneralMilitary> getGeneralsForFollow(final int locationId) {
        final List<PlayerGeneralMilitary> list = (List<PlayerGeneralMilitary>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneralMilitary.getGeneralsForFollow", (Object)locationId);
        return this.sortByQuality(list);
    }
    
    @Override
	public int updateTacticEffect(final int playerId, final int generalId, final int addValue) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("addValue", addValue);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateTacticEffect", params);
    }
    
    @Override
	public int resetTacticEffect(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.resetTacticEffect", playerId);
    }
    
    private List<PlayerGeneralMilitary> sortByQuality(final List<PlayerGeneralMilitary> list) {
        if (list.size() < 2) {
            return list;
        }
        int begin = 0;
        int end = 0;
        boolean find = false;
        PlayerGeneralMilitary temp = null;
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
	public void updateLvAndLeader(final int playerId, final int playerLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("playerLv", playerLv);
        this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateLvAndLeader", params);
    }
    
    @Override
	public int updateJuBenLocation(final int playerId, final int locationId, final int jubenLoId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("jubenLoId", jubenLoId).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateJuBenLocation", params);
    }
    
    @Override
	public int moveJuben(final int playerId, final int generalId, final int state, final int cityId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("state", state);
        params.addParam("cityId", cityId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.moveJuben", params);
    }
    
    @Override
	public int getGeneralNumInCity(final int playerId, final int cityId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("cityId", cityId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneralMilitary.getGeneralNumInCity", (Object)params);
    }
    
    @Override
	public int updateLocationByforceIdAndLocationId(final int cityId) {
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateLocationByforceIdAndLocationId", cityId);
    }
    
    @Override
	public int deleteByPlayerId(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.general.domain.PlayerGeneralMilitary.deleteByPlayerId", playerId);
    }
    
    @Override
	public int consumeForces(final int playerId, final int generalId, double forces, final Date date) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.consumeForces", params);
    }
    
    @Override
	public int consumeForcesByState(final int playerId, final int generalId, double forces, final Date date) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.consumeForcesByState", params);
    }
    
    @Override
	public int consumeCityForces(final int cityId, final double forcesCost, final Date date) {
        final Params params = new Params();
        params.addParam("cityId", cityId).addParam("forcesCost", forcesCost).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.consumeCityForces", params);
    }
    
    @Override
	public int consumeForcesSetState1(final int playerId, final int generalId, double forces, final Date date) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.consumeForcesSetState1", params);
    }
    
    @Override
	public int updateLocationForceSetState1(final int playerId, final int generalId, final int locationId, double forces, final Date date) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("reduceForces", forces).addParam("date", date).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateLocationForceSetState1", params);
    }
    
    @Override
	public int upJuBenLocationForceSetState1(final int playerId, final int generalId, final int locationId, double forces, final Date date) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("reduceForces", forces).addParam("date", date).addParam("locationId", locationId);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.upJuBenLocationForceSetState1", params);
    }
    
    @Override
	public int addPlayerForces(final int playerId, double forces) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("forces", forces);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addPlayerForces", params);
    }
    
    @Override
	public int addGeneralForces(final int playerId, final int generalId, final Date date, final int state, final long forces) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("date", date).addParam("state", state).addParam("forces", forces);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addGeneralForces", params);
    }
    
    @Override
	public int addGeneralForces2(final int playerId, final int generalId, final long forces) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.addGeneralForces2", params);
    }
    
    @Override
	public int setPlayerForces(final int playerId, final int force) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("force", force);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.setPlayerForces", params);
    }
    
    @Override
	public int updateForcesDate(final int playerId, final int generalId, final Date date, double forces, final long max) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date).addParam("max", max);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.updateForcesDate", params);
    }
    
    @Override
	public int resetForces(final int playerId, final int generalId, final Date date, double forces) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneralMilitary.resetForces", params);
    }
}
