package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;

import java.util.concurrent.Executors;

import static com.moekr.dubbo.agent.util.Constants.RETRY_DELAY;
import static com.moekr.dubbo.agent.util.Constants.RETRY_TIME;

public class NettyClientBootstrap {
	private final int port;
	private final String host;

	@Getter
	private SocketChannel socketChannel;

	public NettyClientBootstrap(String host, int port, ChannelHandler handler) {
		this.host = host;
		this.port = port;
		start(handler);
	}

	private void start(ChannelHandler handler) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(EpollSocketChannel.class);
		bootstrap.group(new EpollEventLoopGroup(4, Executors.newFixedThreadPool(4)));
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.remoteAddress(this.host, this.port);
		bootstrap.handler(handler);
		ChannelFuture future = bootstrap.connect().awaitUninterruptibly();
		int retryLeft = RETRY_TIME;
		while (!future.isSuccess() && retryLeft > 0) {
			ToolKit.sleep(RETRY_DELAY);
			future = bootstrap.connect().awaitUninterruptibly();
			retryLeft = retryLeft - 1;
		}
		socketChannel = (SocketChannel) future.channel();
	}
}
