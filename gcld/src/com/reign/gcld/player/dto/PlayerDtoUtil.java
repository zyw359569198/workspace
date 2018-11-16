package com.reign.gcld.player.dto;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.common.*;
import java.util.*;

public class PlayerDtoUtil
{
    public static PlayerDto getPlayerDto(final Player player, final PlayerAttribute pa) {
        final PlayerDto dto = new PlayerDto();
        dto.playerId = player.getPlayerId();
        dto.playerLv = player.getPlayerLv();
        dto.playerName = player.getPlayerName();
        dto.userId = player.getUserId();
        dto.consumeLv = player.getConsumeLv();
        dto.forceId = player.getForceId();
        dto.gm = player.getGm();
        final Date loginTime = player.getLoginTime();
        dto.loginTime = ((loginTime == null) ? 0L : loginTime.getTime());
        dto.yx = player.getYx();
        dto.cs = pa.getFunctionId().toCharArray();
        dto.yxSource = player.getYxSource();
        dto.createTime = player.getCreateTime();
        dto.platForm = PlatForm.PC;
        return dto;
    }
}
