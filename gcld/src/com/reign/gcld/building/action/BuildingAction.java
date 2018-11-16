package com.reign.gcld.building.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.event.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.event.common.*;

public class BuildingAction extends BaseAction
{
    private static final long serialVersionUID = 2740107548895274524L;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IBuildingController buildingController;
    
    @Command("building@getMainCityInfo")
    public ByteResult getMainCityInfo(@SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final byte[] result = this.buildingService.getMainCity(dto.playerId);
        return this.getResult(result, request);
    }
    
    @Command("building@getBuildingInfo")
    public ByteResult getBuildingInfo(@RequestParam("type") final int type, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final byte[] result = this.buildingService.getBuildingInfo(dto.playerId, type);
        return this.getResult(result, request);
    }
    
    @Command("building@upgradeBuilding")
    public ByteResult upgradeBuilding(@RequestParam("buildingId") final int buildingId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final byte[] result = this.buildingService.upgradeBuilding(dto.playerId, buildingId, false).right;
        return this.getResult(result, request);
    }
    
    @Command("building@cdRecover")
    public ByteResult cdRecover(@RequestParam("workId") final int workId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final byte[] result = this.buildingService.cdRecover(dto.playerId, workId);
        return this.getResult(result, request);
    }
    
    @Command("building@cdRecoverConfirm")
    public ByteResult cdRecoverConfirm(@RequestParam("workId") final int workId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final Tuple<Boolean, Object> tuple = this.buildingService.cdRecoverConfirm(dto.playerId, workId);
        if (!(boolean)tuple.left) {
            return this.getResult((byte[])tuple.right, request);
        }
        if (tuple.right != null) {
            return this.getResult(this.buildingService.cdRecoverConfirmCallBack((CallBack)tuple.right), request);
        }
        return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10005), request);
    }
    
    @Command("building@cdSpeedUp")
    public ByteResult cdSpeedUp(@RequestParam("workId") final int workId, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        if (dto == null) {
            return null;
        }
        final Tuple<Integer, byte[]> tuple = this.buildingService.cdSpeedUp(dto.playerId, workId);
        (int)tuple.left;
        return this.getResult(tuple.right, request);
    }
    
    @Command("building@startAutoUpBuilding")
    public ByteResult startAutoUpBuilding(@RequestParam("type") final int type, @SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.startAutoUpBuilding(playerDto, type), request);
    }
    
    @Command("building@stopAutoUpBuilding")
    public ByteResult stopAutoUpBuilding(@SessionParam("PLAYER") final PlayerDto dto, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.stopAutoUpBuilding(playerDto.playerId), request);
    }
    
    @Command("building@addBuildingAddition")
    public ByteResult addBuildingAddition(@RequestParam("buildingType") final int buildingType, @RequestParam("additionMode") final int additionMode, @RequestParam("timeType") final int timeType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        final Tuple<Boolean, byte[]> tuple = this.buildingService.addBuildingAddition(playerDto, buildingType, additionMode, timeType);
        if (tuple.left && EventUtil.isEventTime(12) && ((5 == buildingType && 2 == additionMode) || 3 == additionMode) && (2 == timeType || 3 == timeType)) {
            final int type = (2 == timeType) ? 1 : 2;
            EventUtil.handleOperation(playerDto.playerId, 12, buildingType * 10 + type);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rewardType", ResourceAdditionEvent.rewardTypeMap.get(buildingType));
            doc.createElement("rewardValue", ResourceAdditionEvent.tokenMap.get(type));
            doc.endObject();
            return this.getResult(JsonBuilder.getJson(State.SUCCESS, doc.toByte()), request);
        }
        return this.getResult(tuple.right, request);
    }
    
    @Command("building@getAdditionPrice")
    public ByteResult getBuildingAdditionPrice(@RequestParam("buildingType") final int buildingType, @RequestParam("additionMode") final int additionMode, @RequestParam("timeType") final int timeType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.getBuildingAdditionPrice(buildingType, additionMode, timeType), request);
    }
    
    @Command("building@freeCdRecoverConfirm")
    public ByteResult freeCdRecoverConfirm(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingController.freeCdRecoverConfirm(playerDto.playerId), request);
    }
    
    @Command("building@freeCdRecover")
    public ByteResult freeCdRecover(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.freeCdRecover(playerDto.playerId), request);
    }
    
    @Command("building@killBandit")
    public ByteResult killBandit(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        if (17 == buildingId) {
            return this.getResult(this.buildingService.killBandit(playerDto.playerId, 1), request);
        }
        if (18 == buildingId) {
            return this.getResult(this.buildingService.killBandit(playerDto.playerId, 2), request);
        }
        if (8 == buildingId) {
            return this.getResult(this.buildingService.killKidnapper(playerDto.playerId, 1), request);
        }
        if (9 == buildingId) {
            return this.getResult(this.buildingService.killKidnapper(playerDto.playerId, 2), request);
        }
        return null;
    }
    
    @Command("building@killKidnapper")
    public ByteResult killKidnapper(@RequestParam("kidnapperId") final int kidnapperId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.killKidnapper(playerDto.playerId, kidnapperId), request);
    }
    
    @Command("building@openBluePrint")
    public ByteResult openBluePrint(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.openBluePrint(playerDto.playerId, buildingId), request);
    }
    
    @Command("building@consBluePrint")
    public ByteResult consBluePrint(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.consBluePrint(playerDto.playerId, buildingId), request);
    }
    
    @Command("building@consCdRecover")
    public ByteResult consCdRecover(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.consCdRecover(playerDto.playerId, buildingId), request);
    }
    
    @Command("building@consCdRecoverConfirm")
    public ByteResult consCdRecoverConfirm(@RequestParam("buildingId") final int buildingId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.consCdRecoverConfirm(playerDto.playerId, buildingId), request);
    }
    
    @Command("building@useFeatBuilding")
    public ByteResult useFeatBuilding(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.buildingService.useFeatBuilding(playerDto), request);
    }
}
