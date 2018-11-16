package com.reign.gcld.blacksmith.service;

import org.springframework.stereotype.*;
import com.reign.gcld.blacksmith.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.blacksmith.domain.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.player.domain.*;
import org.apache.commons.lang.*;
import java.util.*;

@Component("blacksmithService")
public class BlacksmithService implements IBlacksmithService
{
    private static final DayReportLogger logger;
    private Logger errorLog;
    @Autowired
    private IPlayerBlacksmithDao playerBlacksmithDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private HmBsMainCache hmBsMainCache;
    @Autowired
    private HmBsGoldCache hmBsGoldCache;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ItemsCache itemsCache;
    private static Map<Integer, Integer> lvItemIdMap;
    private static final int MAX_BLACKSMITH_LV = 5;
    
    static {
        logger = new DayReportLogger();
        (BlacksmithService.lvItemIdMap = new HashMap<Integer, Integer>()).put(1, 1201);
        BlacksmithService.lvItemIdMap.put(2, 1202);
        BlacksmithService.lvItemIdMap.put(3, 1203);
        BlacksmithService.lvItemIdMap.put(4, 1204);
        BlacksmithService.lvItemIdMap.put(5, 1205);
    }
    
    public BlacksmithService() {
        this.errorLog = CommonLog.getLog(BlacksmithService.class);
    }
    
    @Override
    public byte[] getBlacksmithInfo(final PlayerDto playerDto) {
        if (playerDto.cs[66] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final List<PlayerBlacksmith> bsList = this.playerBlacksmithDao.getListByPlayerId(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (bsList == null || bsList.size() < 1) {
            doc.createElement("type", 2);
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, BlacksmithService.lvItemIdMap.get(1), 15);
            doc.createElement("havePic", (shList != null && shList.size() > 0 && shList.get(0).getNum() > 0) ? 1 : 0);
        }
        else {
            doc.createElement("type", 1);
            final int lv = bsList.size();
            final boolean canUpdate = this.canUpdate(bsList.size(), playerDto.playerLv);
            doc.createElement("canUpdate", canUpdate);
            if (canUpdate) {
                final List<StoreHouse> shList2 = this.storeHouseDao.getByItemId(playerId, BlacksmithService.lvItemIdMap.get(lv + 1), 15);
                doc.createElement("havePic", (shList2 != null && shList2.size() > 0 && shList2.get(0).getNum() > 0) ? 1 : 0);
            }
            doc.createElement("lv", lv);
            doc.startArray("blacksmiths");
            for (final PlayerBlacksmith bs : bsList) {
                doc.startObject();
                final int smithId = bs.getSmithId();
                doc.createElement("smithId", smithId);
                doc.createElement("remainNum", this.hmBsMainCache.getDMax(smithId) - bs.getNum());
                final HmBsGold hbg = this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv());
                doc.createElement("gold", (hbg == null) ? 0 : hbg.getUpGold());
                final boolean canUpdate2 = bs.getLv() < this.hmBsGoldCache.getMaxLv(smithId);
                doc.createElement("canUpdate", canUpdate2);
                doc.createElement("smithLv", bs.getLv());
                int extraIron = 0;
                if (canUpdate2) {
                    extraIron = this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv() + 1).getExtra() - this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv()).getExtra();
                }
                doc.createElement("extraIron", extraIron);
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getSmithInfo(final PlayerDto playerDto, final int smithId) {
        if (smithId < 1 || smithId > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[66] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final PlayerBlacksmith bs = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, smithId);
        if (bs == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_SMITH);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final HmBsGold hbg = this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv());
        if (1 == smithId) {
            final HmBsMain hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
            doc.createElement("num", hbm.getNum());
            doc.createElement("output", hbm.getOutput() + hbg.getExtra());
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, 1401, 16);
            doc.createElement("enough", shList != null && shList.size() > 0 && shList.get(0).getNum() >= 1);
            doc.createElement("haveNum", (shList == null || shList.size() <= 0) ? 0 : shList.get(0).getNum());
        }
        else if (2 == smithId) {
            final List<StoreHouse> shList2 = this.storeHouseDao.getGeneralTreasureByType(playerId, 2);
            doc.startArray("generalTreasures");
            String[] strs = null;
            GeneralTreasure gt = null;
            for (final StoreHouse sh : shList2) {
                if (sh.getOwner() > 0) {
                    continue;
                }
                doc.startObject();
                doc.createElement("vId", sh.getVId());
                gt = (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId());
                doc.createElement("name", gt.getName());
                doc.createElement("type", sh.getGoodsType());
                strs = sh.getAttribute().split(",");
                doc.createElement("att1", strs[0]);
                doc.createElement("att2", strs[1]);
                doc.createElement("pic", gt.getPic());
                doc.createElement("quality", gt.getQuality());
                final HmBsMain hbm2 = this.hmBsMainCache.getHmBsMain(smithId, Integer.parseInt(strs[0]) + Integer.parseInt(strs[1]));
                doc.createElement("num", hbm2.getNum());
                doc.createElement("output", hbm2.getOutput() + hbg.getExtra());
                doc.endObject();
            }
            doc.endArray();
        }
        else if (3 == smithId) {
            final List<StoreHouse> shList2 = this.storeHouseDao.getNoOwnerByType(playerId, 1);
            doc.startArray("equipList");
            for (final StoreHouse sh2 : shList2) {
                if (this.equipCache.getJinpinEquips().contains(sh2.getItemId()) && this.is_3_Length(sh2.getRefreshAttribute()) && !this.is_5(sh2.getRefreshAttribute())) {
                    final HmBsMain hbm3 = this.hmBsMainCache.getHmBsMain(smithId, this.getQuality(sh2.getRefreshAttribute()));
                    if (hbm3 == null) {
                        this.errorLog.error("class:BlacksmithService#method:getSmithInfo#vId:" + sh2.getVId() + "#itemId:" + sh2.getItemId() + "#playerId:" + sh2.getPlayerId() + "#refreshAttribute:" + sh2.getRefreshAttribute());
                    }
                    else {
                        doc.startObject();
                        doc.createElement("type", sh2.getGoodsType());
                        doc.createElement("vId", sh2.getVId());
                        doc.createElement("lv", sh2.getLv());
                        final Equip equip = (Equip)this.equipCache.get((Object)sh2.getItemId());
                        doc.createElement("attribute", sh2.getAttribute());
                        doc.createElement("quality", equip.getQuality());
                        doc.createElement("itemName", equip.getName());
                        EquipCommon.getRefreshAttribute(sh2.getRefreshAttribute(), doc, this.equipSkillCache, null, this.equipSkillEffectCache, equip);
                        EquipCommon.getMaxSkillAndLv(doc, equip, this.equipCache, sh2.getSpecialSkillId(), sh2.getRefreshAttribute());
                        doc.createElement("pic", equip.getPic());
                        doc.createElement("num", hbm3.getNum());
                        doc.createElement("output", hbm3.getOutput() + hbg.getExtra());
                        doc.createElement("tips", this.is_refresh(sh2.getRefreshAttribute()));
                        doc.endObject();
                    }
                }
            }
            doc.endArray();
        }
        else if (4 == smithId) {
            final HmBsMain hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
            doc.createElement("num", hbm.getNum());
            doc.createElement("output", hbm.getOutput() + hbg.getExtra());
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, 1402, 16);
            doc.createElement("enough", shList != null && shList.size() > 0 && shList.get(0).getNum() >= 1);
            doc.createElement("haveNum", (shList == null || shList.size() <= 0) ? 0 : shList.get(0).getNum());
        }
        else if (5 == smithId) {
            final HmBsMain hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
            doc.createElement("num", hbm.getNum());
            doc.createElement("output", hbm.getOutput() + hbg.getExtra());
            final int copper = hbm.getNum();
            final PlayerResource pr = this.playerResourceDao.read(playerId);
            doc.createElement("enough", pr.getCopper() >= copper);
            doc.createElement("haveNum", pr.getCopper());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] dissolve(final PlayerDto playerDto, final int smithId, final int vId) {
        if (smithId < 1 || smithId > 5 || ((smithId == 2 || smithId == 3) && vId <= 0)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[66] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final PlayerBlacksmith bs = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, smithId);
        if (bs == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_SMITH);
        }
        final int remainNum = this.hmBsMainCache.getDMax(smithId) - bs.getNum();
        if (remainNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_SMITH_NUM);
        }
        HmBsMain hbm = null;
        StoreHouse sh = null;
        int output = 0;
        if (1 == smithId) {
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, 1401, 16);
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_EQUIP_STONE);
            }
            sh = shList.get(0);
            hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
        }
        else if (2 == smithId) {
            sh = this.storeHouseDao.read(vId);
            if (sh == null || sh.getType() != 3 || sh.getOwner() > 0 || sh.getPlayerId() != playerId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_TREASURE);
            }
            final String[] strs = sh.getAttribute().split(",");
            hbm = this.hmBsMainCache.getHmBsMain(smithId, Integer.parseInt(strs[0]) + Integer.parseInt(strs[1]));
        }
        else if (3 == smithId) {
            sh = this.storeHouseDao.read(vId);
            if (sh == null || sh.getType() != 1 || sh.getOwner() > 0 || sh.getPlayerId() != playerId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_EQUIP);
            }
            if (!this.equipCache.getJinpinEquips().contains(sh.getItemId()) || !this.is_3_Length(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_EQUIP_3_STAR);
            }
            if (this.is_5(sh.getRefreshAttribute())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_EQUIP_ALL_5);
            }
            hbm = this.hmBsMainCache.getHmBsMain(smithId, this.getQuality(sh.getRefreshAttribute()));
        }
        else if (4 == smithId) {
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, 1402, 16);
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_EQUIP_STONE_SRC);
            }
            sh = shList.get(0);
            hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
        }
        else if (5 == smithId) {
            hbm = this.hmBsMainCache.getHmBsMain(smithId, 0);
            final int copper = hbm.getNum();
            final PlayerResource pr = this.playerResourceDao.read(playerId);
            if (copper > pr.getCopper()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
            }
        }
        final HmBsGold hbg = this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv());
        output = hbm.getOutput() + hbg.getExtra();
        this.playerBlacksmithDao.useSmithNum(playerId, smithId);
        if (1 == smithId) {
            if (sh.getNum() <= 1) {
                this.storeHouseDao.deleteById(sh.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(sh.getVId(), 1);
            }
            BlacksmithService.logger.info(LogUtil.formatItemsLog(this.playerDao.read(playerId), "-", "\u4f7f\u7528", true, (Items)this.itemsCache.get((Object)sh.getItemId()), sh, 1, "\u94c1\u5320\u94fa\u6eb6\u89e3\u6d88\u8017\u7384\u94c1\u77f3"));
        }
        else if (2 == smithId) {
            this.storeHouseDao.deleteById(vId);
            BlacksmithService.logger.info(LogUtil.formatGeneralTreasureLog(this.playerDao.read(playerId), "-", "\u4f7f\u7528", (GeneralTreasure)this.generalTreasureCache.get((Object)sh.getItemId()), LocalMessages.T_LOG_GENERAL_TREASURE_6));
        }
        else if (3 == smithId) {
            this.storeHouseDao.deleteById(vId);
            BlacksmithService.logger.info(LogUtil.formatEquipLog(this.playerDao.read(playerId), "-", "\u4f7f\u7528", true, (Equip)this.equipCache.get((Object)sh.getItemId()), sh, LocalMessages.T_LOG_EQUIP_7));
        }
        else if (4 == smithId) {
            if (sh.getNum() <= 1) {
                this.storeHouseDao.deleteById(sh.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(sh.getVId(), 1);
            }
            BlacksmithService.logger.info(LogUtil.formatItemsLog(this.playerDao.read(playerId), "-", "\u4f7f\u7528", true, (Items)this.itemsCache.get((Object)sh.getItemId()), sh, 1, "\u94c1\u5320\u94fa\u6eb6\u89e3\u6d88\u8017\u7384\u94c1\u539f\u77f3"));
        }
        else if (5 == smithId) {
            final int copper2 = hbm.getNum();
            this.playerResourceDao.consumeCopper(playerId, copper2, "\u94c1\u5320\u94fa\u6eb6\u89e3\u6d88\u8017\u94f6\u5e01");
        }
        this.playerResourceDao.addIronIgnoreMax(playerId, output, "\u94c1\u5320\u94fa\u6eb6\u89e3\u83b7\u53d6\u9554\u94c1", true);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("output", output));
    }
    
    @Override
    public byte[] updateBlackSmith(final PlayerDto playerDto) {
        if (playerDto.cs[66] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final int size = this.playerBlacksmithDao.getSizeByPlayerId(playerId);
        if (size >= 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_MAX_BLACKSMITH_LV);
        }
        if (!this.canUpdate(size, playerDto.playerLv)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_PLAYER_LV_IS_LOWER);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, BlacksmithService.lvItemIdMap.get(size + 1), 15);
        if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_PIC);
        }
        final StoreHouse sh = shList.get(0);
        if (sh.getNum() <= 1) {
            this.storeHouseDao.deleteById(sh.getVId());
        }
        else {
            this.storeHouseDao.reduceNum(sh.getVId(), 1);
        }
        BlacksmithService.logger.info(LogUtil.formatItemsLog(this.playerDao.read(playerId), "-", "\u4f7f\u7528", true, (Items)this.itemsCache.get((Object)sh.getItemId()), sh, 1, "\u94c1\u5320\u94fa\u5347\u7ea7\u6d88\u8017\u56fe\u7eb8"));
        final PlayerBlacksmith bs = new PlayerBlacksmith();
        bs.setPlayerId(playerId);
        bs.setSmithId(size + 1);
        bs.setLv(1);
        bs.setNum(0);
        this.playerBlacksmithDao.create(bs);
        if (size + 1 == 4) {
            CityEventManager.getInstance().addPlayerEvent(playerId, 3);
            CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 3);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] updateSmith(final PlayerDto playerDto, final int smithId) {
        if (smithId < 1 || smithId > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[66] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final PlayerBlacksmith bs = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, smithId);
        if (bs == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_NO_SMITH);
        }
        if (bs.getLv() >= this.hmBsGoldCache.getMaxLv(smithId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BS_MAX_SMITH_LV);
        }
        final HmBsGold hbg = this.hmBsGoldCache.getHmBsGold(smithId, bs.getLv());
        final Player player = this.playerDao.read(playerId);
        if (!this.playerDao.canConsumeMoney(player, hbg.getUpGold())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerDao.consumeGold(player, hbg.getUpGold(), "\u5347\u7ea7\u94c1\u5320\u6d88\u8017\u91d1\u5e01");
        this.playerBlacksmithDao.addSmithLv(playerId, smithId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private boolean canUpdate(final int currentLv, final int playerLv) {
        if (currentLv == 0) {
            return playerLv >= 100;
        }
        if (1 == currentLv) {
            return playerLv >= 102;
        }
        if (2 == currentLv) {
            return playerLv >= 104;
        }
        if (3 == currentLv) {
            return playerLv >= 106;
        }
        return 4 == currentLv && playerLv >= 108;
    }
    
    private int getQuality(final String refreshAttribute) {
        if (StringUtils.isBlank(refreshAttribute)) {
            return 0;
        }
        final String[] arr = refreshAttribute.split(";");
        int result = 0;
        String[] array;
        for (int length = (array = arr).length, i = 0; i < length; ++i) {
            final String temp = array[i];
            final String[] att = temp.split(":");
            result += Integer.parseInt(att[1]);
        }
        return result;
    }
    
    @Override
    public boolean is_3_Length(final String refreshAttribute) {
        if (StringUtils.isBlank(refreshAttribute)) {
            return false;
        }
        final String[] arr = refreshAttribute.split(";");
        return arr.length >= 3;
    }
    
    @Override
    public boolean is_5(final String refreshAttribute) {
        if (StringUtils.isBlank(refreshAttribute)) {
            return false;
        }
        final String[] arr = refreshAttribute.split(";");
        if (arr.length < 3) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            final String[] temp = arr[i].split(":");
            if (Integer.parseInt(temp[1]) < 5) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int isSameSpecialSkillLv4(final String refreshAttribute) {
        if (StringUtils.isBlank(refreshAttribute)) {
            return 0;
        }
        final String[] arr = refreshAttribute.split(";");
        if (arr.length < 3) {
            return 0;
        }
        int result = 0;
        final Set<Integer> skillIdSet = new HashSet<Integer>();
        for (int i = 0; i < 3; ++i) {
            final String[] temp = arr[i].split(":");
            if (Integer.parseInt(temp[1]) < 4) {
                return 0;
            }
            result = Integer.parseInt(temp[0]);
            skillIdSet.add(result);
        }
        if (skillIdSet.size() == 1) {
            return result;
        }
        return 0;
    }
    
    private boolean is_refresh(final String refreshAttribute) {
        if (StringUtils.isBlank(refreshAttribute)) {
            return false;
        }
        final String[] arr = refreshAttribute.split(";");
        String[] array;
        for (int length = (array = arr).length, i = 0; i < length; ++i) {
            final String temp = array[i];
            final String[] targetArr = temp.split(":");
            if (Integer.parseInt(targetArr[1]) > 1) {
                return true;
            }
        }
        return false;
    }
}
