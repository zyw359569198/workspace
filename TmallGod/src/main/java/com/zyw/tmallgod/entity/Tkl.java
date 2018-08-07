package com.zyw.tmallgod.entity;

import java.util.List;

public class Tkl {
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public DataObject getData() {
		return data;
	}
	public void setData(DataObject data) {
		this.data = data;
	}
	public class DataObject{
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getPic_url() {
			return pic_url;
		}
		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}
		public String getThumb_pic_url() {
			return thumb_pic_url;
		}
		public void setThumb_pic_url(String thumb_pic_url) {
			this.thumb_pic_url = thumb_pic_url;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUrl1() {
			return url1;
		}
		public void setUrl1(String url1) {
			this.url1 = url1;
		}
		public String getNative_url() {
			return native_url;
		}
		public void setNative_url(String native_url) {
			this.native_url = native_url;
		}
		private String content;
		private String title;
		private String pic_url;
		private String thumb_pic_url;
		private String url;
		private String url1;
		private String native_url;
		
	}
	private String status;
	private String msg;
	private DataObject data;

}
