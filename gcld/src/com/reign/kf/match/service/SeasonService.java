package com.reign.kf.match.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.kf.comm.transfer.oio.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kf.match.log.*;
import com.reign.kf.comm.transfer.*;
import com.reign.kf.match.common.*;
import org.apache.commons.logging.*;
import com.reign.kf.comm.entity.gw.*;
import java.util.*;
import com.reign.kf.match.model.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.param.gw.*;
import com.reign.util.*;

@Component("seasonService")
public class SeasonService implements ISeasonService, InitializingBean, ResponseHandler
{
    private static final Logger log;
    private static final InterfaceLogger interfaceLog;
    private KfConnection connection;
    @Autowired
    private IDataGetter dataGetter;
    
    static {
        log = CommonLog.getLog(SeasonService.class);
        interfaceLog = new InterfaceLogger();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.connection = new KfConnection((TransferConfig)new TransferConfig() {
            @Override
			public long getSendInterval() {
                return 2147483647L;
            }
            
            @Override
			public String getHost() {
                return Configuration.getProperty("gcld.match.gwHost");
            }
            
            @Override
			public int getPort() {
                return Configuration.getIntProperty("gcld.match.gwPort");
            }
            
            @Override
			public boolean compress() {
                return false;
            }
            
            @Override
			public String getCommand() {
                return Configuration.getProperty("gcld.match.gwCommand");
            }
        }, (Log)SeasonService.interfaceLog);
    }
    
    @Override
	public void handle(final Response response) {
        if (response.getCommand() == Command.QUERY_SEASONINFO) {
            final List<SeasonInfoEntity> siList = (List<SeasonInfoEntity>)response.getMessage();
            for (final SeasonInfoEntity entity : siList) {
                if (!entity.getMatchServer().equalsIgnoreCase(Configuration.getProperty("gcld.kfmatch.identityId"))) {
                    continue;
                }
                if (entity.getState() == 0) {
                    this.echoConnect(entity);
                    final Match match = MatchManager.getInstance().createMatch(entity, this.dataGetter);
                    match.init();
                    this.echoReady(entity);
                    SeasonService.log.info("\u4e3b\u529e\u6bd4\u8d5b:" + match.getMatchTag());
                }
                else if (entity.getState() == 3) {
                    final Match match = MatchManager.getInstance().getMatch(entity.getTag());
                    if (match == null) {
                        continue;
                    }
                    match.cancel();
                    SeasonService.log.info("\u53d6\u6d88\u6bd4\u8d5b:" + match.getMatchTag());
                }
                else {
                    if (entity.getState() != 2) {
                        continue;
                    }
                    Match match = MatchManager.getInstance().getMatch(entity.getTag());
                    if (match == null) {
                        match = MatchManager.getInstance().createMatch(entity, this.dataGetter);
                        match.recover();
                        SeasonService.log.info("\u6062\u590d\u6bd4\u8d5b:" + match.getMatchTag());
                    }
                    else {
                        if (match.getState() != 6) {
                            continue;
                        }
                        this.echoFinish(entity);
                        SeasonService.log.info("\u6bd4\u8d5b\u7ed3\u675f:" + match.getMatchTag());
                    }
                }
            }
        }
    }
    
    private void echoFinish(final SeasonInfoEntity entity) {
        final Request request = new Request();
        request.setCommand(Command.UPDATE_SEASONINFO);
        final UpdateSeasonParam param = new UpdateSeasonParam();
        param.setId(entity.getId());
        param.setSeason(entity.getSeason());
        param.setState(4);
        request.setMessage(param);
        this.connection.sendSync(request);
    }
    
    private void echoCancelConfirm(final SeasonInfoEntity entity) {
        final Request request = new Request();
        request.setCommand(Command.UPDATE_SEASONINFO);
        final UpdateSeasonParam param = new UpdateSeasonParam();
        param.setId(entity.getId());
        param.setSeason(entity.getSeason());
        param.setState(5);
        request.setMessage(param);
        this.connection.sendSync(request);
    }
    
    private void echoReady(final SeasonInfoEntity entity) {
        final Request request = new Request();
        request.setCommand(Command.UPDATE_SEASONINFO);
        final UpdateSeasonParam param = new UpdateSeasonParam();
        param.setId(entity.getId());
        param.setSeason(entity.getSeason());
        param.setState(2);
        request.setMessage(param);
        this.connection.sendSync(request);
    }
    
    private void echoConnect(final SeasonInfoEntity entity) {
        final Request request = new Request();
        request.setCommand(Command.UPDATE_SEASONINFO);
        final UpdateSeasonParam param = new UpdateSeasonParam();
        param.setId(entity.getId());
        param.setSeason(entity.getSeason());
        param.setState(1);
        param.setTag(UUIDHexGenerator.getInstance().generate());
        request.setMessage(param);
        this.connection.sendSync(request);
        entity.setTag(param.getTag());
    }
    
    private class SyncThread extends Thread
    {
        public SyncThread() {
            super("match-sync-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    final Request request = new Request();
                    request.setCommand(Command.QUERY_SEASONINFO);
                    SeasonService.this.connection.sendSync(request);
                    Thread.sleep(Configuration.getIntProperty("gcld.kfmatch.syncSeasonInterval"));
                }
                catch (Exception e) {
                    SeasonService.log.error("sync season", e);
                }
            }
        }
    }
}
