package com.reign.gcld.event.service;

import org.springframework.stereotype.*;
import com.reign.gcld.event.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.treasure.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.event.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.slave.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.reward.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.*;

@Component("eventService")
public class EventService implements IEventService, Handler
{
    private static final Logger timerLog;
    private static final Logger errorLog;
    @Autowired
    private IPlayerEventDao playerEventDao;
    @Autowired
    private IActivityDao activityDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private ITreasureService treasureService;
    
    static {
        timerLog = new TimerLogger();
        errorLog = CommonLog.getLog(EventService.class);
    }
    
    @Override
    public void init() {
        EventFactory.getInstance().init(this.dataGetter);
        final Date now = new Date();
        final List<Activity> list = this.activityDao.getModels();
        for (final Activity activity : list) {
            if (activity.getVId() >= 9 && activity.getEndTime().after(now)) {
                EventUtil.addEvent(activity.getVId(), activity.getStartTime(), activity.getEndTime(), activity.getParamsInfo());
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] getEventInfo(final PlayerDto playerDto, final int eventId) {
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10045);
        }
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerDto.playerId, eventId);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(playerDto, eventId, pe));
    }
    
    @Transactional
    @Override
    public byte[] getReward(final PlayerDto playerDto, final int eventId, final int step, final int param1, final int param2) {
        switch (eventId) {
            case 9: {
                return this.getSlaveEventReward(playerDto.playerId, eventId, step);
            }
            case 10: {
                return this.getMidAutumnEventReward(playerDto.playerId, eventId, step);
            }
            case 11: {
                return this.getNationalDayEventReward(playerDto.playerId, eventId, step);
            }
            case 13: {
                return this.getIronRewardEventReward(playerDto.playerId, eventId, step);
            }
            case 14: {
                return this.getXiLianEventReward(playerDto.playerId, eventId, step, param1, param2);
            }
            case 15: {
                return this.getIronGiveEventReward(playerDto, eventId, step);
            }
            case 16: {
                return this.getChristmasDayEventReward(playerDto, eventId, step);
            }
            case 17: {
                return this.getWishEventReward(playerDto, eventId, step, param1);
            }
            case 18: {
                return this.getBeastEventReward(playerDto, eventId, step);
            }
            case 19: {
                return this.getBaiNianEventReward(playerDto, eventId, step);
            }
            case 20: {
                return this.getRedPaperEventReward(playerDto, eventId);
            }
            case 21: {
                return this.getLanternEventReward(playerDto, eventId);
            }
            default: {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10003);
            }
        }
    }
    
    @Override
    public void handler(final Message message) {
        if (message instanceof EventMessage) {
            final EventMessage em = (EventMessage)message;
            if (em.getEventStage() == EventStage.START) {
                em.getEvent().startEvent();
            }
            else if (em.getEventStage() == EventStage.OVER) {
                em.getEvent().overEvent();
            }
        }
    }
    
    private byte[] getSlaveEventReward(final int playerId, final int eventId, final int pos) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pos < 1 || pos > 4 || 1 != SlaveUtil.hasReward(pe.getParam1(), pos) || 1 == SlaveUtil.hasReward(pe.getParam2(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SLAVE_ACTIVITY_CAN_NOT_CAPUTURE);
        }
        this.playerEventDao.setParam2(playerId, eventId, pos - 1);
        final int type = SlaveEvent.rewardTypeMap.get(pos);
        final int value = SlaveEvent.rewardValueMap.get(pos);
        if (5 == type) {
            this.playerService.updateExpAndPlayerLevel(playerId, value, "\u7262\u623f\u6d3b\u52a8\u589e\u52a0\u7ecf\u9a8c");
        }
        else if (4 == type) {
            this.playerResourceDao.addIronIgnoreMax(playerId, value, "\u4f1a\u6218\u96c6\u7ed3\u4efb\u52a1\u83b7\u5f97\u5175\u529b\u6392\u540d\u5956\u52b1\u9554\u94c1", true);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] getMidAutumnEventReward(final int playerId, final int eventId, final int pos) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pos < 1 || pos > 5 || 1 != SlaveUtil.hasReward(pe.getParam3(), pos) || 1 == SlaveUtil.hasReward(pe.getParam4(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MID_AUTUMU_ACTIVITY_CAN_NOT_GET);
        }
        this.playerEventDao.setParam4(playerId, eventId, pos - 1);
        final int value = MidAutumnEvent.rewardMap.get(pos);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (1 == pos || 2 == pos) {
            this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, value, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u7cae\u98df");
            doc.createElement("food", value);
        }
        else if (3 == pos) {
            this.dataGetter.getStoreHouseService().gainSearchItems(106, value, PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId)), "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
            doc.createElement("dstq", value);
        }
        else if (4 == pos) {
            this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerId, value, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
            doc.createElement("phantom", value);
        }
        else if (5 == pos) {
            this.dataGetter.getStoreHouseService().gainItems(playerId, value, MidAutumnEvent.IRON_TOKEN_INDEX, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u9554\u94c1\u4ee4");
            doc.createElement("token", value);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getNationalDayEventReward(final int playerId, final int eventId, final int pos) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pos < 1 || pos > 12 || 1 != SlaveUtil.hasReward(pe.getParam2(), pos) || 1 == SlaveUtil.hasReward(pe.getParam3(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MID_AUTUMU_ACTIVITY_CAN_NOT_GET);
        }
        this.playerEventDao.setParam3(playerId, eventId, pos - 1);
        final int value = NationalDayEvent.rewardMap.get(pos);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerId, value, "\u56fd\u5e86\u793c\u5305\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
        doc.createElement("phantom", value);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getIronRewardEventReward(final int playerId, final int eventId, final int pos) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pos < 1 || pos > IronRewardEvent.LENGTH || 1 != SlaveUtil.hasReward(pe.getParam2(), pos) || 1 == SlaveUtil.hasReward(pe.getParam3(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IRON_REWARD_CAN_NOT_GET);
        }
        PlayerIncenseWeaponEffect piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
        if (piwe == null) {
            this.initPlayerIncenseWeaponEffect(playerId);
            piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
        }
        final int itemId = IronRewardEvent.itemIdMap.get(pos);
        final Items item = (Items)this.dataGetter.getItemsCache().get((Object)itemId);
        long cd = 0L;
        if (1 == pos || 3 == pos) {
            final int incenseId = piwe.getIncenseId();
            final int incenseLimit = piwe.getIncenseLimit();
            final Date incenseEndTime = piwe.getIncenseEndTime();
            if (1 == incenseId) {
                if (incenseLimit > 0) {
                    return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.IRON_REWARD_IN_BUFFER, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(incenseId))).getName() }));
                }
            }
            else if (3 == incenseId && incenseEndTime.getTime() > System.currentTimeMillis()) {
                return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.IRON_REWARD_IN_BUFFER, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(incenseId))).getName() }));
            }
            final ThreeTuple<Integer, Integer, Long> threeTuple = this.getEffect(item.getEffect());
            cd = threeTuple.right + IronRewardEvent.REAL_MORE_CD;
            this.dataGetter.getPlayerIncenseWeaponEffectDao().updateIncenseEffect(playerId, pos, threeTuple.left, threeTuple.middle, TimeUtil.nowAddMs(cd));
            EventService.timerLog.error("class:EventService#method:getIronRewardEventReward#playerId:" + playerId + "#pos:" + pos);
            if (1 == pos) {
                Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("ironIncenseEffect", 1));
            }
        }
        else {
            final int weaponId = piwe.getWeaponId();
            final Date weaponEndTime = piwe.getWeaponEndTime();
            if (weaponId > 0 && weaponEndTime.getTime() > System.currentTimeMillis()) {
                return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.IRON_REWARD_IN_BUFFER, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(weaponId))).getName() }));
            }
            final ThreeTuple<Integer, Integer, Long> threeTuple2 = this.getEffect(item.getEffect());
            cd = threeTuple2.right + IronRewardEvent.REAL_MORE_CD;
            this.dataGetter.getPlayerIncenseWeaponEffectDao().updateWeaponEffect(playerId, pos, threeTuple2.left, threeTuple2.middle, TimeUtil.nowAddMs(cd));
            EventService.timerLog.error("class:EventService#method:getIronRewardEventReward#playerId:" + playerId + "#pos:" + pos);
        }
        this.playerEventDao.setParam3(playerId, eventId, pos - 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cd", cd + IronRewardEvent.MORE_CD);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getXiLianEventReward(final int playerId, final int eventId, final int pos, final int vId, final int equipSkillId) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (vId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (pos < 1 || pos > XiLianEvent.LENGTH || 1 != SlaveUtil.hasReward(pe.getParam2(), pos) || 1 == SlaveUtil.hasReward(pe.getParam3(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_CAN_NOT_GET);
        }
        final StoreHouse sh = this.dataGetter.getStoreHouseDao().read(vId);
        if (sh == null || sh.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        final String skills = sh.getRefreshAttribute();
        if (StringUtils.isBlank(skills)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        int result = 0;
        if (1 == pos) {
            if (!this.dataGetter.getEquipCache().getJinpinEquips().contains(sh.getItemId()) || !this.dataGetter.getBlacksmithService().is_3_Length(sh.getRefreshAttribute()) || this.dataGetter.getBlacksmithService().is_5(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NOT_SPECIAL_EQUIP);
            }
            final int isSameSpecialSkillLv4 = this.dataGetter.getBlacksmithService().isSameSpecialSkillLv4(skills);
            if (isSameSpecialSkillLv4 == 0) {
                this.dataGetter.getStoreHouseDao().updateRefreshAttributeAndMarkId(vId, this.getUpdateRefreshAttribute(skills), XiLianEvent.MARK_ID_1);
            }
            else {
                result = isSameSpecialSkillLv4;
                this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecialAndMarkId(vId, this.getAllRefreshAttribute(result), result, XiLianEvent.MARK_ID_1);
            }
        }
        else if (2 == pos) {
            if (!this.dataGetter.getEquipCache().getJinpinEquips().contains(sh.getItemId()) || !this.dataGetter.getBlacksmithService().is_5(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NOT_FULL_SPECIAL_EQUIP);
            }
            final int size = this.dataGetter.getEquipSkillCache().SIZE;
            final int specialSkillId = WebUtil.nextInt(size) + 1;
            this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecialAndMarkId(vId, this.getAllRefreshAttribute(specialSkillId), specialSkillId, XiLianEvent.MARK_ID_2);
            result = specialSkillId;
        }
        else if (3 == pos) {
            final int size = this.dataGetter.getEquipSkillCache().SIZE;
            if (equipSkillId < 1 || equipSkillId > size) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final Integer oldSpecialSkillId = sh.getSpecialSkillId();
            if (oldSpecialSkillId != null && oldSpecialSkillId == equipSkillId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_SPECIAL_SKILL_CAN_NOT_SAME);
            }
            this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecialAndMarkId(vId, this.getAllRefreshAttribute(equipSkillId), equipSkillId, XiLianEvent.MARK_ID_3);
            result = equipSkillId;
        }
        if (sh.getOwner() > 0) {
            final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, sh.getOwner());
            if (pgm != null) {
                final int oldMaxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                this.dataGetter.getBattleDataCache().removeEquipEffect(playerId, pgm.getGeneralId());
                final int newMaxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                if (newMaxHp < oldMaxHp) {
                    if (pgm.getForces() > newMaxHp) {
                        final int res = this.dataGetter.getPlayerGeneralMilitaryDao().resetForces(playerId, pgm.getGeneralId(), new Date(), newMaxHp);
                        if (res > 0) {
                            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                        }
                    }
                    else if (pgm.getForces() < newMaxHp) {
                        final int res = this.dataGetter.getPlayerGeneralMilitaryDao().updateAutoRecruit(playerId, pgm.getGeneralId());
                        if (res > 0) {
                            this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                        }
                    }
                }
                else if (pgm.getForces() < newMaxHp) {
                    final int res = this.dataGetter.getPlayerGeneralMilitaryDao().updateAutoRecruit(playerId, pgm.getGeneralId());
                    if (res > 0) {
                        this.dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(playerId, pgm.getGeneralId());
                    }
                }
            }
        }
        EventService.timerLog.error("class:EventService#method:getIronRewardEventReward#playerId:" + playerId + "#pos:" + pos);
        this.playerEventDao.setParam3(playerId, eventId, pos - 1);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("result", result));
    }
    
    private byte[] getIronGiveEventReward(final PlayerDto playerDto, final int eventId, final int index) {
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (index < 1 || index > IronGiveEvent.LENGTH || IronGiveEvent.getNum(index, pe) <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IRON_GIVE_NO_TIMES);
        }
        if (4 == index && pe.getParam5() < IronGiveEvent.IRON_TICKET) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IRON_GIVE_NO_TICKET);
        }
        final int gold = this.getGold(index);
        if (gold > 0) {
            Chargeitem ci = null;
            if (1 == index) {
                ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)79);
            }
            else if (2 == index) {
                ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)80);
            }
            else if (3 == index) {
                ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)81);
            }
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), ci)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        if (1 == index) {
            this.playerEventDao.addParam1(playerId, eventId, 1);
        }
        else if (2 == index) {
            this.playerEventDao.addParam2(playerId, eventId, 1);
        }
        else if (3 == index) {
            this.playerEventDao.addParam3(playerId, eventId, 1);
        }
        else {
            this.playerEventDao.addParam4Reduce5(playerId, eventId, 1, IronGiveEvent.IRON_TICKET);
        }
        final int iron = IronGiveEvent.getIron(playerDto.playerLv, index);
        this.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, iron, "\u9554\u94c1\u653e\u9001\u6d3b\u52a8\u83b7\u53d6\u9554\u94c1", true);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("iron", iron);
        doc.endObject();
        final int times = IronGiveEvent.getNum(1, pe) + IronGiveEvent.getNum(2, pe) + IronGiveEvent.getNum(3, pe) + IronGiveEvent.getNum(4, pe);
        if (times <= 1) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveIronGiveActivity", 0));
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getChristmasDayEventReward(final PlayerDto playerDto, final int eventId, final int index) {
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (index < 1 || index > ChristmasDayEvent.LENGTH || SlaveUtil.hasReward(pe.getParam4(), index) != 1 || SlaveUtil.hasReward(pe.getParam5(), index) == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_GIFT_RECEIVED);
        }
        this.dataGetter.getPlayerEventDao().setParam5(playerId, eventId, index - 1);
        final String rewardStr = ChristmasDayEvent.getBaseRewardNumString(playerDto.playerLv, index);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5c0f\u793c\u5305", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getWishEventReward(final PlayerDto playerDto, final int eventId, final int index, final int id) {
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_DAY_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (index < 1 || index > 2 || (1 == index && (id < 1 || id > WishEvent.LENGTH))) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (pe.getParam4() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_RECEIVED);
        }
        final int select = pe.getParam2();
        if (select == 0) {
            if (2 == index) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_COMMON_WISH_FIRST);
            }
            this.dataGetter.getPlayerEventDao().updateParam1updateParam2(playerId, eventId, id, 1);
            this.dataGetter.getPlayerEventDao().updateParam3(playerId, eventId, WishEvent.BITS);
            final String message = MessageFormatter.format(WishEvent.templeMap.get(id), new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName) });
            WishEvent.messageList[WishEvent.msgIndex] = message;
            WishEvent.msgIndex = (WishEvent.msgIndex + 1) % 10;
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        else {
            if (1 != select) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_AREADY_GOLD_WISH);
            }
            if (1 == index) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_COMMON_WISH_ALREADY);
            }
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), 88, "\u65b0\u5e74\u613f\u671b\u6d3b\u52a8\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            this.dataGetter.getPlayerEventDao().updateParam2(playerId, eventId, 2);
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
    }
    
    private byte[] getBeastEventReward(final PlayerDto playerDto, final int eventId, final int select) {
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (select < 1 || select > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date cd = pe.getCd1();
        if (cd != null && cd.after(new Date())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_IN_CD);
        }
        final int id = pe.getParam1();
        if (id == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_PLEASE_GET_BEAST);
        }
        int blood = 0;
        double rate = 0.0;
        if (1 == select) {
            if (pe.getParam4() < BeastEvent.baoZhuTuple.left) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_CRACKER);
            }
            this.dataGetter.getPlayerEventDao().reduceParam4(playerId, eventId, BeastEvent.baoZhuTuple.left);
            blood = BeastEvent.baoZhuTuple.right;
            rate = BeastEvent.rateTuple.left;
        }
        else {
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), ((Chargeitem)this.dataGetter.getChargeitemCache().get((Object)85)).getCost(), "\u6253\u5e74\u517d\u6d3b\u52a8\u4f7f\u7528\u8f70\u5929\u96f7\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            blood = BeastEvent.thunderBlood;
            rate = BeastEvent.rateTuple.right;
        }
        final int totalBlood = pe.getParam2() + blood;
        int lastStraw = 0;
        String rewardStr = "";
        if (totalBlood >= BeastEvent.beastBloodExpMap.get(id).left) {
            if (id < BeastEvent.LENGTH) {
                this.dataGetter.getPlayerEventDao().updateParam1updateParam2updateParam5updateCd1(playerId, eventId, id + 1, 0, id, TimeUtil.nowAddHours((int)BeastEvent.cdGoldTuple.left));
            }
            else {
                this.dataGetter.getPlayerEventDao().updateParam1updateParam2(playerId, eventId, 0, 0);
                if (pe.getParam5() < BeastEvent.LENGTH) {
                    this.dataGetter.getPlayerEventDao().updateParam5(playerId, eventId, BeastEvent.LENGTH);
                }
            }
            rewardStr = BeastEvent.getBaseRewardNumString(playerDto.playerLv, id);
            lastStraw = 1;
        }
        else {
            this.dataGetter.getPlayerEventDao().updateParam2(playerId, eventId, totalBlood);
        }
        final double nextDouble = WebUtil.nextDouble();
        if (nextDouble < rate) {
            if (!StringUtils.isBlank(rewardStr)) {
                rewardStr = String.valueOf(rewardStr) + ";";
            }
            final int index = WebUtil.nextInt(BeastEvent.SIZE) + 1;
            rewardStr = String.valueOf(rewardStr) + BeastEvent.dropMap.get(index);
        }
        if (StringUtils.isNotBlank(rewardStr)) {
            final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
            final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u6253\u5e74\u517d\u6d3b\u52a8\u5956\u52b1", null);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("lastStraw", lastStraw);
            doc.startArray("rewards");
            for (final Reward temp : map.values()) {
                doc.startObject();
                doc.createElement("type", temp.getType());
                doc.createElement("value", temp.getNum());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] getBaiNianEventReward(final PlayerDto playerDto, final int eventId, final int id) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (id < 1 || id > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (pe.getParam1() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_ALREADY_CHOICE);
        }
        this.dataGetter.getPlayerEventDao().updateParam1(playerId, 19, id);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private byte[] getRedPaperEventReward(final PlayerDto playerDto, final int eventId) {
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.RED_PAPER_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pe.getParam3() >= pe.getParam2()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.RED_PAPER_NO_RED_PAPER);
        }
        this.dataGetter.getPlayerEventDao().addParam3(playerId, 20, 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final String rewardStr = RedPaperEvent.getBaseRewardNumString(playerDto.playerLv);
        final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> rewardMap = tr.rewardPlayer(playerDto, this.dataGetter, "\u5145\u503c\u9001\u7ea2\u5305\u6d3b\u52a8\u5956\u52b1", null);
        doc.startArray("rewards");
        for (final Reward reward : rewardMap.values()) {
            doc.startObject();
            doc.createElement("type", reward.getType());
            doc.createElement("value", reward.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getLanternEventReward(final PlayerDto playerDto, final int eventId) {
        if (!EventUtil.isEventTime(eventId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, eventId);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (LanternTask.state != LanternTask.State.END) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_ING);
        }
        final int rank = pe.getParam4();
        if (rank <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_RANK);
        }
        if (1 == pe.getParam5()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_RECEIVED);
        }
        this.playerEventDao.updateParam5(playerId, eventId, 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final String rewardStr = LanternEvent.getBaseRewardNumString(playerDto.playerLv, playerDto.forceId, rank, this.dataGetter);
        final ITaskReward tr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> rewardMap = tr.rewardPlayer(playerDto, this.dataGetter, "\u5143\u5bb5\u6d3b\u52a8\u6392\u540d\u5956\u52b1", null);
        doc.startArray("rewards");
        for (final Reward reward : rewardMap.values()) {
            doc.startObject();
            doc.createElement("type", reward.getType());
            doc.createElement("value", reward.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public int getGold(final int index) {
        if (1 == index) {
            final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)79);
            return ci.getCost();
        }
        if (2 == index) {
            final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)80);
            return ci.getCost();
        }
        if (3 == index) {
            final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)81);
            return ci.getCost();
        }
        return 0;
    }
    
    @Override
    public String getUpdateRefreshAttribute(final String refreshAttribute) {
        final String[] skillArr = refreshAttribute.split(";");
        final StringBuffer sb = new StringBuffer();
        String[] array;
        for (int length = (array = skillArr).length, i = 0; i < length; ++i) {
            final String skill = array[i];
            final SkillDto sd = new SkillDto(skill);
            sb.append(sd.upLv());
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    @Override
    public String getAllRefreshAttribute(final int specialSkillId) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; ++i) {
            sb.append(specialSkillId);
            sb.append(":");
            sb.append(5);
            sb.append(";");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    @Override
    public ThreeTuple<Integer, Integer, Long> getEffect(final String effectStr) {
        final ThreeTuple<Integer, Integer, Long> threeTuple = new ThreeTuple<Integer, Integer, Long>();
        int limit = 0;
        int multi = 0;
        long cd = 0L;
        if (StringUtils.isNotBlank(effectStr)) {
            final String[] effectArr = effectStr.split(";");
            if (effectArr != null) {
                String[] array;
                for (int length = (array = effectArr).length, i = 0; i < length; ++i) {
                    final String temp = array[i];
                    if (StringUtils.isNotBlank(temp)) {
                        final String[] tempArr = temp.split(",");
                        if (tempArr != null && tempArr.length >= 2) {
                            if (IronRewardEvent.LIMIT.equals(tempArr[0])) {
                                limit = Integer.parseInt(tempArr[1]);
                            }
                            else if (IronRewardEvent.MULTI.equals(tempArr[0])) {
                                multi = Integer.parseInt(tempArr[1]);
                            }
                            else if (IronRewardEvent.TIME.equals(tempArr[0])) {
                                cd = Integer.parseInt(tempArr[1]) * 1000;
                            }
                        }
                    }
                }
            }
        }
        threeTuple.left = limit;
        threeTuple.middle = multi;
        threeTuple.right = cd;
        return threeTuple;
    }
    
    @Override
    public void initPlayerIncenseWeaponEffect(final int playerId) {
        final PlayerIncenseWeaponEffect piwe = new PlayerIncenseWeaponEffect();
        final Date now = new Date();
        piwe.setPlayerId(playerId);
        piwe.setIncenseId(0);
        piwe.setIncenseLimit(0);
        piwe.setIncenseMulti(0);
        piwe.setIncenseEndTime(now);
        piwe.setWeaponId(0);
        piwe.setWeaponLimit(0);
        piwe.setWeaponMulti(0);
        piwe.setWeaponEndTime(now);
        this.dataGetter.getPlayerIncenseWeaponEffectDao().create(piwe);
    }
    
    private byte[] getResult(final PlayerDto playerDto, final int eventId, final PlayerEvent pe) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        switch (eventId) {
            case 9: {
                final SlaveEvent event = (SlaveEvent)EventUtil.getEvent(eventId);
                event.buildJson(doc, playerDto, pe);
                break;
            }
            case 10: {
                final MidAutumnEvent maEvent = (MidAutumnEvent)EventUtil.getEvent(eventId);
                maEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 11: {
                final NationalDayEvent ndEvent = (NationalDayEvent)EventUtil.getEvent(eventId);
                ndEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 12: {
                final ResourceAdditionEvent raEvent = (ResourceAdditionEvent)EventUtil.getEvent(eventId);
                raEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 13: {
                final IronRewardEvent irEvent = (IronRewardEvent)EventUtil.getEvent(eventId);
                irEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 14: {
                final XiLianEvent xlEvent = (XiLianEvent)EventUtil.getEvent(eventId);
                xlEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 15: {
                final IronGiveEvent igEvent = (IronGiveEvent)EventUtil.getEvent(eventId);
                igEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 16: {
                final ChristmasDayEvent cdEvent = (ChristmasDayEvent)EventUtil.getEvent(eventId);
                cdEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 17: {
                final WishEvent wEvent = (WishEvent)EventUtil.getEvent(eventId);
                wEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 18: {
                final BeastEvent bEvent = (BeastEvent)EventUtil.getEvent(eventId);
                bEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 19: {
                final BaiNianEvent bnEvent = (BaiNianEvent)EventUtil.getEvent(eventId);
                bnEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 20: {
                final RedPaperEvent rpEvent = (RedPaperEvent)EventUtil.getEvent(eventId);
                rpEvent.buildJson(doc, playerDto, pe);
                break;
            }
            case 21: {
                final LanternEvent lEvent = (LanternEvent)EventUtil.getEvent(eventId);
                lEvent.buildJson(doc, playerDto, pe);
                break;
            }
        }
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public void saveEventSetting(final int eventId, final Date startTime, final Date endTime, final String paramInfo) {
        Activity activity = this.activityDao.read(eventId);
        if (activity == null) {
            activity = new Activity();
            activity.setVId(eventId);
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            activity.setName("");
            activity.setParamsInfo(paramInfo);
            this.activityDao.create(activity);
        }
        else {
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            this.activityDao.updateInfo(eventId, startTime, endTime, paramInfo);
        }
    }
    
    @Override
    public void removeEventSetting(final int eventId) {
        this.activityDao.deleteById(eventId);
    }
    
    @Override
    public void removeEvent(final int eventId) {
        this.dataGetter.getPlayerEventDao().clearEvent(eventId);
    }
    
    @Override
    public byte[] lashSlave(final int playerId, final int pos) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, 9);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pos < 1 || pos > 4 || 1 != SlaveUtil.hasReward(pe.getParam2(), pos) || 1 == SlaveUtil.hasReward(pe.getParam3(), pos)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SLAVE_ACTIVITY_CAN_NOT_LASH);
        }
        if (3 == SlaveUtil.get1Num(pe.getParam3())) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveSlaveActivity", 0));
        }
        this.playerEventDao.setParam3(playerId, 9, pos - 1);
        final Integer before = SlaveEvent.rateMap.get(playerId);
        final int beforeNum = (before == null) ? 0 : before;
        SlaveEvent.rateMap.put(playerId, beforeNum + 1);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("slaveActivityBuff", SlaveEvent.getAdditionExp(playerId)));
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] getBigGift(final int playerId) {
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, 10);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pe.getParam1() < MidAutumnEvent.MAX_NUM || 1 == pe.getParam5()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MID_AUTUMU_ACTIVITY_CAN_NOT_GET);
        }
        this.playerEventDao.setParam5(playerId, 10, 0);
        this.dataGetter.getPlayerTicketsDao().addTickets(playerId, MidAutumnEvent.TICKET, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u5f97\u70b9\u5238", true);
        this.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, MidAutumnEvent.IRON, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u53d6\u9554\u94c1", true);
        this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, MidAutumnEvent.FOOD, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u5f97\u7cae\u98df");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("ticket", MidAutumnEvent.TICKET);
        doc.createElement("iron", MidAutumnEvent.IRON);
        doc.createElement("food", MidAutumnEvent.FOOD);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getNationalDayBigGift(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.playerEventDao.getPlayerEvent(playerId, 11);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_EVENT_10001);
        }
        if (pe.getParam1() < MidAutumnEvent.MAX_NUM || pe.getParam4() >= 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MID_AUTUMU_ACTIVITY_CAN_NOT_GET);
        }
        final int state = pe.getParam4();
        if (state == 0) {
            this.treasureService.tryGetGeneralTreasure(playerDto, 4, false, 0, 0, true, "\u56fd\u5e86\u6d3b\u52a8\u83b7\u5f97\u548c\u6c0f\u74a7");
            this.playerEventDao.updateParam4(playerId, 11, 1);
        }
        else if (1 == state) {
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), 500, "\u56fd\u5e86\u6d3b\u52a8\u7b2c\u4e00\u6b21\u8d2d\u4e70\u548c\u6c0f\u74a7\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            this.treasureService.tryGetGeneralTreasure(playerDto, 4, false, 0, 0, false, "\u56fd\u5e86\u6d3b\u52a8\u7b2c\u4e00\u6b21\u82b1\u91d1\u5e01\u83b7\u5f97\u548c\u6c0f\u74a7");
            this.playerEventDao.updateParam4(playerId, 11, 2);
        }
        else if (2 == state) {
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), 500, "\u56fd\u5e86\u6d3b\u52a8\u7b2c\u4e8c\u6b21\u8d2d\u4e70\u548c\u6c0f\u74a7\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            this.treasureService.tryGetGeneralTreasure(playerDto, 4, false, 0, 0, false, "\u56fd\u5e86\u6d3b\u52a8\u7b2c\u4e8c\u6b21\u82b1\u91d1\u5e01\u83b7\u5f97\u548c\u6c0f\u74a7");
            this.playerEventDao.updateParam4(playerId, 11, 3);
        }
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("heshibi", 1));
    }
    
    @Override
    public void moonCakeTimeTask(final String param) {
        final long start = System.currentTimeMillis();
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "moonCakeTimeTask", 0, 0L, param));
        try {
            final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getPlayerEventList(10);
            for (final PlayerEvent playerEvent : peList) {
                try {
                    final int param2 = playerEvent.getParam3();
                    final int param3 = playerEvent.getParam4();
                    int food = 0;
                    int dstq = 0;
                    int phantom = 0;
                    int token = 0;
                    if (1 == SlaveUtil.hasReward(param2, 1) && SlaveUtil.hasReward(param3, 1) == 0) {
                        food += MidAutumnEvent.rewardMap.get(1);
                    }
                    if (1 == SlaveUtil.hasReward(param2, 2) && SlaveUtil.hasReward(param3, 2) == 0) {
                        food += MidAutumnEvent.rewardMap.get(2);
                    }
                    if (1 == SlaveUtil.hasReward(param2, 3) && SlaveUtil.hasReward(param3, 3) == 0) {
                        dstq += MidAutumnEvent.rewardMap.get(3);
                    }
                    if (1 == SlaveUtil.hasReward(param2, 4) && SlaveUtil.hasReward(param3, 4) == 0) {
                        phantom += MidAutumnEvent.rewardMap.get(4);
                    }
                    if (1 == SlaveUtil.hasReward(param2, 5) && SlaveUtil.hasReward(param3, 5) == 0) {
                        token += MidAutumnEvent.rewardMap.get(5);
                    }
                    final int playerId = playerEvent.getPlayerId();
                    final StringBuffer sb = new StringBuffer(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_HEAD);
                    int count = 0;
                    if (food > 0) {
                        this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, food, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u7cae\u98df");
                        if (count > 0) {
                            sb.append("\uff0c");
                        }
                        ++count;
                        sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_FOOD, new Object[] { food }));
                    }
                    if (dstq > 0) {
                        this.dataGetter.getStoreHouseService().gainSearchItems(106, dstq, PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId)), "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
                        if (count > 0) {
                            sb.append("\uff0c");
                        }
                        ++count;
                        sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_DSTQ, new Object[] { dstq }));
                    }
                    if (phantom > 0) {
                        this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerId, phantom, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
                        if (count > 0) {
                            sb.append("\uff0c");
                        }
                        ++count;
                        sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_PHANTOM, new Object[] { phantom }));
                    }
                    if (token > 0) {
                        this.dataGetter.getStoreHouseService().gainItems(playerId, token, MidAutumnEvent.IRON_TOKEN_INDEX, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u9554\u94c1\u4ee4");
                        if (count > 0) {
                            sb.append("\uff0c");
                        }
                        ++count;
                        sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_IRON_TOKEN, new Object[] { token }));
                    }
                    if (count <= 0) {
                        continue;
                    }
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_TITLE, sb.toString(), 1, playerId, 0);
                }
                catch (Exception e) {
                    EventService.errorLog.error("class:EventService#method:moonCakeTimeTask#playerId:" + playerEvent.getPlayerId(), e);
                }
            }
            this.playerEventDao.clearMidAutumn(10, MidAutumnEvent.limitMap.get(1));
        }
        catch (Exception e2) {
            EventService.errorLog.error("class:EventService#method:moonCakeTimeTask", e2);
        }
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "moonCakeTimeTask", 2, System.currentTimeMillis() - start, param));
        EventService.timerLog.info(LogUtil.formatTimerLog("eventService", "moonCakeTimeTask", System.currentTimeMillis() - start));
    }
    
    @Override
    public void nationalDayTimeTask(final String param) {
        final long start = System.currentTimeMillis();
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "nationalDayTimeTask", 0, 0L, param));
        try {
            final int day = Integer.parseInt(param);
            final int npcBase = NationalDayEvent.npcMaxMap.get(day);
            this.dataGetter.getPlayerEventDao().updateParam8All(11, npcBase);
            CityEventManager.getInstance().removePlayerEventByEventType(4);
            this.dataGetter.getPlayerBattleAttributeDao().resetEventNationalTreasureCountToday();
            CityEventManager.getInstance().addFirstRoundPlayerEvent(4);
        }
        catch (Exception e) {
            EventService.errorLog.error("class:EventService#method:nationalDayTimeTask", e);
        }
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "nationalDayTimeTask", 2, System.currentTimeMillis() - start, param));
        EventService.timerLog.info(LogUtil.formatTimerLog("eventService", "nationalDayTimeTask", System.currentTimeMillis() - start));
    }
    
    @Override
    public void addBmw(final int playerId, final int num, final String attribute) {
        EventService.timerLog.error("class:EventService#method:addBmw:#playerId:" + playerId + "#num:" + num);
        if (EventUtil.isEventTime(11) && num > 0) {
            this.playerEventDao.addBmw(playerId, 11, num, attribute);
        }
    }
    
    @Override
    public void addXo(final int playerId, final int num, final String attribute) {
        EventService.timerLog.error("class:EventService#method:addXo:#playerId:" + playerId + "#num:" + num);
        if (EventUtil.isEventTime(11) && num > 0) {
            this.playerEventDao.addXo(playerId, 11, num, attribute);
        }
    }
    
    @Override
    public void addPicasso(final int playerId, final int num, final String attribute) {
        EventService.timerLog.error("class:EventService#method:addPicasso:#playerId:" + playerId + "#num:" + num);
        if (EventUtil.isEventTime(11) && num > 0) {
            this.playerEventDao.addPicasso(playerId, 11, num, attribute);
        }
    }
    
    @Override
    public void xiLianTimeTask(final String param) {
        final long start = System.currentTimeMillis();
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "xiLianTimeTask", 0, 0L, param));
        try {
            this.dataGetter.getStoreHouseDao().clearMarkId();
            final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getXiLianRewardList(14);
            for (final PlayerEvent pe : peList) {
                try {
                    int count = 0;
                    final StringBuffer mailMsg = new StringBuffer(LocalMessages.XILIAN_MAIL_CONTENT_HEAD_TODAY);
                    for (int i = 1; i <= XiLianEvent.LENGTH; ++i) {
                        if (1 == SlaveUtil.hasReward(pe.getParam2(), i) && 1 != SlaveUtil.hasReward(pe.getParam3(), i)) {
                            this.dataGetter.getStoreHouseService().gainItems(pe.getPlayerId(), 1, XiLianEvent.itemIdMap.get(i), "\u6d17\u7ec3\u6d3b\u52a8\u83b7\u53d6\u4ee4");
                            mailMsg.append(MessageFormatter.format(LocalMessages.XILIAN_MAIL_TOKEN, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)XiLianEvent.itemIdMap.get(i))).getName() }));
                            ++count;
                        }
                    }
                    if (count > 0) {
                        mailMsg.setLength(mailMsg.length() - 1);
                        mailMsg.append(LocalMessages.XILIAN_MAIL_CONTENT_TAIL);
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.XILIAN_MAIL_TITLE, mailMsg.toString(), 1, pe.getPlayerId(), 0);
                    }
                    final int num = pe.getParam5();
                    if (num <= 0) {
                        continue;
                    }
                    this.dataGetter.getPlayerQuenchingRelativeDao().addFreeNiubiTimes(pe.getPlayerId(), num, "\u6d17\u70bc\u6d3b\u52a8\u589e\u52a0\u514d\u8d39\u81ea\u5c0a\u6d17\u70bc\u6b21\u6570");
                    final String msg = MessageFormatter.format(LocalMessages.XILIAN_MAIL_CONTENT_FREE_TODAY, new Object[] { num });
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.XILIAN_MAIL_TITLE, msg, 1, pe.getPlayerId(), 0);
                }
                catch (Exception e) {
                    EventService.errorLog.error("class:EventServicet#method:xiLianTimeTask#playerId:" + pe.getPlayerId(), e);
                }
            }
            this.dataGetter.getPlayerEventDao().clearEvent(14);
        }
        catch (Exception e2) {
            EventService.errorLog.error("class:EventService#method:xiLianTimeTask", e2);
        }
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "xiLianTimeTask", 2, System.currentTimeMillis() - start, param));
        EventService.timerLog.info(LogUtil.formatTimerLog("eventService", "xiLianTimeTask", System.currentTimeMillis() - start));
    }
    
    @Override
    public void baiNianTimeTask(final String param) {
        final long start = System.currentTimeMillis();
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "baiNianTimeTask", 0, 0L, param));
        try {
            Players.push(Integer.parseInt(param), PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveBaiNianBuff", 0));
        }
        catch (Exception e) {
            EventService.errorLog.error("class:EventService#method:baiNianTimeTask", e);
        }
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "baiNianTimeTask", 2, System.currentTimeMillis() - start, param));
    }
    
    @Override
    public void lanternTimeTask(final String param) {
        final long start = System.currentTimeMillis();
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "lanternTimeTask", 0, 0L, param));
        try {
            final LanternTask lTask = new LanternTask(this.dataGetter);
            lTask.start();
            EventService.timerLog.error("........lanterThreadStart#time:" + new Date());
        }
        catch (Exception e) {
            EventService.errorLog.error("class:EventService#method:lanternTimeTask", e);
        }
        EventService.timerLog.info(LogUtil.formatThreadLog("EventService", "lanternTimeTask", 2, System.currentTimeMillis() - start, param));
    }
    
    @Transactional
    @Override
    public byte[] getXiLianReward(final int playerId) {
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 14);
        if (pe == null || pe.getParam5() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NO_FREE_XILIAN_TIME);
        }
        final int num = pe.getParam5();
        this.dataGetter.getPlayerEventDao().reduceParam5(playerId, 14, num);
        this.dataGetter.getPlayerQuenchingRelativeDao().addFreeNiubiTimes(playerId, num, "\u6d17\u70bc\u6d3b\u52a8\u589e\u52a0\u514d\u8d39\u81ea\u5c0a\u6d17\u70bc\u6b21\u6570");
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("num", num));
    }
    
    @Transactional
    @Override
    public byte[] decorateTree(final PlayerDto playerDto, final int id) {
        if (id < 1 || id > ChristmasDayEvent.LENGTH) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!EventUtil.isEventTime(16)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 16);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_TARK_PART_IN);
        }
        final int suitNum = ChristmasDayEvent.getSuitNum(id, pe);
        final String key = String.valueOf(id) + "_" + suitNum;
        final int needNum = ChristmasDayEvent.baseNumMap.get(key);
        if (needNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_DECORATE_FINISH);
        }
        final int num = ChristmasDayEvent.getNum(id, pe);
        if (needNum > num) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_DECORATE_GOOD_NOT_ENOUGH);
        }
        int num2 = pe.getParam6();
        int num3 = pe.getParam7();
        int num4 = pe.getParam8();
        final int before = pe.getParam4();
        final int layer = SlaveUtil.getLast1Index(before) + 1;
        final int maxNeedNum = ChristmasDayEvent.getMaxSuitNum(layer, id);
        if (suitNum >= maxNeedNum) {
            return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.CHRISTMAS_DAY_CAN_NOT_DECORATE_CROSS, new Object[] { layer }));
        }
        if (1 == id) {
            this.dataGetter.getPlayerEventDao().reduceParam1AndParam6(playerId, 16, needNum, 1);
            ++num2;
        }
        else if (2 == id) {
            this.dataGetter.getPlayerEventDao().reduceParam2AndParam7(playerId, 16, needNum, 1);
            ++num3;
        }
        else if (3 == id) {
            this.dataGetter.getPlayerEventDao().reduceParam3AndParam8(playerId, 16, needNum, 1);
            ++num4;
        }
        final int after = ChristmasDayEvent.getBits(num2, num3, num4);
        int finishId = 0;
        if (after > before) {
            this.dataGetter.getPlayerEventDao().updateParam4(playerId, 16, after);
            finishId = SlaveUtil.getLast1Index(after);
        }
        final String rewardStr = ChristmasDayEvent.getSingleRewardNumString(playerDto.playerLv, layer);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u88c5\u9970\u5956\u52b1", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("finishId", finishId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] yaoYiYao(final int playerId) {
        if (!EventUtil.isEventTime(16)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_ACTIVITY);
        }
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 16);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_TARK_PART_IN);
        }
        if (SlaveUtil.get1Num(pe.getParam5()) < ChristmasDayEvent.LENGTH) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_CAN_NOT_YAOYIYAO);
        }
        if (SlaveUtil.get1Num(pe.getParam10()) > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_PLEASE_PICK_UP);
        }
        final int times = pe.getParam9();
        if (times > ChristmasDayEvent.ZYYY_NUM) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_CAN_NOT_YAOYIYAO_AGAIN);
        }
        if (times > 0) {
            final int gold = ChristmasDayEvent.goldMap.get(times);
            if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), gold, "\u5723\u8bde\u6d3b\u52a8\u518d\u6447\u4e00\u6447\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        final int bits = ChristmasDayEvent.getRandomBits();
        this.dataGetter.getPlayerEventDao().updateParam9UpdateParam10(playerId, 16, times + 1, bits);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] getChristmasBigGift(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(16)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 16);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_TARK_PART_IN);
        }
        final int bits = pe.getParam10();
        if (bits <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CHRISTMAS_DAY_NO_BIG_GIFT);
        }
        final int id = SlaveUtil.getFirst1Index(bits);
        final String rewardStr = ChristmasDayEvent.getBigRewardNumString(playerDto.playerLv, id);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5723\u8bde\u5927\u793c\u5305", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        final int newBits = (int)SlaveUtil.set0(bits, id);
        this.dataGetter.getPlayerEventDao().updateParam10(playerId, 16, newBits);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getWishBigGift(final PlayerDto playerDto, final int id) {
        if (!EventUtil.isEventTime(17)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_DAY_NO_ACTIVITY);
        }
        if (id <= 0 || id > WishEvent.LENGTH) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 17);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_DAY_NO_TARK_PART_IN);
        }
        if (EventUtil.getEventCd(17) > WishEvent.START_RECEIVED_DAY_MS) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_PLEASE_WAITING);
        }
        final int select = pe.getParam2();
        if (select == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_NO_WISH);
        }
        if (SlaveUtil.hasReward(pe.getParam3(), id) != 1 || SlaveUtil.hasReward(pe.getParam4(), id) == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WISH_RECEIVED);
        }
        String rewardStr = null;
        if (1 == select) {
            rewardStr = WishEvent.getBaseRewardNumString(playerDto.playerLv, pe.getParam1(), id);
        }
        else if (2 == select) {
            rewardStr = WishEvent.getGoldRewardNumString(playerDto.playerLv, pe.getParam1(), id);
        }
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u65b0\u5e74\u8bb8\u613f\u6d3b\u52a8\u5956\u52b1", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        this.dataGetter.getPlayerEventDao().setParam4(playerId, 17, id - 1);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getBaiNianBigGift(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!EventUtil.isEventTime(19)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 19);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_NO_TARK_PART_IN);
        }
        if (pe.getParam1() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_PLEASE_BEFORE_CHOICE);
        }
        if (pe.getParam2() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BAINIAN_ALREADY_RECEIVED);
        }
        final Date cd = TimeUtil.nowAddHours(24);
        this.dataGetter.getPlayerEventDao().updateParam2updateCD1(playerId, 19, 1, cd);
        BaiNianEvent.buffMap.put(playerId, cd.getTime());
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("haveBaiNianBuff", 1);
        doc2.createElement("baiNianBuffCd", 24 * Constants.ONE_HOUR_MS);
        doc2.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        this.dataGetter.getJobService().addJob("eventService", "baiNianTimeTask", new StringBuilder(String.valueOf(playerId)).toString(), cd.getTime());
        final String rewardStr = BaiNianEvent.getBaseRewardNumString(playerDto.playerLv);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u62dc\u5e74\u6d3b\u52a8\u5956\u52b1", null);
        final JsonDocument doc3 = new JsonDocument();
        doc3.startObject();
        doc3.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc3.startObject();
            doc3.createElement("type", temp.getType());
            doc3.createElement("value", temp.getNum());
            doc3.endObject();
        }
        doc3.endArray();
        doc3.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc3.toByte());
    }
    
    @Override
    public byte[] getLanternBigGift(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(21)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 21);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_TARK_PART_IN);
        }
        if (LanternTask.state != LanternTask.State.OVER) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_OVER);
        }
        final int total = pe.getParam2();
        final int title = LanternEvent.getTitle(total);
        if (title <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_TITLE);
        }
        if (1 == pe.getParam6()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_TITLE_RECEIVED);
        }
        this.dataGetter.getPlayerEventDao().updateParam6(playerId, 21, 1);
        final String rewardStr = LanternEvent.getTitleRewardNumString(title, playerDto.playerLv, playerDto.forceId, this.dataGetter);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u5143\u5bb5\u6d3b\u52a8\u79f0\u53f7\u5956\u52b1", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : map.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] buyBeast(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(18)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 18);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_TARK_PART_IN);
        }
        if (pe.getParam5() < BeastEvent.LENGTH) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_CAN_NOT_BUY_BEAST);
        }
        if (pe.getParam1() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_FINISH_CURRENT_BEAST);
        }
        final int beastGold = BeastEvent.beastGoldTuple.left + BeastEvent.beastGoldTuple.right * pe.getParam3();
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), beastGold, "\u6253\u5e74\u517d\u6d3b\u52a8\u8d2d\u4e70\u5e74\u517d\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerEventDao().updateParam1updateParam2(playerId, 18, BeastEvent.LENGTH, 0);
        this.dataGetter.getPlayerEventDao().addParam3(playerId, 18, 1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] recoverBeastCd(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(18)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 18);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NO_TARK_PART_IN);
        }
        final Date cd = pe.getCd1();
        if (cd == null || cd.before(new Date())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BEAST_NOT_IN_CD);
        }
        final int beastGold = (int)Math.ceil((cd.getTime() - System.currentTimeMillis()) * 1.0 / Constants.ONE_HOUR_MS) * BeastEvent.cdGoldTuple.right;
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), beastGold, "\u6253\u5e74\u517d\u6d3b\u52a8\u79d2\u5e74\u517dCD\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerEventDao().updateCd1(playerId, 18, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] buyLantern(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(21)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 21);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_TARK_PART_IN);
        }
        final long canBuy = TimeUtil.getSpecialTime(21, 30);
        final long current = System.currentTimeMillis();
        if (current <= canBuy) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_CAN_NOT_BUY);
        }
        final int needNum = 10 - LanternTask.rounds.get();
        if (pe.getParam1() >= needNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_MAX_LANTERN);
        }
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), 5, "\u5143\u5bb5\u6d3b\u52a8\u8d2d\u4e70\u8f6e\u6b21\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerEventDao().addParam1(playerId, 21, 1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static void main(final String[] args) {
        System.out.println(WebUtil.nextInt(1));
        System.out.println(WebUtil.nextInt(1));
        System.out.println(WebUtil.nextInt(1));
        System.out.println(WebUtil.nextInt(2));
        System.out.println(Integer.MAX_VALUE);
    }
}
