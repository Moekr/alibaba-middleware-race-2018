package com.moekr.dubbo.agent.registry;

import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import lombok.extern.apachecommons.CommonsLog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@CommonsLog
class EtcdEndpointSet implements EndpointSet {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Watch.Watcher watcher;

	private Map<String, Endpoint> endpointMap;

	EtcdEndpointSet(Watch.Watcher watcher) {
		this.watcher = watcher;
	}

	void initialize(Collection<KeyValue> keyValues) {
		endpointMap = new HashMap<>();
		for (KeyValue keyValue : keyValues) {
			put(keyValue);
		}
		Executors.newSingleThreadExecutor().submit(this::watch);
	}

	@Override
	public Endpoint select() {
		lock.readLock().tryLock();
		try {
			return select0();
		} finally {
			lock.readLock().unlock();
		}
	}

	private Endpoint select0() {
		if (endpointMap == null) {
			throw new IllegalStateException("EndpointSet hasn't been initialized");
		}
		if (endpointMap.isEmpty()) {
			throw new IllegalStateException("EndpointSet doesn't contain any endpoints");
		}
		Endpoint selected = endpointMap.values().stream().min(Endpoint::compareTo).orElse(null);
		if (selected == null) {
			throw new IllegalStateException("EndpointSet doesn't contain available endpoints");
		}
		return selected;
	}

	private void watch() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				WatchResponse response = watcher.listen();
				for (WatchEvent event : response.getEvents()) {
					WatchEvent.EventType eventType = event.getEventType();
					if (eventType == WatchEvent.EventType.PUT) {
						put(event.getKeyValue());
					} else if (eventType == WatchEvent.EventType.DELETE) {
						remove(event.getPrevKV());
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		watcher.close();
	}

	private void put(KeyValue keyValue) {
		lock.writeLock().tryLock();
		try {
			put0(keyValue);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void put0(KeyValue keyValue) {
		String key = keyValue.getKey().toStringUtf8();
		int index = key.lastIndexOf('/');
		String address = key.substring(index + 1);
		String[] content = address.split(":");
		try {
			Endpoint endpoint = new Endpoint(content[0], Integer.valueOf(content[1]));
			endpointMap.put(key, endpoint);
		} catch (Exception e) {
			log.error("Failed to initialize endpoint", e);
		}
	}

	private void remove(KeyValue keyValue) {
		lock.writeLock().tryLock();
		try {
			remove0(keyValue);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void remove0(KeyValue keyValue) {
		String key = keyValue.getKey().toStringUtf8();
		endpointMap.remove(key);
	}
}
