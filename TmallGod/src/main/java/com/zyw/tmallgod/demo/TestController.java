package com.zyw.tmallgod.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping(value="/print",method=RequestMethod.POST)
	@ResponseBody
	public String printTest(HttpServletRequest request,HttpServletResponse response) {
		/*
		 * 该部分我们获取用户发送的信息，并且解析成<K,V>的形式进行显示
		 */
		// 解析用户发送过来的信息
		InputStream is = null;
		try {
			is = request.getInputStream();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}// 拿取请求流
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		// 解析xml，将获取到的返回结果xml进行解析成我们习惯的文字信息
		SAXReader reader = new SAXReader();// 第三方jar:dom4j【百度：saxreader解析xml】
		Document document = null;
		try {
		 document = reader.read(is);
		} catch (DocumentException e1) {
		 // TODO Auto-generated catch block
		 e1.printStackTrace();
		}
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		 
		// 遍历所有子节点
		for (Element e : elementList)
		 map.put(e.getName(), e.getText());
		 
		// 测试输出
		Set<String> keySet = map.keySet();
		// 测试输出解析后用户发过来的信息
		System.out.println("：解析用户发送过来的信息开始");
		for (String key : keySet) {
		 System.out.println(key + ":" + map.get(key));
		}
		System.out.println("：解析用户发送过来的信息结束");  
		return "Hello World!";
		
	}
	
	@RequestMapping(value="/hello",method=RequestMethod.GET)
	@ResponseBody
	public String hello(HttpServletRequest request,HttpServletResponse response) {
		return request.getParameter("say");
		
	}
	
	@RequestMapping(value="/printTest",method=RequestMethod.GET)
	@ResponseBody
	public String taoBaoTest() {
		
	/*	TaobaoClient client = new DefaultTaobaoClient("", "", "");
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ("女装");
		req.setCat("16,18");
		req.setItemloc("杭州");
		req.setSort("tk_rate_des");
		req.setIsTmall(false);
		req.setIsOverseas(false);
		req.setStartPrice(10L);
		req.setEndPrice(10L);
		req.setStartTkRate(123L);
		req.setEndTkRate(123L);
		req.setPlatform(1L);
		req.setPageNo(123L);
		req.setPageSize(20L);
		TbkItemGetResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());*/
		return "Hello World!";
		
	}

}
