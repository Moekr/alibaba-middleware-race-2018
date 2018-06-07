package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.netty.NettyBootstrap;
import com.moekr.dubbo.agent.netty.ProviderRequestSender;
import com.moekr.dubbo.agent.netty.ProviderResponseSender;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import com.moekr.dubbo.agent.protocol.codec.AgentToDubboRequestEncoder;
import com.moekr.dubbo.agent.protocol.codec.DubboToAgentResponseDecoder;
import com.moekr.dubbo.agent.registry.Registry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.moekr.dubbo.agent.util.Constants.*;

@Component
@ConditionalOnProperty(name = AGENT_TYPE_PROPERTY, havingValue = PROVIDER_TYPE)
public class ProviderAgent {
	private final Registry registry;

	private Channel channel;

	@Autowired
	public ProviderAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() throws Exception {
		int dubboPort = Integer.valueOf(System.getProperty(DUBBO_PORT_PROPERTY));
		int serverPort = Integer.valueOf(System.getProperty(SERVER_PORT_PROPERTY));
		int weight = Integer.valueOf(System.getProperty(PROVIDER_WEIGHT_PROPERTY));

		channel = NettyBootstrap.connect(
				LOCAL_HOST,
				dubboPort,
				EpollSocketChannel.class,
				new EpollEventLoopGroup(1),
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel) {
						channel.pipeline()
								.addLast(new AgentToDubboRequestEncoder())
								.addLast(new DubboToAgentResponseDecoder())
								.addLast(new ProviderResponseSender());
					}
				}
		);

		NettyBootstrap.bind(
				serverPort,
				EpollServerSocketChannel.class,
				new EpollEventLoopGroup(BOSS_THREAD),
				new EpollEventLoopGroup(WORKER_THREAD),
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel) {
						channel.pipeline()
								.addLast(new AgentMessageEncoder())
								.addLast(new AgentMessageDecoder())
								.addLast(new ProviderRequestSender(() -> ProviderAgent.this.channel));
					}
				}
		);

		registry.register(SERVICE_NAME, serverPort, weight);
	}
}
