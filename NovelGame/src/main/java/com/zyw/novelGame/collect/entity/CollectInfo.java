package com.zyw.novelGame.collect.entity;
import java.util.List;

public class CollectInfo {
	
	private String novelSiteName;
	
	private String novelSiteUrl;
	
	private Rule bookRule;
	
	private BookInfo bookInfo;

	public String getNovelSiteName() {
		return novelSiteName;
	}

	public void setNovelSiteName(String novelSiteName) {
		this.novelSiteName = novelSiteName;
	}

	public String getNovelSiteUrl() {
		return novelSiteUrl;
	}

	public void setNovelSiteUrl(String novelSiteUrl) {
		this.novelSiteUrl = novelSiteUrl;
	}

	public Rule getBookRule() {
		return bookRule;
	}

	public void setBookRule(Rule bookRule) {
		this.bookRule = bookRule;
	}

	public BookInfo getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(BookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}
	

}
