package com.reign.gcld.store.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.store.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.framework.netty.mvc.annotation.*;

public class EquipAction extends BaseAction
{
    private static final long serialVersionUID = -2883123529995541330L;
    @Autowired
    private IEquipService equipService;
    
    @Command("equip@getEquipInfo")
    public ByteResult getEquipInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.getEquipInfo(playerDto), request);
    }
    
    @Command("equip@updateEquip")
    @ChatTransactional
    public ByteResult updateEquip(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.updateEquip(playerDto, vId), request);
    }
    
    @Command("equip@updateEquipTen")
    @ChatTransactional
    public ByteResult updateEquipTen(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.updateEquipTen(playerDto, vId), request);
    }
    
    @Command("equip@openStoreHouse")
    public ByteResult openStoreHouse(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final List<StoreHouse> list = this.equipService.beforeOpenStoreHouse(playerDto);
        return this.getResult(this.equipService.openStoreHouse(playerDto, list), request);
    }
    
    @Command("equip@buySTSize")
    public ByteResult buySTSize(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.buySTSize(playerDto.playerId), request);
    }
    
    @Command("equip@sellGoods")
    public ByteResult sellGoods(@RequestParam("vId") final int vId, @RequestParam("num") final int num, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.sellGoods(playerDto, vId, num), request);
    }
    
    @Command("equip@openSTBack")
    public ByteResult openSTBack(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.openSTBack(playerDto), request);
    }
    
    @Command("equip@buyBackGoods")
    public ByteResult buyBackGoods(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.buyBackGoods(playerDto, vId), request);
    }
    
    @Command("equip@getWearEquip")
    public ByteResult getWearEquip(@RequestParam("type") final int type, @RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.getWearEquip(playerDto, type, generalId), request);
    }
    
    @Command("equip@changeEquip")
    public ByteResult changeEquip(@RequestParam("vId") final int vId, @RequestParam("type") final int type, @RequestParam("generalId") final int generalId, @RequestParam("change") final boolean change, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.changeEquip(playerDto, vId, generalId, type, change), request);
    }
    
    @Command("equip@unloadEquip")
    public ByteResult unLoadEquip(@RequestParam("vId") final int vId, @RequestParam("type") final int type, @RequestParam("generalId") final int generalId, @RequestParam("change") final boolean change, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.unloadEquip(playerDto, vId, generalId, type, change), request);
    }
    
    @Command("equip@preMakeGem")
    public ByteResult preMakeGem(@RequestParam("gemId") final int gemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.preMakeGem(playerDto, gemId), request);
    }
    
    @Command("equip@makeGem")
    public ByteResult makeGem(@RequestParam("gemId") final int gemId, @RequestParam("type") final int type, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.makeGem(playerDto, gemId, type), request);
    }
    
    @Command("equip@unMakeGem")
    public ByteResult unMakeGem(@RequestParam("gemId") final int gemId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.unMakeGem(playerDto, gemId), request);
    }
    
    @Command("equip@getCanUseGeneral")
    public ByteResult getCanUseGeneral(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.getCanUseGeneral(playerDto, vId), request);
    }
    
    @Command("equip@useOnGeneral")
    public ByteResult useOnGeneral(@RequestParam("vId") final int vId, @RequestParam("generalId") final int generalId, final Request request, @RequestParam("time") final int time) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.useOnGeneral(playerDto, vId, generalId, time), request);
    }
    
    @Command("equip@useIronRewardToken")
    public ByteResult useIronRewardToken(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.useIronRewardToken(playerDto, vId), request);
    }
    
    @Command("equip@useXiLianToken")
    public ByteResult useXiLianToken(@RequestParam("vId1") final int vId1, @RequestParam("vId2") final int vId2, @RequestParam("equipSkillId") final int equipSkillId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.useXiLianToken(playerDto.playerId, vId1, vId2, equipSkillId), request);
    }
    
    @Command("equip@useCreateBuilding")
    public ByteResult useCreateBuilding(@RequestParam("vId") final int vId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.useCreateBuilding(playerDto, vId), request);
    }
    
    @Command("equip@compoundSuit")
    public ByteResult compoundSuit(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.compoundSuit(playerDto, vId), request);
    }
    
    @Command("equip@doCompoundSuit")
    public ByteResult doCompoundSuit(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.doCompoundSuit(playerDto, vId), request);
    }
    
    @Command("equip@deMountSuit")
    public ByteResult demountSuit(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.demountSuit(playerDto, vId), request);
    }
    
    @Command("equip@demountGold")
    public ByteResult demountSuitGold(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.demountSuitGold(playerDto, vId), request);
    }
    
    @Command("equip@compoundProset")
    public ByteResult compoundProset(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.compoundProset(playerDto, vId), request);
    }
    
    @Command("equip@doCompoundProset")
    public ByteResult doCompoundProset(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.doCompoundProset(playerDto, vId), request);
    }
    
    @Command("equip@demoutProsetGold")
    public ByteResult demoutProsetGold(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.demoutProsetGold(playerDto, vId), request);
    }
    
    @Command("equip@doDemoutProset")
    public ByteResult doDemoutProset(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.doDemoutProset(playerDto, vId), request);
    }
    
    @Command("equip@useResourceToken")
    public ByteResult useResourceToken(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.useResourceToken(playerDto, vId), request);
    }
    
    @Command("equip@bindEquip")
    public ByteResult bindEquip(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.bindEquip(playerDto, vId), request);
    }
    
    @Command("equip@unbindEquip")
    public ByteResult unbindEquip(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.unbindEquip(playerDto, vId), request);
    }
    
    @Command("equip@cancelUnbindEquip")
    public ByteResult cancelUnbindEquip(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.cancelUnbindEquip(playerDto, vId), request);
    }
    
    @Command("equip@getEquipSkillInfo")
    public ByteResult getEquipSkillInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.equipService.getEquipSkillInfo(), request);
    }
}
