package com.reign.gcld.weapon.service;

import org.springframework.stereotype.*;
import com.reign.gcld.store.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.sdata.domain.*;

@Component("gemService")
public class GemService implements IGemService
{
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private ArmsJsSkillCache armsJsSkillCache;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger errorLog;
    private static final DayReportLogger logger;
    private static int SPECIAL_GEM_TECH;
    private static int JS_LEVEL_1_ID;
    
    static {
        errorLog = CommonLog.getLog(GemService.class);
        logger = new DayReportLogger();
        GemService.SPECIAL_GEM_TECH = 1102;
        GemService.JS_LEVEL_1_ID = 1001;
    }
    
    @Transactional
    @Override
    public byte[] polish(final PlayerDto playerDto, final int id) {
        if (id != 15) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, GemService.SPECIAL_GEM_TECH);
        if (pt == null || pt.getStatus() != 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_TECH);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, id, 2);
        if (shList == null || shList.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int num = this.storeHouseDao.getSetGemsDiamondsCount(playerId, 2, 1);
        if (num >= 6) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_TOO_MANY);
        }
        final StoreHouse sh = shList.get(0);
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)83);
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), ci.getCost(), "\u6253\u78e8\u5b9d\u77f3\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final ArmsGem gems = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)GemService.JS_LEVEL_1_ID);
        final int skillId = WebUtil.nextInt(ArmsJsSkillCache.SKILL_NUM) + 1;
        final String refreshAttribute = String.valueOf(skillId) + ":" + 1;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        if (1 == sh.getNum()) {
            this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndRefreshAttributeAndGoosType(sh.getVId(), GemService.JS_LEVEL_1_ID, gems.getGemLv(), refreshAttribute, 1);
            GemService.logger.info(LogUtil.formatGemLog(player, "-", "\u6253\u78e8", true, (ArmsGem)this.armsGemCache.get((Object)sh.getItemId()), 1, LocalMessages.T_LOG_GEM_25));
            GemService.logger.info(LogUtil.formatGemLog(player, "+", "\u6253\u78e8", true, (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)GemService.JS_LEVEL_1_ID), 1, LocalMessages.T_LOG_GEM_24));
        }
        else {
            this.dataGetter.getStoreHouseDao().reduceNum(sh.getVId(), 1);
            GemService.logger.info(LogUtil.formatGemLog(player, "-", "\u6253\u78e8", true, (ArmsGem)this.armsGemCache.get((Object)sh.getItemId()), 1, LocalMessages.T_LOG_GEM_25));
            this.dataGetter.getStoreHouseService().gainGem(player, 1, GemService.JS_LEVEL_1_ID, LocalMessages.T_LOG_GEM_24, refreshAttribute);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] gemUpgrade(final PlayerDto playerDto, final int id, final String ids) {
        if (id <= 0 || StringUtils.isBlank(ids)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_CHOICE_GEM);
        }
        final StoreHouse sh = this.storeHouseDao.read(id);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getType() != 2 || sh.getGoodsType() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_ONLY_JS_CAN_JJ);
        }
        final ArmsGem newGem = (ArmsGem)this.armsGemCache.get((Object)(sh.getItemId() + 1));
        if (newGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_JS_MAX_LV);
        }
        final String[] idArr = ids.split(",");
        if (idArr.length <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        String[] array;
        for (int length = (array = idArr).length, j = 0; j < length; ++j) {
            final String temp = array[j];
            try {
                final int vId = Integer.parseInt(temp);
                final Integer times = map.get(vId);
                if (times == null) {
                    map.put(vId, 1);
                }
                else {
                    map.put(vId, times + 1);
                }
            }
            catch (Exception e) {
                GemService.errorLog.error("class:GemService#method:gemUpgrade#playerId:" + playerDto.playerId + "#ids:" + ids);
            }
        }
        if (map.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        for (final Map.Entry<Integer, Integer> entry : map.entrySet()) {
            final int vId2 = entry.getKey();
            final StoreHouse storeHouse = this.dataGetter.getStoreHouseDao().read(vId2);
            if (storeHouse == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (storeHouse.getPlayerId() != playerDto.playerId) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (storeHouse.getType() != 2 || storeHouse.getGoodsType() != 2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_JJ_ONLY_USE_GEM);
            }
            final int num = entry.getValue();
            if (num > storeHouse.getNum()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        int haveNum = 0;
        for (final Map.Entry<Integer, Integer> entry2 : map.entrySet()) {
            final int vId = entry2.getKey();
            final int num2 = entry2.getValue();
            final StoreHouse storeHouse2 = this.dataGetter.getStoreHouseDao().read(vId);
            final ArmsGem ag = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)storeHouse2.getItemId());
            final int addNum = (int)Math.pow(2.0, ag.getGemLv() - 1) * num2;
            haveNum += addNum;
            if (storeHouse2.getNum() > num2) {
                this.dataGetter.getStoreHouseDao().reduceNum(storeHouse2.getVId(), num2);
            }
            else {
                this.dataGetter.getStoreHouseDao().deleteById(storeHouse2.getVId());
            }
            GemService.logger.info(LogUtil.formatGemLog(player, "-", "\u6676\u77f3\u8fdb\u9636", true, (ArmsGem)this.armsGemCache.get((Object)storeHouse2.getItemId()), num2, LocalMessages.T_LOG_GEM_26));
        }
        final List<Integer> openSkillList = new ArrayList<Integer>();
        final ArmsGem oldGem = (ArmsGem)this.armsGemCache.get((Object)sh.getItemId());
        final int needNum = oldGem.getUpgradeGem1() - Integer.parseInt(sh.getAttribute());
        final int beforeItemId = sh.getItemId();
        int afterItemId = 0;
        if (haveNum < needNum) {
            this.dataGetter.getStoreHouseDao().updateAttribute(sh.getVId(), String.valueOf(Integer.parseInt(sh.getAttribute()) + haveNum));
        }
        else if (haveNum == needNum) {
            afterItemId = beforeItemId + 1;
            final int nowLv = sh.getLv();
            final int nextLv = nowLv + 1;
            if (ArmsGemCache.skillNumLvReverseMap.get(nextLv) != null) {
                openSkillList.add(ArmsGemCache.skillNumLvReverseMap.get(nextLv));
                final StringBuffer sb = new StringBuffer(sh.getRefreshAttribute());
                sb.append(";");
                final int skillId = WebUtil.nextInt(ArmsJsSkillCache.SKILL_NUM) + 1;
                sb.append(skillId);
                sb.append(":");
                sb.append(1);
                this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttributeAndRefreshAttribute(sh.getVId(), afterItemId, nextLv, "0", sb.toString());
            }
            else {
                this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttribute(sh.getVId(), afterItemId, nextLv, "0");
            }
        }
        else {
            int remainNum = haveNum - needNum;
            afterItemId = sh.getItemId() + 1;
            final boolean flag = true;
            boolean isMaxLv = false;
            int openSkilllNum = 0;
            while (flag) {
                final ArmsGem currentGem = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)afterItemId);
                if (ArmsGemCache.skillNumLvReverseMap.get(currentGem.getGemLv()) != null) {
                    openSkillList.add(ArmsGemCache.skillNumLvReverseMap.get(currentGem.getGemLv()));
                    ++openSkilllNum;
                }
                if (currentGem.getGemLv() == ArmsGemCache.JS_MAX_LV) {
                    isMaxLv = true;
                    break;
                }
                if (remainNum < currentGem.getUpgradeGem1()) {
                    break;
                }
                ++afterItemId;
                remainNum -= currentGem.getUpgradeGem1();
            }
            final ArmsGem gem = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)afterItemId);
            if (isMaxLv) {
                if (openSkilllNum > 0) {
                    final StringBuffer sb2 = new StringBuffer(sh.getRefreshAttribute());
                    for (int i = 0; i < openSkilllNum; ++i) {
                        sb2.append(";");
                        final int skillId2 = WebUtil.nextInt(ArmsJsSkillCache.SKILL_NUM) + 1;
                        sb2.append(skillId2);
                        sb2.append(":");
                        sb2.append(1);
                    }
                    this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttributeAndRefreshAttribute(sh.getVId(), afterItemId, gem.getGemLv(), "0", sb2.toString());
                }
                else {
                    this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttribute(sh.getVId(), afterItemId, gem.getGemLv(), "0");
                }
                if (remainNum > 0) {
                    this.dataGetter.getStoreHouseService().gainGem(player, remainNum, 1, LocalMessages.T_LOG_GEM_21, null);
                }
            }
            else if (openSkilllNum > 0) {
                final StringBuffer sb2 = new StringBuffer(sh.getRefreshAttribute());
                for (int i = 0; i < openSkilllNum; ++i) {
                    sb2.append(";");
                    final int skillId2 = WebUtil.nextInt(ArmsJsSkillCache.SKILL_NUM) + 1;
                    sb2.append(skillId2);
                    sb2.append(":");
                    sb2.append(1);
                }
                this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttributeAndRefreshAttribute(sh.getVId(), afterItemId, gem.getGemLv(), String.valueOf(remainNum), sb2.toString());
            }
            else {
                this.dataGetter.getStoreHouseDao().updateItemIdAndGemLvAndAttribute(sh.getVId(), afterItemId, gem.getGemLv(), String.valueOf(remainNum));
            }
        }
        if (afterItemId > beforeItemId && sh.getGemId() > 0) {
            final PlayerWeapon pw = this.dataGetter.getPlayerWeaponDao().getPlayerWeapon(playerDto.playerId, sh.getGemId());
            final String gemIds = pw.getGemId();
            if (StringUtils.isNotBlank(gemIds)) {
                final String[] gemStrs = gemIds.split(",");
                if (gemStrs != null && gemStrs.length > 0) {
                    int count = 0;
                    final StringBuffer sb3 = new StringBuffer();
                    String[] array2;
                    for (int length2 = (array2 = gemStrs).length, k = 0; k < length2; ++k) {
                        final String temp2 = array2[k];
                        if (count == 0 && beforeItemId == Integer.valueOf(temp2)) {
                            sb3.append(afterItemId).append(",");
                            ++count;
                        }
                        else {
                            sb3.append(temp2).append(",");
                        }
                    }
                    if (count > 0) {
                        this.dataGetter.getPlayerWeaponDao().upgradeLoadGem(playerDto.playerId, pw.getWeaponId(), sb3.toString());
                    }
                }
            }
            this.battleDataCache.refreshWeaponEffect(playerDto.playerId, sh.getGemId());
            this.battleDataCache.refreshDiamondEffect(playerDto.playerId);
        }
        if (openSkillList.size() <= 0) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("openSkills");
        for (final Integer skillId3 : openSkillList) {
            doc.startObject();
            doc.createElement("skillId", skillId3);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] gemRefine(final PlayerDto playerDto, final int id, final int sn) {
        if (id <= 0 || sn <= 0 || sn > 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, GemService.SPECIAL_GEM_TECH);
        if (pt == null || pt.getStatus() != 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_TECH);
        }
        final StoreHouse sh = this.dataGetter.getStoreHouseDao().read(id);
        if (sh == null || sh.getType() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (sh.getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (sh.getGoodsType() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_ONLY_JS_CANJL);
        }
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)84);
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), ci.getCost(), "\u7cbe\u70bc\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final String oldRefreshAttribute = sh.getRefreshAttribute();
        final String[] arrs = oldRefreshAttribute.split(";");
        if (sn > arrs.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_SKILL_NO_OPEN);
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arrs.length; ++i) {
            if (i + 1 == sn) {
                final String[] temp = arrs[i].split(":");
                final int oldSkillId = Integer.parseInt(temp[0]);
                final int oldLv = Integer.parseInt(temp[1]);
                final int newSkillId = WebUtil.nextInt(ArmsJsSkillCache.SKILL_NUM) + 1;
                final ArmsJsSkill ajs = (ArmsJsSkill)this.dataGetter.getArmsJsSkillCache().get((Object)oldSkillId);
                int newLv = oldLv;
                final ArmsGem ag = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)sh.getItemId());
                if (oldLv < ag.getSkillMaxLv()) {
                    double updateRate = 0.0;
                    if (1 == oldLv) {
                        updateRate = ajs.getUpp1();
                    }
                    else if (2 == oldLv) {
                        updateRate = ajs.getUpp2();
                    }
                    else if (3 == oldLv) {
                        updateRate = ajs.getUpp3();
                    }
                    else if (4 == oldLv) {
                        updateRate = ajs.getUpp4();
                    }
                    final double rate = WebUtil.nextDouble();
                    if (rate < updateRate) {
                        ++newLv;
                    }
                }
                sb.append(newSkillId);
                sb.append(":");
                sb.append(newLv);
                sb.append(";");
            }
            else {
                sb.append(arrs[i]);
                sb.append(";");
            }
        }
        sb.setLength(sb.length() - 1);
        final String newRefreshAttribute = sb.toString();
        this.dataGetter.getStoreHouseDao().updateRefreshAttribute(sh.getVId(), newRefreshAttribute);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("id", id);
        final String[] skills = newRefreshAttribute.split(";");
        doc.startArray("skills");
        String[] array;
        for (int length = (array = skills).length, j = 0; j < length; ++j) {
            final String str = array[j];
            final String[] skill = str.split(":");
            doc.startObject();
            doc.createElement("type", skill[0]);
            doc.createElement("lv", skill[1]);
            final ArmsJsSkill ajs2 = (ArmsJsSkill)this.armsJsSkillCache.get((Object)Integer.valueOf(skill[0]));
            doc.createElement("name", ajs2.getName());
            doc.createElement("pic", ajs2.getPic());
            doc.createElement("intro", ajs2.getIntro());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        if (sh.getGemId() > 0) {
            this.battleDataCache.refreshDiamondEffect(playerDto.playerId);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public static void main(final String[] args) {
        System.out.println(WebUtil.nextInt(2));
        System.out.println(WebUtil.nextInt(2));
        System.out.println(WebUtil.nextInt(2));
        System.out.println(WebUtil.nextInt(2));
        System.out.println(WebUtil.nextInt(2));
    }
}
