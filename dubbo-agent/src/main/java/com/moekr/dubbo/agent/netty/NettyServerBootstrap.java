package com.moekr.dubbo.agent.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

import java.util.concurrent.Executors;

public class NettyServerBootstrap {
	private final int port;

	@Getter
	private ServerSocketChannel channel;

	public NettyServerBootstrap(int port, ChannelHandler handler) {
		this.port = port;
		bind(handler);
	}

	private void bind(ChannelHandler handler) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(
				new NioEventLoopGroup(0, Executors.newCachedThreadPool()),
				new NioEventLoopGroup(0, Executors.newCachedThreadPool())
		);
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(handler);
		ChannelFuture future = bootstrap.bind(port).awaitUninterruptibly();
		if (future.isSuccess()) {
			channel = (ServerSocketChannel) future.channel();
		}
	}
}
