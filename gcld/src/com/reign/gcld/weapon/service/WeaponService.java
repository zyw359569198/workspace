package com.reign.gcld.weapon.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.weapon.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.store.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.general.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;

@Component("weaponService")
public class WeaponService implements IWeaponService
{
    @Autowired
    private ArmsWeaponCache armsWeaponCache;
    @Autowired
    private IPlayerWeaponDao playerWeaponDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IDataGetter dataGetter;
    private static final DayReportLogger logger;
    
    static {
        logger = new DayReportLogger();
    }
    
    @Override
    public byte[] getWeaponInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        final List<PlayerWeapon> list = this.playerWeaponDao.getPlayerWeapons(playerId);
        final Map<Integer, PlayerWeapon> map = new HashMap<Integer, PlayerWeapon>();
        for (final PlayerWeapon playerWeapon : list) {
            map.put(playerWeapon.getWeaponId(), playerWeapon);
        }
        final List<ArmsWeapon> allWeapons = this.armsWeaponCache.getModels();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("weapons");
        for (final ArmsWeapon armsWeapon : allWeapons) {
            doc.startObject();
            final PlayerWeapon playerWeapon2 = map.get(armsWeapon.getId());
            int lv = 0;
            int times = 0;
            String gemIds = null;
            boolean open = false;
            if (playerWeapon2 != null) {
                lv = playerWeapon2.getLv();
                times = playerWeapon2.getTimes();
                gemIds = playerWeapon2.getGemId();
                open = true;
            }
            int totalTimes = this.serialCache.get(armsWeapon.getIronT(), lv);
            totalTimes = ((totalTimes == 0) ? 100 : totalTimes);
            final int upgradeCost = this.getUpgradeCost(armsWeapon, lv) / totalTimes;
            doc.createElement("id", armsWeapon.getId());
            doc.createElement("name", armsWeapon.getName());
            doc.createElement("pic", armsWeapon.getPic());
            doc.createElement("type", armsWeapon.getType());
            int curValue = 0;
            if (lv == 0 || lv == 1) {
                curValue = armsWeapon.getBaseAttribute();
            }
            else {
                curValue = armsWeapon.getBaseAttribute() + (lv - 1) * armsWeapon.getStrengthen();
            }
            int nextValue = armsWeapon.getBaseAttribute() + armsWeapon.getStrengthen() * lv;
            doc.createElement("times", times);
            doc.createElement("totalTimes", totalTimes);
            doc.createElement("lv", lv);
            doc.createElement("open", open);
            if (!open) {
                if (armsWeapon.getId() > 0 && armsWeapon.getId() < 4) {
                    doc.createElement("openLv", 50);
                }
                else {
                    doc.createElement("openLv", 80);
                }
                doc.createElement("introungot", armsWeapon.getIntroUngot());
            }
            else if (lv == 0) {
                doc.startArray("resources");
                boolean flag = true;
                for (final Integer key : armsWeapon.getCostMap().keySet()) {
                    doc.startObject();
                    doc.createElement("type", key);
                    final Integer value = armsWeapon.getCostMap().get(key);
                    doc.createElement("value", armsWeapon.getCostMap().get(key));
                    if (!this.conditionFulfill(key, value, playerId)) {
                        flag = false;
                        doc.createElement("fulfill", false);
                    }
                    else {
                        doc.createElement("fulfill", true);
                    }
                    doc.endObject();
                }
                doc.endArray();
                final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, armsWeapon.getItemId(), 6);
                final int num = (shList == null || shList.size() <= 0 || shList.get(0).getNum() == null) ? 0 : shList.get(0).getNum();
                doc.createElement("num", num);
                doc.createElement("maxNum", armsWeapon.getItemNum());
                doc.createElement("markTrace", armsWeapon.getMarkTrace());
                doc.createElement("make", flag && num >= armsWeapon.getItemNum());
                doc.createElement("introgot", armsWeapon.getIntroGot());
            }
            else {
                doc.createElement("make", false);
            }
            doc.createElement("upgradeCost", upgradeCost);
            String[] gemStrs = null;
            if (gemIds != null && !gemIds.isEmpty()) {
                gemStrs = gemIds.split(",");
            }
            int gemStar = 0;
            int gemId = 0;
            doc.startArray("gems");
            for (int i = 1; i <= 1; ++i) {
                doc.startObject();
                if (gemStrs != null && i <= gemStrs.length) {
                    gemId = Integer.valueOf(gemStrs[i - 1]);
                    if (gemId == 0) {
                        doc.createElement("pos", i);
                        doc.createElement("gemId", 0);
                    }
                    else {
                        final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)gemId);
                        gemStar = gem.getGemLv();
                        doc.createElement("pos", i);
                        doc.createElement("gemId", gemId);
                        doc.createElement("gemName", gem.getName());
                        doc.createElement("gemPic", gem.getPic());
                        doc.createElement("gemLv", gem.getGemLv());
                        doc.createElement("att", gem.getAtt());
                        doc.createElement("def", gem.getDef());
                        doc.createElement("blood", gem.getBlood() / 3);
                        doc.createElement("goodsType", (gemId > 15) ? 1 : 2);
                        if (gemId > 15) {
                            final StoreHouse storeHouse = this.dataGetter.getStoreHouseDao().getStoreHouseByWeaponId(playerId, playerWeapon2.getWeaponId());
                            if (StringUtils.isNotBlank(storeHouse.getRefreshAttribute())) {
                                final String[] skills = storeHouse.getRefreshAttribute().split(";");
                                doc.startArray("skills");
                                String[] array;
                                for (int length = (array = skills).length, j = 0; j < length; ++j) {
                                    final String str = array[j];
                                    final String[] skill = str.split(":");
                                    doc.startObject();
                                    doc.createElement("type", skill[0]);
                                    doc.createElement("lv", skill[1]);
                                    final ArmsJsSkill ajs = (ArmsJsSkill)this.dataGetter.getArmsJsSkillCache().get((Object)Integer.valueOf(skill[0]));
                                    doc.createElement("name", ajs.getName());
                                    doc.createElement("pic", ajs.getPic());
                                    doc.createElement("intro", ajs.getIntro());
                                    doc.endObject();
                                }
                                doc.endArray();
                            }
                        }
                    }
                }
                else {
                    doc.createElement("pos", i);
                    if (this.techEffectCache.getTechEffect(playerId, 46) > 0) {
                        doc.createElement("gemId", (-2));
                    }
                    else {
                        doc.createElement("gemId", (-1));
                    }
                }
                doc.endObject();
            }
            doc.endArray();
            if (armsWeapon.getType() % 3 == 0) {
                curValue /= 3;
                nextValue /= 3;
            }
            doc.createElement("value", curValue);
            doc.createElement("nextvalue", nextValue);
            doc.createElement("gemStar", gemStar);
            final int type = armsWeapon.getType();
            final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)gemId);
            doc.createElement("gemValue", (gemStar == 0 || gem == null) ? 0 : ((1 == type) ? gem.getAtt() : ((2 == type) ? gem.getDef() : (gem.getBlood() / 3))));
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("nowIron", pr.getIron());
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)40);
        doc.createElement("buyCost", (ci == null) ? 10 : ci.getCost());
        doc.createElement("iron", 100000);
        doc.createElement("isOpenTech", (this.playerTechDao.getByPlayerIdAndTechKey(playerId, 46).size() > 0) ? 1 : 0);
        doc.endObject();
        TaskMessageHelper.sendOpenWeaponTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private boolean conditionFulfill(final Integer key, final Integer value, final int playerId) {
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        switch (key) {
            case 1: {
                return pr.getCopper() > value;
            }
            case 2: {
                return pr.getWood() > value;
            }
            case 3: {
                return pr.getFood() > value;
            }
            case 4: {
                return pr.getIron() > value;
            }
            default: {
                return false;
            }
        }
    }
    
    private int getUpgradeCost(final ArmsWeapon armsWeapon, final int level) {
        return (int)(armsWeapon.getIronE() * this.serialCache.get(armsWeapon.getIronS(), level));
    }
    
    @Transactional
    @Override
    public byte[] upgradeWeapon(final int id, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final ArmsWeapon armsWeapon = (ArmsWeapon)this.armsWeaponCache.get((Object)id);
        if (armsWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        boolean updateLv = false;
        int addType = 1;
        int addTimes = 0;
        if (playerWeapon.getLv() < 1) {
            StoreHouse sh = null;
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, armsWeapon.getItemId(), 6);
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < armsWeapon.getItemNum()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_NEED_ITEM);
            }
            sh = shList.get(0);
            final Integer copper = armsWeapon.getCostMap().get(1);
            final int copper2 = (copper == null) ? 0 : copper;
            final Integer food = armsWeapon.getCostMap().get(3);
            final int food2 = (food == null) ? 0 : food;
            final Integer wood = armsWeapon.getCostMap().get(2);
            final int wood2 = (wood == null) ? 0 : wood;
            final Integer iron = armsWeapon.getCostMap().get(4);
            final int iron2 = (iron == null) ? 0 : iron;
            final PlayerResource prd = this.playerResourceDao.read(playerId);
            if (prd.getCopper() < copper2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
            if (prd.getFood() < food2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
            }
            if (prd.getWood() < wood2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10008);
            }
            if (prd.getIron() < iron2) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10026);
            }
            if (!this.playerResourceDao.consumeResource(playerId, copper2, food2, wood2, iron2, "\u79d1\u6280\u6253\u9020\u6d88\u8017")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
            }
            if (sh.getNum().equals(armsWeapon.getItemNum())) {
                this.storeHouseDao.deleteById(sh.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(sh.getVId(), armsWeapon.getItemNum());
            }
            this.playerWeaponDao.upgradeWeapon(playerId, id, 1, 0);
            updateLv = true;
            TaskMessageHelper.sendWeaponMakeDoneTaskMessage(playerId);
        }
        else {
            int totalTimes = this.serialCache.get(armsWeapon.getIronT(), playerWeapon.getLv());
            totalTimes = ((totalTimes == 0) ? 100 : totalTimes);
            final int times = playerWeapon.getTimes();
            final double[] boxProb = { 0.69, 0.2, 0.1, 0.01 };
            final double[] boxProb2 = { 0.51, 0.35, 0.13, 0.01 };
            final int[] timesArray = { 1, 2, 4, 10 };
            try {
                final PlayerIncenseWeaponEffect piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
                boolean flag = true;
                if (piwe != null) {
                    final int weaponId = piwe.getWeaponId();
                    final int weaponLimit = piwe.getWeaponLimit();
                    final int weaponMulti = piwe.getWeaponMulti();
                    final Date now = new Date();
                    if (2 == weaponId || 4 == weaponId) {
                        if (now.before(piwe.getWeaponEndTime()) && weaponLimit > 0) {
                            this.dataGetter.getPlayerIncenseWeaponEffectDao().reduceWeaponLimit(playerId);
                            addTimes = weaponMulti;
                            flag = false;
                        }
                    }
                    else if (5 == weaponId && now.before(piwe.getWeaponEndTime())) {
                        addTimes = KillRankService.getCrit(boxProb2, timesArray);
                        flag = false;
                    }
                }
                if (flag) {
                    addTimes = KillRankService.getCrit(boxProb, timesArray);
                }
            }
            catch (Exception e) {
                addTimes = 1;
            }
            addType = addTimes;
            final int nowLevel = playerWeapon.getLv();
            if (nowLevel >= playerDto.playerLv) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_LV_LIMIT);
            }
            final int cost = this.getUpgradeCost(armsWeapon, nowLevel) / totalTimes;
            if (times + addTimes >= totalTimes) {
                addTimes = times + addTimes - totalTimes;
                updateLv = true;
                addTimes = 0;
                final int nextLevel = nowLevel + 1;
                if (nextLevel > playerDto.playerLv) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_LV_LIMIT);
                }
            }
            else {
                addTimes += times;
            }
            if (!this.playerResourceDao.consumeIron(playerId, cost, "\u5347\u7ea7\u5175\u5668\u6d88\u8017\u9554\u94c1")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
            }
            if (addType == 10) {
                final String msg = MessageFormatter.format(LocalMessages.WEAPON_TEM_TIMES_UPDATE, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
            }
            this.playerWeaponDao.upgradeWeapon(playerId, id, updateLv ? 1 : 0, addTimes);
            if (updateLv && (playerWeapon.getLv() + 1 == 170 || playerWeapon.getLv() + 1 == 190 || playerWeapon.getLv() + 1 == 210)) {
                final String gemIds = playerWeapon.getGemId();
                String[] gemStrs = null;
                if (gemIds == null || gemIds.isEmpty()) {
                    this.playerWeaponDao.upgradeLoadGem(playerId, playerWeapon.getWeaponId(), "0,");
                }
                else {
                    gemStrs = gemIds.split(",");
                    if (gemStrs.length < 3) {
                        this.playerWeaponDao.upgradeLoadGem(playerId, playerWeapon.getWeaponId(), String.valueOf(playerWeapon.getGemId()) + "0,");
                    }
                }
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("updateLv", updateLv);
        doc.createElement("addType", addType);
        doc.createElement("addTimes", addTimes);
        doc.endObject();
        if (updateLv) {
            this.battleDataCache.refreshWeaponEffect(playerDto.playerId, id);
            final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                if (pgm.getState() == 0) {
                    final int res = this.playerGeneralMilitaryDao.updateAutoRecruit(playerId, pgm.getGeneralId());
                    if (res <= 0) {
                        continue;
                    }
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm.getGeneralId());
                }
            }
        }
        TaskMessageHelper.sendArmsWeaponOnMessage(playerId);
        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "build");
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] buyWeaponItem(final PlayerDto playerDto, final int id) {
        final int playerId = playerDto.playerId;
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final ArmsWeapon armsWeapon = (ArmsWeapon)this.armsWeaponCache.get((Object)id);
        if (armsWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)40);
        if (!this.playerDao.consumeGold(this.playerDao.read(playerId), (ci == null) ? 10 : ((int)ci.getCost()), "\u591f\u4e70\u56fe\u7eb8\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.storeHouseService.gainItems(playerId, 1, armsWeapon.getItemId(), LocalMessages.T_LOG_ITEM_2);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("num", true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] loadGem(final int id, final int vId, final int pos, final PlayerDto playerDto) {
        if (pos < 1 || pos > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[28] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final StoreHouse storeHouse = this.storeHouseDao.read(vId);
        if (storeHouse == null || storeHouse.getType() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_GEM);
        }
        if (playerId != storeHouse.getPlayerId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_GEM_NOT_BELONG_YOU);
        }
        if (storeHouse.getNum() <= 0) {
            this.storeHouseDao.deleteById(storeHouse.getVId());
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        if (storeHouse.getGemId() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_GEM_ALREADY_LOADED);
        }
        final String gemIds = playerWeapon.getGemId();
        String[] gemStrs = null;
        if (gemIds != null && !gemIds.isEmpty()) {
            gemStrs = gemIds.split(",");
        }
        if (gemStrs == null || pos > gemStrs.length || Integer.parseInt(gemStrs[pos - 1]) != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)storeHouse.getItemId());
        final int gold = armsGem.getLoadGold();
        if (!this.playerDao.consumeGold(player, gold, "\u5b9d\u77f3\u9576\u5d4c\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= gemStrs.length; ++i) {
            if (i == pos) {
                sb.append(storeHouse.getItemId()).append(",");
            }
            else {
                sb.append(gemStrs[i - 1]).append(",");
            }
        }
        if (armsGem.getId() > 15) {
            this.storeHouseDao.updateGemId(storeHouse.getVId(), id);
        }
        else {
            if (storeHouse.getNum() == 1) {
                this.storeHouseDao.deleteById(storeHouse.getVId());
            }
            else {
                this.storeHouseDao.reduceNum(storeHouse.getVId(), 1);
            }
            WeaponService.logger.info(LogUtil.formatGemLog(player, "-", "\u88c5\u5907\u9576\u5d4c", true, (ArmsGem)this.armsGemCache.get((Object)storeHouse.getItemId()), 1, LocalMessages.T_LOG_GEM_22));
        }
        this.playerWeaponDao.upgradeLoadGem(playerId, id, sb.toString());
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerDto.playerId);
        this.battleDataCache.refreshWeaponEffect(playerDto.playerId, id);
        this.battleDataCache.refreshDiamondEffect(playerDto.playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            final int newMaxHp = this.battleDataCache.getMaxHp(pgm);
            if (pgm.getForces() < newMaxHp) {
                final int res = this.playerGeneralMilitaryDao.updateAutoRecruit(playerDto.playerId, pgm.getGeneralId());
                if (res <= 0) {
                    continue;
                }
                this.generalService.sendGeneralMilitaryRecruitInfo(playerDto.playerId, pgm.getGeneralId());
            }
        }
        final int type = playerWeapon.getType();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement((1 == type) ? "att" : ((2 == type) ? "def" : "blood"), (1 == type) ? armsGem.getAtt() : ((2 == type) ? armsGem.getDef() : (armsGem.getBlood() / 3)));
        doc.createElement("lv", armsGem.getGemLv());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] unloadGem(final int id, final int pos, final PlayerDto playerDto) {
        if (pos < 1 || pos > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
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
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final String gemIds = playerWeapon.getGemId();
        String[] gemStrs = null;
        if (gemIds != null && !gemIds.isEmpty()) {
            gemStrs = gemIds.split(",");
        }
        if (gemStrs == null || pos > gemStrs.length || Integer.valueOf(gemStrs[pos - 1]) <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int gemId = Integer.valueOf(gemStrs[pos - 1]);
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        if (armsGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        final int gold = armsGem.getUnloadGold();
        if (!this.playerDao.consumeGold(player, gold, "\u5b9d\u77f3\u9576\u5d4c\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= gemStrs.length; ++i) {
            if (i == pos) {
                sb.append(0).append(",");
            }
            else {
                sb.append(gemStrs[i - 1]).append(",");
            }
        }
        this.playerWeaponDao.upgradeLoadGem(playerId, id, sb.toString());
        if (armsGem.getId() > 15) {
            this.dataGetter.getStoreHouseDao().updateGemIdByPlayerIdAndGemId(playerId, id, 0);
        }
        else {
            this.dataGetter.getStoreHouseService().gainGem(player, 1, gemId, LocalMessages.T_LOG_GEM_23, null);
        }
        this.battleDataCache.refreshWeaponEffect(playerDto.playerId, id);
        this.battleDataCache.refreshDiamondEffect(playerDto.playerId);
        final int type = playerWeapon.getType();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement((1 == type) ? "att" : ((2 == type) ? "def" : "blood"), (1 == type) ? armsGem.getAtt() : ((2 == type) ? armsGem.getDef() : (armsGem.getBlood() / 3)));
        doc.createElement("lv", armsGem.getGemLv());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] openSlot(final int id, final int pos, final PlayerDto playerDto) {
        if (pos < 1 || pos > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        if (StringUtils.isNotBlank(playerWeapon.getGemId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_ALREADY_OPEN_SLOT);
        }
        if (this.techEffectCache.getTechEffect(playerId, 46) <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_GEM_TECH);
        }
        if (!this.playerResourceDao.consumeIron(playerId, 100000, "\u5175\u5668\u5f00\u5b54\u6d88\u8017\u9554\u94c1")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10026);
        }
        this.playerWeaponDao.upgradeLoadGem(playerId, id, "0,");
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] getUnSetGems(final int id, final int pos, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerWeapon playerWeapon = this.playerWeaponDao.getPlayerWeapon(playerId, id);
        if (playerWeapon == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WEAPON_NO_SUCH_WEAPON);
        }
        final String gemIds = playerWeapon.getGemId();
        String[] gemStrs = null;
        if (gemIds != null && !gemIds.isEmpty()) {
            gemStrs = gemIds.split(",");
        }
        doc.startArray("gem");
        if (gemStrs != null) {
            for (int i = 0; i < gemStrs.length; ++i) {
                final int gemId = Integer.valueOf(gemStrs[i]);
                if (gemId > 0) {
                    if (i == pos - 1) {
                        final ArmsGem gem = (ArmsGem)this.armsGemCache.get((Object)gemId);
                        doc.startObject();
                        doc.createElement("vId", (-(i + 1)));
                        doc.createElement("gemId", gem.getId());
                        doc.createElement("gemName", gem.getName());
                        doc.createElement("gemPic", gem.getPic());
                        doc.createElement("gemLv", gem.getGemLv());
                        doc.createElement("att", gem.getAtt());
                        doc.createElement("def", gem.getDef());
                        doc.createElement("blood", gem.getBlood() / 3);
                        doc.createElement("num", 1);
                        doc.createElement("bind", 1);
                        doc.createElement("goodsType", (gem.getId() > 15) ? 1 : 2);
                        if (gemId > 15) {
                            final StoreHouse storeHouse = this.dataGetter.getStoreHouseDao().getStoreHouseByWeaponId(playerId, playerWeapon.getWeaponId());
                            if (StringUtils.isNotBlank(storeHouse.getRefreshAttribute())) {
                                final String[] skills = storeHouse.getRefreshAttribute().split(";");
                                doc.startArray("skills");
                                String[] array;
                                for (int length = (array = skills).length, j = 0; j < length; ++j) {
                                    final String str = array[j];
                                    final String[] skill = str.split(":");
                                    doc.startObject();
                                    doc.createElement("type", skill[0]);
                                    doc.createElement("lv", skill[1]);
                                    final ArmsJsSkill ajs = (ArmsJsSkill)this.dataGetter.getArmsJsSkillCache().get((Object)Integer.valueOf(skill[0]));
                                    doc.createElement("name", ajs.getName());
                                    doc.createElement("pic", ajs.getPic());
                                    doc.createElement("intro", ajs.getIntro());
                                    doc.endObject();
                                }
                                doc.endArray();
                            }
                        }
                        doc.endObject();
                    }
                }
            }
        }
        final List<StoreHouse> gemList = this.storeHouseDao.getUnSetGems(playerId);
        for (final StoreHouse storeHouse2 : gemList) {
            if (storeHouse2.getGemId() > 0) {
                continue;
            }
            final ArmsGem gem2 = (ArmsGem)this.armsGemCache.get((Object)storeHouse2.getItemId());
            doc.startObject();
            doc.createElement("vId", storeHouse2.getVId());
            doc.createElement("gemId", gem2.getId());
            doc.createElement("gemName", gem2.getName());
            doc.createElement("gemPic", gem2.getPic());
            doc.createElement("gemLv", gem2.getGemLv());
            doc.createElement("att", gem2.getAtt());
            doc.createElement("def", gem2.getDef());
            doc.createElement("blood", gem2.getBlood() / 3);
            doc.createElement("num", storeHouse2.getNum());
            doc.createElement("bind", 0);
            doc.createElement("goodsType", (gem2.getId() > 15) ? 1 : 2);
            if (gem2.getId() > 15 && StringUtils.isNotBlank(storeHouse2.getRefreshAttribute())) {
                final String[] skills = storeHouse2.getRefreshAttribute().split(";");
                doc.startArray("skills");
                String[] array2;
                for (int length2 = (array2 = skills).length, k = 0; k < length2; ++k) {
                    final String str = array2[k];
                    final String[] skill = str.split(":");
                    doc.startObject();
                    doc.createElement("type", skill[0]);
                    doc.createElement("lv", skill[1]);
                    final ArmsJsSkill ajs = (ArmsJsSkill)this.dataGetter.getArmsJsSkillCache().get((Object)Integer.valueOf(skill[0]));
                    doc.createElement("name", ajs.getName());
                    doc.createElement("pic", ajs.getPic());
                    doc.createElement("intro", ajs.getIntro());
                    doc.endObject();
                }
                doc.endArray();
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] preLoadGem(final int oGemId, final int gemId, final PlayerDto playerDto) {
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        if (armsGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        int gold = armsGem.getLoadGold();
        if (oGemId > 0) {
            final ArmsGem oArmsGem = (ArmsGem)this.armsGemCache.get((Object)oGemId);
            if (oArmsGem == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
            }
            gold += oArmsGem.getUnloadGold();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] preUnloadGem(final int gemId, final PlayerDto playerDto) {
        final ArmsGem armsGem = (ArmsGem)this.armsGemCache.get((Object)gemId);
        if (armsGem == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GEM_NO_SUCH_GEM);
        }
        final int gold = armsGem.getUnloadGold();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void assignWeapon(final int playerId, final int batch) {
        if (batch != 1 && batch != 2) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        final List<PlayerWeapon> list = this.playerWeaponDao.getPlayerWeapons(playerId);
        final Map<Integer, PlayerWeapon> map = new HashMap<Integer, PlayerWeapon>();
        for (final PlayerWeapon playerWeapon : list) {
            map.put(playerWeapon.getWeaponId(), playerWeapon);
        }
        if (batch == 1) {
            for (int id = 1; id < 4; ++id) {
                if (!map.containsKey(id)) {
                    final PlayerWeapon playerWeapon2 = new PlayerWeapon();
                    playerWeapon2.setPlayerId(playerId);
                    playerWeapon2.setWeaponId(id);
                    playerWeapon2.setLv(0);
                    playerWeapon2.setType(((ArmsWeapon)this.armsWeaponCache.get((Object)id)).getType());
                    playerWeapon2.setGemId("");
                    playerWeapon2.setTimes(0);
                    this.playerWeaponDao.create(playerWeapon2);
                    final ArmsWeapon weapon = (ArmsWeapon)this.armsWeaponCache.get((Object)id);
                    WeaponService.logger.info(LogUtil.formatWeaponLog(player, "+", "\u83b7\u5f97", weapon, playerWeapon2, 0, LocalMessages.T_lOG_WEAPON_1));
                }
            }
        }
        else if (batch == 2) {
            for (int id = 4; id < 7; ++id) {
                if (!map.containsKey(id)) {
                    final PlayerWeapon playerWeapon2 = new PlayerWeapon();
                    playerWeapon2.setPlayerId(playerId);
                    playerWeapon2.setWeaponId(id);
                    playerWeapon2.setLv(0);
                    playerWeapon2.setType(((ArmsWeapon)this.armsWeaponCache.get((Object)id)).getType());
                    playerWeapon2.setGemId("");
                    playerWeapon2.setTimes(0);
                    this.playerWeaponDao.create(playerWeapon2);
                    final ArmsWeapon weapon = (ArmsWeapon)this.armsWeaponCache.get((Object)id);
                    WeaponService.logger.info(LogUtil.formatWeaponLog(player, "+", "\u83b7\u5f97", weapon, playerWeapon2, 0, LocalMessages.T_lOG_WEAPON_1));
                }
            }
        }
        this.battleDataCache.refreshWeaponEffect(playerId);
    }
    
    @Override
    public void openWeaponFunction(final int playerId) {
        if (playerId <= 0) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        if (player == null || pa == null) {
            return;
        }
        final String function = (pa.getFunctionId() == null) ? "" : pa.getFunctionId();
        final char[] cs = function.toCharArray();
        cs[29] = '1';
        this.playerAttributeDao.updateFunction(playerId, String.valueOf(cs));
        final List<PlayerWeapon> list = this.playerWeaponDao.getPlayerWeapons(playerId);
        final Map<Integer, PlayerWeapon> map = new HashMap<Integer, PlayerWeapon>();
        for (final PlayerWeapon playerWeapon : list) {
            map.put(playerWeapon.getWeaponId(), playerWeapon);
        }
        if (!map.containsKey(1)) {
            final PlayerWeapon playerWeapon = new PlayerWeapon();
            playerWeapon.setPlayerId(playerId);
            playerWeapon.setWeaponId(1);
            playerWeapon.setLv(0);
            playerWeapon.setType(((ArmsWeapon)this.armsWeaponCache.get((Object)1)).getType());
            playerWeapon.setGemId("");
            playerWeapon.setTimes(0);
            this.playerWeaponDao.create(playerWeapon);
            final ArmsWeapon weapon = (ArmsWeapon)this.armsWeaponCache.get((Object)1);
            WeaponService.logger.info(LogUtil.formatWeaponLog(player, "+", "\u83b7\u5f97", weapon, playerWeapon, 0, LocalMessages.T_lOG_WEAPON_1));
        }
    }
}
