package com.reign.gcld.juben.service;

import java.util.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.juben.common.*;

public class JuBenManager
{
    private static final Logger log;
    private static final JuBenManager instance;
    public static Map<Integer, JuBenDto> juBenMap;
    
    static {
        log = CommonLog.getLog(JuBenManager.class);
        instance = new JuBenManager();
        JuBenManager.juBenMap = new ConcurrentHashMap<Integer, JuBenDto>();
    }
    
    public static JuBenManager getInstance() {
        return JuBenManager.instance;
    }
    
    public void putJuBen(final JuBenDto juBenDto) {
        JuBenManager.juBenMap.put(juBenDto.player_id, juBenDto);
    }
    
    public JuBenDto getByPid(final int playerId) {
        return JuBenManager.juBenMap.get(playerId);
    }
    
    public JuBenCityDto getJuBenCityDto(final int playerId, final int cityId) {
        if (JuBenManager.juBenMap.containsKey(playerId)) {
            return JuBenManager.juBenMap.get(playerId).juBenCityDtoMap.get(cityId);
        }
        JuBenManager.log.error("JuBenManager getJuBenCityDto playerId:" + playerId + " cityId:" + cityId);
        return null;
    }
    
    public void clearByVid(final int playerId) {
        JuBenManager.juBenMap.remove(playerId);
    }
    
    public void buyPhantom(final int playerId, final int cityId, final int pgmVId) {
    }
}
