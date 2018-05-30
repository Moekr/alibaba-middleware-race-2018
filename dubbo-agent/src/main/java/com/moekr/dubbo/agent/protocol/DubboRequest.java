package com.moekr.dubbo.agent.protocol;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

import static com.moekr.dubbo.agent.protocol.DubboConstants.DUBBO_VERSION;

@Data
public class DubboRequest {
	protected static final AtomicLong SEQUENCE = new AtomicLong(0);

	private final long id;

	private String dubboVersion = DUBBO_VERSION;
	private String interfaceName;
	private String version;
	private String methodName;
	private String parameterTypesString;
	private String parameter;

	private boolean twoWay = true;
	private boolean event = false;

	private DubboRequest(long id) {
		this.id = id;
	}

	public static DubboRequest newInstance() {
		return new DubboRequest(SEQUENCE.incrementAndGet());
	}
}
