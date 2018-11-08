package com.hotels.agoda.launch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HotelsApp {

	public static void main(String[] args) {

		new SpringApplicationBuilder(HotelsApp.class)
		.properties("spring.config.name:application,ratelimit",
				"spring.config.location:classpath:/properties/")
		.build().run(args);
	}

}
