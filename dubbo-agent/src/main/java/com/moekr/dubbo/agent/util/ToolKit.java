package com.moekr.dubbo.agent.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class ToolKit {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void writeObject(Object obj, OutputStream outputStream) throws IOException {
		SerializeWriter serializeWriter = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(serializeWriter);
		serializer.config(SerializerFeature.WriteEnumUsingToString, true);
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
