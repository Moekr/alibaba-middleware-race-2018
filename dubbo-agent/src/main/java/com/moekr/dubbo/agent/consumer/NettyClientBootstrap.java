package com.moekr.dubbo.agent.consumer;

import com.moekr.dubbo.agent.protocol.codec.MessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

@Data
public class NettyClientBootstrap {
	private final int port;
	private final String host;

	private SocketChannel socketChannel;

	public NettyClientBootstrap(String host, int port) throws InterruptedException {
		this.host = host;
		this.port = port;
		start();
	}

	private void start() throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.remoteAddress(this.host, this.port);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), new NettyClientHandler());
			}
		});
		ChannelFuture future = bootstrap.connect().sync();
		if (future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
		}
	}
}
