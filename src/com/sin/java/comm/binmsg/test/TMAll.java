package com.sin.java.comm.binmsg.test;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgBody.LayoutType;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody(type = LayoutType.Order)
public class TMAll<T> {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public int len = 3;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public int dst = 4;
	@MsgField(offset = 2, type = FieldType.BYTE)
	public int src = 5;
	@MsgField(offset = 3, type = FieldType.BYTE)
	public int cmd = 6;
	@MsgField(offset = 10, type = FieldType.MESSAGE)
	public TM1 m1;
	@MsgField(offset = 11, type = FieldType.MESSAGE)
	public TM2 m2;
	@MsgField(offset = 100, type = FieldType.BYTE)
	public int check = 8;
}
