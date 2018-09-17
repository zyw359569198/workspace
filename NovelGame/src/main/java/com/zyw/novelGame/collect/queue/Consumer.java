package com.zyw.novelGame.collect.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer  extends Thread{
	public static final  Logger logger=LoggerFactory.getLogger(Consumer.class);
	
	private Resource resource;
	
	private Consumer() {}
	
	public Consumer(Resource resource){
		this.resource=resource;
	}
	
	public void run() {
	         while (true) {
	        	 try {
		            Thread.sleep((long) (1000 * Math.random()));
		             } catch (InterruptedException e) {
		                 e.printStackTrace();
		             }
	        	 resource.remove();
		         }
		     }
	

}
