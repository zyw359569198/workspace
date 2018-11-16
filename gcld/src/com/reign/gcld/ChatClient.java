package com.reign.gcld;

import java.net.*;
import org.jboss.netty.buffer.*;
import com.reign.framework.netty.tcp.handler.*;
import java.util.zip.*;
import java.io.*;
import com.reign.framework.netty.util.*;

public class ChatClient
{
    private static Socket socket;
    
    public static void main(final String[] args) throws IOException {
        final InetAddress addr = InetAddress.getByName("127.0.0.1");
        ChatClient.socket = new Socket(addr, 5577);
        final OutputStream os = ChatClient.socket.getOutputStream();
        final InputStream is = ChatClient.socket.getInputStream();
        final Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    final byte[] buff = new byte[20480];
                    int len = -1;
                    try {
                        len = is.read(buff);
                        final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(buff, 0, len);
                        this.print(buffer);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            private void print(final ChannelBuffer buffer) {
                while (buffer.readable()) {
                    final int head = buffer.readInt();
                    final ChannelBuffer bf = ChannelBuffers.dynamicBuffer(head);
                    buffer.readBytes(bf, head);
                    final RequestMessage r = new RequestMessage();
                    r.setCommand(new String(bf.readBytes(32).array()).trim());
                    r.setRequestId(bf.readInt());
                    r.setContent(bf.readBytes(bf.readableBytes()).array());
                    try {
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        final InflaterOutputStream ios = new InflaterOutputStream(bos);
                        ios.write(r.getContent());
                        ios.finish();
                        ios.close();
                        r.setContent(bos.toByteArray());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Command:" + r.getCommand() + ", RequestId:" + r.getRequestId() + ", Echo:" + new String(r.getContent()));
                }
            }
        };
        t.start();
    Label_0049_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        int requestId = 1;
                        System.out.println("\u8bf7\u8f93\u5165\u547d\u4ee4: command param1 param2");
                        final BufferedReader ir = new BufferedReader(new InputStreamReader(System.in));
                        final String line = ir.readLine();
                        final String[] str = line.split(" ");
                        final String command = str[0];
                        final String params = getParams(str);
                        os.write(WrapperUtil.wrapper(command, requestId++, params.getBytes(), false).array());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue Label_0049_Outer;
                }
            }
        }
    }
    
    private static String getParams(final String[] str) {
        if (str.length == 1) {
            return "";
        }
        String param = "";
        int index = 1;
        for (int i = 1; i < str.length; i += 2) {
            if (index != 1) {
                param = String.valueOf(param) + "&";
            }
            param = String.valueOf(param) + str[i] + "=" + str[i + 1];
            ++index;
        }
        return param;
    }
}
