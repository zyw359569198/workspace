package com.reign.gcld.gm.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.gm.controller.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.gm.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import org.apache.commons.lang.*;
import com.reign.gcld.player.domain.*;

public class GMAction extends BaseAction
{
    private static final long serialVersionUID = 8883136910150498819L;
    @Autowired
    private IGMController controller;
    @Autowired
    private IPlayerDao playerDao;
    
    @com.reign.framework.netty.mvc.annotation.Command("gm@gmcommand")
    public ByteResult parse(@SessionParam("PLAYER") final PlayerDto dto, final Request request, @RequestParam("command") final String command, @RequestParam("param") final String... params) {
        if (dto == null) {
            return null;
        }
        final Command cmd = Command.getCommand(command);
        if (cmd == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10001), request);
        }
        final Player player = this.playerDao.read(dto.playerId);
        if (player.getGm() > 0) {
            if (player.getGm() == 2) {
                final int num = Integer.valueOf(params[0].trim());
                if (!cmd.equals(Command.TASK) || num >= 122) {
                    return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
                }
            }
            else if (player.getGm() != 3) {
                return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
            }
        }
        else if (StringUtils.isBlank(Configuration.getProperty("gcld.use.gm.commond")) || !Configuration.getProperty("gcld.use.gm.commond").toString().equals("1")) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012), request);
        }
        return this.getResult(this.controller.handle(dto, cmd, request, params), request);
    }
}
