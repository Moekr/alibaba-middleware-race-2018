package com.moekr.dubbo.agent.util;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ContextHolder {
	private static final Map<Long, RequestContext> CONTEXT_MAP = new HashMap<>();

	public static void hold(RequestContext context) {
		CONTEXT_MAP.put(context.getId(), context);
	}

	public static RequestContext remove(long id) {
		return CONTEXT_MAP.remove(id);
	}

	private static final int COUNTER_THRESHOLD = 200;
	private static final Map<Channel, AtomicInteger> COUNTER_MAP = new ConcurrentHashMap<>();

	public static boolean increase(Channel channel) {
		AtomicInteger value = COUNTER_MAP.computeIfAbsent(channel, ch -> new AtomicInteger());
		if (value.incrementAndGet() < COUNTER_THRESHOLD) {
			return true;
		} else {
			value.decrementAndGet();
			return false;
		}
	}

	public static void decrease(Channel channel) {
		COUNTER_MAP.get(channel).decrementAndGet();
	}
}
