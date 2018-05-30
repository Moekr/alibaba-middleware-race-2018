package com.moekr.dubbo.agent.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractRequest {
	protected static final AtomicLong SEQUENCE = new AtomicLong(0);

	private long id;
}
