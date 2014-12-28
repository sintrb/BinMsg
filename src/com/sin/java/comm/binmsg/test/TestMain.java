package com.sin.java.comm.binmsg.test;

import java.io.IOException;

import com.sin.java.comm.binmsg.BinMsg;
import com.sin.java.comm.binmsg.stream.SeekableBuffedOutputStream;

/**
 * 测试 <br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 */
public class TestMain {
	public static void printByteArray(byte[] arr) {
		for (byte b : arr) {
			System.out.print(String.format("%02x ", b));
		}
		System.out.println();
	}

	public static void testBinMsg(Object msg) throws IllegalArgumentException, SecurityException, IOException, IllegalAccessException, NoSuchFieldException, InstantiationException {
		byte[] dts = BinMsg.msgToBin(msg);
		printByteArray(dts);

		Object msg2 = BinMsg.binToMsg(dts, msg.getClass());
		printByteArray(BinMsg.msgToBin(msg2));
	}

	public static void main(String[] args) {
		try {
			// SeekableOutStream 测试
			SeekableBuffedOutputStream sos = new SeekableBuffedOutputStream(2);
			sos.write(1);
			sos.write(1);
			sos.write(1);
			sos.seek(10);
			sos.write(7);
			sos.seek(5);
			sos.write(5);
			sos.close();
			printByteArray(sos.toByteArray());

			// BinMsg
			TMsg msg = new TMsg();
			// msg.code = 12;
			msg.data1 = new byte[] { 0x01, 0x02, 0x03 };
			msg.len1 = (byte) (msg.data1.length * 1);

			msg.data2 = new short[] { 0x01, 0x02, 0x03, 0x04 };
			msg.len2 = (short) (msg.data2.length * 2);

			msg.data3 = new int[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x01, 0x02, 0x03, 0x04, 0x05 };
			msg.len3 = (int) (msg.data3.length * 2);

			testBinMsg(msg);

			TMAll<TM1> t2 = new TMAll<TM1>();
			t2.m1 = new TM1();
			t2.m1.m2 = new TM2();
			t2.m2 = new TM2();
			t2.m2.n1 = 0x30;
			t2.m2.n2 = 0x31;
			// printByteArray(BinMsg.msgToBin(t2));
			testBinMsg(t2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
