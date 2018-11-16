package com.reign.gcld.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import org.apache.commons.lang.*;
import com.reign.kf.comm.protocol.*;

@Component("matchService")
public class MatchService implements IMatchService, InitializingBean, ResponseHandler
{
    private static Log logger;
    private KfConnection connection;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerDao playerDao;
    private static ScheduledExecutorService executor;
    
    static {
        MatchService.logger = new KfwdMatchOperationLogger();
        MatchService.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }
    
    public static ScheduledExecutorService getExecutor() {
        return MatchService.executor;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    }
    
    @Override
	public void handle(final Response response) {
        if (response.getCommand() == Command.QUERY_SEASONINFO) {
            MatchService.logger.info("\u83b7\u5f97gw\u4fe1\u606f");
            final List<SeasonInfoEntity> siList = (List<SeasonInfoEntity>)response.getMessage();
            for (final SeasonInfoEntity entity : siList) {
                if (entity.getState() == 2) {
                    if (MatchManager.getInstance().containMatch(entity.getTag())) {
                        continue;
                    }
                    final Match match = new Match(entity, this.dataGetter);
                    if (!match.isPassServerCondition()) {
                        continue;
                    }
                    MatchManager.getInstance().addMatch(match);
                    match.init();
                }
                else {
                    if (entity.getState() != 3 || !MatchManager.getInstance().containMatch(entity.getTag())) {
                        continue;
                    }
                    MatchService.logger.info("\u53d6\u6d88\u6bd4\u8d5b");
                    final Match match = MatchManager.getInstance().getMatch(entity.getTag());
                    match.cancel();
                    MatchManager.getInstance().removeMatch(entity.getTag());
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("matchState", 7);
                    doc.endObject();
                    final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
                    for (final PlayerDto onlinePlayer : onlinePlayerList) {
                        Players.push(onlinePlayer.playerId, PushCommand.PUSH_KFWD_MATCH, doc.toByte());
                    }
                }
            }
        }
    }
    
    @Override
    public byte[] getMatchInfo(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().query(player);
    }
    
    @Override
    public byte[] signUp(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final List<PlayerGeneralMilitary> list = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final StringBuilder sb = new StringBuilder();
        for (final PlayerGeneralMilitary pg : list) {
            sb.append(pg.getGeneralId());
            sb.append("#");
        }
        return MatchManager.getInstance().signup(player, sb.toString());
    }
    
    @Override
    public byte[] enter(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().enter(player);
    }
    
    @Override
    public byte[] exit(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().exit(player);
    }
    
    @Override
    public byte[] setFormation(final String gIds, final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (StringUtils.isEmpty(gIds)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        return MatchManager.getInstance().setFormation(player, gIds);
    }
    
    @Override
    public byte[] changeRewardMode(final int mode, final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().changeRewardMode(player, mode);
    }
    
    @Override
    public byte[] inspire(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().inspire(player);
    }
    
    @Override
    public byte[] getBoxInfo(final PlayerDto playerDto) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        return MatchManager.getInstance().getBoxInfo(player);
    }
    
    @Override
    public byte[] receiveBox(final PlayerDto playerDto, final int point) {
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (point != 3 && point != 6 && point != 9 && point != 12 && point != 15) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        return MatchManager.getInstance().receiveBox(player, point, this.dataGetter);
    }
    
    @Override
    public byte[] getRankList(final PlayerDto playerDto) {
        return MatchManager.getInstance().getRankList(playerDto);
    }
    
    private class SyncGWThread extends Thread
    {
        public SyncGWThread() {
            super("sync-gw-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    final Request request = new Request();
                    request.setCommand(Command.QUERY_SEASONINFO);
                    MatchService.this.connection.sendSync(request);
                }
                catch (Exception e) {
                    MatchService.logger.error("gw query thread error", e);
                }
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException e2) {
                    MatchService.logger.error("gw query thread error", e2);
                }
            }
        }
    }
}
