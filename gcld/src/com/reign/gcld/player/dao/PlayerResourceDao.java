package com.reign.gcld.player.dao;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.incense.dao.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.player.common.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerResourceDao")
public class PlayerResourceDao extends BaseDao<PlayerResource> implements IPlayerResourceDao
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerIncenseDao playerIncenseDao;
    @Autowired
    private IResourceUpdateSynService resourceUpdateSynService;
    
    @Override
	public int create(final PlayerResource playerResource) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerResource.create", playerResource);
    }
    
    @Override
	public PlayerResource read(final int playerId) {
        final PlayerResource result = (PlayerResource)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerResource.read", (Object)playerId);
        if (result != null) {
            final PlayerResource pr = new PlayerResource();
            final Resource rs = this.resourceUpdateSynService.getResouce(playerId);
            pr.setCopper(rs.getCopper());
            pr.setExp(result.getExp());
            pr.setFood(rs.getFood());
            pr.setIron(rs.getIron());
            pr.setKfgzVersion(rs.getKfgzVersion());
            pr.setPlayerId(playerId);
            pr.setUpdateTime(new Date(rs.getUpdateTime()));
            pr.setWood(rs.getWood());
            KfgzMatchService.freshPlayerResourceCache(pr);
            return pr;
        }
        return result;
    }
    
    @Override
	public boolean consumeCopper(final int playerId, final int copper, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = -copper;
        rp.attribute = attribute;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public boolean consumeCopperUnconditional(final int playerId, final int copper, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = -copper;
        rp.attribute = attribute;
        rp.canMinus = true;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public void addCopperIgnoreMax(final int playerId, final double copper, final Object attribute, final boolean sendEvent) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = (int)copper;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public void setCopper(final int playerId, final double copper, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = -this.resourceUpdateSynService.getCopper(playerId) + (int)copper;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public boolean consumeWood(final int playerId, final int wood, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.wood = -wood;
        rp.attribute = attribute;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public void addWoodIgnoreMax(final int playerId, final double wood, final Object attribute, final boolean sendEvent) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.wood = (int)wood;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public void setWood(final int playerId, final double wood, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.wood = -this.resourceUpdateSynService.getWood(playerId) + (int)wood;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public boolean consumeFood(final int playerId, final int food, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.food = -food;
        rp.attribute = attribute;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public void addFoodIgnoreMax(final int playerId, final double food, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.food = (int)food;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public void setFood(final int playerId, final double food, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.food = -this.resourceUpdateSynService.getFood(playerId) + (int)food;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public boolean consumeIron(final int playerId, final int iron, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.iron = -iron;
        rp.attribute = attribute;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public void addIronIgnoreMax(final int playerId, final int iron, final Object attribute, final boolean joinActivity) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.iron = iron;
        rp.attribute = attribute;
        rp.joinActivity = joinActivity;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public void setIron(final int playerId, final double iron, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.iron = -this.resourceUpdateSynService.getIron(playerId) + (int)iron;
        rp.attribute = attribute;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public void addExp(final int playerId, final long exp, final Object attribute) {
        final Player player = this.playerDao.read(playerId);
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("exp", exp);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerResource.addExp", params);
        if (affectRows == 1) {
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "exp", exp, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public void setExp(final int playerId, long exp, final Object attribute, final long added) {
        final Player player = this.playerDao.read(playerId);
        exp = exp;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("exp", exp);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerResource.setExp", params);
        if (affectRows == 1) {
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "exp", added, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public void updateResourceCareMax(final int playerId, final List<ResourceDto> list, final Date date, final Object attribute, final PlayerResource pr, final boolean joinActivity) {
        int copperMax = 0;
        int woodMax = 0;
        int foodMax = 0;
        int ironMax = 0;
        int copper = 0;
        int wood = 0;
        int food = 0;
        int iron = 0;
        for (int i = 0; i < list.size(); ++i) {
            final ResourceDto rd = list.get(i);
            if (rd.getType() == 1) {
                copperMax = (int)rd.getMaxValue();
                copper = (int)rd.getValue();
            }
            else if (rd.getType() == 2) {
                woodMax = (int)rd.getMaxValue();
                wood = (int)rd.getValue();
            }
            else if (rd.getType() == 3) {
                foodMax = (int)rd.getMaxValue();
                food = (int)rd.getValue();
            }
            else if (rd.getType() == 4) {
                ironMax = (int)rd.getMaxValue();
                iron = (int)rd.getValue();
            }
        }
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = copper;
        rp.wood = wood;
        rp.food = food;
        rp.iron = iron;
        rp.maxC = copperMax;
        rp.maxW = woodMax;
        rp.maxF = foodMax;
        rp.maxI = ironMax;
        rp.attribute = attribute;
        rp.joinActivity = joinActivity;
        rp.isCareMax = true;
        rp.updateTime = true;
        this.resourceUpdateSynService.updateResource(rp);
    }
    
    @Override
	public boolean addResourceIgnoreMax(final int playerId, final List<ResourceDto> list, final Object attribute, final boolean joinActivity) {
        int copper = 0;
        int wood = 0;
        int food = 0;
        int iron = 0;
        for (int i = 0; i < list.size(); ++i) {
            final ResourceDto rd = list.get(i);
            if (rd.getType() == 1) {
                copper = (int)rd.getValue();
            }
            else if (rd.getType() == 2) {
                wood = (int)rd.getValue();
            }
            else if (rd.getType() == 3) {
                food = (int)rd.getValue();
            }
            else if (rd.getType() == 4) {
                iron = (int)rd.getValue();
            }
        }
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = copper;
        rp.wood = wood;
        rp.food = food;
        rp.iron = iron;
        rp.attribute = attribute;
        rp.joinActivity = joinActivity;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public void pushIncenseData(final int playerId, final int type) {
        if (type < 1 || type > 5) {
            return;
        }
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[16] != '1') {
            return;
        }
        final int pow = (int)Math.pow(2.0, type - 1);
        final int openBit = this.playerIncenseDao.getOpenBit(playerId);
        if ((openBit & pow) != pow) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("showIncense", true);
        doc.createElement("resourceType", type);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Override
	public boolean consumeResource(final int playerId, final int copper, final int food, final int wood, final int iron, final Object attribute) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = -copper;
        rp.food = -food;
        rp.wood = -wood;
        rp.iron = -iron;
        rp.attribute = attribute;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public int updateUpdateTime(final int playerId, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerResource.updateUpdateTime", params);
    }
    
    @Override
	public boolean updateResourceForKfgz(final int playerId, final long copper, final long wood, final long food, final long iron, final long fromVersion, final long toVersion, final String reason) {
        final ResourceParams rp = new ResourceParams();
        rp.playerId = playerId;
        rp.copper = (int)copper;
        rp.wood = (int)wood;
        rp.food = (int)food;
        rp.iron = (int)iron;
        rp.attribute = reason;
        rp.isKF = true;
        rp.fromVersion = fromVersion;
        rp.toVersion = toVersion;
        final int result = this.resourceUpdateSynService.updateResource(rp);
        return result == 1;
    }
    
    @Override
	public int clearKfgzVersion(final int playerId, final long newVersion) {
        this.resourceUpdateSynService.setKfgzVersion(playerId, newVersion);
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("newVersion", newVersion);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerResource.clearKfgzVersion", params);
    }
    
    @Override
	public int resourceUpdate(final int playerId, final Resource rs) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("copper", rs.getC()).addParam("wood", rs.getW()).addParam("food", rs.getF()).addParam("iron", rs.getI()).addParam("kfgzVersion", rs.getKfgzVersion()).addParam("updateTime", new Date(rs.getUpdateTime()));
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerResource.resourceUpdate", params);
    }
    
    @Override
	public PlayerResource get(final int playerId) {
        return (PlayerResource)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerResource.read", (Object)playerId);
    }
}
