package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentMessage;
import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.protocol.AgentResponse;
import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.moekr.dubbo.agent.protocol.AgentConstants.HEADER_LENGTH;
import static com.moekr.dubbo.agent.protocol.AgentConstants.MAGIC_NUMBER;
import static io.netty.util.CharsetUtil.UTF_8;

public class AgentMessageDecoder extends ByteToMessageDecoder {
	private final Class<? extends AgentMessage> targetClass;

	public AgentMessageDecoder(Class<? extends AgentMessage> targetClass) {
		Assert.notNull(targetClass, "Target class can't be null.");
		this.targetClass = targetClass;
	}

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEADER_LENGTH) return;
		in.markReaderIndex();
		int magicNumber = in.readInt();
		if (magicNumber != MAGIC_NUMBER) {
			context.close();
			return;
		}
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
		AgentMessage message = null;
		if (targetClass == AgentRequest.class) {
			message = decodeRequest(id, payload);
		} else if (targetClass == AgentResponse.class) {
			message = decodeResponse(id, payload);
		}
		if (message != null) {
			out.add(message);
		} else {
			context.close();
		}
	}

	private AgentRequest decodeRequest(long id, byte[] payload) throws Exception {
		AgentRequest request = new AgentRequest(id);
		String body = new String(payload, UTF_8);
		Map<String, String> form = ToolKit.decodeForm(body);
		request.setInterfaceName(form.get("interface"));
		request.setMethodName(form.get("method"));
		request.setParameterTypesString(form.get("parameterTypesString"));
		request.setParameter(form.get("parameter"));
		request.setFullRequest(body);
		return request;
	}

	private AgentResponse decodeResponse(long id, byte[] payload) {
		AgentResponse response = new AgentResponse(id);
		response.setResult(new String(payload, UTF_8));
		return response;
	}
}
