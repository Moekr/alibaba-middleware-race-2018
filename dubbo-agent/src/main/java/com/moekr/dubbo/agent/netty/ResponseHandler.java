package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AbstractResponse;
import com.moekr.dubbo.agent.util.FutureHolder;
import com.moekr.dubbo.agent.util.ResponseFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseHandler extends SimpleChannelInboundHandler<AbstractResponse> {
	@Override
	@SuppressWarnings("unchecked")
	protected void channelRead0(ChannelHandlerContext context, AbstractResponse response) {
		ResponseFuture future = FutureHolder.remove(response.getId());
		if (future != null) {
			future.done(response);
		}
	}
}
