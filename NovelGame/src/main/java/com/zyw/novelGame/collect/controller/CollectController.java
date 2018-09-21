package com.zyw.novelGame.collect.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zyw.novelGame.collect.CollectData;
import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.entity.StoreInfo;
import com.zyw.novelGame.collect.queue.Consumer;
import com.zyw.novelGame.collect.queue.Producer;

@RestController
@RequestMapping("/collect")
public class CollectController {
	public static final  Logger logger=LoggerFactory.getLogger(CollectController.class);
	
	@Autowired
	private Consumer consumer;
	
	@Autowired
	private Producer producer;
	
	@RequestMapping(value="/init",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map init(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();	
		CollectInfo collect=new CollectInfo();
		Rule bookRule=new Rule();
		collect.setNovelSiteName("txt2小说网");		
		collect.setNovelSiteUrl("https://txt2.cc");
		bookRule.setType("0");
		bookRule.setUrlStartNum("8");
		bookRule.setUrlEndNum("8");
		bookRule.setUrl("https://txt2.cc/map/@/");
		bookRule.setUrlMatch("body > a[href]");
		collect.setBookRule(bookRule);
		BookInfo bookInfo=new BookInfo();
		Rule authorName=new Rule();
		authorName.setType("3");
		authorName.setUrlMatch("div.jieshao div.rt div.msg em:eq(0) a");
		bookInfo.setAuthorName(authorName);
		Rule bookDesc=new Rule();
		bookDesc.setType("3");
		bookDesc.setUrlMatch("div.jieshao div.rt div.intro");
		bookInfo.setBookDesc(bookDesc);
		Rule bookName=new Rule();
		bookName.setType("3");
		bookName.setUrlMatch("div.jieshao div.rt h1");
		bookInfo.setBookName(bookName);
		bookInfo.setBookUrl("");
		Rule updateTime=new Rule();
		updateTime.setType("3");
		updateTime.setUrlMatch("div.jieshao div.rt div.msg em:eq(2)");
		updateTime.setValueDeal("：");
		bookInfo.setUpdateTime(updateTime);
		Rule isCompletion=new Rule();
		isCompletion.setType("3");
		isCompletion.setUrlMatch("div.jieshao div.rt div.msg em:eq(1)");
		isCompletion.setValueDeal("：");
		bookInfo.setIsCompletion(isCompletion);
		Rule imageUrl=new Rule();
		imageUrl.setType("3");
		imageUrl.setUrlMatch("div.jieshao div.lf img[src]");
		bookInfo.setImageUrl(imageUrl);
		Rule storeRule=new Rule();
		storeRule.setType("3");
		storeRule.setUrlMatch("div.mulu a[href]");
		bookInfo.setStoreRule(storeRule);
		Rule cataName=new Rule();
		cataName.setType("3");
		cataName.setUrlMatch("div.place a:eq(1)");
		bookInfo.setCataName(cataName);
		StoreInfo storeInfo=new StoreInfo();
		storeInfo.setStoreUrl("");
		Rule storeContent=new Rule();
		storeContent.setType("3");
		storeContent.setUrlMatch("div.yd_text2");
		storeInfo.setStoreContent(storeContent);
		Rule storeName=new Rule();
		storeName.setType("3");
		storeName.setUrlMatch("div.novel h1");
		storeInfo.setStoreName(storeName);
		bookInfo.setStoreInfo(storeInfo);
		collect.setBookInfo(bookInfo);
		for(int i=0;i<10;i++) {
			consumer.execute();
		}
		producer.add(CollectData.analyzeBookList(collect));
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	

	@RequestMapping(value="/initData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map initDataShuHuang(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();	
		CollectInfo collect=new CollectInfo();
		Rule bookRule=new Rule();
		collect.setNovelSiteName("书荒阁");		
		collect.setNovelSiteUrl("http://www.shuhuangge.org");
		collect.setNovelCharset("gbk");
		bookRule.setType("0");
		bookRule.setUrlStartNum("1");
		bookRule.setUrlEndNum("177");
		bookRule.setUrl("http://www.shuhuangge.org/xuanhuanxiaoshuo/1_@.html");
		bookRule.setUrlMatch("div#newscontent div.l span.s2 a[href]");
		collect.setBookRule(bookRule);
		BookInfo bookInfo=new BookInfo();
		Rule authorName=new Rule();
		authorName.setType("3");
		authorName.setUrlMatch("div#info p:eq(1) a");
		bookInfo.setAuthorName(authorName);
		Rule bookDesc=new Rule();
		bookDesc.setType("3");
		bookDesc.setUrlMatch("div#intro p");
		bookInfo.setBookDesc(bookDesc);
		Rule bookName=new Rule();
		bookName.setType("3");
		bookName.setUrlMatch("div#info h1");
		bookInfo.setBookName(bookName);
		bookInfo.setBookUrl("");
/*		Rule isCompletion=new Rule();
		isCompletion.setType("3");
		isCompletion.setUrlMatch("div#fmimg .b");
		isCompletion.setValueDeal("");
		bookInfo.setIsCompletion(isCompletion);*/
		Rule updateTime=new Rule();
		updateTime.setType("3");
		updateTime.setUrlMatch("div#info p:eq(3)");
		updateTime.setValueDeal("：");
		bookInfo.setUpdateTime(updateTime);
		Rule imageUrl=new Rule();
		imageUrl.setType("3");
		imageUrl.setUrlMatch("div#fmimg img[src]");
		bookInfo.setImageUrl(imageUrl);
		Rule storeRule=new Rule();
		storeRule.setType("3");
		storeRule.setUrlMatch("div#list dl a[href]");
		bookInfo.setStoreRule(storeRule);
		Rule cataName=new Rule();
		cataName.setType("3");
		cataName.setUrlMatch("div.con_top a:eq(2)");
		cataName.setValueDeal("substr 0 2");
		bookInfo.setCataName(cataName);
		StoreInfo storeInfo=new StoreInfo();
		storeInfo.setStoreUrl("");
		Rule storeContent=new Rule();
		storeContent.setType("3");
		storeContent.setUrlMatch("div#content");
		storeInfo.setStoreContent(storeContent);
		Rule storeName=new Rule();
		storeName.setType("3");
		storeName.setUrlMatch("div.bookname h1");
		storeInfo.setStoreName(storeName);
		bookInfo.setStoreInfo(storeInfo);
		collect.setBookInfo(bookInfo);
		for(int i=0;i<2;i++) {
			consumer.execute();
		}
		producer.add(CollectData.analyzeBookList(collect));
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	/**
	 * 纵横中文网
	 * @param request
	 * @param response1
	 * @return
	 */
/*	@RequestMapping(value="/initData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody*/
	public Map initData(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();	
		CollectInfo collect=new CollectInfo();
		Rule bookRule=new Rule();
		collect.setNovelSiteName("纵横中文网");		
		collect.setNovelSiteUrl("http://www.zongheng.com/");
		bookRule.setType("0");
		bookRule.setUrlStartNum("1");
		bookRule.setUrlEndNum("1");
		bookRule.setUrl("http://book.zongheng.com/store/c0/c0/b0/u0/p@/v9/s9/t0/u0/i1/ALL.html");
		bookRule.setUrlMatch("div.store_collist div.bookbox.fl div.bookimg a[href]");
		collect.setBookRule(bookRule);
		BookInfo bookInfo=new BookInfo();
		Rule authorName=new Rule();
		authorName.setType("3");
		authorName.setUrlMatch("div.au-name a");
		bookInfo.setAuthorName(authorName);
		Rule bookDesc=new Rule();
		bookDesc.setType("3");
		bookDesc.setUrlMatch("div.book-dec.Jbook-dec.hide p");
		bookInfo.setBookDesc(bookDesc);
		Rule bookName=new Rule();
		bookName.setType("3");
		bookName.setUrlMatch("div.book-name:first-child");
		bookInfo.setBookName(bookName);
		bookInfo.setBookUrl("");
		Rule isCompletion=new Rule();
		isCompletion.setType("3");
		isCompletion.setUrlMatch("div.book-label a:eq(0)");
		isCompletion.setValueDeal("");
		bookInfo.setIsCompletion(isCompletion);
		Rule imageUrl=new Rule();
		imageUrl.setType("3");
		imageUrl.setUrlMatch("div.book-img.fl img[src]");
		bookInfo.setImageUrl(imageUrl);
		Rule storeCataUrl=new Rule();
		storeCataUrl.setType("3");
		storeCataUrl.setUrlMatch("div.fr.link-group a:eq(0)[href]");
		bookInfo.setStoreCataUrl(storeCataUrl);
		Rule storeRule=new Rule();
		storeRule.setType("3");
		storeRule.setUrlMatch("div.volume-list li a[href]");
		bookInfo.setStoreRule(storeRule);
		Rule cataName=new Rule();
		cataName.setType("3");
		cataName.setUrlMatch("div.crumb a:eq(1)");
		bookInfo.setCataName(cataName);
		StoreInfo storeInfo=new StoreInfo();
		storeInfo.setStoreUrl("");
		Rule storeContent=new Rule();
		storeContent.setType("3");
		storeContent.setUrlMatch("div.content");
		storeInfo.setStoreContent(storeContent);
		Rule storeName=new Rule();
		storeName.setType("3");
		storeName.setUrlMatch("div.title_txtbox");
		storeInfo.setStoreName(storeName);
		bookInfo.setStoreInfo(storeInfo);
		collect.setBookInfo(bookInfo);
		for(int i=0;i<1;i++) {
			consumer.execute();
		}
		producer.add(CollectData.analyzeBookList(collect));
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}


}
