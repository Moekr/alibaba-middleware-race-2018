package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.netty.AgentResponseSender;
import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
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
						.addLast(new AgentMessageEncoder())
						.addLast(new AgentMessageDecoder())
						.addLast(new AgentResponseSender());
			}
		}).getSocketChannel();
	}
}
