package com.reign.gcld.player.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("playerNamesCache")
public class PlayerNamesCache implements InitializingBean
{
    @Autowired
    private IPlayerNameDao playerNameDao;
    private static String[] playerNameList;
    private static int index;
    
    static {
        PlayerNamesCache.playerNameList = new String[10];
        PlayerNamesCache.index = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<String> nameList = this.playerNameDao.getNameList(10);
        for (final String playerName : nameList) {
            this.put(playerName);
        }
    }
    
    public String[] getPlayerNameList() {
        return PlayerNamesCache.playerNameList;
    }
    
    public void put(final String playerName) {
        PlayerNamesCache.playerNameList[PlayerNamesCache.index] = playerName;
        PlayerNamesCache.index = (PlayerNamesCache.index + 1) % 10;
    }
}
