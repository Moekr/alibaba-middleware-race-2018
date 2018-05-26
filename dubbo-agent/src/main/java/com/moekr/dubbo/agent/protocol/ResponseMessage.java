package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseMessage extends AbstractMessage {
	private String result;

	public ResponseMessage() {
		super(0);
	}

	public ResponseMessage(int sequence) {
		super(sequence);
	}
}
