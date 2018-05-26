package com.moekr.dubbo.agent.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moekr.dubbo.agent.protocol.AbstractMessage;
import com.moekr.dubbo.agent.protocol.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class MessageEncoder extends MessageToByteEncoder<AbstractMessage> {
	private static final int MAGIC_NUMBER = 0x0CAFFEE0;

	private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected void encode(ChannelHandlerContext context, AbstractMessage message, ByteBuf out) throws Exception {
		ByteBufOutputStream writer = new ByteBufOutputStream(out);
		byte[] payload = OBJECT_MAPPER.writeValueAsBytes(message);
		writer.writeInt(MAGIC_NUMBER);
		writer.writeByte(message instanceof RequestMessage ? 0 : 1);
		writer.writeInt(payload.length);
		writer.write(payload);
	}
}
