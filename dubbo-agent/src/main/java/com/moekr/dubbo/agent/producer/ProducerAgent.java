package com.moekr.dubbo.agent.producer;

import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnNotWebApplication
public class ProducerAgent {
	private final Registry registry;

	@Autowired
	public ProducerAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() throws Exception {
		int port = Integer.valueOf(System.getProperty(Constants.SERVER_PORT_PROPERTY));
		new NettyServerBootstrap(port);
		registry.register(Constants.SERVICE_NAME, port);
	}
}
