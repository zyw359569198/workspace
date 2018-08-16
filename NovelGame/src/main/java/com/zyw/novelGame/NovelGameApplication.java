package com.zyw.novelGame;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.zyw.novelGame.mapper")
public class NovelGameApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(NovelGameApplication.class, args);
	}
}
