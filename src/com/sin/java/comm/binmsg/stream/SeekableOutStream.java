package com.sin.java.comm.binmsg.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 可寻址的输出内存流 <br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 */
public class SeekableOutStream extends OutputStream {
	private byte[] buf = null;
	private int capacity = 256;
	private int size = 0;
	private int pos = 0;

	/**
	 * 创建一个可寻址的输出流
	 * 
	 */
	public SeekableOutStream() {
		super();
		this.buf = new byte[capacity];
	}

	/**
	 * 创建一个可寻址的输出流
	 * 
	 * @param capacity
	 *            初始缓冲区容量
	 */
	public SeekableOutStream(int capacity) {
		super();
		this.capacity = capacity;
		this.buf = new byte[capacity];
	}

	/**
	 * 获取缓冲区容量
	 * 
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * 设置缓冲区大小
	 * 
	 * @param capacity
	 *            缓冲区大小，小于当前缓冲区的话设置不会生效
	 * @return 新的缓冲区大小
	 */
	public int setCapacity(int capacity) {
		if (capacity < this.size)
			throw new IllegalArgumentException("capacity must larger than current size");
		if (capacity > this.capacity)
			this.newBuf(capacity);
		return this.capacity;
	}

	/**
	 * 获取当前的数据长度
	 * 
	 * @return 数据长度
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 获取指针位置
	 * 
	 * @return 指针位置
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * 分配新的缓冲区
	 * 
	 * @param new_capacity
	 */
	private void newBuf(int new_capacity) {
		if (new_capacity < capacity)
			return;
		byte[] new_buf = new byte[new_capacity];
		System.arraycopy(this.buf, 0, new_buf, 0, this.size);
		this.buf = new_buf;
		this.capacity = new_capacity;
	}

	@Override
	public void write(int b) throws IOException {
		if (this.size == this.capacity) {
			this.newBuf(this.capacity * 2);
		}
		this.buf[this.pos++] = (byte) (b & 0x00ff);
		if (this.pos > this.size)
			this.size = this.pos;
	}

	/**
	 * 寻址
	 * 
	 * @param newpos
	 *            新的位置，超出缓冲区空间时会自动重新分配
	 */
	public void seek(int newpos) {
		// System.out.println("slk: " + sk);
		if (newpos >= this.capacity) {
			this.newBuf(newpos * 2);
			this.size = newpos;
		}
		this.pos = newpos;
	}

	/**
	 * 转换为字节数组
	 * 
	 * @return 字节数组
	 */
	public byte[] toByteArray() {
		byte[] dats = new byte[this.size];
		System.arraycopy(this.buf, 0, dats, 0, this.size);
		return dats;
	}
}
