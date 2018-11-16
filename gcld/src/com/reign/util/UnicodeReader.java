package com.reign.util;

import java.io.*;

public class UnicodeReader extends Reader
{
    PushbackInputStream internalIn;
    InputStreamReader internalIn2;
    String defaultEnc;
    private static final int BOM_SIZE = 4;
    
    public UnicodeReader(final InputStream in, final String defaultEnc) {
        this.internalIn2 = null;
        this.internalIn = new PushbackInputStream(in, 4);
        this.defaultEnc = defaultEnc;
    }
    
    public String getDefaultEncoding() {
        return this.defaultEnc;
    }
    
    public String getEncoding() {
        if (this.internalIn2 == null) {
            return null;
        }
        return this.internalIn2.getEncoding();
    }
    
    protected void init() throws IOException {
        if (this.internalIn2 != null) {
            return;
        }
        final byte[] bom = new byte[4];
        final int n = this.internalIn.read(bom, 0, bom.length);
        String encoding;
        int unread;
        if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
            encoding = "UTF-32BE";
            unread = n - 4;
        }
        else if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
            encoding = "UTF-32LE";
            unread = n - 4;
        }
        else if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
            encoding = "UTF-8";
            unread = n - 3;
        }
        else if (bom[0] == -2 && bom[1] == -1) {
            encoding = "UTF-16BE";
            unread = n - 2;
        }
        else if (bom[0] == -1 && bom[1] == -2) {
            encoding = "UTF-16LE";
            unread = n - 2;
        }
        else {
            encoding = this.defaultEnc;
            unread = n;
        }
        if (unread > 0) {
            this.internalIn.unread(bom, n - unread, unread);
        }
        if (encoding == null) {
            this.internalIn2 = new InputStreamReader(this.internalIn);
        }
        else {
            this.internalIn2 = new InputStreamReader(this.internalIn, encoding);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.init();
        this.internalIn2.close();
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        this.init();
        return this.internalIn2.read(cbuf, off, len);
    }
}
