package com.moekr.dubbo.agent;

import com.moekr.dubbo.agent.netty.AgentRequestSender;
import com.moekr.dubbo.agent.netty.NettyServerBootstrap;
import com.moekr.dubbo.agent.protocol.converter.HttpToAgentRequestConverter;
import com.moekr.dubbo.agent.registry.Registry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
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
						.addLast(new HttpRequestDecoder())
						.addLast(new HttpResponseEncoder())
						.addLast(new HttpObjectAggregator(1024 * 1024))
						.addLast(new HttpToAgentRequestConverter())
						.addLast(new AgentRequestSender(registry));
			}
		});
	}
}
