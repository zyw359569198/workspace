package com.zyw.novelGame.collect.novelSite;

import java.util.List;

import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.queue.QueueInfo;
import com.zyw.novelGame.collect.queue.Resource;
import com.zyw.utils.Common;

public abstract class BasicNovelSite implements Runnable{
	
	public abstract CollectInfo getCollectInfo() ;
			
	public abstract List<QueueInfo> getNovelSite() ;
	 
	@Override
	public void run() {
		getNovelSite().forEach(item->{
		while(true) {
			if(Resource.getInstance().size()<Common.BLOCKING_QUEUE_THRESHOLD) {
				Resource.getInstance().add(item);
				System.out.println("资源池加入资源："+Resource.getInstance().size());
				break;
			}else {
				try {
					System.out.println("加入资源线程等待");
					Thread.sleep((long) (5000*Math.random()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		});
	}

}
