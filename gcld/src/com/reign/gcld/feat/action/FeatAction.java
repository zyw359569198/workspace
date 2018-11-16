package com.reign.gcld.feat.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.feat.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;

public class FeatAction extends BaseAction
{
    private static final long serialVersionUID = -4807227828415756360L;
    @Autowired
    private IFeatService featService;
    
    @Command("feat@getRankInfo")
    public ByteResult getRankInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.featService.getRankInfo(playerDto), request);
    }
    
    @Command("feat@getBoxReward")
    public ByteResult getBoxReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.featService.getBoxReward(playerDto), request);
    }
    
    @Command("feat@getRankReward")
    public ByteResult getRankReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.featService.getRankReward(playerDto), request);
    }
}
