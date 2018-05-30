package com.moekr.dubbo.agent.protocol.converter;

import com.moekr.dubbo.agent.protocol.DubboResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

import java.util.List;

import static com.moekr.dubbo.agent.util.Constants.HTTP_ID_HEADER;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class DubboToHttpResponseConverter extends MessageToMessageDecoder<DubboResponse> {
	@Override
	protected void decode(ChannelHandlerContext ctx, DubboResponse dubboResponse, List<Object> out) {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(dubboResponse.getResult());
		HttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
		httpResponse.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
		httpResponse.headers().set(CONTENT_TYPE, TEXT_PLAIN);
		httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
		httpResponse.headers().set(HTTP_ID_HEADER, dubboResponse.getId());
		out.add(httpResponse);
	}
}
