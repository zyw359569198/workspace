package com.reign.gcld.antiaddiction;

import org.springframework.stereotype.*;
import com.reign.gcld.job.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.user.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("antiAddictionService")
public class AntiAddictionService implements IAntiAddictionService
{
    @Autowired
    private IJobService jobService;
    @Autowired
    private IChatService chatService;
    
    @Override
    public void updateState(final String params) {
        final int playerId = Integer.valueOf(params);
        final PlayerDto player = Players.playerMap.get(playerId);
        final Session session = Players.playerSessionMap.get(playerId);
        if (session == null) {
            return;
        }
        final UserDto user = Users.sessionUserMap.get(session.getId());
        if (user != null && player != null && user.isNeedAntiAddiction()) {
            user.getAntiAddictionStateMachine().update(player, this.jobService);
            final int currentLevel = user.getAntiAddictionStateMachine().getCurrentState().getAntiAddictionLevel();
            switch (currentLevel) {
                case 1: {
                    this.chatService.sendSystemChat("SYS2ONE", playerId, player.forceId, LocalMessages.ANTIADDICTION_REACH1HOUR, null);
                    break;
                }
                case 2: {
                    this.chatService.sendSystemChat("SYS2ONE", playerId, player.forceId, LocalMessages.ANTIADDICTION_REACH2HOUR, null);
                    break;
                }
                case 3: {
                    this.chatService.sendSystemChat("SYS2ONE", playerId, player.forceId, LocalMessages.ANTIADDICTION_REACH3HOUR, null);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    this.chatService.sendSystemChat("SYS2ONE", playerId, player.forceId, LocalMessages.ANTIADDICTION_HALF_GAIN, null);
                    break;
                }
                case 7: {
                    this.chatService.sendSystemChat("SYS2ONE", playerId, player.forceId, LocalMessages.ANTIADDICTION_ZERO_GAIN, null);
                    break;
                }
            }
        }
    }
    
    @Override
    public void sendAntiAddicitonNotice(final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("msg", LocalMessages.T_CHAT_ANTIADDICTION);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_ANTIADDICTION, doc.toByte());
    }
}
