package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

import static com.moekr.dubbo.agent.protocol.DubboConstants.HEADER_LENGTH;
import static com.moekr.dubbo.agent.protocol.DubboConstants.MAGIC_NUMBER;
import static io.netty.util.CharsetUtil.UTF_8;

public class DubboToAgentResponseDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
		if (in.readableBytes() < HEADER_LENGTH) return;
		in.markReaderIndex();
		short magicNumber = in.readShort();
		if (magicNumber != MAGIC_NUMBER) {
			context.close();
			return;
		}
		@SuppressWarnings("unused")
		byte flag = in.readByte();
		@SuppressWarnings("unused")
		byte status = in.readByte();
		long id = in.readLong();
		int length = in.readInt();
		if (length < 0) {
			context.close();
			return;
		}
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}
		byte[] body = new byte[length];
		in.readBytes(body);
		body = Arrays.copyOfRange(body, 2, length - 1);
		AgentResponse response = new AgentResponse(id);
		response.setResult(new String(body, UTF_8));
		out.add(response);
	}
}
