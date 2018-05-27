package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.DubboResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

import static com.moekr.dubbo.agent.protocol.DubboConstants.HEADER_LENGTH;
import static com.moekr.dubbo.agent.protocol.DubboConstants.MAGIC_NUMBER;

public class DubboResponseDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> list) {
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
		byte[] payload = new byte[length];
		in.readBytes(payload);
		payload = Arrays.copyOfRange(payload, 2, length - 1);
		DubboResponse response = new DubboResponse(id);
		response.setResult(payload);
		list.add(response);
	}
}
