package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.netty.ConsumerResponseSender;
import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(exclude = "channelThreadLocal")
@ToString(exclude = "channelThreadLocal")
public class Endpoint {
	private final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

	private final String host;
	private final int port;

	@Getter
	@Setter
	private int weight;

	public Endpoint(String host, int port, int weight) {
		this.host = host;
		this.port = port;
		this.weight = weight;
	}

	public Channel getChannel() {
		Channel channel = channelThreadLocal.get();
		if (channel == null) {
			channel = new NettyClientBootstrap(host, port, new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) {
					channel.pipeline()
							.addLast(new AgentMessageEncoder())
							.addLast(new AgentMessageDecoder())
							.addLast(new ConsumerResponseSender());
				}
			}).getSocketChannel();
			channelThreadLocal.set(channel);
		}
		return channel;
	}
}
