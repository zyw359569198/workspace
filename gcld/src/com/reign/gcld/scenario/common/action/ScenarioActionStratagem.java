package com.reign.gcld.scenario.common.action;

import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.player.dto.*;

public class ScenarioActionStratagem extends ScenarioAction implements Cloneable
{
    private int cityId;
    private int stratagemId;
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getStratagemId() {
        return this.stratagemId;
    }
    
    public void setStratagemId(final int stratagemId) {
        this.stratagemId = stratagemId;
    }
    
    public ScenarioActionStratagem(final String[] operationSingle) {
        final int cityId = Integer.parseInt(operationSingle[1]);
        final int stratagemId = Integer.parseInt(operationSingle[2]);
        final int time = Integer.parseInt(operationSingle[3]);
        this.cityId = cityId;
        this.stratagemId = stratagemId;
        this.executeTimesGoal = ((time == 0) ? 1 : Integer.MAX_VALUE);
        this.repeatCirce = time * 1000L;
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        final int scenarioId = dto.juBen_id;
        getter.getCilvilTrickService().useTrickForAction(this.cityId, this.stratagemId, playerId, scenarioId);
        if (scenarioId == 1 && scenarioEvent.getSoloEvent().getId() == 5 && this.stratagemId == 10001 && this.cityId == 0) {
            final SoloDrama drama = (SoloDrama)getter.getSoloDramaCache().get((Object)scenarioId);
            final PlayerDto playerDto = Players.getPlayer(playerId);
            if (drama == null || playerDto == null) {
                return;
            }
            final String jubenName = drama.getName();
            final String msg = MessageFormatter.format(LocalMessages.ZHANGHE_STRATAGEM_INFO, new Object[] { jubenName });
            getter.getChatService().sendSystemChat("SYS2ONE", playerId, playerDto.forceId, msg, null);
            final String pic = "zhanghe";
            final String trickName = ((Stratagem)getter.getStratagemCache().get((Object)this.stratagemId)).getName();
            ScenarioEventJsonBuilder.sendAllTrickInfo(pic, trickName, this.repeatCirce, playerId);
        }
    }
    
    @Override
    public ScenarioActionStratagem clone() throws CloneNotSupportedException {
        return (ScenarioActionStratagem)super.clone();
    }
}
