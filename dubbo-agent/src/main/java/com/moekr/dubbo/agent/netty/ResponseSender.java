package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AbstractResponse;
import com.moekr.dubbo.agent.util.ContextHolder;
import com.moekr.dubbo.agent.util.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseSender extends SimpleChannelInboundHandler<AbstractResponse> {
	@Override
	protected void channelRead0(ChannelHandlerContext context, AbstractResponse response) {
		RequestContext requestContext = ContextHolder.remove(response.getId());
		if (requestContext != null) {
			requestContext.getContext().writeAndFlush(response);
		}
	}
}
