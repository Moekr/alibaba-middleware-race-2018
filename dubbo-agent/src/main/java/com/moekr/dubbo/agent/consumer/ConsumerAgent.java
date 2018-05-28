package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.Constants;
import com.moekr.dubbo.agent.util.FutureHolder;
import com.moekr.dubbo.agent.util.ResponseFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@ConditionalOnWebApplication
public class ConsumerAgent {
	private final Registry registry;

	@Autowired
	public ConsumerAgent(Registry registry) {
		this.registry = registry;
	}

	@RequestMapping
	public String invoke(@RequestParam("interface") String interfaceName,
						 @RequestParam("method") String methodName,
						 @RequestParam("parameterTypesString") String parameterTypesString,
						 @RequestParam("parameter") String parameter) throws Exception {
		AgentRequest request = AgentRequest.newInstance();
		request.setInterfaceName(interfaceName);
		request.setMethodName(methodName);
		request.setParameterTypesString(parameterTypesString);
		request.setParameter(parameter);

		Endpoint endpoint = registry.find(interfaceName).select();
		ResponseFuture<AgentRequest, AgentResponse> future = new ResponseFuture<>(request);
		FutureHolder.hold(future);
		endpoint.getChannel().writeAndFlush(request);
		AgentResponse response = future.get(30, TimeUnit.SECONDS);
		if (response == null) return Constants.ERROR_RESULT;
		return response.getResult();
	}
}
