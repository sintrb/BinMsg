package com.sin.java.comm.binmsg.test.mb03;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody
public class MB03Res {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public byte addr;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public byte func;
	@MsgField(offset = 2, type = FieldType.BYTE)
	public int size;
	@MsgField(offset = 3, type = FieldType.BIG16S, sizedepend = "size")
	public short[] values;
}