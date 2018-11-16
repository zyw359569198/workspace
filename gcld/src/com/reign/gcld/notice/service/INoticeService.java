package com.reign.gcld.notice.service;

public interface INoticeService
{
    int addNotice(final int p0, final String p1, final String p2, final long p3, final long p4, final int p5);
    
    int modifyNotice(final int p0, final int p1, final String p2, final String p3, final long p4, final long p5, final int p6);
    
    int deleteNotice(final int p0, final String p1);
    
    byte[] getNoticeList(final String p0);
    
    void pushNotice();
}
