package ast.gcldcore.fight;

class Formula
{
    protected static double getBaseDamage(final int attacker_att, final int defender_def) {
        double damage = attacker_att - defender_def;
        if (damage < 0.0) {
            damage = 0.0;
        }
        return damage;
    }
}
