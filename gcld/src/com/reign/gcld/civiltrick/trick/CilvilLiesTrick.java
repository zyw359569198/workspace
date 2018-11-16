package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.world.domain.*;
import com.reign.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.juben.common.*;
import java.util.*;

public class CilvilLiesTrick implements ITrick
{
    private Stratagem trick;
    
    public CilvilLiesTrick(final Stratagem trick) {
        this.trick = trick;
    }
    
    @Override
    public byte[] getPitchLocation(final IDataGetter dataGetter, final int playerId, final List<PlayerGeneralMilitary> list) {
        final Set<Integer> neighbours = TrickFactory.getNeibours(list, dataGetter);
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_TRICK);
        }
        final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array;
            for (int length = (array = ids).length, i = 0; i < length; ++i) {
                final String str = array[i];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        final List<Integer> returnCities = new ArrayList<Integer>();
        for (final Integer c : neighbours) {
            if (!((WorldCity)dataGetter.getWorldCityCache().get((Object)c)).getTerrain().equals(6) && attedSet.contains(c) && !WorldCityCommon.mainCityNationIdMap.containsKey(c) && dataGetter.getCityDao().read(c).getForceId() != dataGetter.getPlayerDao().read(playerId).getForceId()) {
                returnCities.add(c);
            }
        }
        if (returnCities == null || returnCities.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_LOCATION);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cities");
        for (final Integer city : returnCities) {
            doc.startObject();
            doc.createElement("cityId", city);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("ok", true);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] useTrick(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type) {
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final Integer cityID = cityId;
        final boolean canAtt = TrickFactory.decideCanAttCity(cityId, playerDto.forceId, dataGetter);
        if (!canAtt) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (!canAtt || ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityID)).getTerrain().equals(6) || WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final City city = dataGetter.getCityDao().read(cityId);
        final String trickInfo = city.getTrickinfo();
        final Tuple<Long, Integer> cd = this.getTrickProtectCd(trickInfo, dataGetter.getStratagemCache().getTrickMap());
        if (cd != null && cd.left > 0L) {
            return TrickFactory.getJsonForTrick(State.FAIL, MessageFormatter.format(LocalMessages.LOCATION_IN_PROTECT, new Object[] { stratagem.getName(), TimeUtil.getTimeLeft((long)cd.left) }), type);
        }
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        final long lastTime = new Date().getTime() + this.trick.getPar1() * 1000;
        final String newtrickInfo = this.getNewTrickInfo(trickInfo, new Date().getTime() + stratagem.getPar4() * 1000 * 60, lastTime, dataGetter.getStratagemCache().getTrickMap(), playerDto.forceId);
        final int suc = dataGetter.getCityDao().updateTrickInfo(cityId, newtrickInfo);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), city.getForceId());
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("cantmove", true);
            doc.createElement("cd", playerCd);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            dataGetter.getCityTrickStateCache().changeCityState(cityId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    private Tuple<Long, Integer> getTrickProtectCd(final String tString, final Map<String, List<Integer>> trickMap) {
        final Tuple<Long, Integer> tuple = new Tuple();
        if (StringUtils.isBlank(tString)) {
            return null;
        }
        String[] split;
        for (int length = (split = tString.split("#")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            if (!StringUtils.isBlank(s)) {
                final String[] b = s.split("-");
                if (b.length > 0) {
                    if (trickMap.get(this.trick.getType()).contains(Integer.valueOf(b[0]))) {
                        long cd = Long.parseLong(b[2]);
                        final long now = new Date().getTime();
                        cd = ((cd > now) ? (cd - now) : 0L);
                        tuple.left = cd;
                        tuple.right = Integer.valueOf(b[0]);
                        return tuple;
                    }
                }
            }
        }
        return null;
    }
    
    private String getNewTrickInfo(final String trickInfo, final long time, final long last, final Map<String, List<Integer>> map, final int forceId) {
        final StringBuffer s = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            s.append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(time).append("-").append(forceId).append("-").append(last);
            return s.toString();
        }
        final String[] info = trickInfo.split("#");
        int a = 0;
        int first = 0;
        String[] array;
        for (int length = (array = info).length, j = 0; j < length; ++j) {
            String i = array[j];
            if (!StringUtils.isBlank(i)) {
                final String[] div = i.split("-");
                if (div.length > 0) {
                    if (map.get(this.trick.getType()).contains(Integer.valueOf(div[0])) && forceId == Integer.parseInt(div[3])) {
                        div[0] = String.valueOf(this.trick.getId());
                        div[1] = String.valueOf(this.trick.getQuality());
                        div[2] = String.valueOf(time);
                        div[3] = String.valueOf(forceId);
                        div[4] = String.valueOf(last);
                        ++a;
                    }
                    i = TrickFactory.ConcateString(div, "-");
                    if (first == 0) {
                        s.append(i);
                    }
                    else {
                        s.append("#").append(i);
                    }
                    ++first;
                }
            }
        }
        if (a == 0) {
            s.append("#").append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(time).append("-").append(forceId).append("-").append(last);
        }
        return s.toString();
    }
    
    @Override
    public byte[] useTrickInScenario(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type, final Map<Integer, JuBenCityDto> map, final int jubenId) {
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final boolean canAtt = TrickFactory.decideCanAttCityInScenario(cityId, playerDto.forceId, map, dataGetter);
        if (!canAtt) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final JuBenCityDto city = map.get(cityId);
        final long now = System.currentTimeMillis();
        final HashMap<Integer, HashMap<String, TrickDto>> forceMap = city.trickDto;
        final HashMap<String, TrickDto> trickInfo = TrickFactory.getTrickDto(forceMap, playerDto.forceId);
        if (trickInfo.get(trickId) != null) {
            final TrickDto trickDto = trickInfo.get(trickId);
            final long cd = trickDto.getProtectTime() - now;
            if (cd > 0L) {
                return TrickFactory.getJsonForTrick(State.FAIL, MessageFormatter.format(LocalMessages.LOCATION_IN_PROTECT, new Object[] { stratagem.getName(), TimeUtil.getTimeLeft(cd) }), type);
            }
        }
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        final long lastTime = new Date().getTime() + this.trick.getPar1() * 1000;
        trickInfo.put(this.trick.getType(), new TrickDto(trickId, stratagem.getQuality(), stratagem.getType(), lastTime, now + stratagem.getPar4() * 1000 * 60, playerDto.forceId, 0));
        city.trickDto.put(playerDto.forceId, trickInfo);
        final String newtrickInfo = TrickFactory.getTrickInfoInScenarioWithMap(city.trickDto);
        final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newtrickInfo, playerDto.playerId, jubenId, cityId);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        if (suc > 0) {
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("cantmove", true);
            doc.createElement("cd", playerCd);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    @Override
    public void useTrickNpc(final int cityId, final int playerId, final IDataGetter dataGetter, final int scenarioId) {
    }
}
