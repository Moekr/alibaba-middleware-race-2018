package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AgentRequest extends AgentMessage {
	protected static final AtomicLong SEQUENCE = new AtomicLong(0);

	private String interfaceName;
	private String methodName;
	private String parameterTypesString;
	private String parameter;
	private String fullRequest;

	public AgentRequest(long id) {
		super(id);
	}

	public static AgentRequest newInstance() {
		return new AgentRequest(SEQUENCE.incrementAndGet());
	}
}
