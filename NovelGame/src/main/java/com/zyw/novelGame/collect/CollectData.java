package com.zyw.novelGame.collect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.queue.Consumer;
import com.zyw.novelGame.collect.queue.Producer;
import com.zyw.novelGame.collect.queue.QueueInfo;
import com.zyw.novelGame.collect.queue.Resource;
import com.zyw.utils.Common;
import com.zyw.utils.JsoupParse;

public class CollectData {
	private static Resource resource=new Resource(Common.BLOCKING_QUEUE_NUMS);
	
	public static QueueInfo  analyzeBookList(CollectInfo collect) {
		List bookItemList=new ArrayList();
		QueueInfo queueInfo=new QueueInfo();
		if("0".equalsIgnoreCase(collect.getBookRule().getType())) {
			for(int i=Integer.parseInt(collect.getBookRule().getUrlStartNum());i<(Integer.parseInt(collect.getBookRule().getUrlEndNum())+1);i++) {
				bookItemList.add(collect.getBookRule().getUrl()+"/"+i);
			}
			queueInfo.setType("0");
			queueInfo.setResultList(bookItemList);
		}else if("1".equalsIgnoreCase(collect.getBookRule().getType())) {
			
		}else if("2".equalsIgnoreCase(collect.getBookRule().getType())) {
			
		}		
		
		return queueInfo;
	}
	public static void main(String[] args) {
		CollectInfo collect=new CollectInfo();
		Rule bookRule=new Rule();
		collect.setNovelSiteName("txt2小说网");
		collect.setNovelSiteUrl("https://txt2.cc");
		bookRule.setType("0");
		bookRule.setUrlStartNum("0");
		bookRule.setUrlEndNum("9");
		bookRule.setUrl("https://txt2.cc/map");
		bookRule.setUrlMatch("body > a[href]");
		collect.setBookRule(bookRule);
		BookInfo bookInfo=new BookInfo();
		Rule authorName=new Rule();
		authorName.setType("3");
		authorName.setUrlMatch("div.jieshao div.rt h1");
		bookInfo.setAuthorName(authorName);
		Rule bookDesc=new Rule();
		bookDesc.setType("3");
		bookDesc.setUrlMatch("div.jieshao div.rt div.intro");
		bookInfo.setBookDesc(bookDesc);
		collect.setBookInfo(bookInfo);
		Producer producer=new Producer(resource);
		producer.start();
		producer.add(analyzeBookList(collect));
		producer.add(analyzeBookList(collect));
		producer.add(analyzeBookList(collect));
		producer.add(analyzeBookList(collect));
		Consumer  consumer=new Consumer(resource);
		consumer.start();
	}

}
