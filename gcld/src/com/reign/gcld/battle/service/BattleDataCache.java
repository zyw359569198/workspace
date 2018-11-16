package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import com.reign.gcld.common.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import java.util.*;
import com.reign.gcld.store.domain.*;

@Component("battleDataCache")
public class BattleDataCache implements IBattleDataCache
{
    @Autowired
    private IDataGetter dataGetter;
    private ConcurrentMap<Integer, BattleEffectCache> cacheMap;
    
    public BattleDataCache() {
        this.cacheMap = new ConcurrentHashMap<Integer, BattleEffectCache>();
    }
    
    private BattleEffectCache getBattleEffect(final int playerId) {
        if (this.cacheMap.containsKey(playerId)) {
            return this.cacheMap.get(playerId);
        }
        final BattleEffectCache bec = new BattleEffectCache(this.dataGetter, playerId);
        this.cacheMap.put(playerId, bec);
        return bec;
    }
    
    @Override
    public void refreshWeaponEffect(final int playerId) {
        BattleEffectCache bec = null;
        if (this.cacheMap.containsKey(playerId)) {
            bec = this.cacheMap.get(playerId);
            bec.refreshWeaponEffect(playerId);
        }
        else {
            bec = new BattleEffectCache(this.dataGetter, playerId);
        }
    }
    
    @Override
    public void refreshDiamondEffect(final int playerId) {
        BattleEffectCache bec = null;
        if (this.cacheMap.containsKey(playerId)) {
            bec = this.cacheMap.get(playerId);
            bec.refreshDiamondEffect(playerId);
        }
        else {
            bec = new BattleEffectCache(this.dataGetter, playerId);
        }
    }
    
    @Override
    public void refreshWeaponEffect(final int playerId, final int weaponId) {
        BattleEffectCache bec = null;
        if (this.cacheMap.containsKey(playerId)) {
            bec = this.cacheMap.get(playerId);
            bec.refreshWeaponEffect(playerId, weaponId);
        }
        else {
            bec = new BattleEffectCache(this.dataGetter, playerId);
        }
    }
    
    @Override
    public void removeEquipEffect(final int playerId, final int generalId) {
        if (this.cacheMap.containsKey(playerId)) {
            final BattleEffectCache bec = this.cacheMap.get(playerId);
            bec.removeEquipEffect(generalId);
        }
    }
    
    @Override
    public void removeTroopEffect(final int playerId, final int techId) {
    }
    
    @Override
    public void addTreasureEffect(final int playerId, final String effect) {
        if (this.cacheMap.containsKey(playerId)) {
            final BattleEffectCache bec = this.cacheMap.get(playerId);
            bec.addTreasureEffect(effect);
        }
    }
    
    @Override
    public int getAtt(final PlayerGeneralMilitary pgm) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
        return this.getAtt(pgm.getPlayerId(), pgm.getGeneralId(), troop, pgm.getLv());
    }
    
    @Override
    public int getAtt(final int playerId, final int generalId, final Troop troop, final int lv) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troop.getSerial());
        int att = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Att")).getValue();
        att += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Att")).getValue();
        final int qiangzhuangAtt = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 30);
        att += bec.getBwAttBase() + eec.att + bec.getBqAttBase() + troop.getAtt() + qiangzhuangAtt;
        att = (int)(att * bec.getBwAttCoe() * tec.techAtt);
        return att;
    }
    
    @Override
    public Map<Integer, Integer> getAttDefHp(final PlayerGeneralMilitary pgm) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
        return this.getAttDefHp(pgm.getPlayerId(), pgm.getGeneralId(), troop, pgm.getLv());
    }
    
    @Override
    public Map<Integer, Integer> getAttDefHp(final int playerId, final int generalId, final Troop troop, final int lv) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troop.getSerial());
        int att = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Att")).getValue();
        int def = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Def")).getValue();
        int hp = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Blood")).getValue();
        att += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Att")).getValue();
        def += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Def")).getValue();
        hp += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Blood")).getValue();
        final int qiangzhuangAtt = this.dataGetter.getTechEffectCache().getTechEffect(playerId, 30);
        att += bec.getBwAttBase() + eec.att + bec.getBqAttBase() + troop.getAtt() + qiangzhuangAtt;
        final int qiangzhuangDef = this.dataGetter.getTechEffectCache().getTechEffect2(playerId, 30);
        def += bec.getBwDefBase() + eec.def + bec.getBqDefBase() + troop.getDef() + qiangzhuangDef;
        att = (int)(att * bec.getBwAttCoe() * tec.techAtt);
        def = (int)(def * bec.getBwDefCoe() * tec.techDef);
        final int qiangzhuangHp = (int)this.dataGetter.getTechEffectCache().getTechEffect3(playerId, 30);
        hp = hp + eec.hp + bec.getBqHpBase() + qiangzhuangHp;
        hp = (int)(hp * tec.techHp) / 3;
        hp = hp * this.getColumNum(playerId) * 3;
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, att);
        map.put(2, def);
        map.put(3, hp);
        return map;
    }
    
    @Override
    public int getDef(final PlayerGeneralMilitary pgm) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
        return this.getDef(pgm.getPlayerId(), pgm.getGeneralId(), troop, pgm.getLv());
    }
    
    @Override
    public int getDef(final int playerId, final int generalId, final Troop troop, final int lv) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troop.getSerial());
        int def = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Def")).getValue();
        def += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Def")).getValue();
        final int qiangzhuangDef = this.dataGetter.getTechEffectCache().getTechEffect2(playerId, 30);
        def += bec.getBwDefBase() + eec.def + bec.getBqDefBase() + troop.getDef() + qiangzhuangDef;
        def = (int)(def * bec.getBwDefCoe() * tec.techDef);
        return def;
    }
    
    @Override
    public int getMaxHp(final PlayerGeneralMilitary pgm) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), pgm.getPlayerId());
        return this.getMaxHp(pgm.getPlayerId(), pgm.getGeneralId(), troop, pgm.getLv());
    }
    
    @Override
    public int getMaxHp(final int playerId, final int generalId, final Troop troop, final int lv) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troop.getSerial());
        int hp = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Blood")).getValue();
        hp += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Blood")).getValue();
        final int qiangzhuangHp = (int)this.dataGetter.getTechEffectCache().getTechEffect3(playerId, 30);
        hp = hp + eec.hp + bec.getBqHpBase() + qiangzhuangHp;
        hp = (int)(hp * tec.techHp) / 3;
        hp = hp * this.getColumNum(playerId) * 3;
        return hp;
    }
    
    @Override
    public int getColumNum(final int playerId) {
        return 2 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 4);
    }
    
    @Override
    public EquipEffectCache getEquipMax(final StoreHouse sh) {
        return BattleEffectCache.getEquipMax(this.dataGetter, sh);
    }
    
    @Override
    public int changGEquipNewMaxHp(final PlayerGeneralMilitary pgm, final int orgE, final int nowE) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        final int troopIdSerial = ((Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop())).getSerial();
        return this.changGEquipNewMaxHp(pgm.getPlayerId(), pgm.getGeneralId(), troopIdSerial, pgm.getLv(), orgE, nowE);
    }
    
    private int changGEquipNewMaxHp(final int playerId, final int generalId, final int troopIdSerial, final int lv, final int orgE, final int nowE) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troopIdSerial);
        int hp = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Blood")).getValue();
        hp += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Blood")).getValue();
        final int qiangzhuangHp = (int)this.dataGetter.getTechEffectCache().getTechEffect3(playerId, 30);
        hp = hp + eec.hp + bec.getBqHpBase() + qiangzhuangHp;
        hp = hp - orgE + nowE;
        hp = (int)(hp * tec.techHp) / 3;
        hp = hp * this.getColumNum(playerId) * 3;
        return hp;
    }
    
    @Override
    public int changCEquipNewMaxHp(final PlayerGeneralMilitary pgm, final int orgE, final int nowE) {
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        return this.changCEquipNewMaxHp(pgm.getPlayerId(), pgm.getGeneralId(), general.getTroop(), pgm.getLv(), orgE, nowE);
    }
    
    private int changCEquipNewMaxHp(final int playerId, final int generalId, final int troopIdSerial, final int lv, final int orgE, final int newE) {
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        final EquipEffectCache eec = bec.getEquipMilitaryEffect(generalId);
        final TroopSerialEffectCache tec = bec.getTroopEffect(troopIdSerial);
        int hp = (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Origin.Blood")).getValue();
        hp += (lv - 1) * (int)(Object)((C)this.dataGetter.getcCache().get((Object)"General.Grow.Blood")).getValue();
        final int qiangzhuangHp = (int)this.dataGetter.getTechEffectCache().getTechEffect3(playerId, 30);
        hp = hp + eec.hp + bec.getBqHpBase() + qiangzhuangHp;
        hp = (int)(hp * tec.techHp) / 3;
        hp = hp * this.getColumNum(playerId) * 3;
        return hp;
    }
    
    @Override
    public Map<Integer, Double> getGemAttribute(final int playerId) {
        if (playerId <= 0) {
            return null;
        }
        final BattleEffectCache bec = this.getBattleEffect(playerId);
        return bec.getGemAttributeMap();
    }
}
