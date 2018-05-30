package com.moekr.dubbo.agent.protocol.converter;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.POST;

public class HttpToAgentRequestConverter extends MessageToMessageDecoder<FullHttpRequest> {
	@Override
	protected void decode(ChannelHandlerContext context, FullHttpRequest httpRequest, List<Object> out) throws Exception {
		if (httpRequest.method() != POST)
			throw new IllegalArgumentException();
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);
		String interfaceName, methodName, parameterTypesString, parameter;
		try {
			if (decoder.isMultipart())
				throw new IllegalArgumentException();
			interfaceName = query(decoder, "interface");
			methodName = query(decoder, "method");
			parameterTypesString = query(decoder, "parameterTypesString");
			parameter = query(decoder, "parameter");
		} finally {
			decoder.destroy();
		}

		AgentRequest agentRequest = AgentRequest.newInstance();
		agentRequest.setInterfaceName(interfaceName);
		agentRequest.setMethodName(methodName);
		agentRequest.setParameterTypesString(parameterTypesString);
		agentRequest.setParameter(parameter);
		out.add(agentRequest);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		context.close();
	}

	private String query(HttpPostRequestDecoder decoder, String key) throws IOException {
		InterfaceHttpData data = decoder.getBodyHttpData(key);
		if (data instanceof Attribute) {
			return ((Attribute) data).getValue();
		}
		throw new IllegalArgumentException();
	}
}
