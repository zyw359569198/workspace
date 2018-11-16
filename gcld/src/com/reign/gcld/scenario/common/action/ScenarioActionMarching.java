package com.reign.gcld.scenario.common.action;

import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.scenario.common.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;

public class ScenarioActionMarching extends ScenarioAction
{
    protected int forceId;
    protected int time;
    protected int[] cities;
    protected boolean[] ifPitch;
    protected long nextExcutedTime;
    protected int curCity;
    protected int marchState;
    protected static final int MARCHING = 0;
    protected static final int OCCUPYING = 1;
    protected static final int BLOCKING = 2;
    protected static final int DEAD = 3;
    protected int serial;
    protected boolean isDead;
    protected boolean isFirst;
    protected String pic;
    protected List<Tuple<Integer, Integer>> generalList;
    private static final int DEFAULT_FORCEID = 4;
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public boolean isFirst() {
        return this.isFirst;
    }
    
    public void setFirst(final boolean isFirst) {
        this.isFirst = isFirst;
    }
    
    public boolean isDead() {
        return this.isDead;
    }
    
    public void setDead(final boolean isDead) {
        this.isDead = isDead;
    }
    
    public int getSerial() {
        return this.serial;
    }
    
    public void setSerial(final int serial) {
        this.serial = serial;
    }
    
    public int getMarchState() {
        return this.marchState;
    }
    
    public void setMarchState(final int marchState) {
        this.marchState = marchState;
    }
    
    public int getCurCity() {
        return this.curCity;
    }
    
    public void setCurCity(final int curCity) {
        this.curCity = curCity;
    }
    
    public long getNextExcutedTime() {
        return this.nextExcutedTime;
    }
    
    public void setNextExcutedTime(final long nextExcutedTime) {
        this.nextExcutedTime = nextExcutedTime;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    public int[] getCities() {
        return this.cities;
    }
    
    public void setCities(final int[] cities) {
        this.cities = cities;
    }
    
    public ScenarioActionMarching(final String[] operationSingle) {
        int index = 0;
        for (int i = 1; i < operationSingle.length; ++i) {
            if (operationSingle[i].equalsIgnoreCase("$")) {
                index = i;
                break;
            }
        }
        this.generalList = new ArrayList<Tuple<Integer, Integer>>();
        this.setGeneralNumberMap(operationSingle, index);
        this.pic = operationSingle[index + 1];
        this.forceId = Integer.parseInt(operationSingle[index + 2]);
        this.time = Integer.parseInt(operationSingle[index + 3]);
        this.repeatCirce = 1000L;
        final int size = operationSingle.length - index - 4;
        this.cities = new int[size];
        this.ifPitch = new boolean[size];
        this.curCity = 0;
        for (int j = index + 4, k = 0; j < operationSingle.length; ++j, ++k) {
            this.cities[k] = Integer.parseInt(operationSingle[j]);
            this.ifPitch[k] = false;
        }
        this.nextExcutedTime = 0L;
        this.executeTimesGoal = Integer.MAX_VALUE;
        this.marchState = -1;
        this.serial = 0;
        this.hasMarching = true;
        this.isDead = false;
        this.isFirst = true;
    }
    
    public ScenarioActionMarching() {
    }
    
    private void setGeneralNumberMap(final String[] operationSingle, final int index) {
        Tuple<Integer, Integer> general = null;
        for (int i = 1; i < index; ++i) {
            final String[] single = operationSingle[i].split("-");
            general = new Tuple();
            general.left = Integer.valueOf(single[0]);
            general.right = Integer.valueOf(single[1]);
            this.generalList.add(general);
        }
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        if (this.isFirst) {
            this.changPath(getter, playerId, scenarioEvent);
            this.isFirst = false;
        }
        final long triggerTime = scenarioEvent.getTriggerTime();
        if (this.isDead) {
            this.changeMarchingState(3, playerId);
            this.doAfterDead(getter, playerId, scenarioEvent);
            return;
        }
        if (this.nextExcutedTime == 0L) {
            this.nextExcutedTime = System.currentTimeMillis() + this.time * 1000L;
            return;
        }
        final long now = System.currentTimeMillis();
        if (triggerTime >= now) {
            return;
        }
        final int state = scenarioEvent.getState();
        if (state > 2) {
            return;
        }
        if (this.curCity >= this.cities.length) {
            return;
        }
        final int curCityId = this.cities[this.curCity];
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto == null || juBenDto.juBenCityDtoMap == null || !juBenDto.juBenCityDtoMap.containsKey(curCityId) || juBenDto.state == 0) {
            return;
        }
        int realNpcForceId = JuBenService.changeForceId(juBenDto.player_force_id);
        if (this.forceId != 4) {
            realNpcForceId = this.forceId;
        }
        final int cityId = this.cities[this.cities.length - 1];
        final JuBenCityDto endCityDto = juBenDto.juBenCityDtoMap.get(cityId);
        if (endCityDto == null) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("endCityDto is null..." + cityId);
            return;
        }
        if (endCityDto.forceId == juBenDto.player_force_id) {
            if (juBenDto.juBen_id == 9) {
                scenarioEvent.setState(2);
                this.changeMarchingState(3, playerId);
            }
        }
        else if (endCityDto.forceId == realNpcForceId) {
            this.dealOccupyLastCity(scenarioEvent, getter, juBenDto);
        }
        final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
        final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(curCityId);
        if (realNpcForceId == cityDto.forceId && cityDto.state == 0) {
            if (now < this.nextExcutedTime) {
                this.changeMarchingState(0, playerId);
                return;
            }
            if (this.curCity >= this.cities.length - 1) {
                return;
            }
            final int nextCur = this.curCity + 1;
            final int nextCityId = this.cities[nextCur];
            final JuBenCityDto nextCityDto = juBenDto.juBenCityDtoMap.get(nextCityId);
            if (nextCityDto == null) {
                errorSceneLog.error("nextCityDto is null...cityId:" + nextCityId);
                return;
            }
            final int nextCityForceId = nextCityDto.forceId;
            final SoloRoad soloRoad = getter.getSoloRoadCache().getRoad(juBenDto.juBen_id, curCityId, nextCityId);
            if (soloRoad == null) {
                errorSceneLog.error("curCityId:" + curCityId + " next:" + nextCityForceId);
                return;
            }
            final int roadId = soloRoad.getId();
            if (juBenDto.roadLinked.contains(roadId)) {
                this.changeMarchingState(2, playerId);
                return;
            }
            if (nextCityForceId != 0 && nextCityForceId != juBenDto.player_force_id && nextCityDto.state == 0) {
                this.curCity = nextCur;
                this.changeMarchingInfoNextTime(playerId, this.time);
                this.changeMarchingState(0, playerId);
                return;
            }
            if (this.ifPitch[nextCur]) {
                if (nextCityDto.state == 0 && nextCityForceId != realNpcForceId) {
                    this.isDead = true;
                }
            }
            else {
                this.curCity = nextCur;
                final List<Tuple<Integer, Integer>> mm = this.getAddGeneralList();
                if (mm != null) {
                    getter.getJuBenService().addNpcToCity(playerId, nextCityId, mm, this.forceId);
                }
                this.changeMarchingState(1, playerId);
                this.changeMarchingInfoNextTime(playerId, this.time);
                this.ifPitch[nextCur] = true;
            }
        }
        else if (!this.ifPitch[this.curCity]) {
            final List<Tuple<Integer, Integer>> mm2 = this.getAddGeneralList();
            if (mm2 != null) {
                getter.getJuBenService().addNpcToCity(playerId, curCityId, mm2, this.forceId);
            }
            this.ifPitch[this.curCity] = true;
            this.changeMarchingState(0, playerId);
            this.changeMarchingState(1, playerId);
        }
        else if (cityDto.state == 0) {
            this.isDead = true;
        }
        else {
            this.changeMarchingState(1, playerId);
        }
        if (juBenDto.juBen_id == 10) {
            if (this.ifPitch[this.ifPitch.length - 1]) {
                scenarioEvent.setState(2);
                this.changeMarchingState(3, playerId);
            }
        }
        else if (juBenDto.juBen_id == 11 && curCityId == juBenDto.mengdeLocation) {
            scenarioEvent.jubenFail(getter, playerId, juBenDto);
        }
    }
    
    private void dealOccupyLastCity(final ScenarioEvent scenarioEvent, final IDataGetter getter, final JuBenDto juBenDto) {
        if (juBenDto.juBen_id != 10) {
            scenarioEvent.setState(4);
            scenarioEvent.jubenFail(getter, juBenDto.player_id, juBenDto);
            ScenarioEventJsonBuilder.sendDialog(juBenDto.player_id, 9005, null);
        }
    }
    
    protected void doAfterDead(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
    }
    
    protected List<Tuple<Integer, Integer>> getAddGeneralList() {
        return this.generalList;
    }
    
    private void changPath(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null || juBenDto.juBen_id != 10) {
                return;
            }
            final int jadeBelong = juBenDto.royalJadeBelong;
            if (jadeBelong < 0 || this.forceId == jadeBelong) {
                return;
            }
            final int jadeForceId = (jadeBelong >= 100) ? jadeBelong : 0;
            final int cityId1 = getter.getSoloCityCache().getCapitalCityIdByForceId(juBenDto.juBen_id, jadeForceId);
            final int cityId2 = getter.getSoloCityCache().getCapitalCityIdByForceId(juBenDto.juBen_id, this.forceId);
            final Set<Integer> setTemp = new HashSet<Integer>();
            setTemp.add(cityId2);
            setTemp.add(cityId1);
            for (final JuBenCityDto temp : juBenDto.juBenCityDtoMap.values()) {
                setTemp.add(temp.cityId);
            }
            final int[] arr = new int[setTemp.size()];
            int j = 0;
            for (final Integer key : setTemp) {
                arr[j] = key;
                ++j;
            }
            final List<Integer> cityList = getter.getJuBenDataCache().getMinPath(juBenDto.juBen_id, cityId2, cityId1, arr);
            if (cityList == null || cityList.size() == 0) {
                this.isDead = true;
                return;
            }
            this.cities = new int[cityList.size()];
            this.ifPitch = new boolean[cityList.size()];
            for (int i = 0; i < cityList.size(); ++i) {
                this.cities[i] = cityList.get(i);
                this.ifPitch[i] = false;
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    protected void changeMarchingState(final int ms, final int playerId) {
        try {
            if (ms != this.marchState) {
                this.marchState = ms;
                final int curCityId = this.cities[this.curCity];
                int nextCityId = 0;
                if (this.curCity < this.cities.length - 1) {
                    nextCityId = this.cities[this.curCity + 1];
                }
                final long now = System.currentTimeMillis();
                ScenarioEventJsonBuilder.sendMarchingInfo(this.serial, this.marchState, this.nextExcutedTime - now, curCityId, playerId, nextCityId, this.forceId, this.pic);
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    protected void changeMarchingInfoNextTime(final int playerId, final int addTime) {
        try {
            final long toAddTime;
            final long now = toAddTime = System.currentTimeMillis();
            this.nextExcutedTime = toAddTime + addTime * 1000L;
            final int curCityId = this.cities[this.curCity];
            int nextCityId = 0;
            if (this.curCity < this.cities.length - 1) {
                nextCityId = this.cities[this.curCity + 1];
            }
            ScenarioEventJsonBuilder.sendMarchingInfo(this.serial, this.marchState, this.nextExcutedTime - now, curCityId, playerId, nextCityId, this.forceId, this.pic);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    @Override
    public ScenarioActionMarching clone() throws CloneNotSupportedException {
        final ScenarioActionMarching marching = (ScenarioActionMarching)super.clone();
        marching.ifPitch = new boolean[this.cities.length];
        for (int i = 0; i < marching.ifPitch.length; ++i) {
            marching.ifPitch[i] = this.ifPitch[i];
        }
        return marching;
    }
    
    public void ratioTime(final float number, final int playerId, final String picToChange) {
        if (this.isDead) {
            return;
        }
        if (!StringUtils.isBlank(picToChange) && this.pic.equalsIgnoreCase(picToChange)) {
            final int timeBefore = this.time;
            this.time *= (int)number;
            final int timeAfter = this.time;
            this.changeMarchingInfoNextTime(playerId, timeAfter - timeBefore);
        }
    }
    
    public void appendMarchingInfo(final JsonDocument doc) {
        try {
            final long now = System.currentTimeMillis();
            final int curCityId = this.cities[this.curCity];
            int nextCityId = 0;
            if (this.curCity < this.cities.length - 1) {
                nextCityId = this.cities[this.curCity + 1];
            }
            doc.createElement("curCityId", curCityId);
            doc.createElement("nextCityId", nextCityId);
            if (this.marchState == 0) {
                doc.createElement("nextExcutedTime", this.nextExcutedTime - now);
            }
            doc.createElement("marchState", this.marchState);
            doc.createElement("serial", this.serial);
            doc.createElement("forceId", this.forceId);
            doc.createElement("pic", this.pic);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    public void init(final int serial2) {
        for (int i = 0; i < this.ifPitch.length; ++i) {
            this.ifPitch[i] = false;
        }
        this.serial = serial2;
    }
    
    public void reducePitchNumber(final int generalId, final int number) {
        for (final Tuple<Integer, Integer> tuple : this.generalList) {
            if (tuple.left == generalId) {
                final Tuple<Integer, Integer> tuple2 = tuple;
                tuple2.right = tuple2.right - number;
            }
        }
    }
    
    public void changeMarchingPathTemporary(final int source, final int dest, final String pic) {
        try {
            if (StringUtils.isBlank(pic) || pic.equalsIgnoreCase(this.pic)) {
                int sourceIndex = -1;
                for (int i = 0; i < this.cities.length; ++i) {
                    if (this.cities[i] == source) {
                        sourceIndex = i;
                    }
                }
                if (sourceIndex < this.curCity) {
                    return;
                }
                final int[] tempPath = new int[this.cities.length + 2];
                final boolean[] tempIfPitch = new boolean[tempPath.length];
                final int[] temp = { dest, source };
                for (int j = 0; j < tempPath.length; ++j) {
                    if (j >= sourceIndex + 1 && j <= sourceIndex + 2) {
                        tempPath[j] = temp[j - sourceIndex - 1];
                        tempIfPitch[j] = false;
                    }
                    else if (j > sourceIndex + 2) {
                        tempPath[j] = this.cities[j - 2];
                        tempIfPitch[j] = this.ifPitch[j - 2];
                        this.changeCityMap(tempPath.length + sourceIndex - j, tempPath.length - (j - sourceIndex - 2));
                    }
                    else {
                        tempPath[j] = this.cities[j];
                        tempIfPitch[j] = this.ifPitch[j];
                    }
                }
                this.cities = tempPath;
                this.ifPitch = tempIfPitch;
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorLog = ErrorSceneLog.getInstance();
            errorLog.error(this, e);
        }
    }
    
    protected void changeCityMap(final int source, final int i) {
    }
}
