package com.moekr.dubbo.agent.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public abstract class ToolKit {
	private static final Charset CHARSET = Charset.forName("UTF-8");

	public static void writeObject(Object obj, OutputStream outputStream) throws IOException {
		SerializeWriter serializeWriter = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(serializeWriter);
		serializer.config(SerializerFeature.WriteEnumUsingToString, true);
		serializer.write(obj);
		serializeWriter.writeTo(outputStream, CHARSET);
		serializeWriter.close();
		outputStream.write('\n');
		outputStream.flush();
	}

	public static String currentIpAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
}
