package com.moekr.dubbo.agent.provider;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@CommonsLog
public class AgentRequestHandler extends SimpleChannelInboundHandler<AgentRequest> {
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final Function<AgentRequest, byte[]> invokeFunction;

	public AgentRequestHandler(Function<AgentRequest, byte[]> invokeFunction) {
		this.invokeFunction = invokeFunction;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, AgentRequest request) {
		executorService.submit(() -> {
			AgentResponse response = new AgentResponse(request);
			try {
				byte[] bytes = invokeFunction.apply(request);
				response.setResult(new String(bytes));
			} catch (Exception e) {
				response.setResult(Constants.ERROR_RESULT);
			}
			context.channel().writeAndFlush(response);
		});
	}
}
