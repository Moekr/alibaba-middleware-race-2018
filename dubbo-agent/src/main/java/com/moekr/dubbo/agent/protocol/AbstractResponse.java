package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractResponse {
	private long id;

	public AbstractResponse(long id) {
		this.id = id;
	}
}
