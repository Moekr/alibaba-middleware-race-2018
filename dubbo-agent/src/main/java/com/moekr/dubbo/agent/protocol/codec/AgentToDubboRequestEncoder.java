package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static com.moekr.dubbo.agent.protocol.DubboConstants.*;

public class AgentToDubboRequestEncoder extends ChannelOutboundHandlerAdapter {
	@Override
	public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) throws IOException {
		if (!(object instanceof AgentRequest)) {
			context.write(object, promise);
			return;
		}
		AgentRequest request = (AgentRequest) object;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ToolKit.writeObject(DUBBO_VERSION, outputStream);
		ToolKit.writeObject(request.getInterfaceName(), outputStream);
		ToolKit.writeObject(null, outputStream);
		ToolKit.writeObject(request.getMethodName(), outputStream);
		ToolKit.writeObject(request.getParameterTypesString(), outputStream);
		ToolKit.writeObject(request.getParameter(), outputStream);
		ToolKit.writeObject(Collections.emptyMap(), outputStream);
		byte[] body = outputStream.toByteArray();
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.ioBuffer(HEADER_LENGTH + body.length);
		byteBuf.writeShort(MAGIC_NUMBER);
		byteBuf.writeByte(FLAG_REQUEST | FAST_JSON_SERIALIZATION_ID | FLAG_TWO_WAY);
		byteBuf.writeByte(0);
		byteBuf.writeLong(request.getId());
		byteBuf.writeInt(body.length);
		byteBuf.writeBytes(body);
		context.write(byteBuf, promise);
	}
}
