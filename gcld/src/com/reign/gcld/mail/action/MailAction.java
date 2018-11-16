package com.reign.gcld.mail.action;

import com.reign.gcld.common.web.*;
import com.reign.gcld.mail.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import com.reign.gcld.common.util.characterFilter.*;
import com.reign.gcld.mail.domain.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.util.*;

public class MailAction extends BaseAction
{
    private static final long serialVersionUID = -2051642452886228485L;
    @Autowired
    private IMailService mailService;
    
    @Command("mail@writeMail")
    public ByteResult writeMail(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("tName") final String tName, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        if (StringUtils.isEmpty(tName) || tName.trim().equals(LocalMessages.T_COMM_10010)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_CHAT_PLAYER_NO_USE), request);
        }
        if (StringUtils.isBlank(title)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10007), request);
        }
        if (title.length() > 20) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10008), request);
        }
        if (StringUtils.isBlank(content)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10009), request);
        }
        if (content.length() > 255) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_MAIL_10010), request);
        }
        boolean danger = false;
        Tuple<Boolean, String> tuple = WebUtil.getHTMLContent(content);
        if (tuple.left) {
            content = String.valueOf(tuple.right) + LocalMessages.T_MAIL_10011;
            danger = true;
        }
        else {
            content = tuple.right;
        }
        tuple = WebUtil.getHTMLContent(title);
        title = tuple.right;
        if (tuple.left && !danger) {
            content = String.valueOf(content) + LocalMessages.T_MAIL_10011;
        }
        final ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("default");
        if (characterFilter != null) {
            content = characterFilter.filter(content);
            title = characterFilter.filter(title);
        }
        final Mail mail = this.getNewMail(playerDto.playerId, title, content);
        return this.getResult(this.mailService.writeMail(mail, tName), request);
    }
    
    @Command("mail@getMail")
    public ByteResult getMail(@RequestParam("page") final int page, final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.getMailByPlayerId(dto.playerId, page), request);
    }
    
    @Command("mail@getMailByType")
    public ByteResult getMailByType(@RequestParam("page") final int page, @RequestParam("type") final int type, final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.getMailByType(dto.playerId, type, page), request);
    }
    
    @Command("mail@getDeleteMail")
    public ByteResult getDeleteMail(@RequestParam("page") final int page, final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.getDeleteMailByPlayerId(dto.playerId, page), request);
    }
    
    @Command("mail@delete")
    public ByteResult deleteMail(final Request request, @RequestParam("mailId") final int... mailIds) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        if (mailIds.length == 1) {
            return this.getResult(this.mailService.deleteMail(dto.playerId, mailIds[0]), request);
        }
        return this.getResult(this.mailService.deleteMailAll(dto.playerId, mailIds), request);
    }
    
    @Command("mail@thoroughDelete")
    public ByteResult thoroughDelete(final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.thoroughDeleteMail(dto.playerId), request);
    }
    
    @Command("mail@save")
    public ByteResult saveMail(final Request request, @RequestParam("mailId") final int mailId) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.saveMail(dto.playerId, mailId), request);
    }
    
    @Command("mail@retrieve")
    public ByteResult retrieveMail(final Request request, @RequestParam("mailId") final int mailId) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.retrieveMail(dto.playerId, mailId), request);
    }
    
    @Command("mail@read")
    public ByteResult readMail(@RequestParam("mailId") final int mailId, final Request request) {
        final PlayerDto dto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (dto == null) {
            return null;
        }
        return this.getResult(this.mailService.readMail(dto.playerId, mailId), request);
    }
    
    private Mail getNewMail(final int playerId, final String title, final String content) {
        final Mail mail = new Mail();
        mail.setTitle(title);
        mail.setFId(playerId);
        mail.setContent(content);
        mail.setMailType(2);
        mail.setSendtime(new Date());
        mail.setIsDelete(0);
        mail.setIsRead(0);
        return mail;
    }
}
