package com.reign.gcld.civiltrick.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.civiltrick.trick.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.world.domain.*;
import com.reign.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.juben.service.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.team.service.*;
import java.util.*;
import com.reign.gcld.juben.common.*;

@Component("cilvilTrickService")
public class CilvilTrickService implements ICilvilTrickService
{
    private static final Logger errorLog;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    static ReadWriteLock lock2;
    
    static {
        errorLog = CommonLog.getLog(CilvilTrickService.class);
        CilvilTrickService.lock2 = new ReentrantReadWriteLock();
    }
    
    @Override
    public byte[] getPitchLocation(final int playerId, final int generalId, final int trickId) {
        if (this.trickNotOpen(playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)trickId);
        if (stratagem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_TRICK);
        }
        final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        if (list == null || list.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final ITrick trick = TrickFactory.getTrick(stratagem);
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilList(playerId);
        final List<Integer> trickList = new ArrayList<Integer>();
        for (final PlayerGeneralCivil pgc : pgcList) {
            final General general = (General)this.generalCache.get((Object)pgc.getGeneralId());
            trickList.add(general.getStratagemId());
        }
        if (!trickList.contains(trickId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_TRICK);
        }
        if (trick == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_TRICK);
        }
        return trick.getPitchLocation(this.dataGetter, playerId, list);
    }
    
    private boolean trickNotOpen(final int playerId) {
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        return cs[50] != '1';
    }
    
    @Override
    public byte[] useTrick(final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int trickIndex, final int type) {
        if (this.trickNotOpen(playerDto.playerId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10020, type);
        }
        final PlayerGeneralCivil pgC = this.playerGeneralCivilDao.getCivil(playerDto.playerId, trickIndex);
        if (pgC == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final General general = (General)this.generalCache.get((Object)trickIndex);
        if (general.getStratagemId() != trickId) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final Date cDate = pgC.getCd();
        if (cDate != null && cDate.getTime() > new Date().getTime()) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.TRICK_IN_CD, type);
        }
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)trickId);
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final ITrick trick = TrickFactory.getTrick(stratagem);
        if (trick == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final PlayerWorld pw = this.dataGetter.getPlayerWorldDao().read(playerDto.playerId);
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
        if (!attedSet.contains(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldFarmCache.forceCityIdMap.values().contains(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.MAINCITY_CANNOT_BE_TRICKED, type);
        }
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        if (worldCity == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10020, type);
        }
        if (worldCity.getTerrainEffectType() == 4 && !stratagem.getType().equals("kongcheng")) {
            return TrickFactory.getJsonForTrick(State.FAIL, stratagem.getError(), type);
        }
        synchronized (CityService.cityBatIdSet.get(cityId)) {
            // monitorexit((String)CityService.cityBatIdSet.get((Object)Integer.valueOf(cityId)))
            return trick.useTrick(this.dataGetter, playerDto, generalId, trickId, cityId, trickIndex, type);
        }
    }
    
    @Override
    public Stratagem afterStateTrick(final int cityId, final int type, final int forceId) {
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return null;
        }
        final String trickInfo = city.getTrickinfo();
        final Tuple<Integer, String> re = TrickFactory.getTrick(trickInfo, type, forceId, this.dataGetter.getStratagemCache().getTrickMap());
        if (re == null) {
            return null;
        }
        final long cd = Long.parseLong(re.right);
        if (cd <= new Date().getTime()) {
            return null;
        }
        final int trickId = re.left;
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)trickId);
        final Stratagem stratagemCopy = this.getStratagemByCopy(stratagem);
        final long time = System.currentTimeMillis();
        if (stratagemCopy.getType().equalsIgnoreCase("huangbao")) {
            stratagemCopy.setError(MessageFormatter.format(LocalMessages.TRICK_LIES, new Object[] { TimeUtil.getTimeLeft(cd - time) }));
        }
        else if (stratagemCopy.getType().equalsIgnoreCase("kongcheng")) {
            stratagemCopy.setError(MessageFormatter.format(LocalMessages.CITY_IN_PROTECT, new Object[] { TimeUtil.getTimeLeft(cd - time) }));
        }
        return stratagemCopy;
    }
    
    private Stratagem getStratagemByCopy(final Stratagem stratagem) {
        final Stratagem resultStratagem = new Stratagem();
        resultStratagem.setCd(stratagem.getCd());
        resultStratagem.setError(stratagem.getError());
        resultStratagem.setId(stratagem.getId());
        resultStratagem.setIntro(stratagem.getIntro());
        resultStratagem.setLastCd(stratagem.getLastCd());
        resultStratagem.setName(stratagem.getName());
        resultStratagem.setPar1(stratagem.getPar1());
        resultStratagem.setPar1Intro(stratagem.getPar1Intro());
        resultStratagem.setPar2(stratagem.getPar2());
        resultStratagem.setPar3(stratagem.getPar3());
        resultStratagem.setPar2Intro(stratagem.getPar2Intro());
        resultStratagem.setPar3Intro(stratagem.getPar3Intro());
        resultStratagem.setPar4(stratagem.getPar4());
        resultStratagem.setPar4Intro(stratagem.getPar4Intro());
        resultStratagem.setPic(stratagem.getPic());
        resultStratagem.setQuality(stratagem.getQuality());
        resultStratagem.setType(stratagem.getType());
        return resultStratagem;
    }
    
    @Override
    public Tuple<Double, Double> afterStateTrick2(final int cityId, final int attForceId, final int defForceId) {
        final Tuple<Double, Double> result = new Tuple();
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return null;
        }
        result.left = 0.0;
        result.right = 0.0;
        final String trickInfo = city.getTrickinfo();
        final Tuple<Integer, String> attInspire = TrickFactory.getTrick(trickInfo, 1, attForceId, this.dataGetter.getStratagemCache().getTrickMap());
        final Tuple<Integer, String> defInspire = TrickFactory.getTrick(trickInfo, 1, defForceId, this.dataGetter.getStratagemCache().getTrickMap());
        final Tuple<Integer, String> attShrink = TrickFactory.getTrick(trickInfo, 2, attForceId, this.dataGetter.getStratagemCache().getTrickMap());
        final Tuple<Integer, String> defShrink = TrickFactory.getTrick(trickInfo, 2, defForceId, this.dataGetter.getStratagemCache().getTrickMap());
        final long curTime = System.currentTimeMillis();
        Stratagem stratagem = null;
        if (attInspire != null && Long.parseLong(attInspire.right) > curTime) {
            stratagem = (Stratagem)this.stratagemCache.get((Object)attInspire.left);
            final Tuple<Double, Double> tuple = result;
            tuple.left = tuple.left + stratagem.getPar1() / 100.0;
        }
        if (defShrink != null && Long.parseLong(defShrink.right) > curTime) {
            stratagem = (Stratagem)this.stratagemCache.get((Object)defShrink.left);
            final Tuple<Double, Double> tuple2 = result;
            tuple2.left = tuple2.left - stratagem.getPar1() / 100.0;
        }
        if (defInspire != null && Long.parseLong(defInspire.right) > curTime) {
            stratagem = (Stratagem)this.stratagemCache.get((Object)defInspire.left);
            final Tuple<Double, Double> tuple3 = result;
            tuple3.right = tuple3.right + stratagem.getPar1() / 100.0;
        }
        if (attShrink != null && Long.parseLong(attShrink.right) > curTime) {
            stratagem = (Stratagem)this.stratagemCache.get((Object)attShrink.left);
            final Tuple<Double, Double> tuple4 = result;
            tuple4.right = tuple4.right - stratagem.getPar1() / 100.0;
        }
        return result;
    }
    
    @Override
    public Tuple<Integer, Stratagem> afterTrapTrick(final int cityId, final int forceId) {
        final Tuple<Integer, Stratagem> result = new Tuple();
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return null;
        }
        final String trickInfo = city.getTrickinfo();
        final Tuple<Integer, String> re = TrickFactory.getTrick(trickInfo, 4, forceId, this.dataGetter.getStratagemCache().getTrickMap());
        if (re == null) {
            return null;
        }
        if (re.right.equals("")) {
            return null;
        }
        final String[] splitString = re.right.split("-");
        final int trickId = re.left;
        final long time = Long.parseLong(splitString[0]);
        if (time < new Date().getTime()) {
            return null;
        }
        final Integer times = Integer.valueOf(splitString[1]);
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)trickId);
        result.left = times;
        result.right = stratagem;
        return result;
    }
    
    @Override
    public void removeTrap(final int cityId, final Map<String, List<Integer>> map) {
        synchronized (CityService.cityBatIdSet.get(cityId)) {
            final City city = this.cityDao.read(cityId);
            final StringBuffer sb = new StringBuffer();
            if (city == null) {
                // monitorexit((String)CityService.cityBatIdSet.get((Object)Integer.valueOf(cityId)))
                return;
            }
            final String trickInfo = city.getTrickinfo();
            final int first = 0;
            if (!StringUtils.isBlank(trickInfo)) {
                String[] split;
                for (int length = (split = trickInfo.split("#")).length, i = 0; i < length; ++i) {
                    final String s = split[i];
                    if (!StringUtils.isBlank(s)) {
                        final String[] a = s.split("-");
                        if (map.get("xianjing").contains(Integer.valueOf(a[0]))) {
                            break;
                        }
                        if (first != 0) {
                            sb.append("#");
                        }
                        else {
                            sb.append(s);
                        }
                    }
                }
            }
            this.cityDao.updateTrickInfo(cityId, sb.toString());
        }
        // monitorexit((String)CityService.cityBatIdSet.get((Object)Integer.valueOf(cityId)))
    }
    
    @Override
    public Tuple<List<Stratagem>, List<Stratagem>> getStateList(final int cityId, final int attForceId, final int defForceId) {
        final List<Stratagem> attList = new ArrayList<Stratagem>();
        final List<Stratagem> defList = new ArrayList<Stratagem>();
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return null;
        }
        final String trickInfo = city.getTrickinfo();
        if (StringUtils.isBlank(trickInfo)) {
            return null;
        }
        Tuple<Integer, String> attInspire = null;
        Tuple<Integer, String> defInspire = null;
        Tuple<Integer, String> attShrink = null;
        Tuple<Integer, String> defShrink = null;
        try {
            attInspire = TrickFactory.getTrick(trickInfo, 1, attForceId, this.dataGetter.getStratagemCache().getTrickMap());
            defInspire = TrickFactory.getTrick(trickInfo, 1, defForceId, this.dataGetter.getStratagemCache().getTrickMap());
            attShrink = TrickFactory.getTrick(trickInfo, 2, attForceId, this.dataGetter.getStratagemCache().getTrickMap());
            defShrink = TrickFactory.getTrick(trickInfo, 2, defForceId, this.dataGetter.getStratagemCache().getTrickMap());
        }
        catch (Exception e) {
            CilvilTrickService.errorLog.error("CilvilTrickService getStateList ", e);
        }
        final long curTime = System.currentTimeMillis();
        if (attInspire != null && Long.parseLong(attInspire.right) > curTime) {
            attList.add((Stratagem)this.stratagemCache.get((Object)attInspire.left));
        }
        if (attShrink != null && Long.parseLong(attShrink.right) > curTime) {
            attList.add((Stratagem)this.stratagemCache.get((Object)attShrink.left));
        }
        if (defInspire != null && Long.parseLong(defInspire.right) > curTime) {
            defList.add((Stratagem)this.stratagemCache.get((Object)defInspire.left));
        }
        if (defShrink != null && Long.parseLong(defShrink.right) > curTime) {
            defList.add((Stratagem)this.stratagemCache.get((Object)defShrink.left));
        }
        final Tuple<List<Stratagem>, List<Stratagem>> result = new Tuple();
        result.left = attList;
        result.right = defList;
        return result;
    }
    
    @Override
    public byte[] useTrickInScenario(final PlayerDto playerDto, final int generalId, final int trickId, final int cityId, final int trickIndex, final int type) {
        if (this.trickNotOpen(playerDto.playerId)) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10020, type);
        }
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerDto.playerId);
        if (dto == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.JUBEN_CANNT_QUIT, type);
        }
        final Map<Integer, JuBenCityDto> map = dto.juBenCityDtoMap;
        final int jubenId = dto.juBen_id;
        if (map == null || map.get(cityId) == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.T_COMM_10020, type);
        }
        final PlayerGeneralCivil pgC = this.playerGeneralCivilDao.getCivil(playerDto.playerId, trickIndex);
        if (pgC == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final General general = (General)this.generalCache.get((Object)trickIndex);
        if (general.getStratagemId() != trickId) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final Date cDate = pgC.getCd();
        if (cDate != null && cDate.getTime() > new Date().getTime()) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.TRICK_IN_CD, type);
        }
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)trickId);
        if (stratagem == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final ITrick trick = TrickFactory.getTrick(stratagem);
        if (trick == null) {
            return TrickFactory.getJsonForTrick(State.FAIL, LocalMessages.WRONG_TRICK, type);
        }
        final Lock writeLock = CilvilTrickService.lock2.writeLock();
        writeLock.lock();
        try {
            return trick.useTrickInScenario(this.dataGetter, playerDto, generalId, trickId, cityId, trickIndex, type, map, jubenId);
        }
        finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public void useTrickForAction(final int cityId, final int stratagemId, final int playerId, final int scenarioId) {
        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)stratagemId);
        if (stratagem == null) {
            return;
        }
        final ITrick trick = TrickFactory.getTrick(stratagem);
        if (trick == null) {
            return;
        }
        final Lock writeLock = CilvilTrickService.lock2.writeLock();
        writeLock.lock();
        try {
            trick.useTrickNpc(cityId, playerId, this.dataGetter, scenarioId);
        }
        finally {
            writeLock.unlock();
        }
        writeLock.unlock();
    }
    
    @Override
    public OperationResult hasTrick(final JuBenCityDto targetCity) {
        if (targetCity == null) {
            return null;
        }
        final HashMap<String, TrickDto> map = targetCity.trickDto.get(0);
        if (map != null) {
            final long now = System.currentTimeMillis();
            final TrickDto dto = map.get("kongcheng");
            if (dto != null && dto.getLastTime() > now) {
                final long time = System.currentTimeMillis();
                return new OperationResult(false, MessageFormatter.format(LocalMessages.CITY_IN_PROTECT, new Object[] { TimeUtil.getTimeLeft(dto.getLastTime() - time) }));
            }
        }
        return new OperationResult(true);
    }
    
    @Override
    public void updateTrickInfo(final int cityId, final int forceId, final Stratagem stratagem) {
        final City city = this.cityDao.read(cityId);
        final String trickInfo = city.getTrickinfo();
        final String newTrickInfo = TrickFactory.getNewInfo(trickInfo, stratagem, true, 0L, forceId, this.stratagemCache.getTrickMap());
        this.cityDao.updateTrickInfo(cityId, newTrickInfo);
    }
}
