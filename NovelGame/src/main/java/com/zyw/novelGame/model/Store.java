package com.zyw.novelGame.model;

import java.util.Date;

public class Store {
    private String id;

	private String bookId;

	private String storeId;

	private String storeName;

	private String storeUrl;

	private String preStoreId;

	private String nextStoreId;

	private Date createTime;

	private Long orderIndex;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId == null ? null : bookId.trim();
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId == null ? null : storeId.trim();
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName == null ? null : storeName.trim();
	}

	public String getStoreUrl() {
		return storeUrl;
	}

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl == null ? null : storeUrl.trim();
	}

	public String getPreStoreId() {
		return preStoreId;
	}

	public void setPreStoreId(String preStoreId) {
		this.preStoreId = preStoreId == null ? null : preStoreId.trim();
	}

	public String getNextStoreId() {
		return nextStoreId;
	}

	public void setNextStoreId(String nextStoreId) {
		this.nextStoreId = nextStoreId == null ? null : nextStoreId.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Long orderIndex) {
		this.orderIndex = orderIndex;
	}

}