package com.zyw.novelGame.collect.entity;

public class BookInfo {
	
	private Rule authorName;
	
	private Rule bookName;
		
	private Rule bookDesc;
	
	private Rule imageUrl;
	
	private Rule createTime;
	
	private Rule updateTime;
	
	private Rule isCompletion;
	
	private Rule cataName;
	
	private Rule storeCataUrl;
	
	private Rule storeRule;
	
	private StoreInfo storeInfo;
	
	private String bookUrl;

	public Rule getStoreCataUrl() {
		return storeCataUrl;
	}

	public void setStoreCataUrl(Rule storeCataUrl) {
		this.storeCataUrl = storeCataUrl;
	}

	public Rule getCataName() {
		return cataName;
	}

	public void setCataName(Rule cataName) {
		this.cataName = cataName;
	}

	public String getBookUrl() {
		return bookUrl;
	}

	public void setBookUrl(String bookUrl) {
		this.bookUrl = bookUrl;
	}

	public Rule getAuthorName() {
		return authorName;
	}

	public void setAuthorName(Rule authorName) {
		this.authorName = authorName;
	}

	public Rule getBookName() {
		return bookName;
	}

	public void setBookName(Rule bookName) {
		this.bookName = bookName;
	}

	public Rule getBookDesc() {
		return bookDesc;
	}

	public void setBookDesc(Rule bookDesc) {
		this.bookDesc = bookDesc;
	}

	public Rule getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(Rule imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Rule getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Rule createTime) {
		this.createTime = createTime;
	}

	public Rule getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Rule updateTime) {
		this.updateTime = updateTime;
	}

	public Rule getIsCompletion() {
		return isCompletion;
	}

	public void setIsCompletion(Rule isCompletion) {
		this.isCompletion = isCompletion;
	}

	public Rule getStoreRule() {
		return storeRule;
	}

	public void setStoreRule(Rule storeRule) {
		this.storeRule = storeRule;
	}

	public StoreInfo getStoreInfo() {
		return storeInfo;
	}

	public void setStoreInfo(StoreInfo storeInfo) {
		this.storeInfo = storeInfo;
	}
	
}
