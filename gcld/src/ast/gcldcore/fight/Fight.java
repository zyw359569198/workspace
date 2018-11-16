package ast.gcldcore.fight;

import java.util.*;

public class Fight
{
    static Random rg;
    static int MAX_ROUND;
    static int LEN;
    
    static {
        Fight.rg = new Random();
        Fight.MAX_ROUND = 7;
        Fight.LEN = 3;
    }
    
    public static int getOneDamage(final TroopData a, final TroopData b, final boolean isCrip) {
        if (a.hp == 0 || b.hp == 0) {
            return 0;
        }
        final int BASE_ATT = (int)(a.att * (1.0 + a.JS_SKILL_att + a.JS_SKILL_dt));
        final int BASE_DEF = (int)(b.def * (1.0 + b.JS_SKILL_def + b.JS_SKILL_dt));
        double damage = Formula.getBaseDamage(BASE_ATT, BASE_DEF);
        damage *= 0.9 + 0.2 * Fight.rg.nextDouble();
        damage += a.base_damage;
        double bloodE = Math.pow(1.0 * a.hp / b.hp, 0.25);
        if (bloodE < 0.2) {
            bloodE = 0.2;
        }
        if (bloodE > 5.0) {
            bloodE = 5.0;
        }
        damage *= bloodE;
        double t_e = 1.0 + a.terrain_effect - b.terrain_effect;
        if (t_e < 0.6) {
            t_e = 0.6;
        }
        if (t_e > 1.6) {
            t_e = 1.6;
        }
        damage *= t_e;
        double all_damage_e = 1.0 + a.all_damage_e - b.all_damage_e;
        if (all_damage_e < 0.5) {
            all_damage_e = 0.5;
        }
        if (all_damage_e > 2.0) {
            all_damage_e = 2.0;
        }
        damage *= all_damage_e;
        double world_weaken_besiege = 1.0 - a.world_weaken_besiege;
        if (world_weaken_besiege < 0.5) {
            world_weaken_besiege = 0.5;
        }
        if (world_weaken_besiege > 1.0) {
            world_weaken_besiege = 1.0;
        }
        damage *= world_weaken_besiege;
        double world_weaken_frontLine_buff = 1.0 - a.world_weaken_frontLine_buff;
        if (world_weaken_frontLine_buff < 0.5) {
            world_weaken_frontLine_buff = 0.5;
        }
        if (world_weaken_frontLine_buff > 1.0) {
            world_weaken_frontLine_buff = 1.0;
        }
        damage *= world_weaken_frontLine_buff;
        final int LeaDiff = a.Lea - b.Lea;
        double LeaCoE = 1.0 + LeaDiff * 0.005;
        if (LeaCoE > 1.35) {
            LeaCoE = 1.35;
        }
        if (LeaCoE < 0.65) {
            LeaCoE = 0.65;
        }
        damage *= LeaCoE;
        double world_legion_e = 1.0 + a.world_legion_e;
        if (world_legion_e < 1.0) {
            world_legion_e = 1.0;
        }
        if (world_legion_e > 1.15) {
            world_legion_e = 1.15;
        }
        damage *= world_legion_e;
        if (b.isMz) {
            double mz_e = 1.0 + a.world_mz_e;
            if (mz_e > 1.3) {
                mz_e = 1.3;
            }
            if (mz_e < 1.0) {
                mz_e = 1.0;
            }
            damage *= mz_e;
        }
        if (a.isFS) {
            double fs_e = 2.0 - a.world_fs_d * 0.1;
            if (fs_e < 0.5) {
                fs_e = 0.5;
            }
            if (fs_e > 2.0) {
                fs_e = 2.0;
            }
            damage *= fs_e;
        }
        if (a.isBS && a.BS_My != 0 && a.BS_Your != 0) {
            double bs_e = Math.sqrt(1.0 * a.BS_Your / a.BS_My);
            if (bs_e < 0.5) {
                bs_e = 0.5;
            }
            if (bs_e > 2.0) {
                bs_e = 2.0;
            }
            damage *= bs_e;
        }
        final double damage_append = a.ATT_B - b.DEF_B;
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
        if (damage < damage_append * 0.5) {
            damage = damage_append * 0.5;
        }
        if (isCrip) {
            damage *= 2.0;
        }
        if (damage < 1.0) {
            damage = 1.0;
        }
        if (damage > b.hp) {
            damage = b.hp;
        }
        return (int)damage;
    }
    
    public static String[] fight(final TroopData[] attacker, final TroopData[] defender) {
        if (attacker.length != Fight.LEN || defender.length != Fight.LEN) {
            return null;
        }
        int winSide = 1;
        boolean extraFight = true;
        if (attacker == null || defender == null || attacker.length != Fight.LEN || defender.length != Fight.LEN) {
            return null;
        }
        final TroopData att = (TroopData)attacker[0].clone();
        final TroopData def = (TroopData)defender[0].clone();
        String[] reports = new String[Fight.LEN];
        final int[] attState = new int[Fight.MAX_ROUND + 1];
        final int[] defState = new int[Fight.MAX_ROUND + 1];
        for (int i = 1; i <= Fight.MAX_ROUND; ++i) {
            defState[i] = (attState[i] = 0);
        }
        attState[0] = att.hp;
        defState[0] = def.hp;
        for (int row = 0; row < Fight.LEN; ++row) {
            attacker[row].start_hp = att.hp;
            defender[row].start_hp = def.hp;
        }
        int round = 1;
        final double probATTms = Fight.rg.nextDouble();
        final double probDEFms = Fight.rg.nextDouble();
        boolean isATTms = false;
        if (probATTms < att.JS_SKILL_ms) {
            isATTms = true;
        }
        boolean isDEFms = false;
        if (probDEFms < def.JS_SKILL_ms) {
            isDEFms = true;
        }
        final double probATTbj = Fight.rg.nextDouble();
        final double probDEFbj = Fight.rg.nextDouble();
        boolean isATTbj = false;
        if (probATTbj < attacker[0].JS_SKILL_bj) {
            isATTbj = true;
        }
        boolean isDEFbj = false;
        if (probDEFbj < defender[0].JS_SKILL_bj) {
            isDEFbj = true;
        }
        if (isATTms && isDEFms) {
            def.hp = 0;
            attState[round] = (att.hp = 0);
            defState[round] = def.hp;
            winSide = 3;
        }
        else if (isATTms && !isDEFms) {
            final int damage_att = getOneDamage(def, att, isDEFbj);
            def.hp = 0;
            final TroopData troopData = att;
            troopData.hp -= damage_att;
            attState[round] = att.hp;
            defState[round] = def.hp;
            winSide = 1;
        }
        else if (!isATTms && isDEFms) {
            final int damage_def = getOneDamage(att, def, isATTbj);
            final TroopData troopData2 = def;
            troopData2.hp -= damage_def;
            attState[round] = (att.hp = 0);
            defState[round] = def.hp;
            winSide = 2;
        }
        else {
            while (round < Fight.MAX_ROUND) {
                final int damage_def = getOneDamage(att, def, isATTbj);
                final int damage_att2 = getOneDamage(def, att, isDEFbj);
                final TroopData troopData3 = def;
                troopData3.hp -= damage_def;
                final TroopData troopData4 = att;
                troopData4.hp -= damage_att2;
                attState[round] = att.hp;
                defState[round] = def.hp;
                if (att.hp == 0 || def.hp == 0) {
                    extraFight = false;
                    break;
                }
                ++round;
            }
            if (extraFight) {
                if (att.hp > def.hp) {
                    final TroopData troopData5 = att;
                    troopData5.hp -= def.hp;
                    def.hp = 0;
                }
                else if (att.hp < def.hp) {
                    final TroopData troopData6 = def;
                    troopData6.hp -= att.hp;
                    att.hp = 0;
                }
                else {
                    att.hp = 0;
                    def.hp = 0;
                }
            }
            attState[round] = att.hp;
            defState[round] = def.hp;
            if (att.hp < def.hp) {
                winSide = 2;
            }
            else if (att.hp > def.hp) {
                winSide = 1;
            }
            else {
                winSide = 3;
            }
        }
        for (int row2 = 0; row2 < Fight.LEN; ++row2) {
            attacker[row2].hp = att.hp;
            defender[row2].hp = def.hp;
            attacker[row2].lost_hp = attacker[row2].start_hp - attacker[row2].hp;
            defender[row2].lost_hp = defender[row2].start_hp - defender[row2].hp;
        }
        final int[][] attReport = randomReport(attState, Fight.LEN, round);
        final int[][] defReport = randomReport(defState, Fight.LEN, round);
        reports = getStringReports(attReport, defReport, Fight.LEN, round, winSide, isATTbj, isDEFbj, isATTms, isDEFms);
        return reports;
    }
    
    private static int[][] randomReport(final int[] state, final int LEN, final int round) {
        int[][] States = getStates(state, LEN, round, true);
        int[][] report = getReports(States, LEN, round);
        if (report == null) {
            States = getStates(state, LEN, round, false);
            report = getReports(States, LEN, round);
        }
        return report;
    }
    
    private static int[][] getStates(final int[] state, final int LEN, final int round, final boolean isRandom) {
        final int[][] States = new int[LEN][round + 1];
        for (int i = 0; i < LEN; ++i) {
            States[i] = state.clone();
        }
        if (isRandom) {
            return randomStates(States, LEN, round);
        }
        return States;
    }
    
    private static int[][] randomStates(final int[][] states, final int LEN, final int round) {
        for (int row = 0; row < LEN; ++row) {
            for (int col = round - 1; col > 0; --col) {
                if (col != Fight.MAX_ROUND - 1) {
                    final double rand = 0.1 - 0.2 * Fight.rg.nextDouble();
                    final int[] array = states[row];
                    final int n = col;
                    array[n] += (int)((states[row][col] - states[row][col + 1]) * rand);
                }
            }
        }
        return states;
    }
    
    private static int[][] getReports(final int[][] states, final int LEN, final int round) {
        final int[][] reports = new int[LEN][round];
        for (int i = 0; i < LEN; ++i) {
            for (int j = 0; j < round; ++j) {
                reports[i][j] = states[i][j] - states[i][j + 1];
                if (reports[i][j] < 0) {
                    return null;
                }
            }
        }
        return reports;
    }
    
    private static String[] getStringReports(final int[][] attReport, final int[][] defReport, final int LEN, final int round, final int winSide, final boolean isATTbj, final boolean isDEFbj, final boolean isATTms, final boolean isDEFms) {
        if (attReport.length != LEN || defReport.length != LEN) {
            return null;
        }
        final String[] reports = new String[LEN];
        String att_bj_key = "";
        String def_bj_key = "";
        String att_ms_key = "";
        String def_ms_key = "";
        if (isATTbj) {
            def_bj_key = "bj:";
        }
        if (isDEFbj) {
            att_bj_key = "bj:";
        }
        if (isATTms) {
            def_ms_key = "ms:";
            def_bj_key = "";
        }
        if (isDEFms) {
            att_ms_key = "ms:";
            att_bj_key = "";
        }
        for (int row = 0; row < LEN; ++row) {
            reports[row] = "";
            for (int col = 0; col < round; ++col) {
                final int r = col + 1;
                final String[] array = reports;
                final int n = row;
                array[n] = String.valueOf(array[n]) + r + "|" + att_bj_key + att_ms_key + attReport[row][col] + "|" + def_bj_key + def_ms_key + defReport[row][col] + ";";
            }
            final String[] array2 = reports;
            final int n2 = row;
            array2[n2] = String.valueOf(array2[n2]) + "1001|" + winSide;
        }
        return reports;
    }
    
    public static void main(final String[] args) {
        final TroopData a = new TroopData();
        a.att = 4785;
        a.def = 2595;
        a.hp = 8700;
        a.criP = 0.0;
        a.criE = 0.0;
        a.max_hp = 8700;
        a.Lea = 100;
        a.Str = 100;
        a.ATT_B = 0.0;
        a.JS_SKILL_ms = 0.0;
        a.JS_SKILL_bj = 1.0;
        a.JS_SKILL_att = 0.12;
        final TroopData b = new TroopData();
        b.att = 4785;
        b.def = 2595;
        b.hp = 8700;
        b.criP = 0.0;
        b.criE = 0.0;
        b.max_hp = 8700;
        b.Str = 100;
        b.Lea = 100;
        b.DEF_B = 0.0;
        b.JS_SKILL_ms = 1.0;
        b.JS_SKILL_bj = 0.0;
        final int N = 3;
        final int TEST = 100000;
        int aWin = 0;
        int bWin = 0;
        int eQua = 0;
        boolean eReport = false;
        boolean aReport = false;
        boolean bReport = false;
        for (int i = 0; i < TEST; ++i) {
            final TroopData[] aa = new TroopData[N];
            final TroopData[] bb = new TroopData[N];
            for (int j = 0; j < N; ++j) {
                aa[j] = (TroopData)a.clone();
                bb[j] = (TroopData)b.clone();
            }
            final String[] reportT = fight(aa, bb);
            final String reportS = reportT[0];
            final String[] reportSS = reportS.split(";");
            final String reportR = reportSS[reportSS.length - 1];
            if (reportR.equals("1001|1")) {
                ++aWin;
                if (!aReport) {
                    for (int k = 0; k < N; ++k) {
                        System.out.println(reportT[k]);
                    }
                    System.out.println();
                    aReport = true;
                }
            }
            else if (reportR.equals("1001|2")) {
                ++bWin;
                if (!bReport) {
                    for (int k = 0; k < N; ++k) {
                        System.out.println(reportT[k]);
                    }
                    System.out.println();
                    bReport = true;
                }
            }
            else if (reportR.equals("1001|3")) {
                ++eQua;
                if (!eReport) {
                    for (int k = 0; k < N; ++k) {
                        System.out.println(reportT[k]);
                    }
                    System.out.println();
                    eReport = true;
                }
            }
        }
        System.out.println(String.valueOf(1.0 * aWin / TEST) + "\t" + 1.0 * bWin / TEST + "\t" + 1.0 * eQua / TEST);
    }
}
