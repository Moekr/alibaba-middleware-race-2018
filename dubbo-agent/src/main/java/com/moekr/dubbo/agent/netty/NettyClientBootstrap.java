package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

import java.util.concurrent.Executors;

import static com.moekr.dubbo.agent.util.Constants.RETRY_DELAY;
import static com.moekr.dubbo.agent.util.Constants.RETRY_TIME;

public class NettyClientBootstrap {
	private final String host;
	private final int port;
	private final boolean useEpoll;

	@Getter
	private SocketChannel socketChannel;

	public NettyClientBootstrap(String host, int port, boolean useEpoll, ChannelHandler handler) {
		this.host = host;
		this.port = port;
		this.useEpoll = useEpoll;
		start(handler);
	}

	private void start(ChannelHandler handler) {
		Bootstrap bootstrap = new Bootstrap();
		if (useEpoll) {
			bootstrap.channel(EpollSocketChannel.class);
			bootstrap.group(new EpollEventLoopGroup(0, Executors.newCachedThreadPool()));
		} else {
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.group(new NioEventLoopGroup(0, Executors.newCachedThreadPool()));
		}
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
