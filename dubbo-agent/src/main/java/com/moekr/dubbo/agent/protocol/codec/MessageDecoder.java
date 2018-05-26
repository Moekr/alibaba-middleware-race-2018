package com.moekr.dubbo.agent.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moekr.dubbo.agent.protocol.AbstractMessage;
import com.moekr.dubbo.agent.protocol.RequestMessage;
import com.moekr.dubbo.agent.protocol.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.apachecommons.CommonsLog;

import java.util.List;

@CommonsLog
public class MessageDecoder extends ByteToMessageDecoder {
	private static final int MAGIC_NUMBER = 0x0CAFFEE0;

	private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 9) return;
		in.markReaderIndex();
		int magicNumber = in.readInt();
		if (magicNumber != MAGIC_NUMBER) {
			context.close();
		} else {
			byte type = in.readByte();
			int length = in.readInt();
			if (length < 0) {
				context.close();
			} else if (in.readableBytes() < length) {
				in.resetReaderIndex();
			} else {
				byte[] payload = new byte[length];
				in.readBytes(payload);
				AbstractMessage message = null;
				if (type == 0) {
					message = OBJECT_MAPPER.readValue(payload, RequestMessage.class);
				} else if (type == 1) {
					message = OBJECT_MAPPER.readValue(payload, ResponseMessage.class);
				}
				if (message != null) {
					out.add(message);
				}
			}
		}
	}
}
