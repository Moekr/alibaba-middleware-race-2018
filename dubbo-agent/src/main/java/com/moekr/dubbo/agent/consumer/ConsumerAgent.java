package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.netty.NettyServerBootstrap;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.FutureHolder;
import com.moekr.dubbo.agent.util.ResponseFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.moekr.dubbo.agent.util.Constants.*;

@Component
@ConditionalOnProperty(name = AGENT_TYPE_PROPERTY, havingValue = CONSUMER_TYPE)
public class ConsumerAgent {
	private final Registry registry;

	@Autowired
	public ConsumerAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() {
		int serverPort = Integer.valueOf(System.getProperty(SERVER_PORT_PROPERTY));
		new NettyServerBootstrap(serverPort, 384, new HttpServerHandler(this::invoke));
	}

	private String invoke(FullHttpRequest httpRequest) {
		try {
			return invoke0(httpRequest);
		} catch (Exception e) {
			return ERROR_RESULT;
		}
	}

	private String invoke0(FullHttpRequest httpRequest) throws Exception {
		if (httpRequest.method() != HttpMethod.POST) return ERROR_RESULT;
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);
		String interfaceName, methodName, parameterTypesString, parameter;
		try {
			if (decoder.isMultipart()) return ERROR_RESULT;
			interfaceName = query(decoder, "interface");
			methodName = query(decoder, "method");
			parameterTypesString = query(decoder, "parameterTypesString");
			parameter = query(decoder, "parameter");
		} catch (Exception e) {
			return ERROR_RESULT;
		} finally {
			decoder.destroy();
		}

		AgentRequest request = AgentRequest.newInstance();
		request.setInterfaceName(interfaceName);
		request.setMethodName(methodName);
		request.setParameterTypesString(parameterTypesString);
		request.setParameter(parameter);

		Endpoint endpoint = registry.find(interfaceName).select();
		ResponseFuture<AgentRequest, AgentResponse> future = new ResponseFuture<>(request);
		FutureHolder.hold(future);
		endpoint.getChannel().writeAndFlush(request);
		AgentResponse response = future.get(TIMEOUT, TIMEOUT_UNIT);
		if (response == null) return ERROR_RESULT;
		return response.getResult();
	}

	private String query(HttpPostRequestDecoder decoder, String key) throws IOException {
		InterfaceHttpData data = decoder.getBodyHttpData(key);
		if (data instanceof Attribute) {
			return ((Attribute) data).getValue();
		}
		throw new IllegalArgumentException();
	}
}
