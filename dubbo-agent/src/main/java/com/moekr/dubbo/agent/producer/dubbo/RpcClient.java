package com.moekr.dubbo.agent.producer.dubbo;

import com.moekr.dubbo.agent.producer.dubbo.model.*;
import com.moekr.dubbo.agent.registry.Registry;
import io.netty.channel.Channel;
import lombok.extern.apachecommons.CommonsLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

@CommonsLog
public class RpcClient {

	private ConnecManager connectManager;

	public RpcClient(Registry registry) {
		this.connectManager = new ConnecManager();
	}

	public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws IOException, InterruptedException {

		Channel channel = connectManager.getChannel();

		RpcInvocation invocation = new RpcInvocation();
		invocation.setMethodName(method);
		invocation.setAttachment("path", interfaceName);
		invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
		JsonUtils.writeObject(parameter, writer);
		invocation.setArguments(out.toByteArray());

		Request request = new Request();
		request.setVersion("2.0.0");
		request.setTwoWay(true);
		request.setData(invocation);

		log.info("requestId=" + request.getId());

		RpcFuture future = new RpcFuture();
		RpcRequestHolder.put(String.valueOf(request.getId()), future);

		channel.writeAndFlush(request);

		Object result = null;
		try {
			result = future.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
