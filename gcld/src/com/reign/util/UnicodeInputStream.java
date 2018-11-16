package com.reign.util;

import java.io.*;

public class UnicodeInputStream extends InputStream
{
    PushbackInputStream internalIn;
    boolean isInited;
    String defaultEnc;
    String encoding;
    private static final int BOM_SIZE = 4;
    
    UnicodeInputStream(final InputStream in, final String defaultEnc) {
        this.isInited = false;
        this.internalIn = new PushbackInputStream(in, 4);
        this.defaultEnc = defaultEnc;
    }
    
    public String getDefaultEncoding() {
        return this.defaultEnc;
    }
    
    public String getEncoding() {
        if (!this.isInited) {
            try {
                this.init();
            }
            catch (IOException ex) {
                final IllegalStateException ise = new IllegalStateException("Init method failed.");
                ise.initCause(ise);
                throw ise;
            }
        }
        return this.encoding;
    }
    
    protected void init() throws IOException {
        if (this.isInited) {
            return;
        }
        final byte[] bom = new byte[4];
        final int n = this.internalIn.read(bom, 0, bom.length);
        int unread;
        if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
            this.encoding = "UTF-32BE";
            unread = n - 4;
        }
        else if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
            this.encoding = "UTF-32LE";
            unread = n - 4;
        }
        else if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
            this.encoding = "UTF-8";
            unread = n - 3;
        }
        else if (bom[0] == -2 && bom[1] == -1) {
            this.encoding = "UTF-16BE";
            unread = n - 2;
        }
        else if (bom[0] == -1 && bom[1] == -2) {
            this.encoding = "UTF-16LE";
            unread = n - 2;
        }
        else {
            this.encoding = this.defaultEnc;
            unread = n;
        }
        if (unread > 0) {
            this.internalIn.unread(bom, n - unread, unread);
        }
        this.isInited = true;
    }
    
    @Override
    public void close() throws IOException {
        this.isInited = true;
        this.internalIn.close();
    }
    
    @Override
    public int read() throws IOException {
        this.isInited = true;
        return this.internalIn.read();
    }
}
