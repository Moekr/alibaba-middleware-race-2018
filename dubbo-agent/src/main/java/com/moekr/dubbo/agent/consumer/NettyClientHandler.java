package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.protocol.ResponseMessage;
import com.moekr.dubbo.agent.util.MessageFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class NettyClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {
	@Override
	protected void channelRead0(ChannelHandlerContext context, ResponseMessage responseMessage) {
		MessageFuture future = MessageFutureHolder.remove(responseMessage.getSequence());
		if (future != null) {
			future.getEndpoint().decrease();
			future.setResponseMessage(responseMessage);
		}
	}
}
