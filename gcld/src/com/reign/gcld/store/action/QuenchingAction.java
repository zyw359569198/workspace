package com.reign.gcld.store.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.store.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class QuenchingAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IQuenchingService quenchingService;
    
    @ChatTransactional
    @Command("quenching@openQuenching")
    public ByteResult openQuenching(@RequestParam("storehouseId") final int storehouseId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.getQuenchingInfo(playerDto, storehouseId), request);
    }
    
    @ChatTransactional
    @Command("quenching@quenchingEquip")
    public ByteResult quenchingEquip(@RequestParam("storehouseId") final int storehouseId, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.quenchingEquip(playerDto, storehouseId, type), request);
    }
    
    @ChatTransactional
    @Command("quenching@getEquips")
    public ByteResult getEquips(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.getEquips(playerDto), request);
    }
    
    @Command("quenching@remindSet")
    public ByteResult remindSet(@RequestParam("remind") final int remind, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.remindSet(playerDto, remind), request);
    }
    
    @Command("quenching@getRestoreInfo")
    public ByteResult getRestoreInfo(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("id") final int storeHouseId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.getRestoreInfo(playerDto, storeHouseId), request);
    }
    
    @Command("quenching@restoreSpecial")
    public ByteResult restoreSpecial(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("id") final int storeHouseId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.quenchingService.restoreSpecial(playerDto, storeHouseId), request);
    }
}
