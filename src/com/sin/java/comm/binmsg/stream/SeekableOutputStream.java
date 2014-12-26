package com.sin.java.comm.binmsg.stream;

import java.io.OutputStream;

/**
 * 可寻址的输出内存流抽象类 <br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 */
public abstract class SeekableOutputStream extends OutputStream {
	/**
	 * 设置寻址
	 * 
	 * @param seek
	 *            位置
	 */
	public abstract void seek(int pos);

	/**
	 * 获取当前指针位置
	 * 
	 * @return
	 */
	public abstract int getPosition();
}
