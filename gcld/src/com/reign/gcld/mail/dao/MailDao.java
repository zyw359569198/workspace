package com.reign.gcld.mail.dao;

import com.reign.gcld.mail.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("mailDao")
public class MailDao extends BaseDao<Mail> implements IMailDao
{
    @Override
	public Mail read(final int id) {
        return (Mail)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.read", (Object)id);
    }
    
    @Override
	public Mail readForUpdate(final int id) {
        return (Mail)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.readForUpdate", (Object)id);
    }
    
    @Override
	public List<Mail> getModels() {
        return (List<Mail>)this.getSqlSession().selectList("com.reign.gcld.mail.domain.Mail.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getModelSize");
    }
    
    @Override
	public int create(final Mail mail) {
        return this.getSqlSession().insert("com.reign.gcld.mail.domain.Mail.create", mail);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.mail.domain.Mail.deleteById", id);
    }
    
    @Override
	public int saveMail(final Mail mail) {
        final int todayMailNum = (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getTodayNum", (Object)mail.getFId());
        if (todayMailNum < 20) {
            return this.create(mail);
        }
        return -1;
    }
    
    @Override
	public int saveSystemMail(final Mail mail) {
        return this.create(mail);
    }
    
    @Override
	public List<Mail> getMail(final int playerId, final int page, final int excludeType) {
        final Params params = new Params();
        final int offset = page * 6;
        params.addParam("id", playerId);
        params.addParam("offset", offset);
        params.addParam("pageCount", 6);
        params.addParam("excludeType", excludeType);
        final List<Mail> resultList = (List<Mail>)this.getSqlSession().selectList("com.reign.gcld.mail.domain.Mail.getMailByPlayerId", (Object)params);
        return resultList;
    }
    
    @Override
	public void readmail(final int mailId) {
        this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.readMail", mailId);
    }
    
    @Override
	public int getMailCount(final int playerId, final int excludeType) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("excludeType", excludeType);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getMailCount", (Object)params);
    }
    
    @Override
	public List<Mail> getMailByType(final int playerId, final int type, final int page) {
        final Params params = new Params();
        final int offset = page * 6;
        params.addParam("id", playerId).addParam("type", type).addParam("offset", offset).addParam("pageCount", 6);
        final List<Mail> resultList = (List<Mail>)this.getSqlSession().selectList("com.reign.gcld.mail.domain.Mail.getMailByType", (Object)params);
        return resultList;
    }
    
    @Override
	public int getMailCountByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("id", playerId);
        params.addParam("type", type);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getMailCountByType", (Object)params);
    }
    
    @Override
	public int deleteUserMail(final int playerId, final int[] mailIdArray) {
        final StringBuilder league = new StringBuilder("(");
        for (int i = 0; i < mailIdArray.length; ++i) {
            if (i == 0) {
                league.append(mailIdArray[i]);
            }
            else {
                league.append(",").append(mailIdArray[i]);
            }
        }
        league.append(")");
        final Params params = new Params();
        params.addParam("id", playerId);
        params.addParam("league", league.toString());
        return this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.deleteUserMail", params);
    }
    
    @Override
	public List<Mail> getDeleteMail(final int playerId, final int page) {
        final Params params = new Params();
        final int offset = page * 6;
        params.addParam("id", playerId);
        params.addParam("offset", offset);
        params.addParam("pageCount", 6);
        final List<Mail> resultList = (List<Mail>)this.getSqlSession().selectList("com.reign.gcld.mail.domain.Mail.getDeleteMail", (Object)params);
        return resultList;
    }
    
    @Override
	public int getDeleteMailCount(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getDeleteMailCount", (Object)playerId);
    }
    
    @Override
	public void updateMailDeleteState(final int mailId, final int isDelete) {
        final Params params = new Params();
        params.addParam("id", mailId);
        params.addParam("isDelete", isDelete);
        this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.updateMailDeleteState", params);
    }
    
    @Override
	public void clearExpiredMail(final int mailType, final int delMailDays) {
        final Date expireDate = new Date(System.currentTimeMillis() - delMailDays * 86400000L);
        final Params params = new Params();
        params.addParam("expireDate", expireDate);
        params.addParam("mailType", mailType);
        this.getSqlSession().delete("com.reign.gcld.mail.domain.Mail.clearExpiredMail", params);
    }
    
    @Override
	public int haveNewMail(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.haveNewMail", (Object)playerId);
    }
    
    @Override
	public int updateType(final int mailId, final int type) {
        final Params params = new Params();
        params.addParam("mailId", mailId);
        params.addParam("type", type);
        return this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.updateType", params);
    }
    
    @Override
	public int getFirstReceivedMailId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.mail.domain.Mail.getFirstReceivedMailId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int thoroughDeleteMail(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.mail.domain.Mail.thoroughDeleteMail", playerId);
    }
    
    @Override
	public int sendMail(final String yx, final String f_name, final String title, final String content, final int playerLv) {
        final Params params = new Params();
        params.addParam("yx", yx);
        params.addParam("f_name", f_name);
        params.addParam("title", title);
        params.addParam("content", content);
        params.addParam("playerLv", playerLv);
        return this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.sendMail", params);
    }
    
    @Override
	public int sendMailByForceId(final String yx, final String f_name, final String title, final String content, final int forceId, final int playerLv) {
        final Params params = new Params();
        params.addParam("yx", yx);
        params.addParam("f_name", f_name);
        params.addParam("title", title);
        params.addParam("content", content);
        params.addParam("forceId", forceId);
        params.addParam("playerLv", playerLv);
        return this.getSqlSession().update("com.reign.gcld.mail.domain.Mail.sendMailByForceId", params);
    }
}
