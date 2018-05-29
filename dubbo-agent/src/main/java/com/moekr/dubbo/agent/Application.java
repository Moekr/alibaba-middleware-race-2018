package com.moekr.dubbo.agent;

import io.netty.util.ResourceLeakDetector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends SpringApplication {
	public static void main(String[] args) {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		SpringApplication.run(Application.class, args);
	}
}
