package com.reign.gcld.chat.repeatedLimit;

public class ChatContentNodeList
{
    public static final int NEW = 1;
    public static final int REPEATED = 2;
    public static final int LIMIT = 3;
    private ChatContentNode header;
    private ChatContentNode footer;
    private int count;
    private int maxCount;
    private long expireTime;
    private int maxRepeatTimes;
    private double minSimPoint;
    
    public ChatContentNodeList(final int maxCount, final long expireTime, final int maxRepeatTimes, final double minSimPoint) {
        this.header = new ChatContentNode("");
        this.footer = new ChatContentNode("");
        this.header.setNext(this.footer);
        this.footer.setPrev(this.header);
        this.count = 0;
        this.maxCount = maxCount;
        this.expireTime = expireTime;
        this.maxRepeatTimes = maxRepeatTimes;
        this.minSimPoint = minSimPoint;
    }
    
    public int checkChatContent(final String chatContent) {
        int rtn = 1;
        final long now = System.currentTimeMillis();
        for (ChatContentNode node = this.header.getNext(); node != this.footer; node = node.getNext()) {
            if (now - node.getLastChatTime() > this.expireTime) {
                this.removeChatContent(node);
            }
            else if (node.getSim(chatContent) > this.minSimPoint) {
                if (node.getChatTimes() >= this.maxRepeatTimes) {
                    rtn = 3;
                }
                else {
                    node.setChatTimes(node.getChatTimes() + 1);
                    rtn = 2;
                }
                node.setLastChatTime(now);
                this.removeChatContent(node);
                this.addChatContent(node);
                break;
            }
        }
        if (rtn == 1) {
            this.addChatContent(chatContent, now);
        }
        return rtn;
    }
    
    private void addChatContent(final ChatContentNode node) {
        final ChatContentNode privNode = this.footer.getPrev();
        node.setNext(this.footer);
        node.setPrev(privNode);
        privNode.setNext(node);
        this.footer.setPrev(node);
        ++this.count;
        if (this.count > this.maxCount) {
            this.removeChatContent(this.header.getNext());
        }
    }
    
    public void addChatContent(final String str, final long timestamp) {
        final ChatContentNode curr = new ChatContentNode(str, timestamp);
        final ChatContentNode privNode = this.footer.getPrev();
        curr.setNext(this.footer);
        curr.setPrev(privNode);
        privNode.setNext(curr);
        this.footer.setPrev(curr);
        ++this.count;
        if (this.count > this.maxCount) {
            this.removeChatContent(this.header.getNext());
        }
    }
    
    public void removeChatContent(final ChatContentNode node) {
        if (node == this.header || node == this.footer) {
            return;
        }
        final ChatContentNode prevNode = node.getPrev();
        final ChatContentNode nextNode = node.getNext();
        if (prevNode == null || nextNode == null) {
            return;
        }
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        --this.count;
    }
}
