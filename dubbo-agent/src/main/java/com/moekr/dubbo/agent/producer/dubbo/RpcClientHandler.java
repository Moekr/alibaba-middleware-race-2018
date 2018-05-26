package com.moekr.dubbo.agent.producer.dubbo;

import com.moekr.dubbo.agent.producer.dubbo.model.RpcFuture;
import com.moekr.dubbo.agent.producer.dubbo.model.RpcRequestHolder;
import com.moekr.dubbo.agent.producer.dubbo.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
		String requestId = response.getRequestId();
		RpcFuture future = RpcRequestHolder.get(requestId);
		if (null != future) {
			RpcRequestHolder.remove(requestId);
			future.done(response);
		}
	}
}
