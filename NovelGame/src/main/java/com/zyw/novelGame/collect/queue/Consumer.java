package com.zyw.novelGame.collect.queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.ApplicationContextProvider;


@Component
public class Consumer {
	public static final  Logger logger=LoggerFactory.getLogger(Consumer.class);
	
	private Deal deal;
	
	@Async(value = "taskExecutorNovel")
	public void execute() {
	         while (true) {
	        	 try {
		            Thread.sleep((long) (1000 * Math.random()));
		        	 ApplicationContextProvider.getBean("deal", Deal.class).init(Resource.getInstance().remove());
		             } catch (InterruptedException e) {
		                 e.printStackTrace();
		             }
		         }
		     }
	
	

}
