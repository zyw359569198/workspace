package com.reign.gcld.battle.common;

import com.reign.gcld.common.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.treasure.domain.*;
import java.util.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.store.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.player.domain.*;

public class BattleEffectCache
{
    private static final Logger log;
    private IDataGetter dataGetter;
    private int playerId;
    private Map<Integer, EquipEffectCache> equipMap;
    private int bwAttBase;
    private int bwDefBase;
    private int bqAttBase;
    private int bqDefBase;
    private int bqHpBase;
    private int troopAttBase;
    private int trooDefBase;
    private Map<Integer, TroopSerialEffectCache> troopMap;
    private float bwAttCoe;
    private float bwDefCoe;
    private Map<Integer, Double> gemAttributeMap;
    
    static {
        log = CommonLog.getLog(BattleEffectCache.class);
    }
    
    public Map<Integer, Double> getGemAttributeMap() {
        return this.gemAttributeMap;
    }
    
    public BattleEffectCache(final IDataGetter dataGetter, final int playerId) {
        this.equipMap = new HashMap<Integer, EquipEffectCache>();
        this.bwAttBase = 0;
        this.bwDefBase = 0;
        this.bqAttBase = 0;
        this.bqDefBase = 0;
        this.bqHpBase = 0;
        this.troopAttBase = 0;
        this.trooDefBase = 0;
        this.troopMap = new HashMap<Integer, TroopSerialEffectCache>();
        this.bwAttCoe = 1.0f;
        this.bwDefCoe = 1.0f;
        this.gemAttributeMap = new HashMap<Integer, Double>(10);
        this.dataGetter = dataGetter;
        this.playerId = playerId;
        for (int i = 1; i <= 10; ++i) {
            this.gemAttributeMap.put(i, 0.0);
        }
        final List<PlayerTreasure> tList = dataGetter.getPlayerTreasureDao().getPlayerTreasures(this.playerId);
        for (final PlayerTreasure pt : tList) {
            final Treasure tc = (Treasure)dataGetter.getTreasureCache().get((Object)pt.getTreasureId());
            this.addTreasureEffect(tc.getEffect());
        }
        this.refreshWeaponEffect(this.playerId);
        this.refreshDiamondEffect(this.playerId);
    }
    
    public void addTreasureEffect(final String effect) {
        final String[] effs = effect.split(";");
        String[] array;
        for (int length = (array = effs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            final String[] s = str.split("=");
            if (s[0].equalsIgnoreCase("ATT")) {
                this.addBwAttCoe(Float.valueOf(s[1]));
            }
            else if (s[0].equalsIgnoreCase("DEF")) {
                this.addBwAttBase(Float.valueOf(s[1]));
            }
            else if (s[0].equalsIgnoreCase("ATT_BASE")) {
                this.addBwAttBase(Float.valueOf(s[1]));
            }
            else if (s[0].equalsIgnoreCase("DEF_BASE")) {
                this.addBwDefBase(Float.valueOf(s[1]));
            }
        }
    }
    
    public void addBwAttCoe(final float bwAttCoe) {
        this.bwAttCoe *= bwAttCoe + 1.0f;
    }
    
    public void addBwDefCoe(final float bwDefCoe) {
        this.bwDefCoe *= bwDefCoe + 1.0f;
    }
    
    public void addBwAttBase(final float bwAttBase) {
        this.bwAttBase += (int)bwAttBase;
    }
    
    public void addBwDefBase(final float bwDefBase) {
        this.bwDefBase += (int)bwDefBase;
    }
    
    public void refreshWeaponEffect(final int playerId) {
        this.bqAttBase = 0;
        this.bqDefBase = 0;
        this.bqHpBase = 0;
        final List<PlayerWeapon> pwList = this.dataGetter.getPlayerWeaponDao().getPlayerWeapons(playerId);
        int bsAtt = 0;
        int bsDef = 0;
        int bsHp = 0;
        for (final PlayerWeapon pw : pwList) {
            if (pw.getLv() == 0) {
                continue;
            }
            final ArmsWeapon awc = (ArmsWeapon)this.dataGetter.getArmsWeaponCache().get((Object)pw.getWeaponId());
            final AttDefHp adh = this.getAttDefHpforGem(pw.getGemId());
            final int add = awc.getBaseAttribute() + awc.getStrengthen() * (pw.getLv() - 1);
            if (awc.getType() == 1) {
                this.bqAttBase += add;
                bsAtt += adh.att;
            }
            else if (awc.getType() == 2) {
                this.bqDefBase += add;
                bsDef += adh.def;
            }
            else {
                if (awc.getType() != 3) {
                    continue;
                }
                this.bqHpBase += add;
                bsHp += adh.hp;
            }
        }
        this.bqAttBase *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 24) / 100.0);
        this.bqDefBase *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 25) / 100.0);
        this.bqHpBase *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 26) / 100.0);
        this.bqAttBase += bsAtt;
        this.bqDefBase += bsDef;
        this.bqHpBase += bsHp;
    }
    
    private AttDefHp getAttDefHpforGem(final String gemsId) {
        ArmsGem armsGem = null;
        final AttDefHp adh = new AttDefHp();
        if (gemsId != null && !gemsId.isEmpty()) {
            int gemId = 0;
            final String[] strs = gemsId.split(",");
            for (int i = 0; i < strs.length; ++i) {
                gemId = Integer.valueOf(strs[i]);
                if (gemId > 0) {
                    armsGem = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)gemId);
                    final AttDefHp attDefHp = adh;
                    attDefHp.att += armsGem.getAtt();
                    final AttDefHp attDefHp2 = adh;
                    attDefHp2.def += armsGem.getDef();
                    final AttDefHp attDefHp3 = adh;
                    attDefHp3.hp += armsGem.getBlood();
                }
            }
        }
        return adh;
    }
    
    public void refreshWeaponEffect(final int playerId, final int weaponId) {
        final int type = ((ArmsWeapon)this.dataGetter.getArmsWeaponCache().get((Object)weaponId)).getType();
        int bqValue = 0;
        int bsValue = 0;
        final List<PlayerWeapon> pwList = this.dataGetter.getPlayerWeaponDao().getPlayerWeaponsByType(playerId, type);
        for (final PlayerWeapon pw : pwList) {
            if (pw.getLv() <= 0) {
                continue;
            }
            bsValue += this.getAttDefHpforGem(pw.getGemId(), type);
            final ArmsWeapon awc = (ArmsWeapon)this.dataGetter.getArmsWeaponCache().get((Object)weaponId);
            bqValue += awc.getBaseAttribute() + awc.getStrengthen() * (pw.getLv() - 1);
        }
        if (type == 1) {
            bqValue *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 24) / 100.0);
            this.bqAttBase = bqValue + bsValue;
        }
        else if (type == 2) {
            bqValue *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 25) / 100.0);
            this.bqDefBase = bqValue + bsValue;
        }
        else {
            bqValue *= (int)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 26) / 100.0);
            this.bqHpBase = bqValue + bsValue;
        }
    }
    
    public void refreshDiamondEffect(final int playerId) {
        try {
            final List<StoreHouse> list = this.dataGetter.getStoreHouseDao().getSetGemsDiamonds(playerId);
            int type = 0;
            int lv = 0;
            final Map<Integer, Double> tempMap = new HashMap<Integer, Double>(10);
            for (int i = 1; i <= 10; ++i) {
                tempMap.put(i, 0.0);
            }
            for (final StoreHouse sh : list) {
                if (StringUtils.isNotBlank(sh.getRefreshAttribute())) {
                    final String[] skills = sh.getRefreshAttribute().split(";");
                    String[] array;
                    for (int length = (array = skills).length, j = 0; j < length; ++j) {
                        final String temp = array[j];
                        final String[] skill = temp.split(":");
                        type = Integer.valueOf(skill[0]);
                        lv = Integer.valueOf(skill[1]);
                        final ArmsJsSkill sjs = (ArmsJsSkill)this.dataGetter.getArmsJsSkillCache().get((Object)type);
                        if (!tempMap.containsKey(type)) {
                            tempMap.put(type, sjs.getByLv(lv));
                        }
                        else {
                            tempMap.put(type, sjs.getByLv(lv) + tempMap.get(type));
                        }
                    }
                }
            }
            for (final Integer key : tempMap.keySet()) {
                this.gemAttributeMap.put(key, tempMap.get(key));
            }
        }
        catch (Exception e) {
            BattleEffectCache.log.error("refreshDiamondEffect playerId" + playerId, e);
        }
    }
    
    private int getAttDefHpforGem(final String gemsId, final int type) {
        ArmsGem armsGem = null;
        final AttDefHp adh = new AttDefHp();
        if (gemsId != null && !gemsId.isEmpty()) {
            int gemId = 0;
            final String[] strs = gemsId.split(",");
            for (int i = 0; i < strs.length; ++i) {
                gemId = Integer.valueOf(strs[i]);
                if (gemId > 0) {
                    armsGem = (ArmsGem)this.dataGetter.getArmsGemCache().get((Object)gemId);
                    final AttDefHp attDefHp = adh;
                    attDefHp.att += armsGem.getAtt();
                    final AttDefHp attDefHp2 = adh;
                    attDefHp2.def += armsGem.getDef();
                    final AttDefHp attDefHp3 = adh;
                    attDefHp3.hp += armsGem.getBlood();
                }
            }
        }
        if (type == 1) {
            return adh.att;
        }
        if (type == 2) {
            return adh.def;
        }
        return adh.hp;
    }
    
    public EquipEffectCache getEquipMilitaryEffect(final int generalId) {
        if (this.equipMap.containsKey(generalId)) {
            return this.equipMap.get(generalId);
        }
        return this.calcEquipMilitaryEffect(this.playerId, generalId);
    }
    
    public static EquipEffectCache getEquipMax(final IDataGetter dataGetter, final StoreHouse psh) {
        final EquipEffectCache equipData = new EquipEffectCache();
        if (psh.getGoodsType() != 10 && psh.getGoodsType() != 14) {
            if (psh.getGoodsType() == 1 || psh.getGoodsType() == 2) {
                final EquipEffectCache equipEffectCache = equipData;
                equipEffectCache.att += Integer.valueOf(psh.getAttribute());
            }
            else if (psh.getGoodsType() == 3 || psh.getGoodsType() == 4) {
                final EquipEffectCache equipEffectCache2 = equipData;
                equipEffectCache2.def += Integer.valueOf(psh.getAttribute());
            }
            else {
                final EquipEffectCache equipEffectCache3 = equipData;
                equipEffectCache3.hp += Integer.valueOf(psh.getAttribute());
            }
            if (psh.getRefreshAttribute() != null && !psh.getRefreshAttribute().isEmpty()) {
                final String[] skills = psh.getRefreshAttribute().split(";");
                String[] array;
                for (int length = (array = skills).length, j = 0; j < length; ++j) {
                    final String skill = array[j];
                    final String[] idLv = skill.split(":");
                    if (idLv.length == 2) {
                        final int id = Integer.parseInt(idLv[0]);
                        final int lv = Integer.parseInt(idLv[1]);
                        final EquipSkillEffect equipSkillEffect = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(id, lv);
                        if (equipSkillEffect != null) {
                            if (equipSkillEffect.getAttDefHp().att > 0) {
                                final EquipEffectCache equipEffectCache4 = equipData;
                                equipEffectCache4.att += equipSkillEffect.getAttDefHp().att;
                            }
                            if (equipSkillEffect.getAttDefHp().def > 0) {
                                final EquipEffectCache equipEffectCache5 = equipData;
                                equipEffectCache5.def += equipSkillEffect.getAttDefHp().def;
                            }
                            if (equipSkillEffect.getAttDefHp().hp > 0) {
                                final EquipEffectCache equipEffectCache6 = equipData;
                                equipEffectCache6.hp += equipSkillEffect.getAttDefHp().hp;
                            }
                        }
                    }
                }
            }
        }
        else if (psh.getGoodsType() == 14) {
            final EquipProset equipProset = dataGetter.getEquipCache().getEquipProsetByItemId(psh.getItemId());
            if (equipProset == null) {
                return equipData;
            }
            final EquipCoordinates mainSuit = dataGetter.getEquipCache().getMainSuit(equipProset.getId());
            calcEquipCoordinateEffect(equipData, mainSuit, dataGetter);
            final int att = equipProset.getAtt();
            final int def = equipProset.getDef();
            final int blood = equipProset.getBlood();
            if (att > 0) {
                final EquipEffectCache equipEffectCache7 = equipData;
                equipEffectCache7.att += att;
            }
            if (def > 0) {
                final EquipEffectCache equipEffectCache8 = equipData;
                equipEffectCache8.def += def;
            }
            if (blood > 0) {
                final EquipEffectCache equipEffectCache9 = equipData;
                equipEffectCache9.hp += blood * 3;
            }
        }
        else {
            final EquipCoordinates equipCoordinates = dataGetter.getEquipCache().getEquipCoordinateByItemId(psh.getItemId());
            if (equipCoordinates == null) {
                return equipData;
            }
            final Integer[] type = dataGetter.getEquipCache().getSkillArray(equipCoordinates.getId());
            for (int i = 1; i <= type.length; ++i) {
                final Equip equip = dataGetter.getEquipCache().getSuitSingleEquipByType(i);
                final int equipType = equip.getType();
                if (equipType == 1 || equipType == 2) {
                    final EquipEffectCache equipEffectCache10 = equipData;
                    equipEffectCache10.att += Integer.valueOf(equip.getAttribute());
                }
                else if (equipType == 3 || equipType == 4) {
                    final EquipEffectCache equipEffectCache11 = equipData;
                    equipEffectCache11.def += Integer.valueOf(equip.getAttribute());
                }
                else {
                    final EquipEffectCache equipEffectCache12 = equipData;
                    equipEffectCache12.hp += Integer.valueOf(equip.getAttribute());
                }
                final int skillId = type[i - 1];
                final EquipSkillEffect equipSkillEffect2 = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId, equip.getSkillLvMax());
                if (equipSkillEffect2 != null) {
                    if (equipSkillEffect2.getAttDefHp().att > 0) {
                        final EquipEffectCache equipEffectCache13 = equipData;
                        equipEffectCache13.att += equipSkillEffect2.getAttDefHp().att * 4;
                    }
                    if (equipSkillEffect2.getAttDefHp().def > 0) {
                        final EquipEffectCache equipEffectCache14 = equipData;
                        equipEffectCache14.def += equipSkillEffect2.getAttDefHp().def * 4;
                    }
                    if (equipSkillEffect2.getAttDefHp().hp > 0) {
                        final EquipEffectCache equipEffectCache15 = equipData;
                        equipEffectCache15.hp += equipSkillEffect2.getAttDefHp().hp * 4;
                    }
                }
            }
            final int att = equipCoordinates.getAtt();
            final int def = equipCoordinates.getDef();
            final int blood = equipCoordinates.getBlood();
            if (att > 0) {
                final EquipEffectCache equipEffectCache16 = equipData;
                equipEffectCache16.att += att;
            }
            if (def > 0) {
                final EquipEffectCache equipEffectCache17 = equipData;
                equipEffectCache17.def += def;
            }
            if (blood > 0) {
                final EquipEffectCache equipEffectCache18 = equipData;
                equipEffectCache18.hp += blood * 3;
            }
        }
        return equipData;
    }
    
    public EquipEffectCache calcEquipMilitaryEffect(final int playerId, final int generalId) {
        final PlayerGeneralMilitary pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
        final EquipEffectCache equipData = new EquipEffectCache();
        try {
            this.equipMap.put(generalId, equipData);
            if (pgm != null) {
                final List<StoreHouse> shList = this.dataGetter.getStoreHouseDao().getGeneralEquipList(playerId, generalId, 1);
                for (final StoreHouse psh : shList) {
                    if (psh.getGoodsType() == 1 || psh.getGoodsType() == 2) {
                        final EquipEffectCache equipEffectCache = equipData;
                        equipEffectCache.att += Integer.valueOf(psh.getAttribute());
                    }
                    else if (psh.getGoodsType() == 3 || psh.getGoodsType() == 4) {
                        final EquipEffectCache equipEffectCache2 = equipData;
                        equipEffectCache2.def += Integer.valueOf(psh.getAttribute());
                    }
                    else {
                        final EquipEffectCache equipEffectCache3 = equipData;
                        equipEffectCache3.hp += Integer.valueOf(psh.getAttribute());
                    }
                    if (psh.getRefreshAttribute() != null && !psh.getRefreshAttribute().isEmpty()) {
                        final String[] skills = psh.getRefreshAttribute().split(";");
                        String[] array;
                        for (int length = (array = skills).length, j = 0; j < length; ++j) {
                            final String skill = array[j];
                            final String[] idLv = skill.split(":");
                            if (idLv.length == 2) {
                                final int id = Integer.parseInt(idLv[0]);
                                final int lv = Integer.parseInt(idLv[1]);
                                final EquipSkillEffect equipSkillEffect = this.dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(id, lv);
                                if (equipSkillEffect == null) {
                                    final Player player = this.dataGetter.getPlayerDao().read(playerId);
                                    ErrorSceneLog.getInstance().appendErrorMsg("equipSkillEffect is null.").appendPlayerId(playerId).appendPlayerName(player.getPlayerName()).appendGeneralId(generalId).appendGeneralName(((General)this.dataGetter.getGeneralCache().get((Object)generalId)).getName()).flush();
                                }
                                else {
                                    if (equipSkillEffect.getAttDefHp().att > 0) {
                                        final EquipEffectCache equipEffectCache4 = equipData;
                                        equipEffectCache4.att += equipSkillEffect.getAttDefHp().att;
                                    }
                                    if (equipSkillEffect.getAttDefHp().def > 0) {
                                        final EquipEffectCache equipEffectCache5 = equipData;
                                        equipEffectCache5.def += equipSkillEffect.getAttDefHp().def;
                                    }
                                    if (equipSkillEffect.getAttDefHp().hp > 0) {
                                        final EquipEffectCache equipEffectCache6 = equipData;
                                        equipEffectCache6.hp += equipSkillEffect.getAttDefHp().hp;
                                    }
                                }
                            }
                        }
                    }
                }
                final List<StoreHouse> suitList = this.dataGetter.getStoreHouseDao().getGeneralEquipList(playerId, generalId, 10);
                if (suitList.size() > 1) {
                    ErrorSceneLog.getInstance().appendErrorMsg("general has two equipCoordinates..playerId:" + playerId + "   generalId:" + generalId).flush();
                }
                for (final StoreHouse suit : suitList) {
                    final EquipCoordinates equipCoordinates = this.dataGetter.getEquipCache().getEquipCoordinateByItemId(suit.getItemId());
                    if (equipCoordinates == null) {
                        continue;
                    }
                    final Integer[] type = this.dataGetter.getEquipCache().getSkillArray(equipCoordinates.getId());
                    for (int i = 1; i <= type.length; ++i) {
                        final Equip equip = this.dataGetter.getEquipCache().getSuitSingleEquipByType(i);
                        final int equipType = equip.getType();
                        if (equipType == 1 || equipType == 2) {
                            final EquipEffectCache equipEffectCache7 = equipData;
                            equipEffectCache7.att += Integer.valueOf(equip.getAttribute());
                        }
                        else if (equipType == 3 || equipType == 4) {
                            final EquipEffectCache equipEffectCache8 = equipData;
                            equipEffectCache8.def += Integer.valueOf(equip.getAttribute());
                        }
                        else {
                            final EquipEffectCache equipEffectCache9 = equipData;
                            equipEffectCache9.hp += Integer.valueOf(equip.getAttribute());
                        }
                        final int skillId = type[i - 1];
                        final EquipSkillEffect equipSkillEffect2 = this.dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId, equip.getSkillLvMax());
                        if (equipSkillEffect2 != null) {
                            if (equipSkillEffect2.getAttDefHp().att > 0) {
                                final EquipEffectCache equipEffectCache10 = equipData;
                                equipEffectCache10.att += equipSkillEffect2.getAttDefHp().att * 4;
                            }
                            if (equipSkillEffect2.getAttDefHp().def > 0) {
                                final EquipEffectCache equipEffectCache11 = equipData;
                                equipEffectCache11.def += equipSkillEffect2.getAttDefHp().def * 4;
                            }
                            if (equipSkillEffect2.getAttDefHp().hp > 0) {
                                final EquipEffectCache equipEffectCache12 = equipData;
                                equipEffectCache12.hp += equipSkillEffect2.getAttDefHp().hp * 4;
                            }
                        }
                    }
                    final int att = equipCoordinates.getAtt();
                    final int def = equipCoordinates.getDef();
                    final int blood = equipCoordinates.getBlood();
                    if (att > 0) {
                        final EquipEffectCache equipEffectCache13 = equipData;
                        equipEffectCache13.att += att;
                    }
                    if (def > 0) {
                        final EquipEffectCache equipEffectCache14 = equipData;
                        equipEffectCache14.def += def;
                    }
                    if (blood <= 0) {
                        continue;
                    }
                    final EquipEffectCache equipEffectCache15 = equipData;
                    equipEffectCache15.hp += blood * 3;
                }
                final List<StoreHouse> prosetList = this.dataGetter.getStoreHouseDao().getGeneralEquipList(playerId, generalId, 14);
                if (prosetList != null && prosetList.size() > 0) {
                    if (prosetList.size() > 1) {
                        ErrorSceneLog.getInstance().appendErrorMsg("general has two proset..playerId:" + playerId + "   generalId:" + generalId).flush();
                    }
                    final StoreHouse proset = prosetList.get(0);
                    final EquipProset equipProset = this.dataGetter.getEquipCache().getEquipProsetByItemId(proset.getItemId());
                    if (equipProset != null) {
                        final EquipCoordinates mainSuit = this.dataGetter.getEquipCache().getMainSuit(equipProset.getId());
                        calcEquipCoordinateEffect(equipData, mainSuit, this.dataGetter);
                        final int att = equipProset.getAtt();
                        final int def = equipProset.getDef();
                        final int blood = equipProset.getBlood();
                        if (att > 0) {
                            final EquipEffectCache equipEffectCache16 = equipData;
                            equipEffectCache16.att += att;
                        }
                        if (def > 0) {
                            final EquipEffectCache equipEffectCache17 = equipData;
                            equipEffectCache17.def += def;
                        }
                        if (blood > 0) {
                            final EquipEffectCache equipEffectCache18 = equipData;
                            equipEffectCache18.hp += blood * 3;
                        }
                    }
                    else {
                        ErrorSceneLog.getInstance().appendErrorMsg("equipProset is null").appendPlayerId(playerId).append("proset.getItemId()", proset.getItemId()).appendClassName("calcEquipMilitaryEffect").appendMethodName("BattleEffectCache").flush();
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("calcMilatary equip effect fail....playerId:" + playerId + "..generalId" + generalId);
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
        return equipData;
    }
    
    private static void calcEquipCoordinateEffect(final EquipEffectCache equipData, final EquipCoordinates mainSuit, final IDataGetter dataGetter) {
        final Integer[] type = dataGetter.getEquipCache().getSkillArray(mainSuit.getId());
        for (int i = 1; i <= type.length; ++i) {
            final Equip equip = dataGetter.getEquipCache().getSuitSingleEquipByType(i);
            final int equipType = equip.getType();
            if (equipType == 1 || equipType == 2) {
                equipData.att += Integer.valueOf(equip.getAttribute());
            }
            else if (equipType == 3 || equipType == 4) {
                equipData.def += Integer.valueOf(equip.getAttribute());
            }
            else {
                equipData.hp += Integer.valueOf(equip.getAttribute());
            }
            final int skillId = type[i - 1];
            final EquipSkillEffect equipSkillEffect = dataGetter.getEquipSkillEffectCache().getEquipSkillEffectByIdLV(skillId, equip.getSkillLvMax());
            if (equipSkillEffect != null) {
                if (equipSkillEffect.getAttDefHp().att > 0) {
                    equipData.att += equipSkillEffect.getAttDefHp().att * 4;
                }
                if (equipSkillEffect.getAttDefHp().def > 0) {
                    equipData.def += equipSkillEffect.getAttDefHp().def * 4;
                }
                if (equipSkillEffect.getAttDefHp().hp > 0) {
                    equipData.hp += equipSkillEffect.getAttDefHp().hp * 4;
                }
            }
        }
    }
    
    public TroopSerialEffectCache getTroopEffect(final int serial) {
        if (this.troopMap.containsKey(serial)) {
            return this.troopMap.get(serial);
        }
        return this.calcTechEffect(this.playerId, serial);
    }
    
    public TroopSerialEffectCache calcTechEffect(final int playerId, final int serial) {
        final TroopSerialEffectCache troopData = new TroopSerialEffectCache();
        this.troopMap.put(serial, troopData);
        if (serial == 5) {
            final TroopSerialEffectCache troopSerialEffectCache = troopData;
            troopSerialEffectCache.techAtt *= (float)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 7) / 100.0);
            final TroopSerialEffectCache troopSerialEffectCache2 = troopData;
            troopSerialEffectCache2.techDef *= (float)(1.0 + this.dataGetter.getTechEffectCache().getTechEffect(playerId, 9) / 100.0);
        }
        else if (serial == 6 || serial == 7 || serial != 8) {}
        return troopData;
    }
    
    public void removeEquipEffect(final int generalId) {
        this.equipMap.remove(generalId);
    }
    
    public void removeTroopEffect(final int serial) {
        this.troopMap.remove(serial);
    }
    
    public int getBwAttBase() {
        return this.bwAttBase;
    }
    
    public int getBwDefBase() {
        return this.bwDefBase;
    }
    
    public int getBqAttBase() {
        return this.bqAttBase;
    }
    
    public int getBqDefBase() {
        return this.bqDefBase;
    }
    
    public int getBqHpBase() {
        return this.bqHpBase;
    }
    
    public float getBwAttCoe() {
        return this.bwAttCoe;
    }
    
    public float getBwDefCoe() {
        return this.bwDefCoe;
    }
    
    public int getTroopAttBase() {
        return this.troopAttBase;
    }
    
    public void setTroopAttBase(final int troopAttBase) {
        this.troopAttBase = troopAttBase;
    }
    
    public int getTrooDefBase() {
        return this.trooDefBase;
    }
    
    public void setTrooDefBase(final int trooDefBase) {
        this.trooDefBase = trooDefBase;
    }
}
