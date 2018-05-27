package com.moekr.dubbo.agent.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

public class NettyServerBootstrap {
	private final int port;

	@Getter
	private ServerSocketChannel channel;

	public NettyServerBootstrap(int port, ChannelHandler handler) throws InterruptedException {
		this.port = port;
		bind(handler);
	}

	private void bind(ChannelHandler handler) throws InterruptedException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(handler);
		ChannelFuture future = bootstrap.bind(port).sync();
		if (future.isSuccess()) {
			channel = (ServerSocketChannel) future.channel();
		}
	}
}
