package com.reign.gcld.store.service;

import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.tech.service.*;

public interface IEquipService
{
    byte[] getEquipInfo(final PlayerDto p0);
    
    byte[] updateEquip(final PlayerDto p0, final int p1);
    
    byte[] openStoreHouse(final PlayerDto p0, final List<StoreHouse> p1);
    
    byte[] buySTSize(final int p0);
    
    byte[] sellGoods(final PlayerDto p0, final int p1, final int p2);
    
    byte[] openSTBack(final PlayerDto p0);
    
    byte[] buyBackGoods(final PlayerDto p0, final int p1);
    
    byte[] getWearEquip(final PlayerDto p0, final int p1, final int p2);
    
    byte[] changeEquip(final PlayerDto p0, final int p1, final int p2, final int p3, final boolean p4);
    
    byte[] unloadEquip(final PlayerDto p0, final int p1, final int p2, final int p3, final boolean p4);
    
    byte[] preMakeGem(final PlayerDto p0, final int p1);
    
    byte[] makeGem(final PlayerDto p0, final int p1, final int p2);
    
    byte[] unMakeGem(final PlayerDto p0, final int p1);
    
    byte[] getCanUseGeneral(final PlayerDto p0, final int p1);
    
    byte[] useOnGeneral(final PlayerDto p0, final int p1, final int p2, final int p3);
    
    byte[] useIronRewardToken(final PlayerDto p0, final int p1);
    
    byte[] useXiLianToken(final int p0, final int p1, final int p2, final int p3);
    
    byte[] useCreateBuilding(final PlayerDto p0, final int p1);
    
    byte[] updateEquipTen(final PlayerDto p0, final int p1);
    
    byte[] compoundSuit(final PlayerDto p0, final int p1);
    
    byte[] doCompoundSuit(final PlayerDto p0, final int p1);
    
    byte[] demountSuit(final PlayerDto p0, final int p1);
    
    boolean canGetSuit(final int p0, final TechEffectCache p1);
    
    byte[] demountSuitGold(final PlayerDto p0, final int p1);
    
    List<StoreHouse> beforeOpenStoreHouse(final PlayerDto p0);
    
    byte[] compoundProset(final PlayerDto p0, final int p1);
    
    byte[] doCompoundProset(final PlayerDto p0, final int p1);
    
    byte[] demoutProsetGold(final PlayerDto p0, final int p1);
    
    byte[] doDemoutProset(final PlayerDto p0, final int p1);
    
    byte[] useResourceToken(final PlayerDto p0, final int p1);
    
    byte[] bindEquip(final PlayerDto p0, final int p1);
    
    byte[] unbindEquip(final PlayerDto p0, final int p1);
    
    byte[] cancelUnbindEquip(final PlayerDto p0, final int p1);
    
    byte[] getEquipSkillInfo();
}
