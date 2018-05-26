package com.moekr.dubbo.agent.producer;

import com.moekr.dubbo.agent.producer.dubbo.RpcClient;
import com.moekr.dubbo.agent.protocol.RequestMessage;
import com.moekr.dubbo.agent.protocol.ResponseMessage;
import com.moekr.dubbo.agent.util.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommonsLog
public class NettyServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

	private final RpcClient rpcClient = new RpcClient(null);

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	protected void channelRead0(ChannelHandlerContext context, RequestMessage requestMessage) {
		executorService.submit(() -> {
			ResponseMessage responseMessage = new ResponseMessage(requestMessage.getSequence());
			try {
				byte[] bytes = (byte[]) rpcClient.invoke(
						requestMessage.getInterfaceName(),
						requestMessage.getMethodName(),
						requestMessage.getParameterTypesString(),
						requestMessage.getParameter());
				responseMessage.setResult(new String(bytes));
			} catch (Exception e) {
				responseMessage.setResult(Constants.ERROR_RESULT);
			}
			context.channel().writeAndFlush(responseMessage);
		});
	}
}
