package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AgentResponse extends AbstractResponse implements AgentMessage {
	private String result;

	public AgentResponse(AgentRequest request) {
		super(request.getId());
	}
}
