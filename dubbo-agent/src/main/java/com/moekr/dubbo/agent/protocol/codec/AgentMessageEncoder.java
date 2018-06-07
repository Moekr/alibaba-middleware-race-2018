package com.moekr.dubbo.agent.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moekr.dubbo.agent.protocol.AgentMessage;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import static com.moekr.dubbo.agent.protocol.AgentConstants.*;

public class AgentMessageEncoder extends MessageToByteEncoder<AgentMessage> {
	private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected void encode(ChannelHandlerContext context, AgentMessage message, ByteBuf out) throws IOException {
		byte[] payload = OBJECT_MAPPER.writeValueAsBytes(message);
		out.writeInt(MAGIC_NUMBER);
		out.writeByte(message instanceof AgentRequest ? REQUEST_TYPE : RESPONSE_TYPE);
		out.writeInt(payload.length);
		out.writeBytes(payload);
	}
}
