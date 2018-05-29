package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.consumer.AgentClientHandler;
import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Endpoint {
	private final SocketChannel channel;

	private final String host;
	private final int port;

	@Setter
	private int weight;

	public Endpoint(String host, int port, int weight) {
		this.host = host;
		this.port = port;
		this.weight = weight;

		this.channel = new NettyClientBootstrap(host, port, new AgentClientHandler()).getSocketChannel();
	}
}
