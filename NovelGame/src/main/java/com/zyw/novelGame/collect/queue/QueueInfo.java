package com.zyw.novelGame.collect.queue;

import java.util.List;
import java.util.Map;

import com.zyw.novelGame.collect.entity.CollectInfo;

public class QueueInfo {
	private String type;
	
	private CollectInfo collect;
	
	private String  result;
	
	private Map mark;	
	
	private List resultList;
	

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public Map getMark() {
		return mark;
	}

	public void setMark(Map mark) {
		this.mark = mark;
	}

	public CollectInfo getCollect() {
		return collect;
	}

	public void setCollect(CollectInfo collect) {
		this.collect = collect;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	

}
