package com.moekr.dubbo.agent.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.moekr.dubbo.agent.protocol.DubboConstants.DUBBO_VERSION;

@Data
@EqualsAndHashCode(callSuper = true)
public class DubboRequest extends AbstractRequest {
	private String dubboVersion = DUBBO_VERSION;
	private String interfaceName;
	private String version;
	private String methodName;
	private String parameterTypesString;
	private String parameter;

	private boolean twoWay = true;
	private boolean event = false;

	public static DubboRequest newInstance() {
		DubboRequest request = new DubboRequest();
		request.setId(SEQUENCE.incrementAndGet());
		return request;
	}
}
