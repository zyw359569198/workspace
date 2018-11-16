package com.reign.gcld.mail.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.mail.domain.*;
import java.util.*;

public interface IMailDao extends IBaseDao<Mail>
{
    Mail read(final int p0);
    
    Mail readForUpdate(final int p0);
    
    List<Mail> getModels();
    
    int getModelSize();
    
    int create(final Mail p0);
    
    int deleteById(final int p0);
    
    int saveMail(final Mail p0);
    
    int saveSystemMail(final Mail p0);
    
    List<Mail> getMail(final int p0, final int p1, final int p2);
    
    void readmail(final int p0);
    
    int getMailCount(final int p0, final int p1);
    
    List<Mail> getMailByType(final int p0, final int p1, final int p2);
    
    int getMailCountByType(final int p0, final int p1);
    
    int deleteUserMail(final int p0, final int[] p1);
    
    List<Mail> getDeleteMail(final int p0, final int p1);
    
    int getDeleteMailCount(final int p0);
    
    void clearExpiredMail(final int p0, final int p1);
    
    int haveNewMail(final int p0);
    
    int updateType(final int p0, final int p1);
    
    void updateMailDeleteState(final int p0, final int p1);
    
    int getFirstReceivedMailId(final int p0);
    
    int thoroughDeleteMail(final int p0);
    
    int sendMail(final String p0, final String p1, final String p2, final String p3, final int p4);
    
    int sendMailByForceId(final String p0, final String p1, final String p2, final String p3, final int p4, final int p5);
}
