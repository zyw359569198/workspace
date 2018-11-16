package com.reign.gcld.market.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.market.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.gcld.market.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.log.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;
import java.io.*;

@Component("marketService")
public class MarketService implements IMarketService, InitializingBean
{
    @Autowired
    private IPlayerMarketDao playerMarketDao;
    @Autowired
    private MarketProductsCache marketProductsCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private CCache cCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private MarketDegreeCache marketDegreeCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private WorldCitySpecialCache worldCitySpecialCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private CityDataCache cityDataCache;
    @Autowired
    private MarketIronCache marketIronCache;
    @Autowired
    private DataPushCenterUtil dataPushCenterUtil;
    private static final Logger log;
    private static final Logger timerLog;
    private static String[] messageList;
    private static int msgIndex;
    private static Map<Integer, List<MarketProducts>> optionalListMap;
    
    static {
        log = CommonLog.getLog(MarketService.class);
        timerLog = new TimerLogger();
        MarketService.messageList = new String[10];
        MarketService.msgIndex = 0;
        MarketService.optionalListMap = new HashMap<Integer, List<MarketProducts>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.refreshMarket();
    }
    
    @Transactional
    @Override
    public byte[] getMarketInfo(final PlayerDto playerDto) {
        final Date nowDate = new Date();
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[27] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MAKET_FUNCITON_NOT_OPEN);
        }
        PlayerMarket pm = this.playerMarketDao.read(playerId);
        if (pm == null) {
            this.openMarketFunction(playerDto);
            pm = this.playerMarketDao.read(playerId);
        }
        if (StringUtils.isBlank(pm.getShowInfo())) {
            this.refreshShowInfo(pm, playerDto.playerLv);
        }
        final Calendar cg = Calendar.getInstance();
        cg.set(12, 0);
        cg.set(13, 0);
        cg.set(14, 0);
        final Date nowHourSharp = cg.getTime();
        if (pm.getRefreshTime().before(nowHourSharp)) {
            this.refreshShowInfo(pm, playerDto.playerLv);
        }
        cg.add(11, 1);
        final Date nextHourSharp = cg.getTime();
        final long refreshCD = CDUtil.getCD(nextHourSharp, nowDate);
        String[] ids = new String[0];
        if (!StringUtils.isBlank(pm.getShowInfo())) {
            ids = pm.getShowInfo().split(",");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("products");
        int index = 1;
        String[] array;
        for (int length = (array = ids).length, j = 0; j < length; ++j) {
            final String id = array[j];
            doc.startObject();
            doc.createElement("id", index);
            final int itemId = Integer.valueOf(id);
            if (itemId <= 90000) {
                double rate = 1.0;
                rate += this.cityEffectCache.getCityEffect(playerDto.forceId, 1) / 100.0;
                final MarketProducts product = (MarketProducts)this.marketProductsCache.get((Object)itemId);
                if ("recruit_token".equals(product.getItemType())) {
                    rate = 1.0;
                }
                doc.createElement("itemType", product.getItemType());
                doc.createElement("itemNum", (int)(product.getItemNum() * rate));
                doc.createElement("costType", product.getCostType());
                doc.createElement("costNum", (int)(product.getCostNum() * rate));
                doc.createElement("quality", product.getQuality());
            }
            else {
                final MarketIron mi = (MarketIron)this.marketIronCache.get((Object)(itemId - 90000));
                doc.createElement("itemType", "iron");
                doc.createElement("itemNum", mi.getItemNum());
                doc.createElement("costType", "copper");
                doc.createElement("costNum", mi.getCostNum());
                doc.createElement("quality", mi.getQuality());
            }
            ++index;
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("forcast");
        for (int i = 0; i < MarketService.messageList.length; ++i) {
            if (!StringUtils.isBlank(MarketService.messageList[i])) {
                doc.startObject();
                doc.createElement("content", MarketService.messageList[i]);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("nowBuyNum", (int)(double)pm.getCanbuyNum());
        doc.createElement("maxBuyNum", 24);
        final double num = 0.5 + this.techEffectCache.getTechEffect3(playerId, 2);
        doc.createElement("cycle", (int)(30.0 / num));
        doc.createElement("refreshCD", refreshCD);
        doc.startArray("specialCities");
        final Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(1);
        if (cityId != null) {
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("cityName", ((WorldCity)this.worldCityCache.get((Object)cityId)).getName());
            doc.createElement("hasSpecialCity", this.cityDataCache.hasCity(playerDto.forceId, cityId) ? 1 : 0);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] buyMarketProduct(final int id, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        if (id < 1 || id > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerMarket pm = this.playerMarketDao.read(playerId);
        if (pm == null || StringUtils.isBlank(pm.getShowInfo())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pm.getCanbuyNum() < 1.0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MAKET_NO_ENOUGH_NUM);
        }
        final String[] ids = pm.getShowInfo().split(",");
        MarketProducts product = null;
        MarketIron mi = null;
        int index = 0;
        final String[] array;
        final int length = (array = ids).length;
        int i = 0;
        while (i < length) {
            final String itemId = array[i];
            if (++index == id) {
                final int temp = Integer.valueOf(itemId);
                if (temp <= 90000) {
                    product = (MarketProducts)this.marketProductsCache.get((Object)temp);
                    break;
                }
                mi = (MarketIron)this.marketIronCache.get((Object)(temp - 90000));
                break;
            }
            else {
                ++i;
            }
        }
        if (product == null && mi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final String itemType = (product != null) ? product.getItemType() : "iron";
        final String costType = (product != null) ? product.getCostType() : "copper";
        int costNum = (product != null) ? product.getCostNum() : mi.getCostNum();
        if (mi == null) {
            double rate = 1.0;
            rate += this.cityEffectCache.getCityEffect(playerDto.forceId, 1) / 100.0;
            if ("recruit_token".equals(product.getItemType())) {
                rate = 1.0;
            }
            costNum *= (int)rate;
        }
        if (costType.equalsIgnoreCase("gold")) {
            if (!this.playerDao.consumeGold(player, costNum, "\u96c6\u5e02\u8d2d\u4e70\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        else if (costType.equalsIgnoreCase("copper")) {
            if (!this.playerResourceDao.consumeCopper(playerId, costNum, "\u96c6\u5e02\u8d2d\u4e70\u6d88\u8017\u94f6\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
        }
        else if (costType.equalsIgnoreCase("food")) {
            if (!this.playerResourceDao.consumeFood(playerId, costNum, "\u96c6\u5e02\u8d2d\u4e70\u6d88\u8017\u7cae\u98df")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
            }
        }
        else if (costType.equalsIgnoreCase("lumber")) {
            if (!this.playerResourceDao.consumeWood(playerId, costNum, "\u96c6\u5e02\u8d2d\u4e70\u6d88\u8017\u6728\u6750")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
            }
        }
        else if (costType.equalsIgnoreCase("iron") && !this.playerResourceDao.consumeIron(playerId, costNum, "\u96c6\u5e02\u8d2d\u4e70\u6d88\u8017\u9554\u94c1")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
        }
        int itemNum = (product != null) ? product.getItemNum() : mi.getItemNum();
        if (mi == null) {
            double rate2 = 1.0;
            rate2 += this.cityEffectCache.getCityEffect(playerDto.forceId, 1) / 100.0;
            if ("recruit_token".equals(product.getItemType())) {
                rate2 = 1.0;
            }
            itemNum *= (int)rate2;
        }
        String itemName = "";
        if (itemType.equalsIgnoreCase("gold")) {
            this.playerDao.addSysGold(player, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u91d1\u5e01");
            itemName = LocalMessages.T_COMM_10009;
            final String msg = MessageFormatter.format(LocalMessages.BROADCAST_MARKET, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(itemNum) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
        }
        else if (itemType.equalsIgnoreCase("copper")) {
            this.playerResourceDao.addCopperIgnoreMax(playerId, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u94f6\u5e01", true);
            itemName = LocalMessages.T_COMM_10004;
        }
        else if (itemType.equalsIgnoreCase("food")) {
            this.playerResourceDao.addFoodIgnoreMax(playerId, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u7cae\u98df");
            itemName = LocalMessages.T_COMM_10017;
        }
        else if (itemType.equalsIgnoreCase("lumber")) {
            this.playerResourceDao.addWoodIgnoreMax(playerId, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u6728\u6750", true);
            itemName = LocalMessages.T_COMM_10005;
        }
        else if (itemType.equalsIgnoreCase("iron")) {
            this.playerResourceDao.addIronIgnoreMax(playerId, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u9554\u94c1", true);
            itemName = LocalMessages.T_COMM_10018;
        }
        else if (itemType.equalsIgnoreCase("recruit_token")) {
            this.playerAttributeDao.addRecruitToken(playerId, itemNum, "\u96c6\u5e02\u8d2d\u4e70\u83b7\u5f97\u52df\u5175\u4ee4");
            itemName = LocalMessages.T_COMM_10022;
        }
        this.playerMarketDao.minuseCanbuyNum(playerId);
        final String message = MessageFormatter.format(LocalMessages.T_MARKET_NOTICE, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), itemNum, itemName });
        MarketService.messageList[MarketService.msgIndex] = message;
        MarketService.msgIndex = (MarketService.msgIndex + 1) % 10;
        this.refreshShowInfo(pm, playerDto.playerLv);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", itemType);
        doc.createElement("addNum", itemNum);
        doc.endObject();
        TaskMessageHelper.sendMarketBuyTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void refreshShowInfo(final PlayerMarket pm, final int playerLv) {
        final Date nowDate = new Date();
        final StringBuilder sb = new StringBuilder();
        int num = 0;
        final List<MarketProducts> optionalList = this.getOptionalList(playerLv);
        if (optionalList == null) {
            MarketService.log.error("\u96c6\u5e02\u5237\u4e0d\u51fa\u5546\u54c1[playerLv:" + playerLv);
            return;
        }
        if (optionalList.size() < 3) {
            while (num < 3) {
                for (final MarketProducts mp : optionalList) {
                    sb.append(mp.getId());
                    sb.append(",");
                    if (++num == 3) {
                        break;
                    }
                }
                if (num == 3) {
                    break;
                }
            }
        }
        else {
            int i;
            for (int optionSize = optionalList.size(), start = i = WebUtil.nextInt(optionSize); i < start + optionSize; ++i) {
                final int index = i % optionSize;
                final MarketProducts product = optionalList.get(index);
                sb.append(product.getId());
                sb.append(",");
                if (++num == 3) {
                    break;
                }
            }
        }
        if (this.techEffectCache.getTechEffect(pm.getPlayerId(), 36) > 0) {
            final int degree = this.marketDegreeCache.getDegreeByPlayerLv(playerLv);
            final MarketDegree md = (MarketDegree)this.marketDegreeCache.get((Object)degree);
            if (md != null && WebUtil.nextDouble() < md.getIronProb()) {
                final MarketIron mi = this.marketIronCache.getMarketIronByDegree(degree);
                if (mi != null) {
                    final List<Integer> list = new ArrayList<Integer>(3);
                    list.add(90000 + mi.getId());
                    final String[] arrs = sb.toString().split(",");
                    for (int j = 0; j < 2; ++j) {
                        list.add(Integer.parseInt(arrs[j]));
                    }
                    Collections.shuffle(list);
                    sb.setLength(0);
                    for (final Integer id : list) {
                        sb.append(id);
                        sb.append(",");
                    }
                }
            }
        }
        pm.setShowInfo(sb.toString());
        pm.setRefreshTime(nowDate);
        this.playerMarketDao.updateInfo(pm.getPlayerId(), sb.toString(), nowDate);
    }
    
    private List<MarketProducts> getOptionalList(final int playerLv) {
        for (final MarketDegree md : this.marketDegreeCache.getModels()) {
            if (playerLv >= md.getMinLv() && playerLv <= md.getMaxLv()) {
                return MarketService.optionalListMap.get(md.getDegree());
            }
        }
        return null;
    }
    
    @Override
    public void refreshMarket() {
        final long start = System.currentTimeMillis();
        for (final MarketDegree md : this.marketDegreeCache.getModels()) {
            final Map<Integer, List<MarketProducts>> map = this.marketProductsCache.getDegreeMap(md.getDegree());
            if (map == null) {
                continue;
            }
            final List<MarketProducts> optionalList = new ArrayList<MarketProducts>();
            final String[] qualityNum = md.getQList().split(",");
            int chooseNum = 0;
            for (int i = 1; i < qualityNum.length + 1; ++i) {
                final List<MarketProducts> list = map.get(i);
                chooseNum = Integer.valueOf(qualityNum[i - 1]);
                for (int j = 0; j < chooseNum; ++j) {
                    optionalList.add(this.getMarketProducts(list));
                }
            }
            Collections.shuffle(optionalList);
            MarketService.optionalListMap.put(md.getDegree(), optionalList);
        }
        MarketService.timerLog.info(LogUtil.formatThreadLog("MarketService", "refreshMarket", 2, System.currentTimeMillis() - start, ""));
    }
    
    private MarketProducts getMarketProducts(final List<MarketProducts> list) {
        if (1 == list.size()) {
            return list.get(0);
        }
        double rate = WebUtil.nextDouble();
        for (final MarketProducts mp : list) {
            if (rate < mp.getProb()) {
                return mp;
            }
            rate -= mp.getProb();
        }
        return list.get(0);
    }
    
    @Override
    public void addCanBuyNum() {
        final long start = System.currentTimeMillis();
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        final Date nowDate = new Date();
        final Map<Integer, Double> playerMarketInfoMap = new HashMap<Integer, Double>();
        final List<Integer> pIdList = new ArrayList<Integer>();
        List<Integer> numList = new ArrayList<Integer>();
        for (final PlayerDto dto : onlinePlayerList) {
            final int playerId = dto.playerId;
            final double num = 0.5 + this.techEffectCache.getTechEffect3(playerId, 2);
            playerMarketInfoMap.put(playerId, num);
            pIdList.add(playerId);
        }
        this.batchAddCanbuyNum(playerMarketInfoMap, nowDate, 24);
        numList = this.playerMarketDao.getCanBuyNumList(pIdList);
        for (int i = 0; i < numList.size(); ++i) {
            Players.push(pIdList.get(i), PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("marketCanBuyNum", numList.get(i)));
        }
        MarketService.timerLog.info(LogUtil.formatThreadLog("MarketService", "addCanBuyNum", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    public void batchAddCanbuyNum(final Map<Integer, Double> playerMarketInfoMap, final Date nowDate, final int marketMaxBuyNum) {
        this.playerMarketDao.batchAddCanbuyNum(playerMarketInfoMap, marketMaxBuyNum, nowDate);
    }
    
    @Transactional
    @Override
    public void supplyCanBuyNum(final int playerId) {
        final Date nowDate = new Date();
        final PlayerMarket pm = this.playerMarketDao.read(playerId);
        if (pm == null) {
            return;
        }
        if (pm.getGetBuynumTime() != null) {
            final double num = 0.5 + this.techEffectCache.getTechEffect3(playerId, 2);
            final int times = (int)((System.currentTimeMillis() - pm.getGetBuynumTime().getTime()) / 1800000L);
            if (times > 0) {
                this.playerMarketDao.addCanbuyNum(playerId, num * times, 24, nowDate);
                Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("marketCanBuyNum", pm.getCanbuyNum() + num * times));
            }
        }
    }
    
    @Transactional
    @Override
    public void openMarketFunction(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Date nowDate = new Date();
        PlayerMarket pm = this.playerMarketDao.read(playerId);
        if (pm == null) {
            pm = new PlayerMarket();
            pm.setPlayerId(playerId);
            pm.setCanbuyNum(10.0);
            pm.setGetBuynumTime(nowDate);
            this.playerMarketDao.create(pm);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("functionOpen", 27);
            doc.createElement("marketCanBuyNum", 10.0);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
    }
    
    @Override
    public byte[] getBlackMarketInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[43] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final float base = ((C)this.cCache.get((Object)"BlackMarket.BaseQuantity")).getValue();
        final float baseE = ((C)this.cCache.get((Object)"BlackMarket.BaseE")).getValue();
        int leftValue = (int)(base * baseE);
        int rightValue = (int)base;
        final int techAddition = this.techEffectCache.getTechEffect(playerId, 37);
        leftValue *= (int)(1.0 + techAddition / 100.0);
        rightValue *= (int)(1.0 + techAddition / 100.0);
        rightValue *= (int)(1.0 + this.cityEffectCache.getCityEffect2(playerDto.forceId, 2) / 100.0);
        doc.startArray("left");
        for (int i = 1; i <= 3; ++i) {
            doc.startObject();
            doc.createElement("type", i);
            doc.createElement("value", leftValue);
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("right");
        for (int i = 1; i <= 3; ++i) {
            doc.startObject();
            doc.createElement("type", i);
            doc.createElement("value", rightValue);
            doc.endObject();
        }
        doc.endArray();
        final Date date = pa.getBlackMarketCd();
        if (date != null) {
            final long cd = TimeUtil.now2specMs(date.getTime());
            if (cd > 0L) {
                doc.createElement("cd", cd);
                doc.createElement("cdMax", (int)(float)((C)this.cCache.get((Object)"BlackMarket.CDMax")).getValue() * 60000);
                final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)33);
                doc.createElement("gold", ci.getCost());
                doc.createElement("vipLimit", ci.getLv());
            }
        }
        doc.startArray("specialCities");
        Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(2);
        if (cityId != null) {
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.createElement("cityName", ((WorldCity)this.worldCityCache.get((Object)cityId)).getName());
            doc.createElement("hasSpecialCity", this.cityDataCache.hasCity(playerDto.forceId, cityId) ? 1 : 0);
            doc.endObject();
        }
        doc.endArray();
        cityId = this.worldCitySpecialCache.getCityIdCalByKey(2);
        doc.createElement("addRate", (int)(double)((WorldCitySpecial)this.worldCitySpecialCache.get((Object)cityId)).getPar2());
        doc.endObject();
        TaskMessageHelper.sendBlackMarketVisitTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] blackMarketTrade(final int left, final int right, final PlayerDto playerDto) {
        if (left < 1 || left > 3 || right < 1 || right > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (left == right) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLACK_MARKET_TYPE);
        }
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[43] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Date cdTime = this.playerAttributeDao.getBlackMarketCd(playerId);
        if (cdTime != null && TimeUtil.now2specMs(cdTime.getTime()) >= (int)(float)((C)this.cCache.get((Object)"BlackMarket.CDMax")).getValue() * 60000) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLACK_MARKET_IN_CD);
        }
        final Tuple<Boolean, String> tuple = this.resourceService.canAddResource(right, playerId);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final float base = ((C)this.cCache.get((Object)"BlackMarket.BaseQuantity")).getValue();
        final float baseE = ((C)this.cCache.get((Object)"BlackMarket.BaseE")).getValue();
        int leftValue = (int)(base * baseE);
        int rightValue = (int)base;
        final int techAddition = this.techEffectCache.getTechEffect(playerId, 37);
        leftValue *= (int)(1.0 + techAddition / 100.0);
        final int src;
        rightValue = (src = (int)(rightValue * (1.0 + techAddition / 100.0)));
        rightValue *= (int)(1.0 + this.cityEffectCache.getCityEffect2(playerDto.forceId, 2) / 100.0);
        switch (left) {
            case 1: {
                if (!this.playerResourceDao.consumeCopper(playerId, leftValue, "\u9ed1\u5e02\u4ea4\u6613\u6d88\u8017\u8d44\u6e90")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
                }
                break;
            }
            case 2: {
                if (!this.playerResourceDao.consumeWood(playerId, leftValue, "\u9ed1\u5e02\u4ea4\u6613\u6d88\u8017\u8d44\u6e90")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
                }
                break;
            }
            case 3: {
                if (!this.playerResourceDao.consumeFood(playerId, leftValue, "\u9ed1\u5e02\u4ea4\u6613\u6d88\u8017\u8d44\u6e90")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_MARKET_NOENOUGH_RESOURCE);
                }
                break;
            }
        }
        if (rightValue > 0) {
            switch (right) {
                case 1: {
                    this.playerResourceDao.addCopperIgnoreMax(playerId, rightValue, "\u9ed1\u5e02\u4ea4\u6613\u83b7\u5f97\u8d44\u6e90", true);
                    break;
                }
                case 2: {
                    this.playerResourceDao.addWoodIgnoreMax(playerId, rightValue, "\u9ed1\u5e02\u4ea4\u6613\u83b7\u5f97\u8d44\u6e90", true);
                    break;
                }
                case 3: {
                    this.playerResourceDao.addFoodIgnoreMax(playerId, rightValue, "\u9ed1\u5e02\u4ea4\u6613\u83b7\u5f97\u8d44\u6e90");
                    break;
                }
            }
        }
        float cd = ((C)this.cCache.get((Object)"BlackMarket.BaseCD")).getValue();
        cd -= (float)this.cityEffectCache.getCityEffect(playerDto.forceId, 2);
        if (cd < 0.0f) {
            cd = 0.0f;
        }
        final Date now = new Date();
        final Date baseTime = (cdTime == null) ? now : (cdTime.before(now) ? now : cdTime);
        this.playerAttributeDao.setBlackMarketCd(playerId, TimeUtil.specialAddMinutes(baseTime, (int)cd));
        final long endCD = this.playerAttributeDao.getBlackMarketCd(playerId).getTime();
        final long cdMs = TimeUtil.now2specMs(endCD);
        TaskMessageHelper.sendBlackMarketBuyMessage(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", right);
        doc.createElement("baseNum", src);
        doc.createElement("addNum", rightValue - src);
        doc.createElement("cd", cdMs);
        doc.createElement("cdMax", (int)(float)((C)this.cCache.get((Object)"BlackMarket.CDMax")).getValue() * 60000);
        doc.endObject();
        this.dataPushCenterUtil.addBlackMarketCd(playerId, endCD);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] blackMarketCdRecover(final int playerId) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[43] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Date cd = pa.getBlackMarketCd();
        long cdMs = 0L;
        if (cd == null || (cdMs = TimeUtil.now2specMs(cd.getTime())) <= 0L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLACK_MARKET_NOT_CD);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)33);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int gold = (int)Math.ceil(cdMs / 60000.0 / ci.getParam()) * ci.getCost();
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Transactional
    @Override
    public byte[] blackMarketCdRecoverConfirm(final int playerId) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[43] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Date cd = pa.getBlackMarketCd();
        long cdMs = 0L;
        if (cd == null || (cdMs = TimeUtil.now2specMs(cd.getTime())) <= 0L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLACK_MARKET_NOT_CD);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)33);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int gold = (int)Math.ceil(cdMs / 60000.0 / ci.getParam()) * ci.getCost();
        if (!this.playerDao.consumeGold(this.playerDao.read(playerId), gold, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final Date now = new Date();
        this.playerAttributeDao.setBlackMarketCd(playerId, now);
        this.dataPushCenterUtil.addBlackMarketCd(playerId, now.getTime());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
