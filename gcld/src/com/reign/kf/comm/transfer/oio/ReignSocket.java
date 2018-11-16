package com.reign.kf.comm.transfer.oio;

import java.io.*;
import java.net.*;

public class ReignSocket
{
    public static final int DEFAULT_PORT = 9999;
    public static final int DEFAULT_TIMEOUT = 20000;
    private Socket socket;
    private String host;
    private int port;
    private OutputStream outputStream;
    private InputStream inputStream;
    private int timeout;
    
    public ReignSocket(final String host) {
        this(host, 9999);
    }
    
    public ReignSocket(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.timeout = 20000;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }
    
    public void sendTestPackage() throws IOException {
    }
    
    public void connect() {
        if (!this.isConnected()) {
            try {
                (this.socket = new Socket()).setReuseAddress(true);
                this.socket.setKeepAlive(true);
                this.socket.setTcpNoDelay(true);
                this.socket.setSoLinger(true, 0);
                this.socket.connect(new InetSocketAddress(this.host, this.port), this.timeout);
                this.socket.setSoTimeout(this.timeout);
                this.outputStream = this.socket.getOutputStream();
                this.inputStream = this.socket.getInputStream();
            }
            catch (IOException ex) {
                throw new NetErrorException(ex);
            }
        }
    }
    
    public ReignSocket send(final byte[] bytes, final Protocol protocol) {
        this.connect();
        protocol.send(this.outputStream, bytes);
        return this;
    }
    
    public Object read(final Protocol protocol) {
        this.flush();
        return protocol.read(this.inputStream);
    }
    
    protected void flush() {
        try {
            this.outputStream.flush();
        }
        catch (IOException e) {
            throw new NetErrorException(e);
        }
    }
    
    public boolean isConnected() {
        return this.socket != null && this.socket.isBound() && !this.socket.isClosed() && this.socket.isConnected() && !this.socket.isInputShutdown() && !this.socket.isOutputShutdown();
    }
    
    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.inputStream.close();
                this.outputStream.close();
                if (!this.socket.isClosed()) {
                    this.socket.close();
                }
            }
            catch (IOException ex) {
                throw new NetErrorException(ex);
            }
        }
    }
}
