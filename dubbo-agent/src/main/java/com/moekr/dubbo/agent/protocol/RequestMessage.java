package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestMessage extends AbstractMessage {
	private String interfaceName;
	private String methodName;
	private String parameterTypesString;
	private String parameter;
}
