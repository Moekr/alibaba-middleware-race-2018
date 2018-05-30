package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AgentRequest extends AbstractRequest implements AgentMessage {
	private String interfaceName;
	private String methodName;
	private String parameterTypesString;
	private String parameter;

	public AgentRequest(long id) {
		super(id);
	}

	public static AgentRequest newInstance() {
		return new AgentRequest(SEQUENCE.incrementAndGet());
	}
}
