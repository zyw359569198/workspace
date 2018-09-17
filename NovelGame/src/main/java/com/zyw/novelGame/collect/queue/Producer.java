package com.zyw.novelGame.collect.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer  extends Thread{
public static final  Logger logger=LoggerFactory.getLogger(Producer.class);
	
	private Resource resource;
	
	private Producer() {}
	
	public Producer(Resource resource){
		this.resource=resource;
	}
	
	public void add(QueueInfo queueInfo) {
		resource.add(queueInfo);
	}
	
	public void run() {
	         while (true) {
	        	 try {
		            Thread.sleep((long) (1000 * Math.random()));
		             } catch (InterruptedException e) {
		                 e.printStackTrace();
		             }
	        	 
		         }
		     }
	


}
