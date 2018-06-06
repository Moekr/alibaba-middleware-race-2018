package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.util.ContextHolder;
import com.moekr.dubbo.agent.util.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;

import java.util.function.Supplier;

public class ProviderRequestSender extends SimpleChannelInboundHandler<AgentRequest> {
	private final Supplier<SocketChannel> channelSupplier;

	public ProviderRequestSender(Supplier<SocketChannel> channelSupplier) {
		Assert.notNull(channelSupplier, "channelSupplier");
		this.channelSupplier = channelSupplier;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentRequest request) {
		SocketChannel channel = channelSupplier.get();
		if (channel != null) {
			RequestContext requestContext = new RequestContext(request.getId(), context.channel());
			ContextHolder.hold(requestContext);
			channel.writeAndFlush(request);
		}
	}
}
