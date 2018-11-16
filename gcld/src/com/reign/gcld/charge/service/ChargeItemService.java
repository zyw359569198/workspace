package com.reign.gcld.charge.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.sdata.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.charge.dto.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.sdata.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.player.domain.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;

@Service
public class ChargeItemService implements InitializingBean, IChargeItemService
{
    public static final int NO_NEED = 0;
    public static final int DEMAND = 1;
    public static final int FORCE = 2;
    private static final Logger logger;
    private static final HashMap<Integer, String> variables;
    private static final HashMap<Integer, Boolean> dynamics;
    private static final HashMap<String, Integer> variablesInverse;
    @Autowired
    private ChargeitemCache chargeItemCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerConstantsDao playerConstantsDao;
    private final TreeMap<Integer, List<Integer>> level2ChargeItems;
    private final TreeMap<Integer, List<Integer>> level2AllChargeItems;
    private List<ChargeItemDto>[] level2Config;
    
    static {
        logger = CommonLog.getLog(ChargeItemService.class);
        variables = new HashMap<Integer, String>();
        dynamics = new HashMap<Integer, Boolean>();
        variablesInverse = new HashMap<String, Integer>();
        ChargeItemService.variables.put(5, "buildingCd");
        ChargeItemService.variables.put(6, "recruitGeneral");
        ChargeItemService.variables.put(7, "refreshGeneralCd");
        ChargeItemService.variables.put(8, "storeBuyItem");
        ChargeItemService.variables.put(9, "refreshStoreCd");
        ChargeItemService.variables.put(10, "buyStorehouseSize");
        ChargeItemService.variables.put(11, "techCd");
        ChargeItemService.variables.put(12, "moveCd");
        ChargeItemService.variables.put(13, "goldRecruit");
        ChargeItemService.variables.put(14, "battleExp1");
        ChargeItemService.variables.put(16, "resourceMode1");
        ChargeItemService.variables.put(17, "resourceMode2");
        ChargeItemService.variables.put(18, "resourceMode3");
        ChargeItemService.variables.put(19, "searchGold");
        ChargeItemService.variables.put(20, "searchExtreme");
        ChargeItemService.variables.put(21, "buySearchNum");
        ChargeItemService.variables.put(22, "politicsEvent");
        ChargeItemService.variables.put(24, "loadGem");
        ChargeItemService.variables.put(25, "unloadGem");
        ChargeItemService.variables.put(26, "shaodaozi");
        ChargeItemService.variables.put(27, "nverhong");
        ChargeItemService.variables.put(28, "zhuyeqing");
        ChargeItemService.variables.put(29, "tradeUp");
        ChargeItemService.variables.put(30, "tradeRefresh");
        ChargeItemService.variables.put(31, "searchRefresh");
        ChargeItemService.variables.put(32, "incenseGold");
        ChargeItemService.variables.put(33, "blackMarketGold");
        ChargeItemService.variables.put(34, "nationalRankBatBuyNumGold");
        ChargeItemService.variables.put(35, "buyBonusArmyGold");
        ChargeItemService.variables.put(36, "quenchingGold");
        ChargeItemService.variables.put(37, "buySlaveCell");
        ChargeItemService.variables.put(38, "slashSlave");
        ChargeItemService.variables.put(39, "slaveFreedom");
        ChargeItemService.variables.put(40, "buyWeaponItem");
        ChargeItemService.variables.put(41, "buyPowerExtraGold");
        ChargeItemService.variables.put(43, "freeConsGold");
        ChargeItemService.variables.put(44, "consDrawingGold");
        ChargeItemService.variables.put(45, "kfDqGold");
        ChargeItemService.variables.put(52, "youdiGold");
        ChargeItemService.variables.put(53, "jiebingGold");
        ChargeItemService.variables.put(54, "chujiGold");
        ChargeItemService.variables.put(59, "createBattleTeam");
        ChargeItemService.variables.put(63, "battleTeamInspire");
        ChargeItemService.variables.put(62, "copperInvest");
        ChargeItemService.variables.put(64, "coverManzuShoumaiCd");
        ChargeItemService.variables.put(72, "teamOrder");
        ChargeItemService.variables.put(73, "updateLash");
        ChargeItemService.variables.put(76, "useGoldOrder");
        ChargeItemService.variables.put(77, "kfzbBuySup");
        ChargeItemService.variables.put(78, "recoverGold");
        ChargeItemService.variables.put(79, "irongive");
        ChargeItemService.variables.put(83, "gemDamo");
        ChargeItemService.variables.put(84, "gemJinglian");
        ChargeItemService.variables.put(85, "buyCrack");
        ChargeItemService.variables.put(86, "farmQuickFinish");
        verify();
        final Set<Map.Entry<Integer, String>> entrySet = ChargeItemService.variables.entrySet();
        for (final Map.Entry<Integer, String> entry : entrySet) {
            ChargeItemService.variablesInverse.put(entry.getValue(), entry.getKey());
        }
    }
    
    public ChargeItemService() {
        this.level2ChargeItems = new TreeMap<Integer, List<Integer>>();
        this.level2AllChargeItems = new TreeMap<Integer, List<Integer>>();
    }
    
    private static void verify() {
        final Collection<String> values = ChargeItemService.variables.values();
        if (values.size() != ChargeItemService.variables.size()) {
            throw new AssertionError("ChargeItemService variables name size error");
        }
        for (final String value : values) {
            int count = 0;
            for (final String other : values) {
                if ((other == value || other.equalsIgnoreCase(value)) && ++count > 1) {
                    throw new AssertionError("ChargeItemService variable name '" + value + "' and other '" + other + "' can't be same!");
                }
            }
            if (count != 1) {
                throw new AssertionError("ChargeItemService variable '" + value + "' name exception!");
            }
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.init();
    }
    
    private void init() {
        final int maxLevel = 11;
        for (final Chargeitem chargeItem : this.chargeItemCache.getModels()) {
            List<Integer> chargeItems = this.level2ChargeItems.get(chargeItem.getLv());
            if (chargeItems == null) {
                chargeItems = new ArrayList<Integer>();
                this.level2ChargeItems.put(chargeItem.getLv(), chargeItems);
            }
            chargeItems.add(chargeItem.getId());
            chargeItem.getLv();
        }
        for (int i = 0; i <= maxLevel; ++i) {
            final List<Integer> allChargeItems = new ArrayList<Integer>();
            for (int j = 0; j <= i; ++j) {
                final List<Integer> chargeItems2 = this.level2ChargeItems.get(j);
                if (chargeItems2 != null) {
                    allChargeItems.addAll(chargeItems2);
                }
            }
            this.level2AllChargeItems.put(i, allChargeItems);
        }
        this.level2Config = new List[maxLevel + 1];
        for (int i = 0; i <= maxLevel; ++i) {
            final List<ChargeItemDto> chargeItemDtoList = new ArrayList<ChargeItemDto>();
            final List<Integer> allChargeItems2 = this.level2AllChargeItems.get(i);
            if (allChargeItems2 != null && !allChargeItems2.isEmpty()) {
                for (final Integer chargeItemId : allChargeItems2) {
                    final String variable = ChargeItemService.variables.get(chargeItemId);
                    if (variable != null) {
                        final ChargeItemDto chargeItemDto = new ChargeItemDto();
                        chargeItemDto.setName(variable);
                        chargeItemDto.setShow(true);
                        final Boolean dynamic = ChargeItemService.dynamics.get(chargeItemId);
                        final Chargeitem chargeItem2 = (Chargeitem)this.chargeItemCache.get((Object)chargeItemId);
                        if (dynamic == null || Boolean.FALSE.equals(dynamic)) {
                            chargeItemDto.setCost(chargeItem2.getCost());
                        }
                        else {
                            chargeItemDto.setDynamic(true);
                        }
                        chargeItemDto.setChargeItemId(chargeItemId);
                        chargeItemDto.setAlert(chargeItem2.getAlert());
                        chargeItemDto.setLv(chargeItem2.getLv());
                        chargeItemDtoList.add(chargeItemDto);
                    }
                    else {
                        ChargeItemService.logger.info("no variable name config for charge item: " + chargeItemId);
                    }
                }
            }
            this.level2Config[i] = chargeItemDtoList;
        }
    }
    
    public List<ChargeItemDto> getConfigByConsumeLv(final int consumeLv) {
        return this.level2Config[consumeLv];
    }
    
    @Override
	public String getConfigByPlayer(final int playerId) {
        final Player player = this.playerDao.read(playerId);
        final List<ChargeItemDto> chargeItemDtoList = this.getConfigByConsumeLv(player.getConsumeLv());
        final int[] array = this.getVipExpressionArray(playerId);
        final Set<Integer> noDisturbSet = new HashSet<Integer>();
        final Set<Map.Entry<Integer, String>> entrySet = ChargeItemService.variables.entrySet();
        for (final Map.Entry<Integer, String> entry : entrySet) {
            if (array[entry.getKey() - 1] == 1) {
                noDisturbSet.add(entry.getKey());
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        for (final ChargeItemDto chargeItemDto : chargeItemDtoList) {
            doc.startObject(chargeItemDto.getName());
            doc.createElement("alert", chargeItemDto.getAlert());
            doc.createElement("show", chargeItemDto.getShow());
            if (chargeItemDto.getDynamic()) {
                doc.createElement("dynamic", chargeItemDto.getDynamic());
            }
            else {
                doc.createElement("cost", chargeItemDto.getCost());
            }
            if (noDisturbSet.contains(chargeItemDto.getChargeItemId())) {
                doc.createElement("noDisturb", true);
            }
            doc.createElement("vipLv", chargeItemDto.getLv());
            doc.endObject();
        }
        doc.endObject();
        return doc.toString();
    }
    
    public static Integer getChargeItemId(final String key) {
        return ChargeItemService.variablesInverse.get(key);
    }
    
    @Transactional
    public int[] getVipExpressionArray(final int playerId) {
        return this.checkVipExpression(playerId);
    }
    
    private int[] checkVipExpression(final int playerId) {
        int[] array = null;
        final PlayerConstants pc = this.playerConstantsDao.read(playerId);
        if (pc.getVipExpression() == null || pc.getVipExpression().length() != ChargeitemCache.size) {
            array = this.parseExpression(pc.getVipExpression(), ChargeitemCache.size);
            final String newExpression = this.getExpression(array);
            if (!newExpression.equals(pc.getVipExpression())) {
                pc.setVipExpression(newExpression);
                this.playerConstantsDao.updateExpression(playerId, newExpression);
            }
        }
        else {
            array = this.parseExpression(pc.getVipExpression(), ChargeitemCache.size);
        }
        return array;
    }
    
    private String getExpression(final int[] array) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
        }
        return builder.toString();
    }
    
    private int[] parseExpression(final String expression, final int size) {
        if (StringUtils.isBlank(expression)) {
            return new int[size];
        }
        int[] array = new int[expression.length()];
        for (int i = 0; i < expression.length(); ++i) {
            array[i] = this.getValue(expression.charAt(i));
        }
        if (array.length < size) {
            array = Arrays.copyOf(array, size);
        }
        else if (array.length > size) {
            array = Arrays.copyOf(array, size);
        }
        return array;
    }
    
    private int getValue(final char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    @Override
	@Transactional
    public byte[] noDisturb(final int playerId, final String[] key, final int[] on) {
        if (key == null || key.length <= 0 || on == null || on.length <= 0 || key.length > on.length) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        for (int i = 0; i < key.length; ++i) {
            final Integer id = getChargeItemId(key[i]);
            if (id != null) {
                this.noDisturb(playerId, id, on[i]);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    public byte[] noDisturb(final int playerId, final int id, final int on) {
        if (on != 0 && on != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10007);
        }
        final Player player = this.playerDao.read(playerId);
        final Chargeitem ci = (Chargeitem)this.chargeItemCache.get((Object)id);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        int[] array = null;
        final PlayerConstants pc = this.playerConstantsDao.read(playerId);
        if (pc.getVipExpression() == null || pc.getVipExpression().length() != ChargeitemCache.size) {
            array = this.parseExpression(pc.getVipExpression(), ChargeitemCache.size);
        }
        else {
            array = this.parseExpression(pc.getVipExpression(), ChargeitemCache.size);
        }
        array[id - 1] = on;
        final String newExpression = this.getExpression(array);
        if (!newExpression.equals(pc.getVipExpression())) {
            pc.setVipExpression(newExpression);
            this.playerConstantsDao.updateExpression(playerId, newExpression);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
