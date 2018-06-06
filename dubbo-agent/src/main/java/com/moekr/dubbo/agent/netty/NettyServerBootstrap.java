package com.moekr.dubbo.agent.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
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
		bootstrap.channel(EpollServerSocketChannel.class);
		bootstrap.group(
				new EpollEventLoopGroup(1, Executors.newFixedThreadPool(1)),
				new EpollEventLoopGroup(4, Executors.newFixedThreadPool(4))
		);
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(handler);
		ChannelFuture future = bootstrap.bind(port).awaitUninterruptibly();
		if (future.isSuccess()) {
			channel = (ServerSocketChannel) future.channel();
		}
	}
}
