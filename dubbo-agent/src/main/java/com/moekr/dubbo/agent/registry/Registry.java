package com.moekr.dubbo.agent.registry;

public interface Registry {
	void register(String serviceName, int port, int weight) throws Exception;

	EndpointSet find(String serviceName) throws Exception;
}
