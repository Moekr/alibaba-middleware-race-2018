package com.moekr.dubbo.agent.protocol;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public abstract class AbstractMessage {
	private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

	private int sequence;

	public AbstractMessage() {
		this(SEQUENCE.getAndIncrement());
	}

	public AbstractMessage(int sequence) {
		this.sequence = sequence;
	}
}
