package com.reign.framework.netty.tcp.handler;

public class RequestMessage
{
    private int requestId;
    private byte[] content;
    private String command;
    private String sessionId;
    
    public RequestMessage() {
    }
    
    public RequestMessage(final int requestId, final String command, final byte[] content) {
        this.requestId = requestId;
        this.content = content;
        this.command = command;
    }
    
    public int getRequestId() {
        return this.requestId;
    }
    
    public void setRequestId(final int requestId) {
        this.requestId = requestId;
    }
    
    public byte[] getContent() {
        return this.content;
    }
    
    public void setContent(final byte[] content) {
        this.content = content;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
