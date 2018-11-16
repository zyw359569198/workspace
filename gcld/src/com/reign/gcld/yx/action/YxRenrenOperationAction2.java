package com.reign.gcld.yx.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.gcld.player.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.util.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.servlet.*;
import com.reign.plugin.yx.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.gcld.user.dto.*;
import com.reign.util.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class YxRenrenOperationAction2 extends BaseAction
{
    private static final long serialVersionUID = -2835731388697916907L;
    private static final Log log;
    private static final String RENREN = "renren";
    private static final int REGISTER_MD5_ERROR = 9;
    private static final int REGISTER_MAX_PLAYER = 10;
    private static final int REGISTER_USERID_EMPTY = 11;
    private static final int REGISTER_FORCEID_OUT_OF_RANGE = 12;
    private static final int REGISTER_EXCEPTION = 13;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerDao playerDao;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Command("yxRenren@getForceInfo")
    public ByteResult getForceInfo(@RequestParam("callback") String callback, final Request request) {
        final Tuple<Boolean, String> htmlContent = WebUtil.getHTMLContent(callback);
        callback = htmlContent.right;
        final StringBuffer sb = new StringBuffer();
        sb.append(callback);
        sb.append("(");
        sb.append(new String(JsonBuilder.getSimpleJson("forceId", this.playerService.getYxRenrenForceId(request, "renren"))));
        sb.append(")");
        return new ByteResult(sb.toString().getBytes());
    }
    
    @Sync
    @Command("yxRenren@register2")
    public ByteResult register(@RequestParam("userId") final String userId, @RequestParam("forceId") final int forceId, @RequestParam("key") final String key, @RequestParam("yxSource") final String yxSource, final Request request, final Response response) {
        final StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(forceId);
        sb.append(PluginContext.configuration.getRenrenSecret("renren"));
        final String md5Value = MD5SecurityUtil.code(sb.toString()).toLowerCase();
        if (!key.equals(md5Value)) {
            return new ByteResult(String.valueOf(9).getBytes());
        }
        UserDto dto = null;
        try {
            if (StringUtils.isBlank(userId)) {
                return new ByteResult(String.valueOf(11).getBytes());
            }
            if (forceId < 1 || forceId > 3) {
                return new ByteResult(String.valueOf(12).getBytes());
            }
            final int count = this.playerDao.getRoleCount(userId, "renren");
            if (count >= WebUtil.getMaxPlayerNum("renren")) {
                return new ByteResult(String.valueOf(10).getBytes());
            }
            final Tuple<byte[], Boolean> tuple = this.playerService.addNewPlayer(LocalMessages.T_COMM_10010, userId, "renren", yxSource, forceId, request);
            if (!(boolean)tuple.right) {
                return new ByteResult(tuple.left);
            }
            dto = (UserDto)this.getFromSession("user", request);
            if (dto == null) {
                dto = new UserDto();
                dto.userId = userId;
                dto.yx = "renren";
                dto.loginTime = System.currentTimeMillis();
                dto.setYxSource(yxSource);
            }
            dto.firstLogin = false;
            dto.success = true;
            this.putToSession("user", dto, request);
        }
        catch (Exception e) {
            YxRenrenOperationAction2.log.error("register2 exception");
            YxRenrenOperationAction2.log.error(MessageFormatter.format("register2 exception:yx={0},userId={1},sfId={2},adult={3}", new Object[] { "renren", userId, "", "1" }));
            YxRenrenOperationAction2.log.error("register2 exception", e);
            return new ByteResult(String.valueOf(13).getBytes());
        }
        YxRenrenOperationAction2.log.info(MessageFormatter.format("register2 success:yx={0},userId={1},sfId={2},adult={3}", new Object[] { "renren", userId, "", "1" }));
        return new ByteResult(String.valueOf(1).getBytes());
    }
}
