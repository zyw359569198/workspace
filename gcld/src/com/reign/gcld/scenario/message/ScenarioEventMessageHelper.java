package com.reign.gcld.scenario.message;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.message.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.service.*;
import com.reign.gcld.juben.common.*;

public class ScenarioEventMessageHelper
{
    private static final Logger errorLogger;
    
    static {
        errorLogger = CommonLog.getLog(ScenarioEventMessageHelper.class);
    }
    
    public static void sendMoveToCitykMessage(final int playerId, final int generalId, final int cityId) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageMoveToCity(playerId, generalId, cityId));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendMoveToCitykMessage fail...playerId:" + playerId + "  generalId:" + generalId + "  cityId:" + cityId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendHoldCityScenarioMessage(final int playerId) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageCityHold(playerId));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendHoldCityScenarioMessage fail...playerId:" + playerId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendTujinScenarioMessage(final int playerId) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageTujin(playerId));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendTujinScenarioMessage fail...playerId:" + playerId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendMirageScenarioMessage(final int playerId) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageMirage(playerId));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendMirageScenarioMessage fail...playerId:" + playerId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendCityStateChangeMessage(final int cityId, final int state, final int playerId) {
        HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageCityStateChange(cityId, state, playerId));
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageCityStateChange(cityId, state, playerId));
            if (state == 0 && (cityId == 5 || cityId == 7)) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto dongshanDto = dto.juBenCityDtoMap.get(5);
                    final JuBenCityDto nanshanDto = dto.juBenCityDtoMap.get(7);
                    if (dongshanDto != null && nanshanDto != null) {
                        final int forceId = (cityId == 5) ? dongshanDto.forceId : nanshanDto.forceId;
                        if (dongshanDto.forceId != nanshanDto.forceId && forceId != 0 && dto.grade > 2) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 1003, 0);
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 53) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto huangjin3 = dto.juBenCityDtoMap.get(53);
                    if (huangjin3 != null) {
                        final int forceId2 = huangjin3.forceId;
                        if (forceId2 != 0 && dto.grade > 2) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 4001, 0);
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 65) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto baima = dto.juBenCityDtoMap.get(65);
                    if (baima != null) {
                        final int forceId2 = baima.forceId;
                        if (forceId2 != 0) {
                            final int eventState = ScenarioEventManager.getEventState(Constants.EVENT_ID_ZHUWENCHOU, dto.eventList);
                            if (eventState < 2 && eventState >= 0) {
                                ScenarioEventJsonBuilder.sendDialog(playerId, 5001, 0);
                            }
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 44) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto sishuiguan = dto.juBenCityDtoMap.get(44);
                    if (sishuiguan != null) {
                        final int forceId2 = sishuiguan.forceId;
                        if (forceId2 != 0 && dto.grade >= 4) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 3002, 0);
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 97) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto guanghan = dto.juBenCityDtoMap.get(97);
                    if (guanghan != null) {
                        final int forceId2 = guanghan.forceId;
                        if (forceId2 != 0 && dto.grade >= 1) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 8003, 0);
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 121) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto guanghan = dto.juBenCityDtoMap.get(121);
                    if (guanghan != null) {
                        final int forceId2 = guanghan.forceId;
                        if (forceId2 == dto.player_force_id) {
                            if (dto.grade == 1) {
                                ScenarioEventJsonBuilder.sendDialog(playerId, 10002, 0);
                            }
                            else {
                                ScenarioEventJsonBuilder.sendDialog(playerId, 10003, 0);
                            }
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 122) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto guanghan = dto.juBenCityDtoMap.get(122);
                    if (guanghan != null) {
                        final int forceId2 = guanghan.forceId;
                        if (forceId2 == dto.player_force_id) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 10007, 0);
                        }
                    }
                }
            }
            else if (state == 0 && cityId == 123) {
                final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
                if (dto != null) {
                    final JuBenCityDto guanghan = dto.juBenCityDtoMap.get(123);
                    if (guanghan != null) {
                        final int forceId2 = guanghan.forceId;
                        if (forceId2 == dto.player_force_id && dto.grade >= 2) {
                            ScenarioEventJsonBuilder.sendDialog(playerId, 10015, 0);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendCityStateChangeMessage fail...playerId:" + playerId + "  state:" + state + "  cityId:" + cityId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendEventFinishMessage(final int playerId, final int scenaarioEventId, final int curChoice) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventMessageEventFinish(playerId, scenaarioEventId, curChoice));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendEventFinishMessage fail...playerId:" + playerId + "  id:" + scenaarioEventId);
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
    
    public static void sendTimeTickMessage(final Integer playerId) {
        try {
            HandlerManager.getHandler(ScenarioEventMessage.class).handler(new ScenarioEventTimeTickMessage(playerId));
        }
        catch (Exception e) {
            ScenarioEventMessageHelper.errorLogger.error("sendTimeTickMessage fail...playerId:" + playerId + "  id:");
            ScenarioEventMessageHelper.errorLogger.error(e.getMessage());
            ScenarioEventMessageHelper.errorLogger.error(ScenarioEventMessageHelper.class, e);
        }
    }
}
