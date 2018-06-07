package com.moekr.dubbo.agent.protocol.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moekr.dubbo.agent.protocol.AgentMessage;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.apachecommons.CommonsLog;

import java.util.List;

import static com.moekr.dubbo.agent.protocol.AgentConstants.*;

@CommonsLog
public class AgentMessageDecoder extends ByteToMessageDecoder {

	private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEADER_LENGTH) return;
		in.markReaderIndex();
		int magicNumber = in.readInt();
		if (magicNumber != MAGIC_NUMBER) {
			context.close();
			return;
		}
		byte type = in.readByte();
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
		AgentMessage message = null;
		if (type == REQUEST_TYPE) {
			message = OBJECT_MAPPER.readValue(payload, AgentRequest.class);
		} else if (type == RESPONSE_TYPE) {
			message = OBJECT_MAPPER.readValue(payload, AgentResponse.class);
		}
		if (message != null) {
			out.add(message);
			log.info(message.getId() + " " + System.nanoTime());
		} else {
			context.close();
		}
	}
}
