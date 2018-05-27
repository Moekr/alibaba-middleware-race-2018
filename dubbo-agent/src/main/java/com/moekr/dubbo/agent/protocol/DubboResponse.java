package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DubboResponse extends AbstractResponse {
	private byte[] result;

	public DubboResponse(long id) {
		super(id);
	}
}
