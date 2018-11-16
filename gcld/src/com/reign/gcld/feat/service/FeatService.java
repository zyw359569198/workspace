package com.reign.gcld.feat.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.treasure.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.feat.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.feat.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.task.message.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.pay.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;

@Component("featService")
public class FeatService implements IFeatService
{
    private static final Logger errorLog;
    private static int ITEM_ID;
    private static int ITEM_ID_1701;
    private static Map<Integer, Integer> gTMap;
    @Autowired
    private IPlayerFeatRankDao playerFeatRankDao;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private TpFtTnumCache tpFtTnumCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private TpFtRankingCache tpFtRankingCache;
    @Autowired
    private TpFtTrewardCache tpFtTrewardCache;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private ITreasureService treasureService;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private BroadCastUtil broadCastUtil;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private CCache cCache;
    @Autowired
    private TpFtTironCache tpFtTironCache;
    @Autowired
    private IYxOperation yxOperation;
    @Autowired
    private PlayerVipTxDao playerVipTxDao;
    @Autowired
    private IFeatBuildingDao featBuildingDao;
    
    static {
        errorLog = CommonLog.getLog(FeatService.class);
        FeatService.ITEM_ID = 101;
        FeatService.ITEM_ID_1701 = 1701;
        (FeatService.gTMap = new HashMap<Integer, Integer>()).put(1, 29);
        FeatService.gTMap.put(2, 26);
        FeatService.gTMap.put(3, 27);
        FeatService.gTMap.put(4, 28);
        FeatService.gTMap.put(5, 30);
        FeatService.gTMap.put(6, 31);
    }
    
    @Override
    public void openFeatRecord(final int playerId) {
        PlayerFeatRank pfr = this.playerFeatRankDao.read(playerId);
        if (pfr == null) {
            pfr = new PlayerFeatRank();
            pfr.setPlayerId(playerId);
            pfr.setKillNum(0);
            pfr.setOccupyNum(0);
            pfr.setAssistNum(0);
            pfr.setCheerNum(0);
            pfr.setKillFeat(0);
            pfr.setTotalFeat(0);
            pfr.setLastRank(0);
            pfr.setReceived(0);
            this.playerFeatRankDao.create(pfr);
        }
    }
    
    @Override
    public void openFeatBuilding(final int playerId) {
        FeatBuilding fb = this.featBuildingDao.read(playerId);
        if (fb == null) {
            fb = new FeatBuilding();
            fb.setPlayerId(playerId);
            fb.setFeat(0);
            this.featBuildingDao.create(fb);
        }
    }
    
    @Override
    public byte[] getRankInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.cs[31] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        boolean isTx = false;
        if (this.yxOperation.checkTencentPf(playerDto.yx)) {
            isTx = true;
        }
        final int featBoxNum = this.playerDragonDao.getFeatBoxNum(playerId);
        PlayerFeatRank pfr = this.playerFeatRankDao.read(playerId);
        if (pfr == null) {
            this.openFeatRecord(playerId);
            pfr = this.playerFeatRankDao.read(playerId);
        }
        final int feat = pfr.getTotalFeat();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int lv = 0;
        int count = 0;
        int needFeat = 0;
        boolean flag_needFeat = false;
        final int size = this.tpFtTnumCache.getModels().size();
        final double rate = 1.0 + this.techEffectCache.getTechEffect2(playerId, 34) / 100.0;
        for (int i = 1; i <= size; ++i) {
            final TpFtTnum tft = (TpFtTnum)this.tpFtTnumCache.get((Object)i);
            final int nNum = (int)(tft.getFeats() * rate);
            final int rNum = tft.getTNum();
            doc.startObject();
            doc.createElement("feat", nNum);
            doc.createElement("rewardNum", rNum);
            doc.endObject();
            ++count;
            if (feat >= nNum) {
                lv = count;
            }
            if (!flag_needFeat && feat < nNum) {
                needFeat = nNum - feat;
                flag_needFeat = true;
            }
        }
        doc.endArray();
        doc.startArray("ranks");
        final List<RankData> rdList = this.rankService.getFeatRank().getRankList(playerDto.forceId);
        int index = 1;
        for (final RankData rd : rdList) {
            if (index > 100) {
                break;
            }
            doc.startObject();
            doc.createElement("rank", (index++));
            doc.createElement("playerId", rd.playerId);
            final Player player = this.playerDao.read(rd.playerId);
            doc.createElement("playerLv", player.getPlayerLv());
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("feat", rd.value);
            if (isTx) {
                final PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerId);
                if (pvt != null) {
                    doc.createElement("isYellowVip", true);
                    doc.createElement("yellowVipLv", pvt.getYellowVipLv());
                }
            }
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("feat", feat);
        doc.createElement("featBoxNum", featBoxNum);
        doc.createElement("lv", lv + 1);
        doc.createElement("needFeat", needFeat);
        doc.createElement("maxFeat", this.tpFtTnumCache.getMaxFeat(rate));
        doc.createElement("occupy", pfr.getOccupyNum());
        doc.createElement("assist", pfr.getAssistNum());
        doc.createElement("cheer", pfr.getCheerNum());
        final int killNum = pfr.getKillNum();
        doc.createElement("killNum", killNum);
        final int killFeat = pfr.getKillFeat();
        int nextKillNum = 0;
        int default_rate = killFeat;
        boolean tips = false;
        if (feat < 1200) {
            default_rate = default_rate + needFeat - 10;
            tips = true;
        }
        for (int j = 0; j <= default_rate; j += 10) {
            nextKillNum += j * 40 + 10000;
        }
        doc.createElement("needKillNum", nextKillNum - killNum);
        doc.createElement("openCityFeat", (this.techEffectCache.getTechEffect(playerId, 52) > 0) ? 1 : 0);
        doc.createElement("occupyFeat", 100);
        doc.createElement("assistFeat", 40);
        doc.createElement("cheerFeat", 20);
        final int lastRank = pfr.getLastRank();
        doc.createElement("lastRank", lastRank);
        doc.createElement("lastCopper", this.tpFtRankingCache.getRewardCopper(lastRank));
        int iron = this.tpFtRankingCache.getRewardIron(lastRank);
        iron *= (int)(this.techEffectCache.getTechEffect(playerId, 33) / 100.0);
        doc.createElement("lastIron", iron);
        int exp = this.tpFtRankingCache.getRewardExp(lastRank);
        exp *= (int)(this.techEffectCache.getTechEffect(playerId, 51) / 100.0);
        doc.createElement("lastExp", exp);
        final int rank = this.rankService.getFeatRank().getRank(1, playerId, playerDto.forceId);
        doc.createElement("rank", rank);
        int needUpdateFeat = 0;
        if (rank > 1) {
            final RankData rd2 = this.rankService.getFeatRank().getRankNum(playerDto.forceId, rank - 2);
            if (rd2 != null) {
                needUpdateFeat = rd2.value - pfr.getTotalFeat();
                if (needUpdateFeat <= 0) {
                    needUpdateFeat = 1;
                }
            }
            else {
                FeatService.errorLog.error("class:FeatService#method:getRankInfo#playerId:" + playerId + "#rank:" + rank);
                needUpdateFeat = 1;
            }
        }
        doc.createElement("needUpdateFeat", needUpdateFeat);
        doc.createElement("received", (pfr.getReceived() != 0 || lastRank <= 0) ? 1 : 0);
        doc.createElement("copper", this.tpFtRankingCache.getRewardCopper(rank));
        iron = this.tpFtRankingCache.getRewardIron(rank);
        iron *= (int)(this.techEffectCache.getTechEffect(playerId, 33) / 100.0);
        doc.createElement("iron", iron);
        exp = this.tpFtRankingCache.getRewardExp(rank);
        exp *= (int)(this.techEffectCache.getTechEffect(playerId, 51) / 100.0);
        doc.createElement("exp", exp);
        doc.createElement("tips", tips);
        doc.createElement("boxLv", 1 + this.playerTechDao.getEffectSizeByTechKey(playerId, 34));
        doc.endObject();
        TaskMessageHelper.sendCheckDailyKillTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getBoxReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int featBoxNum = this.playerDragonDao.getFeatBoxNum(playerId);
        if (featBoxNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FEAT_RANK_NO_BOX);
        }
        this.playerDragonDao.useFeatBoxNum(playerId, "\u51cf\u5c11\u529f\u52cb\u699c\u5b9d\u7bb1");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final BuildingDrawing bd = this.buildingService.dropBluePrintByType(playerDto.playerId, 2);
        if (bd != null) {
            doc.createElement("type", 7);
            doc.createElement("pic", bd.getPic());
            doc.createElement("num", 1);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (this.techEffectCache.getTechEffect3(playerId, 34) > 0.0) {
            final float rate = WebUtil.nextFloat();
            final float value = ((C)this.cCache.get((Object)"Tp.Ft.tIronProb")).getValue();
            if (rate < value) {
                final TpFtTiron tft = this.tpFtTironCache.getTpFtTiron();
                final int num = tft.getIron();
                this.playerResourceDao.addIronIgnoreMax(playerId, num, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u53d6\u9554\u94c1", true);
                doc.createElement("type", 4);
                doc.createElement("num", num);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
        final TpFtTreward tft2 = this.tpFtTrewardCache.getTpFtTreward();
        int num2 = tft2.getNum();
        final int type = tft2.getType();
        int type2 = 0;
        if (tft2.getIsMulti() > 0) {
            num2 *= (int)(1.0 + this.techEffectCache.getTechEffect(playerId, 34) / 100.0);
        }
        if (1 == type) {
            this.playerService.updateExpAndPlayerLevel(playerId, num2, "\u529f\u52cb\u699c\u5b9d\u7bb1\u5956\u52b1\u7ecf\u9a8c");
        }
        else if (2 == type) {
            final Tuple<Integer, GeneralTreasure> tuple = this.treasureService.tryGetGeneralTreasure(playerDto, 0, false, 0, 0, false, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u5f97\u5fa1\u5b9d");
            if (tuple != null && tuple.right != null) {
                type2 = tuple.right.getId();
                if (3 == type2) {
                    this.broadCastUtil.sendLuminousCupBroadCast(playerId);
                }
            }
            else {
                FeatService.errorLog.error("class:FeatService#method:getBoxReward#no_generaltreasure#playerId:" + playerId);
            }
        }
        else if (3 == type) {
            this.playerAttributeDao.addRecruitToken(playerId, num2, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u5f97\u52df\u5175\u4ee4");
        }
        else if (4 == type) {
            this.playerResourceDao.addFoodIgnoreMax(playerId, num2, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u53d6\u7cae\u98df");
        }
        else if (5 == type) {
            this.playerResourceDao.addIronIgnoreMax(playerId, num2, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u53d6\u9554\u94c1", true);
        }
        else if (6 == type) {
            this.storeHouseService.gainSearchItems(FeatService.ITEM_ID, num2, playerDto, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
        }
        else {
            this.storeHouseService.gainItems(playerId, num2, FeatService.ITEM_ID_1701, "\u529f\u52cb\u699c\u5b9d\u7bb1\u83b7\u5f97\u5c6f\u7530\u4ee4");
        }
        doc.createElement("type", this.getDisplayType(type, type2));
        doc.createElement("num", num2);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getRankReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.cs[32] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        PlayerFeatRank pfr = this.playerFeatRankDao.read(playerId);
        if (pfr == null) {
            this.openFeatRecord(playerId);
            pfr = this.playerFeatRankDao.read(playerId);
        }
        final int lastRank = pfr.getLastRank();
        if (lastRank <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FEAT_RANK_NO_RANK);
        }
        if (1 == pfr.getReceived()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FEAT_RANK_RECEIVED);
        }
        this.playerFeatRankDao.received(playerId);
        final int copper = this.tpFtRankingCache.getRewardCopper(lastRank);
        if (copper > 0) {
            this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u529f\u52cb\u699c\u6392\u540d\u5956\u52b1\u94f6\u5e01", true);
        }
        else {
            FeatService.errorLog.error("class:FeatService#method:getRankReward#playerId:" + playerId + "#lastRank" + lastRank + "#copper" + copper);
        }
        int iron = this.tpFtRankingCache.getRewardIron(lastRank);
        iron *= (int)(this.techEffectCache.getTechEffect(playerId, 33) / 100.0);
        if (iron > 0) {
            this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u529f\u52cb\u699c\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
        }
        int exp = this.tpFtRankingCache.getRewardExp(lastRank);
        exp *= (int)(this.techEffectCache.getTechEffect(playerId, 51) / 100.0);
        if (exp > 0) {
            this.playerService.updateExpAndPlayerLevel(playerId, exp, "\u529f\u52cb\u699c\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("copper", copper);
        doc.createElement("iron", iron);
        doc.createElement("exp", exp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getDisplayType(final int type, final int type2) {
        if (1 == type) {
            return 6;
        }
        if (2 == type) {
            final Integer result = FeatService.gTMap.get(type2);
            if (result != null) {
                return result;
            }
            FeatService.errorLog.error("class:FeatService#method:getDisplayType#no_mapped#type2:" + type2);
            return 0;
        }
        else {
            if (3 == type) {
                return 9;
            }
            if (4 == type) {
                return 3;
            }
            if (5 == type) {
                return 4;
            }
            if (6 == type) {
                return 24;
            }
            return 91;
        }
    }
}
