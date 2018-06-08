package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentMessage;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.moekr.dubbo.agent.protocol.AgentConstants.MAGIC_NUMBER;
import static io.netty.util.CharsetUtil.UTF_8;

public class AgentMessageEncoder extends MessageToByteEncoder<AgentMessage> {
	@Override
	protected void encode(ChannelHandlerContext context, AgentMessage message, ByteBuf out) {
		byte[] payload = null;
		if (message instanceof AgentRequest) {
			payload = ((AgentRequest) message).getFullRequest().getBytes(UTF_8);
		} else if (message instanceof AgentResponse) {
			payload = ((AgentResponse) message).getResult().getBytes(UTF_8);
		}
		if (payload == null) return;
		out.writeInt(MAGIC_NUMBER);
		out.writeLong(message.getId());
		out.writeInt(payload.length);
		out.writeBytes(payload);
	}
}
