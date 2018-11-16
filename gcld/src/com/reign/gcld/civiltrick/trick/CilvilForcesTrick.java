package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.player.dto.*;
import com.reign.util.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.scene.*;
import java.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.juben.common.*;

public class CilvilForcesTrick implements ITrick
{
    private Stratagem trick;
    
    public CilvilForcesTrick(final Stratagem trick) {
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
            if (((WorldCity)dataGetter.getWorldCityCache().get((Object)c)).getTerrainEffectType().equals(stratagem.getPar1()) && attedSet.contains(c) && !WorldCityCommon.mainCityNationIdMap.containsKey(c)) {
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
    
    private Tuple<Long, Integer> getTrickProtectCd(final String tString, final Map<String, List<Integer>> map) {
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
        if (type != 0 && type != 1) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10011, type);
        }
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        if (!((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getTerrainEffectType().equals(stratagem.getPar1())) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final boolean canAtt = TrickFactory.decideCanAttCity(cityId, playerDto.forceId, dataGetter);
        if (!canAtt) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final City city = dataGetter.getCityDao().read(cityId);
        final String trickInfo = city.getTrickinfo();
        final Tuple<Long, Integer> cd = this.getTrickProtectCd(trickInfo, dataGetter.getStratagemCache().getTrickMap());
        if (cd != null && cd.left > 0L) {
            return TrickFactory.getJsonForTrick(State.FAIL, MessageFormatter.format(LocalMessages.LOCATION_IN_PROTECT, new Object[] { stratagem.getName(), TimeUtil.getTimeLeft((long)cd.left) }), type);
        }
        int forceSum = 0;
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        final String newtrickInfo = this.getNewTrickInfo(trickInfo, new Date().getTime() + stratagem.getPar4() * 1000 * 60, dataGetter.getStratagemCache().getTrickMap());
        final int suc = dataGetter.getCityDao().updateTrickInfo(cityId, newtrickInfo);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (suc > 0) {
            dataGetter.getCityDataCache().fireCityTrickMessage(cityId, playerDto.forceId, this.trick.getName(), city.getForceId());
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            final Battle bat = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (bat == null) {
                final List<PlayerGeneralMilitary> list = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationId(cityId);
                final List<PlayerGeneralMilitary> newList = new ArrayList<PlayerGeneralMilitary>();
                for (final PlayerGeneralMilitary p : list) {
                    if (dataGetter.getPlayerDao().read(p.getPlayerId()).getForceId() != playerDto.forceId && p.getState() <= 1) {
                        newList.add(p);
                    }
                }
                final int size = (newList.size() > stratagem.getPar3()) ? stratagem.getPar3() : newList.size();
                final int originPSize;
                int phantomsize = originPSize = ((size < stratagem.getPar3()) ? (stratagem.getPar3() - size) : 0);
                for (int i = 0; i < size; ++i) {
                    final int random = WebUtil.nextInt(newList.size());
                    final PlayerGeneralMilitary pgmi = newList.get(random);
                    final int forceReduce = (stratagem.getPar2() > pgmi.getForces()) ? pgmi.getForces() : ((int)stratagem.getPar2());
                    final int res = dataGetter.getPlayerGeneralMilitaryDao().consumeForcesByState(pgmi.getPlayerId(), pgmi.getGeneralId(), forceReduce, new Date());
                    if (res > 0) {
                        forceSum += forceReduce;
                        dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(pgmi.getPlayerId(), pgmi.getGeneralId());
                        final JsonDocument document = new JsonDocument();
                        document.startObject();
                        document.createElement("generalId", pgmi.getGeneralId());
                        document.createElement("trickInfo", (Object)MessageFormatter.format(LocalMessages.TRICK_INFO, new Object[] { playerDto.playerName, this.trick.getName(), this.trick.getPar2() }));
                        document.endObject();
                        Players.push(pgmi.getPlayerId(), PushCommand.PUSH_TRICKINFO, document.toByte());
                        newList.remove(random);
                    }
                }
                if (phantomsize > 0) {
                    final List<PlayerGeneralMilitaryPhantom> phantomsBack = dataGetter.getPlayerGeneralMilitaryPhantomDao().getPhantomByLocationIdOrderByPlayerIdLvDesc(cityId);
                    final List<PlayerGeneralMilitaryPhantom> phantoms = new ArrayList<PlayerGeneralMilitaryPhantom>();
                    for (int j = 0; j < phantomsBack.size(); ++j) {
                        final PlayerGeneralMilitaryPhantom temPhantom = phantomsBack.get(j);
                        if (temPhantom != null) {
                            if (temPhantom.getForceId() != playerDto.forceId) {
                                phantoms.add(temPhantom);
                            }
                        }
                    }
                    phantomsize = ((phantomsize > phantoms.size()) ? phantoms.size() : phantomsize);
                    for (int j = 0; j < phantomsize; ++j) {
                        final int random2 = WebUtil.nextInt(phantoms.size());
                        final PlayerGeneralMilitaryPhantom phantom = phantoms.get(random2);
                        if (phantom != null) {
                            final int forceReduce2 = (stratagem.getPar2() > phantom.getHp()) ? phantom.getHp() : ((int)stratagem.getPar2());
                            forceSum += forceReduce2;
                            final int hp = (phantom.getHp() > stratagem.getPar2()) ? (phantom.getHp() - stratagem.getPar2()) : 0;
                            if (hp > 0) {
                                dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(phantom.getVId(), hp);
                            }
                            else {
                                dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteById(phantom.getVId());
                            }
                            phantoms.remove(phantom);
                        }
                    }
                }
                final int barSize = originPSize - phantomsize;
                if (barSize > 0) {
                    final int num = dataGetter.getBattleService().trickReduceHpBarbarain(cityId, barSize, stratagem.getPar2());
                    if (num > 0) {
                        forceSum += num;
                    }
                    else {
                        forceSum += dataGetter.getBattleService().trickReduceHpYellowTurbans(cityId, barSize, stratagem.getPar2());
                    }
                }
            }
            else {
                forceSum = this.trickReduceHpInBattle(dataGetter, bat, playerDto, stratagem.getPar2(), stratagem.getPar3());
            }
            if (forceSum > 0) {
                dataGetter.getKillRankService().dealKillrank(forceSum, playerDto.forceId, dataGetter, playerDto.playerId);
            }
            final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
            final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
            final long trickcd = (long)(new Date().getTime() + playerCd);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            doc.createElement("stratagemId", trickId);
            doc.createElement("type", this.trick.getType());
            doc.createElement("forceSum", forceSum);
            doc.createElement("cityId", cityId);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", stratagem.getPar4() * 60 * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
            TrickFactory.sendCityTrickState(cityId, stratagem, doc, playerDto.forceId);
            TrickFactory.sendTrickInfo(playerDto.playerId, this.trick);
            dataGetter.getCityTrickStateCache().changeCityState(cityId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.LOCATION_TRICK_FAILURE, type);
    }
    
    public int trickReduceHpInBattle(final IDataGetter dataGetter, final Battle bat, final PlayerDto playerDto, int reduce, final int num) {
        synchronized (bat.getBattleId()) {
            LinkedList<CampArmy> campList = null;
            BaseInfo baseInfo = null;
            final Set<CampArmy> onQueueSet = new HashSet<CampArmy>();
            if (bat.getDefBaseInfo().getForceId() == playerDto.forceId) {
                campList = bat.getAttCamp();
                baseInfo = bat.getAttBaseInfo();
                for (final BattleArmy ba : bat.getAttList()) {
                    onQueueSet.add(ba.getCampArmy());
                }
            }
            else {
                campList = bat.getDefCamp();
                baseInfo = bat.getDefBaseInfo();
                for (final BattleArmy ba : bat.getDefList()) {
                    onQueueSet.add(ba.getCampArmy());
                }
            }
            int reduceSum = 0;
            final LinkedList<CampArmy> deadCampList = new LinkedList<CampArmy>();
            final int remainder = reduce % 3;
            reduce -= remainder;
            final int start = onQueueSet.size();
            for (int end = start + num, i = start; i < end && campList.size() > i; ++i) {
                final CampArmy temp = campList.get(i);
                if (!onQueueSet.contains(temp)) {
                    boolean dead = false;
                    int tempReduce;
                    if (temp.getArmyHp() <= (tempReduce = reduce)) {
                        tempReduce = temp.getArmyHp();
                        dead = true;
                    }
                    temp.setArmyHp(temp.getArmyHp() - tempReduce);
                    temp.setArmyHpLoss(temp.getArmyHpLoss() + tempReduce);
                    baseInfo.setNum(baseInfo.getNum() - tempReduce);
                    if (dead) {
                        deadCampList.add(temp);
                    }
                    if (temp.getPlayerId() > 0 && temp.isUpdateDB() && !temp.isPhantom()) {
                        if (dead) {
                            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(temp.getForceId());
                            final int done = dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(temp.getPlayerId(), temp.getGeneralId(), capitalId, tempReduce, new Date());
                            if (done != 1) {
                                ErrorSceneLog.getInstance().appendErrorMsg("updateLocationForceSetState1 failed").appendPlayerId(temp.getPlayerId()).appendGeneralId(temp.getGeneralId()).append("capitalId", capitalId).append("tempReduce", tempReduce).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle").flush();
                            }
                            try {
                                dataGetter.getCityService().updateGNumAndSend(bat.getDefBaseInfo().getId(), capitalId);
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().appendErrorMsg("updateGNumAndSend exception").append("playerId", temp.getPlayerId()).append("generalId", temp.getGeneralId()).appendClassName("CityService").appendMethodName("updateGNumAndSend").flush();
                                ErrorSceneLog.getInstance().error("trickReduceHpInBattle 1 ", e);
                            }
                            try {
                                dataGetter.getCityService().sendAttMoveInfo(temp.getPlayerId(), temp.getGeneralId(), bat.getDefBaseInfo().getId(), capitalId, temp.getForceId(), "", temp.getArmyHp(), true);
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().appendErrorMsg("sendAttMoveInfo exception").append("playerId", temp.getPlayerId()).append("generalId", temp.getGeneralId()).appendClassName("CityService").appendMethodName("sendAttMoveInfo").flush();
                                ErrorSceneLog.getInstance().error("trickReduceHpInBattle 2 ", e);
                            }
                            try {
                                final String cgm = dataGetter.getCityService().getColoredGeneralName(temp.getGeneralId());
                                dataGetter.getCityDataCache().fireCityMoveMessage(temp.getPlayerId(), bat.getDefBaseInfo().getId(), capitalId, cgm);
                            }
                            catch (Exception e) {
                                ErrorSceneLog.getInstance().error("trickReduceHpInBattle 3 ", e);
                            }
                        }
                        else {
                            final int done2 = dataGetter.getPlayerGeneralMilitaryDao().consumeForces(temp.getPlayerId(), temp.getGeneralId(), tempReduce, new Date());
                            if (done2 != 1) {
                                ErrorSceneLog.getInstance().appendErrorMsg("consumeForcesSetState1 failed").appendPlayerId(temp.getPlayerId()).appendGeneralId(temp.getGeneralId()).append("tempReduce", tempReduce).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle").flush();
                            }
                        }
                        dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(temp.getPlayerId(), temp.getGeneralId());
                        final JsonDocument document = new JsonDocument();
                        document.startObject();
                        document.createElement("generalId", temp.getGeneralId());
                        document.createElement("trickInfo", (Object)MessageFormatter.format(LocalMessages.TRICK_INFO, new Object[] { playerDto.playerName, this.trick.getName(), tempReduce }));
                        document.endObject();
                        Players.push(temp.getPlayerId(), PushCommand.PUSH_TRICKINFO, document.toByte());
                    }
                    if (temp.isPhantom() && dead) {
                        final int done2 = dataGetter.getPlayerGeneralMilitaryPhantomDao().deleteById(temp.getPgmVId());
                        if (done2 != 1) {
                            ErrorSceneLog.getInstance().appendErrorMsg("phanton delete failed.").appendBattleId(bat.getBattleId()).append("phantom vId", temp.getPgmVId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName()).append("cityId", bat.getDefBaseInfo().getId()).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle");
                        }
                    }
                    else {
                        final int done2 = dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(temp.getPgmVId(), temp.getArmyHp());
                        if (done2 != 1) {
                            ErrorSceneLog.getInstance().appendErrorMsg("phanton updateHp failed.").appendBattleId(bat.getBattleId()).append("phantom vId", temp.getPgmVId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName()).append("cityId", bat.getDefBaseInfo().getId()).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle");
                        }
                    }
                    if (temp.isYellowTrubans) {
                        if (dead) {
                            final int done2 = dataGetter.getYellowTurbansDao().deleteById(temp.getPgmVId());
                            if (done2 != 1) {
                                ErrorSceneLog.getInstance().appendErrorMsg("Yellow Turbans delete failed.").appendBattleId(bat.getBattleId()).append("Yellow Turbans vId", temp.getPgmVId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName()).append("cityId", bat.getDefBaseInfo().getId()).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle");
                            }
                        }
                        else {
                            final int done2 = dataGetter.getYellowTurbansDao().updateHpAndTacticVal(temp.getPgmVId(), temp.getArmyHp(), temp.getTacticVal());
                            if (done2 != 1) {
                                ErrorSceneLog.getInstance().appendErrorMsg("Yellow Turbans delete failed.").appendBattleId(bat.getBattleId()).append("Yellow Turbans vId", temp.getPgmVId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)bat.getDefBaseInfo().getId())).getName()).append("cityId", bat.getDefBaseInfo().getId()).appendClassName("CilvilForcesTrick").appendMethodName("trickReduceHpInBattle");
                            }
                        }
                    }
                    reduceSum += tempReduce;
                }
            }
            for (final CampArmy temp2 : deadCampList) {
                campList.remove(temp2);
            }
            // monitorexit(bat.getBattleId())
            return reduceSum;
        }
    }
    
    private String getNewTrickInfo(final String trickInfo, final long l, final Map<String, List<Integer>> map) {
        final StringBuffer s = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            s.append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l);
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
            s.append("#").append(this.trick.getId()).append("-").append(this.trick.getQuality()).append("-").append(l);
        }
        return s.toString();
    }
    
    @Override
    public byte[] useTrickInScenario(final IDataGetter dataGetter, final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int vid, final int type, final Map<Integer, JuBenCityDto> map, final int jubenId) {
        if (type != 0 && type != 1) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10011, type);
        }
        final Stratagem stratagem = (Stratagem)dataGetter.getStratagemCache().get((Object)this.trick.getId());
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        if (!((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getTerrainEffectType().equals(stratagem.getPar1())) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        final boolean canAtt = TrickFactory.decideCanAttCityInScenario(cityId, playerDto.forceId, map, dataGetter);
        if (!canAtt) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
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
        final int forceSum = 0;
        if (type != 0) {
            return TrickFactory.getJsonForTrick(State.SUCCESS, "", type);
        }
        trickInfo.put(this.trick.getType(), new TrickDto(trickId, stratagem.getQuality(), stratagem.getType(), now, now + stratagem.getPar4() * 1000 * 60, playerDto.forceId, 0));
        city.trickDto.put(playerDto.forceId, trickInfo);
        final String newtrickInfo = TrickFactory.getTrickInfoInScenarioWithMap(city.trickDto);
        final int suc = dataGetter.getPlayerScenarioCityDao().updateTrickInfo(newtrickInfo, playerDto.playerId, jubenId, cityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (suc > 0) {
            dataGetter.getChatUtil().sendTrickChat(playerDto.playerId, stratagem.getName());
            final double trickAdd = dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 11);
            final double playerCd = this.trick.getCd() * 60 * 1000 * (1.0 - trickAdd / 100.0);
            final long trickcd = (long)(new Date().getTime() + playerCd);
            dataGetter.getPlayerGeneralCivilDao().updateCd(playerDto.playerId, vid, new Date(trickcd));
            doc.createElement("stratagemId", trickId);
            doc.createElement("type", this.trick.getType());
            doc.createElement("forceSum", forceSum);
            doc.createElement("cityId", cityId);
            doc.createElement("cd", playerCd);
            doc.createElement("protectCd", stratagem.getPar4() * 60 * 1000);
            doc.createElement("Quality", stratagem.getQuality());
            doc.createElement("pos", vid);
            doc.endObject();
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
            final int forceSum = dataGetter.getJuBenService().trickReduceForce(playerId, cityId, this.trick.getPar2(), this.trick.getPar3());
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("stratagemId", this.trick.getId());
            final int terrian = this.trick.getPar1();
            if (terrian == 0) {
                if (dto.terrianType == 1) {
                    doc.createElement("type", "huogong");
                }
                else if (dto.terrianType == 3) {
                    doc.createElement("type", "shuigong");
                }
                else if (dto.terrianType == 2) {
                    doc.createElement("type", "luoshi");
                }
                else {
                    doc.createElement("type", this.trick.getType());
                }
            }
            else {
                doc.createElement("type", this.trick.getType());
            }
            doc.createElement("forceSum", forceSum);
            doc.createElement("cityId", cityId);
            doc.createElement("protectCd", this.trick.getPar4() * 60 * 1000);
            doc.createElement("Quality", this.trick.getQuality());
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_JUBEN_NPCTRICK, doc.toByte());
        }
        catch (Exception e) {
            ITrick.errorLogger.error("Forces doUseTrickNpc fail...playerId:" + playerId + "cityId:" + cityId);
            ITrick.errorLogger.error(e.getMessage());
            ITrick.errorLogger.error(this, e);
        }
    }
}
