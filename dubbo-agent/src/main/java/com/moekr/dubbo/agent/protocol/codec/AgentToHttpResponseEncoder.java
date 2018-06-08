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
		String bodyString = response.getResult();
		ByteBuf bodyPart = PooledByteBufAllocator.DEFAULT.ioBuffer(bodyString.length());
		bodyPart.writeCharSequence(bodyString, UTF_8);
		String dynamicString = CONTENT_LENGTH + COLON + bodyPart.readableBytes() + CRLF + CRLF;
		ByteBuf dynamicPart = PooledByteBufAllocator.DEFAULT.ioBuffer(dynamicString.length());
		dynamicPart.writeCharSequence(dynamicString, UTF_8);
		ByteBuf byteBuf = new CompositeByteBuf(PooledByteBufAllocator.DEFAULT, true, 3, staticPart.retain(), dynamicPart, bodyPart);
		context.write(byteBuf, promise);
	}

	@Override
	protected void finalize() {
		staticPart.release();
	}
}
