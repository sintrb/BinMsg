package com.sin.java.comm.binmsg.test;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgBody.LayoutType;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody(type = LayoutType.Offset)
public class TMsg {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public byte len1;

	@MsgField(offset = 2, type = FieldType.BYTES, sizedepend = "len1")
	public byte[] data1;

	@MsgField(offset = 10, type = FieldType.BIG16)
	public short len2;

	@MsgField(offset = 12, type = FieldType.BIG16S, sizedepend = "len2")
	public short[] data2;

	@MsgField(offset = 20, type = FieldType.LIT16)
	public int len3;

	@MsgField(offset = 22, type = FieldType.LIT16S, sizedepend = "len3")
	public int[] data3;
}
