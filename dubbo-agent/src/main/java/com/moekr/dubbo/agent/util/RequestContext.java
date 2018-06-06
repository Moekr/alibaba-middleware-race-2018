package com.moekr.dubbo.agent.util;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class RequestContext {
	private final long id;
	private final Channel channel;

	public RequestContext(long id, Channel channel) {
		this.id = id;
		this.channel = channel;
	}
}
