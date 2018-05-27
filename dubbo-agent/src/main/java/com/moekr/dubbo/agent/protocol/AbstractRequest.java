package com.moekr.dubbo.agent.protocol;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public abstract class AbstractRequest {
	protected static final AtomicLong SEQUENCE = new AtomicLong(0);

	private long id;
}
