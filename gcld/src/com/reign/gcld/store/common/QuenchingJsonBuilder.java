package com.reign.gcld.store.common;

import com.reign.gcld.store.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.cache.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;

public class QuenchingJsonBuilder
{
    private static final int MAX_SKILL_NUM = 4;
    
    public static void getEquipInfo(final List<StoreHouse> equipList, final JsonDocument doc, final EquipCache equipCache, final EquipSkillCache equipSkillCache, final GeneralCache generalCache, final EquipSkillEffectCache equipSkillEffectCache) {
        doc.startArray("equips");
        for (final StoreHouse s : equipList) {
            if (s == null) {
                break;
            }
            doc.startObject();
            getEquipInfo(s, doc, equipCache, equipSkillCache, generalCache, true, equipSkillEffectCache);
            appendCanComposeSuitInfo(s, doc, equipCache);
            doc.endObject();
        }
        doc.endArray();
    }
    
    private static void appendCanComposeSuitInfo(final StoreHouse storeHouse, final JsonDocument doc, final EquipCache equipCache) {
        try {
            if (storeHouse == null) {
                return;
            }
            final String attr = storeHouse.getRefreshAttribute();
            if (StringUtils.isBlank(attr)) {
                return;
            }
            final int num = EquipCommon.getRefreshAttNum(attr);
            if (num >= 4) {
                final int skillId = storeHouse.getSpecialSkillId();
                final int goodsType = storeHouse.getGoodsType();
                final List<MultiResult> canCompoundSuitList = equipCache.getCanCompoundSuitListBySkillId(skillId, goodsType);
                if (canCompoundSuitList == null || canCompoundSuitList.isEmpty()) {
                    return;
                }
                doc.startArray("canSuit");
                for (final MultiResult result : canCompoundSuitList) {
                    final String name = (String)result.result2;
                    final String pic = (String)result.result3;
                    doc.startObject();
                    doc.createElement("name", name);
                    doc.createElement("pic", pic);
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("appendCanComposeSuitInfo exception", e);
        }
    }
    
    public static void getEquipInfo(final StoreHouse storeHouse, final JsonDocument doc, final EquipCache equipCache, final EquipSkillCache equipSkillCache, final GeneralCache generalCache, final boolean flag, final EquipSkillEffectCache equipSkillEffectCache) {
        doc.createElement("lv", storeHouse.getLv());
        doc.createElement("quality", storeHouse.getQuality());
        doc.createElement("id", storeHouse.getVId());
        final Equip equip = (Equip)equipCache.get((Object)storeHouse.getItemId());
        if (equip.getType() == 5 || equip.getType() == 6) {
            doc.createElement("attr", Integer.valueOf(storeHouse.getAttribute()) / 3);
        }
        else {
            doc.createElement("attr", storeHouse.getAttribute());
        }
        doc.createElement("name", equip.getName());
        doc.createElement("pic", equip.getPic());
        doc.createElement("type", equip.getType());
        doc.createElement("maxLv", equip.getSkillLvMax());
        final Integer owner = storeHouse.getOwner();
        if (owner != null && owner != 0) {
            final General general = (General)generalCache.get((Object)storeHouse.getOwner());
            doc.createElement("owner", general.getName());
            doc.createElement("generalQuality", general.getQuality());
        }
        if (flag) {
            EquipCommon.getRefreshAttribute(storeHouse.getRefreshAttribute(), doc, equipSkillCache, null, equipSkillEffectCache, equip);
            EquipCommon.getMaxSkillAndLv(doc, equip, equipCache, storeHouse.getSpecialSkillId(), storeHouse.getRefreshAttribute());
        }
    }
}
