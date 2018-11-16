package com.reign.gcld.slave.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.slave.domain.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.general.dto.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.util.*;

@Component("slaveService")
public class SlaveService implements ISlaveService
{
    @Autowired
    private IPlayerSlaveDao playerSlaveDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ISlaveholderDao slaveholderDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private PrisonCatchProbCache prisonCatchProbCache;
    @Autowired
    private PrisonLashRewardCache prisonLashRewardCache;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private PrisonLvCache prisonLvCache;
    @Autowired
    private OfficialCache officialCache;
    @Autowired
    private PrisonDegreeCache prisonDegreeCache;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private TechEffectCache techEffectCache;
    private Logger errorLog;
    private static final Logger timerLog;
    public static Map<Integer, String> nationIdNameMap;
    
    static {
        timerLog = new TimerLogger();
        (SlaveService.nationIdNameMap = new HashMap<Integer, String>()).put(1, LocalMessages.T_FORCE_WEI);
        SlaveService.nationIdNameMap.put(2, LocalMessages.T_FORCE_SHU);
        SlaveService.nationIdNameMap.put(3, LocalMessages.T_FORCE_WU);
        SlaveService.nationIdNameMap.put(101, LocalMessages.T_FORCE_BEIDI);
        SlaveService.nationIdNameMap.put(102, LocalMessages.T_FORCE_XIRONG);
        SlaveService.nationIdNameMap.put(103, LocalMessages.T_FORCE_DONGYI);
        SlaveService.nationIdNameMap.put(104, LocalMessages.T_FORCE_HUANGJIN);
    }
    
    public SlaveService() {
        this.errorLog = CommonLog.getLog(SlaveService.class);
    }
    
    @Override
    public byte[] getSlaveInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Slaveholder slaveholder = this.slaveholderDao.read(playerId);
        if (slaveholder == null) {
            doc.createElement("type", 2);
            final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, this.prisonLvCache.getItemsId(1), 8);
            doc.createElement("havePic", (shList != null && shList.size() > 0 && shList.get(0).getNum() > 0) ? 1 : 0);
        }
        else {
            doc.createElement("type", 1);
            final int prisonLv = slaveholder.getPrisonLv();
            final boolean canUpdate = this.canUpdate(prisonLv, playerDto.playerLv);
            doc.createElement("canUpdate", canUpdate);
            if (canUpdate) {
                final List<StoreHouse> shList2 = this.storeHouseDao.getByItemId(playerId, this.prisonLvCache.getItemsId(prisonLv + 1), 8);
                doc.createElement("havePic", (shList2 != null && shList2.size() > 0 && shList2.get(0).getNum() > 0) ? 1 : 0);
            }
            doc.createElement("prisonLv", prisonLv);
            final int lashLv = slaveholder.getLashLv();
            final boolean isInTaril = this.isSlaveHolderInTrail(slaveholder);
            final int maxLashLv = this.prisonDegreeCache.getModels().size();
            final PrisonDegree pd1 = (PrisonDegree)this.prisonDegreeCache.get((Object)this.getTrailLashLv(slaveholder));
            doc.createElement("lashLv", lashLv);
            doc.createElement("maxLashLv", maxLashLv);
            doc.createElement("extraExp", pd1.getExpExtra());
            doc.createElement("extraCd", pd1.getTimeExtra());
            boolean canTry = false;
            if (lashLv < maxLashLv) {
                final int needGold = ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getCost() - slaveholder.getPoint() - slaveholder.getTrailGold();
                if (isInTaril) {
                    doc.createElement("trailCD", slaveholder.getExpireTime().getTime() - System.currentTimeMillis());
                }
                else {
                    canTry = true;
                }
                if (((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getTryGold() >= needGold) {
                    canTry = false;
                }
                doc.createElement("canTry", canTry);
                doc.createElement("upgradeGold", needGold);
                doc.createElement("trailGold", ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getTryGold());
            }
            final int num = slaveholder.getGrabNum();
            doc.createElement("num", num);
            doc.createElement("quality", this.prisonCatchProbCache.getQuality(num, prisonLv));
            doc.startArray("lashList");
            for (final PrisonDegree prisonDegree : this.prisonDegreeCache.getModels()) {
                doc.startObject();
                doc.createElement("lashLv", prisonDegree.getDegree());
                doc.createElement("extraExp", prisonDegree.getExpExtra());
                doc.createElement("extraCd", prisonDegree.getTimeExtra());
                doc.endObject();
            }
            doc.endArray();
            doc.startArray("generals");
            final List<PlayerSlave> playerSlaveList = this.playerSlaveDao.getListByPlayerId(playerId);
            Collections.sort(playerSlaveList, new Comparator<PlayerSlave>() {
                @Override
                public int compare(final PlayerSlave ps1, final PlayerSlave ps2) {
                    if (ps1.getSlashTimes() < ps2.getSlashTimes()) {
                        return -1;
                    }
                    if (ps1.getSlashTimes() > ps2.getSlashTimes()) {
                        return 1;
                    }
                    if (ps1.getSlashTimes() <= 0) {
                        if (ps1.getGrabTime().before(ps2.getGrabTime())) {
                            return -1;
                        }
                        if (ps1.getGrabTime().after(ps2.getGrabTime())) {
                            return 1;
                        }
                        return 0;
                    }
                    else {
                        if (ps1.getGrabTime().before(ps2.getGrabTime())) {
                            return 1;
                        }
                        if (ps1.getGrabTime().after(ps2.getGrabTime())) {
                            return -1;
                        }
                        return 0;
                    }
                }
            });
            final Date now = new Date();
            for (final PlayerSlave ps : playerSlaveList) {
                if (((ps.getType() == 0 || 2 == ps.getType()) && ps.getSlashTimes() > 0 && TimeUtil.specialAddMinutes(ps.getGrabTime(), 30).before(now)) || ((ps.getType() == 0 || 2 == ps.getType()) && ps.getSlashTimes() <= 0 && TimeUtil.specialAddDays(ps.getGrabTime(), 3).before(now)) || (1 == ps.getType() && ps.getSlashTimes() > 0 && TimeUtil.specialAddDays(ps.getGrabTime(), 3).before(now))) {
                    this.playerSlaveDao.deleteById(ps.getVId());
                }
                else {
                    final Player player = this.playerDao.read(ps.getSlaveId());
                    doc.startObject();
                    doc.createElement("vId", ps.getVId());
                    doc.createElement("lashTimes", ps.getSlashTimes());
                    doc.createElement("type", ps.getType());
                    if (prisonLv >= 3) {
                        final Halls mineHalls = (Halls)this.hallsCache.get((Object)this.playerOfficeRelativeDao.getOfficerId(ps.getSlaveId()));
                        if (mineHalls != null) {
                            doc.createElement("officerName", mineHalls.getNameList());
                            doc.createElement("nameShort", ((Official)this.officialCache.get((Object)mineHalls.getOfficialId())).getNameShort());
                        }
                        else {
                            doc.createElement("officerName", "");
                            doc.createElement("nameShort", "");
                        }
                    }
                    final General general = (General)this.generalCache.get((Object)ps.getGeneralId());
                    if (ps.getType() < 2) {
                        doc.createElement("forceId", player.getForceId());
                        doc.createElement("playerName", player.getPlayerName());
                        doc.createElement("generalName", general.getName());
                        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(ps.getSlaveId(), ps.getGeneralId());
                        if (pgm != null) {
                            doc.createElement("generalLv", pgm.getLv());
                        }
                        else {
                            final PlayerGeneral pg = this.playerGeneralDao.getPlayerGeneral(ps.getSlaveId(), ps.getGeneralId());
                            doc.createElement("generalLv", pg.getLv());
                        }
                    }
                    else {
                        doc.createElement("forceId", ps.getForceId());
                        doc.createElement("playerName", "NPC");
                        doc.createElement("generalName", ps.getName());
                        doc.createElement("generalLv", ps.getLv());
                    }
                    doc.createElement("generalPic", general.getPic());
                    doc.createElement("quality", general.getQuality());
                    long cd = 0L;
                    if (ps.getCd() != null) {
                        cd = TimeUtil.now2specMs(ps.getCd().getTime());
                    }
                    doc.createElement("cd", cd);
                    doc.endObject();
                }
            }
            doc.endArray();
            doc.createElement("autoLashExp", slaveholder.getAutoLashExp());
            doc.createElement("activityLashRate", SlaveEvent.getAdditionExp(playerId));
            doc.createElement("eventCd", EventUtil.getEventCd(9));
            doc.createElement("haveTech", (this.techEffectCache.getTechEffect(playerId, 58) > 0) ? 1 : 0);
            doc.createElement("currentFreePoint", slaveholder.getPoint());
            doc.createElement("totalTrailGold", slaveholder.getTrailGold());
            final PrisonDegree pd2 = (PrisonDegree)this.prisonDegreeCache.get((Object)lashLv);
            doc.createElement("maxFreePoint", pd2.getExpFree());
            doc.createElement("totalPoint", pd2.getExpSum());
            doc.createElement("lashNum", slaveholder.getLashNum());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getTrailLashLv(final Slaveholder sh) {
        int lashLv = sh.getLashLv();
        final boolean isInTaril = this.isSlaveHolderInTrail(sh);
        if (isInTaril) {
            ++lashLv;
        }
        return (lashLv > 5) ? 5 : lashLv;
    }
    
    @Override
    public byte[] getTrailGold(final PlayerDto playerDto) {
        final Slaveholder sh = this.slaveholderDao.read(playerDto.playerId);
        final int lashLv = sh.getLashLv();
        final int trailGold = ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getTryGold();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("trailGold", trailGold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] useInTaril(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Slaveholder sh = this.slaveholderDao.read(playerId);
        final int lashLv = sh.getLashLv();
        if (this.isSlaveHolderInTrail(sh)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRISON_IN_TRAIL);
        }
        final int trailGold = ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getTryGold();
        if (!this.playerDao.consumeGold(player, trailGold, "\u7262\u623f\u8bd5\u7528\u5347\u7ea7\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final int cost = ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getCost();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (trailGold + sh.getPoint() + sh.getTrailGold() >= cost) {
            this.slaveholderDao.resetExpireTimeAndTrailGold(playerId);
            doc.createElement("prisonUpgrade", true);
            this.slaveholderDao.updateLashLv(playerId, lashLv + 1);
        }
        else {
            this.slaveholderDao.addExpireTimeAndTrailGold(playerId, new Date(System.currentTimeMillis() + 86400000L), trailGold);
            doc.createElement("success", true);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] lash(final PlayerDto playerDto, final int vId) {
        if (vId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Slaveholder sh = this.slaveholderDao.read(playerId);
        if (sh == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_LIMBO);
        }
        final PlayerSlave ps = this.playerSlaveDao.read(vId);
        if (ps == null || playerId != ps.getPlayerId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_SLAVE);
        }
        if (ps.getSlashTimes() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_HAVE_SLASH);
        }
        this.playerSlaveDao.lashSlave(vId);
        int officerId = 0;
        int generalLv = 0;
        int officerLv = 0;
        if (2 == ps.getType() || 3 == ps.getType()) {
            generalLv = ps.getLv();
        }
        else {
            officerId = this.playerOfficeRelativeDao.getOfficerId(ps.getSlaveId());
            final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(ps.getSlaveId(), ps.getGeneralId());
            if (pgm != null) {
                generalLv = pgm.getLv();
            }
            else {
                final PlayerGeneral pg = this.playerGeneralDao.getPlayerGeneral(ps.getSlaveId(), ps.getGeneralId());
                generalLv = pg.getLv();
            }
            officerLv = ((Halls)this.hallsCache.get((Object)officerId)).getOfficialId();
        }
        final int rewardExp = this.prisonLashRewardCache.getRewardExp(sh.getPrisonLv(), generalLv, officerLv);
        final int lashLv = this.getTrailLashLv(sh);
        int extraExp = ((PrisonDegree)this.prisonDegreeCache.get((Object)lashLv)).getExpExtra();
        extraExp += SlaveEvent.getAdditionExp(playerId);
        this.playerService.updateExpAndPlayerLevel(playerId, rewardExp + extraExp, "\u97ad\u7b1e\u5974\u96b6\u589e\u52a0\u7ecf\u9a8c");
        this.slaveholderDao.addAutoLashExp(playerId, rewardExp + extraExp);
        if (1 == ps.getType()) {
            if (Players.getPlayer(ps.getSlaveId()) != null) {
                String beLashTemp = "";
                if (1 == lashLv) {
                    beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_1;
                }
                else if (2 == lashLv) {
                    beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_2;
                }
                else if (3 == lashLv) {
                    beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_3;
                }
                else if (4 == lashLv) {
                    beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_4;
                }
                else if (5 == lashLv) {
                    beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_5;
                }
                final String beLashMsg = MessageFormatter.format(beLashTemp, new Object[] { ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getPlayerId()), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(ps.getPlayerId()))), ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getPlayerId()), this.playerDao.getPlayerName(ps.getPlayerId())) });
                this.dataGetter.getChatService().sendSystemChat("SYS2ONE", ps.getSlaveId(), this.playerDao.getForceId(ps.getSlaveId()), beLashMsg, null);
            }
            if (1 == officerId) {
                final String msg = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_SLAVE_LASH_KING, new Object[] { ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getSlaveId()), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(ps.getSlaveId()))), ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getSlaveId()), this.playerDao.getPlayerName(ps.getSlaveId())), ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getPlayerId()), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(ps.getPlayerId()))), ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getPlayerId()), this.playerDao.getPlayerName(ps.getPlayerId())), ColorUtil.getForceMsg(this.playerDao.getForceId(ps.getSlaveId()), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(ps.getSlaveId()))) });
                this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
            }
        }
        if (lashLv >= 2 && ps.getCd() != null) {
            final Date cd = TimeUtil.specialAddSeconds(ps.getCd(), ((PrisonDegree)this.prisonDegreeCache.get((Object)lashLv)).getTimeExtra());
            this.playerSlaveDao.setCd(vId, cd);
            this.jobService.addJob("slaveService", "escapeJob", String.valueOf(vId), cd.getTime(), false);
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(ps.getSlaveId(), ps.getGeneralId());
            if (gmd != null) {
                gmd.cityState = 23;
                gmd.runawayTime = cd.getTime();
                this.generalService.sendGeneralMilitaryRecruitInfo(ps.getSlaveId(), ps.getGeneralId());
            }
        }
        this.addPoint(sh);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("rewardExp", rewardExp);
        doc.createElement("extraExp", extraExp);
        int addCd = 0;
        if (1 == ps.getType()) {
            addCd = ((PrisonDegree)this.prisonDegreeCache.get((Object)lashLv)).getTimeExtra();
        }
        doc.createElement("addCd", addCd);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] makeCell(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (this.slaveholderDao.read(playerId) != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_HAVE_CELL);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, 601, 8);
        if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_LIMBO_PIC);
        }
        final StoreHouse storeHouse = shList.get(0);
        final Slaveholder sh = new Slaveholder();
        sh.setPlayerId(playerId);
        sh.setPrisonLv(1);
        sh.setLashLv(1);
        sh.setGrabNum(0);
        sh.setLashNum(0);
        sh.setAutoLashExp(0);
        sh.setPoint(0);
        sh.setTrailGold(0);
        this.slaveholderDao.create(sh);
        CityEventManager.getInstance().addPlayerEvent(playerDto.playerId, 8);
        if (storeHouse.getNum() > 1) {
            this.storeHouseDao.reduceNum(storeHouse.getVId(), 1);
        }
        else {
            this.storeHouseDao.deleteById(storeHouse.getVId());
        }
        TaskMessageHelper.sendBuildedLimboMessage(playerId);
        final String msg = MessageFormatter.format(LocalMessages.S_CELL_COMPLETED, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, playerDto.playerName) });
        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] escape(final PlayerDto playerDto, final int generalId) {
        if (generalId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerSlave ps = this.playerSlaveDao.getBySlaveIdAndGeneralId(playerId, generalId);
        if (ps == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_IS_NOT_SLAVE);
        }
        if (ps.getCd() != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_IN_ESCAPE);
        }
        int extraCd = 0;
        if (ps.getSlashTimes() > 0) {
            final Slaveholder sh = this.slaveholderDao.read(ps.getPlayerId());
            final int lashLv = this.getTrailLashLv(sh);
            extraCd = ((PrisonDegree)this.prisonDegreeCache.get((Object)lashLv)).getTimeExtra();
            if (lashLv >= 2) {
                String temp = "";
                if (2 == lashLv) {
                    temp = LocalMessages.S_SEND_MESSAGE_SLAVE_ESCAPE_2;
                }
                else if (3 == lashLv) {
                    temp = LocalMessages.S_SEND_MESSAGE_SLAVE_ESCAPE_3;
                }
                else if (4 == lashLv) {
                    temp = LocalMessages.S_SEND_MESSAGE_SLAVE_ESCAPE_4;
                }
                else if (5 == lashLv) {
                    temp = LocalMessages.S_SEND_MESSAGE_SLAVE_ESCAPE_5;
                }
                final String msg = MessageFormatter.format(temp, new Object[] { extraCd });
                this.dataGetter.getChatService().sendSystemChat("SYS2ONE", ps.getSlaveId(), this.playerDao.getForceId(ps.getSlaveId()), msg, null);
            }
        }
        final Date cd = TimeUtil.nowAddSeconds(30 + extraCd);
        this.jobService.addJob("slaveService", "escapeJob", ps.getVId().toString(), cd.getTime(), false);
        this.playerSlaveDao.setCd(ps.getVId(), cd);
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
        if (gmd != null) {
            gmd.cityState = 23;
            gmd.runawayTime = cd.getTime();
        }
        if (Players.getPlayer(ps.getPlayerId()) != null) {
            Players.push(ps.getPlayerId(), PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("startEscape", 1));
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cd", (30 + extraCd) * 1000);
        doc.endObject();
        this.generalService.sendGeneralMilitaryList(playerId);
        if (ps.getSlashTimes() <= 0 && Players.getPlayer(ps.getPlayerId()) != null) {
            final String msg = MessageFormatter.format(LocalMessages.S_ESCAPE, new Object[] { this.playerDao.getPlayerName(playerId), ((General)this.generalCache.get((Object)ps.getGeneralId())).getName() });
            this.dataGetter.getChatService().sendSystemChat("SYS2ONE", ps.getPlayerId(), playerDto.forceId, msg, null);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] viewMaster(final PlayerDto playerDto, final int masterId) {
        if (masterId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerSlave ps = this.playerSlaveDao.getOneSlave(masterId, playerId);
        if (ps == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NOT_SLAVE_OF_MASTER);
        }
        final Player player = this.playerDao.read(masterId);
        final PlayerDto pd = new PlayerDto(masterId);
        pd.consumeLv = player.getConsumeLv();
        pd.forceId = player.getForceId();
        pd.loginTime = player.getLoginTime().getTime();
        pd.playerLv = player.getPlayerLv();
        pd.playerName = player.getPlayerName();
        pd.userId = player.getUserId();
        pd.yx = player.getYx();
        return this.generalService.getGeneralInfo(pd);
    }
    
    @Transactional
    @Override
    public byte[] freedom(final PlayerDto playerDto, final int generalId) {
        if (generalId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerSlave ps = this.playerSlaveDao.getBySlaveIdAndGeneralId(playerId, generalId);
        if (ps == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_IS_NOT_SLAVE);
        }
        final Player player = this.playerDao.read(playerId);
        if (!this.playerDao.canConsumeMoney(this.playerDao.read(playerId), 5)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerDao.consumeGold(player, 5, "\u8d4e\u8eab\u6d88\u8017\u91d1\u5e01");
        this.playerSlaveDao.deleteById(ps.getVId());
        final GeneralMoveDto gmd = CityService.getGeneralMoveDto(ps.getSlaveId(), ps.getGeneralId());
        if (gmd != null) {
            gmd.cityState = 0;
            gmd.runawayTime = 0L;
        }
        this.generalService.sendGeneralMilitaryRecruitInfo(ps.getSlaveId(), ps.getGeneralId());
        if (Players.getPlayer(ps.getPlayerId()) != null) {
            Players.push(ps.getPlayerId(), PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("freedom", 1));
            Players.push(ps.getPlayerId(), PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("slaveNum", this.playerSlaveDao.getSizeByPlayerId(ps.getPlayerId())));
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] updateLimbo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Slaveholder slaveholder = this.slaveholderDao.read(playerId);
        if (slaveholder == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_LIMBO);
        }
        final int prisonLv = slaveholder.getPrisonLv();
        if (prisonLv >= 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_MAX_LV);
        }
        if (!this.canUpdate(prisonLv, playerDto.playerLv)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_LOW_PLAYER_LV_TO_UPDATE);
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, this.prisonLvCache.getItemsId(prisonLv + 1), 8);
        if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_LIMBO_PIC);
        }
        final StoreHouse sh = shList.get(0);
        if (sh.getNum() > 1) {
            this.storeHouseDao.reduceNum(sh.getVId(), 1);
        }
        else {
            this.storeHouseDao.deleteById(sh.getVId());
        }
        this.slaveholderDao.updateLimbo(playerId, prisonLv + 1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] updateLashLv(final int playerId) {
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Slaveholder slaveholder = this.slaveholderDao.read(playerId);
        if (slaveholder == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_NO_LIMBO);
        }
        final int lashLv = slaveholder.getLashLv();
        if (lashLv >= this.prisonDegreeCache.getModels().size()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.S_MAX_LASH_LV);
        }
        final PrisonDegree pd = (PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1));
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)73);
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        int gold = pd.getCost() - slaveholder.getPoint() - slaveholder.getTrailGold();
        if (gold <= 0) {
            this.errorLog.error("#class:SlaveService#method:updateLashLv#playerId:" + playerId + "#lashLv:" + lashLv + "#point:" + slaveholder.getPoint() + "#gold:" + gold);
            gold = 0;
        }
        if (!this.playerDao.canConsumeMoney(player, gold)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.playerDao.consumeGold(player, gold, ci.getIntro());
        this.slaveholderDao.updateLashLv(playerId, lashLv + 1);
        this.slaveholderDao.resetExpireTimeAndTrailGold(playerId);
        EventUtil.handleOperation(playerId, 9, lashLv);
        String temp = "";
        if (lashLv == 1) {
            temp = LocalMessages.S_LASH_UPGRADED_2;
        }
        else if (lashLv == 2) {
            temp = LocalMessages.S_LASH_UPGRADED_3;
        }
        else if (lashLv == 3) {
            temp = LocalMessages.S_LASH_UPGRADED_4;
        }
        else {
            temp = LocalMessages.S_LASH_UPGRADED_5;
        }
        final int forceId = this.playerDao.getForceId(playerId);
        final String msg = MessageFormatter.format(temp, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMapDot.get(forceId)), ColorUtil.getForceMsg(forceId, this.playerDao.getPlayerName(playerId)) });
        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public synchronized boolean dealSlave(final String params) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(params)) {
                this.errorLog.error("class:SlaveService#method:dealSlave#param:" + params);
                return false;
            }
            final String[] dataArray = params.split("#");
            if (dataArray == null || dataArray.length < 6) {
                this.errorLog.error("class:SlaveService#method:dealSlave#param:" + params + "#dataArray" + dataArray);
                return false;
            }
            final int winPlayerId = Integer.parseInt(dataArray[0]);
            final int winGeneralId = Integer.parseInt(dataArray[1]);
            final int losePlayerId = Integer.parseInt(dataArray[2]);
            final int loseGeneralId = Integer.parseInt(dataArray[3]);
            final int killNum = Integer.parseInt(dataArray[4]);
            final int type = Integer.parseInt(dataArray[5]);
            int forceId = 0;
            String name = null;
            int lv = 0;
            if (2 == type || type == 3) {
                if (dataArray.length != 9) {
                    this.errorLog.error("class:SlaveService#method:dealSlave#param:" + params + "#dataArray" + dataArray);
                    return false;
                }
                forceId = Integer.parseInt(dataArray[6]);
                name = dataArray[7];
                lv = Integer.parseInt(dataArray[8]);
                if (2 == type && !SlaveService.nationIdNameMap.containsKey(forceId)) {
                    return false;
                }
                if (StringUtils.isBlank(name) || lv <= 0) {
                    return false;
                }
            }
            final Slaveholder sh = this.slaveholderDao.read(winPlayerId);
            if (sh == null) {
                return false;
            }
            if (type == 0 && sh.getPrisonLv() < 2) {
                return false;
            }
            final int num = sh.getGrabNum();
            boolean canCatch = true;
            if (type != 3) {
                canCatch = this.prisonCatchProbCache.canCatch(num, sh.getPrisonLv(), killNum);
            }
            if (canCatch) {
                if (1 == type && this.playerSlaveDao.isSlave2(losePlayerId, loseGeneralId)) {
                    this.errorLog.info("slaveService#winPlayerId:" + winPlayerId + "#winGeneralId" + winGeneralId + "#losePlayerId:" + losePlayerId + "#loseGeneralId:" + loseGeneralId);
                    return false;
                }
                int lashTimes = 0;
                boolean auto = false;
                if (sh.getPrisonLv() >= 5) {
                    auto = true;
                    lashTimes = 1;
                    int officerId = 0;
                    int generalLv = 0;
                    int officerLv = 0;
                    if (2 == type || 3 == type) {
                        generalLv = lv;
                    }
                    else {
                        officerId = this.playerOfficeRelativeDao.getOfficerId(losePlayerId);
                        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(losePlayerId, loseGeneralId);
                        if (pgm != null) {
                            generalLv = pgm.getLv();
                        }
                        else {
                            final PlayerGeneral pg = this.playerGeneralDao.getPlayerGeneral(losePlayerId, loseGeneralId);
                            generalLv = pg.getLv();
                        }
                        officerLv = ((Halls)this.hallsCache.get((Object)officerId)).getOfficialId();
                    }
                    final int lashLv = this.getTrailLashLv(sh);
                    final int rewardExp = this.prisonLashRewardCache.getRewardExp(sh.getPrisonLv(), generalLv, officerLv);
                    int extraExp = ((PrisonDegree)this.prisonDegreeCache.get((Object)lashLv)).getExpExtra();
                    extraExp += SlaveEvent.getAdditionExp(winPlayerId);
                    this.playerService.updateExpAndPlayerLevel(winPlayerId, rewardExp + extraExp, "\u97ad\u7b1e\u5974\u96b6\u589e\u52a0\u7ecf\u9a8c");
                    this.slaveholderDao.addAutoLashExp(winPlayerId, rewardExp + extraExp);
                    if (1 == type) {
                        final String generalMsg = MessageFormatter.format(LocalMessages.S_LASH_GENERAL, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(losePlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), this.playerDao.getPlayerName(losePlayerId)), ((General)this.generalCache.get((Object)loseGeneralId)).getName(), rewardExp + extraExp });
                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", winPlayerId, this.playerDao.getForceId(winPlayerId), generalMsg, null);
                        if (Players.getPlayer(losePlayerId) != null) {
                            String beLashTemp = "";
                            if (1 == lashLv) {
                                beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_1;
                            }
                            else if (2 == lashLv) {
                                beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_2;
                            }
                            else if (3 == lashLv) {
                                beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_3;
                            }
                            else if (4 == lashLv) {
                                beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_4;
                            }
                            else if (5 == lashLv) {
                                beLashTemp = LocalMessages.S_SEND_MESSAGE_SLAVE_BE_LASH_5;
                            }
                            final String beLashMsg = MessageFormatter.format(beLashTemp, new Object[] { ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(winPlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), this.playerDao.getPlayerName(winPlayerId)) });
                            this.dataGetter.getChatService().sendSystemChat("SYS2ONE", losePlayerId, this.playerDao.getForceId(losePlayerId), beLashMsg, null);
                        }
                        if (1 == officerId) {
                            final String msg = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_SLAVE_LASH_KING, new Object[] { ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(losePlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), this.playerDao.getPlayerName(losePlayerId)), ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(winPlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), this.playerDao.getPlayerName(winPlayerId)), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(losePlayerId))) });
                            this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
                        }
                    }
                    else if (type == 0) {
                        final String generalMsg = MessageFormatter.format(LocalMessages.S_LASH_PHANTOM, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(losePlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(losePlayerId), this.playerDao.getPlayerName(losePlayerId)), ((General)this.generalCache.get((Object)loseGeneralId)).getName(), rewardExp + extraExp });
                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", winPlayerId, this.playerDao.getForceId(winPlayerId), generalMsg, null);
                    }
                    else if (2 == type) {
                        final String generalMsg = MessageFormatter.format(LocalMessages.S_LASH_NPC, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMapDot.get(forceId)), ColorUtil.getForceMsg(forceId, ((General)this.generalCache.get((Object)loseGeneralId)).getName()), rewardExp + extraExp });
                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", winPlayerId, this.playerDao.getForceId(winPlayerId), generalMsg, null);
                    }
                }
                this.saveSlave(winPlayerId, losePlayerId, loseGeneralId, type, forceId, name, lv, lashTimes);
                if (1 == type) {
                    final GeneralMoveDto gmd = CityService.getGeneralMoveDto(losePlayerId, loseGeneralId);
                    if (gmd != null) {
                        gmd.cityState = 22;
                    }
                }
                final PlayerDto playerDto = Players.getPlayer(winPlayerId);
                if (playerDto != null && !auto) {
                    String msg2 = "";
                    if (1 == type) {
                        msg2 = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_GENERAL_1, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), this.playerDao.getPlayerName(losePlayerId), ((General)this.generalCache.get((Object)loseGeneralId)).getName() });
                    }
                    else if (type == 0) {
                        msg2 = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_GENERAL_2, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), this.playerDao.getPlayerName(losePlayerId), ((General)this.generalCache.get((Object)loseGeneralId)).getName() });
                    }
                    else if (2 == type) {
                        msg2 = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_GENERAL_3, new Object[] { ((General)this.generalCache.get((Object)winGeneralId)).getName(), ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMapDot.get(forceId)), ColorUtil.getForceMsg(forceId, ((General)this.generalCache.get((Object)loseGeneralId)).getName()) });
                    }
                    if (type < 3 && StringUtils.isNotBlank(msg2)) {
                        this.dataGetter.getChatService().sendSystemChat("SYS2ONE", winPlayerId, playerDto.forceId, msg2, null);
                    }
                    if (sh.getPrisonLv() < 5) {
                        Players.push(winPlayerId, PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("slaveNum", this.playerSlaveDao.getSizeByPlayerId(winPlayerId)));
                    }
                }
                if (1 == type && Players.getPlayer(losePlayerId) != null) {
                    this.generalService.sendGeneralMilitaryRecruitInfo(losePlayerId, loseGeneralId);
                    final String msg2 = MessageFormatter.format(LocalMessages.S_SEND_MESSAGE_SLAVE_BE_GRAB, new Object[] { ((General)this.generalCache.get((Object)loseGeneralId)).getName(), ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), WorldCityCommon.nationIdNameMapDot.get(this.playerDao.getForceId(winPlayerId))), ColorUtil.getForceMsg(this.playerDao.getForceId(winPlayerId), this.playerDao.getPlayerName(winPlayerId)) });
                    this.dataGetter.getChatService().sendSystemChat("SYS2ONE", losePlayerId, this.playerDao.getForceId(losePlayerId), msg2, null);
                }
                this.slaveholderDao.updateGrabNum(winPlayerId);
                if (auto) {
                    this.addPoint(sh);
                }
            }
            SlaveService.timerLog.info(LogUtil.formatThreadLog("SlaveService", "dealSlave", 2, System.currentTimeMillis() - start, "params:" + params));
            return canCatch;
        }
        catch (Exception e) {
            this.errorLog.error("class:SlaveService#method:dealSlave#param:" + params, e);
            return false;
        }
    }
    
    private void saveSlave(final int winPlayerId, int losePlayerId, final int generalId, final int type, final int forceId, final String name, final int lv, final int lashTimes) {
        if (2 == type) {
            losePlayerId = 0;
        }
        final PlayerSlave ps = new PlayerSlave();
        ps.setPlayerId(winPlayerId);
        ps.setSlaveId(losePlayerId);
        ps.setGeneralId(generalId);
        ps.setGrabTime(new Date());
        ps.setSlashTimes(lashTimes);
        ps.setCd(null);
        ps.setType(type);
        ps.setForceId(forceId);
        ps.setName(name);
        ps.setLv(lv);
        this.playerSlaveDao.create(ps);
    }
    
    @Transactional
    @Override
    public void escapeJob(final String params) {
        final int vId = Integer.parseInt(params);
        final PlayerSlave ps = this.playerSlaveDao.read(vId);
        if (ps != null) {
            if (this.slaveholderDao.read(ps.getPlayerId()).getLashLv() >= 2 && ps.getCd().after(new Date())) {
                return;
            }
            this.playerSlaveDao.deleteById(vId);
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(ps.getSlaveId(), ps.getGeneralId());
            if (gmd != null) {
                gmd.cityState = 0;
                gmd.runawayTime = 0L;
            }
            if (Players.getPlayer(ps.getPlayerId()) != null) {
                Players.push(ps.getPlayerId(), PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("freedom", 1));
                Players.push(ps.getPlayerId(), PushCommand.PUSH_SLAVE, JsonBuilder.getSimpleJson("slaveNum", this.playerSlaveDao.getSizeByPlayerId(ps.getPlayerId())));
            }
        }
    }
    
    @Override
    public void resetSlaveSystem(final int playerId) {
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[52] != '1') {
            return;
        }
        this.playerSlaveDao.releaseSlave(playerId);
    }
    
    @Override
    public boolean haveLimboPic(final int playerId, final int itemId) {
        int type = 9;
        if (itemId >= 601 && itemId <= 605) {
            type = 8;
        }
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerId, itemId, type);
        return shList != null && shList.size() > 0 && shList.get(0).getNum() > 0;
    }
    
    @Override
    public void addLimboPic(final int playerId, final int num, final int limboPicLV) {
        final Integer itemsId = this.prisonLvCache.getItemsId(limboPicLV);
        if (itemsId == null) {
            this.errorLog.error("class:slaveService#method:addLimboPic#playerId:" + playerId + "#num:" + num + "#limboPicLV:" + limboPicLV);
            return;
        }
        this.storeHouseService.gainItems(playerId, num, itemsId, "\u8de8\u670d\u70b9\u5238\u5151\u6362\u7262\u623f\u56fe\u7eb8");
    }
    
    @Override
    public void addPoint(final Slaveholder sh) {
        final int playerId = sh.getPlayerId();
        if (this.techEffectCache.getTechEffect(playerId, 58) > 0) {
            final int lashLv = sh.getLashLv();
            final PrisonDegree pd = (PrisonDegree)this.prisonDegreeCache.get((Object)lashLv);
            final int currentFreePoint = sh.getPoint();
            if (currentFreePoint < pd.getExpFree()) {
                final double rate = WebUtil.nextDouble();
                if (rate < pd.getGetExpProb()) {
                    this.slaveholderDao.addPoint(playerId);
                    final int cost = ((PrisonDegree)this.prisonDegreeCache.get((Object)(lashLv + 1))).getCost();
                    if (currentFreePoint + 1 + sh.getTrailGold() >= cost) {
                        this.slaveholderDao.updateLashLv(playerId, lashLv + 1);
                        this.slaveholderDao.resetExpireTimeAndTrailGold(playerId);
                        EventUtil.handleOperation(playerId, 9, lashLv);
                    }
                }
            }
        }
    }
    
    private boolean canUpdate(final int nowPrisonLv, final int playerLv) {
        if (1 == nowPrisonLv) {
            return playerLv >= 83;
        }
        if (2 == nowPrisonLv) {
            return playerLv >= 85;
        }
        if (3 == nowPrisonLv) {
            return playerLv >= 87;
        }
        return 4 == nowPrisonLv && playerLv >= 89;
    }
    
    private boolean isSlaveHolderInTrail(final Slaveholder sh) {
        return sh.getExpireTime() != null && System.currentTimeMillis() < sh.getExpireTime().getTime();
    }
}
