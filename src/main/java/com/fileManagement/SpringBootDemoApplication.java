package com.fileManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * <p>
 * User: Yang yun-long
 * <p>
 * Date: 15-9-5
 * <p>
 * Version: 1.0
 */

@SpringBootApplication
@EnableAutoConfiguration
public class SpringBootDemoApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootDemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApplication.class);
	}
}
