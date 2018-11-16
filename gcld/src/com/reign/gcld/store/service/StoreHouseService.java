package com.reign.gcld.store.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.chat.service.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.util.*;
import org.mybatis.spring.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;

@Component("storeHouseService")
public class StoreHouseService implements IStoreHouseService
{
    private static final DayReportLogger logger;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IChatService chatService;
    
    static {
        logger = new DayReportLogger();
    }
    
    public static int getStoreHouseType(final int itemType) {
        int type = 0;
        if (itemType == 1) {
            type = 4;
        }
        else if (itemType == 2) {
            type = 5;
        }
        else if (itemType == 3) {
            type = 6;
        }
        else if (itemType == 4) {
            type = 7;
        }
        else if (itemType == 8) {
            type = 8;
        }
        else if (itemType == 6) {
            type = 9;
        }
        else if (itemType == 9) {
            type = 11;
        }
        else if (itemType == 10) {
            type = 12;
        }
        else if (itemType == 11) {
            type = 13;
        }
        else if (itemType == 12) {
            type = 15;
        }
        else if (itemType == 14) {
            type = 16;
        }
        else if (itemType == 13) {
            type = 17;
        }
        else if (itemType == 15) {
            type = 18;
        }
        else if (itemType == 16) {
            type = 19;
        }
        else if (itemType == 17) {
            type = 20;
        }
        else if (itemType == 18) {
            type = 21;
        }
        else if (itemType == 19) {
            type = 22;
        }
        return type;
    }
    
    @Transactional
    @Override
    public void gainItems(final int playerId, final int num, final int id, final String reason) {
        this.gainItems(playerId, num, id, reason, false);
    }
    
    @Transactional
    @Override
    public void gainItems(final int playerId, final int num, final int id, final String reason, final boolean mustGain) {
        synchronized (this) {
            final Items item = (Items)this.itemsCache.get((Object)id);
            final int type = getStoreHouseType(item.getType());
            if (type == 0) {
                // monitorexit(this)
                return;
            }
            int quality = 0;
            if (item.getQuality() != null) {
                quality = item.getQuality();
            }
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            int maxNum = 0;
            if (pa != null) {
                maxNum = pa.getMaxStoreNum();
            }
            final int currentNum = this.storeHouseDao.getCountByPlayerId(playerId);
            List<StoreHouse> shList = null;
            if (type != 7 && type != 9 && type != 13) {
                shList = this.storeHouseDao.getByItemId(playerId, item.getId(), type);
            }
            final Player player = this.playerDao.read(playerId);
            final PlayerDto playerDto = new PlayerDto();
            playerDto.playerId = player.getPlayerId();
            playerDto.playerLv = player.getPlayerLv();
            playerDto.playerName = player.getPlayerName();
            playerDto.forceId = player.getForceId();
            final StoreHouse storeHouse = (shList == null || shList.size() <= 0) ? null : shList.get(0);
            if (storeHouse != null) {
                if (item.getChangeItemId() > 0 && storeHouse.getNum() + num >= item.getChangeNum()) {
                    if (storeHouse.getNum() + num == item.getChangeNum()) {
                        this.storeHouseDao.deleteById(storeHouse.getVId());
                    }
                    else {
                        this.storeHouseDao.reduceNum(storeHouse.getVId(), item.getChangeNum());
                    }
                    final Items changeItem = (Items)this.itemsCache.get((Object)item.getChangeItemId());
                    final String msg = MessageFormatter.format(LocalMessages.CHANGE_SUIT_INFO, new Object[] { ColorUtil.getGreenMsg(player.getPlayerName()), ColorUtil.getGreenMsg(changeItem.getName()) });
                    this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
                    final StoreHouse sh = new StoreHouse();
                    sh.setType(getStoreHouseType(changeItem.getType()));
                    sh.setGoodsType(getStoreHouseType(changeItem.getType()));
                    sh.setItemId(changeItem.getId());
                    sh.setLv(0);
                    sh.setPlayerId(playerId);
                    sh.setOwner(0);
                    sh.setQuality(quality);
                    sh.setGemId(0);
                    sh.setAttribute("0");
                    sh.setNum(num);
                    sh.setState(0);
                    sh.setRefreshAttribute("");
                    sh.setQuenchingTimes(0);
                    sh.setBindExpireTime(0L);
                    sh.setMarkId(0);
                    this.storeHouseDao.create(sh);
                }
                else {
                    this.storeHouseDao.addNum(storeHouse.getVId(), num);
                }
                StoreHouseService.logger.info(LogUtil.formatItemsLog(player, "+", "\u83b7\u5f97", true, (Items)this.itemsCache.get((Object)id), storeHouse, num, reason));
            }
            else {
                boolean flag = false;
                if ((item.getType() == 2 && item.getId() != 401) || item.getType() == 3 || item.getType() == 4 || mustGain) {
                    flag = true;
                }
                else if (currentNum < maxNum) {
                    flag = true;
                }
                if (flag) {
                    final StoreHouse sh2 = new StoreHouse();
                    sh2.setType(type);
                    sh2.setGoodsType(type);
                    sh2.setItemId(item.getId());
                    sh2.setLv(0);
                    sh2.setPlayerId(playerId);
                    sh2.setOwner(0);
                    sh2.setQuality(quality);
                    sh2.setGemId(0);
                    sh2.setAttribute("0");
                    sh2.setNum(num);
                    sh2.setState(0);
                    sh2.setRefreshAttribute("");
                    sh2.setQuenchingTimes(0);
                    sh2.setBindExpireTime(0L);
                    sh2.setMarkId(0);
                    this.storeHouseDao.create(sh2);
                    StoreHouseService.logger.info(LogUtil.formatItemsLog(player, "+", "\u83b7\u5f97", true, (Items)this.itemsCache.get((Object)id), sh2, num, reason));
                }
                else {
                    final StoreHouseSell shs = new StoreHouseSell();
                    shs.setType(type);
                    shs.setGoodsType(type);
                    shs.setItemId(item.getId());
                    shs.setLv(0);
                    shs.setPlayerId(playerId);
                    shs.setQuality(quality);
                    shs.setGemId(0);
                    shs.setAttribute("0");
                    shs.setSellTime(new Date());
                    shs.setNum(num);
                    shs.setRefreshAttribute("");
                    shs.setQuenchingTimes(0);
                    this.storeHouseSellDao.create(shs);
                    final int copper = item.getCopper() * num;
                    this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u4ed3\u5e93\u6ee1\u653e\u5165\u56de\u8d2d\u589e\u52a0\u94f6\u5e01,\u7269\u54c1\u6765\u6e90:" + reason, true);
                    StoreHouseService.logger.info(LogUtil.formatItemsLog2(player, "-", "\u83b7\u5f97\u5e76\u8fdb\u5165\u56de\u8d2d", true, (Items)this.itemsCache.get((Object)id), shs, num, reason));
                }
            }
        }
    }
    
    @Transactional
    @Override
    public void gainSearchItems(final int id, final int num, final PlayerDto playerDto, final String reason) {
        final int playerId = playerDto.playerId;
        final Items item = (Items)this.itemsCache.get((Object)id);
        if (item == null) {
            return;
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        int goodsType = 0;
        final String[] s = item.getEffect().split(",");
        int attribute;
        if (item.getEffect().contains("generalExp")) {
            goodsType = 1;
            attribute = Integer.valueOf(s[1]);
        }
        else {
            if (item.getEffect().contains("lea")) {
                goodsType = 2;
            }
            else if (item.getEffect().contains("str")) {
                goodsType = 3;
            }
            else if (item.getEffect().contains("int")) {
                goodsType = 4;
            }
            else if (item.getEffect().contains("pol")) {
                goodsType = 5;
            }
            final int first = Integer.valueOf(s[1]);
            final int second = Integer.valueOf(s[2]);
            attribute = ((first == second) ? first : (first + WebUtil.nextInt(second - first)));
        }
        final int currentNum = this.storeHouseDao.getCountByPlayerId(playerId);
        List<StoreHouse> shList = null;
        StoreHouse storeHouse = null;
        if (goodsType == 1) {
            try {
                shList = this.storeHouseDao.getByItemId(playerId, id, 4);
                storeHouse = ((shList == null || shList.size() <= 0) ? null : shList.get(0));
            }
            catch (MyBatisSystemException e) {
                final List<StoreHouse> expList = this.storeHouseDao.getByType(playerId, 4);
                int addNum = 0;
                for (final StoreHouse sHouse : expList) {
                    if (sHouse.getItemId() == id && addNum == 0) {
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
        }
        if (goodsType == 1 && storeHouse != null) {
            this.storeHouseDao.addNum(storeHouse.getVId(), num);
            StoreHouseService.logger.info(LogUtil.formatItemsLog(this.playerDao.read(playerId), "+", "\u83b7\u5f97", true, (Items)this.itemsCache.get((Object)id), storeHouse, num, reason));
        }
        else {
            boolean flag = false;
            if (item.getType() == 2 || item.getType() == 3 || item.getType() == 4) {
                flag = true;
            }
            else if (currentNum < maxNum) {
                flag = true;
            }
            if (flag) {
                final StoreHouse sh = new StoreHouse();
                sh.setType(4);
                sh.setGoodsType(goodsType);
                sh.setItemId(id);
                sh.setNum(num);
                sh.setPlayerId(playerId);
                sh.setOwner(0);
                sh.setLv(0);
                sh.setQuality(0);
                sh.setState(0);
                sh.setAttribute(String.valueOf(attribute));
                sh.setRefreshAttribute("");
                sh.setQuenchingTimes(0);
                sh.setBindExpireTime(0L);
                sh.setMarkId(0);
                this.storeHouseDao.create(sh);
                StoreHouseService.logger.info(LogUtil.formatItemsLog(this.playerDao.read(playerId), "+", "\u83b7\u5f97", true, (Items)this.itemsCache.get((Object)id), sh, num, reason));
            }
            else {
                final StoreHouseSell shs = new StoreHouseSell();
                shs.setType(4);
                shs.setGoodsType(goodsType);
                shs.setItemId(id);
                shs.setNum(num);
                shs.setPlayerId(playerId);
                shs.setLv(0);
                shs.setQuality(0);
                shs.setAttribute(String.valueOf(attribute));
                shs.setSellTime(new Date());
                shs.setRefreshAttribute("");
                shs.setQuenchingTimes(0);
                this.storeHouseSellDao.create(shs);
                final int copper = item.getCopper() * num;
                this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u4ed3\u5e93\u6ee1\u653e\u5165\u56de\u8d2d\u589e\u52a0\u94f6\u5e01,\u7269\u54c1\u6765\u6e90:" + item.getName(), true);
                StoreHouseService.logger.info(LogUtil.formatItemsLog2(this.playerDao.read(playerId), "-", "\u83b7\u5f97\u5e76\u8fdb\u5165\u56de\u8d2d", true, (Items)this.itemsCache.get((Object)id), shs, num, reason));
            }
        }
    }
    
    @Transactional
    @Override
    public void gainGem(final Player player, final int num, final int id, final String reason, final String refreshAttribute) {
        final int playerId = player.getPlayerId();
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)id);
        if (armsGem == null) {
            return;
        }
        final boolean isJs = id > 15;
        synchronized (this) {
            boolean create = true;
            if (!isJs) {
                final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, armsGem.getId(), 2);
                if (shList != null && shList.size() > 0) {
                    this.storeHouseDao.addNum(shList.get(0).getVId(), num);
                    create = false;
                }
            }
            if (create) {
                final int goosType = isJs ? 1 : 2;
                final StoreHouse sh = new StoreHouse();
                sh.setType(2);
                sh.setGoodsType(goosType);
                sh.setItemId(armsGem.getId());
                sh.setLv(armsGem.getGemLv());
                sh.setPlayerId(playerId);
                sh.setOwner(0);
                sh.setQuality(armsGem.getGemLv());
                sh.setGemId(0);
                sh.setAttribute("0");
                sh.setNum(num);
                sh.setState(0);
                if (StringUtils.isNotBlank(refreshAttribute)) {
                    sh.setRefreshAttribute(refreshAttribute);
                }
                else {
                    sh.setRefreshAttribute("");
                }
                sh.setQuenchingTimes(0);
                sh.setBindExpireTime(0L);
                sh.setMarkId(0);
                this.storeHouseDao.create(sh);
            }
        }
        StoreHouseService.logger.info(LogUtil.formatGemLog(player, "+", "\u83b7\u5f97", true, armsGem, num, reason));
    }
}
