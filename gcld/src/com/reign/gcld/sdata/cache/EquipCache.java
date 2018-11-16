package com.reign.gcld.sdata.cache;

import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import java.util.*;

@Component("equipCache")
public class EquipCache extends AbstractCache<Integer, Equip>
{
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    private Map<Integer, List<Equip>> qualityEquipMap;
    private Map<Integer, Map<Integer, List<Equip>>> map;
    private Map<Integer, Equip> suitEquipsMap;
    private Map<Integer, EquipCoordinates> coordinateMap;
    private Map<Integer, Integer[]> skillArrayMap;
    List<Integer> jinpinEquips;
    private Map<Integer, EquipProset> equipProsetMap;
    private Map<Integer, Integer> suitIdToProsetIdMap;
    private Map<Integer, CommonValueParameter> itemIdToAttributeMap;
    
    public EquipCache() {
        this.qualityEquipMap = new HashMap<Integer, List<Equip>>();
        this.map = new HashMap<Integer, Map<Integer, List<Equip>>>();
        this.suitEquipsMap = new HashMap<Integer, Equip>();
        this.coordinateMap = new HashMap<Integer, EquipCoordinates>();
        this.skillArrayMap = new HashMap<Integer, Integer[]>();
        this.jinpinEquips = new ArrayList<Integer>();
        this.equipProsetMap = new HashMap<Integer, EquipProset>();
        this.suitIdToProsetIdMap = new HashMap<Integer, Integer>();
        this.itemIdToAttributeMap = new HashMap<Integer, CommonValueParameter>();
    }
    
    public List<Integer> getJinpinEquips() {
        return this.jinpinEquips;
    }
    
    public void setJinpinEquips(final List<Integer> jinpinEquips) {
        this.jinpinEquips = jinpinEquips;
    }
    
    public Map<Integer, CommonValueParameter> getItemIdToAttributeMap() {
        return this.itemIdToAttributeMap;
    }
    
    public void setItemIdToAttributeMap(final Map<Integer, CommonValueParameter> itemIdToAttributeMap) {
        this.itemIdToAttributeMap = itemIdToAttributeMap;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Equip> resultList = this.dataLoader.getModels((Class)Equip.class);
        final List<EquipCoordinates> coordinates = this.dataLoader.getModels((Class)EquipCoordinates.class);
        for (final Equip temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            List<Equip> list = this.qualityEquipMap.get(temp.getQuality());
            if (list == null) {
                list = new ArrayList<Equip>();
                this.qualityEquipMap.put(temp.getQuality(), list);
            }
            Map<Integer, List<Equip>> mapList = this.map.get(temp.getType());
            if (mapList == null) {
                mapList = new HashMap<Integer, List<Equip>>();
                final List<Equip> list2 = new ArrayList<Equip>();
                list2.add(temp);
                mapList.put(temp.getQuality(), list2);
                this.map.put(temp.getType(), mapList);
            }
            else {
                List<Equip> list3 = mapList.get(temp.getQuality());
                if (list3 == null) {
                    list3 = new ArrayList<Equip>();
                    list3.add(temp);
                    mapList.put(temp.getQuality(), list3);
                }
                else {
                    list3.add(temp);
                }
            }
            list.add(temp);
            if (temp.getQuality() == 6 && temp.getSkillNum() == this.getEquipMaxSkillNum(temp)) {
                this.suitEquipsMap.put(temp.getType(), temp);
            }
        }
        for (final Integer k : this.map.keySet()) {
            final Map<Integer, List<Equip>> lisMap = this.map.get(k);
            for (final Integer key : lisMap.keySet()) {
                if (key < 4) {
                    continue;
                }
                int max = 0;
                int index = 0;
                for (int i = 0; i < lisMap.get(key).size(); ++i) {
                    final Equip equip = lisMap.get(key).get(i);
                    if (equip.getSkillNum() > max) {
                        index = equip.getId();
                        max = equip.getSkillNum();
                    }
                }
                if (max == 0 || index == 0) {
                    continue;
                }
                this.jinpinEquips.add(index);
            }
        }
        for (final EquipCoordinates suit : coordinates) {
            if (suit == null) {
                continue;
            }
            this.coordinateMap.put(suit.getId(), suit);
            final Integer[] array = { suit.getPos1Skill(), suit.getPos2Skill(), suit.getPos3Skill(), suit.getPos4Skill(), suit.getPos5Skill(), suit.getPos6Skill() };
            this.skillArrayMap.put(suit.getId(), array);
            final CommonValueParameter cvp = this.getEquipCoordinatesAttribute(suit, array, true);
            cvp.setPar3(cvp.getPar3() / 3);
            this.itemIdToAttributeMap.put(suit.getItemId(), cvp);
        }
        final List<EquipProset> equipProsets = this.dataLoader.getModels((Class)EquipProset.class);
        for (final EquipProset equipProset : equipProsets) {
            this.equipProsetMap.put(equipProset.getId(), equipProset);
            final int mainSuitId = equipProset.getSetMain();
            final int secondSuitId = equipProset.getSet1();
            final EquipCoordinates main = this.coordinateMap.get(mainSuitId);
            final EquipCoordinates second = this.coordinateMap.get(secondSuitId);
            this.suitIdToProsetIdMap.put(main.getItemId(), equipProset.getId());
            this.suitIdToProsetIdMap.put(second.getItemId(), equipProset.getId());
            final EquipCoordinates mainSuit = this.getMainSuit(equipProset.getId());
            final Integer[] array2 = { mainSuit.getPos1Skill(), mainSuit.getPos2Skill(), mainSuit.getPos3Skill(), mainSuit.getPos4Skill(), mainSuit.getPos5Skill(), mainSuit.getPos6Skill() };
            final CommonValueParameter cvp2 = this.getEquipCoordinatesAttribute(mainSuit, array2, false);
            final int att = equipProset.getAtt();
            final int def = equipProset.getDef();
            final int blood = equipProset.getBlood();
            if (att > 0) {
                cvp2.setPar1(cvp2.getPar1() + att);
            }
            if (def > 0) {
                cvp2.setPar2(cvp2.getPar2() + def);
            }
            if (blood > 0) {
                cvp2.setPar3(cvp2.getPar3() + blood * 3);
            }
            cvp2.setPar3(cvp2.getPar3() / 3);
            this.itemIdToAttributeMap.put(equipProset.getItemId(), cvp2);
        }
    }
    
    private CommonValueParameter getEquipCoordinatesAttribute(final EquipCoordinates suit, final Integer[] array, final boolean withExtra) {
        CommonValueParameter cvp = new CommonValueParameter();
        int att = 0;
        int def = 0;
        int blood = 0;
        for (int i = 1; i <= array.length; ++i) {
            cvp = new CommonValueParameter();
            final Equip equip = this.getSuitSingleEquipByType(i);
            final int equipType = equip.getType();
            if (equipType == 1 || equipType == 2) {
                att += Integer.valueOf(equip.getAttribute());
            }
            else if (equipType == 3 || equipType == 4) {
                def += Integer.valueOf(equip.getAttribute());
            }
            else {
                blood += Integer.valueOf(equip.getAttribute());
            }
            final int skillId = array[i - 1];
            final EquipSkillEffect equipSkillEffect = this.equipSkillEffectCache.getEquipSkillEffectByIdLV(skillId, equip.getSkillLvMax());
            if (equipSkillEffect != null) {
                if (equipSkillEffect.getAttDefHp().att > 0) {
                    att += equipSkillEffect.getAttDefHp().att * 4;
                }
                if (equipSkillEffect.getAttDefHp().def > 0) {
                    def += equipSkillEffect.getAttDefHp().def * 4;
                }
                if (equipSkillEffect.getAttDefHp().hp > 0) {
                    blood += equipSkillEffect.getAttDefHp().hp * 4;
                }
            }
        }
        if (withExtra) {
            final int tempAtt = suit.getAtt();
            final int tempDef = suit.getDef();
            final int tempBlood = suit.getBlood();
            if (att > 0) {
                att += tempAtt;
            }
            if (def > 0) {
                def += tempDef;
            }
            if (blood > 0) {
                blood += tempBlood * 3;
            }
        }
        cvp.setPar1(att);
        cvp.setPar2(def);
        cvp.setPar3(blood);
        return cvp;
    }
    
    public List<Equip> getEquipsByQuality(final int quality) {
        return this.qualityEquipMap.get(quality);
    }
    
    public List<Equip> getEquipsByTypeQlt(final int type, final int quality) {
        final List<Equip> resultList = new ArrayList<Equip>();
        for (int i = 1; i < quality; ++i) {
            final List<Equip> list = this.qualityEquipMap.get(i);
            for (final Equip equip : list) {
                if (equip.getType() == type) {
                    resultList.add(equip);
                }
            }
        }
        if (quality == 1) {
            final List<Equip> list2 = this.qualityEquipMap.get(1);
            for (final Equip equip2 : list2) {
                if (equip2.getType() == type) {
                    resultList.add(equip2);
                }
            }
        }
        return resultList;
    }
    
    public List<Equip> getEquipsByTypeQuality(final int type, final int quality) {
        final List<Equip> resultList = new ArrayList<Equip>();
        final List<Equip> list = this.qualityEquipMap.get(quality);
        for (final Equip equip : list) {
            if (equip.getType() == type) {
                resultList.add(equip);
            }
        }
        return resultList;
    }
    
    public int getEquipMaxSkillNum(final Equip equip) {
        int result = equip.getSkillNum();
        for (final Integer key : super.getCacheMap().keySet()) {
            final Equip e = (Equip)super.get((Object)key);
            if (!e.getType().equals(equip.getType())) {
                continue;
            }
            if (!e.getQuality().equals(equip.getQuality())) {
                continue;
            }
            if (e.getSkillNum() <= result) {
                continue;
            }
            result = e.getSkillNum();
        }
        return result;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.qualityEquipMap.clear();
        this.map.clear();
        this.jinpinEquips.clear();
        this.suitEquipsMap.clear();
        this.coordinateMap.clear();
        this.skillArrayMap.clear();
    }
    
    public Equip getEquipsByTypeQualityBest(final int type, final int equipQualityPurple) {
        final List<Equip> equips = this.getEquipsByTypeQuality(type, equipQualityPurple);
        if (equips == null) {
            return null;
        }
        for (final Equip equip : equips) {
            if (equip == null) {
                continue;
            }
            if (equip.getSkillNum() == this.getEquipMaxSkillNum(equip)) {
                return equip;
            }
        }
        return null;
    }
    
    public EquipCoordinates getEquipCoordinateByItemId(final int itemId) {
        for (final EquipCoordinates suit : this.coordinateMap.values()) {
            if (suit == null) {
                continue;
            }
            if (suit.getItemId() == itemId) {
                return suit;
            }
        }
        return null;
    }
    
    public Equip getSuitSingleEquipByType(final int type) {
        return this.suitEquipsMap.get(type);
    }
    
    public Integer[] getSkillArray(final int id) {
        return this.skillArrayMap.get(id);
    }
    
    public EquipCoordinates getMainSuit(final int prosetId) {
        final EquipProset equipProset = this.equipProsetMap.get(prosetId);
        if (equipProset == null) {
            return null;
        }
        final int mainSuitId = equipProset.getSetMain();
        final EquipCoordinates equipCoordinates = this.coordinateMap.get(mainSuitId);
        return equipCoordinates;
    }
    
    public List<EquipCoordinates> getAllSuits(final int prosetId) {
        final EquipProset equipProset = this.equipProsetMap.get(prosetId);
        if (equipProset == null) {
            return null;
        }
        final int mainSuitId = equipProset.getSetMain();
        final int secondSuitId = equipProset.getSet1();
        final EquipCoordinates main = this.coordinateMap.get(mainSuitId);
        final EquipCoordinates second = this.coordinateMap.get(secondSuitId);
        final List<EquipCoordinates> list = new ArrayList<EquipCoordinates>();
        list.add(main);
        list.add(second);
        return list;
    }
    
    public EquipProset getEquipProsetByItemId(final int itemId) {
        for (final EquipProset equipProset : this.equipProsetMap.values()) {
            if (equipProset == null) {
                continue;
            }
            if (equipProset.getItemId() == itemId) {
                return equipProset;
            }
        }
        return null;
    }
    
    public EquipProset getProsetBySubSuitId(final int changeItemId) {
        final Integer result = this.suitIdToProsetIdMap.get(changeItemId);
        if (result == null) {
            return null;
        }
        return this.equipProsetMap.get(result);
    }
    
    public List<MultiResult> getCanCompoundSuitListBySkillId(final int skillId, final int goodsType) {
        if (goodsType < 0 || goodsType > 6) {
            return null;
        }
        final List<MultiResult> list = new ArrayList<MultiResult>();
        MultiResult temp = null;
        final Set<Integer> suitIds = new HashSet<Integer>();
        for (final EquipCoordinates cell : this.coordinateMap.values()) {
            if (this.getSkillIdByPosition(cell, goodsType) == skillId) {
                temp = new MultiResult();
                temp.result1 = cell.getItemId();
                temp.result2 = cell.getName();
                temp.result3 = cell.getPic();
                list.add(temp);
                suitIds.add(cell.getItemId());
            }
        }
        for (final Integer id : suitIds) {
            final EquipProset proset = this.equipProsetMap.get(id);
            if (proset == null) {
                continue;
            }
            temp = new MultiResult();
            temp.result1 = proset.getId();
            temp.result2 = proset.getName();
            temp.result3 = proset.getPic();
            list.add(temp);
        }
        return list;
    }
    
    private int getSkillIdByPosition(final EquipCoordinates cell, final int goodsType) {
        if (goodsType == 1) {
            return cell.getPos1Skill();
        }
        if (goodsType == 2) {
            return cell.getPos2Skill();
        }
        if (goodsType == 3) {
            return cell.getPos3Skill();
        }
        if (goodsType == 4) {
            return cell.getPos4Skill();
        }
        if (goodsType == 5) {
            return cell.getPos5Skill();
        }
        if (goodsType == 6) {
            return cell.getPos6Skill();
        }
        return 0;
    }
}
