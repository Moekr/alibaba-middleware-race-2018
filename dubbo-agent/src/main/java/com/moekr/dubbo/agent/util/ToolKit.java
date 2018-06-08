package com.moekr.dubbo.agent.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteEnumUsingToString;
import static io.netty.util.CharsetUtil.UTF_8;

public abstract class ToolKit {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static Map<String, String> decodeForm(String formStr) throws UnsupportedEncodingException {
		Map<String, String> form = new HashMap<>();
		String[] kvPairs = formStr.split("&");
		for (String kvPair : kvPairs) {
			int equalIndex = kvPair.indexOf('=');
			String key = kvPair.substring(0, equalIndex);
			String value = URLDecoder.decode(kvPair.substring(equalIndex + 1), UTF_8.name());
			form.put(key, value);
		}
		return form;
	}

	public static void writeObject(Object obj, OutputStream outputStream) throws IOException {
		SerializeWriter serializeWriter = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(serializeWriter);
		serializer.config(WriteEnumUsingToString, true);
		serializer.write(obj);
		serializeWriter.writeTo(outputStream, CharsetUtil.UTF_8);
		serializeWriter.close();
		outputStream.write('\n');
		outputStream.flush();
	}

	public static String currentIpAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
}
