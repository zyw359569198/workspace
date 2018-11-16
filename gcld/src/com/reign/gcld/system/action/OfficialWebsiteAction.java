package com.reign.gcld.system.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.gcld.system.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class OfficialWebsiteAction extends BaseAction
{
    private static final long serialVersionUID = -239273602540837348L;
    @Autowired
    private IOfficialWebsiteService officialWebsiteService;
    private static final Logger opPeport;
    
    static {
        opPeport = new OpReportLogger();
    }
    
    public ByteResult preLogin(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("tp") final long tp, @RequestParam("additionalKey") final String additionalKey, @RequestParam("ticket") final String ticket, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final Tuple<Boolean, ByteResult> tuple = YxUtil.checkIP(request, yx);
        if (tuple.left) {
            OfficialWebsiteAction.opPeport.info(OpLogUtil.formatOpInterfaceLog("preLogin", "preLogin_fail_IP_IS_FORBIDDEN", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, tuple.right, 202));
            return tuple.right;
        }
        return new ByteResult(this.officialWebsiteService.preLogin(yx, userId, tp, additionalKey, ticket, request));
    }
    
    public ByteResult login(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("userName") final String userName, @RequestParam("ticket") final String ticket, @RequestParam("tp") final long tp, @RequestParam("sfid") final String sfid, @RequestParam("adult") final int adult, @RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        return new ByteResult(this.officialWebsiteService.login(yx, userId, userName, tp, sfid, adult, yxSource, ticket, request, response));
    }
    
    public ByteResult pay(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, @RequestParam("playerId") final int playerId, @RequestParam("orderId") final String orderId, @RequestParam("gold") final int gold, @RequestParam("tp") final long tp, @RequestParam("ticket") final String ticket, final Request request) {
        final long start = System.currentTimeMillis();
        final Tuple<Boolean, ByteResult> tuple = YxUtil.checkIP(request, yx);
        if (tuple.left) {
            OfficialWebsiteAction.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_IP_IS_FORBIDDEN", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, tuple.right, 202));
            return tuple.right;
        }
        return new ByteResult(this.officialWebsiteService.pay(yx, userId, playerId, orderId, gold, tp, ticket, request));
    }
    
    public ByteResult playerInfo(@RequestParam("yx") final String yx, @RequestParam("userId") final String userId, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final Tuple<Boolean, ByteResult> tuple = YxUtil.checkIP(request, yx);
        if (tuple.left) {
            OfficialWebsiteAction.opPeport.info(OpLogUtil.formatOpInterfaceLog("playerInfo", "playerInfo_fail_IP_IS_FORBIDDEN", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, tuple.right, 202));
            return tuple.right;
        }
        return new ByteResult(this.officialWebsiteService.palayerInfo(yx, userId, request));
    }
    
    @Command("rankList")
    public ByteResult rankList(@RequestParam("yx") final String yx, @RequestParam("forceId") final int forceId, final Request request, final Response response) {
        final long start = System.currentTimeMillis();
        final Tuple<Boolean, ByteResult> tuple = YxUtil.checkIP(request, yx);
        if (tuple.left) {
            OfficialWebsiteAction.opPeport.info(OpLogUtil.formatOpInterfaceLog("rankList", "rankList_fail_IP_IS_FORBIDDEN", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, tuple.right, 202));
            return tuple.right;
        }
        return new ByteResult(this.officialWebsiteService.rankList(yx, forceId, request));
    }
}
