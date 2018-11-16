package com.reign.util.characterFilter.dataGetter;

import com.reign.util.log.*;
import sun.net.ftp.*;
import java.io.*;
import sun.net.*;

public class FtpDataGetter implements IDataGetter
{
    Logger logger;
    private String ip;
    private int port;
    private String userName;
    private String password;
    private String fileName;
    
    public FtpDataGetter(final String ip, final int port, final String userName, final String password, final String fileName) {
        this.logger = CommonLog.getLog(FtpDataGetter.class);
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.fileName = fileName;
    }
    
    public FtpDataGetter(final String ip, final String userName, final String password, final String fileName) {
        this.logger = CommonLog.getLog(FtpDataGetter.class);
        this.ip = ip;
        this.port = 21;
        this.userName = userName;
        this.password = password;
        this.fileName = fileName;
    }
    
    public String getIp() {
        return this.ip;
    }
    
    public void setIp(final String ip) {
        this.ip = ip;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    @Override
    public BufferedReader getData() {
        FtpClient ftpClient = null;
        BufferedReader br = null;
        try {
            ftpClient = new FtpClient(this.ip, this.port);
            ftpClient.login(this.userName, this.password);
            ftpClient.binary();
            final TelnetInputStream ftpIn = ftpClient.get(this.fileName);
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            final byte[] buf = new byte[204800];
            int buffsize = 0;
            while ((buffsize = ftpIn.read(buf, 0, buf.length)) != -1) {
                byteOut.write(buf, 0, buffsize);
            }
            final byte[] byteArray = byteOut.toByteArray();
            byteOut.close();
            ftpIn.close();
            ftpClient.sendServer("QUIT\r\n");
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArray), "UTF-8"));
        }
        catch (UnsupportedEncodingException usee) {
            this.logger.error("\u6587\u4ef6\u7f16\u7801\u9519\u8bef!", usee);
        }
        catch (IOException ioe) {
            this.logger.error("\u4eceftp\u8bfb\u53d6\u6587\u4ef6\u9519\u8bef!", ioe);
        }
        return br;
    }
}
