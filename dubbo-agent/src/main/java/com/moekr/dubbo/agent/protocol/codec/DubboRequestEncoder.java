package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.DubboRequest;
import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static com.moekr.dubbo.agent.protocol.DubboConstants.*;

public class DubboRequestEncoder extends MessageToByteEncoder<DubboRequest> {
	@Override
	protected void encode(ChannelHandlerContext context, DubboRequest dubboRequest, ByteBuf out) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ToolKit.writeObject(dubboRequest.getDubboVersion(), outputStream);
		ToolKit.writeObject(dubboRequest.getInterfaceName(), outputStream);
		ToolKit.writeObject(dubboRequest.getVersion(), outputStream);
		ToolKit.writeObject(dubboRequest.getMethodName(), outputStream);
		ToolKit.writeObject(dubboRequest.getParameterTypesString(), outputStream);
		ToolKit.writeObject(dubboRequest.getParameter(), outputStream);
		ToolKit.writeObject(Collections.emptyMap(), outputStream);

		out.writeShort(MAGIC_NUMBER);
		byte flag = FLAG_REQUEST | FAST_JSON_SERIALIZATION_ID;
		if (dubboRequest.isTwoWay()) flag |= FLAG_TWO_WAY;
		if (dubboRequest.isEvent()) flag |= FLAG_EVENT;
		out.writeByte(flag);
		out.writeByte(0);
		out.writeLong(dubboRequest.getId());
		out.writeInt(outputStream.size());
		out.writeBytes(outputStream.toByteArray());
	}
}
