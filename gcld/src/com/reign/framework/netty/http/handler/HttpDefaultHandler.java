package com.reign.framework.netty.http.handler;

import org.apache.commons.logging.*;
import java.net.*;
import java.util.regex.*;
import java.nio.channels.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.http.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import java.io.*;
import com.reign.framework.netty.util.*;
import org.jboss.netty.handler.stream.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;
import java.util.*;
import org.jboss.netty.handler.codec.http.*;
import java.util.concurrent.*;

public class HttpDefaultHandler extends SimpleChannelUpstreamHandler
{
    private static final Log log;
    private final Servlet servlet;
    private final ServletContext sc;
    public ChunkedWriteHandler chunkedWriteHandler;
    private static final Pattern pattern;
    
    static {
        log = LogFactory.getLog(HttpDefaultHandler.class);
        pattern = Pattern.compile("^/root/([\\w-/]*)\\.action([\\s\\S]*)?$");
    }
    
    public static void main(final String[] args) throws UnsupportedEncodingException {
        final String a = "/root/abc/gateway.action?abc=122.abc";
        final Matcher match = HttpDefaultHandler.pattern.matcher(a);
        match.find();
        System.out.println(URLEncoder.encode("=", "UTF-8"));
    }
    
    public HttpDefaultHandler(final Servlet servlet, final ServletContext sc) {
        this.chunkedWriteHandler = new ChunkedWriteHandler();
        this.sc = sc;
        this.servlet = servlet;
    }
    
    @Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        if (!(e.getCause() instanceof ClosedChannelException)) {
            HttpDefaultHandler.log.error("http channel error, channel[id:" + e.getChannel().getId() + ", interestOps:" + e.getChannel().getInterestOps() + ", bound:" + e.getChannel().isBound() + ", connected:" + e.getChannel().isConnected() + ", open:" + e.getChannel().isOpen() + ", readable:" + e.getChannel().isReadable() + ", writable:" + e.getChannel().isWritable() + "]", e.getCause());
        }
    }
    
    @Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            try {
                final HttpRequest httpRequest = (HttpRequest)msg;
                if (httpRequest.getUri().equalsIgnoreCase("/crossdomain.xml")) {
                    final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.setContent(ChannelBuffers.copiedBuffer(ServerConstants.CROSSDOMAIN));
                    response.setHeader("Content-Type", "text/plain;charset=UTF_8");
                    e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                if (httpRequest.getUri().equalsIgnoreCase("/test.html")) {
                    final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.setContent(ChannelBuffers.copiedBuffer(this.getHtml("test.html")));
                    response.setHeader("Content-Type", "text/html;charset=UTF_8");
                    e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                final String uri = httpRequest.getUri();
                final Matcher matcher = HttpDefaultHandler.pattern.matcher(uri);
                if (!matcher.find()) {
                    final HttpResponse response2 = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY);
                    e.getChannel().write(response2).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                final String command = matcher.group(1);
                final Tuple<byte[], byte[]> requestContent = HttpUtil.getRequestContent(httpRequest);
                final Map<String, Cookie> cookies = HttpUtil.getCookies(httpRequest);
                final Map<String, String> headers = HttpUtil.getHeaders(httpRequest);
                final Response response3 = new HttpRespone(e.getChannel());
                final Request request = new com.reign.framework.netty.http.HttpRequest(ctx, this.sc, e.getChannel(), requestContent.left, requestContent.right, command, cookies, headers, response3);
                ((com.reign.framework.netty.http.HttpRequest)request).setUrl(uri);
                response3.onWriteChunk(new ChunkAction<byte[]>() {
                    @Override
                    public synchronized void invoke(final byte[] result) {
                        HttpDefaultHandler.this.writeChunk(request, response3, ctx, httpRequest, result);
                    }
                });
                if (!request.isHttpLong()) {
                    this.servlet.service(request, response3);
                    if (!response3.isChunk()) {
                        this.doResponse(ctx, request, response3, httpRequest);
                    }
                }
            }
            catch (Exception ex) {
                HttpDefaultHandler.log.error("handle http request error", ex);
                final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
    
    private byte[] getHtml(final String string) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final FileInputStream fis = new FileInputStream(new File("C:\\test.html"));
            final byte[] buff = new byte[1024];
            int len = -1;
            while ((len = fis.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return bos.toByteArray();
    }
    
    protected void writeChunk(final Request request, final Response response, final ChannelHandlerContext ctx, final HttpRequest httpRequest, final byte[] chunk) {
        try {
            if (response.getDirect() == null) {
                response.addHeader("Transfer-Encoding", "chunked");
                response.setDirect(new LazyChunkedInput());
                this.doResponse(ctx, request, response, httpRequest);
            }
            ((LazyChunkedInput)response.getDirect()).writeChunk(chunk);
            final ChunkedWriteHandler handler = (ChunkedWriteHandler)ctx.getChannel().getPipeline().get("chunkedWriter");
            handler.resumeTransfer();
        }
        catch (Exception e) {
            HttpDefaultHandler.log.error("writeChunk error", e);
        }
    }
    
    private void doResponse(final ChannelHandlerContext ctx, final Request request, final Response response, final HttpRequest httpRequest) throws Exception {
        final HttpResponse nettyResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        nettyResponse.setHeader("Content-Type", WrapperUtil.getContentType());
        final boolean keepAlive = isKeepAlive((HttpMessage)httpRequest);
        if (keepAlive && httpRequest.getProtocolVersion().equals((Object)HttpVersion.HTTP_1_0)) {
            nettyResponse.setHeader("Connection", "Keep-Alive");
        }
        addHeadAndCookieToResponse(response, nettyResponse);
        final Object obj = response.getDirect();
        ChunkedInput stream = null;
        if (obj instanceof ChunkedInput) {
            stream = (ChunkedInput)obj;
        }
        if (stream != null) {
            ChannelFuture writeFuture = ctx.getChannel().write(nettyResponse);
            if (!httpRequest.getMethod().equals((Object)HttpMethod.HEAD) && !nettyResponse.getStatus().equals(HttpResponseStatus.NOT_MODIFIED)) {
                writeFuture = ctx.getChannel().write(stream);
            }
            else {
                stream.close();
            }
            if (!keepAlive) {
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
        else {
            writeResponse(ctx, response, nettyResponse, httpRequest);
        }
    }
    
    protected static void writeResponse(final ChannelHandlerContext ctx, final Response response, final HttpResponse httpResponse, final HttpRequest httpRequest) {
        byte[] content = null;
        final boolean keepAlive = isKeepAlive((HttpMessage)httpRequest);
        if (httpRequest.getMethod().equals((Object)HttpMethod.HEAD)) {
            content = new byte[0];
        }
        else {
            content = response.getContent();
        }
        final ChannelBuffer buf = ChannelBuffers.wrappedBuffer(content);
        httpResponse.setContent(buf);
        setContentLength(httpResponse, content.length);
        final ChannelFuture f = ctx.getChannel().write(httpResponse);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    public void closeChunked(final Request request, final Response response, final ChannelHandlerContext ctx, final HttpRequest httpRequest) {
        try {
            ((LazyChunkedInput)response.getDirect()).close();
            final ChunkedWriteHandler handler = (ChunkedWriteHandler)ctx.getChannel().getPipeline().get("chunkedWriter");
            handler.resumeTransfer();
        }
        catch (Exception e) {
            HttpDefaultHandler.log.error("close chunk error", e);
        }
    }
    
    private static void setContentLength(final HttpResponse httpResponse, final int contentLength) {
        httpResponse.setHeader("Content-Length", String.valueOf(contentLength));
    }
    
    protected static void addHeadAndCookieToResponse(final Response response, final HttpResponse nettyResponse) {
        final Map<String, String> headers = response.getHeaders();
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                nettyResponse.setHeader(entry.getKey(), entry.getValue());
            }
        }
        final Map<String, Cookie> cookies = response.getCookies();
        if (cookies != null) {
            for (final Map.Entry<String, Cookie> entry2 : cookies.entrySet()) {
                final CookieEncoder encoder = new CookieEncoder(true);
                encoder.addCookie(entry2.getValue());
                nettyResponse.addHeader("Set-Cookie", encoder.encode());
            }
        }
        if (headers != null && !headers.containsKey("Cache-Control") && !headers.containsKey("Expires")) {
            nettyResponse.setHeader("Cache-Control", "no-cache");
        }
        nettyResponse.setStatus(response.getStatus());
    }
    
    public static boolean isKeepAlive(final HttpMessage message) {
        return HttpHeaders.isKeepAlive(message);
    }
    
    static class LazyChunkedInput implements ChunkedInput
    {
        private boolean closed;
        private ConcurrentLinkedQueue<ChannelBuffer> nextChunks;
        
        LazyChunkedInput() {
            this.closed = false;
            this.nextChunks = new ConcurrentLinkedQueue<ChannelBuffer>();
        }
        
        @Override
		public boolean hasNextChunk() throws Exception {
            return !this.nextChunks.isEmpty();
        }
        
        @Override
		public Object nextChunk() throws Exception {
            if (this.nextChunks.isEmpty()) {
                return null;
            }
            return this.nextChunks.poll();
        }
        
        @Override
		public boolean isEndOfInput() throws Exception {
            return this.closed && this.nextChunks.isEmpty();
        }
        
        @Override
		public void close() throws Exception {
            if (!this.closed) {
                this.nextChunks.offer(WrapperUtil.wrapperChunk(WrapperUtil.EMPTY_BYTE));
            }
            this.closed = true;
        }
        
        public void writeChunk(final byte[] chunk) throws Exception {
            this.nextChunks.offer(WrapperUtil.wrapperChunk(chunk));
        }
    }
}
