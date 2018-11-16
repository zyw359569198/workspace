package com.reign.gcld.diamondshop.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.diamondshop.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.diamondshop.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.log.*;
import java.util.*;

@Component("diamondShopService")
public class DiamondShopService implements IDiamondShopService
{
    @Autowired
    private HmGtMainCache hmGtMainCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerDiamondShopDao playerDiamondShopDao;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    private static final Logger timerLog;
    
    static {
        timerLog = new TimerLogger();
    }
    
    @Override
    public byte[] getInfo(final PlayerDto playerDto) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[68] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NOT_OPEN);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int maxLv = this.hmGtMainCache.getCacheMap().size();
        int curLv = 0;
        final PlayerDiamondShop pds = this.playerDiamondShopDao.getMaxShop(playerDto.playerId);
        if (pds != null) {
            curLv = pds.getShopId();
        }
        final int nextLv = curLv + 1;
        boolean hasLink = false;
        boolean hasUpgradeButton = false;
        if (curLv < maxLv) {
            final Items item = this.itemsCache.getItemsByTypeAndIndex(18, nextLv);
            if (item == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_SUCH_DRAWING);
            }
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, item.getId(), StoreHouseService.getStoreHouseType(item.getType()));
            if (shList == null || shList.size() <= 0) {
                hasLink = true;
            }
            else {
                hasUpgradeButton = true;
            }
        }
        doc.createElement("hasLink", hasLink);
        doc.createElement("hasUpgradeButton", hasUpgradeButton);
        doc.createElement("curShopId", curLv);
        doc.startArray("shopInfoList");
        for (int i = 0; i < maxLv; ++i) {
            doc.startObject();
            boolean isEnough = true;
            final int shopId = i + 1;
            final HmGtMain hmGtMain = (HmGtMain)this.hmGtMainCache.get((Object)shopId);
            if (hmGtMain == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_SUCH_SHOP);
            }
            doc.createElement("shopId", shopId);
            doc.createElement("shopName", hmGtMain.getName());
            final PlayerDiamondShop temp = this.playerDiamondShopDao.getByShopId(playerDto.playerId, shopId);
            if (temp == null) {
                doc.createElement("isOpen", false);
                doc.endObject();
            }
            else {
                doc.createElement("isOpen", true);
                doc.createElement("rTimes", temp.getRTimes());
                final int lv = temp.getLv();
                boolean isTopLv = false;
                if (lv >= 1) {
                    isTopLv = true;
                }
                final int baseGem = hmGtMain.getGem();
                final int extraGem = temp.getLv() * hmGtMain.getExtraGem();
                doc.createElement("curLv", lv);
                doc.createElement("isTopLv", isTopLv);
                doc.createElement("cunsumeType", shopId);
                final int cunsumeAmount = (hmGtMain.getIron() > 0) ? hmGtMain.getIron() : hmGtMain.getNum();
                final int itemId = hmGtMain.getItems();
                if (itemId > 0) {
                    final Items item2 = (Items)this.itemsCache.get((Object)itemId);
                    final List<StoreHouse> shList2 = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(item2.getType()));
                    if (shList2 == null || shList2.size() <= 0 || cunsumeAmount > shList2.get(0).getNum()) {
                        isEnough = false;
                    }
                }
                final PlayerResource pr = this.playerResourceDao.read(playerDto.playerId);
                if (hmGtMain.getIron() > 0 && pr.getIron() < hmGtMain.getIron()) {
                    isEnough = false;
                }
                doc.createElement("cunsumeAmount", cunsumeAmount);
                doc.createElement("cunsumeTips", hmGtMain.getItemIntro());
                doc.createElement("traderTips", hmGtMain.getTraderIntro());
                doc.createElement("gainAmount", baseGem + extraGem);
                doc.createElement("upgradeTipsGold", hmGtMain.getUpGold());
                doc.createElement("upgradeTipsGem", hmGtMain.getExtraGem());
                doc.createElement("isEnough", isEnough);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] addNewShop(final PlayerDto playerDto) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[68] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NOT_OPEN);
        }
        final int maxLv = this.hmGtMainCache.getCacheMap().size();
        final PlayerDiamondShop pds = this.playerDiamondShopDao.getMaxShop(playerDto.playerId);
        if (pds != null && pds.getShopId() >= maxLv) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_HIGHEST_LV);
        }
        int nextLv = 1;
        if (pds != null) {
            nextLv = pds.getShopId() + 1;
        }
        final Items item = this.itemsCache.getItemsByTypeAndIndex(18, nextLv);
        if (item == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_SUCH_DRAWING);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, item.getId(), StoreHouseService.getStoreHouseType(item.getType()));
        if (shList == null || shList.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_DRAWING);
        }
        final PlayerDiamondShop temp = new PlayerDiamondShop();
        temp.setLv(0);
        temp.setPlayerId(playerDto.playerId);
        final int dMax = ((HmGtMain)this.hmGtMainCache.get((Object)nextLv)).getDMax();
        temp.setRTimes(dMax);
        temp.setShopId(nextLv);
        this.playerDiamondShopDao.create(temp);
        this.dataGetter.getStoreHouseDao().deleteById(shList.get(0).getVId());
        if (nextLv >= 1) {
            CityEventManager.getInstance().removePlayerEventAfterDiamondShopBuild(playerDto.playerId);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("newLv", nextLv);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] upgradeShop(final PlayerDto playerDto, final int shopId) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[68] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NOT_OPEN);
        }
        final PlayerDiamondShop pds = this.playerDiamondShopDao.getByShopId(playerDto.playerId, shopId);
        if (pds == null || pds.getLv() >= 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_UPGRADE_ERROR_1);
        }
        final HmGtMain hmGtMain = (HmGtMain)this.hmGtMainCache.get((Object)shopId);
        if (hmGtMain == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_SUCH_SHOP);
        }
        final int cost = hmGtMain.getUpGold();
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (!this.dataGetter.getPlayerDao().consumeGold(player, cost, "\u8d2d\u4e70\u5b9d\u77f3\u5546\u4f1a\u56fe\u7eb8\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerDiamondShopDao.updateDiamondShopLv(1, playerDto.playerId, shopId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] exchange(final PlayerDto playerDto, final int shopId) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[68] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NOT_OPEN);
        }
        final PlayerDiamondShop pds = this.playerDiamondShopDao.getByShopId(playerDto.playerId, shopId);
        if (pds == null || pds.getRTimes() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_EXECHANGE_ERROR_1);
        }
        final HmGtMain hmGtMain = (HmGtMain)this.hmGtMainCache.get((Object)shopId);
        if (hmGtMain == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DIAMOND_SHOP_NO_SUCH_SHOP);
        }
        final Player player = this.playerDao.read(playerDto.playerId);
        final int iron = hmGtMain.getIron();
        final int itemId = hmGtMain.getItems();
        final int num = hmGtMain.getNum();
        final int baseGem = hmGtMain.getGem();
        final int extraGem = pds.getLv() * hmGtMain.getExtraGem();
        if (iron > 0 && !this.playerResourceDao.consumeIron(playerDto.playerId, iron, "\u5b9d\u77f3\u5546\u4f1a\u5151\u6362\u5b9d\u77f3\u6d88\u8017\u9554\u94c1")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
        }
        if (itemId > 0) {
            final Items item = (Items)this.itemsCache.get((Object)itemId);
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, itemId, StoreHouseService.getStoreHouseType(item.getType()));
            if (shList == null || shList.size() <= 0 || num > shList.get(0).getNum()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10049);
            }
            final StoreHouse sh = shList.get(0);
            if (sh.getNum() - num <= 0) {
                this.storeHouseDao.deleteById(sh.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(sh.getVId(), num);
            }
        }
        this.dataGetter.getStoreHouseService().gainGem(player, baseGem + extraGem, 1, LocalMessages.T_LOG_GEM_20, null);
        this.playerDiamondShopDao.reduceDailyTimes(playerDto.playerId, shopId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("diamondNum", baseGem + extraGem);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void dropProps(final int playerId, final int itemId, final int num) {
        try {
            if (!this.canRecvDropProps(playerId, itemId)) {
                return;
            }
            final Items item = (Items)this.itemsCache.get((Object)itemId);
            final List<StoreHouse> shList = this.dataGetter.getStoreHouseDao().getByItemId(playerId, itemId, StoreHouseService.getStoreHouseType(item.getType()));
            if (shList != null && !shList.isEmpty()) {
                this.dataGetter.getStoreHouseService().gainItems(playerId, num, itemId, String.valueOf(LocalMessages.T_LOG_GEM_PREFIX) + item.getName());
                return;
            }
            final int usedStoreNum = this.storeHouseDao.getCountByPlayerId(playerId);
            final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerId);
            final int maxStoreNum = pa.getMaxStoreNum();
            if (usedStoreNum >= maxStoreNum) {
                final StoreHouseSell storeHouseSell = new StoreHouseSell();
                storeHouseSell.setAttribute("0");
                storeHouseSell.setGemId(0);
                storeHouseSell.setType(StoreHouseService.getStoreHouseType(item.getType()));
                storeHouseSell.setGoodsType(StoreHouseService.getStoreHouseType(item.getType()));
                storeHouseSell.setItemId(itemId);
                storeHouseSell.setLv(0);
                storeHouseSell.setNum(num);
                storeHouseSell.setPlayerId(playerId);
                storeHouseSell.setQuality(item.getQuality());
                storeHouseSell.setSellTime(new Date());
                storeHouseSell.setRefreshAttribute("");
                storeHouseSell.setQuenchingTimes(0);
                storeHouseSell.setQuenchingTimesFree(0);
                storeHouseSell.setSpecialSkillId(0);
                this.dataGetter.getStoreHouseSellDao().create(storeHouseSell);
                final int cooper = item.getCopper();
                if (cooper > 0) {
                    this.playerResourceDao.addCopperIgnoreMax(playerId, cooper * num, "\u9053\u5177\u6389\u843d\u56de\u8d2d\u589e\u52a0\u94f6\u5e01", true);
                }
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.DIAMOND_SHOP_DROP_PROPS_TITLE, LocalMessages.DIAMOND_SHOP_DROP_PROPS_CONTENT, 1, playerId, new Date());
            }
            else {
                this.dataGetter.getStoreHouseService().gainItems(playerId, num, itemId, String.valueOf(LocalMessages.T_LOG_GEM_PREFIX) + item.getName());
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    @Override
    public void resetRTimes() {
        final long start = System.currentTimeMillis();
        for (final HmGtMain hmGtMain : this.hmGtMainCache.getCacheMap().values()) {
            this.playerDiamondShopDao.resetRTimes(hmGtMain.getLv(), hmGtMain.getDMax());
        }
        DiamondShopService.timerLog.info(LogUtil.formatThreadLog("DiamondShopService", "resetRTimes", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public boolean canRecvDropProps(final int playerId, final int itemId) {
        try {
            int curLv = 0;
            final PlayerDiamondShop pds = this.playerDiamondShopDao.getMaxShop(playerId);
            if (pds != null) {
                curLv = pds.getShopId();
            }
            final Items item = (Items)this.itemsCache.get((Object)itemId);
            if (item == null) {
                return false;
            }
            final int targetLv = item.getIndex() + 1;
            return curLv >= targetLv;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return false;
        }
    }
}
