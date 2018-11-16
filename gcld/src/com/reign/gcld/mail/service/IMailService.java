package com.reign.gcld.mail.service;

import com.reign.gcld.mail.domain.*;
import java.util.*;

public interface IMailService
{
    byte[] getMailByPlayerId(final int p0, final int p1);
    
    byte[] getDeleteMailByPlayerId(final int p0, final int p1);
    
    byte[] getMail(final int p0, final int p1);
    
    byte[] readMail(final int p0, final int p1);
    
    byte[] deleteMail(final int p0, final int p1);
    
    byte[] deleteMailAll(final int p0, final int[] p1);
    
    byte[] thoroughDeleteMail(final int p0);
    
    byte[] writeMail(final Mail p0, final String p1);
    
    void clearDeleteMail();
    
    void writeSystemMail(final String p0, final String p1, final String p2, final int p3, final int p4, final int p5);
    
    void writeSystemMail(final String p0, final String p1, final String p2, final int p3, final int p4, final Date p5);
    
    boolean haveNewMail(final int p0);
    
    byte[] getMailByType(final int p0, final int p1, final int p2);
    
    byte[] saveMail(final int p0, final int p1);
    
    byte[] retrieveMail(final int p0, final int p1);
}
