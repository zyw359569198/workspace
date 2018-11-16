package com.reign.gcld.player.message;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.user.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.player.message.login.*;
import org.springframework.transaction.annotation.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.player.action.*;
import java.text.*;
import com.reign.gcld.antiaddiction.util.*;
import com.reign.gcld.antiaddiction.*;
import com.reign.gcld.user.domain.*;

@Component("loginMessageHandler")
public class LoginMessageHandler implements Handler
{
    private static final Logger rtLog;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IUserLoginInfoDao userLoginInfoDao;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private DataPushCenterUtil dataPushCenterUtil;
    @Autowired
    private ICityService cityService;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IDataGetter dataGetter;
    
    static {
        rtLog = new RTReportLogger();
    }
    
    @Transactional
    @Override
    public void handler(final Message message) {
        if (message == null) {
            return;
        }
        if (message instanceof LoginMessage) {
            final LoginMessage loginMessage = (LoginMessage)message;
            if (loginMessage.getPlayerDto() == null || loginMessage.getPlayerDto().playerId == 0) {
                return;
            }
            if (Action.LOGIN.equals(loginMessage.getAction())) {
                this.handlerLogin(loginMessage);
            }
            else if (Action.LOGINOUT.equals(loginMessage.getAction())) {
                this.handlerLoginOut(loginMessage);
            }
            else if (Action.REGISTER.equals(loginMessage.getAction())) {
                this.handlerRegister(loginMessage);
            }
        }
    }
    
    private void handlerRegister(final LoginMessage loginMessage) {
        this.playerDao.updateLoginTime(loginMessage.getPlayerDto().playerId, new Date());
        final Player player = this.playerDao.read(loginMessage.getPlayerDto().playerId);
        LoginMessageHandler.rtLog.info(LogUtil.formatReportCreateLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "c", loginMessage.getRoleIndex(), player.getForceId(), player.getConsumeLv(), loginMessage.getPlayerDto().yxSource, null));
    }
    
    private void handlerLoginOut(final LoginMessage loginMessage) {
        final Date date = new Date();
        final int playerId = loginMessage.getPlayerDto().playerId;
        this.playerDao.updateQuitTime(playerId, date);
        if (loginMessage.getUserDto().isNeedAntiAddiction()) {
            this.handlerLogoutWithoutBanshuServer(loginMessage);
        }
        final Player player = this.playerDao.read(playerId);
        LoginMessageHandler.rtLog.info(LogUtil.formatReportLoginLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "lo", loginMessage.getUserDto().firstLogin, loginMessage.getPlayerDto().yxSource, player.getForceId(), player.getConsumeLv(), loginMessage.getPlayerDto()));
        BuildingService.clearPlayerBuilding(playerId);
        this.techEffectCache.clearTechEffect(playerId);
        this.buildingOutputCache.logoutClear(playerId);
        this.dataPushCenterUtil.remove(playerId);
        this.cityService.clearGeneralsMove(playerId);
        this.playerService.removeTopLv(playerId);
        this.countDailyOnlineTime(playerId);
        PlayerAction.clearPKeyInfo(playerId);
        ValidateCodeAction.playerCodeMap.remove(playerId);
        this.dataGetter.getCourtesyService().removePlayerAfterLogOut(loginMessage.getPlayerDto());
    }
    
    private void countDailyOnlineTime(final int playerId) {
        final Date loginTime = this.playerDao.getLoginTimeByPlayerId(playerId);
        final Date quitTime = this.playerDao.getQuitTimeByPlayerId(playerId);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date daily0Click = null;
        try {
            daily0Click = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = 0L;
        if (loginTime.before(daily0Click)) {
            diff = quitTime.getTime() - daily0Click.getTime();
        }
        else {
            diff = quitTime.getTime() - loginTime.getTime();
        }
        if (diff < 0L) {
            diff = 0L;
        }
        this.playerDao.updateDailyOnlineTime((int)diff / 1000, playerId);
    }
    
    private void handlerLogin(final LoginMessage loginMessage) {
        final int playerId = loginMessage.getPlayerDto().playerId;
        this.playerDao.updateLoginTime(playerId, new Date());
        if (loginMessage.getUserDto().isNeedAntiAddiction()) {
            this.handleLoginWithoutBanshuServer(loginMessage);
        }
        final Player player = this.playerDao.read(playerId);
        LoginMessageHandler.rtLog.info(LogUtil.formatReportLoginLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "li", loginMessage.getUserDto().firstLogin, loginMessage.getPlayerDto().yxSource, player.getForceId(), player.getConsumeLv(), loginMessage.getPlayerDto()));
    }
    
    private void handleLoginWithoutBanshuServer(final LoginMessage loginMessage) {
        final Date nowDate = new Date();
        final UserLoginInfo uli = AntiAddictionUtil.getUserLoginInfo(this.userLoginInfoDao, loginMessage.getPlayerDto());
        final Date refreshPoint = AntiAddictionUtil.getOnlineTimeRefreshPoint();
        if (uli.getTag() == 1) {
            if (uli.getLastLoginTime().after(refreshPoint)) {
                uli.setOnlineTime(uli.getOnlineTime() + nowDate.getTime() - uli.getLastLoginTime().getTime());
            }
            else {
                uli.setOnlineTime(nowDate.getTime() - refreshPoint.getTime());
            }
            uli.setLastLogoutTime(nowDate);
        }
        else if (uli.getLastLogoutTime() != null && uli.getLastLogoutTime().before(refreshPoint)) {
            uli.setOnlineTime(0L);
            uli.setOfflineTime(0L);
        }
        else if (uli.getLastLogoutTime() != null) {
            final long offlineTime = nowDate.getTime() - uli.getLastLogoutTime().getTime() + uli.getOfflineTime();
            if (offlineTime >= AntiAddictionStateFactory.MAX_RESETOFFLINETIME) {
                uli.setOnlineTime(0L);
                uli.setOfflineTime(0L);
            }
            else {
                uli.setOfflineTime(offlineTime);
            }
        }
        uli.setLastLoginTime(nowDate);
        uli.setTag(1);
        this.userLoginInfoDao.update(uli);
        loginMessage.getUserDto().setOnlineTime(uli.getOnlineTime());
        loginMessage.getUserDto().setLoginTime(uli.getLastLoginTime().getTime());
    }
    
    private void handlerLogoutWithoutBanshuServer(final LoginMessage loginMessage) {
        final Date nowDate = new Date();
        final UserLoginInfo uli = AntiAddictionUtil.getUserLoginInfo(this.userLoginInfoDao, loginMessage.getPlayerDto());
        final Date refreshPoint = AntiAddictionUtil.getOnlineTimeRefreshPoint();
        if (uli.getTag() == 0) {
            if (uli.getLastLogoutTime() == null) {
                uli.setLastLogoutTime(nowDate);
            }
            if (uli.getLastLogoutTime().after(refreshPoint)) {
                uli.setOnlineTime(uli.getOnlineTime() + nowDate.getTime() - uli.getLastLogoutTime().getTime());
                uli.setLastLoginTime(uli.getLastLogoutTime());
            }
            else {
                uli.setOnlineTime(nowDate.getTime() - refreshPoint.getTime());
                uli.setLastLoginTime(refreshPoint);
            }
        }
        else if (uli.getLastLoginTime().after(refreshPoint)) {
            uli.setOnlineTime(uli.getOnlineTime() + nowDate.getTime() - uli.getLastLoginTime().getTime());
        }
        else {
            uli.setOnlineTime(nowDate.getTime() - refreshPoint.getTime());
            uli.setLastLoginTime(refreshPoint);
        }
        uli.setLastLogoutTime(nowDate);
        uli.setTag(0);
        this.userLoginInfoDao.update(uli);
        loginMessage.getUserDto().setOnlineTime(uli.getOnlineTime());
    }
}
