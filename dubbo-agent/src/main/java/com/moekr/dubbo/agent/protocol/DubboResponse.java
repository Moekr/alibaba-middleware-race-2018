package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DubboResponse extends AbstractResponse {
	private byte[] result;

	public DubboResponse(long id) {
		super(id);
	}
}
