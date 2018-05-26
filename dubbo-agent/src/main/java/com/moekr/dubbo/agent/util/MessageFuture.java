package com.moekr.dubbo.agent.util;

import com.moekr.dubbo.agent.protocol.RequestMessage;
import com.moekr.dubbo.agent.protocol.ResponseMessage;
import com.moekr.dubbo.agent.registry.Endpoint;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Data
public class MessageFuture implements Future<ResponseMessage> {
	private Endpoint endpoint;
	private RequestMessage requestMessage;
	private ResponseMessage responseMessage;
	private CountDownLatch latch;

	public MessageFuture(Endpoint endpoint, RequestMessage requestMessage) {
		this.endpoint = endpoint;
		this.requestMessage = requestMessage;
		this.latch = new CountDownLatch(1);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return responseMessage != null;
	}

	@Override
	public ResponseMessage get() throws InterruptedException {
		latch.await();
		return responseMessage;
	}

	@Override
	public ResponseMessage get(long timeout, TimeUnit unit) throws InterruptedException {
		latch.await(timeout, unit);
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
		latch.countDown();
	}
}
