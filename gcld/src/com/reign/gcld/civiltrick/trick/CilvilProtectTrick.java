package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.common.*;

public class CilvilProtectTrick implements ITrick
{
    private Stratagem trick;
    
    public CilvilProtectTrick(final Stratagem trick) {
        this.trick = trick;
    }
    
    @Override
    public byte[] getPitchLocation(final IDataGetter dataGetter, final int playerId, final List<PlayerGeneralMilitary> list) {
        final Set<Integer> resSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary p : list) {
            resSet.add(p.getLocationId());
        }
        final List<Integer> returnCities = new ArrayList<Integer>();
        for (final Integer city : resSet) {
            final City c = dataGetter.getCityDao().read(city);
            if (c.getForceId().equals(dataGetter.getPlayerDao().read(playerId).getForceId()) && !WorldCityCommon.mainCityNationIdMap.containsKey(c.getId()) && ((WorldCity)dataGetter.getWorldCityCache().get((Object)c.getId())).getTerrain().equals(6)) {
                returnCities.add(c.getId());
            }
        }
        if (returnCities == null || returnCities.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_LOCATION);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cities");
        for (final Integer i : returnCities) {
            doc.startObject();
            final City c2 = dataGetter.getCityDao().read(i);
            final Tuple<Long, Integer> cd = getTrickProtectCd(c2.getTrickinfo(), dataGetter.getStratagemCache().getTrickMap(), this.trick);
            if (cd != null && cd.left > 0L) {
                doc.createElement("cityId", c2.getId());
                doc.createElement("ok", false);
                doc.createElement("cd", cd.left);
                doc.createElement("stratagemId", cd.right);
                doc.createElement("type", this.trick.getType());
            }
            else {
                doc.createElement("cityId", c2.getId());
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
    
    public static Tuple<Long, Integer> getTrickProtectCd(final String trickinfo, final Map<String, List<Integer>> map, final Stratagem trick) {
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
                    if (map.get(trick.getType()).contains(Integer.valueOf(b[0]))) {
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
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final List<PlayerGeneralMilitary> list = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Set<Integer> reSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary p : list) {
            reSet.add(p.getLocationId());
        }
        final City pitchCity = dataGetter.getCityDao().read(cityId);
        if (pitchCity == null || pitchCity.getForceId() != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final City city = dataGetter.getCityDao().read(cityId);
        final String trickInfo = dataGetter.getCityDao().read(cityId).getTrickinfo();
        final Tuple<Long, Integer> cd = getTrickProtectCd(trickInfo, dataGetter.getStratagemCache().getTrickMap(), this.trick);
        if (cd != null && cd.left > 0L) {
            return TrickFactory.getJsonForTrick(State.FAIL, MessageFormatter.format(LocalMessages.LOCATION_IN_PROTECT, new Object[] { stratagem.getName(), TimeUtil.getTimeLeft((long)cd.left) }), type);
        }
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        final Date nowDate = new Date();
        final String newtrickInfo = this.getNewTrickInfo(trickInfo, nowDate.getTime() + this.trick.getPar2() * 60 * 1000, nowDate.getTime() + this.trick.getPar1() * 1000, dataGetter.getStratagemCache().getTrickMap());
        final int suc = dataGetter.getCityDao().updateTrickInfo(cityId, newtrickInfo);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(nowDate.getTime() + playerCd);
        final JsonDocument doc = new JsonDocument();
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), city.getForceId());
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("canntAttack", true);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", stratagem.getPar2() * 60 * 1000);
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
            ITrick.testLogger.error("#" + cityId + "#" + trickInfo + "#" + newtrickInfo);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            dataGetter.getCityTrickStateCache().changeCityState(cityId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    private String getNewTrickInfo(final String trickInfo, final long l, final long m, final Map<String, List<Integer>> map) {
        final StringBuffer s = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            s.append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l).append("-").append(m);
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
                    if (map.get(this.trick.getType()).contains(Integer.valueOf(div[0]))) {
                        div[0] = String.valueOf(this.trick.getId());
                        div[1] = String.valueOf(this.trick.getQuality());
                        div[2] = String.valueOf(l);
                        div[3] = String.valueOf(m);
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
            s.append("#").append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l).append("-").append(m);
        }
        return s.toString();
    }
    
    @Override
    public byte[] useTrickInScenario(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type, final Map<Integer, JuBenCityDto> map, final int jubenId) {
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final List<PlayerGeneralMilitary> list = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Set<Integer> reSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary p : list) {
            reSet.add(p.getJubenLoId());
        }
        final JuBenCityDto city = map.get(cityId);
        if (city == null || city.forceId != playerDto.forceId) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
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
        trickInfo.put(this.trick.getType(), new TrickDto(trickId, stratagem.getQuality(), stratagem.getType(), now + this.trick.getPar1() * 1000, now + stratagem.getPar2() * 1000 * 60, playerDto.forceId, 0));
        city.trickDto.put(playerDto.forceId, trickInfo);
        final String newtrickInfo = TrickFactory.getTrickInfoInScenarioWithMap(city.trickDto);
        final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newtrickInfo, playerDto.playerId, jubenId, cityId);
        final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
        final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
        final long trickcd = (long)(now + playerCd);
        final JsonDocument doc = new JsonDocument();
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), city.forceId);
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("canntAttack", true);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", stratagem.getPar2() * 60 * 1000);
            doc.createElement("lastTime", stratagem.getPar1() * 1000);
            doc.createElement("stratagemId", this.trick.getId());
            doc.createElement("type", this.trick.getType());
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
            ITrick.testLogger.error("#" + cityId + "#" + trickInfo + "#" + newtrickInfo);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    @Override
    public void useTrickNpc(final int cityId, final int playerId, final IDataGetter dataGetter, final int scenarioId) {
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null) {
            return;
        }
        if (cityId == 0 || cityId == 1) {
            for (final JuBenCityDto cityDto : juBenDto.juBenCityDtoMap.values()) {
                if (cityDto == null) {
                    continue;
                }
                final SoloCity city = (SoloCity)dataGetter.getSoloCityCache().get((Object)cityDto.cityId);
                if (city == null) {
                    continue;
                }
                if (city.getCapital() == 1) {
                    continue;
                }
                if (cityId == 0 && cityDto.forceId == 0) {
                    continue;
                }
                if (cityId == 1 && cityDto.forceId != 0) {
                    continue;
                }
                this.doUseTrickNpc(juBenDto, cityDto.cityId, dataGetter, playerId, scenarioId);
            }
        }
        else {
            this.doUseTrickNpc(juBenDto, cityId, dataGetter, playerId, scenarioId);
        }
    }
    
    private void doUseTrickNpc(final JuBenDto juBenDto, final int cityId, final IDataGetter dataGetter, final int playerId, final int scenarioId) {
        try {
            final JuBenCityDto dto = juBenDto.juBenCityDtoMap.get(cityId);
            if (dto == null) {
                return;
            }
            final long now = System.currentTimeMillis();
            final HashMap<String, TrickDto> trickDto = new HashMap<String, TrickDto>();
            trickDto.put(this.trick.getType(), new TrickDto(this.trick.getId(), this.trick.getQuality(), this.trick.getType(), now + this.trick.getPar1() * 1000, now + this.trick.getPar2() * 1000 * 60, 0, 0));
            dto.trickDto.put(0, trickDto);
            final String newString = TrickFactory.getTrickInfoInScenarioWithMap(dto.trickDto);
            final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newString, playerId, scenarioId, cityId);
            if (suc > 0) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("stratagemId", this.trick.getId());
                doc.createElement("type", this.trick.getType());
                doc.createElement("cityId", cityId);
                doc.createElement("protectCd", this.trick.getPar4() * 60 * 1000);
                doc.createElement("Quality", this.trick.getQuality());
                doc.createElement("lastTime", this.trick.getPar1() * 1000);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_JUBEN_NPCTRICK, doc.toByte());
            }
        }
        catch (Exception e) {
            ITrick.errorLogger.error("Protect doUseTrickNpc fail...playerId:" + playerId + "cityId:" + cityId);
            ITrick.errorLogger.error(e.getMessage());
            ITrick.errorLogger.error(this, e);
        }
    }
}
