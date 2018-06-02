package com.moekr.dubbo.agent.protocol.converter;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.DubboRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class AgentToDubboRequestConverter extends MessageToMessageDecoder<AgentRequest> {
	@Override
	protected void decode(ChannelHandlerContext context, AgentRequest agentRequest, List<Object> out) {
		DubboRequest dubboRequest = new DubboRequest(agentRequest.getId());
		dubboRequest.setInterfaceName(agentRequest.getInterfaceName());
		dubboRequest.setMethodName(agentRequest.getMethodName());
		dubboRequest.setParameterTypesString(agentRequest.getParameterTypesString());
		dubboRequest.setParameter(agentRequest.getParameter());
		out.add(dubboRequest);
	}
}
