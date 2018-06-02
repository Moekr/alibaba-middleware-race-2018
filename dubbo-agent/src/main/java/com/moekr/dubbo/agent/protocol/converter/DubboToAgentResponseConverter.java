package com.moekr.dubbo.agent.protocol.converter;

import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.protocol.DubboResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class DubboToAgentResponseConverter extends MessageToMessageDecoder<DubboResponse> {
	@Override
	protected void decode(ChannelHandlerContext context, DubboResponse dubboResponse, List<Object> out) {
		AgentResponse agentResponse = new AgentResponse(dubboResponse.getId());
		agentResponse.setResult(new String(dubboResponse.getResult(), CharsetUtil.UTF_8));
		out.add(agentResponse);
	}
}
