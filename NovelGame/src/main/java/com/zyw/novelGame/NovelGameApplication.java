package com.zyw.novelGame;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.zyw.novelGame.mapper")
@ComponentScan(basePackages = {"com.zyw.*"})
@EnableAsync
@EnableTransactionManagement
public class NovelGameApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(NovelGameApplication.class, args);
		//ApplicationContextProvider.getBean("collect", CollectController.class).init(null, null);
	}
	
/*	@Bean(name = "taskExecutorNovel")
	public Executor taskExecutorNovel() {
	   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	   executor.setCorePoolSize(20);
	   executor.setMaxPoolSize(20);
	   executor.setQueueCapacity(200);

	   executor.setKeepAliveSeconds(60);
	   executor.setThreadNamePrefix("taskExecutorNovel-");
	   executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	   executor.setWaitForTasksToCompleteOnShutdown(true);
	   executor.setAwaitTerminationSeconds(60);
	    return executor;
	}*/
}
