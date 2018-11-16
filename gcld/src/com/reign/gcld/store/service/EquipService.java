package com.reign.gcld.store.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.kfzb.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import org.springframework.transaction.annotation.*;
import com.reign.util.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.kfzb.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.task.message.*;
import org.mybatis.spring.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.util.*;
import com.reign.kf.comm.util.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

@Component("equipService")
public class EquipService implements IEquipService
{
    private static final DayReportLogger logger;
    private static final Logger log;
    private static final Logger timerLog;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private EquipSuitCache equipSuitCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IStoreHouseBakDao storeHouseBakDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IKfzbFeastDao kfzbFeastDao;
    @Autowired
    private ArmsJsSkillCache armsJsSkillCache;
    
    static {
        logger = new DayReportLogger();
        log = CommonLog.getLog(EquipService.class);
        timerLog = new TimerLogger();
    }
    
    @Transactional
    @Override
    public byte[] getEquipInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("equips");
        doc.endArray();
        final List<StoreHouse> gemList = this.storeHouseDao.getUnSetGems(playerId);
        doc.startArray("gem");
        for (final StoreHouse storeHouse : gemList) {
            final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)storeHouse.getItemId());
            doc.startObject();
            doc.createElement("vId", storeHouse.getVId());
            doc.createElement("gemId", gem.getId());
            doc.createElement("gemLv", gem.getGemLv());
            doc.createElement("gemName", gem.getName());
            doc.createElement("gemPic", gem.getPic());
            doc.createElement("att", gem.getAtt());
            doc.createElement("def", gem.getDef());
            doc.createElement("blood", gem.getBlood() / 3);
            doc.createElement("num", storeHouse.getNum());
            doc.createElement("goodsType", storeHouse.getGoodsType());
            if (1 == storeHouse.getGoodsType()) {
                doc.createElement("weaponId", storeHouse.getGemId());
                doc.createElement("curPs", storeHouse.getAttribute());
                doc.createElement("maxPs", gem.getUpgradeGem1());
                if (StringUtils.isNotBlank(storeHouse.getRefreshAttribute())) {
                    final String[] skills = storeHouse.getRefreshAttribute().split(";");
                    doc.startArray("skills");
                    String[] array;
                    for (int length = (array = skills).length, i = 0; i < length; ++i) {
                        final String str = array[i];
                        final String[] skill = str.split(":");
                        doc.startObject();
                        doc.createElement("type", skill[0]);
                        doc.createElement("lv", skill[1]);
                        final ArmsJsSkill ajs = (ArmsJsSkill)this.armsJsSkillCache.get((Object)Integer.valueOf(skill[0]));
                        doc.createElement("name", ajs.getName());
                        doc.createElement("pic", ajs.getPic());
                        doc.createElement("intro", ajs.getIntro());
                        doc.endObject();
                    }
                    doc.endArray();
                }
            }
            else {
                final ArmsGem nextGem = (ArmsGem)this.armsGemCache.get((Object)(gem.getId() + 1));
                if (nextGem != null) {
                    doc.createElement("nextAtt", nextGem.getAtt());
                    doc.createElement("nextDef", nextGem.getDef());
                    doc.createElement("nextBlood", nextGem.getBlood() / 3);
                }
            }
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("skillNumLvs");
        for (final Map.Entry<Integer, Integer> entry : ArmsGemCache.skillNumLvMap.entrySet()) {
            doc.startObject();
            doc.createElement("skillNum", entry.getKey());
            doc.createElement("lv", entry.getValue());
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("jsLvNums");
        for (final Map.Entry<Integer, Integer> entry : ArmsGemCache.jsLvNumMap.entrySet()) {
            doc.startObject();
            doc.createElement("lv", entry.getKey());
            doc.createElement("num", entry.getValue());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("goldDamo", ((Chargeitem)this.dataGetter.getChargeitemCache().get((Object)83)).getCost());
        doc.createElement("goldJinglian", ((Chargeitem)this.dataGetter.getChargeitemCache().get((Object)84)).getCost());
        doc.createElement("jsMaxLv", ArmsGemCache.JS_MAX_LV);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] updateEquip(final PlayerDto playerDto, final int vId) {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public List<StoreHouse> beforeOpenStoreHouse(final PlayerDto playerDto) {
        List<StoreHouse> list = this.storeHouseDao.getStoreHouseGoods(playerDto.playerId);
        boolean isChange = false;
        for (int i = 0; i < list.size(); ++i) {
            final StoreHouse sh = list.get(i);
            if (sh.getType() == 11) {
                final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                final int left = sh.getNum() % items.getChangeNum();
                final int change = sh.getNum() / items.getChangeNum();
                if (change > 0) {
                    for (int m = 0; m < change; ++m) {
                        this.storeHouseService.gainItems(playerDto.playerId, 1, items.getChangeItemId(), LocalMessages.T_LOG_ITEM_6);
                        final Items changeItems = (Items)this.itemsCache.get((Object)items.getChangeItemId());
                        final String msg = MessageFormatter.format(LocalMessages.CHANGE_SUIT_INFO, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(changeItems.getName()) });
                        this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
                    }
                    this.storeHouseDao.reduceNum(sh.getVId(), change * items.getChangeNum());
                    isChange = true;
                }
                if (left == 0) {
                    this.storeHouseDao.deleteById(sh.getVId());
                }
            }
        }
        if (isChange) {
            list = this.storeHouseDao.getStoreHouseGoods(playerDto.playerId);
        }
        return list;
    }
    
    @Transactional
    @Override
    public byte[] openStoreHouse(final PlayerDto playerDto, final List<StoreHouse> list) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        doc.createElement("maxSize", pa.getMaxStoreNum());
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)10);
        doc.createElement("cost", ci.getCost());
        doc.createElement("vipLimit", ci.getLv());
        doc.startArray("goods");
        int copper = 100;
        for (int i = 0; i < list.size(); ++i) {
            final StoreHouse sh = list.get(i);
            if (sh.getState() == 0) {
                if (!sh.getType().equals(2)) {
                    copper = 100;
                    doc.startObject();
                    doc.createElement("vId", sh.getVId());
                    doc.createElement("itemId", sh.getItemId());
                    doc.createElement("kind", sh.getType());
                    if (sh.getType() == 1) {
                        final Equip equip = (Equip)this.equipCache.get((Object)sh.getItemId());
                        doc.createElement("itemName", equip.getName());
                        doc.createElement("pic", equip.getPic());
                        doc.createElement("lv", sh.getLv());
                        doc.createElement("intro", equip.getIntro());
                        doc.createElement("quality", equip.getQuality());
                        doc.createElement("suitName", this.equipSuitCache.getSuitName(equip.getId()));
                        doc.createElement("type", equip.getType());
                        if (equip.getType() == 6 || equip.getType() == 5) {
                            doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
                        }
                        else {
                            doc.createElement("attribute", sh.getAttribute());
                        }
                        EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip);
                        EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, sh.getSpecialSkillId(), sh.getRefreshAttribute());
                        copper = equip.getCopperSold();
                    }
                    else if (sh.getType() == 2) {
                        final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)sh.getItemId());
                        doc.createElement("itemName", gem.getName());
                        doc.createElement("pic", gem.getPic());
                        doc.createElement("gemLv", gem.getGemLv());
                        doc.createElement("att", gem.getAtt());
                        doc.createElement("def", gem.getDef());
                        doc.createElement("blood", gem.getBlood() / 3);
                        doc.createElement("num", sh.getNum());
                    }
                    else if (sh.getType() == 3) {
                        final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
                        doc.createElement("itemName", generalTreasure.getName());
                        doc.createElement("pic", generalTreasure.getPic());
                        doc.createElement("intro", generalTreasure.getIntro());
                        doc.createElement("quality", generalTreasure.getQuality());
                        final String[] strs = sh.getAttribute().split(",");
                        doc.createElement("type", generalTreasure.getType());
                        doc.createElement("att1", strs[0]);
                        if (strs.length < 2) {
                            doc.createElement("att2", 0);
                        }
                        else {
                            doc.createElement("att2", strs[1]);
                        }
                        doc.createElement("minLv", generalTreasure.getMinGeneralLevel());
                        copper = generalTreasure.getCopperPrice();
                    }
                    else if (sh.getType() == 4 || sh.getType() == 5 || sh.getType() == 6 || sh.getType() == 7 || sh.getType() == 8 || sh.getType() == 9 || sh.getType() == 12 || sh.getType() == 13 || sh.getType() == 17 || sh.getType() == 15 || sh.getType() == 16 || sh.getType() == 18 || sh.getType() == 19 || sh.getType() == 20 || sh.getType() == 21 || sh.getType() == 22) {
                        final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                        doc.createElement("itemName", items.getName());
                        doc.createElement("pic", items.getPic());
                        doc.createElement("intro", items.getIntro());
                        if (sh.getType() == 17) {
                            final String itemStr = items.getEffect();
                            this.appendResourceTokenEffectInfo(doc, itemStr);
                        }
                        else {
                            doc.createElement("effectType", sh.getGoodsType());
                            doc.createElement("effectNum", sh.getAttribute());
                        }
                        doc.createElement("num", sh.getNum());
                        doc.createElement("quality", sh.getQuality());
                        copper = items.getCopper();
                        if (sh.getType() == 18) {
                            final int id = IronRewardEvent.itemIdReverseMap.get(items.getId());
                            doc.createElement("openType", id);
                        }
                        else if (sh.getType() == 19) {
                            final int id = XiLianEvent.itemIdReverseMap.get(items.getId());
                            doc.createElement("openType", id);
                        }
                    }
                    else if (sh.getType() == 11) {
                        final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                        final Items changeIteams = (Items)this.itemsCache.get((Object)items.getChangeItemId());
                        doc.createElement("itemName", items.getName());
                        doc.createElement("pic", items.getPic());
                        doc.createElement("intro", items.getIntro());
                        doc.createElement("effectType", sh.getGoodsType());
                        doc.createElement("effectNum", sh.getAttribute());
                        doc.createElement("num", sh.getNum());
                        doc.createElement("quality", sh.getQuality());
                        copper = items.getCopper();
                        doc.createElement("changeNum", changeIteams.getName());
                        doc.createElement("changeName", changeIteams.getChangeNum());
                    }
                    else if (sh.getType() == 10) {
                        final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                        final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(sh.getItemId());
                        if (items == null || equipCoordinates == null) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                        }
                        doc.createElement("itemName", equipCoordinates.getName());
                        doc.createElement("pic", equipCoordinates.getPic());
                        doc.createElement("num", sh.getNum());
                        doc.createElement("quality", sh.getQuality());
                        final Integer[] singleType = this.equipCache.getSkillArray(equipCoordinates.getId());
                        doc.startArray("subEquips");
                        for (int j = 1; j <= singleType.length; ++j) {
                            final Equip equip2 = this.equipCache.getSuitSingleEquipByType(j);
                            if (equip2 == null) {
                                EquipService.log.error("can't find purple equip...type:" + j);
                            }
                            else {
                                doc.startObject();
                                doc.createElement("equipId", equip2.getId());
                                doc.createElement("type", j);
                                doc.createElement("equipName", equip2.getName());
                                doc.createElement("equipPic", equip2.getPic());
                                doc.createElement("skillNum", 4);
                                final int skillType = singleType[j - 1];
                                final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)skillType);
                                doc.createElement("skillType", skillType);
                                doc.createElement("quality", equip2.getQuality());
                                doc.createElement("skillName", equipSkill.getName());
                                doc.createElement("skillLv", equip2.getSkillLvMax());
                                doc.endObject();
                            }
                        }
                        doc.endArray();
                        final CommonValueParameter cvp = this.equipCache.getItemIdToAttributeMap().get(equipCoordinates.getItemId());
                        if (cvp != null) {
                            if (cvp.getPar1() > 0) {
                                doc.createElement("att", cvp.getPar1());
                            }
                            if (cvp.getPar2() > 0) {
                                doc.createElement("def", cvp.getPar2());
                            }
                            if (cvp.getPar3() > 0) {
                                doc.createElement("blood", cvp.getPar3());
                            }
                        }
                        doc.createElement("suitIntro", equipCoordinates.getIntro());
                    }
                    else if (sh.getType() == 14) {
                        final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                        if (items == null) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                        }
                        final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(items.getId());
                        if (equipProset == null) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                        }
                        doc.createElement("itemName", equipProset.getName());
                        doc.createElement("pic", equipProset.getPic());
                        doc.createElement("num", sh.getNum());
                        doc.createElement("quality", sh.getQuality());
                        final EquipCoordinates equipCoordinates2 = this.equipCache.getMainSuit(equipProset.getId());
                        if (equipCoordinates2 == null) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                        }
                        this.appendSingleEquipCoordinatesInfo(equipCoordinates2, doc);
                        final CommonValueParameter cvp = this.equipCache.getItemIdToAttributeMap().get(equipProset.getItemId());
                        if (cvp != null) {
                            if (cvp.getPar1() > 0) {
                                doc.createElement("att", cvp.getPar1());
                            }
                            if (cvp.getPar2() > 0) {
                                doc.createElement("def", cvp.getPar2());
                            }
                            if (cvp.getPar3() > 0) {
                                doc.createElement("blood", cvp.getPar3());
                            }
                        }
                        doc.createElement("suitIntro", equipProset.getIntro());
                    }
                    doc.createElement("copper", copper);
                    if (isEquip(sh.getType())) {
                        final Long bindExpireTime = sh.getBindExpireTime();
                        final int state = getBindState(bindExpireTime);
                        if (state == -1) {
                            doc.createElement("isBind", (-1));
                        }
                        else if (state == 0) {
                            doc.createElement("isBind", 0);
                        }
                        else {
                            doc.createElement("isBind", 1);
                            doc.createElement("leftTime", state);
                        }
                    }
                    doc.endObject();
                }
            }
        }
        doc.endArray();
        int freeCard = 0;
        int goldCard = 0;
        final KfzbFeast kf = this.kfzbFeastDao.read(playerDto.playerId);
        if (kf != null) {
            freeCard = kf.getFreeCard();
            goldCard = kf.getGoldCard();
        }
        doc.createElement("freeCard", freeCard);
        doc.createElement("goldCard", goldCard);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void appendResourceTokenEffectInfo(final JsonDocument doc, final String itemStr) {
        try {
            final String[] single = itemStr.split(";");
            final int effectType = Integer.parseInt(single[0]);
            int effectNum = Integer.parseInt(single[1]);
            int day = Integer.parseInt(single[2]);
            day = getDayByType(day);
            effectNum = this.getPercentage(effectNum);
            doc.createElement("effectType", effectType);
            doc.createElement("effectNum", effectNum);
            doc.createElement("effectDay", day);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    private int getPercentage(final int effectNum) {
        switch (effectNum) {
            case 2: {
                return 200;
            }
            case 3: {
                return 300;
            }
            default: {
                return 100;
            }
        }
    }
    
    public static int getDayByType(final int day) {
        switch (day) {
            case 3: {
                return 30;
            }
            case 2: {
                return 7;
            }
            default: {
                return 1;
            }
        }
    }
    
    private void appendSingleEquipCoordinatesInfo(final EquipCoordinates equipCoordinates, final JsonDocument doc) {
        try {
            final Integer[] singleType = this.equipCache.getSkillArray(equipCoordinates.getId());
            doc.startArray("subEquips");
            for (int j = 1; j <= singleType.length; ++j) {
                final Equip equip = this.equipCache.getSuitSingleEquipByType(j);
                if (equip == null) {
                    EquipService.log.error("can't find purple equip...type:" + j);
                }
                else {
                    doc.startObject();
                    doc.createElement("equipId", equip.getId());
                    doc.createElement("type", j);
                    doc.createElement("equipName", equip.getName());
                    doc.createElement("equipPic", equip.getPic());
                    doc.createElement("skillNum", 4);
                    final int skillType = singleType[j - 1];
                    final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)skillType);
                    doc.createElement("skillType", skillType);
                    doc.createElement("quality", equip.getQuality());
                    doc.createElement("skillName", equipSkill.getName());
                    doc.createElement("skillLv", equip.getSkillLvMax());
                    doc.endObject();
                }
            }
            doc.endArray();
        }
        catch (Exception e) {
            EquipService.log.error(e.getMessage());
            EquipService.log.error(this, e);
        }
    }
    
    @Transactional
    @Override
    public byte[] buySTSize(final int playerId) {
        final Player player = this.playerDao.read(playerId);
        final int maxStoreNum = this.playerAttributeDao.getMaxStoreNum(playerId);
        if (maxStoreNum >= 200) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.STORE_HOURSE_MAX_ERROR);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)10);
        if (player.getConsumeLv() < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        if (!this.playerDao.consumeGold(player, ci)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerAttributeDao.addMaxStoreNum(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("maxSize", maxStoreNum + 1);
        doc.createElement("cost", ci.getCost());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] sellGoods(final PlayerDto playerDto, final int vId, int num) {
        final int playerId = playerDto.playerId;
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null || sh.getPlayerId() != playerDto.playerId || sh.getOwner() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int type = sh.getType();
        if (isEquip(type)) {
            final int state = getBindState(sh.getBindExpireTime());
            if (state != 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.EQUIP_IS_BOUND);
            }
        }
        if (type == 3 && sh.getState() != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (type == 10 || type == 14) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.STORE_HOUSE_CANNOT_SELL);
        }
        if (playerDto.playerLv < 18) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_CAN_NOT_SELL);
        }
        if ((type == 5 || type == 7) && sh.getItemId() != 401 && sh.getItemId() != 1701) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.STORE_HOUSE_CANNOT_SELL);
        }
        int copper = 100;
        String reason = "\u51fa\u552e\u88c5\u5907\u589e\u52a0\u94f6\u5e01";
        if (num <= 0) {
            num = 1;
        }
        final StoreHouseSell storeHouseSell = new StoreHouseSell();
        storeHouseSell.setAttribute(sh.getAttribute());
        storeHouseSell.setGemId(sh.getGemId());
        storeHouseSell.setType(sh.getType());
        storeHouseSell.setGoodsType(sh.getGoodsType());
        storeHouseSell.setItemId(sh.getItemId());
        storeHouseSell.setLv(sh.getLv());
        storeHouseSell.setNum(num);
        storeHouseSell.setPlayerId(playerId);
        storeHouseSell.setQuality(sh.getQuality());
        storeHouseSell.setSellTime(new Date());
        storeHouseSell.setRefreshAttribute(sh.getRefreshAttribute());
        final int q = (sh.getQuenchingTimes() == null) ? 0 : sh.getQuenchingTimes();
        storeHouseSell.setQuenchingTimes(q);
        final int qf = (sh.getQuenchingTimesFree() == null) ? 0 : sh.getQuenchingTimesFree();
        storeHouseSell.setQuenchingTimesFree(qf);
        storeHouseSell.setSpecialSkillId(sh.getSpecialSkillId());
        final Player player = this.playerDao.read(playerId);
        if (type == 1) {
            final Equip equip = (Equip)this.equipCache.get((Object)sh.getItemId());
            if (equip != null) {
                copper = equip.getCopperSold();
            }
            this.battleDataCache.removeEquipEffect(playerDto.playerId, sh.getOwner());
            this.storeHouseDao.deleteById(vId);
            EquipService.logger.info(LogUtil.formatEquipLog(player, "-", "\u5356\u51fa", true, (Equip)this.equipCache.get((Object)sh.getItemId()), sh, LocalMessages.T_LOG_EQUIP_4));
        }
        else if (type == 2 || type == 5 || type == 6 || type == 9 || type == 11 || type == 8 || type == 12 || type == 13 || type == 17 || sh.getType() == 15 || sh.getType() == 16 || sh.getType() == 18 || sh.getType() == 19 || sh.getType() == 20 || sh.getType() == 21 || sh.getType() == 22) {
            num = sh.getNum();
            storeHouseSell.setNum(num);
            if (num > sh.getNum()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_COUNT_NOT_ENOUGH);
            }
            if (num == sh.getNum()) {
                this.storeHouseDao.deleteById(vId);
            }
            else {
                this.storeHouseDao.reduceNum(vId, num);
            }
            if (type == 2) {
                reason = "\u51fa\u552e\u5b9d\u77f3\u589e\u52a0\u94f6\u5e01";
                EquipService.logger.info(LogUtil.formatGemLog(player, "-", "\u5356\u51fa", true, (ArmsGem)this.armsGemCache.get((Object)sh.getItemId()), num, LocalMessages.T_LOG_GEM_1));
            }
            else {
                reason = "\u51fa\u552e\u6536\u96c6\u7269\u54c1\u589e\u52a0\u94f6\u5e01";
                final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                copper = items.getCopper();
                EquipService.logger.info(LogUtil.formatItemsLog(player, "-", "\u5356\u51fa", true, (Items)this.itemsCache.get((Object)sh.getItemId()), sh, num, LocalMessages.T_LOG_ITEM_SJWP_1));
            }
            copper *= num;
        }
        else if (type == 3) {
            this.storeHouseDao.deleteById(vId);
            final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
            if (generalTreasure != null) {
                copper = generalTreasure.getCopperPrice();
            }
            reason = "\u51fa\u552e\u5fa1\u5b9d\u589e\u52a0\u94f6\u5e01";
            EquipService.logger.info(LogUtil.formatGeneralTreasureLog(player, "-", "\u5356\u51fa", (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId()), LocalMessages.T_LOG_GENERAL_TREASURE_4));
        }
        else if (type == 4 || type == 7) {
            num = sh.getNum();
            storeHouseSell.setNum(num);
            final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
            copper = items.getCopper();
            if (type == 4) {
                final int leftNum = sh.getNum() - num;
                if (leftNum <= 0) {
                    this.storeHouseDao.deleteById(vId);
                }
                else {
                    this.storeHouseDao.reduceNum(vId, num);
                }
                copper *= num;
            }
            else {
                this.storeHouseDao.deleteById(vId);
            }
        }
        this.storeHouseSellDao.create(storeHouseSell);
        this.playerResourceDao.addCopperIgnoreMax(playerDto.playerId, copper, reason, true);
        if (sh.getType() == 1 && sh.getGoodsType() <= 6) {
            TaskMessageHelper.sendSellEquipTaskMessage(playerId);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("copper", copper);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] openSTBack(final PlayerDto playerDto) {
        final List<StoreHouseSell> list = this.storeHouseSellDao.getByPlayerId(playerDto.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("goods");
        int copper = 100;
        for (int i = 0; i < list.size(); ++i) {
            final StoreHouseSell sh = list.get(i);
            copper = 100;
            long limitTime = System.currentTimeMillis() - sh.getSellTime().getTime();
            if (limitTime > 259200000L && sh.getType() != 2) {
                if (limitTime > 1296000000L) {
                    this.storeHouseSellDao.deleteById(sh.getVId());
                }
            }
            else {
                doc.startObject();
                doc.createElement("vId", sh.getVId());
                doc.createElement("itemId", sh.getItemId());
                doc.createElement("kind", sh.getType());
                if (sh.getType() == 1) {
                    final Equip equip = (Equip)this.equipCache.get((Object)sh.getItemId());
                    doc.createElement("itemName", equip.getName());
                    doc.createElement("pic", equip.getPic());
                    doc.createElement("lv", sh.getLv());
                    doc.createElement("intro", equip.getIntro());
                    doc.createElement("quality", equip.getQuality());
                    doc.createElement("suitName", this.equipSuitCache.getSuitName(equip.getId()));
                    doc.createElement("type", equip.getType());
                    if (equip.getType() == 6 || equip.getType() == 5) {
                        doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
                    }
                    else {
                        doc.createElement("attribute", sh.getAttribute());
                    }
                    EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip);
                    EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, sh.getSpecialSkillId(), sh.getRefreshAttribute());
                    copper = equip.getCopperSold();
                    if (sh.getGemId() > 0) {
                        final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)sh.getGemId());
                        doc.createElement("gemName", gem.getName());
                        doc.createElement("gemPic", gem.getPic());
                        doc.createElement("gemLv", gem.getGemLv());
                        doc.createElement("att", gem.getAtt());
                        doc.createElement("def", gem.getDef());
                        doc.createElement("blood", gem.getBlood() / 3);
                    }
                }
                else if (sh.getType() == 2) {
                    final ArmsGem gem2 = (ArmsGem)this.armsGemCache.get((Object)sh.getItemId());
                    doc.createElement("itemName", gem2.getName());
                    doc.createElement("pic", gem2.getPic());
                    doc.createElement("gemLv", gem2.getGemLv());
                    doc.createElement("att", gem2.getAtt());
                    doc.createElement("def", gem2.getDef());
                    doc.createElement("blood", gem2.getBlood() / 3);
                    doc.createElement("num", sh.getNum());
                }
                else if (sh.getType() == 3) {
                    final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
                    doc.createElement("itemName", generalTreasure.getName());
                    doc.createElement("pic", generalTreasure.getPic());
                    doc.createElement("intro", generalTreasure.getIntro());
                    doc.createElement("quality", generalTreasure.getQuality());
                    final String[] strs = sh.getAttribute().split(",");
                    doc.createElement("type", generalTreasure.getType());
                    doc.createElement("att1", strs[0]);
                    copper = generalTreasure.getCopperPrice();
                    if (strs.length >= 2) {
                        doc.createElement("att2", strs[1]);
                    }
                    doc.createElement("minLv", generalTreasure.getMinGeneralLevel());
                }
                else if (sh.getType() == 4 || sh.getType() == 5 || sh.getType() == 6 || sh.getType() == 7 || sh.getType() == 8 || sh.getType() == 9 || sh.getType() == 12 || sh.getType() == 13 || sh.getType() == 17 || sh.getType() == 15 || sh.getType() == 16 || sh.getType() == 18 || sh.getType() == 19 || sh.getType() == 20 || sh.getType() == 21 || sh.getType() == 22) {
                    final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                    doc.createElement("itemName", items.getName());
                    doc.createElement("pic", items.getPic());
                    doc.createElement("intro", items.getIntro());
                    if (sh.getType() == 17) {
                        final String itemStr = items.getEffect();
                        this.appendResourceTokenEffectInfo(doc, itemStr);
                    }
                    else {
                        doc.createElement("effectType", sh.getGoodsType());
                        doc.createElement("effectNum", sh.getAttribute());
                    }
                    doc.createElement("num", sh.getNum());
                    doc.createElement("quality", sh.getQuality());
                    copper = items.getCopper();
                }
                else if (sh.getType() == 11) {
                    final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                    final Items changeIteams = (Items)this.itemsCache.get((Object)items.getChangeItemId());
                    doc.createElement("itemName", items.getName());
                    doc.createElement("pic", items.getPic());
                    doc.createElement("intro", items.getIntro());
                    doc.createElement("effectType", sh.getGoodsType());
                    doc.createElement("effectNum", sh.getAttribute());
                    doc.createElement("num", sh.getNum());
                    doc.createElement("quality", sh.getQuality());
                    copper = items.getCopper();
                    doc.createElement("changeNum", changeIteams.getName());
                    doc.createElement("changeName", changeIteams.getChangeNum());
                }
                else if (sh.getType() == 10) {
                    final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                    final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(sh.getItemId());
                    if (items == null || equipCoordinates == null) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                    }
                    doc.createElement("itemName", equipCoordinates.getName());
                    doc.createElement("pic", equipCoordinates.getPic());
                    doc.createElement("num", sh.getNum());
                    doc.createElement("quality", sh.getQuality());
                }
                else if (sh.getType() == 14) {
                    final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
                    final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(sh.getItemId());
                    if (items == null || equipProset == null) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                    }
                    doc.createElement("itemName", equipProset.getName());
                    doc.createElement("pic", equipProset.getPic());
                    doc.createElement("num", sh.getNum());
                    doc.createElement("quality", sh.getQuality());
                }
                doc.createElement("copper", copper);
                limitTime = 259200000L - limitTime;
                if (sh.getType() == 2) {
                    limitTime = 0L;
                }
                doc.createElement("limitTime", (limitTime <= 0L) ? 0L : limitTime);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] buyBackGoods(final PlayerDto playerDto, final int vId) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final StoreHouseSell shs = this.storeHouseSellDao.read(vId);
        if (shs == null || shs.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int type = shs.getType();
        String reason = "\u56de\u8d2d\u88c5\u5907\u82b1\u8d39\u94f6\u5e01";
        final int usedSize = this.storeHouseDao.getCountByPlayerId(playerId);
        final int maxSize = this.playerAttributeDao.read(playerId).getMaxStoreNum();
        int copper = 100;
        boolean isCreate = true;
        List<StoreHouse> shList = null;
        StoreHouse storeHouse = null;
        if (type == 2 || type == 4 || type == 5 || type == 6 || type == 11 || type == 16 || type == 17 || type == 20 || type == 21 || type == 22) {
            shList = this.storeHouseDao.getByItemId(playerId, shs.getItemId(), shs.getType());
            if (shList != null && shList.size() > 0) {
                storeHouse = shList.get(0);
                isCreate = false;
            }
            else if (usedSize >= maxSize && type != 2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
            }
            if (type == 2) {
                reason = "\u56de\u8d2d\u5b9d\u77f3\u82b1\u8d39\u94f6\u5e01";
            }
            else {
                reason = "\u56de\u8d2d\u6536\u96c6\u7269\u54c1\u83b7\u5f97\u94f6\u5e01";
            }
        }
        else if (usedSize >= maxSize) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
        }
        if (type == 4) {
            try {
                shList = this.storeHouseDao.getByItemId(playerId, shs.getItemId(), 4);
                storeHouse = ((shList == null || shList.size() <= 0) ? null : shList.get(0));
            }
            catch (MyBatisSystemException e) {
                final List<StoreHouse> expList = this.storeHouseDao.getByType(playerId, 4);
                int addNum = 0;
                for (final StoreHouse sHouse : expList) {
                    if (sHouse.getItemId() == shs.getItemId() && addNum == 0) {
                        storeHouse = sHouse;
                    }
                    else {
                        addNum += sHouse.getNum();
                        this.storeHouseDao.deleteById(sHouse.getVId());
                    }
                }
                if (storeHouse != null && addNum != 0) {
                    this.storeHouseDao.addNum(storeHouse.getVId(), addNum);
                }
            }
            if (storeHouse != null) {
                isCreate = false;
            }
        }
        if (type == 3) {
            reason = "\u56de\u8d2d\u5fa1\u5b9d\u82b1\u8d39\u94f6\u5e01";
            final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)shs.getItemId());
            if (generalTreasure != null) {
                copper = generalTreasure.getCopperPrice();
            }
        }
        if (type == 5 || type == 4 || type == 6 || type == 9 || type == 8 || type == 11 || type == 12 || type == 13 || type == 17 || type == 15 || type == 16 || type == 18 || type == 19 || type == 20 || type == 21 || type == 22) {
            final Items items = (Items)this.itemsCache.get((Object)shs.getItemId());
            copper = items.getCopper();
        }
        copper *= shs.getNum();
        if (1 == type) {
            final Equip equip = (Equip)this.equipCache.get((Object)shs.getItemId());
            if (equip != null) {
                copper = equip.getCopperSold();
            }
        }
        if (!this.playerResourceDao.consumeCopper(playerDto.playerId, copper, reason)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
        }
        if (isCreate) {
            storeHouse = new StoreHouse();
            storeHouse.setAttribute(shs.getAttribute());
            storeHouse.setGemId(shs.getGemId());
            storeHouse.setGoodsType(shs.getGoodsType());
            storeHouse.setItemId(shs.getItemId());
            storeHouse.setLv(shs.getLv());
            storeHouse.setNum(shs.getNum());
            storeHouse.setOwner(0);
            storeHouse.setPlayerId(playerId);
            storeHouse.setQuality(shs.getQuality());
            storeHouse.setType(shs.getType());
            storeHouse.setState(0);
            String rString = shs.getRefreshAttribute();
            rString = ((rString == null) ? "" : rString);
            storeHouse.setRefreshAttribute(rString);
            final int q = (shs.getQuenchingTimes() == null) ? 0 : shs.getQuenchingTimes();
            storeHouse.setQuenchingTimes(q);
            final int qf = (shs.getQuenchingTimesFree() == null) ? 0 : shs.getQuenchingTimesFree();
            storeHouse.setQuenchingTimesFree(qf);
            storeHouse.setSpecialSkillId(shs.getSpecialSkillId());
            storeHouse.setBindExpireTime(0L);
            storeHouse.setMarkId(0);
            this.storeHouseDao.create(storeHouse);
        }
        else {
            this.storeHouseDao.addNum(storeHouse.getVId(), shs.getNum());
        }
        this.storeHouseSellDao.deleteById(vId);
        if (1 == type) {
            final Equip equip = (Equip)this.equipCache.get((Object)shs.getItemId());
            if (equip != null) {
                copper = equip.getCopperSold();
            }
            EquipService.logger.info(LogUtil.formatEquipLog(player, "+", "\u4e70\u56de", true, (Equip)this.equipCache.get((Object)shs.getItemId()), storeHouse, LocalMessages.T_LOG_EQUIP_5));
        }
        else if (2 == type) {
            EquipService.logger.info(LogUtil.formatGemLog(player, "+", "\u4e70\u56de", true, (ArmsGem)this.armsGemCache.get((Object)shs.getItemId()), shs.getNum(), LocalMessages.T_LOG_GEM_2));
        }
        else if (3 == type) {
            EquipService.logger.info(LogUtil.formatGeneralTreasureLog(player, "+", "\u4e70\u56de", (GeneralTreasure)this.generalTreasureCache.get((Object)shs.getItemId()), LocalMessages.T_LOG_GENERAL_TREASURE_2));
        }
        else {
            EquipService.logger.info(LogUtil.formatItemsLog(player, "+", "\u4e70\u56de", true, (Items)this.itemsCache.get((Object)shs.getItemId()), storeHouse, shs.getNum(), LocalMessages.T_LOG_ITEM_SJWP_2));
        }
        TaskMessageHelper.sendEquipTaskMessage(playerDto.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("copper", copper);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getWearEquip(final PlayerDto playerDto, final int goodsType, final int gId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<StoreHouse> exceptOtherWear = new ArrayList<StoreHouse>();
        final List<StoreHouse> fromBase = this.storeHouseDao.getWearableEquip(playerDto.playerId, goodsType, 1);
        final List<StoreHouse> shList = new ArrayList<StoreHouse>();
        StoreHouse firstWear = null;
        if (fromBase != null) {
            for (final StoreHouse sHouse : fromBase) {
                if (sHouse.getOwner() <= 0) {
                    exceptOtherWear.add(sHouse);
                }
                else {
                    if (gId != sHouse.getOwner()) {
                        continue;
                    }
                    if (firstWear == null) {
                        firstWear = sHouse;
                    }
                    else {
                        exceptOtherWear.add(sHouse);
                    }
                }
            }
        }
        final List<StoreHouse> suits = this.storeHouseDao.getWearableEquip(playerDto.playerId, 10, 10);
        if (firstWear != null) {
            shList.add(firstWear);
        }
        final List<StoreHouse> proset = this.storeHouseDao.getWearableEquip(playerDto.playerId, 14, 14);
        shList.addAll(proset);
        shList.addAll(suits);
        shList.addAll(exceptOtherWear);
        doc.startArray("equips");
        for (int i = 0; i < shList.size(); ++i) {
            final StoreHouse sh = shList.get(i);
            if (sh.getOwner() > 0) {
                if (sh.getOwner() != gId) {
                    continue;
                }
                doc.startObject();
                doc.createElement("generalId", sh.getOwner());
                final General general = (General)this.generalCache.get((Object)sh.getOwner());
                doc.createElement("generalName", general.getName());
                doc.createElement("gQuality", general.getQuality());
            }
            else {
                doc.startObject();
            }
            final int type = sh.getGoodsType();
            doc.createElement("type", type);
            doc.createElement("vId", sh.getVId());
            doc.createElement("lv", sh.getLv());
            if (type == 10) {
                final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(sh.getItemId());
                if (equipCoordinates == null) {
                    EquipService.log.error("getWearEquip error....suit itemId wrong...itemId:" + sh.getItemId());
                }
                else {
                    final Integer[] singleType = this.equipCache.getSkillArray(equipCoordinates.getId());
                    doc.startArray("subEquips");
                    for (int j = 1; j <= singleType.length; ++j) {
                        final Equip equip = this.equipCache.getSuitSingleEquipByType(j);
                        if (equip == null) {
                            EquipService.log.error("can't find purple equip...type:" + j);
                        }
                        else {
                            doc.startObject();
                            doc.createElement("equipId", equip.getId());
                            doc.createElement("type", j);
                            doc.createElement("equipName", equip.getName());
                            doc.createElement("equipPic", equip.getPic());
                            doc.createElement("skillNum", 4);
                            final int skillType = singleType[j - 1];
                            final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)skillType);
                            doc.createElement("skillType", skillType);
                            doc.createElement("quality", equip.getQuality());
                            doc.createElement("skillName", equipSkill.getName());
                            doc.createElement("skillLv", equip.getSkillLvMax());
                            doc.endObject();
                        }
                    }
                    doc.endArray();
                    int att = 0;
                    int def = 0;
                    int blood = 0;
                    att = equipCoordinates.getAtt();
                    def = equipCoordinates.getDef();
                    blood = equipCoordinates.getBlood();
                    if (att > 0) {
                        doc.createElement("att", att);
                    }
                    if (def > 0) {
                        doc.createElement("def", def);
                    }
                    if (blood > 0) {
                        doc.createElement("blood", blood);
                    }
                    doc.createElement("itemName", equipCoordinates.getName());
                    doc.createElement("pic", equipCoordinates.getPic());
                    doc.createElement("suitIntro", equipCoordinates.getIntro());
                    doc.endObject();
                }
            }
            else if (type == 14) {
                final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(sh.getItemId());
                if (equipProset == null) {
                    EquipService.log.error("getWearEquip error....proset itemId wrong...itemId:" + sh.getItemId());
                }
                else {
                    final EquipCoordinates equipCoordinates2 = this.equipCache.getMainSuit(equipProset.getId());
                    if (equipCoordinates2 == null) {
                        EquipService.log.error("getWearEquip error....suit itemId wrong...itemId:" + sh.getItemId());
                    }
                    else {
                        final Integer[] singleType2 = this.equipCache.getSkillArray(equipCoordinates2.getId());
                        doc.startArray("subEquips");
                        for (int k = 1; k <= singleType2.length; ++k) {
                            final Equip equip2 = this.equipCache.getSuitSingleEquipByType(k);
                            if (equip2 == null) {
                                EquipService.log.error("can't find purple equip...type:" + k);
                            }
                            else {
                                doc.startObject();
                                doc.createElement("equipId", equip2.getId());
                                doc.createElement("type", k);
                                doc.createElement("equipName", equip2.getName());
                                doc.createElement("equipPic", equip2.getPic());
                                doc.createElement("skillNum", 4);
                                final int skillType2 = singleType2[k - 1];
                                final EquipSkill equipSkill2 = (EquipSkill)this.equipSkillCache.get((Object)skillType2);
                                doc.createElement("skillType", skillType2);
                                doc.createElement("quality", equip2.getQuality());
                                doc.createElement("skillName", equipSkill2.getName());
                                doc.createElement("skillLv", equip2.getSkillLvMax());
                                doc.endObject();
                            }
                        }
                        doc.endArray();
                        int att2 = 0;
                        int def2 = 0;
                        int blood2 = 0;
                        att2 = equipProset.getAtt();
                        def2 = equipProset.getDef();
                        blood2 = equipProset.getBlood();
                        if (att2 > 0) {
                            doc.createElement("att", att2);
                        }
                        if (def2 > 0) {
                            doc.createElement("def", def2);
                        }
                        if (blood2 > 0) {
                            doc.createElement("blood", blood2);
                        }
                        doc.createElement("itemName", equipProset.getName());
                        doc.createElement("pic", equipProset.getPic());
                        doc.createElement("suitIntro", equipProset.getIntro());
                        doc.endObject();
                    }
                }
            }
            else {
                final Equip equip3 = (Equip)this.equipCache.get((Object)sh.getItemId());
                if (equip3.getType() == 5 || equip3.getType() == 6) {
                    doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
                }
                else {
                    doc.createElement("attribute", sh.getAttribute());
                }
                doc.createElement("quality", equip3.getQuality());
                doc.createElement("itemName", equip3.getName());
                EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip3);
                EquipCommon.getMaxSkillAndLv(doc, equip3, this.equipCache, sh.getSpecialSkillId(), sh.getRefreshAttribute());
                doc.createElement("pic", equip3.getPic());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] changeEquip(final PlayerDto playerDto, final int vId, final int generalId, final int goodsType, final boolean change) {
        final int playerId = playerDto.playerId;
        if (generalId <= 0 || goodsType < 1 || (goodsType > 12 && goodsType != 14)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null || sh.getPlayerId() != playerId || sh.getGoodsType() != goodsType) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getOwner() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_EQUIP_HAD_WAER);
        }
        final General general = (General)this.generalCache.get((Object)generalId);
        if (goodsType > 6 && goodsType != 10 && goodsType != 14 && general.getType() == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (goodsType <= 6 && general.getType() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        PlayerGeneralMilitary pgm = null;
        if (goodsType <= 6 || goodsType == 10 || goodsType == 14) {
            pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
        }
        else {
            final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
            if (cs[49] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, pgc.getOwner());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        boolean reload = false;
        long newHp = (pgm == null) ? 0 : pgm.getForces();
        final int type = (goodsType == 10 || goodsType == 14) ? goodsType : 1;
        if (type == 10 || type == 14) {
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            final int maxNum = (pa == null) ? 0 : pa.getMaxStoreNum();
            final int curNum = this.storeHouseDao.getCountByPlayerId(playerId);
            final int equipNum = this.storeHouseDao.getGeneralEquipCount(playerId, generalId);
            final int treasureNum = this.storeHouseDao.getGeneralTreasureNum(playerId, generalId);
            if (curNum + equipNum - treasureNum - 1 > maxNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
            }
        }
        if (goodsType <= 6 || goodsType == 10 || goodsType == 14) {
            final StoreHouse inSh = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, goodsType, type);
            newHp = this.battleDataCache.changGEquipNewMaxHp(pgm, (inSh == null) ? 0 : this.battleDataCache.getEquipMax(inSh).hp, this.battleDataCache.getEquipMax(sh).hp);
            reload = true;
        }
        else if ((goodsType == 7 || goodsType == 8) && pgm != null) {
            final StoreHouse inSh = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, goodsType, 1);
            newHp = this.battleDataCache.changCEquipNewMaxHp(pgm, (inSh == null) ? 0 : ((int)Integer.valueOf(inSh.getAttribute())), Integer.valueOf(sh.getAttribute()));
        }
        if (pgm != null && pgm.getForces() > newHp) {
            if (!change) {
                doc.createElement("reload", false);
                doc.createElement("toForce", false);
                doc.createElement("info", (Object)LocalMessages.BATTLE_CHANGE_EQUIP_LOST_FORCES);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            this.playerGeneralMilitaryDao.resetForces(playerId, pgm.getGeneralId(), new Date(), newHp);
        }
        if ((goodsType <= 6 || goodsType == 10 || goodsType == 14) && pgm.getForces() < newHp) {
            this.playerGeneralMilitaryDao.updateAutoRecruit(pgm.getPlayerId(), pgm.getGeneralId());
        }
        doc.createElement("reload", reload);
        doc.createElement("toForce", true);
        final StoreHouse inSh = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, goodsType, type);
        final StoreHouse equipedSuit = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, 10, 10);
        final StoreHouse prosetStoreHouse = this.storeHouseDao.getByGeneralIdAndType(playerId, generalId, 14, 14);
        if (inSh != null) {
            this.storeHouseDao.resetOwnerByGeneralIdAndType(playerId, generalId, goodsType, type);
            if (inSh.getQuality() == 1) {
                EventListener.fireEvent(new CommonEvent(31, playerId));
            }
        }
        if (equipedSuit != null) {
            this.storeHouseDao.resetOwnerByGeneralIdAndType(playerId, generalId, 10, 10);
        }
        if (prosetStoreHouse != null) {
            this.storeHouseDao.resetOwnerByGeneralIdAndType(playerId, generalId, 14, 14);
        }
        if (type == 10 || type == 14) {
            this.storeHouseDao.resetOwnerByGeneralIdExceptTreasure(playerId, generalId);
        }
        this.storeHouseDao.resetOwnerByVId(vId, generalId);
        final Map<Integer, Integer> adhOldMap = this.battleDataCache.getAttDefHp(pgm);
        this.battleDataCache.removeEquipEffect(playerId, generalId);
        final Map<Integer, Integer> adhnewMap = this.battleDataCache.getAttDefHp(pgm);
        if (goodsType <= 6) {
            final int forcesMax = adhnewMap.get(3);
            doc.createElement("forcesMax", forcesMax);
            doc.createElement("att", adhnewMap.get(1));
            doc.createElement("def", adhnewMap.get(2));
        }
        else if (goodsType == 10) {
            final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(sh.getItemId());
            int att = 0;
            int def = 0;
            int blood = 0;
            att = equipCoordinates.getAtt();
            def = equipCoordinates.getDef();
            blood = equipCoordinates.getBlood();
            if (att > 0) {
                doc.createElement("att", att);
            }
            if (def > 0) {
                doc.createElement("def", def);
            }
            if (blood > 0) {
                doc.createElement("blood", blood);
            }
            doc.createElement("itemName", equipCoordinates.getName());
            doc.createElement("pic", equipCoordinates.getPic());
            doc.createElement("attReduce", adhnewMap.get(1) - adhOldMap.get(1));
            doc.createElement("defReduce", adhnewMap.get(2) - adhOldMap.get(2));
            doc.createElement("bloodReduce", adhnewMap.get(3) - adhOldMap.get(3));
        }
        else if (goodsType == 14) {
            final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(sh.getItemId());
            int att = 0;
            int def = 0;
            int blood = 0;
            att = equipProset.getAtt();
            def = equipProset.getDef();
            blood = equipProset.getBlood();
            if (att > 0) {
                doc.createElement("att", att);
            }
            if (def > 0) {
                doc.createElement("def", def);
            }
            if (blood > 0) {
                doc.createElement("blood", blood);
            }
            doc.createElement("itemName", equipProset.getName());
            doc.createElement("pic", equipProset.getPic());
            doc.createElement("attReduce", adhnewMap.get(1) - adhOldMap.get(1));
            doc.createElement("defReduce", adhnewMap.get(2) - adhOldMap.get(2));
            doc.createElement("bloodReduce", adhnewMap.get(3) - adhOldMap.get(3));
        }
        doc.createElement("type", sh.getGoodsType());
        if (goodsType == 5 || goodsType == 6) {
            doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
        }
        else {
            doc.createElement("attribute", sh.getAttribute());
        }
        doc.createElement("vId", sh.getVId());
        doc.createElement("lv", sh.getLv());
        if (goodsType != 10 && goodsType != 14) {
            final Equip equip = (Equip)this.equipCache.get((Object)sh.getItemId());
            EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip);
            EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, sh.getSpecialSkillId(), sh.getRefreshAttribute());
            doc.createElement("quality", equip.getQuality());
            doc.createElement("suitName", this.equipSuitCache.getSuitName(equip.getId()));
            doc.createElement("itemName", equip.getName());
            doc.createElement("pic", equip.getPic());
        }
        doc.endObject();
        TaskMessageHelper.sendWearEquipTaskMessage(playerId);
        TaskMessageHelper.sendEquipOnTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] unloadEquip(final PlayerDto playerDto, final int vId, final int generalId, final int goodsType, final boolean change) {
        final int playerId = playerDto.playerId;
        if (generalId <= 0 || goodsType < 1 || (goodsType > 12 && goodsType != 14)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null || sh.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        PlayerGeneralMilitary pgm = null;
        if (goodsType <= 6 || goodsType == 10 || goodsType == 14) {
            pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
        }
        else {
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, pgc.getOwner());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        boolean reload = false;
        long newHp = (pgm == null) ? 0 : pgm.getForces();
        if (goodsType <= 6 || goodsType == 10 || goodsType == 14) {
            newHp = this.battleDataCache.changGEquipNewMaxHp(pgm, this.battleDataCache.getEquipMax(sh).hp, 0);
            reload = true;
        }
        else if ((goodsType == 7 || goodsType == 8) && pgm != null) {
            newHp = this.battleDataCache.changCEquipNewMaxHp(pgm, Integer.valueOf(sh.getAttribute()), 0);
        }
        if (pgm != null && pgm.getForces() > newHp) {
            if (!change) {
                doc.createElement("reload", false);
                doc.createElement("toForce", false);
                doc.createElement("info", (Object)LocalMessages.BATTLE_CHANGE_EQUIP_LOST_FORCES);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            this.playerGeneralMilitaryDao.resetForces(playerId, pgm.getGeneralId(), new Date(), newHp);
        }
        doc.createElement("reload", reload);
        doc.createElement("toForce", true);
        final int usedSize = this.storeHouseDao.getCountByPlayerId(playerId);
        final int maxSize = this.playerAttributeDao.read(playerId).getMaxStoreNum();
        if (usedSize + 1 > maxSize) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_TOP_UNLOAD);
        }
        final int type = (sh.getType() == 10 || sh.getType() == 14) ? sh.getType() : 1;
        this.storeHouseDao.resetOwnerByGeneralIdAndType(playerDto.playerId, generalId, goodsType, type);
        final Map<Integer, Integer> adholdMap = this.battleDataCache.getAttDefHp(pgm);
        this.battleDataCache.removeEquipEffect(playerId, generalId);
        final Map<Integer, Integer> adhnewMap = this.battleDataCache.getAttDefHp(pgm);
        if (goodsType <= 6) {
            final int forcesMax = adhnewMap.get(3);
            doc.createElement("forcesMax", forcesMax);
            doc.createElement("att", adhnewMap.get(1));
            doc.createElement("def", adhnewMap.get(2));
            if (sh.getQuality() == 1) {
                EventListener.fireEvent(new CommonEvent(31, playerId));
            }
        }
        else if (goodsType == 10 || goodsType == 14) {
            doc.createElement("attReduce", adhnewMap.get(1) - adholdMap.get(1));
            doc.createElement("defReduce", adhnewMap.get(2) - adholdMap.get(2));
            doc.createElement("bloodReduce", adhnewMap.get(3) - adholdMap.get(3));
        }
        else {
            doc.createElement("type", sh.getGoodsType());
            doc.createElement("attribute", sh.getAttribute());
        }
        doc.createElement("vId", sh.getVId());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] preMakeGem(final PlayerDto playerDto, final int gemId) {
        final int playerId = playerDto.playerId;
        boolean functionOpen = false;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[28] == '1') {
            functionOpen = true;
        }
        if (!functionOpen) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, gemId, 2);
        if (shList == null || shList.size() <= 0) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("showIncense", true);
            doc.createElement("resourceType", 5);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (gemId >= 10) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_LV_TOP);
        }
        final ArmsGem oldGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        final ArmsGem newGem = (ArmsGem)this.armsGemCache.get((Object)(gemId + 1));
        if (oldGem == null || newGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (gemId + 1 > this.techEffectCache.getTechEffect(playerId, 46)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_LV_NOT_REACH);
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("gemId", gemId + 1);
        doc2.createElement("num", shList.get(0).getNum() / 2);
        doc2.createElement("gemPic", newGem.getPic());
        doc2.createElement("gemLv", newGem.getGemLv());
        doc2.createElement("att", newGem.getAtt());
        doc2.createElement("def", newGem.getDef());
        doc2.createElement("blood", newGem.getBlood());
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Transactional
    @Override
    public byte[] makeGem(final PlayerDto playerDto, final int gemId, final int type) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        boolean functionOpen = false;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[28] == '1') {
            functionOpen = true;
        }
        if (!functionOpen) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (type != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, gemId, 2);
        if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 2) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("showIncense", true);
            doc.createElement("resourceType", 5);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_COUNT_NOT_ENOUGH);
        }
        final StoreHouse storeHouse = shList.get(0);
        final ArmsGem oldGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        final ArmsGem newGem = (ArmsGem)this.armsGemCache.get((Object)(gemId + 1));
        if (oldGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (oldGem.getId() >= ArmsGemCache.maxId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_LV_TOP);
        }
        if (newGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (gemId + 1 > this.techEffectCache.getTechEffect(playerId, 46)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_LV_NOT_REACH);
        }
        final int nowSize = storeHouse.getNum();
        int needCostNum = 0;
        if (type == 0) {
            needCostNum = 2;
        }
        else {
            needCostNum = nowSize - nowSize % 2;
        }
        final List<StoreHouse> newStoreHouseList = this.storeHouseDao.getByItemId(playerId, newGem.getId(), 2);
        final int newMakeNum = needCostNum / 2;
        if (needCostNum >= storeHouse.getNum()) {
            this.storeHouseDao.deleteById(storeHouse.getVId());
        }
        else {
            this.storeHouseDao.reduceNum(storeHouse.getVId(), needCostNum);
        }
        EquipService.logger.info(LogUtil.formatGemLog(player, "-", "\u5347\u7ea7", true, oldGem, needCostNum, LocalMessages.T_LOG_GEM_3));
        if (newStoreHouseList != null && newStoreHouseList.size() > 0) {
            this.storeHouseDao.addNum(newStoreHouseList.get(0).getVId(), newMakeNum);
        }
        else {
            final StoreHouse sh = new StoreHouse();
            sh.setType(2);
            sh.setGoodsType(2);
            sh.setItemId(newGem.getId());
            sh.setLv(newGem.getGemLv());
            sh.setPlayerId(playerDto.playerId);
            sh.setOwner(0);
            sh.setGemId(0);
            sh.setAttribute("0");
            sh.setNum(newMakeNum);
            sh.setState(0);
            sh.setQuenchingTimes(0);
            sh.setRefreshAttribute("");
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
        }
        EquipService.logger.info(LogUtil.formatGemLog(player, "+", "\u5347\u7ea7", true, newGem, newMakeNum, LocalMessages.T_LOG_GEM_4));
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("num", newMakeNum);
        doc2.createElement("gemLv", newGem.getGemLv());
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Transactional
    @Override
    public byte[] unMakeGem(final PlayerDto playerDto, final int gemId) {
        final int playerId = playerDto.playerId;
        if (gemId <= 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_UNMAKE_LOW_GEM_ID);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[28] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, gemId, 2);
        if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("showIncense", true);
            doc.createElement("resourceType", 5);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_COUNT_NOT_ENOUGH);
        }
        final StoreHouse storeHouse = shList.get(0);
        final ArmsGem oldGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        final ArmsGem newGem = (ArmsGem)this.armsGemCache.get((Object)(gemId - 1));
        if (newGem == null || oldGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (storeHouse.getGoodsType() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_JS_CAN_NOT_UN_MAKE);
        }
        final int nowSize = storeHouse.getNum();
        final List<StoreHouse> newStoreHouseList = this.storeHouseDao.getByItemId(playerId, newGem.getId(), 2);
        if (1 == nowSize) {
            this.storeHouseDao.deleteById(storeHouse.getVId());
        }
        else {
            this.storeHouseDao.reduceNum(storeHouse.getVId(), 1);
        }
        final Player player = this.playerDao.read(playerId);
        EquipService.logger.info(LogUtil.formatGemLog(player, "-", "\u5347\u7ea7", true, oldGem, 1, LocalMessages.T_LOG_GEM_18));
        if (newStoreHouseList != null && newStoreHouseList.size() > 0) {
            this.storeHouseDao.addNum(newStoreHouseList.get(0).getVId(), 2);
        }
        else {
            final StoreHouse sh = new StoreHouse();
            sh.setType(2);
            sh.setGoodsType(2);
            sh.setItemId(newGem.getId());
            sh.setLv(newGem.getGemLv());
            sh.setPlayerId(playerDto.playerId);
            sh.setOwner(0);
            sh.setGemId(0);
            sh.setAttribute("0");
            sh.setNum(2);
            sh.setState(0);
            sh.setQuenchingTimes(0);
            sh.setRefreshAttribute("");
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
        }
        EquipService.logger.info(LogUtil.formatGemLog(player, "+", "\u5347\u7ea7", true, newGem, 2, LocalMessages.T_LOG_GEM_19));
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("gemLv", newGem.getGemLv()));
    }
    
    @Override
    public byte[] getCanUseGeneral(final PlayerDto playerDto, final int vId) {
        final int playerId = playerDto.playerId;
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_EQUIP);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getType() != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_TYPE_WRONG);
        }
        final int goodsType = sh.getGoodsType();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("generals");
        if (goodsType == 4 || goodsType != 5) {}
        if (goodsType == 2 || goodsType == 3 || goodsType == 1) {
            final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
                if (general != null) {
                    doc.startObject();
                    doc.createElement("type", general.getType());
                    doc.createElement("generalId", general.getId());
                    doc.createElement("name", general.getName());
                    doc.createElement("pic", general.getPic());
                    doc.createElement("quality", general.getQuality());
                    doc.createElement("lv", pgm.getLv());
                    doc.createElement("lea", pgm.getLeader(general.getLeader()));
                    doc.createElement("str", pgm.getStrength(general.getStrength()));
                    doc.createElement("exp", pgm.getExp());
                    doc.createElement("goodsType", goodsType);
                    final Integer generalExpMax = this.serialCache.get(general.getUpExpS(), pgm.getLv());
                    doc.createElement("expMax", generalExpMax);
                    doc.endObject();
                }
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] useCreateBuilding(final PlayerDto playerDto, final int vId) {
        final int playerId = playerDto.playerId;
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_EQUIP);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (sh.getType() == 7) {
            final Items items = (Items)this.itemsCache.get((Object)sh.getItemId());
            if (!items.getEffect().isEmpty()) {
                final String[] strs = items.getEffect().split(";");
                int buildingId = 1;
                doc.startArray("building");
                String[] array;
                for (int length = (array = strs).length, i = 0; i < length; ++i) {
                    final String str = array[i];
                    try {
                        doc.startObject();
                        buildingId = Integer.valueOf(str);
                        this.buildingService.createBuilding(playerId, buildingId, 0);
                        final Building building = (Building)this.buildingCache.get((Object)buildingId);
                        doc.createElement("buildingName", building.getName());
                        doc.endObject();
                    }
                    catch (NumberFormatException nfe) {
                        EquipService.log.error("itemsCache effect NumberFormatException id:" + items.getId());
                    }
                }
                doc.endArray();
            }
            this.storeHouseDao.deleteById(vId);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] useOnGeneral(final PlayerDto playerDto, final int vId, final int generalId, int time) {
        final int playerId = playerDto.playerId;
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_EQUIP);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getType() != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_TYPE_WRONG);
        }
        time = ((time <= 0) ? 1 : time);
        if (sh.getNum() < time) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int goodsType = sh.getGoodsType();
        final General general = (General)this.generalCache.get((Object)generalId);
        if (general == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_GENERAL);
        }
        int realBeyondValue = 0;
        int addValue = 0;
        boolean upLv = false;
        UpdateExp exp = null;
        long totalExp = 0L;
        int useTime = time;
        if (general.getType() == 1) {
            if (goodsType == 2 || goodsType == 3) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_ITEMS_WRONG);
            }
            final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
            if (pgc == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_GENERAL);
            }
            final int maxAttribute = pgc.getLv() + 20;
            if (goodsType == 4) {
                realBeyondValue = pgc.getIntel(general.getIntel()) - (int)this.getGeneralTreasureEffect(pgc).left;
                if (realBeyondValue >= maxAttribute) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_ATTRIBUTE_LIMIT);
                }
                if (realBeyondValue + Integer.valueOf(sh.getAttribute()) > maxAttribute) {
                    addValue = maxAttribute - realBeyondValue;
                }
                else {
                    addValue = Integer.valueOf(sh.getAttribute());
                }
                this.playerGeneralCivilDao.addIntel(playerId, generalId, addValue);
            }
            else if (goodsType == 5) {
                realBeyondValue = pgc.getPolitics(general.getPolitics()) - (int)this.getGeneralTreasureEffect(pgc).right;
                if (realBeyondValue >= maxAttribute) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_ATTRIBUTE_LIMIT);
                }
                if (realBeyondValue + Integer.valueOf(sh.getAttribute()) > maxAttribute) {
                    addValue = maxAttribute - realBeyondValue;
                }
                else {
                    addValue = Integer.valueOf(sh.getAttribute());
                }
                this.playerGeneralCivilDao.addPolitics(playerId, generalId, addValue);
            }
            else if (goodsType == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_EXP_FULL);
            }
        }
        else {
            if (goodsType == 4 || goodsType == 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_ITEMS_WRONG);
            }
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
            if (pgm == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_GENERAL);
            }
            final int maxAttribute = pgm.getLv() + 20;
            if (goodsType == 2) {
                realBeyondValue = pgm.getLeader(general.getLeader()) - (int)this.getGeneralTreasureEffect(pgm).left;
                if (realBeyondValue >= maxAttribute) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_ATTRIBUTE_LIMIT);
                }
                if (realBeyondValue + Integer.valueOf(sh.getAttribute()) > maxAttribute) {
                    addValue = maxAttribute - realBeyondValue;
                }
                else {
                    addValue = Integer.valueOf(sh.getAttribute());
                }
                this.playerGeneralMilitaryDao.addLeader(playerId, generalId, addValue);
            }
            else if (goodsType == 3) {
                realBeyondValue = pgm.getStrength(general.getStrength()) - (int)this.getGeneralTreasureEffect(pgm).right;
                if (realBeyondValue >= maxAttribute) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_ATTRIBUTE_LIMIT);
                }
                if (realBeyondValue + Integer.valueOf(sh.getAttribute()) > maxAttribute) {
                    addValue = maxAttribute - realBeyondValue;
                }
                else {
                    addValue = Integer.valueOf(sh.getAttribute());
                }
                this.playerGeneralMilitaryDao.addStrength(playerId, generalId, addValue);
            }
            else if (goodsType == 1) {
                final int singleExp = Integer.valueOf(sh.getAttribute());
                final int useTotalExp = time * singleExp;
                final List<UpdateExp> list = this.generalService.updateExpAndGeneralLevel(playerId, generalId, useTotalExp);
                if (list != null && !list.isEmpty()) {
                    if (list.get(0).getCurExp() == 0L) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_EQUIP_EXP_FULL);
                    }
                    exp = list.get(list.size() - 1);
                    upLv = (exp.getCurLv() > pgm.getLv());
                    for (final UpdateExp exp2 : list) {
                        totalExp += exp2.getCurExp();
                    }
                    final int tmp = (int)(totalExp / singleExp);
                    useTime = ((totalExp % singleExp > 0L) ? (tmp + 1) : tmp);
                }
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (sh.getNum() > useTime) {
            this.storeHouseDao.reduceNum(sh.getVId(), useTime);
            doc.createElement("haveLeft", true);
            doc.createElement("useTime", useTime);
        }
        else {
            this.storeHouseDao.deleteById(vId);
            doc.createElement("haveLeft", false);
            doc.createElement("useTime", sh.getNum());
        }
        if (upLv) {
            doc.createElement("lvUp", upLv);
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
        }
        if (exp != null) {
            doc.createElement("exp", totalExp);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] useIronRewardToken(final PlayerDto playerDto, final int vId) {
        final int playerId = playerDto.playerId;
        final StoreHouse sh = this.storeHouseDao.read(vId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_EQUIP);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getType() != 18) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_HOUSE_TYPE_WRONG);
        }
        final int itemId = sh.getItemId();
        final int id = IronRewardEvent.itemIdReverseMap.get(itemId);
        PlayerIncenseWeaponEffect piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
        if (piwe == null) {
            this.dataGetter.getEventService().initPlayerIncenseWeaponEffect(playerId);
            piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
        }
        final Items item = (Items)this.dataGetter.getItemsCache().get((Object)itemId);
        long cd = 0L;
        if (1 == id || 3 == id) {
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
            final ThreeTuple<Integer, Integer, Long> threeTuple = this.dataGetter.getEventService().getEffect(item.getEffect());
            cd = threeTuple.right + IronRewardEvent.REAL_MORE_CD;
            this.dataGetter.getPlayerIncenseWeaponEffectDao().updateIncenseEffect(playerId, id, threeTuple.left, threeTuple.middle, TimeUtil.nowAddMs(cd));
            EquipService.timerLog.error("class:EquipService#method:useIronRewardToken#playerId:" + playerId + "#pos:" + id);
            if (1 == id) {
                Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("ironIncenseEffect", 1));
            }
        }
        else {
            final int weaponId = piwe.getWeaponId();
            final Date weaponEndTime = piwe.getWeaponEndTime();
            if (weaponId > 0 && weaponEndTime.getTime() > System.currentTimeMillis()) {
                return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.IRON_REWARD_IN_BUFFER, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(weaponId))).getName() }));
            }
            final ThreeTuple<Integer, Integer, Long> threeTuple2 = this.dataGetter.getEventService().getEffect(item.getEffect());
            cd = threeTuple2.right + IronRewardEvent.REAL_MORE_CD;
            this.dataGetter.getPlayerIncenseWeaponEffectDao().updateWeaponEffect(playerId, id, threeTuple2.left, threeTuple2.middle, TimeUtil.nowAddMs(cd));
            EquipService.timerLog.error("class:EquipService#method:useIronRewardToken#playerId:" + playerId + "#id:" + id);
        }
        if (sh.getNum() > 1) {
            this.storeHouseDao.reduceNum(sh.getVId(), 1);
        }
        else {
            this.storeHouseDao.deleteById(vId);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cd", cd + IronRewardEvent.MORE_CD);
        doc.createElement("id", id);
        doc.createElement("effectType", IronRewardEvent.effectTypeMap.get(id));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] useXiLianToken(final int playerId, final int vId1, final int vId2, final int equipSkillId) {
        if (vId1 <= 0 || vId2 <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final StoreHouse storeHouse = this.dataGetter.getStoreHouseDao().read(vId1);
        if (storeHouse == null || storeHouse.getPlayerId() != playerId || storeHouse.getType() != 19) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NO_TOKEN);
        }
        final StoreHouse sh = this.dataGetter.getStoreHouseDao().read(vId2);
        if (sh == null || sh.getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        final String skills = sh.getRefreshAttribute();
        if (StringUtils.isBlank(skills)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WRONG_EQUIP);
        }
        final int pos = XiLianEvent.itemIdReverseMap.get(storeHouse.getItemId());
        int result = 0;
        if (1 == pos) {
            if (!this.dataGetter.getEquipCache().getJinpinEquips().contains(sh.getItemId()) || !this.dataGetter.getBlacksmithService().is_3_Length(sh.getRefreshAttribute()) || this.dataGetter.getBlacksmithService().is_5(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NOT_SPECIAL_EQUIP);
            }
            final int isSameSpecialSkillLv4 = this.dataGetter.getBlacksmithService().isSameSpecialSkillLv4(skills);
            if (isSameSpecialSkillLv4 == 0) {
                this.dataGetter.getStoreHouseDao().updateRefreshAttribute(vId2, this.dataGetter.getEventService().getUpdateRefreshAttribute(skills));
            }
            else {
                result = isSameSpecialSkillLv4;
                this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecial(vId2, this.dataGetter.getEventService().getAllRefreshAttribute(isSameSpecialSkillLv4), isSameSpecialSkillLv4);
            }
        }
        else if (2 == pos) {
            if (!this.dataGetter.getEquipCache().getJinpinEquips().contains(sh.getItemId()) || !this.dataGetter.getBlacksmithService().is_5(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.XILIAN_NOT_FULL_SPECIAL_EQUIP);
            }
            final int size = this.dataGetter.getEquipSkillCache().SIZE;
            final int specialSkillId = WebUtil.nextInt(size) + 1;
            this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecial(vId2, this.dataGetter.getEventService().getAllRefreshAttribute(specialSkillId), specialSkillId);
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
            this.dataGetter.getStoreHouseDao().updateRefreshAttriAndSpecial(vId2, this.dataGetter.getEventService().getAllRefreshAttribute(equipSkillId), equipSkillId);
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
        if (storeHouse.getNum() > 1) {
            this.storeHouseDao.reduceNum(vId1, 1);
        }
        else {
            this.storeHouseDao.deleteById(vId1);
        }
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("result", result));
    }
    
    private Tuple<Integer, Integer> getGeneralTreasureEffect(final PlayerGeneralCivil pgc) {
        final List<StoreHouse> shList = this.storeHouseDao.getGeneralEquipList(pgc.getPlayerId(), pgc.getGeneralId(), 3);
        final Tuple<Integer, Integer> result = (Tuple<Integer, Integer>)new Tuple();
        result.left = 0;
        result.right = 0;
        for (final StoreHouse sh : shList) {
            final String[] attributeList = StringUtils.split(sh.getAttribute(), ',');
            final int firstAtt = Integer.valueOf(attributeList[0]);
            final int secondAtt = Integer.valueOf(attributeList[1]);
            final Tuple<Integer, Integer> tuple = result;
            tuple.left = (int)tuple.left + firstAtt;
            final Tuple<Integer, Integer> tuple2 = result;
            tuple2.right = (int)tuple2.right + secondAtt;
        }
        return result;
    }
    
    private Tuple<Integer, Integer> getGeneralTreasureEffect(final PlayerGeneralMilitary pgm) {
        final List<StoreHouse> shList = this.storeHouseDao.getGeneralEquipList(pgm.getPlayerId(), pgm.getGeneralId(), 3);
        final Tuple<Integer, Integer> result = (Tuple<Integer, Integer>)new Tuple();
        result.left = 0;
        result.right = 0;
        for (final StoreHouse sh : shList) {
            final String[] attributeList = StringUtils.split(sh.getAttribute(), ',');
            final int firstAtt = Integer.valueOf(attributeList[0]);
            final int secondAtt = Integer.valueOf(attributeList[1]);
            final Tuple<Integer, Integer> tuple = result;
            tuple.left = (int)tuple.left + firstAtt;
            final Tuple<Integer, Integer> tuple2 = result;
            tuple2.right = (int)tuple2.right + secondAtt;
        }
        return result;
    }
    
    @Override
    public byte[] updateEquipTen(final PlayerDto playerDto, final int vId) {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] compoundSuit(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int itemId = storeHouse.getItemId();
        final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(itemId);
        if (equipCoordinates == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("num", 6);
        doc.createElement("suitName", equipCoordinates.getName());
        doc.startArray("suits");
        final int skillNum = 4;
        final Integer[] type = this.equipCache.getSkillArray(equipCoordinates.getId());
        int curNum = 0;
        final List<StoreHouse> equips = this.storeHouseDao.getMilitaryEquipList(playerDto.playerId, 6);
        for (int i = 1; i <= type.length; ++i) {
            final Equip equip = this.equipCache.getSuitSingleEquipByType(i);
            if (equip == null) {
                EquipService.log.error("can't find purple equip...type:" + i);
            }
            else {
                doc.startObject();
                doc.createElement("equipId", equip.getId());
                doc.createElement("lv", equip.getLevel());
                int attribute = equip.getAttribute();
                if (i == 5 || i == 6) {
                    attribute /= 3;
                }
                doc.createElement("attribute", attribute);
                doc.createElement("type", i);
                doc.createElement("copper", equip.getCopperSold());
                doc.createElement("maxLv", equip.getMaxLevel());
                doc.createElement("maxSkillNum", this.equipCache.getEquipMaxSkillNum(equip));
                doc.createElement("equipName", equip.getName());
                doc.createElement("equipPic", equip.getPic());
                doc.createElement("skillNum", 4);
                final int skillType = type[i - 1];
                final EquipSkill equipSkill = (EquipSkill)this.equipSkillCache.get((Object)skillType);
                doc.createElement("skillType", skillType);
                doc.createElement("quality", equip.getQuality());
                doc.createElement("skillName", equipSkill.getName());
                doc.createElement("skillPic", equipSkill.getPic());
                doc.createElement("skillLv", equip.getSkillLvMax());
                final StoreHouse theSelectOneStoreHouse = this.getTheSelectSuitOneStoreHouse(equip, type[i - 1], playerDto.playerId, equips);
                if (theSelectOneStoreHouse != null) {
                    ++curNum;
                    if (theSelectOneStoreHouse.getOwner() > 0) {
                        final General general = (General)this.generalCache.get((Object)theSelectOneStoreHouse.getOwner());
                        doc.createElement("ownerName", general.getName());
                    }
                }
                doc.createElement("isOwn", theSelectOneStoreHouse != null);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("curNum", curNum);
        final int att = equipCoordinates.getAtt();
        if (att > 0) {
            doc.createElement("att", att);
        }
        final int def = equipCoordinates.getDef();
        if (def > 0) {
            doc.createElement("def", def);
        }
        final int blood = equipCoordinates.getBlood();
        if (blood > 0) {
            doc.createElement("blood", equipCoordinates.getBlood());
        }
        doc.createElement("suitIntro", equipCoordinates.getIntro());
        doc.createElement("suitPic", equipCoordinates.getPic());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private StoreHouse getTheSelectSuitOneStoreHouse(final Equip equip, final int skillType, final int playerId, final List<StoreHouse> equips) {
        if (equips == null || equips.isEmpty()) {
            return null;
        }
        for (final StoreHouse toCheck : equips) {
            if (toCheck.getSpecialSkillId() == null) {
                continue;
            }
            if (equip.getType() != toCheck.getGoodsType()) {
                continue;
            }
            if (equip.getQuality() != toCheck.getQuality()) {
                continue;
            }
            if (EquipCommon.getRefreshAttNum(toCheck.getRefreshAttribute()) == equip.getSkillNum() + 1 && toCheck.getSpecialSkillId() == skillType) {
                return toCheck;
            }
        }
        return null;
    }
    
    @Transactional
    @Override
    public byte[] doCompoundSuit(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final boolean tech = this.canGetSuit(playerDto.playerId, this.techEffectCache);
        if (!tech) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TAOZHUANG_TECH);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Items items = (Items)this.itemsCache.get((Object)storeHouse.getItemId());
        if (items == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(storeHouse.getItemId());
        if (equipCoordinates == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Integer[] type = this.equipCache.getSkillArray(equipCoordinates.getId());
        final List<StoreHouse> storeHouses = new ArrayList<StoreHouse>();
        final List<Integer> generals = new ArrayList<Integer>();
        final List<StoreHouse> equips = this.storeHouseDao.getMilitaryEquipList(playerDto.playerId, 6);
        for (int i = 1; i <= type.length; ++i) {
            final Equip equip = this.equipCache.getSuitSingleEquipByType(i);
            final StoreHouse toStore = this.getTheSelectSuitOneStoreHouse(equip, type[i - 1], playerDto.playerId, equips);
            if (toStore == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.SUIT_COMPOUND_EQUIP_NOT_FUL);
            }
            storeHouses.add(toStore);
            if (toStore.getOwner() > 0) {
                generals.add(toStore.getOwner());
            }
        }
        this.storeHouseDao.deleteById(vId);
        final StoreHouse suit = new StoreHouse();
        suit.setPlayerId(playerDto.playerId);
        suit.setType(10);
        suit.setGoodsType(10);
        suit.setItemId(storeHouse.getItemId());
        suit.setQuenchingTimes(0);
        suit.setQuenchingTimesFree(0);
        suit.setRefreshAttribute("");
        suit.setSpecialSkillId(0);
        suit.setOwner(0);
        suit.setState(0);
        suit.setNum(1);
        suit.setQuality(6);
        suit.setLv(0);
        suit.setGemId(0);
        suit.setQuenchingTimes(0);
        suit.setBindExpireTime(0L);
        suit.setMarkId(0);
        this.storeHouseDao.create(suit);
        final int suiId = suit.getVId();
        StoreHouse toDelete = null;
        for (int j = 0; j < storeHouses.size(); ++j) {
            toDelete = storeHouses.get(j);
            this.storeHouseDao.deleteById(toDelete.getVId());
            final StoreHouseBak storeHouseBak = new StoreHouseBak();
            copyProperties(storeHouseBak, toDelete);
            storeHouseBak.setOwner(0);
            storeHouseBak.setSuitId(suiId);
            this.storeHouseBakDao.create(storeHouseBak);
        }
        if (generals != null && !generals.isEmpty()) {
            for (final Integer generalId : generals) {
                this.battleDataCache.removeEquipEffect(playerDto.playerId, generalId);
            }
        }
        final String msg = MessageFormatter.format(LocalMessages.TAOZHUANG_PUSH_MSG, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, playerDto.playerName), equipCoordinates.getName() });
        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static void copyProperties(final StoreHouseBak storeHouseBak, final StoreHouse toDelete) {
        storeHouseBak.setAttribute(toDelete.getAttribute());
        storeHouseBak.setGemId(toDelete.getGemId());
        storeHouseBak.setGoodsType(toDelete.getGoodsType());
        storeHouseBak.setItemId(toDelete.getItemId());
        storeHouseBak.setLv(toDelete.getLv());
        storeHouseBak.setNum(toDelete.getNum());
        storeHouseBak.setOwner(toDelete.getOwner());
        storeHouseBak.setPlayerId(toDelete.getPlayerId());
        storeHouseBak.setQuality(toDelete.getQuality());
        storeHouseBak.setQuenchingTimes(toDelete.getQuenchingTimes());
        storeHouseBak.setQuenchingTimesFree(toDelete.getQuenchingTimesFree());
        storeHouseBak.setRefreshAttribute(toDelete.getRefreshAttribute());
        storeHouseBak.setSpecialSkillId(toDelete.getSpecialSkillId());
        storeHouseBak.setState(toDelete.getState());
        storeHouseBak.setBindExpireTime((toDelete.getBindExpireTime() == null) ? 0L : ((long)toDelete.getBindExpireTime()));
        storeHouseBak.setType(toDelete.getType());
    }
    
    @Transactional
    @Override
    public byte[] demountSuit(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (isEquip(storeHouse.getType())) {
            final int state = getBindState(storeHouse.getBindExpireTime());
            if (state != 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.EQUIP_IS_BOUND);
            }
        }
        final List<StoreHouseBak> list = this.storeHouseBakDao.getListByStoreId(storeHouse.getVId());
        final List<StoreHouse> toStore = new ArrayList<StoreHouse>();
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(storeHouse.getItemId());
        final Integer[] type = this.equipCache.getSkillArray(equipCoordinates.getId());
        if (list.size() != type.length) {
            EquipService.log.error("demountSuit fail....bak data is inconsistent with suit..bak size is:" + list.size());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        final int maxNum = (pa == null) ? 0 : pa.getMaxStoreNum();
        final int curNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
        if (curNum + type.length > maxNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
        }
        final int gold = equipCoordinates.getUnloadGold();
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, gold, "\u62c6\u89e3\u5957\u88c5\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        StoreHouseBak bak = null;
        StoreHouse house = null;
        for (int i = 1; i <= type.length; ++i) {
            bak = list.get(i - 1);
            if (bak == null) {
                EquipService.log.error("bak is null.....");
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            house = new StoreHouse();
            copyProperties(house, bak);
            toStore.add(i - 1, house);
        }
        this.storeHouseDao.demoutCreate(toStore);
        this.storeHouseBakDao.demoutDelete(storeHouse.getVId());
        this.storeHouseDao.deleteById(storeHouse.getVId());
        final Items item = (Items)this.itemsCache.get((Object)storeHouse.getItemId());
        final StoreHouse sh = new StoreHouse();
        sh.setType(9);
        sh.setGoodsType(9);
        sh.setItemId(item.getId());
        sh.setLv(0);
        sh.setPlayerId(playerDto.playerId);
        sh.setOwner(0);
        sh.setQuality(0);
        sh.setGemId(0);
        sh.setAttribute("0");
        sh.setNum(1);
        sh.setState(0);
        sh.setRefreshAttribute("");
        sh.setQuenchingTimes(0);
        sh.setBindExpireTime(0L);
        sh.setMarkId(0);
        this.storeHouseDao.create(sh);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static void copyProperties(final StoreHouse toCreate, final StoreHouseBak storeHouseBak) {
        toCreate.setAttribute(storeHouseBak.getAttribute());
        toCreate.setGemId(storeHouseBak.getGemId());
        toCreate.setGoodsType(storeHouseBak.getGoodsType());
        toCreate.setItemId(storeHouseBak.getItemId());
        toCreate.setLv(storeHouseBak.getLv());
        toCreate.setNum(storeHouseBak.getNum());
        toCreate.setOwner(storeHouseBak.getOwner());
        toCreate.setPlayerId(storeHouseBak.getPlayerId());
        toCreate.setQuality(storeHouseBak.getQuality());
        toCreate.setQuenchingTimes(storeHouseBak.getQuenchingTimes());
        toCreate.setQuenchingTimesFree(storeHouseBak.getQuenchingTimesFree());
        toCreate.setRefreshAttribute(storeHouseBak.getRefreshAttribute());
        toCreate.setSpecialSkillId(storeHouseBak.getSpecialSkillId());
        toCreate.setState(storeHouseBak.getState());
        toCreate.setType(storeHouseBak.getType());
        toCreate.setBindExpireTime((storeHouseBak.getBindExpireTime() == null) ? 0L : ((long)storeHouseBak.getBindExpireTime()));
        toCreate.setMarkId(0);
    }
    
    @Override
    public boolean canGetSuit(final int playerId, final TechEffectCache techEffectCache) {
        final int tech = techEffectCache.getTechEffect(playerId, 48);
        return tech > 0;
    }
    
    @Override
    public byte[] demountSuitGold(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<StoreHouseBak> list = this.storeHouseBakDao.getListByStoreId(storeHouse.getVId());
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(storeHouse.getItemId());
        if (equipCoordinates == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Integer[] type = this.equipCache.getSkillArray(equipCoordinates.getId());
        if (list.size() != type.length) {
            EquipService.log.error("demountSuit fail....bak data is inconsistent with suit..bak size is:" + list.size());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int gold = equipCoordinates.getUnloadGold();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] compoundProset(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Items items = (Items)this.itemsCache.get((Object)storeHouse.getItemId());
        if (items == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(storeHouse.getItemId());
        if (equipProset == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<EquipCoordinates> list = this.equipCache.getAllSuits(equipProset.getId());
        if (list == null || list.size() <= 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        int curNum = 0;
        doc.createElement("num", list.size());
        doc.createElement("itemName", equipProset.getName());
        doc.createElement("att", equipProset.getAtt());
        doc.createElement("def", equipProset.getDef());
        doc.createElement("blood", equipProset.getBlood());
        doc.createElement("pic", equipProset.getPic());
        doc.createElement("type", 14);
        doc.createElement("suitIntro", equipProset.getIntro());
        this.appendSingleEquipCoordinatesInfo(this.equipCache.getMainSuit(equipProset.getId()), doc);
        doc.createElement("suitIntro", equipProset.getIntro());
        doc.startArray("equipProsets");
        for (final EquipCoordinates equipCoordinates : list) {
            if (equipCoordinates == null) {
                continue;
            }
            doc.startObject();
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, equipCoordinates.getItemId(), 10);
            int ownerId = 0;
            if (shList != null && shList.size() > 0) {
                final StoreHouse itemHouse = shList.get(0);
                doc.createElement("owned", true);
                ++curNum;
                ownerId = itemHouse.getOwner();
                final int bindState = getBindState(itemHouse.getBindExpireTime());
                if (bindState == -1) {
                    doc.createElement("isBind", (-1));
                }
                else if (bindState == 0) {
                    doc.createElement("isBind", 0);
                }
                else {
                    doc.createElement("isBind", 1);
                    doc.createElement("leftTime", bindState);
                }
            }
            else {
                doc.createElement("owned", false);
                doc.createElement("isBind", 0);
            }
            this.appendEquipCoordinatesInfo(equipCoordinates, ownerId, doc);
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("curNum", curNum);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void appendEquipCoordinatesInfo(final EquipCoordinates equipCoordinates, final int ownerId, final JsonDocument doc) {
        if (equipCoordinates == null) {
            return;
        }
        doc.createElement("itemName", equipCoordinates.getName());
        if (ownerId != 0) {
            final General general = (General)this.generalCache.get((Object)ownerId);
            if (general != null) {
                doc.createElement("generalName", general.getName());
            }
        }
        this.appendSingleEquipCoordinatesInfo(equipCoordinates, doc);
        int att = 0;
        int def = 0;
        int blood = 0;
        att = equipCoordinates.getAtt();
        def = equipCoordinates.getDef();
        blood = equipCoordinates.getBlood();
        doc.createElement("att", att);
        doc.createElement("def", def);
        doc.createElement("blood", blood);
        doc.createElement("type", 10);
        doc.createElement("pic", equipCoordinates.getPic());
        doc.createElement("suitIntro", equipCoordinates.getIntro());
    }
    
    @Transactional
    @Override
    public byte[] doCompoundProset(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final boolean tech = this.canGetSuit(playerDto.playerId, this.techEffectCache);
        if (!tech) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TAOZHUANG_TECH);
        }
        final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(storeHouse.getItemId());
        if (equipProset == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<EquipCoordinates> list = this.equipCache.getAllSuits(equipProset.getId());
        if (list == null || list.size() <= 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        List<StoreHouse> inStoreHouseList = null;
        final List<StoreHouse> storeHouses = new ArrayList<StoreHouse>();
        long binMaxExpireTime = 0L;
        for (final EquipCoordinates equipCoordinates : list) {
            inStoreHouseList = this.storeHouseDao.getByItemId(playerDto.playerId, equipCoordinates.getItemId(), 10);
            if (inStoreHouseList == null || inStoreHouseList.size() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.SUIT_COMPOUND_EQUIP_NOT_FUL);
            }
            storeHouses.add(inStoreHouseList.get(0));
            final Long bindLong = inStoreHouseList.get(0).getBindExpireTime();
            if (bindLong == null || bindLong <= binMaxExpireTime) {
                continue;
            }
            binMaxExpireTime = bindLong;
        }
        if (storeHouses.size() <= 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SUIT_COMPOUND_EQUIP_NOT_FUL);
        }
        final StoreHouse suit = new StoreHouse();
        suit.setPlayerId(playerDto.playerId);
        suit.setType(14);
        suit.setGoodsType(14);
        suit.setItemId(equipProset.getItemId());
        suit.setQuenchingTimes(0);
        suit.setQuenchingTimesFree(0);
        suit.setRefreshAttribute("");
        suit.setSpecialSkillId(0);
        suit.setOwner(0);
        suit.setState(0);
        suit.setNum(1);
        suit.setQuality(6);
        suit.setLv(0);
        suit.setGemId(0);
        suit.setQuenchingTimes(0);
        suit.setBindExpireTime(binMaxExpireTime);
        suit.setMarkId(0);
        this.storeHouseDao.create(suit);
        final int suitVid = suit.getVId();
        int index = 0;
        Set<Integer> generals = null;
        for (final StoreHouse singleHouse : storeHouses) {
            if (singleHouse == null) {
                continue;
            }
            this.storeHouseBakDao.changeSuitId(suitVid, playerDto.playerId, singleHouse.getVId(), index);
            this.storeHouseDao.deleteById(singleHouse.getVId());
            ++index;
            if (singleHouse.getOwner() == 0) {
                continue;
            }
            if (generals == null) {
                generals = new HashSet<Integer>();
            }
            generals.add(singleHouse.getOwner());
        }
        if (generals != null && !generals.isEmpty()) {
            for (final Integer general : generals) {
                this.battleDataCache.removeEquipEffect(playerDto.playerId, general);
            }
        }
        this.storeHouseDao.deleteById(vId);
        return JsonBuilder.getJson(State.SUCCESS, LocalMessages.COMPOUND_SUCCESS);
    }
    
    @Override
    public byte[] demoutProsetGold(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final boolean tech = this.canGetSuit(playerDto.playerId, this.techEffectCache);
        if (!tech) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TAOZHUANG_TECH);
        }
        final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(storeHouse.getItemId());
        if (equipProset == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<StoreHouseBak> list = this.storeHouseBakDao.getListByStoreId(storeHouse.getVId());
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<EquipCoordinates> equipCoordinates = this.equipCache.getAllSuits(equipProset.getId());
        final int size = equipCoordinates.size() * 6;
        if (size != list.size()) {
            EquipService.log.error("demountProset fail....bak data is inconsistent with suit..bak size is:" + list.size());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int gold = equipProset.getUnloadGold();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] doDemoutProset(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (isEquip(storeHouse.getType())) {
            final int state = getBindState(storeHouse.getBindExpireTime());
            if (state != 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.EQUIP_IS_BOUND);
            }
        }
        final boolean tech = this.canGetSuit(playerDto.playerId, this.techEffectCache);
        if (!tech) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TAOZHUANG_TECH);
        }
        final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(storeHouse.getItemId());
        if (equipProset == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<StoreHouseBak> list = this.storeHouseBakDao.getListByStoreId(storeHouse.getVId());
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<EquipCoordinates> equipCoordinates = this.equipCache.getAllSuits(equipProset.getId());
        final int size = equipCoordinates.size() * 6;
        if (size != list.size()) {
            EquipService.log.error("demountProset fail....bak data is inconsistent with suit..bak size is:" + list.size());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        final int maxNum = (pa == null) ? 0 : pa.getMaxStoreNum();
        final int curNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
        if (curNum + equipCoordinates.size() > maxNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
        }
        final int gold = equipProset.getUnloadGold();
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, gold, "\u62c6\u89e3\u5957\u88c5\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        StoreHouse suit = null;
        int suitId = 0;
        int index = 0;
        for (final EquipCoordinates single : equipCoordinates) {
            if (single == null) {
                continue;
            }
            suit = new StoreHouse();
            suit.setPlayerId(playerDto.playerId);
            suit.setType(10);
            suit.setGoodsType(10);
            suit.setItemId(single.getItemId());
            suit.setQuenchingTimes(0);
            suit.setQuenchingTimesFree(0);
            suit.setRefreshAttribute("");
            suit.setSpecialSkillId(0);
            suit.setOwner(0);
            suit.setState(0);
            suit.setNum(1);
            suit.setQuality(6);
            suit.setLv(0);
            suit.setGemId(0);
            suit.setQuenchingTimes(0);
            suit.setBindExpireTime(0L);
            suit.setMarkId(0);
            this.storeHouseDao.create(suit);
            suitId = suit.getVId();
            this.storeHouseBakDao.changeBackJunior(suitId, storeHouse.getVId(), index, playerDto.playerId);
            ++index;
        }
        this.storeHouseDao.deleteById(storeHouse.getVId());
        final Items item = (Items)this.itemsCache.get((Object)storeHouse.getItemId());
        final StoreHouse sh = new StoreHouse();
        sh.setType(13);
        sh.setGoodsType(13);
        sh.setItemId(item.getId());
        sh.setLv(0);
        sh.setPlayerId(playerDto.playerId);
        sh.setOwner(0);
        sh.setQuality(0);
        sh.setGemId(0);
        sh.setAttribute("0");
        sh.setNum(1);
        sh.setState(0);
        sh.setRefreshAttribute("");
        sh.setQuenchingTimes(0);
        sh.setBindExpireTime(0L);
        sh.setMarkId(0);
        this.storeHouseDao.create(sh);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] useResourceToken(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Items items = (Items)this.itemsCache.get((Object)storeHouse.getItemId());
        if (items == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String itemInfo = items.getEffect();
        final String[] single = itemInfo.split(";");
        if (single.length < 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int buildingType = 0;
        int mode = 0;
        int day = 0;
        try {
            buildingType = Integer.parseInt(single[0]);
            mode = Integer.parseInt(single[1]);
            day = Integer.parseInt(single[2]);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final com.reign.util.Tuple<Boolean, String> result = this.buildingService.addBuildingAdditionForFree(playerDto, buildingType, mode, day);
        if (result == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (!(boolean)result.left) {
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        if (storeHouse.getNum() > 1) {
            this.storeHouseDao.reduceNum(vId, 1);
        }
        else {
            this.storeHouseDao.deleteById(vId);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] bindEquip(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Long bindExpireTime = storeHouse.getBindExpireTime();
        if (!isEquip(storeHouse.getType())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int bindState = getBindState(bindExpireTime);
        if (bindState != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.EQUIP_HAS_BOUND_ALREADY);
        }
        this.storeHouseDao.updateBindExpireTime(vId, Long.MAX_VALUE);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static int getBindState(final Long expireTime) {
        if (expireTime == null) {
            return 0;
        }
        if (expireTime == Long.MAX_VALUE) {
            return -1;
        }
        final long leftTime = expireTime - System.currentTimeMillis();
        if (leftTime <= 0L) {
            return 0;
        }
        return (int)leftTime;
    }
    
    @Override
    public byte[] unbindEquip(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Long bindExpireTime = storeHouse.getBindExpireTime();
        final int bindState = getBindState(bindExpireTime);
        if (bindState != -1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.EQUIP_HAS_BOUND_ALREADY);
        }
        if (!isEquip(storeHouse.getType())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final long toUpdateTime = System.currentTimeMillis() + 259200000L;
        this.storeHouseDao.updateBindExpireTime(vId, toUpdateTime);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static boolean isEquip(final int type) {
        return type == 1 || type == 10 || type == 14;
    }
    
    @Override
    public byte[] cancelUnbindEquip(final PlayerDto playerDto, final int vId) {
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null || storeHouse.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Long bindExpireTime = storeHouse.getBindExpireTime();
        final int bindState = getBindState(bindExpireTime);
        if (bindState <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (!isEquip(storeHouse.getType())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.storeHouseDao.updateBindExpireTime(vId, Long.MAX_VALUE);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getEquipSkillInfo() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("equipSkills");
        for (final EquipSkill es : this.dataGetter.getEquipSkillCache().getModels()) {
            doc.startObject();
            doc.createElement("id", es.getId());
            doc.createElement("name", es.getName());
            doc.createElement("pic", es.getPic());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
