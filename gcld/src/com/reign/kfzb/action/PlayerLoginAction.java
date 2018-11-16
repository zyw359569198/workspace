package com.reign.kfzb.action;

import com.reign.kf.match.common.*;
import com.reign.kfwd.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kfzb.service.*;
import com.reign.kfzb.constants.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;

public class PlayerLoginAction extends BaseAction
{
    private static final long serialVersionUID = 7009474579223100645L;
    @Autowired
    IKfwdScheduleService kfwdScheduleService;
    
    @Command("gameserver@kfzblogin")
    public ByteResult login(@RequestParam("competitorId") final int competitorId, @RequestParam("certificate") final String certificate, final Request request) {
        final PlayerDto playerDto = new PlayerDto(competitorId, 3);
        if (!certificate.equals(KfzbCommonConstants.getKfzbKey(competitorId, KfzbTimeControlService.getSeasonId()))) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, "\u8bc1\u4e66\u9519\u8bef"), request);
        }
        this.putToSession("CONNECTOR", playerDto, request);
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, ""), request);
    }
}
