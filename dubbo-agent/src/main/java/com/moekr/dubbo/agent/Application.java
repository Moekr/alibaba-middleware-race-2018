package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.util.enums.AgentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends SpringApplication {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		if (AgentType.current() == AgentType.PROVIDER) {
			application.setWebApplicationType(WebApplicationType.NONE);
		}
		application.run(args);
	}
}
