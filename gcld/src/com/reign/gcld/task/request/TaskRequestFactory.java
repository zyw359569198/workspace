package com.reign.gcld.task.request;

import org.apache.commons.lang.*;
import com.reign.gcld.sdata.cache.*;

public class TaskRequestFactory
{
    private static final TaskRequestFactory instance;
    
    static {
        instance = new TaskRequestFactory();
    }
    
    public static TaskRequestFactory getInstance() {
        return TaskRequestFactory.instance;
    }
    
    public ITaskRequest getTaskReQuest(final String target) {
        if (StringUtils.isBlank(target)) {
            return null;
        }
        final String[] targets = target.split(";");
        if (targets.length < 1) {
            return null;
        }
        if (targets.length > 1) {
            final String[] s = targets[0].split(",");
            boolean isShowProcess = true;
            if (s.length > 1) {
                isShowProcess = s[1].trim().endsWith("1");
            }
            if (s[0].equalsIgnoreCase("or")) {
                return new TaskRequestOr(targets, isShowProcess);
            }
            return new TaskRequestAnd(targets, isShowProcess);
        }
        else {
            final String[] s = targets[0].split(",");
            final String type = s[0];
            if ("building".equalsIgnoreCase(type)) {
                return new TaskRequestBuilding(s);
            }
            if ("chose_side".equalsIgnoreCase(type)) {
                return new TaskRequestChoseSide(s);
            }
            if ("?".equalsIgnoreCase(type)) {
                return new TaskRequestDone(s);
            }
            if ("visit".equalsIgnoreCase(type)) {
                return new TaskRequestVisitArea(s);
            }
            if ("tavern_refresh".equalsIgnoreCase(type)) {
                return new TaskRequestTavernRefresh(s);
            }
            if ("officer".equalsIgnoreCase(type)) {
                return new TaskRequestOfficer(s);
            }
            if ("general".equalsIgnoreCase(type)) {
                return new TaskRequestGeneral(s);
            }
            if ("building_output".equalsIgnoreCase(type)) {
                return new TaskRequestBuildingOutput(s);
            }
            if ("get_power".equalsIgnoreCase(type)) {
                return new TaskRequestGetPower(s);
            }
            if ("battle_win".equalsIgnoreCase(type)) {
                return new TaskRequestBattleWin(s);
            }
            if ("incense".equalsIgnoreCase(type)) {
                return new TaskRequestIncense(s);
            }
            if ("store_refresh".equalsIgnoreCase(type)) {
                return new TaskRequestStoreRefresh(s);
            }
            if ("store_buy".equalsIgnoreCase(type)) {
                return new TaskRequestStoreBuy(s);
            }
            if ("wear_equip".equalsIgnoreCase(type)) {
                return new TaskRequestWearEquip(s);
            }
            if ("recruit_forces".equalsIgnoreCase(type)) {
                return new TaskRequestRecruitForces(s);
            }
            if ("update_equip".equalsIgnoreCase(type)) {
                return new TaskRequestUpdateEquip(s);
            }
            if ("halls_fight".equalsIgnoreCase(type)) {
                return new TaskRequestHallsFight(s);
            }
            if ("halls_position".equalsIgnoreCase(type)) {
                return new TaskRequestHallsPosition(s);
            }
            if ("general_lv".equalsIgnoreCase(type)) {
                return new TaskRequestGeneralLv(s);
            }
            if ("chief_lv".equalsIgnoreCase(type)) {
                return new TaskRequestChiefLv(s);
            }
            if ("tech_upgrade".equalsIgnoreCase(type)) {
                return new TaskRequestTechUpgrade(s);
            }
            if ("world_move".equalsIgnoreCase(type)) {
                return new TaskRequestMove(s);
            }
            if ("world_search".equalsIgnoreCase(type)) {
                return new TaskRequestSearch(s);
            }
            if ("world_pvp".equalsIgnoreCase(type)) {
                return new TaskRequestWorldPvp(s);
            }
            if ("att".equalsIgnoreCase(type)) {
                return new TaskRequestAtt(s);
            }
            if ("def".equalsIgnoreCase(type)) {
                return new TaskRequestDef(s);
            }
            if ("blood".equalsIgnoreCase(type)) {
                return new TaskRequestBlood(s);
            }
            if ("battle_win_times".equalsIgnoreCase(type)) {
                return new TaskRequestBattleWinTimes(s);
            }
            if ("full_blood".equalsIgnoreCase(type)) {
                return new TaskRequestFullBlood(s);
            }
            if ("world_search_exhaust".equalsIgnoreCase(type)) {
                return new TaskRequestSearchExhaust(s);
            }
            if ("equip".equalsIgnoreCase(type)) {
                return new TaskRequestEquip(s);
            }
            if ("store_buy_s".equalsIgnoreCase(type)) {
                return new TaskRequestStoreBuyS(s);
            }
            if ("event_daily".equalsIgnoreCase(type)) {
                return new TaskRequestEventDaily(s);
            }
            if ("halls_visit".equalsIgnoreCase(type)) {
                return new TaskRequestHallsVisit(s);
            }
            if ("treasure".equalsIgnoreCase(type)) {
                return new TaskRequestTreasure(s);
            }
            if ("tech".equalsIgnoreCase(type)) {
                return new TaskRequestTechLevel(s);
            }
            if ("equip_on".equalsIgnoreCase(type)) {
                return new TaskRequestEquipOn(s);
            }
            if ("market_buy".equalsIgnoreCase(type)) {
                return new TaskRequestMarketBuy(s);
            }
            if ("army_adviser".equalsIgnoreCase(type)) {
                return new TaskRequestArmyAdviser(s);
            }
            if ("officer_affair".equalsIgnoreCase(type)) {
                return new TaskRequestOfficerAffair(s);
            }
            if ("officer_harvest".equalsIgnoreCase(type)) {
                return new TaskRequestOfficerHarvest(s);
            }
            if ("check_daily_kill".equalsIgnoreCase(type)) {
                return new TaskRequestCheckDailyKill(s);
            }
            if ("world_mine_iron_own".equalsIgnoreCase(type)) {
                return new TaskRequestWorldMineIronOwn(s);
            }
            if ("check_country_list".equalsIgnoreCase(type)) {
                return new TaskRequestCheckCountryList(s);
            }
            if ("trade".equalsIgnoreCase(type)) {
                return new TaskRequestTrade(s);
            }
            if ("world_mine_jade_own".equalsIgnoreCase(type)) {
                return new TaskRequestWorldMineJadeOwn(s);
            }
            if ("treasure_visit".equalsIgnoreCase(type)) {
                return new TaskRequestTreasureVisit(s);
            }
            if ("dinner".equalsIgnoreCase(type)) {
                return new TaskRequestDinner(s);
            }
            if ("arms_weapon_on".equalsIgnoreCase(type)) {
                return new TaskRequestArmsWeaponOn(s);
            }
            if ("world_mine_iron_visit".equalsIgnoreCase(type)) {
                return new TaskRequestWorldMineIronVisit(s);
            }
            if ("black_market_buy".equalsIgnoreCase(type)) {
                return new TaskRequestBlackMarketBuy(s);
            }
            if ("fire_general".equalsIgnoreCase(type)) {
                return new TaskRequestFireGeneral(s);
            }
            if ("get_salary".equalsIgnoreCase(type)) {
                return new TaskRequestGetSalary(s);
            }
            if ("official".equalsIgnoreCase(type)) {
                return new TaskRequestOfficial(s);
            }
            if ("change_name".equalsIgnoreCase(type)) {
                return new TaskRequestChangeName(s);
            }
            if ("general_min_lv".equalsIgnoreCase(type)) {
                return new TaskRequestGeneralMinLv(s);
            }
            if ("use_incense".equalsIgnoreCase(type)) {
                return new TaskRequestUseIncense(s);
            }
            if ("sell_equip".equalsIgnoreCase(type)) {
                return new TaskRequestSellEquip(s);
            }
            if ("auto_armies".equalsIgnoreCase(type)) {
                return new TaskRequestOneAutoArmies(s);
            }
            if ("rank_battle".equalsIgnoreCase(type)) {
                return new TaskRequestOneNationalRankBattle(s);
            }
            if ("rank_win".equalsIgnoreCase(type)) {
                return new TaskRequestOneNationalRankBattleWin(s);
            }
            if ("use_incense".equalsIgnoreCase(type)) {
                return new TaskRequestUseIncense(s);
            }
            if ("sell_equip".equalsIgnoreCase(type)) {
                return new TaskRequestSellEquip(s);
            }
            if ("resource_total".equalsIgnoreCase(type)) {
                return new TaskRequestResourceTotal(s);
            }
            if ("tech_inject".equalsIgnoreCase(type)) {
                return new TaskRequestTechInject(s);
            }
            if ("tech_research".equalsIgnoreCase(type)) {
                return new TaskRequestTechResearch(s);
            }
            if ("bonus".equalsIgnoreCase(type)) {
                return new TaskRequestBonusBattle(s);
            }
            if ("collect".equalsIgnoreCase(type)) {
                final int itemId = Integer.parseInt(s[1]);
                if (TaskCache.itemMap.get(itemId) == null) {
                    throw new RuntimeException("collect task itemId is wrong");
                }
                return new TaskRequestCollect(s);
            }
            else {
                if ("world_treasure_id".equalsIgnoreCase(type)) {
                    return new TaskRequestWorldTreasure(s);
                }
                if ("world_maskNPC_win".equalsIgnoreCase(type)) {
                    if (s.length < 3) {
                        throw new RuntimeException("world_maskNPC_win task error. " + targets[0]);
                    }
                    return new TaskRequestWorldMistWin(s);
                }
                else {
                    if ("world_treasure_type".equalsIgnoreCase(type)) {
                        return new TaskRequestWorldTreasureByType(s);
                    }
                    if ("choose_general".equalsIgnoreCase(type)) {
                        return new TaskRequestChooseGeneral(s);
                    }
                    if ("tech_research_begin".equalsIgnoreCase(type)) {
                        return new TaskRequestTechResearchBegin(s);
                    }
                    if ("tech_research_done".equalsIgnoreCase(type)) {
                        return new TaskRequestTechResearchDone(s);
                    }
                    if ("use_free_cons".equalsIgnoreCase(type)) {
                        return new TaskRequestUseFreeCons(s);
                    }
                    if ("equip_skill_refresh".equalsIgnoreCase(type)) {
                        return new TaskRequestQuenching(s);
                    }
                    if ("check_arms_weapon".equalsIgnoreCase(type)) {
                        return new TaskRequestArmesWeapon(s);
                    }
                    if ("black_market_visit".equalsIgnoreCase(type)) {
                        return new TaskRequestBlackMarketVisit(s);
                    }
                    if ("kill_bandit".equalsIgnoreCase(type)) {
                        return new TaskRequestKillBandit(s);
                    }
                    if ("open_blue_print".equalsIgnoreCase(type)) {
                        return new TaskRequestOpenBluePrint(s);
                    }
                    if ("world_kill".equalsIgnoreCase(type)) {
                        return new TaskRequestKillRank(s);
                    }
                    if ("weapon_make_done".equalsIgnoreCase(type)) {
                        return new TaskRequestWeaponMakeDone(s);
                    }
                    if ("kill_kidnapper".equalsIgnoreCase(type)) {
                        return new TaskRequestKillKidnapper(s);
                    }
                    if ("times_up".equalsIgnoreCase(type)) {
                        return new TaskRequestTimesUp(s);
                    }
                    if ("recruit_general".equalsIgnoreCase(type)) {
                        return new TaskRequestRecuitGeneral(s);
                    }
                    if ("builded_limbo".equalsIgnoreCase(type)) {
                        return new TaskRequestBuildedLimbo(s);
                    }
                    return null;
                }
            }
        }
    }
}
