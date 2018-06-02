package com.moekr.dubbo.agent.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moekr.dubbo.agent.protocol.AgentMessage;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import static com.moekr.dubbo.agent.protocol.AgentConstants.*;

public class AgentMessageEncoder extends MessageToByteEncoder<AgentMessage> {
	private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected void encode(ChannelHandlerContext context, AgentMessage message, ByteBuf out) throws IOException {
		byte[] payload = OBJECT_MAPPER.writeValueAsBytes(message);
		ByteBufOutputStream writer = new ByteBufOutputStream(out);
		writer.writeInt(MAGIC_NUMBER);
		writer.writeByte(message instanceof AgentRequest ? REQUEST_TYPE : RESPONSE_TYPE);
		writer.writeInt(payload.length);
		writer.write(payload);
	}
}
