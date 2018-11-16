package com.reign.gcld.weapon.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.weapon.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.mvc.annotation.*;

public class WeaponAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    @Autowired
    private IWeaponService weaponService;
    
    @Command("weapon@getWeaponInfo")
    public ByteResult getWeaponInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.getWeaponInfo(playerDto), request);
    }
    
    @Command("weapon@upgrade")
    public ByteResult upgradeWeapon(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.upgradeWeapon(id, playerDto), request);
    }
    
    @Command("weapon@buyWeaponItem")
    public ByteResult buyWeaponItem(@RequestParam("id") final int id, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.buyWeaponItem(playerDto, id), request);
    }
    
    @Command("weapon@getUnSetGems")
    public ByteResult getUnSetGems(@RequestParam("id") final int id, @RequestParam("pos") final int pos, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.getUnSetGems(id, pos, playerDto), request);
    }
    
    @Command("weapon@preLoadGem")
    public ByteResult preLoadGem(@RequestParam("gemId") final int equipVid, @RequestParam("oGemId") final int oGemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.preLoadGem(oGemId, equipVid, playerDto), request);
    }
    
    @Command("weapon@loadGem")
    public ByteResult loadGem(@RequestParam("id") final int id, @RequestParam("vId") final int vId, @RequestParam("pos") final int pos, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.loadGem(id, vId, pos, playerDto), request);
    }
    
    @Command("weapon@unloadGem")
    public ByteResult unloadGem(@RequestParam("id") final int id, @RequestParam("pos") final int pos, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.unloadGem(id, pos, playerDto), request);
    }
    
    @Command("weapon@preUnloadGem")
    public ByteResult preUnloadGem(@RequestParam("gemId") final int equipVid, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.preUnloadGem(equipVid, playerDto), request);
    }
    
    @Command("weapon@openSlot")
    public ByteResult openSlot(@RequestParam("id") final int id, @RequestParam("pos") final int pos, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.weaponService.openSlot(id, pos, playerDto), request);
    }
}
