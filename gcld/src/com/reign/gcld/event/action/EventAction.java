package com.reign.gcld.event.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.event.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.annotation.*;

public class EventAction extends BaseAction
{
    private static final long serialVersionUID = -7793851816237980492L;
    @Autowired
    private IEventService eventService;
    @Autowired
    private IDataGetter dataGetter;
    
    @Command("event@getInfo")
    public ByteResult getInfo(@RequestParam("eventId") final int eventId, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getEventInfo(playerDto, eventId), request);
    }
    
    @Command("event@getReward")
    public ByteResult getReward(@RequestParam("eventId") final int eventId, @RequestParam("step") final int step, @RequestParam("param1") final int param1, @RequestParam("param2") final int param2, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getReward(playerDto, eventId, step, param1, param2), request);
    }
    
    @Command("event@lashSlave")
    public ByteResult lashSlave(@RequestParam("pos") final int pos, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.lashSlave(playerDto.playerId, pos), request);
    }
    
    @Command("event@getBigGift")
    public ByteResult getBigGift(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getBigGift(playerDto.playerId), request);
    }
    
    @Command("event@getNationalDayBigGift")
    public ByteResult getNationalDayBigGift(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getNationalDayBigGift(playerDto), request);
    }
    
    @Command("event@getXiLianReward")
    public ByteResult getXiLianReward(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getXiLianReward(playerDto.playerId), request);
    }
    
    @Command("event@decorateTree")
    public ByteResult decorateTree(@RequestParam("id") final int id, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.decorateTree(playerDto, id), request);
    }
    
    @Command("event@yaoYiYao")
    public ByteResult yaoYiYao(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.yaoYiYao(playerDto.playerId), request);
    }
    
    @Command("event@getChristmasBigGift")
    public ByteResult getChristmasBigGift(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getChristmasBigGift(playerDto), request);
    }
    
    @Command("event@getWishBigGift")
    public ByteResult getWishBigGift(@RequestParam("id") final int id, @SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getWishBigGift(playerDto, id), request);
    }
    
    @Command("event@getBaiNianBigGift")
    public ByteResult getBaiNianBigGift(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getBaiNianBigGift(playerDto), request);
    }
    
    @Command("event@buyBeast")
    public ByteResult buyBeast(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.buyBeast(playerDto), request);
    }
    
    @Command("event@recoverBeastCd")
    public ByteResult recoverBeastCd(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.recoverBeastCd(playerDto), request);
    }
    
    @Command("event@buyLantern")
    public ByteResult buyLantern(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.buyLantern(playerDto), request);
    }
    
    @Command("event@eatLantern")
    public ByteResult eatLantern(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.dataGetter.getRankService().eatLantern(playerDto), request);
    }
    
    @Command("event@getLanternBigGift")
    public ByteResult getLanternBigGift(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.eventService.getLanternBigGift(playerDto), request);
    }
}
