package com.zyw.tmallgod.utils;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.zyw.tmallgod.entity.Tkl;
public class WebChatUtil {
	public static void tkTest(String tkl) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("http://api.kfsoft.net/api/tb/tklQuery/v1.php?user_key=f29CfGVPc0STIflw&tkl="+tkl);
		CloseableHttpResponse response =null;
		try {
		response = httpclient.execute(httpget);
		 HttpEntity entity = response.getEntity();
		 if (entity != null) {
		 Gson gson=new Gson();
		 //System.out.println(EntityUtils.toString(entity));
		 Tkl tk=gson.fromJson(EntityUtils.toString(entity), Tkl.class);
		 System.out.println(tk.getMsg());
		 System.out.println(tk.getStatus());
		 System.out.println(tk.getData().getContent());
		 System.out.println(tk.getData().getNative_url());
		 System.out.println(tk.getData().getPic_url());
		 System.out.println(tk.getData().getThumb_pic_url());
		 System.out.println(tk.getData().getTitle());
		 System.out.println(tk.getData().getUrl());
		 System.out.println(tk.getData().getUrl1());
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
	public static void wxTest() {
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
	public static void main(String[] args) {

		tkTest("复制这条信息，打开「手机淘宝」即可查看【超值活动，惊喜活动多多！】€bFCobbNMMfy€");
		/*String tkl="{\"status\":\"success\",\"msg\":\"success\",\"data\":{\"content\":\"\\u8d85\\u503c\\u6d3b\\u52a8\\uff0c\\u60ca\\u559c\\u6d3b\\u52a8\\u591a\\u591a\\uff01\",\"title\":\"\\u6dd8\\u53e3\\u4ee4-\\u9875\\u9762\",\"pic_url\":\"http:\\/\\/img.alicdn.com\\/imgextra\\/TB2.CLgXJAmyKJjSZFKXXXCQXXa-458264020.png\",\"url\":\"https:\\/\\/detail.tmall.com\\/item.htm?id=523284160652&spm=a211b4.24696657&pvid=f86cd266-4d61-4919-be70-a6476f545d1a&scm=1007.12144.69634.9011_8949&sku_properties=-1:-1&ut_sk=1.utdid_24761043_1532922485896.TaoPassword-Outside.isv&sp_tk=4oKsYkZDb2JiTk1NZnnigqw=&visa=13a09278fde22a2e&disablePopup=true&disableSJ=1\",\"url1\":\"http:\\/\\/item.taobao.com\\/item.htm?id=523284160652\",\"native_url\":\"tbopen:\\/\\/m.taobao.com\\/tbopen\\/index.html?action=ali.open.nav&module=h5&h5Url=https%3A%2F%2Fdetail.tmall.com%2Fitem.htm%3Fid%3D523284160652%26spm%3Da211b4.24696657%26pvid%3Df86cd266-4d61-4919-be70-a6476f545d1a%26scm%3D1007.12144.69634.9011_8949%26sku_properties%3D-1%3A-1%26ut_sk%3D1.utdid_24761043_1532922485896.TaoPassword-Outside.isv%26sp_tk%3D4oKsYkZDb2JiTk1NZnnigqw%3D%26visa%3D13a09278fde22a2e%26disablePopup%3Dtrue%26disableSJ%3D1&appkey=24696657&visa=13a09278fde22a2e\",\"thumb_pic_url\":\"http:\\/\\/img.alicdn.com\\/imgextra\\/TB2.CLgXJAmyKJjSZFKXXXCQXXa-458264020.png_170x170.jpg\"}}\r\n" + 
				"";
		 Gson gson=new Gson();
		 System.out.println(tkl);
		 Tkl tk=gson.fromJson(tkl, Tkl.class);
		 System.out.println(tk.getData().getContent());*/
		
	}

}
