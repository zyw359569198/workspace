package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.sdata.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("forceInfoDao")
public class ForceInfoDao extends BaseDao<ForceInfo> implements IForceInfoDao
{
    @Autowired
    private CdExamsCache cdExamsCache;
    
    @Override
	public ForceInfo read(final int forceId) {
        return (ForceInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.read", (Object)forceId);
    }
    
    @Override
	public ForceInfo readForUpdate(final int forceId) {
        return (ForceInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.readForUpdate", (Object)forceId);
    }
    
    @Override
	public List<ForceInfo> getModels() {
        return (List<ForceInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.ForceInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.getModelSize");
    }
    
    @Override
	public int create(final ForceInfo forceInfo) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.ForceInfo.create", forceInfo);
    }
    
    @Override
	public int deleteById(final int forceId) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.ForceInfo.deleteById", forceId);
    }
    
    @Override
	public int updateEndTime(final Date date, final int forceId) {
        final Params params = new Params();
        params.addParam("date", date);
        params.addParam("forceId", forceId);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateEndTime", params);
    }
    
    @Override
	public void updateNationExp(final int forceId, final int maxExp) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("maxExp", maxExp);
        final ForceInfo before = this.read(forceId);
        final int result = this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateNationExp", params);
        if (result >= 1) {
            this.dealTry(before);
        }
    }
    
    @Override
	public void updateIsWin(final Integer forceId, final int i) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("isWin", i);
        this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateIsWin", params);
    }
    
    @Override
	public int updateIsWinAndLvAndExp(final Integer forceId, final int i, final int exp) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("isWin", i);
        params.addParam("exp", exp);
        final ForceInfo before = this.read(forceId);
        final int result = this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateIsWinAndLvAndExp", params);
        if (result >= 1) {
            this.dealTry(before);
        }
        return result;
    }
    
    @Override
	public void updateEndTimeAndIsWin(final Date date, final int forceId, final int i) {
        final Params params = new Params();
        params.addParam("date", date);
        params.addParam("forceId", forceId);
        params.addParam("isWin", i);
        this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateEndTimeAndIsWin", params);
    }
    
    @Override
	public int addShouMaiSum(final int manZuCityId, final int forceId, final int AddNum) {
        switch (manZuCityId) {
            case 251: {
                return this.addBeiDiShouMaiSum(forceId, AddNum);
            }
            case 250: {
                return this.addXiRongShouMaiSum(forceId, AddNum);
            }
            case 252: {
                return this.addDongYiShouMaiSum(forceId, AddNum);
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("cityId is not manzu capital").append("cityId", manZuCityId).appendMethodName("addShouMaiSum").appendClassName("ForceInfoDao").flush();
                return 0;
            }
        }
    }
    
    @Override
	public int addBeiDiShouMaiSum(final int forceId, final int beiDiAddNum) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("beiDiAddNum", beiDiAddNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.addBeiDiShouMaiSum", params);
    }
    
    @Override
	public int addXiRongShouMaiSum(final int forceId, final int xiRongAddNum) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("xiRongAddNum", xiRongAddNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.addXiRongShouMaiSum", params);
    }
    
    @Override
	public int addDongYiShouMaiSum(final int forceId, final int DongYiAddNum) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("DongYiAddNum", DongYiAddNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.addDongYiShouMaiSum", params);
    }
    
    @Override
	public int decreaseLeftCountAndAddQmdAndResetShouMaiSum(final int manZuCityId, final ForceInfo forceInfo, final int qmdLimit) {
        final int forceId = forceInfo.getForceId();
        switch (manZuCityId) {
            case 251: {
                if (forceInfo.getBeidiQinmidu() >= qmdLimit) {
                    if (forceInfo.getBeidiQinmidu() > qmdLimit) {
                        ErrorSceneLog.getInstance().appendErrorMsg("forceInfo.getBeidiQinmidu() error").append("forceInfo.getBeidiQinmidu()", forceInfo.getBeidiQinmidu()).append("qmdLimit", qmdLimit).append("forceId", forceId).flush();
                    }
                    return this.decreaseLeftCountAndResetShouMaiSumOfBeiDi(forceId);
                }
                return this.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfBeiDi(forceId);
            }
            case 250: {
                if (forceInfo.getXirongQinmidu() >= qmdLimit) {
                    if (forceInfo.getXirongQinmidu() > qmdLimit) {
                        ErrorSceneLog.getInstance().appendErrorMsg("forceInfo.getXirongQinmidu() error").append("forceInfo.getXirongQinmidu()", forceInfo.getXirongQinmidu()).append("qmdLimit", qmdLimit).append("forceId", forceId).flush();
                    }
                    return this.decreaseLeftCountAndResetShouMaiSumOfXiRong(forceId);
                }
                return this.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfXiRong(forceId);
            }
            case 252: {
                if (forceInfo.getDongyiQinmidu() >= qmdLimit) {
                    if (forceInfo.getDongyiQinmidu() > qmdLimit) {
                        ErrorSceneLog.getInstance().appendErrorMsg("forceInfo.getDongyiQinmidu() error").append("forceInfo.getDongyiQinmidu()", forceInfo.getDongyiQinmidu()).append("qmdLimit", qmdLimit).append("forceId", forceId).flush();
                    }
                    return this.decreaseLeftCountAndResetShouMaiSumOfDongYi(forceId);
                }
                return this.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfDongYi(forceId);
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("cityId is not manzu capital").append("cityId", manZuCityId).appendMethodName("decreaseLeftCountAndAddQmd").appendClassName("ForceInfoDao").flush();
                return 0;
            }
        }
    }
    
    @Override
	public int decreaseLeftCountAndResetShouMaiSumOfBeiDi(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndResetShouMaiSumOfBeiDi", forceId);
    }
    
    @Override
	public int decreaseLeftCountAndResetShouMaiSumOfXiRong(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndResetShouMaiSumOfXiRong", forceId);
    }
    
    @Override
	public int decreaseLeftCountAndResetShouMaiSumOfDongYi(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndResetShouMaiSumOfDongYi", forceId);
    }
    
    @Override
	public int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfBeiDi(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfBeiDi", forceId);
    }
    
    @Override
	public int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfXiRong(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfXiRong", forceId);
    }
    
    @Override
	public int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfDongYi(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.decreaseLeftCountAndAddQmdAndResetShouMaiSumOfDongYi", forceId);
    }
    
    @Override
	public int resetShouMaiCount(final int countPerDay) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.resetShouMaiCount", countPerDay);
    }
    
    @Override
	public int getMaxLv() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.getMaxLv");
    }
    
    @Override
	public int addIds(final int forceId, final String addedIds) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("addedIds", addedIds);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.addIds", params);
    }
    
    @Override
	public int updateTryInfo(final int forceId, final int id, final Date date, final int stage, final int tryWin, final int generalNum) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("id", id);
        params.addParam("date", date);
        params.addParam("stage", stage);
        params.addParam("tryWin", tryWin);
        params.addParam("generalNum", generalNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateTryInfo", params);
    }
    
    @Override
	public int updateTryWin(final int forceId, final Date date, final int stage) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("date", date);
        params.addParam("stage", stage);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateTryWin", params);
    }
    
    @Override
	public int updateTryFail(final int forceId, final int stage) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("stage", stage);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateTryFail", params);
    }
    
    @Override
	public int addGeneralNum(final int forceId, final int generalNum) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("generalNum", generalNum);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.addGeneralNum", params);
    }
    
    @Override
	public int getGeneralNum(final int forceId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.getGeneralNum", (Object)forceId);
    }
    
    @Override
	public int updateStage(final int forceId, final int stage) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("stage", stage);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateStage", params);
    }
    
    @Override
	public int updatePWin(final int forceId, final int pWin) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("pWin", pWin);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updatePWin", params);
    }
    
    @Override
	public int startPTask(final int forceId, final int pForceId, final int pCityId, final int pId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("pForceId", pForceId);
        params.addParam("pCityId", pCityId);
        params.addParam("pId", pId);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.startPTask", params);
    }
    
    private void dealTry(final ForceInfo before) {
        final int forceId = before.getForceId();
        final ForceInfo after = this.read(forceId);
        final List<Integer> idsList = this.cdExamsCache.getIdList(before.getForceLv(), before.getForceExp(), after.getForceLv(), after.getForceExp());
        if (idsList != null && idsList.size() > 0) {
            final StringBuffer sb = new StringBuffer();
            for (final int id : idsList) {
                sb.append(id);
                sb.append(";");
            }
            this.addIds(forceId, sb.toString());
        }
    }
    
    @Override
	public int updateInvestSum(final int forceId, final long sumAfter) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("sum", sumAfter);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateInvestSum", params);
    }
    
    @Override
	public void updateFarmLv(final int forceId, final int lvAfter) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("lv", lvAfter);
        this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateFarmLv", params);
    }
    
    @Override
	public int updateForceLvAndAddExp(final int forceId, final int lv, final int exp) {
        final Params params = new Params();
        params.addParam("lv", lv);
        params.addParam("exp", exp);
        if (forceId > 0) {
            params.addParam("forceId", forceId);
            return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateForceLvAndAddExp", params);
        }
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateForceLvAndAddExp0", params);
    }
    
    @Override
	public int getMaxExp() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.ForceInfo.getMaxExp");
    }
    
    @Override
	public int updateNationIndivId(final int forceId, final String info) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("info", info);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.ForceInfo.updateNationIndivId", params);
    }
}
