package com.zyw.novelGame.collect.novelSite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.CollectData;
import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.entity.StoreInfo;
import com.zyw.novelGame.collect.queue.QueueInfo;
public class ShuHuangGeNovelSite extends BasicNovelSite{

	@Override
	public List<QueueInfo> getNovelSite() {
		super.blocking_queue_threshold=1;
		List<QueueInfo> list=new ArrayList<QueueInfo>();
		CollectInfo collect=getCollectInfo();
		List<String> bookList=new ArrayList<String>();
		bookList.add("xuanhuanxiaoshuo");
		bookList.add("xiuzhenxiaoshuo");
		bookList.add("dushixiaoshuo");
		bookList.add("chuanyuexiaoshuo");
		bookList.add("wangyouxiaoshuo");
		bookList.add("kehuanxiaoshuo");
		for(int i=0;i<bookList.size();i++) {
			Rule bookRule=new Rule();
			bookRule.setType("0");
			bookRule.setUrlStartNum("1");
			bookRule.setUrlEndNum("10");
			bookRule.setUrl("http://www.shuhuangge.org/"+bookList.get(i)+"/"+(i+1)+"_@.html");
			bookRule.setUrlMatch("div#newscontent div.l span.s2 a[href]");
			collect.setBookRule(bookRule);
			list.addAll(CollectData.analyzeBookList(collect));
		}
		return list;
	}

	@Override
	public CollectInfo getCollectInfo() {
		CollectInfo collect=new CollectInfo();
		collect.setNovelSiteName("书荒阁");		
		collect.setNovelSiteUrl("http://www.shuhuangge.org");
		collect.setNovelCharset("gbk");
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
		return collect;
	}

}
