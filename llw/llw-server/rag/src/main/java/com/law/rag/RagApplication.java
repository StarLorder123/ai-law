package com.law.rag;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.law.rag.mapper")
public class RagApplication {

	private static final Logger log = LoggerFactory.getLogger(RagApplication.class);

	public static void main(String[] args) {
		log.info("Application start!");
		SpringApplication.run(RagApplication.class, args);
	}

}
