package com.reign.gcld.user.service;

import org.springframework.stereotype.*;
import com.reign.gcld.user.dao.*;
import org.springframework.beans.factory.annotation.*;
import org.apache.commons.lang.*;
import com.reign.framework.json.*;
import com.reign.gcld.user.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.web.*;

@Component("userService")
public class UserService implements IUserService
{
    @Autowired
    private IUserDao userDao;
    
    @Transactional
    @Override
    public byte[] createUser(final String userName, final String password) {
        final String canRegister = Configuration.getProperty("gcld.register.user");
        if (StringUtils.isBlank(canRegister) || !canRegister.trim().equals("1")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10005);
        }
        User user = this.userDao.getUserByUserName(userName);
        if (user != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10001);
        }
        user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setActivate(1);
        user.setActivateCode("");
        user.setAdult(0);
        this.userDao.create(user);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public ThreeTuple<Boolean, User, byte[]> login(final String userName, final String password) {
        final ThreeTuple<Boolean, User, byte[]> tuple = new ThreeTuple<Boolean, User, byte[]>(false, null, null);
        final User user = this.userDao.getUserByUserName(userName);
        if (user == null) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10002);
            return tuple;
        }
        if (!user.getPassword().equals(password)) {
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10003);
            return tuple;
        }
        if (AuthInterceptor.checkBlock(user.getId().toString(), "gcld")) {
            tuple.left = true;
            tuple.middle = user;
            return tuple;
        }
        tuple.middle = user;
        tuple.right = JsonBuilder.getJson(State.SUCCESS, "");
        return tuple;
    }
}
