package com.reign.gcld.treasure.service;

import org.springframework.stereotype.*;
import com.reign.gcld.treasure.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.treasure.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.store.domain.*;
import java.util.*;
import com.reign.kf.comm.entity.auction.*;

@Component("treasureService")
public class TreasureService implements ITreasureService
{
    @Autowired
    private IPlayerTreasureDao playerTreasureDao;
    @Autowired
    private TreasureCache treasureCache;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    private static final DayReportLogger logger;
    
    static {
        logger = new DayReportLogger();
    }
    
    @Override
    public byte[] getTreasures(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final List<PlayerTreasure> list = this.playerTreasureDao.getPlayerTreasures(playerId);
        final Map<Integer, PlayerTreasure> ownedMap = new HashMap<Integer, PlayerTreasure>();
        for (final PlayerTreasure pt : list) {
            ownedMap.put(pt.getTreasureId(), pt);
        }
        final List<Treasure> treasures = this.treasureCache.getModels();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("treasures");
        for (final Treasure treasure : treasures) {
            doc.startObject();
            doc.createElement("id", treasure.getId());
            doc.createElement("position", treasure.getPos());
            doc.createElement("name", treasure.getName());
            doc.createElement("pic", treasure.getPic());
            if (ownedMap.containsKey(treasure.getId())) {
                doc.createElement("tips", treasure.getTipsOwned());
                doc.createElement("owned", true);
            }
            else {
                doc.createElement("tips", treasure.getTipsLack());
                doc.createElement("owned", false);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        TaskMessageHelper.sendTreasureVisitMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public Treasure tryGetTreasure(final PlayerDto playerDto, final int type, final double dropRate) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[20] != '1') {
            return null;
        }
        final double rate = WebUtil.nextDouble();
        if (rate > dropRate) {
            return null;
        }
        final List<Treasure> optionalList = this.treasureCache.getTreasuresByType(type);
        final List<PlayerTreasure> ownedList = this.playerTreasureDao.getPlayerTreasures(playerId);
        final Map<Integer, PlayerTreasure> ownedMap = new HashMap<Integer, PlayerTreasure>();
        for (final PlayerTreasure pt : ownedList) {
            ownedMap.put(pt.getTreasureId(), pt);
        }
        Treasure result = null;
        for (final Treasure t : optionalList) {
            if (ownedMap.containsKey(t.getId())) {
                continue;
            }
            final PlayerTreasure pt2 = new PlayerTreasure();
            pt2.setPlayerId(playerId);
            pt2.setTreasureId(t.getId());
            this.playerTreasureDao.create(pt2);
            result = t;
            this.battleDataCache.addTreasureEffect(playerId, t.getEffect());
            String getReason = "";
            if (type == 2) {
                getReason = LocalMessages.T_LOG_TREASURE_3;
            }
            else if (type == 1) {
                getReason = LocalMessages.T_LOG_TREASURE_2;
            }
            else if (type == 5) {
                getReason = LocalMessages.T_LOG_TREASURE_4;
            }
            TaskMessageHelper.sendTreasureMessage(playerId);
            TreasureService.logger.info(LogUtil.formatTreasureLog(player, "+", "\u83b7\u5f97", t, getReason));
            break;
        }
        return result;
    }
    
    @Transactional
    @Override
    public Tuple<Integer, GeneralTreasure> tryGetGeneralTreasure(final PlayerDto playerDto, final int id, final boolean special, final int param1, final int param2, final boolean min, final String reason) {
        final Tuple<Integer, GeneralTreasure> tuple = new Tuple();
        tuple.left = 0;
        tuple.right = null;
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        final int currentNum = this.storeHouseDao.getCountByPlayerId(playerId);
        GeneralTreasure generalTreasure = this.generalTreasureCache.getGeneralTreasure(playerDto.playerLv);
        if (id > 0) {
            generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)id);
        }
        final StringBuilder attb = new StringBuilder();
        if (generalTreasure.getType() == 1) {
            if (special) {
                attb.append(param1).append(",").append(param2);
            }
            else if (min) {
                attb.append(generalTreasure.getIntMin()).append(",").append(generalTreasure.getPolMin());
            }
            else {
                attb.append(generalTreasure.getIntMin() + WebUtil.nextInt(generalTreasure.getIntMax() + 1 - generalTreasure.getIntMin())).append(",").append(generalTreasure.getPolMin() + WebUtil.nextInt(generalTreasure.getPolMax() + 1 - generalTreasure.getPolMin()));
            }
        }
        else if (special) {
            attb.append(param1).append(",").append(param2);
        }
        else if (min) {
            attb.append(generalTreasure.getLeaMin()).append(",").append(generalTreasure.getStrMin());
        }
        else {
            attb.append(generalTreasure.getLeaMin() + WebUtil.nextInt(generalTreasure.getLeaMax() + 1 - generalTreasure.getLeaMin())).append(",").append(generalTreasure.getStrMin() + WebUtil.nextInt(generalTreasure.getStrMax() + 1 - generalTreasure.getStrMin()));
        }
        if (currentNum < maxNum) {
            final StoreHouse sh = new StoreHouse();
            sh.setType(3);
            sh.setGoodsType(generalTreasure.getType());
            sh.setItemId(generalTreasure.getId());
            sh.setLv(0);
            sh.setPlayerId(playerId);
            sh.setOwner(0);
            sh.setQuality(generalTreasure.getQuality());
            sh.setGemId(0);
            sh.setAttribute(attb.toString());
            sh.setNum(1);
            sh.setState(0);
            sh.setRefreshAttribute("");
            sh.setQuenchingTimes(0);
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
            tuple.left = 1;
            TreasureService.logger.info(LogUtil.formatGeneralTreasureLog(this.playerDao.read(playerId), "+", "\u83b7\u5f97", generalTreasure, reason));
        }
        else {
            final StoreHouseSell shs = new StoreHouseSell();
            shs.setType(3);
            shs.setGoodsType(generalTreasure.getType());
            shs.setItemId(generalTreasure.getId());
            shs.setLv(0);
            shs.setPlayerId(playerId);
            shs.setQuality(generalTreasure.getQuality());
            shs.setGemId(0);
            shs.setAttribute(attb.toString());
            shs.setSellTime(new Date());
            shs.setNum(1);
            shs.setRefreshAttribute("");
            shs.setQuenchingTimes(0);
            this.storeHouseSellDao.create(shs);
            final int copper = generalTreasure.getCopperPrice();
            this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u4ed3\u5e93\u6ee1\u653e\u5165\u56de\u8d2d\u589e\u52a0\u94f6\u5e01,\u7269\u54c1\u6765\u6e90:" + reason, true);
            tuple.left = 2;
            TreasureService.logger.info(LogUtil.formatGeneralTreasureLog(this.playerDao.read(playerId), "+", "\u83b7\u5f97\u5e76\u8fdb\u5165\u56de\u8d2d", generalTreasure, reason));
        }
        tuple.right = generalTreasure;
        return tuple;
    }
    
    @Override
    public boolean autionFailBackToBag(final Integer itemVid) {
        final StoreHouse sh = this.storeHouseDao.read(itemVid);
        final int playerId = sh.getPlayerId();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        final int currentNum = this.storeHouseDao.getCountByPlayerId(playerId);
        if (currentNum < maxNum) {
            this.storeHouseDao.changeState(itemVid, 0);
            return true;
        }
        this.storeHouseDao.deleteById(itemVid);
        final StoreHouseSell shs = new StoreHouseSell();
        shs.setType(3);
        shs.setGoodsType(sh.getGoodsType());
        shs.setItemId(sh.getItemId());
        shs.setLv(sh.getLv());
        shs.setPlayerId(playerId);
        shs.setQuality(sh.getQuality());
        shs.setGemId(0);
        shs.setAttribute(sh.getAttribute());
        shs.setSellTime(new Date());
        shs.setNum(1);
        shs.setSpecialSkillId(sh.getSpecialSkillId());
        shs.setRefreshAttribute(sh.getRefreshAttribute());
        final int q = (sh.getQuenchingTimes() == null) ? 0 : sh.getQuenchingTimes();
        shs.setQuenchingTimes(q);
        this.storeHouseSellDao.create(shs);
        final int copper = 100;
        this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u51fa\u552e\u88c5\u5907\u589e\u52a0\u94f6\u5e01", true);
        return false;
    }
    
    @Transactional
    @Override
    public boolean putOneItemInBag(final int playerId, final NewAuctionGeneralEntity auctionGeneralEntity) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        int maxNum = 0;
        if (pa != null) {
            maxNum = pa.getMaxStoreNum();
        }
        final int currentNum = this.storeHouseDao.getCountByPlayerId(playerId);
        final GeneralTreasure generalTreasure = (GeneralTreasure)this.generalTreasureCache.get((Object)auctionGeneralEntity.getGeneralId());
        if (currentNum < maxNum) {
            final StoreHouse sh = new StoreHouse();
            sh.setType(3);
            sh.setGoodsType(generalTreasure.getType());
            sh.setItemId(generalTreasure.getId());
            sh.setLv(auctionGeneralEntity.getGeneralLv());
            sh.setPlayerId(playerId);
            sh.setOwner(0);
            sh.setQuality(generalTreasure.getQuality());
            sh.setGemId(0);
            final StringBuilder attb = new StringBuilder();
            if (generalTreasure.getType() == 1) {
                attb.append(auctionGeneralEntity.getIntelligence()).append(",").append(auctionGeneralEntity.getPolitics());
            }
            else {
                attb.append(auctionGeneralEntity.getLeader()).append(",").append(auctionGeneralEntity.getForces());
            }
            sh.setAttribute(attb.toString());
            sh.setNum(1);
            sh.setState(0);
            sh.setRefreshAttribute("");
            sh.setQuenchingTimes(0);
            sh.setBindExpireTime(0L);
            sh.setMarkId(0);
            this.storeHouseDao.create(sh);
            return true;
        }
        final StoreHouseSell shs = new StoreHouseSell();
        shs.setType(3);
        shs.setGoodsType(generalTreasure.getType());
        shs.setItemId(generalTreasure.getId());
        shs.setLv(auctionGeneralEntity.getGeneralLv());
        shs.setPlayerId(playerId);
        shs.setQuality(generalTreasure.getQuality());
        shs.setGemId(0);
        final StringBuilder attb = new StringBuilder();
        if (generalTreasure.getType() == 1) {
            attb.append(auctionGeneralEntity.getIntelligence()).append(",").append(auctionGeneralEntity.getPolitics());
        }
        else {
            attb.append(auctionGeneralEntity.getLeader()).append(",").append(auctionGeneralEntity.getForces());
        }
        shs.setAttribute(attb.toString());
        shs.setSellTime(new Date());
        shs.setRefreshAttribute("");
        shs.setQuenchingTimes(0);
        shs.setNum(1);
        this.storeHouseSellDao.create(shs);
        final int copper = 100;
        this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u51fa\u552e\u88c5\u5907\u589e\u52a0\u94f6\u5e01", true);
        return false;
    }
}
