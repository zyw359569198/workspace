package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;
import com.reign.gcld.player.domain.*;

public class PlayerOccupyCityRanker extends MultiRanker
{
    public PlayerOccupyCityRanker(final IDataGetter dataGetter) {
        super(dataGetter);
    }
    
    @Override
    public void initPlayerAttributes() {
        final List<PlayerOccupyCity> list = this.dataGetter.getPlayerOccupyCityDao().getModels();
        if (list == null || list.size() == 0) {
            return;
        }
        final Map<Integer, Integer> playerInfo = new HashMap<Integer, Integer>();
        for (final PlayerOccupyCity info : list) {
            if (info == null) {
                continue;
            }
            if (playerInfo.containsKey(info.getPlayerId())) {
                int pre = playerInfo.get(info.getPlayerId());
                playerInfo.put(info.getPlayerId(), pre += info.getOccupyCityNum());
            }
            else {
                playerInfo.put(info.getPlayerId(), info.getOccupyCityNum());
            }
        }
        for (final Integer key : playerInfo.keySet()) {
            final int value = playerInfo.get(key);
            final int playerId = key;
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            if (player == null) {
                continue;
            }
            final int playerLv = player.getPlayerLv();
            final ComparableFactor[] arrays = MultiRankData.orgnizeValue(value, 0, playerLv, 0);
            final MultiRankData multiRankData = new MultiRankData(playerId, arrays);
            this.playerAttributes.add(multiRankData);
        }
    }
}
