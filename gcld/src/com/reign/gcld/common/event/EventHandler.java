package com.reign.gcld.common.event;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.charge.service.*;
import com.reign.gcld.tavern.service.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.domain.*;

@Component("eventHandler")
public class EventHandler
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IChargeItemService chargeItemService;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private ITavernService tavernService;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private TroopCache troopCache;
    
    public void handle(final int playerId, final PlayerDto sessionPlayer, final PushCommand command) {
        final Map<Integer, Event> resultMap = EventListener.getEvent(playerId);
        Player player = null;
        PlayerResource pr = null;
        PlayerAttribute pa = null;
        for (final Event event : resultMap.values()) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            switch (event.getEventId()) {
                case 1: {
                    player = ((player == null) ? this.playerDao.read(playerId) : player);
                    doc.createElement("gold", player.getSysGold() + player.getUserGold());
                    break;
                }
                case 2: {
                    pr = ((pr == null) ? this.playerResourceDao.read(playerId) : pr);
                    doc.createElement("copper", pr.getCopper());
                    break;
                }
                case 8: {
                    pr = ((pr == null) ? this.playerResourceDao.read(playerId) : pr);
                    doc.createElement("food", pr.getFood());
                    break;
                }
                case 7: {
                    pr = ((pr == null) ? this.playerResourceDao.read(playerId) : pr);
                    doc.createElement("wood", pr.getWood());
                    break;
                }
                case 12: {
                    pr = ((pr == null) ? this.playerResourceDao.read(playerId) : pr);
                    doc.createElement("iron", pr.getIron());
                    break;
                }
                case 3: {
                    player = ((player == null) ? this.playerDao.read(playerId) : player);
                    doc.createElement("playerLv", player.getPlayerLv());
                    sessionPlayer.playerLv = player.getPlayerLv();
                    break;
                }
                case 4: {
                    doc.createElement("newMail", "1");
                    break;
                }
                case 5: {
                    doc.appendJson((byte[])event.getEventContent());
                    break;
                }
                case 6: {
                    doc.appendJson("chargeItems", this.chargeItemService.getConfigByPlayer(playerId).getBytes());
                    player = ((player == null) ? this.playerDao.read(playerId) : player);
                    doc.createElement("vipLv", player.getConsumeLv());
                    break;
                }
                case 9: {
                    pr = ((pr == null) ? this.playerResourceDao.read(playerId) : pr);
                    doc.createElement("exp", pr.getExp());
                    break;
                }
                case 10: {
                    doc.createElement("expNeed", this.serialCache.get((int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue(), player.getPlayerLv()));
                    break;
                }
                case 11: {
                    doc.createElement("newBuilding", true);
                    break;
                }
                case 13: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    doc.createElement("function", new String(pa.getFunctionId()));
                    break;
                }
                case 15: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 1)) {
                        doc.createElement("fECopper", true);
                        break;
                    }
                    continue;
                }
                case 16: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 2)) {
                        doc.createElement("fECity", true);
                        break;
                    }
                    continue;
                }
                case 17: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (!this.isFirstEnter(pa, 4)) {
                        continue;
                    }
                    doc.createElement("fEBattle", true);
                    if (this.tavernService.recruitGeneralDirect(playerId, 259, true)) {
                        final General general = (General)this.generalCache.get((Object)259);
                        final Troop troop = this.troopCache.getTroop(general.getTroop(), playerId);
                        doc.createElement("gName", general.getName());
                        doc.createElement("gPic", general.getPic());
                        doc.createElement("gQuality", general.getQuality());
                        doc.createElement("gTroopName", troop.getName());
                        doc.createElement("gTroopQuality", troop.getQuality());
                        break;
                    }
                    break;
                }
                case 18: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 8)) {
                        doc.createElement("fEWood", true);
                        break;
                    }
                    continue;
                }
                case 19: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 16)) {
                        doc.createElement("fEFood", true);
                        break;
                    }
                    continue;
                }
                case 20: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 32)) {
                        doc.createElement("fEIron", true);
                        break;
                    }
                    continue;
                }
                case 21: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 64)) {
                        doc.createElement("fETroop", true);
                        break;
                    }
                    continue;
                }
                case 22: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (!this.isFirstEnter(pa, 128)) {
                        continue;
                    }
                    final PlayerWorld pw = this.playerWorldDao.read(playerId);
                    final int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
                    if ((quizInfo & 0x2) != 0x2) {
                        doc.createElement("fEWorld", true);
                        break;
                    }
                    continue;
                }
                case 24: {
                    doc.createElement("rankBat", true);
                    break;
                }
                case 27: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 256)) {
                        doc.createElement("fREquip", true);
                        break;
                    }
                    continue;
                }
                case 28: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 512)) {
                        doc.createElement("fEXiaPei", true);
                        break;
                    }
                    continue;
                }
                case 32: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 8192)) {
                        doc.createElement("fEGuanDu", true);
                        break;
                    }
                    continue;
                }
                case 37: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 131072)) {
                        doc.createElement("fEChangBanPo", true);
                        break;
                    }
                    continue;
                }
                case 38: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 262144)) {
                        doc.createElement("fEJiangDong", true);
                        break;
                    }
                    continue;
                }
                case 39: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 524288)) {
                        doc.createElement("fEManHuang", true);
                        break;
                    }
                    continue;
                }
                case 29: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 1024)) {
                        doc.createElement("fEfoodLimit", true);
                        break;
                    }
                    continue;
                }
                case 30: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 2048)) {
                        doc.createElement("fETroopLimit", true);
                        break;
                    }
                    continue;
                }
                case 33: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 16384)) {
                        doc.createElement("fETroopLimit30000", true);
                        break;
                    }
                    continue;
                }
                case 31: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 4096)) {
                        doc.createElement("fCBlueEquip", true);
                        break;
                    }
                    continue;
                }
                case 34: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 34)) {
                        doc.createElement("fEFreeCons", true);
                        break;
                    }
                    continue;
                }
                case 35: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 32768)) {
                        doc.createElement("fEHuangCheng", true);
                        break;
                    }
                    continue;
                }
                case 36: {
                    pa = ((pa == null) ? this.playerAttributeDao.read(playerId) : pa);
                    if (this.isFirstEnter(pa, 65536)) {
                        doc.createElement("fEConquerLeft", true);
                        break;
                    }
                    continue;
                }
            }
            doc.endObject();
            EventListener.dealEvent(event, command, doc.toByte());
        }
    }
    
    private boolean isFirstEnter(final PlayerAttribute pa, final int sequenceNum) {
        if ((pa.getEnterCount() & sequenceNum) != sequenceNum) {
            this.playerAttributeDao.updateEnterCount(pa.getPlayerId(), pa.getEnterCount() | sequenceNum);
            return true;
        }
        return false;
    }
}
