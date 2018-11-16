package com.reign.kf.gw.controller;

import org.springframework.stereotype.*;
import com.reign.kf.gw.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.gw.kfwd.service.*;
import com.reign.kfgz.service.*;
import com.reign.kfzb.service.*;
import com.reign.kf.gw.common.log.*;
import org.jboss.netty.channel.*;
import com.reign.kf.gw.common.*;
import com.reign.kf.comm.param.gw.*;
import com.reign.kfgz.dto.request.*;
import java.util.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component("gwController")
public class GWController implements IGWController
{
    private static final Logger log;
    @Autowired
    private IGWService gwService;
    @Autowired
    private IKfwdGatewayService kfwdGatewayService;
    @Autowired
    private IKfgzGatewayService kfgzGatewayService;
    @Autowired
    private IKfzbGatewayService kfzbGatewayService;
    @Autowired
    private IKfzbFeastService kfzbFeastService;
    
    static {
        log = CommonLog.getLog(GWController.class);
    }
    
    @Override
    public Response handle(final Channel channel, final Request request, final int refer) {
        final CommandWatch watch = new CommandWatch();
        final Response response = new Response();
        response.setResponseId(request.getRequestId());
        response.setCommand(request.getCommand());
        try {
            if (request.getCommand() != Command.QUERY_SEASONINFO && request.getCommand() != Command.UPDATE_SEASONINFO) {
                if (request.getCommand() == Command.QUERY_AUCTIONINFO) {
                    final AuctionInfoEntity entity = this.gwService.getAuctionInfo(refer);
                    response.setMessage(entity);
                }
                else if (request.getCommand() == Command.UPDATE_AUCTIONINFO) {
                    final UpdateAuctionParam param = (UpdateAuctionParam)request.getMessage();
                    final CommEntity entity2 = this.gwService.updateAuctionInfo(param);
                    response.setMessage(entity2);
                }
                else if (request.getCommand() == Command.KFWD_GW_SEASONINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfwdSeasonInfo sInfo = this.kfwdGatewayService.handleAstdKFwdSeasonInfo(gs);
                    response.setMessage(sInfo);
                }
                else if (request.getCommand() == Command.KFWD_GW_SCHEDULEINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfwdScheduleInfoDto sInfo2 = this.kfwdGatewayService.handleAstdKFwdSeasonScheduleInfo(gs);
                    response.setMessage(sInfo2);
                }
                else if (request.getCommand() == Command.KFWD_GW_REWARDINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfwdRewardResult sInfo3 = this.kfwdGatewayService.handleAstdKFwdSeasonRewardInfo(gs);
                    response.setMessage(sInfo3);
                }
                else if (request.getCommand() == Command.KFWD_GW_TICKETMARKETINFO) {
                    final KfwdTicketMarketListInfo sInfo4 = this.kfwdGatewayService.handleGetTicketMarketInfo();
                    response.setMessage(sInfo4);
                }
                else if (request.getCommand() == Command.KFWD_GW_SEASONINFO_FROM_MATCH) {
                    final MatchServerEntity ms = (MatchServerEntity)request.getMessage();
                    final KfwdSeasonInfo sInfo = this.kfwdGatewayService.handleMatchKFwdSeasonInfo(ms);
                    response.setMessage(sInfo);
                }
                else if (request.getCommand() == Command.KFWD_GW_SCHEDULEINFO_FROM_MATCH) {
                    final MatchServerEntity ms = (MatchServerEntity)request.getMessage();
                    final KfwdMatchScheduleInfo sInfo5 = this.kfwdGatewayService.handleMatchKFwdSeasonScheduleInfo(ms);
                    response.setMessage(sInfo5);
                }
                else if (request.getCommand() == Command.KFWD_GW_REWARDINFO_FROM_MATCH) {
                    final MatchServerEntity ms = (MatchServerEntity)request.getMessage();
                    final KfwdRewardResult rInfo = this.kfwdGatewayService.handleMatchKFwdRewardRuleInfo(ms);
                    response.setMessage(rInfo);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETSEASONINFO_FROM_GAME_SERVER) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfgzSeasonInfoRes sInfo6 = this.kfgzGatewayService.handleKfgzSeasonInfo();
                    response.setMessage(sInfo6);
                }
                else if (request.getCommand() == Command.KF_GETCID_FROM_GAME_SEVERR) {
                    final Integer res = this.kfgzGatewayService.handleGetGameServerPlayerUid();
                    response.setMessage(res);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETSCHEDULEINFO_FROM_GAME_SERVER) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfgzScheduleInfoList sInfo7 = this.kfgzGatewayService.handleGamerServerKfgzScheduleInfo(gs);
                    response.setMessage(sInfo7);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETSEASONINFO_FROM_MATCH) {
                    final KfgzSeasonInfoRes sInfo8 = this.kfgzGatewayService.handleKfgzSeasonInfo();
                    response.setMessage(sInfo8);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETRULEINFO) {
                    final KfgzRuleInfoList sInfo9 = this.kfgzGatewayService.handleKfgzRuleInfo();
                    response.setMessage(sInfo9);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETREWARDINFO) {
                    final KfgzRewardInfoRes sInfo10 = this.kfgzGatewayService.handleKfgzRewardInfo();
                    response.setMessage(sInfo10);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETSCHEDULEINFO_FROM_MATCH) {
                    final MatchServerEntity ms = (MatchServerEntity)request.getMessage();
                    final KfgzScheduleInfoList sInfo7 = this.kfgzGatewayService.getSchInfoFromMatch(ms);
                    response.setMessage(sInfo7);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETAllSCHEDULEINFO_FROM_GAME_SERVER) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfgzScheduleInfoList sInfo7 = this.kfgzGatewayService.handleGamerServerKfgzAllScheduleInfo(gs);
                    response.setMessage(sInfo7);
                }
                else if (request.getCommand() == Command.KFGZ_GW_PUSHBATTLERESULT_FROM_MATCH) {
                    final KfgzBattleResultInfo bInfo = (KfgzBattleResultInfo)request.getMessage();
                    final KfgzBattleResultRes rInfo2 = this.kfgzGatewayService.handleBattleResInfo(bInfo);
                    response.setMessage(rInfo2);
                }
                else if (request.getCommand() == Command.KFGZ_GW_GETBATTLERRANKING_FROM_GAME_SERVER) {
                    final kfgzNationGzKey ngKey = (kfgzNationGzKey)request.getMessage();
                    final KfgzAllRankRes res2 = this.kfgzGatewayService.getBattleRankInfo(ngKey);
                    response.setMessage(res2);
                }
                else if (request.getCommand() == Command.KFZB_GW_SEASONINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfzbSeasonInfo sInfo11 = this.kfzbGatewayService.handleSeasonInfo(gs);
                    response.setMessage(sInfo11);
                }
                else if (request.getCommand() == Command.KFZB_GW_REWARDINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfzbRewardInfo sInfo12 = this.kfzbGatewayService.handleRewardInfo();
                    response.setMessage(sInfo12);
                }
                else if (request.getCommand() == Command.KFZB_GW_SEASONINFO_FROM_MATCH) {
                    final KfzbSeasonInfo sInfo13 = this.kfzbGatewayService.handleSeasonInfo(null);
                    response.setMessage(sInfo13);
                }
                else if (request.getCommand() == Command.KFZB_GW_REWARDINFO_FROM_MATCH) {
                    final KfzbRewardInfo sInfo14 = this.kfzbGatewayService.handleRewardInfo();
                    response.setMessage(sInfo14);
                }
                else if (request.getCommand() == Command.KFZB_GW_REWARDINFO) {
                    final KfzbRewardInfo sInfo14 = this.kfzbGatewayService.handleRewardInfo();
                    response.setMessage(sInfo14);
                }
                else if (request.getCommand() == Command.KFZB_GW_PLAYERLIMIT) {
                    final KfzbPlayerLimitInfo sInfo15 = this.kfzbGatewayService.handlePlayerLimitInfo();
                    response.setMessage(sInfo15);
                }
                else if (request.getCommand() == Command.KFZB_WINNER_INFO) {
                    final KfzbWinnerInfo winInfo = (KfzbWinnerInfo)request.getMessage();
                    this.kfzbGatewayService.handleWinnerInfo(winInfo);
                    response.setMessage(new Integer(1));
                }
                else if (request.getCommand() == Command.KFZB_GETTOP16PLAYERINFO) {
                    final KfzbWinnerInfo winInfo = this.kfzbGatewayService.handleGetTop16PlayerInfo();
                    response.setMessage(winInfo);
                }
                else if (request.getCommand() == Command.SI_SYNFEAST_INFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfzbFeastInfo feastInfo = this.kfzbFeastService.getFeastInfo(gs);
                    response.setMessage(feastInfo);
                }
                else if (request.getCommand() == Command.SI_PATICIPATEFEAST) {
                    final Map<Integer, Long> map = this.kfzbFeastService.addNewFeastParticipate((KfzbFeastParticipateInfo)request.getMessage());
                    final KfzbFeastPlayerRoomInfo info = new KfzbFeastPlayerRoomInfo();
                    info.setMap(map);
                    response.setMessage(info);
                }
                else if (request.getCommand() == Command.SI_ORGANIZER_ADDFEAST) {
                    this.kfzbFeastService.organizerAddFeast((KfzbFeastOrganizer)request.getMessage());
                    response.setMessage(1);
                }
                else if (request.getCommand() == Command.SI_FEASTROOMINFO) {
                    final KfzbRoomInfoList list = this.kfzbFeastService.getRoomInfo((KfzbRoomKeyList)request.getMessage());
                    response.setMessage(list);
                }
                else if (request.getCommand() == Command.KFWD_GW_TREASURE_REWARDINFO) {
                    final GameServerEntity gs = (GameServerEntity)request.getMessage();
                    final KfwdRankTreasureList list2 = this.kfwdGatewayService.handleGetTreasureRewardInfo(gs);
                    response.setMessage(list2);
                }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
            try {
                GWController.log.error("handle request, param:" + Types.OBJECT_MAPPER.writeValueAsString(request), t);
            }
            catch (Exception ex) {}
            response.setMessage((Object)null);
        }
        return response;
    }
}
