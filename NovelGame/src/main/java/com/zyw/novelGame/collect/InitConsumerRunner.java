package com.zyw.novelGame.collect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.zyw.novelGame.collect.queue.Consumer;
import com.zyw.utils.Common;

@Component  
@Order(value=1)
public class InitConsumerRunner implements CommandLineRunner{
	@Autowired
	private  Consumer consumer;

	@Override
	public void run(String... args) throws Exception {
		for(int i=0;i<Common.CONSUMER_NUMS;i++) {
			consumer.execute();
		}
		
	}

}
