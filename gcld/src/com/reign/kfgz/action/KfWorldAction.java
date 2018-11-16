package com.reign.kfgz.action;

import com.reign.kf.match.common.*;
import com.reign.kfgz.world.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;
import com.reign.framework.netty.mvc.annotation.*;

public class KfWorldAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IKfWorldService kfWorldService;
    
    @Command("kfworld@getWorldMap")
    public ByteResult getWorldMap(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfWorldService.getWorldMap(player), request);
    }
    
    @Command("kfworld@move")
    public ByteResult move(@RequestParam("cityId") final int cityId, @RequestParam("gId") final int gId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfWorldService.move(player, gId, cityId).left, request);
    }
    
    @Command("kfworld@getCityInfo")
    public ByteResult getCityInfo(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfWorldService.getCityInfo(player, cityId), request);
    }
    
    @Command("kfworld@getJieBingInfo")
    public ByteResult getJieBingInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfWorldService.getJieBingInfo(player), request);
    }
    
    @Command("kfworld@getAllyInfo")
    public ByteResult getAllyInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("CONNECTOR", request);
        final KfPlayerInfo player = KfgzPlayerManager.getPlayerByCId(playerDto.getCompetitorId());
        if (player == null) {
            return null;
        }
        return this.getResult(this.kfWorldService.getAllyInfo(player), request);
    }
}
