package com.sin.java.comm.binmsg.test.mb03;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody
public class MB03Req {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public byte addr;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public byte func = 0x03; // 请求码
	@MsgField(offset = 2, type = FieldType.BIG16)
	public short start;
	@MsgField(offset = 4, type = FieldType.BIG16)
	public short count;
}