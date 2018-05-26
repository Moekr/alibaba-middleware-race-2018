package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.util.MessageFuture;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageFutureHolder {
	private static Map<Integer, MessageFuture> MESSAGE_FUTURE_MAP = new HashMap<>();

	public static void hold(MessageFuture future) {
		MESSAGE_FUTURE_MAP.put(future.getRequestMessage().getSequence(), future);
	}

	public static MessageFuture remove(int sequence) {
		return MESSAGE_FUTURE_MAP.remove(sequence);
	}
}
