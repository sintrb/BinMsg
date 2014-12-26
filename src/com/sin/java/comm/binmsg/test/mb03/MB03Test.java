package com.sin.java.comm.binmsg.test.mb03;

import com.sin.java.comm.binmsg.BinMsg;

public class MB03Test {
	public static void printByteArray(byte[] arr) {
		for (byte b : arr) {
			System.out.print(String.format("%02x ", b));
		}
		System.out.println();
	}

	public static void main(String[] args) {
		try {
			// 请求
			MB03Req req = new MB03Req();
			req.addr = 0x01;
			req.func = 0x03;
			req.start = 10;
			req.count = 2;
			byte[] dats = BinMsg.msgToBin(req);
			// 接下来根据dats计算CRC16
			printByteArray(dats); // 打印二进制主机

			// 响应
			// 返回两个寄存器值0x1234 0x5678，末尾两个CRC16值忽略
			byte[] rebts = new byte[] { 0x01, 0x03, 4, 0x12, 0x34, 0x56, 0x78, 0, 0 };
			MB03Res res = BinMsg.binToMsg(rebts, MB03Res.class);
			for (short v : res.values) {
				System.out.print(String.format("%04x ", v));
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
