package com.reign.gcld.civiltrick.trick;

import com.reign.gcld.sdata.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.world.domain.*;
import java.util.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.common.util.*;

public class TrickFactory
{
    public static final String TRICK_GUWU = "guwu";
    public static final String TRICK_DONGYAO = "dongyao";
    public static final String TRICK_HUOGONG = "huogong";
    public static final String TRICK_SHUIGONG = "shuigong";
    public static final String TRICK_LUOSHI = "luoshi";
    public static final String TRICK_XIANJING = "xianjing";
    public static final String TRICK_KONGCHENG = "kongcheng";
    public static final String TRICK_HUANGBAO = "huangbao";
    
    public static ITrick getTrick(final Stratagem trick) {
        if (trick == null) {
            return null;
        }
        final String type = trick.getType();
        if (StringUtils.isBlank(type)) {
            return null;
        }
        if (type.equalsIgnoreCase("guwu") || type.equalsIgnoreCase("dongyao")) {
            return new CilvilStateTrick(trick);
        }
        if (type.equalsIgnoreCase("huogong") || type.equalsIgnoreCase("shuigong") || type.equalsIgnoreCase("luoshi")) {
            return new CilvilForcesTrick(trick);
        }
        if (type.equalsIgnoreCase("xianjing")) {
            return new CilvilTrapTrick(trick);
        }
        if (type.equalsIgnoreCase("kongcheng")) {
            return new CilvilProtectTrick(trick);
        }
        if (type.equalsIgnoreCase("huangbao")) {
            return new CilvilLiesTrick(trick);
        }
        return null;
    }
    
    public static Tuple<Integer, String> getTrick(final String trickInfo, final int type, final int forceId, final Map<String, List<Integer>> map) {
        final Tuple<Integer, String> result = new Tuple();
        int min = 1000;
        if (type == 2) {
            min = 0;
        }
        int id = 0;
        int times = 0;
        long time = 0L;
        if (StringUtils.isBlank(trickInfo)) {
            return null;
        }
        String[] split;
        for (int length = (split = trickInfo.split("#")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            if (!StringUtils.isBlank(s)) {
                final String[] b = s.split("-");
                if (b.length > 0) {
                    id = Integer.parseInt(b[0]);
                    switch (type) {
                        case 1: {
                            if (!map.get("guwu").contains(Integer.valueOf(b[0]))) {
                                break;
                            }
                            final int f = Integer.parseInt(b[3]);
                            final long last = Long.parseLong(b[b.length - 1]);
                            if (forceId == f && last > new Date().getTime()) {
                                result.left = id;
                                result.right = b[b.length - 1];
                                return result;
                            }
                            break;
                        }
                        case 2: {
                            if (!map.get("dongyao").contains(Integer.valueOf(b[0]))) {
                                break;
                            }
                            final int f = Integer.parseInt(b[3]);
                            final long last = Long.parseLong(b[b.length - 1]);
                            if (f != forceId && last > new Date().getTime() && id > min) {
                                min = id;
                                time = Long.parseLong(b[b.length - 1]);
                                break;
                            }
                            break;
                        }
                        case 4: {
                            if (map.get("xianjing").contains(Integer.valueOf(b[0])) && id < min && Integer.parseInt(b[3]) > 0 && Long.parseLong(b[b.length - 1]) > new Date().getTime() && forceId != Integer.parseInt(b[4])) {
                                min = id;
                                times = Integer.parseInt(b[3]);
                                time = Long.valueOf(b[b.length - 1]);
                                break;
                            }
                            break;
                        }
                        case 5: {
                            if (map.get("kongcheng").contains(Integer.valueOf(b[0]))) {
                                result.left = id;
                                result.right = b[b.length - 1];
                                return result;
                            }
                            break;
                        }
                        case 6: {
                            if (map.get("huangbao").contains(Integer.valueOf(b[0])) && forceId != Integer.parseInt(b[3])) {
                                result.left = id;
                                result.right = b[b.length - 1];
                                return result;
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (type == 4) {
            result.left = min;
            result.right = String.valueOf(time) + "-" + times;
            return result;
        }
        if (type == 2 && min != 24) {
            result.left = min;
            result.right = String.valueOf(time);
            return result;
        }
        return null;
    }
    
    public static String getNewInfo(final String trickInfo, final Stratagem trick, final boolean flag, final long cd, final int forceId, final Map<String, List<Integer>> map) {
        if (!trick.getType().equalsIgnoreCase("xianjing")) {
            return "";
        }
        final StringBuffer s = new StringBuffer();
        if (trickInfo.equals("")) {
            return "";
        }
        final String[] info = trickInfo.split("#");
        int a = 0;
        int first = 0;
        String[] array;
        for (int length = (array = info).length, j = 0; j < length; ++j) {
            String i = array[j];
            if (!StringUtils.isBlank(i)) {
                final String[] div = i.split("-");
                if (div.length > 0) {
                    if (flag) {
                        if (StringUtils.isBlank(div[0])) {
                            continue;
                        }
                        if (Integer.parseInt(div[0]) == trick.getId()) {
                            if (cd != 0L) {
                                div[2] = String.valueOf(cd);
                                div[3] = String.valueOf(trick.getPar3());
                                div[4] = String.valueOf(forceId);
                            }
                            else {
                                div[3] = String.valueOf(Integer.parseInt(div[3]) - 1);
                                if (Integer.parseInt(div[3]) == 0) {
                                    div[1] = (div[0] = "");
                                    div[3] = (div[2] = "");
                                    div[5] = (div[4] = "");
                                }
                            }
                            ++a;
                        }
                    }
                    else if (map.get(trick.getType()).contains(Integer.valueOf(div[0]))) {
                        div[0] = String.valueOf(trick.getId());
                        div[1] = String.valueOf(trick.getQuality());
                        div[2] = String.valueOf(cd);
                        ++a;
                    }
                    i = ConcateString(div, "-");
                    if (first == 0) {
                        s.append(i);
                    }
                    else {
                        s.append("#").append(i);
                    }
                    ++first;
                }
            }
        }
        if (a == 0) {
            return "";
        }
        return s.toString();
    }
    
    public static String ConcateString(final String[] info, final String splitString) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < info.length; ++i) {
            if (StringUtils.isBlank(info[0])) {
                return "";
            }
            sb.append(info[i]);
            if (i != info.length - 1) {
                sb.append(splitString);
            }
        }
        return sb.toString();
    }
    
    public static String getCityState(final String trickInfo) {
        final StringBuffer sb = new StringBuffer();
        if (StringUtils.isBlank(trickInfo)) {
            return "";
        }
        final String[] a = trickInfo.split("#");
        String[] array;
        for (int length = (array = a).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] b = s.split("-");
                if (b.length > 3 && Long.parseLong(b[b.length - 1]) > new Date().getTime()) {
                    sb.append(s).append("#");
                }
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static void sendCityTrickState(final int cityId, final Stratagem trick, final JsonDocument doc, final int forceId) {
        final Group group = GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + forceId);
        if (group != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITIES.getModule(), doc.toByte()));
            group.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITIES.getCommand(), 0, bytes));
        }
    }
    
    public static void sendTrickInfo(final int playerId, final Stratagem trick) {
        final String firString = MessageFormatter.format(LocalMessages.TRICK_HEAD, new Object[] { trick.getName() });
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("trickInfo", firString);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_TRICKINFO, doc.toByte());
    }
    
    public static void sendLiesInfo(final int playerId, final Integer generalId, final Stratagem stratagem) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", generalId);
        doc.createElement("trickInfo", (Object)LocalMessages.LOCATION_IN_LIES);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_TRICKINFO, doc.toByte());
    }
    
    public static Set<Integer> getNeibours(final List<PlayerGeneralMilitary> list, final IDataGetter dataGetter) {
        final Set<Integer> result = new HashSet<Integer>();
        for (final PlayerGeneralMilitary p : list) {
            result.addAll(dataGetter.getWorldRoadCache().getNeighbors(p.getLocationId()));
        }
        return result;
    }
    
    public static void getTrickInfo(final JsonDocument doc, final Stratagem stratagem) {
        doc.createElement("trickName", stratagem.getName());
        doc.createElement("lv", stratagem.getQuality());
        doc.createElement("promptCD", stratagem.getCd());
        doc.createElement("intro", stratagem.getIntro());
        doc.createElement("trickPic", stratagem.getPic());
        doc.createElement("type", stratagem.getType());
        final String types = stratagem.getType();
        if (types.equalsIgnoreCase("guwu") || types.equalsIgnoreCase("dongyao")) {
            doc.createElement("lastTime", stratagem.getPar2());
        }
        else if (types.equalsIgnoreCase("huangbao")) {
            doc.createElement("lastTime", stratagem.getPar1());
        }
        else if (types.equalsIgnoreCase("huogong") || types.equalsIgnoreCase("shuigong") || types.equalsIgnoreCase("luoshi")) {
            doc.createElement("protectCd", stratagem.getPar4());
        }
        else if (types.equalsIgnoreCase("kongcheng")) {
            doc.createElement("lastTime", stratagem.getPar1());
            doc.createElement("protectCd", stratagem.getPar2());
        }
        else if (types.equalsIgnoreCase("xianjing")) {
            doc.createElement("lastTime", stratagem.getPar1());
        }
    }
    
    public static boolean decideCanAttCity(final int cityId, final int forceId, final IDataGetter dataGetter) {
        final City chooseCity = CityDataCache.cityArray[cityId];
        if (chooseCity == null) {
            return false;
        }
        if (chooseCity.getForceId() == forceId && chooseCity.getState() != 0) {
            return true;
        }
        if (chooseCity.getForceId() != forceId) {
            final Set<Integer> nbSet = dataGetter.getWorldRoadCache().getNeighbors(cityId);
            City subCity = null;
            for (final Integer nbCity : nbSet) {
                subCity = CityDataCache.cityArray[nbCity];
                if (subCity == null) {
                    continue;
                }
                if (subCity.getForceId() == forceId) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean decideCanAttEnemyCity(final int cityId, final int forceId, final IDataGetter dataGetter) {
        final City chooseCity = CityDataCache.cityArray[cityId];
        if (chooseCity == null || chooseCity.getForceId() == forceId) {
            return false;
        }
        if (chooseCity.getForceId() != forceId) {
            final Set<Integer> nbSet = dataGetter.getWorldRoadCache().getNeighbors(cityId);
            City subCity = null;
            for (final Integer nbCity : nbSet) {
                subCity = CityDataCache.cityArray[nbCity];
                if (subCity == null) {
                    continue;
                }
                if (subCity.getForceId() == forceId) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static byte[] getJsonForTrick(final State state, final String msg, final int type) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (type == 1) {
            JsonBuilder.createNamedElement(doc, "state", State.SUCCESS.getValue());
        }
        else {
            JsonBuilder.createNamedElement(doc, "state", state.getValue());
        }
        doc.startObject("data");
        if (type == 1) {
            if (state == State.FAIL) {
                JsonBuilder.createNamedElement(doc, "isCanUse", false);
            }
            else {
                JsonBuilder.createNamedElement(doc, "isCanUse", true);
            }
        }
        else {
            JsonBuilder.createNamedElement(doc, "msg", msg);
        }
        doc.endObject();
        doc.endObject();
        return doc.toByte();
    }
    
    public static boolean decideCanAttCityInScenario(final int cityId, final int forceId, final Map<Integer, JuBenCityDto> map, final IDataGetter dataGetter) {
        final JuBenCityDto chooseCity = map.get(cityId);
        if (chooseCity == null) {
            return false;
        }
        if (chooseCity.forceId == forceId && chooseCity.state != 0) {
            return true;
        }
        if (chooseCity.forceId != forceId) {
            final Set<Integer> nbSet = dataGetter.getSoloRoadCache().getNeighbors(cityId);
            JuBenCityDto subCity = null;
            for (final Integer nbCity : nbSet) {
                subCity = map.get(nbCity);
                if (subCity == null) {
                    continue;
                }
                if (subCity.forceId == forceId) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static HashMap<String, TrickDto> getTrickDto(final HashMap<Integer, HashMap<String, TrickDto>> forceMap, final int forceId) {
        HashMap<Integer, HashMap<String, TrickDto>> temp = null;
        if (forceMap == null) {
            temp = new HashMap<Integer, HashMap<String, TrickDto>>();
        }
        else {
            temp = forceMap;
        }
        HashMap<String, TrickDto> result = temp.get(forceId);
        result = ((result == null) ? new HashMap<String, TrickDto>() : result);
        return result;
    }
    
    public static String getTrickInfoInScenarioWithMap(final HashMap<Integer, HashMap<String, TrickDto>> trickDto) {
        if (trickDto == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (final Integer index : trickDto.keySet()) {
            final HashMap<String, TrickDto> map = trickDto.get(index);
            if (map != null) {
                if (map.isEmpty()) {
                    continue;
                }
                for (final String in : map.keySet()) {
                    final TrickDto dto = map.get(in);
                    if (dto == null) {
                        continue;
                    }
                    sb.append(dto.toString()).append("#");
                }
            }
        }
        SymbolUtil.removeTheLast(sb);
        return sb.toString();
    }
}
