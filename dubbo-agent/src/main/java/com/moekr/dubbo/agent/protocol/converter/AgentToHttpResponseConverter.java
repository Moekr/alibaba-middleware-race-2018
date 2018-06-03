package com.moekr.dubbo.agent.protocol.converter;

import com.moekr.dubbo.agent.protocol.AgentResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.util.List;

import static com.moekr.dubbo.agent.util.Constants.HTTP_ID_HEADER;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class AgentToHttpResponseConverter extends MessageToMessageDecoder<AgentResponse> {
	@Override
	protected void decode(ChannelHandlerContext context, AgentResponse agentResponse, List<Object> out) {
		byte[] body = agentResponse.getResult().getBytes(CharsetUtil.UTF_8);
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(body.length).writeBytes(body);
		HttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
		httpResponse.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
		httpResponse.headers().set(CONTENT_TYPE, TEXT_PLAIN);
		httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
		httpResponse.headers().set(HTTP_ID_HEADER, agentResponse.getId());
		out.add(httpResponse);
	}
}
