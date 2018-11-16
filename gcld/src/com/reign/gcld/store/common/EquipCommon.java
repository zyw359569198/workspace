package com.reign.gcld.store.common;

import com.reign.framework.json.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.sdata.cache.*;

public class EquipCommon
{
    public static int getEquipLimitLv(final Equip equip, final int playerLv) {
        return playerLv;
    }
    
    public static void getRefreshAttribute(final String refreshAttribute, final JsonDocument doc, final EquipSkillCache equipSkillCache, final List<Integer> list, final EquipSkillEffectCache equipSkillEffectCache, final Equip equip) {
        if (!StringUtils.isBlank(refreshAttribute)) {
            doc.startArray("refreshAttribute");
            int count = 0;
            String[] split;
            for (int length = (split = refreshAttribute.split(";")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                if (!StringUtils.isBlank(s)) {
                    doc.startObject();
                    final String[] info = s.split(":");
                    final int attType = Integer.parseInt(info[0]);
                    doc.createElement("attType", attType);
                    final EquipSkill eSkill = (EquipSkill)equipSkillCache.get((Object)attType);
                    doc.createElement("attrName", eSkill.getName());
                    final int level = Integer.parseInt(info[1]);
                    doc.createElement("attValue", level);
                    doc.createElement("skillPic", eSkill.getPic());
                    doc.createElement("attIntro", eSkill.getIntro());
                    doc.createElement("baseAttribute", eSkill.getIntro());
                    if (list != null && list.contains(count)) {
                        doc.createElement("isUp", true);
                    }
                    if (equip.getQuality() >= 5 && count >= equip.getSkillNum()) {
                        doc.createElement("isSpecial", true);
                    }
                    doc.endObject();
                    ++count;
                }
            }
            doc.endArray();
        }
    }
    
    public static void getMaxSkillAndLv(final JsonDocument doc, final Equip equip, final EquipCache equipCache, final Integer specialId, final String attriString) {
        doc.createElement("maxLv", equip.getSkillLvMax());
        final int length = getRefreshAttNum(attriString);
        final int maxNum = Math.max(equipCache.getEquipMaxSkillNum(equip), length);
        doc.createElement("maxSkillNum", maxNum);
        doc.createElement("copper", equip.getCopperSold());
    }
    
    public static int getRefreshAttNum(final String attribute) {
        if (StringUtils.isBlank(attribute)) {
            return 0;
        }
        int length = 0;
        final String[] infos = attribute.split(";");
        for (int i = 0; i < infos.length; ++i) {
            if (!StringUtils.isBlank(infos[i])) {
                ++length;
            }
        }
        return length;
    }
    
    public static boolean isFullLvAndNumber(final String skills, final Equip equip, final EquipCache equipCache) {
        if (StringUtils.isBlank(skills)) {
            return false;
        }
        if (!equipCache.getJinpinEquips().contains(equip.getId()) && equip.getQuality() >= 5) {
            return false;
        }
        final String[] infos = skills.split(";");
        int length = 0;
        SkillDto dto = null;
        for (int i = 0; i < infos.length; ++i) {
            if (!StringUtils.isBlank(infos[i])) {
                ++length;
                dto = new SkillDto(infos[i]);
                if (dto.getSkillLv() != equip.getSkillLvMax()) {
                    return false;
                }
            }
        }
        return length >= equip.getSkillNum();
    }
}
