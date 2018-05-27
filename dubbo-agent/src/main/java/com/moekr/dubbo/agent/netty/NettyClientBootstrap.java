package com.moekr.dubbo.agent.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

public class NettyClientBootstrap {
	private final int port;
	private final String host;

	@Getter
	private SocketChannel socketChannel;

	public NettyClientBootstrap(String host, int port, ChannelHandler handler) throws InterruptedException {
		this.host = host;
		this.port = port;
		start(handler);
	}

	private void start(ChannelHandler handler) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.remoteAddress(this.host, this.port);
		bootstrap.handler(handler);
		ChannelFuture future = bootstrap.connect().sync();
		if (future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
		}
	}
}
