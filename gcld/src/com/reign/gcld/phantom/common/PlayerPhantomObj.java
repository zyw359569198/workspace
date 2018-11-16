package com.reign.gcld.phantom.common;

import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;

public class PlayerPhantomObj
{
    public int baoJiType;
    public int baoJiNum;
    public double costCoe;
    public int maxPhantomNum;
    public boolean isAutoOutPut;
    public static final int BAO_JI_TYPE_1 = 1;
    public static final int BAO_JI_TYPE_2 = 2;
    public static final double COST_COE_TYPE_1 = 1.0;
    public static final double COST_COE_TYPE_2 = 0.75;
    public static final int MAX_NUM_TYPE_1 = 30;
    public static final int MAX_NUM_TYPE_2 = 60;
    
    public PlayerPhantomObj() {
        this.baoJiType = 1;
        this.baoJiNum = 1;
        this.costCoe = 1.0;
        this.maxPhantomNum = 30;
        this.isAutoOutPut = false;
    }
    
    public void refreshBaoJiNum(final IDataGetter dataGetter) {
        try {
            final HmPwCrit hmPwCrit = dataGetter.getHmPwCritCache().getRandHmPwCrit(this.baoJiType);
            if (hmPwCrit == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("hmPwCrit is null").append("this.baoJiType", this.baoJiType).appendClassName("PlayerPhantomObj").appendMethodName("refreshBaoJiNum").flush();
                return;
            }
            this.baoJiNum = hmPwCrit.getCrit();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PlayerPhantomObj.refreshBaoJiNum catch Exception", e);
        }
    }
}
