package com.zyw.novelGame.collect.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Producer {
public static final  Logger logger=LoggerFactory.getLogger(Producer.class);
	
    @Async(value = "taskExecutorNovel")
	public void add(QueueInfo queueInfo) {
		Resource.getInstance().add(queueInfo);
	}
	
/*    @Async(value = "taskExecutorNovel")
	public void execute() {
	         while (true) {
	        	 try {
		            Thread.sleep((long) (1000 * Math.random()));
		             } catch (InterruptedException e) {
		                 e.printStackTrace();
		             }
	        	 
		         }
		     }*/
	


}
