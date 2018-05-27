package com.moekr.dubbo.agent.util.enums;

import static com.moekr.dubbo.agent.util.Constants.*;

public enum AgentType {
	CONSUMER, PROVIDER;

	private static final AgentType CURRENT_AGENT_TYPE;

	static {
		String type = System.getProperty(AGENT_TYPE_PROPERTY);
		if (CONSUMER_TYPE.equals(type)) {
			CURRENT_AGENT_TYPE = CONSUMER;
		} else if (PROVIDER_TYPE.equals(type)) {
			CURRENT_AGENT_TYPE = PROVIDER;
		} else {
			CURRENT_AGENT_TYPE = null;
		}
	}

	public static AgentType current() {
		if (CURRENT_AGENT_TYPE == null) {
			throw new IllegalArgumentException("Unknown agent type!");
		}
		return CURRENT_AGENT_TYPE;
	}
}
