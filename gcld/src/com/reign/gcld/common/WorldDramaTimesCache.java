package com.reign.gcld.common;

import com.reign.gcld.juben.dao.*;
import org.apache.commons.lang.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.util.*;
import java.util.*;
import com.reign.gcld.store.common.*;

public class WorldDramaTimesCache extends AbstractDataCache<String, String>
{
    private IPlayerScenarioDao playerScenarioDao;
    private static WorldDramaTimesCache cache;
    
    static {
        WorldDramaTimesCache.cache = null;
    }
    
    public static WorldDramaTimesCache getInstatnce() {
        if (WorldDramaTimesCache.cache == null) {
            WorldDramaTimesCache.cache = new WorldDramaTimesCache();
        }
        return WorldDramaTimesCache.cache;
    }
    
    public void initWorldDramaTimesCache(final IPlayerScenarioDao playerScenarioDao) {
        this.playerScenarioDao = playerScenarioDao;
    }
    
    @Override
    public String read(final String key) {
        final MultiResult result = this.getValueResult(key);
        if (result == null) {
            return null;
        }
        final String dramaInfo = this.playerScenarioDao.getDramaTimes(result.result1, result.result2);
        return dramaInfo;
    }
    
    @Override
    public int update(final String key, final String value) {
        final MultiResult result = this.getValueResult(key);
        if (result == null) {
            return 0;
        }
        return this.playerScenarioDao.updateDramaTimes(result.result1, result.result2, value);
    }
    
    public int updateTimes(final int playerId, final int soloId, final int grade, final int times) {
        try {
            Map<Integer, Integer> dtos = this.getTimesMap(playerId, soloId);
            if (dtos == null) {
                dtos = new HashMap<Integer, Integer>();
                dtos.put(grade, times);
            }
            else {
                dtos.put(grade, times);
            }
            final String result = this.composeString(dtos);
            final String value = this.put(this.getKey(playerId, soloId), result);
            if (StringUtils.isBlank(value)) {
                return 0;
            }
            return 1;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    private String composeString(final Map<Integer, Integer> dtos) {
        final TreeSet<Integer> keySet = new TreeSet<Integer>(dtos.keySet());
        final StringBuffer sb = new StringBuffer();
        for (final Integer key : keySet) {
            sb.append(key).append(":").append(dtos.get(key)).append(";");
        }
        SymbolUtil.removeTheLast(sb);
        return sb.toString();
    }
    
    public String getKey(final int playerId, final int soloId) {
        return String.valueOf(playerId) + "-" + soloId;
    }
    
    public MultiResult getValueResult(final String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        final String[] single = key.split("-");
        final MultiResult result = new MultiResult();
        result.result1 = new Integer(single[0]);
        result.result2 = new Integer(single[1]);
        return result;
    }
    
    public String getScenarioTimesByPIdAndSId(final int playerId, final int soloId) {
        final String key = this.getKey(playerId, soloId);
        return this.get(key);
    }
    
    public int getTimesByPIDAndSIdAndGrade(final int playerId, final int soloId, final int grade) {
        try {
            final Map<Integer, Integer> dtos = this.getTimesMap(playerId, soloId);
            if (dtos == null) {
                return 0;
            }
            final Integer result = dtos.get(grade);
            return (result == null) ? 0 : result;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    public Map<Integer, Integer> getTimesMap(final int playerId, final int soloId) {
        final String s = this.getScenarioTimesByPIdAndSId(playerId, soloId);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        final Map<Integer, Integer> dtos = new HashMap<Integer, Integer>();
        this.composeList(dtos, s);
        return dtos;
    }
    
    private void composeList(final Map<Integer, Integer> dtos, final String s) {
        final String[] single = s.split(";");
        for (int i = 0; i < single.length; ++i) {
            final SkillDto dto = new SkillDto(single[i]);
            dtos.put(dto.getSkillId(), dto.getSkillLv());
        }
    }
}
