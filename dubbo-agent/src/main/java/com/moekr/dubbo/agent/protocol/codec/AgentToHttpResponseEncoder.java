package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.util.CharsetUtil.UTF_8;

public class AgentToHttpResponseEncoder extends ChannelOutboundHandlerAdapter {
	private static final String SPACE = " ";
	private static final String CRLF = "\r\n";
	private static final String COLON = ": ";

	private final ByteBuf staticPart;

	public AgentToHttpResponseEncoder() {
		String staticString = HTTP_1_1 + SPACE + OK + CRLF +
				CONTENT_TYPE + COLON + TEXT_PLAIN + CRLF +
				CONNECTION + COLON + KEEP_ALIVE + CRLF;
		byte[] staticBytes = staticString.getBytes(UTF_8);
		staticPart = PooledByteBufAllocator.DEFAULT.ioBuffer(staticBytes.length);
		staticPart.writeBytes(staticBytes);
	}

	@Override
	public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) {
		if (!(object instanceof AgentResponse)) {
			context.write(object, promise);
			return;
		}
		AgentResponse response = (AgentResponse) object;
		byte[] body = response.getResult().getBytes(UTF_8);
		String dynamicString = CONTENT_LENGTH + COLON + body.length + CRLF + CRLF;
		byte[] dynamicBytes = dynamicString.getBytes(UTF_8);
		ByteBuf dynamicPart = PooledByteBufAllocator.DEFAULT.ioBuffer(dynamicBytes.length);
		dynamicPart.writeBytes(dynamicBytes);
		dynamicPart.writeBytes(body);
		ByteBuf byteBuf = new CompositeByteBuf(PooledByteBufAllocator.DEFAULT, true, 2, staticPart.retain(), dynamicPart);
		context.write(byteBuf, promise);
	}

	@Override
	protected void finalize() {
		staticPart.release();
	}
}
