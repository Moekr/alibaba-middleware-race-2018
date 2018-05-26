package com.moekr.dubbo.agent.registry;

import com.moekr.dubbo.agent.consumer.NettyClientBootstrap;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint implements Comparable<Endpoint> {
	@Getter
	private final String host;
	@Getter
	private final int port;
	@Getter
	private final SocketChannel channel;
	private AtomicInteger count = new AtomicInteger(0);

	public Endpoint(String host, int port) throws InterruptedException {
		this.host = host;
		this.port = port;
		this.channel = new NettyClientBootstrap(host, port).getSocketChannel();
	}

	public void increase() {
		count.incrementAndGet();
	}

	public void decrease() {
		count.decrementAndGet();
	}

	@Override
	public int compareTo(Endpoint endpoint) {
		return count.get() - endpoint.count.get();
	}
}
