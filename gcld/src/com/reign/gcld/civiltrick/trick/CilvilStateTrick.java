package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.juben.common.*;
import java.util.*;

public class CilvilStateTrick implements ITrick
{
    private Stratagem trick;
    
    public CilvilStateTrick(final Stratagem trick) {
        this.trick = trick;
    }
    
    @Override
    public byte[] getPitchLocation(final IDataGetter dataGetter, final int playerId, final List<PlayerGeneralMilitary> list) {
        final Set<Integer> neighbours = TrickFactory.getNeibours(list, dataGetter);
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_TRICK);
        }
        final List<Integer> returnCities = new ArrayList<Integer>();
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
        for (final Integer c : neighbours) {
            if (attedSet.contains(c) && !WorldCityCommon.mainCityNationIdMap.containsKey(c)) {
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
            doc.createElement("ok", true);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
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
        final City c = dataGetter.getCityDao().read(cityId);
        boolean canPitch = true;
        if (stratagem.getType().equalsIgnoreCase("dongyao")) {
            canPitch = TrickFactory.decideCanAttCity(cityId, playerDto.forceId, dataGetter);
            if (!canPitch) {
                return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.CANNOT_USE_OUTSIDE_BATTLE_AREA, type);
            }
        }
        else {
            canPitch = (c.getForceId() == playerDto.forceId || TrickFactory.decideCanAttCity(cityId, playerDto.forceId, dataGetter));
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId) || !canPitch) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_WRONG, type);
        }
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        boolean flag = true;
        final String a = c.getTrickinfo();
        if (!a.equals("")) {
            final String[] b = a.split("#");
            String[] array;
            for (int length = (array = b).length, i = 0; i < length; ++i) {
                final String s = array[i];
                if (!StringUtils.isBlank(s)) {
                    final String[] d = s.split("-");
                    if (d.length > 0) {
                        final Stratagem cityStraInfo = (Stratagem)dataGetter.getStratagemCache().get((Object)Integer.valueOf(d[0]));
                        if (cityStraInfo != null) {
                            final int j = Integer.valueOf(d[1]);
                            if (cityStraInfo.getType().equalsIgnoreCase(this.trick.getType())) {
                                final int forceId = Integer.parseInt(d[3]);
                                if (forceId == playerDto.forceId && j > this.trick.getQuality()) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (!flag) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.HAS_HIGHER_LEVEL, type);
        }
        final String trickInfo = dataGetter.getCityDao().read(cityId).getTrickinfo();
        final String newtrickInfo = this.getNewTrickInfo(trickInfo, new Date().getTime(), playerDto.forceId, new Date().getTime() + stratagem.getPar2() * 1000, dataGetter.getStratagemCache().getTrickMap());
        final int suc = dataGetter.getCityDao().updateTrickInfo(cityId, newtrickInfo);
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), c.getForceId());
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.createElement("cityId", cityId);
            doc.createElement("stratagemId", stratagem.getId());
            doc.createElement("stateNumber", stratagem.getPar1());
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("cd", playerCd);
            doc.createElement("stateValue", this.trick.getPar1());
            doc.createElement("lastTime", stratagem.getPar2() * 1000);
            doc.createElement("type", this.trick.getType());
            doc.createElement("pos", vid);
            doc.endObject();
            if (stratagem.getType() == "dongyao" || stratagem.getType() == "guwu") {
                dataGetter.getBattleService().reSetAllDamageE(cityId);
            }
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            dataGetter.getCityTrickStateCache().changeCityState(cityId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    private String getNewTrickInfo(final String trickInfo, final long m, final int force, final long l, final Map<String, List<Integer>> map) {
        final StringBuffer s = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            s.append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(m).append("-").append(force).append("-").append(l);
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
                    if (map.get(this.trick.getType()).contains(Integer.valueOf(div[0])) && force == Integer.valueOf(div[3])) {
                        div[0] = String.valueOf(this.trick.getId());
                        div[1] = String.valueOf(this.trick.getQuality());
                        div[2] = String.valueOf(m);
                        div[3] = String.valueOf(force);
                        div[4] = String.valueOf(l);
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
            s.append("#").append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(m).append("-").append(force).append("-").append(l);
        }
        return s.toString();
    }
    
    @Override
    public byte[] useTrickInScenario(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type, final Map<Integer, JuBenCityDto> map, final int jubenId) {
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final JuBenCityDto city = map.get(cityId);
        boolean canPitch = true;
        if (stratagem.getType().equalsIgnoreCase("dongyao")) {
            canPitch = TrickFactory.decideCanAttCityInScenario(cityId, playerDto.forceId, map, dataGetter);
            if (!canPitch) {
                return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.CANNOT_USE_OUTSIDE_BATTLE_AREA, type);
            }
        }
        else {
            canPitch = (city.forceId == playerDto.forceId || TrickFactory.decideCanAttCityInScenario(cityId, playerDto.forceId, map, dataGetter));
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId) || !canPitch) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_WRONG, type);
        }
        final long now = System.currentTimeMillis();
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        boolean flag = true;
        final HashMap<Integer, HashMap<String, TrickDto>> forceMap = city.trickDto;
        final HashMap<String, TrickDto> trickInfo = TrickFactory.getTrickDto(forceMap, playerDto.forceId);
        if (trickInfo.get(trickId) != null) {
            final TrickDto trickDto = trickInfo.get(trickId);
            final long cd = trickDto.getLastTime() - now;
            if (cd > 0L && trickDto.getLv() > stratagem.getQuality()) {
                flag = false;
            }
        }
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (!flag) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.HAS_HIGHER_LEVEL, type);
        }
        trickInfo.put(this.trick.getType(), new TrickDto(trickId, stratagem.getQuality(), stratagem.getType(), now + this.trick.getPar2() * 1000, now, playerDto.forceId, 0));
        city.trickDto.put(playerDto.forceId, trickInfo);
        final String newtrickInfo = TrickFactory.getTrickInfoInScenarioWithMap(city.trickDto);
        final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newtrickInfo, playerDto.playerId, jubenId, cityId);
        if (suc > 0) {
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.createElement("cityId", cityId);
            doc.createElement("stratagemId", stratagem.getId());
            doc.createElement("stateNumber", stratagem.getPar1());
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("cd", playerCd);
            doc.createElement("stateValue", this.trick.getPar1());
            doc.createElement("lastTime", stratagem.getPar2() * 1000);
            doc.createElement("type", this.trick.getType());
            doc.createElement("pos", vid);
            doc.endObject();
            if (stratagem.getType() == "dongyao" || stratagem.getType() == "guwu") {
                dataGetter.getBattleService().reSetAllDamageE(cityId);
            }
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
