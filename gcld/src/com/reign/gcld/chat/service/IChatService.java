package com.reign.gcld.chat.service;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.chat.common.*;
import java.util.*;

public interface IChatService
{
    byte[] getBlackList(final PlayerDto p0);
    
    byte[] addBlackName(final PlayerDto p0, final String p1);
    
    byte[] removeBlackName(final PlayerDto p0, final int p1);
    
    String[] initBlackList(final int p0);
    
    byte[] send(final String p0, final PlayerDto p1, final String p2, final String p3);
    
    void sendSystemChat(final String p0, final int p1, final int p2, final String p3, final ChatLink p4);
    
    void sendManWangChat(final String p0, final int p1, final int p2, final String p3, final ChatLink p4);
    
    void sendYxChat(final String p0, final String p1);
    
    void sendBigNotice(final String p0, final PlayerDto p1, final String p2, final Object p3);
    
    void keepSilence(final String p0, final String p1, final int p2, final String p3, final Date p4);
    
    void startTransactional();
    
    void commitTransactional();
    
    void endTransactional();
    
    byte[] SystemOFakene2one(final int p0, final String p1, final int p2, final ChatType p3, final String p4, final ChatLink p5);
    
    byte[] speak(final String p0, final int p1, final String p2, final String p3, final PlayerDto p4);
}
