package com.zyw.novelGame.collect;

import java.util.ArrayList;
import java.util.List;

import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.queue.Consumer;
import com.zyw.novelGame.collect.queue.Producer;
import com.zyw.novelGame.collect.queue.QueueInfo;

public class CollectData {
	
	public static List<QueueInfo>  analyzeBookList(CollectInfo collect) {
		List<QueueInfo> queueInfoList=new ArrayList<QueueInfo>();
		QueueInfo queueInfo=new QueueInfo();
		if("0".equalsIgnoreCase(collect.getBookRule().getType())) {
			if(Integer.parseInt(collect.getBookRule().getUrlStartNum())==-1||Integer.parseInt(collect.getBookRule().getUrlEndNum())==-1) {
				queueInfo.setType("0");
				queueInfo.setResult(collect.getBookRule().getUrl());
				queueInfo.setCollect(collect);
				queueInfoList.add(queueInfo);
			}else {
				for(int i=Integer.parseInt(collect.getBookRule().getUrlStartNum());i<(Integer.parseInt(collect.getBookRule().getUrlEndNum())+1);i++) {
					queueInfo.setType("0");
					queueInfo.setResult(addStr(collect.getBookRule().getUrl(),"@",i+""));
					queueInfo.setCollect(collect);
					queueInfoList.add(queueInfo);
				}
			}
		}else if("1".equalsIgnoreCase(collect.getBookRule().getType())) {
			
		}else if("2".equalsIgnoreCase(collect.getBookRule().getType())) {
			
		}		
		
		return queueInfoList;
	}
	
	public static String addStr(String str,String parrent,String data) {
		StringBuffer sb=new StringBuffer();
		String[] strs=str.split(parrent);
		if(strs.length<2) {
			sb.append(strs[0]);
			sb.append(data);
		}else {
			sb.append(strs[0]);
			sb.append(data);
			sb.append(strs[1]);
		}
		return sb.toString();
		
	}
	public static void main(String[] args) {
		System.out.println(addStr("http://www.baidu.com/@/$/","@","1"));
		/*CollectInfo collect=new CollectInfo();
		Rule bookRule=new Rule();
		collect.setNovelSiteName("txt2小说网");
		
		collect.setNovelSiteUrl("https://txt2.cc");
		bookRule.setType("0");
		bookRule.setUrlStartNum("0");
		bookRule.setUrlEndNum("0");
		bookRule.setUrl("https://txt2.cc/map");
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
		collect.setBookInfo(bookInfo);*/
/*		Producer producer=new Producer();
		producer.start();
		producer.add(analyzeBookList(collect));
		Consumer  consumer=new Consumer();
		consumer.start();
		Consumer  consumer1=new Consumer();
		consumer1.start();*/
	}

}
