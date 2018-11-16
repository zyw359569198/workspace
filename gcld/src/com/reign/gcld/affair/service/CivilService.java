package com.reign.gcld.affair.service;

import org.springframework.stereotype.*;
import com.reign.gcld.affair.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.affair.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

@Component("civilService")
public class CivilService implements ICivilService
{
    @Autowired
    private ICivilAffairDao civilAffairDao;
    @Autowired
    private OfficerAffairCache officerAffairCache;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private OfficerSpecialtyCache officerSpecialtyCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    private static final int AFFAIR_OUTPUT_COPPER = 1;
    private static final int AFFAIR_OUTPUT_FOOD = 2;
    private static final int AFFAIR_OUTPUT_WOOD = 3;
    private static final int AFFAIR_OUTPUT_IRON = 4;
    private static final int AFFAIR_OUTPUT_EXP = 5;
    private static final int AFFAIR_OUTPUT_GOLD = 9;
    
    @Transactional
    @Override
    public byte[] startAffair(final PlayerDto playerDto, final int generalId, final int affairId) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[48] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
        if (pgc == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_CIVIL);
        }
        final CivilAffair civilAffair = this.civilAffairDao.getAffair(playerId, generalId, affairId);
        if (civilAffair == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_AFFAIR);
        }
        final General general = (General)this.generalCache.get((Object)generalId);
        final int maxAffairNum = general.getQuality();
        final int nowRunningNum = this.civilAffairDao.getRunningAffairCount(playerId, generalId);
        if (nowRunningNum >= maxAffairNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AFFAIR_NUM_TOP);
        }
        if (civilAffair.getStartTime() != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AFFAIR_RUNNING);
        }
        final Date date = new Date();
        this.civilAffairDao.updageStartTime(playerId, generalId, affairId, date);
        TaskMessageHelper.sendOfficerAffairTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] stopAffair(final PlayerDto playerDto, final int generalId, final int affairId) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[48] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
        if (pgc == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_CIVIL);
        }
        final CivilAffair civilAffair = this.civilAffairDao.getAffair(playerId, generalId, affairId);
        if (civilAffair == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_AFFAIR);
        }
        if (civilAffair.getStartTime() == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AFFAIR_ALREADY_STOP);
        }
        final OfficerAffair officerAffair = (OfficerAffair)this.officerAffairCache.get((Object)civilAffair.getAffairId());
        if (officerAffair == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_AFFAIR);
        }
        final Date startTime = civilAffair.getStartTime();
        if (startTime == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AFFAIR_ALREADY_STOP);
        }
        final double rate = 1.0 + this.techEffectCache.getTechEffect(playerId, 5) / 100.0;
        final Tuple<Integer, Integer> result = this.receiveAffairReward(pgc, civilAffair, officerAffair, rate);
        TaskMessageHelper.sendOfficerHarvestTaskMessage(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("reward");
        if (result != null) {
            doc.startObject();
            doc.createElement("type", result.left);
            doc.createElement("count", result.right);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    private Tuple<Integer, Integer> receiveAffairReward(final PlayerGeneralCivil pgc, final CivilAffair civilAffair, final OfficerAffair officerAffair, final double rate) {
        final Tuple<Integer, Integer> tuple = new Tuple();
        final int playerId = pgc.getPlayerId();
        final int generalId = pgc.getGeneralId();
        final Player player = this.playerDao.read(playerId);
        final int affairId = civilAffair.getAffairId();
        final Date startTime = civilAffair.getStartTime();
        if (startTime == null) {
            return null;
        }
        final Date nowDate = new Date();
        final long cd = CDUtil.getCD(nowDate, startTime);
        final int outputType = officerAffair.getResourceOutputType();
        tuple.left = outputType;
        int times = (int)(cd / (officerAffair.getTime() * 60000L));
        if (times == 0) {
            this.civilAffairDao.updageStartTime(playerId, generalId, affairId, null);
            return null;
        }
        final int maxTimes = (int)(86400000L / (2L * (officerAffair.getTime() * 60000L)));
        times = Math.min(times, maxTimes);
        Date newStartTime = new Date(startTime.getTime() + times * (officerAffair.getTime() * 60000L));
        if (times == maxTimes) {
            newStartTime = null;
        }
        final int unitProduce = officerAffair.getResourceOutput() + (civilAffair.getLv() - 1) * officerAffair.getUpgradeOutputIncrease();
        int totalProduce = DataCastUtil.double2int(times * unitProduce * rate);
        final int addExp = officerAffair.getOfficerExpOutput() * times;
        final General general = (General)this.generalCache.get((Object)generalId);
        final OfficerSpecialty os = (OfficerSpecialty)this.officerSpecialtyCache.get((Object)general.getTacticId());
        if (os.getType() == outputType) {
            totalProduce *= (int)(Object)os.getMagnification();
        }
        tuple.right = totalProduce;
        if (totalProduce > 0) {
            switch (outputType) {
                case 1: {
                    this.playerResourceDao.addCopperIgnoreMax(playerId, totalProduce, "\u5185\u653f\u4e8b\u52a1\u83b7\u5f97\u94f6\u5e01", true);
                    break;
                }
                case 2: {
                    this.playerResourceDao.addFoodIgnoreMax(playerId, totalProduce, "\u5185\u653f\u4e8b\u52a1\u83b7\u5f97\u7cae\u98df");
                    break;
                }
                case 3: {
                    this.playerResourceDao.addWoodIgnoreMax(playerId, totalProduce, "\u5185\u653f\u4e8b\u52a1\u83b7\u5f97\u6728\u6750", true);
                    break;
                }
                case 4: {
                    this.playerResourceDao.addIronIgnoreMax(playerId, totalProduce, "\u5185\u653f\u4e8b\u52a1\u83b7\u5f97\u9554\u94c1", true);
                    break;
                }
                case 5: {
                    this.playerService.updateExpAndPlayerLevel(playerId, totalProduce, "\u5185\u653f\u4e8b\u52a1\u589e\u52a0\u7ecf\u9a8c");
                    break;
                }
                case 9: {
                    this.playerDao.addSysGold(player, totalProduce, "\u5185\u653f\u4e8b\u52a1\u5956\u52b1\u91d1\u5e01");
                    break;
                }
            }
        }
        if (addExp > 0) {
            this.updateExpAndCivilLevel(player.getPlayerLv(), pgc, addExp);
        }
        this.civilAffairDao.updageStartTime(playerId, generalId, affairId, newStartTime);
        return tuple;
    }
    
    private void upgradeAffair(final PlayerGeneralCivil pgc) {
        final List<CivilAffair> affairList = this.civilAffairDao.getAffairList(pgc.getPlayerId(), pgc.getGeneralId());
        for (final CivilAffair civilAffair : affairList) {
            final int affairId = civilAffair.getAffairId();
            final int nowLevel = civilAffair.getLv();
            final OfficerAffair affair = (OfficerAffair)this.officerAffairCache.get((Object)affairId);
            if (nowLevel >= affair.getMaxLevel()) {
                return;
            }
            final int needAdd = (pgc.getLv() - affair.getOpenLv()) / affair.getUpgradeInterval() + 1 - nowLevel;
            if (needAdd <= 0) {
                continue;
            }
            this.civilAffairDao.addAffairLevel(civilAffair.getVId(), needAdd);
        }
    }
    
    @Override
    public byte[] finishAllAffair(final int generalId, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[48] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        final PlayerGeneralCivil pgc = this.playerGeneralCivilDao.getCivil(playerId, generalId);
        final List<CivilAffair> affairList = this.civilAffairDao.getAffairList(playerId, generalId);
        final double rate = 1.0 + this.techEffectCache.getTechEffect(playerId, 5) / 100.0;
        for (final CivilAffair civilAffair : affairList) {
            final OfficerAffair officerAffair = (OfficerAffair)this.officerAffairCache.get((Object)civilAffair.getAffairId());
            final Tuple<Integer, Integer> result = this.receiveAffairReward(pgc, civilAffair, officerAffair, rate);
            if (result != null) {
                if (map.containsKey(result.left)) {
                    final int nowCount = map.get(result.left);
                    map.put(result.left, nowCount + result.right);
                }
                else {
                    map.put(result.left, result.right);
                }
                TaskMessageHelper.sendOfficerHarvestTaskMessage(playerId);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("reward");
        for (final Map.Entry<Integer, Integer> entry : map.entrySet()) {
            doc.startObject();
            doc.createElement("type", entry.getKey());
            doc.createElement("count", entry.getValue());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void tryAssignAffair(final PlayerGeneralCivil pgc) {
        final int playerId = pgc.getPlayerId();
        final int generalId = pgc.getGeneralId();
        final List<CivilAffair> affairList = this.civilAffairDao.getAffairList(playerId, generalId);
        final Map<Integer, CivilAffair> map = new HashMap<Integer, CivilAffair>();
        for (final CivilAffair civilAffair : affairList) {
            map.put(civilAffair.getAffairId(), civilAffair);
        }
        final List<OfficerAffair> totalList = this.officerAffairCache.getModels();
        for (final OfficerAffair affair : totalList) {
            if (pgc.getLv() >= affair.getOpenLv() && !map.containsKey(affair.getId())) {
                final CivilAffair civilAffair2 = new CivilAffair();
                civilAffair2.setPlayerId(playerId);
                civilAffair2.setGeneralId(generalId);
                civilAffair2.setAffairId(affair.getId());
                civilAffair2.setLv(1);
                civilAffair2.setStartTime(null);
                civilAffair2.setUpgradeShow(0);
                this.civilAffairDao.create(civilAffair2);
            }
        }
    }
    
    @Transactional
    @Override
    public boolean updateExpAndCivilLevel(final int playerLv, final PlayerGeneralCivil pgc, final int addExp) {
        boolean addActually = true;
        final int playerId = pgc.getPlayerId();
        final int generalId = pgc.getGeneralId();
        final long originalExp = pgc.getExp();
        int gFactor;
        int upNum;
        int upLv;
        long curExp;
        for (gFactor = ((General)this.generalCache.get((Object)generalId)).getUpExpS(), upNum = this.serialCache.get(gFactor, pgc.getLv()), upLv = 0, curExp = pgc.getExp(), curExp += addExp; curExp >= upNum; curExp -= upNum, upNum = this.serialCache.get(gFactor, pgc.getLv() + upLv)) {
            if (pgc.getLv() + upLv + 1 > playerLv) {
                curExp = upNum;
                break;
            }
            ++upLv;
        }
        if (upLv == 0 && originalExp == curExp) {
            addActually = false;
        }
        else {
            this.playerGeneralCivilDao.updateExpAndGlv(playerId, generalId, (int)curExp, upLv);
            pgc.setLv(pgc.getLv() + upLv);
            pgc.setExp(curExp);
            if (upLv > 0) {
                this.tryAssignAffair(pgc);
                this.upgradeAffair(pgc);
            }
        }
        return addActually;
    }
}
