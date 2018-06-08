package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.util.ContextHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.Assert;

import java.util.function.Supplier;

public class ProviderRequestSender extends SimpleChannelInboundHandler<AgentRequest> {
	private final Supplier<Channel> channelSupplier;

	public ProviderRequestSender(Supplier<Channel> channelSupplier) {
		Assert.notNull(channelSupplier, "channelSupplier");
		this.channelSupplier = channelSupplier;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentRequest request) {
		Channel channel = channelSupplier.get();
		if (channel != null) {
			ContextHolder.hold(request.getId(), context.channel());
			channel.writeAndFlush(request);
		}
	}
}
