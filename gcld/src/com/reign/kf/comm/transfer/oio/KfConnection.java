package com.reign.kf.comm.transfer.oio;

import org.apache.commons.logging.*;
import com.reign.kf.comm.util.*;
import com.reign.kf.comm.transfer.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import com.reign.kf.comm.protocol.*;
import java.io.*;
import java.util.zip.*;
import org.codehaus.jackson.*;
import java.util.*;

public class KfConnection implements IConnection, Protocol
{
    private Log logger;
    private volatile boolean init;
    private Object lock;
    private TransferConfig config;
    private Map<Integer, ResponseHandler> handlerMap;
    private Map<Integer, Tuple<Request, RequestHandler>> requestHandlerMap;
    private List<IActionHandler> afterConnectSuccessActionList;
    private BlockingQueue<Request> sendQueue;
    private List<IRequestMergeStrategy> requestMergeStrategies;
    private volatile boolean connected;
    private volatile boolean cloesd;
    private boolean requestListOnly;
    private boolean stoped;
    private int key;
    private static AtomicInteger iid;
    private ReignSocket socket;
    
    static {
        KfConnection.iid = new AtomicInteger(0);
    }
    
    public KfConnection(final TransferConfig config, final Log logger) {
        this.init = false;
        this.lock = new Object();
        this.connected = false;
        this.cloesd = false;
        this.requestListOnly = false;
        this.stoped = false;
        this.logger = logger;
        this.handlerMap = new HashMap<Integer, ResponseHandler>();
        this.sendQueue = new LinkedBlockingQueue<Request>();
        this.requestHandlerMap = new ConcurrentHashMap<Integer, Tuple<Request, RequestHandler>>();
        this.afterConnectSuccessActionList = new ArrayList<IActionHandler>();
        this.requestMergeStrategies = new ArrayList<IRequestMergeStrategy>();
        this.stoped = false;
        this.key = KfConnection.iid.getAndIncrement();
        this.init(config);
    }
    
    public KfConnection(final TransferConfig config, final Log logger, final String threadName) {
        this.init = false;
        this.lock = new Object();
        this.connected = false;
        this.cloesd = false;
        this.requestListOnly = false;
        this.stoped = false;
        this.logger = logger;
        this.handlerMap = new HashMap<Integer, ResponseHandler>();
        this.sendQueue = new LinkedBlockingQueue<Request>();
        this.requestHandlerMap = new ConcurrentHashMap<Integer, Tuple<Request, RequestHandler>>();
        this.afterConnectSuccessActionList = new ArrayList<IActionHandler>();
        this.requestMergeStrategies = new ArrayList<IRequestMergeStrategy>();
        this.init(config);
        this.requestListOnly = true;
        final SendThread thread = new SendThread(threadName);
        thread.start();
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void registerAfterConnectAction(final IActionHandler actionHandler) {
        synchronized (this.lock) {
            this.afterConnectSuccessActionList.add(actionHandler);
        }
        // monitorexit(this.lock)
    }
    
    public void connect() {
        this.cloesd = false;
        final ConnectThread thread = new ConnectThread();
        thread.start();
    }
    
    public void disconnect() {
        this.cloesd = true;
        try {
            this.socket.disconnect();
        }
        catch (Exception e) {
            this.logger.error("reconnect:disconnect error", e);
        }
        this.logger.info("connection finish");
    }
    
    private boolean reconnect() {
        boolean rtn = false;
        try {
            this.socket.disconnect();
        }
        catch (Exception e) {
            this.logger.error("reconnect:disconnect error", e);
        }
        try {
            this.socket.connect();
            rtn = true;
        }
        catch (Exception e) {
            this.logger.error("reconnect:connect error", e);
        }
        return rtn;
    }
    
    @Override
    public void send(final Request request) {
        if (!this.init) {
            throw new RuntimeException("not init yet");
        }
        this.sendQueue.add(request);
    }
    
    @Override
    public void send(final Request request, final RequestHandler requestHandler) {
        if (!this.init) {
            throw new RuntimeException("not init yet");
        }
        this.sendQueue.add(request);
        this.requestHandlerMap.put(request.getRequestId(), new Tuple<Request, RequestHandler>(request, requestHandler));
    }
    
    @Override
    public void send(final List<Request> requestList) {
        if (!this.init) {
            throw new RuntimeException("not init yet");
        }
        this.sendQueue.addAll((Collection<?>)requestList);
    }
    
    @Override
    public void sendSync(final Request request) {
        this.sendRequest(request);
    }
    
    public void sendHeartBeat() {
        final Request request = new Request();
        request.setCommand(Command.GZ_ALIVE);
        try {
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(request), this);
            }
            // monitorexit(this.lock)
        }
        catch (IOException e) {
            this.logger.error("send heart beat io error", e);
            throw new NetErrorException();
        }
    }
    
    public void sendHeartBeatByList() {
        final Request request = new Request();
        request.setCommand(Command.GZ_ALIVE);
        final List<Request> requestList = new ArrayList<Request>();
        final RequestChunk requestChunk = new RequestChunk();
        requestChunk.setMachineId(this.config.getMachineId());
        requestChunk.setRequestList(requestList);
        try {
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(requestChunk), this);
            }
            // monitorexit(this.lock)
        }
        catch (IOException e) {
            this.logger.error("send heart beat io error", e);
            throw new NetErrorException();
        }
    }
    
    public Response sendSyncAndGetResponse(final Request request) {
        return this.sendRequestAndGetResponse(request);
    }
    
    public Response sendSyncAndGetResponse(final List<Request> requests) {
        return this.sendRequestAndGetResponse(requests);
    }
    
    @Override
    public void sendSync(final List<Request> requestList) {
        this.sendRequest(requestList);
    }
    
    @Override
    public void registerHandler(final int command, final ResponseHandler handler) {
        if (!this.init) {
            throw new RuntimeException("not init yet");
        }
        this.handlerMap.put(command, handler);
    }
    
    @Override
    public void addRequestMergeStrategy(final IRequestMergeStrategy strategy) {
        if (!this.init) {
            throw new RuntimeException("not init yet");
        }
        this.requestMergeStrategies.add(strategy);
    }
    
    private void sendRequest(final Request request) {
        try {
            byte[] bytes = null;
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(request), this);
                bytes = (byte[])this.socket.read(this);
            }
            // monitorexit(this.lock)
            this.processResponse(bytes);
        }
        catch (NetErrorException nee) {
            this.logger.error("send request io NetErrorException", nee);
            this.disconnect();
        }
        catch (IOException e) {
            this.logger.error("send request io error", e);
            throw new NetErrorException();
        }
    }
    
    private Response sendRequestAndGetResponse(final Request request) {
        try {
            byte[] bytes = null;
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(request), this);
                bytes = (byte[])this.socket.read(this);
            }
            // monitorexit(this.lock)
            final Response r = (Response)Types.objectReader(Response.class).readValue(bytes);
            return r;
        }
        catch (NetErrorException nee) {
            this.logger.error("send request io NetErrorException", nee);
            this.disconnect();
            throw new NetErrorException();
        }
        catch (IOException e) {
            this.logger.error("send request io error", e);
            throw new NetErrorException();
        }
    }
    
    private Response sendRequestAndGetResponse(final List<Request> requestList) {
        try {
            final RequestChunk requestChunk = new RequestChunk();
            requestChunk.setMachineId(this.config.getMachineId());
            requestChunk.setRequestList(requestList);
            byte[] bytes = null;
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(requestChunk), this);
                bytes = (byte[])this.socket.read(this);
            }
            // monitorexit(this.lock)
            List<Response> responses = null;
            try {
                responses = (List<Response>)Types.objectReader(Types.JAVATYPE_RESPONSELIST).readValue(bytes);
            }
            catch (Exception e) {
                this.logger.error("jsonerror", e);
            }
            return responses.get(0);
        }
        catch (NetErrorException nee) {
            this.logger.error("send request io NetErrorException", nee);
            this.disconnect();
            throw new NetErrorException();
        }
        catch (IOException e2) {
            this.logger.error("send request io error", e2);
            throw new NetErrorException();
        }
    }
    
    private void sendRequest(final List<Request> requestList) {
        try {
            final RequestChunk requestChunk = new RequestChunk();
            requestChunk.setMachineId(this.config.getMachineId());
            requestChunk.setRequestList(requestList);
            byte[] bytes = null;
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(requestChunk), this);
                bytes = (byte[])this.socket.read(this);
            }
            // monitorexit(this.lock)
            this.processResponseList(bytes);
        }
        catch (IOException e) {
            this.logger.error("send request io error", e);
            this.send(requestList);
            throw new NetErrorException();
        }
    }
    
    private void init(final TransferConfig config) {
        if (this.init) {
            throw new RuntimeException("init already");
        }
        synchronized (this.lock) {
            if (this.init) {
                throw new RuntimeException("init already");
            }
            this.config = config;
            this.socket = new ReignSocket(config.getHost(), config.getPort());
            this.init = true;
        }
        // monitorexit(this.lock)
    }
    
    @Override
    public void send(final OutputStream os, final byte[] body) {
        try {
            byte[] bodyBytes = body;
            if (this.config.compress()) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final GZIPOutputStream dis = new GZIPOutputStream(out);
                try {
                    dis.write(body);
                    dis.finish();
                    dis.close();
                    bodyBytes = out.toByteArray();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (dis != null) {
                        try {
                            dis.close();
                        }
                        catch (IOException ex) {}
                    }
                }
                if (dis != null) {
                    try {
                        dis.close();
                    }
                    catch (IOException ex2) {}
                }
            }
            byte[] commandBytes = this.config.getCommand().getBytes();
            commandBytes = Arrays.copyOf(commandBytes, 32);
            final int dataLen = bodyBytes.length + 36;
            final byte[] lenBytes = this.getLenBytes(4, dataLen);
            final byte[] requestIdBytes = this.getLenBytes(4, 1);
            os.write(lenBytes);
            os.write(commandBytes);
            os.write(requestIdBytes);
            os.write(bodyBytes);
        }
        catch (IOException e2) {
            e2.printStackTrace();
            throw new NetErrorException("write data error", e2);
        }
    }
    
    @Override
    public Object read(final InputStream is) {
        try {
            final byte[] lenBytes = new byte[4];
            final int len = is.read(lenBytes, 0, lenBytes.length);
            if (len == -1 || len != 4) {
                throw new NetErrorException("illegal protocol");
            }
            final int dataLen = this.getInt(lenBytes);
            final byte[] bodyBytes = new byte[dataLen];
            for (int offset = 0; offset < dataLen; offset += is.read(bodyBytes, offset, dataLen - offset)) {}
            if (this.config.compress()) {
                final ByteArrayInputStream bis = new ByteArrayInputStream(bodyBytes);
                final GZIPInputStream dis = new GZIPInputStream(bis);
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final byte[] buff = new byte[4096];
                int i = -1;
                while ((i = dis.read(buff)) != -1) {
                    bos.write(buff, 0, i);
                }
                return bos.toByteArray();
            }
            return bodyBytes;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new NetErrorException("write data error", e);
        }
    }
    
    private void processResponse(final byte[] bytes) {
        try {
            final Response r = (Response)Types.objectReader(Response.class).readValue(bytes);
            final ResponseHandler handler = this.handlerMap.get(r.getCommand());
            handler.handle(r);
        }
        catch (JsonProcessingException e) {
            this.logger.error("process response json error", e);
        }
        catch (IOException e2) {
            this.logger.error("process response io error", e2);
        }
    }
    
    private void processResponseList(final byte[] bytes) {
        try {
            final List<Response> responses = (List<Response>)Types.objectReader(Types.JAVATYPE_RESPONSELIST).readValue(bytes);
            for (final Response r : responses) {
                final Tuple<Request, RequestHandler> tuple = this.requestHandlerMap.remove(r.getResponseId());
                if (tuple != null) {
                    tuple.right.handle(tuple.left, r);
                }
                else {
                    final ResponseHandler handler = this.handlerMap.get(r.getCommand());
                    handler.handle(r);
                }
            }
        }
        catch (JsonProcessingException e) {
            this.logger.error("process response json error", e);
        }
        catch (IOException e2) {
            this.logger.error("process response io error", e2);
        }
    }
    
    private byte[] getLenBytes(final int bitLen, final int bodyLen) {
        final byte[] array = new byte[bitLen];
        array[0] = (byte)(bodyLen >>> 24);
        array[1] = (byte)(bodyLen >>> 16);
        array[2] = (byte)(bodyLen >>> 8);
        array[3] = (byte)(bodyLen >>> 0);
        return array;
    }
    
    public int getInt(final byte[] array) {
        return (array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF) << 0;
    }
    
    public void loop() {
        List<Request> requestList = new ArrayList<Request>();
        int len = 0;
        final int maxSize = this.config.getMaxSendSize();
        Request request = null;
        while ((request = this.sendQueue.poll()) != null) {
            requestList.add(request);
            if (++len > maxSize) {
                break;
            }
        }
        if (requestList.size() <= 0) {
            return;
        }
        for (final IRequestMergeStrategy strategy : this.requestMergeStrategies) {
            requestList = strategy.merge(requestList);
        }
        this.sendRequest(requestList);
    }
    
    public boolean isStoped() {
        return this.stoped;
    }
    
    public void setStoped(final boolean stoped) {
        this.stoped = stoped;
    }
    
    public List<Response> sendRequestAndGetResponseList(final List<Request> requestList) {
        try {
            final RequestChunk requestChunk = new RequestChunk();
            requestChunk.setMachineId(this.config.getMachineId());
            requestChunk.setRequestList(requestList);
            byte[] bytes = null;
            synchronized (this.lock) {
                this.socket.send(Types.OBJECT_MAPPER.writeValueAsBytes(requestChunk), this);
                bytes = (byte[])this.socket.read(this);
            }
            // monitorexit(this.lock)
            List<Response> responses = null;
            try {
                responses = (List<Response>)Types.objectReader(Types.JAVATYPE_RESPONSELIST).readValue(bytes);
            }
            catch (Exception e) {
                this.logger.error("jsonerror", e);
            }
            return responses;
        }
        catch (NetErrorException nee) {
            this.logger.error("send request io NetErrorException", nee);
            this.disconnect();
            throw new NetErrorException();
        }
        catch (IOException e2) {
            this.logger.error("send request io error", e2);
            throw new NetErrorException();
        }
    }
    
    static /* synthetic */ void access$5(final KfConnection kfConnection, final boolean connected) {
        kfConnection.connected = connected;
    }
    
    private class CheckAliveThread extends Thread
    {
        public CheckAliveThread() {
            super("kfgz-CheckAliveThread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted() && !KfConnection.this.cloesd) {
                try {
                    Thread.sleep(KfConnection.this.config.getCheckConnectInterval());
                }
                catch (InterruptedException e) {
                    KfConnection.this.logger.error("kfgz checkAliveThread thread error", e);
                }
                try {
                    KfConnection.this.logger.isDebugEnabled();
                    if (KfConnection.this.requestListOnly) {
                        KfConnection.this.sendHeartBeatByList();
                    }
                    else {
                        KfConnection.this.sendHeartBeat();
                    }
                    KfConnection.this.logger.isDebugEnabled();
                }
                catch (Exception e2) {
                    KfConnection.this.logger.error("kfgz connect off", e2);
                    synchronized (KfConnection.this.lock) {
                        KfConnection.access$5(KfConnection.this, false);
                    }
                    // monitorexit(KfConnection.access$4(this.this$0))
                    final ConnectThread thread = new ConnectThread();
                    thread.start();
                }
            }
        }
    }
    
    private class ConnectThread extends Thread
    {
        public ConnectThread() {
            super("kfgz-connect-gw-Thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted() && !KfConnection.this.cloesd) {
                if (KfConnection.this.logger.isDebugEnabled()) {
                    KfConnection.this.logger.debug("kfgz connect gw start");
                }
                try {
                    if (KfConnection.this.reconnect()) {
                        if (KfConnection.this.logger.isDebugEnabled()) {
                            KfConnection.this.logger.debug("kfgz connect gw success");
                        }
                        synchronized (KfConnection.this.lock) {
                            KfConnection.access$5(KfConnection.this, true);
                            for (final IActionHandler actionHandler : KfConnection.this.afterConnectSuccessActionList) {
                                actionHandler.handler();
                            }
                        }
                        // monitorexit(KfConnection.access$4(this.this$0))
                        final CheckAliveThread thread = new CheckAliveThread();
                        thread.start();
                        return;
                    }
                    KfConnection.this.logger.info("kfgz connect gw fail");
                }
                catch (Exception e) {
                    KfConnection.this.logger.error("kfgz connect gw error", e);
                }
                try {
                    Thread.sleep(KfConnection.this.config.getReonnectInterval());
                }
                catch (InterruptedException e2) {
                    KfConnection.this.logger.error("kfgz connect gw error", e2);
                }
            }
        }
    }
    
    private class SendThread extends Thread
    {
        public SendThread(final String threadName) {
            super(threadName);
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (KfConnection.this.stoped) {
                        return;
                    }
                    KfConnection.this.loop();
                }
                catch (Exception e) {
                    KfConnection.this.logger.error("kf connection loop error", e);
                }
                try {
                    Thread.sleep(KfConnection.this.config.getSendInterval());
                }
                catch (InterruptedException e2) {
                    KfConnection.this.logger.error("kf connection sleep error", e2);
                }
            }
        }
    }
}
