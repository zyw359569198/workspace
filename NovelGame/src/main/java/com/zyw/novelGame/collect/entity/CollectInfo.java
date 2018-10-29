package com.zyw.novelGame.collect.entity;

public class CollectInfo {
	
	private String novelSiteName;
	
	private String novelSiteUrl;
	
	private String novelCharset="utf-8";
	
	private Rule bookRule;
	
	private BookInfo bookInfo;

	public String getNovelCharset() {
		return novelCharset;
	}

	public void setNovelCharset(String novelCharset) {
		this.novelCharset = novelCharset;
	}

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
