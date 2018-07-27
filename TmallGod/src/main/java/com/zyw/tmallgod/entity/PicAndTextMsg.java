package com.zyw.tmallgod.entity;

import java.util.List;

public class PicAndTextMsg {
	private String ToUserName;
	 private String FromUserName;
	 private long CreateTime;
	 private String MsgType;
	 private int ArticleCount;
	 private List<Article> Articles;
	 
	 @Override
	 public String toString() {
	  return "PicAndTextMsg [ToUserName=" + ToUserName + ", FromUserName="
	    + FromUserName + ", CreateTime=" + CreateTime + ", MsgType="
	    + MsgType + ", ArticleCount=" + ArticleCount + ", Articles="
	    + Articles + "]";
	 }
	 
	 public String getToUserName() {
	  return ToUserName;
	 }
	 
	 public void setToUserName(String toUserName) {
	  ToUserName = toUserName;
	 }
	 
	 public String getFromUserName() {
	  return FromUserName;
	 }
	 
	 public void setFromUserName(String fromUserName) {
	  FromUserName = fromUserName;
	 }
	 
	 public long getCreateTime() {
	  return CreateTime;
	 }
	 
	 public void setCreateTime(long createTime) {
	  CreateTime = createTime;
	 }
	 
	 public String getMsgType() {
	  return MsgType;
	 }
	 
	 public void setMsgType(String msgType) {
	  MsgType = msgType;
	 }
	 
	 public int getArticleCount() {
	  return ArticleCount;
	 }
	 
	 public void setArticleCount(int articleCount) {
	  ArticleCount = articleCount;
	 }
	 
	 public List<Article> getArticles() {
	  return Articles;
	 }
	 
	 public void setArticles(List<Article> articles) {
	  Articles = articles;
	 }
}
