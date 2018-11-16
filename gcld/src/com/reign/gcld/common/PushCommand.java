package com.reign.gcld.common;

public enum PushCommand
{
    PUSH_UPDATE("PUSH_UPDATE", 0, "push@player", "update", "\u4e00\u822c\u7684\u63a8\u9001\u6d88\u606f"), 
    PUSH_BUILDING_OUTPUT("PUSH_BUILDING_OUTPUT", 1, "push@building", "output", "\u5efa\u7b5110s\u4ea7\u51fa"), 
    PUSH_BUILDING_UPGRADE("PUSH_BUILDING_UPGRADE", 2, "push@building", "upgrade", "\u5efa\u7b51\u5347\u7ea7"), 
    PUSH_CHAT_SEND("PUSH_CHAT_SEND", 3, "push@chat", "chatSend", "\u53d1\u9001\u804a\u5929\u4fe1\u606f"), 
    PUSH_BATTLE_DOBATTLE("PUSH_BATTLE_DOBATTLE", 4, "push@battle", "doBattle", "\u53d1\u9001\u6218\u6597\u4fe1\u606f"), 
    PUSH_GM_BATTLE_DOBATTLE("PUSH_GM_BATTLE_DOBATTLE", 5, "push@battle", "doGmBattle", "gm\u53d1\u9001\u6218\u6597\u4fe1\u606f"), 
    PUSH_TASK("PUSH_TASK", 6, "push@task", "curTask", "\u4efb\u52a1\u4fe1\u606f\u66f4\u65b0"), 
    PUSH_GENERAL("PUSH_GENERAL", 7, "push@general", "general", "\u6b66\u5c06\u62db\u52df"), 
    PUSH_GENERAL_BATTLE("PUSH_GENERAL_BATTLE", 8, "push@generalBattle", "general", "\u6b66\u5c06\u635f\u5931\u5175\u529b"), 
    PUSH_ANTIADDICTION("PUSH_ANTIADDICTION", 9, "push@antiaddiction", "antiaddiction", "\u9632\u6c89\u8ff7"), 
    PUSH_NOTICE("PUSH_NOTICE", 10, "push@notice", "notice", "\u9192\u76ee\u64ad\u62a5"), 
    PUSH_ATTMOV("PUSH_ATTMOV", 11, "push@cities", "attmov", "\u4e16\u754c\u91cc\u7684\u79fb\u52a8\u53d8\u5316"), 
    PUSH_CITIES("PUSH_CITIES", 12, "push@cities", "cities", "\u4e16\u754c\u4fe1\u606f\u53d8\u5316"), 
    PUSH_MANWANG("PUSH_MANWANG", 13, "push@cities", "manwang", "\u86ee\u738b\u4ee4\u57ce\u4fe1\u606f"), 
    PUSH_POLITICS_EVENT("PUSH_POLITICS_EVENT", 14, "push@politics", "politics", "\u653f\u52a1\u7cfb\u7edf\u4e8b\u4ef6"), 
    PUSH_OFFICER_BUILDING_APPLY("PUSH_OFFICER_BUILDING_APPLY", 15, "push@officerBuildingApply", "officerBuilding", "\u65b0\u5b98\u804c\u5efa\u7b51\u7533\u8bf7"), 
    PUSH_AUTO_POWER("PUSH_AUTO_POWER", 16, "push@autoPower", "autoPower", "\u81ea\u52a8\u526f\u672c\u6b21\u6570"), 
    PUSH_BATTLE_REWARD("PUSH_BATTLE_REWARD", 17, "push@batReward", "batReward", "\u6218\u6597\u5956\u52b1"), 
    PUSH_KFWD_MATCH("PUSH_KFWD_MATCH", 18, "push@kfwdMatch", "kfwdMatch", "\u8de8\u670d\u6b66\u6597"), 
    PUSH_KFWD_MATCH_REPORT("PUSH_KFWD_MATCH_REPORT", 19, "push@kfwdMatchReport", "kfwdMatchReport", "\u8de8\u670d\u6b66\u6597\u6218\u62a5"), 
    PUSH_KFGZ_MATCH("PUSH_KFGZ_MATCH", 20, "push@kfgzMatch", "kfgzMatch", "\u8de8\u670d\u56fd\u6218"), 
    PUSH_PLAYERINFO("PUSH_PLAYERINFO", 21, "push@getPlayerInfo", "info", "\u73a9\u5bb6\u91cd\u65b0\u767b\u5f55"), 
    PUSH_TRICKINFO("PUSH_TRICKINFO", 22, "push@trickInfo", "trickInfo", "\u73a9\u5bb6\u53d7\u5230\u8ba1\u7b56\u4f24\u5bb3"), 
    PUSH_SLAVE("PUSH_SLAVE", 23, "push@slave", "slaveInfo", "\u5974\u96b6\u7cfb\u7edf\u4fe1\u606f"), 
    PUSH_STORE("PUSH_STORE", 24, "push@store", "refresh", "\u5f3a\u5236\u5237\u65b0\u5546\u5e97"), 
    PUSH_WINDOW("PUSH_WINDOW", 25, "push@window", "window", "\u63a8\u9001\u5f39\u7a97\u6d88\u606f"), 
    PUSH_CITY_MESSAGE("PUSH_CITY_MESSAGE", 26, "push@citymessage", "message", "\u63a8\u9001\u57ce\u5e02\u6d88\u606f\u5217\u8868"), 
    PUSH_WORLD("PUSH_WORLD", 27, "push@world", "refresh", "\u5237\u65b0\u4e16\u754c"), 
    PUSH_POWER("PUSH_POWER", 28, "push@power", "power", "\u5237\u65b0\u526f\u672c"), 
    PUSH_WORLD_CNP("PUSH_WORLD_CNP", 29, "push@world", "cnpInfo", "\u4e16\u754c\u56fd\u529b\u503c"), 
    PUSH_RIGHT_NOTICE("PUSH_RIGHT_NOTICE", 30, "push@rightNotice", "rightNotice", "\u53f3\u4fa7\u6d88\u606f\u64ad\u62a5"), 
    PUSH_REDIRECT_URL("PUSH_REDIRECT_URL", 31, "push@redirectUrl", "message", "\u91cd\u65b0\u8df3\u8f6c"), 
    PUSH_WORLD_REWARD("PUSH_WORLD_REWARD", 32, "push@worldReward", "worldReward", "\u6574\u70b9\u53d1\u9001\u519b\u8d44\u4fe1\u606f"), 
    PUSH_KILL_ADD("PUSH_KILL_ADD", 33, "push@worldKillChange", "worldKill", "\u51fb\u6740\u6570\u53d8\u5316"), 
    PUSH_GENERAL_INFO("PUSH_GENERAL_INFO", 34, "push@generalInfo", "youDiChuji", "\u6b66\u5c06\u6761\u8bf1\u654c\u51fa\u51fb\u4fe1\u606f"), 
    PUSH_GENERAL_INFO2("PUSH_GENERAL_INFO2", 35, "push@generalInfo", "onQueues", "\u6b66\u5c06\u4e0a\u9635\u4fe1\u606f"), 
    PUSH_GENERAL_INFO3("PUSH_GENERAL_INFO3", 36, "push@generalInfo", "teamOrder", "\u6b66\u5c06\u88ab\u52a0\u901f\u8865\u5175"), 
    PUSH_GENERALMOVE("PUSH_GENERALMOVE", 37, "push@cities", "generalMove", "\u6b66\u5c06\u79fb\u52a8"), 
    PUSH_OFFICER_TOKEN("PUSH_OFFICER_TOKEN", 38, "push@officerToken", "tokenInfo", "\u5f53\u524d\u5b98\u5458\u4ee4\u6d88\u606f"), 
    PUSH_GOLD_ORDER_BATTLE("PUSH_GOLD_ORDER_BATTLE", 39, "push@goldOrderBattle", "goldOrderInfoBattle", "\u6218\u6597\u4e2d\u7684\u73a9\u5bb6\u662f\u5426\u53ef\u4ee5\u518d\u53d1\u5f81\u53ec\u4ee4"), 
    PUSH_NATION_TASK_STATE_CHANGE("PUSH_NATION_TASK_STATE_CHANGE", 40, "push@nationTaskStateChange", "taskMessage", "\u56fd\u5bb6\u4efb\u52a1\u6d88\u606f"), 
    PUSH_BAR_TASK_STATE_CHANGE("PUSH_BAR_TASK_STATE_CHANGE", 41, "push@barTaskStateChange", "bartaskMessage", "\u56fd\u5bb6\u4efb\u52a1\u6d88\u606f"), 
    PUSH_NATION_TASK_SIMPLE("PUSH_NATION_TASK_SIMPLE", 42, "push@nationTask", "simpleMessage", "\u56fd\u5bb6\u4efb\u52a1\u6d88\u606f"), 
    PUSH_TEAM_FULL("PUSH_TEAM_FULL", 43, "push@teamInfo", "full", "\u519b\u56e2\u5df2\u6ee1\u5458"), 
    PUSH_TEAM_CHANGE("PUSH_TEAM_CHANGE", 44, "push@teamInfo", "info", "\u519b\u56e2\u6210\u5458\u53d8\u66f4"), 
    PUSH_TEAM_REWARD("PUSH_TEAM_REWARD", 45, "push@teamInfo", "reward", "\u83b7\u5f97\u5956\u52b1"), 
    PUSH_TEAM_GENERAL_BAT("PUSH_TEAM_GENERAL_BAT", 46, "push@teamInfo", "teamBat", "\u96c6\u56e2\u519b\u51fa\u9635"), 
    PUSH_TEAM_START_BAT("PUSH_TEAM_START_BAT", 47, "push@teamInfo", "joinInfo", "\u96c6\u56e2\u519b\u51fa\u9635\u6218\u573a\u516c\u544a\u4fe1\u606f"), 
    PUSH_BARBARAIN_INVADE("PUSH_BARBARAIN_INVADE", 48, "push@barbarain_invade", "invade_info", "\u86ee\u65cf\u5165\u4fb5\u6d88\u606f"), 
    PUSH_BARBARAIN_FADONG("PUSH_BARBARAIN_FADONG", 49, "push@barbarain_fadong", "fadong_info", "\u86ee\u65cf\u53d1\u52a8\u6d88\u606f"), 
    PUSH_CITY_EVENT("PUSH_CITY_EVENT", 50, "push@cityEvent", "cityEventInfo", "\u57ce\u6c60\u4e8b\u4ef6\u6d88\u606f"), 
    PUSH_PLAYER_EVENT("PUSH_PLAYER_EVENT", 51, "push@playerEvent", "playerEventInfo", "\u73a9\u5bb6\u57ce\u6c60\u4e8b\u4ef6\u6d88\u606f"), 
    PUSH_BARBARAIN_INVADE_FOOD_ARMY("PUSH_BARBARAIN_INVADE_FOOD_ARMY", 52, "push@barbarainInvadeFoodArmy", "foodArmyInfo", "\u86ee\u65cf\u53d1\u52a8\u6d88\u606f"), 
    PUSH_GENERAL_JUBENMOVE("PUSH_GENERAL_JUBENMOVE", 53, "push@juben", "generalMove", "\u5267\u672c\u6b66\u5c06\u79fb\u52a8"), 
    PUSH_ATTMOV_JUBEN("PUSH_ATTMOV_JUBEN", 54, "push@juben", "attmov", "\u5267\u672c\u91cc\u7684\u79fb\u52a8\u53d8\u5316"), 
    PUSH_JUBEN("PUSH_JUBEN", 55, "push@juben", "refresh", "\u5237\u65b0\u5267\u672c"), 
    PUSH_JUBEN_INFO("PUSH_JUBEN_INFO", 56, "push@juben", "info", "\u5237\u65b0\u5267\u672c\u754c\u9762"), 
    PUSH_JUBEN_RES("PUSH_JUBEN_RES", 57, "push@juben", "res", "\u5267\u672c\u7ed3\u679c"), 
    PUSH_JUBEN_FLAG("PUSH_JUBEN_FLAG", 58, "push@juben", "flag", "\u63a8\u9001\u5267\u672cflag"), 
    PUSH_WHOLE_KILL("PUSH_WHOLE_KILL", 59, "push@wholeKill", "wholeKill", "\u63a8\u9001\u662f\u5426\u6709\u6574\u70b9\u6740\u654c"), 
    PUSH_JUBEN_DIALOG("PUSH_JUBEN_DIALOG", 60, "push@juben", "dialog", "\u63a8\u9001\u5267\u672cdialog"), 
    PUSH_JUBEN_STATE("PUSH_JUBEN_STATE", 61, "push@juben", "bat", "\u5237\u65b0\u5267\u672c\u6218\u6597\u72b6\u6001"), 
    PUSH_JUBEN_TIMECHANGE("PUSH_JUBEN_TIMECHANGE", 62, "push@juben", "timeChange", "\u5267\u672c\u7ed3\u675f\u65f6\u95f4\u53d1\u751f\u53d8\u5316"), 
    PUSH_JUBEN_NPCTRICK("PUSH_JUBEN_NPCTRICK", 63, "push@juben", "npcTrick", "\u4e8b\u4ef6\u4e2dnpc\u65bd\u653e\u8ba1\u7b56"), 
    PUSH_JUBEN_EVENTFINISH("PUSH_JUBEN_EVENTFINISH", 64, "push@juben", "eventFinish", "\u5267\u672c\u4e2d\u4e8b\u4ef6\u5b8c\u6210,\u7528\u4e8e\u6210\u5c31\u7684\u63a8\u9001"), 
    PUSH_JUBEN_EVENTOVER("PUSH_JUBEN_EVENTOVER", 65, "push@juben", "eventOver", "\u5267\u672c\u4e8b\u4ef6\u5b8c\u6210,\u7528\u4e8e\u57ce\u5e02\u4e0a\u5934\u50cf\u7684\u6d88\u5931"), 
    PUSH_JUBEN_GENERALADD("PUSH_JUBEN_GENERALADD", 66, "push@juben", "generalAdd", "\u5267\u672c\u589e\u52a0\u6b66\u5c06\u63a8\u9001\u4fe1\u606f"), 
    PUSH_ALL_TRICK("PUSH_ALL_TRICK", 67, "push@juben", "allTrick", "\u5168\u5c4f\u8ba1\u7b56\u5012\u8ba1\u65f6"), 
    PUSH_EVENT_DEADLINE("PUSH_EVENT_DEADLINE", 68, "push@juben", "eventDeadLine", "\u4e8b\u4ef6\u7ed3\u675f\u5012\u8ba1\u65f6"), 
    PUSH_MAN_WANG_LING("PUSH_MAN_WANG_LING", 69, "push@manWangLing", "manWangLing", "\u86ee\u738b\u4ee4\u6d88\u606f"), 
    PUSH_REPUTAION("PUSH_REPUTAION", 70, "push@reputation", "reputation", "\u63a8\u9001\u5b98\u5a01\u4fe1\u606f"), 
    PUSH_CHASING_INFO("PUSH_CHASING_INFO", 71, "push@juben", "chasingInfo", "\u8ffd\u9010\u4fe1\u606f\u53d8\u5316\u65f6\u63a8\u9001"), 
    PUSH_ZERO_OCLOCK_RESET_REFRESH("PUSH_ZERO_OCLOCK_RESET_REFRESH", 72, "push@zeroOclockReset", "refreshInfo", "\u51cc\u6668\u91cd\u7f6e\uff0c\u524d\u7aef\u5237\u65b0"), 
    PUSH_WIZARD_WORKSHOP("PUSH_WIZARD_WORKSHOP", 73, "push@wizardWorkShop", "Info", "\u672f\u58eb\u5de5\u574aICON\u63d0\u793a"), 
    PUSH_FB_GUIDE("PUSH_FB_GUIDE", 74, "push@fubenGuide", "Info", "\u526f\u672c\u5f15\u5bfc"), 
    PUSH_JUBEN_ROAD_LINKED("PUSH_JUBEN_ROAD_LINKED", 75, "push@roadLinked", "road", "\u5267\u672c\u5185\u9053\u8def\u8fde\u901a\u4e0e\u5426\u4fe1\u606f"), 
    PUSH_JUBEN_MARCHING_INFO("PUSH_JUBEN_MARCHING_INFO", 76, "push@juben", "marching", "\u5267\u672c\u91cc\u884c\u519b\u4e8b\u4ef6\u4fe1\u606f"), 
    PUSH_COURTESY_EVENT("PUSH_COURTESY_EVENT", 77, "push@courtesy_event", "courtesyEvent", "\u793c\u5c1a\u5f80\u6765"), 
    PUSH_HJ_REWARD("PUSH_HJ_REWARD", 78, "push@nationTask", "hjReward", "\u9ec4\u5dfe\u4efb\u52a1\u5360\u57ce\u5956\u52b1"), 
    PUSH_NATIONMIRACLE("PUSH_NATIONMIRACLE", 79, "push@nationTask", "miracleInfo", "\u56fd\u5bb6\u5947\u8ff9\u5efa\u9020\u4fe1\u606f"), 
    PUSH_NATIONMIRAL_WORKERINFO("PUSH_NATIONMIRAL_WORKERINFO", 80, "push@nationTask", "workerInfo", "\u56fd\u5bb6\u5947\u8ff9\u8fd0\u8f93\u961f\u8fd0\u7cae\u961f\u4fe1\u606f"), 
    PUSH_JUBEN_ROYALJADE_TRANSFER("PUSH_JUBEN_ROYALJADE_TRANSFER", 81, "push@juben", "transferJade", "\u5267\u672c\u91cc\u7389\u73ba\u7684\u8f6c\u79fb\u4fe1\u606f"), 
    PUSH_HUIZHAN_BATTLE_LETTER("PUSH_HUIZHAN_BATTLE_LETTER", 82, "push@huizhan", "hzBattleLetterIcon", "\u5ba3\u6218\u6210\u529f\u540e\u5411\u524d\u7aef\u63a8\u9001\u6218\u4e66"), 
    PUSH_HUIZHAN_GATHER("PUSH_HUIZHAN_GATHER", 83, "push@huizhan", "hzGatherIcon", "\u5ba3\u6218\u8fdb\u5165\u51c6\u5907\u9636\u6bb5\uff0c\u63a8\u9001\u53ec\u96c6\u56fe\u6807"), 
    PUSH_HUIZHAN_PK_REWARD("PUSH_HUIZHAN_PK_REWARD", 84, "push@huizhan", "hzPkReward", "\u4f1a\u6218\u5355\u6311\u5956\u52b1"), 
    PUSH_HUIZHAN_FORCE_CHANGE("PUSH_HUIZHAN_FORCE_CHANGE", 85, "push@huizhan", "hzForceChange", "\u4f1a\u6218\u53cc\u65b9\u5175\u529b\u53d8\u66f4"), 
    PUSH_HUIZHAN_TASK_INFO("PUSH_HUIZHAN_TASK_INFO", 86, "push@huizhan", "hzTaskInfo", "\u4f1a\u6218\u4efb\u52a1\u680f\u4fe1\u606f"), 
    PUSH_HUIZHAN_INFO_IN_CITY("PUSH_HUIZHAN_INFO_IN_CITY", 87, "push@huizhan", "hzInfoInCity", "\u4f1a\u6218\u5728\u4e16\u754c\u57ce\u5e02\u4e2d\u7684\u8868\u73b0\u4fe1\u606f"), 
    PUSH_HUIZHAN_ICON("PUSH_HUIZHAN_ICON", 88, "push@huizhan", "HuiZhanIcon", "\u4f1a\u6218\u56fe\u6807"), 
    PUSH_HUIZHAN_SUPPORT_TOKEN_ICON("PUSH_HUIZHAN_SUPPORT_TOKEN_ICON", 89, "push@huizhan", "hzSupportTokenIcon", "\u4f1a\u6218\u7b2c\u4e09\u56fd\u652f\u63f4\u4ee4\u56fe\u6807"), 
    PUSH_JUBEN_MENGDEINFO("PUSH_JUBEN_MENGDEINFO", 90, "push@juben", "mengdeInfo", "\u63a8\u9001\u66f9\u519b\u5730\u70b9\u53d8\u6362"), 
    PUSH_KFZB("PUSH_KFZB", 91, "push@kfzb", "report", "\u6218\u62a5\u4fe1\u606f"), 
    PUSH_JUBEN_CITY_CHANGE("PUSH_JUBEN_CITY_CHANGE", 92, "push@juben", "cityChange", "\u63a8\u9001\u57ce\u5e02\u5916\u89c2\u53d8\u5316\u4fe1\u606f"), 
    PUSH_WORLD_DRAMA("PUSH_WORLD_DRAMA", 93, "push@worlddrama", "missionComplete", "\u4e00\u8f6e\u79d1\u6280\u5bf9\u5e94\u7684\u5267\u672c\u5168\u90e8\u5b8c\u6210"), 
    PUSH_WORLD_FARM("PUSH_WORLD_FARM", 94, "push@worldfarm", "worldfarm", "\u63a8\u9001\u5c6f\u7530\u4fe1\u606f"), 
    PUSH_NATIONINDIV_TASK("PUSH_NATIONINDIV_TASK", 95, "push@indiv", "procChange", "\u63a8\u9001\u4e2a\u4eba\u4efb\u52a1\u8fdb\u5ea6\u53d8\u5316\u4fe1\u606f"), 
    PUSH_AUTO_BATTLE("PUSH_AUTO_BATTLE", 96, "push@autoBattle", "autoBattle", "\u63a8\u9001\u81ea\u52a8\u56fd\u6218\u53d8\u5316\u4fe1\u606f"), 
    PUSH_NATIONINDIV_REWARD("PUSH_NATIONINDIV_REWARD", 97, "push@indiv", "taskComplete", "\u63a8\u9001\u4e2a\u4eba\u4efb\u52a1\u5b8c\u6210\u65f6\u5956\u52b1\u76f8\u5173\u4fe1\u606f");
    
    private String command;
    private String module;
    private String intro;
    
    private PushCommand(final String s, final int n, final String command, final String module, final String intro) {
        this.command = command;
        this.module = module;
        this.intro = intro;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public String getModule() {
        return this.module;
    }
}
