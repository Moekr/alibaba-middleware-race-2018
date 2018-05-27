package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AgentRequest extends AbstractRequest implements AgentMessage {
	private String interfaceName;
	private String methodName;
	private String parameterTypesString;
	private String parameter;

	public static AgentRequest newInstance() {
		AgentRequest request = new AgentRequest();
		request.setId(SEQUENCE.incrementAndGet());
		return request;
	}
}
