package com.moekr.dubbo.agent.util;

import java.util.HashMap;
import java.util.Map;

public abstract class ContextHolder {
	private static final Map<Long, RequestContext> CONTEXT_MAP = new HashMap<>();

	public static void hold(RequestContext context) {
		CONTEXT_MAP.put(context.getId(), context);
	}

	public static RequestContext remove(long id) {
		return CONTEXT_MAP.remove(id);
	}
}
