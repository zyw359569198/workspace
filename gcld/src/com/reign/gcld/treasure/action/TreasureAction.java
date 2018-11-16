package com.reign.gcld.treasure.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.treasure.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class TreasureAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private ITreasureService treasureService;
    
    @Command("treasure@getTreasures")
    public ByteResult getTreasures(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.treasureService.getTreasures(playerDto), request);
    }
}
