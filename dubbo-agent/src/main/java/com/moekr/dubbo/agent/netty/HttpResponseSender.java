package com.moekr.dubbo.agent.netty;

import com.moekr.dubbo.agent.util.ContextHolder;
import com.moekr.dubbo.agent.util.RequestContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

import static com.moekr.dubbo.agent.util.Constants.HTTP_ID_HEADER;

public class HttpResponseSender extends SimpleChannelInboundHandler<FullHttpResponse> {
	public HttpResponseSender() {
		super(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, FullHttpResponse response) {
		Integer id = response.headers().getInt(HTTP_ID_HEADER);

		if (id != null) {
			RequestContext requestContext = ContextHolder.remove(id);
			if (requestContext != null) {
				ContextHolder.decrease(context.channel());
				requestContext.getContext().writeAndFlush(response);
			}
		}
	}
}
