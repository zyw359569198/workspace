package com.reign.gcld.chat.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.common.*;

@Component("broadCastUtil")
public class BroadCastUtil
{
    @Autowired
    private IChatService chatService;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerDao playerDao;
    
    public void sendWinNPCBroadCast(final int playerId, final String NPCName) {
        PlayerDto playerDto = Players.getPlayer(playerId);
        if (playerDto == null) {
            final Player player = this.playerDao.read(playerId);
            playerDto = new PlayerDto(player.getPlayerId(), player.getForceId());
        }
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_WIN_NPC, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(NPCName) });
        this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
    }
    
    public void sendPassBonusBroadCast(final int playerId, final String bounsName) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_PASS_BONUS, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(bounsName) });
        this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
    }
    
    public void sendCaptureNeutralPlaceBroadCast(final int forceId, final String placeName) {
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_CAPTURE_NEUTRAL_PLACE, new Object[] { ColorUtil.getGreenMsg(placeName) });
        this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, forceId), msg, null);
    }
    
    public void sendWinHCCommonPlaceBroadCast(final int myForceId, final int forceId, final String placeName) {
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_WIN_HOSTILE_COUNTRY_COMMON_PLACE, new Object[] { ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)), ColorUtil.getGreenMsg(placeName) });
        this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, myForceId), msg, null);
    }
    
    public void sendLoseCommonPlaceBroadCast(final int myForceId, final int forceId, final String placeName) {
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_LOSE_COMMON_PLACE, new Object[] { ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)), ColorUtil.getGreenMsg(placeName) });
        this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, myForceId), msg, null);
    }
    
    public void sendPlaceChangeHandsBroadCast(final int forceId, final int winForceId, final String placeName) {
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_PLACE_CHANGE_HANDS, new Object[] { ColorUtil.getForceMsg(winForceId, WebUtil.getForceName(winForceId)), ColorUtil.getGreenMsg(placeName) });
        this.chatService.sendBigNotice("COUNTRY", new PlayerDto(0, forceId), msg, null);
    }
    
    public void sendReOpenBonusBroadCast(final int playerId, final int powerId, final String bonusName) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_REOPEN_BONUS, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(bonusName) });
        if (powerId >= 1 && powerId <= 2) {
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_1");
        }
        else if (powerId >= 3 && powerId <= 4) {
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_2");
        }
    }
    
    public void sendGainGeneralBroadCast(final int playerId, final int generalId) {
        final General general = (General)this.generalCache.get((Object)generalId);
        if (general == null) {
            return;
        }
        final int broadCast = general.getBroadCast();
        if (broadCast == 0) {
            final PlayerDto playerDto = Players.getPlayer(playerId);
            final String msg = MessageFormatter.format(LocalMessages.BROADCAST_GENERAL_ALL, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getColorMsg(general.getQuality(), general.getName()) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
        }
        else if (1 == broadCast) {
            if (general.getType() == 2) {
                PlayerDto playerDto = Players.getPlayer(playerId);
                if (playerDto == null) {
                    final Player player = this.playerDao.read(playerId);
                    playerDto = new PlayerDto();
                    playerDto.playerId = playerId;
                    playerDto.forceId = player.getForceId();
                    playerDto.playerName = player.getPlayerName();
                }
                final String msg = MessageFormatter.format(LocalMessages.BROADCAST_MILITARY_LEVEL_STAGE_LIMIT_1, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getColorMsg(general.getQuality(), general.getName()) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_1");
            }
        }
        else if (2 == broadCast) {
            if (general.getType() == 2) {
                final PlayerDto playerDto = Players.getPlayer(playerId);
                final String msg = MessageFormatter.format(LocalMessages.BROADCAST_MILITARY_LEVEL_STAGE_LIMIT_2, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getColorMsg(general.getQuality(), general.getName()) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_2");
            }
            else if (general.getType() == 1) {
                final PlayerDto playerDto = Players.getPlayer(playerId);
                final String msg = MessageFormatter.format(LocalMessages.BROADCAST_CIVIL_LEVEL_STAGE_LIMIT_2, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getColorMsg(general.getQuality(), general.getName()) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_2");
            }
        }
    }
    
    public void sendLuminousCupBroadCast(final int playerId) {
        final PlayerDto playerDto = Players.getPlayer(playerId);
        final String msg = MessageFormatter.format(LocalMessages.BROADCAST_GET_LUMINOUS_CUP, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)), ColorUtil.getForceMsg(playerDto.forceId, playerDto.playerName) });
        this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
    }
}
