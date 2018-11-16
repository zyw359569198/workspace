package com.reign.gcld.task.service;

import org.springframework.stereotype.*;
import com.reign.gcld.task.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.mail.service.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.task.request.*;
import org.apache.commons.lang.*;
import com.reign.gcld.task.reward.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.plugin.yx.util.kingnet.udplog.*;
import com.reign.plugin.yx.util.kingnet.demo.*;
import com.reign.gcld.user.dto.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.util.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.reward.*;

@Component("playerTaskService")
public class PlayerTaskService implements IPlayerTaskService
{
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IDataGetter taskDataGetter;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IYxOperation yxOperation;
    private static final Logger errorLog;
    private static List<Tuple<Integer, Integer>> dailyBattleList;
    
    static {
        errorLog = CommonLog.getLog(PlayerTaskService.class);
        (PlayerTaskService.dailyBattleList = new ArrayList<Tuple<Integer, Integer>>()).add(new Tuple(1, 2));
        PlayerTaskService.dailyBattleList.add(new Tuple(2, 5));
        PlayerTaskService.dailyBattleList.add(new Tuple(3, 9));
        PlayerTaskService.dailyBattleList.add(new Tuple(4, 14));
        PlayerTaskService.dailyBattleList.add(new Tuple(5, 20));
        PlayerTaskService.dailyBattleList.add(new Tuple(6, 27));
    }
    
    @Override
    public byte[] getCurTaskSimpleInfo(final int playerId) {
        final List<PlayerTask> playerTasks = this.playerTaskDao.getDisPlayPlayerTask(playerId);
        final List<TaskChangeContent> changContentList = new ArrayList<TaskChangeContent>();
        for (final PlayerTask playerTask : playerTasks) {
            GameTask task = null;
            if (playerTask.getType() == 1) {
                task = TaskFactory.getInstance().getTask(playerTask.getTaskId());
                if (playerTask.getState() == 2) {
                    final GameTask nextTask = TaskFactory.getInstance().getNextTask(task);
                    if (nextTask == null) {
                        task = null;
                    }
                    else {
                        playerTask.setTaskId(nextTask.getId());
                        playerTask.setState(1);
                        playerTask.setProcess(0);
                        playerTask.setStartTime(System.currentTimeMillis());
                        this.playerTaskDao.update(playerTask);
                        task = nextTask;
                        this.dealTimeUpTask(playerId, nextTask.getTarget());
                    }
                }
            }
            else if (playerTask.getType() == 3) {
                task = TaskFactory.getInstance().getTask(playerTask.getGroupId(), playerTask.getTaskId(), playerTask.getType());
            }
            if (task != null) {
                PlayerDto playerDto = Players.getPlayer(playerId);
                if (playerDto == null) {
                    playerDto = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final TaskRequestProcessViewer requestProcess = task.getTaskRequest().getProcess(playerDto, this.taskDataGetter, playerTask.getVId());
                if (task.getTaskRequest().isMobileFastFinish(playerDto)) {
                    requestProcess.setCompleted(true);
                    requestProcess.setCurrNum(requestProcess.getWannaNum());
                }
                final TaskChangeContent taskChangeContent = new TaskChangeContent(task, requestProcess.getProcessStr(), requestProcess.isCompleted());
                changContentList.add(taskChangeContent);
            }
        }
        return TaskBuilderJson.sendJsonTaskInfo(changContentList, Players.getPlayer(playerId), this.taskDataGetter, playerId);
    }
    
    private void dealTimeUpTask(final int playerId, final String target) {
        if (StringUtils.isBlank(target)) {
            return;
        }
        final String[] targets = target.split(";");
        String[] array;
        for (int length = (array = targets).length, i = 0; i < length; ++i) {
            final String str = array[i];
            final String[] s = str.split(",");
            final String type = s[0];
            if ("times_up".equalsIgnoreCase(type)) {
                this.jobService.addJob("playerTaskService", "timesUpExe", new StringBuilder(String.valueOf(playerId)).toString(), System.currentTimeMillis() + Integer.valueOf(s[1]) * 60000L + 5000L, true);
            }
        }
    }
    
    @Override
    public void timesUpExe(final String str) {
        TaskMessageHelper.sendTimesUpTaskMessage(Integer.valueOf(str));
    }
    
    @Override
    public byte[] getCurTaskInfo(final PlayerDto playerDto) {
        return JsonBuilder.getJson(State.SUCCESS, this.getAllTaskInfo(playerDto));
    }
    
    @Override
    public byte[] getAllTaskInfo(final PlayerDto playerDto) {
        final List<Tuple<TaskChangeContent, Map<Integer, Reward>>> taskList = new ArrayList<Tuple<TaskChangeContent, Map<Integer, Reward>>>();
        final List<PlayerTask> playerTasks = this.playerTaskDao.getDisPlayPlayerTask(playerDto.playerId);
        boolean hasMainLineTask = false;
        for (final PlayerTask playerTask : playerTasks) {
            GameTask task = null;
            if (playerTask.getType() == 1) {
                hasMainLineTask = true;
                task = TaskFactory.getInstance().getTask(playerTask.getTaskId());
                if (playerTask.getState() == 2) {
                    final GameTask nextTask = TaskFactory.getInstance().getNextTask(task);
                    if (nextTask == null) {
                        task = null;
                    }
                    else {
                        playerTask.setTaskId(nextTask.getId());
                        playerTask.setState(1);
                        playerTask.setProcess(0);
                        playerTask.setStartTime(System.currentTimeMillis());
                        this.playerTaskDao.update(playerTask);
                        task = nextTask;
                        this.dealTimeUpTask(playerTask.getPlayerId(), nextTask.getTarget());
                    }
                }
            }
            else if (playerTask.getType() == 3) {
                task = TaskFactory.getInstance().getTask(playerTask.getGroupId(), playerTask.getTaskId(), playerTask.getType());
            }
            if (task != null) {
                final TaskRequestProcessViewer requestProcess = task.getTaskRequest().getProcess(playerDto, this.taskDataGetter, playerTask.getVId());
                if (task.getTaskRequest().isMobileFastFinish(playerDto)) {
                    requestProcess.setCompleted(true);
                    requestProcess.setCurrNum(requestProcess.getWannaNum());
                }
                final TaskChangeContent taskChangeContent = new TaskChangeContent(task, requestProcess.getProcessStr(), requestProcess.isCompleted());
                final Tuple<TaskChangeContent, Map<Integer, Reward>> tuple = new Tuple();
                tuple.left = taskChangeContent;
                tuple.right = task.getTaskRewarList(playerDto, this.taskDataGetter);
                taskList.add(tuple);
            }
        }
        if (!hasMainLineTask) {
            final GameTask task2 = TaskFactory.getInstance().getTaskBySeq(TaskFactory.getInstance().getTaskNum());
            final TaskRequestProcessViewer requestProcess2 = task2.getTaskRequest().getProcess(playerDto, this.taskDataGetter, 0);
            if (task2.getTaskRequest().isMobileFastFinish(playerDto)) {
                requestProcess2.setCompleted(true);
                requestProcess2.setCurrNum(requestProcess2.getWannaNum());
            }
            final TaskChangeContent taskChangeContent2 = new TaskChangeContent(task2, requestProcess2.getProcessStr(), requestProcess2.isCompleted());
            final Tuple<TaskChangeContent, Map<Integer, Reward>> tuple2 = new Tuple();
            tuple2.left = taskChangeContent2;
            tuple2.right = task2.getTaskRewarList(playerDto, this.taskDataGetter);
            taskList.add(tuple2);
        }
        return TaskBuilderJson.sendJsonTaskInfo(taskList, this.taskDataGetter, playerDto);
    }
    
    @Transactional
    @Override
    public byte[] finishCurTask(final PlayerDto playerDto, final int type, final int group, final int index) {
        if (playerDto == null || playerDto.playerId <= 0) {
            JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        if (type != 1 && type != 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int playerId = playerDto.playerId;
        PlayerTask playerTask = null;
        GameTask gameTask = null;
        if (type == 1) {
            playerTask = this.playerTaskDao.getCurMainTask(playerId);
            gameTask = TaskFactory.getInstance().getTask(playerTask.getTaskId());
        }
        else if (type == 2) {
            playerTask = this.playerTaskDao.getDailyTask(playerId, group, index);
            gameTask = TaskFactory.getInstance().getTask(group, index, type);
        }
        else if (type == 3) {
            playerTask = this.playerTaskDao.getBranchTask(playerId, group, index);
            gameTask = TaskFactory.getInstance().getTask(group, index, type);
        }
        if (playerTask == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TASK_NO_TASK);
        }
        if (playerTask.getState() == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TASK_TASK_FINISHED);
        }
        if (gameTask == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TASK_TASK_NOT_EXIST);
        }
        if (!gameTask.getTaskRequest().isMobileFastFinish(playerDto) && !gameTask.getTaskRequest().doRequest(playerDto, this.taskDataGetter, playerTask.getVId())) {
            Players.push(playerDto.playerId, PushCommand.PUSH_TASK, this.getAllTaskInfo(playerDto));
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (type == 1) {
            this.dataGetter.getCourtesyService().addPlayerEvent(playerId, 10, playerTask.getTaskId());
        }
        else if (type == 3) {
            this.dataGetter.getCourtesyService().addPlayerEvent(playerId, 11, playerTask.getGroupId());
        }
        final Map<Integer, Reward> rewardMap = gameTask.getTaskReward().rewardPlayer(playerDto, this.taskDataGetter, "\u4efb\u52a1\u5956\u52b1", null);
        if (type == 1) {
            final GameTask nextTask = TaskFactory.getInstance().getNextTask(gameTask);
            if (nextTask == null) {
                final TaskChangeContent taskChangeContent = new TaskChangeContent();
                taskChangeContent.setType(1);
                Players.push(playerDto.playerId, PushCommand.PUSH_TASK, TaskBuilderJson.sendJsonTaskChange(taskChangeContent, playerDto, this.taskDataGetter));
                playerTask.setState(2);
                this.playerTaskDao.update(playerTask);
            }
            else {
                playerTask.setTaskId(nextTask.getId());
                playerTask.setState(1);
                playerTask.setProcess(0);
                playerTask.setStartTime(System.currentTimeMillis());
                this.playerTaskDao.update(playerTask);
                this.dealTimeUpTask(playerTask.getPlayerId(), nextTask.getTarget());
                Players.push(playerDto.playerId, PushCommand.PUSH_TASK, this.getAllTaskInfo(playerDto));
                final JsonDocument fbDoc = new JsonDocument();
                fbDoc.startObject();
                fbDoc.endObject();
                Players.push(playerDto.playerId, PushCommand.PUSH_FB_GUIDE, fbDoc.toByte());
            }
            if (this.yxOperation.checkTencentPf(playerDto.yx)) {
                try {
                    final UserDto userDto = Users.getUserDto(playerDto.userId, playerDto.yx);
                    final LogUserInfo logInfo = new LogUserInfo();
                    logInfo.setGameTime(System.currentTimeMillis() / 1000L);
                    logInfo.setOuid(userDto.getOpenId());
                    logInfo.setIuid(new StringBuilder(String.valueOf(playerId)).toString());
                    logInfo.setUserLevel(playerDto.playerLv);
                    logInfo.setVipLevel(new StringBuilder(String.valueOf(userDto.getYellowVipLevel())).toString());
                    logInfo.setTimestamp(System.currentTimeMillis() / 1000L);
                    final UdpSender udpSender = new UdpSender();
                    udpSender.sendGuideLog(logInfo, gameTask.getId());
                }
                catch (Exception e) {
                    PlayerTaskService.errorLog.error("#PlayerTaskService#finishCurTask#kingnet udp sent failed.Exception e :" + e);
                }
            }
        }
        else if (type == 3) {
            this.playerTaskDao.deleteById(playerTask.getVId());
            Players.push(playerDto.playerId, PushCommand.PUSH_TASK, this.getAllTaskInfo(playerDto));
        }
        else if (type == 2) {
            playerTask.setState(2);
            this.playerTaskDao.update(playerTask);
            Players.push(playerDto.playerId, PushCommand.PUSH_TASK, this.getAllTaskInfo(playerDto));
        }
        return JsonBuilder.getJson(State.SUCCESS, gameTask.getTaskRewardJsonDesc(playerDto, this.taskDataGetter, rewardMap));
    }
    
    @Override
    public void handleMessage(final TaskMessage taskMessage) {
        final List<PlayerTask> taskList = this.playerTaskDao.getDisPlayPlayerTask(taskMessage.getPlayerId());
        GameTask gameTask = null;
        for (final PlayerTask playerTask : taskList) {
            final int taskType = playerTask.getType();
            if (taskType == 1) {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getTaskId());
            }
            else {
                gameTask = TaskFactory.getInstance().getTask(playerTask.getGroupId(), playerTask.getTaskId(), taskType);
            }
            if (gameTask == null) {
                continue;
            }
            if (!gameTask.getTaskRequest().isConcernedMessage(taskMessage)) {
                continue;
            }
            gameTask.getTaskRequest().handleMessage(taskMessage, this.taskDataGetter, playerTask.getVId());
        }
    }
    
    @Override
    public void resetDailyTask(final int playerId) {
        this.playerTaskDao.resetDailyTask(playerId);
    }
    
    @Override
    public byte[] getDailyBattleTaskInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final int rewardTimes = pa.getBattleRewardTimes();
        final int winTimes = pa.getBattleWinTimes();
        int canRewardBattleTimes = 0;
        int canRewardTimes = 0;
        for (final Tuple<Integer, Integer> tuple : PlayerTaskService.dailyBattleList) {
            if (winTimes < tuple.right) {
                break;
            }
            canRewardBattleTimes = tuple.right;
            canRewardTimes = tuple.left;
        }
        int availableWinTimes = winTimes - canRewardBattleTimes;
        int requestedTimes = 0;
        if (canRewardTimes == 6) {
            requestedTimes = 0;
            availableWinTimes = 0;
        }
        else {
            requestedTimes = canRewardTimes + 2;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("requestedTimes", requestedTimes);
        doc.createElement("needWinTimes", requestedTimes - availableWinTimes);
        doc.createElement("canReceiveTimes", canRewardTimes - rewardTimes);
        doc.createElement("alreadyReceiveTimes", rewardTimes);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] receiveDailyBattleTaskReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final int rewardTimes = pa.getBattleRewardTimes();
        if (rewardTimes >= 6) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TASK_REWARD_ALREADY_RECEIVED);
        }
        final int requestedTimes = rewardTimes + 2;
        int winTimes = pa.getBattleWinTimes();
        for (int i = 0; i < rewardTimes; ++i) {
            final int needWinTimes = i + 2;
            winTimes -= needWinTimes;
        }
        if (winTimes < requestedTimes) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TASK_NOT_FIT_REQUEST);
        }
        final List<ResourceDto> basicResourceList = this.resourceService.buildingOutputPerHour(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("reward");
        for (final ResourceDto dto : basicResourceList) {
            dto.setValue(dto.getValue() * 0.2);
            if (dto.getValue() > 0.0) {
                doc.startObject();
                doc.createElement("type", dto.getType());
                doc.createElement("value", (int)dto.getValue());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        this.playerResourceDao.addResourceIgnoreMax(playerId, basicResourceList, "\u6bcf\u65e5\u526f\u672c\u6d3b\u52a8\u5956\u52b1\u8d44\u6e90", true);
        this.playerAttributeDao.addBattleRewardTimes(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void cleanCurrBranchTask(final PlayerDto playerDto) {
        final List<PlayerTask> taskList = this.playerTaskDao.getDisPlayPlayerTask(playerDto.playerId);
        for (final PlayerTask pt : taskList) {
            if (pt.getType() == 3) {
                final GameTask gameTask = TaskFactory.getInstance().getTask(pt.getGroupId(), pt.getTaskId(), 3);
                if (gameTask != null) {
                    gameTask.getTaskReward().rewardPlayer(playerDto, this.taskDataGetter, "\u4efb\u52a1\u5956\u52b1", null);
                }
                this.playerTaskDao.deleteById(pt.getVId());
            }
        }
    }
    
    @Override
    public byte[] guideUpdate(final PlayerDto playerDto, final int guideId) {
        this.playerAttributeDao.updateGuideId(playerDto.playerId, guideId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public void startFreshManTask(final Player p) {
        try {
            final long now = System.currentTimeMillis();
            this.jobService.addJob("playerTaskService", "endFreshManTask", String.valueOf(p.getPlayerId()), now + 259200000L);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("startFreshManTask " + e);
        }
    }
    
    @Override
    public void startPushFreshManTaskIcon(final Player p) {
        try {
            if (p == null || p.getCreateTime() == null) {
                return;
            }
            final long createTime = p.getCreateTime().getTime();
            final long now = System.currentTimeMillis();
            Players.push(p.getPlayerId(), PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("endTime", createTime - now + 259200000L));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("startPushFreshManTaskIcon " + e);
        }
    }
    
    @Override
    public void endFreshManTask(final String params) {
        if (StringUtils.isBlank(params)) {
            return;
        }
        final int playerId = Integer.parseInt(params);
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            return;
        }
        int gold = 0;
        final List<PlayerArmy> list = this.playerArmyDao.getPlayerArmyRewardList(playerId, 1);
        if (list == null || list.size() == 0) {
            return;
        }
        for (final PlayerArmy pArmy : list) {
            if (this.armiesCache.getHasGold().contains(pArmy.getArmyId())) {
                final Armies armies = (Armies)this.armiesCache.get((Object)pArmy.getArmyId());
                final String goldString = armies.getGoldReward();
                gold += Integer.parseInt(goldString.split(",")[1]);
            }
        }
        this.playerDao.addSysGold(player, gold, "\u65b0\u624b\u5f00\u670d\u4efb\u52a1\u5956\u52b1\u91d1\u5e01");
        final String header = LocalMessages.FRESH_MAN_MAIL_HEADER;
        final String content = MessageFormatter.format(LocalMessages.FRESH_MAN_MAIL_CONTENT, new Object[] { gold });
        this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, header, content, 1, playerId, new Date());
    }
    
    @Override
    public byte[] getFreshManTaskInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final long createTime = (player == null || player.getCreateTime() == null) ? 0L : player.getCreateTime().getTime();
        final long now = System.currentTimeMillis();
        long endTime = createTime + 259200000L - now;
        endTime = ((endTime > 0L) ? endTime : 0L);
        if (endTime <= 0L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TIME_END);
        }
        final List<Integer> hasGold = this.armiesCache.getHasGold();
        final int[] golds = new int[hasGold.size()];
        int nowGold = 0;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("armiesArray");
        int i = 0;
        for (final Integer index : hasGold) {
            doc.startObject();
            final Armies armies = (Armies)this.armiesCache.get((Object)index);
            doc.createElement("armiesId", index);
            doc.createElement("armiesName", armies.getName());
            final int generalId = armies.getChief();
            final General general = (General)this.generalCache.get((Object)generalId);
            doc.createElement("pic", general.getPic());
            doc.createElement("quality", general.getQuality());
            final String goldString = armies.getGoldReward();
            final int gold = Integer.parseInt(goldString.split(",")[1]);
            golds[i++] = gold;
            doc.createElement("gold", gold);
            final PlayerArmy playerArmy = this.playerArmyDao.getPlayerArmy(playerId, armies.getId());
            final int goldState = (playerArmy == null || playerArmy.getGoldReward() == null) ? 0 : playerArmy.getGoldReward();
            doc.createElement("goldState", goldState);
            if (goldState == 2) {
                nowGold += gold;
            }
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("goldArrays");
        int goldS = 0;
        for (int j = 0; j < golds.length; ++j) {
            goldS += golds[j];
            doc.createElement(goldS);
        }
        doc.endArray();
        doc.createElement("curGold", nowGold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getfmTaskReward(final PlayerDto playerDto, final int armiesId) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final long createTime = (player == null || player.getCreateTime() == null) ? 0L : player.getCreateTime().getTime();
        final long now = System.currentTimeMillis();
        long endTime = createTime + 259200000L - now;
        endTime = ((endTime > 0L) ? endTime : 0L);
        if (endTime <= 0L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TIME_END);
        }
        final Armies armies = (Armies)this.armiesCache.get((Object)armiesId);
        if (armies == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (!this.armiesCache.getHasGold().contains(armiesId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        final PlayerArmy playerArmy = this.playerArmyDao.getPlayerArmy(playerDto.playerId, armiesId);
        final int goldState = (playerArmy == null || playerArmy.getGoldReward() == null) ? 0 : playerArmy.getGoldReward();
        if (goldState == 0 || goldState == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        final String goldString = armies.getGoldReward();
        if (StringUtils.isBlank(goldString) || goldString.equals("null")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        RewardType.reward(goldString, this.taskDataGetter, playerDto.playerId, 7);
        this.playerArmyDao.updateGoldReward(playerDto.playerId, armiesId, 2);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        RewardType.rewardDoc(goldString, doc);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
