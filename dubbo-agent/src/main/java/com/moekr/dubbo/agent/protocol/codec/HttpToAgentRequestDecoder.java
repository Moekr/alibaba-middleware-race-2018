package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.util.ByteProcessor.FIND_CR;
import static io.netty.util.CharsetUtil.UTF_8;

public class HttpToAgentRequestDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		Integer length = null;
		int previous = -1;
		while (true) {
			int index = in.forEachByte(FIND_CR);
			if (index == -1) {
				in.resetReaderIndex();
				return;
			}
			in.readerIndex(index + 2);
			if (previous != -1 && previous + 2 == index) {
				break;
			}
			if (length == null) {
				byte[] buffer = new byte[index - (previous + 2)];
				in.getBytes(previous + 2, buffer);
				String header = new String(buffer, UTF_8);
				if (StringUtils.startsWithIgnoreCase(header, CONTENT_LENGTH)) {
					length = Integer.valueOf(header.substring((CONTENT_LENGTH + ": ").length()));
				}
			}
			previous = index;
		}
		if (length == null) {
			context.close();
			return;
		}
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}
		byte[] buffer = new byte[length];
		in.readBytes(buffer);
		String body = new String(buffer, 0, buffer.length, UTF_8);
		Map<String, List<String>> form = new HashMap<>();
		String[] kvPairs = body.split("&");
		for (String kvPair : kvPairs) {
			int equalIndex = kvPair.indexOf('=');
			String key = URLDecoder.decode(kvPair.substring(0, equalIndex), UTF_8.name());
			String value = URLDecoder.decode(kvPair.substring(equalIndex + 1), UTF_8.name());
			form.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
		}
		AgentRequest request = AgentRequest.newInstance();
		request.setInterfaceName(form.get("interface").get(0));
		request.setMethodName(form.get("method").get(0));
		request.setParameterTypesString(form.get("parameterTypesString").get(0));
		request.setParameter(form.get("parameter").get(0));
		out.add(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		context.close();
	}
}
