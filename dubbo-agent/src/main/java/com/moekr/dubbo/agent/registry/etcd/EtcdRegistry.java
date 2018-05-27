package com.moekr.dubbo.agent.registry.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.moekr.dubbo.agent.registry.EndpointSet;
import com.moekr.dubbo.agent.registry.Registry;
import com.moekr.dubbo.agent.util.ToolKit;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static com.moekr.dubbo.agent.util.Constants.ETCD_ADDRESS_PROPERTY;
import static com.moekr.dubbo.agent.util.Constants.ETCD_ROOT_PATH;

@Component
public class EtcdRegistry implements Registry {
	private final Watch watch;
	private final Lease lease;
	private final KV kv;
	private final long leaseId;
	private final Map<String, EndpointSet> endpointSetMap = new HashMap<>();

	public EtcdRegistry() throws ExecutionException, InterruptedException {
		String address = System.getProperty(ETCD_ADDRESS_PROPERTY);
		Client client = Client.builder().endpoints(address).build();
		this.watch = client.getWatchClient();
		this.lease = client.getLeaseClient();
		this.kv = client.getKVClient();
		this.leaseId = lease.grant(30).get().getID();
		Executors.newSingleThreadExecutor().submit(this::keepAlive);
	}

	@Override
	public void register(String serviceName, int port) throws ExecutionException, InterruptedException, UnknownHostException {
		String key = MessageFormat.format("/{0}/{1}/{2}:{3}", ETCD_ROOT_PATH, serviceName, ToolKit.currentIpAddress(), String.valueOf(port));
		ByteSequence keyByteSequence = ByteSequence.fromString(key);
		ByteSequence valueByteSequence = ByteSequence.fromString("");
		kv.put(keyByteSequence, valueByteSequence, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
	}

	@Override
	public EndpointSet find(String serviceName) throws ExecutionException, InterruptedException {
		if (!endpointSetMap.containsKey(serviceName)) {
			synchronized (endpointSetMap) {
				if (!endpointSetMap.containsKey(serviceName)) {
					String key = MessageFormat.format("/{0}/{1}", ETCD_ROOT_PATH, serviceName);
					ByteSequence keyByteSequence = ByteSequence.fromString(key);
					GetResponse response = kv.get(keyByteSequence, GetOption.newBuilder().withPrefix(keyByteSequence).build()).get();
					Watch.Watcher watcher = watch.watch(keyByteSequence, WatchOption.newBuilder().withPrefix(keyByteSequence).build());
					EndpointSet endpointSet = new EtcdEndpointSet(watcher);
					((EtcdEndpointSet) endpointSet).initialize(response.getKvs());
					endpointSetMap.put(serviceName, endpointSet);
				}
			}
		}
		return endpointSetMap.get(serviceName);
	}

	private void keepAlive() {
		try {
			Lease.KeepAliveListener listener = lease.keepAlive(leaseId);
			listener.listen();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
