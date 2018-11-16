package com.reign.gcld.rank.common;

import com.reign.gcld.rank.domain.*;
import java.util.*;

public class PlayerCouponUseRecord
{
    public static Map<Integer, Map<Integer, PlayerCoupon>> playerCouponUseMap;
    
    static {
        PlayerCouponUseRecord.playerCouponUseMap = new HashMap<Integer, Map<Integer, PlayerCoupon>>();
    }
    
    public static void useCoupon(final int playerId, final int vId, final int type) {
        Map<Integer, PlayerCoupon> playerMap = PlayerCouponUseRecord.playerCouponUseMap.get(playerId);
        if (playerMap == null) {
            playerMap = new HashMap<Integer, PlayerCoupon>();
            final PlayerCoupon playerCoupon = new PlayerCoupon();
            playerCoupon.setVid(vId);
            playerCoupon.setPlayerId(playerId);
            playerCoupon.setCouponType(type);
            playerCoupon.setCouponNum(1);
            playerMap.put(type, playerCoupon);
            PlayerCouponUseRecord.playerCouponUseMap.put(playerId, playerMap);
        }
        else {
            final PlayerCoupon typeCoupon = playerMap.get(type);
            if (typeCoupon == null) {
                final PlayerCoupon playerCoupon2 = new PlayerCoupon();
                playerCoupon2.setVid(vId);
                playerCoupon2.setPlayerId(playerId);
                playerCoupon2.setCouponType(type);
                playerCoupon2.setCouponNum(1);
                playerMap.put(type, playerCoupon2);
            }
            else {
                typeCoupon.setCouponNum(typeCoupon.getCouponNum() + 1);
            }
        }
    }
    
    public static int deleteCoupon(final int playerId, final int type) {
        final Map<Integer, PlayerCoupon> playerMap = PlayerCouponUseRecord.playerCouponUseMap.get(playerId);
        if (playerMap == null) {
            return 0;
        }
        final PlayerCoupon playerCoupon = playerMap.get(type);
        if (playerCoupon == null) {
            return 0;
        }
        playerCoupon.setCouponNum(playerCoupon.getCouponNum() - 1);
        if (playerCoupon.getCouponNum() == 0) {
            playerMap.remove(type);
        }
        return playerCoupon.getVid();
    }
    
    public static int inUseNum(final int playerId, final int type) {
        final Map<Integer, PlayerCoupon> playerMap = PlayerCouponUseRecord.playerCouponUseMap.get(playerId);
        if (playerMap == null) {
            return 0;
        }
        final PlayerCoupon playerCoupon = playerMap.get(type);
        if (playerCoupon == null) {
            return 0;
        }
        return playerCoupon.getCouponNum();
    }
}
