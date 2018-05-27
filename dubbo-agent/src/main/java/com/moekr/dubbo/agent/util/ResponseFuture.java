package com.moekr.dubbo.agent.util;

import com.moekr.dubbo.agent.protocol.AbstractRequest;
import com.moekr.dubbo.agent.protocol.AbstractResponse;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ResponseFuture<Req extends AbstractRequest, Res extends AbstractResponse> implements Future<Res> {
	private final CountDownLatch latch = new CountDownLatch(1);

	@Getter
	private final Req request;
	private Res response;

	public ResponseFuture(Req request) {
		Objects.requireNonNull(request);
		this.request = request;
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
		return response != null;
	}

	@Override
	public Res get() throws InterruptedException {
		latch.await();
		return response;
	}

	@Override
	public Res get(long timeout, TimeUnit unit) throws InterruptedException {
		latch.await(timeout, unit);
		return response;
	}

	public void done(Res response) {
		Objects.requireNonNull(response);
		this.response = response;
		latch.countDown();
	}
}
