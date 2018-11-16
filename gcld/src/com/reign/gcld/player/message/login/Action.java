package com.reign.gcld.player.message.login;

public enum Action
{
    LOGIN("LOGIN", 0), 
    LOGINOUT("LOGINOUT", 1), 
    REGISTER("REGISTER", 2);
    
    private Action(final String s, final int n) {
    }
}
