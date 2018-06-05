package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.netty.ConsumerRequestSender;
import com.moekr.dubbo.agent.netty.NettyServerBootstrap;
import com.moekr.dubbo.agent.protocol.codec.AgentToHttpResponseEncoder;
import com.moekr.dubbo.agent.protocol.codec.HttpToAgentRequestDecoder;
import com.moekr.dubbo.agent.registry.Registry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.moekr.dubbo.agent.util.Constants.*;

@Component
@ConditionalOnProperty(name = AGENT_TYPE_PROPERTY, havingValue = CONSUMER_TYPE)
public class ConsumerAgent {
	private final Registry registry;

	@Autowired
	public ConsumerAgent(Registry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void initialize() {
		int serverPort = Integer.valueOf(System.getProperty(SERVER_PORT_PROPERTY));
		new NettyServerBootstrap(serverPort, new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) {
				channel.pipeline()
						.addLast(new AgentToHttpResponseEncoder())
						.addLast(new HttpToAgentRequestDecoder())
						.addLast(new ConsumerRequestSender(registry));
			}
		});
	}
}
