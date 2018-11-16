package com.reign.gcld.event.common;

import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.store.common.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;

public class XiLianEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    private static Map<Integer, Integer> xiLianTimesMap;
    public static int LENGTH;
    public static int POINT;
    public static int ZIZUN_XILIAN;
    public static int COMBO_POINT;
    public static Map<Integer, Integer> itemIdMap;
    public static Map<Integer, Integer> itemIdReverseMap;
    public static int MARK_ID_1;
    public static int MARK_ID_2;
    public static int MARK_ID_3;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(XiLianEvent.class);
        (XiLianEvent.xiLianTimesMap = new HashMap<Integer, Integer>()).put(1, 25);
        XiLianEvent.xiLianTimesMap.put(2, 60);
        XiLianEvent.xiLianTimesMap.put(3, 200);
        XiLianEvent.LENGTH = XiLianEvent.xiLianTimesMap.size();
        XiLianEvent.POINT = 10;
        XiLianEvent.ZIZUN_XILIAN = 1;
        XiLianEvent.COMBO_POINT = 10;
        (XiLianEvent.itemIdMap = new HashMap<Integer, Integer>()).put(1, 1601);
        XiLianEvent.itemIdMap.put(2, 1602);
        XiLianEvent.itemIdMap.put(3, 1603);
        XiLianEvent.itemIdReverseMap = new HashMap<Integer, Integer>();
        for (final Map.Entry<Integer, Integer> entry : XiLianEvent.itemIdMap.entrySet()) {
            XiLianEvent.itemIdReverseMap.put(entry.getValue(), entry.getKey());
        }
        XiLianEvent.MARK_ID_1 = 1;
        XiLianEvent.MARK_ID_2 = 2;
        XiLianEvent.MARK_ID_3 = 3;
    }
    
    public XiLianEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 14) {
            final int xiLianTimes = val / 100;
            final int point = val % 100;
            XiLianEvent.timerLog.error("class:XiLianEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#xiLianTimes:" + xiLianTimes + "#point:" + point);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 14);
            if (playerEvent == null) {
                playerEvent = this.initXiLianEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (xiLianTimes != 1) {
                return;
            }
            final int before = playerEvent.getParam1();
            final int after = before + xiLianTimes;
            final int param2 = this.getBits(after);
            this.dataGetter.getPlayerEventDao().updateInfo3(playerId, 14, after, param2);
            if (point > 0) {
                final int beforePoint = playerEvent.getParam4();
                final int afterPoint = beforePoint + point;
                this.dataGetter.getPlayerEventDao().updateParam4(playerId, 14, afterPoint);
                final int ziZunXiLianTimes = afterPoint / XiLianEvent.POINT * XiLianEvent.ZIZUN_XILIAN - beforePoint / XiLianEvent.POINT * XiLianEvent.ZIZUN_XILIAN;
                if (ziZunXiLianTimes > 0) {
                    this.dataGetter.getPlayerEventDao().addParam5(playerId, 14, ziZunXiLianTimes);
                    XiLianEvent.timerLog.error("class:XiLianEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#xiLianTimes:" + xiLianTimes + "#point:" + point + "#addNum:" + ziZunXiLianTimes);
                }
            }
        }
    }
    
    private int getBits(final int xiLianTimes) {
        if (xiLianTimes <= 0) {
            return 0;
        }
        int result = 0;
        for (int i = 1; i <= XiLianEvent.LENGTH && xiLianTimes >= XiLianEvent.xiLianTimesMap.get(i); ++i) {
            result += (int)Math.pow(2.0, i - 1);
        }
        return result;
    }
    
    private PlayerEvent initXiLianEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(14);
        pe.setPlayerId(playerId);
        pe.setParam1(0);
        pe.setParam2(0);
        pe.setParam3(0);
        pe.setParam4(0);
        pe.setParam5(0);
        pe.setParam6(0);
        pe.setParam7(0);
        pe.setParam8(0);
        pe.setParam9(0);
        pe.setParam10(0);
        return pe;
    }
    
    public void buildJson(final JsonDocument doc, final PlayerDto playerDto, PlayerEvent pe) {
        doc.createElement("eventId", this.eventId);
        doc.createElement("eventCd", this.getEventCD());
        if (pe == null) {
            this.handleOperation(this.eventId, playerDto.playerId, 0);
            pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        doc.createElement("num", pe.getParam1());
        doc.createElement("alreadyNum", pe.getParam4() / XiLianEvent.POINT * XiLianEvent.ZIZUN_XILIAN);
        doc.createElement("point", XiLianEvent.POINT);
        doc.createElement("ziZunXiLian", XiLianEvent.ZIZUN_XILIAN);
        doc.startArray("xiLianTimes");
        for (int i = 1; i <= XiLianEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("ids", i);
            doc.createElement("times", XiLianEvent.xiLianTimesMap.get(i));
            final Items item = (Items)this.dataGetter.getItemsCache().get((Object)XiLianEvent.itemIdMap.get(i));
            doc.createElement("pics", item.getPic());
            doc.createElement("received", SlaveUtil.hasReward(pe.getParam3(), i));
            doc.createElement("tips", item.getIntro());
            final StoreHouse sh = this.dataGetter.getStoreHouseDao().getStoreHouseByPlayerIdAndMarkId(playerDto.playerId, i);
            if (sh != null) {
                final int type = sh.getGoodsType();
                doc.createElement("type", type);
                doc.createElement("vId", sh.getVId());
                doc.createElement("lv", sh.getLv());
                final Equip equip = (Equip)this.dataGetter.getEquipCache().get((Object)sh.getItemId());
                if (equip.getType() == 5 || equip.getType() == 6) {
                    doc.createElement("attribute", Integer.valueOf(sh.getAttribute()) / 3);
                }
                else {
                    doc.createElement("attribute", sh.getAttribute());
                }
                doc.createElement("quality", equip.getQuality());
                doc.createElement("itemName", equip.getName());
                EquipCommon.getRefreshAttribute(sh.getRefreshAttribute(), doc, this.dataGetter.getEquipSkillCache(), null, this.dataGetter.getEquipSkillEffectCache(), equip);
                EquipCommon.getMaxSkillAndLv(doc, equip, this.dataGetter.getEquipCache(), sh.getSpecialSkillId(), sh.getRefreshAttribute());
                doc.createElement("pic", equip.getPic());
            }
            doc.endObject();
        }
        doc.endArray();
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveXiLianActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[51] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getStoreHouseDao().clearMarkId();
        final List<Date> dateList = this.get0ClockList();
        for (int size = dateList.size(), i = 0; i < size; ++i) {
            final Date date = dateList.get(i);
            this.dataGetter.getJobService().addJob("eventService", "xiLianTimeTask", String.valueOf(i + 1), date.getTime(), false);
            XiLianEvent.timerLog.error("class:EventFactory#method:createEvent#target:xiLianTimeTask#" + String.valueOf(i + 1) + "-day-job_time:" + date);
        }
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveXiLianActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[51] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getStoreHouseDao().clearMarkId();
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getXiLianRewardList(14);
        for (final PlayerEvent pe : peList) {
            try {
                int count = 0;
                final StringBuffer mailMsg = new StringBuffer(LocalMessages.XILIAN_MAIL_CONTENT_HEAD);
                for (int i = 1; i <= XiLianEvent.LENGTH; ++i) {
                    if (1 == SlaveUtil.hasReward(pe.getParam2(), i) && 1 != SlaveUtil.hasReward(pe.getParam3(), i)) {
                        this.dataGetter.getStoreHouseService().gainItems(pe.getPlayerId(), 1, XiLianEvent.itemIdMap.get(i), "\u6d17\u7ec3\u6d3b\u52a8\u83b7\u53d6\u4ee4");
                        mailMsg.append(MessageFormatter.format(LocalMessages.XILIAN_MAIL_TOKEN, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)XiLianEvent.itemIdMap.get(i))).getName() }));
                        ++count;
                    }
                }
                if (count > 0) {
                    mailMsg.setLength(mailMsg.length() - 1);
                    mailMsg.append(LocalMessages.XILIAN_MAIL_CONTENT_TAIL);
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.XILIAN_MAIL_TITLE, mailMsg.toString(), 1, pe.getPlayerId(), 0);
                }
                final int num = pe.getParam5();
                if (num <= 0) {
                    continue;
                }
                this.dataGetter.getPlayerQuenchingRelativeDao().addFreeNiubiTimes(pe.getPlayerId(), num, "\u6d17\u70bc\u6d3b\u52a8\u589e\u52a0\u514d\u8d39\u81ea\u5c0a\u6d17\u70bc\u6b21\u6570");
                final String msg = MessageFormatter.format(LocalMessages.XILIAN_MAIL_CONTENT_FREE, new Object[] { num });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.XILIAN_MAIL_TITLE, msg, 1, pe.getPlayerId(), 0);
            }
            catch (Exception e) {
                XiLianEvent.log.error("class:IronRewardEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
