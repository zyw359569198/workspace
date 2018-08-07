package com.zyw.tmallgod.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.thoughtworks.xstream.XStream;
import com.zyw.tmallgod.entity.Article;
import com.zyw.tmallgod.entity.PicAndTextMsg;
import com.zyw.tmallgod.entity.TextMsg;
import com.zyw.tmallgod.utils.CheckUtil;

@RestController
@RequestMapping("/test")
public class TestController {
	
	public static final  Logger logger=LoggerFactory.getLogger(TestController.class);
	
	@RequestMapping(value="/print",method= {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String printTest(HttpServletRequest request,HttpServletResponse response) {
		//注释这段代码主要是为了接入服务器时才需要
/*		logger.info("进来了11111111111111");
			    String signature = request.getParameter("signature");
		        String timestamp = request.getParameter("timestamp");
		        String nonce = request.getParameter("nonce");
		        String echostr = request.getParameter("echostr");

		        PrintWriter out = null;
				try {
					response.reset();
					out = response.getWriter();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
		        if(CheckUtil.checkSignature(signature, timestamp, nonce)){
		            out.print(echostr);
		            return "success";
		        }else
		        	return "success";*/
	   logger.info("进来了22222222222222222222");
		
		// * 该部分我们获取用户发送的信息，并且解析成<K,V>的形式进行显示
		 
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
		logger.info("：解析用户发送过来的信息开始");
		for (String key : keySet) {
			logger.info(key + ":" + map.get(key));
		}
		logger.info("：解析用户发送过来的信息结束");  

		return printPicAndTextMsg(map);
		
	}
	
	public static String printPicAndTextMsg(Map<String, String> map){
		//实例2，发送图文消息。请查看文档关于“回复图文消息”的xml格式
		 
		// 第一步：按照回复图文信息构造需要的参数
		List<Article> articles = new ArrayList<Article>();
		Article a = new Article();
		a.setTitle("我是图片标题");
		a.setUrl("http://119.3.2.234");// 该地址是点击图片跳转后
		a.setPicUrl("http://b.hiphotos.baidu.com/image/pic/item/08f790529822720ea5d058ba7ccb0a46f21fab50.jpg");// 该地址是一个有效的图片地址
		a.setDescription("我是图片的描述");
		articles.add(a);
		PicAndTextMsg picAndTextMsg = new PicAndTextMsg();
		picAndTextMsg.setToUserName(map.get("FromUserName"));// 发送和接收信息“User”刚好相反
		picAndTextMsg.setFromUserName(map.get("ToUserName"));
		picAndTextMsg.setCreateTime(new Date().getTime());// 消息创建时间 （整型）
		picAndTextMsg.setMsgType("news");// 图文类型消息
		picAndTextMsg.setArticleCount(1);
		picAndTextMsg.setArticles(articles);
		// 第二步，将构造的信息转化为微信识别的xml格式【百度：xstream bean转xml】
		XStream xStream = new XStream();
		xStream.alias("xml", picAndTextMsg.getClass());
		xStream.alias("item", a.getClass());
		String picAndTextMsg2Xml = xStream.toXML(picAndTextMsg);
		System.out.println(picAndTextMsg2Xml);
		// 第三步，发送xml的格式信息给微信服务器，服务器转发给用户
		return picAndTextMsg2Xml;
	}
	public static String printTextMsg(Map<String, String> map) {
		
		//实例1：发送普通文本消息,请查看文档关于“回复文本消息”的xml格式
		
		// 第一步：按照回复文本信息构造需要的参数
		TextMsg textMsg = new TextMsg();
		textMsg.setToUserName(map.get("FromUserName"));// 发送和接收信息“User”刚好相反
		textMsg.setFromUserName(map.get("ToUserName"));
		textMsg.setCreateTime(new Date().getTime());// 消息创建时间 （整型）
		textMsg.setMsgType("text");// 文本类型消息
		textMsg.setContent("我是服务器回复给用户的信息");
		
		// // 第二步，将构造的信息转化为微信识别的xml格式【百度：xstream bean转xml】
		XStream xStream = new XStream();
		xStream.alias("xml", textMsg.getClass());
		String textMsg2Xml = xStream.toXML(textMsg);
		System.out.println(textMsg2Xml);
		return textMsg2Xml;
	}
	
	@RequestMapping(value="/hello",method=RequestMethod.GET)
	@ResponseBody
	public String hello(HttpServletRequest request,HttpServletResponse response) {
		logger.info("say ~~~~~");
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
