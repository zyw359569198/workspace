package ast.gcldcore.fight;

import java.util.*;

public class Tactic
{
    private static Random rg;
    
    static {
        Tactic.rg = new Random();
    }
    
    public static String tacticAttack(final TroopData[] attacker, final TroopData[][] defenders) {
        double DAMAGE_APPEND = 1.0;
        String report = "";
        if (attacker == null || defenders == null) {
            return null;
        }
        if (attacker[0].general_quality > 3 && defenders[0][0].general_quality > 3) {
            double probSHIPO = 0.05;
            final int strDiff = attacker[0].Str - defenders[0][0].Str;
            if (strDiff < 0) {
                probSHIPO -= 0.02 * strDiff;
            }
            else {
                probSHIPO -= 0.01 * strDiff;
            }
            if (probSHIPO < 0.01) {
                probSHIPO = 0.01;
            }
            if (probSHIPO > 0.5) {
                probSHIPO = 0.5;
            }
            final double ranSHIPO = Tactic.rg.nextDouble();
            if (ranSHIPO < probSHIPO) {
                return "SP";
            }
        }
        final double probBJ = attacker[0].JS_SKILL_zfbj;
        final double probJB = defenders[0][0].JS_SKILL_zfjb;
        double JS_SKILL_MUTI = 1.0;
        final double ranProbBJ = Tactic.rg.nextDouble();
        if (ranProbBJ < probBJ) {
            JS_SKILL_MUTI *= 2.0;
            report = String.valueOf(report) + "jsBJ|";
        }
        final double ranProbJB = Tactic.rg.nextDouble();
        if (ranProbJB < probJB) {
            JS_SKILL_MUTI *= 0.5;
            report = String.valueOf(report) + "jsJB|";
        }
        int ATTACK_AREA = attacker[0].tactic_range;
        for (int i = 1; i < attacker.length; ++i) {
            if (attacker[i].tactic_range != ATTACK_AREA) {
                return null;
            }
        }
        final int DEFENSE_AREA = defenders.length;
        if (ATTACK_AREA > DEFENSE_AREA) {
            DAMAGE_APPEND = 1.0 * ATTACK_AREA / DEFENSE_AREA;
            ATTACK_AREA = DEFENSE_AREA;
        }
        for (int iRow = 0; iRow < ATTACK_AREA; ++iRow) {
            final int iColMax = defenders[iRow].length;
            if (attacker.length != iColMax) {
                return null;
            }
            final TroopData a = (TroopData)attacker[1].clone();
            final TroopData b = (TroopData)defenders[iRow][1].clone();
            final int damage = getOneTacticDamage(a, b, iRow, DAMAGE_APPEND, JS_SKILL_MUTI);
            for (int iCol = 0; iCol < iColMax; ++iCol) {
                if (attacker[iCol] != null && defenders[iRow][iCol] != null) {
                    report = String.valueOf(report) + damage;
                }
                else {
                    report = String.valueOf(report) + 0;
                }
                if (iCol < iColMax - 1) {
                    report = String.valueOf(report) + ",";
                }
            }
            if (iRow < defenders.length - 1) {
                report = String.valueOf(report) + ";";
            }
        }
        return report;
    }
    
    public static int getOneTacticDamage(final TroopData attacker, final TroopData defender, final int defenderRowNum, final double DAMAGE_APPEND, final double JS_SKILL_MUTI) {
        if (defenderRowNum >= attacker.tactic_range || attacker.hp == 0 || defender.hp == 0) {
            return 0;
        }
        double damage = Formula.getBaseDamage(attacker.att, defender.def);
        double world_weaken_frontLine_buff = 1.0 - attacker.world_weaken_frontLine_buff;
        if (world_weaken_frontLine_buff < 0.5) {
            world_weaken_frontLine_buff = 0.5;
        }
        if (world_weaken_frontLine_buff > 1.0) {
            world_weaken_frontLine_buff = 1.0;
        }
        damage *= world_weaken_frontLine_buff;
        damage += attacker.base_damage;
        damage *= attacker.tactic_damage_e;
        damage *= DAMAGE_APPEND;
        double tech_yingyong = 1.0 + 0.01 * (attacker.tech_yingyong_damage_e - defender.tech_jianren_damage_e);
        if (tech_yingyong > 2.0) {
            tech_yingyong = 2.0;
        }
        if (tech_yingyong < 0.5) {
            tech_yingyong = 0.5;
        }
        damage *= tech_yingyong;
        double all_damage_e = 1.0 + attacker.all_damage_e - defender.all_damage_e;
        if (all_damage_e < 0.5) {
            all_damage_e = 0.5;
        }
        if (all_damage_e > 2.0) {
            all_damage_e = 2.0;
        }
        damage *= all_damage_e;
        if (attacker.isCityAttacker) {
            damage *= 0.5;
        }
        if (attacker.isBS && attacker.BS_My != 0 && attacker.BS_Your != 0) {
            double bs_e = Math.sqrt(1.0 * attacker.BS_Your / attacker.BS_My);
            if (bs_e < 0.5) {
                bs_e = 0.5;
            }
            if (bs_e > 2.0) {
                bs_e = 2.0;
            }
            damage *= bs_e;
        }
        if (attacker.isYX && attacker.YX_max_Blood > 0 && attacker.YX_cur_Blood > 0) {
            double cur_blood_e = attacker.YX_cur_Blood * 1.0 / attacker.YX_max_Blood;
            if (cur_blood_e > 1.0 || cur_blood_e < 0.0) {
                cur_blood_e = 1.0;
            }
            double yx_e = 1.8 - cur_blood_e;
            if (cur_blood_e < 0.25) {
                yx_e += 0.3;
            }
            if (yx_e > 3.0) {
                yx_e = 3.0;
            }
            damage *= yx_e;
        }
        if (defender.isTD && defender.TD_defense_e > 0.0) {
            double TD_defense_e = defender.TD_defense_e;
            if (TD_defense_e > 1.0) {
                TD_defense_e = 1.0;
            }
            damage *= TD_defense_e;
        }
        if (defender.isMz && attacker.world_mz_e > 0.0) {
            double mz_e = 1.0 + attacker.world_mz_e;
            if (mz_e > 1.3) {
                mz_e = 1.3;
            }
            damage *= mz_e;
        }
        if (attacker.isFS) {
            double fs_e = 2.0 - attacker.world_fs_d * 0.1;
            if (fs_e < 0.5) {
                fs_e = 0.5;
            }
            if (fs_e > 2.0) {
                fs_e = 2.0;
            }
            damage *= fs_e;
        }
        damage *= JS_SKILL_MUTI;
        final double damage_append = attacker.TACTIC_ATT - defender.TACTIC_DEF;
        final double damage_append_temp = damage + damage_append;
        if (damage_append_temp < damage * 0.1) {
            damage *= 0.1;
        }
        else if (damage_append_temp > damage * 3.0) {
            damage *= 3.0;
        }
        else {
            damage = damage_append_temp;
        }
        if (damage < 0.5 * damage_append) {
            damage = 0.5 * damage_append;
        }
        if (damage > defender.hp) {
            damage = defender.hp;
        }
        if (damage < 0.0) {
            damage = 0.0;
        }
        return (int)damage;
    }
    
    public static String tacticAttackTest(final TroopData[] attacker, final TroopData[][] defenders, final int[] isKill) {
        double DAMAGE_APPEND = 1.0;
        if (isKill.length != 8) {
            return null;
        }
        String report = "";
        if (attacker == null || defenders == null) {
            return null;
        }
        int ATTACK_AREA = attacker[0].tactic_range;
        for (int i = 1; i < attacker.length; ++i) {
            if (attacker[i].tactic_range != ATTACK_AREA) {
                return null;
            }
        }
        final int DEFENSE_AREA = defenders.length;
        if (ATTACK_AREA > DEFENSE_AREA) {
            DAMAGE_APPEND = 1.0 * ATTACK_AREA / DEFENSE_AREA;
            ATTACK_AREA = DEFENSE_AREA;
        }
        final double probBJ = attacker[0].JS_SKILL_zfbj;
        final double probJB = defenders[0][0].JS_SKILL_zfjb;
        double JS_SKILL_MUTI = 1.0;
        final double ranProbBJ = Tactic.rg.nextDouble();
        if (ranProbBJ < probBJ) {
            JS_SKILL_MUTI *= 2.0;
            report = String.valueOf(report) + "jsBJ|";
        }
        final double ranProbJB = Tactic.rg.nextDouble();
        if (ranProbJB < probJB) {
            JS_SKILL_MUTI *= 0.5;
            report = String.valueOf(report) + "jsJB|";
        }
        for (int iRow = 0; iRow < ATTACK_AREA; ++iRow) {
            final int iColMax = defenders[iRow].length;
            if (attacker.length != iColMax) {
                return null;
            }
            final TroopData a = (TroopData)attacker[1].clone();
            final TroopData b = (TroopData)defenders[iRow][1].clone();
            int damage = getOneTacticDamage(a, b, iRow, DAMAGE_APPEND, JS_SKILL_MUTI);
            if (isKill[iRow] == 1) {
                damage = b.hp;
            }
            for (int iCol = 0; iCol < iColMax; ++iCol) {
                if (attacker[iCol] != null && defenders[iRow][iCol] != null) {
                    report = String.valueOf(report) + damage;
                }
                else {
                    report = String.valueOf(report) + 0;
                }
                if (iCol < iColMax - 1) {
                    report = String.valueOf(report) + ",";
                }
            }
            if (iRow < defenders.length - 1) {
                report = String.valueOf(report) + ";";
            }
        }
        return report;
    }
    
    public static void main(final String[] args) {
        final TroopData a = new TroopData();
        a.att = 4785;
        a.def = 2595;
        a.hp = 8760;
        a.Str = 300;
        a.criP = 0.0;
        a.tactic_id = 1;
        a.tactic_damage_e = 1.35;
        a.tactic_range = 3;
        a.tech_yingyong_damage_e = 15;
        a.isCityAttacker = false;
        a.BS_My = 10;
        a.BS_Your = 8;
        a.JS_SKILL_zfbj = 1.0;
        final TroopData b = new TroopData();
        b.Str = 300;
        b.att = 4785;
        b.def = 2595;
        b.hp = 8760;
        b.criP = 0.0;
        b.JS_SKILL_zfjb = 0.0;
        final int N = 3;
        final int M = 8;
        final TroopData[] aa = new TroopData[N];
        final TroopData[][] bb = new TroopData[M][N];
        for (int i = 0; i < N; ++i) {
            aa[i] = (TroopData)a.clone();
            for (int j = 0; j < M; ++j) {
                bb[j][i] = (TroopData)b.clone();
                if (j == 1) {
                    bb[j][i].isMz = true;
                }
            }
        }
        final String report = tacticAttack(aa, bb);
        System.out.println(report);
    }
}
