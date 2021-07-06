package com.example.demo;

import javax.faces.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
                //SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
                //builder.headless(false);
                //ConfigurableApplicationContext context = builder.run(args);
	}

}
