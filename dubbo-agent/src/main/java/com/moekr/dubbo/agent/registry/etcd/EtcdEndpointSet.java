package com.moekr.dubbo.agent.registry.etcd;

import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.moekr.dubbo.agent.registry.Endpoint;
import com.moekr.dubbo.agent.registry.EndpointSet;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

class EtcdEndpointSet implements EndpointSet {
	private final Random random = new Random();

	private final Map<String, Endpoint> endpointMap = new LinkedHashMap<>();
	private final Watch.Watcher watcher;

	private int totalWeight = 0;

	EtcdEndpointSet(Collection<KeyValue> keyValues, Watch.Watcher watcher) {
		for (KeyValue keyValue : keyValues) {
			put(keyValue);
		}
		this.watcher = watcher;
		Executors.newSingleThreadExecutor().submit(this::watch);
	}

	@Override
	public Endpoint select() {
		if (totalWeight == 0) {
			throw new IllegalStateException("EndpointSet doesn't contain any endpoints");
		}
		Collection<Endpoint> endpoints = endpointMap.values();
		int randomWeight = random.nextInt(totalWeight);
		int currentTotalWeight = 0;
		for (Endpoint endpoint : endpoints) {
			currentTotalWeight = currentTotalWeight + endpoint.getWeight();
			if (randomWeight < currentTotalWeight) {
				return endpoint;
			}
		}
		// Shouldn't happen
		return null;
	}

	private void put(KeyValue keyValue) {
		String key = keyValue.getKey().toStringUtf8();
		String value = keyValue.getValue().toStringUtf8();
		Endpoint endpoint = endpointMap.get(key);
		int weight = Integer.valueOf(value);
		if (endpoint == null) {
			int index = key.lastIndexOf('/');
			String address = key.substring(index + 1);
			String[] content = address.split(":");
			String host = content[0];
			int port = Integer.valueOf(content[1]);
			endpoint = new Endpoint(host, port, weight);
			totalWeight = totalWeight + weight;
			endpointMap.put(key, endpoint);
		} else {
			totalWeight = totalWeight - endpoint.getWeight() + weight;
			endpoint.setWeight(weight);
		}
	}

	private void remove(KeyValue keyValue) {
		String key = keyValue.getKey().toStringUtf8();
		Endpoint endpoint = endpointMap.remove(key);
		if (endpoint != null) {
			totalWeight = totalWeight - endpoint.getWeight();
		}
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
}
