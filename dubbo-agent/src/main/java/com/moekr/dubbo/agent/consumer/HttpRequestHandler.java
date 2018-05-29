package com.moekr.dubbo.agent.consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.util.MimeTypeUtils;

import java.util.function.Function;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final Function<FullHttpRequest, String> invokeFunction;

	public HttpRequestHandler(Function<FullHttpRequest, String> invokeFunction) {
		this.invokeFunction = invokeFunction;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, FullHttpRequest request) {
		String content = invokeFunction.apply(request);
		ByteBuf byteBuf = Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8));
		HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		context.writeAndFlush(response).awaitUninterruptibly();
	}
}
