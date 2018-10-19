package com.zyw.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
public class HttpConnectionPoolUtil {
 
    private static Logger logger = LoggerFactory.getLogger(HttpConnectionPoolUtil.class);
 
    private static final int CONNECT_TIMEOUT = 10*1000;// 设置连接建立的超时时间为10s
    private static final int SOCKET_TIMEOUT = 10*1000;
    private static final int MAX_CONN = 400; // 最大连接数
    private static final int Max_PRE_ROUTE = 80;
    private static final int MAX_ROUTE = 80;
    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    private static PoolingHttpClientConnectionManager manager; //连接池管理类
    private static ScheduledExecutorService monitorExecutor;
 
    private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全
    
    private static CookieStore cookieStore;
    
    public static void setCookieStore(CookieStore cook) {
    	          cookieStore=cook;
    }
 
    /**
     * 对http请求进行基本设置
     * @param httpRequestBase http请求
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();
 
        httpRequestBase.setConfig(requestConfig);
    }
 
    public static CloseableHttpClient getHttpClient(String url){
    	try {
			Thread.sleep((long) (Common.COLLECT_WAIT_TIME * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String hostName = url.split("/")[2];
        System.out.println(hostName);
        int port = 80;
        if (hostName.contains(":")){
            String[] args = hostName.split(":");
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        }
 
        if (httpClient == null){
            //多线程下多个线程同时调用getHttpClient容易导致重复创建httpClient对象的问题,所以加上了同步锁
            synchronized (syncLock){
                if (httpClient == null){
                    httpClient = createHttpClient(hostName, port);
                    //开启监控线程,对异常和空闲线程进行关闭
                    monitorExecutor = Executors.newScheduledThreadPool(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            //关闭异常连接
                            manager.closeExpiredConnections();
                            //关闭5s空闲的连接
                            manager.closeIdleConnections(5*1000, TimeUnit.MILLISECONDS);
                            logger.info("close expired and idle for over 5s connection");
                        }
                    }, 5*1000, 5*1000, TimeUnit.MILLISECONDS);
                }
            }
        }
        return httpClient;
    }
    
	/** 
	* 绕过验证 
	*   
	* @return 
	* @throws NoSuchAlgorithmException  
	* @throws KeyManagementException  
	*/  
	public static SSLContext createIgnoreVerifySSL()  {  
	        SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSLv3");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  

	        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
	        X509TrustManager trustManager = new X509TrustManager() {  
	            @Override  
	            public void checkClientTrusted(  
	                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                    String paramString) {  
	            }  

	            @Override  
	            public void checkServerTrusted(  
	                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                    String paramString) {  
	            }  

	            @Override  
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
	                return null;  
	            }  
	        };  

	        try {
				sc.init(null, new TrustManager[] { trustManager }, null);
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        return sc;  
	    }
 
    /**
     * 根据host和port构建httpclient实例
     * @param host 要访问的域名
     * @param port 要访问的端口
     * @return
     */
    public static CloseableHttpClient createHttpClient(String host, int port){
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        //LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
      //采用绕过验证的方式处理https请求  
		SSLContext sslcontext = createIgnoreVerifySSL();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainSocketFactory)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
 
        manager = new PoolingHttpClientConnectionManager(registry);
        //设置连接参数
        manager.setMaxTotal(MAX_CONN); // 最大连接数
        manager.setDefaultMaxPerRoute(Max_PRE_ROUTE); // 路由最大连接数
 
        HttpHost httpHost = new HttpHost(host, port);
        manager.setMaxPerRoute(new HttpRoute(httpHost), MAX_ROUTE);
 
        //请求失败时,进行请求重试
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3){
                    //重试超过3次,放弃请求
                    logger.error("retry has more than 3 time, give up request");
                    return false;
                }
                if (e instanceof NoHttpResponseException){
                    //服务器没有响应,可能是服务器断开了连接,应该重试
                    logger.error("receive no response from server, retry");
                    return true;
                }
                if (e instanceof SSLHandshakeException){
                    // SSL握手异常
                    logger.error("SSL hand shake exception");
                    return false;
                }
                if (e instanceof InterruptedIOException){
                    //超时
                    logger.error("InterruptedIOException");
                    return false;
                }
                if (e instanceof UnknownHostException){
                    // 服务器不可达
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException){
                    // 连接超时
                    logger.error("Connection Time out");
                    return false;
                }
                if (e instanceof SSLException){
                    logger.error("SSLException");
                    return false;
                }
 
                HttpClientContext context = HttpClientContext.adapt(httpContext);
                HttpRequest request = context.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)){
                    //如果请求不是关闭连接的请求
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient client=null;
        if(cookieStore==null) {
            client = HttpClients.custom().setConnectionManager(manager).setRetryHandler(handler).build();
        }else {
            client = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(manager).setRetryHandler(handler).build();
        }
        return client;
    }
 
    /**
     * 设置post请求的参数
     * @param httpPost
     * @param params
     */
    private static void setPostParams(HttpPost httpPost, Map<String, String> params){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keys = params.keySet();
        for (String key: keys){
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static JsonObject post(String url, Map<String, String> params){
        HttpPost httpPost = new HttpPost(url);
        setRequestConfig(httpPost);
        setPostParams(httpPost, params);
        CloseableHttpResponse response = null;
        JsonObject object = null;
        try {
            response = getHttpClient(url).execute(httpPost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                Gson gson = new Gson();
                object = gson.fromJson(result, JsonObject.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
 
    /**
     * 关闭连接池
     */
    public static void closeConnectionPool(){
        try {
            httpClient.close();
            manager.close();
            monitorExecutor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	HttpGet httpget=null;
    	for(int i =0;i<10;i++) {
    		CloseableHttpClient httpClient=HttpConnectionPoolUtil.getHttpClient("http://www.baidu.com");
    		httpget = new HttpGet("http://www.baidu.com");  
            httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            try {
				HttpResponse response = httpClient.execute(httpget);
				logger.info(EntityUtils.toString(response.getEntity()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	HttpConnectionPoolUtil.closeConnectionPool();
    }
}