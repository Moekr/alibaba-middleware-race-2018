package com.moekr.dubbo.agent.util;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public class RequestContext {
	private final long id;
	private final ChannelHandlerContext context;

	public RequestContext(long id, ChannelHandlerContext context) {
		this.id = id;
		this.context = context;
	}
}
