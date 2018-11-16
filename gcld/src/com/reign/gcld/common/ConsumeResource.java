package com.reign.gcld.common;

import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.task.reward.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

@Component("consumeResource")
public class ConsumeResource
{
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    
    public Tuple<Boolean, String> consumeBase(final int playerId, final Collection<Reward> rewards) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        for (final Reward reward : rewards) {
            if (reward.getType() == 1) {
                if (pr.getCopper() < reward.getNum()) {
                    tuple.right = LocalMessages.T_COMM_10001;
                    return tuple;
                }
                continue;
            }
            else if (reward.getType() == 2) {
                if (pr.getWood() < reward.getNum()) {
                    tuple.right = LocalMessages.T_COMM_10008;
                    return tuple;
                }
                continue;
            }
            else if (reward.getType() == 3) {
                if (pr.getFood() < reward.getNum()) {
                    tuple.right = LocalMessages.T_COMM_10027;
                    return tuple;
                }
                continue;
            }
            else {
                if (reward.getType() == 4 && pr.getIron() < reward.getNum()) {
                    tuple.right = LocalMessages.T_COMM_10026;
                    return tuple;
                }
                continue;
            }
        }
        for (final Reward reward : rewards) {
            if (reward.getType() == 1) {
                this.playerResourceDao.consumeCopper(playerId, reward.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u94f6\u5e01");
            }
            else if (reward.getType() == 2) {
                this.playerResourceDao.consumeWood(playerId, reward.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u6728\u6750");
            }
            else if (reward.getType() == 3) {
                if (reward.getNum() <= 0) {
                    continue;
                }
                this.playerResourceDao.consumeFood(playerId, reward.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u7cae\u98df");
            }
            else {
                if (reward.getType() != 4) {
                    continue;
                }
                this.playerResourceDao.consumeIron(playerId, reward.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u9554\u94c1");
            }
        }
        tuple.left = true;
        return tuple;
    }
}
