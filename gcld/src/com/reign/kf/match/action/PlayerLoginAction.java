package com.reign.kf.match.action;

import com.reign.kf.match.common.*;
import com.reign.kfwd.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.kf.match.operationresult.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;

public class PlayerLoginAction extends BaseAction
{
    private static final long serialVersionUID = 7009474579223100645L;
    @Autowired
    IKfwdScheduleService kfwdScheduleService;
    
    @Command("gameserver@kfwdlogin")
    public ByteResult login(@RequestParam("competitorId") final int competitorId, @RequestParam("certificate") final String certificate, final Request request) {
        final OperateResult res = this.kfwdScheduleService.playerLogin(competitorId, certificate);
        if (res.isSuccess()) {
            final OperateResultSuccessWithExtraData operateResultSuccessWithExtraData = (OperateResultSuccessWithExtraData)res;
            final PlayerDto playerDto = (PlayerDto)operateResultSuccessWithExtraData.getExtraData();
            final Session session = request.getSession();
            request.setAttachment(session.getId());
            this.putToSession("CONNECTOR", playerDto, request);
            return this.getResult(res.getResultContent(), request);
        }
        return this.getResult(res.getResultContent(), request);
    }
}
