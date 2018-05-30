package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AgentResponse extends AbstractResponse implements AgentMessage {
	private String result;

	public AgentResponse(long id) {
		super(id);
	}
}
