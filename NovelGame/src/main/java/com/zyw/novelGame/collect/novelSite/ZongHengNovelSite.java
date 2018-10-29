package com.zyw.novelGame.collect.novelSite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.queue.QueueInfo;
public class ZongHengNovelSite extends BasicNovelSite{

	@Override
	public List<QueueInfo> getNovelSite() {
		List<QueueInfo> list=new ArrayList<QueueInfo>();
		/*CollectInfo collect=new CollectInfo();
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
		list.addAll(CollectData.analyzeBookList(collect));*/
		return list;
	}

	@Override
	public CollectInfo getCollectInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
