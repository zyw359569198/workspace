package com.reign.gcld.chat.common;

public class ChatLink
{
    public static final int CHAT_LINK_TYPE_BATTLE = 1;
    public static final int CHAT_LINK_TYPE_CITY = 2;
    public static final int CHAT_LINK_TYPE_BATTLE_VIEW = 3;
    public int type;
    public String params;
    
    public ChatLink() {
    }
    
    public ChatLink(final int type, final String params) {
        this.type = type;
        this.params = params;
    }
}
