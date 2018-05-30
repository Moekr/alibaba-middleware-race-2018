package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.netty.HttpResponseSender;
import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import com.moekr.dubbo.agent.protocol.codec.DubboRequestEncoder;
import com.moekr.dubbo.agent.protocol.codec.DubboResponseDecoder;
import com.moekr.dubbo.agent.protocol.converter.DubboToHttpResponseConverter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;

@Data
public class Endpoint {
	private final SocketChannel channel;

	private final String host;
	private final int port;

	private int weight;

	public Endpoint(String host, int port, int weight) {
		this.host = host;
		this.port = port;
		this.weight = weight;

		this.channel = new NettyClientBootstrap(host, port, new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline()
						.addLast(new DubboRequestEncoder())
						.addLast(new DubboResponseDecoder())
						.addLast(new DubboToHttpResponseConverter())
						.addLast(new HttpResponseSender());
			}
		}).getSocketChannel();
	}
}
