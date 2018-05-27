package com.moekr.dubbo.agent.protocol;

public abstract class AgentConstants {
	public static final int MAGIC_NUMBER = 0xBEEFCAFE;
	public static final int HEADER_LENGTH = 4 + 1 + 4;
	public static final byte REQUEST_TYPE = 0;
	public static final byte RESPONSE_TYPE = 1;
}
