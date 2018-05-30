package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.protocol.AbstractRequest;
import com.moekr.dubbo.agent.util.ContextHolder;
import com.moekr.dubbo.agent.util.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;

import java.util.function.Supplier;

public class RequestSender extends SimpleChannelInboundHandler<AbstractRequest> {
	private final Supplier<SocketChannel> channelSupplier;

	public RequestSender(Supplier<SocketChannel> channelSupplier) {
		Assert.notNull(channelSupplier, "channelSupplier");
		this.channelSupplier = channelSupplier;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AbstractRequest request) {
		SocketChannel channel = channelSupplier.get();
		if (channel != null) {
			RequestContext requestContext = new RequestContext(request.getId(), context);
			ContextHolder.hold(requestContext);
			channel.writeAndFlush(request);
		}
	}
}
