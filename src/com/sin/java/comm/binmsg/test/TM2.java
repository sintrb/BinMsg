package com.sin.java.comm.binmsg.test;

import com.sin.java.comm.binmsg.MsgBody;
import com.sin.java.comm.binmsg.MsgBody.LayoutType;
import com.sin.java.comm.binmsg.MsgField;
import com.sin.java.comm.binmsg.MsgField.FieldType;

@MsgBody(type = LayoutType.Order)
public class TM2 {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public int n1 = 0x21;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public int n2 = 0x22;
}
