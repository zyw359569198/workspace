package com.reign.gcld.gm.common;

import java.util.*;

public enum Command
{
    HELP("HELP", 0, "\u5e2e\u52a9", "0:#\u5e2e\u52a9  -->\u7528\u4e8e\u5217\u51fa\u5e2e\u52a9\u4fe1\u606f"), 
    SDATA("SDATA", 1, "reload", "1:#reload -->\u91cd\u65b0\u8f7d\u5165\u9759\u6001\u5e93"), 
    COPPER("COPPER", 2, "\u94f6\u5e01", "2:#\u94f6\u5e01 + 100000 -->\u94f6\u5e01+100000"), 
    GOLD("GOLD", 3, "\u91d1\u5e01", "3:#\u91d1\u5e01 + 100000 -->\u91d1\u5e01+100000"), 
    WOOD("WOOD", 4, "\u6728\u6750", "4:#\u6728\u6750 + 100000 -->\u6728\u6750+100000"), 
    FOOD("FOOD", 5, "\u7cae\u98df", "5:#\u7cae\u98df + 100000 -->\u7cae\u98df+100000"), 
    IRON("IRON", 6, "\u9554\u94c1", "6:#\u9554\u94c1 + 100000 -->\u9554\u94c1+100000"), 
    TICKET("TICKET", 7, "\u70b9\u5238", "6:#\u70b9\u5238 + 100000 -->\u70b9\u5238+100000"), 
    EXPLOIT("EXPLOIT", 8, "\u519b\u529f", "7:#\u519b\u529f + 100000 -->\u519b\u529f+100000"), 
    ARMY("ARMY", 9, "\u5175", "8:#\u5175 + 100000 -->\u5175+100000"), 
    LEVEL("LEVEL", 10, "\u7b49\u7ea7", "9:#\u7b49\u7ea7 = 20  -->\u4e3b\u5c06\u7b49\u7ea7\u4e3a20"), 
    BATTLE("BATTLE", 11, "\u6218\u6597", "10:#\u6218\u6597 1001 1002  -->npc armies1 armies2\u6218\u6597"), 
    BATTLE_LEAVE("BATTLE_LEAVE", 12, "\u64a4\u9000", "11:#\u64a4\u9000 1battle10011002 1001 -->\u67d0\u652f\u90e8\u961f\u9000\u51fa [\u6218\u573a1battle10011002] [\u90e8\u961fID]"), 
    BATTLE_END("BATTLE_END", 13, "\u7ed3\u675f", "12:#\u7ed3\u675f 1battle10011002  -->\u67d0\u573a\u6218\u6597\u7ed3\u675f"), 
    BATTLE_JOIN("BATTLE_JOIN", 14, "\u589e\u63f4", "13:#\u589e\u63f4 1battle10011002 1002 1  -->\u52a0\u90e8\u961f  [\u6218\u573a1battle10011002] [\u90e8\u961fID] [\u653b\u65b91 \u6216 \u5b88\u65b90]"), 
    CREATE_BUILDING("CREATE_BUILDING", 15, "building", "14:#building -->\u521b\u5efa\u6240\u6709\u5efa\u7b51"), 
    CONSUME_LV("CONSUME_LV", 16, "consume", "15:#consume = 2 -->\u8bbe\u7f6e\u6d88\u8d39\u7b49\u7ea7"), 
    GET("GET", 17, "get", "16:#get -->\u67e5\u770b\u73b0\u5728\u6d88\u8d39\u7b49\u7ea7"), 
    KILL("KILL", 18, "kill", "17:#kill 1 2 -->\u901a\u8fc7\u7b2c1\u4e2a\u526f\u672c\u7684\u7b2c\u4e8c\u4e2aNPC"), 
    FUNCTION("FUNCTION", 19, "function", "18:#function -->\u5f00\u542f\u89d2\u8272\u6240\u6709\u529f\u80fd"), 
    GENERAL("GENERAL", 20, "\u6b66\u5c06", "19:#\u6b66\u5c06 generalId cityId -->\u589e\u52a0\u4e00\u4e2a\u6b66\u5c06\u7f6e\u4e8e\u67d0\u4e2a\u57ce\u5e02"), 
    GENERALLV("GENERALLV", 21, "\u6b66\u5c06\u7b49\u7ea7", "20:#\u6b66\u5c06\u7b49\u7ea7 = 20 --> \u6240\u6709\u6b66\u5c06\u7b49\u7ea7\u8bbe\u7f6e\u4e3a20 "), 
    MOVE("MOVE", 22, "move", "21:#move \u5362\u6c5f -->\u5c06\u73a9\u5bb6\u6240\u6709\u6b66\u5c06\uff0c\u79fb\u5230\u6307\u5b9a\u57ce\u5e02\uff0c\u8be5\u57ce\u5e02\u7684\u6240\u6709\u6b66\u5c06\u79fb\u56de\u81ea\u5df1\u90fd\u57ce"), 
    COPY("COPY", 23, "copy", "22:#copy 2 -->COPY\u73a9\u5bb6\u5f53\u524d\u6570\u636e\uff0c\u4ea7\u51fa\u53e62\u4e2a\u89d2\u8272"), 
    TASK("TASK", 24, "task", "23:#task 2 -->\u4ece\u5f53\u524d\u4efb\u52a1\u5f00\u59cb\u5f80\u4e0b\u6267\u884c,\u6267\u884c\u5230\u53c2\u6570\u7684\u4efb\u52a1ID"), 
    PAY("PAY", 25, "pay", "24:#pay \u8ba2\u5355\u7f16\u53f7 1000 -->\u5145\u503c1000\u91d1\u5e01"), 
    CIVILLV("CIVILLV", 26, "\u6587\u5b98\u7b49\u7ea7", "25:#\u6587\u5b98\u7b49\u7ea7 = 20 -->\u6240\u6709\u6587\u5b98\u7b49\u7ea7\u8bbe\u7f6e\u4e3a20"), 
    OPENMIST("OPENMIST", 27, "openmist", "26:#openmist -->\u6253\u5f00\u4e16\u754c\u7684\u6240\u6709\u8ff7\u96fe"), 
    EXP("EXP", 28, "\u7ecf\u9a8c", "27:#\u7ecf\u9a8c  = 1000 --> \u8bbe\u7f6e\u8be5\u89d2\u8272\u7ecf\u9a8c\u503c\u4e3a1000"), 
    STORE("STORE", 29, "store", "28:#store  = 120 --> \u8bbe\u7f6e\u8be5\u89d2\u8272\u6700\u5927\u4ed3\u5e93\u6570\u91cf\u4e3a120"), 
    RESOURCE("RESOURCE", 30, "\u8d44\u6e90", "29:#\u8d44\u6e90  = 1000 --> \u8bbe\u7f6e\u8be5\u89d2\u8272[\u94f6\u5e01\uff0c\u6728\u6750\uff0c\u7cae\u98df\uff0c\u9554\u94c1]\u6570\u91cf\u90fd\u4e3a1000"), 
    SETTASK("SETTASK", 31, "settask", "30:#settask = 30 --> \u8bbe\u7f6e\u8be5\u89d2\u8272\u5f53\u524d\u4efb\u52a1id\u4e3a30"), 
    CIVIL("CIVIL", 32, "\u6587\u5b98", "31:#\u6587\u5b98  5 --> \u83b7\u53d6generalId\u4e3a 5 \u7684\u6587\u5b98"), 
    TOKEN("TOKEN", 33, "token", "32:#token = 100 --> \u8bbe\u7f6e\u52df\u5175\u4ee4\u6570\u91cf\u4e3a100"), 
    SLAVE("SLAVE", 34, "slave", "33:#slave \u5c0f\u7389 --> \u628a\u5c0f\u7389\u6293\u4e3a\u5974\u96b6"), 
    RESETSLAVE("RESETSLAVE", 35, "resetslave", "34:#resetslave  --> \u6e05\u7a7a\u5974\u96b6\u7cfb\u7edf"), 
    STOPWORK("STOPWORK", 36, "stopwork", "35:#stopwork  --> \u505c\u6b62\u6240\u6709\u5de5\u4f5c\u961f"), 
    STOPAUTO("STOPAUTO", 37, "stopauto", "36:#stopauto  --> \u505c\u6b62\u81ea\u52a8\u5efa\u7b51\u5347\u7ea7"), 
    TRICKCHAT("TRICKCHAT", 38, "trickchat", "37:#trickchat \u9677\u9631--> \u63a8\u9001\u91ca\u653e\u8ba1\u7b56\u804a\u5929\u4fe1\u606f"), 
    WHCCITYCHAT("WHCCITYCHAT", 39, "whccitychat", "38:#whccitychat 2  \u6d1b\u9633--> \u63a8\u9001\u653b\u5360\u8700\u57ce\u6c60\u56fd\u5bb6\u804a\u5929\u4fe1\u606f"), 
    LCCITYCHAT("LCCITYCHAT", 40, "lccitychat", "39:#lccitychat 2  \u6d1b\u9633--> \u63a8\u9001\u6d1b\u9633\u88ab\u8700\u56fd\u5360\u9886\u56fd\u5bb6\u804a\u5929\u4fe1\u606f"), 
    GLPLACECHAT("GLPLACECHAT", 41, "glplacechat", "40:#glplacechat \u5415\u5e03  \u6d1b\u9633 2--> \u63a8\u9001\u5415\u5e03\u9a7b\u5b88\u7684\u57ce\u6c60\u6d1b\u9633\u88ab\u8700\u56fd\u5360\u9886\u804a\u5929\u4fe1\u606f"), 
    GWPLACECHAT("GWPLACECHAT", 42, "gwplacechat", "41:#gwplacechat \u5415\u5e03  \u6d1b\u9633 2--> \u63a8\u9001\u5415\u5e03\u9a7b\u5b88\u7684\u57ce\u6c60\u6d1b\u9633\u88ab\u8700\u56fd\u653b\u51fb,\u5415\u5e03\u6210\u529f\u9632\u5fa1\u804a\u5929\u4fe1\u606f"), 
    RANKCHAT("RANKCHAT", 43, "rankchat", "42:#rankchat 2--> \u63a8\u9001\u6392\u4f4d\u8d5b\u88ab\u6253\u8d25\u7684\u804a\u5929\u4fe1\u606f,\u53bb\u6253\u7b2c\u4e8c\u540d"), 
    WINNPC("WINNPC", 44, "winnpc", "43:#winnpc \u534e\u96c4--> \u63a8\u9001\u6253\u8d25NPC\u534e\u96c4\u5e7f\u64ad\u4fe1\u606f"), 
    PASSBONUS("PASSBONUS", 45, "passbonus", "44:#passbonus \u5415\u5e03\u519b\u56e2--> \u63a8\u9001\u901a\u5173\u4e00\u6b21\u989d\u5916\u526f\u672c\u7684\u5e7f\u64ad\u4fe1\u606f"), 
    CNEUTRALPLACE("CNEUTRALPLACE", 46, "cneutralplace", "45:#cneutralplace \u6210\u90fd--> \u63a8\u9001\u5360\u9886\u4e00\u4e2a\u4e2d\u7acb\u5730\u70b9\u7684\u5e7f\u64ad\u4fe1\u606f"), 
    WINHCCOMONPLACE("WINHCCOMONPLACE", 47, "winhccomonplace", "46:#winhccomonplace 1  \u6d1b\u9633\u897f\u6797--> \u63a8\u9001\u5360\u9886\u9b4f\u56fd\u4e00\u4e2a\u666e\u901a\u5730\u70b9\u7684\u5e7f\u64ad\u4fe1\u606f"), 
    LCOMMONPLACE("LCOMMONPLACE", 48, "lcommonplace", "47:#lcommonplace 1  \u6d1b\u9633\u897f\u6797--> \u63a8\u9001\u5360\u4e00\u4e2a\u666e\u901a\u5730\u70b9\u88ab\u9b4f\u56fd\u5360\u9886\u7684\u5e7f\u64ad\u4fe1\u606f"), 
    PLACECH("PLACECH", 49, "placech", "48:#placech 1  \u6d1b\u9633\u897f\u6797--> \u63a8\u9001\u9b4f\u56fd\u5360\u9886\u4e00\u4e2a\u57ce\u6c60\u7684\u5e7f\u64ad\u4fe1\u606f"), 
    REOPENBONUS("REOPENBONUS", 50, "reopenbonus", "49:#reopenbonus 1  \u5415\u5e03\u519b\u56e2--> \u63a8\u9001\u89d2\u8272\u91cd\u65b0\u5f00\u542f\u4e00\u4e2a\u989d\u5916\u526f\u672c\u7684\u5168\u56fd\u5e7f\u64ad\u4fe1\u606f"), 
    CNP("CNP", 51, "cnp", "50:#cnp 1  --> \u83b7\u53d6\u9b4f\u56fd\u5f53\u524d\u56fd\u529b\u503c(\u6ca1\u6709\u53c2\u6570\u65f6\u9ed8\u8ba4\u4e3a\u672c\u56fd\u56fd\u529b\u503c)"), 
    OFFICIALO("OFFICIALO", 52, "officialo", "51:#officialo   --> \u5b98\u804c\u4ea7\u51fa"), 
    EXETIMES("EXETIMES", 53, "\u6267\u884c\u6b21\u6570", "52:#\u6267\u884c\u6b21\u6570 = 1000 --> \u8f93\u5165\u6267\u884c\u6b21\u6570 1000"), 
    GTREASURE("GTREASURE", 54, "gtreasure", "53:#gtreasure --> \u83b7\u53d6\u4e00\u4e2a\u5fa1\u5b9d"), 
    BASE("BASE", 55, "base", "54:#base 1--> \u83b7\u53d6\u94f6\u5e01\u533a\u8d44\u6e90\u4ea7\u51fa\u57fa\u672c\u503c"), 
    CHECKFUNCTIONID("CHECKFUNCTIONID", 56, "checkfunctionid", "55:#checkfunctionid = 2-->\u68c0\u67e5\u6539\u89d2\u8272\u7b2c\u4e8c\u4f4d\u7684functionId\u662f\u5426\u5f00\u542f"), 
    SETFUNCTIONID("SETFUNCTIONID", 57, "setfunctionid", "56:#setfunctionid 5 0-->\u8be5\u89d2\u8272\u7b2c\u4e94\u4f4d\u7684functionId\u88ab\u5173\u95ed\uff080\u8868\u793a\u5173\u95ed\uff0c1\u8868\u793a\u6253\u5f00\uff0c\u5176\u4f59\u4e0d\u5408\u6cd5\uff09"), 
    OFFICERS("OFFICERS", 58, "officers", "57:#officers 1-->\u83b7\u53d6\u94f6\u5e01\u7684\u5b98\u804c\u4ea7\u51fa\u503c "), 
    TECH("TECH", 59, "tech", "58:#tech 1-->\u83b7\u53d6\u94f6\u5e01\u7684\u79d1\u6280\u4ea7\u51fa\u503c "), 
    ADDITIONS("ADDITIONS", 60, "additions", "59#additions 1-->\u83b7\u53d6\u94f6\u5e01\u7684\u9644\u52a0\u4ea7\u503c"), 
    TECHEFFECT("TECHEFFECT", 61, "techeffect", "60#techeffect 8 1-->\u83b7\u53d6\u89d2\u8272\u79d1\u6280key\u4e3a8\u7684\u7b2c1\u5217\u6548\u679c\u503c"), 
    ADDTECH("ADDTECH", 62, "addtech", "61#addtech 201-->\u4e3a\u8be5\u89d2\u8272\u6dfb\u52a0tech_id\u4e3a201\u7684tech\u8bb0\u5f55\uff0c\u5982\u679c\u5df2\u7ecf\u5b58\u5728\u5219\u8fd4\u56de"), 
    ADDBLUEPRINT("ADDBLUEPRINT", 63, "addblueprint", "62#addblueprint 1--> \u83b7\u53d61\u53f7\u56fe\u7eb8"), 
    KILLBANDIT("KILLBANDIT", 64, "killbandit", "63#killbandit 1--> \u51fb\u67401\u53f7\u571f\u532a"), 
    CITYID("CITYID", 65, "cityid", "64#cityid \u6d1b\u9633--> \u83b7\u53d6\u57ce\u5e02\u6d1b\u9633\u7684cityId"), 
    TECHALL("TECHALL", 66, "techall", "65#techall--> \u8ba9\u73b0\u6709\u79d1\u6280\u5168\u90e8\u751f\u6548"), 
    UNDEFEATABLE("UNDEFEATABLE", 67, "callMeZXZ", "66#callMeZXZ [0|1]--> \u8ba9\u5c06\u519b\u60a8\u77ac\u95f4\u53d8\u5f97\u5982\u540c\u653b\u57ce\u4e2d\u9ad8\u5bcc\u5e05\u7684\u5f20\u5174\u8d5e\u4e00\u6837\u9ad8\u5927\u5a01\u731b(0\u6539\u7edf\u52c7 1\u4e0d\u6539\u7edf\u52c7)"), 
    ID("ID", 68, "id", "67#id --> \u83b7\u53d6\u89d2\u8272id"), 
    ZD("ZD", 69, "zd", "68#zd  10000--> \u9886\u53d610000\u6b21\u6574\u70b9\u5728\u7ebf\u5956\u52b1\uff0c\u67e5\u770b\u6982\u7387\u5206\u5e03"), 
    ADDRANKERNUMERS("ADDRANKERNUMERS", 70, "addRankerNumber", "69#addRankerNumber 1 10-->\u589e\u52a0\u5355\u6311\u6216\u8005\u5360\u57ce\u6570 1 \u5355\u6311 2 \u5360\u57ce "), 
    SHOCITYEVENT("SHOCITYEVENT", 71, "showCE", "70#showCE --> \u663e\u793a\u57ce\u6c60\u4e8b\u4ef6"), 
    SETGEM("SETGEM", 72, "setgem", "71#setgem 1 100 --> \u628a1\u661f\u7ea7\u5b9d\u77f3\u7684\u6570\u91cf\u8bbe\u7f6e\u4e3a100"), 
    LIMBOPIC("LIMBOPIC", 73, "limbopic", "72#limbopic 5  1--> \u83b7\u53d65\u5f201\u7ea7\u7262\u623f\u56fe\u7eb8"), 
    SLAVE2("SLAVE2", 74, "slave2", "73#slave2 \u89d2\u8272\u540d \u9a6c\u8d85 1 --> \u6293\u83b7\u89d2\u8272\u540d\u7684\u5f20\u8fbd\u771f\u8eab[0\u8868\u793a\u5e7b\u5f71]"), 
    GET_SUIT("GET_SUIT", 75, "getsuitpaper", "74#getsuitpaper \u56fe\u7eb8id --> \u83b7\u53d6\u56fe\u7eb8"), 
    SET_CITY("SET_CITY", 76, "setcity", "75#setcity 5 --> \u8bbe\u7f6e\u5176\u4e2d1\u4e2a\u6b66\u5c06\u5360\u9886\u57ce\u5e02\u7684\u6570\u91cf\u4e3a5"), 
    SET_DRAGON("SET_DRAGON", 77, "setdragon", "76#setdragon 10 --> \u8bbe\u7f6e\u7cbd\u5b50\u7684\u6570\u91cf\u4e3a5"), 
    ADD_FORCE_EXP("ADD_FORCE_EXP", 78, "addForceExp", "76#addForceExp 10 --> \u56fd\u5bb6\u7ecf\u9a8c\u52a05"), 
    FIRE_MANWANGLING("FIRE_MANWANGLING", 79, "fireManWangLing", "77#fireManWangLing forceId type--> \u53d1\u86ee\u738b\u4ee4,forceId\u53c2\u6570\u4e0d\u80fd\u662f\u672c\u56fd\u52bf\u529b,type:1-\u52a9\u86ee\u4f10\u654c;2-\u56fd\u5bb6\u8bd5\u70bc"), 
    GET_FOURSTAR_EQUIP("GET_FOURSTAR_EQUIP", 80, "getniubiequip", "78#getniubiequip type skillType num-->\u83b7\u5f97\u6ee1\u661f\u88c5\u5907,type\u662f\u88c5\u5907\u7684type 123456\u5206\u522b\u4ee3\u8868\u6b66\u5668\u9a6c\u554a\u4ec0\u4e48\u7684 skillType\u4ee3\u8868\u6280\u80fd\u7684\u7c7b\u578b num\u6570\u91cf"), 
    ADD_OFFICER_TOKEN("ADD_OFFICER_TOKEN", 81, "setofficertoken", "79#setofficertoken num --> \u8bbe\u7f6e\u81ea\u5df1\u7684\u5b98\u5458\u4ee4\u7684\u4e2a\u6570,\u9700\u8981\u81ea\u5df1\u662f\u5bf9\u5e94\u5b98\u5458"), 
    DEFAULTPAY("DEFAULTPAY", 82, "defaultpay", "80#defaultpay --> \u83b7\u53d6\u9ed8\u8ba4\u5145\u503c\u89d2\u8272"), 
    ADD_MOONCAKE("ADD_MOONCAKE", 83, "addmooncake", "81#addmooncake 100 --> \u589e\u52a0100\u4e2a\u6708\u997c"), 
    ADD_BMW("ADD_BMW", 84, "addbmw", "82#addbmw 100 --> \u589e\u52a0100 \u5b9d\u9a6c"), 
    ADD_XO("ADD_XO", 85, "addxo", "83#addxo 100 --> \u589e\u52a0100 \u7f8e\u9152"), 
    ADD_PICASSO("ADD_PICASSO", 86, "addpicasso", "84#addpicasso 100 --> \u589e\u52a0100 \u4e66\u753b"), 
    MS("MS", 87, "ms", "85#ms 100 --> \u589e\u52a0100 \u540d\u58f0"), 
    ENTER_WORLD_DRAMA("ENTER_WORLD_DRAMA", 88, "enterworlddrama", "86#enterworlddrama 10001 1 --> \u8fdb\u5165\u4e16\u754c\u5267\u672c \u5267\u672cid \u5267\u672c\u96be\u5ea6"), 
    IRON_EFFECT("IRON_EFFECT", 89, "ironeffect", "87#ironeffect --> \u8ba9\u796d\u7940\u6b21\u6570\u8fbe\u5230\u6ee1,\u5e76\u4e14\u53ef\u4ee5\u9886\u53d6\u6240\u6709\u4ee4"), 
    WEAPON_LV("WEAPON_LV", 90, "\u5175\u5668", "88#\u5175\u5668 \u5175\u5668ID 90 -->"), 
    CALL_ME_HUANGXILING("CALL_ME_HUANGXILING", 91, "callmehxl", "89#callmehxl iftaozhuang ifbingqi ifbaoshi --> \u51e0\u4e2aif 1 \u8868\u793a\u8981 0\u8868\u793a\u4e0d\u8981 "), 
    XILIAN("XILIAN", 92, "xilian", "90#xilian 100 --> \u6d17\u70bc100\u6b21 "), 
    CHANGE_FORCELV("CHANGE_FORCELV", 93, "changeforcelv", "#changeforcelv forceid forcelv exp -->forceid 0\u8868\u793a\u5168\u4f53"), 
    CHANGE_NATION_TASK("CHANGE_NATION_TASK", 94, "changenationtask", "#changenationtask type -->type \u4efb\u52a1\u7c7b\u578b(\u4e0b\u4e00\u573a\u4efb\u52a1\u6216\u8981\u5207\u6362\u6210\u7684\u4efb\u52a1type)"), 
    CHANGE_INDIV_TASK("CHANGE_INDIV_TASK", 95, "changeindivtask", "#changeindivtask type1 level type2 level2 type3 level3  \u672c\u56fd\u4efb\u52a1\u7c7b\u578b(level+1000 \u8868\u793a\u53ef\u4ee5\u5347\u7ea7  \u6ce8\u610f \u7528\u8fd9\u4e2a\u6307\u4ee4\u7684\u65f6\u5019\u6700\u597d\u4e0d\u8981\u505a\u4e0e\u4e2a\u4eba\u4efb\u52a1\u76f8\u5173\u7684\u64cd\u4f5c!!!)");
    
    private String value;
    private String intro;
    private static Map<String, Command> CMD_MAP;
    
    static {
        Command.CMD_MAP = new HashMap<String, Command>();
        final Command[] cmds = values();
        Command[] array;
        for (int length = (array = cmds).length, i = 0; i < length; ++i) {
            final Command cmd = array[i];
            Command.CMD_MAP.put(cmd.getValue(), cmd);
        }
    }
    
    private Command(final String s, final int n, final String value, final String intro) {
        this.value = value;
        this.intro = intro;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public static Command getCommand(final String key) {
        return Command.CMD_MAP.get(key);
    }
}
