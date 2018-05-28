package com.moekr.dubbo.agent.provider;

import com.moekr.dubbo.agent.netty.NettyClientBootstrap;
import com.moekr.dubbo.agent.netty.NettyServerBootstrap;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.DubboRequest;
import com.moekr.dubbo.agent.protocol.DubboResponse;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.FutureHolder;
import com.moekr.dubbo.agent.util.ResponseFuture;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.moekr.dubbo.agent.util.Constants.*;

@Component
@ConditionalOnNotWebApplication
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
		int weight = Integer.valueOf(System.getProperty(PRIVIDER_WEIGHT_PROPERTY));
		channel = new NettyClientBootstrap(LOCAL_HOST, dubboPort, new DubboClientHandler()).getSocketChannel();
		new NettyServerBootstrap(serverPort, new AgentServerHandler(this::invoke));
		registry.register(SERVICE_NAME, serverPort, weight);
	}

	private byte[] invoke(AgentRequest request) {
		try {
			return invoke0(request);
		} catch (Exception e) {
			return new byte[0];
		}
	}

	private byte[] invoke0(AgentRequest agentRequest) throws InterruptedException {
		DubboRequest dubboRequest = DubboRequest.newInstance();
		dubboRequest.setInterfaceName(agentRequest.getInterfaceName());
		dubboRequest.setMethodName(agentRequest.getMethodName());
		dubboRequest.setParameterTypesString(agentRequest.getParameterTypesString());
		dubboRequest.setParameter(agentRequest.getParameter());

		ResponseFuture<DubboRequest, DubboResponse> future = new ResponseFuture<>(dubboRequest);
		FutureHolder.hold(future);
		channel.writeAndFlush(dubboRequest);
		DubboResponse response = future.get(TIMEOUT, TIMEOUT_UNIT);
		return response == null ? new byte[0] : response.getResult();
	}
}
