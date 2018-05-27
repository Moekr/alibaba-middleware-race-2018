package com.moekr.dubbo.agent.protocol;

public class DubboConstants {
	public static final int HEADER_LENGTH = 16;
	public static final short MAGIC_NUMBER = (short) 0xdabb;
	public static final byte FLAG_REQUEST = (byte) 0x80;
	public static final byte FLAG_TWO_WAY = (byte) 0x40;
	public static final byte FLAG_EVENT = (byte) 0x20;
	public static final byte FAST_JSON_SERIALIZATION_ID = (byte) 0x06;
	public static final String DUBBO_VERSION = "2.6.0";
}
