package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.registry.Registry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.moekr.dubbo.agent.util.Constants.*;

@Component
@ConditionalOnProperty(name = AGENT_TYPE_PROPERTY, havingValue = PROVIDER_TYPE)
public class ProviderAgent {
	private final Registry registry;

	@Autowired
	public ProviderAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() throws Exception {
		int dubboPort = Integer.valueOf(System.getProperty(DUBBO_PORT_PROPERTY));
		int weight = Integer.valueOf(System.getProperty(PROVIDER_WEIGHT_PROPERTY));
		registry.register(SERVICE_NAME, dubboPort, weight);
	}
}
