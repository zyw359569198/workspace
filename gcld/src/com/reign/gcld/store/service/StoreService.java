package com.reign.gcld.store.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.treasure.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.common.event.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.request.*;
import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.gcld.store.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.treasure.domain.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;

@Component("storeService")
public class StoreService implements IStoreService
{
    private static final DayReportLogger logger;
    @Autowired
    private IPlayerItemRefreshDao playerItemRefreshDao;
    @Autowired
    private IPlayerStoreDao playerStoreDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private StoreStatCache storeStatCache;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private StringCCache stringCCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IChatService chatService;
    @Autowired
    private TreasureCache treasureCache;
    @Autowired
    private IPlayerTreasureDao playerTreasureDao;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private EquipSuitCache equipSuitCache;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private PlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private BuildingDrawingCache buildingDrawingCache;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private WorldCitySpecialCache worldCitySpecialCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private CityDataCache cityDataCache;
    private final int MaxIntimacy = 48511100;
    
    static {
        logger = new DayReportLogger();
    }
    
    @Transactional
    @Override
    public byte[] getItems(final int playerId, final int style) {
        if (style < 1 || style > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (style == 1) {
            if (cs[18] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
        }
        else if (style == 2 && cs[17] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        final Player player = this.playerDao.read(playerId);
        final List<PlayerItemRefresh> list = this.playerItemRefreshDao.getListByPlayerId(playerId);
        if (player.getPlayerLv() >= 18) {
            EventListener.fireEvent(new CommonEvent(27, playerId));
        }
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(list, style, playerId, ci));
    }
    
    private byte[] getResult(final List<PlayerItemRefresh> list, final int style, final int playerId, final Chargeitem ci) {
        final List<PlayerTask> taskList = this.playerTaskDao.getDisPlayPlayerTask(playerId);
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final int generalNum = (pgmList == null) ? 0 : pgmList.size();
        final Player player = this.playerDao.read(playerId);
        GameTask gameTask = null;
        final List<EquipTaskDto> taskRequest = new ArrayList<EquipTaskDto>();
        final List<ITaskRequest> taskListNew = new ArrayList<ITaskRequest>();
        List<StoreHouse> shs = null;
        boolean flag = false;
        for (final PlayerTask playerTask : taskList) {
            final int taskType = playerTask.getType();
            if (taskType == 1) {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getTaskId());
            }
            else {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getGroupId(), playerTask.getTaskId(), taskType);
            }
            if (gameTask != null) {
                final ITaskRequest taskRequestSingle = gameTask.getTaskRequest();
                if (taskRequestSingle instanceof TaskRequestAnd) {
                    if (!this.isEquipConcerned(taskRequestSingle, playerId)) {
                        continue;
                    }
                    final List<ITaskRequest> a = ((TaskRequestAnd)taskRequestSingle).getRequestList();
                    for (final ITaskRequest b : a) {
                        if (this.isEquipConcerned(taskRequestSingle, playerId)) {
                            taskListNew.add(b);
                        }
                    }
                }
                else if (taskRequestSingle instanceof TaskRequestOr) {
                    if (!this.isEquipConcerned(taskRequestSingle, playerId)) {
                        continue;
                    }
                    final List<ITaskRequest> a = ((TaskRequestOr)taskRequestSingle).getRequestList();
                    for (final ITaskRequest b : a) {
                        if (this.isEquipConcerned(taskRequestSingle, playerId)) {
                            taskListNew.add(b);
                        }
                    }
                }
                else {
                    if (!this.isEquipConcerned(taskRequestSingle, playerId)) {
                        continue;
                    }
                    taskListNew.add(taskRequestSingle);
                }
            }
        }
        for (final ITaskRequest c : taskListNew) {
            if (c.isConcernedMessage(new TaskMessageStoreBuyS(playerId))) {
                flag = true;
                final TaskRequestStoreBuyS request = (TaskRequestStoreBuyS)c;
                taskRequest.add(new EquipTaskDto(request));
            }
            else if (c.isConcernedMessage(new TaskMessageEquip(playerId))) {
                flag = true;
                final TaskRequestEquip taskRequestEquip = (TaskRequestEquip)c;
                taskRequest.add(new EquipTaskDto(taskRequestEquip));
            }
            else {
                if (!c.isConcernedMessage(new TaskMessageEquipOn(playerId))) {
                    continue;
                }
                flag = true;
                final TaskRequestEquipOn taskRequestEquipOn = (TaskRequestEquipOn)c;
                taskRequest.add(new EquipTaskDto(taskRequestEquipOn));
            }
        }
        if (flag) {
            shs = this.storeHouseDao.getAllEquip(playerId);
        }
        final Comparator c2 = new ComparatorItemType();
        Collections.sort(list, c2);
        final Date nowDate = new Date();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("vipLimit", ci.getLv());
        doc.startArray("items");
        int needTips = 0;
        for (final PlayerItemRefresh pir : list) {
            int state = 0;
            if (pir.getType() == 0 && pir.getIsGold() == 0) {
                final BuildingDrawing buildingDrawing = (BuildingDrawing)this.buildingDrawingCache.get((Object)pir.getItemId());
                if (buildingDrawing == null) {
                    continue;
                }
                doc.startObject();
                doc.createElement("itemId", buildingDrawing.getId());
                doc.createElement("name", buildingDrawing.getName());
                doc.createElement("pic", buildingDrawing.getPic());
                doc.createElement("price", pir.getPrice());
                doc.createElement("type", pir.getType());
                doc.createElement("isGold", pir.getIsGold());
                doc.createElement("hotDegree", 0);
                doc.createElement("locked", pir.getLocked() == 1);
                doc.createElement("unlockCD", CDUtil.getCD(pir.getUnlockTime(), nowDate));
                doc.createElement("bought", pir.getBought() == 1);
                doc.createElement("isBuildingDraw", true);
                if (pir.getBought() == 0) {
                    needTips = 1;
                }
                doc.endObject();
            }
            else if (pir.getItemId() <= 8) {
                final Treasure treasure = (Treasure)this.treasureCache.get((Object)pir.getItemId());
                int treasureType = 0;
                if (treasure.getType() == 3) {
                    treasureType = 1;
                }
                else {
                    treasureType = 2;
                }
                if (treasureType == style) {
                    doc.startObject();
                    doc.createElement("itemId", treasure.getId());
                    doc.createElement("name", treasure.getName());
                    doc.createElement("intro", treasure.getIntro());
                    doc.createElement("pic", treasure.getPic());
                    doc.createElement("quality", 1);
                    doc.createElement("price", pir.getPrice());
                    doc.createElement("type", pir.getType());
                    doc.createElement("isGold", pir.getIsGold());
                    doc.createElement("hotDegree", 0);
                    doc.createElement("locked", pir.getLocked() == 1);
                    doc.createElement("unlockCD", CDUtil.getCD(pir.getUnlockTime(), nowDate));
                    doc.createElement("bought", pir.getBought() == 1);
                    doc.createElement("isTreasure", true);
                    this.addTreasureEffect(doc, treasure);
                    doc.endObject();
                }
            }
            else {
                final Equip item = (Equip)this.equipCache.get((Object)pir.getItemId());
                if (this.getItemStyle(item.getId()) == style) {
                    doc.startObject();
                    doc.createElement("itemId", item.getId());
                    doc.createElement("name", item.getName());
                    if (this.isUnrefresh(item, playerId)) {
                        doc.createElement("isNew", true);
                    }
                    int skillNum = 0;
                    if (style == 1) {
                        final String attInfo = pir.getRefreshAttribute();
                        if (!StringUtils.isBlank(attInfo)) {
                            skillNum = EquipCommon.getRefreshAttNum(attInfo);
                        }
                        EquipCommon.getRefreshAttribute(attInfo, doc, this.equipSkillCache, null, this.equipSkillEffectCache, item);
                        EquipCommon.getMaxSkillAndLv(doc, item, this.equipCache, 0, attInfo);
                    }
                    doc.createElement("intro", item.getIntro());
                    doc.createElement("pic", item.getPic());
                    doc.createElement("lv", item.getDefaultLevel());
                    doc.createElement("quality", item.getQuality());
                    doc.createElement("suitName", this.equipSuitCache.getSuitName(item.getId()));
                    doc.createElement("type", pir.getType());
                    doc.createElement("price", pir.getPrice());
                    doc.createElement("isGold", pir.getIsGold());
                    final int curMaxQuality = this.equipSuitCache.getMaxQuality(style, player.getPlayerLv());
                    if (curMaxQuality == item.getQuality()) {
                        doc.createElement("maxGeneralNum", generalNum);
                        int shNum = 0;
                        final List<StoreHouse> equipList = this.storeHouseDao.getByQualityNType(playerId, pir.getType(), item.getQuality(), style);
                        if (item.getQuality() <= 3) {
                            shNum = equipList.size();
                        }
                        else {
                            for (final StoreHouse sHouse : equipList) {
                                final int id = sHouse.getItemId();
                                final Equip equip = (Equip)this.equipCache.get((Object)id);
                                int storeSkillNum = 0;
                                final String attriInfo = sHouse.getRefreshAttribute();
                                storeSkillNum = EquipCommon.getRefreshAttNum(attriInfo);
                                if (this.equipCache.getJinpinEquips().contains(id) && equip.getSkillNum() <= storeSkillNum) {
                                    ++shNum;
                                }
                            }
                        }
                        doc.createElement("curItemNum", shNum);
                        if (generalNum > shNum || item.getQuality() == 6) {
                            ++state;
                        }
                    }
                    if (this.equipCache.getJinpinEquips().contains(item.getId()) && skillNum == item.getSkillNum()) {
                        doc.createElement("hotDegree", 1);
                        ++state;
                    }
                    else if (item.getQuality() == 4 || item.getQuality() == 5) {
                        if (pir.getIsCheap() != null && pir.getIsCheap() == 1) {
                            doc.createElement("hotDegree", 2);
                        }
                    }
                    else {
                        doc.createElement("hotDegree", 0);
                    }
                    switch (item.getType()) {
                        case 1:
                        case 2: {
                            doc.createElement("att", item.getAttribute());
                            break;
                        }
                        case 9:
                        case 10: {
                            doc.createElement("att", item.getAttribute());
                            break;
                        }
                        case 3:
                        case 4: {
                            doc.createElement("def", item.getAttribute());
                            break;
                        }
                        case 11:
                        case 12: {
                            doc.createElement("def", item.getAttribute());
                            break;
                        }
                        case 5:
                        case 6: {
                            doc.createElement("blood", item.getAttribute() / 3);
                            break;
                        }
                        case 7:
                        case 8: {
                            doc.createElement("blood", item.getAttribute() / 3);
                            break;
                        }
                    }
                    doc.createElement("locked", pir.getLocked() == 1);
                    doc.createElement("unlockCD", CDUtil.getCD(pir.getUnlockTime(), nowDate));
                    doc.createElement("bought", pir.getBought() == 1);
                    this.CheckInfo(doc, pir, taskRequest, shs);
                    doc.endObject();
                }
            }
            if (state == 2 && pir.getBought() == 0) {
                needTips = 2;
            }
        }
        doc.endArray();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        doc.createElement("nowItemNum", this.getNowItemNum(playerId, style));
        doc.createElement("maxItemNum", maxNum);
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        if (playerStore != null) {
            if (style == 1) {
                doc.createElement("refreshCD", CDUtil.getCD(playerStore.getNextEquipDate(), nowDate));
            }
            else if (style == 2) {
                doc.createElement("refreshCD", CDUtil.getCD(playerStore.getNextToolDate(), nowDate));
            }
        }
        doc.createElement("cdInRedMinutes", 30000L);
        doc.createElement("yellowed", this.playerDao.read(playerId).getPlayerLv() >= 40);
        final Integer intimacy = this.playerAttributeDao.read(playerId).getIntimacy();
        int inti = (intimacy == null) ? 0 : intimacy;
        final int lv = this.serialCache.getIntiLv(inti);
        final int max = this.serialCache.getLvMax(lv);
        inti = ((lv == 1) ? inti : (inti - this.serialCache.getIntiLv().get(lv - 2)));
        doc.createElement("intimacyLv", lv);
        doc.createElement("curIntimacy", inti);
        doc.createElement("maxIntimacy", max);
        final int limitLv = this.equipSuitCache.getNowMaxIntimacyLv(player.getPlayerLv());
        boolean isLimit = false;
        if (lv >= limitLv) {
            isLimit = true;
        }
        doc.createElement("isIntiLimit", isLimit);
        doc.createElement("isIntiUp", inti == 0 && lv != 1 && !isLimit);
        doc.createElement("needTips", needTips);
        doc.startArray("specialCities");
        final Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(3);
        if (cityId != null) {
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("cityName", ((WorldCity)this.worldCityCache.get((Object)cityId)).getName());
            doc.createElement("hasSpecialCity", this.cityDataCache.hasCity(player.getForceId(), cityId) ? 1 : 0);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    private boolean isEquipConcerned(final ITaskRequest taskRequestSingle, final int playerId) {
        return taskRequestSingle.isConcernedMessage(new TaskMessageStoreBuyS(playerId)) || taskRequestSingle.isConcernedMessage(new TaskMessageEquip(playerId)) || taskRequestSingle.isConcernedMessage(new TaskMessageEquipOn(playerId));
    }
    
    private void CheckInfo(final JsonDocument doc, final PlayerItemRefresh pir, final List<EquipTaskDto> taskRequest, final List<StoreHouse> shs) {
        final Tuple<Integer, Integer> aTuple = this.isTaskRelative(pir, taskRequest, shs);
        if (aTuple != null) {
            final int cur = aTuple.left;
            final int max = aTuple.right;
            if (cur < max) {
                doc.createElement("notice", true);
                doc.createElement("curEquipNum", cur);
                doc.createElement("totalNeedNum", max);
            }
        }
    }
    
    private Tuple<Integer, Integer> isTaskRelative(final PlayerItemRefresh pir, final List<EquipTaskDto> taskRequest, final List<StoreHouse> list) {
        if (taskRequest == null || taskRequest.isEmpty()) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        final EquipTaskDto dto = new EquipTaskDto();
        final Equip equip = (Equip)this.equipCache.get((Object)pir.getItemId());
        if (equip == null) {
            return null;
        }
        dto.setType(equip.getType());
        dto.setQuality(equip.getQuality());
        dto.setDegree(0);
        dto.setNum(0);
        final EquipTaskDto max = this.getMaxNum(dto, taskRequest);
        if (max == null) {
            return null;
        }
        final int num = this.getRelevantNum(max, list);
        result.left = num;
        result.right = max.getNum();
        return result;
    }
    
    private EquipTaskDto getMaxNum(final EquipTaskDto dto, final List<EquipTaskDto> taskRequest) {
        for (final EquipTaskDto dto2 : taskRequest) {
            if (dto2.getType() == dto.getType() && dto2.getQuality() <= dto.getQuality()) {
                return dto2;
            }
        }
        return null;
    }
    
    private int getRelevantNum(final EquipTaskDto dto, final List<StoreHouse> list) {
        int num = 0;
        for (final StoreHouse s : list) {
            if (s.getGoodsType() == dto.getType() && s.getQuality() >= dto.getQuality()) {
                ++num;
            }
        }
        return num;
    }
    
    private boolean isUnrefresh(final Equip item, final int playerId) {
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        boolean returnValue = false;
        final StringBuffer sbBuffer = new StringBuffer();
        if (playerStore == null || playerStore.getUnrefreshedEquip().equals("")) {
            return false;
        }
        String[] split;
        for (int length = (split = playerStore.getUnrefreshedEquip().split(";")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            if (s != null && !s.equals("") && Integer.parseInt(s) == item.getId()) {
                returnValue = true;
            }
            else {
                if (sbBuffer.length() != 0) {
                    sbBuffer.append(";");
                }
                sbBuffer.append(s);
            }
        }
        if (returnValue) {
            this.playerStoreDao.updateUnrefreshEquip(playerId, sbBuffer.toString());
        }
        return returnValue;
    }
    
    private void addTreasureEffect(final JsonDocument doc, final Treasure treasure) {
        final String effect = treasure.getEffect();
        final String[] effs = effect.split(";");
        String[] array;
        for (int length = (array = effs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            final String[] s = str.split("=");
            if (s[0].equalsIgnoreCase("ATT")) {
                doc.createElement("att", Float.valueOf(s[1]) * 100.0f);
            }
            else if (s[0].equalsIgnoreCase("DEF")) {
                doc.createElement("def", Float.valueOf(s[1]) * 100.0f);
            }
        }
    }
    
    private int getItemStyle(final int itemId) {
        if (itemId <= 8) {
            int style = 0;
            final Treasure treasure = (Treasure)this.treasureCache.get((Object)itemId);
            if (treasure.getType() == 4) {
                style = 2;
            }
            else if (treasure.getType() == 3) {
                style = 1;
            }
            return style;
        }
        final Equip item = (Equip)this.equipCache.get((Object)itemId);
        if (item == null) {
            return 0;
        }
        switch (item.getType()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                return 1;
            }
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    private int getNowItemNum(final int playerId, final int style) {
        return this.storeHouseDao.getCountByPlayerId(playerId);
    }
    
    @Transactional
    @Override
    public byte[] refreshItem(final int playerId, final int style, final boolean checkLv) {
        if (style < 1 || style > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerId);
        if (checkLv && player.getPlayerLv() < 18) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_REFRESH_LV);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        final Date nowDate = new Date();
        if (style == 1) {
            if (CDUtil.getCD(playerStore.getNextEquipDate(), nowDate) > 1800000L) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_REFRESH_CD);
            }
        }
        else if (style == 2 && CDUtil.getCD(playerStore.getNextToolDate(), nowDate) > 1800000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_REFRESH_CD);
        }
        final int beforeState = playerStore.getStoreState();
        final int nowState = this.decideNextState(style, beforeState, player.getPlayerLv());
        playerStore.setStoreState(nowState);
        if (style == 1) {
            final Date recordDate = playerStore.getNextEquipDate();
            final Date startDate = recordDate.after(nowDate) ? recordDate : nowDate;
            int cd = (int)(3.0 - this.cityEffectCache.getCityEffect(player.getForceId(), 3));
            if (cd < 0) {
                cd = 0;
            }
            final Date nextDate = new Date(startDate.getTime() + 60000L * cd);
            playerStore.setEquipRefreshTime(playerStore.getEquipRefreshTime() + 1);
            playerStore.setNextEquipDate(nextDate);
        }
        else if (style == 2) {
            final Date recordDate = playerStore.getNextToolDate();
            final Date startDate = recordDate.after(nowDate) ? recordDate : nowDate;
            final Date nextDate2 = new Date(startDate.getTime() + 30000L);
            playerStore.setToolRefreshTime(playerStore.getToolRefreshTime() + 1);
            playerStore.setNextToolDate(nextDate2);
        }
        this.playerStoreDao.updatePlayerStore(playerStore);
        return this.refreshItem(playerId, style, playerStore, ci);
    }
    
    private void updateCreate(final List<PlayerItemRefresh> resultList, final Set<Integer> setDel) {
        final List<PlayerItemRefresh> updateList = new ArrayList<PlayerItemRefresh>();
        for (final PlayerItemRefresh pir : resultList) {
            if (setDel.contains(pir.getVId())) {
                setDel.remove(pir.getVId());
            }
            else {
                updateList.add(pir);
            }
        }
        int idx = 0;
        for (final Integer key : setDel) {
            if (updateList.size() > idx) {
                final PlayerItemRefresh pir2 = updateList.get(idx);
                ++idx;
                pir2.setVId(key);
                this.playerItemRefreshDao.update(pir2);
            }
        }
        for (int i = idx; i < updateList.size(); ++i) {
            this.playerItemRefreshDao.create(updateList.get(i));
        }
    }
    
    @Transactional
    @Override
    public byte[] refreshItem(final int playerId, final int style, final PlayerStore playerStore, final Chargeitem ci) {
        final Player player = this.playerDao.read(playerId);
        final List<PlayerItemRefresh> resultList = new ArrayList<PlayerItemRefresh>();
        final List<Integer> types = new ArrayList<Integer>();
        final List<PlayerItemRefresh> beforeList = this.playerItemRefreshDao.getListByPlayerId(playerId);
        final Set<Integer> setDel = new HashSet<Integer>();
        for (final PlayerItemRefresh pir : beforeList) {
            if (this.getItemStyle(pir.getItemId()) == style) {
                setDel.add(pir.getVId());
                if (pir.getLocked() == 1) {
                    resultList.add(pir);
                    types.add(pir.getType());
                    if (pir.getType() == 0) {
                        types.add(1);
                        types.add(7);
                    }
                }
            }
            if (pir.getType() == 0 && pir.getIsGold() == 0) {
                setDel.add(pir.getVId());
            }
        }
        TaskMessageHelper.sendStoreRefreshTaskMessage(playerId);
        if (resultList.size() == 6) {
            this.updateCreate(resultList, setDel);
            return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, style, playerId, ci));
        }
        if (!StringUtils.isBlank(playerStore.getLockEquipId())) {
            final String[] lockIds = playerStore.getLockEquipId().split(";");
            String[] array;
            for (int length = (array = lockIds).length, k = 0; k < length; ++k) {
                final String lockId = array[k];
                final int lockEquipId = Integer.valueOf(lockId);
                if (this.getItemStyle(lockEquipId) == style) {
                    final Equip stateItem = (Equip)this.equipCache.get((Object)lockEquipId);
                    if (!types.contains(stateItem.getType())) {
                        final PlayerItemRefresh pir2 = new PlayerItemRefresh();
                        pir2.setPlayerId(playerId);
                        pir2.setLocked(0);
                        pir2.setItemId(stateItem.getId());
                        pir2.setBought(0);
                        this.decideItemPrice(pir2, false, stateItem.getCopperBuy());
                        pir2.setType(stateItem.getType());
                        if (stateItem.getSkillNum() != 0) {
                            this.getRefreshAttr(pir2, stateItem, stateItem.getSkillType());
                        }
                        else {
                            pir2.setRefreshAttribute("");
                        }
                        types.add(stateItem.getType());
                        resultList.add(pir2);
                    }
                }
                if (resultList.size() == 6) {
                    this.updateCreate(resultList, setDel);
                    return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, style, playerId, ci));
                }
            }
        }
        final double prob = WebUtil.nextDouble();
        boolean functionOpen = false;
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (cs[20] == '1') {
            functionOpen = true;
        }
        final double dropRate = ((C)this.cCache.get((Object)"Treasure.GetProb.Store")).getValue();
        if (prob <= dropRate && functionOpen && !types.contains(1) && !types.contains(7)) {
            List<Treasure> optionalList = null;
            if (style == 1) {
                optionalList = this.treasureCache.getTreasuresByType(3);
            }
            else if (style == 2) {
                optionalList = this.treasureCache.getTreasuresByType(4);
            }
            final List<PlayerTreasure> ownedList = this.playerTreasureDao.getPlayerTreasures(playerId);
            final Map<Integer, PlayerTreasure> ownedMap = new HashMap<Integer, PlayerTreasure>();
            for (final PlayerTreasure pt : ownedList) {
                ownedMap.put(pt.getTreasureId(), pt);
            }
            for (final Treasure t : optionalList) {
                if (ownedMap.containsKey(t.getId())) {
                    continue;
                }
                boolean locked = false;
                for (final PlayerItemRefresh pir3 : resultList) {
                    if (pir3.getItemId() == t.getId()) {
                        locked = true;
                        break;
                    }
                }
                if (locked) {
                    continue;
                }
                PlayerItemRefresh pir3 = new PlayerItemRefresh();
                pir3.setPlayerId(playerId);
                pir3.setLocked(0);
                pir3.setItemId(t.getId());
                pir3.setBought(0);
                pir3.setIsCheap(1);
                pir3.setIsGold(1);
                pir3.setPrice(t.getGold());
                pir3.setType(0);
                types.add(0);
                types.add(1);
                types.add(7);
                resultList.add(pir3);
                break;
            }
        }
        else {
            final BuildingDrawing buildingDrawing = this.buildingService.dropBluePrintByType(playerId, 3);
            if (buildingDrawing != null) {
                final PlayerItemRefresh pir2 = new PlayerItemRefresh();
                pir2.setPlayerId(playerId);
                pir2.setLocked(0);
                pir2.setItemId(buildingDrawing.getId());
                pir2.setBought(0);
                pir2.setIsCheap(0);
                pir2.setIsGold(0);
                pir2.setPrice(10000);
                pir2.setType(0);
                types.add(0);
                types.add(1);
                types.add(7);
                resultList.add(pir2);
            }
        }
        int offset = 0;
        if (style == 2) {
            offset = 6;
        }
        for (int i = 1; i <= 6; ++i) {
            final int type = i + offset;
            if (!types.contains(type)) {
                final List<Equip> equiplist = new ArrayList<Equip>();
                final String equips = this.equipSuitCache.getEquipsByTypeNotAboveLv(player.getPlayerLv(), style);
                if (!StringUtils.isBlank(equips)) {
                    String[] split;
                    for (int length2 = (split = equips.split(";")).length, l = 0; l < length2; ++l) {
                        final String s = split[l];
                        if (!StringUtils.isBlank(s)) {
                            final Equip equip = (Equip)this.equipCache.get((Object)Integer.valueOf(s));
                            if (equip.getType() == type) {
                                equiplist.add(equip);
                            }
                        }
                    }
                }
                final Comparator orderByIntimacy = new IntimacyComparator();
                Collections.sort(equiplist, orderByIntimacy);
                boolean flag = false;
                double attrEquipProbs = 0.0;
                if (equiplist != null && !equiplist.isEmpty()) {
                    final Equip e = equiplist.get(0);
                    final double prob_base = e.getProbBase();
                    final double prob_intimacy = e.getProbIntimacy();
                    final Integer intimacy = this.playerAttributeDao.read(playerId).getIntimacy();
                    final int intimacyValue = (intimacy == null) ? 0 : intimacy;
                    attrEquipProbs = prob_base + intimacyValue * prob_intimacy;
                }
                if (WebUtil.nextDouble() <= attrEquipProbs) {
                    flag = true;
                }
                final List<Equip> intiGroup = this.getTopGroupEquip(equiplist);
                if (flag) {
                    int index = 0;
                    final double ran = WebUtil.nextDouble();
                    double pro = 0.0;
                    for (int j = 0; j < intiGroup.size(); ++j) {
                        pro += intiGroup.get(j).getIntimacyGroupProb();
                        if (ran <= pro) {
                            index = j;
                            break;
                        }
                    }
                    final Equip equipinti = intiGroup.get(index);
                    final PlayerItemRefresh pir4 = new PlayerItemRefresh();
                    pir4.setPlayerId(playerId);
                    pir4.setLocked(0);
                    pir4.setItemId(equipinti.getId());
                    pir4.setBought(0);
                    this.decideItemPrice(pir4, false, equipinti.getCopperBuy());
                    pir4.setType(equipinti.getType());
                    this.getRefreshAttr(pir4, equipinti, equipinti.getSkillType());
                    types.add(equipinti.getType());
                    resultList.add(pir4);
                }
                else if (equiplist != null && equiplist.size() != 0) {
                    final int intiSize = intiGroup.size();
                    final int restSize = equiplist.size() - intiSize;
                    int rand = 0;
                    Equip norEquip = null;
                    if (restSize != 0) {
                        rand = WebUtil.nextInt(restSize);
                        norEquip = equiplist.get(rand + intiSize);
                    }
                    else {
                        rand = WebUtil.nextInt(equiplist.size());
                        norEquip = equiplist.get(rand);
                    }
                    final PlayerItemRefresh pir5 = new PlayerItemRefresh();
                    pir5.setPlayerId(playerId);
                    pir5.setLocked(0);
                    pir5.setItemId(norEquip.getId());
                    pir5.setBought(0);
                    this.decideItemPrice(pir5, false, norEquip.getCopperBuy());
                    pir5.setType(norEquip.getType());
                    pir5.setRefreshAttribute("");
                    types.add(norEquip.getType());
                    resultList.add(pir5);
                }
            }
        }
        this.updateCreate(resultList, setDel);
        final Integer inti = this.playerAttributeDao.read(playerId).getIntimacy();
        final int intiO = (inti == null) ? 1 : inti;
        final int lv = this.serialCache.getIntiLv(intiO);
        boolean isIntiLvUp = true;
        if (lv >= this.equipSuitCache.getNowMaxIntimacyLv(player.getPlayerLv())) {
            isIntiLvUp = false;
        }
        if (player.getPlayerLv() >= 18 && isIntiLvUp) {
            int newInti = (inti == null) ? 1 : (inti + 1);
            newInti = ((newInti >= 48511100) ? 48511100 : newInti);
            this.playerAttributeDao.incrementIntimacy(playerId, newInti);
        }
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(resultList, style, playerId, ci));
    }
    
    private void getRefreshAttr(final PlayerItemRefresh pir, final Equip equipinti, final Integer integer) {
        final List<EquipSkill> eSkills = this.equipSkillCache.getSkillByType(integer);
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < equipinti.getSkillNum(); ++i) {
            final int ran = WebUtil.nextInt(eSkills.size());
            sb.append(eSkills.get(ran).getId()).append(":").append(equipinti.getSkillLvDefault()).append(";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        pir.setRefreshAttribute(sb.toString());
    }
    
    private List<Equip> getTopGroupEquip(final List<Equip> equiplist) {
        if (equiplist.isEmpty()) {
            return null;
        }
        final List<Equip> returnList = new ArrayList<Equip>();
        for (final Equip e : equiplist) {
            if (e.getIntimacyGroup().equals(equiplist.get(0).getIntimacyGroup())) {
                returnList.add(e);
            }
        }
        if (returnList.isEmpty()) {
            return null;
        }
        return returnList;
    }
    
    private void decideItemPrice(final PlayerItemRefresh pir, final boolean mustCopper, final Integer integer) {
        pir.setIsGold(0);
        final int basePrice = (integer == null) ? 0 : integer;
        final StringC priceRate = (StringC)this.stringCCache.get((Object)1);
        final StringC rateProb = (StringC)this.stringCCache.get((Object)2);
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
            pir.setIsCheap(1);
        }
        else {
            pir.setIsCheap(0);
        }
        final int finalPrice = (int)Math.round(finalRate * basePrice);
        pir.setPrice(finalPrice);
    }
    
    @Transactional
    @Override
    public byte[] buyItem(final PlayerDto playerDto, final int itemId) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        final PlayerItemRefresh pir = this.playerItemRefreshDao.getPlayerItemRefresh(playerId, itemId);
        if (pir == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_ITEM_NOT_REFRESH);
        }
        if (pir.getBought() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_ITEM_ALREADY_HAVE);
        }
        String itemName = "";
        if (pir.getType() == 0 && pir.getIsGold() == 0) {
            final BuildingDrawing buildingDrawing = (BuildingDrawing)this.buildingDrawingCache.get((Object)pir.getItemId());
            if (buildingDrawing == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_ITEM);
            }
            this.buildingService.buyBluePrintById(playerId, pir.getItemId());
            itemName = buildingDrawing.getName();
            final int copper = pir.getPrice();
            if (!this.playerResourceDao.consumeCopper(playerId, copper, "\u8d2d\u4e70\u88c5\u5907\u6263\u9664\u94f6\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
            this.playerItemRefreshDao.buyItem(playerId, itemId);
        }
        else if (itemId > 8) {
            final Equip item = (Equip)this.equipCache.get((Object)itemId);
            if (item == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_ITEM);
            }
            itemName = item.getName();
            final int style = this.getItemStyle(item.getId());
            final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
            if (style == 1) {
                if (cs[18] != '1') {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
                }
            }
            else if (style == 2 && cs[17] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
            final int nowStyleItemNum = this.getNowItemNum(playerId, style);
            if (nowStyleItemNum >= maxNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_STORE_NUM_TOP);
            }
            if (pir.getIsGold() == 1) {
                final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)8);
                if (player.getConsumeLv() < ci.getLv()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
                }
                if (!this.playerDao.consumeGold(player, pir.getPrice(), ci.getName())) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                }
            }
            else {
                final int copper2 = pir.getPrice();
                if (!this.playerResourceDao.consumeCopper(playerId, copper2, "\u8d2d\u4e70\u88c5\u5907\u6263\u9664\u94f6\u5e01")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                }
            }
            this.playerItemRefreshDao.buyItem(playerId, itemId);
            final StoreHouse sh = new StoreHouse();
            sh.setItemId(itemId);
            sh.setPlayerId(playerId);
            sh.setLv(item.getDefaultLevel());
            sh.setOwner(0);
            sh.setType(1);
            sh.setGoodsType(item.getType());
            sh.setAttribute(new StringBuilder().append(item.getAttribute()).toString());
            sh.setQuality(item.getQuality());
            sh.setGemId(0);
            sh.setNum(1);
            sh.setState(0);
            sh.setRefreshAttribute(pir.getRefreshAttribute());
            sh.setQuenchingTimes(0);
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
            StoreService.logger.info(LogUtil.formatEquipLog(player, "+", "\u83b7\u5f97", true, item, sh, LocalMessages.T_LOG_EQUIP_6));
            this.sendNotice(item, pir, playerDto);
            TaskMessageHelper.sendStoreBuyTaskMessage(playerId, 1);
            TaskMessageHelper.sendStoreBuySTaskMessage(playerId);
            TaskMessageHelper.sendEquipTaskMessage(playerId);
        }
        else {
            final Treasure treasure = (Treasure)this.treasureCache.get((Object)itemId);
            if (treasure == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_ITEM);
            }
            final char[] cs2 = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
            if (treasure.getType() == 3) {
                if (cs2[18] != '1') {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
                }
            }
            else if (treasure.getType() == 4 && cs2[17] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
            itemName = treasure.getName();
            if (this.playerTreasureDao.getPlayerTreasure(playerId, itemId) != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_ITEM_ALREADY_HAVE);
            }
            this.playerItemRefreshDao.buyItem(playerId, itemId);
            final PlayerTreasure pt = new PlayerTreasure();
            pt.setPlayerId(playerId);
            pt.setTreasureId(treasure.getId());
            this.playerTreasureDao.create(pt);
            this.battleDataCache.addTreasureEffect(playerId, treasure.getEffect());
            TaskMessageHelper.sendTreasureMessage(playerId);
            StoreService.logger.info(LogUtil.formatTreasureLog(player, "+", "\u83b7\u5f97", treasure, LocalMessages.T_LOG_TREASURE_1));
        }
        final int curMaxQuality = this.equipSuitCache.getMaxQuality(this.getItemStyle(itemId), player.getPlayerLv());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("msg", (Object)MessageFormatter.format(LocalMessages.T_ITEM_BUY_SUCCESS, new Object[] { itemName }));
        doc.createElement("nowItemNum", this.getNowItemNum(playerId, this.getItemStyle(itemId)));
        doc.createElement("maxItemNum", maxNum);
        doc.createElement("curMaxQuality", curMaxQuality);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] lockItem(final int playerId, final int itemId) {
        final PlayerItemRefresh pir = this.playerItemRefreshDao.getPlayerItemRefresh(playerId, itemId);
        if (pir != null) {
            this.playerItemRefreshDao.lockItem(pir.getVId());
        }
        final List<PlayerItemRefresh> list = this.playerItemRefreshDao.getListByPlayerId(playerId);
        final int style = this.getItemStyle(itemId);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(list, style, playerId, ci));
    }
    
    @Override
    public byte[] unlockItem(final int playerId, final int itemId) {
        final PlayerItemRefresh pir = this.playerItemRefreshDao.getPlayerItemRefresh(playerId, itemId);
        if (pir != null && pir.getLocked() == 1) {
            this.playerItemRefreshDao.unlockItem(pir.getVId(), new Date());
        }
        final List<PlayerItemRefresh> list = this.playerItemRefreshDao.getListByPlayerId(playerId);
        final int style = this.getItemStyle(itemId);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(list, style, playerId, ci));
    }
    
    @Transactional
    @Override
    public byte[] cdRecover(final int playerId, final int style) {
        if (style < 1 || style > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (style == 1) {
            if (cs[18] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
        }
        else if (style == 2 && cs[17] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        final Date nowDate = new Date();
        Date nextDate = new Date();
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        if (playerStore != null) {
            if (style == 1) {
                nextDate = playerStore.getNextEquipDate();
            }
            else if (style == 2) {
                nextDate = playerStore.getNextToolDate();
            }
        }
        final int gold = this.getCDRecoverCost(ci, nextDate, nowDate);
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Override
    public byte[] cdRecoverConfirm(final int playerId, final int style) {
        if (style < 1 || style > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (style == 1) {
            if (cs[18] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
            }
        }
        else if (style == 2 && cs[17] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final Player player = this.playerDao.read(playerId);
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        final Date nowDate = new Date();
        Date nextDate = new Date();
        if (playerStore != null) {
            if (style == 1) {
                nextDate = playerStore.getNextEquipDate();
            }
            else if (style == 2) {
                nextDate = playerStore.getNextToolDate();
            }
        }
        final int gold = this.getCDRecoverCost(ci, nextDate, nowDate);
        if (player.getConsumeLv() < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        if (!this.playerDao.consumeGold(player, gold, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        if (style == 1) {
            playerStore.setNextEquipDate(nowDate);
        }
        else if (style == 2) {
            playerStore.setNextToolDate(nowDate);
        }
        this.playerStoreDao.updatePlayerStore(playerStore);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private int decideNextState(final int type, final int beforeState, final int playerLevel) {
        final List<StoreStat> list = this.storeStatCache.getStoreStatList(beforeState);
        final List<StoreStat> levelList = new ArrayList<StoreStat>();
        double sumPro = 0.0;
        for (int maxQuality = this.equipSuitCache.getMaxQuality(type, playerLevel), i = 0; i < maxQuality; ++i) {
            levelList.add(list.get(i));
            sumPro += list.get(i).getProb();
        }
        final int length = levelList.size();
        final double[] probs = new double[length];
        for (int j = 0; j < length; ++j) {
            if (j == 0) {
                probs[j] = levelList.get(j).getProb() / sumPro;
            }
            else {
                probs[j] = probs[j - 1] + levelList.get(j).getProb() / sumPro;
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
        return levelList.get(index).getNextStat();
    }
    
    private int getCDRecoverCost(final Chargeitem ci, final Date endDate, final Date nowDate) {
        return (int)Math.ceil(CDUtil.getCD(endDate, nowDate) * 1.0 / (ci.getParam() * 60000.0) * ci.getCost());
    }
    
    private void sendNotice(final Equip item, final PlayerItemRefresh pir, final PlayerDto playerDto) {
        final String gold = LocalMessages.T_COMM_10009;
        final String copper = LocalMessages.T_COMM_10004;
        if (item.getQuality() == 6) {
            final StringBuilder money = new StringBuilder();
            money.append(pir.getPrice());
            money.append((pir.getIsGold() == 1) ? gold : copper);
            final String msg = MessageFormatter.format(LocalMessages.T_STORE_BUY_NOTICE, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), ColorUtil.getYellowMsg(money.toString()), ColorUtil.getVioletMsg(item.getName()) });
            this.chatService.sendBigNotice("GLOBAL", playerDto, msg, null);
        }
        else if (item.getQuality() == 5) {
            final StringBuilder money = new StringBuilder();
            money.append(pir.getPrice());
            money.append((pir.getIsGold() == 1) ? gold : copper);
            final String msg = MessageFormatter.format(LocalMessages.T_STORE_BUY_NOTICE_2, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), ColorUtil.getYellowMsg(money.toString()), ColorUtil.getRedMsg(item.getName()) });
            this.chatService.sendBigNotice("GLOBAL", playerDto, msg, null);
        }
    }
    
    @Transactional
    @Override
    public void addLockId(final int playerId, final int itemId) {
        final PlayerStore playerStore = this.playerStoreDao.read(playerId);
        if (StringUtils.isBlank(playerStore.getLockEquipId())) {
            final StringBuilder sb = new StringBuilder().append(itemId);
            sb.append(";");
            this.playerStoreDao.updateLockId(playerId, sb.toString());
        }
        else {
            final String nowIds = playerStore.getLockEquipId();
            final String[] s = nowIds.split(";");
            String[] array;
            for (int length = (array = s).length, i = 0; i < length; ++i) {
                final String id = array[i];
                if (Integer.valueOf(id) == itemId) {
                    return;
                }
            }
            final StringBuilder sb2 = new StringBuilder().append(nowIds);
            sb2.append(itemId);
            sb2.append(";");
            this.playerStoreDao.updateLockId(playerId, sb2.toString());
        }
    }
    
    @Override
    public boolean hasPurpleEquip(final int playerId) {
        final List<StoreHouse> list = this.storeHouseDao.getMilitaryEquipList(playerId, 6);
        return list.size() > 0;
    }
    
    @Override
    public byte[] getEquipSuitTipInfo(final int playerId, final int style) {
        if (style < 1 || style > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerId);
        final List<EquipSuit> equipSuits = this.equipSuitCache.getModels();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("suits");
        for (final EquipSuit suit : equipSuits) {
            if (suit.getType() == style && suit.getName() != null) {
                if (suit.getName().isEmpty()) {
                    continue;
                }
                doc.startObject();
                doc.createElement("level", suit.getMinChiefLv());
                doc.createElement("name", suit.getName());
                doc.createElement("isOpen", player.getPlayerLv() >= suit.getMinChiefLv());
                doc.createElement("quality", suit.getQuality());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public int checkSuitOpen(final Integer playerLv, final JsonDocument doc, final int playerId) {
        final List<EquipSuit> suits = this.equipSuitCache.getQualityEquipMap().get(playerLv);
        doc.startArray("openSuitName");
        boolean isRightLevel = false;
        if (suits != null) {
            final StringBuffer sbBuffer = new StringBuffer();
            final PlayerStore playerStore = this.playerStoreDao.read(playerId);
            if (playerStore != null) {
                sbBuffer.append(playerStore.getUnrefreshedEquip());
            }
            for (final EquipSuit suit : suits) {
                if (sbBuffer.length() != 0) {
                    sbBuffer.append(";");
                }
                sbBuffer.append(suit.getEquipList());
                doc.createElement(suit.getName());
                isRightLevel = true;
            }
            String updateString = sbBuffer.toString();
            if (sbBuffer.length() >= 80) {
                final int start = sbBuffer.indexOf(";", sbBuffer.length() / 2);
                updateString = sbBuffer.substring(start + 1);
            }
            this.playerStoreDao.updateUnrefreshEquip(playerId, updateString);
            doc.endArray();
            if (isRightLevel) {
                final List<Integer> leveList = this.equipSuitCache.getLevelList();
                for (int i = 0; i < leveList.size(); ++i) {
                    if (playerLv.equals(leveList.get(i)) && i != leveList.size() - 1) {
                        return leveList.get(i + 1);
                    }
                }
            }
            return 0;
        }
        doc.endArray();
        return 0;
    }
    
    class ComparatorItemType implements Comparator
    {
        @Override
        public int compare(final Object o1, final Object o2) {
            final PlayerItemRefresh item1 = (PlayerItemRefresh)o1;
            final PlayerItemRefresh item2 = (PlayerItemRefresh)o2;
            final int type1 = item1.getType();
            final int type2 = item2.getType();
            if (item1.getType() >= 6 && item2.getType() >= 6) {
                if (type1 >= type2) {
                    return 1;
                }
                return 0;
            }
            else {
                final int sortIndex1 = (type1 == 0) ? 100 : (type1 % 2 * 10 + 10 - type1);
                final int sortIndex2 = (type2 == 0) ? 100 : (type2 % 2 * 10 + 10 - type2);
                if (sortIndex1 >= sortIndex2) {
                    return 0;
                }
                return 1;
            }
        }
    }
    
    class IntimacyComparator implements Comparator
    {
        @Override
        public int compare(final Object o1, final Object o2) {
            final Equip e1 = (Equip)o1;
            final Equip e2 = (Equip)o2;
            if (e1.getIntimacyGroup() >= e2.getIntimacyGroup()) {
                return 0;
            }
            return 1;
        }
    }
}
