package com.moekr.dubbo.agent.util;

import com.moekr.dubbo.agent.registry.Endpoint;
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
	private static final Map<Endpoint, AtomicInteger> ENDPOINT_MAP = new ConcurrentHashMap<>();

	public static boolean increase(Endpoint endpoint) {
		AtomicInteger value = ENDPOINT_MAP.computeIfAbsent(endpoint, ch -> new AtomicInteger());
		if (value.incrementAndGet() < COUNTER_THRESHOLD) {
			return true;
		} else {
			value.decrementAndGet();
			return false;
		}
	}

	public static void decrease(Channel channel) {
		ENDPOINT_MAP.get(CHANNEL_MAP.get(channel)).decrementAndGet();
	}

	private static final Map<Channel, Endpoint> CHANNEL_MAP = new HashMap<>();

	public static void register(Endpoint endpoint, Channel channel) {
		CHANNEL_MAP.put(channel, endpoint);
	}
}
