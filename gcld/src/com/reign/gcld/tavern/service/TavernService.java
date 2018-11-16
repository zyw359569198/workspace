package com.reign.gcld.tavern.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.tavern.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.tavern.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.json.*;
import com.reign.gcld.civiltrick.trick.*;
import com.reign.gcld.tavern.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.util.*;
import com.reign.gcld.juben.common.*;
import java.util.*;
import com.reign.gcld.sdata.common.*;

@Component("tavernService")
public class TavernService implements ITavernService
{
    @Autowired
    private IPlayerGeneralRefreshDao playerGeneralRefreshDao;
    @Autowired
    private IPlayerTavernDao playerTavernDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private GeneralRecruitCache generalRecruitCache;
    @Autowired
    private TavernStatCache tavernStatCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private StringCCache stringCCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private GeneralPositionCache generalPositionCache;
    @Autowired
    private FightStrategiesCache fightStrategiesCache;
    @Autowired
    private TacticCache tacticCache;
    
    @Transactional
    @Override
    public byte[] getGeneral(final int playerId, final int type) {
        final Player player = this.playerDao.read(playerId);
        int functionId;
        if (type == 1) {
            functionId = 44;
        }
        else {
            functionId = 45;
        }
        boolean functionOpen = false;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[functionId] == '1') {
            functionOpen = true;
        }
        if (!functionOpen) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        List<PlayerGeneralRefresh> list = null;
        if (!this.checkFunctionIsOpen(55, playerId, pa)) {
            this.refreshGeneral(playerId, type, true, true);
            list = this.playerGeneralRefreshDao.getListByPlayerId(playerId);
        }
        else {
            list = this.playerGeneralRefreshDao.getListByPlayerId(playerId);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)7);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(list, type, player, ci));
    }
    
    @Transactional
    @Override
    public byte[] refreshGeneral(final int playerId, final int type, final boolean firstTime, final boolean flag) {
        if (type != 1 && type != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        if (!firstTime && !this.checkFunctionIsOpen(55, playerId, pa)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Player player = this.playerDao.read(playerId);
        final Date nowDate = new Date();
        PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
        int nowState = 1;
        if (playerTavern == null) {
            playerTavern = new PlayerTavern();
            playerTavern.setCivilRefreshTime(0);
            playerTavern.setMilitaryRefreshTime(0);
            playerTavern.setPlayerId(playerId);
            playerTavern.setTavernState(nowState);
            playerTavern.setNextCivilDate(nowDate);
            playerTavern.setNextMilitaryDate(nowDate);
            this.playerTavernDao.create(playerTavern);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)7);
        if (!firstTime) {
            final int maxGeneralNum = this.getMaxGeneralNum(playerId, player.getPlayerLv(), type);
            if (maxGeneralNum == 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_MAX_ZERO);
            }
            Date nextRefreshDate = null;
            if (type == 1) {
                nextRefreshDate = playerTavern.getNextCivilDate();
            }
            else if (type == 2) {
                nextRefreshDate = playerTavern.getNextMilitaryDate();
            }
            if (CDUtil.getCD(nextRefreshDate, nowDate) >= 3600000L) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_IN_REFRESH_CD);
            }
            final int copper = 0;
            if (!this.playerResourceDao.consumeCopper(playerId, copper, "\u62db\u52df\u6b66\u5c06\u6263\u9664\u94f6\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
            final int beforeState = playerTavern.getTavernState();
            nowState = this.decideNextState(player.getPlayerLv(), beforeState);
            playerTavern.setTavernState(nowState);
            if (type == 1) {
                final Date recordDate = playerTavern.getNextCivilDate();
                final Date startDate = recordDate.after(nowDate) ? recordDate : nowDate;
                final Date nextDate = new Date(startDate.getTime() + 1200000L);
                playerTavern.setCivilRefreshTime(playerTavern.getCivilRefreshTime() + 1);
                playerTavern.setNextCivilDate(nextDate);
            }
            if (type == 2) {
                final Date recordDate = playerTavern.getNextMilitaryDate();
                final Date startDate = recordDate.after(nowDate) ? recordDate : nowDate;
                final Date nextDate = new Date(startDate.getTime() + 1200000L);
                playerTavern.setCivilRefreshTime(playerTavern.getCivilRefreshTime() + 1);
                playerTavern.setNextMilitaryDate(nextDate);
            }
            this.playerTavernDao.updatePlayerTavern(playerTavern);
        }
        final List<PlayerGeneralRefresh> resultList = new ArrayList<PlayerGeneralRefresh>();
        final Set<Integer> positions = new HashSet<Integer>();
        final List<PlayerGeneralRefresh> beforelist = this.playerGeneralRefreshDao.getListByPlayerId(playerId);
        for (final PlayerGeneralRefresh pgf : beforelist) {
            final General general = (General)this.generalCache.get((Object)pgf.getGeneralId());
            if (general.getType() == type) {
                if (pgf.getLocked() == 1) {
                    resultList.add(pgf);
                    positions.add(pgf.getPosition());
                }
                else {
                    this.playerGeneralRefreshDao.deleteById(pgf.getVId());
                }
            }
        }
        if (resultList.size() == 5) {
            return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, type, player, ci));
        }
        if (!StringUtils.isBlank(playerTavern.getLockGeneralId())) {
            final String[] lockIds = playerTavern.getLockGeneralId().split(";");
            int position = 1;
            String[] array;
            for (int length = (array = lockIds).length, l = 0; l < length; ++l) {
                final String lockId = array[l];
                final int lockGeneralId = Integer.valueOf(lockId);
                final General general2 = (General)this.generalCache.get((Object)lockGeneralId);
                if (!positions.contains(position) && general2.getType() == type) {
                    final PlayerGeneralRefresh pgr = new PlayerGeneralRefresh();
                    pgr.setPlayerId(playerId);
                    pgr.setGeneralId(general2.getId());
                    pgr.setLocked(0);
                    pgr.setBought(0);
                    this.decideGeneralPrice(pgr, player.getPlayerLv(), true);
                    pgr.setPosition(position);
                    positions.add(position);
                    resultList.add(pgr);
                    this.playerGeneralRefreshDao.create(pgr);
                    if (resultList.size() == 5) {
                        return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, type, player, ci));
                    }
                    ++position;
                }
            }
        }
        final Map<Integer, List<General>> dropGeneralMap = this.getDropGeneral(playerTavern, type);
        if (nowState != 1) {
            List<General> optionalList = this.generalCache.getGeneralByQuality(nowState, type);
            if (nowState > 3) {
                optionalList = dropGeneralMap.get(nowState);
            }
            final List<General> stateGeneralList = this.filterGeneralList(optionalList, resultList, playerTavern, type);
            if (stateGeneralList.size() > 0) {
                final int index = WebUtil.nextInt(stateGeneralList.size());
                final General stateGeneral = stateGeneralList.get(index);
                final PlayerGeneralRefresh pgr2 = new PlayerGeneralRefresh();
                pgr2.setPlayerId(playerId);
                pgr2.setGeneralId(stateGeneral.getId());
                pgr2.setLocked(0);
                pgr2.setBought(0);
                this.decideGeneralPrice(pgr2, player.getPlayerLv(), false);
                this.decideGeneralPosition(pgr2, positions);
                resultList.add(pgr2);
                this.playerGeneralRefreshDao.create(pgr2);
            }
        }
        if (resultList.size() == 5) {
            return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, type, player, ci));
        }
        final List<General> unstateGeneralList = new ArrayList<General>();
        for (int state = 1; state < nowState; ++state) {
            if (state > 3) {
                final List<General> dropStateGeneral = dropGeneralMap.get(state);
                if (dropStateGeneral != null) {
                    unstateGeneralList.addAll(dropStateGeneral);
                }
            }
            else {
                unstateGeneralList.addAll(this.generalCache.getGeneralByQuality(state, type));
            }
        }
        if (nowState == 1) {
            unstateGeneralList.addAll(this.generalCache.getGeneralByQuality(1, type));
        }
        List<General> hasDefeatedGenerals = null;
        if (flag) {
            String militaryInfo = "";
            if (type == 1) {
                militaryInfo = ((playerTavern.getCivilInfo() == null) ? "" : playerTavern.getCivilInfo());
            }
            else if (type == 2) {
                militaryInfo = ((playerTavern.getMilitaryInfo() == null) ? "" : playerTavern.getMilitaryInfo());
            }
            hasDefeatedGenerals = this.getDefeatGeneral(militaryInfo);
        }
        if (hasDefeatedGenerals != null) {
            for (int i = 0; i < hasDefeatedGenerals.size(); ++i) {
                if (!unstateGeneralList.contains(hasDefeatedGenerals.get(i))) {
                    unstateGeneralList.add(0, hasDefeatedGenerals.get(i));
                }
            }
        }
        final List<General> afterFilterList = this.filterGeneralList(unstateGeneralList, resultList, playerTavern, type);
        final int numNeedFill = 5 - resultList.size();
        if (afterFilterList.size() > numNeedFill) {
            if (!flag) {
                Collections.shuffle(afterFilterList);
            }
            for (int j = 0; j < numNeedFill; ++j) {
                final General unstateGeneral = afterFilterList.get(j);
                final PlayerGeneralRefresh pgr3 = new PlayerGeneralRefresh();
                pgr3.setPlayerId(playerId);
                pgr3.setGeneralId(unstateGeneral.getId());
                pgr3.setLocked(0);
                pgr3.setBought(0);
                this.decideGeneralPrice(pgr3, player.getPlayerLv(), false);
                this.decideGeneralPosition(pgr3, positions);
                resultList.add(pgr3);
                this.playerGeneralRefreshDao.create(pgr3);
            }
        }
        else {
            for (int k = 0; k < afterFilterList.size(); ++k) {
                final General unstateGeneral = afterFilterList.get(k);
                final PlayerGeneralRefresh pgr3 = new PlayerGeneralRefresh();
                pgr3.setPlayerId(playerId);
                pgr3.setGeneralId(unstateGeneral.getId());
                pgr3.setLocked(0);
                pgr3.setBought(0);
                this.decideGeneralPrice(pgr3, player.getPlayerLv(), false);
                this.decideGeneralPosition(pgr3, positions);
                resultList.add(pgr3);
                this.playerGeneralRefreshDao.create(pgr3);
            }
        }
        if (!firstTime) {
            TaskMessageHelper.sendTavernRefreshTaskMessage(playerId);
        }
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, type, player, ci));
    }
    
    private List<General> getDefeatGeneral(final String militaryInfo) {
        final List<General> result = new ArrayList<General>();
        if (StringUtils.isBlank(militaryInfo)) {
            return null;
        }
        final String[] mils = militaryInfo.split(",");
        if (mils.length <= 0) {
            return null;
        }
        String[] array;
        for (int length = (array = mils).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final General general = (General)this.generalCache.get((Object)Integer.valueOf(s));
                if (general != null) {
                    result.add(general);
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean checkFunctionIsOpen(final int functionId42, final int playerId, final PlayerAttribute pa) {
        if (pa == null) {
            return false;
        }
        final String functionString = pa.getFunctionId();
        if (StringUtils.isBlank(functionString)) {
            return false;
        }
        final char[] cs = functionString.toCharArray();
        return cs[functionId42] == '1';
    }
    
    private byte[] getResult(final List<PlayerGeneralRefresh> list, final int type, final Player player, final Chargeitem ci) {
        final Date nowDate = new Date();
        final JsonDocument doc = new JsonDocument();
        final List<PlayerGeneral> retiredGeneralList = this.playerGeneralDao.getGeneralList(player.getPlayerId());
        doc.startObject();
        doc.createElement("vipLimit", ci.getLv());
        doc.startArray("Generals");
        for (final PlayerGeneralRefresh pg : list) {
            final General general = (General)this.generalCache.get((Object)pg.getGeneralId());
            PlayerGeneral retiredGeneral = null;
            for (final PlayerGeneral tempGeneral : retiredGeneralList) {
                if (tempGeneral.getGeneralId().equals(general.getId())) {
                    retiredGeneral = tempGeneral;
                    break;
                }
            }
            if (general.getType() == type) {
                doc.startObject();
                doc.createElement("generalId", pg.getGeneralId());
                doc.createElement("position", pg.getPosition());
                doc.createElement("name", general.getName());
                doc.createElement("quality", general.getQuality());
                doc.createElement("pic", general.getPic());
                if (type == 1) {
                    final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general.getStratagemId());
                    if (stratagem != null) {
                        TrickFactory.getTrickInfo(doc, stratagem);
                    }
                }
                else if (type == 2) {
                    if (retiredGeneral != null) {
                        doc.createElement("leader", GeneralCommon.getShowAttribute(general.getLeader(), retiredGeneral.getLeader()));
                        doc.createElement("strength", GeneralCommon.getShowAttribute(general.getStrength(), retiredGeneral.getStrength()));
                    }
                    else {
                        doc.createElement("leader", general.getLeader());
                        doc.createElement("strength", general.getStrength());
                    }
                    final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
                    doc.createElement("troopId", troop.getSerial());
                    doc.createElement("troopType", troop.getSerial());
                    doc.createElement("troopName", troop.getName());
                    doc.createElement("troopQuality", troop.getQuality());
                    doc.createElement("gIntro", general.getIntro());
                    doc.createElement("tacticId", general.getTacticId());
                    final Tactic tactic = (Tactic)this.tacticCache.get((Object)general.getTacticId());
                    if (tactic != null) {
                        doc.createElement("tacName", tactic.getName());
                        doc.createElement("tacRange", tactic.getRange());
                        doc.createElement("tacIntro", tactic.getIntro());
                    }
                    else {
                        doc.createElement("tacName", "");
                        doc.createElement("tacRange", "");
                        doc.createElement("tacIntro", "");
                    }
                    if (troop.getTsstList() != null) {
                        doc.startArray("tts");
                        FightStrategies fs = null;
                        for (final TerrainStrategySpecDto ts : troop.getTsstList()) {
                            doc.startObject();
                            doc.createElement("terrainId", ts.terrainId);
                            fs = (FightStrategies)this.fightStrategiesCache.get((Object)ts.strategyId);
                            doc.createElement("strategyId", ts.strategyId);
                            doc.createElement("sName", fs.getName());
                            doc.createElement("show", ts.show);
                            doc.endObject();
                        }
                        doc.endArray();
                    }
                    doc.startArray("terrain");
                    for (final Integer key : troop.getTerrains().keySet()) {
                        final TroopTerrain tt = troop.getTerrains().get(key);
                        if (tt.getShow() != 0) {
                            if (tt.getShow() == 2 && tt.getDefEffect() > 0) {
                                doc.startObject();
                                doc.createElement("tType", key);
                                doc.createElement("tShow", tt.getShow());
                                doc.createElement("terrainQ", tt.getDefQuality());
                                doc.createElement("tValue", tt.getDefEffect());
                                doc.endObject();
                            }
                            else {
                                if (tt.getAttEffect() <= 0) {
                                    continue;
                                }
                                doc.startObject();
                                doc.createElement("tType", key);
                                doc.createElement("tShow", tt.getShow());
                                doc.createElement("terrainQ", tt.getAttQuality());
                                doc.createElement("tValue", tt.getAttEffect());
                                doc.endObject();
                            }
                        }
                    }
                    doc.endArray();
                }
                doc.createElement("bought", pg.getBought() == 1);
                if (general.getQuality() == 6) {
                    doc.createElement("hotDegree", 1);
                }
                else if (general.getQuality() == 4 || general.getQuality() == 5) {
                    if (pg.getIsCheap() == 1) {
                        doc.createElement("hotDegree", 2);
                    }
                }
                else {
                    doc.createElement("hotDegree", 0);
                }
                doc.createElement("price", pg.getPrice());
                doc.createElement("isGold", pg.getIsGold());
                doc.createElement("locked", (pg.getLocked() == null) ? 0 : pg.getLocked());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("nowGeneralNum", this.getNowGeneralNum(player.getPlayerId(), type));
        doc.createElement("maxGeneralNum", this.getMaxGeneralNum(player.getPlayerId(), player.getPlayerLv(), type));
        final PlayerTavern playerTavern = this.playerTavernDao.read(player.getPlayerId());
        if (this.checkFunctionIsOpen(55, player.getPlayerId(), this.playerAttributeDao.read(player.getPlayerId())) && playerTavern != null) {
            if (type == 1) {
                doc.createElement("refreshCD", CDUtil.getCD(playerTavern.getNextCivilDate(), nowDate));
            }
            else if (type == 2) {
                doc.createElement("refreshCD", CDUtil.getCD(playerTavern.getNextMilitaryDate(), nowDate));
            }
        }
        doc.createElement("refreshCopper", 0);
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(player.getPlayerId());
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilList(player.getPlayerId());
        doc.startArray("myGeneralList");
        for (final PlayerGeneralMilitary pgm : pgmList) {
            final General general2 = (General)this.generalCache.get((Object)pgm.getGeneralId());
            doc.startObject();
            doc.createElement("generalId", general2.getId());
            doc.createElement("generalName", general2.getName());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("generalPic", general2.getPic());
            doc.createElement("generalQuality", general2.getQuality());
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("myCivilList");
        for (final PlayerGeneralCivil pgc : pgcList) {
            final General general2 = (General)this.generalCache.get((Object)pgc.getGeneralId());
            final Stratagem stratagem2 = (Stratagem)this.stratagemCache.get((Object)general2.getStratagemId());
            doc.startObject();
            doc.createElement("generalId", general2.getId());
            doc.createElement("generalName", general2.getName());
            doc.createElement("generalLv", pgc.getLv());
            doc.createElement("generalPic", general2.getPic());
            doc.createElement("generalQuality", general2.getQuality());
            if (stratagem2 != null) {
                TrickFactory.getTrickInfo(doc, stratagem2);
                doc.createElement("stratagemId", stratagem2.getId());
                doc.createElement("stratagemIntro", stratagem2.getIntro());
            }
            doc.createElement("pic", general2.getPic());
            doc.createElement("cilvilId", general2.getId());
            if (pgc.getCd() != null) {
                final long cd = pgc.getCd().getTime() - new Date().getTime();
                doc.createElement("cd", (cd > 0L) ? cd : 0L);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    private int decideNextState(final int playerLevel, final int beforeState) {
        final List<TavernStat> list = this.tavernStatCache.getTavernStatList(beforeState);
        final int length = list.size();
        final double[] probs = new double[length];
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                probs[i] = list.get(i).getProb();
            }
            else {
                probs[i] = probs[i - 1] + list.get(i).getProb();
            }
        }
        probs[length - 1] = 1.0;
        final Double rand = WebUtil.nextDouble();
        int index = 0;
        for (int k = 0; k < probs.length; ++k) {
            if (k == 0) {
                if (rand <= probs[k]) {
                    index = k;
                    break;
                }
            }
            else if (rand > probs[k - 1] && rand <= probs[k]) {
                index = k;
                break;
            }
        }
        return list.get(index).getNextStat();
    }
    
    private void decideGeneralPrice(final PlayerGeneralRefresh pgr, final int playerLevel, final boolean mustCopper) {
        final GeneralRecruit gr = this.generalRecruitCache.getByGeneralId(pgr.getGeneralId());
        final Double copperProb = 1.0 - gr.getGoldProb();
        final boolean isCopper = WebUtil.nextDouble() < copperProb;
        int basePrice = 0;
        if (mustCopper) {
            pgr.setIsGold(0);
            basePrice = gr.getCopperMax();
        }
        else if (isCopper) {
            pgr.setIsGold(0);
            basePrice = gr.getCopperMax();
        }
        else {
            pgr.setIsGold(1);
            basePrice = gr.getGoldMax();
        }
        final StringC priceRate = (StringC)this.stringCCache.get((Object)3);
        final StringC rateProb = (StringC)this.stringCCache.get((Object)4);
        final String[] priceRates = priceRate.getValue().split(",");
        final String[] rateProbs = rateProb.getValue().split(",");
        final double[] probs = new double[rateProbs.length];
        for (int i = 0; i < rateProbs.length; ++i) {
            if (i == 0) {
                probs[i] = Double.valueOf(rateProbs[i]);
            }
            else {
                probs[i] = probs[i - 1] + Double.valueOf(rateProbs[i]);
            }
        }
        final Double rand = WebUtil.nextDouble();
        int index = 0;
        for (int k = 0; k < probs.length; ++k) {
            if (k == 0) {
                if (rand <= probs[k]) {
                    index = k;
                    break;
                }
            }
            else if (rand > probs[k - 1] && rand <= probs[k]) {
                index = k;
                break;
            }
        }
        final double finalRate = Double.valueOf(priceRates[index]);
        if (index == 0) {
            pgr.setIsCheap(1);
        }
        else {
            pgr.setIsCheap(0);
        }
        final int finalPrice = (int)Math.round(finalRate * basePrice);
        pgr.setPrice(finalPrice);
    }
    
    private void decideGeneralPosition(final PlayerGeneralRefresh pgr, final Set<Integer> positions) {
        int now;
        for (now = WebUtil.nextInt(5) + 1; positions.contains(now); now = WebUtil.nextInt(5) + 1) {}
        positions.add(now);
        pgr.setPosition(now);
    }
    
    private List<General> filterGeneralList(final List<General> list, final List<PlayerGeneralRefresh> currentList, final PlayerTavern playerTavern, final int type) {
        final List<General> theList = new ArrayList<General>();
        final int playerId = playerTavern.getPlayerId();
        List<PlayerGeneralCivil> nowCivilList = new ArrayList<PlayerGeneralCivil>();
        List<PlayerGeneralMilitary> nowMilitaryList = new ArrayList<PlayerGeneralMilitary>();
        if (type == 1) {
            nowCivilList = this.playerGeneralCivilDao.getCivilList(playerId);
        }
        if (type == 2) {
            nowMilitaryList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        }
        if (list != null) {
            for (final General general : list) {
                final int generalId = general.getId();
                final GeneralRecruit gr = this.generalRecruitCache.getByGeneralId(generalId);
                if (gr == null) {
                    continue;
                }
                if (general.getType() != type) {
                    continue;
                }
                if (general.getType() == 1) {
                    if (gr.getMinRefurTime() > playerTavern.getCivilRefreshTime()) {
                        continue;
                    }
                    boolean alreadyRecruited = false;
                    for (final PlayerGeneralCivil pgc : nowCivilList) {
                        if (pgc.getGeneralId() == generalId) {
                            alreadyRecruited = true;
                            break;
                        }
                    }
                    if (alreadyRecruited) {
                        continue;
                    }
                }
                if (general.getType() == 2) {
                    if (gr.getMinRefurTime() > playerTavern.getMilitaryRefreshTime()) {
                        continue;
                    }
                    boolean alreadyRecruited = false;
                    for (final PlayerGeneralMilitary pgm : nowMilitaryList) {
                        if (pgm.getGeneralId() == generalId) {
                            alreadyRecruited = true;
                            break;
                        }
                    }
                    if (alreadyRecruited) {
                        continue;
                    }
                }
                boolean contain = false;
                for (final PlayerGeneralRefresh pgr : currentList) {
                    if (generalId == pgr.getGeneralId()) {
                        contain = true;
                        break;
                    }
                }
                if (contain) {
                    continue;
                }
                theList.add(general);
            }
        }
        return theList;
    }
    
    @Transactional
    @Override
    public byte[] lockGeneral(final int playerId, final int generalId) {
        final PlayerGeneralRefresh pgr = this.playerGeneralRefreshDao.getPlayerGeneralRefresh(playerId, generalId);
        if (pgr != null && pgr.getBought() != 1) {
            pgr.setLocked(1);
            this.playerGeneralRefreshDao.lockGeneral(pgr.getVId());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] unlockGeneral(final int playerId, final int generalId) {
        final PlayerGeneralRefresh pgr = this.playerGeneralRefreshDao.getPlayerGeneralRefresh(playerId, generalId);
        if (pgr != null && pgr.getLocked() == 1) {
            this.playerGeneralRefreshDao.unlockGeneral(pgr.getVId(), new Date());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] recruitGeneral(final PlayerDto playerDto, final int generalId) {
        final int playerId = playerDto.playerId;
        final General general = (General)this.generalCache.get((Object)generalId);
        if (general == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_GENERAL);
        }
        final int generalType = general.getType();
        final int num = this.getNowGeneralNum(playerId, generalType);
        final Player player = this.playerDao.read(playerId);
        final int maxNum = this.getMaxGeneralNum(playerId, player.getPlayerLv(), generalType);
        if (num >= maxNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_NUM_TOP);
        }
        final PlayerGeneralRefresh pgr = this.playerGeneralRefreshDao.getPlayerGeneralRefresh(playerId, generalId);
        if (pgr == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_NOT_REFRESH);
        }
        if (pgr.getBought() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_HAS_RECRUIT);
        }
        if (general.getType() == 1) {
            final PlayerGeneralCivil pg = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pg != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_HAS_RECRUIT);
            }
        }
        else if (general.getType() == 2) {
            final PlayerGeneralMilitary pg2 = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pg2 != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GENERAL_HAS_RECRUIT);
            }
        }
        if (pgr.getIsGold() == 1) {
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)6);
            if (player.getConsumeLv() < ci.getLv()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            }
            if (!this.playerDao.consumeGold(player, pgr.getPrice(), ci.getName())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        else {
            final int copper = pgr.getPrice();
            if (!this.playerResourceDao.consumeCopper(playerId, copper, "\u5237\u65b0\u9152\u9986\u6263\u9664\u94f6\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
        }
        final List<PlayerGeneral> retiredGeneralList = this.playerGeneralDao.getGeneralList(player.getPlayerId());
        PlayerGeneral retiredGeneral = null;
        for (final PlayerGeneral tempGeneral : retiredGeneralList) {
            if (tempGeneral.getGeneralId().equals(general.getId())) {
                retiredGeneral = tempGeneral;
                break;
            }
        }
        final Date date = new Date();
        if (general.getType() == 1) {
            final PlayerGeneralCivil pgc = new PlayerGeneralCivil();
            pgc.setPlayerId(playerId);
            pgc.setGeneralId(generalId);
            if (retiredGeneral != null) {
                pgc.setIntel(retiredGeneral.getIntel());
                pgc.setPolitics(retiredGeneral.getPolitics());
                pgc.setLv(retiredGeneral.getLv());
                pgc.setExp(retiredGeneral.getExp());
                this.playerGeneralDao.deleteById(retiredGeneral.getVId());
            }
            else {
                pgc.setIntel(0);
                pgc.setPolitics(0);
                pgc.setLv(1);
                pgc.setExp(0L);
            }
            pgc.setOwner(0);
            pgc.setUpdateTime(date);
            this.playerGeneralCivilDao.create(pgc);
            TaskMessageHelper.sendOfficerTaskMessage(playerId, num + 1);
            TaskMessageHelper.sendRecuitGeneralMessage(playerId, 1, generalId);
        }
        else if (general.getType() == 2) {
            final PlayerGeneralMilitary pgm = new PlayerGeneralMilitary();
            pgm.setPlayerId(playerId);
            pgm.setGeneralId(generalId);
            if (retiredGeneral != null) {
                pgm.setLeader(retiredGeneral.getLeader());
                pgm.setStrength(retiredGeneral.getStrength());
                pgm.setForces(retiredGeneral.getForces());
                pgm.setLv(retiredGeneral.getLv());
                pgm.setExp(retiredGeneral.getExp());
                this.playerGeneralDao.deleteById(retiredGeneral.getVId());
            }
            else {
                pgm.setLeader(0);
                pgm.setStrength(0);
                pgm.setForces(0);
                pgm.setLv(1);
                pgm.setExp(0L);
            }
            pgm.setLocationId(WorldCityCommon.nationMainCityIdMap.get(player.getForceId()));
            pgm.setUpdateForcesTime(date);
            pgm.setState(1);
            pgm.setMorale(100);
            pgm.setAuto(1);
            pgm.setTacticEffect(0);
            pgm.setForceId(playerDto.forceId);
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                pgm.setJubenLoId(juBenDto.capital);
            }
            else {
                pgm.setJubenLoId(0);
            }
            this.playerGeneralMilitaryDao.create(pgm);
            TaskMessageHelper.sendGeneralTaskMessage(playerId, num + 1);
            TaskMessageHelper.sendRecuitGeneralMessage(playerId, 2, generalId);
        }
        this.playerGeneralRefreshDao.recruitGeneral(pgr.getVId());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("msg", (Object)MessageFormatter.format(LocalMessages.T_GENERAL_RECRUIT_SUCCESS, new Object[] { general.getName() }));
        doc.createElement("nowGeneralNum", this.getNowGeneralNum(player.getPlayerId(), general.getType()));
        doc.createElement("maxGeneralNum", this.getMaxGeneralNum(player.getPlayerId(), player.getPlayerLv(), general.getType()));
        if (general.getType() == 1) {
            doc.startArray("stratagem");
            for (final PlayerGeneralCivil pgc2 : this.playerGeneralCivilDao.getCivilList(playerId)) {
                doc.startObject();
                final General cilvil = (General)this.generalCache.get((Object)pgc2.getGeneralId());
                final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)cilvil.getStratagemId());
                if (stratagem != null) {
                    doc.createElement("stratagemName", stratagem.getName());
                    TrickFactory.getTrickInfo(doc, stratagem);
                    doc.createElement("stratagemId", stratagem.getId());
                    doc.createElement("stratagemIntro", stratagem.getIntro());
                }
                doc.createElement("pic", cilvil.getPic());
                doc.createElement("cilvilId", cilvil.getId());
                if (pgc2.getCd() != null) {
                    final long cd = pgc2.getCd().getTime() - new Date().getTime();
                    doc.createElement("cd", (cd > 0L) ? cd : 0L);
                }
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getNowGeneralNum(final int playerId, final int type) {
        int num = 0;
        if (type == 1) {
            num = this.playerGeneralCivilDao.getCivilNum(playerId);
        }
        else if (type == 2) {
            num = this.playerGeneralMilitaryDao.getMilitaryNum(playerId);
        }
        return num;
    }
    
    @Transactional
    @Override
    public byte[] cdRecover(final int playerId, final int type) {
        final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
        final Date nowDate = new Date();
        Date nextDate = new Date();
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)7);
        if (playerTavern != null) {
            if (type == 1) {
                nextDate = playerTavern.getNextCivilDate();
            }
            else if (type == 2) {
                nextDate = playerTavern.getNextMilitaryDate();
            }
        }
        final int gold = this.getCDRecoverCost(ci, nextDate, nowDate);
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Transactional
    @Override
    public byte[] cdRecoverConfirm(final int playerId, final int type) {
        final Player player = this.playerDao.read(playerId);
        final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
        final Date nowDate = new Date();
        Date nextDate = new Date();
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)7);
        if (playerTavern != null) {
            if (type == 1) {
                nextDate = playerTavern.getNextCivilDate();
            }
            else if (type == 2) {
                nextDate = playerTavern.getNextMilitaryDate();
            }
        }
        final int gold = this.getCDRecoverCost(ci, nextDate, nowDate);
        if (player.getConsumeLv() < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        if (!this.playerDao.consumeGold(player, gold, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        if (type == 1) {
            playerTavern.setNextCivilDate(nowDate);
        }
        else if (type == 2) {
            playerTavern.setNextMilitaryDate(nowDate);
        }
        this.playerTavernDao.updatePlayerTavern(playerTavern);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private int getCDRecoverCost(final Chargeitem ci, final Date endDate, final Date nowDate) {
        return (int)Math.ceil(CDUtil.getCD(endDate, nowDate) * 1.0 / (ci.getParam() * 60000.0) * ci.getCost());
    }
    
    @Transactional
    @Override
    public void addLockId(final int playerId, final int generalId) {
        final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
        if (StringUtils.isBlank(playerTavern.getLockGeneralId())) {
            final StringBuilder sb = new StringBuilder().append(generalId);
            sb.append(";");
            this.playerTavernDao.updateLockId(playerId, sb.toString());
        }
        else {
            final String nowIds = playerTavern.getLockGeneralId();
            final String[] s = nowIds.split(";");
            String[] array;
            for (int length = (array = s).length, i = 0; i < length; ++i) {
                final String id = array[i];
                if (Integer.valueOf(id) == generalId) {
                    return;
                }
            }
            final StringBuilder sb2 = new StringBuilder(nowIds);
            sb2.append(generalId);
            sb2.append(";");
            this.playerTavernDao.updateLockId(playerId, sb2.toString());
        }
    }
    
    private Map<Integer, List<General>> getDropGeneral(final PlayerTavern playerTavern, final int type) {
        final Map<Integer, List<General>> result = new HashMap<Integer, List<General>>();
        String dropGenerals = "";
        if (type == 1) {
            dropGenerals = playerTavern.getCivilInfo();
        }
        else if (type == 2) {
            dropGenerals = playerTavern.getMilitaryInfo();
        }
        if (!StringUtils.isEmpty(dropGenerals)) {
            final String[] ss = dropGenerals.split(",");
            String[] array;
            for (int length = (array = ss).length, i = 0; i < length; ++i) {
                final String s = array[i];
                final int generalId = Integer.valueOf(s);
                final General general = (General)this.generalCache.get((Object)generalId);
                List<General> list = result.get(general.getQuality());
                if (list == null) {
                    list = new ArrayList<General>();
                    result.put(general.getQuality(), list);
                }
                list.add(general);
            }
        }
        return result;
    }
    
    @Transactional
    @Override
    public boolean recruitGeneralDirect(final int playerId, final int generalId, final boolean direct) {
        final General general = (General)this.generalCache.get((Object)generalId);
        final int num = this.getNowGeneralNum(playerId, general.getType());
        final Player player = this.playerDao.read(playerId);
        final int maxNum = this.getMaxGeneralNum(playerId, player.getPlayerLv(), general.getType());
        if (!direct && num >= maxNum) {
            return false;
        }
        if (general.getType() == 2) {
            PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                pgm = new PlayerGeneralMilitary();
                pgm.setPlayerId(playerId);
                pgm.setGeneralId(generalId);
                final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
                pgm.setLeader(0);
                pgm.setStrength(0);
                pgm.setForces(this.battleDataCache.getMaxHp(playerId, generalId, troop, 1));
                pgm.setLv(1);
                pgm.setExp(0L);
                pgm.setLocationId(WorldCityCommon.nationMainCityIdMap.get(player.getForceId()));
                pgm.setUpdateForcesTime(new Date());
                pgm.setState(1);
                pgm.setMorale(100);
                pgm.setAuto(1);
                pgm.setTacticEffect(0);
                pgm.setForceId(player.getForceId());
                this.playerGeneralMilitaryDao.create(pgm);
                TaskMessageHelper.sendRecuitGeneralMessage(playerId, 2, generalId);
            }
        }
        else {
            PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null) {
                pgc = new PlayerGeneralCivil();
                pgc.setPlayerId(playerId);
                pgc.setGeneralId(generalId);
                pgc.setIntel(0);
                pgc.setPolitics(0);
                pgc.setLv(1);
                pgc.setExp(0L);
                pgc.setOwner(0);
                pgc.setUpdateTime(new Date());
                this.playerGeneralCivilDao.create(pgc);
                TaskMessageHelper.sendRecuitGeneralMessage(playerId, 1, generalId);
            }
        }
        return true;
    }
    
    @Override
    public byte[] getCanDropGeneral(final int type, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
        final JsonDocument doc = new JsonDocument();
        String dropGenerals = "";
        if (playerTavern != null) {
            if (type == 1) {
                dropGenerals = playerTavern.getCivilInfo();
            }
            else if (type == 2) {
                dropGenerals = playerTavern.getMilitaryInfo();
            }
        }
        final Set<Integer> dropGeneralSet = new HashSet<Integer>();
        if (!StringUtils.isEmpty(dropGenerals)) {
            final String[] ss = dropGenerals.split(",");
            String[] array;
            for (int length = (array = ss).length, i = 0; i < length; ++i) {
                final String s = array[i];
                dropGeneralSet.add(Integer.valueOf(s));
            }
        }
        final List<RecruitInfo> allNpcList = this.generalRecruitCache.getDropGeneralList(type);
        doc.startObject();
        doc.startArray("dropGenerals");
        final int nowPowerId = this.playerPowerDao.getNowPowerId(playerId);
        for (final RecruitInfo ri : allNpcList) {
            doc.startObject();
            doc.createElement("name", ri.getGeneralName());
            if (dropGeneralSet.contains(ri.getGeneralId())) {
                doc.createElement("quality", ri.getQuality());
            }
            else {
                doc.createElement("quality", 0);
                if (ri.getPowerId() == nowPowerId) {
                    doc.createElement("npc", ri.getPowerName());
                }
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public int getMaxGeneralNum(final int playerId, final int playerLv, final int type) {
        if (type == 1) {
            return this.generalPositionCache.getCivilCountByLv(playerLv) + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 32);
        }
        return this.generalPositionCache.getMilitaryCountByLv(playerLv) + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 27);
    }
}
