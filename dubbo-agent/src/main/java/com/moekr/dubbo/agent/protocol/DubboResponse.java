package com.moekr.dubbo.agent.protocol;

import lombok.Data;

@Data
public class DubboResponse {
	private final long id;

	private byte[] result;

	public DubboResponse(long id) {
		this.id = id;
	}
}
