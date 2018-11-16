package com.reign.gcld.grouparmy.service;

import org.springframework.stereotype.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.grouparmy.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.world.service.*;
import org.apache.commons.lang.*;
import com.reign.util.*;
import com.reign.gcld.general.dto.*;
import org.springframework.transaction.annotation.*;

@Component("groupArmyService")
public class GroupArmyService implements IGroupArmyService
{
    private static final Logger groupArmyLogger;
    @Autowired
    private IGroupArmyDao groupArmyDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerGroupArmyDao playerGroupArmyDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    
    static {
        groupArmyLogger = Logger.getLogger(GroupArmyService.class);
    }
    
    @Override
    public byte[] getCityGroupArmyInfo(final int playerId, final int generalId, final int cityId) {
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        final JsonDocument doc = new JsonDocument();
        if (pga != null) {
            doc.createElement("inFollowTeam", true);
            if (pga.getIsLeader() == 1) {
                final List<PlayerGroupArmy> list = this.playerGroupArmyDao.getList(pga.getArmyId());
                doc.createElement("isLeader", true);
                doc.createElement("followerNum", list.size() - 1);
            }
            else {
                final GroupArmy ga = this.groupArmyDao.read(pga.getArmyId());
                final Player leaderPlayer = this.playerDao.read(ga.getLeaderId());
                doc.createElement("isLeader", false);
                doc.createElement("leaderName", leaderPlayer.getPlayerName());
                final General general = (General)this.generalCache.get((Object)ga.getGeneralId());
                doc.createElement("leaderGeneralName", general.getName());
                doc.createElement("leaderGeneralQuality", general.getQuality());
            }
        }
        else {
            doc.createElement("inFollowTeam", false);
        }
        return doc.toByte();
    }
    
    @Override
    public byte[] getTeamInfo(final PlayerDto playerDto, final int cityId, final int generalId) {
        if (cityId <= 0 || generalId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        if (por == null || por.getOfficerId() == 37) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MUST_HAVE_OFFICER);
        }
        if (pgm.getLocationId() != cityId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_NOT_INCITY);
        }
        final List<PlayerGeneralMilitary> generalList = this.playerGeneralMilitaryDao.getGeneralsForFollow(cityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("generals");
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        List<PlayerGroupArmy> members = null;
        if (pga != null && pga.getIsLeader() == 0) {
            final GroupArmy groupArmy = this.groupArmyDao.read(pga.getArmyId());
            final int leaderPlayerId = groupArmy.getLeaderId();
            final int leaderGeneralId = groupArmy.getGeneralId();
            doc.appendJson(this.getGeneralInfo(leaderPlayerId, leaderGeneralId, "stopFollow"));
        }
        if (pga != null) {
            members = this.playerGroupArmyDao.getList(pga.getArmyId());
        }
        for (final PlayerGeneralMilitary g : generalList) {
            if (g.getPlayerId() == playerId && g.getGeneralId() == generalId) {
                continue;
            }
            final PlayerOfficeRelative gPor = this.playerOfficeRelativeDao.read(g.getPlayerId());
            if (gPor == null) {
                continue;
            }
            if (gPor.getOfficerId() == 37) {
                continue;
            }
            if (members != null) {
                boolean in = false;
                for (final PlayerGroupArmy member : members) {
                    if (g.getPlayerId().equals(member.getPlayerId()) && g.getGeneralId().equals(member.getGeneralId())) {
                        in = true;
                        break;
                    }
                }
                if (in) {
                    continue;
                }
            }
            doc.appendJson(this.getGeneralInfo(g.getPlayerId(), g.getGeneralId(), "follow"));
        }
        if (pga != null && pga.getIsLeader() == 1) {
            for (final PlayerGroupArmy member2 : members) {
                if (member2.getIsLeader() == 0) {
                    doc.appendJson(this.getGeneralInfo(member2.getPlayerId(), member2.getGeneralId(), "followingMe"));
                }
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getGeneralInfo(final int playerId, final int generalId, final String action) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Player player = this.playerDao.read(playerId);
        doc.createElement("playerId", playerId);
        doc.createElement("playerName", player.getPlayerName());
        final General general = (General)this.generalCache.get((Object)generalId);
        doc.createElement("generalId", generalId);
        doc.createElement("generalName", general.getName());
        doc.createElement("quality", general.getQuality());
        doc.createElement("operation", action);
        doc.endObject();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] followGeneral(final PlayerDto playerDto, final int generalId, final int followPlayerId, final int followGeneralId) {
        if (generalId <= 0 || followPlayerId <= 0 || followGeneralId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.playerId == followPlayerId && generalId == followGeneralId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_GENERAL);
        }
        final PlayerGeneralMilitary followPgm = this.playerGeneralMilitaryDao.getMilitary(followPlayerId, followGeneralId);
        if (followPgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_GENERAL);
        }
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        if (por == null || por.getOfficerId() == 37) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MUST_HAVE_OFFICER);
        }
        final PlayerOfficeRelative followPor = this.playerOfficeRelativeDao.read(followPlayerId);
        if (followPor == null || followPor.getOfficerId() == 37) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (!pgm.getLocationId().equals(followPgm.getLocationId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_SAME_CITY);
        }
        final GeneralMoveDto gmt = CityService.getGeneralMoveDto(playerId, generalId);
        if (gmt != null && StringUtils.isNotBlank(gmt.moveLine)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.JOIN_GROUP_IN_AUTO_MOVE);
        }
        final Player followPlayer = this.playerDao.read(followPlayerId);
        if (followPlayer.getForceId() != playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_RIGHT_FORCE);
        }
        PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        PlayerGroupArmy followPga = this.playerGroupArmyDao.getPlayerGroupArmy(followPlayerId, followGeneralId);
        GroupArmy followGa = null;
        GroupArmy newGa = null;
        if (pga != null) {
            if (pga.getIsLeader() == 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.ALREADY_IN_GROUPARMY);
            }
            if (followPga != null) {
                if (pga.getArmyId().equals(followPga.getArmyId())) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
                }
                if (1 == followPga.getIsLeader()) {
                    final int pgaArmyId = pga.getArmyId();
                    final List<PlayerGroupArmy> pgaList = this.playerGroupArmyDao.getList(pgaArmyId);
                    String mailContent = null;
                    for (final PlayerGroupArmy temp : pgaList) {
                        if (temp.getIsLeader() == 0) {
                            mailContent = MessageFormatter.format(LocalMessages.FOLLOW_CHANGE_MAIL, new Object[] { this.playerDao.read(playerId).getPlayerName(), ((General)this.generalCache.get((Object)generalId)).getName(), followPlayer.getPlayerName(), ((General)this.generalCache.get((Object)followGeneralId)).getName() });
                            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, mailContent, 1, temp.getPlayerId(), 0);
                        }
                    }
                    this.playerGroupArmyDao.updateIsLeader(pga.getVId(), 0);
                    this.playerGroupArmyDao.updateArmyId(pgaArmyId, followPga.getArmyId());
                    this.groupArmyDao.deleteById(pgaArmyId);
                }
                else {
                    followGa = this.groupArmyDao.read(followPga.getArmyId());
                    final int pgaArmyId = pga.getArmyId();
                    final List<PlayerGroupArmy> pgaList = this.playerGroupArmyDao.getList(pgaArmyId);
                    String mailContent = null;
                    for (final PlayerGroupArmy temp : pgaList) {
                        if (temp.getIsLeader() == 0) {
                            mailContent = MessageFormatter.format(LocalMessages.FOLLOW_CHANGE_MAIL, new Object[] { this.playerDao.read(playerId).getPlayerName(), ((General)this.generalCache.get((Object)generalId)).getName(), this.playerDao.read(followGa.getLeaderId()).getPlayerName(), ((General)this.generalCache.get((Object)followGa.getGeneralId())).getName() });
                            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, mailContent, 1, temp.getPlayerId(), 0);
                        }
                        else {
                            mailContent = MessageFormatter.format(LocalMessages.FOLLOW_CHANGE_MAIL, new Object[] { followPlayer.getPlayerName(), ((General)this.generalCache.get((Object)followGeneralId)).getName(), this.playerDao.read(followGa.getLeaderId()).getPlayerName(), ((General)this.generalCache.get((Object)followGa.getGeneralId())).getName() });
                            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, mailContent, 1, playerDto.playerId, 0);
                        }
                    }
                    this.playerGroupArmyDao.updateIsLeader(pga.getVId(), 0);
                    this.playerGroupArmyDao.updateArmyId(pgaArmyId, followPga.getArmyId());
                    this.groupArmyDao.deleteById(pgaArmyId);
                }
            }
            else {
                newGa = new GroupArmy();
                newGa.setGeneralId(followGeneralId);
                newGa.setLeaderId(followPlayerId);
                newGa.setNowCityId(pgm.getLocationId());
                this.groupArmyDao.create(newGa);
                followPga = new PlayerGroupArmy();
                followPga.setArmyId(newGa.getId());
                followPga.setGeneralId(followGeneralId);
                followPga.setIsLeader(1);
                followPga.setPlayerId(followPlayerId);
                this.playerGroupArmyDao.create(followPga);
                final int pgaArmyId = pga.getArmyId();
                final List<PlayerGroupArmy> pgaList = this.playerGroupArmyDao.getList(pgaArmyId);
                String mailContent = null;
                for (final PlayerGroupArmy temp : pgaList) {
                    if (temp.getIsLeader() == 0) {
                        mailContent = MessageFormatter.format(LocalMessages.FOLLOW_CHANGE_MAIL, new Object[] { this.playerDao.read(playerId).getPlayerName(), ((General)this.generalCache.get((Object)generalId)).getName(), followPlayer.getPlayerName(), ((General)this.generalCache.get((Object)followGeneralId)).getName() });
                        this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, mailContent, 1, temp.getPlayerId(), 0);
                    }
                }
                this.playerGroupArmyDao.updateIsLeader(pga.getVId(), 0);
                this.playerGroupArmyDao.updateArmyId(pgaArmyId, newGa.getId());
                this.groupArmyDao.deleteById(pgaArmyId);
            }
        }
        else if (followPga == null) {
            newGa = new GroupArmy();
            newGa.setGeneralId(followGeneralId);
            newGa.setLeaderId(followPlayerId);
            newGa.setNowCityId(pgm.getLocationId());
            this.groupArmyDao.create(newGa);
            pga = new PlayerGroupArmy();
            pga.setArmyId(newGa.getId());
            pga.setGeneralId(generalId);
            pga.setPlayerId(playerId);
            pga.setIsLeader(0);
            this.playerGroupArmyDao.create(pga);
            pga.setVId(0);
            pga.setGeneralId(followGeneralId);
            pga.setPlayerId(followPlayerId);
            pga.setIsLeader(1);
            this.playerGroupArmyDao.create(pga);
        }
        else {
            pga = new PlayerGroupArmy();
            pga.setArmyId(followPga.getArmyId());
            pga.setGeneralId(generalId);
            pga.setPlayerId(playerId);
            pga.setIsLeader(0);
            this.playerGroupArmyDao.create(pga);
            if (followPga.getIsLeader() == 0) {
                followGa = this.groupArmyDao.read(followPga.getArmyId());
                final String mailContent2 = MessageFormatter.format(LocalMessages.FOLLOW_CHANGE_MAIL, new Object[] { followPlayer.getPlayerName(), ((General)this.generalCache.get((Object)followGeneralId)).getName(), this.playerDao.read(followGa.getLeaderId()).getPlayerName(), ((General)this.generalCache.get((Object)followGa.getGeneralId())).getName() });
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, mailContent2, 1, playerDto.playerId, 0);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] quit(final int playerId, final int generalId) {
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        if (pga == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_GROUPARMY);
        }
        final GroupArmy groupArmy = this.groupArmyDao.read(pga.getArmyId());
        if (groupArmy == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GROUPARMY_NOT_EXIST);
        }
        final List<PlayerGroupArmy> list = this.playerGroupArmyDao.getList(pga.getArmyId());
        try {
            if (pga.getIsLeader() == 1) {
                for (final PlayerGroupArmy p : list) {
                    this.playerGroupArmyDao.deleteById(p.getVId());
                }
                this.groupArmyDao.deleteById(pga.getArmyId());
            }
            else {
                this.playerGroupArmyDao.deleteById(pga.getVId());
                if (list == null || list.isEmpty() || list.size() == 1) {
                    this.groupArmyDao.deleteById(pga.getArmyId());
                    final PlayerGroupArmy leaderPga = this.playerGroupArmyDao.getPlayerGroupArmy(groupArmy.getLeaderId(), groupArmy.getGeneralId());
                    this.playerGroupArmyDao.deleteById(leaderPga.getVId());
                    return JsonBuilder.getJson(State.SUCCESS, "");
                }
            }
        }
        catch (Exception e) {
            final StringBuffer sb = new StringBuffer();
            sb.append("playerId:").append(playerId).append("\n\r generalId").append(generalId).append("\n\r leaderId:").append(groupArmy.getLeaderId()).append("\n\r errorMessage:").append(e.getMessage());
            GroupArmyService.groupArmyLogger.error(sb.toString());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] stopFollow(final PlayerDto playerDto, final int generalId, final int followPlayerId, final int followGeneralId) {
        if (generalId <= 0 || followPlayerId <= 0 || followGeneralId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        if (pga == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_GROUPARMY);
        }
        final int armId = pga.getArmyId();
        final GroupArmy groupArmy = this.groupArmyDao.read(armId);
        if (groupArmy == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GROUPARMY_NOT_EXIST);
        }
        if (groupArmy.getLeaderId() != followPlayerId || groupArmy.getGeneralId() != followGeneralId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_FOLLOWING_THISONE);
        }
        this.playerGroupArmyDao.deleteById(pga.getVId());
        final List<PlayerGroupArmy> list = this.playerGroupArmyDao.getList(armId);
        if (list == null || list.isEmpty() || list.size() == 1) {
            final PlayerGroupArmy leaderPga = this.playerGroupArmyDao.getPlayerGroupArmy(followPlayerId, followGeneralId);
            this.playerGroupArmyDao.deleteById(leaderPga.getVId());
            this.groupArmyDao.deleteById(armId);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
