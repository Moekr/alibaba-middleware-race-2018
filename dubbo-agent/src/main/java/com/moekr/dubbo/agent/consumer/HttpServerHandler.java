package com.moekr.dubbo.agent.consumer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.function.Function;

public class HttpServerHandler extends ChannelInitializer<SocketChannel> {
	private final Function<FullHttpRequest, String> invokeFunction;

	public HttpServerHandler(Function<FullHttpRequest, String> invokeFunction) {
		this.invokeFunction = invokeFunction;
	}

	@Override
	protected void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new HttpRequestDecoder());
		pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
		pipeline.addLast(new HttpResponseEncoder());
		pipeline.addLast(new HttpRequestHandler(invokeFunction));
	}
}
