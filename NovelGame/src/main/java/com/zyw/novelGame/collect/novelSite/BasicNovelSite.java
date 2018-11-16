package com.zyw.novelGame.collect.novelSite;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.queue.QueueInfo;
import com.zyw.novelGame.collect.queue.Resource;
public abstract class BasicNovelSite implements Runnable{
	
	public abstract CollectInfo getCollectInfo() ;
			
	public abstract List<QueueInfo> getNovelSite() ;
	
	//阻塞队列阈值
	public  int blocking_queue_threshold=1;
	 
	@Override
	public void run() {
		getNovelSite().forEach(item->{
		while(true) {
			if(Resource.getInstance().size()<blocking_queue_threshold) {
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
