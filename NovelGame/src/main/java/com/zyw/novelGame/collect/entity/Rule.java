package com.zyw.novelGame.collect.entity;

public class Rule {
	//0  每次递增1; 1  按照字母排序;  2   通过链接匹配; 3  普通匹配
	private String type;
	//书列表起始页
	private String url;
	//url起始数字、字母、链接字符串
	private String urlStartNum;
	//url起始数字、字母、链接字符串
	private String urlEndNum;
	//selector，支持正则表达式
	private String urlMatch;
	//对结果进行匹配处理
	private String valueDeal;
	
	public String getValueDeal() {
		return valueDeal;
	}
	public void setValueDeal(String valueDeal) {
		this.valueDeal = valueDeal;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlStartNum() {
		return urlStartNum;
	}
	public void setUrlStartNum(String urlStartNum) {
		this.urlStartNum = urlStartNum;
	}
	public String getUrlEndNum() {
		return urlEndNum;
	}
	public void setUrlEndNum(String urlEndNum) {
		this.urlEndNum = urlEndNum;
	}
	public String getUrlMatch() {
		return urlMatch;
	}
	public void setUrlMatch(String urlMatch) {
		this.urlMatch = urlMatch;
	}
	
	
}
