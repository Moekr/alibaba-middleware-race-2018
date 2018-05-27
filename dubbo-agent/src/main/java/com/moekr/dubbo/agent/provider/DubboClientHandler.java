package com.moekr.dubbo.agent.provider;

import com.moekr.dubbo.agent.netty.ResponseHandler;
import com.moekr.dubbo.agent.protocol.codec.DubboRequestEncoder;
import com.moekr.dubbo.agent.protocol.codec.DubboResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class DubboClientHandler extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new DubboResponseDecoder());
		pipeline.addLast(new DubboRequestEncoder());
		pipeline.addLast(new ResponseHandler());
	}
}
