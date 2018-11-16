package com.reign.gcld.rank.domain;

import com.reign.gcld.battle.common.*;
import java.util.*;

public class NationTaskInfo
{
    public static final int KEY_CITY_ID = 105;
    public static final int CITY_ID_NATION_TASK_1 = 135;
    public static final int CITY_ID_NATION_TASK_2 = 103;
    public static final int CITY_ID_NATION_TASK_3 = 139;
    public static final int CITY_ID_NATION_TASK_4 = 193;
    public static final int CITY_ID_NATION_TASK_5 = 132;
    public static final int CITY_ID_NATION_TASK_6 = 60;
    public static int[] cityArray1;
    public static int[] cityArray2;
    private int state;
    private int serial;
    private Date startTimeDate;
    private Date endTimeDate;
    private int[] cityBelong;
    private int[] templeBelong;
    private boolean canAtt;
    private int attackCityForceId;
    private long[] lastNationKill;
    
    static {
        NationTaskInfo.cityArray1 = new int[] { 135, 103, 139 };
        NationTaskInfo.cityArray2 = new int[] { 193, 132, 60 };
    }
    
    public long[] getLastNationKill() {
        return this.lastNationKill;
    }
    
    public void setLastNationKill(final long[] lastNationKill) {
        this.lastNationKill = lastNationKill;
    }
    
    public int getAttackCityForceId() {
        return this.attackCityForceId;
    }
    
    public void setAttackCityForceId(final int attackCityForceId) {
        this.attackCityForceId = attackCityForceId;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getSerial() {
        return this.serial;
    }
    
    public void setSerial(final int serial) {
        this.serial = serial;
    }
    
    public Date getStartTimeDate() {
        return this.startTimeDate;
    }
    
    public void setStartTimeDate(final Date startTimeDate) {
        this.startTimeDate = startTimeDate;
    }
    
    public Date getEndTimeDate() {
        return this.endTimeDate;
    }
    
    public void setEndTimeDate(final Date endTimeDate) {
        this.endTimeDate = endTimeDate;
    }
    
    public int[] getCityBelong() {
        return this.cityBelong;
    }
    
    public void setCityBelong(final int[] cityBelong) {
        this.cityBelong = cityBelong;
    }
    
    public int[] getTempleBelong() {
        return this.templeBelong;
    }
    
    public void setTempleBelong(final int[] templeBelong) {
        this.templeBelong = templeBelong;
    }
    
    public boolean isCanAtt() {
        return this.canAtt;
    }
    
    public void setCanAtt(final boolean canAtt) {
        this.canAtt = canAtt;
    }
    
    public NationTaskInfo() {
        this.state = 0;
        this.serial = 1;
        this.canAtt = false;
        this.cityBelong = new int[3];
        final int[] cityBelong = this.cityBelong;
        final int n = 0;
        final int[] cityBelong2 = this.cityBelong;
        final int n2 = 1;
        final int[] cityBelong3 = this.cityBelong;
        final int n3 = 2;
        final int n4 = 104;
        cityBelong3[n3] = n4;
        cityBelong[n] = (cityBelong2[n2] = n4);
        this.templeBelong = new int[3];
        final int[] templeBelong = this.templeBelong;
        final int n5 = 0;
        final int[] templeBelong2 = this.templeBelong;
        final int n6 = 1;
        final int[] templeBelong3 = this.templeBelong;
        final int n7 = 2;
        final int n8 = 104;
        templeBelong3[n7] = n8;
        templeBelong[n5] = (templeBelong2[n6] = n8);
        this.attackCityForceId = 104;
        this.lastNationKill = new long[3];
        final long[] lastNationKill = this.lastNationKill;
        final int n9 = 0;
        final long[] lastNationKill2 = this.lastNationKill;
        final int n10 = 1;
        final long[] lastNationKill3 = this.lastNationKill;
        final int n11 = 2;
        final long n12 = 0L;
        lastNationKill3[n11] = n12;
        lastNationKill[n9] = (lastNationKill2[n10] = n12);
    }
    
    public void reload(final Date endtime, final Date startTime) {
        this.state = 0;
        this.serial = 1;
        this.canAtt = false;
        this.cityBelong = new int[3];
        final int[] cityBelong = this.cityBelong;
        final int n = 0;
        final int[] cityBelong2 = this.cityBelong;
        final int n2 = 1;
        final int[] cityBelong3 = this.cityBelong;
        final int n3 = 2;
        final int n4 = 104;
        cityBelong3[n3] = n4;
        cityBelong[n] = (cityBelong2[n2] = n4);
        this.templeBelong = new int[3];
        final int[] templeBelong = this.templeBelong;
        final int n5 = 0;
        final int[] templeBelong2 = this.templeBelong;
        final int n6 = 1;
        final int[] templeBelong3 = this.templeBelong;
        final int n7 = 2;
        final int n8 = 104;
        templeBelong3[n7] = n8;
        templeBelong[n5] = (templeBelong2[n6] = n8);
        this.startTimeDate = startTime;
        this.endTimeDate = endtime;
        this.attackCityForceId = 104;
        final long[] lastNationKill = this.lastNationKill;
        final int n9 = 0;
        final long[] lastNationKill2 = this.lastNationKill;
        final int n10 = 1;
        final long[] lastNationKill3 = this.lastNationKill;
        final int n11 = 2;
        final long n12 = 0L;
        lastNationKill3[n11] = n12;
        lastNationKill[n9] = (lastNationKill2[n10] = n12);
    }
    
    public int getTempleNum() {
        try {
            int result = 0;
            for (int i = 0; i < this.templeBelong.length; ++i) {
                if (this.templeBelong[i] == 104) {
                    ++result;
                }
            }
            return result;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    public int getCityNum(final int forceId, final int serialPar) {
        try {
            final int[] array = (serialPar == 1) ? this.cityBelong : this.templeBelong;
            int result = 0;
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == forceId) {
                    ++result;
                }
            }
            return result;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    public int getRewardCityNum(final int forceId, final int serialPar) {
        try {
            int result = 0;
            for (int i = 0; i < this.cityBelong.length; ++i) {
                if (this.cityBelong[i] == forceId) {
                    ++result;
                }
            }
            if (serialPar == 1) {
                return 0;
            }
            return result;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return 0;
        }
    }
    
    public int getThreeCityInfo(final int forceId) {
        int result = 0;
        for (int i = 0; i < this.cityBelong.length; ++i) {
            if (this.cityBelong[i] == forceId) {
                result += 1 << 2 - i;
            }
        }
        return result;
    }
    
    public List<Integer> getUnOccupiedCity() {
        try {
            final List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < this.templeBelong.length; ++i) {
                if (this.templeBelong[i] == 104) {
                    list.add(NationTaskInfo.cityArray2[i]);
                }
            }
            return list;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return null;
        }
    }
}
