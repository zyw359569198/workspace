package com.reign.kfzb.service;

import com.reign.kfzb.domain.*;
import com.reign.kf.comm.entity.*;
import java.util.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;

public interface IKfzbFeastService
{
    void processLastSeasonInfo(final KfzbSeasonInfoD p0);
    
    KfzbFeastInfo getFeastInfo(final GameServerEntity p0);
    
    Map<Integer, Long> addNewFeastParticipate(final KfzbFeastParticipateInfo p0);
    
    void saveFeastOrganizerInfo(final KfzbFeastOrganizer p0);
    
    void organizerAddFeast(final KfzbFeastOrganizer p0);
    
    KfzbRoomInfoList getRoomInfo(final KfzbRoomKeyList p0);
}
