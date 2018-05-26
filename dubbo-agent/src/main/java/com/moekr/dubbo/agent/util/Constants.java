package com.moekr.dubbo.agent.util;

public abstract class Constants {
	public static final String AGENT_TYPE_PROPERTY = "type";
	public static final String ETCD_ADDRESS_PROPERTY = "etcd.url";
	public static final String SERVER_PORT_PROPERTY = "server.port";

	public static final String CONSUMER_TYPE = "consumer";
	public static final String PRODUCER_TYPE = "producer";

	public static final String SERVICE_NAME = "com.alibaba.dubbo.performance.demo.provider.IHelloService";

	public static final String ETCD_ROOT_PATH = "DUBBO_AGENT";

	public static final String ERROR_RESULT = "ERROR";
}
