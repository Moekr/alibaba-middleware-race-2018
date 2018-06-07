package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.netty.ConsumerResponseSender;
import com.moekr.dubbo.agent.netty.NettyBootstrap;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import com.moekr.dubbo.agent.util.ContextHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.*;

import java.io.IOException;

import static com.moekr.dubbo.agent.util.Constants.BOSS_THREAD;

@Data
@EqualsAndHashCode(exclude = "channelThreadLocal")
@ToString(exclude = "channelThreadLocal")
public class Endpoint {
	@Getter(AccessLevel.PRIVATE)
	private final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

	private final String host;
	private final int port;

	private int weight;

	public Endpoint(String host, int port, int weight) {
		this.host = host;
		this.port = port;
		this.weight = weight;
	}

	public Channel channel() {
		try {
			return channel0();
		} catch (IOException e) {
			throw new IllegalStateException("No available channel.", e);
		}
	}

	private Channel channel0() throws IOException {
		Channel channel = channelThreadLocal.get();
		if (channel == null) {
			channel = NettyBootstrap.connect(
					host,
					port,
					EpollSocketChannel.class,
					new EpollEventLoopGroup(BOSS_THREAD),
					new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel channel) {
							channel.pipeline()
									.addLast(new AgentMessageEncoder())
									.addLast(new AgentMessageDecoder())
									.addLast(new ConsumerResponseSender());
						}
					}
			);
			channelThreadLocal.set(channel);
			ContextHolder.register(this, channel);
		}
		return channel;
	}
}
