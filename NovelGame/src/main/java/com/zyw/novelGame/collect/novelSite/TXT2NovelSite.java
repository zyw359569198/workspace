package com.zyw.novelGame.collect.novelSite;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.CollectData;
import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.entity.StoreInfo;
import com.zyw.novelGame.collect.queue.QueueInfo;
public class TXT2NovelSite extends BasicNovelSite{

	@Override
	public List<QueueInfo> getNovelSite() {
		List<QueueInfo> list=new ArrayList<QueueInfo>();
		CollectInfo collect=getCollectInfo();
		List<String> bookList=new ArrayList<String>();
		bookList.add("dushi");
		bookList.add("xuanfan");
		bookList.add("wuxia");
		bookList.add("yanqing");
		bookList.add("chuanyue");
		bookList.add("wangyou");
		bookList.add("kongbu");
		bookList.add("kefan");
		bookList.add("xiuzhen");
		bookList.add("qita");
		for(int i=0;i<bookList.size();i++) {
			Rule bookRule=new Rule();
			bookRule.setType("0");
			bookRule.setUrlStartNum("1");
			bookRule.setUrlEndNum("10");
			bookRule.setUrl("https://txt2.cc/index.php?m=Home&c=Book&a=clist&pinyin="+bookList.get(i)+"&p=@");
			bookRule.setUrlMatch("div.booklist ul li span.sm a[href]");
			collect.setBookRule(bookRule);
			list.addAll(CollectData.analyzeBookList(collect));
		}
		return list;
	}
	
	public static void main(String[] args) {
		ExecutorService executorService=Executors.newFixedThreadPool(1);
		List<BasicNovelSite> monitorLists = new ArrayList<>();
		Reflections reflections = new Reflections("com.zyw.novelGame.collect.novelSite");
		Set<Class<? extends BasicNovelSite>> monitorClasses = reflections.getSubTypesOf(BasicNovelSite.class);
        for (Class<? extends BasicNovelSite> monitor : monitorClasses) { 
        	//monitorLists.add(ApplicationContext.getBean(monitor)); 
        	executorService.execute(new Runnable() {

				@Override
				public void run() {
		        	System.out.println(monitor.getName());
					
				}
        		
        	});
        } 
	}

	@Override
	public CollectInfo getCollectInfo() {
		CollectInfo collect=new CollectInfo();
		collect.setNovelSiteName("txt2小说网");		
		collect.setNovelSiteUrl("https://txt2.cc");
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
		return collect;
	}


}
