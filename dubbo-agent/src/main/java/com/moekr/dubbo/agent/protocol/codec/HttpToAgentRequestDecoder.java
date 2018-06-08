package com.moekr.dubbo.agent.protocol.codec;

import com.moekr.dubbo.agent.protocol.AgentRequest;
import com.moekr.dubbo.agent.util.ToolKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.util.ByteProcessor.FIND_CR;
import static io.netty.util.CharsetUtil.UTF_8;

public class HttpToAgentRequestDecoder extends ByteToMessageDecoder {
	private boolean requestLine = false;
	private boolean header = false;
	private Integer length;

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		if (!decoderRequestLine(in)) return;
		if (!decoderHeader(in)) return;
		if (length == null) {
			context.close();
		} else {
			AgentRequest request = decoderBody(in);
			if (request != null) {
				out.add(request);
				reset();
			}
		}
	}

	private boolean decoderRequestLine(ByteBuf in) {
		if (requestLine) return true;
		int index = in.forEachByte(FIND_CR);
		if (index == -1) return false;
		return (requestLine = trySetReaderIndex(in, index + 2));
	}

	private boolean decoderHeader(ByteBuf in) {
		if (header) return true;
		while (true) {
			int beginIndex = in.readerIndex();
			int endIndex = in.forEachByte(FIND_CR);
			if (endIndex == -1) return false;
			if (trySetReaderIndex(in, endIndex + 2)) {
				if (beginIndex == endIndex) {
					return (header = true);
				}
				if (length == null) {
					byte[] buffer = new byte[endIndex - beginIndex];
					in.getBytes(beginIndex, buffer);
					String header = new String(buffer, UTF_8);
					if (StringUtils.startsWithIgnoreCase(header, CONTENT_LENGTH)) {
						length = Integer.valueOf(header.substring((CONTENT_LENGTH + ": ").length()));
					}
				}
			} else {
				return false;
			}
		}
	}

	private AgentRequest decoderBody(ByteBuf in) throws Exception {
		if (in.readableBytes() < length) return null;
		byte[] buffer = new byte[length];
		in.readBytes(buffer);
		String body = new String(buffer, UTF_8);
		Map<String, String> form = ToolKit.decodeForm(body);
		AgentRequest request = AgentRequest.newInstance();
		request.setInterfaceName(form.get("interface"));
		request.setMethodName(form.get("method"));
		request.setParameterTypesString(form.get("parameterTypesString"));
		request.setParameter(form.get("parameter"));
		request.setFullRequest(body);
		return request;
	}

	private boolean trySetReaderIndex(ByteBuf byteBuf, int index) {
		try {
			byteBuf.readerIndex(index);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	private void reset() {
		requestLine = false;
		header = false;
		length = null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		context.close();
	}
}
