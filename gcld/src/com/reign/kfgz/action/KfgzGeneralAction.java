package com.reign.kfgz.action;

import com.reign.kf.match.common.*;
import com.reign.kfgz.world.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.resource.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfgzGeneralAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IKfWorldService kfWorldService;
    @Autowired
    private IKfgzResourceService kfgzResourceService;
    
    @Command("kfgzGeneral@startMubing")
    public ByteResult startMubing(@RequestParam("gId") final int gId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzResourceService.startMubing(player, gId), request);
    }
    
    @Command("kfgzGeneral@getInfo")
    public ByteResult getInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfgzResourceService.getInfo(player), request);
    }
}
