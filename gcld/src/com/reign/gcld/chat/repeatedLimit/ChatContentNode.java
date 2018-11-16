package com.reign.gcld.chat.repeatedLimit;

public class ChatContentNode
{
    private String content;
    private long lastChatTime;
    private int chatTimes;
    private ChatContentNode prev;
    private ChatContentNode next;
    
    public ChatContentNode(final String content) {
        this.content = content;
        this.lastChatTime = System.currentTimeMillis();
        this.chatTimes = 1;
        this.prev = null;
        this.next = null;
    }
    
    public ChatContentNode(final String content, final long lastChatTime) {
        this.content = content;
        this.lastChatTime = lastChatTime;
        this.chatTimes = 1;
        this.prev = null;
        this.next = null;
    }
    
    public long getLastChatTime() {
        return this.lastChatTime;
    }
    
    public void setLastChatTime(final long lastChatTime) {
        this.lastChatTime = lastChatTime;
    }
    
    public int getChatTimes() {
        return this.chatTimes;
    }
    
    public void setChatTimes(final int chatTimes) {
        this.chatTimes = chatTimes;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public ChatContentNode getPrev() {
        return this.prev;
    }
    
    public void setPrev(final ChatContentNode prev) {
        this.prev = prev;
    }
    
    public ChatContentNode getNext() {
        return this.next;
    }
    
    public void setNext(final ChatContentNode next) {
        this.next = next;
    }
    
    public double getSim(final String newContent) {
        return this.sim(this.content, newContent);
    }
    
    private double sim(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == null || cs2 == null) {
            return 0.0;
        }
        CharSequence s1;
        CharSequence s2;
        if (cs1.length() < cs2.length()) {
            s1 = cs2;
            s2 = cs1;
        }
        else {
            s1 = cs1;
            s2 = cs2;
        }
        if (s2.length() == 0) {
            return 0.0;
        }
        final int[] tmpLcs = new int[s2.length()];
        for (int i = 0; i < s2.length(); ++i) {
            tmpLcs[i] = ((s2.charAt(i) == s1.charAt(0)) ? 1 : 0);
        }
        for (int j = 1; j < s1.length(); ++j) {
            int lastV = 0;
            for (int k = 0; k < s2.length(); ++k) {
                int tmp = tmpLcs[k];
                if (k > 0 && tmpLcs[k - 1] > tmp) {
                    tmp = tmpLcs[k - 1];
                }
                if (s2.charAt(k) == s1.charAt(j) && tmp < lastV + 1) {
                    tmp = lastV + 1;
                }
                lastV = tmpLcs[k];
                tmpLcs[k] = tmp;
            }
        }
        int maxLen = 0;
        for (int l = 0; l < s2.length(); ++l) {
            maxLen = Math.max(maxLen, tmpLcs[l]);
        }
        final double rtn = maxLen * 2.0 / (s1.length() + s2.length());
        return rtn;
    }
}
