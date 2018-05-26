package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.protocol.RequestMessage;
import com.moekr.dubbo.agent.protocol.ResponseMessage;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.Constants;
import com.moekr.dubbo.agent.util.MessageFuture;
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
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setInterfaceName(interfaceName);
		requestMessage.setMethodName(methodName);
		requestMessage.setParameterTypesString(parameterTypesString);
		requestMessage.setParameter(parameter);

		Endpoint endpoint = registry.find(interfaceName).select();
		endpoint.increase();
		MessageFuture future = new MessageFuture(endpoint, requestMessage);
		MessageFutureHolder.hold(future);
		endpoint.getChannel().writeAndFlush(requestMessage);
		ResponseMessage responseMessage = future.get(30, TimeUnit.SECONDS);
		if (responseMessage == null) return Constants.ERROR_RESULT;
		return responseMessage.getResult();
	}
}
