package com.moekr.dubbo.agent.util;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public abstract class ContextHolder {
	private static final Map<Long, Channel> CONTEXT_MAP = new HashMap<>();

	public static void hold(long id, Channel channel) {
		CONTEXT_MAP.put(id, channel);
	}

	public static Channel remove(long id) {
		return CONTEXT_MAP.remove(id);
	}
}
