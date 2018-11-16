package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import java.util.*;

public class CilvilTrapTrick implements ITrick
{
    private Stratagem trick;
    
    public CilvilTrapTrick(final Stratagem trick) {
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
            if (((WorldCity)dataGetter.getWorldCityCache().get((Object)c)).getTerrain() != 6 && attedSet.contains(c) && !WorldCityCommon.mainCityNationIdMap.containsKey(c) && dataGetter.getCityDao().read(c).getForceId() == dataGetter.getPlayerDao().read(playerId).getForceId()) {
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
            final City c2 = dataGetter.getCityDao().read(city);
            final Tuple<Long, Integer> cd = this.getTrickProtectCd(c2.getTrickinfo(), dataGetter.getStratagemCache().getTrickMap());
            if (cd != null && cd.left > 0L) {
                doc.createElement("ok", false);
                doc.createElement("cd", cd.left);
                doc.createElement("stratagemId", cd.right);
                doc.createElement("type", this.trick.getType());
            }
            else {
                doc.createElement("ok", true);
                doc.createElement("stratagemId", this.trick.getId());
                doc.createElement("type", this.trick.getType());
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private Tuple<Long, Integer> getTrickProtectCd(final String trickinfo, final Map<String, List<Integer>> map) {
        final Tuple<Long, Integer> tuple = new Tuple();
        if (StringUtils.isBlank(trickinfo)) {
            return null;
        }
        String[] split;
        for (int length = (split = trickinfo.split("#")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            if (!StringUtils.isBlank(s)) {
                final String[] b = s.split("-");
                if (b.length > 0) {
                    if (map.get(this.trick.getType()).contains(Integer.valueOf(b[0]))) {
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
    
    @Override
    public byte[] useTrick(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type) {
        final List<PlayerGeneralMilitary> gList = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        TrickFactory.getNeibours(gList, dataGetter);
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final City c = dataGetter.getCityDao().read(cityId);
        final Integer cityID = cityId;
        if (c.getForceId() != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (((WorldCity)dataGetter.getWorldCityCache().get((Object)cityID)).getTerrain().equals(6) || WorldCityCommon.mainCityNationIdMap.containsKey(cityId) || dataGetter.getCityDao().read(cityId).getForceId() != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final String trickInfo = c.getTrickinfo();
        final Tuple<Long, Integer> cd = this.getTrickProtectCd(trickInfo, dataGetter.getStratagemCache().getTrickMap());
        if (cd != null && cd.left > 0L) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_IN_PROTECT, type);
        }
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        final String newtrickInfo = this.getNewTrickInfo(trickInfo, new Date().getTime() + this.trick.getPar4() * 60 * 1000, playerDto.forceId, new Date().getTime() + this.trick.getPar1() * 1000, this.trick.getPar3(), dataGetter.getStratagemCache().getTrickMap());
        final int suc = dataGetter.getCityDao().updateTrickInfo(cityId, newtrickInfo);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), c.getForceId());
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.createElement("cityId", cityId);
            doc.createElement("isTrap", true);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", this.trick.getPar4() * 60 * 1000);
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("pos", vid);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            doc.endObject();
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            dataGetter.getCityTrickStateCache().changeCityState(cityId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    private String getNewTrickInfo(final String trickInfo, final long l, final int forceId, final long m, final Integer integer, final Map<String, List<Integer>> map) {
        final StringBuffer s = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            s.append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l).append("-").append((int)integer).append("-").append(forceId).append("-").append(m);
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
                    if (map.get(this.trick.getType()).contains(Integer.valueOf(div[0])) && forceId == Integer.valueOf(div[4])) {
                        div[0] = String.valueOf(this.trick.getId());
                        div[1] = String.valueOf(this.trick.getQuality());
                        div[2] = String.valueOf(l);
                        div[3] = String.valueOf((int)integer);
                        div[4] = String.valueOf(forceId);
                        div[5] = String.valueOf(m);
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
            s.append("#").append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l).append("-").append((int)integer).append("-").append(forceId).append("-").append(m);
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
        if (city.forceId != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (city.forceId != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
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
        trickInfo.put(this.trick.getType(), new TrickDto(trickId, stratagem.getQuality(), stratagem.getType(), now + this.trick.getPar1() * 1000, now + this.trick.getPar4() * 60 * 1000, playerDto.forceId, this.trick.getPar3()));
        city.trickDto.put(playerDto.forceId, trickInfo);
        final String newtrickInfo = TrickFactory.getTrickInfoInScenarioWithMap(city.trickDto);
        final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newtrickInfo, playerDto.playerId, jubenId, cityId);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(new Date().getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (suc > 0) {
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.createElement("cityId", cityId);
            doc.createElement("isTrap", true);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", this.trick.getPar4() * 60 * 1000);
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("pos", vid);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            doc.endObject();
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    @Override
    public void useTrickNpc(final int cityId, final int playerId, final IDataGetter dataGetter, final int scenarioId) {
    }
}
