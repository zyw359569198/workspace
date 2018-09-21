package com.zyw.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.sun.imageio.plugins.common.ImageUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Utils {
	
	public static String getUTF8StringFromGBKString(String gbkStr) {  
        try {  
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            throw new InternalError();  
        }  
    }  
      
    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {  
        int n = gbkStr.length();  
        byte[] utfBytes = new byte[3 * n];  
        int k = 0;  
        for (int i = 0; i < n; i++) {  
            int m = gbkStr.charAt(i);  
            if (m < 128 && m >= 0) {  
                utfBytes[k++] = (byte) m;  
                continue;  
            }  
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
        }  
        if (k < utfBytes.length) {  
            byte[] tmp = new byte[k];  
            System.arraycopy(utfBytes, 0, tmp, 0, k);  
            return tmp;  
        }  
        return utfBytes;  
    }
	
	public  static void  saveHtml(Configuration configuration,HttpServletRequest request,String htmlFileName,String modelName,Map content) {
		if(Common.IS_GENERATE_HTML) {
			//String htmlRealPath=request.getSession().getServletContext().getRealPath("/")+"\\html\\";
			String htmlRealPath="/usr/local/nginx/html";
			System.out.println("保存的绝对路径是:"+htmlRealPath+ "/" + htmlFileName + ".html");
			 File htmlFile = new File(htmlRealPath + "/" + htmlFileName + ".html");
			 try {
			 if (!htmlFile.exists()) {
				 htmlFile.delete();
			      }
		            // 获得模板对象
		            Template template = configuration .getTemplate(modelName+".ftl");

		            //先得到文件的上级目录，并创建上级目录，在创建文件
		            htmlFile.getParentFile().mkdirs();
		           
		                //创建文件
		                htmlFile.createNewFile();
			            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8"));
			            // 合并输出 创建页面文件
			            content.put("request", request);
			            template.process(content,out);
			            out.flush();
			            out.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            } catch (TemplateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}


	}
	
	public  static void  saveImages(String imageUrl,String imagePath) {
		CloseableHttpClient  httpclient = null;
		String path ="/usr/local/nginx/html"+imagePath;
        File storeFile = null;
		try {
			//采用绕过验证的方式处理https请求  
			SSLContext sslcontext = createIgnoreVerifySSL();
			//设置协议http和https对应的处理socket链接工厂的对象  
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
	            .register("http", PlainConnectionSocketFactory.INSTANCE)  
	            .register("https", new SSLConnectionSocketFactory(sslcontext))  
	            .build();  
	        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
	        HttpClients.custom().setConnectionManager(connManager);
    		httpclient =  HttpClients.custom().setConnectionManager(connManager).build();
            // 创建httpget.    
            HttpGet httpget = new HttpGet(imageUrl);  
            httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            HttpResponse response = httpclient.execute(httpget);
            storeFile=new File(path);
            storeFile.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(storeFile);
            
         // 得到网络资源的字节数组,并写入文件
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    byte b[] = new byte[1024];
                    int j = 0;
                    while( (j = instream.read(b))!=-1){
                        output.write(b,0,j);
                    }
                    output.flush();
                    output.close();
                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    httpget.abort();
                    throw ex;
                } finally {
                    // Closing the input stream will trigger connection release
                    try { instream.close(); } catch (Exception ignore) {}
                }
            }
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/** 
	* 绕过验证 
	*   
	* @return 
	* @throws NoSuchAlgorithmException  
	* @throws KeyManagementException  
	*/  
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {  
	        SSLContext sc = SSLContext.getInstance("SSLv3");  

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

	        sc.init(null, new TrustManager[] { trustManager }, null);  
	        return sc;  
	    }

}
