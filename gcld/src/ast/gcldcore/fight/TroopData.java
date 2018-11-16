package ast.gcldcore.fight;

public class TroopData implements Cloneable
{
    public int att;
    public int def;
    public int hp;
    public int max_hp;
    public int start_hp;
    public int lost_hp;
    public int general_quality;
    public int base_damage;
    public int troop_id;
    public boolean isMz;
    public boolean isFS;
    public int Lea;
    public int Str;
    public double criP;
    public double criE;
    public double terrain_effect;
    public double all_damage_e;
    public double world_weaken_besiege;
    public double world_weaken_frontLine_buff;
    public double world_legion_e;
    public double world_mz_e;
    public int world_fs_d;
    public double ATT_B;
    public double DEF_B;
    public double JS_SKILL_ms;
    public double JS_SKILL_bj;
    public double JS_SKILL_zfbj;
    public double JS_SKILL_zfjb;
    public double JS_SKILL_dt;
    public double JS_SKILL_def;
    public double JS_SKILL_att;
    public int tactic_id;
    public double tactic_damage_e;
    public int tactic_range;
    public int tech_yingyong_damage_e;
    public int tech_jianren_damage_e;
    public int TACTIC_ATT;
    public int TACTIC_DEF;
    public boolean isCityAttacker;
    public boolean isBS;
    public int BS_My;
    public int BS_Your;
    public boolean isYX;
    public int YX_max_Blood;
    public int YX_cur_Blood;
    public boolean isTD;
    public double TD_defense_e;
    
    public TroopData() {
        this.general_quality = 1;
        this.base_damage = 0;
        this.isMz = false;
        this.isFS = false;
        this.Lea = 30;
        this.Str = 30;
        this.criP = 0.1;
        this.criE = 1.5;
        this.terrain_effect = 0.0;
        this.all_damage_e = 0.0;
        this.world_weaken_besiege = 0.0;
        this.world_weaken_frontLine_buff = 0.0;
        this.world_legion_e = 0.0;
        this.world_mz_e = 0.0;
        this.world_fs_d = 1;
        this.ATT_B = 0.0;
        this.DEF_B = 0.0;
        this.JS_SKILL_ms = 0.0;
        this.JS_SKILL_bj = 0.0;
        this.JS_SKILL_zfbj = 0.0;
        this.JS_SKILL_zfjb = 0.0;
        this.JS_SKILL_dt = 0.0;
        this.JS_SKILL_def = 0.0;
        this.JS_SKILL_att = 0.0;
        this.tactic_id = 0;
        this.tactic_damage_e = 0.0;
        this.tactic_range = 0;
        this.tech_yingyong_damage_e = 0;
        this.tech_jianren_damage_e = 0;
        this.TACTIC_ATT = 0;
        this.TACTIC_DEF = 0;
        this.isCityAttacker = false;
        this.isBS = false;
        this.BS_My = 0;
        this.BS_Your = 0;
        this.isYX = false;
        this.YX_max_Blood = 0;
        this.YX_cur_Blood = 0;
        this.isTD = false;
        this.TD_defense_e = 0.0;
    }
    
    @Override
	public Object clone() {
        TroopData g = null;
        try {
            g = (TroopData)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return g;
    }
}
