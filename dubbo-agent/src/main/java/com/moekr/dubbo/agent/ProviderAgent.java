package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import com.moekr.dubbo.agent.netty.NettyServerBootstrap;
import com.moekr.dubbo.agent.netty.ProviderRequestSender;
import com.moekr.dubbo.agent.netty.ProviderResponseSender;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageDecoder;
import com.moekr.dubbo.agent.protocol.codec.AgentMessageEncoder;
import com.moekr.dubbo.agent.protocol.codec.AgentToDubboRequestEncoder;
import com.moekr.dubbo.agent.protocol.codec.DubboToAgentResponseDecoder;
import com.moekr.dubbo.agent.registry.Registry;
import io.netty.channel.ChannelInitializer;
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

	private SocketChannel channel;

	@Autowired
	public ProviderAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() throws Exception {
		int dubboPort = Integer.valueOf(System.getProperty(DUBBO_PORT_PROPERTY));
		int serverPort = Integer.valueOf(System.getProperty(SERVER_PORT_PROPERTY));
		int weight = Integer.valueOf(System.getProperty(PROVIDER_WEIGHT_PROPERTY));
		channel = new NettyClientBootstrap(LOCAL_HOST, dubboPort, new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline()
						.addLast(new AgentToDubboRequestEncoder())
						.addLast(new DubboToAgentResponseDecoder())
						.addLast(new ProviderResponseSender());
			}
		}).getSocketChannel();
		new NettyServerBootstrap(serverPort, new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline()
						.addLast(new AgentMessageEncoder())
						.addLast(new AgentMessageDecoder())
						.addLast(new ProviderRequestSender(() -> ProviderAgent.this.channel));
			}
		});
		registry.register(SERVICE_NAME, serverPort, weight);
	}
}
