package com.moekr.dubbo.agent.producer;

import com.moekr.dubbo.agent.protocol.codec.MessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Data
public class NettyServerBootstrap {
	private int port;
	private ServerSocketChannel channel;

	public NettyServerBootstrap(int port) throws InterruptedException {
		this.port = port;
		bind();
	}

	private void bind() throws InterruptedException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), new NettyServerHandler());
			}
		});
		ChannelFuture future = bootstrap.bind(port).sync();
		if (future.isSuccess()) {
			channel = (ServerSocketChannel) future.channel();
		}
	}
}
