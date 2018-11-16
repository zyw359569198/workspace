package com.reign.gcld.tickets.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.phantom.service.*;
import com.reign.gcld.blacksmith.dao.*;
import com.reign.gcld.diamondshop.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.store.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.diamondshop.domain.*;
import com.reign.gcld.tickets.domain.*;
import java.util.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.player.domain.*;

@Component
public class TicketsService implements ITicketsService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private TicketsMarketCache ticketsMarketCache;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private IStoreHouseService getStoreHouseService;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ISlaveholderDao slaveholderDao;
    @Autowired
    private IPhantomService phantomService;
    @Autowired
    private IPlayerBlacksmithDao playerBlacksmithDao;
    @Autowired
    private IPlayerDiamondShopDao playerDiamondShopDao;
    public static final String SPLIT = ":";
    public static final String FOOD = "food";
    public static final String IRON = "iron";
    public static final String COPPER = "copper";
    public static final String GEM = "gem";
    public static final String WOOD = "wood";
    public static final String ITEM = "item";
    
    @Transactional
    @Override
    public byte[] getMarket(final PlayerDto player) {
        final JsonDocument doc = new JsonDocument();
        final Slaveholder slaveholder = this.slaveholderDao.read(player.playerId);
        int prisonLv = 0;
        if (slaveholder != null) {
            prisonLv = slaveholder.getPrisonLv();
        }
        final int blackSmithLv = this.playerBlacksmithDao.getSizeByPlayerId(player.playerId);
        final List<StoreHouse> shList = this.storeHouseDao.getStoreHouseByPlayerId(player.playerId);
        final int phantomLv = this.phantomService.getPhantomWorkShopLv(player.playerId);
        int diamondLv = 0;
        final PlayerDiamondShop pds = this.playerDiamondShopDao.getMaxShop(player.playerId);
        if (pds != null) {
            diamondLv = pds.getShopId();
        }
        final PlayerTickets pt = this.playerTicketsDao.safeGetPlayerTickets(player.playerId);
        doc.createElement("myTickets", pt.getTickets());
        doc.startArray("goods");
        for (final TicketsMarket tm : this.ticketsMarketCache.getModels()) {
            if (player.playerLv >= tm.getSee_lv()) {
                if (this.getGoodsName(tm.getReward()).equalsIgnoreCase("item")) {
                    final Items item = (Items)this.itemsCache.get((Object)this.getGoodsNumOrItemId(tm.getReward()));
                    if (item == null) {
                        continue;
                    }
                    if (item.getType() == 8) {
                        if (item.getIndex() != prisonLv + 1) {
                            continue;
                        }
                        boolean isShow = true;
                        final int itemType = StoreHouseService.getStoreHouseType(item.getType());
                        for (final StoreHouse sh : shList) {
                            if (sh.getItemId() == item.getId() && sh.getType() == itemType) {
                                isShow = false;
                                break;
                            }
                        }
                        if (!isShow) {
                            continue;
                        }
                    }
                    if (item.getType() == 18) {
                        if (item.getIndex() != diamondLv + 1) {
                            continue;
                        }
                        boolean isShow = true;
                        final int itemType = StoreHouseService.getStoreHouseType(item.getType());
                        for (final StoreHouse sh : shList) {
                            if (sh.getItemId() == item.getId() && sh.getType() == itemType) {
                                isShow = false;
                                break;
                            }
                        }
                        if (!isShow) {
                            continue;
                        }
                    }
                    if (item.getType() == 10) {
                        if (item.getIndex() != phantomLv + 1) {
                            continue;
                        }
                        boolean isShow = true;
                        final int itemType = StoreHouseService.getStoreHouseType(item.getType());
                        for (final StoreHouse sh : shList) {
                            if (sh.getItemId() == item.getId() && sh.getType() == itemType) {
                                isShow = false;
                                break;
                            }
                        }
                        if (!isShow) {
                            continue;
                        }
                    }
                    if (item.getType() == 9) {
                        final Items changeItem = (Items)this.itemsCache.get((Object)item.getChangeItemId());
                        boolean isShow2 = true;
                        final int changeItemType = StoreHouseService.getStoreHouseType(changeItem.getType());
                        final int changeItemId = changeItem.getId();
                        final EquipProset equipProset = this.equipCache.getProsetBySubSuitId(changeItemId);
                        for (final StoreHouse sh2 : shList) {
                            if (sh2.getItemId() == changeItem.getId() && (sh2.getType() == changeItemType || sh2.getType() == 10 || sh2.getType() == 14)) {
                                isShow2 = false;
                                break;
                            }
                            if (equipProset != null && sh2.getItemId() == equipProset.getItemId() && sh2.getType() == 14) {
                                isShow2 = false;
                                break;
                            }
                        }
                        if (!isShow2) {
                            continue;
                        }
                    }
                    if (item.getType() == 12) {
                        if (item.getIndex() != blackSmithLv + 1) {
                            continue;
                        }
                        boolean isShow = true;
                        final int itemType = StoreHouseService.getStoreHouseType(item.getType());
                        for (final StoreHouse sh : shList) {
                            if (sh.getItemId() == item.getId() && sh.getType() == itemType) {
                                isShow = false;
                                break;
                            }
                        }
                        if (!isShow) {
                            continue;
                        }
                    }
                    doc.startObject();
                    doc.createElement("id", tm.getId());
                    doc.createElement("buyLv", tm.getBuy_lv());
                    doc.createElement("reward", 1);
                    doc.createElement("pic", item.getPic());
                    if (item.getType() == 6) {
                        this.equipCoordinatesInfo(item, doc);
                    }
                }
                else {
                    doc.startObject();
                    doc.createElement("id", tm.getId());
                    doc.createElement("buyLv", tm.getBuy_lv());
                    doc.createElement("pic", tm.getPic());
                    doc.createElement("reward", this.getGoodsNumOrItemId(tm.getReward()));
                }
                doc.createElement("tickets", tm.getTickets());
                doc.endObject();
            }
        }
        doc.endArray();
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private void equipCoordinatesInfo(final Items item, final JsonDocument doc) {
        try {
            final EquipCoordinates equipCoordinates = this.equipCache.getEquipCoordinateByItemId(item.getId());
            if (equipCoordinates == null) {
                return;
            }
            doc.createElement("suitName", equipCoordinates.getName());
            doc.startArray("suits");
            final int skillNum = 4;
            final Integer[] type = this.equipCache.getSkillArray(equipCoordinates.getId());
            for (int i = 1; i <= type.length; ++i) {
                final Equip equip = this.equipCache.getSuitSingleEquipByType(i);
                if (equip != null) {
                    doc.startObject();
                    doc.createElement("equipId", equip.getId());
                    doc.createElement("lv", equip.getLevel());
                    int attribute = equip.getAttribute();
                    if (i == 5 || i == 6) {
                        attribute /= 3;
                    }
                    doc.createElement("type", i);
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
                    doc.endObject();
                }
            }
            doc.endArray();
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
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("equipCoordinatesInfo fail..itemId" + item.getId());
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
    
    @Transactional
    @Override
    public byte[] buy(final PlayerDto playerDto, final int id, final int num) {
        final TicketsMarket tm = (TicketsMarket)this.ticketsMarketCache.get((Object)id);
        if (tm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (num <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (playerDto.playerLv < tm.getBuy_lv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUCTION_LEVEL_LIMIT);
        }
        final String goodsName = this.getGoodsName(tm.getReward());
        final int rewardNum = this.getGoodsNumOrItemId(tm.getReward());
        final int goodsNum = rewardNum * num;
        if (goodsName.equalsIgnoreCase("item")) {
            final Items item = (Items)this.itemsCache.get((Object)rewardNum);
            if (item == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (num > 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            int itemId = item.getId();
            int itemType = item.getType();
            EquipCoordinates ecd = this.equipCache.getEquipCoordinateByItemId(itemId);
            if (ecd != null) {
                final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, 10);
                if (shList != null && shList.size() > 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                }
            }
            final EquipProset equipProset = this.equipCache.getEquipProsetByItemId(itemId);
            if (equipProset != null) {
                final List<StoreHouse> shList2 = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, 14);
                if (shList2 != null && shList2.size() > 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                }
            }
            final EquipProset canCompoundEquip = this.equipCache.getProsetBySubSuitId(itemId);
            if (canCompoundEquip != null) {
                final List<StoreHouse> shList3 = this.storeHouseDao.getByItemId(playerDto.playerId, canCompoundEquip.getItemId(), 14);
                if (shList3 != null && shList3.size() > 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                }
            }
            if (item.getChangeItemId() > 0) {
                final Items changeItem = (Items)this.itemsCache.get((Object)item.getChangeItemId());
                if (changeItem != null) {
                    itemId = changeItem.getId();
                    itemType = changeItem.getType();
                    ecd = this.equipCache.getEquipCoordinateByItemId(changeItem.getId());
                    if (ecd != null) {
                        final List<StoreHouse> shList4 = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, 10);
                        if (shList4 != null && shList4.size() > 0) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                        }
                    }
                    final EquipProset proset1 = this.equipCache.getEquipProsetByItemId(changeItem.getId());
                    if (proset1 != null) {
                        final List<StoreHouse> shList5 = this.storeHouseDao.getByItemId(playerDto.playerId, changeItem.getId(), 14);
                        if (shList5 != null && shList5.size() > 0) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                        }
                    }
                    final EquipProset proset2 = this.equipCache.getProsetBySubSuitId(changeItem.getId());
                    if (proset2 != null) {
                        final List<StoreHouse> shList6 = this.storeHouseDao.getByItemId(playerDto.playerId, proset2.getItemId(), 14);
                        if (shList6 != null && shList6.size() > 0) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_20);
                        }
                    }
                }
                final List<StoreHouse> shList4 = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(itemType));
                if (shList4 != null && shList4.size() > 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_21);
                }
                final StoreHouseSell shs = this.storeHouseSellDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(itemType));
                if (shs != null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_21);
                }
            }
            else {
                item.getType();
                final List<StoreHouse> shList3 = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(itemType));
                if (shList3 != null && shList3.size() > 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_19);
                }
                final StoreHouseSell shs2 = this.storeHouseSellDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(itemType));
                if (shs2 != null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_19);
                }
            }
            final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
            final int currentNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
            if (currentNum >= pa.getMaxStoreNum()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_FULL);
            }
        }
        final int tickets = tm.getTickets() * num;
        if (!this.playerTicketsDao.consumeTickets(playerDto.playerId, tickets, LocalMessages.ATTRIBUTEKEY_TICKETS_3)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFGZ_15);
        }
        if (goodsName.equalsIgnoreCase("food")) {
            this.playerResourceDao.addFoodIgnoreMax(playerDto.playerId, goodsNum, "\u70b9\u5238\u5546\u57ce\u8d2d\u4e70\u83b7\u53d6\u7cae\u98df");
        }
        else if (goodsName.equalsIgnoreCase("iron")) {
            this.playerResourceDao.addIronIgnoreMax(playerDto.playerId, goodsNum, "\u70b9\u5238\u5546\u57ce\u8d2d\u4e70\u83b7\u53d6\u9554\u94c1", false);
        }
        else if (goodsName.equalsIgnoreCase("copper")) {
            this.playerResourceDao.addCopperIgnoreMax(playerDto.playerId, goodsNum, "\u70b9\u5238\u5546\u57ce\u8d2d\u4e70\u83b7\u53d6\u94f6\u5e01", true);
        }
        else if (goodsName.equalsIgnoreCase("gem")) {
            this.getStoreHouseService.gainGem(this.playerDao.read(playerDto.playerId), goodsNum, 1, LocalMessages.T_LOG_GEM_17, null);
        }
        else if (goodsName.equalsIgnoreCase("wood")) {
            this.playerResourceDao.addWoodIgnoreMax(playerDto.playerId, goodsNum, "\u70b9\u5238\u5546\u57ce\u8d2d\u4e70\u83b7\u53d6\u6728\u6750", true);
        }
        else if (goodsName.equalsIgnoreCase("item")) {
            this.getStoreHouseService.gainItems(playerDto.playerId, 1, rewardNum, LocalMessages.T_LOG_ITEM_10);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private String getGoodsName(final String reward) {
        return reward.split(":")[0];
    }
    
    private int getGoodsNumOrItemId(final String reward) {
        return Integer.valueOf(reward.split(":")[1]);
    }
}
