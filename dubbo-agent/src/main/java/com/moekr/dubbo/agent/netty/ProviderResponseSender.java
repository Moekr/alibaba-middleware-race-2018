package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.util.ContextHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProviderResponseSender extends SimpleChannelInboundHandler<AgentResponse> {
	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentResponse response) {
		Channel channel = ContextHolder.remove(response.getId());
		if (channel != null) {
			channel.writeAndFlush(response);
		}
	}
}
