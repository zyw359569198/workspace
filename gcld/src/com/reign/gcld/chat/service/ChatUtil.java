package com.reign.gcld.chat.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.world.common.*;

@Component("chatUtil")
public class ChatUtil
{
    @Autowired
    private IChatService chatService;
    
    public void sendRankChat(final int playerId, final int fallToRank, final ChatLink battleLink) {
        final String msg = MessageFormatter.format(LocalMessages.CHAT_RANK, new Object[] { ColorUtil.getGreenMsg(fallToRank) });
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, battleLink);
    }
    
    public void sendTrickChat(final int playerId, final String trickName) {
    }
    
    public void sendWinHostileCountryCityChat(final int forceId, final int forceId2, final String cityName, final boolean defend, final String winPlayerName) {
        if (defend) {
            final String msg = MessageFormatter.format(LocalMessages.CHAT_WIN_HOSTILE_COUNTRY_CITY, new Object[] { WebUtil.getForceName(forceId2), ColorUtil.getSpecialColorMsg(cityName), ColorUtil.getSpecialColorMsg(winPlayerName) });
            this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
        }
        else {
            final String msg = MessageFormatter.format(LocalMessages.CHAT_WIN_HOSTILE_COUNTRY_CITY, new Object[] { ColorUtil.getForceMsg(forceId2, WebUtil.getForceName(forceId2)), ColorUtil.getGreenMsg(cityName), ColorUtil.getForceMsg(forceId, winPlayerName) });
            this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, forceId), msg, null);
        }
    }
    
    public void sendLoseCountryCityChat(final int forceId, final int forceId2, final String cityName, final boolean defend, final String winPlayerName) {
        final String msg = MessageFormatter.format(LocalMessages.CHAT_LOSE_COUNTRY_CITY, new Object[] { ColorUtil.getGreenMsg(cityName), ColorUtil.getForceMsg(forceId2, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(forceId2)) + winPlayerName) });
        if (defend) {
            this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
        }
        else {
            this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, forceId), msg, null);
        }
    }
    
    public void sendGeneralLosePlaceChat(final int playerId, final String generalName, final String cityName, final int forceId) {
        final String msg = MessageFormatter.format(LocalMessages.CHAT_GENERAL_LOSE_PLACE_CITY, new Object[] { ColorUtil.getGreenMsg(generalName), ColorUtil.getGreenMsg(cityName), ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)) });
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, null);
    }
    
    public void sendGeneralWinPlaceChat(final int playerId, final String generalName, final int quality, final String cityName, final int forceId) {
        final String msg = MessageFormatter.format(LocalMessages.CHAT_GENERAL_WIN_PLACE_CITY, new Object[] { ColorUtil.getColorMsg(quality, generalName), ColorUtil.getGreenMsg(cityName), ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)) });
        this.chatService.sendSystemChat("SYS2ONE", playerId, 0, msg, null);
    }
    
    public void sendBarbarainChat(final int forceId, final String barbarainName) {
        final String msg = MessageFormatter.format(LocalMessages.CHAT_BARBARAIN, new Object[] { barbarainName });
        this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
    }
    
    public void sendLostCityByBarbarainChat(final int forceId, final String barbarainName, final String cityName) {
        final String msg1 = MessageFormatter.format(LocalMessages.BROADCAST_CHAT_LOST_CITY_BY_BARBARAIN, new Object[] { ColorUtil.getSpecialColorMsg(WebUtil.getForceName(forceId)), ColorUtil.getSpecialColorMsg(cityName) });
        this.chatService.sendSystemChat("GLOBAL", 0, 0, msg1, null);
        final String msg2 = MessageFormatter.format(LocalMessages.BROADCAST_CHAT_LOST_CITY_BY_BARBARAIN, new Object[] { ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)), ColorUtil.getForceMsg(forceId, cityName) });
        this.chatService.sendBigNotice("GLOBAL", null, msg2, null);
    }
}
