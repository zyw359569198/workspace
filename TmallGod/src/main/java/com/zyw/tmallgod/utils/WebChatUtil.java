package com.zyw.tmallgod.utils;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
public class WebChatUtil {
	
	public static void main(String[] args) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2f557258c5ba09b0&secret=dcaf05f2a005e3199b3e4ab5c1de1f23");
		CloseableHttpResponse response =null;
		try {
		response = httpclient.execute(httpget);
		 HttpEntity entity = response.getEntity();
		 if (entity != null) {
		 long len = entity.getContentLength();
		 if (len != -1 && len < 2048) {
		 System.out.println(EntityUtils.toString(entity));
		 } else {
		 // Stream content out
		 }
		 }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		 try {
			response.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		
	}

}
