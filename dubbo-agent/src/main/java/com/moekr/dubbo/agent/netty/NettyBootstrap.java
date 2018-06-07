package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;

import static com.moekr.dubbo.agent.util.Constants.RETRY_DELAY;
import static com.moekr.dubbo.agent.util.Constants.RETRY_TIME;
import static io.netty.channel.ChannelOption.*;

public abstract class NettyBootstrap {
	public static SocketChannel connect(String host, int port, Class<? extends Channel> type, EventLoopGroup worker, ChannelHandler handler) throws IOException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(type);
		bootstrap.group(worker);
		bootstrap.option(SO_KEEPALIVE, true);
		bootstrap.option(TCP_NODELAY, true);
		bootstrap.remoteAddress(host, port);
		bootstrap.handler(handler);
		ChannelFuture future = bootstrap.connect().awaitUninterruptibly();
		int retryLeft = RETRY_TIME;
		while (!future.isSuccess() && retryLeft > 0) {
			ToolKit.sleep(RETRY_DELAY);
			future = bootstrap.connect().awaitUninterruptibly();
			retryLeft = retryLeft - 1;
		}
		if (future.isSuccess()) {
			return (SocketChannel) future.channel();
		} else {
			throw new IOException("Connect to " + host + ":" + port + " timeout.", future.cause());
		}
	}

	public static void bind(int port, Class<? extends ServerChannel> type, EventLoopGroup boss, EventLoopGroup worker, ChannelHandler handler) throws IOException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(type);
		bootstrap.group(boss, worker);
		bootstrap.option(SO_BACKLOG, 1024);
		bootstrap.childOption(SO_KEEPALIVE, true);
		bootstrap.childOption(TCP_NODELAY, true);
		bootstrap.localAddress(port);
		bootstrap.childHandler(handler);
		ChannelFuture future = bootstrap.bind().awaitUninterruptibly();
		if (!future.isSuccess()) {
			throw new IOException("Bind " + port + " timeout.", future.cause());
		}
	}
}
