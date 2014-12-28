package com.sin.java.comm.binmsg.test;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgBody.LayoutType;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody(type = LayoutType.Order)
public class TM1 {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public int p1 = 0x11;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public int p2 = 0x12;
	@MsgField(offset = 2, type = FieldType.MESSAGE)
	public TM2 m2;
}
