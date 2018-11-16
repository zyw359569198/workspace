package com.reign.gcld.store.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.event.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.util.*;

@Component("quenchingService")
public class QuenChingService implements IQuenchingService
{
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private EquipSkillLevelCache equipSkillLevelCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private CCache cCache;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private BattleDataCache battleDataCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private IPlayerQuenchingRelativeDao playerQuenchingRelativeDao;
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger timerLog;
    
    static {
        timerLog = new TimerLogger();
    }
    
    @Override
    public byte[] getQuenchingInfo(final PlayerDto playerDto, int storehouseId) {
        if (this.quenchingNotOpen(playerDto)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (storehouseId == 0) {
            final List<StoreHouse> equips = this.storeHouseDao.getByPlayerId(playerDto.playerId);
            final List<StoreHouse> newList = this.getSpecialStoreHouse(equips);
            Collections.sort(newList, new EquipComparator());
            if (newList != null && !newList.isEmpty()) {
                storehouseId = newList.get(0).getVId();
            }
        }
        final StoreHouse storeHouse = this.storeHouseDao.read(storehouseId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int goldValue = ((Chargeitem)this.chargeitemCache.get((Object)36)).getCost();
        final PlayerQuenchingRelative pqr = this.playerQuenchingRelativeDao.read(playerDto.playerId);
        int freeTimes = 0;
        if (pqr != null && pqr.getFreeQuenchingTimes() != null) {
            freeTimes = pqr.getFreeQuenchingTimes();
        }
        final Equip equip = (Equip)this.equipCache.get((Object)storeHouse.getItemId());
        if (equip != null) {
            doc.createElement("maxLv", equip.getSkillLvMax());
        }
        final Integer specialId = storeHouse.getSpecialSkillId();
        if (specialId != null && specialId != 0) {
            doc.createElement("canRestore", true);
        }
        final int tech = this.techEffectCache.getTechEffect(playerDto.playerId, 45);
        final List<PlayerTech> techs = this.playerTechDao.getAllTechByKey(playerDto.playerId, 45);
        boolean canInvest = false;
        if (techs != null && !techs.isEmpty()) {
            canInvest = (techs.get(0).getStatus() > 0);
        }
        final int freeNiubiTimes = pqr.getFreeNiubiQuenchingTimes();
        doc.createElement("avaliable", canInvest);
        doc.createElement("techIsOpen", tech > 0);
        doc.createElement("remind", (pqr == null) ? 0 : pqr.getRemind());
        doc.createElement("quenchingGold", goldValue);
        doc.createElement("freeTimes", freeTimes);
        doc.createElement("freeNiubiTimes", freeNiubiTimes);
        QuenchingJsonBuilder.getEquipInfo(storeHouse, doc, this.equipCache, this.equipSkillCache, this.generalCache, true, this.equipSkillEffectCache);
        if (EventUtil.isEventTime(14)) {
            final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, 14);
            doc.createElement("num", (pe == null) ? 0 : pe.getParam1());
            doc.createElement("alreadyNum", (pe == null) ? 0 : (pe.getParam4() / XiLianEvent.POINT * XiLianEvent.ZIZUN_XILIAN));
            doc.createElement("point", XiLianEvent.POINT);
            doc.createElement("currentNum", (pe == null) ? 0 : (pe.getParam4() % XiLianEvent.POINT));
            doc.createElement("leftNum", (pe == null) ? 0 : pe.getParam5());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private boolean quenchingNotOpen(final PlayerDto playerDto) {
        return playerDto.cs[51] != '1';
    }
    
    private List<StoreHouse> getSpecialStoreHouse(final List<StoreHouse> equips) {
        final List<StoreHouse> list = new ArrayList<StoreHouse>();
        if (equips != null && equips.size() != 0) {
            for (final StoreHouse sh : equips) {
                if (!StringUtils.isBlank(sh.getRefreshAttribute()) && sh.getType() == 1) {
                    list.add(sh);
                }
            }
        }
        return list;
    }
    
    @Transactional
    @Override
    public byte[] quenchingEquip(final PlayerDto playerDto, final int equipId, final int type) {
        int freeTimes = 0;
        final PlayerQuenchingRelative pqr = this.playerQuenchingRelativeDao.read(playerDto.playerId);
        if (pqr != null && pqr.getFreeQuenchingTimes() != null) {
            freeTimes = pqr.getFreeQuenchingTimes();
        }
        if (this.quenchingNotOpen(playerDto)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (type > 2 && type < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (type == 2 && freeTimes <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_FREE_TIMES);
        }
        final StoreHouse storeHouse = this.storeHouseDao.read(equipId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        final String skills = storeHouse.getRefreshAttribute();
        if (StringUtils.isBlank(skills)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
        }
        final Equip equip = (Equip)this.equipCache.get((Object)storeHouse.getItemId());
        if (equip == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
        }
        int goldValue = 0;
        boolean isFreeNiubi = false;
        final int freeNiubiTimes = pqr.getFreeNiubiQuenchingTimes();
        if (type == 1) {
            if (freeNiubiTimes > 0) {
                isFreeNiubi = true;
            }
            else {
                final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)36);
                goldValue = ci.getCost();
                if (!this.playerDao.consumeGold(this.playerDao.read(playerDto.playerId), ci)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                }
            }
        }
        final String[] skillssString = skills.split(";");
        SkillDto[] dtos = null;
        final int skillType = equip.getSkillType();
        StringBuffer sb = null;
        Integer quenchingTimes = null;
        int count = 1;
        final boolean isFullLvAndNumber = EquipCommon.isFullLvAndNumber(skills, equip, this.equipCache);
        if (type == 1) {
            quenchingTimes = storeHouse.getQuenchingTimes();
            if (isFullLvAndNumber) {
                count = 4;
            }
        }
        else if (type == 2) {
            quenchingTimes = storeHouse.getQuenchingTimesFree();
            if (isFullLvAndNumber) {
                count = 2;
            }
        }
        final int quenchingt = (quenchingTimes == null) ? 0 : quenchingTimes;
        final int skillMax = equip.getSkillLvMax();
        final boolean hasSpecialSkill = skillssString.length > equip.getSkillNum();
        boolean getAllStar = false;
        boolean hasSpecial = false;
        while (count > 0 && !getAllStar) {
            dtos = new SkillDto[skillssString.length];
            sb = new StringBuffer();
            for (int i = 0; i < skillssString.length; ++i) {
                if (!hasSpecialSkill || i != skillssString.length - 1) {
                    dtos[i] = new SkillDto(skillssString[i]);
                    final EquipSkillLv esl = (EquipSkillLv)this.equipSkillLevelCache.get((Object)dtos[i].getSkillLv());
                    if (esl != null) {
                        final int maxTimes = (type == 1) ? esl.getUpgradeMaxTimesGold() : esl.getUpgradeMaxTimesFree();
                        final int nowLv = dtos[i].getSkillLv();
                        if (esl.getUpgradeMaxTimesGold() != 0 && quenchingt >= maxTimes && nowLv < skillMax) {
                            dtos[i].setSkillLv((nowLv >= skillMax) ? skillMax : (nowLv + 1));
                        }
                        else {
                            final double ran = WebUtil.nextDouble();
                            double prob = 0.0;
                            if (type == 1) {
                                prob = esl.getUpgradeProbGold();
                            }
                            if (type == 2) {
                                prob = esl.getUpgradeProbFree();
                            }
                            if (ran < prob && nowLv < skillMax) {
                                dtos[i].setSkillLv((nowLv >= skillMax) ? skillMax : (nowLv + 1));
                            }
                            else {
                                final List<EquipSkill> es = this.equipSkillCache.getSkillByType(skillType);
                                final int ranInt = WebUtil.nextInt(es.size());
                                dtos[i].setSkillId(es.get(ranInt).getId());
                                dtos[i].setSkillLv(dtos[i].getSkillLv());
                            }
                        }
                        if (dtos[i].getSkillId() != 0) {
                            sb.append(dtos[i].toString()).append(";");
                        }
                    }
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            hasSpecial = (getAllStar = this.checkSpecialSkill(equip, dtos, sb, playerDto.playerId));
            --count;
        }
        List<Integer> addTimes = null;
        if (ActivityService.inQuenching) {
            addTimes = new ArrayList<Integer>();
        }
        if (hasSpecial) {
            if (addTimes != null) {
                addTimes.add(this.getAddNiubiFreeTypes(6));
            }
            this.storeHouseDao.updateRefreshAttriAndSpecial(equipId, sb.toString(), dtos[0].getSkillId());
        }
        else {
            this.storeHouseDao.updateRefreshAttribute(equipId, sb.toString());
        }
        if (type == 2) {
            --freeTimes;
            this.playerQuenchingRelativeDao.updateFreeQuenchingTimes(playerDto.playerId, freeTimes);
        }
        this.storeHouseDao.updateQuenchingTimes(equipId, quenchingt + 1, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (type == 1 && !isFreeNiubi) {
            doc.createElement("quenchingGold", goldValue);
        }
        doc.createElement("freeTimes", freeTimes);
        doc.createElement("equipId", storeHouse.getVId());
        final List<Integer> upTimes = new ArrayList<Integer>();
        final List<Integer> list = this.skillChange(skills, sb.toString(), doc, addTimes, upTimes);
        EquipCommon.getRefreshAttribute(sb.toString(), doc, this.equipSkillCache, list, this.equipSkillEffectCache, equip);
        EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, storeHouse.getSpecialSkillId(), sb.toString());
        this.appendNiubiQuenchinInfo(doc, addTimes);
        int getTotalFreeTimes = this.getTotalFreeTimes(addTimes);
        if (isFreeNiubi) {
            if (getTotalFreeTimes > 0) {
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freegoldquenching", getTotalFreeTimes, "+", "\u514d\u8d39\u81f3\u5c0a\u6d17\u70bc\u589e\u52a0\u6b21\u6570", player.getForceId(), player.getConsumeLv()));
            }
            --getTotalFreeTimes;
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freegoldquenching", 1.0, "-", "\u514d\u8d39\u81f3\u5c0a\u6d17\u70bc\u6d88\u8017\u6b21\u6570", player.getForceId(), player.getConsumeLv()));
        }
        else if (getTotalFreeTimes > 0) {
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freegoldquenching", getTotalFreeTimes, "+", "\u514d\u8d39\u81f3\u5c0a\u6d17\u70bc\u589e\u52a0\u6b21\u6570", player.getForceId(), player.getConsumeLv()));
        }
        final int resultNum = freeNiubiTimes + getTotalFreeTimes;
        doc.createElement("freeNiubiTimes", resultNum);
        if (storeHouse.getOwner() > 0) {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerDto.playerId, storeHouse.getOwner());
            if (pgm != null) {
                final int oldMaxHp = this.battleDataCache.getMaxHp(pgm);
                this.battleDataCache.removeEquipEffect(playerDto.playerId, pgm.getGeneralId());
                final int newMaxHp = this.battleDataCache.getMaxHp(pgm);
                if (newMaxHp < oldMaxHp) {
                    if (pgm.getForces() > newMaxHp) {
                        final int res = this.playerGeneralMilitaryDao.resetForces(playerDto.playerId, pgm.getGeneralId(), new Date(), newMaxHp);
                        if (res > 0) {
                            this.generalService.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm.getGeneralId());
                        }
                    }
                    else if (pgm.getForces() < newMaxHp) {
                        final int res = this.playerGeneralMilitaryDao.updateAutoRecruit(playerDto.playerId, pgm.getGeneralId());
                        if (res > 0) {
                            this.generalService.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm.getGeneralId());
                        }
                    }
                }
                else if (pgm.getForces() < newMaxHp) {
                    final int res = this.playerGeneralMilitaryDao.updateAutoRecruit(playerDto.playerId, pgm.getGeneralId());
                    if (res > 0) {
                        this.generalService.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm.getGeneralId());
                    }
                }
            }
        }
        if (resultNum != freeNiubiTimes && resultNum >= 0) {
            this.playerQuenchingRelativeDao.updateFreeNiubiTimes(playerDto.playerId, resultNum);
        }
        TaskMessageHelper.sendQuenchingTaskMessage(playerDto.playerId);
        if (!isFreeNiubi && EventUtil.isEventTime(14)) {
            final int xiLianTimes = 1;
            int point = upTimes.size();
            ++point;
            if (hasSpecial) {
                point += XiLianEvent.COMBO_POINT;
            }
            final int val = xiLianTimes * 100 + point;
            final PlayerEvent peBack = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, 14);
            EventUtil.handleOperation(playerDto.playerId, 14, val);
            final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, 14);
            doc.createElement("num", (pe == null) ? 0 : pe.getParam1());
            doc.createElement("alreadyNum", (pe == null) ? 0 : (pe.getParam4() / XiLianEvent.POINT * XiLianEvent.ZIZUN_XILIAN));
            doc.createElement("point", XiLianEvent.POINT);
            doc.createElement("currentNum", (pe == null) ? 0 : (pe.getParam4() % XiLianEvent.POINT));
            doc.createElement("leftNum", (pe == null) ? 0 : pe.getParam5());
            final int param2Back = (peBack == null) ? 0 : peBack.getParam2();
            final int param2 = (pe == null) ? 0 : pe.getParam2();
            if (param2 > param2Back) {
                int xiLianActivityExtEffect = 0;
                for (int j = 1; j <= IronRewardEvent.LENGTH; ++j) {
                    if (SlaveUtil.hasReward(param2, j) == 1 && SlaveUtil.hasReward(param2Back, j) == 0) {
                        xiLianActivityExtEffect = j;
                    }
                }
                doc.createElement("xiLianActivityExtEffect", xiLianActivityExtEffect);
            }
        }
        doc.endObject();
        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "wash");
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getTotalFreeTimes(final List<Integer> addTimes) {
        if (addTimes == null || addTimes.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (final Integer type : addTimes) {
            result += this.getNumByType(type);
        }
        return result;
    }
    
    private int getNumByType(final int type) {
        switch (type) {
            case 1: {
                return 1;
            }
            case 4: {
                return 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    private void appendNiubiQuenchinInfo(final JsonDocument doc, final List<Integer> addTimes) {
        if (addTimes == null || addTimes.isEmpty()) {
            return;
        }
        doc.startArray("infos");
        for (final Integer addType : addTimes) {
            doc.startObject();
            doc.createElement("type", addType);
            doc.endObject();
        }
        doc.endArray();
    }
    
    private boolean checkSpecialSkill(final Equip equip, final SkillDto[] dtos, final StringBuffer sb, final int playerId) {
        final int tech = this.techEffectCache.getTechEffect(playerId, 45);
        if (tech <= 0) {
            return false;
        }
        if (equip == null || dtos.length < equip.getSkillNum() || equip.getQuality() < 5 || !this.equipCache.getJinpinEquips().contains(equip.getId())) {
            return false;
        }
        for (int i = 0; i < dtos.length; ++i) {
            if (dtos[i] != null) {
                if (dtos[i].getSkillLv() != equip.getSkillLvMax()) {
                    return false;
                }
                if (i != 0 && dtos[i].getSkillId() != dtos[i - 1].getSkillId()) {
                    return false;
                }
            }
        }
        sb.append(";").append(dtos[0].toString());
        return true;
    }
    
    private List<Integer> skillChange(final String skills, final String string, final JsonDocument doc, final List<Integer> addTimes, final List<Integer> upTimes) {
        try {
            final List<Integer> list = new ArrayList<Integer>();
            final int length1 = skills.split(";").length;
            final int length2 = string.split(";").length;
            final int length3 = Math.min(length1, length2);
            final SkillDto[] dtos1 = new SkillDto[length1];
            final SkillDto[] dtos2 = new SkillDto[length2];
            final String[] s1 = skills.split(";");
            final String[] s2 = string.split(";");
            for (int i = 0; i < length3; ++i) {
                dtos1[i] = new SkillDto(s1[i]);
                dtos2[i] = new SkillDto(s2[i]);
                if (dtos1[i].getSkillId() == dtos2[i].getSkillId() && dtos1[i].getSkillLv() < dtos2[i].getSkillLv()) {
                    upTimes.add(0);
                    list.add(i);
                    if (i <= 2 && addTimes != null) {
                        final int times = this.getAddNiubiFreeTypes(dtos2[i].getSkillLv());
                        if (times > 0) {
                            addTimes.add(times);
                        }
                    }
                }
            }
            return list;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
            return null;
        }
    }
    
    private Integer getAddNiubiFreeTypes(final int skillLv) {
        if (skillLv == 5) {
            return 1;
        }
        if (skillLv > 5) {
            return 4;
        }
        return 0;
    }
    
    @Override
    public byte[] getEquips(final PlayerDto playerDto) {
        if (this.quenchingNotOpen(playerDto)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final List<StoreHouse> list = this.storeHouseDao.getByPlayerId(playerDto.playerId);
        final List<StoreHouse> newList = this.getSpecialStoreHouse(list);
        Collections.sort(newList, new EquipComparator());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        QuenchingJsonBuilder.getEquipInfo(newList, doc, this.equipCache, this.equipSkillCache, this.generalCache, this.equipSkillEffectCache);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void addFreeQuenchingTimes() {
        final long start = System.currentTimeMillis();
        final C c = (C)this.cCache.get((Object)"Equip.FreeSkillRefresh.MaxAcc");
        final int max = (c == null) ? 12 : ((int)(Object)c.getValue());
        this.playerQuenchingRelativeDao.updateAllFreeQuenchingTimes(max);
        final List<Integer> allPlayers = Players.getAllPlayerIds();
        if (allPlayers != null && allPlayers.size() > 0) {
            final List<PlayerQuenchingRelative> list = this.playerQuenchingRelativeDao.getListByIds(allPlayers);
            if (list != null) {
                for (final PlayerQuenchingRelative pqr : list) {
                    Players.push(pqr.getPlayerId(), PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("freeQuechingTimes", pqr.getFreeQuenchingTimes()));
                }
            }
        }
        QuenChingService.timerLog.info(LogUtil.formatThreadLog("QuenChingService", "addFreeQuenchingTimes", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public byte[] remindSet(final PlayerDto playerDto, final int remind) {
        if (remind < 0 || remind > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.playerQuenchingRelativeDao.updateRemindQuenching(playerDto.playerId, remind);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("remind", remind);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void checkSpecialSkill(final int playerId) {
        final List<StoreHouse> storeHouses = this.storeHouseDao.getAllEquip(playerId);
        Equip equip = null;
        String[] skillssString = null;
        SkillDto[] dtos = null;
        StringBuffer sb = null;
        for (final StoreHouse sHouse : storeHouses) {
            equip = (Equip)this.equipCache.get((Object)sHouse.getItemId());
            if (equip == null) {
                continue;
            }
            if (equip.getQuality() < 5) {
                continue;
            }
            final String refreshAttribute = sHouse.getRefreshAttribute();
            if (StringUtils.isBlank(refreshAttribute)) {
                continue;
            }
            sb = new StringBuffer();
            skillssString = refreshAttribute.split(";");
            dtos = this.getSkillDtos(skillssString, sb);
            final boolean has = this.checkSpecialSkill(equip, dtos, sb, playerId);
            if (has) {
                this.storeHouseDao.updateRefreshAttriAndSpecial(sHouse.getVId(), sb.toString(), dtos[0].getSkillId());
            }
            this.refreshSkillEffect(sHouse, playerId);
        }
    }
    
    private SkillDto[] getSkillDtos(final String[] skillssString, final StringBuffer sb) {
        final SkillDto[] dto = new SkillDto[skillssString.length];
        for (int i = 0; i < skillssString.length; ++i) {
            dto[i] = new SkillDto(skillssString[i]);
            if (dto[i].getSkillId() != 0) {
                sb.append(dto[i].toString()).append(";");
            }
        }
        SymbolUtil.removeTheLast(sb);
        return dto;
    }
    
    @Override
    public byte[] getRestoreInfo(final PlayerDto playerDto, final int storeHouseId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(storeHouseId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int tech = this.techEffectCache.getTechEffect(playerDto.playerId, 45);
        if (tech <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SPECIAL_SKILL_TECH_NOT_OPEN);
        }
        final Equip equip = (Equip)this.equipCache.get((Object)storeHouse.getItemId());
        if (equip == null || equip.getQuality() < 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Integer specialId = storeHouse.getSpecialSkillId();
        if (specialId == null || specialId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SPECIAL_SKILL);
        }
        final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)specialId);
        if (equipSkill == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SPECIAL_SKILL);
        }
        final int num = equip.getSkillNum() + 1;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("skillId", equipSkill.getId());
        doc.createElement("intro", equipSkill.getIntro());
        doc.createElement("skillName", equipSkill.getName());
        doc.createElement("skillPic", equipSkill.getPic());
        doc.createElement("lv", equip.getSkillLvMax());
        doc.createElement("num", num);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] restoreSpecial(final PlayerDto playerDto, final int storeHouseId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(storeHouseId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int tech = this.techEffectCache.getTechEffect(playerDto.playerId, 45);
        if (tech <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SPECIAL_SKILL_TECH_NOT_OPEN);
        }
        final Equip equip = (Equip)this.equipCache.get((Object)storeHouse.getItemId());
        if (equip == null || equip.getQuality() < 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Integer specialId = storeHouse.getSpecialSkillId();
        if (specialId == null || specialId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SPECIAL_SKILL);
        }
        final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)specialId);
        if (equipSkill == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SPECIAL_SKILL);
        }
        final int num = equip.getSkillNum() + 1;
        final int lv = equip.getSkillLvMax();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; ++i) {
            sb.append(specialId).append(":").append(lv).append(";");
        }
        SymbolUtil.removeTheLast(sb);
        this.storeHouseDao.updateRefreshAttribute(storeHouseId, sb.toString());
        this.refreshSkillEffect(storeHouse, playerDto.playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private void refreshSkillEffect(final StoreHouse storeHouse, final int playerId) {
        if (storeHouse.getOwner() > 0) {
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, storeHouse.getOwner());
            if (pgm != null) {
                final int oldMaxHp = this.battleDataCache.getMaxHp(pgm);
                this.battleDataCache.removeEquipEffect(playerId, pgm.getGeneralId());
                final int newMaxHp = this.battleDataCache.getMaxHp(pgm);
                if (newMaxHp < oldMaxHp) {
                    if (pgm.getForces() > newMaxHp) {
                        this.playerGeneralMilitaryDao.resetForces(playerId, pgm.getGeneralId(), new Date(), newMaxHp);
                        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                    }
                }
                else if (pgm.getForces() < newMaxHp) {
                    this.playerGeneralMilitaryDao.updateAutoRecruit(playerId, pgm.getGeneralId());
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                }
            }
        }
    }
}
