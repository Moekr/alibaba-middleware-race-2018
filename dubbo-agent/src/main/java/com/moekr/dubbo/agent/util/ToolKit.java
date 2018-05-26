package com.moekr.dubbo.agent.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class ToolKit {
	public static String currentIpAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
}
