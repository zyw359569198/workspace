package com.reign.kf.match.common;

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
    PUSH_POLITICS_EVENT("PUSH_POLITICS_EVENT", 13, "push@politics", "politics", "\u653f\u52a1\u7cfb\u7edf\u4e8b\u4ef6"), 
    PUSH_SEARCH_NUM("PUSH_SEARCH_NUM", 14, "push@searchNum", "search", "\u5bfb\u8bbf\u6b21\u6570\u589e\u52a0"), 
    PUSH_POWER("PUSH_POWER", 15, "push@power", "power", "\u5bfb\u8bbf\u6b21\u6570\u589e\u52a0"), 
    PUSH_OFFICER_BUILDING_APPLY("PUSH_OFFICER_BUILDING_APPLY", 16, "push@officerBuildingApply", "officerBuilding", "\u65b0\u5b98\u804c\u5efa\u7b51\u7533\u8bf7"), 
    PUSH_AUTO_POWER("PUSH_AUTO_POWER", 17, "push@autoPower", "autoPower", "\u81ea\u52a8\u526f\u672c\u6b21\u6570"), 
    PUSH_BATTLE_REWARD("PUSH_BATTLE_REWARD", 18, "push@batReward", "batReward", "\u6218\u6597\u5956\u52b1"), 
    PUSH_KFWD_MATCH("PUSH_KFWD_MATCH", 19, "push@kfwdMatch", "kfwdMatch", "\u8de8\u670d\u6b66\u6597"), 
    PUSH_KFWD_MATCH_REPORT("PUSH_KFWD_MATCH_REPORT", 20, "push@kfwdMatchReport", "kfwdMatchReport", "\u8de8\u670d\u6b66\u6597\u6218\u62a5"), 
    PUSH_PLAYERINFO("PUSH_PLAYERINFO", 21, "push@getPlayerInfo", "info", "\u73a9\u5bb6\u91cd\u65b0\u767b\u5f55"), 
    PUSH_TRICKINFO("PUSH_TRICKINFO", 22, "push@trickInfo", "trickInfo", "\u73a9\u5bb6\u53d7\u5230\u8ba1\u7b56\u4f24\u5bb3"), 
    PUSH_SLAVE("PUSH_SLAVE", 23, "push@slave", "slaveInfo", "\u5974\u96b6\u7cfb\u7edf\u4fe1\u606f"), 
    PUSH_STORE("PUSH_STORE", 24, "push@store", "refresh", "\u5f3a\u5236\u5237\u65b0\u5546\u5e97"), 
    PUSH_WINDOW("PUSH_WINDOW", 25, "push@window", "window", "\u63a8\u9001\u5f39\u7a97\u6d88\u606f"), 
    PUSH_CITY_MESSAGE("PUSH_CITY_MESSAGE", 26, "push@citymessage", "message", "\u63a8\u9001\u57ce\u5e02\u6d88\u606f\u5217\u8868"), 
    PUSH_WORLD("PUSH_WORLD", 27, "push@world", "refresh", "\u5237\u65b0\u4e16\u754c"), 
    PUSH_WORLD_CNP("PUSH_WORLD_CNP", 28, "push@world", "cnpInfo", "\u4e16\u754c\u56fd\u529b\u503c"), 
    PUSH_RIGHT_NOTICE("PUSH_RIGHT_NOTICE", 29, "push@rightNotice", "rightNotice", "\u53f3\u4fa7\u6d88\u606f\u64ad\u62a5"), 
    PUSH_REDIRECT_URL("PUSH_REDIRECT_URL", 30, "push@redirectUrl", "message", "\u91cd\u65b0\u8df3\u8f6c"), 
    PUSH_WORLD_REWARD("PUSH_WORLD_REWARD", 31, "push@worldReward", "worldReward", "\u6574\u70b9\u53d1\u9001\u519b\u8d44\u4fe1\u606f"), 
    PUSH_KILL_ADD("PUSH_KILL_ADD", 32, "push@worldKillChange", "worldKill", "\u51fb\u6740\u6570\u53d8\u5316"), 
    PUSH_GENERAL_INFO("PUSH_GENERAL_INFO", 33, "push@generalInfo", "generalInfo", "\u6b66\u5c06\u6761\u4fe1\u606f"), 
    PUSH_GENERALMOVE("PUSH_GENERALMOVE", 34, "push@cities", "generalMove", "\u6b66\u5c06\u79fb\u52a8"), 
    PUSH_BATTLE_DOKFWDBATTLE("PUSH_BATTLE_DOKFWDBATTLE", 35, "push@kfwdbattle", "dokfwdBattle", "\u53d1\u9001\u6218\u6597\u4fe1\u606f"), 
    PUSH_BATTLE_DOKFWDRTINFO("PUSH_BATTLE_DOKFWDRTINFO", 36, "push@kfwdrtInfo", "kfwdrtInfo", "\u53d1\u9001"), 
    PUSH_BATTLE_DOKFGZINFO("PUSH_BATTLE_DOKFGZINFO", 37, "push@kfgzInfo", "kfgz", "\u53d1\u9001"), 
    PUSH_BATTLE_DOKFGZBATTLE("PUSH_BATTLE_DOKFGZBATTLE", 38, "push@kfgzBattle", "kfgzbattle", "\u53d1\u9001\u6218\u6597\u4fe1\u606f"), 
    PUSH_KF_WORLD_GENERALMOVE("PUSH_KF_WORLD_GENERALMOVE", 39, "push@kfworld", "generalMove", "\u8de8\u670d\u56fd\u6218\u6b66\u5c06\u79fb\u52a8"), 
    PUSH_KF_GENERAL_HP("PUSH_KF_GENERAL_HP", 40, "push@kfgeneral", "hp", "\u8de8\u670d\u56fd\u6218\u6b66\u5c06\u5175\u529b\u53d8\u5316"), 
    PUSH_KF_GENERAL_DIRECTMOVE("PUSH_KF_GENERAL_DIRECTMOVE", 41, "push@kfgeneral", "directMove", "\u8de8\u670d\u56fd\u6218\u6b66\u5c06\u76f4\u63a5\u79fb\u52a8"), 
    PUSH_KF_WORLD_CITIES("PUSH_KF_WORLD_CITIES", 42, "push@kfworld", "cities", "\u4e16\u754c\u4fe1\u606f\u53d8\u5316"), 
    PUSH_KF_WORLD_CITYINFO("PUSH_KF_WORLD_CITYINFO", 43, "push@kfworld", "cityInfo", "\u4e16\u754c\u57ce\u6c60\u4fe1\u606f"), 
    PUSH_KF_WORLD_STGS("PUSH_KF_WORLD_STGS", 44, "push@kfworld", "stgs", "\u4e16\u754c\u6280\u80fd\u53d8\u5316"), 
    PUSH_KF_CHAT("PUSH_KF_CHAT", 45, "push@kfchat", "chatSend", "\u63a8\u9001\u804a\u5929\u4fe1\u606f"), 
    PUSH_KF_KILLARMYRANKING("PUSH_KF_KILLARMYRANKING", 46, "push@kfworld", "killRanking", "\u4e16\u754c\u6740\u4eba\u6392\u540d"), 
    PUSH_KF_OFFICETOKEN("PUSH_KF_OFFICETOKEN", 47, "push@kfworld", "officeToken", "\u5b98\u5458\u4ee4"), 
    PUSH_KF_NEWGROUPTIKCET("PUSH_KF_NEWGROUPTIKCET", 48, "push@kfworld", "groupteam", "\u96c6\u56e2\u519b"), 
    PUSH_KF_GROUPTEAMCHANGE("PUSH_KF_GROUPTEAMCHANGE", 49, "push@groupTeam", "teamchange", "\u96c6\u56e2\u519b\u53d8\u5316"), 
    PUSH_KF_CITYOCCUPYEXP("PUSH_KF_CITYOCCUPYEXP", 50, "push@gzplayer", "occupycityexp", "\u56fd\u6218\u5360\u57ce\u7ecf\u9a8c\u79d1\u6280"), 
    PUSH_KF_NOTICE("PUSH_KF_NOTICE", 51, "push@gznotice", "notice", "\u56fd\u6218\u9192\u76ee\u64ad\u62a5"), 
    PUSH_KF_GROUPTEAMSTART("PUSH_KF_GROUPTEAMSTART", 52, "push@groupTeam", "start", "\u96c6\u56e2\u519b\u5f00\u6253\u64ad\u62a5"), 
    PUSH_KF_CHOOSENNPCAI("PUSH_KF_CHOOSENNPCAI", 53, "push@npcAI", "choosen", "npcAI\u9009\u62e9"), 
    PUSH_KF_ORDERTOKEN("PUSH_KF_ORDERTOKEN", 54, "push@kfworld", "orderToken", "\u5b98\u5458\u4ee4"), 
    PUSH_BATTLE_DOKFZBBATTLE("PUSH_BATTLE_DOKFZBBATTLE", 55, "push@kfzbbattle", "dokfzbBattle", "\u53d1\u9001\u6218\u6597\u4fe1\u606f"), 
    PUSH_BATTLE_DOKFZBRTINFO("PUSH_BATTLE_DOKFZBRTINFO", 56, "push@kfzbrtInfo", "kfzbrtInfo", "\u53d1\u9001");
    
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
