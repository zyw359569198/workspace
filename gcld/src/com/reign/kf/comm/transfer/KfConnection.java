package com.reign.kf.comm.transfer;

import org.apache.commons.logging.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.kf.comm.util.*;
import java.io.*;
import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;

public class KfConnection implements IConnection
{
    private Log logger;
    private volatile boolean init;
    private Object lock;
    private TransferConfig config;
    private List<IRequestMergeStrategy> requestMergeStrategies;
    private BlockingQueue<Request> sendQueue;
    private Map<Integer, ResponseHandler> handlerMap;
    private Map<Integer, Tuple<Request, RequestHandler>> requestHandlerMap;
    
    public KfConnection(final TransferConfig config, final Log logger, final String threadName) {
        this.init = false;
        this.lock = new Object();
        this.logger = logger;
        this.requestMergeStrategies = new ArrayList<IRequestMergeStrategy>();
        this.sendQueue = new LinkedBlockingQueue<Request>();
        this.handlerMap = new HashMap<Integer, ResponseHandler>();
        this.requestHandlerMap = new ConcurrentHashMap<Integer, Tuple<Request, RequestHandler>>();
        this.init(config);
        final SendThread thread = new SendThread(threadName);
        thread.start();
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
            this.init = true;
        }
        // monitorexit(this.lock)
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
    
    private void sendRequest(final List<Request> requestList) {
        try {
            final RequestChunk requestChunk = new RequestChunk();
            requestChunk.setMachineId(this.config.getMachineId());
            requestChunk.setRequestList(requestList);
            final String response = HttpUtil.postByGzipedJSON(this.config.getMatchServerUrl(), requestChunk, Types.OBJECT_MAPPER);
            this.logger.info(response);
            this.processResponseList(response);
        }
        catch (IOException e) {
            this.logger.error("send request io error", e);
            this.send(requestList);
        }
    }
    
    private void sendRequest(final Request request) {
        try {
            final String response = HttpUtil.postByGzipedJSON(this.config.getMatchServerUrl(), request, Types.OBJECT_MAPPER);
            this.logger.info(response);
            this.processResponse(response);
        }
        catch (IOException e) {
            throw new RuntimeException("send request error", e);
        }
    }
    
    private void processResponseList(final String response) {
        try {
            final List<Response> responses = (List<Response>)Types.objectReader(Types.JAVATYPE_RESPONSELIST).readValue(response);
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
    
    private void processResponse(final String response) {
        try {
            final Response r = (Response)Types.objectReader(Response.class).readValue(response);
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
    
    private class SendThread extends Thread
    {
        public SendThread(final String threadName) {
            super(threadName);
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
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
